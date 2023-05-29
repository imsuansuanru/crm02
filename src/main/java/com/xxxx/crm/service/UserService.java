package com.xxxx.crm.service;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.xxxx.crm.base.BaseService;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.dao.UserMapper;
import com.xxxx.crm.dao.UserRoleMapper;
import com.xxxx.crm.utils.AssertUtil;
import com.xxxx.crm.utils.Md5Util;
import com.xxxx.crm.utils.PhoneUtil;
import com.xxxx.crm.utils.UserIDBase64;
import com.xxxx.crm.vo.User;
import com.xxxx.crm.vo.UserModel;
import com.xxxx.crm.vo.UserRole;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class UserService extends BaseService<User, Integer> {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    /**
     * 用户登录
     * @param userName
     * @param userPwd
     * @return
     */
    public UserModel userLogin(String userName, String userPwd){
        // 非空校验
        checkLoginParams(userName, userPwd);
        // 用userName查询用户
        User user = userMapper.queryUserByUserName(userName);
        // 判断查询结果是否为空
        AssertUtil.isTrue(null == user, "用户不存在或已注销！");
        // 将查询到的用户密码和前台传入的用户密码比较
        checkLoginPwd(userPwd, user.getUserPwd());
        // 将查询对象的所需字段返回
        return buildUserModel(user);

    }

    /**
     * 修改密码
     * @param userId
     * @param oldPassword
     * @param newPassword
     * @param confirmPassword
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUserPassword(Integer userId, String oldPassword,
                                         String newPassword, String confirmPassword){
        // 通过userId获取用户对象
        User user = userMapper.selectByPrimaryKey(userId);
        // 1.参数校验
        checkPassWord(user, oldPassword, newPassword, confirmPassword);
        // 2.设置用户新密码到user对象中
        user.setUserPwd(Md5Util.encode(newPassword));
        // 3.执行更新操作
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user) < 1, "用户密码更新失败！");
    }


    /**
     * 修改密码时所做的参数校验
     * @param user
     * @param oldPassword
     * @param newPassword
     * @param confirmPassword
     */
    private void checkPassWord(User user, String oldPassword, String newPassword, String confirmPassword) {
        // 判断user是否为空
        AssertUtil.isTrue(user == null,"用户未登录或不存在！");
        // 原始密码的非空校验
        AssertUtil.isTrue(StringUtils.isBlank(oldPassword), "原始密码不能为空！");
        // 原始密码的正确性校验
        AssertUtil.isTrue(!user.getUserPwd().equals(Md5Util.encode(oldPassword)),"原始密码填写错误！");
        // 新密码的非空校验
        AssertUtil.isTrue(StringUtils.isBlank(newPassword),"请输入新密码！");
        // 新密码不能和原始密码相同
        AssertUtil.isTrue(oldPassword.equals(newPassword), "新密码不能和原始密码相同！");
        // 确认密码不能为空
        AssertUtil.isTrue(StringUtils.isBlank(confirmPassword), "请输入确认密码！");
        // 确认密码需要和新密码相同
        AssertUtil.isTrue(!newPassword.equals(confirmPassword), "新密码与确认密码不一致！");
    }


    /**
     * 构建返回的⽤户信息
     * @param user
     * @return
     */
    private UserModel buildUserModel(User user){
        UserModel userModel = new UserModel();
        /*userModel.setUserId(user.getId());*/
        userModel.setUserIdStr(UserIDBase64.encoderUserID(user.getId()));
        userModel.setUserName(user.getUserName());
        userModel.setTrueName(user.getTrueName());
        return userModel;
    }



    /**
     * 验证登录密码
     * @param userPwd 前台传递的密码
     * @param upwd 数据库中查询到的密码
     * @return
     */
    private void checkLoginPwd(String userPwd, String upwd) {
        // 数据库中的密码是经过加密的，将前台传递的密码先加密，再与数据库中的密码作⽐较
        userPwd = Md5Util.encode(userPwd);
        // ⽐较密码
        AssertUtil.isTrue(!userPwd.equals(upwd), "用户密码不正确！");
    }


    /**
     * 验证用户登录参数
     * @param userName
     * @param userPwd
     */
    private void checkLoginParams(String userName, String userPwd) {
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户名不能为空！");
        AssertUtil.isTrue(StringUtils.isBlank(userPwd),"用户密码不能为空！");

    }


    /**
     * 查询所有的销售⼈员
     * @return
     */
    public List<Map<String, Object>> queryAllSales() {
        return userMapper.queryAllSales();
    }




    /**
     * 添加⽤户
     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveUser(User user) {
        // 1. 参数校验
        checkParams(user.getUserName(), user.getEmail(), user.getPhone(),null);
        // 2. 设置默认参数
        user.setIsValid(1);
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        user.setUserPwd(Md5Util.encode("123456"));
        // 3. 执⾏添加，判断结果
        AssertUtil.isTrue(userMapper.insertSelective(user) == null, "⽤户添加失败！");


        /* 用户角色关联 */
        relationUserRole(user.getId(), user.getRoleIds());
    }


    /**
     * 用户角色关联
     * @param userId
     * @param roleIds
     */
    private void relationUserRole(Integer userId, String roleIds) {
        // 通过用户ID查询 角色记录
        Integer count = userRoleMapper.countUserRoleByUserId(userId);
        // 判断角色记录是否存在
        if (count > 0) {
            // 如果角色记录存在，则删除改用户对应的角色记录
            AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId) != count, "用户角色分配失败！");
        }

        // 判断角色ID是否存在，如果存在，则添加该用户对应的角色记录
        if (StringUtils.isNotBlank(roleIds)) {
            // 将用户角色数据设置到集合中，执行批量添加
            List<UserRole> userRoleList = new ArrayList<>();
            // 将角色ID字符串转换成数组
            String [] roleIdsArray = roleIds.split(",");
            // 遍历数组，得到对应的用户角色对象，并设置到集合中
            for (String roleId : roleIdsArray) {
                UserRole userRole = new UserRole();
                userRole.setRoleId(Integer.parseInt(roleId));
                userRole.setUserId(userId);
                userRole.setCreateDate(new Date());
                userRole.setUpdateDate(new Date());

                // 设置到集合中
                userRoleList.add(userRole);
            }
            // 批量添加用户角色记录
            AssertUtil.isTrue(userRoleMapper.insertBatch(userRoleList) != userRoleList.size(),"用户角色分配失败!");
        }

    }


    /**
     * 用户参数校验
     * @param userName
     * @param email
     * @param phone
     */
    private void checkParams(String userName, String email, String phone, Integer userId) {
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户名不能为空！");
        // 验证用户名是否存在
        User temp = userMapper.queryUserByUserName(userName);
        // 如果是添加操作，数据库是没有数据的，数据库中只要查询到⽤户记录就表示不可⽤
        // 如果是修改操作，数据库是有数据的，查询到⽤户记录就是当前要修改的记录本身就表示可⽤，否则不可⽤
        // 数据存在，且不是当前要修改的⽤户记录，则表示其他⽤户占⽤了该⽤户名
        AssertUtil.isTrue(temp!= null && !(temp.getId().equals(userId)),"该用户名已存在！");
        AssertUtil.isTrue(StringUtils.isBlank(email),"请输入邮箱地址！");
        AssertUtil.isTrue(!PhoneUtil.isMobile(phone),"手机号码格式不正确！");
    }


    /**
     * 更新⽤户
     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUser(User user){
        // 1. 参数校验
        // 通过id查询⽤户对象
        User temp = userMapper.selectByPrimaryKey(user.getId());
        // 判断对象是否存在
        AssertUtil.isTrue(temp == null,"待更新的记录不存在！");
        // 验证参数
        checkParams(user.getUserName(),user.getEmail(),user.getPhone(),user.getId());
        // 2. 设置默认参数
        temp.setUpdateDate(new Date());
        // 3. 执⾏更新，判断结果
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)!=1,"用户更新失败！");

        /* 用户角色关联 */
        relationUserRole(user.getId(), user.getRoleIds());
    }


    /**
     * 删除⽤户
     * @param ids
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUserByIds(Integer[] ids) {
        AssertUtil.isTrue(null==ids || ids.length == 0,"请选择待删除的⽤户记录!");
        AssertUtil.isTrue(deleteBatch(ids) != ids.length,"⽤户记录删除失败!");

        // 遍历用户ID的数组
        for (Integer userId : ids) {
            // 通过用户ID查询对应的用户角色记录
            Integer count = userRoleMapper.countUserRoleByUserId(userId);
            // 判断用户角色记录是否存在
            if (count > 0) {
                // 通过用户ID删除对应的用户角色记录
                AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId) != count, "删除用户失败！");
            }
        }
    }
}
