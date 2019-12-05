//1.定义一个公共的控制器
app.controller("baseController",function($scope){

    //分页控件配置
    $scope.paginationConf = {
        currentPage: 1,             //代表当前页
        totalItems: 10,             //代表总记录数
        itemsPerPage: 10,           //代表每页的大小
        perPageOptions: [10, 20, 30, 40, 50],   //分页导航中显示的下拉框，用于选择分页大小
        onChange: function(){       //每次窗体加载完毕后自动调用的方法
            $scope.getGoods();
        }
    };
    //定义要删除的对象的id列表
    $scope.selectIds = [];
    //定义用户选择某一项时根据复选状态决定是否向  $scope.selectIds数组中放值还是删除某个id值
    $scope.updateSelection=(event,id)=>{
        if(event.target.checked){       //① 如果复选框被复选，就向$scope.selectIds这个数组中放id进去
            $scope.selectIds.push(id);
        }else{                          //② 如果未被复选，就从数组$scope.selectIds中删除此id值
            //③ 得到此id在数组$scope.selectIds中下标位置
            let index = $scope.selectIds.indexOf(id);
            //④ 从数组中删除此id
            $scope.selectIds.splice(index,1);
        }
    }
})