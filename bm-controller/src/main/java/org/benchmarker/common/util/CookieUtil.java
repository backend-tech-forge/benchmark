package org.benchmarker.common.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * CookieUtils
 */
public class CookieUtil {

    /**
     * remove cookie by cookie name
     *
     * @param response   {@link HttpServletResponse}
     * @param cookieName {@link String}
     */
    public static void removeCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    /**
     * return cookie value by cookie name
     *
     * @param request    {@link HttpServletRequest}
     * @param cookieName {@link String}
     * @return cookieValue
     */
    public static String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
