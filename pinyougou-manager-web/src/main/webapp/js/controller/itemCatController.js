app.controller("itemCatController",function ($scope,$controller,itemCatService,typeTemplateService) {
    //1.伪继承，继承基本controller
   $controller("baseController",{$scope:$scope});
   //1.1 设置当前级数,初始为第1级分类
    $scope.grade = 1;
    $scope.saveEntity={};
   //2.通过parentId查找对应子目录
   $scope.findByParentId = parentId => {
       itemCatService.findByParentId(parentId).success(response => {
           $scope.items = response;
       })
   };
   $scope.selectList = entity =>{
       //grade+1
       $scope.grade = $scope.grade+1;
       if ($scope.grade == 1){
           $scope.entity1 = {id:0};
           entity = $scope.entity1;
           $scope.entity2 = null;
           $scope.entity3 = null;
       } else if ($scope.grade == 2){
           $scope.entity2 = entity;
           $scope.entity3 = null;

       } else if ($scope.grade == 3) {
           $scope.entity3 = entity;
       }

       //通过parentId进行查询
       $scope.findByParentId(entity.id)
   };
   //更改grade值得方法
    $scope.updateGrade = (grade) =>{
       $scope.grade = grade;
    };
    
    //保存方法
    $scope.save = () =>{
        $scope.saveEntity.parentId = $scope.nowEntity().id;
       let url = "/itemCat/add.do";
       if ($scope.saveEntity.id) {
           url = "/itemCat/update.do";
       }
       itemCatService.save(url,$scope.saveEntity).success(response => {
           if (response.success) {
               //此处需获取后再进行减
               //获得当前的父entity
               $scope.paEntity = $scope.nowEntity();
               $scope.grade = $scope.grade-1;
                //进行刷新操作
               $scope.selectList($scope.paEntity);
           }else alert(response.message)
       })
    };
    //获取当前在层的entity
    $scope.nowEntity = ()=>{
       switch ($scope.grade) {
           case 1:return $scope.entity1;
           case 2:return $scope.entity2;
           case 3:return $scope.entity3;
       }
    };
    //获得上级商品分类
    $scope.getPreItems = ()=>{
        switch ($scope.grade) {
            case 2:return $scope.entity2.name;
            case 3:return $scope.entity2.name+">>"+$scope.entity3.name;
        }
    };

    //查询所有的模板
    $scope.findAllTemplate = ()=>{
        typeTemplateService.findAll().success(response =>{
            $scope.templates = response;
        })
    }

    //查询某一个
    $scope.findOneById = (id)=>{
        itemCatService.findOneById(id).success(response =>{
            $scope.saveEntity = response;
        })
    }

    //删除方法（当删除的是上级目录时，删除上级目分类时会连带下级分类一起删除了）
    $scope.dele = () =>{
        itemCatService.dele($scope.selectedIds).success(response=>{
            alert($scope.selectedIds);
            if (response.success) {
                $scope.selectedIds = [];
                //刷新
                //此处需获取后再进行减
                //获得当前的父entity
                $scope.paEntity = $scope.nowEntity();
                $scope.grade = $scope.grade-1;
                //进行刷新操作
                $scope.selectList($scope.paEntity);
            }else alert(response.message)


        })
    }



});