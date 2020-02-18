package com.atguigu.gmall0624.list.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall0624.bean.*;
import com.atguigu.gmall0624.service.ListService;
import com.atguigu.gmall0624.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Controller
@CrossOrigin
public class ListController {
    @Reference
    private ListService listService;
    @Reference
    private ManageService manageService;

    @RequestMapping("list.html")
    public String getList(SkuLsParams skuLsParams, Model model){
        // 设置每页显示的条数
        skuLsParams.setPageSize(2);

        SkuLsResult skuLsResult = listService.search(skuLsParams);
        List<SkuLsInfo> skuLsInfoList = skuLsResult.getSkuLsInfoList();


        // 从day08查出的es结果中取出平台属性值id集合
        List<String> attrValueIdList = skuLsResult.getAttrValueIdList();
        //将其作为条件进而查平台属性，用于显示页面上半部分
        List<BaseAttrInfo> attrList = manageService.getAttrList(attrValueIdList);

        //声明一个面包屑集合 因为面包屑存在于平台属性值名称里，每点一个面包屑多一个值对象
        List<BaseAttrValue> baseAttrValuesList = new ArrayList<>();

        // 此方法可以记录点击之前用户查询的参数列表-渲染用
        String urlParam = makeUrlParam(skuLsParams);



        // itco 点击平台属性值过滤后，让平台属性消失
        //循环attrList（es查询结果中用于显示页面上半部分的，里面有平台属性，它里面又有平台属性值对象集合，平台属性值对象里有平台属性值id）
        if(attrList!=null && attrList.size()>0) {
            for (Iterator<BaseAttrInfo> iterator = attrList.iterator(); iterator.hasNext(); ) {
                BaseAttrInfo baseAttrInfo = iterator.next();////平台属性
                List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();////平台属性值集合
                for (BaseAttrValue baseAttrValue : attrValueList) {//遍历得平台属性值对象
                    //baseAttrValue.setUrlParam(urlParam);老师没有
                    String attrValueId = baseAttrValue.getId();  //平台属性值id
                    //如果用户点击平台属性之过滤,则找出本方法参数里得平台属性值id，即url后面的valueId
                    if (skuLsParams.getValueId() != null && skuLsParams.getValueId().length > 0) {
                        for (String valueId : skuLsParams.getValueId()) {//url后面的valueId
                            //参数里的属性值（选中的属性值 ）和 查询结果的属性值比较，相等则将平台属性整个删掉，
                            // 自然的平台属性值也就删掉了
                            if (valueId.equals(attrValueId)) {
                                iterator.remove();

                                // 构造面包屑 平台属性名称：平台属性值名称
                                BaseAttrValue baseAttrValueSelected = new BaseAttrValue();
                                //将面包屑存在平台属性值的名称中
                                baseAttrValueSelected.setValueName(baseAttrInfo.getAttrName() + ":" + baseAttrValue.getValueName());
                                // 新的urlParam,将它记录下来
                                String makeUrlParam = makeUrlParam(skuLsParams, valueId);
                                baseAttrValueSelected.setUrlParam(makeUrlParam);
                                //上面这个对象不仅存储了面包屑（用值名字），还存了新的urlparam
                                baseAttrValuesList.add(baseAttrValueSelected);
                            }
                        }
                    }
                }
            }
        }


            // 保存面包屑清单
            model.addAttribute("baseAttrValueArrayList",baseAttrValuesList);
            model.addAttribute("keyword",skuLsParams.getKeyword());
            model.addAttribute("urlParam",urlParam);
            //保存页面渲染上半部分---平台属性
            model.addAttribute("baseAttrInfoList",attrList);//老师课程和笔记中用的不一样//上面移除的代码一定要在这个之前，


             model.addAttribute("totalPages", skuLsResult.getTotalPages());
            model.addAttribute("pageNo",skuLsParams.getPageNo());
            //保存页面渲染下半部分
            model.addAttribute("skuLsInfoList",skuLsInfoList);


        return "list";
    }

    private String makeUrlParam(SkuLsParams skuLsParam,String... excludeValueIds) {
        String urlParam="";
        List<String> paramList = new ArrayList<>();
        //如果当前对象skuLsParams里 keyword不为空，则走的是全文建索
        if(skuLsParam.getKeyword()!=null && skuLsParam.getKeyword().length()>0){
            urlParam+="keyword="+skuLsParam.getKeyword();
        }
        //如果当前对象skuLsParams里的三级id不为空，朔漠ing走的是三级id；
        if (skuLsParam.getCatalog3Id()!=null && skuLsParam.getCatalog3Id().length()>0){
            urlParam+="catalog3Id="+skuLsParam.getCatalog3Id();
        }
        // 上面两种方式只能选择一种进来，之后如果在想按照平台属性值建索，那么继续拼
        if (skuLsParam.getValueId()!=null && skuLsParam.getValueId().length>0){
            for (int i=0;i<skuLsParam.getValueId().length;i++){
                String valueId = skuLsParam.getValueId()[i];

                if (excludeValueIds!=null && excludeValueIds.length>0){
                    String excludeValueId = excludeValueIds[0];//用户点击时的值id
                    if (excludeValueId.equals(valueId)){ //面包屑下
                        // 跳出代码，后面的参数则不会继续追加【后续代码不会执行】
                        continue;
                    }
                }
                if (urlParam.length()>0){
                    urlParam+="&";
                }
                urlParam+="valueId="+valueId;
            }
        }
        return  urlParam;
    }




}
