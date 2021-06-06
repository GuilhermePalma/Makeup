package com.example.maquiagem;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
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

    private final DatabaseHelper dataBaseHelper = new DatabaseHelper(this);
    private String infoType, infoBrand;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Criação da ToolBar
        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        editType = findViewById(R.id.edit_type);
        editBrand = findViewById(R.id.edit_brand);
        result =  findViewById(R.id.txt_result);

        // Inicia o RecyclerView
        setRecyclerView();

        //Inicia o Loader assim que a atividade Inicia
        if (getSupportLoaderManager().getLoader(0) != null) {
            getSupportLoaderManager().initLoader(0, null, this);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.location):
                System.out.println("Localização");
                break;
            case (R.id.clearWindow):

            case (R.id.clearDb):
                clearDataBase();
                break;
            case (R.id.alter_theme):
                if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                break;
            default:
                return  false;
        }
        return super.onOptionsItemSelected(item);
    }

    // Configurações do RecyclerView
    public void setRecyclerView(){
        // Instancia o RecyclerView
        recyclerView =  findViewById(R.id.recyclerView);
        recLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recLayoutManager);

        // Informa o Context, Listarray utilizado e a Activity que será usada
        adapter = new RecycleAdapter(this, makesList, this);
        recyclerView.setAdapter(adapter);
    }


    public void clearWindow(){
        //Limpa o Array MakeList(P/ reiniciar o RecycleView) e apaga o Texto de Resultado
        editType.setText(R.string.string_empty);
        editBrand.setText(R.string.string_empty);

        //TODO testar
        result.setVisibility(View.VISIBLE);
        result.setText(R.string.string_empty);

        // Limpa o Array
        makesList.clear();
        // Reinicia o RecyclerView sem nenhuma informação no Listarray
        recyclerView.setAdapter(adapter);

    }

    //Apaga todos os registros do Banco de Dados
    public void clearDataBase(){
        clearWindow();

        // Apaga os Registros do Banco de Dados
        dataBaseHelper.clearTable();
    }

    //Metodo do Botão Pesquisar
    public void LoadResult(View view) {
        //Limpa o Array e Reinicia o Adapter
        makesList.clear();
        recyclerView.setAdapter(adapter);

        result.setVisibility(View.GONE);
        // TODO testar - result.setText(R.string.string_empty);

        //Instancia de Valores
        // TODO criar validação para esses campos serem obrigatorios
        infoType = editType.getText().toString();
        infoBrand = editBrand.getText().toString();


        //Esconde o Teclado
        InputMethodManager keyboardManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        // Caso o teclado esteja ativo
        if (keyboardManager != null) {
            keyboardManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }


        //Valida se há conexão com a Internet
        ConnectivityManager connectionManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectionManager != null) {
            networkInfo = connectionManager.getActiveNetworkInfo();
        }


        //Validação da Conexão Ativa e dos Campos Preenchidos
        if (networkInfo != null
                && networkInfo.isConnected()
                && infoType.length() != 0
                && infoBrand.length() != 0) {

            // Insere no bundle, o id(como sera chamado) e o dado/variavel
            Bundle queryBundle = new Bundle();
            queryBundle.putString("product_type", infoType);
            queryBundle.putString("brand", infoBrand);

            // Limpa os valores da Tela Inteira
            clearWindow();

            //Reinicia e Inicia a Atividade Assincrona
            getSupportLoaderManager().restartLoader(0, queryBundle, this);
        }
        //Mostra um aviso de que não há Termos de Buscas ou Conexão
        else {
            if (infoType.length() == 0 || infoBrand.length() == 0) {
                Snackbar errorInputs = Snackbar.make(view, R.string.error_input, 15000);
                errorInputs.show();

            } else {
                Snackbar errorConnection = Snackbar.make(view, R.string.error_connection, 15000);
                errorConnection.show();
            }
        }
    }



    //Criação da Atividade Assincrona
    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {

        String queryType, queryBrand;

        // Caso exista, recupera os Valores atraves da Keys(Chave do Bundle)
        if (args != null) {
            queryType = args.getString("product_type");
            queryBrand = args.getString("brand");
        }
        else{
            Snackbar errorInputs = Snackbar.make(findViewById(R.id.viewIndex),
                    R.string.error_input, 15000);
            errorInputs.show();
            return null;
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

            int id, maxResult;
            String name, type , brand, price, currency, description;

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

            // Busca os resultados nos itens do array
            for (int i = 0; i < maxResult; i++) {
                // Pega um objeto de acordo com a posição
                // Cada posição é um Item/Produto
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
                    if (description.equals("null")){
                        description = "Esse produto não possui Descrição Cadastrada.";
                    }
                    if (price.equals("null")){
                        price = "Esse produto não possui Preço Cadastrado.";
                    }
                    if (name.equals("null")){
                        name = "Erro ao procurar o nome do Produto.";
                    }

                    //Instancia a Classe com os Dados
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

            Snackbar errorInputs = Snackbar.make(
                    findViewById(R.id.viewIndex),
                    R.string.error_json,
                    15000);
            errorInputs.show();

            e.printStackTrace();
        }
    }


    // Mostra os Dados na Tela
    public void showWindow(){
        // Recebe os Valores do BD
        // Valores dos dados inseridos pelo usuario
        Cursor cursor = dataBaseHelper.getData(infoType, infoBrand);

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

                // Atualiza o RecyclerView
                makesList.add(makeup);
                // Notifica ao adpter que houve mudança
                adapter.notifyDataSetChanged();
                // Mostra o Titulo na Tela
                result.setVisibility(View.VISIBLE);
                //TODO Testar result.setText(R.string.title_result);
            } while (cursor.moveToNext());

        }
        else{
            // Não possui dados na Tabela
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