package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import com.pinyougou.seckill.service.SecKillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.List;

@Service
public class SecKillServiceImpl implements SecKillService {
    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * @author: zhangyu
     * @date: 2019-12-06
     * @param: []
     * @return: java.util.List<com.pinyougou.pojo.TbSeckillGoods>
     * 功能描述: 从redis缓存库中获取对应的秒杀商品列表
     */
    private static int flag = 0;
    @Override
    public List<TbSeckillGoods> findSecKillGoods() {
        if (flag == 0){
            UpdateToRedis updateToRedis = new UpdateToRedis();
            updateToRedis.start();
            flag = 1;
        }
        return redisTemplate.boundHashOps("secKillGoodsList").values();
    }

    public class UpdateToRedis extends Thread{
        public void run() {
            while (true){
                //创建查询条件
                TbSeckillGoodsExample example = new TbSeckillGoodsExample();
                TbSeckillGoodsExample.Criteria criteria = example.createCriteria();
                //开始时间小于等于当前时间
                criteria.andStartTimeLessThanOrEqualTo(new Date());
                //结束时间大于当前时间
                criteria.andEndTimeGreaterThan(new Date());
                //库存量大于0
                criteria.andStockCountGreaterThan(0);
                //状态需要为1，也就是参加秒杀的商品列表
                criteria.andStatusEqualTo("1");
                //执行查询,得到数据中符合条件的商品列表
                List<TbSeckillGoods> dbSeckillGoods = seckillGoodsMapper.selectByExample(example);
                //查询redis中已经存在的秒杀商品列表
                // System.out.println("查询数据库.....");
                List<TbSeckillGoods> redisSecKillGoodsList = redisTemplate.boundHashOps("secKillGoodsList").values();
                if (dbSeckillGoods != null && dbSeckillGoods.size() > 0){
                    //与redis中的数据做差值，也就是将redis中没有，而数据库中查出来的数据有的商品求出来
                    if (redisSecKillGoodsList != null && redisSecKillGoodsList.size() > 0){
                        dbSeckillGoods.removeAll(redisSecKillGoodsList);
                    }
                    //将差值一个一个的放入redis中
                    for (TbSeckillGoods dbSeckillGood : dbSeckillGoods) {
                        redisTemplate.boundHashOps("secKillGoodsList").put(dbSeckillGood.getId(),dbSeckillGood);
                    }
                    //删除已经超时的商品
                    for (TbSeckillGoods seckillGoods : redisSecKillGoodsList) {
                        //从redis中删除秒杀时间已经超过的数据
                        if (seckillGoods.getEndTime().getTime() - new Date().getTime() <= 0){
                            redisTemplate.boundHashOps("secKillGoodsList").delete(seckillGoods.getId());
                        }
                    }
                }
                //该查询操作睡眠1秒钟，也就是一秒钟同步一次数据到redis中，这样让新加入的数据也可以被展示出来
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * @author: zhangyu
     * @date: 2019-12-06
     * @param: [id]
     * @return: com.pinyougou.pojo.TbSeckillGoods
     * 功能描述: 通过id查询一件秒杀商品
     */
    @Override
    public TbSeckillGoods findOneSecKillGoods(Long id) {
        //从redis中获取该商品对象
        return (TbSeckillGoods) redisTemplate.boundHashOps("secKillGoodsList").get(id);
    }
}
