app.controller("typeTemplateController",function ($scope,$controller,typeTemplateService,brandService,specificationService) {
    //1.实现伪继承
    $controller("baseController",{$scope:$scope});

    //2.查询所有模板
    $scope.findAll = ()=>{
        typeTemplateService.findAll().success(response =>{
            $scope.typeTemplates = response;
        })
    };

    //3.将json字符串转换为json对象
    $scope.jsonToString = (jsonString,field)=>{
        //1.1将json字符串转换成json对象
        let arr = JSON.parse(jsonString);
        //2.遍历json对象
        let result = "";
        for (let i = 0; i < arr.length; i++) {
            //3.得到某个对象某个属性值
            result += arr[i][field]+"、";
        }
        //4.去除最后的顿号
        result = result.substring(0,result.length-1);
        return result;
    };

    //初始化对象
    $scope.entity = {customAttributeItems:[]};
    //4.新增扩展属性
    $scope.addAttr = ()=>{
        $scope.entity.customAttributeItems.push({})
    };
    //5.删除扩展属性
    $scope.removeAttr = (index)=>{
        $scope.entity.customAttributeItems.splice(index);
    };

    //6.查询品牌列表
    $scope.findBrandList = ()=>{
        brandService.findAll().success(response =>{
            $scope.brandList = {data:response};
        })
    };

    //7.查询规格列表
    $scope.findSpecificationList = ()=>{
        specificationService.findAll().success(response =>{
            $scope.specificationList = {data:response};
        })
    };

    //8.保存方法
    $scope.save = ()=>{
        let url = "/typeTemplate/add.do";
        if ($scope.entity.id) url = "/typeTemplate/update.do";
        specificationService.save(url,$scope.entity).success(response =>{
            if (response.success) {
                $scope.findAll();
            }else {
                alert(response.message)
            }
        })
    };

    //9.查询一个模板
    $scope.findOne = (id)=>{
        typeTemplateService.findOne(id).success(response =>{
            $scope.entity = response;
            $scope.entity.customAttributeItems = JSON.parse(response.customAttributeItems);
            $scope.entity.specIds = JSON.parse($scope.entity.specIds);
            $scope.entity.brandIds = JSON.parse($scope.entity.brandIds);
        })
    }

    //10.删除模板
    $scope.dele = ()=>{
        typeTemplateService.dele($scope.selectedIds).success(response=>{
            if (response.success) {
                $scope.selectedIds = [];
                $scope.findAll();
            }
            else alert(response.message);
        })
    }

});