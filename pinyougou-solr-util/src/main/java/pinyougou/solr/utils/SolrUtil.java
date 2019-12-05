package pinyougou.solr.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:spring/applicationContext-*.xml")
public class SolrUtil {
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private TbItemMapper itemMapper;

    /**
     * 功能简述: 导入数据库中的数据到solr索引库
     * 功能详细描述: 调用dao查询并筛选状态为1（即可用数据）;
     * @Author: zhangyu
     * @param: []
     * @createDate: 2019-11-19
     * @return: void
     */
    @Test
    public void ImportItemData(){
        //1.查询数据
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");
        List<TbItem> tbItems = itemMapper.selectByExample(example);
        //2.遍历tbItems将spec（字段）转换为Map并赋值给item；
        tbItems.forEach(item -> {
            //将json字符串转换为Map对象
            Map map = JSON.parseObject(item.getSpec(), Map.class);
            item.setSpecMap(map);
        });
        solrTemplate.saveBeans(tbItems);
//        solrTemplate.delete(new SimpleQuery("*:*"));
        solrTemplate.commit();
        System.out.println("导入成功");
    }
    @Test
    public void delete(){

    }

}
