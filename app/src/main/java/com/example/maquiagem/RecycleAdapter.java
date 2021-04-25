package com.example.maquiagem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

        public RecycleViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.product_name);
            currency_price = itemView.findViewById(R.id.currency_price);
            type_brand = itemView.findViewById(R.id.type_brand);
            description = itemView.findViewById(R.id.description);
        }
    }

    //Cria o ViewHolder; Instancia com o valor do Layout usado
    @NonNull
    @Override
    public RecycleAdapter.RecycleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_result, viewGroup, false);
        return new RecycleAdapter.RecycleViewHolder(itemView);
    }

    //Pega os Valores da List
    @Override
    public void onBindViewHolder(@NonNull RecycleAdapter.RecycleViewHolder holder, int position) {

        MakeupClass makeup = makeupList.get(position);
        holder.name.setText(makeup.getName());
        holder.currency_price.setText(makeup.getCurrency() + " " + makeup.getPrice());
        holder.type_brand.setText("Tipo: " + makeup.getType() + " - " + makeup.getBrand());
        holder.description.setText("Descrição: " + makeup.getDescription());

    }

    //Conta os Itens da Lista
    @Override
    public int getItemCount() {
        return makeupList.size();
    }

}