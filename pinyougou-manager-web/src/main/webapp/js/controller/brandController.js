app.controller("brandController",function ($scope, $controller, brandService) {
    //1.定义继承关系（伪继承）
    $controller("baseController",{$scope:$scope});
    $scope.findAll = () =>{
        brandService.findAll().success(response =>{
            $scope.brandList = response;
        })
    };
    //2.分页查询，加上条件查询
    $scope.search = ()=>{
        $scope.selectedIds = [];
        let index ;
        index = layer.load(3)
        //2.1获取当前页
        let page = $scope.paginationConf.currentPage;
        //2.2 获取每页的展示条数
        let pageSize = $scope.paginationConf.itemsPerPage;
        //2.3调用服务进行查询
        brandService.search(page,pageSize,$scope.searchEntity).success(response => {

            //2.4将查询结果赋值给brandList
            $scope.brandList = response.rows;
            //2.5进行总数的更新
            $scope.paginationConf.totalItems = response.total;
            layer.close(index);
        })
    };
    //3.添加或者修改品牌信息，通过是否有id进行区分
    $scope.save = ()=>{
        let url = "/brand/add.do";
        //3.1判断是否拥有id，有则进行修改，否则进行新增
        if ($scope.entity.id){
            url = "/brand/update.do";
        }
        //3.2 调用service的增加修改方法
        brandService.addOrUpdate(url,$scope.entity).success(response =>{
            //得到返回值
            if (response.success){
                layer.msg(response.message);
                $scope.search();
            }else {
                alert(response.message);
            }
        })
    }
    $scope.updateUI = (UpdateBrand) => {
        $scope.entity =  UpdateBrand;
    }
    //删除功能
    $scope.dele = ()=>{
        brandService.dele($scope.selectedIds).success(response => {
            if (response.success){
                layer.msg(response.message);
                $scope.search();
            } else {
                alert(response.message)
            }
        })
    }
});