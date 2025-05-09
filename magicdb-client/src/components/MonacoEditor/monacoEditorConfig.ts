import { IEditorOptions } from '@/components/MonacoEditor';

export const editorDefaultOptions: IEditorOptions = {
  fontFamily:
   `"JetBrains Mono", "Menlo",
    "DejaVu Sans Mono",
    "Liberation Mono",
    "Consolas",
    "Ubuntu Mono",
    "Courier New",
    "andale mono",
    "lucida console",
    "monospace"`,
  scrollBeyondLastLine: false, // 滚动超过最后一行
  automaticLayout: true, // 自动布局
  dragAndDrop: false, // 拖拽
  fontSize: 13, // 字体大小
  tabSize: 2, // tab大小
  lineHeight: 20, // 行高
  letterSpacing: 0.5, // 字母间距
  theme: 'vscode', // 主题
  roundedSelection: true, // 圆角选择
  readOnly: false, // 只读
  folding: true, // 显示折叠
  foldingHighlight: true, // 折叠高亮
  foldingStrategy: 'auto', // 折叠策略
  showFoldingControls: 'mouseover', // 显示折叠控件
  insertSpaces: true, // 插入空格
  autoClosingQuotes: 'always', // 自动闭合引号
  autoClosingBrackets: 'always', // 自动闭合括号
  autoIndent: 'advanced', // 自动缩进
  formatOnPaste: true, // 粘贴时格式化
  formatOnType: true, // 输入时格式化
  detectIndentation: false, // 检测缩进
  wordWrap: 'on', // 自动换行
  wordWrapColumn: 80, // 自动换行列
  wrappingIndent: 'same', // 换行缩进
  fixedOverflowWidgets: true, // 固定溢出小部件
  renderLineHighlight: 'all', // 渲染行高亮
  highlightActiveIndentGuide: true, // 高亮活动缩进指南
  renderIndentGuides: true, // 渲染缩进指南
  codeLens: false, // 代码镜头
  cursorBlinking: 'smooth', // 光标闪烁
  cursorSmoothCaretAnimation: true, // 光标平滑动画
  scrollbar: {
    // 滚动条
    useShadows: true, // 使用阴影
    verticalHasArrows: false, // 垂直箭头
    horizontalHasArrows: false, // 水平箭头
    vertical: 'auto', // 垂直滚动条
    horizontal: 'auto', // 水平滚动条
    verticalScrollbarSize: 10, // 垂直滚动条大小
    horizontalScrollbarSize: 10, // 水平滚动条大小
    alwaysConsumeMouseWheel: false, // 总是消耗鼠标滚轮
  },
  unicodeHighlight: {
    ambiguousCharacters: false,
    invisibleCharacters: false,
  },
  padding: {
    top: 12,
    bottom: 12,
  },
  minimap: {
    // 缩略图
    enabled: false, // 禁用
  },
  hover: {
    enabled: true, // 启用悬停
    delay: 300, // 延迟
    sticky: true, // 粘性
  },
  find: {
    addExtraSpaceOnTop: true, // 在顶部添加额外空间
    autoFindInSelection: 'never', // 自动在选择中查找
    seedSearchStringFromSelection: 'always', // 从选择中种子搜索字符串
  },
  suggestSelection: 'first', // 建议选择
  suggest: {
    showIcons: true, // 显示图标
    showStatusBar: true, // 显示状态栏
    preview: true, // 预览
    filterGraceful: true, // 优雅过滤
    snippetsPreventQuickSuggestions: false, // 片段防止快速建议
  },
};
