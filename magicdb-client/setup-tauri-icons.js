/**
 * 设置 Tauri 图标脚本
 * 
 * 这个脚本会从 src/assets/logo 目录复制图标文件到 src-tauri/icons 目录，
 * 并根据 Tauri 的要求重命名和调整大小。
 */

const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

// 源图标目录
const sourceDir = path.join(__dirname, 'src', 'assets', 'logo');
// 目标图标目录
const targetDir = path.join(__dirname, 'src-tauri', 'icons');

// 确保目标目录存在
if (!fs.existsSync(targetDir)) {
  fs.mkdirSync(targetDir, { recursive: true });
  console.log(`创建目录: ${targetDir}`);
}

// 复制图标文件
function copyIcons() {
  try {
    // 检查源目录是否存在
    if (!fs.existsSync(sourceDir)) {
      console.error(`源目录不存在: ${sourceDir}`);
      return false;
    }

    // 复制 .ico 文件 (Windows)
    const icoFile = path.join(sourceDir, 'logo.ico');
    if (fs.existsSync(icoFile)) {
      fs.copyFileSync(icoFile, path.join(targetDir, 'icon.ico'));
      console.log('已复制 Windows 图标 (icon.ico)');
    } else {
      console.warn(`未找到 Windows 图标文件: ${icoFile}`);
    }

    // 复制 .icns 文件 (macOS)
    const icnsFile = path.join(sourceDir, 'logo.icns');
    if (fs.existsSync(icnsFile)) {
      fs.copyFileSync(icnsFile, path.join(targetDir, 'icon.icns'));
      console.log('已复制 macOS 图标 (icon.icns)');
    } else {
      console.warn(`未找到 macOS 图标文件: ${icnsFile}`);
    }

    // 复制 .png 文件 (Linux 和其他尺寸)
    const pngFiles = [
      { source: 'logo-32x32.png', target: '32x32.png' },
      { source: 'logo-128x128.png', target: '128x128.png' },
      { source: 'logo-256x256.png', target: '128x128@2x.png' }
    ];

    for (const file of pngFiles) {
      const sourcePath = path.join(sourceDir, file.source);
      const targetPath = path.join(targetDir, file.target);
      
      if (fs.existsSync(sourcePath)) {
        fs.copyFileSync(sourcePath, targetPath);
        console.log(`已复制 PNG 图标 (${file.target})`);
      } else {
        console.warn(`未找到 PNG 图标文件: ${sourcePath}`);
      }
    }

    return true;
  } catch (error) {
    console.error('复制图标文件时出错:', error);
    return false;
  }
}

// 创建默认图标
function createDefaultIcons() {
  try {
    console.log('创建默认图标...');
    
    // 创建一个简单的 SVG 图标
    const svgContent = `<svg width="256" height="256" viewBox="0 0 256 256" fill="none" xmlns="http://www.w3.org/2000/svg">
  <rect width="256" height="256" rx="78" fill="#3B82F6"/>
  <path d="M128 68C94.9 68 68 94.9 68 128C68 161.1 94.9 188 128 188C161.1 188 188 161.1 188 128C188 94.9 161.1 68 128 68ZM128 178C100.4 178 78 155.6 78 128C78 100.4 100.4 78 128 78C155.6 78 178 100.4 178 128C178 155.6 155.6 178 128 178Z" fill="white"/>
  <path d="M128 98C111.4 98 98 111.4 98 128C98 144.6 111.4 158 128 158C144.6 158 158 144.6 158 128C158 111.4 144.6 98 128 98ZM128 148C116.9 148 108 139.1 108 128C108 116.9 116.9 108 128 108C139.1 108 148 116.9 148 128C148 139.1 139.1 148 128 148Z" fill="white"/>
  <circle cx="128" cy="128" r="10" fill="white"/>
</svg>`;

    // 保存 SVG 文件
    const svgPath = path.join(targetDir, 'icon.svg');
    fs.writeFileSync(svgPath, svgContent);
    console.log(`已创建 SVG 图标: ${svgPath}`);

    // 创建 PNG 文件 (如果有 ImageMagick)
    try {
      execSync('which convert', { stdio: 'ignore' });
      
      // 创建不同尺寸的 PNG
      const sizes = [32, 128, 256];
      for (const size of sizes) {
        const outputName = size === 256 ? '128x128@2x.png' : `${size}x${size}.png`;
        const outputPath = path.join(targetDir, outputName);
        
        execSync(`convert ${svgPath} -resize ${size}x${size} ${outputPath}`);
        console.log(`已创建 PNG 图标 (${outputName})`);
      }
    } catch (error) {
      console.warn('未找到 ImageMagick，跳过 PNG 图标创建');
      
      // 创建简单的空 PNG 文件
      for (const name of ['32x32.png', '128x128.png', '128x128@2x.png']) {
        const emptyPngPath = path.join(targetDir, name);
        // 创建一个 1x1 的空白文件
        fs.writeFileSync(emptyPngPath, Buffer.from([
          0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0x00, 0x00, 0x00, 0x0D,
          0x49, 0x48, 0x44, 0x52, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,
          0x08, 0x06, 0x00, 0x00, 0x00, 0x1F, 0x15, 0xC4, 0x89, 0x00, 0x00, 0x00,
          0x0A, 0x49, 0x44, 0x41, 0x54, 0x78, 0x9C, 0x63, 0x00, 0x01, 0x00, 0x00,
          0x05, 0x00, 0x01, 0x0D, 0x0A, 0x2D, 0xB4, 0x00, 0x00, 0x00, 0x00, 0x49,
          0x45, 0x4E, 0x44, 0xAE, 0x42, 0x60, 0x82
        ]));
        console.log(`已创建空 PNG 图标 (${name})`);
      }
    }

    return true;
  } catch (error) {
    console.error('创建默认图标时出错:', error);
    return false;
  }
}

// 主函数
function main() {
  console.log('开始设置 Tauri 图标...');
  
  // 尝试复制现有图标
  const copied = copyIcons();
  
  // 如果复制失败，创建默认图标
  if (!copied) {
    console.log('未找到现有图标，创建默认图标...');
    createDefaultIcons();
  }
  
  console.log('Tauri 图标设置完成！');
}

// 执行主函数
main();
