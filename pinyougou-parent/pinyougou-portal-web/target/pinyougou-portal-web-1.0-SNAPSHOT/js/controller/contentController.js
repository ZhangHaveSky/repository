//广告控制层（运营商后台）
app.controller("contentController",function ($scope,contentService) {
    //广告集合
    $scope.contentList=[];
    $scope.findByCategoryId=function (categoryId) {
        contentService.findByCategoryId(categoryId).success(function (response) {
            $scope.contentList[categoryId]=response;
        })
    };
    $scope.searchs=function (keywords) {
      location.href='http://localhost:9105/search.html#?keywords='+keywords;
    }

});