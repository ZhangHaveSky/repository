package com.pinyougou.sellergoods.service;

import com.pinyougou.entity.PageResult;
import com.pinyougou.pojo.TbBrand;

import java.util.List;

public interface BrandService {
    /**
     * 返回全部列表
     *
     * @return
     * @param pageNum
     * @param pageSize
     */
    PageResult findAll(Integer pageNum, Integer pageSize);

    Boolean update(TbBrand tbBrand);

    Boolean insert(TbBrand tbBrand);

    TbBrand findById(Long id);

    void delete(Long id);

    PageResult search(TbBrand tbBrand, Integer page, Integer size);
}
