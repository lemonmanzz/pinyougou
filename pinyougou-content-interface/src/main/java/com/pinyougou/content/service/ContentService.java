package com.pinyougou.content.service;

import com.pinyougou.pojo.TbContent;

import java.util.List;

public interface ContentService {
    /**
     * @Author: ZhangYu
     * @Date:2019-11-18
     * @Description: java学习
     * @Question: 查询所有的广告
     */
    List<TbContent> findAll();

    /**
     * @Author: ZhangYu
     * @Date:2019-11-18
     * @Description: java学习
     * @Question: 添加广告
     */
    void add(TbContent content);

    /**
     * @Author: ZhangYu
     * @Date:2019-11-18
     * @Description: java学习
     * @Question: 修改广告
     */
    void update(TbContent content);

    /**
     * @Author: ZhangYu
     * @Date:2019-11-18
     * @Description: java学习
     * @Question: 删除选中的广告
     */
    void delete(Long[] ids);

    /**
     * @Author: ZhangYu
     * @Date:2019-11-18
     * @Description: java学习
     * @Question: 查询一个广告实例
     */
    TbContent findOne(Long id);

    /**
     * @Author: ZhangYu
     * @Date:2019-11-18
     * @Description: java学习
     * @Question: 根据分类id查询广告
     */
    List<TbContent> findContentByCategoryId(Long categoryId);
}
