package com.myapps.toualbiamine.basketbusiness.Model;

public class Food {
    private String name;
    private String image;
    private String description;
    private String restaurantID;

    public Food() {}

    public Food(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setDescription(String descriotion) { this.description = description; }

    public String getDescription() { return description; }

    public void setRestaurantID(String restaurantID) { this.restaurantID = restaurantID; }

    public String getRestaurantID() { return restaurantID; }
}
