package com.example.maquiagem.controller;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Classe Responsavel por controlar Recursos Utilizados pela Aplicação. Todos os seus metodos são
 * static, uma vez que não irá armazenar dados, apenas executar
 */
public class ManagerResources {

    /**
     * Constante que define que a {@link String} não possui Tamanho maximo
     *
     * @see #customStringFormat(String, String, int)
     */
    public static final int NO_MAX_LENGTH = -1;

    /**
     * Define se o Dispositivo possui conexão com a Internet
     *
     * @param context {@link Context} utilizado para obter e manipular os Recursos do Dispositivo
     * @return true|false
     */
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

    /**
     * Metodo Responsavel por Informar se o Dispositivo possui o GPS ativo
     *
     * @param context {@link Context} utilizado para obter e manipular os Recursos do Dispositivo
     * @return true|false
     */
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

    /**
     * Metodo Responsavel por validar se uma {@link String} é Nula ou Vazia
     *
     * @param string_check {@link String} que será Validada
     * @return true|false
     */
    public static boolean isNullOrEmpty(String string_check) {
        return string_check == null || string_check.equals("") || string_check.isEmpty();
    }

    /**
     * Metodo Responsavel por retornar uma {@link String} a partir do seu ID, já com as Formatações
     * necessarias
     *
     * @param context     {@link Context} utilizado para obter e manipular os Recursos do Dispositivo
     * @param id_string   ID de onde a String será obtida
     * @param args_string Possiveis Valores que serão substituidos na String (Na ausensica, passar null)
     * @return {@link String}|""
     * @see #getStringIdNormalized(Context, int, Object[])
     */
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

    /**
     * Metodo Responsavel por normalizar a uma String que pode conter caracteres HTML
     *
     * @param string_not_normalized {@link String} que será normalizada
     * @return {@link String}|""
     */
    public static String getNormalizedString(String string_not_normalized) {
        try {
            if (isNullOrEmpty(string_not_normalized)) return "";
            return Html.fromHtml(string_not_normalized).toString();
        } catch (Exception ex) {
            Log.e("ERROR STRING", "Não foi foi possivel Normalizar a String. Exceção: " + ex);
            ex.printStackTrace();
            return "";
        }
    }

    /**
     * Metodo Responsavel por Abrir o teclado na {@link View} Informada
     *
     * @param context  {@link Context} utilizado para obter o Serviço de Teclado
     * @param viewOpen {@link View} onde o teclado será aberto
     */
    public static void openKeyboard(Context context, View viewOpen) {

        InputMethodManager keyboardManager = (InputMethodManager) context.
                getSystemService(Context.INPUT_METHOD_SERVICE);
        // Se ontem o controlador do Teclado = Abre
        if (keyboardManager != null) {
            keyboardManager.showSoftInput(viewOpen, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    /**
     * Metodo responsavel por Fehcar o Teclado
     *
     * @param context  {@link Context} utilziado para obter o Serviço de Teclado
     * @param activity {@link Activity} em que o Teclado será aberto
     */
    public static void closeKeyboard(Context context, Activity activity) {
        InputMethodManager keyboardManager = (InputMethodManager) context.
                getSystemService(Context.INPUT_METHOD_SERVICE);

        // Se obtem o controlador do Teclado = Fecha
        if (keyboardManager != null) {
            keyboardManager.hideSoftInputFromWindow(activity.getWindow().
                    getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * Metodo responsavel por Customizar a {@link String}. Realiza um Corte, respeitando o Tamanho
     * maximo e da String e, caso deseje, adiciona um Prefixo na String
     *
     * @param string_before {@link String} que será customizada
     * @param prefix        Prefixo adicionado à {@link String} (Caso não deseje, passe null)
     * @param max_length    Tamanho maximo da String
     * @return {String}|""
     * @see #NO_MAX_LENGTH
     */
    public static String customStringFormat(String string_before, String prefix, int max_length) {
        try {
            // Formata o Inicio (Prefixo) da String
            String temporary_format = ManagerResources.isNullOrEmpty(prefix) ? "" : prefix;

            // Define o Tamanho Maximo como o Comprimento Total da String
            if (max_length == NO_MAX_LENGTH || max_length <= 0) {
                max_length = string_before.length();
            }

            // Formata o Tamanho da String e Retorna
            temporary_format += string_before.substring(0, Math.min(max_length, string_before.length()));
            return temporary_format;
        } catch (Exception ex) {
            // Tratamento de Possiveis Exceções (Index maior que o temanho, ...)
            Log.e("ERROR NICKNAME", "Erro na Formatação do Nickname. Exceção: " + ex);
            ex.printStackTrace();
            return "";
        }
    }

}
