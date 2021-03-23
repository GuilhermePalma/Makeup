package com.example.maquiagem;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    private TextView result;
    private Button search, clear;

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
        search = findViewById(R.id.btn_search);
        clear = findViewById(R.id.btn_clear);
        result =  findViewById(R.id.txt_result);
        //Armazena os valores inseridos p/ usar no select do BD
        setRecyclerView(); //Inicia o RecyclerView

        //TODO Arrumar esses Metodos
        search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadResult(v);
            }
        });

        clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnClear(v);
            }
        });

        //Inicia o Loader assim que a atividade Inicia
        if (getSupportLoaderManager().getLoader(0) != null) {
            getSupportLoaderManager().initLoader(0, null, this);
        }

    }

    String queryType, queryBrand;

    //Metodo do Botão Pesquisar
    public void LoadResult(View view) {
        //Instancia de Valores
        queryType = editType.getText().toString();
        queryBrand = editBrand.getText().toString();

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
            //Insere no budle, o id(como ele se chamara) e em seguida o dado/variavel
            queryBundle.putString("product_type", queryType);
            queryBundle.putString("brand", queryBrand);

            //Limpa os campos
            editType.setText(R.string.string_empty);
            editBrand.setText(R.string.string_empty);

            getSupportLoaderManager().restartLoader(0, queryBundle, this);
        }
        //Mostra um aviso para informar que não há conexão/termo de busca
        else {
            if (queryType.length() == 0 || queryBrand.length() == 0) {
                Snackbar errorInputs = Snackbar.make(view, R.string.error_input, 30000);
                errorInputs.show();

            } else {
                Snackbar errorConnection = Snackbar.make(view, R.string.error_connection, 30000);
                errorConnection.show();
            }

            editBrand.setText(R.string.string_empty);
            editType.setText(R.string.string_empty);
        }
    }


    //Limpa o Banco de Dados
    public void BtnClear(View view){
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        databaseHelper.clearTable();
    }


    //Criação da atividade Assincrona
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


    //Quando acaba a Atividade Assincrona
    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        try {

            //Utiliza JSONArray das Makeup
            JSONArray itemsArray = new JSONArray(data);
            DatabaseHelper databaseHelper = new DatabaseHelper(this);

            int id = 0, i, maxResult = 0;
            String name = null;
            String type = null;
            String brand = null;
            String price = null;
            String currency = null;
            String description = null;
            String image = null;

            //Recebe o valor do tamanho do Array
            int numberArray = itemsArray.length();
            if(numberArray == 0){
                //Array Vazio
                Snackbar dataEmpty = Snackbar.make(
                        findViewById(R.id.viewIndex),
                        R.string.no_exists,
                        30000);
                dataEmpty.show();
                return;
            }
            else if(numberArray < 4){
                //Caso retorne menos que 5 Itens
                maxResult = numberArray;
            }
            else{
                //Limite de no Maximo 5 Resultados por Marca/Tipo
                maxResult = 6;
            }

            //Busca os resultados nos itens do array
            for (i = 0; i < maxResult; i++) {
                //Pega um objeto de acordo com a posição
                //Cada posição é um item (Cada Posição = 1 Produto)
                JSONObject jsonObject = new JSONObject(itemsArray.getString(i));

                //Tenta Obter as Informações
                try {
                    id = Integer.parseInt(jsonObject.getString("id"));
                    brand = jsonObject.getString("brand");
                    name = jsonObject.getString("name");
                    price = jsonObject.getString("price");
                    currency = jsonObject.getString("currency");
                    image = jsonObject.getString("image_link");
                    type = jsonObject.getString("product_type");
                    description = jsonObject.getString("description");

                    if(currency == null){
                        currency = " ";
                    }

                    Makeup make = new Makeup(id, brand, name, type, price, currency, image, description);
                    //Insere os dados da Classe Makeup no SQLite
                    databaseHelper.insertMakeup(make);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //Metodo que Exibe os dados do SQLite (Mostra no RecycleView)
            showWindow();

        } catch (Exception e) {
            //Caso não receba um JSON Valido
            editType.setText(R.string.string_empty);
            editBrand.setText(R.string.string_empty);

            Snackbar errorInputs = Snackbar.make(
                    findViewById(R.id.viewIndex),
                    R.string.error_json,
                    30000);
            errorInputs.show();

            e.printStackTrace();
        }
    }


    //Configurações do RecyclerView
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
        Cursor cursor = dataBaseHelper.getData(queryType,queryBrand);

        //Caso haja posição para o Cursor
        if(cursor.moveToFirst()){
            String brand, name, price, currency, image, type, description;

            //Pega os dados enquanto o Cursor tiver proxima posição
            do{
                brand = cursor.getString(1);
                name = cursor.getString(2);
                type = cursor.getString(3);
                price = cursor.getString(4);
                currency = cursor.getString(5);
                image = cursor.getString(6);
                description = cursor.getString(7);

                Makeup makeup = new Makeup(brand, name, type, price, currency, image, description);
                makesList.add(makeup);
                //Atualiza o RecyclerView
                adapter.notifyDataSetChanged();
            } while (cursor.moveToNext());

        }
        else{
            //Array está vazio
            Snackbar dataEmpty = Snackbar.make(
                    findViewById(R.id.viewIndex),
                    R.string.table_empty,
                    30000);
            dataEmpty.show();
        }
        cursor.close();
        dataBaseHelper.close();
    }


    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
        //Metodo Vazio com Implementação Obrigatoria
    }
}



