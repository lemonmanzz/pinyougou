package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.sellergoods.service.SpecificationOptionService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class SpecificationOptionServiceImpl implements SpecificationOptionService {


    @Autowired
    private  TbSpecificationOptionMapper specificationOptionMapper ;

    @Override
    public List<TbSpecificationOption> findBySpecId(Long specId) {

        TbSpecificationOptionExample example = new TbSpecificationOptionExample();
        TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
        criteria.andSpecIdEqualTo(specId);
        return specificationOptionMapper.selectByExample(example);
    }
}
