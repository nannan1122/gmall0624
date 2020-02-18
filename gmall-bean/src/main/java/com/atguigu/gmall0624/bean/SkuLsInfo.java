package com.atguigu.gmall0624.bean;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuLsInfo implements Serializable {
    String id;//skuId

    BigDecimal price;//

    String skuName;//商品名称

    String catalog3Id;

    String skuDefaultImg;

    Long hotScore=0L;

    List<SkuLsAttrValue> skuAttrValueList;//平台属性值id集合
//以上除了热度来自于skuInfo  想属性拷贝

}
