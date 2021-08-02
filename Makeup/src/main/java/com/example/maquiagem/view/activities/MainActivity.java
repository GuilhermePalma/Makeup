package com.example.maquiagem.view.activities;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.maquiagem.R;
import com.example.maquiagem.view.AlertDialogs;
import com.example.maquiagem.view.fragments.ListMakeup;
import com.example.maquiagem.view.fragments.SearchMakeup;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private static final int id_profile = R.id.option_profile;
    private static final int id_config = R.id.option_config;
    private static final int id_dataUsed = R.id.option_dataUsed;
    private static final int id_exit = R.id.option_exit;
    private static final int id_searchMakeup = R.id.option_searchMakeup;
    private static final int id_allMakeup = R.id.option_allMakeup;
    private static final int id_favoriteMakeups = R.id.option_favoriteMakeup;
    private static final int id_popularMakeup = R.id.option_popularMakeup;
    private static final int id_location = R.id.option_location;
    private static final int id_sensor = R.id.option_sensor;

    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ListMakeup listMakeup;

    private AlertDialogs dialogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dialogs = new AlertDialogs();
        toolbar = findViewById(R.id.toolBar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        // Criação da ToolBar
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        setUpDrawer();
        listenerNavigation();

        if (savedInstanceState == null) {
            // Define o Background da Main Activity
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_forFragment,
                    new SearchMakeup()).commit();
            navigationView.setCheckedItem(R.id.option_searchMakeup);
        }
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
            switch (item.getItemId()) {
                case id_profile:
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                    break;

                case id_favoriteMakeups:
                    listMakeup = ListMakeup.newInstance("Maquiagens Favoritas");
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_forFragment,
                            listMakeup).commit();
                    break;

                case id_searchMakeup:
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_forFragment,
                            new SearchMakeup()).commit();
                    break;

                case id_popularMakeup:
                    listMakeup = ListMakeup.newInstance("Maquiagens mais Buscadas");
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_forFragment,
                            listMakeup).commit();
                    break;

                case id_allMakeup:
                    listMakeup = ListMakeup.newInstance("Todas as Maquiagens");
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_forFragment,
                            listMakeup).commit();
                    break;

                case id_location:
                    // Possui Conexão
                    if (connectionAvailable()) {
                        // GPS ativo
                        if (gpsAvailable()) {
                            startActivity(new Intent(this, LocationActivity.class));
                        } else {
                            dialogs.message(this, getString(R.string.title_noGps),
                                    getString(R.string.error_noGps)).show();
                        }
                    } else {
                        dialogs.message(this, getString(R.string.title_noConnection),
                                getString(R.string.error_connection)).show();
                    }
                    break;

                case id_sensor:
                    startActivity(new Intent(MainActivity.this,
                            SensorActivity.class));
                    break;

                case id_config:
                    startActivity(new Intent(MainActivity.this,
                            ConfigurationActivity.class));
                    break;

                case id_dataUsed:
                    startActivity(new Intent(MainActivity.this,
                            DataApplicationActivity.class));
                    break;

                case id_exit:
                    // todo: deslogar ---> Implementar depois do Cadastro/Login
                    break;

                default:
                    return true;
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });
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