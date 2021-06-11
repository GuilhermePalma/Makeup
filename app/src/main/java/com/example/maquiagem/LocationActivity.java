package com.example.maquiagem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationActivity extends AppCompatActivity {


    private TextView showAdress;
    public double latitude;
    public double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

    }

    public void pickLocation(View v) {

        showAdress = findViewById(R.id.txt_location);

        // Serviço do Google para Manipular Localizações
        FusedLocationProviderClient fusedLocationClient =LocationServices.
                getFusedLocationProviderClient(getApplicationContext());

        // Verifica se a permissão de Locaçização  foi Concedida
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION},1);

            return;
        } else {
            Log.d("PERMISSÕES", "Permitido o uso do Local ");
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

                        } catch (IOException e) {
                            // Tratamento de Erro da Localização ---> Erro no Processo
                            Log.e("GETLOCATION", "Erro na recuperação do endereço " +
                                    "pela Latitude e Longitude.\n" + e);
                        }
                        catch (IllegalArgumentException illegalArgumentException) {
                            // Tratamento de Erro da Localização ---> Formato não aceito
                            Log.e("TAG???","Latitude = " + location.getLatitude() +
                                    ", Longitude = " +
                                    location.getLongitude(), illegalArgumentException);
                        }
                        finally {
                            // Imprime o resultado da List de Endereços (ArrayList)
                            Log.e("LIST ENDEREÇO",addresses.get(0).toString()
                                    + "\n" + addresses.get(0).getAddressLine(0));


                            // Caso não retorne nenhum endereço
                            if(addresses == null || addresses.isEmpty()){
                                showAdress.setText("Vazio");
                            }
                            else{
                                // Exibe o Endereço
                                showAdress.setText(addresses.get(0).getAddressLine(0));
                            }

                        }
                    }
            })

            // Caso não consiga obter a utlima Localização ---> Erro
            .addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TAG", "onFailure: ", e);
                    }
                }
            );
    }

}