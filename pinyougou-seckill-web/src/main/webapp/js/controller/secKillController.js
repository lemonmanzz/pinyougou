app.controller("secKillController",function ($scope, $controller, $location,secKillService) {

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
        })
    }
});