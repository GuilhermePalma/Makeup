package com.example.maquiagem.view;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;


public class AlertDialogs {

    public AlertDialog message(Context context, String title, String message){

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
}
