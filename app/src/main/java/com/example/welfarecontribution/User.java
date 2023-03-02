package com.example.welfarecontribution;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class User {
    String id, name, phoneNumber, email, type;
    boolean awarded;

    public  User(){
        awarded = false;
    }

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

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("awarded", this.awarded);
        result.put("id", this.id);
        result.put("phoneNumber", this.phoneNumber);
        return  result;
    }
}
