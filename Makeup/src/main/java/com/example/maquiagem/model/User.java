package com.example.maquiagem.model;

import android.content.Context;

import com.example.maquiagem.R;


public class User {

    // todo: adicionar mais itens da classe User
    private String name;
    private String nickname;
    private String email;
    private String password;
    private Context context;

    public User(Context context, String name, String nickname, String email, String password) {
        this.context = context;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
    }

    // TODO: DESCONTINUAR METODO ABAIXO
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

    public String validationName(String name) {
        if (name == null || name.isEmpty()) {
            return context.getString(R.string.validation_empty, "Nome");
        } else if (name.length() < 3 || name.length() > 120) {
            return context.getString(R.string.validation_length, "Nome", "3", "120");
        } else if (!name.matches("^[A-ZÀ-úà-úa-zçÇ\\s]*")) {
            return context.getString(R.string.validation_noFormmat, "Nome", "Letras");
        } else return context.getString(R.string.validation_ok);
    }

    public String validationNickname(String nickname) {
        if (nickname == null || nickname.isEmpty()) {
            return context.getString(R.string.validation_empty,
                    "Nome de Usuario (Nickname)");
        } else if (nickname.length() < 3 || nickname.length() > 40) {
            return context.getString(R.string.validation_length,
                    "Nome de Usuario (Nickname)", "3", "40");
        } else if (!nickname.matches("^[A-Za-z._\\s]*")) {
            return context.getString(R.string.validation_noFormmat,
                    "Nome de Usuario (Nickname)", "Letras");
        } else return context.getString(R.string.validation_ok);
    }

    public String validationEmail(String email) {
        if (email == null || email.isEmpty()) {
            return context.getString(R.string.validation_empty, "Email");
        } else if (email.length() < 8 || email.length() > 150) {
            return context.getString(R.string.validation_length, "Email", "8", "150");
        } else if (!email.matches("^[A-Za-z\\S\\d._\\-@#]*")) {
            return context.getString(R.string.validation_noFormmat, "Email",
                    "Letras, Numeros, e alguns caracteres (Hifen, Ponto, Underline, # e @)");
        } else return context.getString(R.string.validation_ok);
    }

    public String validationPassword(String password) {
        if (password == null || password.isEmpty()) {
            return context.getString(R.string.validation_empty, "Senha (Password)");
        } else if (password.length() < 3 || password.length() > 40) {
            return context.getString(R.string.validation_length, "Senha", "3", "40");
        } else if (!password.matches("^[A-Za-z\\S\\d´`^~.,_\\-?@!*&+=#/|]*")) {
            return context.getString(R.string.validation_noFormmat, "Senha",
                    "Letras, Numeros, Acentos e Caracteres Especiais Validos");
        } else return context.getString(R.string.validation_ok);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
