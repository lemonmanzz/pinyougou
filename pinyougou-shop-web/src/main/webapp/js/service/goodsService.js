//服务层
app.service('goodsService',function($http){

    //1.保存商品添加或修改
    this.save = (url, entity)=>{
        return $http.post(url,entity);
    };
    //2.获得当前用户的的goods对象并展示
    this.getGoods = (page, pageSize)=>{
        return $http.get("../goods/getGoods.do?page="+page+"&pageSize="+pageSize);
    };
    //3.通过商品id查询组合商品对象
    this.findOne = (id) =>{
        return $http.get("../goods/findOne.do?id="+id);
    }

});
