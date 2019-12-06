package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SecKillService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("secKillGoods")
@RestController
public class SecKillGoodsController {
    @Reference
    private SecKillService secKillService;

    /**
     * @author: zhangyu
     * @date: 2019-12-06
     * @param: []
     * @return: java.util.List<com.pinyougou.pojo.TbSeckillGoods>
     * 功能描述: 查询所有复合秒杀的商品列表 ① 在秒杀时间范围内  ② 参与秒杀的库存量需要大于0
     */
    @RequestMapping("findSecKillGoods")
    public List<TbSeckillGoods> findSecKillGoods(){
       return secKillService.findSecKillGoods();
    }

    /**
     * @author: zhangyu
     * @date: 2019-12-06
     * @param: [id]
     * @return: com.pinyougou.pojo.TbSeckillGoods
     * 功能描述: 通过id查询一件秒杀商品
     */
    @RequestMapping("findOneSecKillGoods")
    public TbSeckillGoods findOneSecKillGoods(Long id){
        return secKillService.findOneSecKillGoods(id);
    }
}
