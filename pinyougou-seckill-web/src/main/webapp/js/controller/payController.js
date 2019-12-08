app.controller("payController", function ($scope, $controller,$location, payService) {
    $controller('baseController', {$scope: $scope});//继承
    //1.向微信后台发出下单请求
    $scope.createNative = () => {
        payService.createNative().success(response => {
            //1.1)就根据微信后台返回的code_url这个地址生成二维码
            //1.1.1)得到金额及订单号
            $scope.money = (response.total_fee/100).toFixed(2);
            $scope.out_trade_no = response.out_trade_no;
            //执行查询支付状态的代码
            queryPayStatus();
            //1.1.2)生成二维码
            new QRious({
                element: document.getElementById("img1"),
                size: 200,
                level: 'H',
                value: response.code_url
            });

        })
    };
    //2.向微信后台发出查询订单的请求
    queryPayStatus = ()=>{
        payService.queryPayStatus($scope.out_trade_no).success(response=>{
            if(response.success){       //如果查询成功,跳转到支付成功页面
                location.href = "paysuccess.html#?money="+$scope.money;
            }else{
                if(response.message == "二维码超时"){   //代表需要重新下单
                    location.href = "payTimeOut.html";
                }else{
                    location.href = "payfail.html";
                }
            }
        })
    };
    //3.定义取得从pay.html页面到paysuccess.html这个页面传过来的参数
    $scope.getMoney=()=>{
        return $location.search()["money"];
    }
});