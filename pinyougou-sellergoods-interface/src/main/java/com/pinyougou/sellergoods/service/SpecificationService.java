package com.pinyougou.sellergoods.service;

import com.pinyougou.group.Specification;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.pojo.TbSpecification;

import java.util.List;
import java.util.Map;


/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SpecificationService {


    //分页查询，模糊查询
    PageResult search(int page, int pageSize, String key);

    //添加规格
    void add(Specification specification);

    //修改规格信息
    void update(Specification specification);

    //找出当前规格信息
    Specification findOne(Long id);

    List<Map> findAll();

    void delete(Long[] ids);
}
