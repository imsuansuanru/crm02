layui.use(['form', 'layer'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;


    form.on("submit(addOrUpdateCusDevPlan)",function (data){
        // 提交数据时的加载层 （https://layer.layui.com/）
        var index = top.layer.msg("数据提交中,请稍后...", {
            icon: 16, // 图标
            time: false, // 不关闭
            shade: 0.8 // 设置遮罩的透明度
        });


        // 得到所有表单元素的值
        var formData = data.field;
        // 请求的地址
        var url = ctx + "/cus_dev_plan/add";

        if($("[name='id']").val()){
            url = ctx + "/cus_dev_plan/update";
        }

        // 发送ajax请求
        $.post(url,formData, function (result){
            // 操作成功
            if (result.code == 200){
                // 提示成功
                top.layer.msg("操作成功！");
                // 关闭加载层
                top.layer.close(index);
                // 关闭弹出层
                layer.closeAll("iframe");
                // 刷新⽗⻚⾯，重新渲染表格数据
                parent.location.reload();
            } else {
                // 失败
                layer.msg(result.msg);
            }
        });
        return false;


    })







    /**
     * 关闭弹出层
     */
    $("#closeBtn").click(function (){
        //当你在iframe页面关闭自身时
        var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
        parent.layer.close(index); //再执行关闭
    })



});