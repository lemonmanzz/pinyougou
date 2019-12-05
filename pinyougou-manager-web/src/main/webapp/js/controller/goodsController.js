app.controller("goodsController",function ($scope, $controller, itemCatService,goodsService) {
    //1.伪继承
    $controller("baseController",{$scope:$scope})
    //2.查询所有的未审核，未删除的商品列表
    $scope.search = ()=>{
        goodsService.findAll($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage).success(response =>{
            $scope.goodsList = response.rows;
            //2.2更新总记录数
            $scope.paginationConf.totalItems = response.total;
        })
    };

    //2.查询所有的分类，
    $scope.findCategories = ()=>{
        itemCatService.findAll().success(response=>{
            $scope.categories = response;
        })
    };

    //3.根据id获取分类名称
    $scope.getCategoryNameByid = (id)=>{
       if (id) {
           for (let i = 0, len = $scope.categories.length; i < len; i++) {
               if ($scope.categories[i].id === id) {
                   return $scope.categories[i].name;
               }
           }
       }
       return null;
        for (let i = 0, len = $scope.categories.length; i < len; i++) {
           if ($scope.categories[i].id === id) {
               return $scope.categories[i].name;
           }
        }
    };

    //更改状态方法，包括审核与删除
    $scope.updateStatus = (statusField)=>{
        goodsService.updateStatus($scope.selectedIds,1,statusField).success(response =>{
            if (response.success) {
                $scope.search();
            }else {
                alert(response.message);
            }
        })
    };


});