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
    private static final String LOG_TAG = InternetTools.class.getSimpleName();

    // URL da API
    private static final String MAKEUP_URL = "http://makeup-api.herokuapp.com/api/v1/products.json?";
    // Parametros da string de Busca
    private static final String TYPE_PARAM = "t";
    private static final String BRAND_PARAM = "b";

    //Metodo para Buscar Produtos de Maquigem - API
    static String searchMakeup(String type, String brand) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String makeupJSONString = null;

        try {
            //Formação da URI
            Uri buildURI = Uri.parse(MAKEUP_URL).buildUpon()
                    .appendQueryParameter(TYPE_PARAM, type)
                    .appendQueryParameter(BRAND_PARAM, brand)
                    .build();

            //URI ==> URL.
            URL requestURL = new URL(buildURI.toString());

            //Inicio da Conexão
            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            //Conexão da URL
            urlConnection.connect();

            //Busca o InputStream
            InputStream inputStream = urlConnection.getInputStream();

            //Cria um buffer para InputStream
            reader = new BufferedReader(new InputStreamReader(inputStream));

            // Usa o StringBuilder para receber a resposta.
            StringBuilder builderResponse = new StringBuilder();
            String linha;

            //Loop para a Leitura Linha por Linha
            //.readLine = Metodo de Leitura de uma linha
            while ((linha = reader.readLine()) != null) {
                //Recebe o Valor da Linha
                builderResponse.append(linha)
                               .append("\n");
            }

            //Caso não tenha retorno
            if (builderResponse.length() == 0) {
                return null;
            }

            makeupJSONString = builderResponse.toString();

        } catch (IOException e) {
            //Mostra o Erro/Exceção
            e.printStackTrace();
        } finally {
            //Fecha a Conexão e o Reader Aberto
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //Cria um JSON no LOG
        Log.d(LOG_TAG, makeupJSONString);
        return makeupJSONString;
    }

}
