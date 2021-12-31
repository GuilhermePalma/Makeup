package com.example.maquiagem.model;

import static com.example.maquiagem.controller.ManagerResources.isNullOrEmpty;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.maquiagem.R;
import com.example.maquiagem.controller.ManagerDatabase;
import com.example.maquiagem.controller.ManagerResources;
import com.example.maquiagem.model.entity.Makeup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SerializationData {

    /**
     * Consntante com a quantidade Padrão de Itens que serão serializados
     */
    public static final int DEFAULT_QUANTITY = 40;
    /**
     * Constante com o Valor informando que será serializado todos o Itens
     */
    public static final int ALL_ITEMS_JSON = -1;

    // TODO: Alterar e Colocar uma URL da Imagem Fora do Projeto GitHub
    /**
     * Constante com o Valor de uma URL que será utilzida quando uma Makeup não possuir Imagem
     */
    private static final String URL_NO_IMAGE = "https://github.com/GuilhermePalma/Makeup/blob/main/" +
            "Makeup/src/main/res/drawable/makeup_no_image.jpg";

    private final Context context;

    /**
     * Construtor da Classe {@link SerializationData}
     *
     * @param context {@link Context} utilizado na manipulação de recursos do APP
     */
    public SerializationData(Context context) {
        this.context = context;
    }

    /**
     * Metodo responsavel por Serializar um JSON de {@link Makeup} recebidas
     *
     * @param json          {@link String} Json que será serializado
     * @param quantity_show Quantiade de Itens que serão serializados
     * @return {@link List}|null
     * @see #ALL_ITEMS_JSON
     * @see #DEFAULT_QUANTITY
     */
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

        } catch (Exception ex) {
            // Erro na criação do Array
            Log.e("Erro JSON", "Erro ao Serilizar o JSON. Exceção: " + ex);
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Serialização de um {@link Cursor} resultante de um SELECT no Banco de Dados Local
     * ({@link ManagerDatabase}).
     *
     * @param select {@link String} SELECT de consulta no Banco de Dados Local
     * @return {@link List}|null
     */
    public List<Makeup> serializationSelectMakeup(String select) {
        List<Makeup> list_resultSelect = null;
        Cursor cursor = null;

        try {
            ManagerDatabase database = new ManagerDatabase(context);
            cursor = database.selectMakeup(select);

            // Caso haja posição para o Cursor = Possui Registros
            if (cursor != null && !cursor.isClosed() && cursor.moveToFirst()) {

                list_resultSelect = new ArrayList<>();
                String brand, name, price, currency, type, description, urlImage;
                int id, intFavorite;
                boolean isFavorite;

                // Pega os dados enquanto o Cursor tiver proxima posição
                do {
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(ManagerDatabase.ID_MAKEUP));
                    brand = cursor.getString(cursor.getColumnIndexOrThrow(ManagerDatabase.BRAND_MAKEUP));
                    name = cursor.getString(cursor.getColumnIndexOrThrow(ManagerDatabase.NAME_MAKEUP));
                    type = cursor.getString(cursor.getColumnIndexOrThrow(ManagerDatabase.TYPE_MAKEUP));
                    price = cursor.getString(cursor.getColumnIndexOrThrow(ManagerDatabase.PRICE_MAKEUP));
                    currency = cursor.getString(cursor.getColumnIndexOrThrow(ManagerDatabase.CURRENCY_MAKEUP));
                    description = cursor.getString(cursor.getColumnIndexOrThrow(ManagerDatabase.DESCRIPTION_MAKEUP));
                    urlImage = cursor.getString(cursor.getColumnIndexOrThrow(ManagerDatabase.URL_IMAGE_MAKEUP));
                    intFavorite = cursor.getInt(cursor.getColumnIndexOrThrow(ManagerDatabase.IS_FAVORITE_MAKEUP));
                    isFavorite = intFavorite == 1;

                    Makeup makeup = new Makeup(id, brand, name, type, price, currency, description,
                            urlImage);
                    makeup.setFavorite(isFavorite);

                    // Adiciona o Item à Lista
                    list_resultSelect.add(makeup);

                } while (cursor.moveToNext());

                if (!cursor.isClosed()) cursor.close();
            }
        } catch (Exception ex) {
            // Erro na criação do Array
            Log.e("Erro SQLITE", "Erro ao Serilizar o SELECT do SQLite. Exceção: " + ex);
            ex.printStackTrace();
            list_resultSelect = null;
        } finally {
            if (cursor != null) cursor.close();
        }

        return list_resultSelect;
    }

    // todo: Implementar serialização da API_Local
}