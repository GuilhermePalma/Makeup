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

// Classe Responsavel pelo controle do RecyclerView E Adapter dos Campos Usados
public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.RecyclerViewHolder> {

    // Context e List/Array (mostar/armazenar os dados) e ClickRecyclerView (Trata os Cliques)
    private final List<Makeup> makeupList;
    private final ClickRecyclerView clickRecyclerView;
    private final Context context;

    // Contrutor da Calsse
    public RecycleAdapter(Context context, List<Makeup> list, ClickRecyclerView clickRecyclerView) {
        this.context = context;
        this.makeupList = list;
        this.clickRecyclerView = clickRecyclerView;
    }

    //Cria o ViewHolder; Instancia com o valor do Layout usado
    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        //Cria uma View e Instancia com o Layout Usado
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_recycler_view, viewGroup, false);

        return new RecyclerViewHolder(itemView);
    }

    // Recupera os Valores do ListArray e Insere os Valores de Acordo com a Posição no RecyclerView
    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {

        Makeup makeup = makeupList.get(position);

        String name = makeup.getName();
        String name_formatted;

        // Formatação no Tamanho dos Nomes
        if (name.length() > 13) {
            name_formatted = name.substring(0, 12);
        } else {
            name_formatted = name;
        }

        // Mostra os Dados na Tela
        holder.name.setText(name_formatted);
        holder.currency_price.setText(Html.fromHtml(context.getString(R.string.formatted_currencyPrice,
                makeup.getCurrency(), makeup.getPrice())));

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
            makeup.setFavorite(!makeup.isFavorite());
            holder.checkBox_favorite.setChecked(makeup.isFavorite());
            clickRecyclerView.onClickFavorite(makeup);
        });

    }

    //Conta os Itens da Lista
    @Override
    public int getItemCount() {
        if (makeupList != null && !makeupList.isEmpty()) {
            return makeupList.size();
        } else {
            return 0;
        }
    }

    // Classe que retorna os campos usados já referenciados
    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private final MaterialCardView cardView;
        private final TextView name;
        private final TextView currency_price;
        private final ImageView image;
        private final CheckBox checkBox_favorite;

        // Recupera os valores definidos no Layout do RecycleAdpater
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            name = itemView.findViewById(R.id.txt_nameMakeup);
            currency_price = itemView.findViewById(R.id.txt_priceMakeup);
            image = itemView.findViewById(R.id.image_product);
            checkBox_favorite = itemView.findViewById(R.id.checkBox_favorite);
        }
    }

}