# 数据服务前端实现计划

## 1. 现状分析

### 1.1 已实现功能

目前 magicdb-client 前端已经实现了以下数据服务相关功能：

1. **服务管理界面**
   - 服务树组件 (ServiceTree)：展示服务列表和分组
   - 服务编辑器组件 (ServiceEditor)：编辑服务脚本
   - 服务参数组件 (ServiceParams)：配置服务参数
   - 服务导入/导出功能
   - 服务历史记录功能

2. **服务测试工具**
   - 服务测试组件 (ServiceTest)：测试服务执行
   - 参数配置界面
   - 结果展示界面

3. **API 调用**
   - 数据服务相关的 API 调用已经在 service/data-service.ts 中实现

### 1.2 存在问题

1. **文档功能不完善**
   - 缺少专门的数据服务文档页面
   - 缺少 Swagger/OpenAPI 集成
   - 缺少文档导出功能

2. **脚本编辑器功能不足**
   - 缺少语法高亮和代码补全
   - 缺少调试功能
   - 缺少代码片段支持

3. **数据源集成不完善**
   - 缺少与数据源的集成
   - 缺少数据库表和字段的自动补全

4. **UI/UX 问题**
   - 界面设计不够现代化
   - 缺少响应式设计
   - 国际化支持不完善

5. **功能缺失**
   - 缺少服务监控和统计功能
   - 缺少批量操作功能
   - 缺少权限控制

## 2. 实现计划

### 2.1 服务文档功能

#### 2.1.1 服务文档页面

- [x] **实现服务文档页面 (doc.tsx)**
  - 左侧服务树，右侧文档内容
  - 支持按服务分组展示
  - 支持搜索功能

- [x] **实现服务文档组件 (ServiceDoc)**
  - 展示服务基本信息
  - 展示服务参数信息
  - 展示服务返回值信息
  - 展示示例代码

- [x] **实现 Swagger/OpenAPI 集成**
  - 生成 Swagger/OpenAPI 规范的文档
  - 提供 Swagger UI 界面

- [x] **实现文档导出功能**
  - 支持导出为 Markdown
  - 支持导出为 HTML
  - 支持导出为 PDF

#### 2.1.2 API 文档测试工具

- [x] **实现 API 测试工具**
  - 在文档页面中集成测试功能
  - 支持参数填写和验证
  - 支持发送请求并展示结果

### 2.2 脚本编辑器增强

- [x] **增强 Monaco Editor 集成**
  - 完善语法高亮
  - 实现代码补全
  - 实现错误提示

- [x] **实现调试功能**
  - 添加断点支持
  - 实现变量查看
  - 实现单步执行

- [x] **实现代码片段功能**
  - 预定义常用代码片段
  - 支持自定义代码片段
  - 实现代码片段管理

- [ ] **实现多语言支持**
  - JavaScript
  - Python
  - Kotlin
  - SQL

### 2.3 数据源集成

- [x] **实现数据源选择器**
  - 集成现有数据源列表
  - 支持数据源筛选和搜索

- [x] **实现数据库对象浏览器**
  - 展示数据库表和视图
  - 展示表字段和索引
  - 支持拖拽到编辑器

- [ ] **实现自动补全**
  - 表名自动补全
  - 字段名自动补全
  - SQL 语法自动补全

### 2.4 UI/UX 改进

- [ ] **现代化界面设计**
  - 更新组件样式
  - 优化布局和间距
  - 添加动画和过渡效果

- [ ] **响应式设计**
  - 适配不同屏幕尺寸
  - 支持移动设备访问

- [ ] **完善国际化**
  - 补充缺失的翻译
  - 支持更多语言
  - 优化翻译质量

### 2.5 功能扩展

- [ ] **实现服务监控和统计**
  - 服务调用次数统计
  - 服务执行时间统计
  - 服务错误率统计
  - 可视化图表展示

- [ ] **实现批量操作**
  - 批量导入/导出
  - 批量启用/禁用
  - 批量删除

- [ ] **实现权限控制**
  - 服务级别权限
  - 分组级别权限
  - 操作级别权限

## 3. 优先级和时间安排

### 3.1 第一阶段（2周）

- [x] 服务文档页面实现
- [x] Swagger/OpenAPI 集成
- [x] 文档导出功能

### 3.2 第二阶段（2周）

- [x] 脚本编辑器语法高亮和代码补全
- [x] 数据源选择器
- [x] 数据库对象浏览器

### 3.3 第三阶段（2周）

- [x] API 文档测试工具
- [x] 脚本调试功能
- [x] 代码片段功能

### 3.4 第四阶段（2周）

- [ ] UI/UX 改进
- [ ] 响应式设计
- [ ] 国际化完善

### 3.5 第五阶段（2周）

- [ ] 服务监控和统计
- [ ] 批量操作功能
- [ ] 权限控制

## 4. 技术栈和依赖

- **前端框架**：React
- **UI 组件库**：Ant Design
- **状态管理**：React Hooks
- **代码编辑器**：Monaco Editor
- **图表库**：Ant Design Charts
- **HTTP 客户端**：Axios
- **国际化**：i18next
- **文档生成**：Swagger UI, react-markdown

## 5. 组件设计

### 5.1 服务文档页面

```tsx
// doc.tsx
import React, { useState, useEffect } from 'react';
import { Layout } from 'antd';
import ServiceDocTree from './components/ServiceDocTree';
import ServiceDocContent from './components/ServiceDocContent';
import { getServiceList } from '@/service/data-service';

const { Sider, Content } = Layout;

const ServiceDocPage: React.FC = () => {
  const [serviceList, setServiceList] = useState<any[]>([]);
  const [selectedService, setSelectedService] = useState<any>(null);

  useEffect(() => {
    fetchServiceList();
  }, []);

  const fetchServiceList = async () => {
    // 获取服务列表
  };

  const handleSelectService = (serviceId: string) => {
    // 选择服务
  };

  return (
    <Layout>
      <Sider>
        <ServiceDocTree
          serviceList={serviceList}
          onSelectService={handleSelectService}
        />
      </Sider>
      <Content>
        <ServiceDocContent service={selectedService} />
      </Content>
    </Layout>
  );
};

export default ServiceDocPage;
```

### 5.2 服务文档组件

```tsx
// ServiceDocContent.tsx
import React from 'react';
import { Tabs, Card, Table, Tag, Button } from 'antd';
import SwaggerUI from 'swagger-ui-react';

const { TabPane } = Tabs;

interface ServiceDocContentProps {
  service: any;
}

const ServiceDocContent: React.FC<ServiceDocContentProps> = ({ service }) => {
  if (!service) {
    return <div>请选择一个服务</div>;
  }

  return (
    <Tabs defaultActiveKey="info">
      <TabPane tab="基本信息" key="info">
        {/* 服务基本信息 */}
      </TabPane>
      <TabPane tab="API 文档" key="api">
        {/* Swagger UI */}
        <SwaggerUI spec={service.swaggerSpec} />
      </TabPane>
      <TabPane tab="测试" key="test">
        {/* API 测试工具 */}
      </TabPane>
      <TabPane tab="示例代码" key="examples">
        {/* 示例代码 */}
      </TabPane>
    </Tabs>
  );
};

export default ServiceDocContent;
```

### 5.3 增强的脚本编辑器

```tsx
// EnhancedScriptEditor.tsx
import React, { useRef, useEffect } from 'react';
import * as monaco from 'monaco-editor';
import { Button, Tooltip } from 'antd';
import { DebugOutlined, PlayCircleOutlined, SaveOutlined } from '@ant-design/icons';

interface EnhancedScriptEditorProps {
  value: string;
  language: string;
  onChange: (value: string) => void;
  onExecute?: () => void;
  onDebug?: () => void;
  onSave?: () => void;
}

const EnhancedScriptEditor: React.FC<EnhancedScriptEditorProps> = ({
  value,
  language,
  onChange,
  onExecute,
  onDebug,
  onSave,
}) => {
  const editorRef = useRef<monaco.editor.IStandaloneCodeEditor | null>(null);
  const containerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    // 初始化编辑器
  }, []);

  return (
    <div className="enhanced-script-editor">
      <div className="editor-toolbar">
        <Tooltip title="执行">
          <Button icon={<PlayCircleOutlined />} onClick={onExecute} />
        </Tooltip>
        <Tooltip title="调试">
          <Button icon={<DebugOutlined />} onClick={onDebug} />
        </Tooltip>
        <Tooltip title="保存">
          <Button icon={<SaveOutlined />} onClick={onSave} />
        </Tooltip>
      </div>
      <div ref={containerRef} className="editor-container" />
    </div>
  );
};

export default EnhancedScriptEditor;
```

## 6. API 设计

### 6.1 文档相关 API

```typescript
// service/doc.ts

// 获取服务文档
export async function getServiceDoc(serviceId: string) {
  return request(`/api/data-service/${serviceId}/doc`);
}

// 获取服务 Swagger 规范
export async function getServiceSwagger(serviceId: string) {
  return request(`/api/data-service/${serviceId}/swagger`);
}

// 导出服务文档
export async function exportServiceDoc(serviceId: string, format: string) {
  return request(`/api/data-service/${serviceId}/doc/export`, {
    method: 'GET',
    params: { format },
  });
}
```

### 6.2 调试相关 API

```typescript
// service/debug.ts

// 开始调试会话
export async function startDebugSession(serviceId: string) {
  return request(`/api/data-service/${serviceId}/debug/start`, {
    method: 'POST',
  });
}

// 设置断点
export async function setBreakpoint(sessionId: string, line: number, column: number) {
  return request(`/api/data-service/debug/${sessionId}/breakpoint`, {
    method: 'POST',
    data: { line, column },
  });
}

// 执行下一步
export async function stepNext(sessionId: string) {
  return request(`/api/data-service/debug/${sessionId}/step`, {
    method: 'POST',
  });
}

// 获取变量
export async function getVariables(sessionId: string) {
  return request(`/api/data-service/debug/${sessionId}/variables`);
}

// 结束调试会话
export async function endDebugSession(sessionId: string) {
  return request(`/api/data-service/debug/${sessionId}/end`, {
    method: 'POST',
  });
}
```

## 7. 数据模型

### 7.1 服务文档模型

```typescript
interface ServiceDoc {
  id: string;
  serviceId: string;
  title: string;
  description: string;
  version: string;
  content: string;
  format: string;
  createdAt: string;
  updatedAt: string;
  createdBy: string;
  updatedBy: string;
}
```

### 7.2 服务监控模型

```typescript
interface ServiceMetrics {
  serviceId: string;
  totalCalls: number;
  successCalls: number;
  failedCalls: number;
  avgResponseTime: number;
  minResponseTime: number;
  maxResponseTime: number;
  p95ResponseTime: number;
  p99ResponseTime: number;
  lastCalledAt: string;
  timeRange: string;
}
```

### 7.3 调试会话模型

```typescript
interface DebugSession {
  id: string;
  serviceId: string;
  status: 'running' | 'paused' | 'stopped';
  currentLine: number;
  currentColumn: number;
  breakpoints: Array<{ line: number; column: number }>;
  variables: Array<{ name: string; value: any; type: string }>;
  callStack: Array<{ name: string; line: number; column: number }>;
  startedAt: string;
}
```

## 8. 测试计划

### 8.1 单元测试

- 为每个新组件编写单元测试
- 测试组件渲染和交互
- 测试 API 调用和错误处理

### 8.2 集成测试

- 测试组件之间的交互
- 测试与后端 API 的集成
- 测试数据流和状态管理

### 8.3 端到端测试

- 测试完整的用户流程
- 测试跨页面交互
- 测试真实环境下的性能

## 9. 部署和发布计划

### 9.1 开发环境

- 持续集成和部署
- 自动化测试
- 代码质量检查

### 9.2 测试环境

- 每周发布新功能
- 回归测试
- 性能测试

### 9.3 生产环境

- 每两周发布稳定版本
- 灰度发布
- 监控和告警

## 10. 交互设计详细说明

### 10.1 服务文档页面交互

#### 10.1.1 文档浏览流程

1. 用户进入文档页面，左侧显示服务树，右侧显示欢迎信息或说明
2. 用户从左侧树中选择一个服务，右侧加载该服务的文档
3. 用户可以通过顶部标签页切换查看不同类型的文档内容
4. 用户可以点击“导出”按钮，选择导出格式（Markdown、HTML、PDF）

#### 10.1.2 API 测试流程

1. 用户在文档页面中切换到“测试”标签页
2. 系统自动根据服务参数生成表单
3. 用户填写参数值
4. 用户点击“发送请求”按钮
5. 系统发送请求并展示响应结果
6. 用户可以保存测试用例供后续使用

### 10.2 脚本编辑器交互

#### 10.2.1 代码编辑流程

1. 用户在编辑器中编写代码
2. 系统实时提供语法高亮和错误提示
3. 用户可以使用快捷键或右键菜单触发代码补全
4. 用户可以使用快捷键格式化代码
5. 用户可以通过工具栏按钮保存代码

#### 10.2.2 调试流程

1. 用户点击“调试”按钮启动调试会话
2. 用户可以在代码行号旁边点击设置断点
3. 用户点击“运行”按钮开始执行代码
4. 代码执行到断点处暂停，显示当前变量值
5. 用户可以使用“单步执行”、“跳过”、“继续”等按钮控制执行流程
6. 用户可以在变量面板查看和修改变量值
7. 用户可以随时终止调试会话

### 10.3 数据源集成交互

#### 10.3.1 数据源选择流程

1. 用户在服务编辑页面选择“数据源”下拉框
2. 系统显示可用的数据源列表
3. 用户选择一个数据源后，系统加载该数据源的数据库列表
4. 用户选择数据库后，系统加载该数据库的表和视图列表
5. 用户可以展开表查看字段列表
6. 用户可以将表或字段拖拽到编辑器中，自动生成相应的代码

#### 10.3.2 SQL 辅助流程

1. 用户在编辑器中输入 SQL 关键字
2. 系统提供 SQL 语法补全建议
3. 用户输入表名的一部分，系统提供匹配的表名列表
4. 用户在表名后输入点号，系统提供该表的字段列表
5. 用户可以使用快捷键格式化 SQL 语句

## 11. 组件状态管理

### 11.1 服务文档页面状态

```typescript
// 服务文档页面状态
interface ServiceDocPageState {
  // 服务列表
  serviceList: ServiceItem[];
  // 服务分组
  serviceGroups: ServiceGroup[];
  // 当前选中的服务
  selectedService: ServiceItem | null;
  // 当前选中的服务文档
  serviceDoc: ServiceDoc | null;
  // 加载状态
  loading: {
    serviceList: boolean;
    serviceDoc: boolean;
    exportDoc: boolean;
  };
  // 错误信息
  error: {
    serviceList: string | null;
    serviceDoc: string | null;
    exportDoc: string | null;
  };
  // 导出格式
  exportFormat: 'markdown' | 'html' | 'pdf';
  // API 测试参数
  testParams: Record<string, any>;
  // API 测试结果
  testResult: {
    status: number;
    data: any;
    time: number;
  } | null;
}
```

### 11.2 脚本编辑器状态

```typescript
// 脚本编辑器状态
interface ScriptEditorState {
  // 代码内容
  code: string;
  // 语言
  language: string;
  // 是否修改未保存
  isDirty: boolean;
  // 光标位置
  cursor: {
    line: number;
    column: number;
  };
  // 选中内容
  selection: {
    startLine: number;
    startColumn: number;
    endLine: number;
    endColumn: number;
  } | null;
  // 调试状态
  debug: {
    active: boolean;
    sessionId: string | null;
    breakpoints: Array<{ line: number; column: number }>;
    currentLine: number;
    currentColumn: number;
    variables: Array<{ name: string; value: any; type: string }>;
    callStack: Array<{ name: string; line: number; column: number }>;
    status: 'running' | 'paused' | 'stopped';
  };
  // 代码补全
  completion: {
    active: boolean;
    suggestions: Array<{ label: string; kind: string; detail: string; insertText: string }>;
  };
}
```

## 12. 主题和样式设计

### 12.1 颜色方案

- **主色调**：#1890ff（蓝色）
- **次要色调**：#52c41a（绿色）
- **警告色**：#faad14（黄色）
- **错误色**：#f5222d（红色）
- **背景色**：#f0f2f5（浅灰色）
- **文本色**：#000000d9（深灰色）
- **边框色**：#d9d9d9（中灰色）

### 12.2 组件样式

#### 12.2.1 服务树

```less
.service-tree {
  height: 100%;
  overflow: auto;

  .service-tree-header {
    padding: 12px 16px;
    border-bottom: 1px solid #f0f0f0;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .service-tree-search {
      width: 180px;
    }
  }

  .service-tree-content {
    padding: 8px 0;

    .ant-tree-node-content-wrapper {
      display: flex;
      align-items: center;

      .ant-tree-title {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
  }
}
```

#### 12.2.2 服务文档

```less
.service-doc {
  height: 100%;
  display: flex;
  flex-direction: column;

  .service-doc-header {
    padding: 16px;
    border-bottom: 1px solid #f0f0f0;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .service-doc-title {
      font-size: 18px;
      font-weight: 500;
      display: flex;
      align-items: center;

      .anticon {
        margin-right: 8px;
        color: #1890ff;
      }
    }

    .service-doc-actions {
      display: flex;
      gap: 8px;
    }
  }

  .service-doc-content {
    flex: 1;
    overflow: auto;
    padding: 16px;
  }
}
```

#### 12.2.3 脚本编辑器

```less
.script-editor {
  height: 100%;
  display: flex;
  flex-direction: column;

  .script-editor-toolbar {
    padding: 8px;
    border-bottom: 1px solid #f0f0f0;
    display: flex;
    gap: 8px;
    align-items: center;

    .script-editor-language {
      margin-left: auto;
      width: 120px;
    }
  }

  .script-editor-container {
    flex: 1;
    overflow: hidden;
    position: relative;
  }

  .script-editor-debug-panel {
    height: 200px;
    border-top: 1px solid #f0f0f0;
    overflow: auto;
  }
}
```

## 13. 性能优化策略

### 13.1 代码分割

- 使用 React.lazy 和 Suspense 实现组件懒加载
- 将大型依赖（如 Monaco Editor）单独打包
- 按路由分割代码，实现按需加载

### 13.2 渲染优化

- 使用 React.memo 避免不必要的重渲染
- 使用 useMemo 和 useCallback 缓存计算结果和回调函数
- 实现虚拟滚动，优化长列表渲染

### 13.3 数据加载优化

- 实现数据预加载，提前加载可能需要的数据
- 使用缓存减少重复请求
- 实现分页加载，避免一次加载大量数据

### 13.4 资源优化

- 压缩图片和静态资源
- 使用 CDN 加速静态资源加载
- 实现资源预加载和懒加载

## 14. 风险和缓解措施

### 14.1 技术风险

- **风险**：Monaco Editor 集成复杂
- **缓解**：先实现基本功能，逐步添加高级特性

### 14.2 进度风险

- **风险**：功能点过多，难以按时完成
- **缓解**：优先实现核心功能，其他功能可以后续迭代

### 14.3 用户体验风险

- **风险**：复杂功能可能影响用户体验
- **缓解**：进行用户测试，收集反馈，持续优化

### 14.4 兼容性风险

- **风险**：不同浏览器兼容性问题
- **缓解**：使用 Polyfill 和兼容性处理，进行跨浏览器测试

### 14.5 性能风险

- **风险**：复杂组件可能导致性能问题
- **缓解**：实施性能优化策略，定期进行性能测试