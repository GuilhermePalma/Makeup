package com.example.maquiagem;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private EditText editType;
    private EditText editBrand;
    private TextView name;
    private TextView currencyPrice;
    private TextView brandType;
    private TextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editType = findViewById(R.id.edit_type);
        editBrand = findViewById(R.id.edit_brand);

        if (getSupportLoaderManager().getLoader(0) != null) {
            getSupportLoaderManager().initLoader(0, null, this);
        }

    }


    public void LoadResult(View view) {

        //Instancia de Valores
        String queryType = editType.getText().toString();
        String queryBrand = editBrand.getText().toString();

        //Esconde o Teclado
        InputMethodManager keyboardManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (keyboardManager != null) {
            keyboardManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

        //Valida a conexão com a Internet
        ConnectivityManager connectionManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectionManager != null) {
            networkInfo = connectionManager.getActiveNetworkInfo();
        }

        //Validação da Conexão Ativa e dos Campos Preenchidos
        if (networkInfo != null && networkInfo.isConnected()
                && queryType.length() != 0 && queryBrand.length() != 0) {
            Bundle queryBundle = new Bundle();
            queryBundle.putString("product_type", queryType);
            queryBundle.putString("brand", queryBrand);

            //Limpando os campos
            editType.setText(R.string.string_empty);
            editBrand.setText(R.string.string_empty);

            getSupportLoaderManager().restartLoader(0, queryBundle, this);
            onCreateLoader(0, queryBundle);

        }

        //Mostra um aviso para informar que não há conexão/termo de busca
        else {
            if (queryType.length() == 0 || queryBrand.length() == 0) {
                Snackbar errorInputs = Snackbar.make(view, R.string.error_input, 15);
            } else {
                Snackbar errorConnection = Snackbar.make(view, R.string.error_connection, 15);
            }
            editType.setText(R.string.string_empty);
            editType.setText(R.string.string_empty);
        }

    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        String queryType = "";
        String queryBrand = "";

        if (args != null) {
            queryType = args.getString("product_type");
            queryBrand = args.getString("brand");
        }
        return new LoadMakeup(this, queryType, queryBrand);
    }


    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        try {
            //Converte em JSON
            JSONObject jsonObject = new JSONObject(data);

            //Utiliza JSONArray das Makeup
            JSONArray itensArray = new JSONArray("makeups");

            // Cria um contador
            int i = 0;
            int id = 0;
            String name = null;
            String type = null;
            String brand = null;
            String price = null;
            String currency = null;
            String description = null;
            String image = null;
            ArrayList<Makeup> endArrayMakeup = new ArrayList<Makeup>();
            //TODO https://pt.stackoverflow.com/questions/46570/criar-e-manipular-array-associativo-multidimensional


            //Procura pro resultados nos itens do array
            if (i < itensArray.length() && type == null && brand == null) {
                for (i = 0; i <= 4; i++) {
                //TODO Base https://pt.stackoverflow.com/questions/124861/android-ler-dados-json

                    //Obtem as informações do Array itensArray
                    JSONObject makeup = itensArray.getJSONObject(i); //Pega por Numero o JSON
                    JSONObject infosMakeup = makeup.getJSONObject("resposta"); //TODO ALTERAR

                    //Tenta Obter as Informações
                    try {
                        id = Integer.parseInt(infosMakeup.getString("id"));
                        brand = infosMakeup.getString("brand");
                        name = infosMakeup.getString("name");
                        price = infosMakeup.getString("price");
                        currency = infosMakeup.getString("currency");
                        image = infosMakeup.getString("image_link");
                        type = infosMakeup.getString("product_type");
                        description = infosMakeup.getString("description");

                        Makeup make = new Makeup(id, brand, name, type, price, currency, image, description);

                        endArrayMakeup.add(make);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            int z = 0;

            // Para percorrer o ArrayList:
            //Mostra o Resultado

            ArrayList<String> teste = new ArrayList<String>();
            teste.add("teste1");
            teste.add("teste1");
            teste.add("teste1");
            String t = teste.get(0);

            for (Makeup makeupShow : endArrayMakeup) {
               /* name = findViewById(R.id.t);
                currencyPrice = findViewById(R.id.currency_price);
                brandType = findViewById(R.id.type_brand);
                description = findViewById(R.id.description);

                text.setText(makeupShow.getName());
                text.setText(makeupShow.getType());
                text.setText(makeupShow.getName());
                text.setText(makeupShow.getType() + " - " + makeupShow.getBrand());
                text.setText(makeupShow.getCurrency() + " " + makeupShow.getPrice());
                text.setText("Descrição: " + makeupShow.getDescription());
                */
            }


            if (type != null && brand != null) {
                editType.setText(type);
                editBrand.setText(brand);
                //nmLivro.setText(R.string.str_empty);
            } else {
                //Erro se o campo estiver vazio
                editType.setText(R.string.string_empty);
                editBrand.setText(R.string.string_empty);
            }
        } catch (Exception e) {
            // Se não receber um JSOn valido, informa ao usuário
            editType.setText(R.string.string_empty);
            editBrand.setText(R.string.string_empty);
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
        //Metodo Vazio com Implementação Obrigatoria
    }
}



