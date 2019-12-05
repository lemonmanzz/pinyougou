app.controller("loginController",function ($scope,loginService) {
   $scope.showName = ()=>{
       loginService.showName().success(response=>{
           $scope.loginName = response.username;
       })
   }
});