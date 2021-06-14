package com.example.maquiagem;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import com.example.maquiagem.model.DataBaseMakeup;

import java.text.DateFormat;
import java.util.Date;

/**
 * Implementation of App Widget functionality.
 */
public class WidgetApp extends AppWidgetProvider {

    // Name of shared preferences file & key
    private static final String SHARED_PREFERENCES_FILE = "com.example.android.appapimakeup";
    private static final String COUNT_UPDATE_KEY = "contadorWidget";


    /**
     * Sobrescreve o metodo onUpdate para lidar com todas as atualizações do Wodget
     *
     * @param context          Contexto da Aplicação
     * @param appWidgetManager Widget manager.
     * @param appWidgetIds     Array com ID do Widget
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        // Laço de repetição que atualiza  ID por ID de cada Widget pendente
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }


    // Metodo que atualiza o Widget
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Obtem o arquivo que armazena o contador
        SharedPreferences preferences = context.
                getSharedPreferences(SHARED_PREFERENCES_FILE, 0);
        // Recupera o Valor. Caso não exista ---> Retorna 0
        int counter = preferences.getInt(COUNT_UPDATE_KEY + appWidgetId, 0);
        // Adiciona +1 no contador de Atualização
        counter++;

        // Obtem a Data Atual para informar no Widget (Utlima Atualização)
        String dateString = DateFormat.
                getTimeInstance(DateFormat.SHORT).format(new Date());

        /* RemoteView = Classe que descreve uma view que pode ser exibida em outro processo.
           Ela é inflada a partir de um Layout (widget_app) que possui suas operações que podem
           alterar o conteudo desse Layout */
        RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widget_app);

        // Instancia o HelperDatabase
        DataBaseMakeup database = new DataBaseMakeup(context);

        int correct = database.getCorrectLocation();
        int wrong = database.getWrongLocation();

        // Mostra o ID do Widget
        views.setTextViewText(R.id.appwidget_id, Integer.toString(appWidgetId));

        // Icon do ImageButton
        views.setImageViewResource(R.id.imgBtn_update, R.drawable.ic_autorenew);

        // Mostra a Hora e a quantidade das Atualizações
        views.setTextViewText(R.id.appwidget_update,  context.getResources().getString(
                R.string.txt_formatDateCount, counter, dateString));

        // Buscas de Maquiagem
        views.setTextViewText(R.id.txtWidget_search,
                context.getResources().
                        getString( R.string.txt_brandTypeFormat,
                                Integer.toString(database.getProductsSearch())
                        )
        );

        // Mostra todas as posições Pesquisadas
        views.setTextViewText(R.id.txtWidget_resultLocation,Integer.
                toString(database.getAmountLocation()));

        // Mostra as Posições Certas e Erradas na Tela
        views.setTextViewText(R.id.txtWidget_correctResult, Integer.toString(correct));
        views.setTextViewText(R.id.txtWidget_wrongResult, Integer.toString(wrong));


        // Armazena a contagem/numero em um SharedPreferences
        SharedPreferences.Editor preferencesEditor = preferences.edit();
        // Usa a chave das Preferences, Seleciona pelo ID atual do Widget e Substitui pelo novo
        preferencesEditor.putInt(COUNT_UPDATE_KEY + appWidgetId, counter);
        preferencesEditor.apply();

        // Botão de Update
        Intent intentUpdate = new Intent(context, WidgetApp.class);

        // ACTION_APPWIDGET_UPDATE definido no Manisfest = Ação da Intent é de Update
        intentUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        // Inclui o id do widget para ser atualizado como extra da intent
        // Usado para chamar varios appWidgetsID. Inclui o ID atual para ser atualizado
        int[] idArray = new int[]{appWidgetId};
        intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray);

        // Envolve em um PendingIntent para enviar um Broadcast.
        // Usa o ID do WidgetApp p/ que cada Intent seja unico.
        // Ultimo Parametro = Recoloca os ExtraData Alterados
        PendingIntent pendingUpdate = PendingIntent.getBroadcast(context,
                appWidgetId, intentUpdate, PendingIntent.FLAG_UPDATE_CURRENT);

        // Associa o clique no Botão com o PendingUpdate criado acima
        views.setOnClickPendingIntent(R.id.imgBtn_update, pendingUpdate);

        // Atualiza o Widget usando o ID e a RemoteView
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

}