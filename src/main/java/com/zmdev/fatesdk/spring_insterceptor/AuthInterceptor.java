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

    private String ticketIdCookieKey;
    private String userIdCookieKey;
    private LoginChecker loginChecker;
    private Fate fate;

    public AuthInterceptor(LoginChecker loginChecker, Fate fate, String ticketIdCookieKey, String userIdCookieKey) {
        this.loginChecker = loginChecker;
        this.ticketIdCookieKey = ticketIdCookieKey;
        this.userIdCookieKey = userIdCookieKey;
        this.fate = fate;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie ticketIdCookie = findCookie(ticketIdCookieKey, request);
        if (ticketIdCookie != null) {
            LoginCheckResOrBuilder loginCheckRes = loginChecker.Check(ticketIdCookie.getValue());
            request.setAttribute("loginCheckRes", loginCheckRes);
            if (loginCheckRes.getIsLogin()) { // 登录了
                Cookie userIdCookie = findCookie(userIdCookieKey, request);
                String userIdStr = String.valueOf(loginCheckRes.getUserId());
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
        }

        //未登录
        if (Util.expectsJson(request)) {
            throw new AuthenticationException();
        } else {
            fate.redirectToLogin(request, response);
        }
        return false;

    }

    private Cookie findCookie(String cookieName, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie;
                }
            }
        }
        return null;
    }
}
