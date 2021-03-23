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
    //private static final String LOG_TAG = InternetTools.class.getSimpleName();
    private static final String LOG_TAG = "LOG_MAKEUP";

    // URL da API
    private static final String MAKEUP_URL = "http://makeup-api.herokuapp.com/api/v1/products.json?";
    // Parametros da string de Busca
    private static final String TYPE_PARAM = "product_type";
    private static final String BRAND_PARAM = "brand";

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
            System.out.println(buildURI.toString());

            //URI ==> URL.
            URL requestURL = new URL(buildURI.toString());

            //Inicio da Conexão
            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            System.out.println(urlConnection.toString());
            urlConnection.connect();


            //Busca o InputStream
            InputStream inputStream = urlConnection.getInputStream();

            if (inputStream == null) {
                // Nothing to do.
                return null;
            }

            //Cria um buffer para InputStream
            reader = new BufferedReader(new InputStreamReader(inputStream));
            // Usa o StringBuilder para receber a resposta.
            //StringBuilder builderResponse = new StringBuilder();
            StringBuffer bufferResponse = new StringBuffer();
            String linha;

            //Loop para a Leitura Linha por Linha
            //.readLine = Metodo de Leitura de uma linha
            while ((linha = reader.readLine()) != null) {
                //Recebe o Valor da Linha
                bufferResponse.append(linha + "\n");
            }

            if (bufferResponse.length() == 0) {
                return null;
            }

            //makeupJSONString = builderResponse.toString();
            makeupJSONString = bufferResponse.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
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
