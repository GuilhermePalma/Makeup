package com.example.maquiagem.view.activities;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.maquiagem.R;
import com.example.maquiagem.controller.DataBaseHelper;
import com.example.maquiagem.model.Makeup;
import com.example.maquiagem.model.SearchInternet;
import com.example.maquiagem.model.SerializationData;
import com.example.maquiagem.view.PersonAlertDialogs;
import com.example.maquiagem.view.fragments.FragmentListMakeup;
import com.example.maquiagem.view.fragments.FragmentSearchMakeup;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String URL_API = "http://makeup-api.herokuapp.com/api/v1/products.json?";
    private static final String RATING_GREATER = "rating_greater_than";
    private static final int MAX_RESULT_CATALOG = 60;

    private final int OPTION_PROFILE = R.id.option_profile;
    private final int OPTION_CONFIG = R.id.option_config;
    private final int OPTION_DATA_USED = R.id.option_dataUsed;
    private final int OPTION_EXIT = R.id.option_exit;
    private final int OPTION_SEARCH_MAKEUP = R.id.option_searchMakeup;
    private final int OPTION_HISTORIC_MAKEUP = R.id.option_historicMakeup;
    private final int OPTION_FAVORITE_MAKEUPS = R.id.option_favoriteMakeup;
    private final int OPTION_MORE_FAVORITES = R.id.option_moreFavorites;
    private final int OPTION_HOME_MAKEUP = R.id.option_homeMakeup;
    private final int OPTION_LOCATION = R.id.option_location;
    private final int OPTION_SENSOR = R.id.option_sensor;
    private final int POSITION_TOP_MENU_SEARCH = 0;
    private final int POSITION_TOP_MENU_HOME = 1;
    private final int OPTION_MENU_TOP_HOME = R.id.topMenu_home;
    private final int OPTION_MENU_TOP_SEARCH = R.id.search_option;

    private LinearLayout layout_loading;
    private FrameLayout frame_fragment;
    private Toolbar toolbar;
    private Menu menu;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private List<Makeup> listMakeup;

    private FragmentListMakeup fragmentListMakeup;
    private SerializationData serializationData;
    private PersonAlertDialogs dialogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instanceItems();

        // Criação da ToolBar
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        setUpDrawer();
        listenerNavigation();

        // Define o Item que será inicialmente Selecionado
        navigationView.getMenu().findItem(OPTION_HOME_MAKEUP).setChecked(true);
        navigationView.getMenu().findItem(OPTION_HOME_MAKEUP).setCheckable(true);

        // Mostra o Conteudo da Tela Inicial (Catalogo)
        asyncTask(OPTION_HOME_MAKEUP);
        setUpListFragment(listMakeup, FragmentListMakeup.TYPE_CATALOG);
    }

    /**
     * Instancia os Itens que serão Usados
     */
    private void instanceItems() {
        toolbar = findViewById(R.id.toolBar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        layout_loading = findViewById(R.id.layout_loadingData);
        frame_fragment = findViewById(R.id.frame_forFragment);

        fragmentListMakeup = new FragmentListMakeup();
        listMakeup = new ArrayList<>();
        serializationData = new SerializationData(this);
        dialogs = new PersonAlertDialogs(this);
    }

    /**
     * Cria as Opções do Menu Superior
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        this.menu = menu;
        return true;
    }

    /**
     * Configura o Menu Lateral
     */
    private void setUpDrawer() {
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawer,
                toolbar, R.string.open_menu, R.string.close_menu);
        drawer.addDrawerListener(drawerToggle);

        // Sincroniza o Estado do Icone
        drawerToggle.syncState();
    }

    /**
     * Seleção dos Itens "Pesquisar" ou "Inicio" do Menu Superior Direito
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case OPTION_MENU_TOP_SEARCH:
                //Desseleciona o Menu Lateral e Altera a Visibilidade do Icone Superior
                unselectedItemsMenu();
                menu.getItem(POSITION_TOP_MENU_SEARCH).setVisible(false);
                menu.getItem(POSITION_TOP_MENU_HOME).setVisible(true);

                // Instancia o Fragment e Seleciona sua opção no Menu Lateral
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_forFragment,
                        new FragmentSearchMakeup(this, this)).commit();
                navigationView.getMenu().findItem(OPTION_SEARCH_MAKEUP).setChecked(true);
                navigationView.getMenu().findItem(OPTION_SEARCH_MAKEUP).setCheckable(true);

                return true;
            case OPTION_MENU_TOP_HOME:
                //Desseleciona o Menu Lateral e Altera a Visibilidade do Icone Superior
                unselectedItemsMenu();
                menu.getItem(POSITION_TOP_MENU_SEARCH).setVisible(true);
                menu.getItem(POSITION_TOP_MENU_HOME).setVisible(false);

                // Instancia o Fragment e Seleciona sua opção no Menu Latera
                asyncTask(OPTION_HOME_MAKEUP);
                setUpListFragment(listMakeup, FragmentListMakeup.TYPE_CATALOG);

                navigationView.getMenu().findItem(OPTION_HOME_MAKEUP).setChecked(true);
                navigationView.getMenu().findItem(OPTION_HOME_MAKEUP).setCheckable(true);

                return true;
            default:
                return false;
        }
    }

    /**
     * Remove a Seleção de todos os Itens do Menu Lateral
     */
    private void unselectedItemsMenu() {
        int sizeMenu = navigationView.getMenu().size();
        for (int i = 0; i < sizeMenu; i++) {

            MenuItem menuItem = navigationView.getMenu().getItem(i);

            if (menuItem.hasSubMenu()) {

                // Caso tenha um Sub-menu, acessa eles para retirar a seleção
                for (int u = 0; u < menuItem.getSubMenu().size(); u++) {
                    menuItem.getSubMenu().getItem(u).setChecked(false);
                    menuItem.getSubMenu().getItem(u).setCheckable(false);
                }
            } else {
                navigationView.getMenu().getItem(i).setChecked(false);
                navigationView.getMenu().getItem(i).setCheckable(false);
            }
        }
    }

    /**
     * Trata o Clique nos Itens do Menu Lateral
     */
    private void listenerNavigation() {
        // Trata o Clique nos Itens
        navigationView.setNavigationItemSelectedListener(item -> {

            // Reinica a List Makeup
            listMakeup.clear();

            int id_item = item.getItemId();

            // Caso seja o Item de Localização ---> Valida Conexão com Internet e GPS
            if (id_item == OPTION_LOCATION) {
                if (!connectionAvailable()) {
                    dialogs.message(getString(R.string.title_noConnection),
                            getString(R.string.error_connection)).show();

                    drawer.closeDrawer(GravityCompat.START);
                    return false;
                } else if (!gpsAvailable()) {
                    dialogs.message(getString(R.string.title_noGps),
                            getString(R.string.error_noGps)).show();

                    drawer.closeDrawer(GravityCompat.START);
                    return false;
                }
            }

            // Deseleciona o Item seleciona e Coloca a Seleção do Item Clicado
            unselectedItemsMenu();
            item.setChecked(true);
            item.setCheckable(true);

            // SwitchCase para verificar qual item foi selecionado
            switch (id_item) {
                case OPTION_PROFILE:
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    break;

                case OPTION_HOME_MAKEUP:
                    asyncTask(OPTION_HOME_MAKEUP);
                    setUpListFragment(listMakeup, FragmentListMakeup.TYPE_CATALOG);

                    //Altera o Icone Superior (Icone Search)
                    menu.getItem(POSITION_TOP_MENU_SEARCH).setVisible(true);
                    menu.getItem(POSITION_TOP_MENU_HOME).setVisible(false);

                    break;
                case OPTION_FAVORITE_MAKEUPS:
                    // todo: Implementar API_LOCAL
                    // Verificar a Internet: Se disponivel acessa api_local se não obtem do BD
                    String select_favorite = String.format("SELECT * FROM %1$s WHERE %2$s=1",
                            DataBaseHelper.TABLE_MAKEUP, DataBaseHelper.IS_FAVORITE_MAKEUP);

                    listMakeup.addAll(serializationData.serializationSelectMakeup(select_favorite));
                    setUpListFragment(listMakeup, FragmentListMakeup.TYPE_FAVORITE);
                    break;

                case OPTION_SEARCH_MAKEUP:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_forFragment,
                            new FragmentSearchMakeup(this, this)).commit();

                    //Altera o Icone Superior (Icone Home)
                    menu.getItem(POSITION_TOP_MENU_SEARCH).setVisible(false);
                    menu.getItem(POSITION_TOP_MENU_HOME).setVisible(true);
                    break;

                case OPTION_MORE_FAVORITES:
                    // Todo: Implementar metodo da API_local
                    String select_popular = String.format("SELECT * FROM %s",
                            DataBaseHelper.TABLE_MAKEUP);
                    listMakeup = serializationData.serializationSelectMakeup(select_popular);
                    setUpListFragment(listMakeup, FragmentListMakeup.TYPE_MORE_LIKED);
                    break;

                case OPTION_HISTORIC_MAKEUP:
                    String select_historic = String.format("SELECT * FROM %s",
                            DataBaseHelper.TABLE_MAKEUP);
                    listMakeup = serializationData.serializationSelectMakeup(select_historic);
                    setUpListFragment(listMakeup, FragmentListMakeup.TYPE_HISTORIC);
                    break;

                case OPTION_LOCATION:
                    // Já foi Validado Internet e GPS
                    startActivity(new Intent(this, LocationActivity.class));
                    break;

                case OPTION_SENSOR:
                    startActivity(new Intent(MainActivity.this,
                            SensorActivity.class));
                    break;

                case OPTION_CONFIG:
                    startActivity(new Intent(MainActivity.this,
                            ConfigurationActivity.class));
                    break;

                case OPTION_DATA_USED:
                    startActivity(new Intent(MainActivity.this,
                            DataApplicationActivity.class));
                    break;

                case OPTION_EXIT:
                    // todo: deslogar ---> Implementar depois do Cadastro/Login
                    break;

                default:
                    return true;
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    /**
     * Configura instancias dos Fragment de Lista (Catalogo, Favoritas, Historico)
     */
    private void setUpListFragment(List<Makeup> makeups, String type_fragment) {
        if (makeups == null) {
            dialogs.message(getString(R.string.title_noData),
                    getString(R.string.error_tableEmpty)).show();
        } else {

            fragmentListMakeup = FragmentListMakeup.newInstance(this, listMakeup,
                    type_fragment);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_forFragment,
                    fragmentListMakeup).commit();
        }
    }

    // todo: colocar verificação da Internet e GPS em uma classe separada (Util..)
    // Verifica se a Internet está disponivel
    private boolean connectionAvailable() {
        ConnectivityManager connectionManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo;

        // Valida se o serviço de Internet está ativo
        if (connectionManager != null) {
            networkInfo = connectionManager.getActiveNetworkInfo();

            // Valida se existe conexão ativa
            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            } else {
                // Erro na Conexão
                Log.e("NO CONECTED", "\n Erro na conexão com a Internet" +
                        "\nConexão: " + networkInfo);
                return false;
            }

        } else {
            Log.e("NO SERVICE", "\n Erro no serviço de Internet" +
                    "\nServiço: " + connectionManager);
            return false;
        }
    }

    // Verifica se o GPS está disponivel
    private boolean gpsAvailable() {
        // Gerencia os serviços de Localziação
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Verifica se o GPS está ativo e Habilitado para Usar
        try {
            return service.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (IllegalArgumentException exception) {
            Log.e("ERROR SERVICE GPS", "Não foi foi possivel obter o " +
                    "Serviço de GPS\n" + exception);
            exception.printStackTrace();
            return false;
        }
    }

    /**
     * Este metodo realiza uma busca de forma Assincrona nas APIs (Local ou externa). A partir de
     * um ID do Menu Lateral, configura os dados obtidos e quantidade de resultados
     * <p>
     * Nesse Metodo há tratamento de Possiveis exceções e retornos em caso de Erro.
     */
    private void asyncTask(int option_search) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        // Carrega o Circular Progress Indicator
        layout_loading.setVisibility(View.VISIBLE);
        frame_fragment.setVisibility(View.GONE);

        // Executa as Operações em Backgorund
        executorService.execute(() -> {
            // Obtem o JSON e Quantidade de Resultados
            String json;
            int quantity_result;
            switch (option_search) {
                case OPTION_HOME_MAKEUP:
                    json = getJsonCatalog();
                    quantity_result = MAX_RESULT_CATALOG;
                    break;

                case OPTION_FAVORITE_MAKEUPS:
                    json = getJsonFavorite();
                    quantity_result = SerializationData.ALL_ITEMS_JSON;
                    break;

                case OPTION_MORE_FAVORITES:
                    json = getJsonMoreLiked();
                    quantity_result = SerializationData.DEFAULT_QUANTITY_RESULT;
                    break;
                default:
                    json = "";
                    quantity_result = 0;
                    break;
            }

            // Apresenta o Resultado Background
            runOnUiThread(() -> {
                // Caso o Json seja Invalido ou Vazio
                if (json == null || json.equals("")) {
                    showError();
                    return;
                }

                try {
                    // Serializa O JSON ---> Retorno = List<Makeup> ou null
                    listMakeup.addAll(new SerializationData(getApplicationContext()).
                            serializationJsonMakeup(json, quantity_result));

                    if (listMakeup == null || listMakeup.isEmpty()) {
                        showError();
                    } else {
                        // Carrega o Fragment
                        layout_loading.setVisibility(View.GONE);
                        frame_fragment.setVisibility(View.VISIBLE);
                    }
                } catch (Exception ex) {
                    showError();
                    Log.e("Error", "Erro ao adicionar o JSON Serializado à Lista. " + ex);
                }
            });
        });
    }

    /**
     * Obtem Maquiagens na API Externa (makeup-api)
     */
    public String getJsonCatalog() {
        Uri build_uriAPI = Uri.parse(URL_API).buildUpon()
                .appendQueryParameter(RATING_GREATER, "4.7").build();
        return SearchInternet.searchByUrl(build_uriAPI.toString(), "GET");
    }

    /**
     * Obtem uma String Array das Maquiagens Favoritas do Usuario. Busca na API Local(Makeup_API)
     */
    public String getJsonFavorite() {
        // todo: Implementar API_Interna
        // Obtem os Dados do Usuario no Banco de Dados
        // Envia uma solicitação à Makeup_API
        // Obtem o JSON
        return "In Develop";
    }

    /**
     * Obtem uma String Array das maquiagens mais favoritadas. Busca na API Local(Makeup_API)
     */
    public String getJsonMoreLiked() {
        // todo: Implementar API_Interna
        // Envia uma solicitação à Makeup_API
        // Obtem o JSON
        return "In Develop";
    }

    /**
     * Mensagem de Erro dos dados não encontrados
     */
    public void showError() {
        layout_loading.setVisibility(View.GONE);
        frame_fragment.setVisibility(View.GONE);

        dialogs.message(getString(R.string.title_noData),
                getString(R.string.error_tableEmpty)).show();
    }

    /**
     * Caso clique no botão Voltar, verifica se está voltando do Menu Lateral ou Botão "Voltar"
     */
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}