package com.campusresolve.model;

public final class Student extends User {
    public Student(long id, String name, String email) { super(id, name, email, Role.STUDENT); }
}
