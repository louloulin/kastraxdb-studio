# MagicDB UI 改进计划

## 概述

本文档提供了基于 Supabase UI 设计的 MagicDB 界面优化计划。我们将保留原有的功能接口，同时优化整体 UI 设计，支持黑白两种主题。

## 设计目标

1. 简化界面，提高用户体验
2. 采用更现代的设计语言
3. 优化黑白两种主题的视觉效果
4. 保持原有功能的完整性
5. 提高界面的一致性和专业性

## 主要改进点

### 1. 整体布局优化

#### 当前问题

- 左侧导航栏占用空间较大
- 工作区域分割不够清晰
- 视觉层次不够明确

#### 改进方案

- 采用 Supabase 风格的紧凑型侧边栏
- 优化工作区域的分割比例
- 增强视觉层次感，使用更清晰的分隔线和阴影

### 2. 主题系统优化

#### 当前问题

- 当前主题颜色不够协调
- 黑暗主题的对比度不够理想
- 主题切换不够流畅

#### 改进方案

- 重新设计颜色系统，参考 Supabase 的配色方案
- 优化黑暗主题的对比度和可读性
- 实现更平滑的主题切换效果

### 3. SQL 编辑器优化

#### 当前问题

- 编辑器与结果区域的分割不够直观
- 代码高亮不够醒目
- 工具栏布局不够紧凑

#### 改进方案

- 参考 Supabase 的编辑器设计，优化分割线
- 增强代码高亮的视觉效果
- 重新设计工具栏，使其更加紧凑和直观

### 4. 导航系统优化

#### 当前问题

- 导航图标不够直观
- 导航层次不够清晰
- 活动状态标识不够明显

#### 改进方案

- 使用更直观的导航图标
- 优化导航层次结构
- 增强活动状态的视觉反馈

### 5. 表格和数据展示优化

#### 当前问题

- 表格样式不够现代
- 数据展示不够清晰
- 交互反馈不够明显

#### 改进方案

- 采用更现代的表格设计
- 优化数据展示的排版和间距
- 增强交互反馈效果

## 具体实施计划

### 阶段一：主题系统重构

1. **重新设计颜色变量**

   - 修改 `src/theme/background/dark.ts` 和 `src/theme/background/light.ts`
   - 参考 Supabase 的配色方案
   - 优化颜色变量的命名和组织

2. **优化主题切换机制**
   - 改进 `src/theme/index.ts` 中的主题切换逻辑
   - 实现更平滑的过渡效果

### 阶段二：布局组件优化

1. **侧边导航栏重构**

   - 修改 `src/pages/main/index.tsx` 中的导航栏组件
   - 实现更紧凑的图标导航
   - 优化活动状态的视觉反馈

2. **工作区布局优化**
   - 修改 `src/pages/main/workspace/index.tsx` 中的布局结构
   - 优化分割比例和响应式行为
   - 增强视觉层次感

### 阶段三：SQL 编辑器优化

1. **编辑器界面优化**

   - 修改 `src/components/MonacoEditor/index.tsx` 中的编辑器样式
   - 优化 `src/components/MonacoEditor/monacoEditorConfig.ts` 中的配置
   - 增强代码高亮和编辑体验

2. **结果展示区优化**
   - 修改 `src/components/SearchResult/index.tsx` 中的结果展示组件
   - 优化数据表格的样式和交互
   - 增强视觉反馈效果

### 阶段四：组件样式统一

1. **按钮和表单元素统一**

   - 创建统一的按钮和表单样式组件
   - 替换现有组件中的样式定义

2. **图标系统优化**
   - 统一使用 Lucide React 图标库
   - 替换现有的图标组件

## 详细 Todo 列表

### 主题系统

- [x] 修改 `src/theme/background/dark.ts` 中的暗色主题配色
- [x] 修改 `src/theme/background/light.ts` 中的亮色主题配色
- [x] 优化 `src/theme/index.ts` 中的主题切换逻辑
- [x] 创建新的主题变量，确保在两种主题下的一致性

### 布局组件

- [x] 重构 `src/pages/main/index.tsx` 中的导航栏组件
- [x] 优化 `src/pages/main/workspace/index.tsx` 中的工作区布局
- [x] 修改 `src/components/DraggableContainer/index.tsx` 中的分割线样式
- [x] 优化 `src/pages/main/workspace/components/WorkspaceLeft/index.tsx` 中的树形结构

### SQL 编辑器

- [x] 修改 `src/components/MonacoEditor/index.tsx` 中的编辑器样式
- [x] 优化 `src/components/MonacoEditor/monacoEditorConfig.ts` 中的配置
- [x] 改进 `src/components/ConsoleEditor/index.tsx` 中的编辑器布局
- [x] 优化 `src/pages/main/workspace/components/SQLExecute/index.tsx` 中的执行按钮

### 数据展示

- [x] 修改 `src/components/SearchResult/index.tsx` 中的结果表格样式
- [x] 优化 `src/components/SearchResult/components/TableBox/index.tsx` 中的表格组件
- [x] 改进 `src/components/SearchResult/components/Pagination/index.tsx` 中的分页组件
- [x] 优化 `src/components/SearchResult/components/StatusBar/index.tsx` 中的状态栏

### 组件样式

- [x] 创建统一的按钮样式组件
- [x] 创建统一的表单元素样式组件
- [x] 优化 `src/components/Tabs/index.tsx` 中的标签页样式
- [x] 改进 `src/components/Modal/BaseModal/index.tsx` 中的模态框样式

## CSS 变量优化

为了实现更一致的设计系统，我们将定义以下 CSS 变量：

### 颜色变量

```css
/* 亮色主题 */
html[theme='light'] {
  --background-primary: #ffffff;
  --background-secondary: #f8f9fa;
  --background-tertiary: #f1f3f5;

  --foreground-primary: #1f2937;
  --foreground-secondary: #4b5563;
  --foreground-tertiary: #9ca3af;

  --border-primary: rgba(229, 231, 235, 0.8);
  --border-secondary: rgba(229, 231, 235, 0.5);

  --accent-primary: #3ecf8e;
  --accent-secondary: #10b981;

  --error-primary: #ef4444;
  --warning-primary: #f59e0b;
  --success-primary: #10b981;
}

/* 暗色主题 */
html[theme='dark'] {
  --background-primary: #1f1f1f;
  --background-secondary: #2a2a2a;
  --background-tertiary: #333333;

  --foreground-primary: #f3f4f6;
  --foreground-secondary: #d1d5db;
  --foreground-tertiary: #9ca3af;

  --border-primary: rgba(75, 85, 99, 0.8);
  --border-secondary: rgba(75, 85, 99, 0.5);

  --accent-primary: #3ecf8e;
  --accent-secondary: #10b981;

  --error-primary: #f87171;
  --warning-primary: #fbbf24;
  --success-primary: #34d399;
}
```

## 结论

通过以上优化计划，我们将显著提升 MagicDB 的用户界面体验，使其更加现代化、专业化，同时保持原有功能的完整性。这些改进将使 MagicDB 在视觉和交互上更接近 Supabase 的设计风格，提供更好的用户体验。
