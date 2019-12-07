package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SecKillOrderService;
import com.pinyougou.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;

@Service
public class SecKillOrderServiceImpl implements SecKillOrderService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TbSeckillOrderMapper seckillOrderMapper;
    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private IdWorker idWorker;


    @Override
    public void addSecKillOrder(Long secKillGoodsId, String userId) {
        //判断该用户是否已经有商品在秒杀购物车中
        if (redisTemplate.boundHashOps("secKillOrderList").get(userId) != null){
            throw new RuntimeException("你已经在秒杀商品中....请先支付之前的订单");
        }
        //首先从redis中获取该商品
        TbSeckillGoods secKillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("secKillGoodsList").get(secKillGoodsId);
        //判断该商品是否存在
        if (secKillGoods == null){
            throw new  RuntimeException("商品不存在");
        }
        if (secKillGoods.getStockCount() <= 0){
            throw new RuntimeException("库存不足");
        }

        //从redis中扣除库存
        secKillGoods.setStockCount(secKillGoods.getStockCount()-1);
        //判断此时商品库存是否为0，为0则删除缓存库
        if (secKillGoods.getStockCount() == 0){
            //同步到数据库，并且删除缓存库
            seckillGoodsMapper.updateByPrimaryKey(secKillGoods);
            redisTemplate.boundHashOps("secKillGoodsList").delete(secKillGoodsId);
        }
        //将更改后的商品信息放回redis中，即库存改变够的商品再放回redis中
        redisTemplate.boundHashOps("secKillGoodsList").put(secKillGoodsId,secKillGoods);
        //添加秒杀订单到redis数据库中
        TbSeckillOrder tbSeckillOrder = new TbSeckillOrder();
        tbSeckillOrder.setCreateTime(new Date());
        tbSeckillOrder.setMoney(secKillGoods.getCostPrice());
        tbSeckillOrder.setUserId(userId);
        tbSeckillOrder.setSeckillId(secKillGoodsId);
        tbSeckillOrder.setSellerId(secKillGoods.getSellerId());
        tbSeckillOrder.setId(idWorker.nextId());
        //设置状态为0，表示未支付状态
        tbSeckillOrder.setStatus("0");
        //保存到redis中
        redisTemplate.boundHashOps("secKillOrderList").put(userId,tbSeckillOrder);
    }

    /**
     * @author: zhangyu
     * @date: 2019-12-07
     * @param: [userId]
     * @return: com.pinyougou.pojo.TbSeckillOrder
     * 功能描述: 从redis中查询对应订单项，参数为用户的id
     */
    @Override
    public TbSeckillOrder searchOrderFromRedisByUserId(String userId) {
        return (TbSeckillOrder) redisTemplate.boundHashOps("secKillOrderList").get(userId);
    }

    /**
     * @author: zhangyu
     * @date: 2019-12-07
     * @param: [userId, aLong, transaction_id]
     * @return: void
     * 功能描述: 保存订单到数据库中
     */
    @Override
    public void saveOrderToDB(String userId, Long aLong, Object transaction_id) {

    }
}
