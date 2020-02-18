package com.atguigu.gmall0624.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall0624.bean.UserInfo;
import com.atguigu.gmall0624.passport.config.JwtUtil;
import com.atguigu.gmall0624.service.UserInfoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
@CrossOrigin
public class PassportController {
    @Reference
    UserInfoService userInfoService;

    @Value("${token.key}")
    private String signKey;

    @RequestMapping("index")
    public String index(HttpServletRequest request){
        String originUrl = request.getParameter("originUrl");
        // 保存上
        request.setAttribute("originUrl",originUrl);
        return "index";
    }

     //登录控制器：
    //登录：userInfo
    @RequestMapping("login")
    @ResponseBody
    public String login(HttpServletRequest request, UserInfo userInfo){
        // 在nginx配置的，只有走nginx,,取得ip地址
        String remoteAddr = request.getHeader("X-forwarded-for");

        //获得前端用户名密码和数据库对比
        if(userInfo!=null){
            UserInfo loginUser= userInfoService.login(userInfo);
            if(loginUser!=null){//登录成功
                //生成token
                Map map =new HashMap();
                map.put("userId", loginUser.getId());
                map.put("nickName", loginUser.getNickName());
                String token = JwtUtil.encode(signKey, map, remoteAddr);
                return token;

            }else {
                return "fail";
            }
        }
        return "fail";

    }

    @RequestMapping("verify")
    @ResponseBody
    public String verify(HttpServletRequest request) {
        String token = request.getParameter("token");////等测试时手动输入的就是token
        String currentIp = request.getParameter("currentIp");//等测试时手动输入的就是currentIp

        Map<String, Object> map = JwtUtil.decode(token, signKey, currentIp);
        if (map!=null){
            String userId = (String) map.get("userId");
            //去redis验证
            UserInfo userInfo=userInfoService.verify(userId);
            if (userInfo!=null){
                return "success";
            }

        }
        return "fail";

    }





    }
