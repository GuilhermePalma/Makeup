package com.example.maquiagem.view.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.maquiagem.R;

public class SplashScreen extends AppCompatActivity {

    private final String FILE_PREFERENCE = "login_user";
    private final String FIRST_LOGIN = "first_login";
    private final String LOGIN_NOT_REMEMBER = "not_remember_login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SharedPreferences preferences = getSharedPreferences(FILE_PREFERENCE, 0);

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