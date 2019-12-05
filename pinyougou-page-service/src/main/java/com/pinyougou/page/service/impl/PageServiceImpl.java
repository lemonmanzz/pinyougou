package com.pinyougou.page.service.impl;

import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.PageService;
import com.pinyougou.pojo.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;

@Service
public class PageServiceImpl implements PageService {
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Value("${pagedir}")
    private String pagedir;
    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbItemMapper itemMapper;



    /**
     * @author: zhangyu
     * @date: 2019-11-25
     * @param: [goodsId]
     * @return: boolean
     * 功能描述: 通过商品id创建静态网页的方法
     */
    @Override
    public boolean genStaticItemHtml(Long goodsId) {
        try {
            //1.获得配置对象
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            //2.得到模板对象
            Template template = configuration.getTemplate("item.ftl");
            //3.根据模板对象存放输出内容
            //3.1 创建存放数据的map
            HashMap dataModal = new HashMap();
            //3.2获得分类，进行面包屑导航，通过goodsId查询对应的goods表，从中获取分类id，再通过分类id得到分类名称。
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(goodsId);
            //3.3通过分类id查询对应的分类名称。
            String category1 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id()).getName();
            String category2 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id()).getName();
            String category3 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id()).getName();

            //4.查询商品描述表
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);

            //5.查询sku商品列表
            //5.1创建查询对象
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(goodsId);
            //5.2查询状态为1的商品也就是上架的商品
            criteria.andStatusEqualTo("1");
            //5.3按照指定字段进行排序
            example.setOrderByClause("is_default desc");
            List<TbItem> tbItems = itemMapper.selectByExample(example);

            //6.将查询到的数据放入dataModal Map中
            dataModal.put("goods",tbGoods);
            dataModal.put("goodsDesc",goodsDesc);
            dataModal.put("items",tbItems);
            dataModal.put("category1",category1);
            dataModal.put("category2",category2);
            dataModal.put("category3",category3);

            //7.创建html输出位置目录
            FileWriter out = new FileWriter(new File(pagedir+goodsId+".html"));
            System.out.println(pagedir+"pagedir");
            //8.生成html
            template.process(dataModal,out);
            out.close();

            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public void deleteStaticHtml(Long goodsId){
        new File(pagedir+goodsId+".html").delete();
    }
}
