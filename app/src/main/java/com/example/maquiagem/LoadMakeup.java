package com.example.maquiagem;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

public class LoadMakeup extends AsyncTaskLoader<String> {
    private final String type;
    private final String brand;

    LoadMakeup(Context context, String dataType, String dataBrand) {
        super(context);
        type = dataType;
        brand = dataBrand;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    // Executa a Busca na API em Background
    @Nullable
    @Override
    public String loadInBackground() {
        return InternetTools.searchMakeup(type, brand);
    }

}
