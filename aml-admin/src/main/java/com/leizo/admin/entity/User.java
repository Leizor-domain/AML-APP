package com.leizo.admin.entity;

public class User {

    private String username;
    private String role;

    public User(String userName, String role) {
        this.username = userName;
        this.role = role;
    }
    public String getUserName() {
        return username;
    }
    public String getRole() {
        return role;
    }
    public boolean isAnalyst(){
        return "analyst".equalsIgnoreCase(role);
    }
    public boolean isAdmin(){
        return "admin".equalsIgnoreCase(role);
    }

    public boolean isViewer(){
        return "viewer".equalsIgnoreCase(role);
    }

    public boolean isSupervisor(){
        return "supervisor".equalsIgnoreCase(role);
    }

} 