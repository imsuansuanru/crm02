layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;

    /**
     * 计划项数据展示
     */
    var tableIns = table.render({
        id:'cusDevPlanTable',
        elem: '#cusDevPlanList',
        url : ctx+'/cus_dev_plan/list?saleChanceId='+$("[name='id']").val(),
        cellMinWidth : 95,
        page : true,
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        toolbar: "#toolbarDemo",
        id : "cusDevPlanListTable",
        cols : [[
            {type: "checkbox", fixed:"center"},
            {field: "id", title:'编号',fixed:"true"},
            {field: 'planItem', title: '计划项',align:"center"},
            {field: 'exeAffect', title: '执行效果',align:"center"},
            {field: 'planDate', title: '执行时间',align:"center"},
            {field: 'createDate', title: '创建时间',align:"center"},
            {field: 'updateDate', title: '更新时间',align:"center"},
            {title: '操作',fixed:"right",align:"center", minWidth:150,
                templet:"#cusDevPlanListBar"}
        ]]
    });


    table.on('toolbar(cusDevPlans)',function (data){
        if (data.event == "add") { // 添加计划项

            // 打开添加或修改计划项的页面
            openAddOrUpdateCusDevPlanDialog();
        } else if (data.event == "success") {
            // 开发成功
            updateSaleChanceDevResult(2);
        } else if (data.event == "failed") {
            // 开发失败
            updateSaleChanceDevResult(3);
        }
    });


    table.on("tool(cusDevPlans)",function (obj) {
        var layEvent = obj.event;
        if(layEvent === "edit"){
            // 打开添加或修改计划项的页面
            openAddOrUpdateCusDevPlanDialog(obj.data.id);

        }else if(layEvent === "del"){
            // 删除计划项
            deleteCusDevPlan(obj.data.id);
        }
    });

    function deleteCusDevPlan(id){
        // 询问⽤户是否确认删除
        layer.confirm('您确定要删除该记录吗？',{icon:3, title:'开发项数据管理'},function (index){
            // 发送ajax请求，执行删除操作
            $.post(ctx + '/cus_dev_plan/delete',{id:id},function (result){
                // 判断删除结果
                if (result.code == 200){
                    // 提示成功
                    layer.msg('删除成功',{icon:6});
                    // 刷新数据表格
                    tableIns.reload();
                } else{
                    // 提示失败原因
                    layer.msg(result.msg,{icon:5});
                }
            });
        });
    }


    /**
     * 更新营销机会的状态
     * @param devResult
     */
    function updateSaleChanceDevResult(devResult){
        // 获取当前营销机会的ID（隐藏域中获取）
        var sid = $("[name='id']").val();
        // 弹出提示框询问⽤户
        layer.confirm('确认执⾏当前操作？', {icon:3, title:"计划项维护"}, function (index){
            $.post(ctx+"/sale_chance/updateSaleChanceDevResult",{id:sid,devResult:devResult},
                function (result){
                if (result.code == 200){
                    layer.msg("操作成功！")
                    // 关闭弹出层
                    layer.closeAll("iframe");
                    // 刷新父页面
                    parent.location.reload();
                } else {
                    layer.msg(result.msg,{icon:5})
                }
            });
        });
    }



    function openAddOrUpdateCusDevPlanDialog(id){
        // 弹出层的标题
        var title = "计划项管理 - 添加计划项";
        var url = ctx + "/cus_dev_plan/addOrUpdateCusDevPlanDialog?sid="+$("[name='id']").val();
        if(id){
            title="计划项管理管理-更新计划项";
            url=url+"&id="+id;
        }

        // iframe层
        layui.layer.open({
            // 类型
            type: 2,
            // 标题
            title: title,
            //宽高
            area: ['500px','300px'],
            // url地址
            content: url,
            // 可以最大化与最小化
            maxmin: true
        })
    }




});
