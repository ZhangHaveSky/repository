package com.pinyougou.pojogroup;

import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbSpecification;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class TypeTemplates implements Serializable{
    private List<Map> brands;
    private List<Map> specifications;

    public List<Map> getBrands() {
        return brands;
    }

    public void setBrands(List<Map> brands) {
        this.brands = brands;
    }

    public List<Map> getSpecifications() {
        return specifications;
    }

    public void setSpecifications(List<Map> specifications) {
        this.specifications = specifications;
    }
}
