package com.example.maquiagem.view.activities;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.maquiagem.R;
import com.example.maquiagem.controller.CursorMakeup;
import com.example.maquiagem.controller.DataBaseHelper;
import com.example.maquiagem.model.Makeup;
import com.example.maquiagem.view.PersonAlertDialogs;
import com.example.maquiagem.view.fragments.FragmentListMakeup;
import com.example.maquiagem.view.fragments.SearchMakeup;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final int ID_PROFILE = R.id.option_profile;
    private final int ID_CONFIG = R.id.option_config;
    private final int ID_DATA_USED = R.id.option_dataUsed;
    private final int ID_EXIT = R.id.option_exit;
    private final int ID_SEARCH_MAKEUP = R.id.option_searchMakeup;
    private final int ID_HISTORIC_MAKEUP = R.id.option_historicMakeup;
    private final int ID_FAVORITE_MAKEUPS = R.id.option_favoriteMakeup;
    private final int ID_MORE_FAVORITES = R.id.option_moreFavorites;
    private final int ID_LOCATION = R.id.option_location;
    private final int ID_SENSOR = R.id.option_sensor;

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private List<Makeup> listMakeup;

    private FragmentListMakeup fragmentListMakeup;
    private CursorMakeup cursorMakeup;
    private PersonAlertDialogs dialogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolBar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        fragmentListMakeup = new FragmentListMakeup();
        listMakeup = new ArrayList<>();
        cursorMakeup = new CursorMakeup(getApplicationContext());
        dialogs = new PersonAlertDialogs(this);

        // Criação da ToolBar
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        setUpDrawer();
        listenerNavigation();

        // Define o Background da Main Activity
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_forFragment,
                new SearchMakeup(getApplicationContext())).commit();

        // Define o Item que será inicialmente Selecionado
        navigationView.getMenu().findItem(R.id.option_searchMakeup).setChecked(true);
        navigationView.getMenu().findItem(R.id.option_searchMakeup).setCheckable(true);

    }


    // Configuração do Menu Lateral
    private void setUpDrawer() {
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawer,
                toolbar, R.string.open_menu, R.string.close_menu);
        drawer.addDrawerListener(drawerToggle);

        // Sincroniza o Estado do Icone
        drawerToggle.syncState();
    }


    private void listenerNavigation() {
        // Trata o Clique nos Itens
        navigationView.setNavigationItemSelectedListener(item -> {

            // Tira a Seleção dos Itens que não foram Selecionados
            int sizeMenu = navigationView.getMenu().size();
            for (int i = 0; i < sizeMenu; i++) {
                MenuItem item1 = navigationView.getMenu().getItem(i);
                if (item1.hasSubMenu()) {
                    // Caso tenha um Sub-menu, acessa eles para retirar a seleção
                    for (int u = 0; u < item1.getSubMenu().size(); u++) {
                        item1.getSubMenu().getItem(u).setChecked(false);
                        item1.getSubMenu().getItem(u).setCheckable(false);
                    }
                } else {
                    navigationView.getMenu().getItem(i).setChecked(false);
                    navigationView.getMenu().getItem(i).setCheckable(false);
                }
            }

            // Seleciona o Item clicado
            item.setChecked(true);
            item.setCheckable(true);

            // SwitchCase para verificar qual item foi selecionado
            switch (item.getItemId()) {
                case ID_PROFILE:
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    break;

                case ID_FAVORITE_MAKEUPS:
                    String select_favorite = String.format("SELECT * FROM %1$s WHERE %2$s='%3$s'",
                            DataBaseHelper.TABLE_MAKEUP, DataBaseHelper.IS_FAVORITE_MAKEUP, "true");

                    listMakeup = cursorMakeup.selectDataBase(select_favorite);
                    setUpListFragment(listMakeup, FragmentListMakeup.TYPE_FAVORITE);
                    break;

                case ID_SEARCH_MAKEUP:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_forFragment,
                            new SearchMakeup(getApplicationContext())).commit();
                    break;

                case ID_MORE_FAVORITES:
                    // Todo: Implementar metodo da API das Maquiagens mais Favoritadas
                    String select_popular = String.format("SELECT * FROM %s",
                            DataBaseHelper.TABLE_MAKEUP);
                    listMakeup = cursorMakeup.selectDataBase(select_popular);
                    setUpListFragment(listMakeup, FragmentListMakeup.TYPE_MORE_LIKED);
                    break;

                case ID_HISTORIC_MAKEUP:
                    String select_historic = String.format("SELECT * FROM %s",
                            DataBaseHelper.TABLE_MAKEUP);
                    listMakeup = cursorMakeup.selectDataBase(select_historic);
                    setUpListFragment(listMakeup, FragmentListMakeup.TYPE_HISTORIC);
                    break;

                case ID_LOCATION:
                    // Possui Conexão
                    if (connectionAvailable()) {
                        // GPS ativo
                        if (gpsAvailable()) {
                            startActivity(new Intent(this, LocationActivity.class));
                        } else {
                            dialogs.message(getString(R.string.title_noGps), getString(R.string.error_noGps))
                                    .show();
                        }
                    } else {
                        dialogs.message(getString(R.string.title_noConnection), getString(R.string.error_connection))
                                .show();
                    }
                    break;

                case ID_SENSOR:
                    startActivity(new Intent(MainActivity.this,
                            SensorActivity.class));
                    break;

                case ID_CONFIG:
                    startActivity(new Intent(MainActivity.this,
                            ConfigurationActivity.class));
                    break;

                case ID_DATA_USED:
                    startActivity(new Intent(MainActivity.this,
                            DataApplicationActivity.class));
                    break;

                case ID_EXIT:
                    // todo: deslogar ---> Implementar depois do Cadastro/Login
                    break;

                default:
                    return true;
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });
    }

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

    // Caso clique no botão voltar
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

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

}