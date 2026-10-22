package com.campusresolve.filter;

import com.campusresolve.model.Role;
import com.campusresolve.model.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebFilter(urlPatterns = {"/student/*", "/admin/*", "/staff/*", "/submit-complaint", "/submit-complaint.jsp",
        "/complaint", "/complaint-details.jsp", "/manage-complaint", "/student-dashboard.jsp",
        "/staff-dashboard.jsp", "/admin-dashboard.jsp"})
public class AuthFilter implements Filter {
    @Override public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest http = (HttpServletRequest) request;
        User user = (User) http.getSession().getAttribute("user");
        String path = http.getRequestURI().substring(http.getContextPath().length());
        if (user == null) { ((HttpServletResponse) response).sendRedirect(http.getContextPath() + "/login.jsp"); return; }
        if (!isAuthorized(path, user.getRole())) {
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, "You are not authorized for this area.");
            return;
        }
        chain.doFilter(request, response);
    }

    public static boolean isAuthorized(String path, Role role) {
        if (path.startsWith("/admin")) return role == Role.ADMIN;
        if (path.startsWith("/staff")) return role == Role.STAFF;
        if (path.startsWith("/student") || path.equals("/submit-complaint")) return role == Role.STUDENT;
        if (path.equals("/student-dashboard.jsp")) return role == Role.STUDENT;
        if (path.equals("/staff-dashboard.jsp")) return role == Role.STAFF;
        if (path.equals("/admin-dashboard.jsp")) return role == Role.ADMIN;
        if (path.equals("/submit-complaint.jsp")) return role == Role.STUDENT;
        if (path.equals("/complaint-details.jsp")) return role != null;
        if (path.equals("/manage-complaint") || path.equals("/complaint")) return role != Role.STUDENT;
        return true;
    }
}
