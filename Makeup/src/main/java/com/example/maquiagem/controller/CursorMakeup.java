package com.example.maquiagem.controller;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.maquiagem.model.Makeup;

import java.util.ArrayList;
import java.util.List;

public class CursorMakeup {

    private final DataBaseHelper database;

    // Construtor da Classe ---> Database já configurado e Select no DataBase
    public CursorMakeup(Context context) {
        this.database = new DataBaseHelper(context);
    }

    public List<Makeup> selectDataBase(String select) {

        Cursor cursor = database.selectMakeup(select);
        List<Makeup> list_resultSelect = new ArrayList<>();

        // Caso haja posição para o Cursor
        if (cursor.moveToFirst()) {

            String brand, name, price, currency, type, description, urlImage, stringFavorite;
            int id;
            boolean isFavorited;

            // Pega os dados enquanto o Cursor tiver proxima posição
            do {
                id = cursor.getInt(0);
                brand = cursor.getString(1);
                name = cursor.getString(2);
                type = cursor.getString(3);
                price = cursor.getString(4);
                currency = cursor.getString(5);
                description = cursor.getString(6);
                urlImage = cursor.getString(7);
                stringFavorite = cursor.getString(8);
                isFavorited = stringFavorite.equals("true");

                Makeup makeup = new Makeup(id, brand, name, type, price, currency, description,
                        urlImage);
                makeup.setFavorite(isFavorited);

                // Atualiza a Lista e o RecyclerView
                list_resultSelect.add(makeup);

            } while (cursor.moveToNext());

            cursor.close();
            database.close();

            return list_resultSelect;

        } else {
            cursor.close();
            database.close();

            // Não possui dados na Tabela
            Log.e("EMPTY DATABASE", "Não foi encontrado nenhum " +
                    "dado no Banco de Dados\n" + cursor.toString());
            return null;
        }
    }

}
