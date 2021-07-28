package com.example.maquiagem.model;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SearchInternet {

    // Metodo para Buscar em uma API ---> Retorna uma String com o JSON
    public static String searchByUrl(String url, String method) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String json_string;

        try {
            URL requestURL = new URL(url);
            //Inicio da Conexão usando o metodo GET da API
            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod(method);
            urlConnection.connect();

            //Inicia o InputStream
            InputStream inputStream = urlConnection.getInputStream();

            //Caso não tenha dados para Acessar
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
        Log.i("RESULT SEARCH", json_string);
        return json_string;
    }

}
