package com.pinyougou.service.impl;

import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


import java.util.ArrayList;
import java.util.List;

public class UserDetailsServiceImpl implements UserDetailsService {

    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("ROLE_SELLER");
        List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
        list.add(simpleGrantedAuthority);
        TbSeller tbSeller = sellerService.findOne(s);
        User user =null;
        if(tbSeller!=null){
            if(tbSeller.getStatus().equals("1")){
                user= new User(s,tbSeller.getPassword(),list);
            }
        }
        return user;
    }
}
