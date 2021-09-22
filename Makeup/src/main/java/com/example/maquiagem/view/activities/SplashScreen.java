package com.example.maquiagem.view.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.maquiagem.R;

public class SplashScreen extends AppCompatActivity {

    private final String FILE_PREFERENCE = "com.example.maquiagem";
    private final String FIRST_LOGIN = "first_login";
    private final String NIGHT_MODE = "night_mode";
    private final String LOGIN_NOT_REMEMBER = "not_remember_login";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SharedPreferences preferences = getSharedPreferences(FILE_PREFERENCE, 0);

        // Obtem se o Usuario estava usando DarkTheme e Aplica no APP
        boolean isDarkTheme = preferences.getBoolean(NIGHT_MODE, false);
        int mode = isDarkTheme ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
        AppCompatDelegate.setDefaultNightMode(mode);

        boolean firstLogin = preferences.getBoolean(FIRST_LOGIN, true);
        boolean loginRemember = preferences.getBoolean(LOGIN_NOT_REMEMBER, false);
        Handler handler = new Handler();


        // Leva 2 segundos p/ Iniciar alguma das Activitys
        handler.postDelayed(() -> {
            if (firstLogin) {
                startActivity(new Intent(getApplicationContext(), SlideScreen.class));
            } else if (loginRemember) {
                // todo: implementar verificação na API do Usuario Salvo
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            } else {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
            finish();
        }, 2500);
    }
}