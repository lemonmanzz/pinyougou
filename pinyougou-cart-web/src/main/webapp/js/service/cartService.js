app.service("cartService",function ($http) {
    //1.查询购物车列表
    this.findCartList = ()=>{
        return $http.get("./cart/getCartList.do");
    };
    //2.添加到购物车
    this.addGoodsToCartList=(itemId,num)=>{
        return $http.get("./cart/addToCartList.do?itemId="+itemId+"&num="+num);
    };
    //3.查询用户的收货地址列表
    this.findAddressList = ()=>{
        return $http.get("./cart/findAddressList.do");
    };

    //4.提交订单
    this.saveOrder=(order)=>{
        return $http.post("./order/add.do",order);
    }
});