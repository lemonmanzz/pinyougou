package com.pinyougou.cart.service;

import com.pinyougou.group.Cart;
import com.pinyougou.pojo.TbAddress;

import java.util.List;

public interface CartService {
    void saveCartListToRedis(String username, List<Cart> cartList);

    List<Cart> addToCartList(Long itemId, int num, List<Cart> cartList);

    List<Cart> getCartListFronRedis(String username);

    List<Cart> mergeCartList(List<Cart> cookieCartList, List<Cart> redisCartList);

    List<TbAddress> findAddressList(String username);
}
