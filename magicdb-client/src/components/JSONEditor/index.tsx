import React, { useRef, useEffect } from 'react';
import * as monaco from 'monaco-editor';
import styles from './index.less';

interface JSONEditorProps {
  value?: string;
  onChange?: (value: string) => void;
  height?: number;
  readOnly?: boolean;
}

const JSONEditor: React.FC<JSONEditorProps> = ({
  value = '{}',
  onChange,
  height = 200,
  readOnly = false
}) => {
  const containerRef = useRef<HTMLDivElement>(null);
  const editorRef = useRef<monaco.editor.IStandaloneCodeEditor | null>(null);

  useEffect(() => {
    if (containerRef.current) {
      editorRef.current = monaco.editor.create(containerRef.current, {
        value,
        language: 'json',
        theme: 'vs',
        automaticLayout: true,
        minimap: { enabled: false },
        scrollBeyondLastLine: false,
        lineNumbers: 'on',
        readOnly,
        folding: true,
        formatOnPaste: true,
        formatOnType: true
      });

      editorRef.current.onDidChangeModelContent(() => {
        if (onChange && editorRef.current) {
          onChange(editorRef.current.getValue());
        }
      });

      return () => {
        editorRef.current?.dispose();
      };
    }
  }, []);

  useEffect(() => {
    if (editorRef.current) {
      const currentValue = editorRef.current.getValue();
      if (value !== currentValue) {
        editorRef.current.setValue(value);
      }
    }
  }, [value]);

  return (
    <div
      ref={containerRef}
      className={styles.jsonEditor}
      style={{ height: `${height}px` }}
    />
  );
};

export default JSONEditor;
