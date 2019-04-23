package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
        if(tbItem==null){
            throw new RuntimeException("商品不存在");
        }
        if(!tbItem.getStatus().equals("1")){
            throw new RuntimeException("商品状态不合法");
        }
        String sellerId = tbItem.getSellerId();
        Cart car = getCar(cartList, sellerId);
        if(car==null){
            car=new Cart();
        }
        List<TbOrderItem> orderItemList = car.getOrderItemList();
        if(orderItemList!=null){
            TbOrderItem tbOrderItem = searchOrderItemByItemId(itemId, orderItemList);
            if(tbOrderItem!=null){
                tbOrderItem.setNum(tbOrderItem.getNum()+num);
                tbOrderItem.setTotalFee(new BigDecimal(tbItem.getPrice().doubleValue()*tbOrderItem.getNum()));
                if(tbOrderItem.getNum()<=0){
                    orderItemList.remove(tbOrderItem);
                }
                if(orderItemList.size()==0){
                    cartList.remove(car);
                }
            }else {
                TbOrderItem orderItem = createOrderItem(tbItem, num);
                orderItemList.add(orderItem);
            }
        }else {
            car.setSellerId(sellerId);//商家ID
            car.setSellerName(tbItem.getSeller());//商家名称
            orderItemList=new ArrayList();//创建购物车明细列表
            TbOrderItem orderItem = createOrderItem(tbItem,num);
            orderItemList.add(orderItem);
            car.setOrderItemList(orderItemList);
            if(cartList==null){
                cartList=new ArrayList<Cart>();
            }
            cartList.add(car);
        }
        return  cartList;
    }


     @Autowired
     private RedisTemplate redisTemplate;


    @Override
    public List<Cart> findCartListFromRedis(String username) {
        System.out.println("从redis中提取购物车"+username);
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
        return cartList;
    }

    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        System.out.println("向redis中存入购物车"+username);
        redisTemplate.boundHashOps("cartList").put(username,cartList);
    }

    @Override
    public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
       if(cartList2==null){
          return cartList1;
       }
        for (Cart cart : cartList1) {
            Cart car = getCar(cartList2, cart.getSellerId());
            if(car==null){
                cartList2.add(car);
            }else {
                    for (TbOrderItem tbOrderItem : car.getOrderItemList()) {
                        cartList2= addGoodsToCartList(cartList2, tbOrderItem.getItemId(), tbOrderItem.getNum());
                    }
            }
        }
        return cartList2;
    }

    /**
     * 根据skuID在购物车明细列表中查询购物车明细对象
     * @param itemId
     * @param orderItemList
     * @return
     */
    private TbOrderItem searchOrderItemByItemId(Long itemId,  List<TbOrderItem> orderItemList) {
        for (TbOrderItem tbOrderItem : orderItemList) {
            if(tbOrderItem.getItemId().equals(itemId)){
               return tbOrderItem;
            }
        }
        return null;
    }

    /**
     * 根据商家ID在购物车列表中查询购物车对象
     * @param cartList
     * @param sellerId
     * @return
     */
    private Cart getCar(List<Cart> cartList, String sellerId) {
        if(cartList!=null&&cartList.size()>0){
            for (Cart cart : cartList) {
                    if(cart!=null&&sellerId.equals(cart.getSellerId())){
                        return  cart;
                    }

            }
        }
        return null;
    }

    /**
     * 创建购物车明细对象
     * @param item
     * @param num
     * @return
     */
    private TbOrderItem createOrderItem(TbItem item,Integer num){
        //创建新的购物车明细对象
        TbOrderItem orderItem=new TbOrderItem();
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setItemId(item.getId());
        orderItem.setNum(num);
        orderItem.setPicPath(item.getImage());
        orderItem.setPrice(item.getPrice());
        orderItem.setSellerId(item.getSellerId());
        orderItem.setTitle(item.getTitle());
        orderItem.setTotalFee( new BigDecimal(item.getPrice().doubleValue()*num) );
        System.out.println(orderItem.getTotalFee());
        return orderItem;
    }
}
