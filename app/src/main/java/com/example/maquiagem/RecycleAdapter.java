package com.example.maquiagem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.RecycleViewHolder> {

    //Context e List/Array
    Context context;
    private List<MakeupClass> makeupList;

    //Contrutor
    public RecycleAdapter(Context context, List<MakeupClass> list, MainActivity mainActivity) {
        this.context = context;
        this.makeupList = list;
    }

    //Classe Protegida que retorna os campos usados e a Interface
    protected class RecycleViewHolder extends RecyclerView.ViewHolder {

        protected TextView name;
        protected TextView currency_price;
        protected TextView type_brand;
        protected TextView description;
        protected ImageView image;

        // Recupera os valores definidos no Layout do RecycleAdpater
        public RecycleViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.product_name);
            currency_price = itemView.findViewById(R.id.currency_price);
            type_brand = itemView.findViewById(R.id.content_typeBrand);
            description = itemView.findViewById(R.id.content_description);
            image = itemView.findViewById(R.id.imageProduct);
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
                .inflate(R.layout.layout_result, viewGroup, false);

        return new RecycleAdapter.RecycleViewHolder(itemView);
    }

    // Recupera os Valores do ListArray
    // Insere os Valores na Tela de acordo com a posição pedida
    @Override
    public void onBindViewHolder(@NonNull RecycleAdapter.RecycleViewHolder holder, int position) {

        MakeupClass makeup = makeupList.get(position);
        holder.name.setText(makeup.getName());
        holder.currency_price.setText(makeup.getCurrency() + " " + makeup.getPrice());
        holder.type_brand.setText(makeup.getType() + " - " + makeup.getBrand());
        holder.description.setText(makeup.getDescription());
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