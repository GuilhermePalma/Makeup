package com.example.maquiagem;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.RecycleViewHolder> {

    //Interface, context e list
    public static ClickRecycleView clickRecycleView;
    Context context;
    private List<Makeup> makeupList;

    //Contrutor
    public RecycleAdapter(Context context, List<Makeup> list, ClickRecycleView clickRecycleView) {
        this.context = context;
        this.makeupList = list;
        this.clickRecycleView = clickRecycleView;
    }

    //Classe Protegida que retorna os campos usados e a Interface
    protected class RecycleViewHolder extends RecyclerView.ViewHolder {

        protected TextView name;
        protected TextView currency_price;
        protected TextView type_brand;
        protected TextView description;
        protected ImageView image;

        public RecycleViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.product_name);
            currency_price = itemView.findViewById(R.id.currency_price);
            type_brand = itemView.findViewById(R.id.type_brand);
            description = itemView.findViewById(R.id.description);
            image = itemView.findViewById(R.id.img);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickRecycleView.onCustomClick(makeupList.get(getLayoutPosition()));
                }
            });
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
        Makeup makeup = makeupList.get(position);
        /* holder.txtName.setText(makeup.getNome());
        holder.txtIdade.setText(Integer.toString(makeup.getAge()));*/

        holder.name.setText(makeup.getName());
        holder.currency_price.setText(makeup.getCurrency() + " " + makeup.getPrice());
        holder.type_brand.setText(makeup.getType() + " - " +makeup.getBrand());
        holder.description.setText(makeup.getDescription());


        try {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            URL url = new URL(makeup.getImage_link().toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            //Conexão da URL
            urlConnection.connect();

            //Busca o InputStream
            InputStream inputStream = urlConnection.getInputStream();

            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            holder.image.setImageBitmap(bitmap);

        } catch (IOException e) {
            //Mostra o Erro/Exceção
            e.printStackTrace();
        }

    }

    //Conta os Itens da Lista
    @Override
    public int getItemCount() {
        return makeupList.size();
    }

}