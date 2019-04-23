package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.entity.PageResult;
import com.pinyougou.mapper.TbUserMapper;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.util.DigestUtils;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    JmsTemplate jmsTemplate;
    @Autowired
    Environment env;

    @Value("${template_code}")
    private String template_code;
    @Value("${sign_name}")
    private String sign_name;
    @Autowired
    private ActiveMQQueue smsDestination;
    @Autowired
    private TbUserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<TbUser> findAll() {
        return null;
    }

    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        return null;
    }

    @Override
    public Boolean add(TbUser user, String smscode) {
        String codel = (String) redisTemplate.boundHashOps("smscode").get(user.getPhone());
        if (codel.equals(smscode)) {
            user.setCreated(new Date());
            user.setUpdated(new Date());
            user.setSourceType("1");
            user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
            userMapper.insert(user);
            return true;
        } else {
            return false;
        }

    }

    @Override
    public void update(TbUser user) {

    }

    @Override
    public TbUser findOne(Long id) {
        return null;
    }

    @Override
    public void delete(Long[] ids) {

    }

    @Override
    public PageResult findPage(TbUser user, int pageNum, int pageSize) {
        return null;
    }

    @Override
    public void createSmsCode(final String phone) {
        final String code = (long) (Math.random() * 1000000) + "";
        System.out.println("验证码:"+code);
        redisTemplate.boundHashOps("smscode").put(phone, code);
        jmsTemplate.send(smsDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("mobile", phone);//手机号
                mapMessage.setString("template_code", template_code);//模板编号
                try {
                    mapMessage.setString("sign_name",new String(sign_name.getBytes("ISO-8859-1"),"utf-8"));//签名
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                System.out.println("模板编号:"+env.getProperty("template_code"));
                System.out.println("sign_name:"+sign_name);
                HashMap map = new HashMap();
                map.put("number", code);
                mapMessage.setString("param", JSON.toJSONString(map));
                return mapMessage;
            }
        });

    }

    @Override
    public void checkSmsCode(final String phone, final String code) {


    }
}
