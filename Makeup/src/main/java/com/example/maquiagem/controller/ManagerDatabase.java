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

public class ManagerDatabase extends SQLiteOpenHelper {

    //Definição das Constantes Usadas
    public static final String DATABASE = "makeup_sqlite";
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
    public static final String NAME_USER = "name";
    public static final String NICKNAME_USER = "nickname";
    public static final String EMAIL_USER = "email";
    public static final String PASSWORD_USER = "password";
    public static final String IDIOM_USER = "idiom";
    private static final int NOT_INSERTED = -1;
    private static final int NOT_CHANGED = 0;
    private final Context context;


    /**
     * Construtor da classe {@link ManagerDatabase} herdado da classe {@link SQLiteOpenHelper}.
     * <p>
     * Obtem um {@link Context}, define o Nome do Banco de Dados e Sua Versão
     */
    public ManagerDatabase(Context context) {
        super(context, DATABASE, null, VERSION_DATABASE);
        this.context = context;
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
                        NICKNAME_USER + " text, " +
                        PASSWORD_USER + " text, " +
                        NAME_USER + " text, " +
                        IDIOM_USER + " text, " +
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
    public boolean insertUser(User user) {
        SQLiteDatabase database = null;
        boolean isInserted;

        try {
            if (selectUser() != null) deleteAllUsers();

            database = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(NICKNAME_USER, user.getNickname());
            values.put(PASSWORD_USER, user.getPassword());
            values.put(NAME_USER, user.getName());
            values.put(EMAIL_USER, user.getEmail());
            values.put(IDIOM_USER, user.getIdiom());
            isInserted = database.insert(TABLE_USER, null, values) != NOT_INSERTED;
        } catch (Exception ex) {
            Log.e("Error Database", "Erro ao Inserir um novo Usuario. Exceção: " + ex);
            ex.printStackTrace();
            isInserted = false;
        } finally {
            if (database != null) database.close();
        }

        return isInserted;
    }

    /**
     * Inserção de Dados no Banco de Dados ---> Usa a classe model/Makeup
     */
    public boolean insertMakeup(Makeup makeup) {
        SQLiteDatabase database = null;
        boolean isInserted;

        try {
            if (existsInMakeup(makeup))
                return setFavoriteMakeup(makeup);

            database = this.getWritableDatabase();

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

            isInserted = database.insert(TABLE_MAKEUP, null, values) != NOT_INSERTED;
        } catch (Exception ex) {
            Log.e("Error Database", "Erro ao Inserir uma nova Maquiagem. Exceção: " + ex);
            ex.printStackTrace();
            isInserted = false;
        } finally {
            if (database != null) database.close();
        }

        return isInserted;
    }

    /**
     * Inserção de Dados no Banco de Dados ---> Usa a classe model/Location
     */
    public boolean insertLocation(Location location) {
        SQLiteDatabase database = null;
        boolean isInserted;

        try {

            database = this.getWritableDatabase();

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

            isInserted = database.insert(TABLE_LOCATION, null, values) != NOT_INSERTED;
        } catch (Exception ex) {
            Log.e("Error Database", "Erro ao Inserir uma nova Localização. Exceção: " + ex);
            ex.printStackTrace();
            isInserted = false;
        } finally {
            if (database != null) database.close();
        }

        return isInserted;
    }

    /**
     * Insere se a busca da Localização é correta
     */
    public boolean setCorrectLocation(int idLocation, boolean returnLocation) {
        SQLiteDatabase database = null;
        boolean isUpdated;

        try {
            database = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(RETURN_LOCATION, returnLocation ? TRUE : FALSE);

            isUpdated = database.update(TABLE_LOCATION, values, ID_LOCATION + "= ?",
                    new String[]{String.valueOf(idLocation)}) != NOT_CHANGED;
        } catch (Exception ex) {
            Log.e("Error Database", "Erro ao Atualizar a Localização. Exceção: " + ex);
            ex.printStackTrace();
            isUpdated = false;
        } finally {
            if (database != null) database.close();
        }

        return isUpdated;
    }

    /**
     * Atualiza uma Makeup Pesquisada para Favorita
     */
    public boolean setFavoriteMakeup(Makeup makeup) {
        SQLiteDatabase database = null;
        boolean isUpdated;

        try {
            database = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(IS_FAVORITE_MAKEUP, makeup.isFavorite() ? TRUE : FALSE);

            String whereClause = String.format("%1$s= ? AND %2$s= ? AND %3$s= ?", NAME_MAKEUP,
                    BRAND_MAKEUP, TYPE_MAKEUP);
            String[] valuesWhere = new String[]{makeup.getName(), makeup.getBrand(), makeup.getType()};

            isUpdated = database.update(TABLE_MAKEUP, values, whereClause, valuesWhere) != NOT_CHANGED;
        } catch (Exception ex) {
            Log.e("Error Database", "Erro ao Favoritar a Maquiagem. Exceção: " + ex);
            ex.printStackTrace();
            isUpdated = false;
        } finally {
            if (database != null) database.close();
        }

        return isUpdated;
    }

    /**
     * Atualiza os dados do Usuario
     */
    public boolean updateUser(User user) {
        SQLiteDatabase database = null;
        boolean isUpdated;

        try {
            database = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(NICKNAME_USER, user.getNickname());
            values.put(PASSWORD_USER, user.getPassword());
            values.put(NAME_USER, user.getName());
            values.put(EMAIL_USER, user.getEmail());
            values.put(IDIOM_USER, user.getIdiom());

            isUpdated = database.update(TABLE_USER, values, null, null) != NOT_CHANGED;
        } catch (Exception ex) {
            Log.e("Error Database", "Erro ao Atualizar o Usuario. Exceção: " + ex);
            ex.printStackTrace();
            isUpdated = false;
        } finally {
            if (database != null) database.close();
        }

        return isUpdated;
    }

    /**
     * Recupera o Total de Maquiagens do Banco de Dados
     */
    public int amountMakeupSearch() {
        SQLiteDatabase database = null;
        int quantity_makeups;

        try {
            database = this.getReadableDatabase();
            quantity_makeups = (int) DatabaseUtils.queryNumEntries(database, TABLE_MAKEUP);
        } catch (Exception ex) {
            Log.e("Error Database", "Erro ao obter a Quantidade de Maquiagens. Exceção: " + ex);
            ex.printStackTrace();
            quantity_makeups = NOT_CHANGED;
        } finally {
            if (database != null) database.close();
        }

        return quantity_makeups;
    }

    /**
     * Recupera o Total de Posições do Banco de Dados
     */
    public int amountLocation() {
        SQLiteDatabase database = null;
        int quantity_locations;

        try {
            database = this.getReadableDatabase();
            quantity_locations = (int) DatabaseUtils.queryNumEntries(database, TABLE_LOCATION);
        } catch (Exception ex) {
            Log.e("Error Database", "Erro ao Obter a Quantidade de Localizações. Exceção: " + ex);
            ex.printStackTrace();
            quantity_locations = NOT_CHANGED;
        } finally {
            if (database != null) database.close();
        }

        return quantity_locations;
    }

    /**
     * Recupera a quantidade de posições CERTAS
     */
    public int amountCorrectLocation() {
        SQLiteDatabase database = null;
        int quantity_locations;

        try {
            database = this.getReadableDatabase();
            quantity_locations = (int) DatabaseUtils.queryNumEntries(database, TABLE_LOCATION,
                    RETURN_LOCATION + "= ?", new String[]{String.valueOf(TRUE)});
        } catch (Exception ex) {
            Log.e("Error Database", "Erro ao Obter a Quantidade de Localizações Corretas. Exceção: " + ex);
            ex.printStackTrace();
            quantity_locations = NOT_CHANGED;
        } finally {
            if (database != null) database.close();
        }

        return quantity_locations;
    }

    /**
     * Recupera a quantidade de posições ERRADAS
     */
    public int amountWrongLocation() {
        SQLiteDatabase database = null;
        int quantity_locations;

        try {
            database = this.getReadableDatabase();
            quantity_locations = (int) DatabaseUtils.queryNumEntries(database, TABLE_LOCATION,
                    RETURN_LOCATION + "= ?", new String[]{String.valueOf(FALSE)});
        } catch (Exception ex) {
            Log.e("Error Database", "Erro ao Obter a Quantidade de Localizações Erradas. Exceção: " + ex);
            ex.printStackTrace();
            quantity_locations = NOT_CHANGED;
        } finally {
            if (database != null) database.close();
        }

        return quantity_locations;
    }

    /**
     * Verifica se existe o Produto buscados
     */
    private boolean existsInMakeup(Makeup makeup) {
        SQLiteDatabase database = null;
        int quantity_locations;

        try {
            database = this.getReadableDatabase();

            String whereClause = String.format("%1$s= ? AND %2$s= ? AND %3$s= ?", TYPE_MAKEUP,
                    BRAND_MAKEUP, NAME_MAKEUP);
            String[] whereArgs = new String[]{makeup.getType(), makeup.getBrand(), makeup.getName()};

            quantity_locations = (int) DatabaseUtils.queryNumEntries(database, TABLE_MAKEUP,
                    whereClause, whereArgs);
        } catch (Exception ex) {
            Log.e("Error Database", "Erro ao Obter a Quantidade de Maquiagens. Exceção: " + ex);
            ex.printStackTrace();
            quantity_locations = NOT_CHANGED;
        } finally {
            if (database != null) database.close();
        }

        // Caso tenha 1 ou mais registros ---> True
        return quantity_locations > 0;
    }

    /**
     * Seleciona um Produto atravez de um SELECT passado
     */
    public Cursor selectMakeup(String select) {
        SQLiteDatabase database;
        Cursor cursor;
        try {
            database = this.getReadableDatabase();
            cursor = database.rawQuery(select, null);
        } catch (Exception ex) {
            Log.e("Error Database", "Erro ao Selecionar uma Maquiagem. Exceção: " + ex);
            ex.printStackTrace();
            cursor = null;
        }
        return cursor;
    }

    /**
     * Retorna o resgistro do Usaurio já serializado
     */
    public User selectUser() {
        SQLiteDatabase database = null;
        Cursor cursor = null;
        User user;

        try {
            database = this.getReadableDatabase();
            cursor = database.rawQuery(String.format("SELECT * FROM %1$s", TABLE_USER), null);

            if (cursor != null && cursor.moveToFirst()) {
                user = new User();
                user.setContext(context);
                user.setNickname(cursor.getString(cursor.getColumnIndexOrThrow(NICKNAME_USER)));
                user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(PASSWORD_USER)));
                user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(EMAIL_USER)));
                user.setName(cursor.getString(cursor.getColumnIndexOrThrow(NAME_USER)));
                user.setIdiom(cursor.getString(cursor.getColumnIndexOrThrow(IDIOM_USER)));
            } else user = null;
        } catch (Exception ex) {
            Log.e("Error Database", "Erro ao Selecionar o Usuario. Exceção: " + ex);
            ex.printStackTrace();
            user = null;
        } finally {
            if (database != null) database.close();
            if (cursor != null && !cursor.isClosed()) cursor.close();
        }
        return user;
    }

    /**
     * Apaga todos os Usuarios
     */
    private void deleteAllUsers() {
        SQLiteDatabase database = null;
        try {
            database = this.getWritableDatabase();
            database.delete(TABLE_USER, "1", null);
        } catch (Exception ex) {
            Log.e("Error Database", "Erro ao Excluir Todos os Usuario. Exceção: " + ex);
            ex.printStackTrace();
        } finally {
            if (database != null) database.close();
        }
    }

    /**
     * Apaga todos os Dados da Tabela
     */
    public boolean clearTables() {
        SQLiteDatabase database = null;
        boolean isDeleted;

        try {
            database = this.getWritableDatabase();
            isDeleted = database.delete(TABLE_MAKEUP, "1", null) > NOT_CHANGED &&
                    database.delete(TABLE_LOCATION, "1", null) > NOT_CHANGED &&
                    database.delete(TABLE_USER, "1", null) > NOT_CHANGED;
        } catch (Exception ex) {
            Log.e("Error Database", "Erro ao Excluir Todos as Tabelas. Exceção: " + ex);
            ex.printStackTrace();
            isDeleted = false;
        } finally {
            if (database != null) database.close();
        }
        return isDeleted;
    }
}
