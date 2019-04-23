package com.pinyougou.search.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.Serializable;
import java.util.Arrays;

@Component
public class ItemDeleteListener implements MessageListener {
    @Autowired
    private ItemSearchServiceImpl searchService;

    @Override
    public void onMessage(Message message) {
        ObjectMessage objectMessage=(ObjectMessage)message;
        try {
            Long[] object = (Long[]) objectMessage.getObject();
            searchService.deleteByGoodsIds(Arrays.asList(object));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
