package com.pinyougou.manager.controller;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.pojo.Result;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.sellergoods.service.GoodsService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.util.List;


/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private ActiveMQQueue goodsToSolrQueue;
    @Autowired
    private ActiveMQTopic genStaticHtml;


    @RequestMapping("findAll")
    public PageResult findAll(int pageIndex, int pageSize){
        return goodsService.findAll(pageIndex,pageSize);
    }

	@RequestMapping("updateStatus")
    public Result updateStatus(Long[] ids,String status, String statusField){
        try {
            goodsService.updateStatus(ids,status,statusField);
            for (Long id : ids) {
                final List<TbItem> items = goodsService.findItemListByGoodsIdandStatus(id, status);
                getStaticHtml(id);
                jmsTemplate.send(goodsToSolrQueue, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        String text = JSON.toJSONString(items);
                        TextMessage textMessage = session.createTextMessage(text);
                        return textMessage;
                    }
                });
            }
            return new Result(true,"操作成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"操作失败");

        }

    }
    private void getStaticHtml(final Long goodsId){
        jmsTemplate.send(genStaticHtml, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage textMessage = session.createTextMessage(goodsId+"");
                return textMessage;
            }
        });
    }
}
