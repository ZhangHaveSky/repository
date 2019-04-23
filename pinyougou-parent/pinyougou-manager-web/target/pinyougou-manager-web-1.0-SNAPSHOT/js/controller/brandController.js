app.controller("brandcotroller", function ($scope,$controller,brandservice) {

    //伪继承
    $controller("baseController",{$scope:$scope});


    //查询实体
    $scope.update = function (id) {
        brandservice.get(id).success(function (response) {
            $scope.tbBrand = response;
        })
    };
    //保存
    $scope.save=function () {
        brandservice.save($scope.tbBrand).success(function (response) {
            if (!response.success){
                alert(response.message);
            }else {
                $scope.searchList();
            }
        })
    };

    $scope.delet=function () {
        if(confirm("确定要删除?")){
            brandservice.delet($scope.selectIds).success(function (response) {
                if(response.success){
                    $scope.searchList();//刷新
                }else{
                    alert(response.message);
                }
            })
        }

    };
    //初始化searchentity为没有属性值的对象,以防post请求时为null报错
    $scope.searchentity={};
    //条件查询
    $scope.search=function (page,size) {
        brandservice.search(page,size,$scope.searchentity).success(function (response) {
            $scope.list = response.rows;//显示当前页数据
            $scope.paginationConf.totalItems = response.total;//更新总记录数
        })
    }
});