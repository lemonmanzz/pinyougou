package com.pinyougou.shop.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RestController
public class LoginController {

    @RequestMapping("login")
    public Map showUser(HttpSession session){
        HashMap map;
        if ((map = (HashMap) session.getAttribute("map")) == null){
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            Date loginTime = new Date();
            map = new HashMap();
            map.put("userName",userName);
            map.put("loginTime",loginTime);
            session.setAttribute("map",map);
        }
        return map;
    }
}
