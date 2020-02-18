package com.atguigu.gmall0624.service;

import com.atguigu.gmall0624.bean.PaymentInfo;

import java.util.Map;

public interface PaymentService {

    void  savePaymentInfo(PaymentInfo paymentInfo);


    PaymentInfo getPaymentInfo(PaymentInfo paymentInfo);

    void updatePaymentInfo(String outTradeNo, PaymentInfo paymentInfoUpd);

    boolean refund(String orderId);

    //生成微信支付的ma'p
    Map createNative(String orderId, String total_fee);
}
