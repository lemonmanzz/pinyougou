package com.pinyougou.sellergoods.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by WF on 2019-11-07 11:21
 */
@Service
public class BrandServiceImpl implements BrandService {
    @Autowired
    private TbBrandMapper brandMapper;
    @Override
    public List<TbBrand> findAll() {
        return brandMapper.selectByExample(null);
    }

    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        return null;
    }

    //添加一个品牌信息
    @Override
    public void add(TbBrand brand) {
        brandMapper.insert(brand);
    }

    @Override
    public void update(TbBrand brand) {
        brandMapper.updateByPrimaryKeySelective(brand);
    }

    @Override
    public TbBrand findOne(Long id) {
        return null;
    }

    @Override
    public void delete(Long[] ids) {
        if (ids != null && ids.length != 0){
            TbBrandExample example = new TbBrandExample();
            example.createCriteria().andIdIn(Arrays.asList(ids));
            brandMapper.deleteByExample(example);
        }
    }

    @Override
    public PageResult findPage(TbBrand brand, int pageNum, int pageSize) {
        return null;
    }

    @Override
    public List<Map> findBrandList() {
        return brandMapper.selectBrandList();
    }

    /**
     *
     * 分页查询品牌 并且带有条件查询
     * @return
     */
    @Override
    public PageResult search(int page, int pageSize, TbBrand brand) {
        //1.开始分页
        PageHelper.startPage(page,pageSize);
        //2.创建查询实例
        TbBrandExample example = new TbBrandExample();
        //3.创建查询条件
        TbBrandExample.Criteria criteria = example.createCriteria();
        //4.创设置查询条件
        if (brand != null){
            //4.1判断名字是否为空，不为空则进行模糊查询
            if (StringUtils.isNotBlank(brand.getName())){
                criteria.andNameLike("%"+brand.getName()+"%");
            }
            //4.2判断首字母是否为空，不为空则添加相等的条件
            if (StringUtils.isNotBlank(brand.getFirstChar())){
                criteria.andFirstCharEqualTo(brand.getFirstChar());
            }
        }
        //5.进行查询，得到查询后的结果
        List<TbBrand> brands = brandMapper.selectByExample(example);
        //6.转换为Page对象
        Page<TbBrand> brandPage = (Page<TbBrand>) brands;
        //7.将查询结果封装为PageResult的对象
        PageResult pageResult = new PageResult(brandPage.getTotal(), brandPage.getResult());
        //8.返回查询对象
        return pageResult;
    }
}
