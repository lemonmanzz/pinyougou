package com.pinyougou.seckill.service;

import com.pinyougou.pojo.TbSeckillOrder;

public interface SecKillOrderService {
    //提交秒杀订单
    void addSecKillOrder(Long secKillGoodsId,String userId);

    TbSeckillOrder searchOrderFromRedisByUserId(String userId);

    void saveOrderToDB(String userId, Long aLong, Object transaction_id);
}
