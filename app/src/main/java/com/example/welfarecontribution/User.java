package com.example.welfarecontribution;

import java.util.HashMap;
import java.util.Map;

public class User {
    String id, name, phoneNumber, email, type;
    boolean awarded;
    public  User(){
        awarded = false;
    }

    public static boolean isAdmin = false; // use this one to check if a user is admin or not.

    // getters and setters
    public String getId(){ return id;}
    public String getName() {
        return name;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public String getEmail() {return email;}
    public String getType(){return type;}
    public boolean isAwarded(){ return  awarded;}

    public void setAwarded(boolean awarded){ this.awarded = awarded;}
    public void setName(String name){
        this.name = name;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setType(String type){ this.type = type;}
    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    // user data as key-value pair(HashMap)
   // @Exclude
    public Map<String, Object> toMap(){
        Map<String, Object> userData = new HashMap<>();
        userData.put("id", id);
        userData.put("name", name);
        userData.put("phoneNumber", phoneNumber);
        userData.put("email", email);
        userData.put("awarded", awarded);
        userData.put("type","user");
        return  userData;
    }
}
