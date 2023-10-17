package com.example.dermdetect.query;

public class QueryRequest {
    String userID;
    String description;

    public QueryRequest() {
        // Construtor vazio necessário para Firebase Firestore
    }

    public QueryRequest(String userID, String description) {
        this.userID = userID;
        this.description = description;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
