package com.example.maquiagem.model;

import static com.example.maquiagem.controller.ManagerResources.isNullOrEmpty;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.maquiagem.R;
import com.example.maquiagem.controller.DataBaseHelper;
import com.example.maquiagem.controller.ManagerResources;
import com.example.maquiagem.model.entity.Makeup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SerializationData {

    public static final int DEFAULT_QUANTITY = 40;
    public static final int ALL_ITEMS_JSON = -1;
    private static final String URL_NO_IMAGE = "https://github.com/GuilhermeCallegari/Maquiagem/blob" +
            "/main/app/src/main/res/drawable/makeup_no_image.jpg";

    private final Context context;

    public SerializationData(Context context) {
        this.context = context;
    }

    // Tratamento/Serialização do JSON recebido
    public List<Makeup> serializationJsonMakeup(String json, int quantity_show) {

        List<Makeup> makeupList = new ArrayList<>();
        JSONArray itemsArray;

        try {
            itemsArray = new JSONArray(json);
            // Recebe o valor do Tamanho da List com os Produtos
            int quantity_array = itemsArray.length();
            int max_result;

            // Define a Quantidade de Resultados
            max_result = quantity_show == ALL_ITEMS_JSON ? quantity_array : Math.min(quantity_show, quantity_array);

            for (int i = 0; i < max_result; i++) {
                // Pega um objeto de acordo com a Posição (Posição = Item/Produto)
                JSONObject jsonObject = new JSONObject(itemsArray.getString(i));

                try {
                    Makeup makeupLoop = new Makeup();

                    // Obtem os Dados do JSON, atravez do nome dos campos
                    makeupLoop.setId(Integer.parseInt(jsonObject.getString("id")));
                    makeupLoop.setBrand(jsonObject.getString("brand"));
                    makeupLoop.setName(jsonObject.getString("name"));
                    makeupLoop.setPrice(jsonObject.getString("price").
                            replaceAll("[^0-9^,.]", ""));
                    makeupLoop.setCurrency(jsonObject.getString("currency"));
                    makeupLoop.setType(jsonObject.getString("product_type"));
                    makeupLoop.setDescription(jsonObject.getString("description").
                            replaceAll("\n", "&lt;br />"));
                    makeupLoop.setUrlImage(jsonObject.getString("image_link"));
                    makeupLoop.setFavorite(false);

                    // Normaliza as Strings Recebidas (HTML Tags ---> String)
                    makeupLoop.setBrand(ManagerResources.getNormalizedString(makeupLoop.getBrand()));
                    makeupLoop.setName(ManagerResources.getNormalizedString(makeupLoop.getName()));
                    makeupLoop.setCurrency(ManagerResources.getNormalizedString(makeupLoop.getCurrency()));
                    makeupLoop.setType(ManagerResources.getNormalizedString(makeupLoop.getType()));
                    makeupLoop.setDescription(ManagerResources.getNormalizedString(makeupLoop.getDescription()));

                    //Caso não tenha dados inseridos
                    if (makeupLoop.getName().equals("null") || isNullOrEmpty(makeupLoop.getName())) {
                        makeupLoop.setName(context.getString(R.string.empty_name));
                    }
                    if (makeupLoop.getPrice().equals("null") || isNullOrEmpty(makeupLoop.getPrice())) {
                        makeupLoop.setPrice(context.getString(R.string.empty_price));
                    }
                    if (makeupLoop.getCurrency().equals("null") || isNullOrEmpty(makeupLoop.getCurrency())) {
                        makeupLoop.setCurrency("U$");
                    }
                    if (makeupLoop.getDescription().equals("null") ||
                            isNullOrEmpty(makeupLoop.getDescription())) {
                        makeupLoop.setDescription(context.getString(R.string.empty_description));
                    }
                    if (makeupLoop.getUrlImage().equals("null") || isNullOrEmpty(makeupLoop.getUrlImage())) {
                        makeupLoop.setUrlImage(URL_NO_IMAGE);
                    }

                    // Intancia a Classe e Insere no Banco de Dados
                    makeupList.add(makeupLoop);

                } catch (Exception e) {
                    Log.e("RECOVERY ARRAY", "Erro ao recuperar os valores do Array " +
                            "dos Produtos\n" + e);
                    e.printStackTrace();
                    return null;
                }
            }

            // Após ler a Quantidade de Itens do Array ---> Retorna uma List ou Null
            return makeupList.isEmpty() ? null : makeupList;

        } catch (JSONException e) {
            // Erro na criação do Array
            Log.e("NOT VALID ARRAY", "Erro no Array ou no Recebimento da String\n" + e);
            e.printStackTrace();
            return null;
        }
    }

    // Tratamento/Serialização de Dados do Banco Local (SQLite)
    public List<Makeup> serializationSelectMakeup(String select) {
        DataBaseHelper database = new DataBaseHelper(context);
        Cursor cursor = database.selectMakeup(select);
        List<Makeup> list_resultSelect = new ArrayList<>();

        // Caso haja posição para o Cursor
        if (cursor.moveToFirst()) {

            String brand, name, price, currency, type, description, urlImage;
            int id, intFavorite;
            boolean isFavorite;

            // Pega os dados enquanto o Cursor tiver proxima posição
            do {
                id = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.ID_MAKEUP));
                brand = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.BRAND_MAKEUP));
                name = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.NAME_MAKEUP));
                type = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.TYPE_MAKEUP));
                price = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.PRICE_MAKEUP));
                currency = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.CURRENCY_MAKEUP));
                description = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.DESCRIPTION_MAKEUP));
                urlImage = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.URL_IMAGE_MAKEUP));
                intFavorite = cursor.getInt(cursor.getColumnIndexOrThrow(DataBaseHelper.IS_FAVORITE_MAKEUP));
                isFavorite = intFavorite == 1;

                Makeup makeup = new Makeup(id, brand, name, type, price, currency, description,
                        urlImage);
                makeup.setFavorite(isFavorite);

                // Adiciona o Item à Lista
                list_resultSelect.add(makeup);

            } while (cursor.moveToNext());

        } else {
            // Não possui dados na Tabela
            Log.e("EMPTY DATABASE", "Não foi encontrado nenhum " +
                    "dado no Banco de Dados\n" + cursor.toString());
            return null;
        }

        cursor.close();
        database.close();

        return list_resultSelect.isEmpty() ? null : list_resultSelect;
    }

    // todo: Implementar serialização da API_Local
}