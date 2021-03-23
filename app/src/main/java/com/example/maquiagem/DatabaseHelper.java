package com.example.maquiagem;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.maquiagem.Model.Makeup;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String BD = "maquigemDB";
    private static int VERSION = 1;
    private static final String TABLE_NAME = "products";
    private static final String ID = "id";
    private static final String BRAND = "brand";
    private static final String NAME = "name";
    private static final String TYPE = "type";
    private static final String PRICE = "price";
    private static final String CURRENCY = "currency";
    private static final String IMAGE = "image_link";
    private static final String DESCRIPTION = "description";

    private Object Makeup;

    public DatabaseHelper(Context context){
        super(context, BD, null, VERSION);
    }

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
                    IMAGE + " text, " +
                    DESCRIPTION + " text)"
        );
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int newI) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }


    public void insertMakeup(Makeup makeup) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BRAND, makeup.getBrand());
        values.put(NAME, makeup.getName());
        values.put(TYPE, makeup.getType());
        values.put(PRICE, makeup.getPrice());
        values.put(CURRENCY, makeup.getCurrency());
        values.put(IMAGE, makeup.getImage_link());
        values.put(DESCRIPTION, makeup.getDescription());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }


    public void clearTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }


    public List<Makeup> selectAll(){
        List<Makeup> returnAll = new ArrayList<Makeup>();

        String querryDB = "SELECT * FROM " + TABLE_NAME ;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(querryDB, null);

        if(cursor.moveToFirst()){
            String brand, name, price, currency, image, type, description;
            int id;
            do{
                id = cursor.getInt(0);
                brand = cursor.getString(1);
                name = cursor.getString(2);
                price = cursor.getString(3);
                currency = cursor.getString(4);
                image = cursor.getString(5);
                type = cursor.getString(6);
                description = cursor.getString(7);

                Makeup makeup = new Makeup(id, brand, name, type, price, currency, image, description);
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


    public Cursor getData(String type, String brand) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( "SELECT * FROM " + TABLE_NAME +
                " WHERE " +
                TYPE + "='" + type + "' AND " +
                BRAND +  "='" + brand + "'", null );
        return cursor;
    }
}


