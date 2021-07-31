package com.example.maquiagem.view.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import com.example.maquiagem.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;

public class SplashScreen extends AppCompatActivity {

    private final String FILE_PREFERENCE = "has_login";
    private final String FIRST_LOGIN = "first_login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SharedPreferences preferences = getSharedPreferences(FILE_PREFERENCE, 0);

        boolean firstLogin = preferences.getBoolean(FIRST_LOGIN,true);
        Handler handler = new Handler();

        // Leva 2 segundos p/ Iniciar alguma das Activitys
        handler.postDelayed(() -> {
            if (firstLogin){
                startActivity(new Intent(getApplicationContext(), SlideScreen.class));
            } else {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
            finish();
        }, 2500);
    }
}