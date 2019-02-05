package com.efunhub.pickpriceadmin.Model;

/**
 * Created by Admin on 07-12-2018.
 */

public class Dealer {

    public String dealerId;
    public String dealerName;
    public String dealerContact;
    public String dealerEmail;
    public String dealerProfileImage;
    public String dealerCountry;
    public String dealerState;
    public String dealerCity;
    public String dealerArea;
    public String dealerAddress;
    public String dealerPincode;
    public String dealerStatus;
    public String dealerCreatedAt;

    public Dealer() {
    }

    public Dealer(String dealerId, String dealerName, String dealerContact, String dealerEmail, String dealerProfileImage, String dealerCountry, String dealerState, String dealerCity, String dealerArea,
                  String dealerAddress, String dealerPincode, String dealerStatus, String dealerCreatedAt) {
        this.dealerId = dealerId;
        this.dealerName = dealerName;
        this.dealerContact = dealerContact;
        this.dealerEmail = dealerEmail;
        this.dealerProfileImage = dealerProfileImage;
        this.dealerCountry = dealerCountry;
        this.dealerState = dealerState;
        this.dealerCity = dealerCity;
        this.dealerArea = dealerArea;
        this.dealerAddress = dealerAddress;
        this.dealerPincode = dealerPincode;
        this.dealerStatus = dealerStatus;
        this.dealerCreatedAt = dealerCreatedAt;
    }

    public String getDealerId() {
        return dealerId;
    }

    public void setDealerId(String dealerId) {
        this.dealerId = dealerId;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getDealerContact() {
        return dealerContact;
    }

    public void setDealerContact(String dealerContact) {
        this.dealerContact = dealerContact;
    }

    public String getDealerEmail() {
        return dealerEmail;
    }

    public void setDealerEmail(String dealerEmail) {
        this.dealerEmail = dealerEmail;
    }

    public String getDealerProfileImage() {
        return dealerProfileImage;
    }

    public void setDealerProfileImage(String dealerProfileImage) {
        this.dealerProfileImage = dealerProfileImage;
    }

    public String getDealerCountry() {
        return dealerCountry;
    }

    public void setDealerCountry(String dealerCountry) {
        this.dealerCountry = dealerCountry;
    }

    public String getDealerState() {
        return dealerState;
    }

    public void setDealerState(String dealerState) {
        this.dealerState = dealerState;
    }

    public String getDealerCity() {
        return dealerCity;
    }

    public void setDealerCity(String dealerCity) {
        this.dealerCity = dealerCity;
    }

    public String getDealerArea() {
        return dealerArea;
    }

    public void setDealerArea(String dealerArea) {
        this.dealerArea = dealerArea;
    }

    public String getDealerAddress() {
        return dealerAddress;
    }

    public void setDealerAddress(String dealerAddress) {
        this.dealerAddress = dealerAddress;
    }

    public String getDealerPincode() {
        return dealerPincode;
    }

    public void setDealerPincode(String dealerPincode) {
        this.dealerPincode = dealerPincode;
    }

    public String getDealerStatus() {
        return dealerStatus;
    }

    public void setDealerStatus(String dealerStatus) {
        this.dealerStatus = dealerStatus;
    }

    public String getDealerCreatedAt() {
        return dealerCreatedAt;
    }

    public void setDealerCreatedAt(String dealerCreatedAt) {
        this.dealerCreatedAt = dealerCreatedAt;
    }
}
