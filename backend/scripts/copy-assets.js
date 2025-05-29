const fs = require('fs');
const path = require('path');

const copyRecursiveSync = (src, dest) => {
  const exists = fs.existsSync(src);
  const stats = exists && fs.statSync(src);
  const isDirectory = exists && stats.isDirectory();

  if (isDirectory) {
    fs.mkdirSync(dest, { recursive: true });
    fs.readdirSync(src).forEach(item => {
      copyRecursiveSync(path.join(src, item), path.join(dest, item));
    });
  } else if (path.extname(src) === '.json' || path.extname(src) === '.txt') {
    fs.copyFileSync(src, dest);
  }
};

const assets = ['config', 'data', 'interfaces'];
const srcDir = path.join(__dirname, '../src');
const distDir = path.join(__dirname, '../dist');

assets.forEach(asset => {
  const src = path.join(srcDir, asset);
  const dest = path.join(distDir, asset);
  if (fs.existsSync(src)) copyRecursiveSync(src, dest);
});

console.log('Assets copied successfully');