package com.pinyougou.order.service;

import com.pinyougou.pojo.PageResult;
import com.pinyougou.pojo.TbOrder;

import java.util.List;

public interface OrderService {
    List<TbOrder> findAll();

    PageResult findPage(int page, int rows);

    void add(TbOrder order);

    void update(TbOrder order);

    TbOrder findOne(Long id);

    void delete(Long[] ids);

}
