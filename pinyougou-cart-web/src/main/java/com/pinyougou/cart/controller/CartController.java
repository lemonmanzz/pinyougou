package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.group.Cart;
import com.pinyougou.pojo.Result;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;
    @Reference
    private CartService cartService;
    /**
     * @author: zhangyu
     * @date: 2019-12-02
     * @param: []
     * @return: java.util.List<com.pinyougou.group.Cart>
     * 功能描述: 获取购物车列表
     */
    @RequestMapping("getCartList")
    public List<Cart> getCartList(){
        //0.定义返回的购物车列表
        List<Cart> cartList = new ArrayList<>();
        //1.获得用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //2.判断用户名是否是anonymousUser
        if (username.equals("anonymousUser")){
            //2.1获得cookie中的购物车列表
            String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
            if (StringUtils.isNotBlank(cartListString)){
                cartList = JSON.parseArray(cartListString,Cart.class);
            }
        }else {
            //3.1拿到cookie中的购物车列表
            String cartListString = CookieUtil.getCookieValue(request, "cartList", "utf-8");
            //3.2获得redis中该用户的购物车
            List<Cart> cartListFronRedis = cartService.getCartListFronRedis(username);
            //防止购物车是空的情况下登录，加入购物车列表是null的情况
            if ( cartListFronRedis != null){
                cartList = cartListFronRedis;
            }
            //3.3判断cookie中是否有数据，有数据则合并
            if (StringUtils.isNotBlank(cartListString)){
                cartList = cartService.mergeCartList(JSON.parseArray(cartListString,Cart.class),cartList);
                //3.3.2删除cookie中的购物车数据
                CookieUtil.deleteCookie(request,response,"cartList");
                //3.3.3保存数据到redis中
                cartService.saveCartListToRedis(username,cartList);
            }
        }
        return cartList;
    }

    /**
     * @author: zhangyu
     * @date: 2019-12-02
     * @param: [itemId, num]，参数一表示sku表id，参数二表示购买的数量
     * @return: com.pinyougou.pojo.Result
     * 功能描述: 添加商品到购物车中
     */
    @RequestMapping("addToCartList")
    @CrossOrigin(origins = "http://localhost:9105",allowCredentials = "true")
    public Result addToCartList(Long itemId, int num){
        try {
            //1.获得商品列表
            List<Cart> cartList = getCartList();
            //2.新加入的商品添加到购物车中
           cartList =  cartService.addToCartList(itemId,num,cartList);
            //3.获得用户名
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            //4.判断是否登录
            if (username.equals("anonymousUser")){//未登录，放入cookie中
                CookieUtil.setCookie(request,response,"cartList",JSON.toJSONString(cartList),3600*24,"utf-8");
            }else {//将数据放入redis中
                cartService.saveCartListToRedis(username,cartList);
            }
            return new Result(true,"添加成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }

    /**
     * @author: zhangyu
     * @date: 2019-12-04
     * @param: []
     * @return: java.util.List<com.pinyougou.pojo.TbAddress>
     * 功能描述: 查询当前用户的收货地址
     */
    @RequestMapping("findAddressList")
    public List<TbAddress> findAddressList(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return cartService.findAddressList(username);
    }

}
