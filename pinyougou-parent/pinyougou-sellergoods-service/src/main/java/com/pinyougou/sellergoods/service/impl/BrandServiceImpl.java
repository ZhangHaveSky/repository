package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.entity.PageResult;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.sellergoods.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {


    @Autowired
    TbBrandMapper brandMapper;

    @Override
    public PageResult findAll(Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize);
        Page<TbBrand> tbBrands = (Page<TbBrand>) brandMapper.selectByExample(null);
        long total = tbBrands.getTotal();
        List<TbBrand> result = tbBrands.getResult();

        return new PageResult(total, result);
    }

    @Override
    public Boolean update(TbBrand tbBrand) {
        int i = brandMapper.updateByPrimaryKey(tbBrand);
        if (i > 0) {
            return true;
        } else {
            return false;
        }


    }

    @Override
    public Boolean insert(TbBrand tbBrand) {
        int insert = brandMapper.insert(tbBrand);
        if (insert > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public TbBrand findById(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void delete(Long id) {
        brandMapper.deleteByPrimaryKey(id);
    }

    @Override
    public PageResult search(TbBrand tbBrand, Integer page, Integer size) {
        TbBrandExample example = new TbBrandExample();
        TbBrandExample.Criteria criteria = example.createCriteria();
        if (tbBrand.getFirstChar() != null) {
            criteria.andFirstCharLike("%" + tbBrand.getFirstChar() + "%");
        }
        if (tbBrand.getName() != null && tbBrand.getName().length() > 0) {
            criteria.andNameLike("%" + tbBrand.getName() + "%");
        }

        PageHelper.startPage(page, size);
        Page<TbBrand> tbBrands = (Page<TbBrand>) brandMapper.selectByExample(example);
        long total = tbBrands.getTotal();

        return new PageResult(total, tbBrands.getResult());
    }
}
