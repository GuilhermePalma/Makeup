package com.example.maquiagem.model.entity;

import android.content.Context;
import android.util.Log;

import com.example.maquiagem.R;
import com.example.maquiagem.controller.ManagerResources;

public class User {

    // Context é usado p/ obter as Strings de Validação do String.xml
    private Context context;
    // Atributos já Iniciados para não serem null
    private String name = "";
    private String nickname = "";
    private String email = "";
    private String password = "";
    private String idiom = "";
    private String token_user = "";
    private boolean theme_is_night = false;
    private String error_Validation = "";

    // Construror vazio ----> Manipulação no Banco de Dados
    public User() {
    }

    // Instancia somente com o Context para Validar os Inputs
    public User(Context context) {
        this.context = context;
    }

    public User(Context context, String name, String nickname, String email, String password,
                String idiom, boolean theme_is_night) {
        this.context = context;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.idiom = idiom;
        this.theme_is_night = theme_is_night;
    }

    public boolean validationName(String name) {
        if (name == null || name.isEmpty()) {
            error_Validation = context.getString(R.string.validation_empty,
                    "Nome");
            return false;
        } else if (name.length() < 3 || name.length() > 120) {
            error_Validation = context.getString(R.string.validation_length,
                    "Nome", "3", "120");
            return false;
        } else if (!name.matches("^[A-ZÀ-úà-úa-zçÇ\\s]*")) {
            error_Validation = context.getString(R.string.validation_noFormmat,
                    "Nome", "Letras");
            return false;
        } else return true;
    }

    public boolean validationNickname(String nicknameReceived) {
        if (nicknameReceived == null || nicknameReceived.isEmpty()) {
            error_Validation = context.getString(R.string.validation_empty,
                    "Nome de Usuario (Nickname)");
            return false;
        } else if (nicknameReceived.length() < 3 || nicknameReceived.length() > 40) {
            error_Validation = context.getString(R.string.validation_length,
                    "Nome de Usuario (Nickname)", "3", "40");
            return false;
        } else if (!nicknameReceived.matches("^[A-Za-z._\\S]*")) {
            error_Validation = context.getString(R.string.validation_noFormmat,
                    "Nome de Usuario (Nickname)", "Letras");
            return false;
        } else return true;
    }

    public boolean validationEmail(String emailReceived) {
        if (emailReceived == null || emailReceived.isEmpty()) {
            error_Validation = context.getString(R.string.validation_empty, "Email");
            return false;
        } else if (emailReceived.length() < 8 || emailReceived.length() > 150) {
            error_Validation = context.getString(R.string.validation_length, "Email", "8", "150");
            return false;
        } else if (!emailReceived.matches("^[A-Za-z\\S\\d._\\-@#]*")) {
            error_Validation = context.getString(R.string.validation_noFormmat, "Email",
                    "Letras, Numeros, e alguns caracteres (Hifen, Ponto, Underline, # e @)");
            return false;
        } else return true;
    }

    public boolean validationPassword(String passwordReceived) {
        if (passwordReceived == null || passwordReceived.isEmpty()) {
            error_Validation = context.getString(R.string.validation_empty, "Senha");
            return false;
        } else if (passwordReceived.length() < 3 || passwordReceived.length() > 40) {
            error_Validation = context.getString(R.string.validation_length,
                    "Senha", "3", "40");
            return false;
        } else if (!passwordReceived.matches("^[A-Za-z\\S\\d´`^~.,_\\-?@!*&+=#/|]*")) {
            error_Validation = context.getString(R.string.validation_noFormmat,
                    "Senha", "Letras, Numeros, Acentos e Caracteres Especiais Validos");
            return false;
        } else return true;
    }

    public boolean validationIdiom(String idiomReceived) {
        if (idiomReceived == null || idiomReceived.equals("")) {
            error_Validation = context.getString(R.string.validation_empty, "Idioma");
            return false;
        }

        // Obtem um Array dos Idiomas Validos
        String[] idiomsValid = context.getResources().getStringArray(R.array.array_idioms);
        for (String itemArray : idiomsValid) {
            if (idiomReceived.equals(itemArray)) return true;
        }

        error_Validation = context.getString(R.string.validation_noFormmat,
                "Idioma", "os Idiomas da Lista");
        return false;
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

    public String getIdiom() {
        return idiom;
    }

    public void setIdiom(String idiom) {
        this.idiom = idiom;
    }

    public boolean isTheme_is_night() {
        return theme_is_night;
    }

    public void setTheme_is_night(boolean theme_is_night) {
        this.theme_is_night = theme_is_night;
    }

    public String getError_Validation() {
        return error_Validation;
    }

    public void setError_Validation(String error_validation) {
        this.error_Validation = error_validation;
    }

    public String getToken_user() {
        return token_user;
    }

    public void setToken_user(String token_user) {
        this.token_user = token_user;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
