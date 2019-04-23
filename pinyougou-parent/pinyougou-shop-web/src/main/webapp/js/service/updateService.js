app.service("updateService",function ($http) {
    this.update=function () {
     var formdata=new FormData();
     formdata.append("file",file.files[0]);
     return $http({
         method: 'post',
         url:'/updates/add.do',
         data:formdata,
         headers: {'Content-Type':undefined},
         transformRequest: angular.identity
     })

    }

});