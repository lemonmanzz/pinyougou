package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.group.Goods;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.sellergoods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Service
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbBrandMapper brandMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbSellerMapper sellerMapper;
    @Autowired
    private TbItemMapper itemMapper;


    /**
     * 添加商品
     * @param goods
     */
    @Override
    public void add(Goods goods) {
        //1.添加商品
        goodsMapper.insert(goods.getGoods());
        //2.添加商品描述信息
        //2.1)为商品描述设置id
        goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());
        goodsDescMapper.insert(goods.getGoodsDesc());
        //3.添加sku商品列表
        //3.1 遍历goods.items得到对应的某一条商品列表
        insertItems(goods);
    }


    /**
     * 通过商家id外键查询其对应的商品信息
     * @param sellerId
     * @return
     */
    @Override
    public PageResult getGoodsBySellerId(int page, int pageSize, String sellerId) {
        PageHelper.startPage(page,pageSize);
        TbGoodsExample example = new TbGoodsExample();
        TbGoodsExample.Criteria criteria = example.createCriteria();
        criteria.andSellerIdEqualTo(sellerId);
        List<TbGoods> tbGoods = goodsMapper.selectByExample(example);
        Page<TbGoods> pageList = (Page<TbGoods>) tbGoods;
        return new PageResult(pageList.getTotal(),pageList.getResult());
    }

    /**
     * 通过商品id查询组合商品对象Goods
     * @param id
     * @return
     */
    @Override
    public Goods findOneById(long id) {
        //1.获得商品对象
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        //2.获取商品描述对象
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        //3.通过goodsid外键查询items
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<TbItem> tbItems = itemMapper.selectByExample(example);
        //4.返回组合对象goods
        return new Goods(tbGoods,tbGoodsDesc,tbItems);
    }

    @Override
    public void update(Goods goods) {
        //1.修改商品表
        goodsMapper.updateByPrimaryKeySelective(goods.getGoods());
        //2.修改描述表
        goodsDescMapper.updateByPrimaryKeySelective(goods.getGoodsDesc());
        //3.修改商品列表
        //3.1 通过商品id删除商品列表
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(goods.getGoods().getId());
        itemMapper.deleteByExample(example);
        //3.2 添加items
        insertItems(goods);
    }

    /**
     * @author: zhangyu
     * @date: 2019-11-22
     * @param: []
     * @return: java.util.List<com.pinyougou.pojo.TbGoods>
     * 功能描述: 查询所有未审核并且没有删除的所有SPU商品列表
     */
    @Override
    public PageResult findAll(int pageIndex, int pageSize) {
        PageHelper.startPage(pageIndex,pageSize);
        TbGoodsExample example = new TbGoodsExample();
        TbGoodsExample.Criteria criteria = example.createCriteria();
        //未审核
        //未删除
        criteria.andAuditStatusEqualTo("0");
        List<TbGoods> tbGoods = goodsMapper.selectByExample(example);
        Page<TbGoods> pageList = (Page<TbGoods>) tbGoods;
        PageResult pageResult = new PageResult(pageList.getTotal(),pageList.getResult());

        return pageResult ;
    }
    @Override
    public List<TbItem> findItemListByGoodsIdandStatus(Long goodsId,String status){
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(goodsId);
        criteria.andStatusEqualTo(status);
        return itemMapper.selectByExample(example);
    }

    /**
     * @author: zhangyu
     * @date: 2019-11-22
     * @param: [ids]
     * @return: void
     * 功能描述: 更新商品某个字段的状态值
     */
    @Override
    public void updateStatus(Long[] ids,String status, String statusField) {
        //1.通过id查询所有需要更新的商品列表
        TbGoodsExample example = new TbGoodsExample();
        example.createCriteria().andIdIn(Arrays.asList(ids));
        List<TbGoods> tbGoods = goodsMapper.selectByExample(example);
        //2.判断是否是审核更新
        if (statusField.equals("审核")){
            for (TbGoods tbGood : tbGoods) {
                tbGood.setAuditStatus("1");
                goodsMapper.updateByPrimaryKey(tbGood);
            }
//            //2.2 更新索引库，即添加至索引库
//            //更加spu商品id查询对应的sku商品列表
//            TbItemExample itemExample = new TbItemExample();
//            itemExample.createCriteria().andGoodsIdIn(Arrays.asList(ids));
//            List<TbItem> tbItems = itemMapper.selectByExample(itemExample);
//            for (TbItem tbItem : tbItems) {
//                tbItem.setSpecMap(JSON.parseObject(tbItem.getSpec(),Map.class));
//                System.out.println(tbItem.getBrand());
//            }
//            //2.3 将sku商品列表添加至索引库
//            solrTemplate.saveBeans(tbItems);
//            solrTemplate.commit();
//            System.out.println("更新至索引库成功！");
        }

        //3.判断是否是删除更新，即将is_delete字段改为1
        if (statusField.equals("删除")){
            for (TbGoods tbGood : tbGoods) {
                tbGood.setIsDelete("1");
                goodsMapper.updateByPrimaryKey(tbGood);
            }
        }
    }

    /**
     * 添加items的方法
     * @param goods
     */
    private void insertItems(Goods goods) {
        List<TbItem> items = goods.getItems();
        for (TbItem item : items) {
            //3.2 设置一系列属性
            //①设置对应的商品id
            item.setGoodsId(goods.getGoods().getId());
            //②设置品牌id
            item.setBrand(brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId()).getName());
            //③设置分类的名称
            item.setCategory(itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id()).getName());
            //④设置分类id
            item.setCategoryid(goods.getGoods().getCategory3Id());
            //⑤设置创建时间和更新时间
            item.setCreateTime(new Date());
            item.setUpdateTime(new Date());
            //⑥ 设置商家名称
            item.setSeller(sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId()).getName());
            //⑦ 设置title
            item.setTitle(goods.getGoods().getGoodsName());
            //⑧ 设置上传图片
            String itemImages = goods.getGoodsDesc().getItemImages();
            List<Map> maps = JSON.parseArray(itemImages, Map.class);
            if(maps != null && maps.size() > 0){
                String url = (String) maps.get(0).get("url");
                item.setImage(url);
            }
            //3.4)添加sku商品
            itemMapper.insert(item);
        }
    }
}