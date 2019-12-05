package com.pinyougou.search.listener;

import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.*;
@Component
public class GoodsDeleteFromSolrListener implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message) {

        try {
            //1.获得传过来的消息对象
            ObjectMessage objectMessage = (ObjectMessage) message;
            //2.进行数据转化
            Long[] ids = (Long[]) objectMessage.getObject();
            //3.调用执行删除
            itemSearchService.deleteFromSolr(ids);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
