package com.pinyougou.shop.controller;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Result;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.sellergoods.service.TypeTemplateService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/typeTemplate")
public class TypeTemplateController {
    @Reference
    private TypeTemplateService typeTemplateService;

    //查询所有模板
    @RequestMapping("findAll")
    public List<TbTypeTemplate> findAll(){
       return  typeTemplateService.findAll();
    }

    //添加模板
    @RequestMapping("add")
    public Result add(@RequestBody TbTypeTemplate template){
        try {
            typeTemplateService.add(template);
            return new Result(true,"添加成功");
        }catch (Exception e){
            return new Result(false,"添加失败");
        }

    }

    //查找一个模板
    @RequestMapping("/findOne")
    public TbTypeTemplate findOne(long id){
        return typeTemplateService.findOne(id);
    }

    //修改模板
    @RequestMapping("update")
    public Result update(@RequestBody TbTypeTemplate template){
        try {
            typeTemplateService.update(template);
            return new Result(true,"修改成功!");
        }catch (Exception e){
            return new Result(false,"修改失败");
        }
    }

    //删除模板
    @RequestMapping("delete")
    public Result delete(Long[] ids){
        try {
            typeTemplateService.delete(ids);
            return new Result(true,"删除成功");
        }catch (Exception e){
            return new Result(false,"删除失败");
        }

    }

    @RequestMapping("findSpecList")
    public List<Map> findSpecList(Long id){
        return typeTemplateService.findSpecList(id);
    }


	
}
