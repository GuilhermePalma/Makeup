package com.example.maquiagem.view.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.maquiagem.R;
import com.example.maquiagem.controller.ManagerDatabase;
import com.example.maquiagem.controller.ManagerKeyboard;
import com.example.maquiagem.controller.ManagerSharedPreferences;
import com.example.maquiagem.model.entity.User;
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
    private CustomAlertDialog customDialog;
    private User user;
    private Context context;

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
            startActivity(new Intent(context, LoginActivity.class));
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

        context = SingUpActivity.this;

        managerKeyboard = new ManagerKeyboard(context);
        customDialog = new CustomAlertDialog(context);
        user = new User(context);
    }

    /**
     * Configuração e Listener do AutoCompleteText de Idioms
     */
    private void setInputIdioms() {
        // Configura o AutoCompleteText e suas opções
        String[] array_idioms = getResources().getStringArray(R.array.array_idioms);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
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
            user = new User(context, name, nickname, email, password, user.getIdiom(), false);
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
                if (insertInApi(user)) {

                    // Obtem as Informações do Usuario da API
                    User userInformation = getInformationUser(user);

                    // Obtem e Define o JWT na API
                    String jsonWebToken = getJsonWebToken(userInformation);
                    if (jsonWebToken.equals("")) {
                        customDialog.defaultMessage(R.string.title_errorAPI, R.string.error_JWT, null,
                                null, true).show();
                        return;
                    }

                    // Define o Token e Insere o Usuario no Banco de Dados
                    ManagerSharedPreferences managerPreferences = new ManagerSharedPreferences(context);
                    managerPreferences.setUserToken(jsonWebToken);

                    // Salva o Usuario no Banco de Dados
                    ManagerDatabase database = new ManagerDatabase(this);
                    if(!database.insertUser(userInformation)){
                        customDialog.defaultMessage(R.string.title_errorAPI, R.string.error_database,
                                null, null, true).show();
                        return;
                    }

                    // Define nas Preferences se o Usuario terá ou não que fazer Login a cada Acesso
                    managerPreferences.setRememberLogin(checkBox_remember.isChecked());

                    // Inicia a Nova Acticity e Limpa as Activities da Pilha
                    startActivity(new Intent(context, MainActivity.class));
                    finishAffinity();
                } else {
                    // Erro no Cadastro do Usuario na API
                    customDialog.defaultMessage(R.string.title_existUser, R.string.error_existUser, null,
                            new String[]{""}, true).show();
                }
            }
            // O metodo de Validação dos Inputs já Trata os Possiveis Erros
        });
    }

    // todo: impementar inserção do usuario na API
    private boolean insertInApi(User user) {
        return true;
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
     * Configura como os Erros serão mostrados nos Inputs
     */
    private void errorInput(TextInputEditText inputEditText, String error) {
        inputEditText.setError(error, null);
        inputEditText.requestFocus();
        managerKeyboard.openKeyboard(inputEditText);
    }

}