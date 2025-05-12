import React, { useRef, useEffect, useState, forwardRef, useImperativeHandle } from 'react';
import * as monaco from 'monaco-editor';
import { Button, Tooltip, Select, Space, message } from 'antd';
import { 
  PlayCircleOutlined, 
  SaveOutlined, 
  FormatOutlined, 
  BugOutlined,
  CheckOutlined
} from '@ant-design/icons';
import { getCompletionItems, getDiagnostics, formatCode } from '@/service/script-language';
import { useTheme } from '@/hooks';
import styles from './index.less';

// 支持的语言
const SUPPORTED_LANGUAGES = [
  { value: 'javascript', label: 'JavaScript' },
  { value: 'typescript', label: 'TypeScript' },
  { value: 'sql', label: 'SQL' },
  { value: 'json', label: 'JSON' },
  { value: 'html', label: 'HTML' },
  { value: 'css', label: 'CSS' },
  { value: 'python', label: 'Python' },
  { value: 'kotlin', label: 'Kotlin' }
];

export interface EnhancedMonacoEditorProps {
  value?: string;
  language?: string;
  readOnly?: boolean;
  onChange?: (value: string) => void;
  onSave?: () => void;
  onExecute?: () => void;
  onFormat?: () => void;
  onDebug?: () => void;
  height?: string | number;
  width?: string | number;
  options?: monaco.editor.IStandaloneEditorConstructionOptions;
  className?: string;
}

export interface EnhancedMonacoEditorRef {
  getValue: () => string;
  setValue: (value: string) => void;
  getEditor: () => monaco.editor.IStandaloneCodeEditor | null;
  focus: () => void;
}

const EnhancedMonacoEditor = forwardRef<EnhancedMonacoEditorRef, EnhancedMonacoEditorProps>((props, ref) => {
  const {
    value = '',
    language = 'javascript',
    readOnly = false,
    onChange,
    onSave,
    onExecute,
    onFormat,
    onDebug,
    height = '100%',
    width = '100%',
    options = {},
    className
  } = props;

  const containerRef = useRef<HTMLDivElement>(null);
  const editorRef = useRef<monaco.editor.IStandaloneCodeEditor | null>(null);
  const [currentLanguage, setCurrentLanguage] = useState(language);
  const [messageApi, contextHolder] = message.useMessage();
  const [appTheme] = useTheme();

  // 初始化编辑器
  useEffect(() => {
    if (containerRef.current) {
      // 创建编辑器
      const editor = monaco.editor.create(containerRef.current, {
        value,
        language: currentLanguage,
        theme: appTheme.backgroundColor.includes('dark') ? 'vs-dark' : 'vs',
        automaticLayout: true,
        minimap: { enabled: true },
        scrollBeyondLastLine: false,
        lineNumbers: 'on',
        readOnly,
        folding: true,
        formatOnPaste: true,
        formatOnType: true,
        suggestOnTriggerCharacters: true,
        tabSize: 2,
        wordWrap: 'on',
        ...options
      });

      // 保存编辑器实例
      editorRef.current = editor;

      // 监听内容变化
      editor.onDidChangeModelContent(() => {
        if (onChange && editor) {
          onChange(editor.getValue());
        }
      });

      // 注册自定义代码补全提供程序
      monaco.languages.registerCompletionItemProvider(currentLanguage, {
        triggerCharacters: ['.', '(', '{', '[', ',', ' ', ':'],
        provideCompletionItems: async (model, position) => {
          try {
            const script = model.getValue();
            const offset = model.getOffsetAt(position);
            
            const response = await getCompletionItems(currentLanguage, script, offset);
            
            if (response.success && response.data) {
              return {
                suggestions: response.data.map(item => ({
                  label: item.label,
                  kind: mapCompletionItemKind(item.kind),
                  insertText: item.insertText,
                  detail: item.detail,
                  documentation: item.documentation,
                  sortText: item.sortText,
                  filterText: item.filterText,
                  preselect: item.preselect,
                  insertTextRules: item.insertTextFormat === 'SNIPPET' 
                    ? monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet 
                    : monaco.languages.CompletionItemInsertTextRule.None
                }))
              };
            }
          } catch (error) {
            console.error('Error fetching completion items:', error);
          }
          
          return { suggestions: [] };
        }
      });

      // 注册诊断信息提供程序
      const updateDiagnostics = async () => {
        try {
          const script = editor.getValue();
          const response = await getDiagnostics(currentLanguage, script);
          
          if (response.success && response.data) {
            const markers = response.data.map(item => ({
              message: item.message,
              severity: mapDiagnosticSeverity(item.severity),
              startLineNumber: calculateLineNumber(script, item.startPosition),
              startColumn: calculateColumn(script, item.startPosition),
              endLineNumber: calculateLineNumber(script, item.endPosition),
              endColumn: calculateColumn(script, item.endPosition),
              code: item.code,
              source: item.source
            }));
            
            monaco.editor.setModelMarkers(editor.getModel()!, currentLanguage, markers);
          }
        } catch (error) {
          console.error('Error fetching diagnostics:', error);
        }
      };

      // 初始诊断
      updateDiagnostics();

      // 内容变化时更新诊断
      const changeDisposable = editor.onDidChangeModelContent(() => {
        updateDiagnostics();
      });

      // 清理函数
      return () => {
        changeDisposable.dispose();
        editor.dispose();
      };
    }
  }, [currentLanguage, readOnly]);

  // 更新编辑器值
  useEffect(() => {
    if (editorRef.current) {
      const currentValue = editorRef.current.getValue();
      if (value !== currentValue) {
        editorRef.current.setValue(value);
      }
    }
  }, [value]);

  // 更新编辑器语言
  useEffect(() => {
    if (editorRef.current) {
      const model = editorRef.current.getModel();
      if (model) {
        monaco.editor.setModelLanguage(model, currentLanguage);
      }
    }
  }, [currentLanguage]);

  // 更新编辑器主题
  useEffect(() => {
    if (editorRef.current) {
      const theme = appTheme.backgroundColor.includes('dark') ? 'vs-dark' : 'vs';
      monaco.editor.setTheme(theme);
    }
  }, [appTheme]);

  // 暴露方法给父组件
  useImperativeHandle(ref, () => ({
    getValue: () => editorRef.current?.getValue() || '',
    setValue: (value: string) => {
      if (editorRef.current) {
        editorRef.current.setValue(value);
      }
    },
    getEditor: () => editorRef.current,
    focus: () => {
      if (editorRef.current) {
        editorRef.current.focus();
      }
    }
  }));

  // 处理语言变更
  const handleLanguageChange = (lang: string) => {
    setCurrentLanguage(lang);
  };

  // 处理格式化
  const handleFormat = async () => {
    try {
      if (editorRef.current) {
        const script = editorRef.current.getValue();
        const response = await formatCode(currentLanguage, script);
        
        if (response.success && response.data) {
          editorRef.current.setValue(response.data);
          messageApi.success('代码格式化成功');
        } else {
          messageApi.error('代码格式化失败');
        }
      }
    } catch (error) {
      console.error('Error formatting code:', error);
      messageApi.error('代码格式化出错');
    }
    
    if (onFormat) {
      onFormat();
    }
  };

  // 映射补全项类型
  const mapCompletionItemKind = (kind: string): monaco.languages.CompletionItemKind => {
    const kindMap: Record<string, monaco.languages.CompletionItemKind> = {
      'TEXT': monaco.languages.CompletionItemKind.Text,
      'METHOD': monaco.languages.CompletionItemKind.Method,
      'FUNCTION': monaco.languages.CompletionItemKind.Function,
      'CONSTRUCTOR': monaco.languages.CompletionItemKind.Constructor,
      'FIELD': monaco.languages.CompletionItemKind.Field,
      'VARIABLE': monaco.languages.CompletionItemKind.Variable,
      'CLASS': monaco.languages.CompletionItemKind.Class,
      'INTERFACE': monaco.languages.CompletionItemKind.Interface,
      'MODULE': monaco.languages.CompletionItemKind.Module,
      'PROPERTY': monaco.languages.CompletionItemKind.Property,
      'UNIT': monaco.languages.CompletionItemKind.Unit,
      'VALUE': monaco.languages.CompletionItemKind.Value,
      'ENUM': monaco.languages.CompletionItemKind.Enum,
      'KEYWORD': monaco.languages.CompletionItemKind.Keyword,
      'SNIPPET': monaco.languages.CompletionItemKind.Snippet,
      'COLOR': monaco.languages.CompletionItemKind.Color,
      'FILE': monaco.languages.CompletionItemKind.File,
      'REFERENCE': monaco.languages.CompletionItemKind.Reference,
      'FOLDER': monaco.languages.CompletionItemKind.Folder,
      'ENUM_MEMBER': monaco.languages.CompletionItemKind.EnumMember,
      'CONSTANT': monaco.languages.CompletionItemKind.Constant,
      'STRUCT': monaco.languages.CompletionItemKind.Struct,
      'EVENT': monaco.languages.CompletionItemKind.Event,
      'OPERATOR': monaco.languages.CompletionItemKind.Operator,
      'TYPE_PARAMETER': monaco.languages.CompletionItemKind.TypeParameter
    };
    
    return kindMap[kind] || monaco.languages.CompletionItemKind.Text;
  };

  // 映射诊断严重程度
  const mapDiagnosticSeverity = (severity: string): monaco.MarkerSeverity => {
    const severityMap: Record<string, monaco.MarkerSeverity> = {
      'ERROR': monaco.MarkerSeverity.Error,
      'WARNING': monaco.MarkerSeverity.Warning,
      'INFORMATION': monaco.MarkerSeverity.Info,
      'HINT': monaco.MarkerSeverity.Hint
    };
    
    return severityMap[severity] || monaco.MarkerSeverity.Info;
  };

  // 计算行号
  const calculateLineNumber = (text: string, offset: number): number => {
    const textBeforeOffset = text.substring(0, offset);
    return (textBeforeOffset.match(/\n/g) || []).length + 1;
  };

  // 计算列号
  const calculateColumn = (text: string, offset: number): number => {
    const textBeforeOffset = text.substring(0, offset);
    const lastNewlineIndex = textBeforeOffset.lastIndexOf('\n');
    return lastNewlineIndex === -1 ? offset + 1 : offset - lastNewlineIndex;
  };

  return (
    <div className={`${styles.enhancedMonacoEditor} ${className}`} style={{ height, width }}>
      {contextHolder}
      <div className={styles.toolbar}>
        <Space>
          <Select
            value={currentLanguage}
            onChange={handleLanguageChange}
            options={SUPPORTED_LANGUAGES}
            className={styles.languageSelect}
          />
          
          <Tooltip title="格式化代码">
            <Button 
              icon={<FormatOutlined />} 
              onClick={handleFormat}
              disabled={readOnly}
            />
          </Tooltip>
          
          {onSave && (
            <Tooltip title="保存">
              <Button 
                icon={<SaveOutlined />} 
                onClick={onSave}
                disabled={readOnly}
              />
            </Tooltip>
          )}
          
          {onExecute && (
            <Tooltip title="执行">
              <Button 
                type="primary" 
                icon={<PlayCircleOutlined />} 
                onClick={onExecute}
              />
            </Tooltip>
          )}
          
          {onDebug && (
            <Tooltip title="调试">
              <Button 
                icon={<BugOutlined />} 
                onClick={onDebug}
              />
            </Tooltip>
          )}
        </Space>
      </div>
      
      <div 
        ref={containerRef} 
        className={styles.editorContainer}
      />
    </div>
  );
});

export default EnhancedMonacoEditor;
