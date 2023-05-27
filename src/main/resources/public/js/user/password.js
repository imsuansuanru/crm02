layui.use(['form','jquery','jquery_cookie'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);


    form.on("submit(saveBtn)",function (data){
        // 获取表单元素的内容
        var fieldData = data.field;
        // 所有表单元素的值
        console.log(fieldData);

        // 发送ajax请求
        $.ajax({
            type:"post",
            url:ctx + "/user/updatePwd",
            data:{
                oldPassword:fieldData.old_password,
                newPassword:fieldData.new_password,
                confirmPassword:fieldData.again_password
            },
            success:function (result){
                if (result.code == 200){
                    // 修改密码成功后，系统将在3秒后退出...
                    layer.msg("修改密码成功，系统将在3秒后退出...",function (){
                        // 清空cookie
                        $.removeCookie("userIdStr",{domin:"localhost",path:"/crm"});
                        $.removeCookie("userName",{domin:"localhost",path:"/crm"});
                        $.removeCookie("trueName",{domin:"localhost",path:"/crm"});

                        // 跳转到登录页面（父窗口跳转）
                        window.parent.location.href = ctx + "/index";
                    });
                } else {
                    layer.msg(result.msg,{icon:5});
                }
            }
        });
        return false;
    });
});