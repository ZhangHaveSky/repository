package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.entity.Result;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utils.IdWorker;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private WeixinPayService payService;


    @Autowired
    private RedisTemplate redisTemplate;

    @Reference
    private OrderService orderService;

    @RequestMapping("/createNative")
    public Map createNative() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        TbPayLog payLog = (TbPayLog)redisTemplate.boundHashOps("payLog").get(name);
        if(payLog!=null){
            Map aNative = payService.createNative(payLog.getOutTradeNo(), payLog.getTotalFee()+"");

            return aNative;
        }else {
            return new HashMap();
        }
    }
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        Result result = null;

        while (true){
            int i=0;
            Map map = payService.queryPayStatus(out_trade_no);
            if(map==null){
                result=new Result(false, "支付发生错误");
                break;
            }
            if(map.get("trade_state").equals("SUCCESS")){
                result=new Result(true, "支付成功");
                orderService.updateOrderStatus(out_trade_no,(String)map.get("transaction_id"));
                break;
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
            if(i>=100){
                result=new Result(false, "二维码超时");
                break;
            }
        }
          return  result;
    }
}
