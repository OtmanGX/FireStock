package com.gxma.foodoc.models;

import android.support.annotation.Nullable;

import java.io.Serializable;


public class User implements Serializable {


    private String username;
    private String uid;
    private String email;
    private Boolean isAdmin;
    @Nullable
    private String urlPicture;


    public User() {
        this.isAdmin = false;
    }

    public User(String uid, String username, String urlPicture) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.isAdmin = false;
    }

    // --- GETTERS ---
    public String getUid() { return uid; }
    public String getUsername() { return username; }
    public String getUrlPicture() { return urlPicture; }
    public Boolean getIsAdmin() { return isAdmin; }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // --- SETTERS ---
    public void setUsername(String username) { this.username = username; }
    public void setUid(String uid) { this.uid = uid; }
    public void setUrlPicture(String urlPicture) { this.urlPicture = urlPicture; }
    public void setIsAdmin(Boolean mentor) { isAdmin = mentor; }
}
