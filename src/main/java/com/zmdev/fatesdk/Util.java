package com.zmdev.fatesdk;


import javax.servlet.http.HttpServletRequest;

public class Util {

    public static boolean expectsJson(HttpServletRequest r) {
        return (isAJAX(r) && !isPJAX(r) && acceptsrrAnyContentType(r)) || WantsJson(r);
    }

    public static boolean isAJAX(HttpServletRequest request) {
        return isXmlHttpRequest(request);
    }

    public static boolean isXmlHttpRequest(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    public static boolean isPJAX(HttpServletRequest request) {
        return "true".equals(request.getHeader("X-PJAX"));
    }

    public static boolean acceptsrrAnyContentType(HttpServletRequest request) {
        String accepts = request.getHeader("Accept");
        if (accepts != null) {
            String accept = accepts.split(",", 2)[0];
            return "".equals(accept) || "*/*".equals(accept) || "*".equals(accept);
        }
        return false;
    }

    public static boolean WantsJson(HttpServletRequest request) {
        String accepts = request.getHeader("Accept");
        if (accepts != null) {
            String accept = accepts.split(",", 2)[0];
            return accept.contains("/json") || accept.contains("+json");
        }
        return false;
    }
}
