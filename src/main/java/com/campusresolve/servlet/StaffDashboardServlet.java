package com.campusresolve.servlet;

import com.campusresolve.dao.ComplaintDAO;
import com.campusresolve.model.User;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.IOException;

@WebServlet("/staff/dashboard")
public class StaffDashboardServlet extends HttpServlet {
    private final ComplaintDAO complaints = new ComplaintDAO();
    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        request.setAttribute("complaints", complaints.searchAssignedTo(user.getId(), request.getParameter("q"), request.getParameter("status"), request.getParameter("priority")));
        request.getRequestDispatcher("/staff-dashboard.jsp").forward(request, response);
    }
}
