package com.zmdev.fatesdk.controller;

import com.zmdev.fatesdk.FateConfiguration;
import com.zmdev.fatesdk.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class AuthController {

    private String ticketIdCookieKey;
    private String userIdCookieKey;

    public AuthController(@Autowired FateConfiguration fateConfiguration) {
        this.ticketIdCookieKey = fateConfiguration.getTicketIdCookieKey();
        this.userIdCookieKey = fateConfiguration.getUserIdCookieKey();
    }

    @GetMapping("/fate/callback")
    @ResponseBody
    public void callback(
            @RequestParam(defaultValue = "", name = "ticket_id") String ticketId,
            @RequestParam(defaultValue = "", name = "user_id") String userId,
            @RequestParam(defaultValue = "0", name = "expired_at") long expiredAt,
            @RequestParam(defaultValue = "/") String callback,
            HttpServletResponse response
    ) throws BadRequestException, IOException {
        if (ticketId.equals(""))
            throw new BadRequestException("ticket_id 参数不存在");

        if (userId.equals(""))
            throw new BadRequestException("user_id 参数不存在");


        int maxAge;
        if (expiredAt == 0)
            maxAge = ((Long) (expiredAt - (System.currentTimeMillis() / 1000))).intValue();
        else
            maxAge = 3600 * 24 * 7;

        Cookie cookieTicketId = new Cookie(ticketIdCookieKey, ticketId);
        cookieTicketId.setMaxAge(maxAge);
        cookieTicketId.setPath("/");
        cookieTicketId.setHttpOnly(true);
        Cookie cookieUserId = new Cookie(userIdCookieKey, userId);
        cookieUserId.setMaxAge(maxAge);
        cookieUserId.setPath("/");
        response.addCookie(cookieTicketId);
        response.addCookie(cookieUserId);
        response.sendRedirect(callback);
    }

    @GetMapping("/fate/logout")
    @ResponseBody
    public void logout(HttpServletResponse response) {
        Cookie cookieTicketId = new Cookie(ticketIdCookieKey, "");
        cookieTicketId.setMaxAge(0);
        Cookie cookieUserId = new Cookie(userIdCookieKey, "");
        cookieUserId.setMaxAge(0);
        response.addCookie(cookieTicketId);
        response.addCookie(cookieUserId);
    }
}
