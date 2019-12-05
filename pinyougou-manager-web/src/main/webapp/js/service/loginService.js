app.service("loginService",function ($http) {
   //1.获得登录人的用户名
   this.getLoginName = ()=>{
       return $http.get("/login/name.do");
   }

});