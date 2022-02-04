package com.example.maquiagem.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.maquiagem.model.SerializationData;
import com.example.maquiagem.model.entity.Location;
import com.example.maquiagem.model.entity.Makeup;
import com.example.maquiagem.model.entity.User;

import java.util.List;

/**
 * Classe Responsavel pela manipulação do Banco de Dados Local SQLite. Ele herda os metodos da Classe
 * {@link SQLiteOpenHelper}
 */
public class ManagerDatabase extends SQLiteOpenHelper {

    //Definição das Constantes Usadas
    public static final String DATABASE = "makeup_sqlite";
    public static final int VERSION_DATABASE = 1;
    public static final int TRUE = 1;
    public static final int FALSE = 0;
    // Constantes das Colunas da Tabela "Makeups"
    public static final String TABLE_MAKEUP = "makeups";
    public static final String ID_MAKEUP = "id";
    public static final String URL_API_MAKEUP = "url_api";
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
                        URL_API_MAKEUP + " text, " +
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
     * Insere um Usuario no Banco de Dados. Caso já exista um registro salvo, limpa o Banco de Dados.
     *
     * @param user {@link User} que será Inserido no Banco de Dados
     * @return true|false
     */
    public boolean insertUser(User user) {
        SQLiteDatabase database = null;
        boolean isInserted;

        try {
            if (selectUser() != null) clearTables();

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
     * Insere uma Makeup no Banco de Dados
     *
     * @param makeup {@link Makeup} que será inserida no Banco de Dados
     * @return true|false
     */
    public boolean insertMakeup(Makeup makeup) {
        SQLiteDatabase database = null;
        boolean isInserted;

        try {
            if (existsInMakeup(makeup.getId()))
                return setFavoriteMakeup(makeup);

            database = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(ID_MAKEUP, makeup.getId());
            values.put(URL_API_MAKEUP, makeup.getUrlInAPI());
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
     * Insere uma {@link Location} no Banco de Dados
     *
     * @param location {@link Location} que será inserida no Banco de Dados
     * @return true|false
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
     * Define se a {@link Location} que existe no Banco de Dados está ou não correta
     *
     * @param idLocation     ID da {@link Location} que será alterada
     * @param returnLocation Valor boolean, que define se a {@link Location} está ou não correta
     * @return true|false
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
     * Atualiza uma Makeup no Banco de Dados, Favoritando ou Desfavoritando
     *
     * @param makeup {@link Makeup} que será atualizada
     * @return true|false
     */
    public boolean setFavoriteMakeup(Makeup makeup) {
        SQLiteDatabase database = null;
        boolean isUpdated;

        try {
            database = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(IS_FAVORITE_MAKEUP, makeup.isFavorite() ? TRUE : FALSE);

            isUpdated = database.update(TABLE_MAKEUP, values, String.format("%1$s= ?", ID_MAKEUP),
                    new String[]{String.valueOf(makeup.getId())}) != NOT_CHANGED;
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
     * Atualiza os dados de um Usuario no Banco de Dados Local
     *
     * @param user {@link User} que será atualizado
     * @return true|false
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
     * Recupera o Total de {@link Makeup} do Banco de Dados
     *
     * @return int
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
     * Recupera o Total de {@link Location} do Banco de Dados
     *
     * @return int
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
     * Recupera a quantidade de {@link Location} informadas como "Corretas"
     *
     * @return int
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
     * Recupera a quantidade de {@link Location} informadas como "Erradas"
     *
     * @return int
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
     * Verifica se existe a {@link Makeup} informada
     *
     * @param idMakeup ID da {@link Makeup} que será buscada
     * @return true|false
     */
    private boolean existsInMakeup(int idMakeup) {
        SQLiteDatabase database = null;
        int quantity_locations;

        try {
            database = this.getReadableDatabase();
            quantity_locations = (int) DatabaseUtils.queryNumEntries(database, TABLE_MAKEUP,
                    String.format("%1$s= ?", ID_MAKEUP), new String[]{String.valueOf(idMakeup)});
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

    public List<Makeup> getFavoritesMakeup() {
        SQLiteDatabase database;
        List<Makeup> makeupsFavorites;
        try {
            database = this.getReadableDatabase();
            // Realiza um Select e Envia o Resultado para ser Serializado
            String select_favorite = String.format("SELECT * FROM %1$s WHERE %2$s=%3$s",
                    ManagerDatabase.TABLE_MAKEUP, ManagerDatabase.IS_FAVORITE_MAKEUP, TRUE);
            makeupsFavorites = SerializationData.serializationDatabaseMakeup(
                    database.rawQuery(select_favorite, null));
        } catch (Exception ex) {
            Log.e("Error Database", "Erro ao Selecionar uma Maquiagem. Exceção: " + ex);
            ex.printStackTrace();
            makeupsFavorites = null;
        }
        return makeupsFavorites;
    }

    public List<Makeup> getAllMakeups() {
        SQLiteDatabase database;
        List<Makeup> makeupsFavorites;
        try {
            database = this.getReadableDatabase();
            makeupsFavorites = SerializationData.serializationDatabaseMakeup(
                    database.query(TABLE_MAKEUP, null, null, null, null, null, null));
        } catch (Exception ex) {
            Log.e("Error Database", "Erro ao Selecionar uma Maquiagem. Exceção: " + ex);
            ex.printStackTrace();
            makeupsFavorites = null;
        }
        return makeupsFavorites;
    }

    /**
     * Retorna o resgistro de um {@link User} já serializado e Instanciado
     *
     * @return {@link User}
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
     * Apaga todos os Dados das Tabelas do Banco de Dados
     *
     */
    public void clearTables() {
        SQLiteDatabase database = null;
        try {
            database = this.getWritableDatabase();
            if (database != null) {
                database.execSQL(String.format("DELETE FROM %s",TABLE_MAKEUP ));
                database.execSQL(String.format("DELETE FROM %s",TABLE_LOCATION ));
                database.execSQL(String.format("DELETE FROM %s",TABLE_USER ));
            }
        } catch (Exception ex) {
            Log.e("Error Database", "Erro ao Excluir Todos as Tabelas. Exceção: " + ex);
            ex.printStackTrace();
        } finally {
            if (database != null) database.close();
        }
    }
}
