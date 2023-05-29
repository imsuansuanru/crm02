package com.xxxx.crm.dao;

import com.xxxx.crm.base.BaseMapper;
import com.xxxx.crm.vo.Permission;

import java.util.List;

public interface PermissionMapper extends BaseMapper<Permission,Integer> {
    // 通过角色ID查询对应的权限记录
    Integer countPermissionByRoleId(Integer roleId);
    // 删除权限记录
    void deletePermissionByRoleId(Integer roleId);

    // ⻆⾊拥有权限sql查询
    List<Integer> queryRoleHasAllModuleIdsByRoleId(Integer roleId);
}