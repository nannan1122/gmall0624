package com.atguigu.gmall0624.service;

import com.atguigu.gmall0624.bean.SkuLsInfo;
import com.atguigu.gmall0624.bean.SkuLsParams;
import com.atguigu.gmall0624.bean.SkuLsResult;

public interface ListService {
    //保存数据到es中
    public void saveSkuLsInfo(SkuLsInfo skuLsInfo);

    //全文检索
    public SkuLsResult search(SkuLsParams skuLsParams);
  //热度评分
    public void incrHotScore(String skuId);

}
