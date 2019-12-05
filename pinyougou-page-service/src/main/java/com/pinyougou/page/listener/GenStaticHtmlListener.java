package com.pinyougou.page.listener;

import com.pinyougou.page.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@Component
public class GenStaticHtmlListener implements MessageListener {
    @Autowired
    private PageService pageService;
    @Override
    public void onMessage(Message message) {

        try {
            TextMessage textMessage = (TextMessage) message;
            String text = textMessage.getText();
            pageService.genStaticItemHtml(Long.parseLong(text));
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }
}
