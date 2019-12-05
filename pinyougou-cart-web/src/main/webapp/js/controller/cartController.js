app.controller("cartController",function ($scope,$controller,cartService) {
    $controller('baseController',{$scope:$scope});//继承

    //1.查询购物车列表
    $scope.findCartList = ()=>{
        cartService.findCartList().success(response=>{
            $scope.cartList = response;
            //求累加的数量及金额
            sum($scope.cartList);
        })
    };
    //2.添加到购物车
    $scope.addGoodsToCartList=(itemId,num)=>{
        cartService.addGoodsToCartList(itemId,num).success(response=>{
            if(response.success){
                $scope.findCartList();
            }else{
                alert(response.message);
            }
        })
    };
    //3.定义求和的函数
    sum=(cartList)=>{
        //3.1)定义代表总金额及数量的变量
        $scope.total = {totalNum:0,totalMoney:0};
        //3.2)遍历购物车集合
        for(let i = 0,len = cartList.length; i < len;i++){
            //3.3)得到某个购物车
            let cart = cartList[i];
            //3.4)遍历订单项列表
            for(let j = 0;j < cart.orderItemList.length;j++){
                //3.5)得到某个订单项
                let orderItem = cart.orderItemList[j];
                //3.6)求出累加数量
                $scope.total.totalNum += orderItem.num;
                //3.7)求出累加金额
                $scope.total.totalMoney += orderItem.totalFee;
            }
        }
    };
    //查询当前用户的收货地址
    $scope.findAddressList = ()=>{
        cartService.findAddressList().success(response =>{
            $scope.addressList = response;
            for (let i = 0; i < response.length; i++) {
                if (response[i].isDefault == 1) {
                    $scope.selectAddress(response[i]);
                }
            }
        })
    };
    //选择对应的地址,参数为地址对象，也就是一条地址
    $scope.selectAddress = (address)=>{
        $scope.address = address;
    };

    //判断用户是否选中
    $scope.isSelected = (address) =>{
        return address === $scope.address;
    };

    //7.选择支付方式
    $scope.selectPayType=(type)=>{
        $scope.order.paymentType = type;
    };
    //设置支付方式默认为1，即微信支付
    $scope.order={paymentType:1}
    //提交订单
    $scope.saveOrder = ()=>{
        //8.1)将住址对象中的数据放到订单中
        $scope.order.receiverAreaName = $scope.address.address;
        $scope.order.receiverMobile = $scope.address.mobile;
        $scope.order.receiver = $scope.address.contact;
        //8.2)提交订单
        cartService.saveOrder($scope.order).success(response=>{
            if(response.success){
                if($scope.order.paymentType == 1){
                    location.href = "pay.html";
                }else{
                    location.href="paysuccess.html";
                }
            }else{
                alert(response.message);
            }
        })
    }
});