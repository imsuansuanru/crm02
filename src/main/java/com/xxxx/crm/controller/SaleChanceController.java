package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.service.SaleChanceService;
import com.xxxx.crm.utils.CookieUtil;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.SaleChance;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("sale_chance")
public class SaleChanceController extends BaseController {

    @Resource
    private SaleChanceService saleChanceService;


    /**
     * 多条件分页查询营销机会
     * @param query
     * @param request
     * @param flag =1 "当前查询为开发计划数据"
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> querySaleChanceByParams(SaleChanceQuery query,HttpServletRequest request, Integer flag){
        // 查询参数 flag=1 代表当前查询为开发计划数据，设置查询分配⼈参数
        if (null != flag && flag == 1){
            // 获取当前登录⽤户的ID
            Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
            query.setAssignMan(userId);
        }
        return saleChanceService.querySaleChanceByParams(query);
    }


    /**
     * 进入营销机会页面
     * @return
     */
    @RequestMapping("index")
    public String index(){
        return "saleChance/sale_chance";
    }


    @PostMapping("add")
    @ResponseBody
    public ResultInfo saveSaleChance(HttpServletRequest request , SaleChance saleChance){
        // 从cookie中获取用户姓名
        String userName = CookieUtil.getCookieValue(request, "userName");
        // 将userName设置到saleChance中
        saleChance.setCreateMan(userName);
        // 添加营销机会的数据
        saleChanceService.saveSaleChance(saleChance);

        return  success("营销数据添加成功！s");

    }

    /**
     * 更新营销机会数据
     * @param saleChance
     * @return
     */
    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateSaleChance(SaleChance saleChance){
        // 添加营销机会的数据
        saleChanceService.updateSaleChance(saleChance);

        return  success("营销数据更新成功！s");

    }



    /**
     * 进入营销机会页面
     * @return
     */
    @RequestMapping("toSaleChancePage")
    public String addOrUpdateSaleChancePage(HttpServletRequest request, Integer saleChanceId){

        // 判断saleChanceId是否为空
        if (saleChanceId != null){
            // 通过ID查询营销机会
            SaleChance saleChance = saleChanceService.selectByPrimaryKey(saleChanceId);
            // 将数据设置到请求域中
            request.setAttribute("saleChance", saleChance);
        }
        return "saleChance/add_update";
    }


    /**
     * 删除营销机会数据
     * @param ids
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteSaleChance(Integer[] ids){
        // 删除营销机会的数据
        saleChanceService.deleteSaleChance(ids);
        return success("营销机会数据删除成功！");
    }



    @RequestMapping("updateSaleChanceDevResult")
    @ResponseBody
    public ResultInfo updateSaleChanceDevResult(Integer id, Integer devResult){
        saleChanceService.updateSaleChanceDevResult(id,devResult);
        return success("开发状态更新成功！");
    }



}
