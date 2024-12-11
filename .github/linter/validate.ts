import { existsSync, readdirSync, readFileSync, statSync } from 'node:fs';
import { basename, dirname, join, resolve } from 'node:path';
import { parse } from 'yaml';
import { marked } from 'marked';
import linkCheck from 'link-check';

const cwd = process.cwd();
const root = resolve(cwd, '../..');
if (!existsSync(join(root, 'dco.txt'))) {
    console.error('This script must be run from .github/linter');
    process.exit(1);
}

console.log(`Current directory: ${cwd}\nRoot directory: ${root}`);

class CheckResult {
    filePath: string;
    link: string;
    status: string;
    statusCode: number;
    err: any;
}

const exclude = [
    '.github',
    '.pandoc',
    '.markdownlint.yaml',
    'bootstrapping'
];
const httpLinkRegex = /https?:\/\/[^\s)]+/g;
const linkCheckOptions = JSON.parse(readFileSync('.mlc_config.json', 'utf8'));

class Validator {
    errors: any[] = [];
    warnings: string[] = [];
    deadLinks: CheckResult[] = [];

    mdFiles: string[] = [];
    yamlFiles: string[] = [];

    // Filepath to links
    allLinks = {};
    // FilePath to slugified headings
    allHeadings = {};

    constructor(from: string) {
        this.findFiles(from);
    }

    checkLink = async (filePath: string, link: string): Promise<void> => {
        return link.startsWith('http')
            ? this.checkHttpLink(filePath, link)
            : this.checkOtherLink(filePath, link);
    }

    checkHttpLink = async (filePath: string, link: string): Promise<void> => {
        return new Promise((resolvePromise, rejectPromise) => { // avoid name collision with path.resolve
            const baseName = basename(filePath)
            try {
                // console.log("    ==> URL", link);
                linkCheck(link, linkCheckOptions, function (err: any, result: CheckResult) {
                    if (err) {
                        console.error(`Error processing links in ${baseName}:`, err);
                        err.filePath = filePath
                        this.errors.push(err);
                        return resolvePromise();
                    }
                    if (result.status === 'dead') {
                        result.filePath = filePath;
                        this.deadLinks.push(result);
                    }
                    resolvePromise();
                });
            } catch (error) {
                this.handleScopedError(filePath, error);
                rejectPromise(error);
            }
        });
    }

    checkOtherLink = async (filePath: string, link: string): Promise<void> => {
        return new Promise((resolvePromise, rejectPromise) => { // avoid name collision with path.resolve
            if (link.startsWith('mailto:')) {
                return resolvePromise();
            }
            if (link.startsWith('#')) {
                if (!this.allHeadings[filePath].includes(link)) {
                    this.deadLinks.push({
                        filePath,
                        link,
                        status: 'dead',
                        statusCode: 404,
                        err: new Error('Heading not found')
                    });
                }
                return resolvePromise();
            }
            if (link === 'TBD') {
                this.warnings.push(`TBD link in ${filePath}`);
                return resolvePromise();
            }

            const anchorPos = link.indexOf('#');
            const file = anchorPos > 0 ? link.slice(0, anchorPos) : link;
            const localPath = resolve(dirname(filePath), file);
            if (!localPath.startsWith(root)) {
                this.deadLinks.push({
                    filePath,
                    link,
                    status: 'dead',
                    statusCode: 404,
                    err: new Error(`Link ${link} is outside of directory`)
                });
                return resolvePromise
            }
            if (!existsSync(localPath)) {
                // the referenced file doesn't exist
                this.deadLinks.push({
                    filePath,
                    link,
                    status: 'dead',
                    statusCode: 404,
                    err: new Error(`File ${file} not resolvable as ${localPath}`)
                });
                return resolvePromise();
            }

            if (anchorPos > 0) {
                // we also have a section reference
                const heading = link.slice(anchorPos);
                const headings = this.allHeadings[localPath];
                if (!headings || !headings.includes(heading)) {
                    this.deadLinks.push({
                        filePath,
                        link,
                        status: 'dead',
                        statusCode: 404,
                        err: new Error(`Heading ${heading} not found in ${file}`)
                    });
                }
            }
            // we're all good!
            resolvePromise();
        });
    }

    /**
     * Drop some depedencies and extract links/headings from markdown
     * ourselves (directly from marked)
     * Transform headings to slugs as we see them, and store them for later
     * by filePath
     */
    extractLinksAndHeadings = (filePath: string) => {
        const fileContent = readFileSync(filePath, 'utf8');
        const linkSet = new Set();
        const headingSet = new Set();
        const renderer = new marked.Renderer();

        renderer.link = function (link) {
            linkSet.add(link.href);
            return marked.Renderer.prototype.link.apply(this, arguments);
        };

        renderer.heading = function (heading) {
            // From: https://github.com/tcort/markdown-link-check/
            // https://github.com/tcort/markdown-link-check/blob/master/LICENSE.md
            const id = encodeURIComponent(heading.text
                // replace links, the links can start with "./", "/", "http://", "https://" or "#"
                // and keep the value of the text ($1)
                .replace(/\[(.+)\]\(((?:\.?\/|https?:\/\/|#)[\w\d./?=#-]+)\)/, "$1")
                // make everything (Unicode-aware) lower case
                .toLowerCase()
                // remove everything that is NOT a (Unicode) Letter, (Unicode) Number decimal,
                // (Unicode) Number letter, white space, underscore or hyphen
                // https://ruby-doc.org/3.3.2/Regexp.html#class-Regexp-label-Unicode+Character+Categories
                .replace(/[^\p{L}\p{Nd}\p{Nl}\s_\-`]/gu, "")
                // remove sequences of *
                .replace(/\*(?=.*)/gu, "")
                // remove leftover backticks
                .replace(/`/gu, "")
                // Now replace remaining blanks with '-'
                .replace(/\s+/gu, "-")
            );
            headingSet.add(`#${id}`);
            return marked.Renderer.prototype.heading.apply(this, arguments);
        };

        try {
            marked(fileContent, { renderer });
            this.allLinks[filePath] = [...linkSet];
            this.allHeadings[filePath] = [...headingSet];
        } catch (err) {
            this.handleScopedError(filePath, err)
        }
    }

    findFiles = (from: string) => {
        for (const file of readdirSync(from)) {
            const filePath = join(from, file);
            if (exclude.includes(file)) {
                continue;
            }
            if (file.endsWith(".md")) {
                this.mdFiles.push(filePath);
                this.extractLinksAndHeadings(filePath);
            } else if (file.endsWith('.yaml') || file.endsWith('.yml')) {
                this.yamlFiles.push(filePath);
            } else {
                const stat = statSync(filePath);
                if (stat.isDirectory()) {
                    // RECURSE / TRAVERSE
                    this.findFiles(filePath);
                }
            }
        }
    }

    handleError = (error: any): void => {
        this.errors.push(error);
    }
    handleScopedError = (filePath: string, error: any): void => {
        error.filePath = filePath;
        this.errors.push(error);
    }
}

interface SponsorData {
    tiers: Record<string, SponsorTier>;
    sponsors: Record<string, Sponsor>;
}
interface SponsorTier {
    name: string;
    description: string;
    chars?: number;
}
interface Sponsor {
    name: string;
    display: {
        description?: string;
        logo?: string;
        url?: string;
        "in-kind"?: string;
    }
    tier?: string[];
}

class Sponsors {
    static verifySponsors = (data: SponsorData, errors: string[]) => {
        // Verify sponsor data.
        // Specifically validate sponsor description by tier.
        const allTiers = data.tiers;

        const sponsors = data.sponsors;
        for (const [_, sponsor] of Object.entries(sponsors)) {
            for (const tier of sponsor.tier || []) {
                let content = '';
                let limit = allTiers[tier].chars || 0;
                if (tier === "in-kind") {
                    content = Sponsors.stripFormatting(sponsor.display["in-kind"] || '');
                } else if (allTiers[tier].chars) {
                    content = this.stripFormatting(sponsor.display.description || '');
                }
                if (content.length > limit) {
                    errors.push(`Sponsor ${sponsor.name} exceeds character limit (${limit}) for tier ${tier}. Filtered content is ${content.length} characters`);
                }
            }
        }
    }

    static stripFormatting = (content: string) => {
        if (!content) {
            return '';
        }
        // Regular expression to match Markdown links
        const markdownLinkRegex = /\[([^\]]+)\]\(([^)]+)\)/g;
        // Replace Markdown links with the link text
        return content.replace(markdownLinkRegex, '$1')
            .replace(/\p{P}/gu, '') // remove all punctuation
            .replace(/\s+/g, '');   // Remove all whitespace
    }
}

async function main() {
    const validator = new Validator(root);

    try {
        for (const filePath of validator.mdFiles) {
            for (const href of validator.allLinks[filePath]) {
                await validator.checkLink(filePath, href);
            }
            console.log(` ✔️  ${filePath}`);
        }

        for (const filePath of validator.yamlFiles) {
            try {
                const fileContent = readFileSync(filePath, 'utf8');
                const data = parse(fileContent); // can we parse the yaml content
                if (filePath.endsWith("SPONSORS.yaml")) {
                    Sponsors.verifySponsors(data, validator.errors);
                }
                const httpLinks = fileContent.matchAll(httpLinkRegex);
                for (const match of httpLinks) {
                    await validator.checkLink(filePath, match[0]);
                }
            } catch (err) {
                validator.handleError(err);
            }
            console.log(` ✔️  ${filePath}`);
        }
    } catch (err) {
        validator.handleError(err);
    }

    if (validator.warnings.length > 0) {
        console.warn('🚧 Warnings found')
        for(const warning of validator.warnings) {
            console.warn('- ', warning);
        }
    }

    if (validator.deadLinks.length > 0) {
        console.error('⛓️‍💥 Bad links found')
        for(const deadLink of validator.deadLinks) {
            console.error('- ', deadLink.filePath, `\n    ${deadLink.statusCode}: ${deadLink.err}`, "\n    link:", deadLink.link);
        }
    }
    if (validator.errors.length > 0) {
        console.error('❌ Errors found')
        for(const error of validator.errors) {
            console.error('- ', error.filePath, "\n    ", error);
        }
    }

    if (validator.errors.length > 0 || validator.deadLinks.length > 0) {
        console.log(' 😵‍💫  Finished with errors');
        process.exit(1);
    } else {
        console.log(' 🚀  Done.');
        process.exit(0);
    }
}

main().catch(err => {
    console.error('Unhandled error:', err);
    process.exit(1);
});