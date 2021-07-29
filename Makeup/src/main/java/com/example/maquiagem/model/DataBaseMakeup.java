package com.example.maquiagem.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
    private static final String RETURN_LOCATION = "return_location";
    private static final String ADDRESS = "address";
    private static final String DISTRICT = "bairro";
    private static final String STATE = "state";
    private static final String CITY = "city";
    private static final String NUMBER = "number_address";
    private static final String POSTAL_CODE = "postal_code";
    private static final String COUNTY = "country";
    private static final String COUNTRY_CODE = "country_code";

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
                        ADDRESS + " text, " +
                        DISTRICT + " text, " +
                        CITY + " text, " +
                        STATE + " text, " +
                        NUMBER + " text, " +
                        POSTAL_CODE + " text, " +
                        COUNTY + " text, " +
                        COUNTRY_CODE + " text, " +
                        RETURN_LOCATION + " text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int newI) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAKEUP);
        this.onCreate(db);
    }

    public int amountMakeupSearch(){
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db,TABLE_MAKEUP);
    }

    public int amountLocation(){
        SQLiteDatabase database = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(database,TABLE_LOCATION);
    }

    // Recupera a quantidade de posições certas
    public int amountCorrectLocation(){
        // Abre uma conexão com o Banco de Dados para Leitura (Select)
        SQLiteDatabase db = this.getReadableDatabase();
        // Resultado Pesquisado
        String correct = "correct_position";

        // Retorna a quantidade do select com a palavra acima
        return (int) DatabaseUtils.queryNumEntries(db, TABLE_LOCATION,
                RETURN_LOCATION + "='" + correct + "'");
    }

    // Recupera a quantidade de posições erradas
    public int amountWrongLocation(){
        SQLiteDatabase db = this.getReadableDatabase();
        String wrong = "wrong_position";
        return (int) DatabaseUtils.queryNumEntries(db, TABLE_LOCATION,
                RETURN_LOCATION + "='" + wrong + "'");
    }


    // Inserção de Dados no Banco de Dados ---> Usa a classe model/Makeup
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
    }

    // Inserção de Dados no Banco de Dados ---> Usa a classe model/Location
    public void insertLocation(Location location){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID_LOCATION, location.getLastId());
        values.put(ADDRESS, location.getAddress());
        values.put(DISTRICT, location.getDistrict());
        values.put(CITY, location.getCity());
        values.put(STATE, location.getState());
        values.put(NUMBER, location.getNumber());
        values.put(POSTAL_CODE, location.getPostalCode());
        values.put(COUNTY, location.getCountryName());
        values.put(COUNTRY_CODE, location.getCountryCode());
        database.insert(TABLE_LOCATION, null, values);
    }

    // Insere se a busca da Localização deu certa ou não
    public void insertTypeLocation(int idLocation, boolean returnLocation){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (returnLocation) {
            values.put(RETURN_LOCATION, "correct_position");
        } else {
            values.put(RETURN_LOCATION, "wrong_position");
        }
        db.update(TABLE_LOCATION, values, ID_LOCATION + "='" + idLocation + "'", null );
    }


    // Verifica se ja existe os produtos buscados
    public boolean existsInMakeup(String type, String brand){
        SQLiteDatabase database = this.getReadableDatabase();

        // Conta cada item com as variavesis recebidas
        int amountRecords = (int) DatabaseUtils.queryNumEntries(database,TABLE_MAKEUP,
                TYPE + "='" + type + "' AND " + BRAND + "='" + brand+ "'");

        // Caso tenha 1 ou mais registros ---> True
        return amountRecords != 0;
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
        return cursor;
    }


    // Limpa toda a Tabela do Banco de Dados
    public void clearTableMakeup() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_MAKEUP);
    }

    // Apaga todos os dados da Tabela Localização
    public void clearTableLocation(){
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DELETE FROM " + TABLE_LOCATION);
    }
}


