
package com.atguigu.gmall0624.service;





import com.atguigu.gmall0624.bean.UserAddress;
import com.atguigu.gmall0624.bean.UserInfo;

import java.util.List;

public interface UserInfoService {

   //返回所有数据
    List<UserInfo>findAll();

    //想根据用户id或昵称或名称等不同字段进行查询，若不输入查所有
    List<UserInfo>findUserInfo(UserInfo userInfo);

    //模糊查询nickName
    List<UserInfo>findByNickName(String NickName);


    //添加
    void addUser(UserInfo userInfo);


   //修改
    void updUser(UserInfo userInfo);

   //删除
   void delUser(UserInfo userInfo);

   //根据用户id查用户地址
 List<UserAddress> findUserAddressListByUserId(String UserId);
 //根据用户id查用户地址
 List<UserAddress> findUserAddressListByUserId(UserAddress userAddress);


    UserInfo login(UserInfo userInfo);


    UserInfo verify(String userId);
}
