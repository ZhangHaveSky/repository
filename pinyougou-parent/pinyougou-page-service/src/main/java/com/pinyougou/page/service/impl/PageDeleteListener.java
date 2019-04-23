package com.pinyougou.page.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.jms.*;
import java.io.File;

@Component
public class PageDeleteListener implements MessageListener {

    @Value("${pagedir}")
    String url;

    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage = (ObjectMessage) message;
        try {
            Long[] ids = (Long[]) objectMessage.getObject();

            System.out.println("删除了网页"+ids);
            for (Long id : ids) {
                new File(url + id + ".html").delete();
            }

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
