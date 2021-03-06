package com.example.maquiagem.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;

import androidx.fragment.app.Fragment;

import com.example.maquiagem.R;
import com.example.maquiagem.controller.DataBaseHelper;
import com.example.maquiagem.view.PersonAlertDialogs;

public class FeedbackLocation extends Fragment {

    private final DataBaseHelper helperDatabase;

    // Construtor que recebe context p/ usar no BD
    public FeedbackLocation(Context context) {
        helperDatabase = new DataBaseHelper(context);
    }


    // Cria novamente uma nova instancia do Fragmente
    public static FeedbackLocation newInstance(Context context) {
        return new FeedbackLocation(context);
    }

    // Retorna uma View do Fragment para ser usado em uma view
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
            if (rbtn_correct.isChecked() || rbtn_wrong.isChecked()){
                // Desativa o Botão apos o Clique
                btn_insertDb.setEnabled(false);
                int lastIdLocation = helperDatabase.amountLocation();

                // Caso a posição seja correta, insere no Banco de Dados 'True', se não = 'False'
                helperDatabase.insertTypeLocation(lastIdLocation, rbtn_correct.isChecked());
            } else {
                PersonAlertDialogs personAlertDialogs = new PersonAlertDialogs(
                        view.getContext());
                personAlertDialogs.message(getString(R.string.title_invalidData), Html.fromHtml(
                        getString(R.string.error_selected)).toString())
                        .show();
            }
        });

        rbtn_correct.setOnClickListener(v -> btn_insertDb.setEnabled(true));
        rbtn_wrong.setOnClickListener(v -> btn_insertDb.setEnabled(true));

        // Retorna a view do Fragment
        return view;

    }
}