package com.pinyougou.search.listener;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

@Component
public class GoodsToSolrListener implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message) {
        try {
            //1.获取TextMessage对象
            TextMessage textMessage = (TextMessage) message;
            //2.获得传过来的字符串
            String text = textMessage.getText();
            //3.将JSon字符串转换为java对象
            List<TbItem> items = JSON.parseArray(text, TbItem.class);
            //4.调用searchService将数据加入索引库
            itemSearchService.importDataToSolr(items);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
