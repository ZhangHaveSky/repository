app.controller("itemController", function ($scope, $http) {

    $scope.addNum = function (i) {
        $scope.num += i;
        if ($scope.num < 1) {
            $scope.num = 1;
        }
    };
    $scope.specificationItems = [];
    $scope.selectSpecification = function (key, value) {
        $scope.specificationItems[key] = value;
        searchSku();
    };
    $scope.isSelected = function (key, value) {
        if ($scope.specificationItems[key] === value) {
            return true;
        } else {
            return false;
        }
    };
    $scope.loadSku = function () {
        $scope.sku = skuList[0];
        $scope.specificationItems = JSON.parse(JSON.stringify($scope.sku.spec));
    };
    searchSku = function () {
        for (var i = 0; i < skuList.length; i++) {
            if (matchObject(skuList[i].spec, $scope.specificationItems)) {
                $scope.sku = skuList[i];
                return;
            }
        }
    };
    matchObject = function (list1, list2) {

        for (var key in list1) {
            if (list1[key] !== list2[key]) {
                return false;
            }
        }
        for (var key in list2) {
            if (list2[key] !== list1[key]) {
                return false;
            }
        }
        return true;
    };
    $scope.addToCart = function () {
        $http.get('http://localhost:9117/cart/addGoodsToCartList.do?itemId='
            + $scope.sku.id + '&num=' +
            $scope.num, {'withCredentials': true}).success(function (response) {
            if (response.success) {
                location.href = 'http://localhost:9117/cart.html';
            } else {
                alert(response.message);
            }
        })
    }
});