package com.example.maquiagem.view.activities;

import static com.example.maquiagem.model.entity.User.NO_MAX_LENGTH;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.maquiagem.R;
import com.example.maquiagem.controller.ManagerDatabase;
import com.example.maquiagem.controller.ManagerSharedPreferences;
import com.example.maquiagem.model.entity.User;
import com.example.maquiagem.view.CustomAlertDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private TextInputEditText editText_name;
    private TextInputEditText editText_email;
    private TextInputEditText editText_password;
    private TextInputEditText editText_nickname;

    private TextInputLayout[] editLayouts_array;

    private Button btn_changeUser;

    private View header_profile;
    private LinearLayout layout_buttons;
    private Button btn_confirm;
    private Button btn_abort;

    private User user;
    private Context context;
    private CustomAlertDialog customDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Configura os Elementos da Tela
        setUpToolBar();
        instanceItems();

        // Exibe os Dados do Usuario na Tela
        if (user == null) {
            customDialog.messageWithCloseWindow(this, R.string.title_noExistUser,
                    R.string.error_noExistUser, null, null).show();
        } else {
            TextView txt_description = header_profile.findViewById(R.id.txt_subtitleHeaderList);
            txt_description.setText(R.string.text_detailsProfile);
            showDataUser();
        }
    }

    /**
     * Instancia Itens/Classes que serão usadas
     */
    private void instanceItems() {
        context = ProfileActivity.this;
        customDialog = new CustomAlertDialog(this);

        header_profile = findViewById(R.id.header_profile);
        btn_changeUser = findViewById(R.id.btn_changeUser);

        editText_name = findViewById(R.id.editText_profileName);
        editText_email = findViewById(R.id.editText_profileEmail);
        editText_password = findViewById(R.id.editText_profilePassword);
        editText_nickname = findViewById(R.id.editText_profileNickname);

        TextInputLayout editLayout_name = findViewById(R.id.editLayout_profileName);
        TextInputLayout editLayout_email = findViewById(R.id.editLayout_profileEmail);
        TextInputLayout editLayout_password = findViewById(R.id.editLayout_profilePassword);
        TextInputLayout editLayout_nickname = findViewById(R.id.editLayout_profileNickname);
        editLayouts_array = new TextInputLayout[]{editLayout_name, editLayout_email, editLayout_password,
                editLayout_nickname};

        layout_buttons = findViewById(R.id.linearLayout_buttons);
        btn_confirm = findViewById(R.id.btn_confirmChange);
        btn_abort = findViewById(R.id.btn_abortChange);

        ManagerDatabase database = new ManagerDatabase(context);
        user = database.selectUser();
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
     * Exibe os Dados do Usuario na Tela
     */
    private void showDataUser() {
        TextView txt_helloProfile = header_profile.findViewById(R.id.txt_titleHeaderList);
        txt_helloProfile.setText(String.format("Olá %s", user.customStringFormat(
                user.getNickname(), "@", NO_MAX_LENGTH)));

        // Exibe os Dados do Usuario
        editText_name.setText(user.getName());
        editText_email.setText(user.getEmail());
        editText_nickname.setText(user.getNickname());
        editText_password.setText(user.getPassword());

        // Listener do Botão de "Finalizar"/"Cancelar" Alterações
        listenerButtons();
        enableChangeInputs(false);
    }

    private void enableChangeInputs(boolean enableInput) {
        for (TextInputLayout inputLayout : editLayouts_array) {
            inputLayout.setEnabled(enableInput);
            inputLayout.setEndIconVisible(false);
        }
    }

    /**
     * Listener do Botão "Cancelar" e "Confirmar" do Formulario de Cadastro
     */
    private void listenerButtons() {
        // Botão de Alteração do Usuario
        btn_changeUser.setOnClickListener((v) -> {
            enableChangeInputs(true);
            layout_buttons.setVisibility(View.VISIBLE);
            btn_changeUser.setVisibility(View.GONE);
        });

        // Botão de Cancelar Alterações
        btn_abort.setOnClickListener(v -> {
            // Carrega novamente o Layout dos Itens
            layout_buttons.setVisibility(View.GONE);
            btn_changeUser.setVisibility(View.VISIBLE);
            showDataUser();
        });

        // Botão de Concluir as Alterações
        btn_confirm.setOnClickListener(v -> {
            // Obtem o Valor do Campo a Ser Alterado + Novo Valor
            String name_check = Objects.requireNonNull(editText_name.getText()).toString();
            String email_check = Objects.requireNonNull(editText_email.getText()).toString();
            String password_check = Objects.requireNonNull(editText_password.getText()).toString();
            String nickname_check = Objects.requireNonNull(editText_nickname.getText()).toString();

            // Valida o novo Nome Inserido
            if (!user.validationName(name_check)) {
                editText_name.setError(user.getError_Validation(), null);
            } else if (!user.validationEmail(email_check)) {
                editText_email.setError(user.getError_Validation(), null);
            } else if (!user.validationPassword(password_check)) {
                editText_password.setError(user.getError_Validation(), null);
            } else if (!user.validationNickname(nickname_check)) {
                editText_nickname.setError(user.getError_Validation(), null);
            } else {
                user.setName(name_check);
                user.setEmail(email_check);
                user.setPassword(password_check);
                user.setNickname(nickname_check);

                if (updateUser(user)) {
                    // Exibe uma Mensangem informando que a alteração deu certo
                    customDialog.defaultMessage(R.string.title_updateUser, 0,
                            null, null, false).show();

                    // Recarrega o Layout do Perfil do Usuario
                    layout_buttons.setVisibility(View.GONE);
                    btn_changeUser.setVisibility(View.VISIBLE);
                    showDataUser();
                } else {
                    customDialog.defaultMessage(R.string.title_errorAPI, R.string.error_api, null,
                            null, true).show();
                }
            }
        });
    }

    // Todo Implementar API Interna
    private boolean updateUser(User user) {
        // Atualizar Usuario na API

        // Gerar novo Token
        String new_JWT = "JWT New";

        // Obter Infos do Usuario

        // Salva o Token nas Preferences
        new ManagerSharedPreferences(context).setUserToken(new_JWT);

        // Atualiza o Usuario no Banco de dados Local
        ManagerDatabase database = new ManagerDatabase(context);
        return database.updateUser(user);
    }

}