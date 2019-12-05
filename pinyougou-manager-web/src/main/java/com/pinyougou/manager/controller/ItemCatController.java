package com.pinyougou.manager.controller;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Result;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.sellergoods.service.ItemCatService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/itemCat")
public class ItemCatController {


    @Reference
    private ItemCatService itemCatService;


    /**
     * @Author: ZhangYu
     * @Date:2019-11-13
     * @Description: java学习
     * @Question: 通过parentId查询
     */
    @RequestMapping("findByParentId")
    public List<TbItemCat> findByParentId(Long  parentId){
      return  itemCatService.findByParentId(parentId);
    }

    /**
     * @Author: ZhangYu
     * @Date:2019-11-13
     * @Description: java学习
     * @Question: 添加分类
     */
    @RequestMapping("add")
    public Result add(@RequestBody TbItemCat tbItemCat){
        try {
            itemCatService.add(tbItemCat);
            return new Result(true,"添加成功");
        }catch (Exception e){
            return new Result(false,"添加失败");
        }
    }

    /**
     * @Author: ZhangYu
     * @Date:2019-11-13
     * @Description: java学习
     * @Question:修改分类
     */

    @RequestMapping("update")
    public Result update(@RequestBody TbItemCat tbItemCat){
        try {
            itemCatService.update(tbItemCat);
            return new Result(true,"修改成功");
        }catch (Exception e){
            return new Result(false,"修改失败");
        }
    }

    /**
     * @Author: ZhangYu
     * @Date:2019-11-13
     * @Description: java学习
     * @Question:通过id查询一个分类
     */
    @RequestMapping("findOneById")
    public TbItemCat findOneById(long id){
        return itemCatService.findOneById(id);
    }

    /**
     * @Author: ZhangYu
     * @Date:2019-11-13
     * @Description: java学习
     * @Question: 删除通过ids数组
     */
    @RequestMapping("delete")
    public Result delete(Long[] ids){
        try {
            itemCatService.deleteByArrays(ids);
            return new Result(true,"删除成功");
        }catch (Exception e){
            return new Result(false,"删除失败");
        }

    }

    @RequestMapping("findAll")
    public List<TbItemCat> findAll(){
        return itemCatService.findAll();
    }
	
}
