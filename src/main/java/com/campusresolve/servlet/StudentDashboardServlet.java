package com.campusresolve.servlet;

import com.campusresolve.dao.ComplaintDAO;
import com.campusresolve.model.User;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.IOException;
import com.campusresolve.model.ComplaintStatus;

@WebServlet("/student/dashboard")
public class StudentDashboardServlet extends HttpServlet {
    private final ComplaintDAO complaints = new ComplaintDAO();
    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user");
        var studentComplaints = complaints.findByStudent(user.getId());
        request.setAttribute("complaints", studentComplaints);
        request.setAttribute("openCount", studentComplaints.stream()
                .filter(complaint -> complaint.getStatus() != ComplaintStatus.CLOSED)
                .count());
        request.getRequestDispatcher("/student-dashboard.jsp").forward(request, response);
    }
}
