package com.mike.models;

public class Transaction {
    private String userId;
    private double amount;

    // getters and setters
    public  String getUserId(){
        return this.userId;
    }

    public double getAmount(){
        return this.amount;
    }
}