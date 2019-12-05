package com.pinyougou.sms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("sms")
public class testJMS {
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @GetMapping("send")
    public String sendMessage(){
        String s = "今天吃什么?";
        jmsTemplate.send("test",new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    TextMessage textMessage = session.createTextMessage(s);
                    return textMessage;
                }
        });
        return s;
    }

    @GetMapping("send1")
    public String send1(){
        String s = "好难过啊";
        jmsMessagingTemplate.convertAndSend("test1",s);
        return s;
    }
}
