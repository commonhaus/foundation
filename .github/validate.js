import { readdirSync, readFileSync, statSync } from 'node:fs';
import path from 'node:path';
import { parse } from 'yaml';
import markdownLinkCheck from 'markdown-link-check';

const cwd = process.cwd();
const root = path.resolve(cwd, '..');

const linkCheckOptions = JSON.parse(readFileSync('.mlc_config.json', 'utf8'))
linkCheckOptions.projectBaseUrl = 'https://github.com/commonhaus/foundation/blob/main/';

console.log(`Current directory: ${cwd}\nRoot directory: ${root}`);

const exclude = [
    '.github',
    '.pandoc',
    '.markdownlint.yaml',
    'bootstrapping'
]

const errors = [];
const mdFiles = [];
const yamlFiles = [];

function findFiles(from) {
    for (const file of readdirSync(from)) {
        const filePath = path.join(from, file);
        if (exclude.includes(file)) {
            continue;
        }
        if (file.endsWith(".md")) {
            mdFiles.push(filePath);
        } else if (file.endsWith('.yaml') || file.endsWith('.yml')) {
            yamlFiles.push(filePath);
        } else {
            const stat = statSync(filePath);
            if (stat.isDirectory()) {
                // RECURSE / TRAVERSE
                findFiles(filePath);
            }
        }
    }
}

function toRelativePath(filePath) {
    const relativePath = path.dirname(filePath).replace(root, '').replace(/^\//, '');
    return `https://github.com/commonhaus/foundation/blob/main/${relativePath}`;
}

function checkLinks(content, projectBaseUrl) {
    markdownLinkCheck(content, {
        ...linkCheckOptions,
        projectBaseUrl
    });
}

try {
    findFiles(root);

    for (const filePath of mdFiles) {
        try {
            const fileContent = readFileSync(filePath, 'utf8');
            const projectBaseUrl = toRelativePath(filePath);
            checkLinks(fileContent, projectBaseUrl);
            console.log(` ✅  ${filePath}`);
        } catch (err) {
            console.log(` ❌  ${filePath}`);
            errors.push(err);
        }
    }

    const httpLinkRegex = /https?:\/\/[^\s]+/g;
    for (const filePath of yamlFiles) {
        try {
            const fileContent = readFileSync(filePath, 'utf8');
            const projectBaseUrl = toRelativePath(filePath);

            const data = parse(fileContent); // can we parse the yaml content
            if (filePath.endsWith("SPONSORS.yaml")) {
                verifySponsors(data);
            }

            const httpLinks = fileContent.matchAll(httpLinkRegex);
            for (const match of httpLinks) {
                checkLinks(`[](${match})`, projectBaseUrl);
            }
            console.log(` ✅  ${filePath}`);
        } catch (err) {
            console.log(` ❌  ${filePath}`);
            errors.push(err);
        }
    }
} catch (err) {
    errors.push(err);
}

if (errors.length > 0) {
    console.error('Errors found',
        ...errors.map(error => `\n- ${error}`));
    process.exit(1);
} else {
    console.log(' 🚀  Done.');
}

function verifySponsors(data) {
    // Verify sponsor data.
    // Specifically validate sponsor description by tier.
    const allTiers = data.tiers;

    const sponsors = data.sponsors;
    for (const [key, sponsor] of Object.entries(sponsors)) {
        for (const tier of sponsor.tier) {
            let content = '';
            let limit = '';
            if (tier === "in-kind") {
                content = stripFormatting(sponsor.display["in-kind"]);
                limit = allTiers["in-kind"].chars
            } else if (allTiers[tier].chars) {
                limit = allTiers[tier].chars;
                content = stripFormatting(sponsor.display.description);
            }
            if (content.length > allTiers[tier].chars) {
                errors.push(`Sponsor ${sponsor.name} exceeds character limit (${limit}) for tier ${tier}. Filtered content is ${content.length} characters`);
            }
        }
    }
}

function stripFormatting(content) {
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
