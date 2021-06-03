package com.example.maquiagem;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class InternetTools {
    //Nome do LOG
    private static final String LOG_TAG = "LOG_MAKEUP";
    // URL da API
    private static final String MAKEUP_URL = "http://makeup-api.herokuapp.com/api/v1/products.json?";
    // Constantes de Parametros da string da Busca
    private static final String TYPE_PARAM = "product_type";
    private static final String BRAND_PARAM = "brand";


    //Metodo para Buscar Produtos de Maquigem - API
    static String searchMakeup(String type, String brand) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String makeupJSONString;

        try {
            //Formação da URI
            Uri buildURI = Uri.parse(MAKEUP_URL).buildUpon()
                    .appendQueryParameter(TYPE_PARAM, type)
                    .appendQueryParameter(BRAND_PARAM, brand)
                    .build();

            //URI ==> URL.
            URL requestURL = new URL(buildURI.toString());
            //Inicio da Conexão usando o metodo GET da API
            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();


            //Inicia o InputStream
            InputStream inputStream = urlConnection.getInputStream();

            if (inputStream == null) {
                //Caso não tenha dados no InpuStream
                return null;
            }

            // Cria um buffer(p/ armazenar dados) para InputStream
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer bufferResponse = new StringBuffer();
            // Variavel que recupera da consulta na API o valor por linha
            String linha;

            //Loop para a Leitura Linha por Linha
            //.readLine = Metodo de Leitura de uma linha
            while ((linha = reader.readLine()) != null) {
                //Recebe o Valor da Linha
                bufferResponse.append(linha).append("\n");
            }

            if (bufferResponse.length() == 0) {
                //Buffer sem Informações
                return null;
            }

            //String que recebe os valores da Busca
            makeupJSONString = bufferResponse.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            //Fecha a Conexão
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            //Fecha o Reader
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //Cria um LOG do JSON
        Log.d(LOG_TAG, makeupJSONString);
        return makeupJSONString;
    }

}
