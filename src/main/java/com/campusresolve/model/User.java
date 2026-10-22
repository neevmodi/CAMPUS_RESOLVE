package com.campusresolve.model;

import java.io.Serializable;

public abstract class User implements Serializable {
    private final long id;
    private final String name;
    private final String email;
    private final Role role;

    protected User(long id, String name, String email, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }
}
