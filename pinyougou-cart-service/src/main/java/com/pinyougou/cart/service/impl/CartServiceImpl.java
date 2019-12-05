package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.group.Cart;
import com.pinyougou.mapper.TbAddressMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojo.TbAddressExample;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private TbAddressMapper addressMapper;
    /**
     * @author: zhangyu
     * @date: 2019-12-02
     * @param: [username, cartList]
     * @return: void
     * 功能描述: 保存购物车列表到redis中
     */
    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        redisTemplate.boundHashOps("cartList").put(username,cartList);
    }

    /**
     * @author: zhangyu
     * @date: 2019-12-02
     * @param: [itemId, num, cartList]
     * @return: java.util.List<com.pinyougou.group.Cart>
     * 功能描述: 添加新sku商品到购物车中
     */
    @Override
    public List<Cart> addToCartList(Long itemId, int num, List<Cart> cartList) {
        //1.根据itemId获得item对象
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        //2.根据sellerId看该商品的商家购物车是否在购物车列表中
        Cart cart = findCartBySellerId(cartList,item.getSellerId());
        //3.如果不在则创建，在则添加
        if (cart == null){//不存在
            Cart newCart = CreateCart(item,num);
            cartList.add(newCart);
        }else {
            //4.判断orderItems中是否有该商品
            TbOrderItem orderItem = findOrderItemById(cart.getOrderItemList(),itemId);
            if (orderItem == null){//不存在则创建
                orderItem = createOrderItem(item,num);
                cart.getOrderItemList().add(orderItem);
            }else {
                orderItem.setNum(orderItem.getNum() + num); //修改数量
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue() * orderItem.getNum())); //修改小计
                //第三步：考虑到有可能是减少商品数量的情况，所以有如下可能
                //① 如果此商品的数量为0，则从此购物项列表中删除此商品
                if(orderItem.getNum() == 0){
                    cart.getOrderItemList().remove(orderItem);
                }
                //② 如果此商品的订单项的总个数为0，则从购物车列表删除此购物车
                if(cart.getOrderItemList().size() == 0){
                    cartList.remove(cart);
                }
            }
        }
        return cartList;
    }

    /**
     * @author: zhangyu
     * @date: 2019-12-02
     * @param: [orderItemList, itemId]
     * @return: com.pinyougou.pojo.TbOrderItem
     * 功能描述:  判断orderItems中是否有该商品
     */
    private TbOrderItem findOrderItemById(List<TbOrderItem> orderItemList, Long itemId) {
        for (TbOrderItem orderItem : orderItemList) {
            if (orderItem.getItemId().longValue() == itemId.longValue())return orderItem;
        }
        return null;
    }

    /**
     * @author: zhangyu
     * @date: 2019-12-02
     * @param: [item, num]
     * @return: com.pinyougou.group.Cart
     * 功能描述: 创建新购物车对象
     */
    private Cart CreateCart(TbItem item, int num) {
        //1.创建购物车对象
        Cart cart = new Cart();
        //2.给购物车对象设置值
        cart.setSellerId(item.getSellerId());
        cart.setSellerName(item.getSeller());
        List<TbOrderItem> orderItems = new ArrayList<>();
        TbOrderItem orderItem = createOrderItem(item,num);
        orderItems.add(orderItem);
        cart.setOrderItemList(orderItems);
        return cart;
    }

    /**
     * @author: zhangyu
     * @date: 2019-12-02
     * @param: [item, num]
     * @return: com.pinyougou.pojo.TbOrderItem
     * 功能描述: 将TbItem对象转换为OrderItem对象
     */
    private TbOrderItem createOrderItem(TbItem item, int num) {
        //4.1)创建订单项对象
        TbOrderItem orderItem = new TbOrderItem();
        //4.2)为订单项设置一系列的属性
        orderItem.setSellerId(item.getSellerId());
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setTitle(item.getTitle());
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num)); //小计
        //4.3)返回订单项对象
        return orderItem;
    }

    /**
     * @author: zhangyu
     * @date: 2019-12-02
     * @param: [cartList, sellerId]
     * @return: com.pinyougou.group.Cart
     * 功能描述: 通过商家id判断是否已存在此商家的购物车
     */
    private Cart findCartBySellerId(List<Cart> cartList, String sellerId) {
        if (cartList != null){
            for (Cart cart : cartList) {
                System.out.println(cart.getSellerId()+"se");
                System.out.println(sellerId+"aaa");
                if (cart.getSellerId().equals(sellerId))return cart;
            }
        }
        return null;
    }

    @Override
    public List<Cart> getCartListFronRedis(String username) {
        return (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
    }

    /**
     * @author: zhangyu
     * @date: 2019-12-02
     * @param: [cookieCartList, redisCartList]
     * @return: java.util.List<com.pinyougou.group.Cart>
     * 功能描述: 合并cookie中数据到redis中
     */
    @Override
    public List<Cart> mergeCartList(List<Cart> cookieCartList, List<Cart> redisCartList) {
        for (Cart cart : cookieCartList) {
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                addToCartList(orderItem.getItemId(),orderItem.getNum(),redisCartList);
            }
        }
        return redisCartList;
    }

    /**
     * @author: zhangyu
     * @date: 2019-12-04
     * @param: [username]
     * @return: java.util.List<com.pinyougou.pojo.TbAddress>
     * 功能描述: 查询该用户名下的收货地址
     */
    @Override
    public List<TbAddress> findAddressList(String username) {
        TbAddressExample example = new TbAddressExample();
        TbAddressExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(username);
        return addressMapper.selectByExample(example);
    }
}
