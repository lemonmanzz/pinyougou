package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.group.Specification;
import com.pinyougou.mapper.TbSpecificationMapper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.*;
import com.pinyougou.sellergoods.service.SpecificationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Service
public class SpecificationServiceImpl implements SpecificationService {
    //注入TbSpecificationMapper
    @Autowired
    private TbSpecificationMapper tbSpecificationMapper;

    //注入TbSpecificationOptionMapper
    @Autowired
    private TbSpecificationOptionMapper tbSpecificationOptionMapper;




    @Override
    public PageResult search(int page, int pageSize, String key) {
        //1. 开始分页
        PageHelper.startPage(page,pageSize);
        //2.开始查询
        //2.1 设置查询条件
        TbSpecificationExample example = new TbSpecificationExample();
        TbSpecificationExample.Criteria criteria = example.createCriteria();
        if (key != null) criteria.andSpecNameLike("%"+key+"%");
        //2.3执行查询得到结果
        List<TbSpecification> tbSpecifications = tbSpecificationMapper.selectByExample(example);
        //3.转换成Page对象
        Page<TbSpecification> pageList = (Page<TbSpecification>) tbSpecifications;
        //4.创建PageResult对象
        PageResult pageResult = new PageResult(pageList.getTotal(),pageList.getResult());
        return pageResult;
    }

    //添加规格
    @Override
    public void add(Specification specification) {
        //1.添加规格
        tbSpecificationMapper.insert(specification.getSpec());
        //2.获得id
        Long id = specification.getSpec().getId();
        //3.添加规格选项
        for (TbSpecificationOption option : specification.getSpecificationOptionList()) {
            option.setSpecId(id);
            //4.向数据库中添加规格选项
            tbSpecificationOptionMapper.insert(option);
        }
    }

    //修改规格

    @Override
    public void update(Specification specification) {
        //1.修改规格
        tbSpecificationMapper.updateByPrimaryKey(specification.getSpec());
        //2.修改规格选项
        //2.1 删除该规格所有规格选项
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(specification.getSpec().getId());
        tbSpecificationOptionMapper.deleteByExample(example);

        //2.2 添加修改后的规格到数据库
        for (TbSpecificationOption option : specification.getSpecificationOptionList()) {
            option.setSpecId(specification.getSpec().getId());
            tbSpecificationOptionMapper.insert(option);
        }
    }

    //查询某个规格的所有信息，即所有规格选项
    @Override
    public Specification findOne(Long id) {
        //1.查询规格信息
        TbSpecification tbSpecification = tbSpecificationMapper.selectByPrimaryKey(id);
        //2.查询该规格的所有规格选项
        //2.1获得查询用例
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(id);
        //2.2得到查询结果
        List<TbSpecificationOption> tbSpecificationOptions = tbSpecificationOptionMapper.selectByExample(example);
        //3.创建Specification对象
        Specification specification = new Specification();
        //4.使用set进行赋值
        specification.setSpec(tbSpecification);
        specification.setSpecificationOptionList(tbSpecificationOptions);
        //5.返回
        return specification;
    }


    //查询所有
    @Override
    public List<Map> findAll() {
        return tbSpecificationMapper.findSpecList();
    }


    //按照数组进行删除
    @Override
    public void delete(Long[] ids) {
        //创建删除条件
        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdIn(Arrays.asList(ids));
        //按照规格id删除规格选项
        tbSpecificationOptionMapper.deleteByExample(example);

        //删除规格通过id
        TbSpecificationExample example1 = new TbSpecificationExample();
        TbSpecificationExample.Criteria criteria1 = example1.createCriteria();
        criteria1.andIdIn(Arrays.asList(ids));
        tbSpecificationMapper.deleteByExample(example1);
    }

}
