package com.example.maquiagem.view.activities;

import static com.example.maquiagem.model.SearchInternet.PARAM_BRAND;
import static com.example.maquiagem.model.SearchInternet.PARAM_RATING_GREATER;
import static com.example.maquiagem.model.SearchInternet.PARAM_TYPE;
import static com.example.maquiagem.model.SearchInternet.URL_MAKEUP;
import static com.example.maquiagem.model.SerializationData.ALL_ITEMS_JSON;
import static com.example.maquiagem.model.SerializationData.DEFAULT_QUANTITY;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maquiagem.R;
import com.example.maquiagem.controller.CategoriesAdapter;
import com.example.maquiagem.controller.ClickCategory;
import com.example.maquiagem.controller.ManagerDatabase;
import com.example.maquiagem.controller.ManagerResources;
import com.example.maquiagem.model.entity.Makeup;
import com.example.maquiagem.model.entity.User;
import com.example.maquiagem.view.CustomAlertDialog;
import com.example.maquiagem.view.fragments.FragmentListMakeup;
import com.example.maquiagem.view.fragments.FragmentSearchMakeup;
import com.google.android.material.navigation.NavigationView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Activity Principal. A partir dela será possivel acessar as demais funções do Sistema
 */
public class MainActivity extends AppCompatActivity implements ClickCategory {

    public static final int OPTION_MY_FAVORITE_MAKEUPS = R.id.option_favoriteMakeup;
    public static final int OPTION_MORE_FAVORITES = R.id.option_moreFavorites;
    public static final int OPTION_HOME_MAKEUP = R.id.option_homeMakeup;
    public static final int OPTION_CATEGORIES = R.id.option_categoriesMakeup;
    private final int OPTION_SEARCH_MAKEUP = R.id.option_searchMakeup;
    private final int OPTION_LOCATION = R.id.option_location;
    private final int OPTION_HISTORIC_MAKEUP = R.id.option_historicMakeup;
    private final int POSITION_TOP_MENU_SEARCH = 0;
    private final int POSITION_TOP_MENU_HOME = 1;

    private boolean isLoading = false;
    private Context context;
    private LinearLayout layout_loading;
    private FrameLayout frame_fragment;
    private Toolbar toolbar;
    private Menu menu;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private CustomAlertDialog customAlertDialog;
    private String categorySelected;

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

        ManagerDatabase managerDatabase = new ManagerDatabase(context);
        User user = managerDatabase.selectUser();

        // Configuração no Tamanho do Nome e Nickname no Header do Menu Lateral
        txt_nameHeader.setText(user != null
                ? ManagerResources.customStringFormat(user.getName(), null, 21) : "");
        txt_nicknameHeader.setText(user != null
                ? ManagerResources.customStringFormat(user.getNickname(), "@", 21) : "");
    }

    /**
     * Instancia os Itens e Obtem os IDs que serão Usados
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
        // Não Permite o Clique em outras Opções enquanto o Conteudo não for Carregado
        if (isLoading) {
            showWaitLoading();
            drawer.closeDrawer(GravityCompat.START);
            return false;
        } else if (frame_fragment.getVisibility() == View.GONE) {
            frame_fragment.setVisibility(View.VISIBLE);
        }

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
     * Trata o Clique nos Itens do Menu Lateral
     */
    private void listenerNavigation() {

        // Valores dos IDs das Opções do Menu
        final int OPTION_PROFILE = R.id.option_profile;
        final int OPTION_CONFIG = R.id.option_config;
        final int OPTION_DATA_USED = R.id.option_dataUsed;
        final int OPTION_EXIT = R.id.option_exit;

        // Trata o Clique nos Itens
        navigationView.setNavigationItemSelectedListener(item -> {

            // Não Permite o Clique em outras Opções enquanto o Conteudo não for Carregado
            if (isLoading) {
                showWaitLoading();
                drawer.closeDrawer(GravityCompat.START);
                return false;
            }

            // Obtem o ID do Item selecionado
            int id_item = item.getItemId();

            // Caso seja o Item de Localização ---> Valida Conexão com Internet e GPS
            if (id_item == OPTION_LOCATION && !ManagerResources.hasConnectionGps(context)) {
                customAlertDialog.defaultMessage(R.string.title_noGps, R.string.error_connection,
                        null, new String[]{"GPS"}, true).show();
                drawer.closeDrawer(GravityCompat.START);
                return false;
            } else if (id_item == OPTION_HOME_MAKEUP || id_item == OPTION_MY_FAVORITE_MAKEUPS ||
                    id_item == OPTION_SEARCH_MAKEUP || id_item == OPTION_MORE_FAVORITES ||
                    id_item == OPTION_HISTORIC_MAKEUP || id_item == OPTION_EXIT) {
                if (!ManagerResources.hasConnectionInternet(context)) {
                    customAlertDialog.defaultMessage(R.string.title_noConnection,
                            R.string.error_connection, null, new String[]{"Internet"},
                            true).show();
                    drawer.closeDrawer(GravityCompat.START);
                    return false;
                }
            }

            // Deseleciona o Item seleciona e Coloca a Seleção do Item Clicado
            if (id_item != OPTION_PROFILE && id_item != OPTION_LOCATION && id_item != OPTION_CONFIG
                    && id_item != OPTION_DATA_USED) {
                unselectedItemsMenu();
                item.setChecked(true);
                item.setCheckable(true);

                if (frame_fragment.getVisibility() == View.GONE) {
                    frame_fragment.setVisibility(View.VISIBLE);
                }
            }

            drawer.closeDrawer(GravityCompat.START);

            // SwitchCase para verificar qual item foi selecionado
            switch (id_item) {
                case OPTION_HOME_MAKEUP:
                    asyncGetMakeups(OPTION_HOME_MAKEUP);

                    //Altera o Icone Superior (Icone Search)
                    menu.getItem(POSITION_TOP_MENU_SEARCH).setVisible(true);
                    menu.getItem(POSITION_TOP_MENU_HOME).setVisible(false);

                    break;

                case OPTION_PROFILE:
                    startActivity(new Intent(context, ProfileActivity.class));
                    break;

                case OPTION_MY_FAVORITE_MAKEUPS:
                    // Obtem os Dados da API e Sincroniza o Banco de Dados Local
                    asyncGetMakeups(OPTION_MY_FAVORITE_MAKEUPS);
                    break;

                case OPTION_SEARCH_MAKEUP:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_forFragment,
                            new FragmentSearchMakeup(this, context)).commit();

                    //Altera o Icone Superior (Icone Home)
                    menu.getItem(POSITION_TOP_MENU_SEARCH).setVisible(false);
                    menu.getItem(POSITION_TOP_MENU_HOME).setVisible(true);
                    break;

                case OPTION_CATEGORIES:
                    showCategories();
                    break;

                case OPTION_MORE_FAVORITES:
                    asyncGetMakeups(OPTION_MORE_FAVORITES);
                    break;

                case OPTION_HISTORIC_MAKEUP:
                    asyncGetMakeups(OPTION_HISTORIC_MAKEUP);
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
                    ManagerDatabase database = new ManagerDatabase(context);
                    database.clearTables();
                    // Inicia a SplashScreen
                    startActivity(new Intent(context, SplashScreen.class));
                    finish();
                    break;
            }
            return true;
        });
    }

    /**
     * Metodo Responsavel por Carregar e Configurar o GridView que exibirá as Categorias
     */
    private void showCategories() {
        // Tira o FrameFragment da Exibição
        frame_fragment.setVisibility(View.GONE);

        // Obtem a Instancia do RecyclerView
        final RecyclerView recyclerView_categories = (RecyclerView)
                findViewById(R.id.recyclerView_categoriesMakeup);

        // Configura o Tipo de Layout
        GridLayoutManager gridLayout_categories = new GridLayoutManager(context, 2);
        recyclerView_categories.setLayoutManager(gridLayout_categories);

        // Configura o Titulo do RecyclerView
        final View header_view = LayoutInflater.from(context)
                .inflate(R.layout.layout_header_list, recyclerView_categories, false);
        final TextView header_title = header_view.findViewById(R.id.txt_titleHeaderList);
        header_title.setText(R.string.title_categories);
        final TextView header_subtitle = header_view.findViewById(R.id.txt_subtitleHeaderList);
        header_subtitle.setText(R.string.subTitle_categories);

        // Obtem as Categorias (Tipos de Maquiagens)
        final String[] categoriesArray = getResources().getStringArray(R.array.array_type);

        // Configura quem controlará os Itens do Layout
        CategoriesAdapter categoriesAdapter = new CategoriesAdapter(
                header_view, categoriesArray, this);
        recyclerView_categories.setAdapter(categoriesAdapter);

        // Configura a Disposição do RecyclerView
        gridLayout_categories.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return categoriesAdapter.getItemViewType(position) ==
                        CategoriesAdapter.POSITION_HEADER ? 2 : 1;
            }
        });

        // Exibe o RecyclerView Configurado na Tela
        recyclerView_categories.setVisibility(View.VISIBLE);
    }

    /**
     * Metodo responsavel pela busca de forma Assincrona nas APIs (Local ou Externa). A partir do
     * ID do Menu Lateral, configura os dados obtidos e quantidade de resultados. Tambem é tratado a
     * exibição dos itens no do Fragment
     *
     * @param option_search ID da Opção Selecionada para Formar a URI de Pesquisa
     * @see Makeup#getMakeups(Context, ExecutorService, Uri, int)
     * @see Makeup#getHistoricSearch(Context, ExecutorService)
     * @see Makeup#getHistoricSearch(Context, ExecutorService)
     * @see #OPTION_HOME_MAKEUP
     * @see #OPTION_MORE_FAVORITES
     * @see #OPTION_MY_FAVORITE_MAKEUPS
     * @see #OPTION_HISTORIC_MAKEUP
     */
    private void asyncGetMakeups(int option_search) {
        // Quantidade de Itens que serão Exibidos
        int quantity_items = option_search == OPTION_HOME_MAKEUP ? DEFAULT_QUANTITY : ALL_ITEMS_JSON;

        // Carrega o Circular Progress Indicator
        layout_loading.setVisibility(View.VISIBLE);
        frame_fragment.setVisibility(View.GONE);

        // Somente Busca novos dados se a Ultima Pesquisa já foi Concluida
        if (!isLoading) {

            // Handler exibira os Resultados da Task Async na Thread Main
            Handler handler = new Handler(Looper.getMainLooper());

            // Configura e executa as Tarefas Assincronas
            ExecutorService executorService = Executors.newCachedThreadPool();
            executorService.execute(() -> {
                // Define o Inicio do Carregamento
                handler.post(() -> isLoading = true);

                // Obtem as Makeups
                List<Makeup> async_list;

                switch (option_search) {
                    case OPTION_MY_FAVORITE_MAKEUPS:
                        async_list = Makeup.getFavoritesMakeup(context, executorService);
                        break;
                    case OPTION_HISTORIC_MAKEUP:
                        async_list = Makeup.getHistoricSearch(context, executorService);
                        break;
                    case OPTION_MORE_FAVORITES:
                        // todo: Implementar API_Interna
                        Uri uriMoreLiked = Uri.parse(URL_MAKEUP).buildUpon()
                                .appendQueryParameter(PARAM_BRAND, "l'oreal").build();
                        // Envia uma solicitação à Makeup_API & Obtem o JSON
                        async_list = Makeup.getMakeups(context, executorService, uriMoreLiked, quantity_items);
                        break;
                    case OPTION_CATEGORIES:
                        Uri uriCategories = Uri.parse(URL_MAKEUP).buildUpon()
                                .appendQueryParameter(PARAM_TYPE, categorySelected).build();
                        // Envia uma solicitação à Makeup_API & Obtem o JSON
                        async_list = Makeup.getMakeups(context, executorService, uriCategories, quantity_items);
                        break;
                    default:
                        Uri uriHome = Uri.parse(URL_MAKEUP).buildUpon()
                                .appendQueryParameter(PARAM_RATING_GREATER, "4.8").build();
                        // Envia uma solicitação à Makeup_API & Obtem o JSON
                        async_list = Makeup.getMakeups(context, executorService, uriHome, quantity_items);
                        break;
                }

                // Atribui a Lista usada o Valor Oficial das Maquiagens
                handler.post(() -> {
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
                            case OPTION_CATEGORIES:
                                type_fragment = FragmentListMakeup.TYPE_CATEGORY;
                                break;
                            default:
                                break;
                        }
                        // Configura o Fragment
                        initializeFragmentList(async_list, type_fragment);
                    }
                });

                // Define o Fim do Carregamento
                handler.post(() -> isLoading = false);

                // Sincroniza o Banco de Dados Local com os Dados recebido da API
                if (async_list != null && option_search == OPTION_MY_FAVORITE_MAKEUPS) {
                    ManagerDatabase dataBase = new ManagerDatabase(context);
                    int allInserted = 0;
                    for (int i = 0; i < async_list.size(); i++) {
                        allInserted = dataBase.insertMakeup(async_list.get(i)) ? allInserted : allInserted + 1;
                    }

                    if (allInserted > 0) {
                        customAlertDialog.defaultMessage(R.string.title_possibleError, R.string.error_syncFavorites,
                                null, new String[]{"Favoritas"}, false).show();
                    }
                }
            });
        } else showWaitLoading();
    }

    /**
     * Configura e instancia o Fragment de Lista (Catalogo, Favoritas, Historico)
     *
     * @param makeupList    {@link List} de {@link Makeup} que será exibida
     * @param type_fragment {@link String} do Tipo de Fragment
     * @see FragmentListMakeup
     */
    private void initializeFragmentList(List<Makeup> makeupList, String type_fragment) {
        if (makeupList == null) {
            showError();
        } else {
            // Remove o Layout do "Loading" e exibe o Fragment
            layout_loading.setVisibility(View.GONE);
            frame_fragment.setVisibility(View.VISIBLE);

            FragmentListMakeup fragmentListMakeup = FragmentListMakeup.newInstance(makeupList,
                    type_fragment);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_forFragment,
                    fragmentListMakeup).commitAllowingStateLoss();
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
     * Mensagem de Erro dos dados não encontrados
     */
    public void showError() {
        layout_loading.setVisibility(View.GONE);
        frame_fragment.setVisibility(View.GONE);

        customAlertDialog.defaultMessage(R.string.title_noData, R.string.error_tableEmpty, null,
                null, true).show();
    }

    /**
     * Mensagem de Erro Informando que a busca não foi Concluida
     */
    private void showWaitLoading() {
        customAlertDialog.defaultMessage(R.string.title_noData, R.string.error_waitLoading, null,
                null, false).show();
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClickCategory(String categorySelected) {
        this.categorySelected = categorySelected;
        asyncGetMakeups(OPTION_CATEGORIES);
    }
}