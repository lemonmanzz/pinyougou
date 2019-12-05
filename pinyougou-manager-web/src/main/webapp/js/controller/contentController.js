app.controller("contentController",function ($scope, $controller, uploadService, contentService,contentCategoryService) {
   //进行伪继承，实现共享$scope作用域
    $controller("baseController",{$scope:$scope});
    //初始化广告分类
    $scope.contentCategories = [];
    //1.查询所有的广告
    $scope.findAll = ()=>{
        contentService.findAll().success(response =>{
            $scope.contentList = response;
        });
        //1.2同时查询所有广告分类
        contentCategoryService.findAll().success(response =>{
                $scope.contentCategories = response;
        })
    };
    //2.将分类id转化为分类名称,参数为分类id
    $scope.getCategoryName = (id)=>{
        for (let i = 0; i < $scope.contentCategories.length; i++) {
            if ($scope.contentCategories[i].id == id){
                return $scope.contentCategories[i].name;
            }
        }
    };

    //3.上传图片
    $scope.upload = ()=>{
        uploadService.upload().success(response =>{
            if (response.success) {
                $scope.entity.pic = response.message;
            }else {
                alert(response.message)
            }
        })
    };

    //4.保存保存广告
    $scope.save = ()=>{
        let url = "../content/add.do";
        if ($scope.entity.id) url = "../content/update.do";
        contentService.save(url,$scope.entity).success(response =>{
            if (response.success) {
                //4.1 保存成功刷新
                $scope.findAll();
                // //4.2 关闭模态框
                // $("#editModal").modal("hide");
            }else {
                alert(response.message);
            }
        })
    };

    //5.查询一个广告信息实例
    $scope.findOne = (id)=>{
        contentService.findOne(id).success(response =>{
            $scope.entity = response;
        })
    };

    //6.删除选中的广告
    $scope.delete = ()=>{
        contentService.delete($scope.selectedIds).success(response =>{
            if (response.success) {
                $scope.findAll();
                $scope.selectedIds = [];
            }else {
                alert(response.message);
            }
        })
    };

});