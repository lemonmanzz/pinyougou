package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.content.service.ContentCategoryService;
import com.pinyougou.pojo.TbContentCategory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("contentCategory")
public class ContentCategoryController {

    @Reference
    private ContentCategoryService contentCategoryService;

    /**
     * @Author: ZhangYu
     * @Date:2019-11-18
     * @Description: java学习
     * @Question: 查询所有的广告分类
     */
    @RequestMapping("/findAll")
    public List<TbContentCategory> findAll(){
        return contentCategoryService.findAll();
    }
}
