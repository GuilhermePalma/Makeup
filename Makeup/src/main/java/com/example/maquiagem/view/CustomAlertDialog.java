package com.example.maquiagem.view;

import android.app.Activity;
import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.example.maquiagem.R;
import com.example.maquiagem.controller.ManagerResources;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class CustomAlertDialog {

    private final Context context;

    public CustomAlertDialog(Context context) {
        this.context = context;
    }

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

    public AlertDialog messageWithCloseWindow(Activity activity, int id_title, int id_message, Object[] valuesStringTitle,
                                              Object[] valuesStringMessage) {

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
