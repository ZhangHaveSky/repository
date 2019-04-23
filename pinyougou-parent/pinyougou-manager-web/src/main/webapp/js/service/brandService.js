app.service("brandservice",function ($http) {
    this.add=function () {
        return $http.get("../brand/findAll.do");
    };
    this.get=function (id) {
        return $http.get("../brand/findById.do?id=" + id)
    };
    this.save=function (entity) {
        return $http.post("../brand/save.do",entity)
    };
    this.delet=function (entity) {
        return $http.get("../brand/delete.do?ids="+entity);
    };
    this.search=function (page,size,entity) {
        return $http.post("../brand/search.do?page=" + page + "&size=" + size,entity)
    }
});