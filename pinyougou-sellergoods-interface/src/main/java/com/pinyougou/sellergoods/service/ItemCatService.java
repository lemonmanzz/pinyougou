package com.pinyougou.sellergoods.service;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.pojo.Result;
import com.pinyougou.pojo.TbItemCat;

import java.util.List;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface ItemCatService {


    /**
     * @Author: ZhangYu
     * @Date:2019-11-13
     * @Description: java学习
     * @Question:通过parentId查找
     */
    List<TbItemCat> findByParentId(Long parentId);

    void add(TbItemCat tbItemCat);

    void update(TbItemCat tbItemCat);

    TbItemCat findOneById(long id);

    void deleteByArrays(Long[] ids);

    List<TbItemCat> findAll();
}
