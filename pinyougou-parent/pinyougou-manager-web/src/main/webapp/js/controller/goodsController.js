//控制层
app.controller('goodsController', function ($scope, $controller, $location, goodsService, itemCatService,brandservice) {

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

                brandservice.get($scope.entity.goods.brandId).success(function (brand) {
                    $scope.entity.goods.brandId=brand.name;
                });


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

    $scope.searchEntity = {};//定义搜索对象

    //搜索
    $scope.search = function (page, rows) {
        goodsService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
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

    $scope.changstatus=function (status) {
        goodsService.updateStatus($scope.selectIds,status).success(function (response) {
            if(response.success){
                $scope.reloadList();
                $scope.selectIds=[];
            }else{
                alert(response.message);
            }
        })

    }
});
