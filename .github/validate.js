const { argv } = require('node:process');
const { readFileSync } = require('node:fs');
const YAML = require('yaml');

argv.forEach((val, index) => {
  // ignore first two arguments
  if (index > 1) {
    console.log(`${index}: ${val}`);
    const file = readFileSync(val, 'utf8')
    YAML.parse(file);
  }
});