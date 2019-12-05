//服务层
app.service('uploadService',function($http){
	    	
    //1.文件上传
    this.upload=()=>{
        //1.1)构造要上传的文件对象
        let formData = new FormData;
        //1.2)添加文件内容
        formData.append("file",file.files[0])
        //1.3)发送上传请求
        return $http({
            url:"../upload.do",                     //上传的地址
            method:"post",                          //请求方式
            data:formData,                          //上传的数据
            headers:{"Content-Type":undefined},     //如果设置为undefined，angularjs就转换为multipart/form-data
            transformRequest:angular.identity       //进行序列化上传
        })
    }
});
