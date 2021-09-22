package com.example.maquiagem.controller;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class ManagerKeyboard {

    private final Context context;
    private final InputMethodManager keyboardManager;

    public ManagerKeyboard(Context context) {
        this.context = context;
        // Obtem o Service do Keyboard
        this.keyboardManager = (InputMethodManager) context.
                getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    // Abre o Teclado
    public void openKeyboard(View viewOpen) {
        // Se ontem o controlador do Teclado = Abre
        if (keyboardManager != null) {
            keyboardManager.showSoftInput(viewOpen, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    // Fecha o Teclado
    public void closeKeyboard(Activity activity) {
        // Se obtem o controlador do Teclado = Fecha
        if (keyboardManager != null) {
            keyboardManager.hideSoftInputFromWindow(activity.getWindow().
                    getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
