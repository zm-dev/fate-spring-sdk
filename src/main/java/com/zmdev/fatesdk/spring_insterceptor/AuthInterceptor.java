package com.zmdev.fatesdk.spring_insterceptor;


import com.zmdev.fatesdk.Fate;
import com.zmdev.fatesdk.LoginChecker;
import com.zmdev.fatesdk.Util;
import com.zmdev.fatesdk.exception.AuthenticationException;
import com.zmdev.fatesdk.pb.LoginCheckResOrBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthInterceptor extends HandlerInterceptorAdapter {
    private Fate fate;
    private String userIdCookieKey;

    public AuthInterceptor(Fate fate, String userIdCookieKey) {
        this.fate = fate;
        this.userIdCookieKey = userIdCookieKey;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        fate.saveLoginRes(request);
        if (Util.isLogin(request)) {
            Cookie userIdCookie = Fate.findCookie(userIdCookieKey, request);
            String userIdStr = String.valueOf(Util.userId(request));
            // 如果用户id cookie不存在或者userId cookie中的值错误 重新设置userId cookie
            if (userIdCookie == null || !userIdStr.equals(userIdCookie.getValue())) {
                if (userIdCookie == null) {
                    userIdCookie = new Cookie(userIdCookieKey, userIdStr);
                } else {
                    userIdCookie.setValue(userIdStr);
                }
                response.addCookie(userIdCookie);
            }
            return true;
        }

        //未登录
        if (Util.expectsJson(request)) {
            throw new AuthenticationException();
        } else {
            fate.redirectToLogin(request, response);
            return false;
        }

    }


}
