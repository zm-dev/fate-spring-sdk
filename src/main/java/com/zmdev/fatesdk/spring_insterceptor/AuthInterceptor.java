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

    public AuthInterceptor(Fate fate) {
        this.fate = fate;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (Util.isLogin(request)) {
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
