package com.example.maquiagem.view.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.maquiagem.view.AlertDialogs;
import com.example.maquiagem.view.FeedbackLocation;
import com.example.maquiagem.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationActivity extends AppCompatActivity {

    private TextView showAddress;
    private ImageButton btnFragment;

    private double latitude = 0;
    private double longitude = 0;
    private boolean activeFragment = false;
    static final String STATE_FRAGMENT = "STATE FRAGMENT";

    AlertDialogs dialogs = new AlertDialogs();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        // Criação da ToolBar e Criação da seta de voltar
        Toolbar toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        // Icon de voltar para a Tela Home
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_return_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        btnFragment = findViewById(R.id.btn_showFragment);
        btnFragment.setImageResource(R.drawable.ic_keyboard_arrow_down);

        if (requiredForLocation()) {
            getLastLocationUser();
        }
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


    // Metodo que valida se há conexão e GPS ativos
    public boolean requiredForLocation(){

        // Controla os serviços de Localziação
        LocationManager service = (LocationManager)
                getSystemService(LOCATION_SERVICE);


        // Verifica se o GPS está ativo e se possui conexão com a Internet
        boolean gpsIsEnabled = false;
        gpsIsEnabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

        //Valida a conexão com Internet
        ConnectivityManager connectionManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectionManager != null) {
            // Obteve os serviços de conexão
            networkInfo = connectionManager.getActiveNetworkInfo();
        }

        // Caso o Conection Manager não tenha sido Inciado (Defalult = Null)
        if (networkInfo == null){
            dialogs.message(LocationActivity.this,"Sem Internet",
                    getString(R.string.error_connection)).show();

            return false;
        } else if (!gpsIsEnabled){
            dialogs.message(LocationActivity.this,"Sem GPS",
                    getString(R.string.error_noGps)).show();
            return false;

        } else{
            // Pega a localização do Usuario
            return true;
        }
    }

    // Metodo que recupera a Ultima Localização Conhecida
    public void getLastLocationUser() {

        showAddress = findViewById(R.id.txt_location);

        // Serviço do Google para Manipular Localizações
        FusedLocationProviderClient fusedLocationClient =LocationServices.
                getFusedLocationProviderClient(getApplicationContext());

        // Verifica se a permissão de Locaçização  foi Concedida
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION},0);
            return;
        } else {
            Log.d("PERMISSÕES", "\nPermitido o uso do Local");
        }

        // Recupera a Ultima Localização
        fusedLocationClient.getLastLocation().
            addOnSuccessListener(this,
                new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        List<Address> addresses = new ArrayList<>();
                        Geocoder geocoder = new Geocoder(getApplicationContext(),
                                Locale.getDefault());

                        if (location == null){
                            Log.e("LOCATION", "\nErro na Localização\n" + location);

                            dialogs.message(LocationActivity.this,
                                    "Não encontramos a Localização",
                                    getString(R.string.error_location)).show();
                            return;
                        }

                        try {
                            // Tenta obter a Latitude e Longitude do Local atual
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            // Geolocalização Reversa (Longitude + Latidude = Endereço)
                            // Limita a somente 1 Resultado
                            addresses = geocoder.getFromLocation(latitude, longitude, 1);
                            // Imprime o resultado da List de Endereços (ArrayList)
                            Log.i("LIST ENDEREÇO","\n" + addresses.get(0).toString()
                                    + "\n" + addresses.get(0).getAddressLine(0));
                            // Caso não retorne nenhum endereço

                        } catch (IOException e) {
                            // Tratamento de Erro da Localização ---> Erro no Processo
                            Log.e("GETLOCATION", "\nErro na recuperação do endereço " +
                                    "pela Latitude e Longitude.\n" + e);
                        }
                        catch (IllegalArgumentException illegalArgumentException) {
                            // Tratamento de Erro da Localização ---> Formato não aceito
                            Log.e("ERROR FORMAT","\nLatitude = " + location.getLatitude() +
                                    ", Longitude = " +
                                    location.getLongitude(), illegalArgumentException);
                        }
                        finally {
                            Log.d("FINAL LOCATION", "\nLocalização Final" + addresses.toString());
                            if(addresses.isEmpty()){
                                showAddress.setText(R.string.error_searchLocation);
                            }
                            else{
                                // Exibe o Endereço
                                showAddress.setText(addresses.get(0).getAddressLine(0));
                            }

                        }
                    }
            })

            // Caso não consiga obter a ultima Localização ---> Erro
            .addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TAG", "\nonFailure: ", e);
                    }
                }
            );
    }


    // Mostra o Fragment na Tela
    public void showFragment(){
        // Instancia a classe do Fragment
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