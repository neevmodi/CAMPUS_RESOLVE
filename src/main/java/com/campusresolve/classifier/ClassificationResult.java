package com.campusresolve.classifier;

import com.campusresolve.model.Priority;

public record ClassificationResult(String category, Priority priority, String department, String explanation) { }
