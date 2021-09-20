package com.example.maquiagem.view.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.maquiagem.R;
import com.example.maquiagem.controller.DataBaseHelper;

public class ProfileActivity extends AppCompatActivity {

    private TextView txt_nickname;
    private TextView txt_name;
    private TextView txt_email;
    private TextView txt_password;
    private TextView txt_idiom;
    private TextView txt_theme;
    private TextView txt_error;

    private DataBaseHelper dataBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Configura os Elementos da Tela
        setUpToolBar();
        instanceItems();

        showWindow();
    }

    private void instanceItems() {
        txt_nickname = findViewById(R.id.txt_nickname);
        txt_name = findViewById(R.id.txt_name);
        txt_email = findViewById(R.id.txt_email);
        txt_password = findViewById(R.id.txt_password);
        txt_idiom = findViewById(R.id.txt_idiom);
        txt_theme = findViewById(R.id.txt_theme);
        txt_error = findViewById(R.id.txt_errorDataUser);

        dataBaseHelper = new DataBaseHelper(this);
    }

    // Configura a ToolBar
    private void setUpToolBar() {
        // Criação da ToolBar e Criação da seta de voltar
        Toolbar toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitle(R.string.toolbar_profile);
        setSupportActionBar(toolbar);
        // Icon de voltar para a Tela Home
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_return_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void showWindow() {
        // Obtem os Dados Salvos da API no Banco de Dados (SQLIte)
        // Verifica se Existe Dados ---> Se não = Mensagem Erro

        noDataUser();
    }

    private void noDataUser() {
        txt_nickname.setVisibility(View.GONE);
        txt_name.setVisibility(View.GONE);
        txt_email.setVisibility(View.GONE);
        txt_password.setVisibility(View.GONE);
        txt_idiom.setVisibility(View.GONE);
        txt_theme.setVisibility(View.GONE);

        txt_error.setVisibility(View.VISIBLE);
    }

}