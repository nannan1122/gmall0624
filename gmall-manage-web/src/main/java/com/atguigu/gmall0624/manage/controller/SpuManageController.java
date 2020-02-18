package com.atguigu.gmall0624.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall0624.bean.BaseSaleAttr;
import com.atguigu.gmall0624.bean.SpuInfo;
import com.atguigu.gmall0624.service.ManageService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
public class SpuManageController {

    //http://localhost:8082/spuList?catalog3Id=61
   @Reference
    ManageService manageService;



    @RequestMapping("spuList")
    public List<SpuInfo> getSpuInfoList(SpuInfo spuInfo){

        return manageService.getSpuInfoList(spuInfo);
    }



    //http://localhost:8082/baseSaleAttrList
    @RequestMapping("baseSaleAttrList")
    public List<BaseSaleAttr> getBaseSaleAttrList(){

        return manageService.getBaseSaleAttrList() ;
    }

    //http://localhost:8082/saveSpuInfo
    @RequestMapping("saveSpuInfo")
    public void saveSpuInfo(@RequestBody SpuInfo spuInfo){

         manageService.saveSpuInfo(spuInfo) ;
    }



}
