package com.example.maquiagem.view.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.maquiagem.R;
import com.example.maquiagem.controller.ManagerKeyboard;
import com.example.maquiagem.view.AlertDialogs;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText edit_nickname;
    private TextInputEditText edit_password;
    private MaterialCheckBox checkBox_remberUser;
    private Button btn_singUp;
    private Button btn_login;

    private String nickname, password;

    private ManagerKeyboard managerKeyboard;
    private SharedPreferences preferences;
    private AlertDialogs dialog;

    private final String FILE_PREFERENCE = "login_user";
    private final String FIRST_LOGIN = "first_login";
    private final String LOGIN_NOT_REMEMBER = "not_remember_login";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        instanceItens();

        btn_singUp.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SingUpActivity.class));
            finish();
        });

        btn_login.setOnClickListener(v -> userLogin());
    }

    private void instanceItens() {
        edit_nickname = findViewById(R.id.editText_loginNickname);
        edit_password = findViewById(R.id.editText_loginPassword);
        checkBox_remberUser = findViewById(R.id.checkbox_rememberUser);
        btn_singUp = findViewById(R.id.btn_goSingUp);
        btn_login = findViewById(R.id.btn_login);

        managerKeyboard = new ManagerKeyboard(LoginActivity.this);
        preferences = getSharedPreferences(FILE_PREFERENCE, 0);
        dialog = new AlertDialogs();
    }

    private void userLogin() {

        getValuesInputs();
        if (filledInputs() && lengthInputs()) {
            if (existUserApi()) {

                preferences.edit().putBoolean(FIRST_LOGIN, false).apply();
                // Define nas Preferences se o Usuario terá ou não que fazer Login a cada Acesso
                preferences.edit().putBoolean(LOGIN_NOT_REMEMBER,
                        checkBox_remberUser.isChecked()).apply();

                // todo: implementar SQLite para Salvar o Login
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                // Usuario não Existe na API
                dialog.message(LoginActivity.this, getString(R.string.title_noExistUser),
                        getString(R.string.error_noExistUser, nickname)).show();
            }
        }
    }

    private void getValuesInputs() {
        nickname = Objects.requireNonNull(edit_nickname.getText()).toString();
        password = Objects.requireNonNull(edit_password.getText()).toString();
    }

    private boolean filledInputs() {
        if (nickname.equals("")) {
            errorInput(edit_nickname, getString(R.string.error_valueRequired));
            return false;
        } else if (password.equals("")) {
            errorInput(edit_password, getString(R.string.error_valueRequired));
            return false;
        } else {
            return true;
        }
    }

    private boolean lengthInputs() {
        if (nickname.length() > 35) {
            errorInput(edit_nickname, String.format(
                    getString(R.string.error_wrongInput), "Nome de Usuario"));
            return false;
        } else if (password.length() > 40) {
            errorInput(edit_password, String.format(
                    getString(R.string.error_wrongInput), "Senha"));
            return false;
        } else {
            return true;
        }
    }

    // todo: implementar metodo da API
    private boolean existUserApi() {
        // Caso exista na API retorna TRUE e permite o login
        return true;
    }

    private void errorInput(TextInputEditText inputEditText, String error) {
        inputEditText.setError(error, null);
        inputEditText.requestFocus();
        managerKeyboard.openKeyboard(inputEditText);
    }
}