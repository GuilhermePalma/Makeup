package com.example.maquiagem.view.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Html;
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
import com.example.maquiagem.model.DataBaseMakeup;
import com.example.maquiagem.model.Location;
import com.example.maquiagem.view.fragments.FeedbackLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationActivity extends AppCompatActivity {

    private LinearLayout layout_data;
    public LinearLayout layout_address;

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
    static final String STATE_FRAGMENT = "STATE FRAGMENT";

    private com.example.maquiagem.model.Location classLocation;
    private DataBaseMakeup dataBaseMakeup;
    private int actualId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        setUpToolBar();
        recoveryId();

        classLocation = new com.example.maquiagem.model.Location();
        dataBaseMakeup = new DataBaseMakeup(this);

        loading_location.setMax(10);
        loading_location.setProgress(0);

        // Obtem a ultima localização inserida
        int lastIdLocation = dataBaseMakeup.amountLocation();
        actualId = lastIdLocation + 1;

        getLastLocation();
        listenerReset();

    }

    private void listenerReset() {
        btn_reset.setOnClickListener(v -> {
            overridePendingTransition(0, 0);
            finish();
            startActivity(getIntent());
        });
    }

    // Configura a ToolBar
    private void setUpToolBar() {
        // Criação da ToolBar e Criação da seta de voltar
        Toolbar toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        // Icon de voltar para a Tela Home
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_return_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    // Recupera os ID dos Itens
    private void recoveryId() {
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

    // Metodo ao clicar no ImageButton ---> Setinha em baixo da Localização
    public void buttonFragment(View view){
        // Caso o Fragment esteja ativo --> Fecha. Se não está ativo ----> Abre
        if (activeFragment) {
            closeFragment();
        } else {
            showFragment();
        }
    }

    // Metodo que recupera a Ultima Localização Conhecida
    public void getLastLocation() {

        // Serviço do Google para Manipular Localizações
        FusedLocationProviderClient fusedLocationClient =LocationServices.
                getFusedLocationProviderClient(getApplicationContext());

        // Verifica se a permissão de Localização  foi Concedida --> Se não = Solicita pro Usuario
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION},0);

            showError();
            return;
        }

        // Recupera a Ultima Localização
        fusedLocationClient.getLastLocation().
            addOnSuccessListener(this,
                    location -> {

                        List<Address> addresses = new ArrayList<>();
                        Geocoder geocoder = new Geocoder(getApplicationContext(),
                                Locale.getDefault());

                        if (location == null){
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
                            classLocation = new com.example.maquiagem.model.Location(
                                    actualId,
                                    addresses.get(0).getThoroughfare(),
                                    addresses.get(0).getFeatureName(),
                                    addresses.get(0).getAdminArea(),
                                    addresses.get(0).getSubAdminArea(),
                                    addresses.get(0).getSubThoroughfare(),
                                    addresses.get(0).getPostalCode(),
                                    addresses.get(0).getCountryName(),
                                    addresses.get(0).getCountryCode()
                            );

                            // Insere a Localização no BD e Exibe o Endereço
                            dataBaseMakeup.insertLocation(classLocation);

                        } catch (IOException e) {
                            // Tratamento de Erro da Localização ---> Erro no Processo
                            Log.e("GET LOCATION", "Erro na recuperação do endereço " +
                                    "pela Latitude e Longitude.\n" + e);
                        }
                        catch (IllegalArgumentException illegalArgumentException) {
                            // Tratamento de Erro da Localização ---> Formato não aceito
                            Log.e("ERROR FORMAT","Latitude = " + location.getLatitude() +
                                    ", Longitude = " +
                                    location.getLongitude(), illegalArgumentException);
                        }
                        finally {
                            Log.d("FINAL LOCATION", "Localização Final:\n" +
                                    addresses.toString());

                            if(addresses.isEmpty() || addresses == null){
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

    // Mostra o Layout do Endereço
    private void showWindow(Location location) {

        loading_location.setProgress(10);
        loading_location.setVisibility(View.GONE);
        txt_wait.setVisibility(View.GONE);

        layout_data.setVisibility(View.VISIBLE);
        btnFragment.setVisibility(View.VISIBLE);

        country.setText(Html.fromHtml(String.format(getString(R.string.txt_country),
                location.countryName, location.countryCode)));
        state.setText(Html.fromHtml(String.format(getString(R.string.txt_city),
                location.city, location.state)));
        address.setText(Html.fromHtml(String.format(getString(R.string.txt_address),
                location.address, location.district, location.number)));
        cep.setText(Html.fromHtml(String.format(getString(R.string.txt_cep), location.postalCode)));

        country.setVisibility(View.VISIBLE);
        state.setVisibility(View.VISIBLE);
        address.setVisibility(View.VISIBLE);
        cep.setVisibility(View.VISIBLE);
    }

    // Mostra o Layout de Erro
    public void showError(){

        loading_location.setProgress(10);
        loading_location.setVisibility(View.GONE);
        txt_wait.setVisibility(View.GONE);

        layout_address.setVisibility(View.GONE);
        layout_data.setVisibility(View.VISIBLE);

        btn_reset.setVisibility(View.VISIBLE);
        txt_error.setText(Html.fromHtml(getString(R.string.txt_noSearch)));
        txt_error.setVisibility(View.VISIBLE);
    }

    // Mostra o Fragment na Tela
    public void showFragment(){
        // Instancia a classe do Fragment ---> Envia um Context p/ acessar o Banco de Dados
        FeedbackLocation feedbackLocation = FeedbackLocation.newInstance(getApplicationContext());

        // Dá suporte ao Fragment
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Cria uma ação a ser executada
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.fragment_location, feedbackLocation).
                addToBackStack(null).commit();

        // Altera o Icone para Setinha para Cima
        btnFragment.setImageResource(R.drawable.ic_keyboard_arrow_up);
        activeFragment = true;
    }

    // Fecha o Fragment
    public void closeFragment(){
        //Obtem o gerenciametno de Fragment
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Instancia a Classe do Fragment e busca se existe um Fragment Ativo no ID Informado
        FeedbackLocation feedbackLocation = (FeedbackLocation) fragmentManager.
                findFragmentById(R.id.fragment_location);

        // Existe o Fragment no id Informado
        if (feedbackLocation != null){
            // Cria uma ação a ser executada
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.remove(feedbackLocation).commit();
        }

        // Altera o Icone para Setinha para Baixo
        btnFragment.setImageResource(R.drawable.ic_keyboard_arrow_down);
        activeFragment = false;
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Salva/Armazena o estado do Fragment, usando uma key e um valor Boolean
        savedInstanceState.putBoolean(STATE_FRAGMENT, activeFragment);
        super.onSaveInstanceState(savedInstanceState);
    }
}