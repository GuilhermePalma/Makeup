package com.example.maquiagem.controller;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Classe responsavel por Manipular as {@link SharedPreferences} do APP
 */
public class ManagerSharedPreferences {

    /**
     * Chave da {@link SharedPreferences} que armazena o Numero de Atualizações de um Widget
     */
    public static final String KEY_COUNT_UPDATE = "count_widget";
    /**
     * Constante do Nome do arquivo das {@link SharedPreferences}
     */
    private final static String FILE_PREFERENCE = "com.example.maquiagem";
    /**
     * Chave da {@link SharedPreferences} em que armazena se o usuario escolheu o "Lembrar Login"
     */
    private final static String KEY_REMEMBER_LOGIN = "not_remember_login";
    /**
     * Chave da {@link SharedPreferences} que controla se o Usuario está acessando o APP pela
     * primeira vez
     */
    private final static String KEY_FIRST_LOGIN = "first_login";
    /**
     * Chave da {@link SharedPreferences} que armazena o Theme do APP
     */
    private static final String KEY_IS_DARK_THEME = "count_widget";
    /**
     * Chave da {@link SharedPreferences} que armazena o Token do
     * {@link com.example.maquiagem.model.entity.User User}
     */
    private static final String KEY_USER_TOKEN = "user_token";

    /**
     * Instancia da {@link SharedPreferences} que será manipulada
     */
    private final SharedPreferences sharedPreferences;

    /**
     * Construtor da Classe {@link ManagerSharedPreferences}
     *
     * @param context {@link Context} utilizado na Manipulação da {@link SharedPreferences}
     */
    public ManagerSharedPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(FILE_PREFERENCE, 0);
    }

    /**
     * Metodo responsavel por incrementar +1 no Contador de Atualização de um WIdget
     *
     * @param key Chave do Widget
     * @see #KEY_COUNT_UPDATE
     */
    public void setIncrementCountWidget(String key) {
        sharedPreferences.edit().putInt(key, getWidgetCount(key) + 1).apply();
    }

    /**
     * Metodo responsavel por informar se o Usuario escolheu o "Lembrar Login"
     *
     * @return true|false
     */
    public boolean isRememberLogin() {
        return sharedPreferences.getBoolean(KEY_REMEMBER_LOGIN, false);
    }

    /**
     * Metodo responsavel por salvar a Opção "Lembar Login" do Usuario
     *
     * @param isRemember Valor boolean que controla o Lembrar ou não Login
     */
    public void setRememberLogin(boolean isRemember) {
        sharedPreferences.edit().putBoolean(KEY_REMEMBER_LOGIN, isRemember).apply();
    }

    /**
     * Metodo responsavel por informar se é o Primeiro Acesso do Usuario no Sistema
     *
     * @return true|false
     */
    public boolean isFirstLogin() {
        return sharedPreferences.getBoolean(KEY_FIRST_LOGIN, true);
    }

    /**
     * Metodo responsavel por Salvar se é o Primeiro Acesso do Usuario no Sistema
     *
     * @param isRemember Valor boolean se é ou não o Primeiro Login
     */
    public void setFirstLogin(boolean isRemember) {
        sharedPreferences.edit().putBoolean(KEY_REMEMBER_LOGIN, isRemember).apply();
    }

    /**
     * Metodo responsavel por Informar a quantidade de Vezes que um Widget já Atualizou
     *
     * @param key Chave do Widget
     * @return int
     * @see #KEY_COUNT_UPDATE
     */
    public int getWidgetCount(String key) {
        return sharedPreferences.getInt(key, 0);
    }

    /**
     * Metodo responsavel por Informar o Theme do APP
     *
     * @return true|false
     */
    public boolean isDarkTheme() {
        return sharedPreferences.getBoolean(KEY_IS_DARK_THEME, false);
    }

    /**
     * Metodo responsavel por Salvar o Theme do APP
     *
     * @param isDarkTheme Valor booleano que define se é ou não DarkTheme
     */
    public void setDarkTheme(boolean isDarkTheme) {
        sharedPreferences.edit().putBoolean(KEY_IS_DARK_THEME, isDarkTheme).apply();
    }

    /**
     * Metodo Responsavel por Retornar o Token do Usuario
     *
     * @return {@link String}|""
     */
    public String getUserToken() {
        return sharedPreferences.getString(KEY_USER_TOKEN, "");
    }

    /**
     * Metodo responsavel por Salvar o Token do Usuario
     *
     * @param token {@link String} do Token que será salvo
     */
    public void setUserToken(String token) {
        sharedPreferences.edit().putString(KEY_USER_TOKEN, token).apply();
    }

}
