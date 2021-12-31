package com.example.maquiagem.view.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.maquiagem.R;
import com.example.maquiagem.controller.ManagerDatabase;
import com.example.maquiagem.controller.ManagerSharedPreferences;
import com.example.maquiagem.model.entity.User;

/**
 * Activity da Tela de Abertura do APP. Nessa Activity, faz as verificações de Acesso e Login para
 * redirecionar as Activities corretas
 */
public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        final Context context = SplashScreen.this;
        final ManagerSharedPreferences managerPreferences = new ManagerSharedPreferences(context);

        // Define no APP Dark/Light Theme
        int theme = managerPreferences.isDarkTheme() ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
        AppCompatDelegate.setDefaultNightMode(theme);

        // Leva 2,5 segundos p/ Iniciar alguma das Activities
        new Handler().postDelayed(() -> {
            // Verifica se é o Primeiro Acesso (Mostra as Telas de Apresentação do APP)
            if (managerPreferences.isFirstLogin()) {
                startActivity(new Intent(getApplicationContext(), SlideScreen.class));
            } else if (managerPreferences.isRememberLogin()) {

                // Obtem o Usuario do Banco de Dados e Tenta Realizar o Login
                ManagerDatabase database = new ManagerDatabase(context);
                User user = database.selectUser();

                if (executeLogin(user)) startActivity(new Intent(context, MainActivity.class));
                else startActivity(new Intent(context, LoginActivity.class));

            } else {
                startActivity(new Intent(context, LoginActivity.class));
            }
            finish();
        }, 2500);
    }

    /**
     * A partir de um {@link User} executa o Login na API
     *
     * @param user Instancia do {@link User} que será enviado à API
     * @return true|false
     */
    public boolean executeLogin(User user) {
        // Todo: Adicionar Metodos da API Interna
        // Verifica se o Usuario é Nulo
        return user != null;

        // Caso retorne não Autorizado ---> Gera novo Token com os Dados do Usuario
        // generationNewToken(user)
    }

    /**
     * A partir de um {@link User} obtem o Token do Usuario na API
     *
     * @param user Instancia do {@link User} que será enviado à API
     * @return {@link User}|""
     */
    public String generationNewToken(User user) {
        // Todo: Adicionar Geração do TOken na API Interna
        // Tenta Gerar novo Token com os Dados do Usuario, se não consegue retorna ""(Vazio)
        return "JWT";
    }
}