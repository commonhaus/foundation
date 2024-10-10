import { readFileSync } from 'node:fs';
import { readdir } from 'node:fs/promises';
import path from 'node:path';
import { parse } from 'yaml';

const cwd = process.cwd();
const root = path.resolve(cwd, '..');
console.log(`Current directory: ${cwd}\nRoot directory: ${root}`);

const errors = [];

try {
  const files = await readdir(root);
  for (const file of files) {
    // Verify syntax for YAML files
    if (file.endsWith('.yml') || file.endsWith('.yaml')) {
      try {
        const filePath = path.resolve(root, file);
        console.log('Verify YAML file syntax:', file);

        const fileContent = readFileSync(filePath, 'utf8');
        const data = parse(fileContent);
        if (file === "SPONSORS.yaml") {
          verifySponsors(data);
        }
      } catch (err) {
        // Log the error and continue to the next file
        errors.push(err);
      }
    }
  }
} catch (err) {
  errors.push(err);
  console.error(err);
}

if (errors.length > 0) {
  console.error('Errors found',
      ...errors.map(error => `\n- ${error}`));
  process.exit(1);
}

function verifySponsors(data) {
  // Verify sponsor data.
  // Specifically validate sponsor description by tier.
  const allTiers = data.tiers;

  const sponsors = data.sponsors;
  for(const [key, sponsor] of Object.entries(sponsors)) {
    console.log(key, sponsor.name, sponsor.tier);
    for(const tier of sponsor.tier) {
      let content = '';
      let limit = '';
      if (tier == "in-kind") {
        content = stripFormatting(sponsor.display["in-kind"]);
        limit = allTiers["in-kind"].chars
      } else if (allTiers[tier].chars) {
        limit = allTiers[tier].chars;
        content = stripFormatting(sponsor.display.description);
      }
      console.log(limit, content.length, content);
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