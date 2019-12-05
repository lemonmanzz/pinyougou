package com.pinyougou.sellergoods.service;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.pojo.TbTypeTemplate;

import java.util.List;
import java.util.Map;


/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface TypeTemplateService {

    /**
     * @Author: ZhangYu
     * @Date:2019-11-12
     * @Description: java学习
     * @Question: 查询所有的模板
     */
    List<TbTypeTemplate> findAll();

    /**
     * @Author: ZhangYu
     * @Date:2019-11-12
     * @Description: java学习
     * @Question:添加模板
     */
    void add(TbTypeTemplate template);

    /**
     * @Author: ZhangYu
     * @Date:2019-11-12
     * @Description: java学习
     * @Question:查询一个模板
     */
    TbTypeTemplate findOne(long id);

    /**
     * @Author: ZhangYu
     * @Date:2019-11-12
     * @Description: java学习
     * @Question:修改模板
     */
    void update(TbTypeTemplate template);

    /**
     * @Author: ZhangYu
     * @Date:2019-11-12
     * @Description: java学习
     * @Question:删除方法
     */
    void delete(Long[] ids);

    /**
     * @Author: ZhangYu
     * @Date:2019-11-14
     * @Description: java学习
     * @Question:通过模板id查询规格列表
     */
    List<Map> findSpecList(Long id);
}
