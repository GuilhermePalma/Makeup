package com.example.maquiagem.view.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.View;

import com.example.maquiagem.R;
import com.example.maquiagem.model.DataBaseMakeup;
import com.example.maquiagem.model.Makeup;
import com.example.maquiagem.model.SearchInternet;
import com.example.maquiagem.view.AlertDialogs;
import com.example.maquiagem.view.RecycleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResultActivity extends AppCompatActivity {

    private final String MAKEUP_URL = "http://makeup-api.herokuapp.com/api/v1/products.json?";
    private final String URL_NO_IMAGE = "https://github.com/GuilhermeCallegari/Maquiagem/blob" +
            "/main/app/src/main/res/drawable/makeup_no_image.jpg";

    private RecycleAdapter recycleAdapter;
    private final List<Makeup> makeupListRecycler = new ArrayList<>();

    private DataBaseMakeup dataBaseHelper;
    private String jsonMakeup = "";
    int numberProducts, maxResult;

    AlertDialogs dialogs = new AlertDialogs();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        dataBaseHelper = new DataBaseMakeup(this);

        // Inicia e Configura o RecyclerView
        setRecyclerView();

        String infoType, infoBrand;

        // Obtem os dados passados pela MainActivity
        Bundle querryBundle = getIntent().getExtras();

        if (querryBundle == null){
            dialogs.message(this, "Sem Dados",getString(R.string.error_recoveryData));
            // Volta para a MainActivity
            super.onBackPressed();
        }

        infoType = Objects.requireNonNull(querryBundle).getString("product_type","");
        infoBrand = Objects.requireNonNull(querryBundle).getString("brand","");

        if (!infoType.equals("") && !infoBrand.equals("")){
            // Caso já exista produtos no Banco de Dados com a Marca e Tipo inserida no DB
            if (dataBaseHelper.existsInMakeup(infoType, infoBrand)) {
                dataBaseHelper.close();
                showWindow(infoType, infoBrand);
            } else{
                // Inicia a Ativividade Assincrona
                asyncTask(infoType, infoBrand);
            }
        } else {
            dialogs.message(this, "Sem Dados",getString(R.string.error_recoveryData));
            // Volta para a MainActivity
            super.onBackPressed();

        }

    }


    public void returnMain(View view){
        // Limpa o Array e o RecyclerView
        makeupListRecycler.clear();
        recycleAdapter.notifyDataSetChanged();
        super.onBackPressed();
    }

    // Configura o RecyclerView
    public void setRecyclerView(){
        // Instancia o RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManagerRecycler = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManagerRecycler);

        // Informa o Context, ListArray utilizado e a Activity que será usada
        recycleAdapter = new RecycleAdapter(this, makeupListRecycler, this);
        recyclerView.setAdapter(recycleAdapter);
    }


    private void asyncTask(String type, String brand) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());


        executorService.execute(() -> {
            jsonMakeup = getMakeup(type, brand);

            if (jsonMakeup.equals("")) {
                handler.post(() -> dialogs.message(ResultActivity.this,
                        getString(R.string.title_noExist), getString(R.string.error_noExists)).show());
                return;
            }

            handler.post(() -> {
                // Serializa e Insere os Dados no Banco de Dados

                if (!insertInDataBase(jsonMakeup)) {
                    dialogs.message(ResultActivity.this,
                            getString(R.string.title_errorJson), getString(R.string.error_json))
                            .show();
                } else {
                    showWindow(type, brand);
                }

            });

        });

    }

    // Cria uma URI e inicializa o Metodo SearchInternet
    private String getMakeup(String type, String brand) {

        String BRAND_PARAMETERS = "brand";
        String TYPE_PARAMETERS = "product_type";
        Uri buildMakeup = Uri.parse(MAKEUP_URL).buildUpon()
                .appendQueryParameter(TYPE_PARAMETERS, type)
                .appendQueryParameter(BRAND_PARAMETERS, brand)
                .build();

        return SearchInternet.searchByUrl(buildMakeup.toString(),"GET");
    }

    // Usa a Serilização e Insere os Dados no DataBase
    private boolean insertInDataBase(String json) {

        JSONArray itemsArray;

        try {
            itemsArray = new JSONArray(json);
            // Recebe o valor do Tamanho da List com os Produtos
            numberProducts = itemsArray.length();

            // Define a Quantidade de Resultados ---> Maximo = 20
            maxResult = numberProducts < 19 ? numberProducts : 20;

            for (int i = 0; i < maxResult; i++) {
                // Pega um objeto de acordo com a Posição (Posição = Item/Produto)
                JSONObject jsonObject = new JSONObject(itemsArray.getString(i));

                Makeup makeup = serializationDataMakeup(jsonObject);

                if (makeup != null) {
                    // Intancia a Classe e Insere no Banco de Dados
                    dataBaseHelper.insertMakeup(makeup);
                } else return false;
            }

            return true;

        } catch (JSONException e) {
            //Caso dê Algum Problema durante a criação do Array
            dialogs.message(ResultActivity.this, getString(R.string.title_errorJson),
                    getString(R.string.error_json)).show();
            Log.e("NOT VALID ARRAY", "Erro no Array ou no Recebimento da String\n" + e);
            e.printStackTrace();
            return false;
        } finally {
            dataBaseHelper.close();
        }
    }

    // Faz o Tratamento dos Dados Recebidoss
    private Makeup serializationDataMakeup(JSONObject jsonObject) {

        String name, type, brand, price, currency, description, urlImage;
        int id;

        try {
            id = Integer.parseInt(jsonObject.getString("id"));
            brand = jsonObject.getString("brand");
            name = jsonObject.getString("name");
            price = jsonObject.getString("price").
                    replaceAll("[^0-9^,.]", "");
            currency = jsonObject.getString("currency");
            type = jsonObject.getString("product_type");
            description = jsonObject.getString("description").
                    replaceAll("\n", "");
            urlImage = jsonObject.getString("image_link");

            // Normalizando as Strings Recebidas (HTML Tags ---> String)
            brand = Html.fromHtml(brand).toString();
            name = Html.fromHtml(name).toString();
            currency = Html.fromHtml(currency).toString();
            type = Html.fromHtml(type).toString();
            description = Html.fromHtml(description).toString();

            //Caso não tenha dados inseridos
            if (name.equals("null") || name.equals("")) {
                name = "Produto sem Nome";
            }
            if (price.equals("null") || price.equals("")) {
                price = "Preço não Encontrado";
            }
            if (currency.equals("null") || currency.equals("")) {
                currency = "";
            }
            if (description.equals("null") || description.equals("")) {
                description = "Produto sem Descrição";
            }
            if (urlImage.equals("null") || urlImage.equals("")) {
                urlImage = URL_NO_IMAGE;
            }

            // Intancia a Classe e Insere no Banco de Dados
            return new Makeup(id, brand, name, type, price, currency, description, urlImage);

        } catch (Exception e) {
            dialogs.message(ResultActivity.this, getString(R.string.title_noExist),
                    getString(R.string.error_noExists)).show();
            Log.e("RECOVERY ARRAY", "Erro ao recuperar os valores do Array " +
                    "dos Produtos\n" + e);
            e.printStackTrace();
            return null;
        }
    }

    // Mostra os Dados na Tela
    public void showWindow(String type_search, String brand_search){

        // Busca os Valores no BD usando os Dados inseridos pelo Usuario e usado no Metodo Async
        Cursor cursor = dataBaseHelper.getDataMakeup(type_search, brand_search);

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