app.service("userService",function ($http) {
    //1.获取验证码
    this.getValidCode=(phone)=>{
        return $http.get("./user/getValidCode.do?phone="+phone);
    };
    //2.用户注册
    this.add = (entity,validCode)=>{
        return $http.post("./user/add.do?validCode="+validCode,entity);
    };
    //3.判断用户名是否已经存在
    this.hasUserName = (username)=>{
        return $http.get("/user/hasUserName.do?userName="+username);
    }
});