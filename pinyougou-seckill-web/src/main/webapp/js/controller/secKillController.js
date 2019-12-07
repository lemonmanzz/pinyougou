app.controller("secKillController",function ($scope, $controller, $interval,$location,secKillService,secKillOrderService) {

    //伪继承
    $controller("baseController",{$scope:$scope});

    //查询所有的参与秒杀的秒杀商品列表
    $scope.findSecKillGoods = ()=>{
        secKillService.findSecKillGoods().success(response=>{
            $scope.secKillGoodsList = response;
        })
    };

    //跳转到secKill-item.Html页面
    $scope.goToItemHtml = (id) =>{
        location.href = "seckill-item.html#?id="+id;
    };
    //item.html查询到商品信息，一加载就运行的
    $scope.findOneSecKillGoods = ()=>{
        //通过location服务获取用户传入的id
        let id = $location.search()["id"];
        secKillService.findOneSecKillGoods(id).success(response=>{
            $scope.secKillGoods = response;
            //得到商品剩余时间，用户动态时间显示
            showTime = $interval(()=>{
                //得到该商品的结束时间
                let endTime = Date.parse($scope.secKillGoods.endTime.toString());
                //得到当前时间
                let nowTime = new Date();
                //得到剩余时间,得到秒数
                let timeRemain = (endTime - nowTime)/1000;
                //格式化数据
                let timeStr = "";
                let day = Math.floor(timeRemain / 3600 / 24);
                let hour = Math.floor((timeRemain - day * 3600 * 24) / 3600);
                let minutes = Math.floor((timeRemain - day * 3600 * 24 - hour * 3600) / 60);
                let second = Math.floor(timeRemain - day * 3600 *24 - hour * 3600 - minutes * 60);
                //组合时间字符串
                if (day > 0) {
                    timeStr += day+"天 "
                }
                timeStr += hour+":"+minutes+":"+second;
                $scope.endTime = timeStr;
            },1000);
        })
    };

    //提交订单，进行抢购,并跳转至支付页面
    $scope.submitOrder = ()=>{
        secKillOrderService.submitOrder($scope.secKillGoods.id).success(response =>{
            if (response.success){
                location.href = "/pay.html";
            }else {
                alert(response.message);
                if (response.message =="你已经在秒杀商品中....请先支付之前的订单"){
                    location.href = "/pay.html";
                }
            }
        })
    }

});