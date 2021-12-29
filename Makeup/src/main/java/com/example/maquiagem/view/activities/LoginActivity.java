package com.example.maquiagem.view.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.maquiagem.R;
import com.example.maquiagem.controller.DataBaseHelper;
import com.example.maquiagem.controller.ManagerKeyboard;
import com.example.maquiagem.model.entity.User;
import com.example.maquiagem.view.CustomAlertDialog;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    public final static String FILE_PREFERENCE = "com.example.maquiagem";
    public final static String LOGIN_NOT_REMEMBER = "not_remember_login";
    private TextInputEditText edit_nickname;
    private TextInputEditText edit_password;
    private MaterialCheckBox checkBox_rememberUser;
    private Button btn_singUp;
    private Button btn_login;
    private User user;
    private ManagerKeyboard managerKeyboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Instancia os Widgets E Classes que serão Usados
        instanceItems();

        // Listener no Botão "Cadastrar" e "Login"
        btn_singUp.setOnClickListener(v -> startActivity(new Intent(
                LoginActivity.this, SingUpActivity.class)));
        executeLogin();
    }

    /**
     * Instancia os Widgets/Classes que serão usadas na Activity
     */
    private void instanceItems() {
        edit_nickname = findViewById(R.id.editText_loginNickname);
        edit_password = findViewById(R.id.editText_loginPassword);
        checkBox_rememberUser = findViewById(R.id.checkbox_rememberUser);
        btn_singUp = findViewById(R.id.btn_goSingUp);
        btn_login = findViewById(R.id.btn_login);

        managerKeyboard = new ManagerKeyboard(LoginActivity.this);
    }

    /**
     * Listener do Botão Login. Se passar pelas Validações, realiza o Login do Usuario
     */
    private void executeLogin() {

        btn_login.setOnClickListener(v -> {
            if (validationInputs()) {
                String apiMessage = existUserApi(user);
                if (apiMessage.equals("")) {

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

                    // Define nas Preferences se o Usuario terá ou não que fazer Login a cada Acesso
                    SharedPreferences preferences = getSharedPreferences(FILE_PREFERENCE, 0);
                    preferences.edit().putBoolean(
                            LOGIN_NOT_REMEMBER, checkBox_rememberUser.isChecked()).apply();

                    // Inicia a Nova Acticity e Limpa as Activities da Pilha
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finishAffinity();
                } else {
                    // Mensagem de Erro de Usuario não Cadastrado
                    new CustomAlertDialog(this).message(getString(R.string.title_invalidData), apiMessage).show();
                }
            }
            // Erro dos Inputs já são tratados no proprio metodo
        });
    }

    /**
     * Realiza a Validação dos Inputs do Login (Nickname e Senha)
     */
    private boolean validationInputs() {
        // Obtem os Valores
        String nickname = Objects.requireNonNull(edit_nickname.getText()).toString();
        String password = Objects.requireNonNull(edit_password.getText()).toString();

        // Valida os Valores e retorna caso tenha algum erro
        user = new User(this);
        if (!user.validationNickname(nickname)) {
            errorInput(edit_nickname, user.getError_Validation());
            return false;
        } else if (!user.validationPassword(password)) {
            errorInput(edit_password, user.getError_Validation());
            return false;
        } else {
            // Instancia o Nickname e Senha do Usario
            user.setNickname(nickname);
            user.setPassword(password);
            return true;
        }
    }

    // todo: implementar metodo da API
    private String existUserApi(User user) {
        // Realiza a a VAlidação se O Usuario existe no Banco de Dados

        // Obtem o JsonWebToken

        return "";
    }

    //todo implementar = obter informações
    private User getInformationUser(User user) {
        return user;
    }


    /**
     * Obtem um JWT Valioo para usar nas Opreações da API
     */
    private String getJsonWebToken(User user) {
        // retorna o jwt p/ salvar no banco de dados
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
     * Configuração do Erro nos Inputs Invalidos
     */
    private void errorInput(TextInputEditText inputEditText, String error) {
        inputEditText.setError(error, null);
        inputEditText.requestFocus();
        managerKeyboard.openKeyboard(inputEditText);
    }
}