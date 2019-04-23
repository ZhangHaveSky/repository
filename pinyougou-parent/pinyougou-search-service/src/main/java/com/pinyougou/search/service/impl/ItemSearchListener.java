package com.pinyougou.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.*;

@Component
public class ItemSearchListener implements MessageListener {
    @Autowired
    private ItemSearchServiceImpl searchService;

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            String text = textMessage.getText();
            searchService.importList(JSON.parseArray(text, TbItem.class));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
