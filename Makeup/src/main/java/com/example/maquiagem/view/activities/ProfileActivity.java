package com.example.maquiagem.view.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.maquiagem.R;
import com.example.maquiagem.controller.DataBaseHelper;
import com.example.maquiagem.model.entity.User;
import com.example.maquiagem.view.CustomAlertDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private String HINT_NAME;
    private String HINT_EMAIL;
    private String HINT_PASSWORD;
    private String HINT_NICKNAME;

    private TextView txt_nickname;
    private TextView txt_name;
    private TextView txt_email;
    private TextView txt_password;
    private TextView txt_error;
    private TextView txt_nicknameChange;

    private TextView title_inputChange;
    private Button btn_confirm;
    private Button btn_abort;

    private Button btn_nickname;
    private Button btn_name;
    private Button btn_email;
    private Button btn_password;

    private TextInputLayout inputLayout_update;
    private TextInputEditText editText_update;

    private LinearLayout layout_buttons;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Configura os Elementos da Tela
        setUpToolBar();
        instanceItems();
        setUpWindow();
    }

    /**
     * Instancia Itens/Classes que serão usadas
     */
    private void instanceItems() {
        HINT_NAME = getString(R.string.hint_name);
        HINT_EMAIL = getString(R.string.hint_email);
        HINT_PASSWORD = getString(R.string.hint_password);
        HINT_NICKNAME = getString(R.string.hint_nickname);

        txt_nickname = findViewById(R.id.txt_nickname);
        txt_name = findViewById(R.id.txt_name);
        txt_email = findViewById(R.id.txt_email);
        txt_password = findViewById(R.id.txt_password);
        txt_nicknameChange = findViewById(R.id.txt_nicknameChange);
        txt_error = findViewById(R.id.txt_errorDataUser);
        btn_nickname = findViewById(R.id.btn_changeNickname);
        btn_name = findViewById(R.id.btn_changeName);
        btn_email = findViewById(R.id.btn_changeEmail);
        btn_password = findViewById(R.id.btn_changePassword);

        btn_confirm = findViewById(R.id.btn_confirmChange);
        btn_abort = findViewById(R.id.btn_abortChange);
        title_inputChange = findViewById(R.id.title_inputChange);

        layout_buttons = findViewById(R.id.linearLayout_buttons);
        inputLayout_update = findViewById(R.id.inputLayout_updateUser);
        editText_update = findViewById(R.id.editText_updateUser);

        DataBaseHelper database = new DataBaseHelper(this);
        user = database.selectUser(this);
        database.close();
    }

    /**
     * Configura a ToolBar
     */
    private void setUpToolBar() {
        // Criação da ToolBar e Criação da seta de voltar
        Toolbar toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitle(R.string.toolbar_profile);
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
     * Configura/Valida o Usuario e Mostra seus dados se existirem
     */
    private void setUpWindow() {
        // Obtem os Dados Salvos da API no Banco de Dados (SQLIte)
        // Verifica se Existe Dados ---> Se não = Mensagem Erro

        if (user == null) noDataUser();
        showDataUser();
    }

    /**
     * Exibe os Dados do Usuario na Tela
     */
    private void showDataUser() {
        title_inputChange.setVisibility(View.GONE);
        inputLayout_update.setVisibility(View.GONE);
        layout_buttons.setVisibility(View.GONE);
        editText_update.setText("");

        txt_nickname.setText(String.format("Olá %s", user.getNickname()));
        txt_name.setText(user.getName());
        txt_email.setText(user.getEmail());
        txt_password.setText(user.getPassword());
        txt_nicknameChange.setText(user.getNickname());

        // Listener dos botões de "Alterar" e "Cancelar"/"Confirmar"
        listenerButtonChanges();
        listenerEndChange();
    }

    /**
     * Listener dos Botões "Alterar" ao lado dos dados do Usuario
     */
    private void listenerButtonChanges() {
        // Listener do Clique nos botões "Alterar"
        btn_name.setOnClickListener(v -> {
            title_inputChange.setVisibility(View.VISIBLE);
            inputLayout_update.setVisibility(View.VISIBLE);
            layout_buttons.setVisibility(View.VISIBLE);

            title_inputChange.setText(HINT_NAME);
            inputLayout_update.setHint(HINT_NAME);
            editText_update.setText(user.getName());
        });
        btn_email.setOnClickListener(v -> {
            title_inputChange.setVisibility(View.VISIBLE);
            inputLayout_update.setVisibility(View.VISIBLE);
            layout_buttons.setVisibility(View.VISIBLE);

            title_inputChange.setText(HINT_EMAIL);
            inputLayout_update.setHint(HINT_EMAIL);
            editText_update.setText(user.getEmail());
        });
        btn_password.setOnClickListener(v -> {
            title_inputChange.setVisibility(View.VISIBLE);
            inputLayout_update.setVisibility(View.VISIBLE);
            layout_buttons.setVisibility(View.VISIBLE);

            title_inputChange.setText(HINT_PASSWORD);
            inputLayout_update.setHint(HINT_PASSWORD);
            editText_update.setText(user.getPassword());
        });
        btn_nickname.setOnClickListener(v -> {
            title_inputChange.setVisibility(View.VISIBLE);
            inputLayout_update.setVisibility(View.VISIBLE);
            layout_buttons.setVisibility(View.VISIBLE);

            title_inputChange.setText(HINT_NICKNAME);
            inputLayout_update.setHint(HINT_NICKNAME);
            editText_update.setText(user.getNickname());
        });
    }

    /**
     * Listener do Botão "Cancelar" e "Confirmar" do Formulario de Cadastro
     */
    private void listenerEndChange() {
        btn_abort.setOnClickListener(v -> {
            if (inputLayout_update.getVisibility() == View.VISIBLE) {
                title_inputChange.setVisibility(View.GONE);
                inputLayout_update.setVisibility(View.GONE);
                layout_buttons.setVisibility(View.GONE);
            }
        });

        btn_confirm.setOnClickListener(v -> {
            if (inputLayout_update.getVisibility() == View.VISIBLE) {
                // Obtem o Valor do Campo a Ser Alterado + Novo Valor
                String valueInput = Objects.requireNonNull(editText_update.getText()).toString();
                String valueTitleChange = title_inputChange.getText().toString();
                CustomAlertDialog dialog = new CustomAlertDialog(this);

                if (valueTitleChange.equals(HINT_NAME)) {
                    // Valida o novo Nome Inserido
                    if (user.validationName(valueInput)) {

                        // Atualiza o Nome e Tenta Atualizar na API e Banco Local (SQLite)
                        user.setName(valueInput);
                        if (updateUser(user)) {
                            dialog.defaultMessage(R.string.txt_changed, R.string.txt_changedDetails,
                                    new String[]{"Nome"}, new String[]{"Nome", user.getName()},
                                    true).show();
                            showDataUser();

                        } else {
                            dialog.defaultMessage(R.string.title_errorAPI, R.string.error_change, null,
                                    new String[]{"Nome"}, true).show();
                        }
                    } else editText_update.setError(user.getError_Validation(), null);
                } else if (valueTitleChange.equals(HINT_EMAIL)) {
                    // Valida o Novo Email Inserido
                    if (user.validationEmail(valueInput)) {

                        // Atualiza o Email e Tenta Atualizar na API e Banco Local (SQLite)
                        user.setEmail(valueInput);
                        if (updateUser(user)) {
                            dialog.defaultMessage(R.string.txt_changed, R.string.txt_changedDetails,
                                    new String[]{"Email"}, new String[]{"Email", user.getEmail()},
                                    true).show();
                            showDataUser();
                        } else {
                            dialog.defaultMessage(R.string.title_errorAPI, R.string.error_change, null,
                                    new String[]{"Email"}, true).show();
                        }

                    } else editText_update.setError(user.getError_Validation(), null);
                } else if (valueTitleChange.equals(HINT_PASSWORD)) {
                    // Valida a Nova Senha inserida
                    if (user.validationPassword(valueInput)) {

                        // Atualiza o Nome e Tenta Atualizar na API e Banco Local (SQLite)
                        user.setPassword(valueInput);
                        if (updateUser(user)) {
                            dialog.defaultMessage(R.string.txt_changed, R.string.txt_changedDetails,
                                    new String[]{"Senha"}, new String[]{"Senha", user.getEmail()},
                                    true).show();
                            showDataUser();
                        } else {
                            dialog.defaultMessage(R.string.title_errorAPI, R.string.error_change, null,
                                    new String[]{"Senha"}, true).show();
                        }

                    } else editText_update.setError(user.getError_Validation(), null);
                } else if (valueTitleChange.equals(HINT_NICKNAME)) {

                    // Valida o Novo Nickname Inserido
                    if (user.validationNickname(valueInput)) {

                        // Atualiza o Nickname e Tenta Atualizar na API e Banco Local (SQLite)
                        user.setNickname(valueInput);
                        if (updateUser(user)) {
                            dialog.defaultMessage(R.string.txt_changed, R.string.txt_changedDetails,
                                    new String[]{"Nickname"}, new String[]{"Nickname", user.getNickname()},
                                    true).show();
                            showDataUser();
                        } else {
                            dialog.defaultMessage(R.string.title_errorAPI, R.string.error_change, null,
                                    new String[]{"Nickname"}, true).show();
                        }

                    } else editText_update.setError(user.getError_Validation(), null);
                } else {
                    dialog.defaultMessage(R.string.title_invalidData, R.string.error_api, null,
                            null, true).show();

                    // Recarrega os Dados e Layout na Tela
                    showDataUser();
                }
            }
        });
    }

    //todo: atualiza o nome do usuario na API
    private boolean updateUser(User user) {
        // Operação sem erro = Retorno ""
        // Gerar programaticamente um novo Token
        String remove_newJWT = "JWT New";
        user.setToken_user(remove_newJWT);

        // Atualiza o Usario no Banco Local
        DataBaseHelper database = new DataBaseHelper(this);
        database.updateUser(user);
        database.close();

        return true;
    }

    /**
     * Lyout da Tela ao não ter Dados do usuario
     */
    private void noDataUser() {
        TextView titleName = findViewById(R.id.title_name);
        TextView titleEmail = findViewById(R.id.title_email);
        TextView titlePassword = findViewById(R.id.title_password);
        TextView titleNickname = findViewById(R.id.title_nickname);

        titleName.setVisibility(View.GONE);
        titleEmail.setVisibility(View.GONE);
        titlePassword.setVisibility(View.GONE);
        titleNickname.setVisibility(View.GONE);
        txt_nickname.setVisibility(View.GONE);
        txt_name.setVisibility(View.GONE);
        txt_email.setVisibility(View.GONE);
        txt_password.setVisibility(View.GONE);
        txt_nicknameChange.setVisibility(View.GONE);
        btn_nickname.setVisibility(View.GONE);
        btn_name.setVisibility(View.GONE);
        btn_email.setVisibility(View.GONE);
        btn_password.setVisibility(View.GONE);
        layout_buttons.setVisibility(View.GONE);
        inputLayout_update.setVisibility(View.GONE);

        txt_error.setVisibility(View.VISIBLE);
    }

}