package com.example.maquiagem.controller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maquiagem.R;
import com.google.android.material.card.MaterialCardView;

public class RecyclerViewCategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Constantes do Tipo de View
    public static final int POSITION_HEADER = 0;
    public static final int POSITION_ITEM = 1;

    private final ClickCategory clickCategory;
    private final View viewHeader;
    private final String[] categoriesName;

    public RecyclerViewCategoriesAdapter(View viewHeader, String[] categoriesName, ClickCategory clickCategory) {
        this.categoriesName = categoriesName;
        this.viewHeader = viewHeader;
        this.clickCategory = clickCategory;
    }

    /**
     * Instancia a Classe que contem as Instancias dos Itens dentro da View
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == POSITION_HEADER && viewHeader != null) {
            return new ViewHolderHeader(viewHeader);
        } else {
            View viewItem = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_item_category, parent, false);
            return new ViewHolderItem(viewItem);
        }
    }

    /**
     * Configura os Atributos dentro da View
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderItem) {
            // Subtrai 1 do Index por conta do Header
            final int realIndex = position - 1;

            // Configura o Titulo e o Clique no Item
            final String titleCategory = categoriesName[realIndex];
            ((ViewHolderItem) holder).textTitle.setText(titleCategory);
            ((ViewHolderItem) holder).cardView.setOnClickListener(
                    (v) -> clickCategory.onClickCategory(titleCategory));
        }
    }

    /**
     * Retorna o Tipo da View
     */
    @Override
    public int getItemViewType(int position) {
        return viewHeader != null && position == 0 ? POSITION_HEADER : POSITION_ITEM;
    }

    /**
     * Retorna a Quantidade de Itens no Recycler View
     */
    @Override
    public int getItemCount() {
        int quantityItems = categoriesName == null ? 0 : categoriesName.length;
        return viewHeader != null ? quantityItems + 1 : quantityItems;
    }

    protected static class ViewHolderHeader extends RecyclerView.ViewHolder {
        public ViewHolderHeader(@NonNull View itemView) {
            super(itemView);
        }
    }

    protected static class ViewHolderItem extends RecyclerView.ViewHolder {

        private final MaterialCardView cardView;
        private final TextView textTitle;

        public ViewHolderItem(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView_itemCategory);
            textTitle = itemView.findViewById(R.id.text_nameCategory);
        }
    }

}
