package com.example.maquiagem.view.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.maquiagem.R;
import com.example.maquiagem.controller.ManagerDatabase;
import com.example.maquiagem.controller.ManagerResources;
import com.example.maquiagem.controller.ManagerSharedPreferences;
import com.example.maquiagem.model.entity.User;
import com.example.maquiagem.view.CustomAlertDialog;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText edit_nickname;
    private TextInputEditText edit_password;
    private MaterialCheckBox checkBox_rememberUser;
    private Button btn_singUp;
    private Button btn_login;
    private User user;
    private CustomAlertDialog customDialog;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Instancia os Widgets E Classes que serão Usados
        instanceItems();

        // Verifica se o Usuario Possui Login (Sem o Lembrar Login)
        checkLogin();

        // Listener no Botão "Cadastrar" e "Login"
        btn_singUp.setOnClickListener(v -> startActivity(new Intent(context, SingUpActivity.class)));
        executeLogin();
    }

    private void checkLogin() {
        ManagerDatabase database = new ManagerDatabase(context);
        User user = database.selectUser();

        if (user != null) edit_nickname.setText(user.getNickname());
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
        context = LoginActivity.this;
        customDialog = new CustomAlertDialog(context);
    }

    /**
     * Listener do Botão Login. Se passar pelas Validações, realiza o Login do Usuario
     */
    private void executeLogin() {

        btn_login.setOnClickListener(v -> {
            if (validationInputs()) {
                if (existUserApi(user)) {

                    // Obtem as Informações do Usuario da API
                    User userInformation = getInformationUser(user);

                    // Obtem e Define o JWT na API
                    String jsonWebToken = getJsonWebToken(userInformation);
                    if (ManagerResources.isNullOrEmpty(jsonWebToken)) {
                        customDialog.defaultMessage(R.string.title_errorAPI, R.string.error_JWT,
                                null, null, true).show();
                        return;
                    }

                    // Define o Token e Insere o Usuario no Banco de Dados
                    ManagerSharedPreferences managerPreferences = new ManagerSharedPreferences(context);
                    managerPreferences.setUserToken(jsonWebToken);

                    // Salva o Usuario no Banco de Dados
                    ManagerDatabase database = new ManagerDatabase(this);
                    if (!database.insertUser(user)) {
                        customDialog.defaultMessage(R.string.title_errorAPI, R.string.error_database,
                                null, null, true).show();
                        return;
                    }

                    // Define nas Preferences se o Usuario terá ou não que fazer Login a cada Acesso
                    managerPreferences.setRememberLogin(checkBox_rememberUser.isChecked());

                    // Inicia a Nova Acticity e Limpa as Activities da Pilha
                    startActivity(new Intent(context, MainActivity.class));
                    finishAffinity();
                } else {
                    // Mensagem de Erro de Usuario não Cadastrado
                    customDialog.defaultMessage(R.string.title_invalidData, R.string.error_noExistUser,
                            null, new String[]{""}, true).show();
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

    /**
     * Verifica se o Usuario existe na API
     *
     * @param user Instancia de um {@link User} que será verificado
     * @return true|false
     */
    private boolean existUserApi(User user) {
        // todo: implementar metodo da API

        // Verifica se o Usuario existe no Banco de Dados Local ---> Exibe o Email no Input
        // Se não, Solicita o novo Login --> Autentica --> Gera o Token --> Salva o Usuario

        return true;
    }

    /**
     * Obtem as Informações do Usuario na API
     *
     * @param user Instancia do {@link User} com dados que serão utilizados para obter os demais
     *             dados da API
     * @return {@link User}
     */
    private User getInformationUser(User user) {
        //todo implementar = obter informações
        return user;
    }


    /**
     * Obtem um JWT Valioo para usar nas Opreações da API
     *
     * @param user Instancia de um {@link User} que obterá o JWT
     */
    private String getJsonWebToken(User user) {
        // retorna o jwt p/ salvar no banco de dados
        return "JWT";
    }

    /**
     * Configuração do Erro nos Inputs Invalidos
     *
     * @param inputEditText {@link TextInputEditText} em que o erro será exibido
     * @param error         {@link String} informando o Erro
     */
    private void errorInput(TextInputEditText inputEditText, String error) {
        inputEditText.setError(error, null);
        inputEditText.requestFocus();
        ManagerResources.openKeyboard(context, inputEditText);
    }
}