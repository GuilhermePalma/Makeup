package com.example.maquiagem;

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

    public int getId () { return id; }
    public String getBrand  () { return brand; }
    public String getName  () { return name; }
    public String getPrice () { return price; }
    public String getCurrency () { return currency; }
    public String getImage_link () { return image_link; }
    public String getDescription () { return description; }
    public String getType () { return type; }

    public void setId (int id) { this.id = id; }
    public void setBrand  (String brand) { this.brand = brand; }
    public void setName  (String name) { this.name = name; }
    public void setPrice (String price) { this.price = price; }
    public void setCurrency (String currency) { this.currency = currency; }
    public void setImage_link (String image_link) { this.image_link = image_link; }
    public void setDescription (String description) { this.description = description; }
    public void setType (String type) { this.type = type; }

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

}
