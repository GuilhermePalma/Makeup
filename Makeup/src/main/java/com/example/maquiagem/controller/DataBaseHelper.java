package com.example.maquiagem.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.maquiagem.model.Location;
import com.example.maquiagem.model.Makeup;
import com.example.maquiagem.model.User;

public class DataBaseHelper extends SQLiteOpenHelper {

    //Definição das Constantes usadas
    public static final String DATABASE = "makeupDB";
    public static final int VERSION_DATABASE = 1;
    public static final String TABLE_MAKEUP = "makeups";
    public static final String ID_MAKEUP = "id";
    public static final String BRAND_MAKEUP = "brand";
    public static final String NAME_MAKEUP = "name";
    public static final String TYPE_MAKEUP = "type";
    public static final String PRICE_MAKEUP = "price";
    public static final String CURRENCY_MAKEUP = "currency";
    public static final String DESCRIPTION_MAKEUP = "description";
    public static final String URL_IMAGE_MAKEUP = "image";
    public static final String IS_FAVORITE_MAKEUP = "is_favorite";

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

    private static final String TABLE_USER = "user";
    private static final String NAME_USER = "name";
    private static final String NICKNAME_USER = "nickname";
    private static final String PASSWORD_USER = "password";
    private static final String EMAIL_USER = "email";

    public DataBaseHelper(Context context) {
        super(context, DATABASE, null, VERSION_DATABASE);
    }

    // Crição do Banco de Dados
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + TABLE_MAKEUP + " (" +
                        ID_MAKEUP + " integer PRIMARY KEY, " +
                        BRAND_MAKEUP + " text, " +
                        NAME_MAKEUP + " text, " +
                        TYPE_MAKEUP + " text, " +
                        PRICE_MAKEUP + " text, " +
                        CURRENCY_MAKEUP + " varchar(5), " +
                        DESCRIPTION_MAKEUP + " text, " +
                        URL_IMAGE_MAKEUP + " text, " +
                        IS_FAVORITE_MAKEUP + " text)"
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
        db.execSQL(
                "create table " + TABLE_USER + " (" +
                        NICKNAME_USER + " text, " +
                        PASSWORD_USER + " text, " +
                        NAME_USER + " text, " +
                        EMAIL_USER + " text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int newI) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAKEUP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        this.onCreate(db);
    }

    // Recupera o Total de Maquiagens do Banco de Dados
    public int amountMakeupSearch() {
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, TABLE_MAKEUP);
    }

    // Recupera o Total de Posições do Banco de Dados
    public int amountLocation() {
        SQLiteDatabase database = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(database, TABLE_LOCATION);
    }

    // Recupera a quantidade de posições certas
    public int amountCorrectLocation() {
        // Abre uma conexão com o Banco de Dados para Leitura (Select)
        SQLiteDatabase db = this.getReadableDatabase();
        // Resultado Pesquisado
        String correct = "correct_position";

        // Retorna a quantidade do select com a palavra acima
        return (int) DatabaseUtils.queryNumEntries(db, TABLE_LOCATION,
                RETURN_LOCATION + "='" + correct + "'");
    }

    // Recupera a quantidade de posições erradas
    public int amountWrongLocation() {
        SQLiteDatabase db = this.getReadableDatabase();
        String wrong = "wrong_position";
        return (int) DatabaseUtils.queryNumEntries(db, TABLE_LOCATION,
                RETURN_LOCATION + "='" + wrong + "'");
    }


    // Inserção de Dados no Banco de Dados ---> Usa a classe model/Makeup
    public void insertMakeup(Makeup makeup) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ID_MAKEUP, makeup.getId());
        values.put(BRAND_MAKEUP, makeup.getBrand());
        values.put(NAME_MAKEUP, makeup.getName());
        values.put(TYPE_MAKEUP, makeup.getType());
        values.put(PRICE_MAKEUP, makeup.getPrice());
        values.put(CURRENCY_MAKEUP, makeup.getCurrency());
        values.put(DESCRIPTION_MAKEUP, makeup.getDescription());
        values.put(URL_IMAGE_MAKEUP, makeup.getUrlImage());
        values.put(IS_FAVORITE_MAKEUP, makeup.isFavorite());

        db.insert(TABLE_MAKEUP, null, values);
    }

    // Inserção de Dados no Banco de Dados ---> Usa a classe model/Location
    public void insertLocation(Location location) {
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
    public void insertTypeLocation(int idLocation, boolean returnLocation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (returnLocation) {
            values.put(RETURN_LOCATION, "correct_position");
        } else {
            values.put(RETURN_LOCATION, "wrong_position");
        }
        db.update(TABLE_LOCATION, values, ID_LOCATION + "='" + idLocation + "'", null);
    }

    public void updateFavoriteMakeup(Makeup makeup) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Altera o valor de ser ou não favorito a maquiagem
        values.put(IS_FAVORITE_MAKEUP, String.valueOf(makeup.isFavorite()));

        String whereClause = String.format("%1$s='%2$s' AND %3$s='%4$s' AND %5$s='%6$s'",
                NAME_MAKEUP, makeup.getName(),
                BRAND_MAKEUP, makeup.getBrand(),
                TYPE_MAKEUP, makeup.getType());

        database.update(TABLE_MAKEUP, values, whereClause, null);
    }

    // Insere um Usuario no Banco de Dados
    public void insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NICKNAME_USER, user.getNickname());
        values.put(PASSWORD_USER, user.getPassword());
        values.put(NAME_USER, user.getName());
        values.put(EMAIL_USER, user.getEmail());

        db.insert(TABLE_USER, null, values);
    }

    // Verifica se existe o Produto buscados
    public boolean existsInMakeup(String type, String brand) {
        SQLiteDatabase database = this.getReadableDatabase();

        // Conta cada item com as variavesis recebidas
        int amountRecords = (int) DatabaseUtils.queryNumEntries(database, TABLE_MAKEUP,
                TYPE_MAKEUP + "='" + type + "' AND " + BRAND_MAKEUP + "='" + brand + "'");

        // Caso tenha 1 ou mais registros ---> True
        return amountRecords != 0;
    }

    // Verifica se ja existe o Usuario Informado
    public boolean existsInUser(User user) {
        SQLiteDatabase database = this.getReadableDatabase();

        int amountRecords = (int) DatabaseUtils.queryNumEntries(database, TABLE_USER,
                NICKNAME_USER + "='" + user.getNickname() + "' AND " +
                        PASSWORD_USER + "='" + user.getPassword() + "' AND " +
                        EMAIL_USER + "='" + user.getEmail() + "' AND " +
                        NAME_USER + "='" + user.getName() + "'");

        // Se tiver 1 unico registro = Retorna true
        return amountRecords == 1;
    }

    // Seleciona um Produto atravez de um SELECT passado
    public Cursor selectMakeup(String select) {
        SQLiteDatabase database = this.getReadableDatabase();
        return database.rawQuery(select, null);
    }

    // Apaga todos os Usuarios
    public void deleteAllUsers() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_USER, "1", null);
    }

    // Apaga todos os Dados da Tabela
    public void clearTables() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_MAKEUP, "1", null);
        database.delete(TABLE_LOCATION, "1", null);
        database.delete(TABLE_USER, "1", null);
    }

}


