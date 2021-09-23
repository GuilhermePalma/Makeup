package com.example.maquiagem.view.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.example.maquiagem.R;
import com.example.maquiagem.controller.DataBaseHelper;
import com.example.maquiagem.model.User;
import com.example.maquiagem.view.PersonAlertDialogs;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class ConfigurationActivity extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteIdioms;
    private SwitchMaterial switchDarkTheme;
    private String[] array_idioms;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        // Instancia os Intens
        setUpToolBar();

        // Configura a ToolBar, AutoCompleteIdiom e Swtich Idiom
        instanceItems();
        setUpAutoComplete();
        setUpSwitchDark();
    }

    // Configura a ToolBar
    private void setUpToolBar() {
        // Criação da ToolBar e Criação da seta de voltar
        Toolbar toolbar = findViewById(R.id.toolbar1);
        toolbar.setTitle(R.string.toolbar_configuration);
        setSupportActionBar(toolbar);
        // Icon de voltar para a Tela Home
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_return_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    /**
     * Instancia os Itens Usados na Classe
     */
    private void instanceItems() {
        autoCompleteIdioms = findViewById(R.id.autoComplete_changeIdioms);
        switchDarkTheme = findViewById(R.id.switch_darkTheme);
        array_idioms = getResources().getStringArray(R.array.array_idioms);

        // Obtem a Instancia do Usuario
        DataBaseHelper database = new DataBaseHelper(this);
        user = database.selectUser(this);
        database.close();
    }

    /**
     * Configura o Switch para o Theme Dark do APP
     */
    private void setUpSwitchDark() {
        // Obtem o Theme Atual do APP
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            switchDarkTheme.setChecked(true);
        }

        // Configura o Switch Dark e Light Theme
        switchDarkTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {

            // Define o Novo valor ao Theme e Atualiza no Banco de Dados
            DataBaseHelper database = new DataBaseHelper(this);
            user.setTheme_is_night(isChecked);
            database.updateUser(user);
            database.close();

            String messageTheme_api = updateThemeAPI(user);
            if (messageTheme_api.equals("")) {
                // todo: enviar o theme alterado p/ api local
                int theme_app = isChecked ? AppCompatDelegate.MODE_NIGHT_YES :
                        AppCompatDelegate.MODE_NIGHT_NO;

                // Define o Theme no APP
                AppCompatDelegate.setDefaultNightMode(theme_app);
            } else {
                new PersonAlertDialogs(this).message(
                        getString(R.string.title_errorAPI), messageTheme_api).show();
            }
        });
    }

    /**
     * Configura o AutoComplete de Idioms
     */
    private void setUpAutoComplete() {
        // Obtem e Configura o AutoCompleteIdiom
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, array_idioms);
        autoCompleteIdioms.setAdapter(adapter);

        // Listener do AutoCompleteIdioms
        autoCompleteIdioms.setOnItemClickListener((parent, view, position, id) -> {
            String idiom = array_idioms[position];
            if (!user.validationIdiom(idiom)) {
                // Coloca o Erro no Input Idiom
                autoCompleteIdioms.setError(getString(R.string.error_valueRequired), null);
                autoCompleteIdioms.requestFocus();
                return;
            }

            // Atualiza os Valores no Banco de Dados
            DataBaseHelper database = new DataBaseHelper(this);
            user.setIdiom(idiom);
            database.updateUser(user);
            database.close();

            String messageIdiom_api = updateIdiomAPI(user);
            if (messageIdiom_api.equals("")) {
                // todo: enviar o idioma alterado p/ api local
                new PersonAlertDialogs(this).message(
                        getString(R.string.title_updateUser),
                        getString(R.string.text_idiomUpdate, idiom)).show();
            } else {
                new PersonAlertDialogs(this).message(
                        getString(R.string.title_errorAPI), messageIdiom_api).show();
            }
        });
    }

    private String updateThemeAPI(User user) {
        return "";
    }

    private String updateIdiomAPI(User user) {
        return "";
    }

}