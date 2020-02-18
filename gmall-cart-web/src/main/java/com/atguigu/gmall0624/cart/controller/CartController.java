package com.atguigu.gmall0624.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall0624.bean.CartInfo;
import com.atguigu.gmall0624.bean.SkuInfo;
import com.atguigu.gmall0624.config.CookieUtil;
import com.atguigu.gmall0624.config.LoginRequire;
import com.atguigu.gmall0624.service.CartService;

import com.atguigu.gmall0624.service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class CartController {

    @Reference
    CartService cartService;
    @Reference
    ManageService manageService;


    //http://cart.gmall.com/addToCart
    @RequestMapping("addToCart")
    @LoginRequire(autoRedirect = false)
    public String addToCart(HttpServletRequest request, HttpServletResponse response){
        String skuNum = request.getParameter("skuNum");
        String skuId = request.getParameter("skuId");
        String userId = (String) request.getAttribute("userId");

        if(userId==null){//没登录,造一个
            //cookie中
            userId = CookieUtil.getCookieValue(request, "my-userId", false);
              if(userId==null){// 用户第一次添加购物车
                  userId = UUID.randomUUID().toString().replace("-", "");
                  CookieUtil.setCookie(request,response,"my-userId",userId,60*60*24*7,false);
              }
        }

        //添加购物车
        cartService.addToCart(skuId,userId,Integer.parseInt(skuNum));

        // 保存skuInfo 对象
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);
        request.setAttribute("skuInfo",skuInfo);
        // 保存添加的数量
        request.setAttribute("skuNum",skuNum);
        return "success";
    }


    @RequestMapping("cartList")//不管登录与否都有购物车，区别登录的userid在域里。未登录的在cookie
    @LoginRequire(autoRedirect = false)
    public String cartList(HttpServletRequest request,HttpServletResponse response){
        String userId = (String) request.getAttribute("userId");
        List<CartInfo> cartInfoList = new ArrayList<>();//最终的列表

        if (userId!=null){//登录状态，合并购物车
            //看未登录时有无商品，如果有合并，如果没有只显示登录状态的就可以
            String  userTempId = CookieUtil.getCookieValue(request, "my-userId", false);
            List<CartInfo> cartTempList = new ArrayList<>();//临时列表；
            if(userTempId!=null){
                cartTempList= cartService.getCartList(userTempId);//临时列表
                if (cartTempList!=null && cartTempList.size()>0){
                    //cartTempList未登录购物车，根据userId 查询登录购物车数据
                    cartInfoList = cartService.mergeToCartList(cartTempList,userId);//合并方法
                    //删除临时购物车数据
                    cartService.deleteCartList(userTempId);
                }
            }

            //当未登录时无购物车或无商品，只显示登录得即可
            if (userTempId==null || (cartTempList==null || cartTempList.size()==0) ){
                cartInfoList = cartService.getCartList(userId);
            }

        }else {
            //未登录状态，如果有商品，也能展示购物车列表，usrId是临时的，在cookie
          String  userTempId = CookieUtil.getCookieValue(request, "my-userId", false);
             if(userTempId!=null){
                 cartInfoList= cartService.getCartList(userTempId);
             }
        }

        request.setAttribute("cartInfoList",cartInfoList);
        return "cartList";
    }



    // 得到前台传递过来的参数！
    @RequestMapping("checkCart")
    @ResponseBody
    @LoginRequire(autoRedirect = false)
    public void checkCart(HttpServletRequest request,HttpServletResponse response){
        // 调用服务层
        String isChecked = request.getParameter("isChecked");
        String skuId = request.getParameter("skuId");
        // 获取用户Id
        String userId = (String) request.getAttribute("userId");

        // 判断用户的状态！
        if (userId==null) {
            // 未登录状态
            userId=CookieUtil.getCookieValue(request,"my-userId",false);
        }
        cartService.checkCart(isChecked, skuId, userId);

    }


    @RequestMapping("toTrade")
    @LoginRequire
    public String toTrade(HttpServletRequest request,HttpServletResponse response){
        // 获取userId
        String userId = (String) request.getAttribute("userId");
        List<CartInfo> cartTempList = null;
        // 获取cookie 中的my-userId
        String userTempId = CookieUtil.getCookieValue(request, "my-userId", false);
        if (userTempId!=null){
            cartTempList = cartService.getCartList(userTempId);
            if (cartTempList!=null && cartTempList.size()>0){
                // 合并勾选状态
                List<CartInfo> cartInfoList = cartService.mergeToCartList(cartTempList, userId);
                // 删除
                cartService.deleteCartList(userTempId);
            }
        }
        return "redirect://trade.gmall.com/trade";
    }











}
