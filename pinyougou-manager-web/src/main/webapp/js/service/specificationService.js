app.service("specificationService",function ($http) {
    //1.带分页的条件查询
    this.search = (page,pageSize,specName)=>{
       return $http.get("/specification/search.do?page="+page+"&pageSize="+pageSize+"&specName="+specName);
    };
    //2. 保存方法
    this.save = (url,entity) =>{
        return $http.post(url,entity);
    };

    //3.修改规格，查询原来的规格信息
    this.findOne = id =>{
        return $http.get("/specification/findOne.do?id="+id);
    };

    //4.删除方法
    this.dele = ids =>{
        return $http.get("/specification/delete.do?ids="+ids);
    };
    //5.查询所有
    this.findAll = ()=>{
        return $http.get("/specification/findAll.do");
    }
});