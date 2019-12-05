package com.pinyougou.search.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbSpecificationOptionMapper;
import com.pinyougou.mapper.TbTypeTemplateMapper;
import com.pinyougou.pojo.*;
import com.pinyougou.search.service.ItemSearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
public class ItemServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbTypeTemplateMapper typeTemplateMapper;
    @Autowired
    private TbSpecificationOptionMapper specificationOptionMapper;
    /**
     * @author: zhangyu
     * @date: 2019-11-26
     * @param: [data]
     * @return: void
     * 功能描述: 将sku商品列表放入索引库中
     */
    @Override
    public void importDataToSolr(List data){
        //1.导入数据到索引库
        solrTemplate.saveBeans(data);
        //2.提交
        solrTemplate.commit();
        //3.打印提示
        System.out.println("导入索引库成功");
    }

    /**
     * @author: zhangyu
     * @date: 2019-11-26
     * @param: [ids]
     * @return: void
     * 功能描述: 通过商品id删除对应的sku商品列表的索引
     */
    @Override
    public void deleteFromSolr(Long[] ids){
        //1.创建一个查询对象
        SimpleQuery query = new SimpleQuery();
        //2.添加查询条件
        query.addCriteria(new Criteria("item_goodsid").in(ids));
        //3.删除指定商品id集合索引
        solrTemplate.delete(query);
        //4.提交
        solrTemplate.commit();
        //5.打印提示
        System.out.println("从索引库删除成功");
    }



    /**
     * @author: zhangyu
     * @date: 2019-11-20
     * @param: [searchMap, pageIndex, pageSize]
     * @return: java.util.Map<java.lang.String, java.lang.Object>
     * 功能描述: 对商品进行关键字分页搜索，返回分页结果集
     */
    @Override
    public Map<String, Object> search(Map searchMap) {
        //1.创建一个map用于返回
        HashMap<String, Object> map = new HashMap<>();
        //2.高亮查询
        map.putAll(highLightSearch(searchMap));
        //3.分组查询，将结果通过分类进行分组，返回所有分类集合
        List<String> categoryList = getGroupCategoryList(searchMap);
        map.put("categoryList",categoryList);
        //4.查询是否设置分类查询
        Map brandAndSpecList = null;
        Object category = searchMap.get("category");
        if (StringUtils.isNotBlank(category+"")){
            brandAndSpecList = getBrandAndSpecList(category.toString());
        }else if (categoryList != null && categoryList.size() > 0){
            brandAndSpecList = getBrandAndSpecList(categoryList.get(0));
        }
        map.putAll(brandAndSpecList);
        //.返回查询结果
        return map;
    }
    /**
     * @author: zhangyu
     * @date: 2019-11-21
     * @param: [category]
     * @return: java.util.Map
     * 功能描述: 通过一个分类名称查询其对应的品牌列表和规格列表
     */
    private Map getBrandAndSpecList(String category){
        //1.创建一个返回结果集Map
        HashMap map = new HashMap();
        //2.通过分类名称获取模板id
        //2.1从redis缓存库中查询
        Long typeId = (Long) redisTemplate.boundHashOps("itemCatList").get(category);
        //2.2缓存库中是否有分类名称与模板id的映射
        if (typeId == null){//2.2.1 表示没有放入缓存库，那么就查询数据库，并将结果放入缓存库中
            //2.2.2按照分类名称进行查询
            TbItemCatExample example = new TbItemCatExample();
            TbItemCatExample.Criteria criteria = example.createCriteria();
            criteria.andNameEqualTo(category);
            List<TbItemCat> tbItemCats = itemCatMapper.selectByExample(example);
            //2.2.3判断查询结果是否为空
            if (tbItemCats != null && tbItemCats.size() > 0){
                String itemCatName = tbItemCats.get(0).getName();
                typeId = tbItemCats.get(0).getTypeId();
                //2.2.4 将查询结果按照 key-value 为 分类名称-模板id 的格式放入redis中
                redisTemplate.boundHashOps("itemCatList").put(itemCatName,typeId);
                //设置过期时间
                redisTemplate.expire(itemCatName,1L,TimeUnit.MINUTES);
            }
        }
        //3.通过模板id在缓存中查询品牌列表和规格列表
        List<String> brandList = (List<String>) redisTemplate.boundHashOps("brandList").get(typeId);
        //3.1判断是否能从缓存中查询到结果
        if (brandList == null  ){
            brandList = new ArrayList<>();
            //3.2为空，查询数据库得到品牌列表
            TbTypeTemplate typeTemplate = typeTemplateMapper.selectByPrimaryKey(typeId);
            String brandIds = typeTemplate.getBrandIds();
            List<Map> brandIdsObject = JSON.parseArray(brandIds, Map.class);
            for (Map map1 : brandIdsObject) {
                brandList.add(map1.get("text")+"");
            }
            redisTemplate.boundHashOps("brandList").put(typeId,brandList);

            redisTemplate.expire("brandList",1L, TimeUnit.DAYS);

            redisTemplate.expire("brandList",1L, TimeUnit.MINUTES);

            System.out.println("品牌列表查询了数据库");
        }
        //4.1获取缓存中规格列表
        List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeId);
        //4.2 判断是否为空
        if (specList == null){
            specList = new ArrayList<>();
            //4.3查询数据库得到规格列表
            TbTypeTemplate typeTemplate = typeTemplateMapper.selectByPrimaryKey(typeId);
            String specIds = typeTemplate.getSpecIds();
            List<Map> array = JSON.parseArray(specIds, Map.class);
            for (Map map1 : array) {
                //4.4获得规格id
                long id = (long)(int)  map1.get("id");
                //4.5获得规格名称
                String text = (String) map1.get("text");
                //4.6通过规格id查询对应的规格选项
                TbSpecificationOptionExample tbSpecificationOptionExample = new TbSpecificationOptionExample();
                TbSpecificationOptionExample.Criteria criteria = tbSpecificationOptionExample.createCriteria();
                criteria.andSpecIdEqualTo(id);
                List<TbSpecificationOption> options = specificationOptionMapper.selectByExample(tbSpecificationOptionExample);
                List<String> list = new ArrayList<>();
                for (TbSpecificationOption option : options) {
                    list.add(option.getOptionName());
                }
                HashMap<Object, Object> map2 = new HashMap<>();
                map2.put("specName",text);
                map2.put("options",list);
                specList.add(map2);
            }
            redisTemplate.boundHashOps("specList").put(typeId,specList);

            redisTemplate.expire(specList,1L,TimeUnit.DAYS);

            redisTemplate.expire(specList,1L,TimeUnit.MINUTES);

            System.out.println("规格列表查询了数据库");
        }
        map.put("brandList",brandList);
        map.put("specList",specList);
        return map;
    }

    /**
     * @author: zhangyu
     * @date: 2019-11-20
     * @param: [searchMap]
     * @return: java.util.List<java.lang.String>
     * 功能描述:进行分组查询，查询出关键字下的所有查询结果的，并且通过category自动进行分组
     */
    private List<String> getGroupCategoryList(Map searchMap){
        //1.进行分组查询
        List<String> groupList = new ArrayList<>();
        //2.创建查询对象
        SimpleQuery query = new SimpleQuery();
        //3.创建查询条件
        Criteria criteria = new Criteria("item_keywords");
        //3.2判断查询关键字是否为空
        if (StringUtils.isNotBlank(searchMap.get("keyWords")+"")){
            criteria.is(searchMap.get("keyWords"));
        }
        //3.3添加查询条件到分组查询链
        query.addCriteria(criteria);
        //4.定义分组选项参数，即对那个字段进行分组
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        //5.设置分组选项参数
        query.setGroupOptions(groupOptions);
        //6.执行分组查询返回分组查询结果对象
        GroupPage<TbItem> groupPage = solrTemplate.queryForGroupPage(query, TbItem.class);
        //7.获得对应分组字段的结果对象
        GroupResult<TbItem> itemCategory = groupPage.getGroupResult("item_category");
        //8.获得该字段的分组入口条目
        Page<GroupEntry<TbItem>> itemCategoryGroupEntries = itemCategory.getGroupEntries();
        //9.遍历分组条目
        for (GroupEntry<TbItem> entry : itemCategoryGroupEntries) {
            //10.获得分组结果集
            groupList.add(entry.getGroupValue());
        }
        return groupList;
    }



    private Map highLightSearch(Map searchMap){
        //1.定义返回高亮查询结果的Map
        HashMap<Object, Object> highLightMap = new HashMap<>();
        //1.创建高亮查询实例
         SimpleHighlightQuery query = new SimpleHighlightQuery();
        /*********************************分页查询***************************/
        if (StringUtils.isNotBlank(searchMap.get("pageSize")+"") && StringUtils.isNotBlank(searchMap.get("pageSize") + "")){
            //0.获得当前页和每页大小
            Integer pageIndex = (Integer) searchMap.get("pageIndex");
            Integer pageSize = (Integer) searchMap.get("pageSize");
            //1.设置分页偏移量，即从多少个开始查询
            query.setOffset((pageIndex-1) * pageSize);
            //2.设置每页查询多少个数据
            query.setRows(pageSize);
        }
        /********************************按照商品分类进行过滤查询*****************/
        //0.判断是否需要进行按分类过滤查询，不为空则添加过滤查询
        if (StringUtils.isNotBlank(searchMap.get("category").toString())){
            //1.创建一个分类过滤查询对象
            SimpleFilterQuery filterQuery = new SimpleFilterQuery();
            Criteria criteria = new Criteria("item_category").is(searchMap.get("category").toString());
            filterQuery.addCriteria(criteria);
            query.addFilterQuery(filterQuery);
        }

        /*******************************按照商品的品牌进行过滤查询***********************/
        //1.判断是否需要进行按品牌过滤查询
        if (StringUtils.isNotBlank(searchMap.get("brand")+"")){
            //2.创建过滤查询对象
            SimpleFilterQuery filterQuery = new SimpleFilterQuery();
            Criteria criteria = new Criteria("item_brand").is(searchMap.get("brand"));
            filterQuery.addCriteria(criteria);
            query.addFilterQuery(filterQuery);
        }

        /*********************************按照规格进行过滤查询******************************/
        //1.判断前端传来的规格是否为空，前端传来的数据格式是map{“规格名称”：“规格选项”，“规格名称”}
        Map<String,String> spec = (Map) searchMap.get("spec");
        //2.判断传入的规格map是否为空，并且判断其是否是有键值对
        if (spec != null && spec.keySet().size() > 0){
           //3.遍历keyset进行条件设置
            for (String key : spec.keySet()) {
                //4.创建过滤查询对象
                SimpleFilterQuery filterQuery = new SimpleFilterQuery();
                //5.创建查询条件
                Criteria criteria = new Criteria("item_spec_"+key).is(spec.get(key));
                //6.添加查询条件到过滤查询对象
                filterQuery.addCriteria(criteria);
                //7.添加过滤查询到查询链中
                query.addFilterQuery(filterQuery);
            }

        }

        /*********************************按照价格进行过滤查询******************************/
        //1.获取前端传入的价格对象
        String price = (String) searchMap.get("price");
        //2.判断是否需要进行按价格过滤查询，如果为空则不需要，否则设置按价格查询
        if (StringUtils.isNotBlank(price)){
            //3.拆分价格字符串  [x-x],可以拆分为两段，表示两个区间，
            String[] splitPrice = price.split("-");
            //4.判断价格区间的最大值是否是*，是则不进行最大值过滤
            if (!splitPrice[1].equals("*")){
                //4.1 创建过滤查询对象
                SimpleFilterQuery filterQuery = new SimpleFilterQuery();
                //4.2创建查询条件
                Criteria criteria = new Criteria("item_price").lessThanEqual(splitPrice[1]);
                //4.3查询条件添加至过滤查询对象
                filterQuery.addCriteria(criteria);
                //4.4将过滤查询添加至查询链
                query.addFilterQuery(filterQuery);
            }
            //5.1 创建过滤查询对象
            SimpleFilterQuery filterQuery = new SimpleFilterQuery();
            //5.2创建查询条件
            Criteria criteria = new Criteria("item_price").greaterThanEqual(splitPrice[0]);
            //5.3查询条件添加至过滤查询对象
            filterQuery.addCriteria(criteria);
            //5.4将过滤查询添加至查询链
            query.addFilterQuery(filterQuery);

        }

        /*********************************设置排序***************************************/
        //1.获取排序查询关键字
        String sort = (String) searchMap.get("sort");
        //2.获取排序查询的字段
        String sortField = (String) searchMap.get("sortField");
        if (StringUtils.isNotBlank(sort) && StringUtils.isNotBlank(sortField)){
            //3.判断是否是升序，添加至查询链
            if (sort.equals("ASC")){
                Sort sort1 = new Sort(Sort.Direction.ASC,"item_"+sortField);
                query.addSort(sort1);
            }
            if (sort.equals("DESC")){
                Sort sort1 = new Sort(Sort.Direction.DESC, "item_" + sortField);
                query.addSort(sort1);
            }
        }

        /***********************************高亮查询**************************************/
        //1.创建高亮查询条件
        Criteria highLightCriteria = new Criteria("item_keywords");
        if (StringUtils.isNotBlank(searchMap.get("keyWords").toString()))
        highLightCriteria.is(searchMap.get("keyWords"));
        query.addCriteria(highLightCriteria);
        //2.设置高亮选项
        HighlightOptions highLightOptions = new HighlightOptions();
        //2.1设置前缀
        highLightOptions.setSimplePrefix("<em style='color:red'>");
        //2.2设置后缀
        highLightOptions.setSimplePostfix("</em>");
        //2.3 设置高亮字段
        highLightOptions.addField("item_title");
        //2.4 设置高亮选项到查询实例
        query.setHighlightOptions(highLightOptions);
        //3.执行高亮查询，返回高亮页
        HighlightPage<TbItem> highlightPage = solrTemplate.queryForHighlightPage(query, TbItem.class);
        //3.1获得高亮入口
        List<HighlightEntry<TbItem>> highlighted = highlightPage.getHighlighted();
        //3.2 遍历
        for (HighlightEntry<TbItem> highlightEntry : highlighted) {
            //3.3获得所有的查询结果
            TbItem entity = highlightEntry.getEntity();
            //4.4获得高亮查询结果
            List<HighlightEntry.Highlight> highlights = highlightEntry.getHighlights();
            if (highlights.size() > 0 && highlights.get(0).getSnipplets().size() > 0 && StringUtils.isNotBlank(searchMap.get("keyWords")+"")){
                entity.setTitle(highlights.get(0).getSnipplets().get(0));
            }
        }
        //4.设置到返回结果map
        highLightMap.put("rows",highlightPage.getContent());
        highLightMap.put("totalPages",highlightPage.getTotalPages());
        highLightMap.put("total",highlightPage.getTotalElements());
        //5.返回查询结果集
        return highLightMap;
    }


}
