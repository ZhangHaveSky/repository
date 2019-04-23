package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.pay.service.WeixinPayService;
import org.springframework.beans.factory.annotation.Value;
import utils.HttpClient;
import utils.IdWorker;

import java.util.HashMap;
import java.util.Map;

@Service
public class WeixinPayServiceImpl implements WeixinPayService {

    @Value("${appid}")
    private String appid;
    @Value("${partner}")
    private String partner;
    @Value("${partnerkey}")
    private String partnerkey;
    @Value("${notifyurl}")
    private String notifyurl;

    @Override
    public Map createNative(String out_trade_no, String total_fee) {
        HashMap map = new HashMap();
        map.put("appid",appid);
        map.put("mch_id",partner);
        map.put("nonce_str", WXPayUtil.generateNonceStr());
        map.put("body", "品优购");
        map.put("out_trade_no", out_trade_no);//交易订单号
        map.put("total_fee", total_fee);//金额（分
        map.put("spbill_create_ip", "127.0.0.1");//终端ip
        map.put("notify_url", "http://www.itcast.cn");//通知地址
        map.put("trade_type", "NATIVE");//交易类型

        try {
            //发送请求
            String xmlParam = WXPayUtil.generateSignedXml(map, partnerkey);
            System.out.println("请求的参数："+xmlParam);
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);
            httpClient.post();

            //3.获取结果
            String content = httpClient.getContent();
            Map<String, String> stringStringMap = WXPayUtil.xmlToMap(content);
            HashMap map1 = new HashMap();
            map1.put("code_url",stringStringMap.get("code_url"));
            map1.put("out_trade_no",out_trade_no);
            map1.put("total_fee",total_fee);
            return map1;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap();

        }

    }

    @Override
    public Map queryPayStatus(String out_trade_no) {
        //1.封装参数
        Map param=new HashMap();
        param.put("appid", appid);
        param.put("mch_id", partner);
        param.put("out_trade_no", out_trade_no);
        param.put("nonce_str", WXPayUtil.generateNonceStr());
        try {
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);
            httpClient.post();

            String content = httpClient.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(content);
            System.out.println("调动查询API返回结果："+content);
            return map;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map closePay(String out_trade_no) {
        //1.封装参数
        Map param=new HashMap();
        param.put("appid", appid);
        param.put("mch_id", partner);
        param.put("out_trade_no", out_trade_no);
        param.put("nonce_str", WXPayUtil.generateNonceStr());
        try {
            String xmlParam = WXPayUtil.generateSignedXml(param, partnerkey);
            //2.发送请求
            HttpClient httpClient=new HttpClient("https://api.mch.weixin.qq.com/pay/closeorder");
            httpClient.setHttps(true);
            httpClient.setXmlParam(xmlParam);
            httpClient.post();

            //3.获取结果
            String xmlResult = httpClient.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(xmlResult);
            System.out.println("调动查询API返回结果："+xmlResult);

            return map;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
}
