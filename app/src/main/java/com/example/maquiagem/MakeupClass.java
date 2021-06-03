package com.example.maquiagem;

public class MakeupClass {
    private int id;
    private String brand;
    private String name;
    private String price;
    private String currency;
    private String description;
    private String type;

    public int getId () { return id; }
    public String getBrand  () { return brand; }
    public String getName  () { return name; }
    public String getPrice () { return price; }
    public String getCurrency () { return currency; }
    public String getDescription () { return description; }
    public String getType () { return type; }


    //Contrutor usando ID
    public MakeupClass(int id, String brand, String name, String type, String price , String currency,
                       String description) {
        this.id = id;
        this.brand = brand;
        this.name = name;
        this.type = type;
        this.price = price;
        this.currency = currency;
        this.description = description;
    }

    //Contrutor sem ID
    public MakeupClass(String brand, String name, String type, String price , String currency,
                       String description) {
        this.brand = brand;
        this.name = name;
        this.type = type;
        this.price = price;
        this.currency = currency;
        this.description = description;
    }

}
