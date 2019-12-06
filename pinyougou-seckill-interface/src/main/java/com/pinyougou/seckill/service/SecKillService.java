package com.pinyougou.seckill.service;

import com.pinyougou.pojo.TbSeckillGoods;

import java.util.List;

public interface SecKillService {
    /**
     * @author: zhangyu
     * @date: 2019-12-06
     * @param: []
     * @return: java.util.List<com.pinyougou.pojo.TbSeckillGoods>
     * 功能描述: 查询所有复合秒杀的商品列表 ① 在秒杀时间范围内  ② 参与秒杀的库存量需要大于0
     */
    List<TbSeckillGoods> findSecKillGoods();

    /**
     * @author: zhangyu
     * @date: 2019-12-06
     * @param: [id]
     * @return: com.pinyougou.pojo.TbSeckillGoods
     * 功能描述: 通过id查询一件秒杀商品
     */
    TbSeckillGoods findOneSecKillGoods(Long id);
}
