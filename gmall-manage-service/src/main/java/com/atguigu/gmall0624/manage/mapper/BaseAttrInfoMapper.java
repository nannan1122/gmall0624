package com.atguigu.gmall0624.manage.mapper;

import com.atguigu.gmall0624.bean.BaseAttrInfo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseAttrInfoMapper extends Mapper<BaseAttrInfo> {
    // 根据三级分类id查询属性表
    List<BaseAttrInfo> getBaseAttrInfoListByCatalog3Id(String catalog3Id);


    //根据平台属性值id们查询平台属性和值的列表
    List<BaseAttrInfo> selectAttrInfoListByIds(@Param("valueIds") String attrValueIds);



}
