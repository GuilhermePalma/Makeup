package com.example.maquiagem.view.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.maquiagem.R;
import com.example.maquiagem.controller.ManagerDatabase;
import com.example.maquiagem.controller.ManagerResources;
import com.example.maquiagem.model.entity.Location;
import com.example.maquiagem.view.CustomAlertDialog;
import com.example.maquiagem.view.fragments.FragmentFeedbackLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Activity Responsavel por Obter a Localização atual do Usuario
 */
public class LocationActivity extends AppCompatActivity {

    static final String STATE_FRAGMENT = "STATE FRAGMENT";
    public LinearLayout layout_address;
    private Context context;
    private LinearLayout layout_data;
    private TextView txt_wait;
    private TextView txt_error;
    private TextView country;
    private TextView state;
    private TextView address;
    private TextView cep;
    private Button btn_reset;
    private ImageButton btnFragment;
    private ProgressBar loading_location;
    private double latitude = 0;
    private double longitude = 0;
    private boolean activeFragment = false;
    private Location classLocation;
    private ManagerDatabase managerDatabase;
    private int actualId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        setUpToolBar();
        instanceItems();

        classLocation = new Location();
        managerDatabase = new ManagerDatabase(context);

        loading_location.setMax(10);
        loading_location.setProgress(0);

        // Obtem a ultima localização inserida
        int lastIdLocation = managerDatabase.amountLocation();
        actualId = lastIdLocation + 1;

        getLastLocation();
        listenerReset();
        listenerButtonFragment();
    }

    /**
     * Listener do Botão "Tentar Novamente" que recarrega a Activity
     */
    private void listenerReset() {
        btn_reset.setOnClickListener(v -> {
            overridePendingTransition(0, 0);
            finish();
            startActivity(getIntent());
        });
    }

    /**
     * Configura a Toolbar e o Retorno para a {@link MainActivity}
     */
    private void setUpToolBar() {
        // Criação da ToolBar e Criação da seta de voltar
        Toolbar toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            // Icon de voltar para a Tela Home
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_return_home);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
    }

    /**
     * Metodo Responsavel por obter os IDs e Instanciar os Itens que serão usados
     */
    private void instanceItems() {
        context = LocationActivity.this;
        layout_data = findViewById(R.id.layout_data);
        layout_address = findViewById(R.id.layout_address);
        btnFragment = findViewById(R.id.btn_showFragment);
        btnFragment.setImageResource(R.drawable.ic_keyboard_arrow_down);
        btn_reset = findViewById(R.id.btn_reset);
        txt_error = findViewById(R.id.txt_location);
        country = findViewById(R.id.txt_country);
        state = findViewById(R.id.txt_state);
        address = findViewById(R.id.txt_address);
        cep = findViewById(R.id.txt_cep);
        loading_location = findViewById(R.id.progressBar_location);
        txt_wait = findViewById(R.id.txt_wait);
    }

    /**
     * Listener do Botão de Abrir e Fechar o Fragment
     */
    public void listenerButtonFragment() {
        btnFragment.setOnClickListener((v) -> {
            // Caso o Fragment esteja ativo --> Fecha. Se não está ativo ----> Abre
            if (activeFragment) {
                closeFragment();
            } else {
                showFragment();
            }
        });
    }

    /**
     * Metodo que tenta obter a ultima localização conhecida do Usuario
     */
    public void getLastLocation() {

        // Serviço do Google para Manipular Localizações
        FusedLocationProviderClient fusedLocationClient = LocationServices.
                getFusedLocationProviderClient(context);

        // Verifica se a permissão de Localização  foi Concedida --> Se não = Solicita pro Usuario
        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, 0);

            showError();
            return;
        }

        // Recupera a Ultima Localização
        fusedLocationClient.getLastLocation().
                addOnSuccessListener(this,
                        location -> {

                            List<Address> addresses = new ArrayList<>();
                            Geocoder geocoder = new Geocoder(context,
                                    Locale.getDefault());

                            if (location == null) {
                                Log.e("LOCATION", "Erro na Localização\n" + location);

                                showError();
                                return;
                            }

                            try {
                                // Tenta obter a Latitude e Longitude do Local atual
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();

                                // Geolocalização Reversa (Longitude + Latidude = Endereço)
                                addresses = geocoder.getFromLocation(latitude, longitude, 1);

                                // Imprime o resultado da List de Endereços (ArrayList)
                                Log.d("LIST ENDEREÇO", addresses.get(0).toString()
                                        + "\n" + addresses.get(0).getAddressLine(0));

                                // Instancia a classe de Localização
                                classLocation.setLastId(actualId);
                                classLocation.setAddress(addresses.get(0).getThoroughfare());
                                classLocation.setDistrict(addresses.get(0).getFeatureName());
                                classLocation.setState(addresses.get(0).getAdminArea());
                                classLocation.setCity(addresses.get(0).getSubAdminArea());
                                classLocation.setNumber(addresses.get(0).getSubThoroughfare());
                                classLocation.setPostalCode(addresses.get(0).getPostalCode());
                                classLocation.setCountryName(addresses.get(0).getCountryName());
                                classLocation.setCountryCode(addresses.get(0).getCountryCode());

                                // Insere a Localização no BD e Exibe o Endereço
                                if (!managerDatabase.insertLocation(classLocation)) {
                                    new CustomAlertDialog(context).defaultMessage(R.string.error_api,
                                            R.string.error_database, null, null, true).show();
                                }


                            } catch (IOException e) {
                                // Tratamento de Erro da Localização ---> Erro no Processo
                                Log.e("GET LOCATION", "Erro na recuperação do endereço " +
                                        "pela Latitude e Longitude.\n" + e);
                            } catch (IllegalArgumentException illegalArgumentException) {
                                // Tratamento de Erro da Localização ---> Formato não aceito
                                Log.e("ERROR FORMAT", "Latitude = " + location.getLatitude() +
                                        ", Longitude = " +
                                        location.getLongitude(), illegalArgumentException);
                            } finally {
                                Log.d("FINAL LOCATION", "Localização Final:\n" +
                                        addresses.toString());

                                if (addresses.isEmpty() || addresses == null) {
                                    showError();
                                } else showWindow(classLocation);

                            }
                        })

                // Caso não consiga obter a ultima Localização ---> Erro
                .addOnFailureListener(
                        e -> {
                            Log.e("TAG", "onFailure: ", e);
                            showError();
                        }
                );
    }

    /**
     * Exibe o Layout e Dados da {@link Location}
     *
     * @param location {@link Location} que será exibida
     */
    private void showWindow(Location location) {
        loading_location.setProgress(10);
        loading_location.setVisibility(View.GONE);
        txt_wait.setVisibility(View.GONE);

        layout_data.setVisibility(View.VISIBLE);
        btnFragment.setVisibility(View.VISIBLE);

        country.setText(ManagerResources.getStringIdNormalized(context, R.string.txt_country,
                new String[]{location.getCountryName(), location.getCountryCode()}));
        state.setText(ManagerResources.getStringIdNormalized(context, R.string.txt_city,
                new String[]{location.getCity(), location.getState()}));
        address.setText(ManagerResources.getStringIdNormalized(context, R.string.txt_address,
                new String[]{location.getAddress(), location.getDistrict(), location.getNumber()}));
        cep.setText(ManagerResources.getStringIdNormalized(context, R.string.txt_cep,
                new String[]{location.getPostalCode()}));

        country.setVisibility(View.VISIBLE);
        state.setVisibility(View.VISIBLE);
        address.setVisibility(View.VISIBLE);
        cep.setVisibility(View.VISIBLE);
    }

    /**
     * Exibe o Layout de Erro na Tela
     */
    public void showError() {
        loading_location.setProgress(10);
        loading_location.setVisibility(View.GONE);
        txt_wait.setVisibility(View.GONE);

        layout_address.setVisibility(View.GONE);
        layout_data.setVisibility(View.VISIBLE);

        btn_reset.setVisibility(View.VISIBLE);
        txt_error.setText(ManagerResources.getStringIdNormalized(context, R.string.txt_noSearch, null));
        txt_error.setVisibility(View.VISIBLE);
    }

    /**
     * Exibe o Frangment de Localização na Tela
     */
    public void showFragment() {
        // Instancia a classe do Fragment ---> Envia um Context p/ acessar o Banco de Dados
        FragmentFeedbackLocation fragmentFeedbackLocation = FragmentFeedbackLocation.newInstance(context);

        // Dá suporte ao Fragment
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Cria uma ação a ser executada
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.fragment_location, fragmentFeedbackLocation).
                addToBackStack(null).commit();

        // Altera o Icone para Setinha para Cima
        btnFragment.setImageResource(R.drawable.ic_keyboard_arrow_up);
        activeFragment = true;
    }

    /**
     * Fecha o Frangment de Localização da Tela
     */
    public void closeFragment() {
        //Obtem o gerenciametno de Fragment
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Instancia a Classe do Fragment e busca se existe um Fragment Ativo no ID Informado
        FragmentFeedbackLocation fragmentFeedbackLocation = (FragmentFeedbackLocation) fragmentManager.
                findFragmentById(R.id.fragment_location);

        // Existe o Fragment no id Informado
        if (fragmentFeedbackLocation != null) {
            // Cria uma ação a ser executada
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.remove(fragmentFeedbackLocation).commit();
        }

        // Altera o Icone para Setinha para Baixo
        btnFragment.setImageResource(R.drawable.ic_keyboard_arrow_down);
        activeFragment = false;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Salva/Armazena o estado do Fragment, usando uma key e um valor Boolean
        savedInstanceState.putBoolean(STATE_FRAGMENT, activeFragment);
        super.onSaveInstanceState(savedInstanceState);
    }
}