package com.atguigu.gmall0624.service;

import com.atguigu.gmall0624.bean.*;

import java.util.List;

public interface ManageService {


    List<BaseCatalog1> getCatalog1();
    List<BaseCatalog2> getCatalog2(String catalog1Id);
    List<BaseCatalog2> getCatalog2(BaseCatalog2 baseCatalog2);

    List<BaseCatalog3> getCatalog3(String catalog2Id);
    List<BaseCatalog3> getCatalog3(BaseCatalog3 baseCatalog3);

    List<BaseAttrInfo> getAttrInfoList(String catalog3Id);
    List<BaseAttrInfo> getAttrInfoList(BaseAttrInfo baseAttrInfo);

    void saveAttrInfo(BaseAttrInfo baseAttrInfo);


   // List<BaseAttrValue> getAttrValueList(String attrId);

    BaseAttrInfo getAttrInfo(String attrId);

    List<SpuInfo> getSpuInfoList(SpuInfo spuInfo);

    List<BaseSaleAttr> getBaseSaleAttrList();


    void saveSpuInfo(SpuInfo spuInfo);

    List<SpuImage> getSpuImageList(SpuImage spuImage);

    List<SpuSaleAttr> getSpuSaleAttrList(String spuId);

    void saveSkuInfo(SkuInfo skuInfo);

    SkuInfo getSkuInfo(String skuId);

    List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(SkuInfo skuInfo);

    List<SkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String spuId);

    List<BaseAttrInfo> getAttrList(List<String> attrValueIdList);
}
