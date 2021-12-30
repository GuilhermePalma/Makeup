package com.example.maquiagem.view;

import static com.example.maquiagem.controller.ManagerSharedPreferences.KEY_COUNT_UPDATE;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.widget.RemoteViews;

import com.example.maquiagem.R;
import com.example.maquiagem.controller.ManagerDatabase;
import com.example.maquiagem.controller.ManagerSharedPreferences;

import java.text.DateFormat;
import java.util.Date;

public class WidgetApp extends AppWidgetProvider {

    // Metodo que atualiza o Widget
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Obtem o arquivo que armazena o contador
        ManagerSharedPreferences managerPreferences = new ManagerSharedPreferences(context);
        final String key_preferences = KEY_COUNT_UPDATE + appWidgetId;

        // Obtem a Data Atual para informar no Widget (Utlima Atualização)
        String dateString = DateFormat.
                getTimeInstance(DateFormat.SHORT).format(new Date());

        /* RemoteView = Classe que descreve uma view que pode ser exibida em outro processo.
           Ela é inflada a partir de um Layout (widget_app) que possui suas operações que podem
           alterar o conteudo desse Layout */
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_app);

        // Instancia o HelperDatabase
        ManagerDatabase database = new ManagerDatabase(context);

        int correct = database.amountCorrectLocation();
        int wrong = database.amountWrongLocation();

        // Mostra o ID do Widget
        views.setTextViewText(R.id.appwidget_id_label, Html.fromHtml(
                String.format(context.getString(R.string.txt_idWidget), appWidgetId)));

        // Mostra a Hora e a quantidade das Atualizações
        views.setTextViewText(R.id.appwidget_updateTime_label, Html.fromHtml(
                String.format(context.getString(R.string.txt_updateTime), dateString)));

        // Mostra a Hora e a quantidade das Atualizações
        views.setTextViewText(R.id.appwidget_updateNumber_label, Html.fromHtml(
                String.format(context.getString(R.string.txt_lastUpdate),
                        managerPreferences.getWidgetCount(key_preferences))));

        // Icon do ImageButton
        views.setImageViewResource(R.id.btn_update, R.drawable.ic_autorenew);

        // Buscas de Maquiagem
        views.setTextViewText(R.id.txtWidget_search,
                context.getResources().
                        getString(R.string.txt_brandTypeFormat,
                                Integer.toString(database.amountMakeupSearch())
                        )
        );

        // Mostra todas as posições Pesquisadas
        views.setTextViewText(R.id.txtWidget_resultLocation, Integer.
                toString(database.amountLocation()));

        // Mostra as Posições Certas e Erradas na Tela
        views.setTextViewText(R.id.txtWidget_correctResult, Integer.toString(correct));
        views.setTextViewText(R.id.txtWidget_wrongResult, Integer.toString(wrong));

        // Armazena o Numero de Atualizações nas SharedPreferences
        managerPreferences.setIncrementCountWidget(key_preferences);

        // Botão de Update
        Intent intentUpdate = new Intent(context, WidgetApp.class);

        // ACTION_APPWIDGET_UPDATE definido no Manisfest = Ação da Intent é de Update
        intentUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        // Inclui o id do widget para ser atualizado como extra da intent
        // Usado para chamar varios appWidgetsID. Inclui o ID atual para ser atualizado
        int[] idArray = new int[]{appWidgetId};
        intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray);

        /* Envolve em um PendingIntent para enviar um Broadcast.
           Usa o ID do WidgetApp p/ que cada Intent seja unico.
           Ultimo Parametro = Recoloca os ExtraData Alterados */
        PendingIntent pendingUpdate = PendingIntent.getBroadcast(context,
                appWidgetId, intentUpdate, PendingIntent.FLAG_UPDATE_CURRENT);

        // Associa o clique no Botão com o PendingUpdate criado acima
        views.setOnClickPendingIntent(R.id.btn_update, pendingUpdate);

        // Atualiza o Widget usando o ID e a RemoteView
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    /**
     * Sobrescreve o metodo onUpdate para lidar com todas as atualizações do Widget
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

}