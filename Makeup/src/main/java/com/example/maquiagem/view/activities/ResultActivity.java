package com.example.maquiagem.view.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.example.maquiagem.R;
import com.example.maquiagem.controller.ClickRecyclerView;
import com.example.maquiagem.controller.DataBaseHelper;
import com.example.maquiagem.controller.RecyclerListMakeup;
import com.example.maquiagem.model.Makeup;
import com.example.maquiagem.model.SearchInternet;
import com.example.maquiagem.model.SerializationData;
import com.example.maquiagem.view.PersonAlertDialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResultActivity extends AppCompatActivity implements ClickRecyclerView {

    public static final String PARAMETER_BRAND = "brand";
    public static final String PARAMETER_TYPE = "product_type";
    public static final String PARAMETER_QUANTITY = "product_type";
    private SerializationData serializationJson;
    private TextView title_loading;
    private RecyclerView recyclerView;
    private RecyclerListMakeup adapterListMakeup;
    private SkeletonScreen skeletonScreen;
    private DataBaseHelper dataBaseHelper;
    private Makeup makeup;
    private PersonAlertDialogs dialogs;
    private List<Makeup> makeupListRecycler;
    private int max_result_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        instanceItems();

        Button btn_main = findViewById(R.id.btn_home);
        btn_main.setOnClickListener(v -> returnMain());

        // Obtem os dados passados pela MainActivity
        Bundle queryBundle = getIntent().getExtras();

        // Se não for Nulo, Obtem os Dados passado pela Activity
        if (queryBundle != null) {

            makeup.setType(Objects.requireNonNull(queryBundle).getString(
                    PARAMETER_TYPE, ""));
            makeup.setBrand(Objects.requireNonNull(queryBundle).getString(
                    PARAMETER_BRAND, ""));
            max_result_search = Objects.requireNonNull(queryBundle).getInt(
                    PARAMETER_QUANTITY, SerializationData.DEFAULT_QUANTITY_RESULT);

            if (!makeup.getType().equals("") && !makeup.getBrand().equals("")) {

                dataBaseHelper = new DataBaseHelper(this);
                // Busca e Exibe os Produtos se existirem no DB Local. Se não, Pesquisa na API
                if (dataBaseHelper.existsInMakeup(makeup.getType(), makeup.getBrand())) {
                    setUpRecyclerView();
                    dataBaseHelper.close();
                    showWindow();
                } else {
                    asyncTask();
                }
            } else {
                dialogs.messageWithCloseWindow(this,
                        getString(R.string.title_noData),
                        getString(R.string.error_recoveryData)).show();
            }
        } else {
            dialogs.messageWithCloseWindow(this, getString(R.string.title_noData),
                    getString(R.string.error_recoveryData)).show();
        }
    }

    private void instanceItems() {
        makeup = new Makeup();
        serializationJson = new SerializationData(this);
        makeupListRecycler = new ArrayList<>();
        dialogs = new PersonAlertDialogs(this);
        title_loading = findViewById(R.id.title_loadingMakeup);
        recyclerView = findViewById(R.id.recyclerView);
    }

    // Limpa o Array e o RecyclerView
    public void returnMain() {
        if (makeupListRecycler.size() != 0) {
            makeupListRecycler.clear();
            adapterListMakeup.notifyDataSetChanged();
        }
        super.onBackPressed();
    }

    // Configura o RecyclerView
    public void setUpRecyclerView() {
        // Configura o RecyclerView
        GridLayoutManager layoutManagerRecycler = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManagerRecycler);

        // Informa o Context, ListArray utilizado e a Activity que será usada
        adapterListMakeup = new RecyclerListMakeup(this,
                null, makeupListRecycler, this);
        recyclerView.setAdapter(adapterListMakeup);

        layoutManagerRecycler.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        });

        // Animação do Loading
        title_loading.setVisibility(View.VISIBLE);
        skeletonScreen = Skeleton.bind(recyclerView)
                .adapter(adapterListMakeup)
                .load(R.layout.layout_default_item_skeleton)
                .color(R.color.white_light)
                .duration(1000)
                .show();
    }

    // Busca Assincrona ---> Consulta API e exibe os dados
    private void asyncTask() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            // Obtem o JSON
            String json_search_makeup = getMakeup();

            handler.post(() -> {

                if (json_search_makeup == null || json_search_makeup.equals("")) {
                    handler.post(() -> dialogs.messageWithCloseWindow(this,
                            getString(R.string.title_noExist),
                            getString(R.string.error_noExists)).show());
                    return;
                }

                // Serializa O JSON
                List<Makeup> makeupList = serializationJson.
                        serializationJsonMakeup(json_search_makeup, max_result_search);

                // Verifica se Há Itens na Lista e se Realizou Inserção no Banco de Dados
                if (makeupList == null || makeupList.isEmpty() || !insertInDataBase(makeupList)) {
                    errorData();
                } else {
                    setUpRecyclerView();
                    showWindow();
                }
            });
        });
    }

    // Cria uma URI e inicializa o Metodo SearchInternet
    private String getMakeup() {

        final String MAKEUP_URL = "http://makeup-api.herokuapp.com/api/v1/products.json?";

        Uri buildMakeup = Uri.parse(MAKEUP_URL).buildUpon()
                .appendQueryParameter(PARAMETER_QUANTITY, makeup.getType())
                .appendQueryParameter(PARAMETER_BRAND, makeup.getBrand())
                .build();

        return SearchInternet.searchByUrl(buildMakeup.toString(), "GET");
    }

    // Usa a Serilização e Insere os Dados no DataBase
    private boolean insertInDataBase(List<Makeup> makeups) {

        // Verifica se o Array é null ou Vazio = Evita Exceptions
        if (makeups != null && !makeups.isEmpty()) {
            dataBaseHelper = new DataBaseHelper(this);
            // Insere cada item do Array no DB
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                makeups.forEach(makeup -> dataBaseHelper.insertMakeup(makeup));
            } else {
                for (int i = 0; i < makeups.size(); i++) {
                    dataBaseHelper.insertMakeup(makeups.get(i));
                }
            }
            dataBaseHelper.close();
            return true;
        } else {
            return false;
        }
    }

    // Mostra os Dados na Tela
    public void showWindow() {

        String select_typeBrand = String.format("SELECT * FROM %1$s WHERE %2$s='%3$s'" +
                        " AND %4$s='%5$s'",
                DataBaseHelper.TABLE_MAKEUP,
                DataBaseHelper.TYPE_MAKEUP, makeup.getType(),
                DataBaseHelper.BRAND_MAKEUP, makeup.getBrand());

        // Limpa a Lista caso tivesse algum dado
        if (makeupListRecycler != null && makeupListRecycler.size() > 0) {
            makeupListRecycler.clear();
        }

        // Obtem os Dados da Tabela no Formato de List<Makeup>
        List<Makeup> list_cursorMakeup = serializationJson.serializationSelectMakeup(select_typeBrand);

        // Verifica se a List com o Select do Banco não é Nula ou Vazia
        if (list_cursorMakeup != null && !list_cursorMakeup.isEmpty()) {
            // Instancia a Lista e atualiza o RecyclerView
            makeupListRecycler.addAll(list_cursorMakeup);
            adapterListMakeup.notifyDataSetChanged();

            // Remove a Animação do RecyclerView depois de 2,5 seg
            recyclerView.postDelayed(() -> {
                skeletonScreen.hide();
                title_loading.setVisibility(View.GONE);
            }, 2500);
        } else {
            errorData();
        }
    }

    private void errorData() {
        dialogs.messageWithCloseWindow(this,
                getString(R.string.title_invalidData), getString(R.string.error_json)).show();
    }


    // Metodos da Interface que Trata os Cliques no RecyclerView
    @Override
    public void onClickProduct(Makeup makeup_click) {
        Intent details_makeup = new Intent(getApplicationContext(), MakeupDetailsActivity.class);
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
        int indexChangedItem = makeupListRecycler.indexOf(makeup_click);

        // Atualiza o Item no Banco de Dados e no List do Recycler View
        dataBaseHelper = new DataBaseHelper(this);
        makeup_click.setFavorite(!makeup_click.isFavorite());
        dataBaseHelper.updateFavoriteMakeup(makeup_click);
        makeupListRecycler.set(indexChangedItem, makeup_click);
    }
}