app.service("itemCatService",function ($http) {
    //通过parentid查询分类
    this.findByParentId = parentId =>{
        return $http.get("/itemCat/findByParentId.do?parentId="+parentId);
    };
    // 保存
    this.save = (url,entity)=>{
        return $http.post(url,entity);
    };
    //查询某一个
    this.findOneById = (id) =>{
        return $http.get("/itemCat/findOneById.do?id="+id);
    };
    //删除
    this.dele = (ids) =>{
        return $http.get("/itemCat/delete.do?ids="+ids);
    };

    this.findAll= ()=>{
        return $http.get("/itemCat/findAll.do");
    }
});