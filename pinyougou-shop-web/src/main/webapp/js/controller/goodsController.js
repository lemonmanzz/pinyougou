 //控制层 
app.controller('goodsController' ,function($scope,$location,$controller,typeTemplateService,goodsService,itemCatService,uploadService){
	
	$controller('baseController',{$scope:$scope});//继承
	//1.定义组合商品对象

    //2.根据分类id查询子分类列表
	$scope.init = ()=>{
	    //初始化组合类
        $scope.entity={goods:{typeTemplateId:[]},goodsDesc:{itemImages:[],specificationItems:[]},items:[]};
        $scope.entity.goods.id = $location.search()["goodsId"];
        if (isUpdate()){
            $scope.findOne(isUpdate());
        }
		//查询一级目录的分类列表
		itemCatService.findByParentId(0).success(response =>{
           $scope.category1List = response;
		})
	};

    //3.使用$watch服务监控一级分类的id，查询出二级分类列表
    //参数1：代表要监控的id
    //参数2：代表回调函数（参数1：代表正在选择的一级分类的id，参数2：选择之前的一级分类的id）
	$scope.$watch("entity.goods.category1Id",(newValue)=>{
	    //当新值不为null时执行，避免第一次查询undefined
	    if (newValue)
        itemCatService.findByParentId(newValue).success(response =>{
            $scope.category2List = response;
        });

	});
    //4.使用$watch服务监控二级分类的id，查询出三级分类列表
    //参数1：代表要监控的id
    //参数2：代表回调函数（参数1：代表正在选择的一级分类的id，参数2：选择之前的一级分类的id）
    $scope.$watch("entity.goods.category2Id",(newValue)=>{
        if (newValue)
        itemCatService.findByParentId(newValue).success(response =>{
            $scope.category3List = response;
        })
    });

    //5.根据下拉三级分类变换得到，模板id
    $scope.$watch("entity.goods.category3Id",(newValue)=>{
        if (newValue)
        itemCatService.findOne(newValue).success(response=>{
            $scope.entity.goods.typeTemplateId = response.typeId;
        })
    });

    //6.监控模板id的变化查找对应模板，从而显示对应的品牌下拉框
    $scope.$watch("entity.goods.typeTemplateId",(newValue)=>{
        //判断新值是否为空，防止第一次访问空值
        if (newValue != ""){
            typeTemplateService.findOne(newValue).success(response=>{
                //6.1得到模板品牌
                $scope.brandIds = JSON.parse(response.brandIds);
                //6.2得到扩展属性
                //此处判断是否是修改，修改则不进行覆盖
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse(isUpdate() ? $scope.entity.goodsDesc.customAttributeItems : response.customAttributeItems)

            });
            //第三件式事根据模板id查询规格列表，（规格选项也添加到列表中）
            typeTemplateService.findSpecList(newValue).success(response =>{
               $scope.specList = response;
            });
        }
    });


    //7.保存商品信息
    $scope.save = ()=>{
        //7.1保存之前获取富文本框的信息
        $scope.entity.goodsDesc.introduction = editor.html();
        let url = "../goods/add.do";
        if (!!isUpdate()) url = "../goods/update.do";
        goodsService.save(url,$scope.entity).success(response =>{
            if (response.success) {
                //7.2清空富文本框
                editor.html("");
                //7.3清空entity对象
                $scope.entity={goods:{},goodsDesc:{itemImages:[]},items:[]}
            }else {
                alert(response.message);
            }
        })
    };

    //8.初始化图片对象
    $scope.imgEntity = {};
    //文件上传方法，用于上传图片
    $scope.upload = ()=>{
        uploadService.upload().success(response=>{
            if (response.success) {
                $scope.imgEntity.url = response.message;
            }else {
                alert(response.message);
            }
        })
    };
    //保存图片
    $scope.savePicture = ()=>{
        $scope.entity.goodsDesc.itemImages.push($scope.imgEntity);
    };

    //9. 根据规格id得到规格选项
    $scope.getOptions = (id) =>{
        specificationOptionService.findBySpecId(id).success(response=>{
            return response;
        })
    };

    /**
     * 第三部分规格列表与规格选项进行动态显示
     */
    //1.1此处是为私有方法，定义一个通过key和value的值来查找specificationItems中是否存某个对象
    //参数1 表示查询的数组对象，此处就是specificationItems
    //参数2 表示查询时使用的对象字段，也就是根据这个字段来查询的意思 此处是attributeName
    //参数3 表示查询对象字段对应的值。 此处是所选复选框对应的规格名称
    getSpecificationItemsObjectByKeyValue =  (list,key,value)=>{
        if (list){
            //1.1.1 遍历数组
            for (let i = 0,len = list.length; i < len; i++) {
                if (list[i][key] == value ){
                    return list[i];
                }
            }
        }
        return null;
    };
    //2.点击复选框，将对于规格选项和规格名放入该数组中。
    //参数1 表示点击事件
    //参数2 表示点击复选框对应的规格名称，也就是specificationItems中所需要的attributeName字段值
    //参数3 表示点击复选框的的规格选项名称，也就是specificationItems中所需要的attributeValue字段值，attributeValue应该是一个数组。
    $scope.updateSpecificationItems = (event,attributeName,attributeValue)=>{
        //2.1 判断specificationItems中是否已经拥有该规格对象
        //2.1.2 通过传入的规格名称进行查找
        let obj = getSpecificationItemsObjectByKeyValue($scope.entity.goodsDesc.specificationItems,"attributeName",attributeName);
        //2.1.3 判断是否存在，也就是obj是否为空
        if (obj) {
            if (event.target.checked) {//3.1判断点击事件是选中还是没被选中，选中则执行从atrributeValue添加
                obj.attributeValue.push(attributeValue);
            }else {//3.2没被选中则执行从attributeValue数组中删除
                let index = obj.attributeValue.indexOf(attributeValue);
                obj.attributeValue.splice(index,1);
                //3.2.2 删除之后 判断attributeName数组的长度是否为0，为0贼删除整个obj对象，表示该规格没有选中任何规格选项
                //前端代码 null 0 NaN '' false undefined 均可以直接使用if ，器都代表fasle，
                // 因此以下!obj.attributeValue.length表示数组长度为0则删除obj对象。
                if (!obj.attributeValue.length) {
                    $scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(obj),1);
                }
            }
        }else {//obj为空则执行第一次添加
            $scope.entity.goodsDesc.specificationItems.push({"attributeName":attributeName,"attributeValue":[attributeValue]});
        }

        //4.动态生成sku商品列表
        createItems();

    };

    /**
     * 动态生成sku列表
     */
    //1.创建sku列表
    createItems = () =>{
        //1.1 初始化items的值
        $scope.entity.items = [{spec:{},price:0,num:0,status:'0',isDefault:'0' }];
        //1.2 获得选中的规格选项集合，也就是$scope.entity.goodsDesc.specificationItems数组对象值
        let specificationItems = $scope.entity.goodsDesc.specificationItems;
        //1.3 循环遍历将规格选项动态添加到items中
        for (let i = 0,len = specificationItems.length; i < len; i++) {
            $scope.entity.items = addColumn($scope.entity.items,specificationItems[i].attributeName,specificationItems[i].attributeValue);
        }
        if (!specificationItems.length) {
            $scope.entity.items = [];
        }
    };
    //2.将规格添加到商品列表中选项
    //参数1 $scope.entity.items的值，表示需要最终需要生成的商品列表
    //参数2 表示规格名称
    //参数3 表示当前规格名称下，所选择的规格选项
    addColumn = (list,specName,specOptions)=>{
        //2.1 创建一个新的数组，该数组是更新后的$scope.entity.items
        let newList = [];
        //2.2 遍历传入的数组，更新数组中的商品列表
        for (let i = 0, len = list.length ; i < len; i++) {
            //2.3 遍历当前规格的所有选项，也就是specOption，并将其逐个赋值给newRow
            for (let j = 0, length = specOptions.length ; j < length; j++) {
                //2.4 复制一份老值到给newRow
                let newRow = JSON.parse(JSON.stringify(list[i]));
                //2.5 给newRow赋值新的规格及选项
                newRow.spec[specName] = specOptions[j];
                //2.6 将新的行放入newList中，newRow其实就是一个新的商品列
                newList.push(newRow);
            }
        }

        //全部更新完之后返回新的商品列表
        return newList;
    };

    /**
     * 修改商品部分
     *
     *
     */
    //0.1 状态数组
    $scope.status = ["未审核","已审核","审核通过","已关闭"];

    //1.获得当前用户的的goods对象并展示
    //用户名从security框架里拿,进行分页显示
    $scope.getGoods = () => {
        goodsService.getGoods($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage).success(response=>{
            $scope.goodsList = response.rows;
            //1.2更新总记录数
            $scope.paginationConf.totalItems = response.total;
        })
    };
    //2.通过商品id传入对应的修改页面
    $scope.goToUpdate = (id)=>{
        location.href = "goods_edit.html#?goodsId="+id;
    };
    //3.通过商品id查询商品组合对象
    $scope.findOne = id => {
      goodsService.findOne(id).success(response=>{
          $scope.entity = response;
          editor.html($scope.entity.goodsDesc.introduction);
          $scope.entity.goodsDesc.itemImages = $scope.entity.goodsDesc.itemImages ? JSON.parse($scope.entity.goodsDesc.itemImages) : [];
          $scope.entity.goodsDesc.specificationItems = $scope.entity.goodsDesc.specificationItems ? JSON.parse($scope.entity.goodsDesc.specificationItems) : [];
          let items = $scope.entity.items;
          for (let i = 0, len = items.length; i < len; i++) {
              items[i]["spec"] = JSON.parse(items[i]["spec"]);
          }

      })
    };
    //4.将规格选项的每一项都与specificationItems进行对比在则返回true不在返回false
    $scope.isSelected = (attributeName,text)=>{
        let arr = getSpecificationItemsObjectByKeyValue($scope.entity.goodsDesc.specificationItems,"attributeName",attributeName);
        return arr ? arr.attributeValue.indexOf(text)>=0 : false;
    };
    /**
     * 加载编辑页面的方法
     * 通过是否能获得id值进行判断
     * 有id则修改，否则才进行查询
     */
    isUpdate = ()=>{
        return !!$scope.entity.goods.id?$scope.entity.goods.id:false;
    }

});	
