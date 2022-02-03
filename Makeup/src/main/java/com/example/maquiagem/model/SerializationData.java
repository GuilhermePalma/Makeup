package com.example.maquiagem.model;

import static com.example.maquiagem.controller.ManagerResources.getNormalizedString;
import static com.example.maquiagem.controller.ManagerResources.isNullOrEmpty;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;

import com.example.maquiagem.controller.ManagerDatabase;
import com.example.maquiagem.model.entity.Makeup;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SerializationData {

    /**
     * Consntante com a quantidade Padrão de Itens que serão serializados
     */
    public static final int DEFAULT_QUANTITY = 40;
    /**
     * Constante com o Valor informando que será serializado todos o Itens
     */
    public static final int ALL_ITEMS_JSON = -1;

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
     * Serializa o JSON de Forma Dinamica, obtendo is parametros do  JsonObject/JsonArray/String.
     *
     * @param json          JSON que será Serializado (Não Nulo ou Vazio)
     * @param definedKeys   String Array com as Chaves Definidas que se deseja Obter. Caso queira obter
     *                      todas as Keys do JSON, passe <b>null</b>
     * @param quantity_show Quantiade de Itens que serão serializados
     * @return {@link List<Map> List< Map< Key,Value > >} |null
     * @see #ALL_ITEMS_JSON
     * @see #DEFAULT_QUANTITY
     */
    public static List<Map<String, Object>> serializationJOSN(final String json, final int quantity_show,
                                                              final String[] definedKeys) {

        // Verifica o JSON e Instancia a Variavel que armazenara os valores
        if (isNullOrEmpty(json)) return null;
        List<Map<String, Object>> serializedJSON = new ArrayList<>();

        try {
            // Obtem o Proximo valor do JSON e Verifica se é null
            Object objectJSON = new JSONTokener(json).nextValue();
            if (objectJSON == null) return null;

            // Verifica o Tipo de Varivavel o JSON recebido
            if (objectJSON instanceof JSONArray) {
                // Serializa o JSONObject e Instancia a Lista com a Lista Recebida
                serializedJSON = serializationJsonArray((JSONArray) objectJSON, quantity_show, definedKeys);
            } else if (objectJSON instanceof JSONObject) {
                // Serializa o JSONObject e Insere na Lista
                final Map<String, Object> serializedJsonObject = serializationJsonObject(
                        (JSONObject) objectJSON, definedKeys);
                serializedJSON.add(serializedJsonObject);
            } else if (objectJSON instanceof String) {
                // Armazenará as Keys/Values da String
                Map<String, Object> value = new HashMap<>();
                value.put("item", objectJSON);
                serializedJSON.add(value);
            }

        } catch (Exception ex) {
            Log.e("Erro JSON", "Erro ao Serilizar o JSON. Exceção: " + ex.getClass().getName());
            ex.printStackTrace();
            serializedJSON = null;
        }
        return serializedJSON;
    }

    /**
     * Serializa um {@link JSONObject}, retornando um {@link Map} com as Keys Obtidas e seus Valores
     * dinamicos
     *
     * @param jsonObject  {@link JSONObject} que será serializado
     * @param definedKeys String Array especificando quais chaves serão obtidas
     * @return {@link Map}|null
     */
    private static Map<String, Object> serializationJsonObject(final JSONObject jsonObject,
                                                               final String[] definedKeys) {
        // Verifica se o JSON é Nulo e cria a variavel que armazenará os Dados
        if (jsonObject == null) return null;
        Map<String, Object> valuesJSON;

        try {
            // Obtem as Chaves do JSONObject
            final Iterator<String> listKeys = jsonObject.keys();

            // Não há nenhuma Chave
            if (!listKeys.hasNext()) return null;

            // Define quais Keys seão obtidas do JSON
            List<String> finalKeys = new ArrayList<>();
            if (definedKeys == null) {
                while (listKeys.hasNext()) {
                    finalKeys.add(listKeys.next());
                }
            } else finalKeys.addAll(Arrays.asList(definedKeys));

            // Inicializa a Classe que Map que armazena os Resultados
            valuesJSON = new HashMap<>();

            // Obtem todas os valores das Keys
            for (String key : finalKeys) {

                // Verifica se o valor é null e obtem o Item. Em seguida serializa conforme o Valor
                final Object objectValue = jsonObject.isNull(key) ? "" : jsonObject.get(key);
                if (objectValue instanceof JSONArray) {
                    // Obtem a Lista de Valores do JSON Array e Insere nos Valores
                    final List<Map<String, Object>> valuesList = serializationJsonArray(
                            (JSONArray) objectValue, ALL_ITEMS_JSON, null);

                    if (valuesList != null) valuesJSON.put(key, valuesList);
                } else if (objectValue instanceof JSONObject) {
                    // Obtem o JSONObject e Obtem suas Chaves
                    final JSONObject subJsonObject = (JSONObject) objectValue;
                    final Iterator<String> keysSubJsonObject = subJsonObject.keys();

                    while (keysSubJsonObject.hasNext()) {
                        // Usa a chave, Obtem o Item (Se não for null) e Insere a Key/Value
                        final String keyOfSubJsonObject = keysSubJsonObject.next();
                        Object valueJsonObject = subJsonObject.isNull(key) ? null : subJsonObject.get(key);
                        if (valueJsonObject != null)
                            valuesJSON.put(keyOfSubJsonObject, valueJsonObject);
                    }
                } else if (objectValue instanceof String || objectValue instanceof Boolean
                        || objectValue instanceof Integer || objectValue instanceof Double ||
                        objectValue instanceof Float) {
                    valuesJSON.put(key, objectValue);
                }
            }
        } catch (Exception ex) {
            Log.e("Erro JSON", "Erro ao Serilizar o JSON Object. Exceção: " + ex.getClass().getName());
            ex.printStackTrace();
            valuesJSON = null;
        }
        return valuesJSON;
    }

    /**
     * Serializa um {@link JSONArray}, retornando uma {@link List} com os {@link Map} (que contem as
     * Keys Obtidas e seus Valores dinamicos). Cada Item da {@link List}, representa um Item do
     * {@link JSONArray}. Enquanto cada item do {@link Map}, representa os dados de um Item Especifico
     * do {@link JSONArray}
     * <p>
     * * Itens sem Keys, serão obtidas e armazenadas no {@link Map} com a Key "item"
     *
     * @param jsonArray   {@link JSONArray} que será serializado
     * @param definedKeys String Array especificando quais chaves serão obtidas
     * @return {@link Map}|null
     */
    private static List<Map<String, Object>> serializationJsonArray(final JSONArray jsonArray,
                                                                    final int quantity_show,
                                                                    final String[] definedKeys) {

        // Verifica se o JSONArray é Nulo e cria a Variavel que armazenará os Valores
        if (jsonArray == null) return null;
        List<Map<String, Object>> listValues = new ArrayList<>();

        try {
            // Define a quantidade de Itens do JSON Array que serão obtidos
            int maxIndexArray = quantity_show == ALL_ITEMS_JSON
                    ? jsonArray.length() : Math.min(jsonArray.length(), quantity_show);

            // Obtem os Valores dentro do Array
            for (int i = 0; i < maxIndexArray; i++) {
                // Reinicia o Map das Keys/Values a cada Objeto
                Map<String, Object> valuesItem = new HashMap<>();

                // Obtem o Item dentro de uma Posição Especifica do JSONArray
                Object objectArray = jsonArray.get(i);

                // Verifica o Tipo de Item Recebido
                if (objectArray instanceof JSONArray) {

                    // Caso seja um Array, obtem Item por Item e Serializa conforme as Opções
                    JSONArray subJsonArray = (JSONArray) objectArray;
                    for (int u = 0; i < subJsonArray.length(); u++) {
                        Object objectSubJsonArray = subJsonArray.get(u);

                        // Obtem os Dados de um JSON Object OU o Valor contido dentro do Array
                        if (objectSubJsonArray instanceof JSONObject) {
                            valuesItem.putAll(serializationJsonObject((JSONObject) objectSubJsonArray, null));
                        } else if (objectSubJsonArray instanceof String || objectSubJsonArray instanceof Boolean
                                || objectSubJsonArray instanceof Integer) {
                            valuesItem.put("item", objectSubJsonArray);
                        }
                    }
                } else if (objectArray instanceof JSONObject) {
                    valuesItem.putAll(serializationJsonObject((JSONObject) objectArray, definedKeys));
                } else if (objectArray instanceof String || objectArray instanceof Boolean
                        || objectArray instanceof Integer) {
                    // Adiciona o Item com uma Key Generica
                    valuesItem.put("item", objectArray);
                }

                // Adiciona os Itens serializados do Item Selecionado do JSONArray
                listValues.add(valuesItem);
            }
        } catch (Exception ex) {
            Log.e("Erro JSON", "Erro ao Serilizar o JSON Array. Exceção: " + ex.getClass().getName());
            ex.printStackTrace();
            listValues = null;
        }
        return listValues;
    }

    /**
     * Serializa uma {@link List} com varios {@link Map}, tendo como base os parametros existentes
     * na Classe {@link Makeup}
     *
     * @param context     {@link Context} utilizado para Instanciar a classe {@link Makeup}
     * @param itemsMakeup {@link List} com os {@link Map} que tem os dados de cada Makeup
     * @return {@link List}|null
     * @see Makeup#getParametersJSON()
     */
    public static List<Makeup> instanceMakeups(final Context context, final List<Map<String, Object>> itemsMakeup) {

        // Cria a Varivael que armazenará as Makeups e Obtem as Keys que serão Obtidos
        List<Makeup> makeupList = new ArrayList<>();
        final String[] getParam = Makeup.getParametersJSON();

        try {

            // Obtem as Makeups Favoritas
            final List<Makeup> listFavorites = new ManagerDatabase(context).getFavoritesMakeup();

            // Serializa cada Item dentro da Lista de Map
            for (Map<String, Object> itemMap : itemsMakeup) {

                if (itemMap != null) {
                    Makeup makeup = new Makeup();

                    makeup.setId(itemMap.get(getParam[0]) instanceof Integer ? (int) itemMap.get(getParam[0]) : 0);
                    makeup.setBrand(getNormalizedString((String) itemMap.get(getParam[1])));
                    makeup.setName(getNormalizedString((String) itemMap.get(getParam[2])));

                    // Normaliza e Converte o Preço de Forma apropriada
                    String rawPrice = (String) itemMap.get(getParam[3]);
                    double price = 0;
                    if (!isNullOrEmpty(rawPrice)) {
                        rawPrice = rawPrice.replaceAll("[^0-9^,.]", "");
                        price = Double.parseDouble(rawPrice);
                    }

                    makeup.setPrice(price);
                    makeup.setCharPrice((String) itemMap.get(getParam[4]));
                    makeup.setCurrency((String) itemMap.get(getParam[5]));
                    makeup.setOriginalUrlImage((String) itemMap.get(getParam[6]));
                    makeup.setApiUrlImage((String) itemMap.get(getParam[7]));

                    // Serializa e Retira os Possiveis caracteres que podem gerar erros
                    final String rawDescription = (String) itemMap.get(getParam[8]);
                    String finalDescription = "";
                    if (!isNullOrEmpty(rawDescription)) {
                        finalDescription = rawDescription.replaceAll("\n", "&lt;br />")
                                .replaceAll("<", "&lt;");
                        finalDescription = getNormalizedString(finalDescription);
                    }

                    makeup.setDescription(finalDescription);

                    // A avaliação do Produto pode ser um Float ou um Double
                    if (itemMap.get(getParam[9]) instanceof Float) {
                        makeup.setRatingProduct((float) itemMap.get(getParam[9]));
                    } else if (itemMap.get(getParam[9]) instanceof Integer
                            || itemMap.get(getParam[9]) instanceof Double) {
                        makeup.setRatingProduct(Float.parseFloat(String.valueOf(itemMap.get(getParam[9]))));
                    }
                    makeup.setCategory(getNormalizedString((String) itemMap.get(getParam[10])));
                    makeup.setType(getNormalizedString((String) itemMap.get(getParam[11])));
                    makeup.setUrlInAPI((String) itemMap.get(getParam[13]));

                    // Tenta Obter os Parametros das Tags do Produto
                    if (itemMap.get(getParam[12]) instanceof List) {

                        List<Map<String, Object>> tags = (List<Map<String, Object>>) itemMap.get(getParam[12]);

                        if (tags != null) {
                            // Armazenará os Valores das Tags
                            List<String> finalTags = new ArrayList<>();

                            // Obtem Valor por Valor da List de Map das Tags
                            for (Map<String, Object> mapOfList : tags) {
                                // Cria uma Lista com os Objects da Lista
                                Object[] valuesOfMap = mapOfList.values().toArray();

                                // Obtem Object por Object e tenta adicionar à Lista de Tags
                                for (Object itemOfMap : valuesOfMap) {
                                    if (itemOfMap instanceof String)
                                        finalTags.add((String) itemOfMap);
                                }
                            }
                            // Adiciona à Classe Makeup a lista de Tags Serializada
                            makeup.setTags(finalTags.toArray(new String[0]));
                        }
                    }

                    // Tenta Obter os Dados da Cor da Makeup
                    if (itemMap.get(getParam[14]) instanceof List) {

                        List<Map<String, Object>> colors = (List<Map<String, Object>>) itemMap.get(getParam[14]);

                        if (colors != null) {

                            // Cria duas Listas que armazenarão o Nome e Codigo Hexa Decimal da Cor
                            List<String> colorHex = new ArrayList<>();
                            List<String> colorName = new ArrayList<>();

                            // Variavel que armazenará os Valores das Cores
                            Map<String, Integer> listColors = new HashMap<>();

                            // Obtem os Maps da List de Cores
                            for (Map<String, Object> itemColor : colors) {
                                if (itemColor.get("hex_value") instanceof String && itemColor.get("colour_name") instanceof String) {
                                    // Obtem o Codigo da Cor
                                    int codeColor = Color.parseColor((String) itemColor.get("hex_value"));

                                    // Insere na Lista de Cores o nome e o Codigo Hexadecinal
                                    listColors.put((String) itemColor.get("colour_name"), codeColor);
                                } else if (itemColor.get("hex_value") instanceof String) {
                                    // Quando se tem apenas tem o Codigo da Cor
                                    listColors.put("", Color.parseColor((String) itemColor.get("hex_value")));
                                }
                            }
                            // Insere a Lista de cores na Makeup
                            makeup.setColors(listColors);
                        }
                    }

                    // Define se a Makeup está entre as Favoritas
                    if (listFavorites!= null && !listFavorites.isEmpty()){
                        for (Makeup makeupFavorite : listFavorites) {
                            if (makeupFavorite.getId() == makeup.getId()) makeup.setFavorite(true);
                        }
                    }

                    // Insere o valor da Makeup na Lista Retornada
                    makeupList.add(makeup);
                }
            }
        } catch (Exception ex) {
            Log.e("Erro Makeup", "Erro ao Serilizar o Map para Makeup. Exceção: " + ex.getClass().getName());
            ex.printStackTrace();
            makeupList = null;
        }
        return makeupList;
    }

    public static List<Makeup> serializationDatabaseMakeup(Cursor cursor) {
        List<Makeup> makeupsFavorites = null;

        try {
            if (cursor != null && !cursor.isClosed() && cursor.moveToFirst()) {
                makeupsFavorites = new ArrayList<>();

                do {
                    Makeup makeup = new Makeup();
                    makeup.setId(cursor.getInt(cursor.getColumnIndexOrThrow(ManagerDatabase.ID_MAKEUP)));
                    makeup.setUrlInAPI(cursor.getString(cursor.getColumnIndexOrThrow(ManagerDatabase.URL_API_MAKEUP)));
                    final int valueFavorite = cursor.getInt(cursor.getColumnIndexOrThrow(ManagerDatabase.IS_FAVORITE_MAKEUP));
                    makeup.setFavorite(valueFavorite == ManagerDatabase.TRUE);

                    makeupsFavorites.add(makeup);
                } while (cursor.moveToNext());
            }
        } catch (Exception ex) {
            Log.e("Error Database", "Erro ao Serializar uma Makeup do Banco de Dados. Exceção: " + ex);
            ex.printStackTrace();
            makeupsFavorites = null;
        }
        return makeupsFavorites;
    }
}