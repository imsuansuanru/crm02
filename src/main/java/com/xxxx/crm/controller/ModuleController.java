package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.model.TreeModel;
import com.xxxx.crm.service.ModuleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

@RequestMapping("module")
@Controller
public class ModuleController extends BaseController {


    @Resource
    private ModuleService moduleService;

    /*@RequestMapping("queryAllModules")
    @ResponseBody
    public List<TreeModel> queryAllModules(){
        return moduleService.queryAllModules();
    }*/


    /**
     * 视图转发⽅法
     * @param roleId
     * @param model
     * @return
     */
    @RequestMapping("toAddGrantPage")
    public String toAddGrantPage(Integer roleId, Model model){
        model.addAttribute("roleId",roleId);
        return "role/grant";
    }

    /**
     * ⻆⾊拥有权限
     * @param roleId
     * @return
     */
    @RequestMapping("queryAllModules")
    @ResponseBody
    public List<TreeModel> queryAllModules(Integer roleId){
        return moduleService.queryAllModules02(roleId);
    }




}
