app.service("contentService",function ($http) {
    //1.通过分类id查询对应的广告集合
    this.findContentByCategoryId = (categoryId)=>{
        return $http.get("../content/findContentByCategoryId.do?categoryId="+categoryId);
    }
});