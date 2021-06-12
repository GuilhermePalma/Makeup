package com.example.maquiagem;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SaveMakeup#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SaveMakeup extends Fragment {

    // Constantes usadas para as opções no radio Button
    private static final int NO = 0;
    private static final int YES = 1;

    public SaveMakeup() {
        // Required empty public constructor
    }

    // Cria novamente a classe do Fragmente
    public static SaveMakeup newInstance() {
        return new SaveMakeup();
    }

    // Retorna uma View do Fragment para ser usado em uma view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Cria uma view para o Fragment
        View view = inflater.inflate(R.layout.fragment_save_makeup, container, false);

        // Recupera o radioButton com asopções
        RadioGroup radioGroup = view.findViewById(R.id.group_rbtnSave);

        // Metodo caso alguma opção seja selecionada no radioButton
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                // Cria uma View = View do RadioButton
                // Usa o ID do RadioButton selecionado
                View radioButton = group.findViewById(checkedId);

                TextView result = view.findViewById(R.id.txt_resultOption);

                int numberButton = group.indexOfChild(radioButton);

                // Comandos de acordo com o Botão selecionado
                switch (numberButton){
                    case NO:
                        result.setText("Não Salvar");
                        break;
                    case YES:
                        result.setText("Salvar");
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