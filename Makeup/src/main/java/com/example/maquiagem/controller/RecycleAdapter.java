package com.example.maquiagem.controller;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maquiagem.R;
import com.example.maquiagem.model.Makeup;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.List;

// Classe Responsavel pelo controle do RecyclerView
public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.RecyclerViewHolder> {

    // Context e List/Array --> Usados p/ mostar/armazenar os dados
    Context context;
    private final List<Makeup> makeupList;
    private final ClickRecyclerView clickRecyclerView;

    // Contrutor da Calsse
    public RecycleAdapter(Context context, List<Makeup> list, ClickRecyclerView clickRecyclerView) {
        this.context = context;
        this.makeupList = list;
        this.clickRecyclerView = clickRecyclerView;
    }

    // Classe Protegida que retorna os campos usados e a Interface
    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private final MaterialCardView cardView;
        private final TextView name;
        private final TextView currency_price;
        private final ImageView image;
        private final ImageButton btn_favorite;

        // Recupera os valores definidos no Layout do RecycleAdpater
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            name = itemView.findViewById(R.id.txt_nameMakeup);
            currency_price = itemView.findViewById(R.id.txt_priceMakeup);
            image = itemView.findViewById(R.id.image_product);
            btn_favorite = itemView.findViewById(R.id.imgBtn_favorite);
        }
    }

    //Cria o ViewHolder; Instancia com o valor do Layout usado
    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        //Cria uma View
        View itemView;

        // Instancia o valor do layout usado
        itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_recycler_view, viewGroup, false);

        return new RecyclerViewHolder(itemView);
    }

    // Recupera os Valores do ListArray
    // Insere os Valores na Tela de acordo com a posição pedida
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

        holder.name.setText(name_formatted);
        holder.currency_price.setText(Html.fromHtml(context.getString(R.string.formatted_currencyPrice,
                makeup.getCurrency(), makeup.getPrice())));

        // Recupera se o Produto foi Favoritado ou não
        if (makeup.isFavorite()) {
            // Coloca o Coração em Vermelho
            DrawableCompat.setTint(holder.btn_favorite.getDrawable(),
                    context.getResources().getColor(R.color.cerise));
        } else {
            // Coloca o Coração em Cinza
            DrawableCompat.setTint(holder.btn_favorite.getDrawable(),
                    context.getResources().getColor(R.color.light_grey));
        }

        // Biblioteca Picasso (Converte URL da IMG ---> IMG)
        Picasso.with(context).load(makeup.getUrlImage())
                .error(R.drawable.makeup_no_image)
                .into(holder.image);

        // Listeners dos Cliques nos Itens do RecyclerView
        holder.image.setOnClickListener(v -> clickRecyclerView.onClickProduct(makeup));
        holder.cardView.setOnClickListener(v -> clickRecyclerView.onClickProduct(makeup));
        holder.btn_favorite.setOnClickListener(v -> clickRecyclerView.onClickFavorite(makeup));

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

}