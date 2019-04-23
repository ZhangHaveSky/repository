app.controller('searchController', function ($scope, $location, searchService) {

    //搜索
    $scope.search = function () {
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap = response;
                buildPageLabel();

            }
        );
    };
    $scope.loadkeywords = function () {
       if($location.search().keywords!=null){
           $scope.searchMap.keywords = $location.search().keywords;
           $scope.search();
       }
    };
    $scope.searchs = function () {
        var words = $scope.searchMap.keywords;
        $scope.searchMap = {
            'keywords': '',	//关键字
            'category': '',	//分类
            'brand': '',		//品牌
            'spec': {},		//规格
            'price': '',		//价格
            'pageNo': 1,		//页码
            'pageSize': 40,	//每页记录数
            'sort': '',		//是否排序
            'sortField': ''	//排序字段
        };
        $scope.searchMap.keywords = words;
        $scope.search();
    };
    //搜索对象
    $scope.searchMap = {
        'keywords': '',	//关键字
        'category': '',	//分类
        'brand': '',		//品牌
        'spec': {},		//规格
        'price': '',		//价格
        'pageNo': 1,		//页码
        'pageSize': 40,	//每页记录数
        'sort': '',		//是否排序
        'sortField': ''	//排序字段
    };

    $scope.addSearchItem = function (key, value) {
        if (key === 'brand' || key === 'category' || key === 'price') {
            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }
        $scope.search();
    };

    $scope.removeSearchItem = function (key) {
        if (key === 'brand' || key === 'category' || key === 'price') {
            $scope.searchMap[key] = '';
        } else {
            delete $scope.searchMap.spec[key];
        }
    };
    var buildPageLabel = function () {
        $scope.pageLabel = [];//必须定义 直接值可以不用写,但是调用方法进行赋必须定义
        var begin = 1;
        var end = $scope.resultMap.totalPages;
        //是否显示前后省略
        $scope.firstDot = false;
        $scope.lastDot = false;
        var pageno = $scope.searchMap.pageNo;
        var total = $scope.resultMap.totalPages;
        if (total > 5) {
            if (pageno > 3 && pageno < total - 2) {
                begin = pageno - 2;
                end = pageno + 2;
                $scope.firstDot = true;
                $scope.lastDot = true;
            } else if (pageno >= total - 2) {
                begin = total - 4;
                $scope.firstDot = true;
            } else if (pageno <= 3) {
                $scope.lastDot = true;
                end = 5;
            }
        }
        //构建页码
        for (var i = begin; i <= end; i++) {
            $scope.pageLabel.push(i);
        }
    };
    $scope.isTopPage = function () {
        return $scope.searchMap.pageNo === 1;
    };

    $scope.isEndPage = function () {
        return $scope.searchMap.pageNo === $scope.resultMap.totalPages;
    };

    $scope.queryByPage = function (page) {
        page = parseInt(page);
        if (page < 1 || page > $scope.resultMap.totalPages) {
            return;
        }
        $scope.searchMap.pageNo = page;
        $scope.search();
    };
    $scope.sortSearch = function (field, way) {
        $scope.searchMap.sort = way;
        $scope.searchMap.sortField = field;
        $scope.search();
    };
    $scope.keywordsIsBrand = function () {
        var list = $scope.resultMap.brandList;
        for (var i = 1; i < list.length; i++) {
            if ($scope.searchMap.keywords.indexOf(list[i].text) >= 0) {
                return false;
            }
        }
        return true;
    };
});
