package com.example.maquiagem.model;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.maquiagem.controller.ManagerResources;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SearchInternet {

    /**
     * URL de Pesquisa de Produto na Makeup-API
     *
     * @see <a href="http://makeup-api.herokuapp.com">Makeup-API</a>
     */
    public static final String URL_MAKEUP = "http://makeup-api.herokuapp.com/api/v1/products.json?";
    /**
     * Parametro de Busca de Classificação Maiores das Maquiagens
     *
     * @see #URL_MAKEUP
     */
    public static final String PARAM_RATING_GREATER = "rating_greater_than";
    /**
     * Parametro de Busca de Marcas das Maquiagens
     *
     * @see #URL_MAKEUP
     */
    public static final String PARAM_BRAND = "brand";
    /**
     * Parametro de Busca de Tipos das Maquiagens
     *
     * @see #URL_MAKEUP
     */
    public static final String PARAM_TYPE = "product_type";
    /**
     * Parametro de Busca de Categoria das Maquiagens
     *
     * @see #URL_MAKEUP
     */
    public static final String PARAM_CATEGORY = "product_category";
    /**
     * Parametro de Busca de Tags das Maquiagens
     *
     * @see #URL_MAKEUP
     */
    public static final String PARAM_TAGS = "product_tags";

    /**
     * Metodo HTTPS GET, utilizado normalmente em consultas. Não há necessidade de Body
     */
    public static final String METHOD_GET = "GET";

    /**
     * Metodo Estatico de Consulta em uma API
     *
     * @param context {@link Context} utilizado para verificar a Internet
     * @param uri     {@link Uri} em que será buscada a Makeup
     * @param method  Metodo de Pesquisa na API (GET, POST, PUT, PATCH, etc)
     * @return {@link String}|""
     * @see ManagerResources#hasConnectionInternet(Context)
     */
    public static String searchByUrl(Context context, Uri uri, String method) {

        if (!ManagerResources.hasConnectionInternet(context)) return null;

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String json_string;

        try {
            URL requestURL = new URL(uri.toString());
            //Inicio da Conexão usando o metodo GET da API
            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod(method);
            urlConnection.connect();

            //Inicia o InputStream e Verifica se há dados p/ acessar
            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) return "";

            // Cria um Leitor dos Dados e uma String para armazenar
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder bufferResponse = new StringBuilder();
            String linha;

            //Loop para a Leitura Linha por Linha ---> .readLine = lê uma linha
            while ((linha = reader.readLine()) != null) {
                bufferResponse.append(linha).append("\n");
            }

            if (bufferResponse.length() == 0) return "";

            //String que recebe os valores da Busca
            json_string = bufferResponse.toString();

        } catch (Exception ex) {
            Log.e("ERROR API", "Error ao Obter os Dados da API. Exceção: " + ex);
            ex.printStackTrace();
            json_string = "";
        } finally {
            //Fecha a Conexão e o Reader
            try {
                if (urlConnection != null) urlConnection.disconnect();
                if (reader != null) reader.close();
            } catch (Exception ex) {
                Log.e("Error Close", "Error ao Fechar os Recursos da API. Exceção: " + ex);
                ex.printStackTrace();
            }
        }
        return json_string;
    }

}
