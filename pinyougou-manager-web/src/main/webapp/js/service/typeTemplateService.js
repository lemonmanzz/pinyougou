app.service("typeTemplateService",function ($http) {

    //1.查询所有的模板
    this.findAll = ()=>{
        return $http.get("/typeTemplate/findAll.do");
    };
    //2.保存添加或修改数据
    this.save = (url,entity)=>{
        return $http.post(url,entity);
    };

    //3.查找一个模板
    this.findOne = id =>{
      return $http.get("/typeTemplate/findOne.do?id="+id);
    };

    //4.删除模板
    this.dele = (ids)=>{
        return $http.get("/typeTemplate/delete.do?ids="+ids);
    }


});