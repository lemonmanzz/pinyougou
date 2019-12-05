app.service("brandService",function ($http) {
    //1.查询所有的信息
    this.findAll = () =>{
        return $http.get("/brand/list.do");
    };
    //2.分页查询+条件查询
    this.search = (page,pageSize,searchEntity) =>{
      return  $http.post("/brand/search.do?page="+page+"&pageSize="+pageSize,searchEntity);
    };
    //3.添加或者修改
    this.addOrUpdate = (url,entity) =>{
        return $http.post(url,entity);
    };
    //4.删除品牌信息
    this.dele = (selectedIds) => {
        return $http.get("/brand/delete.do?selectedIds="+selectedIds);
    }
});