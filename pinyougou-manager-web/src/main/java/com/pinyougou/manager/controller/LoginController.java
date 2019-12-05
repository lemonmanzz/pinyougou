package com.pinyougou.manager.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("login")
@SuppressWarnings("all")
public class LoginController {

    @RequestMapping("name")
    public Map showName(){
        //1.从security中拿用户名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //2.将得到的用户名放入map中
        Map map = new HashMap();
        map.put("loginName",name);
        map.put("loginTime",new Date());
        //3.返回
        return map;
    }
}
