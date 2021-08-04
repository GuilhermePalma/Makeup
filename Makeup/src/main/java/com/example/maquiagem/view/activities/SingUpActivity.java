package com.example.maquiagem.view.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.maquiagem.R;
import com.example.maquiagem.controller.ManagerKeyboard;
import com.example.maquiagem.view.AlertDialogs;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class SingUpActivity extends AppCompatActivity {

    private TextInputEditText edit_name;
    private TextInputEditText edit_nickname;
    private TextInputEditText edit_email;
    private TextInputEditText edit_password;
    private TextInputEditText edit_confirmPassword;

    private AutoCompleteTextView autoComplete_idioms;
    private Button btn_singUp;
    private Button btn_goLogin;
    private MaterialCheckBox checkBox_remember;

    private ManagerKeyboard managerKeyboard;
    private SharedPreferences preferences;
    private AlertDialogs dialog;
    private final String FILE_PREFERENCE = "login_user";
    private final String FIRST_LOGIN = "first_login";
    private final String LOGIN_NOT_REMEMBER = "not_remember_login";

    private final String EXIST_LOGIN = "exist_login";
    private final String EXIST_PASSWORD = "exist_password";

    private String name, nickname, email, password, confirmPassword, idioms;
    private String[] array_idioms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);

        instanceItens();

        array_idioms = getResources().getStringArray(R.array.array_idioms);
        setInputIdioms();
        listenerInputIdioms();

        btn_singUp.setOnClickListener(v -> registerUser());

        btn_goLogin.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        });
    }

    private void instanceItens() {
        edit_name = findViewById(R.id.editText_registerName);
        edit_nickname = findViewById(R.id.editText_registerNickname);
        edit_email = findViewById(R.id.editText_registerEmail);
        edit_password = findViewById(R.id.editText_registerPassword);
        edit_confirmPassword = findViewById(R.id.editText_registerConfirmPassword);
        checkBox_remember = findViewById(R.id.checkbox_rememberUserRegister);

        autoComplete_idioms = findViewById(R.id.autoComplete_registerIdioms);
        btn_singUp = findViewById(R.id.btn_singUp);
        btn_goLogin = findViewById(R.id.btn_goLogin);

        preferences = getSharedPreferences(FILE_PREFERENCE, 0);
        managerKeyboard = new ManagerKeyboard(SingUpActivity.this);
        dialog = new AlertDialogs();

        idioms = "";
    }

    private void setInputIdioms() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, array_idioms);
        autoComplete_idioms.setAdapter(adapter);
    }

    private void listenerInputIdioms() {
        autoComplete_idioms.setOnItemClickListener((parent, view, position, id) ->
                idioms = array_idioms[position]);
    }

    private void registerUser() {

        getValuesInputs();

        if (filledInputs() && lengthInputs() && passwordEquals()) {
            if (!existUserApi()) {
                if (inserInApi()) {

                    preferences.edit().putBoolean(FIRST_LOGIN, false).apply();
                    preferences.edit().putBoolean(LOGIN_NOT_REMEMBER, checkBox_remember.isChecked())
                            .apply();
                    // todo: inserir no sqlite os dados do usuario
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                } else {
                    // Não foi Possivel inserir na API o Usuario
                    dialog.message(getApplicationContext(), getString(R.string.title_errorAPI),
                            getString(R.string.error_api)).show();
                }
            } else {
                // Usuario já cadastrado
                dialog.message(getApplicationContext(), getString(R.string.title_existUser),
                        getString(R.string.error_existUser, nickname)).show();
            }
        }
    }

    private void getValuesInputs() {
        name = Objects.requireNonNull(edit_name.getText()).toString();
        nickname = Objects.requireNonNull(edit_nickname.getText()).toString();
        email = Objects.requireNonNull(edit_email.getText()).toString();
        password = Objects.requireNonNull(edit_password.getText()).toString();
        confirmPassword = Objects.requireNonNull(edit_confirmPassword.getText()).toString();
    }

    private boolean filledInputs() {
        if (name.equals("")) {
            errorInput(edit_name, getString(R.string.error_valueRequired));
            return false;
        } else if (idioms.equals("")) {
            autoComplete_idioms.setError(getString(R.string.error_valueRequired), null);
            autoComplete_idioms.requestFocus();
            managerKeyboard.openKeyboard(autoComplete_idioms);
            return false;
        } else if (nickname.equals("")) {
            errorInput(edit_nickname, getString(R.string.error_valueRequired));
            return false;
        } else if (email.equals("")) {
            errorInput(edit_email, getString(R.string.error_valueRequired));
            return false;
        } else if (password.equals("")) {
            errorInput(edit_password, getString(R.string.error_valueRequired));
            return false;
        } else if (confirmPassword.equals("")) {
            errorInput(edit_confirmPassword, getString(R.string.error_valueRequired));
            return false;
        } else {
            return true;
        }
    }

    private boolean lengthInputs() {
        if (name.length() > 65) {
            errorInput(edit_name, String.format(getString(R.string.error_wrongInput),
                    "Nome e Sobrenome"));
            return false;
        } else if (nickname.length() > 35) {
            errorInput(edit_nickname, String.format(getString(R.string.error_wrongInput),
                    "Nome de Usuario"));
            return false;
        } else if (email.length() > 120) {
            errorInput(edit_email, String.format(getString(R.string.error_wrongInput),
                    "Email"));
            return false;
        } else if (password.length() > 40) {
            errorInput(edit_password, String.format(getString(R.string.error_wrongInput),
                    "Senha"));
            return false;
        } else if (confirmPassword.length() > 40) {
            errorInput(edit_confirmPassword, String.format(getString(R.string.error_wrongInput),
                    "Confirmar Senha"));
            return false;
        } else {
            return true;
        }
    }

    private boolean passwordEquals() {
        if (!password.equals(confirmPassword)) {
            errorInput(edit_confirmPassword, getString(R.string.error_noMatchPassword));
            return false;
        }
        return true;
    }

    // todo: implementar busca na API se o usuario já existe
    private boolean existUserApi() {
        // Caso exista na API retorna TRUE e impede o novo cadastro do Usuario
        return false;
    }

    // todo: insert User in API
    private boolean inserInApi() {
        return true;
    }

    private void errorInput(TextInputEditText inputEditText, String error) {
        inputEditText.setError(error, null);
        inputEditText.requestFocus();
        managerKeyboard.openKeyboard(inputEditText);
    }

}