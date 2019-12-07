app.service("secKillOrderService",function ($http) {
    this.submitOrder = (secKillGoodsId)=>{
        return $http.get("./secKillOrder/submitOrder.do?secKillGoodsId="+ secKillGoodsId);
    }
});