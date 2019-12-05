package com.pinyougou.portal.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.pojo.TbContent;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("content")
public class contentController {
    @Reference
    private ContentService contentService;

    /**
     * @Author: ZhangYu
     * @Date:2019-11-18
     * @Description: java学习
     * @Question: 通过分类id查询对应的广告集合
     */
    @RequestMapping("findContentByCategoryId")
    public List<TbContent> findContentByCategoryId(Long categoryId){
        return contentService.findContentByCategoryId(categoryId);
    }
}
