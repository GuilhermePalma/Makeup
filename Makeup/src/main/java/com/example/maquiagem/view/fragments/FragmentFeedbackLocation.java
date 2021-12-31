package com.example.maquiagem.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;

import androidx.fragment.app.Fragment;

import com.example.maquiagem.R;
import com.example.maquiagem.controller.ManagerDatabase;
import com.example.maquiagem.view.CustomAlertDialog;

/**
 * Classe de um {@link Fragment} que verifica com o Usuario se uma {@link com.example.maquiagem.model.entity.Location}
 * está correta ou não
 */
public class FragmentFeedbackLocation extends Fragment {

    private final ManagerDatabase helperDatabase;
    private final CustomAlertDialog customAlertDialog;

    /**
     * Construtor da Classe {@link FragmentFeedbackLocation}
     *
     * @param context {@link Context} utilizado para Instanciar Itens
     */
    private FragmentFeedbackLocation(Context context) {
        helperDatabase = new ManagerDatabase(context);
        customAlertDialog = new CustomAlertDialog(context);
    }


    /**
     * Metodo Estatico que  Cria uma Instancia da Classe {@link FragmentFeedbackLocation}
     *
     * @param context {@link Context} utilizado para instanciar Itens na Classe
     */
    public static FragmentFeedbackLocation newInstance(Context context) {
        return new FragmentFeedbackLocation(context);
    }

    /**
     * Metodo que Retorna a {@link View} do {@link FragmentFeedbackLocation} configurada
     *
     * @param container Local em que a {@link View} do {@link FragmentFeedbackLocation} será inflada
     * @param inflater  Instancia da Classe {@link LayoutInflater} que Infla/Cria o Fragment
     * @return {@link View}
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Cria uma view para o Fragment (Obtem o Layout)
        View view = inflater.inflate(R.layout.fragment_feedback_location, container, false);

        // Recebe os valores dos Botões do Fragment
        Button btn_insertDb = view.findViewById(R.id.btn_sendData);
        RadioButton rbtn_correct = view.findViewById(R.id.rbtn_trueLocation);
        RadioButton rbtn_wrong = view.findViewById(R.id.rbtn_wrongLocation);

        btn_insertDb.setOnClickListener(v -> {
            if (rbtn_correct.isChecked() || rbtn_wrong.isChecked()) {
                // Desativa o Botão apos o Clique
                btn_insertDb.setEnabled(false);
                int lastIdLocation = helperDatabase.amountLocation();

                // Caso a posição seja correta, insere no Banco de Dados 'True', se não = 'False'
                if (!helperDatabase.setCorrectLocation(lastIdLocation, rbtn_correct.isChecked())) {
                    customAlertDialog.defaultMessage(R.string.error_api, R.string.error_database,
                            null, null, true).show();
                }
            } else {
                customAlertDialog.defaultMessage(R.string.title_invalidData, R.string.error_selected,
                        null, null, true).show();
            }
        });

        rbtn_correct.setOnClickListener(v -> btn_insertDb.setEnabled(true));
        rbtn_wrong.setOnClickListener(v -> btn_insertDb.setEnabled(true));

        // Retorna a view do Fragment
        return view;

    }
}