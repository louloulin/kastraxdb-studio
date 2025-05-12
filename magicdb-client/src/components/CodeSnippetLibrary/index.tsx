import React, { useState, useEffect } from 'react';
import { Modal, Input, Select, Button, List, Tag, Tooltip, message, Space, Tabs, Card, Popconfirm } from 'antd';
import { 
  SearchOutlined, 
  PlusOutlined, 
  EditOutlined, 
  DeleteOutlined, 
  CopyOutlined,
  CheckOutlined,
  CodeOutlined
} from '@ant-design/icons';
import { 
  getAllSnippets, 
  getSnippet, 
  createSnippet, 
  updateSnippet, 
  deleteSnippet, 
  searchSnippets 
} from '@/service/code-snippet';
import JSONEditor from '@/components/JsonEditor';
import i18n from '@/i18n';
import styles from './index.less';

const { Option } = Select;
const { TabPane } = Tabs;
const { TextArea } = Input;

interface CodeSnippetLibraryProps {
  visible: boolean;
  language?: string;
  onClose: () => void;
  onSelect: (snippet: any) => void;
}

const CodeSnippetLibrary: React.FC<CodeSnippetLibraryProps> = ({
  visible,
  language,
  onClose,
  onSelect
}) => {
  const [snippets, setSnippets] = useState<any[]>([]);
  const [filteredSnippets, setFilteredSnippets] = useState<any[]>([]);
  const [selectedSnippet, setSelectedSnippet] = useState<any>(null);
  const [searchKeyword, setSearchKeyword] = useState<string>('');
  const [loading, setLoading] = useState<boolean>(false);
  const [editModalVisible, setEditModalVisible] = useState<boolean>(false);
  const [editingSnippet, setEditingSnippet] = useState<any>(null);
  const [copied, setCopied] = useState<boolean>(false);
  const [activeTab, setActiveTab] = useState<string>('all');
  const [messageApi, contextHolder] = message.useMessage();
  
  // 加载代码片段
  useEffect(() => {
    if (visible) {
      fetchSnippets();
    }
  }, [visible, language]);
  
  // 获取代码片段
  const fetchSnippets = async () => {
    try {
      setLoading(true);
      
      const response = await getAllSnippets(language);
      
      if (response && response.success) {
        setSnippets(response.data || []);
        setFilteredSnippets(response.data || []);
      } else {
        messageApi.error(i18n('code-snippet.fetch-failed'));
      }
    } catch (error) {
      console.error('Failed to fetch snippets:', error);
      messageApi.error(i18n('code-snippet.fetch-error'));
    } finally {
      setLoading(false);
    }
  };
  
  // 搜索代码片段
  const handleSearch = async (value: string) => {
    setSearchKeyword(value);
    
    if (!value) {
      setFilteredSnippets(snippets);
      return;
    }
    
    try {
      setLoading(true);
      
      const response = await searchSnippets(value, language);
      
      if (response && response.success) {
        setFilteredSnippets(response.data || []);
      } else {
        messageApi.error(i18n('code-snippet.search-failed'));
      }
    } catch (error) {
      console.error('Failed to search snippets:', error);
      messageApi.error(i18n('code-snippet.search-error'));
    } finally {
      setLoading(false);
    }
  };
  
  // 选择代码片段
  const handleSelectSnippet = (snippet: any) => {
    setSelectedSnippet(snippet);
  };
  
  // 使用代码片段
  const handleUseSnippet = () => {
    if (selectedSnippet) {
      onSelect(selectedSnippet);
      onClose();
    }
  };
  
  // 创建代码片段
  const handleCreateSnippet = () => {
    setEditingSnippet({
      id: '',
      name: '',
      description: '',
      content: '',
      language: language || 'javascript',
      tags: []
    });
    setEditModalVisible(true);
  };
  
  // 编辑代码片段
  const handleEditSnippet = (snippet: any) => {
    setEditingSnippet({
      ...snippet,
      tags: snippet.tags || []
    });
    setEditModalVisible(true);
  };
  
  // 保存代码片段
  const handleSaveSnippet = async () => {
    if (!editingSnippet) return;
    
    try {
      setLoading(true);
      
      if (editingSnippet.id) {
        // 更新代码片段
        const response = await updateSnippet(editingSnippet.id, editingSnippet);
        
        if (response && response.success) {
          messageApi.success(i18n('code-snippet.update-success'));
          
          // 更新列表
          fetchSnippets();
          
          // 关闭编辑窗口
          setEditModalVisible(false);
        } else {
          messageApi.error(i18n('code-snippet.update-failed'));
        }
      } else {
        // 创建代码片段
        const response = await createSnippet(editingSnippet);
        
        if (response && response.success) {
          messageApi.success(i18n('code-snippet.create-success'));
          
          // 更新列表
          fetchSnippets();
          
          // 关闭编辑窗口
          setEditModalVisible(false);
        } else {
          messageApi.error(i18n('code-snippet.create-failed'));
        }
      }
    } catch (error) {
      console.error('Failed to save snippet:', error);
      messageApi.error(i18n('code-snippet.save-error'));
    } finally {
      setLoading(false);
    }
  };
  
  // 删除代码片段
  const handleDeleteSnippet = async (snippet: any) => {
    try {
      setLoading(true);
      
      const response = await deleteSnippet(snippet.id);
      
      if (response && response.success) {
        messageApi.success(i18n('code-snippet.delete-success'));
        
        // 更新列表
        fetchSnippets();
        
        // 清除选中
        if (selectedSnippet && selectedSnippet.id === snippet.id) {
          setSelectedSnippet(null);
        }
      } else {
        messageApi.error(i18n('code-snippet.delete-failed'));
      }
    } catch (error) {
      console.error('Failed to delete snippet:', error);
      messageApi.error(i18n('code-snippet.delete-error'));
    } finally {
      setLoading(false);
    }
  };
  
  // 复制代码
  const handleCopyCode = () => {
    if (selectedSnippet) {
      navigator.clipboard.writeText(selectedSnippet.content);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
      messageApi.success(i18n('code-snippet.copy-success'));
    }
  };
  
  // 过滤代码片段
  const filterSnippetsByTab = (tab: string) => {
    if (tab === 'all') {
      return filteredSnippets;
    } else if (tab === 'system') {
      return filteredSnippets.filter(snippet => snippet.system);
    } else if (tab === 'custom') {
      return filteredSnippets.filter(snippet => !snippet.system);
    }
    return filteredSnippets;
  };
  
  // 渲染代码片段列表
  const renderSnippetList = () => {
    const snippetsToShow = filterSnippetsByTab(activeTab);
    
    return (
      <List
        loading={loading}
        dataSource={snippetsToShow}
        renderItem={(snippet) => (
          <List.Item
            key={snippet.id}
            className={`${styles.snippetItem} ${selectedSnippet?.id === snippet.id ? styles.selected : ''}`}
            onClick={() => handleSelectSnippet(snippet)}
            actions={[
              <Tooltip title={i18n('code-snippet.edit')} key="edit">
                <Button
                  type="text"
                  icon={<EditOutlined />}
                  disabled={snippet.system}
                  onClick={(e) => {
                    e.stopPropagation();
                    handleEditSnippet(snippet);
                  }}
                />
              </Tooltip>,
              <Popconfirm
                key="delete"
                title={i18n('code-snippet.delete-confirm')}
                onConfirm={(e) => {
                  e?.stopPropagation();
                  handleDeleteSnippet(snippet);
                }}
                okText={i18n('common.yes')}
                cancelText={i18n('common.no')}
              >
                <Tooltip title={i18n('code-snippet.delete')}>
                  <Button
                    type="text"
                    danger
                    icon={<DeleteOutlined />}
                    disabled={snippet.system}
                    onClick={(e) => e.stopPropagation()}
                  />
                </Tooltip>
              </Popconfirm>
            ]}
          >
            <List.Item.Meta
              title={
                <div className={styles.snippetTitle}>
                  <span>{snippet.name}</span>
                  {snippet.system && (
                    <Tag color="blue">{i18n('code-snippet.system')}</Tag>
                  )}
                </div>
              }
              description={
                <div className={styles.snippetMeta}>
                  <div className={styles.snippetDescription}>
                    {snippet.description}
                  </div>
                  <div className={styles.snippetTags}>
                    {snippet.tags?.map((tag: string) => (
                      <Tag key={tag}>{tag}</Tag>
                    ))}
                  </div>
                </div>
              }
            />
          </List.Item>
        )}
        locale={{
          emptyText: i18n('code-snippet.no-snippets')
        }}
      />
    );
  };
  
  // 渲染代码片段详情
  const renderSnippetDetail = () => {
    if (!selectedSnippet) {
      return (
        <div className={styles.noSnippetSelected}>
          <CodeOutlined className={styles.noSnippetIcon} />
          <div className={styles.noSnippetText}>
            {i18n('code-snippet.select-snippet')}
          </div>
        </div>
      );
    }
    
    return (
      <div className={styles.snippetDetail}>
        <div className={styles.snippetDetailHeader}>
          <div className={styles.snippetDetailTitle}>
            <h3>{selectedSnippet.name}</h3>
            {selectedSnippet.system && (
              <Tag color="blue">{i18n('code-snippet.system')}</Tag>
            )}
          </div>
          <div className={styles.snippetDetailActions}>
            <Button
              icon={copied ? <CheckOutlined /> : <CopyOutlined />}
              onClick={handleCopyCode}
            >
              {i18n('code-snippet.copy')}
            </Button>
            <Button
              type="primary"
              onClick={handleUseSnippet}
            >
              {i18n('code-snippet.use')}
            </Button>
          </div>
        </div>
        
        {selectedSnippet.description && (
          <div className={styles.snippetDetailDescription}>
            {selectedSnippet.description}
          </div>
        )}
        
        <div className={styles.snippetDetailTags}>
          {selectedSnippet.tags?.map((tag: string) => (
            <Tag key={tag}>{tag}</Tag>
          ))}
        </div>
        
        <div className={styles.snippetDetailContent}>
          <JSONEditor
            value={selectedSnippet.content}
            language={selectedSnippet.language}
            height={300}
            readOnly
          />
        </div>
      </div>
    );
  };
  
  return (
    <Modal
      title={i18n('code-snippet.library')}
      open={visible}
      onCancel={onClose}
      width={900}
      footer={null}
      className={styles.snippetLibraryModal}
    >
      {contextHolder}
      <div className={styles.snippetLibrary}>
        <div className={styles.snippetLibraryHeader}>
          <Input.Search
            placeholder={i18n('code-snippet.search')}
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
            onSearch={handleSearch}
            className={styles.searchInput}
          />
          <Button
            type="primary"
            icon={<PlusOutlined />}
            onClick={handleCreateSnippet}
          >
            {i18n('code-snippet.create')}
          </Button>
        </div>
        
        <div className={styles.snippetLibraryContent}>
          <div className={styles.snippetList}>
            <Tabs activeKey={activeTab} onChange={setActiveTab}>
              <TabPane tab={i18n('code-snippet.all')} key="all" />
              <TabPane tab={i18n('code-snippet.system')} key="system" />
              <TabPane tab={i18n('code-snippet.custom')} key="custom" />
            </Tabs>
            {renderSnippetList()}
          </div>
          
          <div className={styles.snippetPreview}>
            {renderSnippetDetail()}
          </div>
        </div>
      </div>
      
      <Modal
        title={editingSnippet?.id ? i18n('code-snippet.edit') : i18n('code-snippet.create')}
        open={editModalVisible}
        onOk={handleSaveSnippet}
        onCancel={() => setEditModalVisible(false)}
        confirmLoading={loading}
      >
        <div className={styles.snippetForm}>
          <div className={styles.formItem}>
            <div className={styles.formLabel}>
              {i18n('code-snippet.name')}:
            </div>
            <Input
              value={editingSnippet?.name}
              onChange={(e) => setEditingSnippet({ ...editingSnippet, name: e.target.value })}
              placeholder={i18n('code-snippet.name-placeholder')}
            />
          </div>
          
          <div className={styles.formItem}>
            <div className={styles.formLabel}>
              {i18n('code-snippet.description')}:
            </div>
            <TextArea
              value={editingSnippet?.description}
              onChange={(e) => setEditingSnippet({ ...editingSnippet, description: e.target.value })}
              placeholder={i18n('code-snippet.description-placeholder')}
              rows={3}
            />
          </div>
          
          <div className={styles.formItem}>
            <div className={styles.formLabel}>
              {i18n('code-snippet.language')}:
            </div>
            <Select
              value={editingSnippet?.language}
              onChange={(value) => setEditingSnippet({ ...editingSnippet, language: value })}
              style={{ width: '100%' }}
            >
              <Option value="javascript">JavaScript</Option>
              <Option value="typescript">TypeScript</Option>
              <Option value="kotlin">Kotlin</Option>
              <Option value="python">Python</Option>
              <Option value="sql">SQL</Option>
            </Select>
          </div>
          
          <div className={styles.formItem}>
            <div className={styles.formLabel}>
              {i18n('code-snippet.tags')}:
            </div>
            <Select
              mode="tags"
              value={editingSnippet?.tags}
              onChange={(value) => setEditingSnippet({ ...editingSnippet, tags: value })}
              placeholder={i18n('code-snippet.tags-placeholder')}
              style={{ width: '100%' }}
            />
          </div>
          
          <div className={styles.formItem}>
            <div className={styles.formLabel}>
              {i18n('code-snippet.content')}:
            </div>
            <TextArea
              value={editingSnippet?.content}
              onChange={(e) => setEditingSnippet({ ...editingSnippet, content: e.target.value })}
              placeholder={i18n('code-snippet.content-placeholder')}
              rows={10}
            />
          </div>
        </div>
      </Modal>
    </Modal>
  );
};

export default CodeSnippetLibrary;
