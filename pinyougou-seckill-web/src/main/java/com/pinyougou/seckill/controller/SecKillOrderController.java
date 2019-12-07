package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Result;
import com.pinyougou.seckill.service.SecKillOrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("secKillOrder")
public class SecKillOrderController {

    @Reference
    private SecKillOrderService secKillOrderService;
    @RequestMapping("submitOrder")
    public Result submitOrder(Long secKillGoodsId){
        //得到当前登录用户名
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        //判断用户是否登录
        if (userId.equals("anonymousUser")){
            return new Result(false,"请先登录在进行抢购");
        }
        try {
            //提交订单
            secKillOrderService.addSecKillOrder(secKillGoodsId,userId);
            return new Result(true,"提交订单成功");
        }catch (RuntimeException e){
            e.printStackTrace();
            return new Result(false,e.getMessage());
        }catch (Exception ex){
            ex.printStackTrace();
            return new Result(false,"提交失败");
        }

    }
}
