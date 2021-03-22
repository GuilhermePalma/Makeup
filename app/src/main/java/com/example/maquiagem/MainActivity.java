package com.example.maquiagem;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maquiagem.Model.Makeup;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private EditText editType;
    private EditText editBrand;
    private String type, brand;
    private TextView name;
    private TextView currencyPrice;
    private TextView brandType;
    private TextView description;
    private TextView result;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recLayoutManager;
    RecycleAdapter adapter;
    private List<Makeup> makesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editType = findViewById(R.id.edit_type);
        editBrand = findViewById(R.id.edit_brand);
        type = editType.toString();
        brand = editBrand.toString();
        setRecyclerView(); //Inicia o RecyclerView

        //Inicia o Loader assim que a atividade Inicia
        if (getSupportLoaderManager().getLoader(0) != null) {
            getSupportLoaderManager().initLoader(0, null, this);
        }

    }

    //Metodo do Botão Pesquisar
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
                result.setText(R.string.error_input);
            } else {
                Snackbar errorConnection = Snackbar.make(view, R.string.error_connection, 15);
                result.setText(R.string.error_connection);
            }

            editType.setText(R.string.string_empty);
            editType.setText(R.string.string_empty);
            result.setText("");
        }

    }


    public void BtnClear(View view){
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        databaseHelper.clearTable();
    }


    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        String queryType = "";
        String queryBrand = "";

        //Pega os Valores atraves da Keys(Chave do Bundle)
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

            int id = 0, i = 0;
            String name = null;
            String type = null;
            String brand = null;
            String price = null;
            String currency = null;
            String description = null;
            String image = null;
            DatabaseHelper databaseHelper = new DatabaseHelper(this);

            //Procura pro resultados nos itens do array
            //Limitando para 5 Resultados
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
                    databaseHelper.insertMakeup(make);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            showWindow();

        } catch (Exception e) {
            // Se não receber um JSON valido, informa ao usuário
            editType.setText(R.string.string_empty);
            editBrand.setText(R.string.string_empty);
            Snackbar errorInputs = Snackbar.make(findViewById(R.id.viewIndex), R.string.error_json,15);
            e.printStackTrace();
        }
    }


    public void setRecyclerView(){
        //Instancia o RecyclerView
        recyclerView =  findViewById(R.id.recyclerView);
        recLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recLayoutManager);

        adapter = new RecycleAdapter(this,makesList, this);
        recyclerView.setAdapter(adapter);
    }


    public void showWindow(){
        DatabaseHelper dataBaseHelper = new DatabaseHelper(MainActivity.this);
        Cursor cursor = dataBaseHelper.getData(type,brand);

        if(cursor.moveToFirst()){
            String brand, name, price, currency, image, type, description;
            do{
                brand = cursor.getString(1);
                name = cursor.getString(2);
                price = cursor.getString(3);
                currency = cursor.getString(4);
                image = cursor.getString(5);
                type = cursor.getString(6);
                description = cursor.getString(7);

                Makeup makeup = new Makeup(brand, name, type, price, currency, image, description);
                makesList.add(makeup);

                adapter.notifyDataSetChanged();

            } while (cursor.moveToNext());
        }
        else{
            System.out.println("Tabela Vazia");
        }
        cursor.close();
        dataBaseHelper.close();

    }


    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
        //Metodo Vazio com Implementação Obrigatoria
    }
}



