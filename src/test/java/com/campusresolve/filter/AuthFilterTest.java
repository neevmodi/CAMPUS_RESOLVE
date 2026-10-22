package com.campusresolve.filter;

import com.campusresolve.model.Role;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class AuthFilterTest {
    @Test void restrictsRoleSpecificAreas() {
        assertTrue(AuthFilter.isAuthorized("/admin/dashboard", Role.ADMIN));
        assertFalse(AuthFilter.isAuthorized("/admin/dashboard", Role.STAFF));
        assertTrue(AuthFilter.isAuthorized("/staff/dashboard", Role.STAFF));
        assertFalse(AuthFilter.isAuthorized("/student/dashboard", Role.ADMIN));
        assertFalse(AuthFilter.isAuthorized("/manage-complaint", Role.STUDENT));
        assertTrue(AuthFilter.isAuthorized("/complaint", Role.ADMIN));
        assertTrue(AuthFilter.isAuthorized("/complaint-details.jsp", Role.STAFF));
        assertFalse(AuthFilter.isAuthorized("/submit-complaint.jsp", Role.ADMIN));
    }
}
