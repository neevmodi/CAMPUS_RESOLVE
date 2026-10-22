package com.campusresolve.model;

public final class Staff extends User {
    public Staff(long id, String name, String email) { super(id, name, email, Role.STAFF); }
}
