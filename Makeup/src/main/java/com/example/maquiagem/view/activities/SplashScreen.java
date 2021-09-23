package com.example.maquiagem.view.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.maquiagem.R;
import com.example.maquiagem.controller.DataBaseHelper;
import com.example.maquiagem.model.User;

public class SplashScreen extends AppCompatActivity {

    private final String FILE_PREFERENCE = "com.example.maquiagem";
    private final String FIRST_LOGIN = "first_login";
    private final String LOGIN_NOT_REMEMBER = "not_remember_login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SharedPreferences preferences = getSharedPreferences(FILE_PREFERENCE, 0);

        // Obtem se o Usuario estava usando DarkTheme e Aplica no APP
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        User user = dataBaseHelper.selectUser(this);
        boolean isDarkTheme = false;
        if (user != null) isDarkTheme = user.isTheme_is_night();
        dataBaseHelper.close();

        // Define no APP Dark/Light Theme
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
                if (executeLogin(user)) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                } else {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
            } else {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
            finish();
        }, 2500);
    }


    // todo: Verificaçãoo do Usuario na API
    public boolean executeLogin(User user) {

        // Verifica se o Usuario é Nulo
        if (user == null) return false;

        // Caso retorne não Autorizado ---> Gera novo Token com os Dados do Usuario
        // generationNewToken(user)

        return true;
    }

    // todo: Geração de um Novo Tokne
    public String generationNewToken(User user) {
        // Tenta Gerar novo Token com os Dados do Usuario, se não consegue retorna ""(Vazio)
        return "JWT";
    }
}