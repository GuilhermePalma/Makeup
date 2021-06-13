package com.example.maquiagem.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.strictmode.SqliteObjectLeakedViolation;

import java.util.ArrayList;
import java.util.List;

public class DataBaseMakeup extends SQLiteOpenHelper {

    //Definição das Constantes usadas
    private static final String BD = "maquigemDB";
    private static int VERSION = 1;
    private static final String TABLE_MAKEUP = "products";
    private static final String ID_MAKEUP = "id";
    private static final String BRAND = "brand";
    private static final String NAME = "name";
    private static final String TYPE = "type";
    private static final String PRICE = "price";
    private static final String CURRENCY = "currency";
    private static final String DESCRIPTION = "description";
    private static final String IMAGE = "image";

    private static final String TABLE_LOCATION = "location";
    private static final String ID_LOCATION = "id";
    private static final String LOCATION = "return_location";

    public DataBaseMakeup(Context context){
        super(context, BD, null, VERSION);
    }


    // Crição do Banco de Dados
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(
            "create table " + TABLE_MAKEUP + " (" +
                    ID_MAKEUP + " integer PRIMARY KEY AUTOINCREMENT, " +
                    BRAND + " text, " +
                    NAME + " text, " +
                    TYPE + " text, " +
                    PRICE + " text, " +
                    CURRENCY + " varchar(5), " +
                    DESCRIPTION + " text, " +
                    IMAGE + " text)"
        );
        db.execSQL(
                "create table " + TABLE_LOCATION + " (" +
                        ID_LOCATION + " integer PRIMARY KEY AUTOINCREMENT, " +
                        LOCATION + " text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int newI) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAKEUP);
        this.onCreate(db);
    }


    // Inserção de Dados no Banco de Dados ---> Usa a classe MakeupClass
    public void insertMakeup(Makeup makeup) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BRAND, makeup.getBrand());
        values.put(NAME, makeup.getName());
        values.put(TYPE, makeup.getType());
        values.put(PRICE, makeup.getPrice());
        values.put(CURRENCY, makeup.getCurrency());
        values.put(DESCRIPTION, makeup.getDescription());
        values.put(IMAGE, makeup.getUrlImage());

        db.insert(TABLE_MAKEUP, null, values);
        db.close();
    }

    // Insere se a busca da Localização deu certa ou não
    public void insertLocation(String returnLocation){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LOCATION, returnLocation);

        db.insert(TABLE_LOCATION, null, values);
    }


    // Limpa toda a Tabela do Banco de Dados
    public void clearTableMakeup() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_MAKEUP);
        db.close();
    }

    // Apaga todos os dados da Tabela Localização
    public void clearTableLocation(){
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + TABLE_LOCATION);
        database.close();
    }


    //Seleciona uma maquiagem do Banco de Dados
    public Cursor getDataMakeup(String type, String brand) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;

        //Form
        cursor =  db.rawQuery( "SELECT * FROM " + TABLE_MAKEUP +
                " WHERE " + TYPE + "='" + type +
                "' AND " + BRAND +  "='" + brand + "'",
                null );

        return cursor;
    }

}


