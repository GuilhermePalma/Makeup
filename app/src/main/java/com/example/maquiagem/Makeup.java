package com.example.maquiagem;

public class Makeup {
    private int id;
    private String brand;
    private String name;
    private double price;
    private String currency;
    private String image_link;
    private String description;
    private String category;
    private String tag_list;
    private String api_featured_image;
    private String product_colors;

    public Makeup(int id, String brand, String name, double price ,String currency,
                  String image_link, String description, String category , String tag_list,
                  String api_featured_image, String product_colors ) {
        this.id = id;
        this.brand = brand;
        this.name = name;
        this.price = price;
        this.currency = currency;
        this.image_link = image_link;
        this.description = description;
        this.category = category;
        this.tag_list = tag_list;
        this.api_featured_image = api_featured_image;
        this.product_colors = product_colors;

    }

}
