package com.example.maquiagem.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.maquiagem.R;

public class SensorActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager sensorManager;
    Sensor sensorProximity;

    TextView txt_dataSensor;
    ConstraintLayout layoutSensor;

    Boolean sensorAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        // Criação da ToolBar e Criação da seta de voltar
        Toolbar toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        // Icon de voltar para a Tela Home
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_return_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        txt_dataSensor = findViewById(R.id.txt_distance);
        layoutSensor = findViewById(R.id.layout_sensor);

        // Gerencia os Sensores
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Tenta Obter o Sensor de Proximidade
        if(sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) == null){
            txt_dataSensor.setText(R.string.txt_errorSensor);
            sensorAvailable = false;
        } else {
            sensorProximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            sensorAvailable = true;
        }

    }

    // Metodo acionado quando ocorre mudança no Sensor
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (sensorAvailable) {
            txt_dataSensor.setText("Distancia: " + event.values[0] + " cm");
            if (event.values[0] <= 0) {
                layoutSensor.setBackgroundColor(getResources().getColor(R.color.red));
            } else{
                layoutSensor.setBackgroundColor(getResources().getColor(R.color.green));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // Disponibiliza o Sensor para ser usado durante a Execução da Activity
    @Override
    protected void onResume() {
        super.onResume();
        if(sensorAvailable){
            sensorManager.registerListener(this, sensorProximity,
                    SensorManager.SENSOR_DELAY_FASTEST);
        }
    }

    // Quando o usuario sai da Aplicação ---> Pausa os eventos
    @Override
    protected void onPause() {
        super.onPause();
        if(sensorAvailable){
            sensorManager.unregisterListener(this);
        }
    }


}