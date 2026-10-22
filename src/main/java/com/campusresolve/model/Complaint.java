package com.campusresolve.model;

import java.time.LocalDateTime;

public class Complaint {
    private long id;
    private long studentId;
    private String title;
    private String description;
    private String location;
    private String category;
    private Priority priority;
    private String department;
    private ComplaintStatus status;
    private Long assignedStaffId;
    private String resolutionNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Complaint() { }

    public Complaint(long studentId, String title, String description, String location) {
        this.studentId = studentId;
        this.title = title;
        this.description = description;
        this.location = location;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getStudentId() { return studentId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public ComplaintStatus getStatus() { return status; }
    public void setStatus(ComplaintStatus status) { this.status = status; }
    public Long getAssignedStaffId() { return assignedStaffId; }
    public void setAssignedStaffId(Long assignedStaffId) { this.assignedStaffId = assignedStaffId; }
    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
