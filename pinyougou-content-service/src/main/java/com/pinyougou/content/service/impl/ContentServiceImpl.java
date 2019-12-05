package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.mapper.TbContentMapper;
import com.pinyougou.pojo.TbContent;
import com.pinyougou.pojo.TbContentExample;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;


import java.util.Arrays;
import java.util.List;

@Service
public class ContentServiceImpl implements ContentService {
    @Autowired
    private TbContentMapper contentMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * @Author: ZhangYu
     * @Date:2019-11-18
     * @Description: java学习
     * @Question: 查询所有的广告
     */
    @Override
    public List<TbContent> findAll() {
        return contentMapper.selectByExample(null);
    }

    /**
     * @Author: ZhangYu
     * @Date:2019-11-18
     * @Description: java学习
     * @Question: 添加广告
     */
    @Override
    public void add(TbContent content) {
        contentMapper.insert(content);
    }

    /**
     * @Author: ZhangYu
     * @Date:2019-11-18
     * @Description: java学习
     * @Question: 修改广告信息
     */
    @Override
    public void update(TbContent content) {

        contentMapper.updateByPrimaryKeySelective(content);
    }

    /**
     * @Author: ZhangYu
     * @Date:2019-11-18
     * @Description: java学习
     * @Question: 删除广告信息
     */
    @Override
    public void delete(Long[] ids) {

        TbContentExample example = new TbContentExample();
        TbContentExample.Criteria criteria = example.createCriteria();
        criteria.andIdIn(Arrays.asList(ids));
        contentMapper.deleteByExample(example);

    }

    /**
     * @Author: ZhangYu
     * @Date:2019-11-18
     * @Description: java学习
     * @Question: 查询一个广告实例，通过id
     */
    @Override
    public TbContent findOne(Long id) {
        return contentMapper.selectByPrimaryKey(id);
    }


    /**
     * @Author: ZhangYu
     * @Date:2019-11-18
     * @Description: java学习
     * @Question: 根据分类id查询广告
     */
    @Override
    public List<TbContent> findContentByCategoryId(Long categoryId) {
        //1.从redis中拿对应数据
        List<TbContent> contents = (List<TbContent>) redisTemplate.boundHashOps("contents").get(categoryId);
        //2.判断是是否有值,为空则查询数据库，并把结果设置到redis中
        if (contents == null){
            TbContentExample example = new TbContentExample();
            TbContentExample.Criteria criteria = example.createCriteria();
            criteria.andCategoryIdEqualTo(categoryId);
            System.out.println("查询了一次数据库");
            contents = contentMapper.selectByExample(example);
            redisTemplate.boundHashOps("contents").put(categoryId,contents);
        }
        return contents;
    }
}
