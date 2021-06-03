package com.example.maquiagem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    //Definição das Constantes usadas
    private static final String BD = "maquigemDB";
    private static int VERSION = 1;
    private static final String TABLE_NAME = "products";
    private static final String ID = "id";
    private static final String BRAND = "brand";
    private static final String NAME = "name";
    private static final String TYPE = "type";
    private static final String PRICE = "price";
    private static final String CURRENCY = "currency";
    private static final String DESCRIPTION = "description";

    public DatabaseHelper(Context context){
        super(context, BD, null, VERSION);
    }

    //Crição do Banco de Dados
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(
            "create table " + TABLE_NAME + " (" +
                    ID + " integer PRIMARY KEY AUTOINCREMENT, " +
                    BRAND + " text, " +
                    NAME + " text, " +
                    TYPE + " text, " +
                    PRICE + " text, " +
                    CURRENCY + " varchar(5), " +
                    DESCRIPTION + " text)"
        );
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int newI) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }


    //Inserção de Dados no Banco de Dados ---> Usa a classe MakeupClass
    public void insertMakeup(MakeupClass makeup) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BRAND, makeup.getBrand());
        values.put(NAME, makeup.getName());
        values.put(TYPE, makeup.getType());
        values.put(PRICE, makeup.getPrice());
        values.put(CURRENCY, makeup.getCurrency());
        values.put(DESCRIPTION, makeup.getDescription());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }


    //Limpa toda a Tabela do Banco de Dados
    public void clearTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
        db.close();
    }

    //Seleciona uma maquiagem do Banco de Dados
    public Cursor getData(String type, String brand) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;

        //Form
        cursor =  db.rawQuery( "SELECT * FROM " + TABLE_NAME +
                " WHERE " + TYPE + "='" + type +
                "' AND " + BRAND +  "='" + brand + "'",
                null );

        return cursor;
    }

    //Seleciona todos os itens do Banco de Dados
    public List<MakeupClass> selectAll(){

        //Cria um arrei baseado na classe makeup
        List<MakeupClass> returnAll = new ArrayList<MakeupClass>();

        String queryDB = "SELECT * FROM " + TABLE_NAME ;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryDB, null);

        if(cursor.moveToFirst()){
            String brand, name, price, currency, type, description;
            int id;

            //Loop de Repetição que ira funcionar enquanto o cursor tiver uma proxima posição
            do{
                id = cursor.getInt(0);
                brand = cursor.getString(1);
                name = cursor.getString(2);
                price = cursor.getString(3);
                currency = cursor.getString(4);
                type = cursor.getString(5);
                description = cursor.getString(6);

                MakeupClass makeup = new MakeupClass(id, brand, name, type, price, currency, description);
                returnAll.add(makeup);

            }while (cursor.moveToNext());
        }
        else{
            System.out.println("Tabela Vazia");
            return returnAll;
        }

        cursor.close();
        db.close();
        return returnAll;
    }

}


