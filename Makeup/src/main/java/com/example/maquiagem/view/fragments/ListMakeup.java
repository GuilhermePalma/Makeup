package com.example.maquiagem.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.maquiagem.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListMakeup#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListMakeup extends Fragment {

    private static final String TITLE = "title_fragment";


    private String title_fragment;

    // Contrutor Vazio do Fragment
    public ListMakeup() {
    }

    // Intancia do Fragment ---> Inserindo o Valor Instanciado do Titulo
    public static ListMakeup newInstance(String title) {
        ListMakeup fragment = new ListMakeup();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    // Recupera o valor inserido
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title_fragment = getArguments().getString(TITLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list_makeup, container, false);

        TextView text1 = view.findViewById(R.id.txt_fragment);
        text1.setText(title_fragment);

        // Retorna a View Configurada
        return view;
    }
}