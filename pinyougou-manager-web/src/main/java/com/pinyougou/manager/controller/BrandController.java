package com.pinyougou.manager.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.pojo.Result;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Created by WF on 2019-11-07 11:19
 */
@RestController
@RequestMapping("brand")
public class BrandController {
    @Reference
    private BrandService brandService;
    //1.查询所有品牌列表
    @RequestMapping("list")
    public List<Map> findAll(){
        return brandService.findBrandList();
    }

    //2.分页查询+条件查询
    @RequestMapping("search")
    public PageResult search(int page, int pageSize, @RequestBody(required = false) TbBrand brand){
        return brandService.search(page,pageSize,brand);

    }
    //3.添加一个品牌
    @RequestMapping("add")
    public Result add(@RequestBody TbBrand brand){
        try {
            brandService.add(brand);
            return new Result(true,"添加成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }
    @RequestMapping("update")
    public Result update(@RequestBody TbBrand brand) {
        System.out.println(brand.getName());
        System.out.println(brand.getId());
        System.out.println(brand.getFirstChar());
        try {
            brandService.update(brand);
            return new Result(true,"修改成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改失败!");
        }
    }
    @RequestMapping("delete")
    public Result update(Long[] selectedIds){
        try {
            brandService.delete(selectedIds);
            return new Result(true,"删除成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败！");
        }

    }
}
