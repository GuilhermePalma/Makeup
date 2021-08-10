package com.example.maquiagem.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.maquiagem.R;
import com.example.maquiagem.view.AlertDialogs;
import com.example.maquiagem.view.activities.ResultActivity;

public class SearchMakeup extends Fragment {

    private Context context;

    // Contrutor Vazio do Fragment
    public SearchMakeup(Context context) {
        this.context = context;
    }

    // Nova instancia do Fragment SearchMakeup
    public static SearchMakeup newInstance(Context context) {
        return new SearchMakeup(context);
    }

    // Criação do Fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Define a View, com o design  usado
        View view = inflater.inflate(R.layout.fragment_search_makeup, container, false);

        // Define os EditText
        EditText editType = view.findViewById(R.id.edit_type);
        EditText editBrand = view.findViewById(R.id.edit_brand);

        // Listener do Botão "Pesquisar"
        Button search = view.findViewById(R.id.btn_search);
        search.setOnClickListener(v -> {
            // Recupera os valores inseridos pelo Usario
            String infoType = editType.getText().toString();
            String infoBrand = editBrand.getText().toString();

            if (infoType.equals("")) {
                editType.setError(getString(R.string.error_valueRequired));
                editBrand.requestFocus();
                return;
            } else if (infoBrand.equals("")) {
                editBrand.setError(getString(R.string.error_valueRequired));
                editBrand.requestFocus();
                return;
            }

            // Fecha o Teclado
            closeKeyboard(view);

            //Validação da Conexão Ativa
            if (connectionAvailable()) {

                // Limpa os valores da Tela Inteira
                editType.setText(R.string.string_empty);
                editBrand.setText(R.string.string_empty);

                // Inicia a ActivityResult passando os Dados
                Intent activityResult = new Intent(getContext(), ResultActivity.class);
                activityResult.putExtra("product_type", infoType);
                activityResult.putExtra("brand", infoBrand);
                startActivity(activityResult);

            } else {
                // Mostra uma Mensagem na Tela
                new AlertDialogs().message(getContext(), getString(R.string.title_noConnection),
                        getString(R.string.error_connection)).show();
            }
        });

        // Infla o Layout do Fragment
        return view;
    }

    // Valida a Conexão com a Internet
    private boolean connectionAvailable() {
        ConnectivityManager connectionManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo;

        // Valida se o serviço de Internet está ativo
        if (connectionManager != null) {
            networkInfo = connectionManager.getActiveNetworkInfo();

            // Valida se existe conexão ativa
            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            } else {
                // Erro na Conexão
                Log.e("NO CONECTED", "\n Erro na conexão com a Internet" +
                        "\nConexão: " + networkInfo);
                return false;
            }

        } else {
            Log.e("NO SERVICE", "\n Erro no serviço de Internet" +
                    "\nServiço: " + connectionManager);
            return false;
        }
    }

    // Metodo que Fecha o Teclado
    private void closeKeyboard(View view) {
        InputMethodManager keyboardManager = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);

        // Caso o Serviço do Teclado esteja disponivel
        if (keyboardManager != null) {
            keyboardManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}