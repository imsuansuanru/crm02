layui.use(['form','jquery','jquery_cookie'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);

    /**
     * 用户登录 表单提交
     */
    // 指向元素为 `<select lay-filter="login"></select>` 的选择事件
    form.on('submit(login)', function(data){
        var field = data.field; // 获取表单全部字段值


        // 判断参数是否为空
        if (field.username == "undefined" || field.username.trim() == ""){
            layer.msg("用户名不能为空！")
            return false;
        }
        if (field.password == "undefined" || field.password.trim() == ""){
            layer.msg("用户密码不能为空！")
            return false;
        }


        // 发送ajax请求，请求用户登录
        $.ajax({
            type:"post",
            url:ctx + "/user/login",
            data:{
                userName:field.username,
                userPwd:field.password
            },
            dataType:"json",
            success:function (result){
                console.log(result);
                if (result.code == 200){
                    layer.msg("登录成功！",function (){
                        if ($("#rememberMe").prop("checked")){
                            $.cookie("userIdStr",result.result.userIdStr,{expires:7});
                            $.cookie("userName",result.result.userName,{expires:7});
                            $.cookie("trueName",result.result.trueName,{expires:7});
                        } else {
                            $.cookie("userIdStr",result.result.userIdStr);
                            $.cookie("userName",result.result.userName);
                            $.cookie("trueName",result.result.trueName);
                        }


                        // 登录成功后 跳转到首页
                        window.location.href = ctx + "/main";
                    });
                } else {
                    // 登录失败
                    layer.msg(result.msg, {icon:5});
                }
            }
        });
        // 阻止提交
        return false;
    });

});
