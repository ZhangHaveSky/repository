package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.entity.PageResult;
import com.pinyougou.entity.Result;
import com.pinyougou.pojo.TbBrand;

import com.pinyougou.sellergoods.service.BrandService;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    @RequestMapping("/findAll.do")
    public PageResult findAll(@RequestParam(value = "page") Integer pageNum,@RequestParam(value = "size") Integer pageSize){
        return brandService.findAll(pageNum, pageSize);

    }
    @RequestMapping("/findById.do")
    public TbBrand findById(Long id){
        return brandService.findById(id);
    }
    @RequestMapping("/save.do")
    public Result save(@RequestBody TbBrand tbBrand ){
        Result result = new Result();
        Boolean b=null;
        if(tbBrand.getId()!=null){
              b=brandService.update(tbBrand);
              if(b){
                  result.setMessage("更新成功!");
              }else {
                  result.setMessage("更新失败!");
              }
          }else {
              b= brandService.insert(tbBrand);
            if(b){
                result.setMessage("添加成功!");
            }else {
                result.setMessage("添加失败!");
            }
          }
          result.setSuccess(b);
      return result;
    }
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        Result result = new Result();
        for (Long id : ids) {
            try {
                brandService.delete(id);
                result.setSuccess(true);
                result.setMessage("删除成功");
            } catch (Exception e) {
                e.printStackTrace();
                result.setSuccess(false);
                result.setMessage("删除失败");
            }
        }
        return  result;
    }
    @RequestMapping("/search.do")
    public PageResult search(@RequestBody TbBrand tbBrand,Integer page,Integer size){
        PageResult pageResult=null;
        if(tbBrand!=null){
            pageResult=brandService.search(tbBrand,page,size);
        }
        return pageResult;
    }
}

