package com.example.maquiagem.controller;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maquiagem.R;
import com.example.maquiagem.model.Makeup;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

public class RecyclerListMakeup extends RecyclerView.Adapter<RecyclerListMakeup.ViewHolderListMakeup> {

    private final ClickRecyclerView clickRecyclerView;
    private final Context context;
    private final int NUMBER_HEADER = 0;
    private final int NUMBER_ITEM = 1;
    private final List<Makeup> makeupList;
    // Context e List/Array (mostar/armazenar os dados) e ClickRecyclerView (Trata os Cliques)
    private final View header;

    // Contrutor da Calsse
    public RecyclerListMakeup(Context context, View header,
                              List<Makeup> list, ClickRecyclerView clickRecyclerView) {
        this.context = context;
        this.header = header;
        this.makeupList = list;
        this.clickRecyclerView = clickRecyclerView;
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? NUMBER_HEADER : NUMBER_ITEM;
    }

    //Cria o ViewHolder; Instancia com o valor do Layout usado
    @NonNull
    @Override
    public ViewHolderListMakeup onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == NUMBER_HEADER && header != null) {
            // Infla o Layout do Header
            return new ViewHolderListMakeup(header);
        } else {
            // Cria uma View e Instancia com o Layout do RecyclerView
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.layout_recycler_list, viewGroup, false);
            // Infla o Layout dos Itens
            return new ViewHolderListMakeup(itemView);
        }
    }

    // Recupera os Valores do ListArray e Insere os Valores de Acordo com a Posição no RecyclerView
    @Override
    public void onBindViewHolder(@NonNull ViewHolderListMakeup holder, int position) {

        // Caso o Layout Superior Exista, não configura um Item do RecyclerView
        if (header != null && isHeader(position)) return;

        // Subtrai 1 da Posição por conta do Header
        int position_real = position - 1;
        Makeup makeup = makeupList.get(position_real);

        String name = makeup.getName();
        String name_formatted;

        // Formatação no Tamanho dos Nomes
        if (position % 5 == 0) {
            // Caso seja o Card Maior 2x1
            if (name.length() > 29) {
                name_formatted = name.substring(0, 28);
            } else {
                name_formatted = name;
            }
        } else {
            // Caso seja o Card Menor 1x1
            if (name.length() > 13) {
                name_formatted = name.substring(0, 12);
            } else {
                name_formatted = name;
            }
        }

        // Exibe os Dados na Tela
        holder.name.setText(name_formatted);
        holder.currency_price.setText(Html.fromHtml(context.getString(R.string.formatted_currencyPrice,
                makeup.getCurrency(), makeup.getPrice())));

        // TODO: Implementação da Contagem de Favoritos na API
        Random numberRandom = new Random();
        int random_amountFavorite = numberRandom.nextInt(1500);
        holder.amount_favorite.setText(context.getString
                (R.string.txt_quantityFavorite, random_amountFavorite));

        // Recupera se o Produto foi Favoritado ou não (Coloca o Coração em Vermelho ou Branco)
        holder.checkBox_favorite.setChecked(makeup.isFavorite());

        // Biblioteca Picasso (Converte URL da IMG ---> IMG)
        Picasso.with(context).load(makeup.getUrlImage())
                .error(R.drawable.makeup_no_image)
                .into(holder.image);

        // Listeners dos Cliques nos Itens do RecyclerView
        holder.image.setOnClickListener(v -> clickRecyclerView.onClickProduct(makeup));
        holder.cardView.setOnClickListener(v -> clickRecyclerView.onClickProduct(makeup));
        holder.checkBox_favorite.setOnClickListener(v -> {
            // Muda p/ o estado oposto do CheckBox e Instancia o Clique
            holder.checkBox_favorite.setChecked(!makeup.isFavorite());
            clickRecyclerView.onClickFavorite(makeup);
        });
    }

    //Conta os Itens da Lista
    @Override
    public int getItemCount() {
        if (makeupList != null && !makeupList.isEmpty()) {
            return makeupList.size();
        } else return 0;
    }

    public boolean isHeader(int positionItem) {
        return positionItem == NUMBER_HEADER;
    }

    // Classe que retorna os campos usados já referenciados
    protected static class ViewHolderListMakeup extends RecyclerView.ViewHolder {

        private final MaterialCardView cardView;
        private final TextView name;
        private final TextView currency_price;
        private final TextView amount_favorite;
        private final ImageView image;
        private final CheckBox checkBox_favorite;

        // Recupera os valores definidos no Layout do RecycleAdpater
        protected ViewHolderListMakeup(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView_recyclerList);
            image = itemView.findViewById(R.id.image_productList);
            checkBox_favorite = itemView.findViewById(R.id.checkBox_favoriteList);
            name = itemView.findViewById(R.id.txt_nameMakeupList);
            currency_price = itemView.findViewById(R.id.txt_priceMakeupList);
            amount_favorite = itemView.findViewById(R.id.txt_amountFavorite);
        }
    }

}
