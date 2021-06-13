package com.example.maquiagem;

import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.maquiagem.model.DataBaseMakeup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedbackLocation#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedbackLocation extends Fragment {

    // Constantes usadas para as opções no radio Button
    private static final int NO = 0;
    private static final int YES = 1;

    private DataBaseMakeup helper = new DataBaseMakeup(getActivity());


    public FeedbackLocation() {
        // Required empty public constructor
    }

    // Cria novamente a classe do Fragmente
    public static FeedbackLocation newInstance() {
        return new FeedbackLocation();
    }

    // Retorna uma View do Fragment para ser usado em uma view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Cria uma view para o Fragment
        View view = inflater.inflate(R.layout.fragment_feedback_location, container, false);

        // Recupera o radioButton com asopções
        RadioGroup radioGroup = view.findViewById(R.id.group_rbtnLocation);

        // Metodo caso alguma opção seja selecionada no radioButton
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                // Cria uma View = View do RadioButton
                // Usa o ID do RadioButton selecionado
                View radioButton = group.findViewById(checkedId);

                int numberButton = group.indexOfChild(radioButton);

                // Comandos de acordo com o Botão selecionado
                switch (numberButton){
                    case NO:
                        helper.insertLocation("wrong_location");
                        break;
                    case YES:
                        helper.insertLocation("correct_location");
                        break;
                    default:
                        break;
                }

            }
        });

        // Retorna a view do Fragment
        return view;

    }
}