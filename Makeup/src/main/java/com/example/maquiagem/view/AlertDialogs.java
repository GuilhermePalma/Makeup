package com.example.maquiagem.view;

import android.app.Activity;
import android.content.Context;

import androidx.appcompat.app.AlertDialog;


public class AlertDialogs {

    public AlertDialog message(Context context, String title, String message) {

        // Cria o alertDialog
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Configura Titulo e a Mensagem
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);

        // Configura o Botão e o clique para Fechar
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                (dialog, which) -> dialog.dismiss());

        return alertDialog;
    }

    public AlertDialog messageWithCloseWindow(Activity activity, Context context,
                                              String title, String message) {

        // Criação do AlertDialog
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);

        // Configura o Botão e o clique para Fechar
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Fechar",
                (dialog, which) -> {
                    activity.finish();
                    dialog.dismiss();
                });

        return alertDialog;
    }
}
