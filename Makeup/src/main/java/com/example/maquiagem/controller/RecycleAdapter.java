package com.example.maquiagem.controller;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maquiagem.R;
import com.example.maquiagem.model.Makeup;
import com.squareup.picasso.Picasso;

import java.util.List;

// Classe Responsavel pelo controle do RecyclerView
public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.RecycleViewHolder> {

    // Context e List/Array --> Usados p/ mostar/armazenar os dados
    Context context;
    private List<Makeup> makeupList;

    // Contrutor da Calsse
    public RecycleAdapter(Context context, List<Makeup> list, Activity activity) {
        this.context = context;
        this.makeupList = list;
    }

    // Classe Protegida que retorna os campos usados e a Interface
    protected class RecycleViewHolder extends RecyclerView.ViewHolder {

        protected TextView name;
        protected TextView currency_price;
        protected ImageView image;

        // Recupera os valores definidos no Layout do RecycleAdpater
        public RecycleViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txt_nameMakeup);
            currency_price = itemView.findViewById(R.id.txt_priceMakeup);
            image = itemView.findViewById(R.id.image_product);
        }
    }

    //Cria o ViewHolder; Instancia com o valor do Layout usado
    @NonNull
    @Override
    public RecycleAdapter.RecycleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        //Cria uma View
        View itemView;

        // Instancia o valor do layout usado
        itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_recycler_view, viewGroup, false);

        return new RecycleAdapter.RecycleViewHolder(itemView);
    }

    // Recupera os Valores do ListArray
    // Insere os Valores na Tela de acordo com a posição pedida
    @Override
    public void onBindViewHolder(@NonNull RecycleAdapter.RecycleViewHolder holder, int position) {

        Makeup makeup = makeupList.get(position);

        String name = makeup.getName();
        String name_formatted;

        // Caso posua + que 13 Caracteres
        if (name.length() > 13) {
            if (name.startsWith(" ", 12)) {
                // 13° Caracter = Espaço
                name_formatted = name.substring(0, 12);
            } else if (name.startsWith(" ", 13)) {
                // 14° Caracter = Espaço
                name_formatted = name.substring(0, 13);
            } else {
                // 13° Caracter = Preenchido com Algo =! espaço
                name_formatted = name.substring(0, 13) + "...";
            }
        } else {
            // Menos que 13 Caracteres = Pega o nome sem cortar
            name_formatted = name;
        }

        holder.name.setText(name_formatted);
        holder.currency_price.setText(String.format("%s %s", makeup.getCurrency(), makeup.getPrice()));

        // Biblioteca Picasso (Converte URL da IMG ---> IMG)
        Picasso.with(holder.image.getContext()).load(makeup.getUrlImage())
                .error(R.drawable.makeup_no_image)
                .into(holder.image);
    }

    //Conta os Itens da Lista
    @Override
    public int getItemCount() {
        return makeupList.size();
    }

}