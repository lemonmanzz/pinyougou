package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.group.Cart;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.PageResult;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderExample;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private TbOrderMapper orderMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TbOrderItemMapper orderItemMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbOrder> findAll() {
        return orderMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbOrder> page=   (Page<TbOrder>) orderMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbOrder tbOrder) {
        //1.从redis中取得购物车列表
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(tbOrder.getUserId());
        //2.遍历购物车列表
        for (Cart cart : cartList) {
            //2.1)构造一个TbOrder对象
            TbOrder order= new TbOrder();
            //2.2)设置一系列参数
            //生成订单号
            long orderId = idWorker.nextId();
            order.setOrderId(orderId);		//订单号
            order.setSourceType(tbOrder.getSourceType());
            order.setUserId(tbOrder.getUserId());
            order.setCreateTime(new Date());
            order.setUpdateTime(new Date());
            order.setPaymentType(tbOrder.getPaymentType());
            order.setReceiver(tbOrder.getReceiver());
            order.setReceiverAreaName(tbOrder.getReceiverAreaName());
            order.setReceiverMobile(tbOrder.getReceiverMobile());
            order.setSellerId(tbOrder.getSellerId());
            //定义订单总金额
            double sum = 0;
            //2.3）遍历订单项
            for (TbOrderItem orderItem : cart.getOrderItemList()) {
                //2.4）设置订单项的一系列参数
                orderItem.setId(idWorker.nextId());
                orderItem.setOrderId(orderId);
                orderItem.setSellerId(tbOrder.getSellerId());
                //2.5）累加总金额
                sum += orderItem.getTotalFee().doubleValue();
                //2.6)添加订单项
                orderItemMapper.insert(orderItem);
            }
            //2.7)为订单设置总金额
            order.setPayment(new BigDecimal(sum));
            //2.8)添加数据到订单表中
            orderMapper.insert(order);
        }
        //2.9)从redis中删除购物车列表
        //redisTemplate.boundHashOps("cartList").delete(tbOrder.getUserId());
    }
    /**
     * 修改
     */
    @Override
    public void update(TbOrder order){
        orderMapper.updateByPrimaryKey(order);
    }

    /**
     * 根据ID获取实体
     * @param id
     * @return
     */
    @Override
    public TbOrder findOne(Long id){
        return orderMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for(Long id:ids){
            orderMapper.deleteByPrimaryKey(id);
        }
    }


////    @Override
////    public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
////        PageHelper.startPage(pageNum, pageSize);
////
////        TbOrderExample example=new TbOrderExample();
////        TbOrderExample.Criteria criteria = example.createCriteria();
////
////        if(order!=null){
////            if(order.getPaymentType()!=null && order.getPaymentType().length()>0){
////                criteria.andPaymentTypeLike("%"+order.getPaymentType()+"%");
////            }
////            if(order.getPostFee()!=null && order.getPostFee().length()>0){
////                criteria.andPostFeeLike("%"+order.getPostFee()+"%");
////            }
////            if(order.getStatus()!=null && order.getStatus().length()>0){
////                criteria.andStatusLike("%"+order.getStatus()+"%");
////            }
////            if(order.getShippingName()!=null && order.getShippingName().length()>0){
////                criteria.andShippingNameLike("%"+order.getShippingName()+"%");
////            }
////            if(order.getShippingCode()!=null && order.getShippingCode().length()>0){
////                criteria.andShippingCodeLike("%"+order.getShippingCode()+"%");
////            }
////            if(order.getUserId()!=null && order.getUserId().length()>0){
////                criteria.andUserIdLike("%"+order.getUserId()+"%");
////            }
////            if(order.getBuyerMessage()!=null && order.getBuyerMessage().length()>0){
////                criteria.andBuyerMessageLike("%"+order.getBuyerMessage()+"%");
////            }
////            if(order.getBuyerNick()!=null && order.getBuyerNick().length()>0){
////                criteria.andBuyerNickLike("%"+order.getBuyerNick()+"%");
////            }
////            if(order.getBuyerRate()!=null && order.getBuyerRate().length()>0){
////                criteria.andBuyerRateLike("%"+order.getBuyerRate()+"%");
////            }
////            if(order.getReceiverAreaName()!=null && order.getReceiverAreaName().length()>0){
////                criteria.andReceiverAreaNameLike("%"+order.getReceiverAreaName()+"%");
////            }
////            if(order.getReceiverMobile()!=null && order.getReceiverMobile().length()>0){
////                criteria.andReceiverMobileLike("%"+order.getReceiverMobile()+"%");
////            }
////            if(order.getReceiverZipCode()!=null && order.getReceiverZipCode().length()>0){
////                criteria.andReceiverZipCodeLike("%"+order.getReceiverZipCode()+"%");
////            }
////            if(order.getReceiver()!=null && order.getReceiver().length()>0){
////                criteria.andReceiverLike("%"+order.getReceiver()+"%");
////            }
////            if(order.getInvoiceType()!=null && order.getInvoiceType().length()>0){
////                criteria.andInvoiceTypeLike("%"+order.getInvoiceType()+"%");
////            }
////            if(order.getSourceType()!=null && order.getSourceType().length()>0){
////                criteria.andSourceTypeLike("%"+order.getSourceType()+"%");
////            }
////            if(order.getSellerId()!=null && order.getSellerId().length()>0){
////                criteria.andSellerIdLike("%"+order.getSellerId()+"%");
////            }
////
////        }
//
//        Page<TbOrder> page= (Page<TbOrder>)orderMapper.selectByExample(example);
//        return new PageResult(page.getTotal(), page.getResult());
//    }
}
