import React, { useState, useEffect, useRef } from 'react';
import { Layout, Button, Spin, Tabs, message, Tooltip, Space, Collapse, Tree, Table, Input, Modal } from 'antd';
import { 
  PlayCircleOutlined, 
  PauseCircleOutlined, 
  StepForwardOutlined, 
  StepBackwardOutlined,
  VerticalRightOutlined,
  StopOutlined,
  BugOutlined,
  PlusOutlined,
  DeleteOutlined,
  EditOutlined,
  CheckOutlined
} from '@ant-design/icons';
import * as monaco from 'monaco-editor';
import { 
  createDebugSession, 
  terminateDebugSession, 
  addBreakpoint, 
  removeBreakpoint,
  getBreakpoints,
  stepOver,
  stepInto,
  stepOut,
  continueExecution,
  getVariables,
  getCallStack,
  evaluateExpression
} from '@/service/script-debug';
import i18n from '@/i18n';
import styles from './index.less';

const { Sider, Content } = Layout;
const { TabPane } = Tabs;
const { Panel } = Collapse;

interface ScriptDebuggerProps {
  serviceId: string;
  script: string;
  language: string;
  parameters: Record<string, any>;
  onClose: () => void;
  className?: string;
  style?: React.CSSProperties;
}

const ScriptDebugger: React.FC<ScriptDebuggerProps> = ({
  serviceId,
  script,
  language,
  parameters,
  onClose,
  className,
  style
}) => {
  const editorRef = useRef<monaco.editor.IStandaloneCodeEditor | null>(null);
  const containerRef = useRef<HTMLDivElement>(null);
  const [sessionId, setSessionId] = useState<string>('');
  const [loading, setLoading] = useState<boolean>(false);
  const [debugging, setDebugging] = useState<boolean>(false);
  const [paused, setPaused] = useState<boolean>(false);
  const [currentLine, setCurrentLine] = useState<number | null>(null);
  const [breakpoints, setBreakpoints] = useState<any[]>([]);
  const [variables, setVariables] = useState<any[]>([]);
  const [callStack, setCallStack] = useState<any[]>([]);
  const [activeTab, setActiveTab] = useState<string>('variables');
  const [expressionValue, setExpressionValue] = useState<string>('');
  const [expressionResult, setExpressionResult] = useState<any>(null);
  const [breakpointModalVisible, setBreakpointModalVisible] = useState<boolean>(false);
  const [editingBreakpoint, setEditingBreakpoint] = useState<any>(null);
  const [breakpointCondition, setBreakpointCondition] = useState<string>('');
  const [messageApi, contextHolder] = message.useMessage();
  
  // 初始化编辑器
  useEffect(() => {
    if (containerRef.current) {
      // 创建编辑器
      const editor = monaco.editor.create(containerRef.current, {
        value: script,
        language: mapLanguage(language),
        theme: 'vs-dark',
        readOnly: true,
        minimap: { enabled: true },
        lineNumbers: 'on',
        glyphMargin: true,
        folding: true
      });
      
      // 保存编辑器实例
      editorRef.current = editor;
      
      // 添加断点处理
      editor.onMouseDown((e) => {
        if (e.target.type === monaco.editor.MouseTargetType.GUTTER_GLYPH_MARGIN) {
          const lineNumber = e.target.position?.lineNumber;
          if (lineNumber) {
            toggleBreakpoint(lineNumber);
          }
        }
      });
      
      // 清理函数
      return () => {
        editor.dispose();
      };
    }
  }, []);
  
  // 开始调试
  const startDebugging = async () => {
    try {
      setLoading(true);
      
      // 创建调试会话
      const response = await createDebugSession(serviceId, parameters);
      
      if (response && response.success) {
        setSessionId(response.data.id);
        setDebugging(true);
        messageApi.success(i18n('script-debug.start-success'));
        
        // 获取断点
        fetchBreakpoints(response.data.id);
      } else {
        messageApi.error(i18n('script-debug.start-failed'));
      }
    } catch (error) {
      console.error('Failed to start debugging:', error);
      messageApi.error(i18n('script-debug.start-error'));
    } finally {
      setLoading(false);
    }
  };
  
  // 停止调试
  const stopDebugging = async () => {
    if (!sessionId) return;
    
    try {
      setLoading(true);
      
      // 终止调试会话
      const response = await terminateDebugSession(sessionId);
      
      if (response && response.success) {
        setSessionId('');
        setDebugging(false);
        setPaused(false);
        setCurrentLine(null);
        setVariables([]);
        setCallStack([]);
        clearBreakpointDecorations();
        messageApi.success(i18n('script-debug.stop-success'));
      } else {
        messageApi.error(i18n('script-debug.stop-failed'));
      }
    } catch (error) {
      console.error('Failed to stop debugging:', error);
      messageApi.error(i18n('script-debug.stop-error'));
    } finally {
      setLoading(false);
    }
  };
  
  // 获取断点
  const fetchBreakpoints = async (sid: string) => {
    try {
      const response = await getBreakpoints(sid);
      
      if (response && response.success) {
        setBreakpoints(response.data || []);
        
        // 更新断点装饰
        updateBreakpointDecorations(response.data || []);
      }
    } catch (error) {
      console.error('Failed to fetch breakpoints:', error);
    }
  };
  
  // 切换断点
  const toggleBreakpoint = async (lineNumber: number) => {
    if (!sessionId) return;
    
    // 检查是否已存在断点
    const existingBreakpoint = breakpoints.find(bp => bp.line === lineNumber);
    
    if (existingBreakpoint) {
      // 删除断点
      try {
        const response = await removeBreakpoint(sessionId, existingBreakpoint.id);
        
        if (response && response.success) {
          // 更新断点列表
          const updatedBreakpoints = breakpoints.filter(bp => bp.id !== existingBreakpoint.id);
          setBreakpoints(updatedBreakpoints);
          
          // 更新断点装饰
          updateBreakpointDecorations(updatedBreakpoints);
        }
      } catch (error) {
        console.error('Failed to remove breakpoint:', error);
        messageApi.error(i18n('script-debug.breakpoint-remove-error'));
      }
    } else {
      // 添加断点
      try {
        const breakpoint = {
          id: `bp-${Date.now()}`,
          line: lineNumber,
          enabled: true
        };
        
        const response = await addBreakpoint(sessionId, breakpoint);
        
        if (response && response.success) {
          // 更新断点列表
          const updatedBreakpoints = [...breakpoints, breakpoint];
          setBreakpoints(updatedBreakpoints);
          
          // 更新断点装饰
          updateBreakpointDecorations(updatedBreakpoints);
        }
      } catch (error) {
        console.error('Failed to add breakpoint:', error);
        messageApi.error(i18n('script-debug.breakpoint-add-error'));
      }
    }
  };
  
  // 编辑断点
  const editBreakpoint = (breakpoint: any) => {
    setEditingBreakpoint(breakpoint);
    setBreakpointCondition(breakpoint.condition || '');
    setBreakpointModalVisible(true);
  };
  
  // 保存断点
  const saveBreakpoint = async () => {
    if (!sessionId || !editingBreakpoint) return;
    
    try {
      const updatedBreakpoint = {
        ...editingBreakpoint,
        condition: breakpointCondition
      };
      
      // 先删除旧断点
      await removeBreakpoint(sessionId, editingBreakpoint.id);
      
      // 添加新断点
      const response = await addBreakpoint(sessionId, updatedBreakpoint);
      
      if (response && response.success) {
        // 更新断点列表
        const updatedBreakpoints = breakpoints.map(bp => 
          bp.id === editingBreakpoint.id ? updatedBreakpoint : bp
        );
        setBreakpoints(updatedBreakpoints);
        
        // 更新断点装饰
        updateBreakpointDecorations(updatedBreakpoints);
        
        setBreakpointModalVisible(false);
        messageApi.success(i18n('script-debug.breakpoint-update-success'));
      }
    } catch (error) {
      console.error('Failed to update breakpoint:', error);
      messageApi.error(i18n('script-debug.breakpoint-update-error'));
    }
  };
  
  // 更新断点装饰
  const updateBreakpointDecorations = (bps: any[]) => {
    if (!editorRef.current) return;
    
    // 清除现有断点装饰
    clearBreakpointDecorations();
    
    // 添加断点装饰
    const decorations = bps.map(bp => ({
      range: new monaco.Range(bp.line, 1, bp.line, 1),
      options: {
        isWholeLine: false,
        glyphMarginClassName: styles.breakpoint
      }
    }));
    
    editorRef.current.deltaDecorations([], decorations);
  };
  
  // 清除断点装饰
  const clearBreakpointDecorations = () => {
    if (!editorRef.current) return;
    
    editorRef.current.deltaDecorations(
      editorRef.current.getModel()?.getAllDecorations().filter(d => 
        d.options.glyphMarginClassName === styles.breakpoint
      ).map(d => d.id) || [],
      []
    );
  };
  
  // 更新当前行装饰
  const updateCurrentLineDecoration = (line: number | null) => {
    if (!editorRef.current || line === null) return;
    
    // 清除现有当前行装饰
    clearCurrentLineDecoration();
    
    // 添加当前行装饰
    const decorations = [{
      range: new monaco.Range(line, 1, line, 1),
      options: {
        isWholeLine: true,
        className: styles.currentLine
      }
    }];
    
    editorRef.current.deltaDecorations([], decorations);
    
    // 滚动到当前行
    editorRef.current.revealLineInCenter(line);
  };
  
  // 清除当前行装饰
  const clearCurrentLineDecoration = () => {
    if (!editorRef.current) return;
    
    editorRef.current.deltaDecorations(
      editorRef.current.getModel()?.getAllDecorations().filter(d => 
        d.options.className === styles.currentLine
      ).map(d => d.id) || [],
      []
    );
  };
  
  // 单步执行
  const handleStepOver = async () => {
    if (!sessionId) return;
    
    try {
      setLoading(true);
      
      const response = await stepOver(sessionId);
      
      if (response && response.success) {
        handleStepResult(response.data);
      } else {
        messageApi.error(i18n('script-debug.step-failed'));
      }
    } catch (error) {
      console.error('Failed to step over:', error);
      messageApi.error(i18n('script-debug.step-error'));
    } finally {
      setLoading(false);
    }
  };
  
  // 单步进入
  const handleStepInto = async () => {
    if (!sessionId) return;
    
    try {
      setLoading(true);
      
      const response = await stepInto(sessionId);
      
      if (response && response.success) {
        handleStepResult(response.data);
      } else {
        messageApi.error(i18n('script-debug.step-failed'));
      }
    } catch (error) {
      console.error('Failed to step into:', error);
      messageApi.error(i18n('script-debug.step-error'));
    } finally {
      setLoading(false);
    }
  };
  
  // 单步跳出
  const handleStepOut = async () => {
    if (!sessionId) return;
    
    try {
      setLoading(true);
      
      const response = await stepOut(sessionId);
      
      if (response && response.success) {
        handleStepResult(response.data);
      } else {
        messageApi.error(i18n('script-debug.step-failed'));
      }
    } catch (error) {
      console.error('Failed to step out:', error);
      messageApi.error(i18n('script-debug.step-error'));
    } finally {
      setLoading(false);
    }
  };
  
  // 继续执行
  const handleContinue = async () => {
    if (!sessionId) return;
    
    try {
      setLoading(true);
      
      const response = await continueExecution(sessionId);
      
      if (response && response.success) {
        handleStepResult(response.data);
      } else {
        messageApi.error(i18n('script-debug.continue-failed'));
      }
    } catch (error) {
      console.error('Failed to continue:', error);
      messageApi.error(i18n('script-debug.continue-error'));
    } finally {
      setLoading(false);
    }
  };
  
  // 处理步骤结果
  const handleStepResult = (result: any) => {
    // 更新状态
    setPaused(result.status === 'PAUSED');
    
    // 更新当前行
    if (result.line) {
      setCurrentLine(result.line);
      updateCurrentLineDecoration(result.line);
    } else {
      setCurrentLine(null);
      clearCurrentLineDecoration();
    }
    
    // 更新变量
    if (result.variables) {
      setVariables(result.variables);
    }
    
    // 更新调用栈
    if (result.callStack) {
      setCallStack(result.callStack);
    }
    
    // 检查是否完成
    if (result.status === 'COMPLETED') {
      messageApi.success(i18n('script-debug.execution-completed'));
      
      // 如果有结果，显示
      if (result.result !== undefined) {
        console.log('Execution result:', result.result);
      }
    } else if (result.status === 'ERROR') {
      messageApi.error(result.errorMessage || i18n('script-debug.execution-error'));
    }
  };
  
  // 执行表达式
  const handleEvaluateExpression = async () => {
    if (!sessionId || !expressionValue) return;
    
    try {
      setLoading(true);
      
      const response = await evaluateExpression(sessionId, expressionValue);
      
      if (response && response.success) {
        setExpressionResult(response.data);
      } else {
        messageApi.error(i18n('script-debug.evaluate-failed'));
      }
    } catch (error) {
      console.error('Failed to evaluate expression:', error);
      messageApi.error(i18n('script-debug.evaluate-error'));
    } finally {
      setLoading(false);
    }
  };
  
  // 映射语言
  const mapLanguage = (lang: string): string => {
    const languageMap: Record<string, string> = {
      'js': 'javascript',
      'javascript': 'javascript',
      'ts': 'typescript',
      'typescript': 'typescript',
      'kotlin': 'kotlin',
      'python': 'python',
      'py': 'python'
    };
    
    return languageMap[lang.toLowerCase()] || lang;
  };
  
  // 渲染变量树
  const renderVariableTree = () => {
    const formatValue = (value: any, type: string) => {
      if (value === null || value === undefined) {
        return <span className={styles.nullValue}>null</span>;
      }
      
      switch (type) {
        case 'string':
          return <span className={styles.stringValue}>"{value}"</span>;
        case 'number':
          return <span className={styles.numberValue}>{value}</span>;
        case 'boolean':
          return <span className={styles.booleanValue}>{value.toString()}</span>;
        case 'object':
        case 'array':
          return <span className={styles.objectValue}>{JSON.stringify(value)}</span>;
        default:
          return <span>{String(value)}</span>;
      }
    };
    
    const buildTreeData = (vars: any[]) => {
      return vars.map(variable => ({
        key: variable.name,
        title: (
          <span>
            <span className={styles.variableName}>{variable.name}</span>
            <span className={styles.variableValue}>
              {formatValue(variable.value, variable.type)}
            </span>
            <span className={styles.variableType}>{variable.type}</span>
          </span>
        ),
        children: variable.children && variable.children.length > 0
          ? buildTreeData(variable.children)
          : undefined
      }));
    };
    
    return (
      <div className={styles.variablesContainer}>
        {variables.length > 0 ? (
          <Tree
            showLine
            defaultExpandAll
            treeData={buildTreeData(variables)}
          />
        ) : (
          <div className={styles.emptyMessage}>
            {i18n('script-debug.no-variables')}
          </div>
        )}
      </div>
    );
  };
  
  // 渲染调用栈
  const renderCallStack = () => {
    const columns = [
      {
        title: i18n('script-debug.function'),
        dataIndex: 'name',
        key: 'name',
      },
      {
        title: i18n('script-debug.location'),
        dataIndex: 'source',
        key: 'source',
        render: (text: string, record: any) => (
          <span>
            {text}:{record.line}:{record.column}
          </span>
        ),
      }
    ];
    
    return (
      <div className={styles.callStackContainer}>
        {callStack.length > 0 ? (
          <Table
            dataSource={callStack}
            columns={columns}
            pagination={false}
            size="small"
            rowKey="name"
          />
        ) : (
          <div className={styles.emptyMessage}>
            {i18n('script-debug.no-call-stack')}
          </div>
        )}
      </div>
    );
  };
  
  // 渲染断点
  const renderBreakpoints = () => {
    const columns = [
      {
        title: i18n('script-debug.line'),
        dataIndex: 'line',
        key: 'line',
      },
      {
        title: i18n('script-debug.condition'),
        dataIndex: 'condition',
        key: 'condition',
        render: (text: string) => text || '-',
      },
      {
        title: i18n('script-debug.hit-count'),
        dataIndex: 'hitCount',
        key: 'hitCount',
        render: (text: number) => text || 0,
      },
      {
        title: i18n('script-debug.actions'),
        key: 'actions',
        render: (_: any, record: any) => (
          <Space>
            <Button
              type="text"
              icon={<EditOutlined />}
              onClick={() => editBreakpoint(record)}
            />
            <Button
              type="text"
              danger
              icon={<DeleteOutlined />}
              onClick={() => toggleBreakpoint(record.line)}
            />
          </Space>
        ),
      }
    ];
    
    return (
      <div className={styles.breakpointsContainer}>
        {breakpoints.length > 0 ? (
          <Table
            dataSource={breakpoints}
            columns={columns}
            pagination={false}
            size="small"
            rowKey="id"
          />
        ) : (
          <div className={styles.emptyMessage}>
            {i18n('script-debug.no-breakpoints')}
          </div>
        )}
      </div>
    );
  };
  
  // 渲染表达式求值
  const renderExpressionEvaluation = () => {
    return (
      <div className={styles.expressionContainer}>
        <div className={styles.expressionInput}>
          <Input.Search
            placeholder={i18n('script-debug.expression-placeholder')}
            value={expressionValue}
            onChange={(e) => setExpressionValue(e.target.value)}
            onSearch={handleEvaluateExpression}
            enterButton={i18n('script-debug.evaluate')}
            disabled={!paused}
          />
        </div>
        
        {expressionResult !== null && (
          <div className={styles.expressionResult}>
            <div className={styles.expressionResultLabel}>
              {i18n('script-debug.result')}:
            </div>
            <div className={styles.expressionResultValue}>
              {typeof expressionResult === 'object'
                ? JSON.stringify(expressionResult, null, 2)
                : String(expressionResult)
              }
            </div>
          </div>
        )}
      </div>
    );
  };
  
  return (
    <div className={`${styles.scriptDebugger} ${className}`} style={style}>
      {contextHolder}
      <Layout className={styles.debuggerLayout}>
        <div className={styles.toolbar}>
          <div className={styles.toolbarTitle}>
            <BugOutlined /> {i18n('script-debug.title')}
          </div>
          <div className={styles.toolbarActions}>
            {!debugging ? (
              <Button
                type="primary"
                icon={<PlayCircleOutlined />}
                onClick={startDebugging}
                loading={loading}
              >
                {i18n('script-debug.start')}
              </Button>
            ) : (
              <Space>
                {paused ? (
                  <Button
                    type="primary"
                    icon={<PlayCircleOutlined />}
                    onClick={handleContinue}
                    loading={loading}
                  >
                    {i18n('script-debug.continue')}
                  </Button>
                ) : (
                  <Button
                    type="primary"
                    icon={<PauseCircleOutlined />}
                    disabled
                    loading={loading}
                  >
                    {i18n('script-debug.pause')}
                  </Button>
                )}
                
                <Tooltip title={i18n('script-debug.step-over')}>
                  <Button
                    icon={<StepForwardOutlined />}
                    disabled={!paused}
                    onClick={handleStepOver}
                    loading={loading}
                  />
                </Tooltip>
                
                <Tooltip title={i18n('script-debug.step-into')}>
                  <Button
                    icon={<VerticalRightOutlined />}
                    disabled={!paused}
                    onClick={handleStepInto}
                    loading={loading}
                  />
                </Tooltip>
                
                <Tooltip title={i18n('script-debug.step-out')}>
                  <Button
                    icon={<StepBackwardOutlined />}
                    disabled={!paused}
                    onClick={handleStepOut}
                    loading={loading}
                  />
                </Tooltip>
                
                <Button
                  danger
                  icon={<StopOutlined />}
                  onClick={stopDebugging}
                  loading={loading}
                >
                  {i18n('script-debug.stop')}
                </Button>
              </Space>
            )}
            
            <Button onClick={onClose}>
              {i18n('common.close')}
            </Button>
          </div>
        </div>
        
        <Layout className={styles.debuggerContent}>
          <Content className={styles.editorContainer}>
            <Spin spinning={loading}>
              <div ref={containerRef} className={styles.editor} />
            </Spin>
          </Content>
          
          <Sider width={300} theme="light" className={styles.debuggerSider}>
            <Tabs activeKey={activeTab} onChange={setActiveTab}>
              <TabPane tab={i18n('script-debug.variables')} key="variables">
                {renderVariableTree()}
              </TabPane>
              
              <TabPane tab={i18n('script-debug.call-stack')} key="callStack">
                {renderCallStack()}
              </TabPane>
              
              <TabPane tab={i18n('script-debug.breakpoints')} key="breakpoints">
                {renderBreakpoints()}
              </TabPane>
              
              <TabPane tab={i18n('script-debug.watch')} key="watch">
                {renderExpressionEvaluation()}
              </TabPane>
            </Tabs>
          </Sider>
        </Layout>
      </Layout>
      
      <Modal
        title={i18n('script-debug.edit-breakpoint')}
        open={breakpointModalVisible}
        onOk={saveBreakpoint}
        onCancel={() => setBreakpointModalVisible(false)}
      >
        <div className={styles.breakpointForm}>
          <div className={styles.breakpointFormItem}>
            <div className={styles.breakpointFormLabel}>
              {i18n('script-debug.line')}:
            </div>
            <div className={styles.breakpointFormValue}>
              {editingBreakpoint?.line}
            </div>
          </div>
          
          <div className={styles.breakpointFormItem}>
            <div className={styles.breakpointFormLabel}>
              {i18n('script-debug.condition')}:
            </div>
            <div className={styles.breakpointFormValue}>
              <Input
                value={breakpointCondition}
                onChange={(e) => setBreakpointCondition(e.target.value)}
                placeholder={i18n('script-debug.condition-placeholder')}
              />
            </div>
          </div>
        </div>
      </Modal>
    </div>
  );
};

export default ScriptDebugger;
