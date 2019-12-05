package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbUserMapper;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.pojo.TbUserExample;
import com.pinyougou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
@Service
public class UserServiceImpl implements UserService {
        @Autowired
        private TbUserMapper userMapper;
        @Autowired
        private JmsTemplate jmsTemplate;
        @Autowired
        private RedisTemplate redisTemplate;
        @Override
        public boolean hasUserName(String userName) {
            System.out.println(userName);
            TbUserExample example = new TbUserExample();
            TbUserExample.Criteria criteria = example.createCriteria();
            criteria.andUsernameEqualTo(userName);
            return !(userMapper.selectByExample(example).size() > 0) ;
        }

        @Override
        public void getValidCode(final String phone) {

            //1.得到验证码
            final String code = (long)(Math.random()*1000000)+"";
            System.out.println("code = " + code);
            //2.将验证码放入redis中
            redisTemplate.boundHashOps("validCode").put(phone,code);
            redisTemplate.expire(phone,5L,TimeUnit.MINUTES);
            //3.为springboot服务发送验证码消息
            jmsTemplate.send("validCode", new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    MapMessage mapMessage = session.createMapMessage();
                    mapMessage.setString("phone",phone);
                    mapMessage.setString("signName","品优购");
                    mapMessage.setString("templateCode","SMS_178761398");
                    HashMap hashMap = new HashMap();
                    hashMap.put("code",code);
                    System.out.println("hashMap = " + hashMap);
                    String param = JSON.toJSONString(hashMap);
                    System.out.println(param);
                    mapMessage.setString("param",param);
                    return mapMessage;
                }
            });

        }

        @Override
        public boolean checkValidCode(String phone, String validCode) {
            String code = (String) redisTemplate.boundHashOps("validCode").get(phone);
            if (code.equals(validCode)){
                return true;
            }
            return false;
        }

        @Override
        public void add(TbUser user) {
            user.setCreated(new Date());
            user.setUpdated(new Date());
            userMapper.insert(user);
        }
}
