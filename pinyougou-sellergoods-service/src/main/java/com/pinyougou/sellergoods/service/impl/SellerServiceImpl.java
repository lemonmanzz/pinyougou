package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbSellerMapper;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class SellerServiceImpl implements SellerService {
    @Autowired
    private TbSellerMapper sellerMapper;


    /**
     * @Author: ZhangYu
     * @Date:2019-11-12
     * @Description: java学习
     * @Question:添加商家，即注册
     */
    @Override
    public void add(TbSeller seller) {
        sellerMapper.insert(seller);
    }

    @Override
    public TbSeller findSellerByid(String username) {
        return sellerMapper.selectByPrimaryKey(username);
    }

    @Override
    public List<TbSeller> findAll() {
        return sellerMapper.selectByExample(null);
    }

    @Override
    public void updateStatus(String sellerId, String status) {
        TbSeller seller = sellerMapper.selectByPrimaryKey(sellerId);
        seller.setStatus(status);
        sellerMapper.updateByPrimaryKey(seller);
    }
}
