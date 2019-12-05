package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.PayService;
import com.pinyougou.pojo.Result;
import com.pinyougou.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by WF on 2019/12/4 9:55
 */
@RestController
@RequestMapping("pay")
public class PayController {
    @Autowired
    private IdWorker idWorker;
    @Reference
    private PayService payService;
    //1.向微信后台发出下单请求
    @RequestMapping("createNative")
    public Map createNative(){
        //1.1)生成订单号
        long id = idWorker.nextId();
        //1.2)向微信支付服务发出下单请求
        return payService.createNative(id+"","1");
    }
    //2.向微信后台发出查询订单请求（每隔三秒发出一次请求）
    @RequestMapping("queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        int x = 0;          //代表访问次数的变量，每隔3秒累加1次，当累加到100时，代表经过了5分钟
        while(true){
            //2.1)向微信后台发出查询订单请求，并返回查询结果
            Map resultMap = payService.queryPayStatus(out_trade_no);
            //2.2)根据查询结果决定返回
            if(resultMap == null){
                return new Result(false,"查询订单失败!");
            }
            if(resultMap.get("trade_state").equals("SUCCESS")){     //支付状态为成功
                return new Result(true,"支付成功！");
            }
            try {
                //每查询一次就休息3s
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            x++;
            if(x >= 100){
                return new Result(false,"二维码超时");
            }
        }
    }
}
