package com.example.maquiagem.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.maquiagem.model.entity.Location;
import com.example.maquiagem.model.entity.Makeup;
import com.example.maquiagem.model.entity.User;

public class DataBaseHelper extends SQLiteOpenHelper {

    //Definição das Constantes Usadas
    public static final String DATABASE = "makeupDB";
    public static final int VERSION_DATABASE = 1;
    public static final int TRUE = 1;
    public static final int FALSE = 0;

    // Constantes das Colunas da Tabela "Makeups"
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

    // Constantes das Colunas da Tabela "Location"
    public static final String TABLE_LOCATION = "location";
    public static final String ID_LOCATION = "id";
    public static final String RETURN_LOCATION = "return_location";
    public static final String ADDRESS = "address";
    public static final String DISTRICT = "bairro";
    public static final String STATE = "state";
    public static final String CITY = "city";
    public static final String NUMBER = "number_address";
    public static final String POSTAL_CODE = "postal_code";
    public static final String COUNTY = "country";
    public static final String COUNTRY_CODE = "country_code";

    // Constantes das Colunas da Tabela "User"
    public static final String TABLE_USER = "user";
    public static final String TOKEN_USER = "token_user";
    public static final String NAME_USER = "name";
    public static final String NICKNAME_USER = "nickname";
    public static final String EMAIL_USER = "email";
    public static final String PASSWORD_USER = "password";
    public static final String IDIOM_USER = "idiom";
    public static final String THEME_IS_NIGHT_USER = "theme_is_night";

    /**
     * Construtor da classe {@link DataBaseHelper} herdado da classe {@link SQLiteOpenHelper}.
     * <p>
     * Obtem um {@link Context}, define o Nome do Banco de Dados e Sua Versão
     */
    public DataBaseHelper(Context context) {
        super(context, DATABASE, null, VERSION_DATABASE);
    }

    /**
     * Criação das Tabelas do Banco de Dados (Metodo Sobrescrito Herdado)
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + TABLE_MAKEUP + " (" +
                        ID_MAKEUP + " INTEGER PRIMARY KEY, " +
                        BRAND_MAKEUP + " text, " +
                        NAME_MAKEUP + " text, " +
                        TYPE_MAKEUP + " text, " +
                        PRICE_MAKEUP + " text, " +
                        CURRENCY_MAKEUP + " varchar(5), " +
                        DESCRIPTION_MAKEUP + " text, " +
                        URL_IMAGE_MAKEUP + " text, " +
                        IS_FAVORITE_MAKEUP + " integer)"
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
                        RETURN_LOCATION + " integer)"
        );
        db.execSQL(
                "create table " + TABLE_USER + " (" +
                        TOKEN_USER + " text, " +
                        NICKNAME_USER + " text, " +
                        PASSWORD_USER + " text, " +
                        NAME_USER + " text, " +
                        IDIOM_USER + " text, " +
                        THEME_IS_NIGHT_USER + " integer, " +
                        EMAIL_USER + " text)"
        );
    }

    /**
     * Metodo Herdade de Atualização do Banco de Dados. Apaga e cria novamente as Tabelas
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int newI) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAKEUP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        this.onCreate(db);
    }

    /**
     * Insere um Usuario no Banco de Dados
     */
    public void insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TOKEN_USER, user.getToken_user());
        values.put(NICKNAME_USER, user.getNickname());
        values.put(PASSWORD_USER, user.getPassword());
        values.put(NAME_USER, user.getName());
        values.put(EMAIL_USER, user.getEmail());
        values.put(IDIOM_USER, user.getIdiom());
        values.put(THEME_IS_NIGHT_USER, user.isTheme_is_night() ? TRUE : FALSE);

        db.insert(TABLE_USER, null, values);
    }

    /**
     * Inserção de Dados no Banco de Dados ---> Usa a classe model/Makeup
     */
    public void insertMakeup(Makeup makeup) {
        if (existsInMakeup(makeup.getType(), makeup.getBrand())) {
            updateFavoriteMakeup(makeup);
            return;
        }

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
        values.put(IS_FAVORITE_MAKEUP, makeup.isFavorite() ? TRUE : FALSE);

        db.insert(TABLE_MAKEUP, null, values);
    }

    /**
     * Inserção de Dados no Banco de Dados ---> Usa a classe model/Location
     */
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

    /**
     * Insere se a busca da Localização é correta
     */
    public void insertTypeLocation(int idLocation, boolean returnLocation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RETURN_LOCATION, returnLocation ? TRUE : FALSE);

        db.update(TABLE_LOCATION, values, ID_LOCATION + "= ?",
                new String[]{String.valueOf(idLocation)});
    }

    /**
     * Atualiza uma Makeup Pesquisada para Favorita
     */
    public void updateFavoriteMakeup(Makeup makeup) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Altera o valor de ser ou não favorito a maquiagem
        values.put(IS_FAVORITE_MAKEUP, makeup.isFavorite() ? TRUE : FALSE);

        String whereClause = String.format("%1$s= ? AND %2$s= ? AND %3$s= ?",
                NAME_MAKEUP, BRAND_MAKEUP, TYPE_MAKEUP);
        String[] valuesWhere = new String[]{
                makeup.getName(), makeup.getBrand(), makeup.getType()
        };

        database.update(TABLE_MAKEUP, values, whereClause, valuesWhere);
    }

    /**
     * Atualiza os dados do Usuario
     */
    public void updateUser(User user) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TOKEN_USER, user.getToken_user());
        values.put(NICKNAME_USER, user.getNickname());
        values.put(PASSWORD_USER, user.getPassword());
        values.put(NAME_USER, user.getName());
        values.put(EMAIL_USER, user.getEmail());
        values.put(IDIOM_USER, user.getIdiom());
        values.put(THEME_IS_NIGHT_USER, user.isTheme_is_night() ? TRUE : FALSE);

        database.update(TABLE_USER, values, null, null);
    }

    /**
     * Recupera o Total de Maquiagens do Banco de Dados
     */
    public int amountMakeupSearch() {
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, TABLE_MAKEUP);
    }

    /**
     * Recupera o Total de Posições do Banco de Dados
     */
    public int amountLocation() {
        SQLiteDatabase database = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(database, TABLE_LOCATION);
    }

    /**
     * Recupera a quantidade de posições CERTAS
     */
    public int amountCorrectLocation() {
        // Abre uma conexão com o Banco de Dados para Leitura (Select)
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, TABLE_LOCATION,
                RETURN_LOCATION + "= ?", new String[]{String.valueOf(TRUE)});
    }

    /**
     * Recupera a quantidade de posições ERRADAS
     */
    public int amountWrongLocation() {
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, TABLE_LOCATION,
                RETURN_LOCATION + "= ?", new String[]{String.valueOf(FALSE)});
    }

    /**
     * Verifica se existe o Produto buscados
     */
    public boolean existsInMakeup(String type, String brand) {
        SQLiteDatabase database = this.getReadableDatabase();

        String whereClause = String.format("%1$s= ? AND %2$s= ?", TYPE_MAKEUP, BRAND_MAKEUP);

        // Conta cada item com as variavesis recebidas
        int amountRecords = (int) DatabaseUtils.queryNumEntries(database, TABLE_MAKEUP,
                whereClause, new String[]{type, brand});

        // Caso tenha 1 ou mais registros ---> True
        return amountRecords != 0;
    }

    /**
     * Seleciona um Produto atravez de um SELECT passado
     */
    public Cursor selectMakeup(String select) {
        SQLiteDatabase database = this.getReadableDatabase();
        return database.rawQuery(select, null);
    }

    /**
     * Retorna o resgistro do Usaurio já serializado
     */
    public User selectUser(Context context) {
        Cursor cursor = null;
        try {
            SQLiteDatabase database = getReadableDatabase();
            cursor = database.rawQuery(String.format("SELECT * FROM %1$s", TABLE_USER), null);

            if (cursor != null && cursor.moveToFirst()) {
                User user = new User();
                user.setContext(context);
                user.setNickname(cursor.getString(cursor.getColumnIndexOrThrow("nickname")));
                user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(PASSWORD_USER)));
                user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(EMAIL_USER)));
                user.setName(cursor.getString(cursor.getColumnIndexOrThrow(NAME_USER)));
                user.setIdiom(cursor.getString(cursor.getColumnIndexOrThrow(IDIOM_USER)));
                int value_theme = cursor.getInt(cursor.getColumnIndexOrThrow(THEME_IS_NIGHT_USER));
                user.setTheme_is_night(value_theme == TRUE);
                user.setToken_user(cursor.getString(cursor.getColumnIndexOrThrow(TOKEN_USER)));

                return user;
            } else return null;
        } catch (Exception ex) {
            Log.e("Exception", "Erro ao Obter o Usuario do Banco de Dados. Exceção: " + ex);
            ex.printStackTrace();
            return null;
        } finally {
            if (cursor != null) cursor.close();
        }
    }

    /**
     * Apaga todos os Usuarios
     */
    public void deleteAllUsers() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_USER, "1", null);
    }

    /**
     * Apaga todos os Dados da Tabela
     */
    public void clearTables() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_MAKEUP, "1", null);
        database.delete(TABLE_LOCATION, "1", null);
        database.delete(TABLE_USER, "1", null);
    }

}


