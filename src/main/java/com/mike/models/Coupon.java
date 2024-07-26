package com.mike.models;

public class Coupon {
    private String category_id;
    private String brand_name;
    private String offer;

    // getters and setters
//    public  String getCouponCode(){
//        return this.couponCode;
//    }
//
//    public String getEmailId(){
//        return this.emailId;
//    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }
}