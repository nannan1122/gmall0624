package com.atguigu.gmall0624.service;

import com.atguigu.gmall0624.bean.CartInfo;

import java.util.List;

public interface CartService {

    //添加购物车
    void  addToCart(String skuId,String userId,Integer skuNum);

    List<CartInfo> getCartList(String userId);

    //未登录时有临时购物车和商品时，要和登录状态下合并
    List<CartInfo> mergeToCartList(List<CartInfo> cartTempList, String userId);

    //删除未登录得临时购物车
    void deleteCartList(String userTempId);


    void checkCart(String isChecked, String skuId, String userId);

    List<CartInfo> getCartCheckedList(String userId);

    //更新价格并刷新缓存
    List<CartInfo> loadCartCache(String userId);
}
