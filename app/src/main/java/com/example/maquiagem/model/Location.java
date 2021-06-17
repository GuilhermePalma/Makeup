package com.example.maquiagem.model;

public class Location {

    public int lastId;
    public String address;
    public String bairro;
    public String estado;
    public String city;
    public String number;
    public String postalCode;
    public String countryName;
    public String countryCode;


    public Location(){
        // Metodo sem contrutor
    }

    public Location(int lastId,String address, String bairro, String estado, String city, String number, String postalCode, String countryName, String countryCode) {
        this.lastId = lastId;
        this.address = address;
        this.bairro = bairro;
        this.estado = estado;
        this.city = city;
        this.number = number;
        this.postalCode = postalCode;
        this.countryName = countryName;
        this.countryCode = countryCode;
    }

    public int getLastId() {return lastId;}
    public String getAddress() {return  address;}
    public String getBairro() {return bairro;}
    public String getEstado() {return estado;}
    public String getCity() {return city;}
    public String getNumber() {return number;}
    public String getPostalCode() {return postalCode;}
    public String getCountryName() {return countryName;}
    public String getCountryCode() {return countryCode;}
}
