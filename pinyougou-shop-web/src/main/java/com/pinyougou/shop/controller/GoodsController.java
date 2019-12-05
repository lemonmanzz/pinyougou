package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.group.Goods;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.pojo.Result;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.sellergoods.service.GoodsService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Security;
import java.util.List;

@RestController
@RequestMapping("goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    /**
     * 添加商品
     * @return
     */
    @RequestMapping("add")
    public Result add(@RequestBody Goods goods){
        try {
            //1.得到商家id
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            goods.getGoods().setSellerId(sellerId);
            goodsService.add(goods);
            return  new Result(true,"添加成功");

        }catch (Exception e){
            return new Result(false,"添加失败");
        }
    }

    /**
     * 获得当前用户的的goods
     * @return
     */
    @RequestMapping("getGoods")
    public PageResult getGoods(int page, int pageSize){
        //1.得到商家id
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        return goodsService.getGoodsBySellerId(page,pageSize,sellerId);
    }

    /**
     * 通过商品id查询组合商品对象
     * @param id
     * @return
     */
    @RequestMapping("findOne")
    public Goods findOne(long id){
       return goodsService.findOneById(id);
    }

    /**
     * 修改商品信息
     * @param goods
     * @return
     */
    @RequestMapping("update")
    public Result update(@RequestBody Goods goods){
        try {
            goodsService.update(goods);
            return  new Result(true,"修改成功");

        }catch (Exception e){
            return new Result(false,"修改失败");
        }
    }
}
