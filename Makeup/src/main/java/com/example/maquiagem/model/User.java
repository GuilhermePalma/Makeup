package com.example.maquiagem.model;

public class User {

    private String name;
    private String nickname;
    private String email;
    private String password;


    public User(String name, String nickname, String email, String password) {
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
    }

    public User(String nickname, String password) {
        this.nickname = nickname;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
