app.controller("specificationController",function ($scope, $controller, specificationService) {
    //1.定义伪继承
    $controller("baseController",{$scope:$scope});
    //设置默认查询全部
    $scope.specName = ""
    //2.分页查询并且可以带查询条件
    $scope.search = ()=>{
        //2.1加载动画
        let index = layer.load(1);
        //2.2 获取当前页
        let page = $scope.paginationConf.currentPage;
        //2.3 获取每页显示多少条数据
        let pageSize = $scope.paginationConf.itemsPerPage;
        //2.4调用服务进行查询
        specificationService.search(page,pageSize,$scope.specName).success(response=>{
            //2.4.1 将查询结果赋值给specificationList
            $scope.specificationList = response.rows;
            //2.4.2 更新查询总条数
            $scope.paginationConf.totalItems = response.total;
            layer.close(index);
        })
    }
    //为规格组合对象进行初始化
    $scope.entity = {spec:{},specificationOptionList:[]};
    //新增加规格选项
    $scope.addOptions = () =>{
        $scope.entity.specificationOptionList.push({});
    }
    //移除规格选项
    $scope.removeOptions = index =>{
        $scope.entity.specificationOptionList.splice(index,1);
    }

    //保存方法
    $scope.save = ()=>{
        //添加的控制层地址
        let  url = "/specification/add.do";
        if ($scope.entity.spec.id) {
            //修改的控制层地址
            url = "/specification/update.do";
        }

        specificationService.save(url,$scope.entity).success(response =>{
            if (response.success){
                //刷新页面
                $scope.paginationConf.currentPage = 1;
                $scope.specName = '';
                $scope.search();
            }else {
                alert(response.message);
            }
            //关闭模态框
            $("#editModal").modal("hide");
        })

    }

    //修改方法，即打开模态框，显示原有数据
    $scope.update = id =>{
        specificationService.findOne(id).success(response=>{
            $scope.entity = response;
        })
    }

    //删除方法
    $scope.dele = () =>{
        specificationService.dele($scope.selectedIds).success(response =>{
           if (response.success) {
               $scope.selectedIds = [];
               $scope.search();
           }else {
               alert(response.message);
           }
        })
    }

    //查询所有的方法
    $scope.findAll = ()=>{
        specificationService.findAll().success(response =>{

        })
    }



})