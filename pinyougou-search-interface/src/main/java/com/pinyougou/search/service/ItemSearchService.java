package com.pinyougou.search.service;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    /**
     * @author: zhangyu
     * @date: 2019-11-26
     * @param: [data]
     * @return: void
     * 功能描述: 导入商品列表到索引库
     */
    void importDataToSolr(List data);

    /**
     * @author: zhangyu
     * @date: 2019-11-26
     * @param: [ids]
     * @return: void
     * 功能描述: 从索引库中删除数据
     */
    void deleteFromSolr(Long[] ids);

    /**
     * 功能简述: 搜索功能
     * 功能详细描述: 将需要搜索的字段通过Map的格式执行传入，并执行搜索
     * @Author: zhangyu
     * @param: [searchMap]
     * @createDate: 2019-11-19
     * @return: java.util.Map<java.lang.String , java.lang.Object>
     */
    Map<String,Object> search(Map searchMap);
}
