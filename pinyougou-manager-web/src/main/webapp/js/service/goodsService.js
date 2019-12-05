app.service("goodsService",function ($http) {
    this.findAll = (pageIndex,pageSize)=>{
        return  $http.get("../goods/findAll.do?pageIndex="+pageIndex+"&pageSize="+pageSize);
    };
    //更新状态的方法
    this.updateStatus = (selectedIds,stastus,statusField)=>{
        return $http.get("../goods/updateStatus.do?ids="+selectedIds+"&status="+stastus+"&statusField="+statusField);
    }
});