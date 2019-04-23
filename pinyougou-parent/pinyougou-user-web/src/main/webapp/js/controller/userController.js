//控制层
app.controller('userController', function ($scope, userService) {

    $scope.reg = function () {
        if($scope.password!==$scope.entity.password){
            alert("两次输入密码不一致，请重新输入");
            $scope.entity.password="";
            $scope.password="";
            return ;
        }
        userService.add($scope.entity, $scope.smscode).success(function (response) {
            alert(response.message);
        })
    };
    $scope.sendCode=function () {
        var reg_telephone = new RegExp("^(13[09]|17[3|7]|15[0|1|2|3|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$");
        if($scope.entity.phone==null || $scope.entity.phone===""){
            alert("请填写手机号码");
            return ;
        }
        if(!reg_telephone.test($scope.entity.phone)){
            alert("手机号码不合法");
            return;
        }
        userService.sendCode($scope.entity.phone).success(function (response) {
            alert(response.message);
        })
    }
});	
