app.controller("searchController",function ($scope,$location,$controller,searchService) {
    $controller('baseController', {$scope: $scope});//继承
    //0.定义向后台提交的查询对象
    $scope.searchMap = {
        "keyWords": '',
        "category": '',
        "brand": '',
        "spec": {},
        "price": '',
        "sort": '',
        "sortField": '',
        "pageIndex": 1,
        "pageSize": 20
    };
    //1.查询商品
    $scope.search = () => {
        searchService.search($scope.searchMap).success(response => {
            $scope.resultMap = response;
            //绘制分页导航
            paging();

            if ($scope.flag === false){
                $scope.flag = true;
                $scope.hideBrand($scope.searchMap.keyWords);
            }
        })
    };

    //2.添加搜索功能
    $scope.addSearchItem = (key, value) => {

        if (key === 'category' || key === 'brand' || key === 'price') {

            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }
        //进行查询,包括制定当前页是第一页
        $scope.searchMap.pageIndex = 1;
        $scope.search();
    };
    //3.移除搜索选项
    $scope.removeSearchItem = (key) => {
        if (key === 'category' || key === 'brand' || key === 'price') {

            //3.1清空查询属性值
            $scope.searchMap[key] = "";
        } else {
            //3.2删除spec的属性
            delete $scope.searchMap.spec[key];
        }
        //进行查询,包括制定当前页是第一页
        $scope.searchMap.pageIndex = 1;
        $scope.search();
    };

    //4.设置排序查询方法
    $scope.sortSearch = (sort, sortField) => {
        $scope.searchMap.sort = sort;
        $scope.searchMap.sortField = sortField;
        $scope.search();
    };

    //5.定义分页导航
    paging = () => {
        //5.1定义分页的数组
        $scope.pages = [];
        //5.2定义首页和尾页的对象
        $scope.firstPage = 1;
        $scope.lastPage = $scope.resultMap.totalPages;
        //5.3定义左右边的...省略号
        $scope.firstDot = false;
        $scope.lastDot = false;
        //5.4确定首页和尾页
        if ($scope.lastPage > 5) {
            if ($scope.searchMap.pageIndex <= 3) {
                $scope.lastPage = 5;
                $scope.lastDot = true;
            } else if ($scope.searchMap.pageIndex >= $scope.lastPage - 3) {
                $scope.firstPage = $scope.lastPage - 4;
                $scope.firstDot = true;
            } else {
                $scope.firstPage = $scope.searchMap.pageIndex - 2;
                $scope.lastPage = $scope.searchMap.pageIndex + 2;
                $scope.firstDot = true;
                $scope.lastDot = true;
            }
        }
        for (let i = $scope.firstPage; i < $scope.lastPage + 1; i++) {
            $scope.pages.push(i);
        }

    };

    //6.跳转到某一页
    $scope.goToPage = (pageIndex) => {
        //61.获得输入的页数
        pageIndex = parseInt(pageIndex);
        //6.2判断该页数是否在页数范围内
        if (pageIndex > 0 && pageIndex <= $scope.resultMap.totalPages) {
            //6.3在则将当前页设置为输入值
            $scope.searchMap.pageIndex = pageIndex;
        } else if (pageIndex > $scope.resultMap.totalPages) {
            //6.4大于则将当前页设置为最大值
            $scope.searchMap.pageIndex = $scope.resultMap.totalPages;
        } else {
            //6.5其他情况就设置为第一页
            $scope.searchMap.pageIndex = 1;
        }
        $scope.search();
    };


    //7.判断输入的关键字是否是品牌，是则隐藏品牌
    $scope.hideBrand = (keyWords) => {
        brandList = $scope.resultMap.brandList;
        for (let i = 0, len = brandList.length; i < len; i++) {
            if (keyWords.indexOf(brandList[i]) >= 0) {
                $scope.searchMap.brand = brandList[i];
            }
        }
    };

    //8.获取主页传入的sou搜索关键字并进行查询,页面一加载执行的方法;
    $scope.init = ()=>{
        $scope.flag = false;
        let keywords = $location.search()["keywords"];
        if (keywords != null && keywords !== "" ){
            $scope.searchMap.keyWords = keywords;
            //8.2此处需要注意，必须先查询，在进行判断
        }else {
            $scope.searchMap.keyWords = "";
        }
        $scope.search();
    };
});