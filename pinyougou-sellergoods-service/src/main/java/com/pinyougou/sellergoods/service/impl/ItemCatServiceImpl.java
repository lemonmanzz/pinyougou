package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbItemCatExample;
import com.pinyougou.sellergoods.service.ItemCatService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
public class ItemCatServiceImpl implements ItemCatService {

    @Autowired
    private TbItemCatMapper itemCatMapper;


    /**
     * @Author: ZhangYu
     * @Date:2019-11-13
     * @Description: java学习
     * @Question: 通过parentId查询分类
     *
     */
    @Override
    public List<TbItemCat> findByParentId(Long parentId) {
        //1.创建查询实实例
        TbItemCatExample example = new TbItemCatExample();
        TbItemCatExample.Criteria criteria = example.createCriteria();
        //2.设置查询条件
        criteria.andParentIdEqualTo(parentId);
        //3.执行查询并返回
        return itemCatMapper.selectByExample(example);
    }

    /**
     * @Author: ZhangYu
     * @Date:2019-11-13
     * @Description: java学习
     * @Question:添加分类
     */
    @Override
    public void add(TbItemCat tbItemCat) {
        itemCatMapper.insert(tbItemCat);
    }
    /**
     * @Author: ZhangYu
     * @Date:2019-11-13
     * @Description: java学习
     * @Question:修改分类
     */

    @Override
    public void update(TbItemCat tbItemCat) {

        itemCatMapper.updateByPrimaryKeySelective(tbItemCat);
    }

    /**
     * @Author: ZhangYu
     * @Date:2019-11-13
     * @Description: java学习
     * @Question:通过id查找分类
     */
    @Override
    public TbItemCat findOneById(long id) {
        return itemCatMapper.selectByPrimaryKey(id);
    }

    /**
     * @Author: ZhangYu
     * @Date:2019-11-13
     * @Description: java学习
     * @Question:通过id数组删除
     */
    @Override
    public void deleteByArrays(Long[] ids) {
        //2.调用删除方法
//        delete(longs);
        //有子分类则不删除
        for (Long id : ids) {
            TbItemCatExample example = new TbItemCatExample();
            TbItemCatExample.Criteria criteria = example.createCriteria();
            criteria.andParentIdEqualTo(id);
            List<TbItemCat> tbItemCats = itemCatMapper.selectByExample(example);
            if (tbItemCats == null || tbItemCats.size() == 0){
                itemCatMapper.deleteByPrimaryKey(id);
            }
        }
    }

    /**
     * @Description: 查询所有的分类
     * @Author: zhangyu
     * @Date: 2019/11/22
     * @Param: []
     * @Return: java.util.List<com.pinyougou.pojo.TbItemCat>
     **/
    @Override
    public List<TbItemCat> findAll() {
        return itemCatMapper.selectByExample(null);
    }

//    //递归删除多久分类目录
//    private void delete(List<Long> list){
//        //1.同id查询是否有下级目录
//        for (Long aLong : list) {
//            System.out.println(aLong);
//            //2.1创建查询实例
//            TbItemCatExample example = new TbItemCatExample();
//            TbItemCatExample.Criteria criteria = example.createCriteria();
//            //2.2设置查询条件
//            criteria.andParentIdEqualTo(aLong);
//            //2.3执行查询
//            List<TbItemCat> tbItemCats = itemCatMapper.selectByExample(example);
//            if (tbItemCats != null && tbItemCats.size() > 0){
//                List<Long> newList = new ArrayList<>();
//                for (TbItemCat tbItemCat : tbItemCats) {
//                    newList.add(tbItemCat.getId());
//                }
//                delete(newList);
//            }
//            itemCatMapper.deleteByPrimaryKey(aLong);
//
//        }
}

