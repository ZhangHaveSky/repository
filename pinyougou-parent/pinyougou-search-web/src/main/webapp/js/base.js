// 定义模块:
var app = angular.module("pinyougou", []);
// 定义过滤器
app.filter("trustHtml", function ($sce) {
    return function (data) {
        //返回的是过滤后的内容（信任html的转换）
        return $sce.trustAsHtml(data);
    }
});