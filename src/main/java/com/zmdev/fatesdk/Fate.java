package com.zmdev.fatesdk;


import com.zmdev.fatesdk.pb.LoginCheckResOrBuilder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class Fate {

    private String fateURL;
    private int appId;
    private String ticketIdCookieKey;
    private LoginChecker loginChecker;

    public Fate(String fateURL, int appId, String ticketIdCookieKey, LoginChecker loginChecker) {
        this.fateURL = fateURL;
        this.appId = appId;
        this.ticketIdCookieKey = ticketIdCookieKey;
        this.loginChecker = loginChecker;
    }

    public void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuffer callback = request.getRequestURL();
        redirectToLoginWithCallback(callback.toString(), response);
    }

    public void redirectToLoginWithCallback(String callback, HttpServletResponse response) throws IOException {
        String redirectUrl = fateURL + "/?app_id=" + appId + "&callback=" + callback;
        response.sendRedirect(redirectUrl);
    }

    public void saveLoginRes(HttpServletRequest request) {
        String loginResKey = "loginCheckRes";
        if (request.getAttribute(loginResKey) == null) {
            Cookie ticketIdCookie = findCookie(ticketIdCookieKey, request);
            if (ticketIdCookie != null) {
                LoginCheckResOrBuilder loginCheckRes = loginChecker.Check(ticketIdCookie.getValue());
                request.setAttribute(loginResKey, loginCheckRes);
            }
        }
    }

    public static Cookie findCookie(String cookieName, HttpServletRequest request) {
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
