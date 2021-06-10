package com.example.maquiagem.model;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.example.maquiagem.model.InternetTools;

// Metodo Assincrono da Pesquisa de Maquiagem
public class AsyncMakeup extends AsyncTaskLoader<String> {
    private final String type;
    private final String brand;

    // Construtor/Instancia da Classe
    public AsyncMakeup(Context context, String dataType, String dataBrand) {
        super(context);
        type = dataType;
        brand = dataBrand;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    // Executa a Busca na API em Background/2° Plano
    @Nullable
    @Override
    public String loadInBackground() {
        return InternetTools.searchMakeup(type, brand);
    }

}
