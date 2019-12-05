package com.pinyougou.shop.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Result;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/seller")
public class SellerController {
    @Reference
    private SellerService sellerService;


    //添加公司注册
    @RequestMapping("/add")
    public Result add(@RequestBody TbSeller seller){
        try {
            seller.setStatus("0");
            seller.setCreateTime(new Date());
            seller.setPassword(new BCryptPasswordEncoder().encode(seller.getPassword()));
            sellerService.add(seller);
            return new Result(true,"注册成功");
        }catch (Exception e){
            return new Result(false,"注册失败");
        }

    }
}
