app.controller("baseController",function ($scope) {
    $scope.paginationConf = {
        currentPage: 1,             //代表当前页
        totalItems: 10,             //代表总记录数
        itemsPerPage: 10,           //代表每页的大小
        perPageOptions: [10, 20, 30, 40, 50],   //分页导航中显示的下拉框，用于选择分页大小
        onChange: function(){       //每次窗体加载完毕后自动调用的方法
            $scope.search();
        }
    };

    //定义一个要删除的数组
    $scope.selectedIds = [];
    //定义用户选择某一项时根据复选状态决定是否向  $scope.selectIds数组中放值还是删除某个id值
    $scope.updateSelection = (event,id)=>{
        //1.判断是否选中
        if (event.target.checked){
            //2.放入数组中
            $scope.selectedIds.push(id);
        }else {
            //3.得到下标的位置
            let index = $scope.selectedIds.indexOf(id);
            //4.从数组中删除
            $scope.selectedIds.splice(index,1)
        }
    }

});