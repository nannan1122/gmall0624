package com.atguigu.gmall0624.user.controller;

import com.atguigu.gmall0624.bean.UserInfo;
import com.atguigu.gmall0624.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class UserInfoController {
    @Autowired
    UserInfoService userInfoService;

    @RequestMapping("findAll")
    public List<UserInfo>findAll (){
        return userInfoService.findAll();
    }



}
