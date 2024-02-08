package com.example.dermdetect.viewmodels;


public class User {
    private static User instance;
    private String userId, userName, userEmail, imageBase64;

    private User(){}

    public static synchronized User getInstance(){
        if (instance == null){
            instance = new User();
        }
        return instance;
    }


    //Setters
    public void setUserId(String userId){
        this.userId = userId;
    }
    public void setUsername(String userName){
        this.userName = userName;
    }
    public void setUserEmail(String userEmail){
        this.userEmail = userEmail;
    }
    public void setImageBase64(String imageBase64){
        this.imageBase64 = imageBase64;
    }

    //Getters
    public String getUserId(){
        return userId;
    }
    public String getUserName(){
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }
    public String getImageBase64() {
        return imageBase64;
    }
}
