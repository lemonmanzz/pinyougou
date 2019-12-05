package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.PayService;
import com.pinyougou.utils.HttpClient;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

@Service
public class PayServiceImpl implements PayService {
    //将从属性文件中读取属性信息
    @Value("${appid}")
    private String appid;
    @Value("${partner}")
    private String partner;
    @Value("${partnerkey}")
    private String partnerkey;
    @Value("${notifyurl}")
    private String notifyurl;

    //向微信后台下单的地址
    private String orderUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    //向微信后台查询订单的地址
    private String orderQueryUrl = "https://api.mch.weixin.qq.com/pay/orderquery";
    //1.向微信后台发出下单请求
    @Override
    public Map createNative(String out_trade_no, String money) {
        try {
            //1.1)包装要传到后台的数据
            //1.1.1)定义要包装的map集合
            Map paramsMap = new HashMap();
            paramsMap.put("appid",appid);                   //公众账号ID
            paramsMap.put("mch_id",partner);                //商户号
            paramsMap.put("nonce_str",WXPayUtil.generateNonceStr()); //生成随机字符串
            paramsMap.put("body","品优购");                   //商品描述
            paramsMap.put("out_trade_no",out_trade_no);      //商户订单号
            paramsMap.put("total_fee",money);                //订单金额(以分为单位)
            paramsMap.put("spbill_create_ip", "127.0.0.1");  //终端IP
            paramsMap.put("notify_url",notifyurl);           //通知地址
            paramsMap.put("trade_type","NATIVE");           //交易类型
            //1.1.2)根据数据及秘钥生成签名并将原来的map转换为xml这种字符串数据
            String signedXml = WXPayUtil.generateSignedXml(paramsMap, partnerkey);

            //1.2)向后台发出下单请求
            HttpClient httpClient = new HttpClient(orderUrl);
            httpClient.setHttps(true);          //代表发出的http请求
            httpClient.setXmlParam(signedXml);  //设置请求参数
            httpClient.post();                  //向后台发出请求

            //1.3)从微信后台得到返回的数据
            //1.3.1)从后台得到返回的xml数据
            String content = httpClient.getContent();
            //1.3.2)将xml这种数据转换为map
            Map<String, String> xmlMap = WXPayUtil.xmlToMap(content);
            //1.3.3)构造要返回的map
            Map resultMap = new HashMap();
            resultMap.put("out_trade_no",out_trade_no);         //包装订单号
            resultMap.put("total_fee",money);                   //包装金额
            resultMap.put("code_url",xmlMap.get("code_url"));   //包装返回的响应地址（用它生成二维码）

            //1.4)返回
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap();
    }
    //2.向后台发出查询订单的请求
    @Override
    public Map queryPayStatus(String out_trade_no) {
        try {
            //2.1)封装要传到后台的参数
            Map params = new HashMap();
            params.put("appid",appid);                   //公众账号ID
            params.put("mch_id",partner);                //商户号
            params.put("out_trade_no",out_trade_no);     //订单号
            params.put("nonce_str",WXPayUtil.generateNonceStr()); //生成随机字符串
            //转换map为xml并同时得到签名
            String signedXml = WXPayUtil.generateSignedXml(params, partnerkey);

            //2.2)发出查询订单的请求
            HttpClient httpClient = new HttpClient(orderQueryUrl);
            httpClient.setHttps(true);          //代表发出的http请求
            httpClient.setXmlParam(signedXml);  //设置请求参数
            httpClient.post();                  //向后台发出请求

            //2.3)返回查询订单的结果
            //2.3.1)得到xml数据
            String content = httpClient.getContent();
            //2.3.2)将xml数据转换为map
            Map<String, String> paramMap = WXPayUtil.xmlToMap(content);

            //2.4)返回
            return paramMap;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap();
    }
}