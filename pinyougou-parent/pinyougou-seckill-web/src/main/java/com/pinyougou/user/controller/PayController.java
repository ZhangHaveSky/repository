package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.entity.Result;
import com.pinyougou.pay.service.WeixinPayService;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Security;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {
    @Reference
    private SeckillOrderService seckillOrderService;
    @Reference
    private WeixinPayService weixinPayService;
    @RequestMapping("/createNative")
    public Map createNative(){
        //1.获取当前登录用户
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //2.提取秒杀订单（从缓存 ）
        TbSeckillOrder tbSeckillOrder = seckillOrderService.searchOrderFromRedisByUserId(name);
        //3.调用微信支付接口
        if(tbSeckillOrder!=null){
            return weixinPayService.createNative(tbSeckillOrder.getId()+"",(long)(tbSeckillOrder.getMoney().doubleValue()*100)+"" );
        }else{
            return new HashMap<>();
        }

    }
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        //1.获取当前登录用户
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Result result=null;
        int x=0;
        while(true){
            Map<String,String> map = weixinPayService.queryPayStatus(out_trade_no);//调用查询
            if(map==null){
                result=new Result(false, "支付发生错误");
                break;
            }
            if(map.get("trade_state").equals("SUCCESS")){//支付成功
                result=new Result(true, "支付成功");
                //保存订单
                seckillOrderService.saveOrderFromRedisToDb(username, Long.valueOf(out_trade_no) ,map.get("transaction_id"));
                break;
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            x++;
            if(x>=100){
                result=new Result(false, "二维码超时");
                Map<String,String> closemap = weixinPayService.closePay(out_trade_no);
                if(closemap!=null&&"FAIL".equals(closemap.get("return_code"))){
                    if("ORDERPAID".equals(closemap.get("err_code"))){
                        result=new Result(true, "支付成功");
                        seckillOrderService.saveOrderFromRedisToDb(username,Long.valueOf(out_trade_no),closemap.get("transaction_id"));
                    }
                }

                if(!result.isSuccess()){
                    seckillOrderService.deleteOrderFromRedis(username,Long.valueOf(out_trade_no));
                }
                break;
                }
            }
            return result;
        }
}
