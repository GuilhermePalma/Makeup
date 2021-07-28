package com.example.maquiagem.view.activities;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.example.maquiagem.R;
import com.example.maquiagem.model.DataBaseMakeup;
import com.example.maquiagem.view.AlertDialogs;


public class  MainActivity extends AppCompatActivity{

    private EditText editType;
    private EditText editBrand;

    private AlertDialogs dialogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Criação da ToolBar
        Toolbar toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        // Define os EditText
        editType = findViewById(R.id.edit_type);
        editBrand = findViewById(R.id.edit_brand);

        dialogs = new AlertDialogs();
    }

    // Cria o menu na ToolBar
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    // Trata os cliques no Menu da TollBar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.location):
                // Possui Conexão
                if (connectionAvailable()){
                    // GPS ativo
                    if (gpsAvailable()){
                        startActivity(new Intent(this, LocationActivity.class));
                    } else {
                        dialogs.message(this,getString(R.string.title_noGps),
                                getString(R.string.error_noGps)).show();
                    }
                } else {
                    dialogs.message(this,getString(R.string.title_noConnection),
                            getString(R.string.error_connection)).show();
                }
                break;

            case (R.id.clearData):
                // Limpa o Banco de Dados
                DataBaseMakeup dataBaseHelper = new DataBaseMakeup(this);
                dataBaseHelper.clearTableMakeup();
                dataBaseHelper.clearTableLocation();
                dataBaseHelper.close();
                break;
            case (R.id.alter_theme):
                if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                break;
            case (R.id.sensor_menu):
                Intent sensor = new Intent(this,SensorActivity.class);
                startActivity(sensor);
                break;
            default:
                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    // Limpa os Dados da Tela
    public void clearWindow(){
        // Limpa os Campos
        editType.setText(R.string.string_empty);
        editBrand.setText(R.string.string_empty);
    }

    // Metodo do Botão Pesquisar
    public void BtnSearch(View view) {

        // Recupera os valores inseridos pelo Usario
        String infoType = editType.getText().toString();
        String infoBrand = editBrand.getText().toString();

        if (infoType.equals("")){
            editType.setError(getString(R.string.value_required));
            editBrand.requestFocus();
            return;
        }  else if(infoBrand.equals("")){
            editBrand.setError(getString(R.string.value_required));
            editBrand.requestFocus();
            return;
        }

        // Fecha o Teclado
        closeKeyboard(view);

        //Validação da Conexão Ativa
        if (connectionAvailable()){

            // Limpa os valores da Tela Inteira
            clearWindow();

            // Inicia a ActivityResult passando os Dados
            Intent activityResult = new Intent(this, ResultActivity.class);
            activityResult.putExtra("product_type", infoType);
            activityResult.putExtra("brand", infoBrand);
            startActivity(activityResult);

        } else{
            dialogs.message(MainActivity.this, getString(R.string.title_noConnection),
                     getString(R.string.error_connection)).show();
        }
    }


    private boolean connectionAvailable(){

        ConnectivityManager connectionManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo;

        // Valida se o serviço de Internet está ativo
        if (connectionManager != null) {
            networkInfo = connectionManager.getActiveNetworkInfo();

            // Valida se existe conexão ativa
            if (networkInfo != null && networkInfo.isConnected()){
                return true;
            } else{
                // Erro na Conexão
                Log.e("NO CONECTED", "\n Erro na conexão com a Internet" +
                        "\nConexão: " + networkInfo);
                return false;
            }

        } else{
            Log.e("NO SERVICE", "\n Erro no serviço de Internet" +
                    "\nServiço: " + connectionManager);
            return false;
        }

    }

    private boolean gpsAvailable(){
        // Gerencia os serviços de Localziação
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Verifica se o GPS está ativo e Habilitado para Usar
        try {
            return service.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (IllegalArgumentException exception){
            Log.e("ERROR SERVICE GPS", "Não foi foi possivel obter o " +
                    "Serviço de GPS\n" + exception);
            exception.printStackTrace();
            return false;
        }
    }

    private void closeKeyboard(View view){
        InputMethodManager keyboardManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        // Caso o Serviço do Teclado esteja disponivel
        if (keyboardManager != null) {
            keyboardManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}