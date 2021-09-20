package com.example.maquiagem.view.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.example.maquiagem.R;
import com.example.maquiagem.view.PersonAlertDialogs;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class ConfigurationActivity extends AppCompatActivity {

    private final String FILE_PREFERENCE = "com.example.maquiagem";
    private final String NIGHT_MODE = "night_mode";

    private AutoCompleteTextView autoCompleteIdioms;
    private SwitchMaterial switchDarkTheme;
    private String[] array_idioms;
    private String idiom;

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

            SharedPreferences preferences = getSharedPreferences(FILE_PREFERENCE, 0);
            preferences.edit().putBoolean(NIGHT_MODE, isChecked).apply();

            // todo: adicionar envio da alteração p/ a API Local
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
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
            idiom = array_idioms[position];

            // todo: enviar o idioma alterado p/ api local
            // Todo: implementar outras versões de idioma no app
            // Exibe a Mensagem sobre a Alteração do Idioma
            new PersonAlertDialogs(this).message(getString(R.string.title_changeIdiom),
                    getString(R.string.dialog_idiom)).show();

        });
    }

}