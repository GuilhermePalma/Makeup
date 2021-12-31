package com.example.maquiagem.view;

import android.app.Activity;
import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.example.maquiagem.R;
import com.example.maquiagem.controller.ManagerResources;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/**
 * Classe que customiza {@link AlertDialog}
 */
public class CustomAlertDialog {

    private final Context context;

    /**
     * Construtor da Classe {@link CustomAlertDialog}
     *
     * @param context {@link Context} utilizado durante a Classe
     */
    public CustomAlertDialog(Context context) {
        this.context = context;
    }

    /**
     * Metodo que retorna um AlertDialog com Titulo, Mensagem e um Botão, configurado no Design do
     * Material UI
     *
     * @param isCancelable        Valor booleano que define se o Usuario irá conseguir retornar ou não do
     *                            AlertDialog
     * @param id_message          ID da Mensagem que será Exibida (Já Formata textos com Caracteres HTML)
     * @param id_title            ID do Titulo que será exibido (Já Formata textos com Caracteres HTML)
     * @param valuesStringMessage Valores que serão inseridos na Mensagem
     * @param valuesStringTitle   Valores que serão inseridos no Titulo
     * @return {@link AlertDialog}
     */
    public AlertDialog defaultMessage(int id_title, int id_message, Object[] valuesStringTitle,
                                      Object[] valuesStringMessage, boolean isCancelable) {
        // Cria o alertDialog
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(context);

        String txt_title = ManagerResources.getStringIdNormalized(context, id_title, valuesStringTitle);
        String txt_message = ManagerResources.getStringIdNormalized(context, id_message, valuesStringMessage);

        // Configura Titulo e a Mensagem
        alertDialogBuilder.setTitle(txt_title);
        alertDialogBuilder.setMessage(txt_message);

        // Define se o Usuario poderá ou não Fechar o AlertDialog
        alertDialogBuilder.setCancelable(isCancelable);

        // Configura o Botão e o clique para Fechar
        alertDialogBuilder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        return alertDialogBuilder.create();
    }

    /**
     * Metodo que retorna um AlertDialog com Titulo, Mensagem e um Botão que fecha a Activity passada.
     * Esse {@link AlertDialog} é configurado no Design do Material UI
     *
     * @param activity            {@link Activity} que será fechada no AlertDialog
     * @param id_message          ID da Mensagem que será Exibida (Já Formata textos com Caracteres HTML)
     * @param id_title            ID do Titulo que será exibido (Já Formata textos com Caracteres HTML)
     * @param valuesStringMessage Valores que serão inseridos na Mensagem
     * @param valuesStringTitle   Valores que serão inseridos no Titulo
     * @return {@link AlertDialog}
     */
    public AlertDialog messageWithCloseWindow(Activity activity, int id_title, int id_message,
                                              Object[] valuesStringTitle, Object[] valuesStringMessage) {

        // Cria o alertDialog
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(context);

        String txt_title = ManagerResources.getStringIdNormalized(context, id_title, valuesStringTitle);
        String txt_message = ManagerResources.getStringIdNormalized(context, id_message, valuesStringMessage);

        // Configura Titulo e a Mensagem
        alertDialogBuilder.setTitle(txt_title);
        alertDialogBuilder.setMessage(txt_message);

        // Não Permite que o Usuario Feche o Dialog sem clicar no Botão
        alertDialogBuilder.setCancelable(false);

        // Configura o Botão e o clique para Fechar
        alertDialogBuilder.setNegativeButton(context.getString(R.string.btn_close),
                (dialog, which) -> {
                    activity.finish();
                    dialog.dismiss();
                });

        return alertDialogBuilder.create();
    }
}
