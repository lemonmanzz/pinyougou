package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.TbSpecificationOption;
import com.pinyougou.pojo.TbSpecificationOptionExample;
import com.pinyougou.pojo.TbTypeTemplate;
import com.pinyougou.pojo.TbTypeTemplateExample;
import com.pinyougou.sellergoods.service.TypeTemplateService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Autowired
    private TbTypeTemplateMapper typeTemplateMapper;
    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;

    @Override
    public List<TbTypeTemplate> findAll() {
        return typeTemplateMapper.selectByExample(null);
    }

    /**
     * @Author: ZhangYu
     * @Date:2019-11-12
     * @Description: java学习
     * @Question:添加模板
     */
    @Override
    public void add(TbTypeTemplate template) {
        typeTemplateMapper.insert(template);
    }

    /**
     * @Author: ZhangYu
     * @Date:2019-11-12
     * @Description: java学习
     * @Question:查找一个模板通过id
     */
    @Override
    public TbTypeTemplate findOne(long id) {
        return typeTemplateMapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(TbTypeTemplate template) {
        typeTemplateMapper.updateByPrimaryKeySelective(template);
    }

    @Override
    public void delete(Long[] ids) {
        //删除
        TbTypeTemplateExample example = new TbTypeTemplateExample();
        TbTypeTemplateExample.Criteria criteria = example.createCriteria();
        criteria.andIdIn(Arrays.asList(ids));
        typeTemplateMapper.deleteByExample(example);
    }

    /**
     * @Author: ZhangYu
     * @Date:2019-11-14
     * @Description: java学习
     * @Question:通过模板id查询规格列表
     */

    @Override
    public List<Map> findSpecList(Long id) {
       try {
           //1.获得对应模板
           TbTypeTemplate tbTypeTemplate = typeTemplateMapper.selectByPrimaryKey(id);
           //2.获得模板的规格字段值
           String specIds = tbTypeTemplate.getSpecIds();
           //3.将规格字段转换成map
           List<Map> list = JSON.parseArray(specIds,Map.class);
           //4.遍历list添加options
           for (Map map : list) {

               //4.1根据外键查询规格选项
               TbSpecificationOptionExample example = new TbSpecificationOptionExample();
               TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
               criteria.andSpecIdEqualTo(new Long(map.get("id")+""));
               //4.2执行查询
               List<TbSpecificationOption> optionList = specificationOptionMapper.selectByExample(example);
               //4.3将规格选项与规格绑定
               map.put("options",optionList);
           }
           //返回list
           return list;
       }catch (Exception e){
           return null;
       }
    }
}
