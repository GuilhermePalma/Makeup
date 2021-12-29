package com.example.maquiagem.model.entity;

public class Location {

    public int lastId;
    public String address;
    public String district;
    public String state;
    public String city;
    public String number;
    public String postalCode;
    public String countryName;
    public String countryCode;


    public Location(){
        // Metodo sem contrutor
    }

    public Location(int lastId,String address, String district, String state, String city,
                    String number, String postalCode, String countryName, String countryCode) {
        this.lastId = lastId;
        this.address = address;
        this.district = district;
        this.state = state;
        this.city = city;
        this.number = number;
        this.postalCode = postalCode;
        this.countryName = countryName;
        this.countryCode = countryCode;
    }

    public int getLastId() {return lastId;}
    public String getAddress() {return  address;}
    public String getDistrict() {return district;}
    public String getState() {return state;}
    public String getCity() {return city;}
    public String getNumber() {return number;}
    public String getPostalCode() {return postalCode;}
    public String getCountryName() {return countryName;}
    public String getCountryCode() {return countryCode;}
}
