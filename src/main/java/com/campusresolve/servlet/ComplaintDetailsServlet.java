package com.campusresolve.servlet;

import com.campusresolve.dao.ComplaintDAO;
import com.campusresolve.dao.ComplaintHistoryDAO;
import com.campusresolve.dao.UserDAO;
import com.campusresolve.model.Complaint;
import com.campusresolve.model.User;
import com.campusresolve.model.Role;
import com.campusresolve.service.ComplaintService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.IOException;

@WebServlet("/complaint")
public class ComplaintDetailsServlet extends HttpServlet {
    private final ComplaintDAO complaints = new ComplaintDAO();
    private final ComplaintHistoryDAO history = new ComplaintHistoryDAO();
    private final UserDAO users = new UserDAO();
    @Override protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long id;
        try { id = Long.parseLong(request.getParameter("id")); } catch (NumberFormatException error) { response.sendError(400, "Invalid complaint id."); return; }
        Complaint complaint = complaints.findById(id).orElse(null);
        User user = (User) request.getSession().getAttribute("user");
        boolean studentDoesNotOwn = user.getRole() == Role.STUDENT && complaint != null && complaint.getStudentId() != user.getId();
        boolean staffNotAssigned = user.getRole() == Role.STAFF && complaint != null && !Long.valueOf(user.getId()).equals(complaint.getAssignedStaffId());
        if (complaint == null || studentDoesNotOwn || staffNotAssigned) { response.sendError(404); return; }
        request.setAttribute("complaint", complaint);
        request.setAttribute("history", history.findByComplaint(id));
        request.setAttribute("staff", users.findStaff());
        request.setAttribute("nextStatuses", ComplaintService.nextStatuses(complaint.getStatus()));
        request.getRequestDispatcher("/complaint-details.jsp").forward(request, response);
    }
}
