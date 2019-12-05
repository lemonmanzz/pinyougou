app.service("contentService",function ($http) {

    //查询所有的广告信息
    this.findAll = ()=>{
       return $http.get("../content/findAll.do");
    };

    //保存广告信息
    this.save = (url,entity)=>{
        return $http.post(url,entity);
    };
    //查询一个广告实例
    this.findOne = (id)=>{
        return $http.get("../content/findOne.do?id="+id);
    };
    //删除广告
    this.delete = (ids)=>{
        return $http.get("../content/delete.do?ids="+ids);
    };
});