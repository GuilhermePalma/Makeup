package com.example.maquiagem;

public class Makeup {
    //TODO Arrumar os Erros
    private int id;
    private String brand;
    private String name;
    private double price;
    private String currency;
    private String image_link;
    private String description;
    private String category;
    private String tag_list;

    public Makeup(int id, String brand, String name, double price ,String currency,
                  String image_link, String description, String category , String tag_list) {
        this.id = id;
        this.brand = brand;
        this.name = name;
        this.price = price;
        this.currency = currency;
        this.image_link = image_link;
        this.description = description;
        this.category = category;
        this.tag_list = tag_list;


    }

}
