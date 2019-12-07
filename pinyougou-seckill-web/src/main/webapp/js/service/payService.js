app.service("payService",function ($http) {
    //1.向微信后台发出下单请求
    this.createNative=()=>{
        return $http.get("./pay/createNative.do");
    };
    //2.向微信后台发出查询订单请求
    this.queryPayStatus=(out_trade_no)=>{
        return $http.get("./pay/queryPayStatus.do?out_trade_no=" + out_trade_no);
    }
});