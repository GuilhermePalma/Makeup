package com.example.maquiagem;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maquiagem.model.DataBaseMakeup;
import com.example.maquiagem.model.AsyncMakeup;
import com.example.maquiagem.model.Makeup;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    private EditText editType;
    private EditText editBrand;
    private LinearLayout layoutInputs;
    private LinearLayout layoutResult;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManagerRecycler;
    private RecycleAdapter recycleAdapter;

    private List<Makeup> makeupListRecycler = new ArrayList<>();

    private final DataBaseMakeup dataBaseHelper = new DataBaseMakeup(this);
    private String infoType, infoBrand;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Criação da ToolBar
        Toolbar toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        editType = findViewById(R.id.edit_type);
        editBrand = findViewById(R.id.edit_brand);
        layoutInputs = findViewById(R.id.layoutInputs);
        layoutResult = findViewById(R.id.layoutResult);

        // Inicia e Configura o RecyclerView
        setRecyclerView();

        //Inicia o Loader junto com a Activity
        if (getSupportLoaderManager().getLoader(0) != null) {
            getSupportLoaderManager().initLoader(0, null, this);
        }
    }

    // Configura o RecyclerView
    public void setRecyclerView(){
        // Instancia o RecyclerView
        recyclerView =  findViewById(R.id.recyclerView);
        layoutManagerRecycler = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManagerRecycler);

        // Informa o Context, ListArray utilizado e a Activity que será usada
        recycleAdapter = new RecycleAdapter(this, makeupListRecycler, this);
        recyclerView.setAdapter(recycleAdapter);
    }


    // Cria o menu na ToolBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }


    // Trata os cliques no Menu da TollBar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.location):
                Intent location = new Intent(this, LocationActivity.class);
                startActivity(location);
                break;
            case (R.id.clearDb):
                // Limpa o Banco de Dados
                dataBaseHelper.clearTable();
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


    public void returnInputs(View view){
        // Limpa a Tela e o Banco de Dados
        clearWindow();
        dataBaseHelper.clearTable();

        layoutResult.setVisibility(View.GONE);
        layoutInputs.setVisibility(View.VISIBLE);
    }

    public void clearWindow(){
        // Limpa o Array MakeList(P/ reiniciar o RecycleView) e apaga o Texto de Resultado
        editType.setText(R.string.string_empty);
        editBrand.setText(R.string.string_empty);

        layoutResult.setVisibility(View.GONE);

        // Limpa o Array
        makeupListRecycler.clear();
        // Reinicia o RecyclerView sem nenhuma informação no Listarray
        recyclerView.setAdapter(recycleAdapter);

    }


    // Metodo do Botão Pesquisar
    public void LoadResult(View view) {

        // Recupera os valores inseridos pelo Usario
        infoType = editType.getText().toString();
        infoBrand = editBrand.getText().toString();

        if (infoType.equals("")){
            editType.setError("Campo Obrigatorio !");
            return;
        }  else if(infoBrand.equals("")){
            editBrand.setError("Campo Obrigatorio !");
            return;
        }

        //Esconde o Teclado
        InputMethodManager keyboardManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        // Ao clicar no botão, caso o teclado esteja ativo ele é fechado
        if (keyboardManager != null) {
            keyboardManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

        //Valida se possui conexão com a Internet
        ConnectivityManager connectionManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;

        Snackbar errorConnection = Snackbar.
                make(view, R.string.error_connection, Snackbar.LENGTH_LONG);

        if (connectionManager != null) {
            networkInfo = connectionManager.getActiveNetworkInfo();
        } else {
            //Não há conexão com a Internet
            errorConnection.show();
            return;
        }

        //Validação da Conexão Ativa
        if (networkInfo != null && networkInfo.isConnected()) {

            // Insere no bundle, o id(como sera chamado) e o dado/variavel
            Bundle queryBundle = new Bundle();
            queryBundle.putString("product_type", infoType);
            queryBundle.putString("brand", infoBrand);

            // Limpa os valores da Tela Inteira
            clearWindow();

            // Limpa o Banco de Dados
            dataBaseHelper.clearTable();

            //Reinicia e Inicia a Atividade Assincrona
            getSupportLoaderManager().restartLoader(0, queryBundle, this);
        } else{
            // Erro na Conexão
            Log.e("NO CONECTED", "\n Erro na conexão com a Internet" +
                    "\nConexão: " + networkInfo);
            errorConnection.show();
        }
    }



    //Criação da Atividade Assincrona
    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {

        String queryType, queryBrand;

        // Caso exista, atribui os Valores atraves da Keys (Chave do Bundle)
        if (args != null) {
            queryType = args.getString("product_type");
            queryBrand = args.getString("brand");

            return new AsyncMakeup(getApplicationContext(), queryType, queryBrand);
        }
        else{
            // Caso não apresente dados no Bundle ---> Sem dados inseridos pelo Usuario
            return null;
        }
    }


    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {
        //Metodo Vazio com Implementação Obrigatoria
    }


    // Quando Finaliza a Atividade Assincrona
    // Recebe uma String do metodo chamado no loadInBackground (SearchMakeupApi.searchMakeup())
    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {
        try {
            // Utiliza JSONArray das Makeup
            JSONArray itemsArray = new JSONArray(data);

            int numberProducts, maxResult, id;
            String name, type , brand, price, currency, description, urlImage;

            //Recebe o valor do Tamanho da List com os Produtos
            numberProducts = itemsArray.length();
            if(numberProducts == 0){
                //Array Vazio
                Snackbar dataEmpty = Snackbar.make(
                        findViewById(R.id.viewIndex),
                        R.string.no_exists,
                        Snackbar.LENGTH_LONG);
                dataEmpty.show();
                return;
            }
            else if(numberProducts < 19){
                //Caso retorne menos que 5 Itens
                maxResult = numberProducts;
            }
            else{
                //Limite Maximo de 5 Resultados por Marca/Tipo
                maxResult = 20;
            }

            // Laço de Repetição que consulta os resultados nos itens do Array(JSON)
            for (int i = 0; i < maxResult; i++) {

                // Pega um objeto de acordo com a Posição (Posição = Item/Produto)
                JSONObject jsonObject = new JSONObject(itemsArray.getString(i));

                //Tenta Obter as Informações e Converte-las para seus devidos tipos
                try {
                    id = Integer.parseInt(jsonObject.getString("id"));
                    brand = jsonObject.getString("brand");
                    name = jsonObject.getString("name");
                    price = jsonObject.getString("price");
                    currency = jsonObject.getString("currency");
                    type = jsonObject.getString("product_type");
                    description = jsonObject.getString("description").
                            replaceAll("\n", "");
                    urlImage = jsonObject.getString("image_link");

                    //Caso não tenha dados inseridos

                    if (name.equals("null") || name.equals("")){
                        name = "Erro ao procurar o nome do Produto.";
                    }
                    if(currency.equals("null") || currency.equals("")){
                        currency = "";
                    }
                    if (description.equals("null") || description.equals("")){
                        description = "Esse Produto não possui Descrição Cadastrada.";
                    }
                    if (price.equals("null") || price.equals("")){
                        price = "Não possui Preço Cadastrado";
                    }

                    // Instancia a Classe com os Dados
                    Makeup classMakeup = new Makeup(id, brand, name, type, price,
                            currency, description, urlImage);
                    // Insere os dados da Classe Makeup no SQLite
                    dataBaseHelper.insertMakeup(classMakeup);

                } catch (Exception e) {
                    Log.e("RECOVERY ARRAY", "\nErro ao recuperar os valores do Array" +
                            "dos Produtos\n" + e);
                    e.printStackTrace();
                } finally {
                    dataBaseHelper.close();
                }
            }

            // Metodo que Exibe os dados do SQLite (Mostra no RecycleView)
            showWindow();

        } catch (Exception e) {
            //Caso não receba uma String Valida ou tenha algum problema na criação Array
            Snackbar errorInputs = Snackbar.make(
                    findViewById(R.id.viewIndex),
                    R.string.error_json,
                    Snackbar.LENGTH_LONG);
            errorInputs.show();

            Log.e("NOT VALID ARRAY",
                    "\nErro no Array ou no Recebimento da String\n" + e);

            e.printStackTrace();
        }
    }


    // Mostra os Dados na Tela
    public void showWindow(){
        // Busca os Valores no BD
        // Utiliza os valores inseridos pelo usuario e usados pela atividade assicrona
        Cursor cursor = dataBaseHelper.getData(infoType, infoBrand);

        // Caso haja posição para o Cursor
        if(cursor.moveToFirst()){
            String brand, name, price, currency, type, description, urlImage;

            // Esconde o Layout de Pesquisa e mostra o Layout de Resultados
            layoutInputs.setVisibility(View.GONE);
            layoutResult.setVisibility(View.VISIBLE);



            // Pega os dados enquanto o Cursor tiver proxima posição
            do{
                brand = cursor.getString(1);
                name = cursor.getString(2);
                type = cursor.getString(3);
                price = cursor.getString(4);
                currency = cursor.getString(5);
                description = cursor.getString(6);
                urlImage = cursor.getString(7);

                Makeup makeup = new Makeup(brand, name, type, price, currency, description,urlImage);

                // Atualiza o RecyclerView
                makeupListRecycler.add(makeup);

                // Notifica ao adpter que houve mudança
                recycleAdapter.notifyDataSetChanged();
            } while (cursor.moveToNext());

        } else{

            // Não possui dados na Tabela
            Log.e("EMPTY DATABASE", "\nNão foi encontrado nenhum " +
                    "dado no Banco de Dados\n" + cursor.toString());

            Snackbar dataEmpty = Snackbar.make(
                    findViewById(R.id.viewIndex),
                    R.string.table_empty,
                    Snackbar.LENGTH_LONG);
            dataEmpty.show();

        }
        cursor.close();
        dataBaseHelper.close();
    }

}