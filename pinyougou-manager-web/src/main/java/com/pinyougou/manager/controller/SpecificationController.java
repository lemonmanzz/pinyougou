package com.pinyougou.manager.controller;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.group.Specification;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.pojo.Result;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.sellergoods.service.SpecificationService;
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
@RequestMapping("/specification")
public class SpecificationController {

    @Reference
    private SpecificationService specificationService;

    //条件查询和分页
    @RequestMapping("search")
    public PageResult search(int page, int pageSize, String specName) {

        return  specificationService.search(page,pageSize,specName);
    }
    //添加一个规格
    @RequestMapping("add")
	public Result add(@RequestBody Specification specification){
        try {
            specificationService.add(specification);
            return new Result(true,"添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败");
        }

    }
    //修改一个规格
    @RequestMapping("update")
    public Result update(@RequestBody Specification specification){
        try {
            specificationService.update(specification);
            return new Result(true,"修改成功");
        }catch (Exception e){
            return new Result(false,"修改失败");
        }

    }
    //查询某个规格
    @RequestMapping("findOne")
    public Specification findOne(Long id){
        return  specificationService.findOne(id);
    }


    //查询所有的规格
    @RequestMapping("findAll")
    public List<Map> findAll(){
        return specificationService.findAll();
    }

    //删除规格
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            specificationService.delete(ids);
            return new Result(true,"删除成功");
        }catch (Exception e){
            return new Result(false,"删除失败");
        }
    }
}
