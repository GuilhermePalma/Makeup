package com.example.maquiagem.controller;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.util.Log;

public class ManagerResources {

    public static boolean hasConnectionInternet(Context context) {
        try {
            ConnectivityManager connectionManager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo;

            // Valida se o serviço de Internet está ativo
            if (connectionManager != null) {
                networkInfo = connectionManager.getActiveNetworkInfo();

                // Valida se existe Conexão ativa
                if (networkInfo != null && networkInfo.isConnected()) {
                    return true;
                } else {
                    Log.e("NO CONECTED", "Erro na conexão com a Internet.\nConexão: " + networkInfo);
                    return false;
                }

            } else {
                Log.e("NO SERVICE", "Não foi Possivel obter o serviço de Internet.");
                return false;
            }

        } catch (Exception ex) {
            Log.e("NO INTERNET", "Não foi foi possivel obter a Internet. Exceção: " + ex);
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean hasConnectionGps(Context context) {
        // Obtem o Gerenciador de Serviços de Localziação
        LocationManager service = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        try {
            // Verifica se o GPS está ativo e Habilitado para Usar
            return service.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
            Log.e("NO GPS", "Não foi foi possivel obter o GPS. Exceção: " + ex);
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean isNullOrEmpty(String string_check) {
        return string_check == null || string_check.equals("") || string_check.isEmpty();
    }

    public static String getStringIdNormalized(Context context, int id_string, Object[] args_string) {
        try {
            if (id_string == 0) return "";

            String temporary_string = args_string != null
                    ? context.getString(id_string, args_string)
                    : context.getString(id_string);
            if (isNullOrEmpty(temporary_string)) return "";

            return isNullOrEmpty(getNormalizedString(temporary_string))
                    ? "" : getNormalizedString(temporary_string);
        } catch (Exception ex) {
            Log.e("ERROR STRING", "Não foi foi possivel obter a String. Exceção: " + ex);
            ex.printStackTrace();
            return "";
        }
    }

    public static String getNormalizedString(String string_not_normalized) {
        try {
            return Html.fromHtml(string_not_normalized).toString();
        } catch (Exception ex) {
            Log.e("ERROR STRING", "Não foi foi possivel Normalizar a String. Exceção: " + ex);
            ex.printStackTrace();
            return "";
        }
    }

}
