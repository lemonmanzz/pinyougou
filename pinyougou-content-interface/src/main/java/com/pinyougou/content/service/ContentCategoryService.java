package com.pinyougou.content.service;

import com.pinyougou.pojo.TbContentCategory;

import java.util.List;

public interface ContentCategoryService {
    /**
     * @Author: ZhangYu
     * @Date:2019-11-18
     * @Description: java学习
     * @Question: 查询所以的广告分类
     */
    List<TbContentCategory> findAll();

}
