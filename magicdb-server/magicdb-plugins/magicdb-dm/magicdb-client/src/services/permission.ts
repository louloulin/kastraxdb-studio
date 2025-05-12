import { request } from '@/utils/request';

/**
 * 资源类型
 */
export enum ResourceType {
  SERVICE = 'SERVICE',
  GROUP = 'GROUP',
  GLOBAL = 'GLOBAL',
}

/**
 * 权限类型
 */
export enum PermissionType {
  READ = 'READ',
  EXECUTE = 'EXECUTE',
  EDIT = 'EDIT',
  DELETE = 'DELETE',
  MANAGE = 'MANAGE',
}

/**
 * 权限信息
 */
export interface PermissionInfo {
  id: string;
  userId: string;
  username: string;
  resourceType: ResourceType;
  resourceId: string;
  permissionType: PermissionType;
  createTime: string;
  updateTime: string;
}

/**
 * 角色信息
 */
export interface RoleInfo {
  id: string;
  name: string;
  description?: string;
  permissions: PermissionInfo[];
  createTime: string;
  updateTime: string;
}

/**
 * 用户角色信息
 */
export interface UserRoleInfo {
  userId: string;
  username: string;
  roles: RoleInfo[];
  permissions: PermissionInfo[];
}

/**
 * 检查权限
 */
export async function checkPermission(
  userId: string,
  resourceType: ResourceType,
  resourceId: string,
  permissionType: PermissionType,
) {
  return request<boolean>('/api/permissions/check', {
    method: 'GET',
    params: {
      userId,
      resourceType,
      resourceId,
      permissionType,
    },
  });
}

/**
 * 获取用户权限
 */
export async function getUserPermissions(userId: string) {
  return request<PermissionInfo[]>(`/api/permissions/users/${userId}/permissions`);
}

/**
 * 获取用户角色
 */
export async function getUserRoles(userId: string) {
  return request<RoleInfo[]>(`/api/permissions/users/${userId}/roles`);
}

/**
 * 获取用户角色和权限信息
 */
export async function getUserRoleInfo(userId: string) {
  return request<UserRoleInfo>(`/api/permissions/users/${userId}/role-info`);
}

/**
 * 授予用户权限
 */
export async function grantPermission(
  userId: string,
  resourceType: ResourceType,
  resourceId: string,
  permissionType: PermissionType,
) {
  return request<PermissionInfo>(`/api/permissions/users/${userId}/permissions`, {
    method: 'POST',
    data: {
      resourceType,
      resourceId,
      permissionType,
    },
  });
}

/**
 * 撤销用户权限
 */
export async function revokePermission(
  userId: string,
  resourceType: ResourceType,
  resourceId: string,
  permissionType: PermissionType,
) {
  return request<boolean>(`/api/permissions/users/${userId}/permissions`, {
    method: 'DELETE',
    params: {
      resourceType,
      resourceId,
      permissionType,
    },
  });
}

/**
 * 获取所有角色
 */
export async function getRoles() {
  return request<RoleInfo[]>('/api/permissions/roles');
}

/**
 * 获取角色详情
 */
export async function getRole(id: string) {
  return request<RoleInfo>(`/api/permissions/roles/${id}`);
}

/**
 * 创建角色
 */
export async function createRole(name: string, description?: string) {
  return request<RoleInfo>('/api/permissions/roles', {
    method: 'POST',
    data: {
      name,
      description,
    },
  });
}

/**
 * 更新角色
 */
export async function updateRole(id: string, name: string, description?: string) {
  return request<RoleInfo>(`/api/permissions/roles/${id}`, {
    method: 'PUT',
    data: {
      name,
      description,
    },
  });
}

/**
 * 删除角色
 */
export async function deleteRole(id: string) {
  return request<boolean>(`/api/permissions/roles/${id}`, {
    method: 'DELETE',
  });
}

/**
 * 为角色添加权限
 */
export async function addRolePermission(
  roleId: string,
  resourceType: ResourceType,
  resourceId: string,
  permissionType: PermissionType,
) {
  return request<boolean>(`/api/permissions/roles/${roleId}/permissions`, {
    method: 'POST',
    data: {
      resourceType,
      resourceId,
      permissionType,
    },
  });
}

/**
 * 从角色中移除权限
 */
export async function removeRolePermission(
  roleId: string,
  resourceType: ResourceType,
  resourceId: string,
  permissionType: PermissionType,
) {
  return request<boolean>(`/api/permissions/roles/${roleId}/permissions`, {
    method: 'DELETE',
    params: {
      resourceType,
      resourceId,
      permissionType,
    },
  });
}

/**
 * 为用户分配角色
 */
export async function assignRoleToUser(userId: string, roleId: string) {
  return request<boolean>(`/api/permissions/users/${userId}/roles/${roleId}`, {
    method: 'POST',
  });
}

/**
 * 从用户中移除角色
 */
export async function removeRoleFromUser(userId: string, roleId: string) {
  return request<boolean>(`/api/permissions/users/${userId}/roles/${roleId}`, {
    method: 'DELETE',
  });
}
