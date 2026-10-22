package com.campusresolve.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

/** Adds a per-session token to pages and rejects forged state-changing requests. */
@WebFilter("/*")
public class CsrfFilter implements Filter {
    private static final String SESSION_KEY = "csrfToken";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest http = (HttpServletRequest) request;
        if (isStatic(http)) { chain.doFilter(request, response); return; }
        HttpSession session = http.getSession(true);
        String token = (String) session.getAttribute(SESSION_KEY);
        if (token == null) {
            byte[] bytes = new byte[32];
            new SecureRandom().nextBytes(bytes);
            token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
            session.setAttribute(SESSION_KEY, token);
        }
        request.setAttribute(SESSION_KEY, token);
        if ("POST".equalsIgnoreCase(http.getMethod()) && !isPublicForm(http) && !token.equals(http.getParameter("csrfToken"))) {
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, "Your form session has expired. Please try again.");
            return;
        }
        chain.doFilter(request, response);
    }

    private boolean isStatic(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/images/") || path.equals("/favicon.ico");
    }
    private boolean isPublicForm(HttpServletRequest request) {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return path.equals("/login") || path.equals("/register");
    }
}
