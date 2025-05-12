import { request } from 'umi';

/**
 * 获取所有数据源
 */
export async function getAllDataSources() {
  return request('/api/data-source/list');
}

/**
 * 获取数据源信息
 */
export async function getDataSource(dataSourceId: number) {
  return request(`/api/data-source/${dataSourceId}`);
}

/**
 * 获取数据库列表
 */
export async function getDatabases(dataSourceId: number) {
  return request(`/api/data-source/${dataSourceId}/databases`);
}

/**
 * 获取表列表
 */
export async function getTables(dataSourceId: number, databaseName: string) {
  return request(`/api/data-source/${dataSourceId}/databases/${databaseName}/tables`);
}

/**
 * 获取列信息
 */
export async function getColumns(dataSourceId: number, databaseName: string, tableName: string) {
  return request(`/api/data-source/${dataSourceId}/databases/${databaseName}/tables/${tableName}/columns`);
}

/**
 * 执行查询
 */
export async function executeQuery(dataSourceId: number, databaseName: string, sql: string) {
  return request(`/api/data-source/${dataSourceId}/databases/${databaseName}/query`, {
    method: 'POST',
    data: { sql }
  });
}
