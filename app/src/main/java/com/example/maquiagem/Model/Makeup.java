package com.example.maquiagem.Model;

public class Makeup {
    //TODO Arrumar os Erros
    private int id;
    private String brand;
    private String name;
    private String price;
    private String currency;
    private String image_link;
    private String description;
    private String type;

    public Makeup(String tipos, String tags) {

    }

    public int getId () { return id; }
    public String getBrand  () { return brand; }
    public String getName  () { return name; }
    public String getPrice () { return price; }
    public String getCurrency () { return currency; }
    public String getImage_link () { return image_link; }
    public String getDescription () { return description; }
    public String getType () { return type; }


    //Contrutor usando ID
    public Makeup(int id, String brand, String name, String type, String price ,String currency,
                  String image_link, String description) {
        this.id = id;
        this.brand = brand;
        this.name = name;
        this.type = type;
        this.price = price;
        this.currency = currency;
        this.image_link = image_link;
        this.description = description;
    }

    //Contrutor sem ID
    public Makeup(String brand, String name, String type, String price ,String currency,
                  String image_link, String description) {
        this.brand = brand;
        this.name = name;
        this.type = type;
        this.price = price;
        this.currency = currency;
        this.image_link = image_link;
        this.description = description;
    }

}
