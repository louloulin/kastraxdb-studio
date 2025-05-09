# MagicDB Tauri 使用指南

本文档提供了在 MagicDB 项目中使用 Tauri 的详细指南。

## 目录

1. [简介](#简介)
2. [项目结构](#项目结构)
3. [开发环境设置](#开发环境设置)
4. [常用 API](#常用-api)
5. [构建和打包](#构建和打包)
6. [常见问题](#常见问题)
7. [最佳实践](#最佳实践)

## 简介

MagicDB 使用 Tauri 作为桌面应用程序框架，它是一个轻量级的替代 Electron 的解决方案。Tauri 使用系统的 WebView 组件来渲染 UI，并使用 Rust 编写后端逻辑。

### 为什么选择 Tauri？

- **更小的安装包体积**：Tauri 应用程序比 Electron 应用程序小得多
- **更好的性能**：使用更少的内存和 CPU 资源
- **增强的安全性**：具有更安全的架构和细粒度的权限控制
- **原生外观和感觉**：在每个平台上看起来和感觉更加原生

## 项目结构

Tauri 相关的文件和目录结构如下：

```
magicdb-client/
├── src/                      # 前端代码
│   ├── components/           # React 组件
│   │   └── TauriDemo.tsx     # Tauri 功能演示组件
│   ├── pages/                # 页面组件
│   │   └── tauri-demo/       # Tauri 演示页面
│   ├── utils/                # 工具函数
│   │   ├── environment.ts    # 环境检测工具
│   │   └── tauri-api.ts      # Tauri API 包装器
│   └── typings/              # 类型定义
│       └── tauri.d.ts        # Tauri 类型定义
├── src-tauri/                # Tauri 后端代码
│   ├── src/                  # Rust 源代码
│   │   └── main.rs           # 主入口文件
│   ├── Cargo.toml            # Rust 依赖配置
│   ├── tauri.conf.json       # Tauri 配置文件
│   ├── build.rs              # 构建脚本
│   └── icons/                # 应用图标
├── setup-tauri-icons.js      # 设置 Tauri 图标的脚本
├── TAURI-MIGRATION.md        # 从 Electron 迁移到 Tauri 的指南
└── TAURI-USAGE.md            # Tauri 使用指南（本文档）
```

## 开发环境设置

### 前提条件

在开始使用 Tauri 开发之前，您需要安装以下软件：

1. **Rust 和 Cargo**：Tauri 的后端使用 Rust 编写
   ```bash
   curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh
   ```

2. **系统依赖**：根据您的操作系统，您可能需要安装其他依赖项
   - **Windows**：Microsoft Visual C++ 构建工具
   - **macOS**：Xcode 命令行工具
   - **Linux**：各种开发包（详见 Tauri 文档）

3. **Node.js 和 npm/yarn**：前端开发所需

### 安装项目依赖

克隆项目后，安装依赖：

```bash
cd magicdb-client
yarn install
```

### 运行开发服务器

```bash
yarn start
```

这将启动前端开发服务器和 Tauri 应用程序。

## 常用 API

MagicDB 提供了一个 Tauri API 包装器，位于 `src/utils/tauri-api.ts`，它提供了以下功能：

### 窗口控制

```typescript
// 最小化窗口
await tauriApi.minimizeWindow();

// 最大化/还原窗口
await tauriApi.setMaximize();

// 关闭窗口
await tauriApi.closeWindow();

// 检查窗口是否最大化
const isMaximized = await tauriApi.isMaximized();
```

### 文件操作

```typescript
// 读取文件
const content = await tauriApi.readFile('/path/to/file.txt');

// 写入文件
await tauriApi.writeFile('/path/to/file.txt', 'Hello, World!');

// 检查文件是否存在
const exists = await tauriApi.fileExists('/path/to/file.txt');

// 获取用户主目录
const homeDir = await tauriApi.getHomeDir();
```

### 对话框

```typescript
// 消息对话框
await tauriApi.messageDialog({
  title: '提示',
  message: '操作成功！'
});

// 确认对话框
const confirmed = await tauriApi.confirmDialog({
  title: '确认',
  message: '确定要执行此操作吗？'
});

// 打开文件对话框
const filePath = await tauriApi.openFileDialog({
  title: '选择文件',
  filters: [
    { name: '文本文件', extensions: ['txt', 'md'] },
    { name: '所有文件', extensions: ['*'] }
  ]
});

// 保存文件对话框
const savePath = await tauriApi.saveFileDialog({
  title: '保存文件',
  filters: [
    { name: '文本文件', extensions: ['txt'] }
  ]
});
```

### 其他功能

```typescript
// 打开外部链接
await tauriApi.openExternal('https://tauri.app/');

// 获取应用信息
const appInfo = await tauriApi.getAppInfo();

// 获取平台信息
const platformInfo = tauriApi.getPlatform();

// 退出应用程序
await tauriApi.quitApp();

// 重启应用程序
await tauriApi.relaunchApp();
```

## 构建和打包

### 构建生产版本

```bash
yarn build
```

这将构建前端和 Tauri 应用程序。生成的可执行文件位于 `src-tauri/target/release/` 目录中。

### 自定义图标

要自定义应用程序图标，请运行：

```bash
yarn setup-icons
```

这将从 `src/assets/logo` 目录复制图标文件到 `src-tauri/icons` 目录，并根据 Tauri 的要求重命名和调整大小。

## 常见问题

### 1. 如何调试 Tauri 应用程序？

在开发模式下，Tauri 应用程序会自动打开开发者工具。您也可以在代码中使用 `console.log` 进行调试。

### 2. 如何处理平台特定的代码？

使用 `environment.ts` 中的 `getPlatform()` 函数来检测当前平台：

```typescript
import { getPlatform } from '../utils/environment';

const { isLinux, isWin, isMac } = getPlatform();

if (isMac) {
  // macOS 特定代码
} else if (isWin) {
  // Windows 特定代码
} else if (isLinux) {
  // Linux 特定代码
}
```

### 3. 如何在 Tauri 中使用自定义命令？

在 `src-tauri/src/main.rs` 中定义命令，然后在前端使用 `invoke` 调用：

```typescript
// 前端
import { invoke } from '@tauri-apps/api/tauri';

const result = await invoke('my_custom_command', { param1: 'value1' });
```

## 最佳实践

1. **使用 Tauri API 包装器**：尽量使用 `tauri-api.ts` 中的函数，而不是直接使用 Tauri API，这样可以保持代码的一致性。

2. **检测环境**：始终使用 `isTauri()` 函数检测当前是否在 Tauri 环境中运行，以便在 Web 环境中提供回退行为。

3. **错误处理**：Tauri API 调用可能会失败，始终使用 try/catch 块处理错误。

4. **权限控制**：在 `tauri.conf.json` 中仅启用应用程序所需的权限，遵循最小权限原则。

5. **资源管理**：使用 Tauri 的资源系统来管理应用程序资源，而不是将它们嵌入到前端代码中。

## 参考资料

- [Tauri 官方文档](https://tauri.app/v1/guides/)
- [Tauri API 参考](https://tauri.app/v1/api/js/)
- [Rust 文档](https://doc.rust-lang.org/book/)
- [MagicDB Tauri 迁移指南](./TAURI-MIGRATION.md)
