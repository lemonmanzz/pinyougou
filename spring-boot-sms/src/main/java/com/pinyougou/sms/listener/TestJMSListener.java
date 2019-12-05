package com.pinyougou.sms.listener;

import com.aliyuncs.exceptions.ClientException;
import com.pinyougou.sms.utils.SmsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.annotation.JmsListeners;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TestJMSListener {
    @Autowired
    private SmsUtils smsUtils;

    @JmsListener(destination = "test")
    public void getMessage(String message){
        System.out.println(message);
    }
    @JmsListener(destination = "test1")
    public void get1(String s){
        System.out.println(s);
    }

    @JmsListener(destination = "validCode")
    public void getCodeMessage(Map map) throws ClientException {
        String phone = (String) map.get("phone");
        String signName = (String) map.get("signName");
        String templateCode = (String) map.get("templateCode");
        String param = (String) map.get("param");
        System.out.println(map);
        //向阿里大于发送验证码信息
        smsUtils.sendSms(phone,signName,templateCode,param);
    }
}
