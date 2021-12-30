package com.example.maquiagem.controller;

import android.content.Context;
import android.content.SharedPreferences;

public class ManagerSharedPreferences {

    public final static String FILE_PREFERENCE = "com.example.maquiagem";
    public final static String KEY_REMEMBER_LOGIN = "not_remember_login";
    public final static String KEY_FIRST_LOGIN = "first_login";
    public static final String KEY_COUNT_UPDATE = "count_widget";
    public static final String KEY_IS_DARK_THEME = "count_widget";
    public static final String KEY_USER_TOKEN = "user_token";
    private final SharedPreferences sharedPreferences;

    public ManagerSharedPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(FILE_PREFERENCE, 0);
    }

    public void setIncrementCountWidget(String key) {
        sharedPreferences.edit().putInt(key, getWidgetCount(key) + 1).apply();
    }

    public boolean isRememberLogin() {
        return sharedPreferences.getBoolean(KEY_REMEMBER_LOGIN, false);
    }

    public void setRememberLogin(boolean isRemember) {
        sharedPreferences.edit().putBoolean(KEY_REMEMBER_LOGIN, isRemember).apply();
    }

    public boolean isFirstLogin() {
        return sharedPreferences.getBoolean(KEY_FIRST_LOGIN, true);
    }

    public void setFirstLogin(boolean isRemember) {
        sharedPreferences.edit().putBoolean(KEY_REMEMBER_LOGIN, isRemember).apply();
    }

    public int getWidgetCount(String key) {
        return sharedPreferences.getInt(key, 0);
    }

    public boolean isDarkTheme() {
        return sharedPreferences.getBoolean(KEY_IS_DARK_THEME, false);
    }

    public void setDarkTheme(boolean isDarkTheme) {
        sharedPreferences.edit().putBoolean(KEY_IS_DARK_THEME, isDarkTheme).apply();
    }

    public String getUserToken() {
        return sharedPreferences.getString(KEY_USER_TOKEN, "");
    }

    public void setUserToken(String token) {
        sharedPreferences.edit().putString(KEY_USER_TOKEN, token).apply();
    }

}
