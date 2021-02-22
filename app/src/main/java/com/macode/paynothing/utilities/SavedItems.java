package com.macode.paynothing.utilities;

public class SavedItems {
    private String dateItemSaved, title, imageUrl;

    public SavedItems() {
    }

    public SavedItems(String dateItemSaved, String title, String imageUrl) {
        this.dateItemSaved = dateItemSaved;
        this.title = title;
        this.imageUrl = imageUrl;
    }

    public String getDateItemSaved() {
        return dateItemSaved;
    }

    public void setDateItemSaved(String dateItemSaved) {
        this.dateItemSaved = dateItemSaved;
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
}
