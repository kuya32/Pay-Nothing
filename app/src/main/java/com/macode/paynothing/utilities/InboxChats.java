package com.macode.paynothing.utilities;

public class InboxChats {
    private String sellerId, itemKey, buyerId, mostRecentMessage, dateOfMostRecentMessage;

    public InboxChats() {
    }

    public InboxChats(String sellerId, String itemKey, String buyerId, String mostRecentMessage, String dateOfMostRecentMessage) {
        this.sellerId = sellerId;
        this.itemKey = itemKey;
        this.buyerId = buyerId;
        this.mostRecentMessage = mostRecentMessage;
        this.dateOfMostRecentMessage = dateOfMostRecentMessage;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getItemKey() {
        return itemKey;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getMostRecentMessage() {
        return mostRecentMessage;
    }

    public void setMostRecentMessage(String mostRecentMessage) {
        this.mostRecentMessage = mostRecentMessage;
    }

    public String getDateOfMostRecentMessage() {
        return dateOfMostRecentMessage;
    }

    public void setDateOfMostRecentMessage(String dateOfMostRecentMessage) {
        this.dateOfMostRecentMessage = dateOfMostRecentMessage;
    }
}
