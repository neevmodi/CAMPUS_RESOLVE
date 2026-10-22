package com.campusresolve.servlet;

import com.campusresolve.dao.ComplaintDAO;
import com.campusresolve.dao.UserDAO;
import com.campusresolve.exception.ComplaintException;
import com.campusresolve.model.*;
import com.campusresolve.service.ComplaintService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/manage-complaint")
public class ComplaintManagementServlet extends HttpServlet {
    private final ComplaintDAO complaints = new ComplaintDAO();
    private final UserDAO users = new UserDAO();
    private final ComplaintService service = new ComplaintService();

    @Override protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User actor = (User) request.getSession().getAttribute("user");
        long id;
        try {
            id = Long.parseLong(request.getParameter("id"));
            String action = request.getParameter("action");
            if ("assign".equals(action)) {
                if (actor.getRole() != Role.ADMIN) throw new ComplaintException("Only administrators can assign complaints.");
                String department = required(request.getParameter("department"), "Department is required.");
                String rawStaffId = request.getParameter("staffId");
                Long staffId = Long.valueOf(required(rawStaffId, "A staff member is required."));
                service.assign(id, department, staffId, actor.getId());
            } else if ("status".equals(action)) {
                if (actor.getRole() == Role.STAFF && !complaints.isAssignedTo(id, actor.getId())) {
                    throw new ComplaintException("You can update only complaints assigned to you.");
                }
                ComplaintStatus status = ComplaintStatus.valueOf(required(request.getParameter("status"), "Status is required."));
                service.updateStatus(id, status, actor.getId(), request.getParameter("note"));
            } else {
                throw new ComplaintException("Unknown complaint action.");
            }
            response.sendRedirect("complaint?id=" + id + "&message=Complaint+updated");
        } catch (IllegalArgumentException exception) {
            redirectError(response, idFrom(request), "Invalid complaint update.");
        } catch (ComplaintException exception) {
            redirectError(response, idFrom(request), exception.getMessage());
        }
    }

    private String required(String value, String message) {
        if (value == null || value.isBlank()) throw new ComplaintException(message);
        return value;
    }

    private long idFrom(HttpServletRequest request) {
        try { return Long.parseLong(request.getParameter("id")); } catch (NumberFormatException exception) { return 0; }
    }

    private void redirectError(HttpServletResponse response, long id, String message) throws IOException {
        response.sendRedirect("complaint?id=" + id + "&error="
                + URLEncoder.encode(message == null ? "Update failed." : message, StandardCharsets.UTF_8));
    }
}
