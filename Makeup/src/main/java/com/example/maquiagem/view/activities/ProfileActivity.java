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
import com.example.maquiagem.model.User;
import com.example.maquiagem.view.PersonAlertDialogs;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private String VALIDATION_OK;
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

    private DataBaseHelper dataBaseHelper;
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
        VALIDATION_OK = getString(R.string.validation_ok);

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
    }

    /**
     * Configura a ToolBar
     */
    private void setUpToolBar() {
        // Criação da ToolBar e Criação da seta de voltar
        Toolbar toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitle(R.string.toolbar_profile);
        setSupportActionBar(toolbar);
        // Icon de voltar para a Tela Home
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_return_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    /**
     * Configura/Valida o Usuario e Mostra seus dados se existirem
     */
    private void setUpWindow() {
        // Obtem os Dados Salvos da API no Banco de Dados (SQLIte)
        // Verifica se Existe Dados ---> Se não = Mensagem Erro

        //showDataUser();
        noDataUser();
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
                PersonAlertDialogs dialog = new PersonAlertDialogs(this);

                //todo incluir metodos da api
                if (valueTitleChange.equals(HINT_NAME)) {
                    // Valida o novo Nome Inserido
                    String resultValidation = user.validationName(valueInput);
                    if (resultValidation.equals(VALIDATION_OK)) {
                        user.setName(valueInput);
                        dialog.message("Nome Alterado", "Nome Alterado para: "
                                + user.getName()).show();

                        // Recarrega os Dados e Layout na Tela
                        showDataUser();
                    } else editText_update.setError(resultValidation, null);
                } else if (valueTitleChange.equals(HINT_EMAIL)) {
                    // Valida o Novo Email Inserido
                    String resultValidation = user.validationEmail(valueInput);
                    if (resultValidation.equals(VALIDATION_OK)) {
                        user.setEmail(valueInput);
                        dialog.message("Email Alterado", "Email Alterado para: "
                                + user.getEmail()).show();

                        // Recarrega os Dados e Layout na Tela
                        showDataUser();
                    } else editText_update.setError(resultValidation, null);
                } else if (valueTitleChange.equals(HINT_PASSWORD)) {
                    // Valida a Nova Senha inserida
                    String resultValidation = user.validationPassword(valueInput);
                    if (resultValidation.equals(VALIDATION_OK)) {
                        user.setPassword(valueInput);
                        dialog.message("Senha Alterada", "Senha Alterado para: "
                                + user.getPassword()).show();

                        // Recarrega os Dados e Layout na Tela
                        showDataUser();
                    } else editText_update.setError(resultValidation, null);
                } else if (valueTitleChange.equals(HINT_NICKNAME)) {
                    // Valida o Novo Nickname Inserido
                    String resultValidation = user.validationNickname(valueInput);
                    if (resultValidation.equals(VALIDATION_OK)) {
                        user.setNickname(valueInput);
                        dialog.message("Nickname Alterado", "Nickname Alterado para: "
                                + user.getNickname()).show();

                        // Recarrega os Dados e Layout na Tela
                        showDataUser();
                    } else editText_update.setError(resultValidation, null);
                } else {
                    dialog.message(getString(R.string.title_invalidData),
                            getString(R.string.error_api)).show();

                    // Recarrega os Dados e Layout na Tela
                    showDataUser();
                }
            }
        });
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