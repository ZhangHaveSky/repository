package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.entity.Result;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utils.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    /**
     * 购物车列表
     *
     * @return
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList() {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Cart> list = null;
        String cartList = CookieUtil.getCookieValue(request, "cartList", "utf-8");

        if (cartList == null || cartList.equals("")) {
            cartList = "[]";
        }
        list = JSON.parseArray(cartList, Cart.class);
        if ("anonymousUser".equals(name)) {
            return list;
        } else {
            List<Cart> cartListFromRedis = cartService.findCartListFromRedis(name);
            if(list.size()>0){
                cartListFromRedis = cartService.mergeCartList(list, cartListFromRedis);
                cartService.saveCartListToRedis(name, cartListFromRedis);
                CookieUtil.deleteCookie(request, response, "cartList");
                System.out.println("执行了合并购物车的逻辑");
            }
            return cartListFromRedis;
        }
    }

    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins ="http://localhost:9105",allowCredentials = "true")
    public Result addGoodsToCartList(Long itemId, Integer num) {
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("当前登录人：" + name);
        try {
            List<Cart> cartList = findCartList();
            List<Cart> list = cartService.addGoodsToCartList(cartList, itemId, num);
            if (name.equals("anonymousUser")) {
                CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(list), 3600 * 24, "utf-8");
                System.out.println("向cookie存储购物车");
            } else {
                cartService.saveCartListToRedis(name, list);
                System.out.println("向redis存储购物车");
            }
            return new Result(true, "存入购物车成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "存入购物车失败");
        }

    }
}
