package com.xxxx.crm.vo;


/**
 * 定义 UserModel 实体类，⽤来返回登录成功后的⽤⼾信息
 */
public class UserModel {
    /*private Integer userId;*/
    private String userName;
    private String trueName;
    private String userIdStr;

    public String getUserIdStr() {
        return userIdStr;
    }

    public void setUserIdStr(String userIdStr) {
        this.userIdStr = userIdStr;
    }


/*public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }*/

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTrueName() {
        return trueName;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName;
    }
}
