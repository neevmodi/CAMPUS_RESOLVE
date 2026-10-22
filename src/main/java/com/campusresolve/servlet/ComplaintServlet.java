package com.campusresolve.servlet;

import com.campusresolve.model.*;
import com.campusresolve.service.ComplaintService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/submit-complaint")
public class ComplaintServlet extends HttpServlet {
    private final ComplaintService service = new ComplaintService();
    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) request.getSession().getAttribute("user");
        Complaint complaint = new Complaint(user.getId(), request.getParameter("title"), request.getParameter("description"), request.getParameter("location"));
        try { service.submit(complaint); response.sendRedirect("student/dashboard?message=Complaint+submitted"); }
        catch (RuntimeException exception) { response.sendRedirect("submit-complaint.jsp?error=" + java.net.URLEncoder.encode(exception.getMessage(), java.nio.charset.StandardCharsets.UTF_8)); }
    }
}
