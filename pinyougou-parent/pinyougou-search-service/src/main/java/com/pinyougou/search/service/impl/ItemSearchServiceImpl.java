package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(timeout = 5000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {
        String replace = ((String) searchMap.get("keywords")).replace(" ", "");
        searchMap.put("keywords",replace);
        Map map = new HashMap<String, Object>();
        map.putAll(getRowsMap(searchMap));
        map.put("categorylist", searchCategoryList(searchMap));
        Long itemCat=null;
        if(!StringUtils.isEmpty(searchMap.get("category"))){
            itemCat = (Long) redisTemplate.boundHashOps("itemCat").get(searchMap.get("category"));
        }else {
            itemCat = (Long) redisTemplate.boundHashOps("itemCat").get(searchCategoryList(searchMap).get(0));
        }

        if (itemCat != null) {
            map.put("brandList", redisTemplate.boundHashOps("brandList").get(itemCat));
            map.put("specList", redisTemplate.boundHashOps("specList").get(itemCat));
        }

        return map;
    }

    @Override
    public void importList(List list) {
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    @Override
    public void deleteByGoodsIds(List goodsIdList) {
        System.out.println("从solr删除商品 ID"+goodsIdList);
        Query query=new SimpleQuery("*:*");
        Criteria criteria=new Criteria("item_goodsid").in(goodsIdList);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    private List searchCategoryList(Map searchMap) {
        Query query = new SimpleQuery();
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        //得到分组页
        GroupPage<TbItem> tbItems = solrTemplate.queryForGroupPage(query, TbItem.class);
        //根据列得到分组结果集
        GroupResult<TbItem> item_category = tbItems.getGroupResult("item_category");
        //得到分组结果入口页
        Page<GroupEntry<TbItem>> groupEntries = item_category.getGroupEntries();
        //得到分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        ArrayList<String> list = new ArrayList<>();
        for (GroupEntry<TbItem> tbItemGroupEntry : content) {
            //将分组结果的名称封装到返回值中
            list.add(tbItemGroupEntry.getGroupValue());
        }
        return list;
    }

    private Map<String, Object> getRowsMap(Map searchMap) {
        Map map = new HashMap<String, Object>();
        //高亮域
        HighlightOptions options = new HighlightOptions();
        //前缀
        options.setSimplePrefix("<em style='color:red'>");
        //后缀
        options.setSimplePostfix("</em>");
        //高亮选项初始化
        HighlightQuery query = new SimpleHighlightQuery();
        //为查询对象设置高亮选项
        query.setHighlightOptions(options);

        //关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //按品牌过滤
        if(!StringUtils.isEmpty(searchMap.get("category"))){
            FilterQuery filterquery=new SimpleFilterQuery();
            Criteria criteria1 = new Criteria("item_category").is(searchMap.get("category"));
            filterquery.addCriteria(criteria);
            query.addFilterQuery(filterquery);
        }

        if(!StringUtils.isEmpty(searchMap.get("brand"))){
            SimpleFilterQuery filterQuery = new SimpleFilterQuery();
            Criteria criteria1 = new Criteria("item_brand").is(searchMap.get("brand"));
            filterQuery.addCriteria(criteria1);
            query.addFilterQuery(filterQuery);
        }
        //按照规格过滤
        if(searchMap.get("spec")!=null&&((Map<String,String>)(searchMap.get("spec"))).size()>0){
            Map<String,String>specmap=(Map<String,String>)searchMap.get("spec");
            for (String key : specmap.keySet()) {
                SimpleFilterQuery filterQuery = new SimpleFilterQuery();
                Criteria criteria1 = new Criteria("item_spec_"+key).is(specmap.get(key));
                filterQuery.addCriteria(criteria1);
                query.addFilterQuery(filterQuery);
            }
        }
        //按价格过滤
        if(!StringUtils.isEmpty(searchMap.get("price"))){
            String[] prices = ((String) searchMap.get("price")).split("-");
            if(!"0".equals(prices[0])){
                SimpleFilterQuery filterQuery = new SimpleFilterQuery();
                Criteria criteria1 = new Criteria("item_price").greaterThanEqual(prices[0]);
                filterQuery.addCriteria(criteria1);
                query.addFilterQuery(filterQuery);
            }
            if(!"*".equals(prices[1])){
                SimpleFilterQuery filterQuery = new SimpleFilterQuery();
                Criteria criteria1 = new Criteria("item_price").lessThanEqual(prices[1]);
                filterQuery.addCriteria(criteria1);
                query.addFilterQuery(filterQuery);
            }
        }
        if(!StringUtils.isEmpty(searchMap.get("sort"))){
            if("asc".equals((String)searchMap.get("sort"))){
                Sort orders = new Sort(Sort.Direction.ASC,"item_"+(String)searchMap.get("sortField"));
               query.addSort(orders);
            }
            if("desc".equals((String)searchMap.get("sort"))){
                Sort orders = new Sort(Sort.Direction.DESC,"item_"+(String)searchMap.get("sortField"));
                query.addSort(orders);
            }
        }
        Integer pageNo =(Integer)searchMap.get("pageNo");
        Integer pageSize=(Integer)searchMap.get("pageSize");
        if(pageNo==null){
            pageNo=1;
        }
        if(pageSize==null){
            pageSize=20;
        }
        query.setOffset((pageNo-1)*pageSize);
        query.setRows(pageSize);
        //***********  获取高亮结果集  ***********
        //高亮页对象
        HighlightPage<TbItem> tbItems = solrTemplate.queryForHighlightPage(query, TbItem.class);
        //高亮入口集合(每条记录的高亮入口)
        List<HighlightEntry<TbItem>> highlighted = tbItems.getHighlighted();
        //entry=实体+高亮片段
        for (HighlightEntry<TbItem> entry : highlighted) {
            TbItem entity = entry.getEntity();
            if (entry.getHighlights().size() > 0 && entry.getHighlights().get(0).getSnipplets().size() > 0) {
                entity.setTitle(entry.getHighlights().get(0).getSnipplets().get(0));
            }
        }
        map.put("totalPages",tbItems.getTotalPages());//总页数
        map.put("total",tbItems.getTotalElements());//总
        map.put("rows", tbItems.getContent());
        return map;
    }
}
