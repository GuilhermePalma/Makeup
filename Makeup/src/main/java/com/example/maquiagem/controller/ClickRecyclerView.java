package com.example.maquiagem.controller;

import com.example.maquiagem.model.Makeup;

// Trata os Cliques no RecyclerView
public interface ClickRecyclerView {
    void onClickProduct(Makeup makeup_select);

    void onClickFavorite(Makeup makeup_select);
}
