package com.efunhub.pickpriceadmin.Model;

/**
 * Created by Admin on 13-12-2018.
 */

public class Customer {

    public String customerId;
    public String customerName;
    public String customerContact;
    public String customerEmail;
    public String customerProfileImage;
    public String customerCountry;
    public String customerState;
    public String customerCity;
    public String customerArea;
    public String customerAddress;
    public String customerPincode;
    public String customerStatus;
    public String customerCreatedAt;


    public Customer() {
    }

    public Customer(String customerId, String customerName, String customerContact, String customerEmail,
                    String customerProfileImage, String customerCountry, String customerState,
                    String customerCity, String customerArea, String customerAddress,
                    String customerPincode, String customerStatus, String customerCreatedAt) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerContact = customerContact;
        this.customerEmail = customerEmail;
        this.customerProfileImage = customerProfileImage;
        this.customerCountry = customerCountry;
        this.customerState = customerState;
        this.customerCity = customerCity;
        this.customerArea = customerArea;
        this.customerAddress = customerAddress;
        this.customerPincode = customerPincode;
        this.customerStatus = customerStatus;
        this.customerCreatedAt = customerCreatedAt;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerContact() {
        return customerContact;
    }

    public void setCustomerContact(String customerContact) {
        this.customerContact = customerContact;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerProfileImage() {
        return customerProfileImage;
    }

    public void setCustomerProfileImage(String customerProfileImage) {
        this.customerProfileImage = customerProfileImage;
    }

    public String getCustomerCountry() {
        return customerCountry;
    }

    public void setCustomerCountry(String customerCountry) {
        this.customerCountry = customerCountry;
    }

    public String getCustomerState() {
        return customerState;
    }

    public void setCustomerState(String customerState) {
        this.customerState = customerState;
    }

    public String getCustomerCity() {
        return customerCity;
    }

    public void setCustomerCity(String customerCity) {
        this.customerCity = customerCity;
    }

    public String getCustomerArea() {
        return customerArea;
    }

    public void setCustomerArea(String customerArea) {
        this.customerArea = customerArea;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerPincode() {
        return customerPincode;
    }

    public void setCustomerPincode(String customerPincode) {
        this.customerPincode = customerPincode;
    }

    public String getCustomerStatus() {
        return customerStatus;
    }

    public void setCustomerStatus(String customerStatus) {
        this.customerStatus = customerStatus;
    }

    public String getCustomerCreatedAt() {
        return customerCreatedAt;
    }

    public void setCustomerCreatedAt(String customerCreatedAt) {
        this.customerCreatedAt = customerCreatedAt;
    }
}
