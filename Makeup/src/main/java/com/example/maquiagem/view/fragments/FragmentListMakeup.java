package com.example.maquiagem.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maquiagem.R;
import com.example.maquiagem.controller.ClickRecyclerView;
import com.example.maquiagem.controller.DataBaseHelper;
import com.example.maquiagem.controller.RecyclerListMakeup;
import com.example.maquiagem.model.Makeup;
import com.example.maquiagem.view.activities.MakeupDetailsActivity;

import java.util.List;

public class FragmentListMakeup extends Fragment implements ClickRecyclerView {

    public static final String TYPE_CATALOG = "catalog";
    public static final String TYPE_FAVORITE = "favorite";
    public static final String TYPE_MORE_LIKED = "more_search";
    public static final String TYPE_HISTORIC = "historic";
    private static final String TYPE = "type_fragment";
    private View header_view;
    private RecyclerView recyclerView;
    private RecyclerListMakeup recyclerListMakeup;
    private String type_fragment;
    private Context context;
    private List<Makeup> makeupList;
    private DataBaseHelper database;

    // Contrutor Vazio do Fragment
    public FragmentListMakeup() {
    }

    // Contrutor passando a List (RecyclerView) e Context (DataBase)
    public FragmentListMakeup(Context context, List<Makeup> makeups) {
        this.makeupList = makeups;
        this.database = new DataBaseHelper(context);
    }

    // Intancia do Fragment ---> Inserindo o Valor Instanciado do Titulo, Context e List Usada
    public static FragmentListMakeup newInstance(Context context, List<Makeup> makeups, String type) {
        FragmentListMakeup fragment = new FragmentListMakeup(context, makeups);
        Bundle args = new Bundle();
        args.putString(TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    // Recupera o valor inserido
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type_fragment = getArguments().getString(TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list_makeup, container, false);

        context = view.getContext();
        recyclerView = view.findViewById(R.id.recycler_listMakeup);

        // Cofigura o Recycler e Header do RecyclerView
        setUpRecyclerList();

        // Retorna a View Configurada
        return view;
    }

    private void setUpRecyclerList() {
        // Configura o Layout do RecyclerView
        GridLayoutManager gridLayout = new GridLayoutManager(context, 2);
        recyclerView.setLayoutManager(gridLayout);

        // Configura o Header do RecyclerView
        setUpHeader();

        // Define o adapter (Classe que configura o RecyclerView) do RecyclerView
        recyclerListMakeup = new RecyclerListMakeup(context, header_view, makeupList, this);
        recyclerView.setAdapter(recyclerListMakeup);

        // Define a disposição do Layout (4 Blocos Pequenos (1 | 1) e o 5° Grande (2))
        gridLayout.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position % 5 == 0 ? 2 : 1;
            }
        });
    }

    private void setUpHeader() {
        header_view = LayoutInflater.from(context)
                .inflate(R.layout.layout_header_list, recyclerView, false);

        TextView header_title = header_view.findViewById(R.id.txt_titleHeaderList);
        TextView header_subtitle = header_view.findViewById(R.id.txt_subtitleHeaderList);

        switch (type_fragment) {
            case TYPE_FAVORITE:
                header_title.setText(R.string.title_favoriteList);
                header_subtitle.setText(R.string.subtitle_favoriteList);
                break;

            case TYPE_HISTORIC:
                header_title.setText(R.string.title_historicList);
                header_subtitle.setText(R.string.subtitle_historicList);
                break;

            case TYPE_MORE_LIKED:
                header_title.setText(R.string.title_moreLikedList);
                header_subtitle.setText(R.string.subtitle_moreLikedList);
                break;

            case TYPE_CATALOG:
                header_title.setText(R.string.txt_titleCatalog);
                header_subtitle.setText(R.string.txt_subtitleCatalog);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClickProduct(Makeup makeup_click) {
        Intent details_makeup = new Intent(context, MakeupDetailsActivity.class);
        details_makeup.putExtra(DataBaseHelper.ID_MAKEUP, makeup_click.getId());
        details_makeup.putExtra(DataBaseHelper.BRAND_MAKEUP, makeup_click.getBrand());
        details_makeup.putExtra(DataBaseHelper.NAME_MAKEUP, makeup_click.getName());
        details_makeup.putExtra(DataBaseHelper.PRICE_MAKEUP, makeup_click.getPrice());
        details_makeup.putExtra(DataBaseHelper.CURRENCY_MAKEUP, makeup_click.getCurrency());
        details_makeup.putExtra(DataBaseHelper.DESCRIPTION_MAKEUP, makeup_click.getDescription());
        details_makeup.putExtra(DataBaseHelper.TYPE_MAKEUP, makeup_click.getType());
        details_makeup.putExtra(DataBaseHelper.URL_IMAGE_MAKEUP, makeup_click.getUrlImage());
        details_makeup.putExtra(DataBaseHelper.IS_FAVORITE_MAKEUP, makeup_click.isFavorite());

        startActivity(details_makeup);
    }

    @Override
    public void onClickFavorite(Makeup makeup_click) {
        int indexItem = makeupList.indexOf(makeup_click);

        // Atualiza o Estado de'Favorito' do Item e atualiza o Banco de Dados
        makeup_click.setFavorite(!makeup_click.isFavorite());
        database.updateFavoriteMakeup(makeup_click);

        if (type_fragment.equals(TYPE_FAVORITE)) {
            makeupList.remove(indexItem);
        } else {
            makeupList.set(indexItem, makeup_click);
        }
        // Atualiza o Recycler com a Nova List
        recyclerListMakeup.notifyDataSetChanged();
    }
}