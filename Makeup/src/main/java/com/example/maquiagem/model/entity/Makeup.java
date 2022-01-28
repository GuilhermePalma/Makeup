package com.example.maquiagem.model.entity;

import static com.example.maquiagem.model.SearchInternet.METHOD_GET;
import static com.example.maquiagem.model.SearchInternet.PARAM_BRAND;
import static com.example.maquiagem.model.SearchInternet.PARAM_RATING_GREATER;
import static com.example.maquiagem.model.SearchInternet.URL_MAKEUP;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.maquiagem.controller.ManagerDatabase;
import com.example.maquiagem.model.SearchInternet;
import com.example.maquiagem.model.SerializationData;
import com.example.maquiagem.view.activities.MainActivity;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Classe das Maquiagens
 */
public class Makeup {

    // Atributos da Classe
    private int id = 0;
    private String brand = "";
    private String name = "";
    private String category = "";
    private double price = -1;
    private String charPrice = "";
    private String currency = "";
    private String description = "";
    private String type = "";
    private String originalUrlImage = "";
    private float ratingProduct = -1;
    private String apiUrlImage = "";
    private boolean isFavorite = false;
    private Context context;
    private String[] tags = null;
    private Map<String, Integer> colors = null;
    private String urlInAPI;

    /**
     * Construtor Vazio da Classe {@link Makeup}
     */
    public Makeup() {
    }

    /**
     * Construtor da Classe {@link Makeup}
     *
     * @param context {@link Context} necessario para obter recursos do APP
     * @see #getMakeups(ExecutorService, Uri, int)
     */
    public Makeup(Context context) {
        this.context = context;
    }

    /**
     * Construtor passando todos os Dados de uma {@link Makeup }
     *
     * @param id               ID da {@link Makeup}
     * @param type             Tipo da {@link Makeup}
     * @param price            Preço da {@link Makeup}
     * @param brand            Marcada {@link Makeup}
     * @param currency         Moeda da {@link Makeup}
     * @param description      Descrição da {@link Makeup}
     * @param name             Nome da {@link Makeup}
     * @param originalUrlImage URL da {@link Makeup}
     */
    public Makeup(int id, String brand, String name, String type, double price, String currency,
                  String description, String originalUrlImage) {
        this.id = id;
        this.brand = brand;
        this.name = name;
        this.type = type;
        this.price = price;
        this.currency = currency;
        this.description = description;
        this.originalUrlImage = originalUrlImage;
    }

    public static String[] getParametersJSON() {
        return new String[]{"id", "brand", "name", "price", "price_sign", "currency", "image_link",
                "api_featured_image", "description", "rating", "category", "product_type", "tag_list",
                "product_api_url", "product_colors"};
    }

    public Map<String, Integer> getColors() {
        return colors;
    }

    public void setColors(Map<String, Integer> colors) {
        this.colors = colors;
    }

    public String getUrlInAPI() {
        return urlInAPI;
    }

    public void setUrlInAPI(String urlInAPI) {
        this.urlInAPI = urlInAPI;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCharPrice() {
        return charPrice;
    }

    public void setCharPrice(String charPrice) {
        this.charPrice = charPrice;
    }

    public float getRatingProduct() {
        return ratingProduct;
    }

    public void setRatingProduct(float ratingProduct) {
        this.ratingProduct = ratingProduct;
    }

    public String getApiUrlImage() {
        return apiUrlImage;
    }

    public void setApiUrlImage(String apiUrlImage) {
        this.apiUrlImage = apiUrlImage;
    }

    /**
     * A partir de uma {@link Uri} realiza uma busca assincrona na {@link SearchInternet#URL_MAKEUP API Makeup}
     * para obter uma {@link List} de {@link Makeup}
     *
     * @param executorService {@link ExecutorService} que executa a Atitividade Assincrona (Necessario
     *                        para não quebrar a UI do APP)
     * @param uri_search      {@link Uri} em que a {@link Makeup} será Obtida
     * @param quantity_items  Quantidade de Itens que será Serializado
     * @return {@link List}|null
     * @see SerializationData#ALL_ITEMS_JSON
     */
    public List<Makeup> getMakeups(ExecutorService executorService, Uri uri_search, int quantity_items) {
        // Todo: Remover, temporario enquanto não se tem a API Interna
        if (uri_search.toString().equals("In Develop 1")) {
            String select_favorite = String.format("SELECT * FROM %1$s WHERE %2$s=1",
                    ManagerDatabase.TABLE_MAKEUP, ManagerDatabase.IS_FAVORITE_MAKEUP);
            return new SerializationData(context).serializationSelectMakeup(select_favorite);
        }

        // Configura a Execução da Tarefa Assincrona
        Set<Callable<String>> callableTaskAPI = new HashSet<>();
        callableTaskAPI.add(() -> SearchInternet.searchByUrl(context, uri_search, METHOD_GET));

        try {
            // Obtem o Resultado da Busca Assincrona
            List<Future<String>> futureTasksList = executorService.invokeAll(callableTaskAPI);
            String json = futureTasksList.get(0).get();

            if (json == null || json.equals("")) return null;

            List<Map<String, Object>> jsonSerialized =
                    SerializationData.serializationJOSN(json, quantity_items, getParametersJSON());
            return SerializationData.instanceMakeups(context, jsonSerialized);
        } catch (Exception ex) {
            Log.e("Error", "Erro ao manipular o JSON ou na sua Serialização. " + ex);
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Metodo de Formatação da URI conforma o Tipo de Busca Informado
     *
     * @param type_search Tipo da Busca
     * @see MainActivity#OPTION_HOME_MAKEUP
     * @see MainActivity#OPTION_MY_FAVORITE_MAKEUPS
     * @see MainActivity#OPTION_MORE_FAVORITES
     */
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

    // Getters and Setters of Classe Makeup
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

    public String getOriginalUrlImage() {
        return originalUrlImage;
    }

    public void setOriginalUrlImage(String originalUrlImage) {
        this.originalUrlImage = originalUrlImage;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

}
