package com.example.maquiagem.model;

import android.content.Context;
import android.util.Log;

import com.example.maquiagem.controller.ManagerResources;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SearchInternet {

    public static final String URL_MAKEUP = "http://makeup-api.herokuapp.com/api/v1/products.json?";
    public static final String PARAM_RATING_GREATER = "rating_greater_than";
    public static final String PARAM_BRAND = "brand";
    public static final String PARAM_TYPE = "product_type";
    public static final String PARAM_CATEGORY = "product_category";
    public static final String PARAM_TAGS = "product_tags";

    // Metodo para Buscar em uma API ---> Retorna uma String com o JSON
    public static String searchByUrl(Context context, String url, String method) {

        if (!ManagerResources.hasConnectionInternet(context)) return null;

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String json_string;

        try {
            URL requestURL = new URL(url);
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

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e("ERROR FORMED URL", "Erro na Formação da URL\n" + e);
            return "";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("NO DATA AVAILABLE", "Arquivo não Disponivel\n" + e);
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ERROR READ URL", "Error ao Ler a URL Connection\n" + e);
            return "";
        } finally {
            //Fecha a Conexão
            if (urlConnection != null) urlConnection.disconnect();

            //Fecha o Reader
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return json_string;
    }

}
