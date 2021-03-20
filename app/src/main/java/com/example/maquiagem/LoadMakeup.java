package com.example.maquiagem;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

public class LoadMakeup extends AsyncTaskLoader<String> {
    private String type;
    private String brand;

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

    @Nullable
    @Override
    public String loadInBackground() {
        return InternetTools.searchMakeup(type, brand);
    }

}
