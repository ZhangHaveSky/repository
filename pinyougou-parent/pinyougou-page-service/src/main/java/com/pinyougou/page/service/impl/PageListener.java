package com.pinyougou.page.service.impl;

import com.pinyougou.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.io.File;

@Component
public class PageListener implements MessageListener{

    @Autowired
    private ItemPageService service;
    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage=(ObjectMessage)message;
        try {
            Long text = (Long) objectMessage.getObject();
            service.genItemHtml(text);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
