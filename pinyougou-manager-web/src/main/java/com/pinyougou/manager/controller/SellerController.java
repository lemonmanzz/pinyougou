package com.pinyougou.manager.controller;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.pojo.Result;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * controller
 * @author Administrator
 *
 */
@RestController
@RequestMapping("/seller")
public class SellerController {

    @Reference
    private SellerService sellerService;

	@RequestMapping("findAll")
    public List<TbSeller> findAll(){
	    return sellerService.findAll();
    }
    @RequestMapping("updateStatus")
    public Result updateStatus(String sellerId,String status) {
        try {
            sellerService.updateStatus(sellerId, status);
            return new Result(true, "更改成功");
        } catch (Exception e) {
            return new Result(false, "更改失败");
        }
    }
}
