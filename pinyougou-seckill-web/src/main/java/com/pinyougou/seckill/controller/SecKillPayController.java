package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pay.service.PayService;
import com.pinyougou.pojo.Result;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SecKillOrderService;
import com.pinyougou.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("pay")
public class SecKillPayController {

    @Reference
    private SecKillOrderService secKillOrderService;
    @Reference
    private PayService payService;
    @Autowired
    private IdWorker idWorker;

    @RequestMapping("createNative")
    public Map  createNative(){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        //从redis中获取秒杀订单
        TbSeckillOrder seckillOrder = secKillOrderService.searchOrderFromRedisByUserId(userId);
        //判断订单是否存在
        if (seckillOrder != null){
            //得到钱数，以分为单位
            long fen = (long) (seckillOrder.getMoney().doubleValue()*100);
            //调用支付模块进行支付申请
            return payService.createNative(seckillOrder.getId()+"",fen+"");
        }else {
            return new HashMap();
        }
    }

    //查询
    @RequestMapping("queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        //获取当前用户名
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        //设置定时参数
        int time = 0;
        while (true){
            Map map = payService.queryPayStatus(out_trade_no);
            if (map == null){//表示出错
                return new Result(false,"支付出错");
            }
            if (map.get("trade_state").equals("SUCCESS")){//支付成功
                //保存订单至数据库
                secKillOrderService.saveOrderToDB(userId,Long.valueOf(out_trade_no),idWorker.nextId()+"");
                //返回
                return new Result(true,"支付成功");
            }
            //休眠3秒钟
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            time++;
            //一分钟超时
            if (time > 20){
                //执行关单操作
                Map<String,String> closeMap =  payService.closePay(out_trade_no);
                if (!"SUCCESS".equals(closeMap.get("result_code"))){//等于SUCCESS表示正常关闭
                    if (closeMap.get("err_code").equals("ORDERPAID")){//代表已经支付成功
                        //将redis中的订单保存到数据库中
                        secKillOrderService.saveOrderToDB(userId,Long.valueOf(out_trade_no),map.get("transaction_id").toString());
                        return new Result(true,"支付成功");
                    }

                }
                //从redis中删除订单，并且回复商品库存
                secKillOrderService.deleteOrderFromRedis(userId,out_trade_no);
                return new Result(false,"二维码超时");
            }
        }
    }
}
