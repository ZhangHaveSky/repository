//控制层
app.controller('goodsController', function ($scope, $controller, $location, goodsService, updateService, itemCatService, typeTemplateService) {

    $controller('baseController', {$scope: $scope});//继承

    //读取列表数据绑定到表单中  
    $scope.findAll = function () {
        goodsService.findAll().success(
            function (response) {
                $scope.list = response;
            }
        );
    };

    //分页
    $scope.findPage = function (page, rows) {
        goodsService.findPage(page, rows).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };

    //查询实体
    $scope.findOne = function () {
        var id = $location.search()["id"];
        goodsService.findOne(id).success(
            function (response) {
                $scope.entity = response;
                editor.html(response.goodsDesc.introduction);
                $scope.entity.goodsDesc.itemImages = JSON.parse(response.goodsDesc.itemImages);
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse(response.goodsDesc.customAttributeItems);
                $scope.entity.goodsDesc.specificationItems = JSON.parse(response.goodsDesc.specificationItems);
                for (var i = 0; i < response.itemList.length; i++) {
                    $scope.entity.itemList[i].spec = JSON.parse(response.itemList[i].spec);
                }
            }
        );
    };

    //保存
    $scope.save = function () {
        $scope.entity.goodsDesc.introduction = editor.html();
        var serviceObject;//服务层对象
        if ($scope.entity.goods.id != null) {//如果有ID
            serviceObject = goodsService.update($scope.entity); //修改
        } else {
            serviceObject = goodsService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    alert("保存成功");
                    location.href = 'goods.html';
                } else {
                    alert(response.message);
                }
            }
        );
    };


    //批量删除
    $scope.dele = function () {
        //获取选中的复选框
        goodsService.dele($scope.selectIds).success(
            function (response) {
                if (response.success) {
                    $scope.reloadList();//刷新列表
                    $scope.selectIds = [];
                }
            }
        );
    };
    //定义搜索对象
    $scope.searchEntity = {};

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    };
    // 向FastDFS服务器上传照片
    $scope.update = function () {
        updateService.update().success(function (response) {
            if (response.success) {
                $scope.image_entity.url = response.message;
            } else {
                alert(response.message)
            }

        })
    };
    //初始化对象
    $scope.entity = {goods: {}, goodsDesc: {itemImages: [], specificationItems: []}};
    $scope.add_image_entity = function () {
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    };
    //从列表删除图片
    $scope.delet_image_entity = function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index, 1);
    };

    $scope.itemCat1List = {};
    //加载一级联动下拉列表
    $scope.selectItemCat1List = function () {
        itemCatService.findByparentId(0).success(function (response) {
            $scope.itemCat1List = response;
        });
    };

    $scope.statuslist = ["未审核", "已审核", "审核未通过", "关闭"];
    //商品分类列表
    $scope.itemcastAll = [];
    //加载商品分类列表
    $scope.itemcats = function () {
        itemCatService.findAll().success(function (response) {
            for (var i = 0; i < response.length; i++) {
                $scope.itemcastAll[response[i].id] = response[i].name;
            }

        })
    };
    //加载二级联动
    $scope.$watch('entity.goods.category1Id', function (newValue, oldValue) {
        $scope.itemCat3List = [];
        $scope.entity.goods.typeTemplateId = null;
        itemCatService.findByparentId(newValue).success(function (response) {
            $scope.itemCat2List = response;

        });

    });
    //加载三级联动
    $scope.$watch('entity.goods.category2Id', function (newValue, oldValue) {
        itemCatService.findByparentId(newValue).success(function (response) {
            $scope.itemCat3List = response;
        })
    });
    //加载模板ID
    $scope.$watch('entity.goods.category3Id', function (newValue, oldValue) {
        itemCatService.findOne(newValue).success(function (response) {
            $scope.entity.goods.typeTemplateId = response.typeId;
        })
    });
    //加载扩展属性,规格属性
    $scope.$watch('entity.goods.typeTemplateId', function (newValue, oldValue) {

        typeTemplateService.findOne(newValue).success(function (response) {
            $scope.brands = JSON.parse(response.brandIds);
            //冲突解决:监控模板ID变化,如果是新增,扩展属性来自于模板表,如果是修改,来自于商品
            if ($location.search()['id'] == null) {
                $scope.entity.goodsDesc.customAttributeItems = JSON.parse(response.customAttributeItems);
            }
        });
        //加载规格属性
        typeTemplateService.findSpecList(newValue).success(function (response) {
            $scope.specList = response;
        })
    });
    //根据规格复选框状态添加对象
    $scope.updateSpecAttribute = function ($event, name, value) {
        var obj = $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems, 'attributeName', name);
        if (obj != null) {
            if ($event.target.checked) {
                obj.attributeValue.push(value)
            } else {
                obj.attributeValue.splice(obj.attributeValue.indexOf(value), 1);
                if (obj.attributeValue.length < 1) {
                    $scope.entity.goodsDesc.specificationItems.splice($scope.entity.goodsDesc.specificationItems.indexOf(obj), 1);
                }
            }
        } else {
            $scope.entity.goodsDesc.specificationItems.push({attributeName: name, attributeValue: [value]})
        }
    };

    $scope.searchObjectByKey = function (list, name, value) {
        for (var i = 0; i < list.length; i++) {
            if (list[i][name] === value) {
                return list[i];
            }
        }
        return null;
    };
    //更新规格显示条
    $scope.createItemList = function () {
        $scope.entity.itemList = [{spec: {}, price: 0, num: 0, status: '0', isDefault: '0'}];
        var itlist = $scope.entity.itemList;
        var list = $scope.entity.goodsDesc.specificationItems;
        for (var i = 0; i < list.length; i++) {
            var newlist = [];
            for (var j = 0; j < itlist.length; j++) {
                var olds = itlist[j];
                for (var s = 0; s < list[i].attributeValue.length; s++) {
                    var news = JSON.parse(JSON.stringify(olds));
                    news.spec[list[i].attributeName] = list[i].attributeValue[s];
                    newlist.push(news);
                }
            }
            itlist = newlist;
        }
        $scope.entity.itemList = itlist;
    };
    //更新规格复选框
    $scope.checkAttributeValue = function (text, name) {
        var list = $scope.entity.goodsDesc.specificationItems;
        var obj = $scope.searchObjectByKey(list, 'attributeName', text);
        if (obj == null) {
            return false;
        } else {
            return obj.attributeValue.indexOf(name) >= 0;
        }
    };
    //改变是否上架
    $scope.changmarketable = function (id, isMarketable) {
        goodsService.changemarke(id, isMarketable).success(function (response) {
            if (response.success) {
                $scope.reloadList();
            } else {
                alert(response.message)
            }
        })
    }
});	
