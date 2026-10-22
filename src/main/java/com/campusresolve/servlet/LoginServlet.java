package com.campusresolve.servlet;

import com.campusresolve.dao.UserDAO;
import com.campusresolve.exception.DatabaseException;
import com.campusresolve.model.User;
import com.campusresolve.util.PasswordUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private final UserDAO users = new UserDAO();
    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        if (email == null || password == null || email.isBlank() || password.isBlank()) {
            response.sendRedirect("login.jsp?error=Email+and+password+are+required"); return;
        }
        java.util.Optional<User> authenticated;
        try {
            authenticated = users.authenticate(email.trim(), password);
        } catch (DatabaseException exception) {
            response.sendRedirect("login.jsp?error=Database+temporarily+unavailable");
            return;
        }
        if (authenticated.isEmpty()) { response.sendRedirect("login.jsp?error=Invalid+credentials"); return; }
        HttpSession existingSession = request.getSession(false);
        if (existingSession != null) existingSession.invalidate();
        HttpSession session = request.getSession(true);
        session.setAttribute("user", authenticated.get());
        User user = authenticated.get();
        String target = switch (user.getRole()) { case ADMIN -> "admin/dashboard"; case STAFF -> "staff/dashboard"; case STUDENT -> "student/dashboard"; };
        response.sendRedirect(target);
    }
}
