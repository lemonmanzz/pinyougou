package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.content.service.ContentService;
import com.pinyougou.pojo.Result;
import com.pinyougou.pojo.TbContent;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {
    @Reference
    private ContentService contentService;

    /**
     * @Author: ZhangYu
     * @Date:2019-11-18
     * @Description: java学习
     * @Question: 查询所有的广告
     */
    @RequestMapping("findAll")
    public List<TbContent> findAll(){
        return contentService.findAll();
    }
    /**
     * @Author: ZhangYu
     * @Date:2019-11-18
     * @Description: java学习
     * @Question: 添加广告
     */
    @RequestMapping("add")
    public Result add(@RequestBody TbContent content){
        try {
            contentService.add(content);
            return new Result(true,"添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }


    /**
     * @Author: ZhangYu
     * @Date:2019-11-18
     * @Description: java学习
     * @Question: 修改广告
     */
    @RequestMapping("update")
    public Result update(@RequestBody TbContent content){
        try {
            contentService.update(content);
            return new Result(true,"修改成功");
        }catch (Exception e){
            return new Result(false,"修改失败");
        }
    }

    /**
     * @Author: ZhangYu
     * @Date:2019-11-18
     * @Description: java学习
     * @Question: 删除选中的广告
     */
    @RequestMapping("delete")
    public Result update(Long[] ids){
        try {
            contentService.delete(ids);
            return new Result(true,"删除成功");
        }catch (Exception e){
            return new Result(false,"删除失败");
        }
    }

    @RequestMapping("findOne")
    public TbContent findOne(Long id){
        return contentService.findOne(id);
    }
}
