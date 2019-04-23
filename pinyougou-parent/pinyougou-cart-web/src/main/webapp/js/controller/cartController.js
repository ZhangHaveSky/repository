//购物车控制层
app.controller('cartController', function ($scope, cartService) {
    //查询购物车列表
    $scope.findCartList = function () {
        cartService.findCartList().success(
            function (response) {
                $scope.cartList = response;
                $scope.totalValue = cartService.sum($scope.cartList);
            }
        );
    };

    //数量加减
    $scope.addGoodsToCartList = function (itemId, num) {
        cartService.addGoodsToCartList(itemId, num).success(
            function (response) {
                if (response.success) {//如果成功
                    $scope.findCartList();//刷新列表
                } else {
                    alert(response.message);
                }
            }
        );
    };

    $scope.findAddressList = function () {
        cartService.findAddressList().success(function (response) {
            $scope.addressList = response;
            for (var i = 0; i < $scope.addressList.length; i++) {
                if ($scope.addressList[i].isDefault === '1') {
                    $scope.address = $scope.addressList[i];
                    break;
                }
            }
        })
    };
    $scope.isSeletedAddress = function (address) {
        return $scope.address === address;
    };

    $scope.order = {paymentType: '1'};

    //选择支付方式
    $scope.selectPayType = function (type) {
        $scope.order.paymentType = type;
    };
    $scope.submitOrder=function () {
        $scope.order.receiverAreaName=$scope.address.address;//地址
        $scope.order.receiverMobile=$scope.address.mobile;//手机
        $scope.order.receiver=$scope.address.contact;//联系人
        cartService.submitOrder($scope.order).success(function (response) {
            if(response.success){
                if($scope.order.paymentType==='1'){
                  location.href="pay.html";
                }
                if($scope.order.paymentType==='2'){
                   location.href="paysuccess.html";
                }
            }else {
                alert(response.message);
            }
        })

    }
});