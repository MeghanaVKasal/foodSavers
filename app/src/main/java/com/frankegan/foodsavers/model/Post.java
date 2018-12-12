package com.frankegan.foodsavers.model;

import com.google.firebase.firestore.GeoPoint;

import java.util.List;

public class Post {
    GeoPoint location;
    String address;
    String pictureURL;
    String description;
    boolean claimed;
    String producer;
    String consumer;
    List<String> tags;

    public Post() {
        //required for Firebase deserialization
    }

    public Post(
            GeoPoint location,
            String address,
            List<String> tags,
            String pictureURL,
            String description,
            boolean claimed,
            String producer,
            String consumer
    ) {
        this.location = location;
        this.address = address;
        this.tags = tags;
        this.pictureURL = pictureURL;
        this.description = description;
        this.claimed = claimed;
        this.producer = producer;
        this.consumer = consumer;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public String getAddress() {
        return address;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getPictureURL() {
        return pictureURL;
    }

    public String getDescription() {
        return description;
    }

    public boolean isClaimed() {
        return claimed;
    }

    public String getProducer() {
        return producer;
    }

    public String getConsumer() {
        return consumer;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setClaimed(boolean claimed) {
        this.claimed = claimed;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public void setConsumer(String consumer) {
        this.consumer = consumer;
    }
}
