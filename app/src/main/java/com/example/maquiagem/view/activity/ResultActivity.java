package com.example.maquiagem.view.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.maquiagem.R;
import com.example.maquiagem.model.AsyncMakeup;
import com.example.maquiagem.model.DataBaseMakeup;
import com.example.maquiagem.model.Makeup;
import com.example.maquiagem.view.AlertDialogs;
import com.example.maquiagem.view.RecycleAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {


    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManagerRecycler;
    private RecycleAdapter recycleAdapter;
    private List<Makeup> makeupListRecycler = new ArrayList<>();

    private final DataBaseMakeup dataBaseHelper = new DataBaseMakeup(this);
    private String infoType, infoBrand;

    AlertDialogs dialogs = new AlertDialogs();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Inicia e Configura o RecyclerView
        setRecyclerView();

        // Obtem os dados passados pela MainActivity
        Bundle querryBundle = getIntent().getExtras();

        if (querryBundle != null) {

            infoType = querryBundle.getString("product_type");
            infoBrand = querryBundle.getString("brand");

            // Caso já exista produtos no Banco de Dados com a Marca e Tipo inserida no DB
            if (dataBaseHelper.existsInMakeup(infoType, infoBrand)) {
                dataBaseHelper.close();
                showWindow();
            } else{
                //Reinicia e Inicia a Atividade Assincrona
                getSupportLoaderManager().restartLoader(0, querryBundle, this);
            }
        } else{
            dialogs.message(this, "Sem Dados",
                    getString(R.string.error_recoveryData));
            // Volta para a MainActivity
            super.onBackPressed();
        }

    }

    public void returnMain(View view){
        // Limpa o Array
        makeupListRecycler.clear();
        // Reinicia o RecyclerView sem nenhuma informação no Listarray
        recyclerView.setAdapter(recycleAdapter);
        super.onBackPressed();
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
                dialogs.message(ResultActivity.this,"Produto/Marca não encontrado",
                        getString(R.string.no_exists)).show();
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
            dialogs.message(ResultActivity.this,"Erro na leitura",
                    getString(R.string.error_json)).show();

            Log.e("NOT VALID ARRAY",
                    "\nErro no Array ou no Recebimento da String\n" + e);

            e.printStackTrace();
        }
    }


    // Mostra os Dados na Tela
    public void showWindow(){

        // Busca os Valores no BD
        // Utiliza os valores inseridos pelo usuario e usados pela atividade assicrona
        Cursor cursor = dataBaseHelper.getDataMakeup(infoType, infoBrand);

        // Caso haja posição para o Cursor
        if(cursor.moveToFirst()){
            String brand, name, price, currency, type, description, urlImage;


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
            dialogs.message(ResultActivity.this,"Sem Dados",
                    getString(R.string.table_empty)).show();

        }
        cursor.close();
        dataBaseHelper.close();
    }

}