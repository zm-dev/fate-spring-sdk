package com.zmdev.fatesdk;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class Fate {

    private String fateURL;
    private int appId;

    public Fate(String fateURL, int appId) {
        this.fateURL = fateURL;
        this.appId = appId;
    }

    public void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuffer callback = request.getRequestURL();
        redirectToLoginWithCallback(callback.toString(), response);
    }

    public void redirectToLoginWithCallback(String callback, HttpServletResponse response) throws IOException {
        String redirectUrl = fateURL + "/?app_id=" + appId + "&callback=" + callback;
        response.sendRedirect(redirectUrl);
    }

}
