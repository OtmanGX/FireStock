package com.gxma.foodoc.models;

import java.io.Serializable;

public class Category implements Serializable {
    private String name;
    private String description;
    private String urlImage;

    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }

    public Category(String name, String description, String urlImage) {
        this.name = name;
        this.description = description;
        this.urlImage = urlImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

}
