package com.example.maquiagem;

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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationActivity extends AppCompatActivity {

    private TextView showAddress;
    private ImageButton showFragment;

    private double latitude;
    private double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        // Criação da ToolBar e Criação da seta de voltar
        Toolbar toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_return_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Valida conexão de Internet e GPS
        if (requiredForLocation()){
            getLastLocationUser();
        }

        showFragment = findViewById(R.id.btn_showFragment);

        showFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    // Metodo que valida se há conexão e GPS ativos
    public boolean requiredForLocation(){
        // Controla os serviços de Localziação
        LocationManager service = (LocationManager)
                getSystemService(LOCATION_SERVICE);

        //Valida a conexão com Internet
        ConnectivityManager connectionManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectionManager != null) {
            // Obteve os serviços de conexão
            networkInfo = connectionManager.getActiveNetworkInfo();
        }

        // Verifica se o GPS está ativo e se possui conexão com a Internet
        boolean gpsIsEnabled = false;
        gpsIsEnabled = service.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // Caso o Conection Manager não tenha sido Inciado (Defalult = Null)
        if (networkInfo == null){
            Snackbar notInternet =  Snackbar.make(
                    findViewById(R.id.layout_location),
                    R.string.error_connection,
                    Snackbar.LENGTH_LONG);
            notInternet.show();
            return false;
        } else if (!gpsIsEnabled){
            Snackbar noGps =  Snackbar.make(
                    findViewById(R.id.layout_location),
                    R.string.error_noGps,
                    Snackbar.LENGTH_LONG);
            noGps.show();
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
                    Manifest.permission.ACCESS_FINE_LOCATION},1);

            return;
        } else {
            Log.d("PERMISSÕES", "\nPermitido o uso do Local ");
        }

        // Recupera a Ultima Localização
        fusedLocationClient.getLastLocation().
            addOnSuccessListener(this,
                new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        List<Address> addresses = null;
                        Geocoder geocoder = new Geocoder(getApplicationContext(),
                                Locale.getDefault());

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
                            if(addresses == null){
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

}