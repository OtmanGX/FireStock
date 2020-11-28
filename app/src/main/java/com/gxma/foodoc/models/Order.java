package com.gxma.foodoc.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.Date;

public class Order implements Serializable {
    private Product product;
    private int quantity;
    private User user;
    private Date dateCreated;
    private int state;
    private String description;
    private String response;
    private boolean urgent;
    private boolean withBill;
    private float pricettc;

    public Order() {
    }

    public Order(Product product, int quantity, User user) {
        this.product = product;
        this.quantity = quantity;
        this.user = user;
        this.state = 0;
        this.urgent = false;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ServerTimestamp
    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public boolean isUrgent() {
        return urgent;
    }

    public void setUrgent(boolean urgent) {
        this.urgent = urgent;
    }

    public boolean isWithBill() {
        return withBill;
    }

    public void setWithBill(boolean withBill) {
        this.withBill = withBill;
    }

    public float getPricettc() {
        return pricettc;
    }

    public void setPricettc(float pricettc) {
        this.pricettc = pricettc;
    }
}
