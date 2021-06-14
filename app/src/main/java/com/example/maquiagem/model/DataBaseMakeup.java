package com.example.maquiagem.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DataBaseMakeup extends SQLiteOpenHelper {

    //Definição das Constantes usadas
    private static final String BD = "makeupDB";
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

    // Verifica se ja existe os produtos buscados
    public boolean existsRecords(String type, String brand){
        SQLiteDatabase database = this.getReadableDatabase();

        // Conta cada item com as variavesis recebidas
        int amountRecords = (int) DatabaseUtils.queryNumEntries(database,TABLE_MAKEUP,
                TYPE + "='" + type + "' AND " + BRAND + "='" + brand+ "'");

        database.close();

        // Caso tenha 1 ou mais registros ---> True
        if (amountRecords >= 1){
            return true;
        } else {
            return false;
        }
    }

    // Insere se a busca da Localização deu certa ou não
    public void insertLocation(boolean returnLocation){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (returnLocation) {
            values.put(LOCATION, "correct_position");
        } else {
            values.put(LOCATION, "wrong_position");
        }
        db.insert(TABLE_LOCATION, null, values);
        db.close();
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


    // Seleciona um Produto do Banco de Dados
    public Cursor getDataMakeup(String type, String brand) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor;
        // Cria um cursor com cada Produto
        cursor =  db.rawQuery( "SELECT * FROM " + TABLE_MAKEUP +
                " WHERE " + TYPE + "='" + type +
                "' AND " + BRAND +  "='" + brand + "'",
                null );
        db.close();
        return cursor;
    }


    // Recupera a quantidade de posições certas
    public int getCorrectLocation(){
        // Abre uma conexão com o Banco de Dados para Leitura (Select)
        SQLiteDatabase db = this.getReadableDatabase();
        // Resultado Pesquisado
        String correct = "correct_position";

        // Retorna a quantidade do select com a palavra acima
        int amountCorrect = (int) DatabaseUtils.queryNumEntries(db, TABLE_LOCATION,
                LOCATION + "='" + correct + "'");

        db.close();
        return amountCorrect;
    }

    // Recupera a quantidade de posições erradas
    public int getWrongLocation(){
        SQLiteDatabase db = this.getReadableDatabase();
        String wrong = "wrong_position";

        int amountWrong = (int) DatabaseUtils.queryNumEntries(db, TABLE_LOCATION,
                LOCATION + "='" + wrong + "'");

        db.close();
        return amountWrong;
    }

    public int getAmountLocation(){
        SQLiteDatabase database = this.getReadableDatabase();
        int amountLocation = (int) DatabaseUtils.queryNumEntries(database,TABLE_LOCATION);

        database.close();
        return amountLocation;
    }

    public int getProductsSearch(){
        SQLiteDatabase db = this.getReadableDatabase();
        int amountProducts = (int) DatabaseUtils.queryNumEntries(db,TABLE_MAKEUP);

        db.close();
        return amountProducts;
    }
}


