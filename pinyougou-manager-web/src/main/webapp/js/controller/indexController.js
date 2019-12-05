app.controller("indexController",function ($scope, $controller, loginService) {

    //1.完成伪继承
    // $controller("baseController",{$scope:$scope})
    //2.获取当前登录用户
    $scope.showLoginName = ()=>{
        loginService.getLoginName().success(response =>{
            $scope.loginName = response.loginName;
            $scope.loginTime = response.loginTime;
        })
    }

});