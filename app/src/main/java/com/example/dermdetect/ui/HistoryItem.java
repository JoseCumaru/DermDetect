package com.example.dermdetect.ui;

import android.graphics.Bitmap;

import java.util.Date;

public class HistoryItem {
    private Bitmap imageURL;
    private String detectedDisease;
    private double confidence;
    private Date timestamp;

    public HistoryItem() {
        // Construtor vazio necessário para Firestore
    }

    public HistoryItem(Bitmap imageURL, String detectedDisease, double confidence, Date timestamp) {
        this.imageURL = imageURL;
        this.detectedDisease = detectedDisease;
        this.confidence = confidence;
        this.timestamp = timestamp;
    }


    public Bitmap getImageURL() {
        return imageURL;
    }

    public void setImageURL(Bitmap imageURL) {
        this.imageURL = imageURL;
    }

    public String getDetectedDisease() {
        return detectedDisease;
    }

    public void setDetectedDisease(String detectedDisease) {
        this.detectedDisease = detectedDisease;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public Date getTimestamp(){
        return timestamp;
    }
}
