package com.mike.models;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Brand {
    @Id
    private Long id;
    private String brandName;
    private String couponCode;
    private String details;
    private String validity;

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public String getDetails() {
        return details;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setBrandName(String details) {
        this.brandName = brandName;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }
}

