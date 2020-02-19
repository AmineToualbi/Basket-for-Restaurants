package com.myapps.toualbiamine.basketbusiness.Common;

import com.myapps.toualbiamine.basketbusiness.Model.User;

//Common data that is shared across the app.
public class Common {

    public static User currentUser;
    public static final String DELETE = "Delete";
    public static final String UPDATE = "Update";
    public static final String USER_KEY = "User";
    public static final String PWD_KEY = "Password";
    public static final String NAME_KEY = "Name";

    public static String convertCodeToStatus(String code) {
        if(code.equals("0")) return "Placed";
        if(code.equals("1")) return "Ready";
        if(code.equals("2")) return "Picked up";
        return "";
    }

}
