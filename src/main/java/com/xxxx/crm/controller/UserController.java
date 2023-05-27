package com.xxxx.crm.controller;

import com.xxxx.crm.base.BaseController;
import com.xxxx.crm.base.ResultInfo;
import com.xxxx.crm.exceptions.ParamsException;
import com.xxxx.crm.query.UserQuery;
import com.xxxx.crm.service.UserService;
import com.xxxx.crm.utils.LoginUserUtil;
import com.xxxx.crm.vo.User;
import com.xxxx.crm.vo.UserModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("user")
public class UserController extends BaseController {

    @Resource
    private UserService userService;


    /**
     * 用户登录
     * @param userName
     * @return
     */
    @PostMapping("login")
    @ResponseBody
    public ResultInfo userLogin(String userName, String userPwd){
        ResultInfo resultInfo = new ResultInfo();

        UserModel userModel = userService.userLogin(userName, userPwd);

        resultInfo.setResult(userModel);

        // 捕获Service层可能会抛出的异常
        /*try {
            // 调⽤Service层的登录⽅法，得到返回的⽤户对象
            UserModel userModel = userService.userLogin(userName, userPwd);
            *//**
             * 登录成功后，有两种处理：
             * 1. 将⽤户的登录信息存⼊ Session （ 问题：重启服务器，Session 失效，客户端需要重复登录 ）
             * 2. 将⽤户信息返回给客户端，由客户端（Cookie）保存
             *//*
            // 将返回的UserModel对象设置到 ResultInfo 对象中
            resultInfo.setResult(userModel);

        } catch (ParamsException e) {
            e.printStackTrace();
            resultInfo.setCode(e.getCode());
            resultInfo.setMsg(e.getMsg());
        } catch (Exception e) {
            e.printStackTrace();
            resultInfo.setCode(500);
            resultInfo.setMsg("操作失败！");
        }*/
        return resultInfo;
    }


    /**
     * 修改密码
     * @param request
     * @param oldPassword
     * @param newPassword
     * @param confirmPassword
     * @return
     */
    @PostMapping("updatePwd")
    @ResponseBody
    public ResultInfo updateUserPassword(HttpServletRequest request, String oldPassword,
                                         String newPassword, String confirmPassword){
        ResultInfo resultInfo = new ResultInfo();

        // 从cookie中获取用户id
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
        // 调用Service层的方法修改密码
        userService.updateUserPassword(userId, oldPassword, newPassword, confirmPassword);

        // 捕获Service层可能会抛出的异常
        /*try{
            // 从cookie中获取用户id
            Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
            // 调用Service层的方法修改密码
            userService.updateUserPassword(userId, oldPassword, newPassword, confirmPassword);


        } catch (ParamsException e) {
            e.printStackTrace();
            resultInfo.setCode(e.getCode());
            resultInfo.setMsg(e.getMsg());
        } catch (Exception e) {
            e.printStackTrace();
            resultInfo.setCode(500);
            resultInfo.setMsg("操作失败！");
        }*/
        return resultInfo;


    }

    /**
     * 进入修改密码的界面
     * @return
     */
    @RequestMapping("toPasswordPage")
    public String toPasswordPage(){
        return "user/password";
    }


    /**
     * 查询所有的销售⼈员
     * @return
     */
    @RequestMapping("queryAllSales")
    @ResponseBody
    public List<Map<String, Object>> queryAllSales() {
        return userService.queryAllSales();
    }


    /**
     * 多条件查询⽤户数据
     * @param userQuery
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> queryUserByParams(UserQuery userQuery) {
        return userService.queryByParamsForTable(userQuery);
    }




    /**
     * 进⼊⽤户⻚⾯
     * @return
     */
    @RequestMapping("index")
    public String index(){
        return "user/user";
    }



    /**
     * 添加⽤户
     * @param user
     * @return
     */
    @RequestMapping("add")
    @ResponseBody
    public ResultInfo saveUser(User user) {
        userService.saveUser(user);
        return success("⽤户添加成功！");
    }


    /**
     * 更新⽤户
     * @param user
     * @return
     */
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateUser(User user) {
        userService.updateUser(user);
        return success("⽤户更新成功！");
    }


    /**
     * 进⼊⽤户添加或更新⻚⾯
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("addOrUpdateUserPage")
    public String addUserPage(Integer id, Model model){
        if(null != id){
            model.addAttribute("userInfo",userService.selectByPrimaryKey(id));
        }
        return "user/add_update";
    }



    /**
     * 删除⽤户
     * @param ids
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteUser(Integer[] ids){
        userService.deleteBatch(ids);
        return success("⽤户记录删除成功");
    }
}
