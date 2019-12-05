package com.pinyougou.sellergoods.service;

import com.pinyougou.group.Goods;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;

import java.util.List;


/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface GoodsService {


    /**
     * 添加商品
     * @param goods
     */
    void add(Goods goods);

    /**
     * 通过商家id查询其对应的商品
     * @param sellerId
     * @return
     */
    PageResult getGoodsBySellerId(int page, int pageSize, String sellerId);

    /**
     * 通过商品id查询组合商品对象
     * @param id
     * @return
     */
    Goods findOneById(long id);

    /**
     * 修改商品信息
     * @param goods
     */
    void update(Goods goods);

    /**
     * @author: zhangyu
     * @date: 2019-11-22
     * @param: []
     * @return: java.util.List<com.pinyougou.pojo.TbGoods>
     * 功能描述: 查询所有未审核并且未删除的所有SPU商品列表
     */
    PageResult findAll(int pageIndex, int pageSize);

    List<TbItem> findItemListByGoodsIdandStatus(Long goodsId, String status);

    /**
     * @author: zhangyu
     * @date: 2019-11-22
     * @param: [ids]
     * @return: void
     * 功能描述: 进行商品审核
     */
    void updateStatus(Long[] ids,String status,String statusField);
}