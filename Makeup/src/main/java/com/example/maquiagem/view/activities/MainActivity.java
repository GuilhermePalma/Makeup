package com.example.maquiagem.view.activities;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.example.maquiagem.model.User;
import com.example.maquiagem.view.CustomAlertDialog;
import com.example.maquiagem.view.fragments.FragmentListMakeup;
import com.example.maquiagem.view.fragments.FragmentSearchMakeup;
import com.google.android.material.navigation.NavigationView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {

    private static final String URL_API = "http://makeup-api.herokuapp.com/api/v1/products.json?";
    private static final String RATING_GREATER = "rating_greater_than";
    private static final int MAX_RESULT_CATALOG = 60;

    private final int OPTION_SEARCH_MAKEUP = R.id.option_searchMakeup;
    private final int OPTION_FAVORITE_MAKEUPS = R.id.option_favoriteMakeup;
    private final int OPTION_MORE_FAVORITES = R.id.option_moreFavorites;
    private final int OPTION_HOME_MAKEUP = R.id.option_homeMakeup;
    private final int OPTION_LOCATION = R.id.option_location;
    private final int POSITION_TOP_MENU_SEARCH = 0;
    private final int POSITION_TOP_MENU_HOME = 1;

    private LinearLayout layout_loading;
    private FrameLayout frame_fragment;
    private Toolbar toolbar;
    private Menu menu;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private SerializationData serializationData;
    private CustomAlertDialog dialogs;

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
        List<Makeup> list_catalogMakeup = asyncGetMakeups(OPTION_HOME_MAKEUP);
        setUpListFragment(list_catalogMakeup, FragmentListMakeup.TYPE_CATALOG);

        //Configura o Header do Navigation View
        View headerView = navigationView.getHeaderView(0);

        // Obtém a referência do nome do usuário e altera seu nome
        TextView txt_nameHeader = headerView.findViewById(R.id.text_nameHeader);
        TextView txt_nicknameHeader = headerView.findViewById(R.id.text_nicknameHeader);

        DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
        User user = dataBaseHelper.selectUser(this);
        dataBaseHelper.close();
        if (user != null) {
            // Configuração no Tamanho do Nome e Nickname no Header do Menu Lateral
            String name_formatted, nickname_formatted;
            String name = user.getName();
            String nickname = user.getNickname();
            if (name.length() > 21) {
                name_formatted = name.substring(0, 20);
            } else name_formatted = name;

            if (nickname.length() > 21) {
                nickname_formatted = nickname.substring(0, 20);
            } else nickname_formatted = nickname;

            txt_nameHeader.setText(name_formatted);
            txt_nicknameHeader.setText(
                    getString(R.string.text_nicknameFormatted, nickname_formatted));
        } else {
            txt_nameHeader.setText("");
            txt_nicknameHeader.setText("");
        }
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

        serializationData = new SerializationData(this);
        dialogs = new CustomAlertDialog(this);
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
        // Valores das Opções do Menu Superior (3 Pontinhos)
        final int OPTION_MENU_TOP_HOME = R.id.topMenu_home;
        final int OPTION_MENU_TOP_SEARCH = R.id.search_option;

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
                List<Makeup> list_catalogMakeup = asyncGetMakeups(OPTION_HOME_MAKEUP);
                setUpListFragment(list_catalogMakeup, FragmentListMakeup.TYPE_CATALOG);

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

        // Valores dos IDs das Opções do Menu
        final int OPTION_PROFILE = R.id.option_profile;
        final int OPTION_CONFIG = R.id.option_config;
        final int OPTION_DATA_USED = R.id.option_dataUsed;
        final int OPTION_EXIT = R.id.option_exit;
        final int OPTION_HISTORIC_MAKEUP = R.id.option_historicMakeup;

        // Trata o Clique nos Itens
        navigationView.setNavigationItemSelectedListener(item -> {
            // Obtem o ID do Item selecionado
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
            drawer.closeDrawer(GravityCompat.START);

            // SwitchCase para verificar qual item foi selecionado
            switch (id_item) {
                case OPTION_PROFILE:
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    break;

                case OPTION_HOME_MAKEUP:
                    List<Makeup> list_catalogMakeup = asyncGetMakeups(OPTION_HOME_MAKEUP);
                    setUpListFragment(list_catalogMakeup, FragmentListMakeup.TYPE_CATALOG);

                    //Altera o Icone Superior (Icone Search)
                    menu.getItem(POSITION_TOP_MENU_SEARCH).setVisible(true);
                    menu.getItem(POSITION_TOP_MENU_HOME).setVisible(false);

                    break;
                case OPTION_FAVORITE_MAKEUPS:
                    // todo: Implementar API_LOCAL
                    // Verificar a Internet: Se disponivel acessa api_local se não obtem do BD
                    String select_favorite = String.format("SELECT * FROM %1$s WHERE %2$s=1",
                            DataBaseHelper.TABLE_MAKEUP, DataBaseHelper.IS_FAVORITE_MAKEUP);

                    List<Makeup> list_favoriteMakeup = serializationData.serializationSelectMakeup(select_favorite);
                    setUpListFragment(list_favoriteMakeup, FragmentListMakeup.TYPE_FAVORITE);
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
                    setUpListFragment(null, FragmentListMakeup.TYPE_MORE_LIKED);
                    break;

                case OPTION_HISTORIC_MAKEUP:
                    String select_historic = String.format("SELECT * FROM %s", DataBaseHelper.TABLE_MAKEUP);

                    List<Makeup> list_historicSearches = serializationData.serializationSelectMakeup(select_historic);
                    setUpListFragment(list_historicSearches, FragmentListMakeup.TYPE_HISTORIC);
                    break;

                case OPTION_LOCATION:
                    // Já foi Validado Internet e GPS
                    startActivity(new Intent(this, LocationActivity.class));
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
                    // Limpa o Banco Local do Usuario
                    DataBaseHelper database = new DataBaseHelper(this);
                    database.deleteAllUsers();
                    database.close();
                    // Inicia a SplashScreen
                    startActivity(new Intent(this, SplashScreen.class));
                    finish();
                    break;
            }
            return true;
        });
    }

    /**
     * Configura instancias dos Fragment de Lista (Catalogo, Favoritas, Historico)
     */
    private void setUpListFragment(List<Makeup> makeupList, String type_fragment) {
        if (makeupList == null) {
            showError();
        } else {
            // Remove o Layout do "Loading" e exibe o Fragment
            layout_loading.setVisibility(View.GONE);
            frame_fragment.setVisibility(View.VISIBLE);

            FragmentListMakeup fragmentListMakeup = FragmentListMakeup.newInstance(makeupList,
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
    private List<Makeup> asyncGetMakeups(int option_search) {
        // Carrega o Circular Progress Indicator
        layout_loading.setVisibility(View.VISIBLE);
        frame_fragment.setVisibility(View.GONE);

        // Configura uma Atividade Assincrona
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        // Configura a Execução da Tarefa Assincrona
        Set<Callable<String>> callableTaskAPI = new HashSet<>();
        callableTaskAPI.add(() -> {
            // Obtem o JSON e Quantidade de Resultados
            String json;
            switch (option_search) {
                case OPTION_HOME_MAKEUP:
                    json = getJsonCatalog();
                    break;

                case OPTION_FAVORITE_MAKEUPS:
                    json = getJsonFavorite();
                    break;

                case OPTION_MORE_FAVORITES:
                    json = getJsonMoreLiked();
                    break;

                default:
                    json = "";
                    break;
            }
            return json;
        });

        try {
            // Obtem o Resultado da Busca Assincrona
            List<Future<String>> futureTasksList = executorService.invokeAll(callableTaskAPI);
            String json = futureTasksList.get(0).get();

            if (json == null || json.equals("")) return null;

            int quantity_result = option_search == OPTION_FAVORITE_MAKEUPS
                    ? SerializationData.ALL_ITEMS_JSON : MAX_RESULT_CATALOG;

            // Retorna o JSON Serializado ou null
            return new SerializationData(getApplicationContext()).serializationJsonMakeup(json, quantity_result);
        } catch (Exception ex) {
            Log.e("Error", "Erro ao manipular o JSON ou na sua Serialização. " + ex);
            ex.printStackTrace();
            return null;
        }
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
                Html.fromHtml(getString(R.string.error_tableEmpty)).toString()).show();
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