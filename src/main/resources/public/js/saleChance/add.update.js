layui.use(['form', 'layer'], function () {
    var form = layui.form,
        layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery;


    /**
     * 关闭弹出层
     */
    $("#closeBtn").click(function (){
        //当你在iframe页面关闭自身时
        var index = parent.layer.getFrameIndex(window.name); //先得到当前iframe层的索引
        parent.layer.close(index); //再执行关闭
    })



    /**
     * 加载下拉框
     */
    $.ajax({
        type:"get",
        url:ctx + "/user/queryAllSales",
        data:{},
        success:function (data){
            // 判断返回的数据是否为空
            if (data != null){
                console.log(data);
                // 获取隐藏域中的指派人id
                var assignManId = $("#assignManId").val();

                for (var i = 0; i < data.length; i++) {
                    var opt = "";
                    // 当前修改记录的指派⼈的值 与 循环到的值 相等，下拉框则选中
                    if (assignManId == data[i].id){
                        opt = "<option value='"+data[i].id+"' selected>"+data[i].uname+"</option>";
                    } else {
                        opt = "<option value='"+data[i].id+"'>"+data[i].uname+"</option>";
                    }
                    // 将下拉选项设置到下拉框中
                    $("#assignMan").append(opt);
                }
            }
            // 重新渲染下拉框内容
            layui.form.render("select");
        }
    });







    /**
     * 监听submit事件
     * 实现营销机会的添加与更新
     */
    form.on("submit(addOrUpdateSaleChance)", function (data) {
        // 提交数据时的加载层 （https://layer.layui.com/）
        var index = layer.msg("数据提交中,请稍后...", {
            icon: 16, // 图标
            time: false, // 不关闭
            shade: 0.8 // 设置遮罩的透明度
        });
        // 请求的地址
        var url = ctx + "/sale_chance/add";


        var saleChanceId = $("input[name='id']").val();
        //判断隐藏域中的id是否为空，如果不为空则为修改操作
        if (saleChanceId != null && saleChanceId != ''){
            url = ctx + "/sale_chance/update";
        }


        // 发送ajax请求
        $.post(url,data.field, function (result){
            // 操作成功
            if (result.code == 200){
                // 提示成功
                layer.msg("操作成功！");
                // 关闭加载层
                layer.close(index);
                // 关闭弹出层
                layer.closeAll("iframe");
                // 刷新⽗⻚⾯，重新渲染表格数据
                parent.location.reload();
            } else {
                layer.msg(result.msg);
            }
        });
        return false; // 阻⽌表单提交
    });
});