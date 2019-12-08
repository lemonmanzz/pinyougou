package com.pinyougou.seckill.service;

import com.pinyougou.pojo.TbSeckillOrder;

public interface SecKillOrderService {
    //提交秒杀订单
    void addSecKillOrder(Long secKillGoodsId,String userId);
    //从redis中查询订单
    TbSeckillOrder searchOrderFromRedisByUserId(String userId);
    //保存订单到数据库中
    void saveOrderToDB(String userId, Long out_trade_no, String transaction_id);
    //从redis中删除订单
    void deleteOrderFromRedis(String userId, String out_trade_no);
}
