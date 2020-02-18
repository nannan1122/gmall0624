package com.atguigu.gmall0624.manage.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall0624.bean.*;
import com.atguigu.gmall0624.service.ManageService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class ManageController {

    @Reference
    private ManageService manageService;

//查询一级分类http://localhost:8082/getCatalog1
    @RequestMapping("getCatalog1")
    public List<BaseCatalog1>getCatalog1(){
        return manageService.getCatalog1();
    }

    //http://localhost:8082/getCatalog2?catalog1Id=2
    @RequestMapping("getCatalog2")
    public List<BaseCatalog2>getCatalog2(String catalog1Id){
        return manageService.getCatalog2(catalog1Id);
    }

    //http://localhost:8082/getCatalog3?catalog2Id=13
    @RequestMapping("getCatalog3")
    public List<BaseCatalog3>getCatalog3(String catalog2Id){
        return manageService.getCatalog3(catalog2Id);
    }

    //http://localhost:8082/attrInfoList?catalog3Id=190
    @RequestMapping("attrInfoList")
    public List<BaseAttrInfo>attrInfoList(String catalog3Id){
        return manageService.getAttrInfoList(catalog3Id);
    }

    //添加修改平台属性http://localhost:8082/saveAttrInfo
    @RequestMapping("saveAttrInfo")
    public void saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        manageService.saveAttrInfo(baseAttrInfo);

    }
    //修改回显平台属性http://localhost:8082/getAttrValueList?attrId=100
    @RequestMapping("getAttrValueList")
    public List<BaseAttrValue>getAttrValueList(String attrId){
        BaseAttrInfo attrInfo = manageService.getAttrInfo(attrId);
        return attrInfo.getAttrValueList();

    }
}
