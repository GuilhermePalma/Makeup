package com.example.maquiagem;

import android.content.Context;
import android.database.Cursor;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private EditText editType;
    private EditText editBrand;
    private TextView result;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recLayoutManager;
    RecycleAdapter adapter;

    private List<MakeupClass> makesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editType = findViewById(R.id.edit_type);
        editBrand = findViewById(R.id.edit_brand);
        result =  findViewById(R.id.txt_result);
        //Inicia o RecyclerView
        setRecyclerView();

        //Inicia o Loader assim que a atividade Inicia
        if (getSupportLoaderManager().getLoader(0) != null) {
            getSupportLoaderManager().initLoader(0, null, this);
        }

    }


    String queryType, queryBrand;
    DatabaseHelper dataBaseHelper = new DatabaseHelper(this);

    //Configurações do RecyclerView
    public void setRecyclerView(){
        //Instancia o RecyclerView
        recyclerView =  findViewById(R.id.recyclerView);
        recLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recLayoutManager);

        adapter = new RecycleAdapter(this,makesList, this);
        recyclerView.setAdapter(adapter);
    }


    //Apaga todos os registros do Banco de Dados
    public void BtnClear(View view){
        //Limpa o Array MakeList(P/ reiniciar o RecycleView) e apaga o Texto de Resultado
        editType.setText(R.string.string_empty);
        editBrand.setText(R.string.string_empty);
        result.setText(R.string.string_empty);

        makesList.clear();
        recyclerView.setAdapter(adapter);


        dataBaseHelper.clearTable();
    }


    //Metodo do Botão Pesquisar
    public void LoadResult(View view) {
        //Limpa o Array e Limpa o Texto de Resultado
        makesList.clear();
        recyclerView.setAdapter(adapter);

        result.setText(R.string.string_empty);

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
            //Insere no budle, o id(como sera chamado) em seguida, o dado/variavel
            queryBundle.putString("product_type", queryType);
            queryBundle.putString("brand", queryBrand);

            //Limpa os campos que tinham os valores
            editType.setText(R.string.string_empty);
            editBrand.setText(R.string.string_empty);

            //Reinicia e Inicia a Atividade Assincrona
            getSupportLoaderManager().restartLoader(0, queryBundle, this);
        }
        //Mostra um aviso de que não há Conexão ou Termos de Buscas
        else {
            if (queryType.length() == 0 || queryBrand.length() == 0) {
                Snackbar errorInputs = Snackbar.make(view, R.string.error_input, 15000);
                errorInputs.show();

            } else {
                Snackbar errorConnection = Snackbar.make(view, R.string.error_connection, 15000);
                errorConnection.show();
            }
            editBrand.setText(R.string.string_empty);
            editType.setText(R.string.string_empty);
        }
    }



    //Criação da Atividade Assincrona
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

        //Incia/Instancia a Atividade Assincrona
        return new LoadMakeup(this, queryType, queryBrand);
    }


    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
        //Metodo Vazio com Implementação Obrigatoria
    }


    //Quando Finaliza a Atividade Assincrona
    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        try {
            //Utiliza JSONArray das Makeup
            JSONArray itemsArray = new JSONArray(data);

            int id = 0, maxResult = 0, i;
            String name = null;
            String type = null;
            String brand = null;
            String price = null;
            String currency = null;
            String description = null;

            //Recebe o valor do tamanho do Array
            int numberArray = itemsArray.length();
            if(numberArray == 0){
                //Array Vazio
                Snackbar dataEmpty = Snackbar.make(
                        findViewById(R.id.viewIndex),
                        R.string.no_exists,
                        15000);
                dataEmpty.show();
                return;
            }
            else if(numberArray < 4){
                //Caso retorne menos que 5 Itens
                maxResult = numberArray;
            }
            else{
                //Limite Maximo de 5 Resultados por Marca/Tipo
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
                    type = jsonObject.getString("product_type");
                    description = jsonObject.getString("description").replaceAll("\n", "");

                    //Caso não tenha dados inseridos
                    if(currency.equals("null")){
                        currency = "";
                    }
                    else if (description.equals("null")){
                        description = "Esse produto não possui Descrição Cadastrada.";
                    }
                    else if (price.equals("null")){
                        price = "Esse produto não possui Preço Cadastrado.";
                    }
                    else if (name.equals("null")){
                        name = "Erro ao procurar o nome do Produto.";
                    }

                    MakeupClass make = new MakeupClass(id, brand, name, type, price, currency, description);
                    //Insere os dados da Classe Makeup no SQLite
                    dataBaseHelper.insertMakeup(make);

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
                    15000);
            errorInputs.show();

            e.printStackTrace();
        }
    }


    public void showWindow(){
        Cursor cursor = dataBaseHelper.getData(queryType,queryBrand);

        //Caso haja posição para o Cursor
        if(cursor.moveToFirst()){
            String brand, name, price, currency, type, description;

            //Pega os dados enquanto o Cursor tiver proxima posição
            do{
                brand = cursor.getString(1);
                name = cursor.getString(2);
                type = cursor.getString(3);
                price = cursor.getString(4);
                currency = cursor.getString(5);
                description = cursor.getString(6);

                MakeupClass makeup = new MakeupClass(brand, name, type, price, currency, description);

                //Atualiza o RecyclerView
                makesList.add(makeup);
                adapter.notifyDataSetChanged();

                result.setText(R.string.title_result);
            } while (cursor.moveToNext());

        }
        else{
            //Array está vazio
            Snackbar dataEmpty = Snackbar.make(
                    findViewById(R.id.viewIndex),
                    R.string.table_empty,
                    15000);
            dataEmpty.show();
        }
        cursor.close();
        dataBaseHelper.close();
    }

}