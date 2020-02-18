package com.atguigu.gmall0624.service;

import com.atguigu.gmall0624.bean.OrderInfo;

public interface OrderService {
    //保存订单
    String saveOrder(OrderInfo orderInfo);
    //生成流水号
    String getTradeNo(String userId);

    //验证流水号
    boolean checkTradeCode(String userId,String tradeCodeNo);

    //删除流水号
    void deleteTradeCode(String userId);
    //现在验证库存数量方法
    boolean checkStock(String skuId, Integer skuNum);

    //查询订单信息
    OrderInfo getOrderInfo(String orderId);



}
