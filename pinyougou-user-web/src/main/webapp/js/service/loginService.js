app.service("loginService",function ($http) {
    this.showName = ()=>{
        return $http.get("/login/name.do");
    };
});