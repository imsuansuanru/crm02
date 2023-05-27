layui.use(['table','layer'],function(){
    var layer = parent.layer === undefined ? layui.layer : top.layer,
        $ = layui.jquery,
        table = layui.table;


    /**
     * ⽤户列表展示
     */
    var tableIns = table.render({
        elem: '#userList',
        url : ctx+'/user/list',
        cellMinWidth : 95,
        page : true,
        height : "full-125",
        limits : [10,15,20,25],
        limit : 10,
        toolbar: "#toolbarDemo",
        id : "userListTable",
        cols : [[
            {type: "checkbox", fixed:"left", width:50},
            {field: "id", title:'编号',fixed:"true", width:80},
            {field: 'userName', title: '⽤户名', minWidth:50, align:"center"},
            {field: 'email', title: '⽤户邮箱', minWidth:100, align:'center'},
            {field: 'phone', title: '⽤户电话', minWidth:100, align:'center'},
            {field: 'trueName', title: '真实姓名', align:'center'},
            {field: 'createDate', title: '创建时间', align:'center',minWidth:150},
            {field: 'updateDate', title: '更新时间', align:'center',minWidth:150},
            {title: '操作', minWidth:150, templet:'#userListBar',fixed:"right",align:"center"}
        ]]
    });



    $(".search_btn").click(function (){
        // 表格重载多条件查询
        tableIns.reload({
            // 设置需要传递给后端的参数
            where: { //设定异步数据接口的额外参数，任意设
                // 通过文本框/下拉框的值，设置传递的参数
                userName: $("[name='userName']").val()// 用户名称
                ,email:$("[name='email']").val()// 邮箱
                ,phone:$("[name='phone']").val()// 电话
            }
            ,page: {
                curr: 1 // 重新从第 1 页开始
            }
        });
    });


    /**
     * 监听头部工具栏
     */
     table.on('toolbar(users)',function (data){


         if (data.event == "add"){ //添加用户

             // 打开添加/修改用户的对话框
             openAddOrUpdateUserDialog();

         } else if (data.event == "del"){ //删除用户
             // 获取被选中的数据的信息
             var checkStatus = table.checkStatus(data.config.id);
             // 删除多个用户记录
             deleteUsers(checkStatus.data)
         }
     });


     table.on("tool(users)", function (data){
         var layEvent = data.event;
         // 监听编辑事件
         if(layEvent === "edit") {
             openAddOrUpdateUserDialog(data.data.id);
         } else if(layEvent === "del") {
             // 监听删除事件
             deleteUser(data.data.id);
         }
     });


     function deleteUser(id){
         // 询问用户是否要确认删除
         layer.confirm('确定要删除该条记录吗？',{icon:3, title:"营销机会管理"},function (index) {
             // 关闭确认框
             layer.close(index);

             // 发送ajax请求，删除记录
             $.ajax({
                 type: "post",
                 url: ctx + "/user/delete",
                 data: {
                     ids:id
                 },
                 success: function (result) {
                     // 判断删除结果
                     if (result.code == 200) {
                         // 提示成功
                         layer.msg("删除成功！", {icon: 6});
                         // 刷新表格
                         tableIns.reload();
                     } else {
                         // 提示失败
                         layer.msg(result.msg, {icon: 5})
                     }
                 }
             })
         })
     }




    /**
     * 打开添加/修改用户的对话框
     */
    function openAddOrUpdateUserDialog(userId){
        var title = "用户管理-用户添加";
        var url = ctx + "/user/addOrUpdateUserPage";
        if (userId) {
            url = url + "?id="+userId;
            title = "用户管理-用户更新";
        }

        layui.layer.open({
            title : title,
            type : 2,
            area:["650px","400px"],
            maxmin:true,
            content : url
        });
    }





    function deleteUsers(datas) {
        if(datas.length == 0){
            layer.msg("请选择删除记录!", {icon: 5});
            return;
        }
        layer.confirm('确定删除选中的⽤户记录？', {
            btn: ['确定','取消'] //按钮
        }, function(index){
            layer.close(index);
            var ids= "ids=";
            for(var i=0;i<datas.length;i++){
                if(i<datas.length-1){
                    ids=ids+datas[i].id+"&ids=";
                }else {
                    ids=ids+datas[i].id
                }
            }
            $.ajax({
                type:"post",
                url:ctx + "/user/delete",
                data:ids,
                dataType:"json",
                success:function (data) {
                    if(data.code==200){
                        // 提示成功
                        layer.msg("删除成功！", {icon: 6});
                        // 刷新表格
                        tableIns.reload();
                    }else{
                        layer.msg(data.msg, {icon: 5});
                    }
                }
            })
        });
    }






});
