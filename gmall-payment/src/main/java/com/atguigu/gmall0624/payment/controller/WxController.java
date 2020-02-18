package com.atguigu.gmall0624.payment.controller;

import com.atguigu.gmall0624.service.PaymentService;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class WxController {

    @Reference
    private PaymentService paymentService;

    @RequestMapping("wx/submit")
    @ResponseBody
    public Map createNative(String orderId){
        //  // 做一个判断：支付日志中的订单支付状态 如果是已支付，则不生成二维码直接重定向到消息提示页面！
// 调用服务层数据
// 第一个参数是订单Id ，第二个参数是多少钱，单位是分,实际应该获取，这里写死
        Map map = paymentService.createNative(orderId +"", "1");
        System.out.println(map.get("code_url"));
// data = map
        return map;
    }

}
