package com.example.maquiagem.view.activities;

import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.example.maquiagem.R;
import com.example.maquiagem.controller.DataBaseHelper;
import com.example.maquiagem.model.entity.User;
import com.example.maquiagem.view.CustomAlertDialog;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class ConfigurationActivity extends AppCompatActivity {

    private CustomAlertDialog customDialog;
    private AutoCompleteTextView autoCompleteIdioms;
    private SwitchMaterial switchDarkTheme;
    private String[] array_idioms;
    private User user;
    private Context context;

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

        if (getSupportActionBar() != null) {
            // Icon de voltar para a Tela Home
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_return_home);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
    }

    /**
     * Instancia os Itens Usados na Classe
     */
    private void instanceItems() {
        autoCompleteIdioms = findViewById(R.id.autoComplete_changeIdioms);
        switchDarkTheme = findViewById(R.id.switch_darkTheme);
        array_idioms = getResources().getStringArray(R.array.array_idioms);
        context = ConfigurationActivity.this;
        customDialog = new CustomAlertDialog(context);

        // Obtem a Instancia do Usuario
        DataBaseHelper database = new DataBaseHelper(context);
        user = database.selectUser(context);
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
            DataBaseHelper database = new DataBaseHelper(context);
            user.setTheme_is_night(isChecked);
            database.updateUser(user);
            database.close();

            if (updateThemeAPI(user)) {
                // todo: enviar o theme alterado p/ api local
                int theme_app = isChecked ? AppCompatDelegate.MODE_NIGHT_YES :
                        AppCompatDelegate.MODE_NIGHT_NO;

                // Define o Theme no APP
                AppCompatDelegate.setDefaultNightMode(theme_app);
                recreate();
            } else {
                customDialog.defaultMessage(R.string.title_errorAPI, R.string.error_change, null,
                        new String[]{"Tema", "Usario"}, true).show();
            }
        });
    }

    /**
     * Configura o AutoComplete de Idioms
     */
    private void setUpAutoComplete() {
        // Obtem e Configura o AutoCompleteIdiom
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
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
            DataBaseHelper database = new DataBaseHelper(context);
            user.setIdiom(idiom);
            database.updateUser(user);
            database.close();

            if (updateIdiomAPI(user)) {
                customDialog.defaultMessage(R.string.title_updateUser, R.string.text_idiomUpdate,
                        null, new String[]{idiom}, true).show();
            } else {
                customDialog.defaultMessage(R.string.title_errorAPI, R.string.error_change, null,
                        new String[]{"Idioma", "Usario"}, true).show();
            }
        });
    }

    // todo: enviar o tema alterado p/ API LOCAL
    private boolean updateThemeAPI(User user) {
        return true;
    }

    // todo: enviar o idioma alterado p/ API LOCAL
    private boolean updateIdiomAPI(User user) {
        return true;
    }

}