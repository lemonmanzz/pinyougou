app.controller("contentController",function ($scope, $controller, contentService) {
    //1.实现伪继承，共享scope作用域
    $controller("baseController",{$scope:$scope});
    //初始化
    $scope.contents = {};
    //2.通过分类id查询对应分类的集合
    $scope.findContentByCategoryId = (categoryId)=>{
        //2.1查询对应广告分类id下的所有广告
        contentService.findContentByCategoryId(categoryId).success(response =>{
            //2.2contents结构contents = {"categoryId":[] ......}
            $scope.contents[categoryId] = response;
        });
    };

    //3.根据搜索的关键字，从主页跳转至搜索界面
    $scope.gotoSearch = ()=>{
        location.href = "http://localhost:9104/search.html#?keywords="+$scope.keywords;
    }
});