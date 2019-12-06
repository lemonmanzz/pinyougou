app.service("secKillService",function ($http) {

    this.findSecKillGoods = ()=>{
        return $http.get("./secKillGoods/findSecKillGoods.do");
    };
    this.findOneSecKillGoods = (id) =>{
        return $http.get("./secKillGoods/findOneSecKillGoods.do?id="+id);
    };
});