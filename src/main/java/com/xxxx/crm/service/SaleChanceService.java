package com.xxxx.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.dao.SaleChanceMapper;
import com.xxxx.crm.enums.CustomerServeStatus;
import com.xxxx.crm.enums.DevResult;
import com.xxxx.crm.enums.StateStatus;
import com.xxxx.crm.query.SaleChanceQuery;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.PhoneUtil;
import com.xxxx.crm.vo.SaleChance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class SaleChanceService extends BaseService<SaleChance,Integer> {

    @Resource
    private SaleChanceMapper saleChanceMapper;


    /**
     * 多条件分⻚查询营销机会 (BaseService 中有对应的⽅法)
     * @param query SaleChanceQuery (page,limit,customerName,createMan,state)
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public Map<String,Object> querySaleChanceByParams(SaleChanceQuery query){
        Map<String,Object> map = new HashMap<>();
        // 开启分页
        PageHelper.startPage(query.getPage(),query.getLimit());
        // 得到对应的分页对象
        PageInfo<SaleChance> pageInfo = new PageInfo<>(saleChanceMapper.selectByParams(query));

        map.put("code",0);
        map.put("msg","success!");
        map.put("count",pageInfo.getTotal());
        map.put("data",pageInfo.getList());

        return map;
    }


    /**
     * 营销机会数据添加
     * 1.参数校验
     *      customerName:⾮空
     *      linkMan:⾮空
     *      linkPhone:⾮空 11位⼿机号
     * 2.设置相关参数默认值
     *      state:默认未分配 如果选择分配⼈ state 为已分配
     *      assignTime:如果 如果选择分配⼈ 时间为当前系统时间
     *      devResult:默认未开发 如果选择分配⼈devResult为开发中 0-未开发 1-开发中 2-开发成功 3-开发失败
     *      isValid:默认有效数据(1-有效 0-⽆效)
     *      createDate updateDate:默认当前系统时间
     * 3.执⾏添加 判断结果
     */
    public void saveSaleChance(SaleChance saleChance){
        // 1.参数校验 参数的非空校验
        checkParams(saleChance.getCustomerName(), saleChance.getLinkMan(), saleChance.getLinkPhone());
        // 2.设置相关参数默认值
        saleChance.setIsValid(1);
        saleChance.setCreateDate(new Date());
        saleChance.setUpdateDate(new Date());

        if (StringUtils.isNotBlank(saleChance.getAssignMan())){
            saleChance.setState(StateStatus.STATED.getType());
            saleChance.setDevResult(DevResult.DEV_SUCCESS.getStatus());
            saleChance.setAssignTime(new Date());

        } else {
            saleChance.setAssignTime(null);
            saleChance.setState(StateStatus.UNSTATE.getType());
            saleChance.setDevResult(DevResult.UNDEV.getStatus());

        }

        // 3.执⾏添加 判断结果
        AssertUtil.isTrue(saleChanceMapper.insertSelective(saleChance) < 1,"营销机会数据添加失败！");

    }

    /**
     * 营销机会数据更新
     * 1.参数校验
     *      id:记录必须存在
     *      customerName:⾮空
     *      linkMan:⾮空
     *      linkPhone:⾮空，11位⼿机号
     * 2. 设置相关参数值
     *      updateDate:系统当前时间
     *      原始记录 未分配 修改后改为已分配(由分配⼈决定)
     *          state 0->1
     *          assginTime 系统当前时间
     *          devResult 0-->1
     *      原始记录 已分配 修改后 为未分配
     *          state 1-->0
     *          assignTime 待定 null
     *          devResult 1-->0
     * 3.执⾏更新 判断结果
     */
    public void updateSaleChance(SaleChance saleChance){
        // 1.参数校验
        // 通过id查询记录
        SaleChance temp = saleChanceMapper.selectByPrimaryKey(saleChance.getId());
        AssertUtil.isTrue(temp == null,"待更新记录不存在！");
        // 校验基础参数
        checkParams(saleChance.getCustomerName(), saleChance.getLinkMan(), saleChance.getLinkPhone());
        // 2. 设置相关参数值
        saleChance.setUpdateDate(new Date());
        if (StringUtils.isBlank(temp.getAssignMan()) && StringUtils.isNotBlank(saleChance.getAssignMan())){
            // 原始记录 未分配 修改后改为已分配(由分配⼈决定)
            saleChance.setState(StateStatus.STATED.getType());
            saleChance.setAssignTime(new Date());
            saleChance.setDevResult(DevResult.DEVING.getStatus());
        } else if (StringUtils.isNotBlank(temp.getAssignMan()) && StringUtils.isBlank(saleChance.getAssignMan())){
            // 原始记录 未分配 修改后改为已分配(由分配⼈决定)
            saleChance.setState(StateStatus.UNSTATE.getType());
            saleChance.setAssignTime(null);
            saleChance.setDevResult(DevResult.UNDEV.getStatus());
        }

        // 3.执⾏更新 判断结果
        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(saleChance) < 1, "营销机会数据更新失败！");
    }

    /**
     * 营销机会数据删除
     * @param ids
     */
    public void deleteSaleChance (Integer[] ids){
        // 判断要删除的id是否为空
        AssertUtil.isTrue(null == ids || ids.length ==0, "请选择需要删除的数据！" );
        // 删除数据
        AssertUtil.isTrue(saleChanceMapper.deleteBatch(ids) < 0, "营销机会数据删除失败！");
    }



    /**
     * 参数的校验
     * @param customerName
     * @param linkMan
     * @param linkPhone
     */
    private void checkParams(String customerName, String linkMan, String linkPhone) {
        AssertUtil.isTrue(StringUtils.isBlank(customerName),"请输入用户名！");
        AssertUtil.isTrue(StringUtils.isBlank(linkMan),"请输入联系人！");
        AssertUtil.isTrue(StringUtils.isBlank(linkPhone),"请输入联系号码！");
        AssertUtil.isTrue(!PhoneUtil.isMobile(linkPhone),"手机号格式不正确！");
    }



    /**
     * 更新营销机会的状态
     * 成功 = 2
     * 失败 = 3
     * @param id
     * @param devResult
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSaleChanceDevResult(Integer id, Integer devResult) {
        AssertUtil.isTrue(null == id,"待更新记录不存在！");
        SaleChance temp = saleChanceMapper.selectByPrimaryKey(id);
        AssertUtil.isTrue(null == temp,"待更新记录不存在!");
        temp.setDevResult(devResult);
        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(temp)!=1,"机会数据更新失败！");
    }
}


