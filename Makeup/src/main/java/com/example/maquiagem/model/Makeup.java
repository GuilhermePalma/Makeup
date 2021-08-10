package com.example.maquiagem.model;

public class Makeup {

    private int id;
    private String brand;
    private String name;
    private String price;
    private String currency;
    private String description;
    private String type;
    private String urlImage;
    private boolean isFavorite = false;

    public void setId(int id) {
        this.id = id;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public int getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getCurrency() {
        return currency;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public Makeup() {
    }

    //Contrutor usando ID
    public Makeup(int id, String brand, String name, String type, String price, String currency,
                  String description, String urlImage) {
        this.id = id;
        this.brand = brand;
        this.name = name;
        this.type = type;
        this.price = price;
        this.currency = currency;
        this.description = description;
        this.urlImage = urlImage;
    }

    //Contrutor sem ID
    public Makeup(String brand, String name, String type, String price, String currency,
                  String description, String urlImage) {
        this.brand = brand;
        this.name = name;
        this.type = type;
        this.price = price;
        this.currency = currency;
        this.description = description;
        this.urlImage = urlImage;
    }

}
