const fs = require('fs');
const path = require('path');

const srcDir = path.join(__dirname, '../src');
const distDir = path.join(__dirname, '../dist');

const assetsToCopy = [
  'config',
  'controllers',
  'services',
  'utils',
  'data',
  'interfaces'
];

function copyRecursiveSync(src, dest) {
  const exists = fs.existsSync(src);
  const stats = exists && fs.statSync(src);
  const isDirectory = exists && stats.isDirectory();

  if (isDirectory) {
    if (!fs.existsSync(dest)) {
      fs.mkdirSync(dest, { recursive: true });
    }
    fs.readdirSync(src).forEach(childItemName => {
      copyRecursiveSync(
        path.join(src, childItemName),
        path.join(dest, childItemName)
      );
    });
  } else {
    fs.copyFileSync(src, dest);
  }
}

try {
  if (!fs.existsSync(distDir)) {
    fs.mkdirSync(distDir, { recursive: true });
  }

  assetsToCopy.forEach(asset => {
    const srcPath = path.join(srcDir, asset);
    const destPath = path.join(distDir, asset);
    
    if (fs.existsSync(srcPath)) {
      console.log(`Copying ${asset}...`);
      copyRecursiveSync(srcPath, destPath);
    }
  });

  console.log('Assets copied successfully!');
} catch (err) {
  console.error('Error copying assets:', err);
  process.exit(1);
}