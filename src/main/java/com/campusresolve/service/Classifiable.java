package com.campusresolve.service;

import com.campusresolve.classifier.ClassificationResult;

public interface Classifiable {
    ClassificationResult classify(String title, String description);
}
