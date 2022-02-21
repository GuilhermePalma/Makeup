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
import com.example.maquiagem.controller.ClickMakeup;
import com.example.maquiagem.controller.ManagerDatabase;
import com.example.maquiagem.controller.MakeupsAdapter;
import com.example.maquiagem.model.entity.Makeup;
import com.example.maquiagem.view.CustomAlertDialog;
import com.example.maquiagem.view.activities.MakeupDetailsActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Classe que Configura o Fragment que manipulará e Exibira as {@link Makeup}. Ele possui metodos
 * herdados da Classe {@link Fragment} e possui uma Interface Implementada ({@link ClickMakeup})
 * para tratar os Cliques nos Itens da Lista
 */
public class FragmentListMakeup extends Fragment implements ClickMakeup {

    /**
     * Constante que define o tipo de Fragment como Catalogo (Exibição de Produtos Varidados
     */
    public static final String TYPE_CATALOG = "catalog";
    /**
     * Constante que define o tipo de Fragment como as {@link Makeup} Favoritas do Usuario
     */
    public static final String TYPE_MY_FAVORITE = "my_favorites";
    /**
     * Constante que define o tipo de Fragment como as Maquiagens mais Pesquisadas pelos Usuarios
     */
    public static final String TYPE_MORE_LIKED = "more_search";
    /**
     * Constante que define o tipo de Fragment como o Historico Local de Busca do Usuario
     */
    public static final String TYPE_HISTORIC = "historic";
    /**
     * Constante que define a Key para obter o tipo de Fragment
     */
    private static final String KEY_TYPE = "type_fragment";
    /**
     * {@link List} de Makeups que serão exibidas
     */
    private final List<Makeup> makeupList;
    private RecyclerView recyclerView;
    /**
     * Classe Adapter para manipular e Gerenciar o RecyclerView
     */
    private MakeupsAdapter makeupsAdapter;
    private String type_fragment;
    private Context context;
    private CustomAlertDialog customDialog;

    /**
     * Construor da Classe {@link FragmentListMakeup}
     *
     * @param makeupsList {@link List} das {@link Makeup} que serão exibidas
     */
    private FragmentListMakeup(List<Makeup> makeupsList) {
        this.makeupList = makeupsList;
    }

    /**
     * Metodo Estatico Responsavel pela Instancia do {@link FragmentListMakeup}
     *
     * @param makeupsList {@link List} das {@link Makeup} que serão exibidas
     * @param type        {@link String} que diz qual é o Tipo do Fragment
     * @see #TYPE_CATALOG
     * @see #TYPE_HISTORIC
     * @see #TYPE_MORE_LIKED
     * @see #TYPE_MY_FAVORITE
     */
    public static FragmentListMakeup newInstance(List<Makeup> makeupsList, String type) {
        FragmentListMakeup fragment = new FragmentListMakeup(makeupsList);
        Bundle args = new Bundle();
        args.putString(KEY_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Criação da View do Fragment. Obtem o TYPE do Fragment Informado
     *
     * @see #KEY_TYPE
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            type_fragment = getArguments().getString(KEY_TYPE);
        }
    }

    /**
     * Cria e Configura a View do {@link Fragment}
     *
     * @param container Local onde o {@link Fragment} será Infaldo
     * @param inflater  Instancia do {@link LayoutInflater} responsavel por Inflar/Criar a {@link View}
     * @return {@link View}
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_makeup, container, false);

        context = view.getContext();
        customDialog = new CustomAlertDialog(context);

        if (makeupList == null || makeupList.isEmpty()) {
            customDialog.defaultMessage(R.string.title_noData, R.string.txt_maintenance, null,
                    null, true).show();
        } else {
            // Cofigura o Recycler e Header do RecyclerView
            recyclerView = view.findViewById(R.id.recycler_listMakeup);
            setUpRecyclerList();
        }

        // Retorna a View Configurada
        return view;
    }

    /**
     * Configura o {@link RecyclerView} do {@link Fragment}
     */
    private void setUpRecyclerList() {
        // Configura o Layout do RecyclerView
        GridLayoutManager gridLayout = new GridLayoutManager(context, 2);
        recyclerView.setLayoutManager(gridLayout);

        // Passa as Propriedades para o RecyclerView
        makeupsAdapter = new MakeupsAdapter(context, setUpHeader(), makeupList, this);
        recyclerView.setAdapter(makeupsAdapter);

        // Define a disposição do Layout (4 Blocos Pequenos (1 | 1) e o 5° Grande (2))
        gridLayout.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position % 5 == 0 ? 2 : 1;
            }
        });
    }

    /**
     * Configura o Heaader ({@link View}) que será inserido no {@link FragmentListMakeup}
     *
     * @return {@link View}|null
     */
    private View setUpHeader() {
        View header_view = LayoutInflater.from(context)
                .inflate(R.layout.layout_header_list, recyclerView, false);

        TextView header_title = header_view.findViewById(R.id.txt_titleHeaderList);
        TextView header_subtitle = header_view.findViewById(R.id.txt_subtitleHeaderList);

        switch (type_fragment) {
            case TYPE_MY_FAVORITE:
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
                header_view = null;
                break;
        }
        return header_view;
    }

    /**
     * Metodo da Interface {@link ClickMakeup} que manipula o Clique em uma {@link Makeup}.
     * Ao clicar na Makeup, inicializa a {@link MakeupDetailsActivity}
     */
    @Override
    public void onClickProduct(Makeup makeup_click) {
        Intent details_makeup = new Intent(context, MakeupDetailsActivity.class);
        details_makeup.putExtra(MakeupDetailsActivity.ID_MAKEUP, makeup_click.getId());
        details_makeup.putExtra(MakeupDetailsActivity.BRAND_MAKEUP, makeup_click.getBrand());
        details_makeup.putExtra(MakeupDetailsActivity.NAME_MAKEUP, makeup_click.getName());
        details_makeup.putExtra(MakeupDetailsActivity.PRICE_MAKEUP, makeup_click.getPrice());
        details_makeup.putExtra(MakeupDetailsActivity.CURRENCY_MAKEUP, makeup_click.getCurrency());
        details_makeup.putExtra(MakeupDetailsActivity.DESCRIPTION_MAKEUP, makeup_click.getDescription());
        details_makeup.putExtra(MakeupDetailsActivity.TYPE_MAKEUP, makeup_click.getType());
        details_makeup.putExtra(MakeupDetailsActivity.URL_IMAGE_ORIGINAL, makeup_click.getOriginalUrlImage());
        details_makeup.putExtra(MakeupDetailsActivity.URL_IMAGE_API, makeup_click.getApiUrlImage());
        details_makeup.putExtra(MakeupDetailsActivity.IS_FAVORITE_MAKEUP, makeup_click.isFavorite());
        details_makeup.putExtra(MakeupDetailsActivity.CHAR_PRICE_MAKEUP, makeup_click.getCharPrice());
        details_makeup.putExtra(MakeupDetailsActivity.RATING_MAKEUP, makeup_click.getRatingProduct());
        details_makeup.putExtra(MakeupDetailsActivity.TAGS_MAKEUP, makeup_click.getTags());
        details_makeup.putExtra(MakeupDetailsActivity.URL_ITEM_API, makeup_click.getUrlInAPI());

        // Obtem as cores da Makeup
        Map<String,Integer > listColors = makeup_click.getColors();
        if(listColors!= null && !listColors.isEmpty()){

            // Armazenarão os dados do Array
            ArrayList<String> nameColors = new ArrayList<>();
            ArrayList<Integer> valueColors = new ArrayList<>();

            // Obtem e insere as keys(name)/values(int value hex)
            for (Map.Entry<String, Integer> entry : listColors.entrySet()) {
                nameColors.add(entry.getKey());
                valueColors.add(entry.getValue());
            }

            // Insere as Keys (nomes) e values (values int) das Cores
            details_makeup.putExtra(MakeupDetailsActivity.COLORS_KEY_MAKEUP, nameColors);
            details_makeup.putExtra(MakeupDetailsActivity.COLORS_VALUE_MAKEUP, valueColors);
        }
        startActivity(details_makeup);
    }

    /**
     * Metodo da Interface {@link ClickMakeup} que manipula o ato de Favoritar/Desfavoritar
     * uma {@link Makeup}
     *
     * @see ManagerDatabase#setFavoriteMakeup(Makeup)
     */
    @Override
    public void onClickFavorite(Makeup makeup_click, int position_item) {
        // Obtem a Posição do Item na Lista
        int index_list = makeupList.indexOf(makeup_click);
        makeup_click.setFavorite(!makeup_click.isFavorite());
        ManagerDatabase database = new ManagerDatabase(context);

        // Salva o Usuario no Banco de Dados
        if (!database.insertMakeup(makeup_click)) {
            customDialog.defaultMessage(R.string.title_errorAPI, R.string.error_database,
                    null, null, true).show();
            return;
        }

        // Atualiza o Item que foi Favoritado/Desfavoritado
        if (type_fragment.equals(TYPE_MY_FAVORITE)) {
            if (makeupList.size() == 1) {
                makeupList.clear();
                makeupsAdapter.notifyDataSetChanged();
            } else {
                makeupList.remove(index_list);
                makeupsAdapter.notifyItemRemoved(position_item);
                makeupsAdapter.notifyItemRangeChanged(position_item, makeupList.size());
            }
        } else {
            makeupList.set(index_list, makeup_click);
        }
    }
}