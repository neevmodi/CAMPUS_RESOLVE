package com.campusresolve.servlet;

import com.campusresolve.dao.ComplaintDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.IOException;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {
    private final ComplaintDAO complaints = new ComplaintDAO();
    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("complaints", complaints.search(request.getParameter("q"), request.getParameter("status"), request.getParameter("priority")));
        request.getRequestDispatcher("/admin-dashboard.jsp").forward(request, response);
    }
}
