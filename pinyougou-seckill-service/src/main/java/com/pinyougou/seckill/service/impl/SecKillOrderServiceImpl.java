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
    public void saveOrderToDB(String userId, Long out_trade_no, String transaction_id) {
        //根据用户id查询出订单
        TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("secKillOrderList").get(userId);
        //判断订单是否存在
        if (seckillOrder == null){
            throw new RuntimeException("对不起，订单不存在!");
        }
        if (seckillOrder.getId().longValue() != out_trade_no.longValue()){
            throw new RuntimeException("两次订单不一致!");
        }
        //给订单设置属性值
        seckillOrder.setPayTime(new Date());
        seckillOrder.setStatus("1");
        seckillOrder.setTransactionId(transaction_id);
        //添加到数据库
        seckillOrderMapper.insert(seckillOrder);
        //从redis中删除
        redisTemplate.boundHashOps("secKillOrderList").delete(userId);
    }
    /**
     * @author: zhangyu
     * @date: 2019/12/8
     * @param: [userId, out_trade_no]
     * @return: void
     * 功能描述: 当支付超时时，从redis中删除订单
     */
    @Override
    public void deleteOrderFromRedis(String userId, String out_trade_no) {
        //通过用户id查询对应订单
        TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("secKillOrderList").get(userId);
        //判断是否同一个订单
        if (seckillOrder != null && seckillOrder.getId().longValue() == new Long(out_trade_no).longValue()){
            //从redis中删除订单
            redisTemplate.boundHashOps("secKillOrderList").delete(userId);
            //从redis中获取商品信息
            TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("secKillGoodsList").get(seckillOrder.getSeckillId());
            //判断redis中是否含有该商品
            if (seckillGoods != null){
                //库存加1
                seckillGoods.setStockCount(seckillGoods.getStockCount()+1);
            }else {
                //查询数据库，得到商品信息
                seckillGoods = seckillGoodsMapper.selectByPrimaryKey(seckillOrder.getId());
                seckillGoods.setStockCount(seckillGoods.getStockCount()+1);
            }
            //将更新库存后的商品放入redis中
            redisTemplate.boundHashOps("secKillGoodsList").put(seckillGoods.getId(),seckillGoods);
        }

    }
}
