package com.example.maquiagem.view.activities;

import static com.example.maquiagem.model.SerializationData.ALL_ITEMS_JSON;
import static com.example.maquiagem.model.SerializationData.DEFAULT_QUANTITY;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.maquiagem.controller.ManagerResources;
import com.example.maquiagem.model.SerializationData;
import com.example.maquiagem.model.entity.Makeup;
import com.example.maquiagem.model.entity.User;
import com.example.maquiagem.view.CustomAlertDialog;
import com.example.maquiagem.view.fragments.FragmentListMakeup;
import com.example.maquiagem.view.fragments.FragmentSearchMakeup;
import com.google.android.material.navigation.NavigationView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    public static final int OPTION_MY_FAVORITE_MAKEUPS = R.id.option_favoriteMakeup;
    public static final int OPTION_MORE_FAVORITES = R.id.option_moreFavorites;
    public static final int OPTION_HOME_MAKEUP = R.id.option_homeMakeup;
    private final int OPTION_SEARCH_MAKEUP = R.id.option_searchMakeup;
    private final int OPTION_LOCATION = R.id.option_location;
    private final int POSITION_TOP_MENU_SEARCH = 0;
    private final int POSITION_TOP_MENU_HOME = 1;

    private Context context;
    private LinearLayout layout_loading;
    private FrameLayout frame_fragment;
    private Toolbar toolbar;
    private Menu menu;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private CustomAlertDialog customAlertDialog;


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
        asyncGetMakeups(OPTION_HOME_MAKEUP);

        //Configura o Header do Navigation View
        View headerView = navigationView.getHeaderView(0);

        // Obtém a referência do nome do usuário e altera seu nome
        TextView txt_nameHeader = headerView.findViewById(R.id.text_nameHeader);
        TextView txt_nicknameHeader = headerView.findViewById(R.id.text_nicknameHeader);

        DataBaseHelper dataBaseHelper = new DataBaseHelper(context);
        User user = dataBaseHelper.selectUser(context);
        dataBaseHelper.close();

        // Configuração no Tamanho do Nome e Nickname no Header do Menu Lateral
        txt_nameHeader.setText(user != null
                ? user.customStringFormat(user.getName(), null, 21) : "");
        txt_nicknameHeader.setText(user != null
                ? user.customStringFormat(user.getNickname(), "@", 21) : "");
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
        context = MainActivity.this;
        customAlertDialog = new CustomAlertDialog(context);
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
                        new FragmentSearchMakeup(this, context)).commit();
                navigationView.getMenu().findItem(OPTION_SEARCH_MAKEUP).setChecked(true);
                navigationView.getMenu().findItem(OPTION_SEARCH_MAKEUP).setCheckable(true);

                return true;
            case OPTION_MENU_TOP_HOME:
                //Desseleciona o Menu Lateral e Altera a Visibilidade do Icone Superior
                unselectedItemsMenu();

                menu.getItem(POSITION_TOP_MENU_SEARCH).setVisible(true);
                menu.getItem(POSITION_TOP_MENU_HOME).setVisible(false);

                // Instancia o Fragment e Seleciona sua opção no Menu Latera
                asyncGetMakeups(OPTION_HOME_MAKEUP);

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
                if (!ManagerResources.hasConnectionInternet(context)) {
                    customAlertDialog.defaultMessage(R.string.title_noConnection,
                            R.string.error_connection, null, new String[]{"Internet"},
                            true).show();
                    drawer.closeDrawer(GravityCompat.START);
                    return false;
                } else if (!ManagerResources.hasConnectionGps(context)) {
                    customAlertDialog.defaultMessage(R.string.title_noGps, R.string.error_connection,
                            null, new String[]{"GPS"}, true).show();
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
                    startActivity(new Intent(context, ProfileActivity.class));
                    break;

                case OPTION_HOME_MAKEUP:
                    asyncGetMakeups(OPTION_HOME_MAKEUP);

                    //Altera o Icone Superior (Icone Search)
                    menu.getItem(POSITION_TOP_MENU_SEARCH).setVisible(true);
                    menu.getItem(POSITION_TOP_MENU_HOME).setVisible(false);

                    break;
                case OPTION_MY_FAVORITE_MAKEUPS:
                    if (ManagerResources.hasConnectionInternet(context)) {
                        // Obtem os Dados da API e Sincroniza o Banco de Dados Local
                        asyncGetMakeups(OPTION_MY_FAVORITE_MAKEUPS);
                    } else {
                        // Obtem os Dados Salvos do Banco Local (Sem Internet)
                        String select_favorite = String.format("SELECT * FROM %1$s WHERE %2$s=1",
                                DataBaseHelper.TABLE_MAKEUP, DataBaseHelper.IS_FAVORITE_MAKEUP);
                        List<Makeup> list_favorites = new SerializationData(context)
                                .serializationSelectMakeup(select_favorite);
                        setUpListFragment(list_favorites, FragmentListMakeup.TYPE_MY_FAVORITE);
                    }
                    break;

                case OPTION_SEARCH_MAKEUP:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_forFragment,
                            new FragmentSearchMakeup(this, context)).commit();

                    //Altera o Icone Superior (Icone Home)
                    menu.getItem(POSITION_TOP_MENU_SEARCH).setVisible(false);
                    menu.getItem(POSITION_TOP_MENU_HOME).setVisible(true);
                    break;

                case OPTION_MORE_FAVORITES:
                    asyncGetMakeups(OPTION_MORE_FAVORITES);
                    break;

                case OPTION_HISTORIC_MAKEUP:
                    String select_historic = String.format("SELECT * FROM %s", DataBaseHelper.TABLE_MAKEUP);

                    List<Makeup> list_historicSearches = new SerializationData(context)
                            .serializationSelectMakeup(select_historic);
                    setUpListFragment(list_historicSearches, FragmentListMakeup.TYPE_HISTORIC);
                    break;

                case OPTION_LOCATION:
                    // Já foi Validado Internet e GPS
                    startActivity(new Intent(context, LocationActivity.class));
                    break;

                case OPTION_CONFIG:
                    startActivity(new Intent(context,
                            ConfigurationActivity.class));
                    break;

                case OPTION_DATA_USED:
                    startActivity(new Intent(context,
                            DataApplicationActivity.class));
                    break;

                case OPTION_EXIT:
                    // Limpa o Banco Local do Usuario
                    DataBaseHelper database = new DataBaseHelper(context);
                    database.deleteAllUsers();
                    database.close();
                    // Inicia a SplashScreen
                    startActivity(new Intent(context, SplashScreen.class));
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

    /**
     * Metodo responsavel pela busca de forma Assincrona nas APIs (Local ou Externa). A partir do
     * ID do Menu Lateral, configura os dados obtidos e quantidade de resultados. Tambem é tratado a
     * exibição dos itens no do Fragment
     */
    private void asyncGetMakeups(int option_search) {
        // Quantidade de Itens que serão Exibidos
        int quantity_items = option_search == OPTION_HOME_MAKEUP ? DEFAULT_QUANTITY : ALL_ITEMS_JSON;

        // Carrega o Circular Progress Indicator
        layout_loading.setVisibility(View.VISIBLE);
        frame_fragment.setVisibility(View.GONE);

        // Executa a Busca Assincrona. Dessa forma, a busca pode continuar após o metodo terminar
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(() -> {
            Makeup makeup = new Makeup(context);
            List<Makeup> async_list = makeup.getMakeups(executorService, makeup.getUri(option_search),
                    quantity_items);

            // Atribui a Lista usada o Valor Oficial das Maquiagens
            runOnUiThread(() -> {
                if (async_list == null || async_list.isEmpty()) showError();
                else {
                    String type_fragment = "";
                    switch (option_search) {
                        case OPTION_HOME_MAKEUP:
                            type_fragment = FragmentListMakeup.TYPE_CATALOG;
                            break;
                        case OPTION_MY_FAVORITE_MAKEUPS:
                            type_fragment = FragmentListMakeup.TYPE_MY_FAVORITE;
                            break;
                        case OPTION_MORE_FAVORITES:
                            type_fragment = FragmentListMakeup.TYPE_MORE_LIKED;
                            break;
                        default:
                            break;
                    }
                    setUpListFragment(async_list, type_fragment);
                }
            });

            // Sincroniza o Banco de Dados Local com os Dados recebido da API
            if (async_list != null && option_search == OPTION_MY_FAVORITE_MAKEUPS) {
                DataBaseHelper dataBase = new DataBaseHelper(context);
                for (int i = 0; i < async_list.size(); i++) {
                    dataBase.insertMakeup(async_list.get(i));
                }
                dataBase.close();
            }
        });
    }

    /**
     * Mensagem de Erro dos dados não encontrados
     */
    public void showError() {
        layout_loading.setVisibility(View.GONE);
        frame_fragment.setVisibility(View.GONE);

        customAlertDialog.defaultMessage(R.string.title_noData, R.string.error_tableEmpty, null,
                null, true).show();
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