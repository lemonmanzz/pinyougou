app.service("contentCategoryService",function ($http) {

    this.findAll = ()=>{
        return $http.get("/contentCategory/findAll.do");
    }
});