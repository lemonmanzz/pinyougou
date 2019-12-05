package com.pinyougou.sellergoods.service;
import java.util.List;

import com.pinyougou.pojo.PageResult;
import com.pinyougou.pojo.TbSpecificationOption;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SpecificationOptionService {

    List<TbSpecificationOption> findBySpecId(Long specId);
}
