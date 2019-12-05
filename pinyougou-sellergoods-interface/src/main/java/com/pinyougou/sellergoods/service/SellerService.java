package com.pinyougou.sellergoods.service;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.pojo.TbSeller;

import java.util.List;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SellerService {

    void add(TbSeller seller);

    //通过
    TbSeller findSellerByid(String username);

    List<TbSeller> findAll();

    void updateStatus(String sellerId, String status);
}
