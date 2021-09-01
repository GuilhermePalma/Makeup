package com.example.maquiagem;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.example.maquiagem.controller.ClickRecyclerView;
import com.example.maquiagem.controller.DataBaseHelper;
import com.example.maquiagem.controller.RecyclerResultSearch;
import com.example.maquiagem.model.Makeup;
import com.example.maquiagem.model.SearchInternet;
import com.example.maquiagem.model.SerializationData;
import com.example.maquiagem.view.PersonAlertDialogs;
import com.example.maquiagem.view.activities.MakeupDetailsActivity;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Um {@link Fragment} que exibe o Catalogo/Tela Inicial do APP
 * <p>
 * {@link CatalogFragment#newInstance} é usado para Instanciar o Fragment
 * <p>
 * Possui a Interface {@link ClickRecyclerView} que controla os Cliques nos Itens do RecyclerView
 */
public class CatalogFragment extends Fragment implements ClickRecyclerView {

    private static final int MAX_RESULT_DEFAULT = 60;
    private static final String URL_API = "http://makeup-api.herokuapp.com/api/v1/products.json?";
    private static final String RATING_GREATER = "rating_greater_than";

    private final DataBaseHelper dataBase;
    private final PersonAlertDialogs personAlertDialogs;
    private final Context context;
    private RecyclerResultSearch adapterResultSearch;
    private SkeletonScreen skeletonScreen;
    private View include_header;
    private TextView txt_loading;
    private RecyclerView recyclerView;
    private ChipGroup chipGroup;

    private List<Makeup> list_makeupApi;
    private String[] itemsChips;

    public CatalogFragment(Context context) {
        this.context = context;
        dataBase = new DataBaseHelper(context);
        personAlertDialogs = new PersonAlertDialogs(context);
    }

    public static CatalogFragment newInstance(Context context) {
        return new CatalogFragment(context);
    }

    /**
     * Cria e retorna a View Configurada com seus Respectivos Metodos
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla a View que contem o Layout do Fragment
        View viewFragment = inflater.inflate(R.layout.fragment_catalog,
                container, false);

        // Instancia os Itens
        list_makeupApi = new ArrayList<>();
        include_header = viewFragment.findViewById(R.id.header_catalog);
        txt_loading = viewFragment.findViewById(R.id.text_loadingCatalog);
        chipGroup = viewFragment.findViewById(R.id.chipGroup_catalog);
        recyclerView = viewFragment.findViewById(R.id.recyclerView_Catalog);

        // Adiciona Chips no ChipGroup, obtendo os valores do 'array_chips'
        itemsChips = new String[9];
        itemsChips = context.getResources().getStringArray(R.array.array_chips);
        for (String item : itemsChips) {
            addChip(item);
        }

        setUpRecyclerView();

        // Realiza uma busca na API para obter as Maquiagens e Apresenta-las
        if (list_makeupApi.isEmpty()) asyncTask();

        return viewFragment;
    }

    /**
     * Configura o Recycler View (Layout, Disposição e Adapter)
     * <p>
     * Utiliza o GridLaoutManaget para fazer um Layout em Grade (2 Colunas)
     * <p>
     * Usa o Adapter {@link RecyclerResultSearch} que Intancia e Configura uma Lista de Itens com
     * um Layout definido e uma lista Passada
     */
    private void setUpRecyclerView() {
        // Configura o Layout do RecyclerView
        GridLayoutManager gridLayout = new GridLayoutManager(context, 2);
        recyclerView.setLayoutManager(gridLayout);

        // Define o adapter (Classe que configura o RecyclerView) do RecyclerView
        adapterResultSearch = new RecyclerResultSearch(
                context, list_makeupApi, this);
        recyclerView.setAdapter(adapterResultSearch);

        // Define a disposição do Layout (4 Blocos Pequenos (1 | 1) e o 5° Grande (2))
        gridLayout.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position % 5 == 0 ? 2 : 1;
            }
        });

        // Animação do Loading
        include_header.setVisibility(View.GONE);
        chipGroup.setVisibility(View.GONE);
        txt_loading.setVisibility(View.VISIBLE);
        skeletonScreen = Skeleton.bind(recyclerView)
                .adapter(adapterResultSearch)
                .load(R.layout.layout_default_item_skeleton)
                .color(R.color.white_light)
                .duration(1000)
                .show();
    }

    /**
     * Adiciona Chips (Material UI) no ChipGroup e Trata o Clique (Incluindo Nome e Estado) do
     * Chip
     */
    private void addChip(String text_item) {
        Chip itemChip = new Chip(context, null, R.style.Widget_MaterialComponents_Chip_Filter);
        itemChip.setText(text_item);
        itemChip.setCheckedIconVisible(true);
        itemChip.setCheckable(true);
        itemChip.setOnClickListener(v -> printValue(text_item, itemChip.isChecked()));
        chipGroup.addView(itemChip);
    }

    /**
     * Executa uma busca Assincrona (WEB)
     * <p>
     * - Consulta na API Makeup
     * <p>
     * - Configura o RecyclerView, iniciando o metodo {@link #setUpRecyclerView }
     * <p>
     * - Instancia a {@link List} list_makeupApi e Exibe os Itens na Tela
     */
    private void asyncTask() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            // Obtem o JSON
            String json_makeup = getJsonMakeup();

            handler.post(() -> {

                if (json_makeup == null || json_makeup.equals("")) {
                    handler.post(() -> personAlertDialogs.message(
                            getString(R.string.title_noExist),
                            getString(R.string.error_noExists)).show());
                    return;
                }

                // Serializa O JSON ---> Retorno = List<Makeup> ou null
                list_makeupApi.addAll(new SerializationData(context).
                        serializationJsonMakeup(json_makeup, MAX_RESULT_DEFAULT));

                // Verifica se Há Itens na Lista e se Realizou Inserção no Banco de Dados
                if (list_makeupApi == null || list_makeupApi.isEmpty()) errorData();

                showWindow();

            });
        });
    }

    /**
     * Constroi uma URL de Pesquisa para a API MAKEUP e retorna o JSON (ou null)
     * <p>
     * Utiliza {@link #RATING_GREATER} para restringir a busca à Produtos com Avaliação maior que
     * 4.7
     */
    private String getJsonMakeup() {
        Uri build_uriAPI = Uri.parse(URL_API).buildUpon()
                .appendQueryParameter(RATING_GREATER, "4.7").build();
        return SearchInternet.searchByUrl(build_uriAPI.toString(), "GET");
    }

    /**
     * É necessario ter a {@link List} list_makeupApi instanciada com Itens serializados do JSON
     * da API Makeup para Inserir no RecyclerView e Mostrar para o Usuario
     */
    private void showWindow() {
        if (list_makeupApi == null || list_makeupApi.isEmpty()) errorData();

        //Notifica ao RecyclerView Adapter que houve alterações na Lista
        adapterResultSearch.notifyDataSetChanged();

        // Remove a Animação e Exibe os Itens na Tela depois de 2,5 seg
        recyclerView.post(() -> {
            skeletonScreen.hide();
            include_header.setVisibility(View.VISIBLE);
            chipGroup.setVisibility(View.VISIBLE);
            txt_loading.setVisibility(View.GONE);
        });
    }

    /**
     * Utiliza a classe {@link PersonAlertDialogs#message(String titulo, String mensagem)} para
     * mostrar uma Mensagem de Erro na tela
     */
    private void errorData() {
        personAlertDialogs.message(getString(R.string.title_invalidData),
                getString(R.string.error_json)).show();
    }

    // todo: Implementar Busca na API atraves da selação dos Chips
    private void printValue(String value, boolean isChecked) {
        if (isChecked) {
            Log.e("Selecionado Value: ", value);
        } else {
            Log.e("Desselecionado Value: ", value);
        }
    }


    /**
     * Sobrescreve os metodos do {@link ClickRecyclerView}, onde trata os Cliques nos itens do
     * RecyclerView.
     * <p>
     * {@link ClickRecyclerView#onClickProduct(Makeup)} - Trata os cliques nos Produtos.
     * <p>
     * {@link ClickRecyclerView#onClickFavorite(Makeup)} - Trata os cliques no Botão de
     * Favoritar (CheckBox Coração).
     */
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
        int indexItem = list_makeupApi.indexOf(makeup_click);

        // Atualiza o Estado de'Favorito' do Item e atualiza o Banco de Dados
        makeup_click.setFavorite(!makeup_click.isFavorite());
        dataBase.updateFavoriteMakeup(makeup_click);

        list_makeupApi.set(indexItem, makeup_click);

        // Atualiza o Recycler com a Nova List
        adapterResultSearch.notifyDataSetChanged();
    }
}