package com.campusresolve.servlet;

import com.campusresolve.dao.UserDAO;
import com.campusresolve.exception.DatabaseException;
import com.campusresolve.util.PasswordUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private final UserDAO users = new UserDAO();
    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("name"), email = request.getParameter("email"), password = request.getParameter("password"), confirm = request.getParameter("confirm");
        if (name == null || name.isBlank() || email == null || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$") || password == null || password.length() < 8 || !password.equals(confirm)) {
            response.sendRedirect("register.jsp?error=Please+check+your+name%2C+email%2C+password+and+confirmation"); return;
        }
        try {
            users.createStudent(name.trim(), email.trim(), PasswordUtil.hash(password));
            response.sendRedirect("login.jsp?message=Registration+successful");
        } catch (DatabaseException exception) {
            response.sendRedirect("register.jsp?error=That+email+may+already+be+registered+or+the+database+is+unavailable");
        }
    }
}
