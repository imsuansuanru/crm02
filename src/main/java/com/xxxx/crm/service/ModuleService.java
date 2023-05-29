package com.xxxx.crm.service;

import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.ModuleMapper;
import com.xxxx.crm.dao.PermissionMapper;
import com.xxxx.crm.model.TreeModel;
import com.xxxx.crm.vo.Module;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class ModuleService extends BaseService<Module,Integer> {

    @Resource
    private ModuleMapper moduleMapper;
    @Resource
    private PermissionMapper permissionMapper;


    public List<TreeModel> queryAllModules(){
        return moduleMapper.queryAllModules();
    }


    public List<TreeModel> queryAllModules02(Integer roleId) {
        List<TreeModel> treeModels=moduleMapper.queryAllModules();
        // 根据⻆⾊id 查询⻆⾊拥有的菜单id List<Integer>
        List<Integer> permissionIds=permissionMapper.queryRoleHasAllModuleIdsByRoleId(roleId);
        if(null !=permissionIds && permissionIds.size()>0){
            treeModels.forEach(treeDto -> {
                if(permissionIds.contains(treeDto.getId())){
                    // 说明当前⻆⾊ 分配了该菜单
                    treeDto.setChecked(true);
                }
            });
        }
        return treeModels;
    }


}
