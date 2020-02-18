package com.atguigu.gmall0624.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall0624.bean.SkuInfo;
import com.atguigu.gmall0624.bean.SkuSaleAttrValue;
import com.atguigu.gmall0624.bean.SpuSaleAttr;
import com.atguigu.gmall0624.config.LoginRequire;
import com.atguigu.gmall0624.service.ListService;
import com.atguigu.gmall0624.service.ManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import sun.text.resources.ar.FormatData_ar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ItemController {

    @Reference
    private ManageService manageService;

    @Reference
    private ListService listService;


    @RequestMapping("{skuId}.html")
    public String skuInfoPage(@PathVariable String skuId, Model model){
        // 存储基本的skuInfo信息
        SkuInfo skuInfo=manageService.getSkuInfo(skuId);
        model.addAttribute("skuInfo",skuInfo);

        //存储 spu，sku数据 以前写过但不够用，
        // 这里要根据实际业务逻辑，完成销售属性，销售属性值回显并锁定！
        List<SpuSaleAttr> saleAttrList = manageService.getSpuSaleAttrListCheckBySku(skuInfo);
        model.addAttribute("saleAttrList",saleAttrList);

        //查询出该spu下的所有skuId和属性值关联关系
        List<SkuSaleAttrValue> skuSaleAttrValueListBySpu = manageService.getSkuSaleAttrValueListBySpu(skuInfo.getSpuId());
        //把列表变换成  ：skuId  的 哈希表 用于在页面中定位查询
        String valueIdsKey="";
        Map<String,String> valuesSkuMap=new HashMap<>();
        if(skuSaleAttrValueListBySpu!=null&&skuSaleAttrValueListBySpu.size()>0){
            for (int i = 0; i < skuSaleAttrValueListBySpu.size(); i++) {
                SkuSaleAttrValue skuSaleAttrValue = skuSaleAttrValueListBySpu.get(i);
                if(valueIdsKey.length()!=0){
                    valueIdsKey=valueIdsKey+"|";
                }
                valueIdsKey=valueIdsKey+skuSaleAttrValue.getSaleAttrValueId();
                if((i+1)== skuSaleAttrValueListBySpu.size()||!skuSaleAttrValue.getSkuId().equals(skuSaleAttrValueListBySpu.get(i+1).getSkuId())){

                    valuesSkuMap.put(valueIdsKey,skuSaleAttrValue.getSkuId());
                    valueIdsKey="";
                }

            }



        }

        //把map变成json串
        String valuesSkuJson = JSON.toJSONString(valuesSkuMap);

        model.addAttribute("valuesSkuJson",valuesSkuJson);

        //热度
        listService.incrHotScore(skuId);


        return "item";
    }
}
