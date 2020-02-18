package com.atguigu.gmall0624.order.orderService.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall0624.bean.OrderDetail;
import com.atguigu.gmall0624.bean.OrderInfo;
import com.atguigu.gmall0624.config.RedisUtil;
import com.atguigu.gmall0624.order.mapper.OrderDetailMapper;
import com.atguigu.gmall0624.order.mapper.OrderInfoMapper;
import com.atguigu.gmall0624.service.OrderService;
import com.atguigu.gmall0624.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderInfoMapper orderInfoMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private RedisUtil redisUtil;

   @Override
   @Transactional
    public  String saveOrder(OrderInfo orderInfo) {
        // 设置创建时间
        orderInfo.setCreateTime(new Date());
        // 设置失效时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        orderInfo.setExpireTime(calendar.getTime());
        // 生成第三方支付编号
        String outTradeNo = "ATGUIGU" + System.currentTimeMillis() + "" + new Random().nextInt(1000);
        orderInfo.setOutTradeNo(outTradeNo);
        orderInfoMapper.insertSelective(orderInfo);

        // 插入订单详细信息
        List<OrderDetail> orderDetailList = orderInfo.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetailList) {
            orderDetail.setOrderId(orderInfo.getId());
            orderDetailMapper.insertSelective(orderDetail);
        }
// 为了跳转到支付页面使用。支付会根据订单id进行支付。
        String orderId = orderInfo.getId();
        return orderId;


      }

      //生成流水号并利用userId作为key,将流水号保存到缓存中
    @Override
    public String getTradeNo(String userId) {
        Jedis jedis = redisUtil.getJedis();
        String tradeNoKey="user:"+userId+":tradeCode";
        String tradeCode = UUID.randomUUID().toString().replace("-","");
        jedis.setex(tradeNoKey,10*60,tradeCode);
        jedis.close();
        return tradeCode;

    }

    //验证流水号
    @Override
    public boolean checkTradeCode(String userId, String tradeCodeNo) {

        Jedis jedis = redisUtil.getJedis();
        String tradeNoKey = "user:"+userId+":tradeCode";
        String redisTradeCode = jedis.get(tradeNoKey);
        jedis.close();
       return tradeCodeNo.equals(redisTradeCode);

    }

    //删除流水号
    @Override
    public void deleteTradeCode(String userId) {
        Jedis jedis = redisUtil.getJedis();
        String tradeNoKey ="user:"+userId+":tradeCode";
        String tradeCode = jedis.get(tradeNoKey); // 获取jedis 中的流水号
        // jedis.del(tradeNoKey);
        String script ="if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        jedis.eval(script, Collections.singletonList(tradeNoKey),Collections.singletonList(tradeCode));

        jedis.close();

    }

    @Override
    public boolean checkStock(String skuId, Integer skuNum) {
        String result = HttpClientUtil.doGet("http://www.gware.com/hasStock?skuId=" + skuId + "&num=" + skuNum);
        if ("1".equals(result)){
            return  true;
        }else {
            return  false;
        }

    }

    //根据订单编号查询订单信息
    @Override
    public OrderInfo getOrderInfo(String orderId) {

        OrderInfo orderInfo = orderInfoMapper.selectByPrimaryKey(orderId);
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(orderId);
        // 将orderDetai 放入orderInfo 中
        orderInfo.setOrderDetailList(orderDetailMapper.select(orderDetail));
        return orderInfo;

    }


}
