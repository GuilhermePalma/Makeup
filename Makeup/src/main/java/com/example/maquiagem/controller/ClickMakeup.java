package com.example.maquiagem.controller;

import com.example.maquiagem.model.entity.Makeup;

/**
 * Interface que controla as Ações dos Itens do RecyclerView {@link MakeupsAdapter}
 */
public interface ClickMakeup {
    /**
     * Metodo que será sobrescrito do Clique em um Produto
     */
    void onClickProduct(Makeup makeup_select);

    /**
     * Metodo que será sobrescrito do Clique de Favoritar um Produto
     */
    void onClickFavorite(Makeup makeup_select, int position_item);
}
