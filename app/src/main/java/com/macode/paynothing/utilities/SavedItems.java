package com.macode.paynothing.utilities;

public class SavedItems {
    private String dateItemSaved, title, imageUrl, itemKey;

    public SavedItems() {
    }

    public SavedItems(String dateItemSaved, String title, String imageUrl, String itemKey) {
        this.dateItemSaved = dateItemSaved;
        this.title = title;
        this.imageUrl = imageUrl;
        this.itemKey = itemKey;
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

    public String getItemKey() {
        return itemKey;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }
}
