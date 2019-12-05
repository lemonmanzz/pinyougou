package com.pinyougou.pay.service;

import java.util.Map;

public interface PayService {
    //1.向微信后台发出下单请求
    Map createNative(String out_trade_no, String money);

    //2.向后台发出查询订单的请求
    Map queryPayStatus(String out_trade_no);
}
