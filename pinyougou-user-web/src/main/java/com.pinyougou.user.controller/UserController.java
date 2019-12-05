package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Result;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import com.pinyougou.utils.PhoneFormatCheckUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("user")
public class UserController {

    @Reference
    private UserService userService;
    /**
     * @author: zhangyu
     * @date: 2019-11-27
     * @param: []
     * @return: com.pinyougou.pojo.Result
     * 功能描述: 用户注册功能
     */
    @RequestMapping("add")
    public Result add(@RequestBody TbUser user,String validCode){
        //1.判断用户输入的验证是否和redis中的验证码一致
        if (userService.checkValidCode(user.getPhone(),validCode)){
            userService.add(user);
            return new Result(true,"注册成功");
        }else {
            return new Result(false,"验证码错误");
        }
    }

    @RequestMapping("getValidCode")
    public Result getValidCode(String phone){
        //1.判断手机号是否合法
        if (PhoneFormatCheckUtils.isChinaPhoneLegal(phone)){
            userService.getValidCode(phone);
            return new Result(true,"验证码发送成功");
        }else {
            return new Result(false,"手机号码不合法!");
        }
    }

    @RequestMapping("hasUserName")
    public Result hasUserName(String userName){
        if (userService.hasUserName(userName)){
            return new Result(true,"可以注册");
        }else {
            return new Result(false,"用户名已经存在");
        }
    }
}
