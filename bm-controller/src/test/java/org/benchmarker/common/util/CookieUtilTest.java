package org.benchmarker.common.util;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class CookieUtilTest {

    @Test
    @DisplayName("쿠키 제거 성공")
    public void testRemoveCookie() {
        // Given
        MockHttpServletResponse response = new MockHttpServletResponse();
        String cookieName = "testCookie";

        // When
        CookieUtil.removeCookie(response, cookieName);
        Cookie[] cookies = response.getCookies();

        // Then
        assertEquals(1, cookies.length);
        assertEquals(cookieName, cookies[0].getName());
        assertEquals("", cookies[0].getValue());
        assertEquals(0, cookies[0].getMaxAge());
    }

    @Test
    @DisplayName("쿠키 값 확인 성공")
    public void testGetCookieValue() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        String cookieName = "testCookie";
        String cookieValue = "testValue";
        Cookie cookie = new Cookie(cookieName, cookieValue);
        request.setCookies(cookie);

        // When
        String retrievedCookieValue = CookieUtil.getCookieValue(request, cookieName);

        // Then
        assertEquals(cookieValue, retrievedCookieValue);
    }

    @Test
    @DisplayName("쿠키 값이 없을 때 null 반환 확인")
    public void testGetNonExistentCookieValue() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        String cookieName = "nonExistentCookie";

        // When
        String retrievedCookieValue = CookieUtil.getCookieValue(request, cookieName);

        // Then
        assertNull(retrievedCookieValue);
    }

}