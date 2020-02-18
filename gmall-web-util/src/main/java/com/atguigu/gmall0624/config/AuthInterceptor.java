package com.atguigu.gmall0624.config;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall0624.utils.HttpClientUtil;
import io.jsonwebtoken.impl.Base64UrlCodec;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getParameter("newToken");
        if (token!=null){
            CookieUtil.setCookie(request,response,"token",token,WebConst.COOKIE_MAXAGE,false);
        }
        if(token==null){
            token = CookieUtil.getCookieValue(request, "token", false);
        }

        if (token!=null){
            //读取token
            Map map = getUserMapByToken(token);
            String nickName = (String) map.get("nickName");
            request.setAttribute("nickName",nickName);
        }

        HandlerMethod handlerMethod =(HandlerMethod) handler;
        LoginRequire loginRequireAnnotation  = handlerMethod.getMethodAnnotation(LoginRequire.class);
        if(loginRequireAnnotation!=null){//有注解去认证
            String remoteAddr = request.getHeader("x-forwarded-for");
            String result = HttpClientUtil.doGet(WebConst.VERIFY_ADDRESS + "?token=" + token + "&currentIp=" + remoteAddr);
            if ("success".equals(result)){//认证成功
                Map map = getUserMapByToken(token);
                String userId =(String) map.get("userId");// 登录成功 记录一下谁登录了。记录userId 即可！
                request.setAttribute("userId",userId); // 保存到作用域
                return true; // 放行！

            }else {//有注解，认证失败
                if(loginRequireAnnotation.autoRedirect()){//且属性值为必须----去登录页面
                    String requestURL  = request.getRequestURL().toString();
                    String encodeURL = URLEncoder.encode(requestURL, "UTF-8");
                    // 重定向到登录的url
                    response.sendRedirect(WebConst.LOGIN_ADDRESS+"?originUrl="+encodeURL);
                    return false;

                }

            }

        }


        return true;
    }

    private Map getUserMapByToken(String token) {
        //解码token过程
        String tokenUserInfo = StringUtils.substringBetween(token, ".");
        Base64UrlCodec base64UrlCodec = new Base64UrlCodec();
        byte[] tokenBytes  = base64UrlCodec.decode(tokenUserInfo);

        String tokenJson =  null;
        try {
            tokenJson= new String(tokenBytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Map map = JSON.parseObject(tokenJson, Map.class);
        return map;


    }
}
