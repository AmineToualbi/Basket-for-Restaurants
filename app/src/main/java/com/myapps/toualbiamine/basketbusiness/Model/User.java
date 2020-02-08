package com.myapps.toualbiamine.basketbusiness.Model;

public class User {

        private String name;
        private String password;
        private String restaurantID;
        private boolean isStaff;

    public User() { }

    public User(String email, String name, String password, boolean isStaff) {
        this.restaurantID = email;
        this.name = name;
        this.password = password;
        this.isStaff = isStaff;
    }

    public String getRestaurantID() { return restaurantID; }
    public void setRestaurantID(String restaurantID) { this.restaurantID = restaurantID; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public boolean getIsStaff() { return isStaff; }
    public void setIsStaff(boolean isStaff) { this.isStaff = isStaff; }

}

