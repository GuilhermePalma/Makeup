package com.example.maquiagem.model.entity;

/**
 * Classe que gerencia a Localização do Usuario
 */
public class Location {

    // Atributos da Classe
    private int lastId;
    private String address;
    private String district;
    private String state;
    private String city;
    private String number;
    private String postalCode;
    private String countryName;
    private String countryCode;

    /**
     * Contrutor da Classe {@link Location} (Contrutor Vazio)
     */
    public Location() {
    }

    // Getters and Setters of Class Location
    public int getLastId() {
        return lastId;
    }

    public void setLastId(int lastId) {
        this.lastId = lastId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
