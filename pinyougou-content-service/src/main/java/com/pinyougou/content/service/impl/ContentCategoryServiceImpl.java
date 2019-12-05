package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.content.service.ContentCategoryService;
import com.pinyougou.mapper.TbContentCategoryMapper;
import com.pinyougou.pojo.TbContentCategory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {
    @Autowired
    private TbContentCategoryMapper contentCategoryMapper;

    /**
     * @Author: ZhangYu
     * @Date:2019-11-18
     * @Description: java学习
     * @Question: 查询所有的广告分类
     */
    @Override
    public List<TbContentCategory> findAll() {
        return contentCategoryMapper.selectByExample(null);
    }
}
