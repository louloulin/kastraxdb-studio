# MagicDB Client

MagicDB 是一个集成 AI 能力的智能数据库客户端和智能 BI 报表工具。

## 技术选型

1. 脚手架：umi v4
2. 组件库：antd v5
3. 状态管理库：dva
4. 图表库：echarts
5. 国际化：内置
6. 桌面应用框架：Tauri

## 开发环境要求

- Node.js 16+
- Rust 1.60+
- Tauri CLI
- 系统依赖（根据 Tauri 要求）

### 安装 Rust 和 Tauri CLI

```bash
# 安装 Rust
curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh

# 安装 Tauri CLI
cargo install tauri-cli
```

## 启动项目

### 安装依赖

```bash
npm install
# 或
yarn
```

### 开发模式

```bash
# 启动开发服务器（前端 + Tauri）
npm run start
# 或
yarn start

# 仅启动前端开发服务器
npm run start:web
# 或
yarn start:web

# 仅启动 Tauri 开发服务器
npm run start:tauri
# 或
yarn start:tauri
```

### 构建生产版本

```bash
# 构建生产版本（前端 + Tauri）
npm run build
# 或
yarn build

# 仅构建前端
npm run build:web
# 或
yarn build:web

# 仅构建 Tauri 应用
npm run build:tauri
# 或
yarn build:tauri
```

### 部署到后端

```bash
# 构建前端
yarn run build:web:prod

# 复制打包结果到指定目录
cp -r dist ../magicdb-server/magicdb-server-start/src/main/resources/static/front
# Windows 可能命令不一样，可以手动复制

# 启动后端
mvn clean package -B '-Dmaven.test.skip=true' -f magicdb-server/pom.xml
```

## TS书写规范

  1. 所有的interface 与 type 必须已I开头
    `interface IState { name: string }` // good
    `interface State { name: string }` // bad

## 如何在 js 与 css 中使用颜色

具体转换在 /theme/index.ts 中的 injectThemeVar

- js 在 window.\_AppThemePack 中去取 eg：`window._AppThemePack.controlItemBgActive` // good
- css eg: `background: var(--control-item-bg-active)` // good
- css `color: #fff` // bad

## 如何使用国际化

所有 key 参考格式为 `模块名称.文案类型.文案描述`。若文案包含可变部分，可使用 `{1}`、`{2}`、`{3}` 代替。

`src/i18n/index.ts` 中默认导出 `i18n` 转换方法，可以将 key 转换为对应的实际文案。文案中的 `{1}` 将被替换为第二个入参，以此类推。例如：

```tsx
// 'home.tip.welcome': '欢迎您，{1}！'
i18n('home.tip.welcome', user.name); // => '欢迎您，张三！'
```

也可以使用 `src/i18n/index.ts` 中导出的 `i18nElement` 方法，可以将文案中的占位符替换为 JSX 元素。例如：

```tsx
i18nElement('home.tip.welcome', <b>{user.name}</b>); // => <>欢迎您，<b>张三</b>！</>'
```

## 项目结构

```
magicdb-client/
├── dist/                  # 前端构建输出目录
├── node_modules/          # Node.js 依赖
├── public/                # 静态资源
├── src/                   # 前端源代码
│   ├── assets/            # 资源文件
│   ├── blocks/            # 区块组件
│   ├── components/        # React 组件
│   ├── config/            # 配置文件
│   ├── constant/          # 常量定义
│   ├── layouts/           # 布局组件
│   ├── locales/           # 国际化文件
│   ├── main/              # 主进程代码
│   ├── models/            # 数据模型
│   ├── pages/             # 页面组件
│   ├── typings/           # 类型定义
│   └── utils/             # 工具函数
│       └── tauri-api.ts   # Tauri API 包装器
├── src-tauri/             # Tauri 应用源代码
│   ├── icons/             # 应用图标
│   ├── src/               # Rust 源代码
│   │   └── main.rs        # Tauri 应用入口
│   ├── build.rs           # Tauri 构建脚本
│   ├── Cargo.toml         # Rust 依赖配置
│   └── tauri.conf.json    # Tauri 配置
├── release/               # 发布输出目录
├── .gitignore             # Git 忽略文件
├── package.json           # Node.js 依赖配置
├── tsconfig.json          # TypeScript 配置
├── typings.d.ts           # 全局类型定义
└── README.md              # 项目说明
```

## Electron 到 Tauri 的迁移

本项目最初使用 Electron 开发，现已迁移到 Tauri。迁移的详细信息请参阅 [TAURI-MIGRATION.md](./TAURI-MIGRATION.md)。

Tauri 相比 Electron 有以下优势：

1. **更小的安装包体积**：Tauri 应用程序比 Electron 应用程序小得多，因为它使用系统的原生 WebView 而不是捆绑 Chromium。
2. **更好的性能**：Tauri 应用程序使用更少的内存和 CPU 资源。
3. **增强的安全性**：Tauri 具有更安全的架构，具有细粒度的权限控制。
4. **原生外观和感觉**：Tauri 应用程序在每个平台上看起来和感觉更加原生。
