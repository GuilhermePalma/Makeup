package com.example.maquiagem.model.entity;

import static com.example.maquiagem.model.SearchInternet.PARAM_BRAND;
import static com.example.maquiagem.model.SearchInternet.PARAM_RATING_GREATER;
import static com.example.maquiagem.model.SearchInternet.URL_MAKEUP;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.maquiagem.controller.DataBaseHelper;
import com.example.maquiagem.model.SearchInternet;
import com.example.maquiagem.model.SerializationData;
import com.example.maquiagem.view.activities.MainActivity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class Makeup {

    private int id;
    private String brand = "";
    private String name = "";
    private String category = "";
    private String price = "";
    private String currency = "";
    private String description = "";
    private String type = "";
    private String urlImage = "";
    private boolean isFavorite = false;
    private Context context;

    public Makeup() {
    }

    public Makeup(Context context) {
        this.context = context;
    }

    //Contrutor usando ID
    public Makeup(int id, String brand, String name, String type, String price, String currency,
                  String description, String urlImage) {
        this.id = id;
        this.brand = brand;
        this.name = name;
        this.type = type;
        this.price = price;
        this.currency = currency;
        this.description = description;
        this.urlImage = urlImage;
    }

    public List<Makeup> getMakeups(ExecutorService executorService, Uri uri_search, int quantity_items) {
        // Todo: Remover, temporario enquanto não se tem a API Interna
        if(uri_search.toString().equals("In Develop 1")){
            String select_favorite = String.format("SELECT * FROM %1$s WHERE %2$s=1",
                    DataBaseHelper.TABLE_MAKEUP, DataBaseHelper.IS_FAVORITE_MAKEUP);
            return new SerializationData(context).serializationSelectMakeup(select_favorite);
        }

        // Configura a Execução da Tarefa Assincrona
        Set<Callable<String>> callableTaskAPI = new HashSet<>();
        callableTaskAPI.add(() -> SearchInternet.searchByUrl(context, uri_search.toString(), "GET"));

        try {
            // Obtem o Resultado da Busca Assincrona
            List<Future<String>> futureTasksList = executorService.invokeAll(callableTaskAPI);
            String json = futureTasksList.get(0).get();

            if (json == null || json.equals("")) return null;

            // Retorna o JSON Serializado ou null
            return new SerializationData(context).serializationJsonMakeup(json, quantity_items);
        } catch (Exception ex) {
            Log.e("Error", "Erro ao manipular o JSON ou na sua Serialização. " + ex);
            ex.printStackTrace();
            return null;
        }
    }

    public Uri getUri(int type_search) {
        switch (type_search) {
            case MainActivity.OPTION_HOME_MAKEUP:
                return Uri.parse(URL_MAKEUP).buildUpon()
                        .appendQueryParameter(PARAM_RATING_GREATER, "4.8").build();

            case MainActivity.OPTION_MY_FAVORITE_MAKEUPS:
                // todo: Implementar API_Interna
                // Envia uma solicitação à Makeup_API & Obtem o JSON & Atualiza o Banco de Dados
                return Uri.parse("In Develop 1");

            case MainActivity.OPTION_MORE_FAVORITES:
                // todo: Implementar API_Interna
                // Envia uma solicitação à Makeup_API & Obtem o JSON
                return Uri.parse(URL_MAKEUP).buildUpon()
                        .appendQueryParameter(PARAM_BRAND, "l'oreal").build();

            default:
                return null;
        }
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

}
