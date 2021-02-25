package com.macode.paynothing.utilities;

public class Items {

    private String dateItemPosted, userId, title, imageUrl, category, condition, brand, model, type, description, location, latitude, longitude;
    private Boolean pickUpOnly, sold;

    public Items() {
    }

    public Items(String dateItemPosted, String userId, String title, String imageUrl, String category, String condition, String brand, String model, String type, String description, String location, String latitude, String longitude, Boolean pickUpOnly, Boolean sold) {
        this.dateItemPosted = dateItemPosted;
        this.userId = userId;
        this.title = title;
        this.imageUrl = imageUrl;
        this.category = category;
        this.condition = condition;
        this.brand = brand;
        this.model = model;
        this.type = type;
        this.description = description;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.pickUpOnly = pickUpOnly;
        this.sold = sold;
    }

    public String getDateItemPosted() {
        return dateItemPosted;
    }

    public void setDateItemPosted(String dateItemPosted) {
        this.dateItemPosted = dateItemPosted;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Boolean getPickUpOnly() {
        return pickUpOnly;
    }

    public void setPickUpOnly(Boolean pickUpOnly) {
        this.pickUpOnly = pickUpOnly;
    }

    public Boolean getSold() {
        return sold;
    }

    public void setSold(Boolean sold) {
        this.sold = sold;
    }
}
