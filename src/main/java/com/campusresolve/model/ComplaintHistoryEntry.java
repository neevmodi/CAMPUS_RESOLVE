package com.campusresolve.model;

import java.time.LocalDateTime;

public record ComplaintHistoryEntry(ComplaintStatus status, long changedBy, String note, LocalDateTime changedAt) { }
