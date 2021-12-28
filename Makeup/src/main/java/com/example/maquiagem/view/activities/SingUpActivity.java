package com.example.maquiagem.view.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.maquiagem.R;
import com.example.maquiagem.controller.DataBaseHelper;
import com.example.maquiagem.controller.ManagerKeyboard;
import com.example.maquiagem.model.User;
import com.example.maquiagem.view.CustomAlertDialog;
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
    private CustomAlertDialog dialog;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);

        // Instancia os Itens que serão Usados e Configura o AutoCompleteText Idioms
        instanceItens();
        setInputIdioms();

        // Listener dos cliques no Botão "Cadastrar" e "Login"
        executeSingUp();
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

        managerKeyboard = new ManagerKeyboard(SingUpActivity.this);
        dialog = new CustomAlertDialog(this);
        user = new User(this);
    }

    /**
     * Configuração e Listener do AutoCompleteText de Idioms
     */
    private void setInputIdioms() {
        // Configura o AutoCompleteText e suas opções
        String[] array_idioms = getResources().getStringArray(R.array.array_idioms);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, array_idioms);
        autoComplete_idioms.setAdapter(adapter);

        // Obtem o Item selecionado do AutoCompleteText
        autoComplete_idioms.setOnItemClickListener((parent, view, position, id) ->
                user.setIdiom(array_idioms[position])
        );
    }

    /**
     * Valida os Inputs(Campos) e trata os Erros se Houverem
     */
    private boolean validationInputs() {
        String name = Objects.requireNonNull(edit_name.getText()).toString();
        String nickname = Objects.requireNonNull(edit_nickname.getText()).toString();
        String email = Objects.requireNonNull(edit_email.getText()).toString();
        String password = Objects.requireNonNull(edit_password.getText()).toString();
        String confirmPassword = Objects.requireNonNull(edit_confirmPassword.getText()).toString();

        if (!user.validationName(name)) {
            errorInput(edit_name, user.getError_Validation());
            return false;
        } else if (!user.validationNickname(nickname)) {
            errorInput(edit_nickname, user.getError_Validation());
            return false;
        } else if (!user.validationEmail(email)) {
            errorInput(edit_email, user.getError_Validation());
            return false;
        } else if (!user.validationName(name)) {
            errorInput(edit_name, user.getError_Validation());
            return false;
        } else if (!user.validationPassword(password)) {
            errorInput(edit_password, user.getError_Validation());
            return false;
        } else if (!password.equals(confirmPassword)) {
            // Password já validado acima, se forem iguais, a validação de 1 já é o suficiente
            errorInput(edit_confirmPassword, getString(R.string.incorrect_password));
            return false;
        } else if (!user.validationIdiom(user.getIdiom())) {
            autoComplete_idioms.setError(getString(R.string.error_valueRequired), null);
            autoComplete_idioms.requestFocus();
            managerKeyboard.openKeyboard(autoComplete_idioms);
            return false;
        } else {
            // Reatribui um novo Valor à Classe user e Define por Padrão o Tema Claro
            user = new User(this, name, nickname, email, password, user.getIdiom(), false);
            return true;
        }
    }

    /**
     * Listener do Botão "Cadastrar". Cadastra um novo Usuario se passar pelas Validações
     */
    private void executeSingUp() {
        btn_singUp.setOnClickListener(v -> {
            if (validationInputs()) {
                // Obtem a Mensagem de erro (caso exista) da API
                String insert_Api = insertInApi(user);
                if (insert_Api.equals("")) {

                    // Obtem as Informações do Usuario da API
                    User userInformation = getInformationUser(user);

                    // Obtem e Define o JWT na API
                    String jsonWebToken = getJsonWebToken(userInformation);
                    if (jsonWebToken.equals("")) {
                        new CustomAlertDialog(this).message(
                                getString(R.string.title_errorAPI),
                                getString(R.string.error_JWT)).show();
                        return;
                    }

                    // Define o Token e Insere o Usuario no Banco de Dados
                    userInformation.setToken_user(jsonWebToken);
                    insertUserDatabase(userInformation);

                    insertUserDatabase(user);

                    // Define nas Preferences se o Usuario terá ou não que fazer Login a cada Acesso
                    SharedPreferences preferences =
                            getSharedPreferences(LoginActivity.FILE_PREFERENCE, 0);
                    preferences.edit().putBoolean(LoginActivity.LOGIN_NOT_REMEMBER,
                            checkBox_remember.isChecked()).apply();

                    // Inicia a Nova Acticity e Limpa as Activities da Pilha
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finishAffinity();
                } else {
                    // Erro no Cadastro do Usuario na API
                    dialog.message(getString(R.string.title_existUser), insert_Api).show();
                }
            }
            // O metodo de Validação dos Inputs já Trata os Possiveis Erros
        });
    }

    // todo: impementar inserção do usuario na API
    private String insertInApi(User user) {
        return "";
    }


    // todo: implementar obter informações
    private User getInformationUser(User user) {
        return user;
    }

    // todo implementar obtenção do JWT
    public String getJsonWebToken(User user) {
        return "JWT";
    }

    /**
     * Insere o Usuario no Banco de Dados Local(SQLite)
     */
    private void insertUserDatabase(User user) {
        DataBaseHelper database = new DataBaseHelper(this);
        // Exclui dados do Banco de Dados se houverem e Insere o Novo Usuario
        database.deleteAllUsers();
        database.insertUser(user);
        database.close();
    }

    /**
     * Configura como os Erros serão mostrados nos Inputs
     */
    private void errorInput(TextInputEditText inputEditText, String error) {
        inputEditText.setError(error, null);
        inputEditText.requestFocus();
        managerKeyboard.openKeyboard(inputEditText);
    }

}