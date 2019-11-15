package com.myapps.toualbiamine.basketbusiness.Model;


public class User {
        private String name;
        private String password;
        private String email;
        private boolean isStaff;

    public User() { }

    public User(String email, String name, String password, boolean isStaff) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.isStaff = isStaff;
    }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public boolean getIsStaff() { return isStaff; }

    public void setIsStaff() { this.isStaff = isStaff; }

}

