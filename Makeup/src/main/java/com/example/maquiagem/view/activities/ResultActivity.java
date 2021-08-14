package com.example.maquiagem.view.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
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
import com.example.maquiagem.controller.CursorMakeup;
import com.example.maquiagem.controller.DataBaseHelper;
import com.example.maquiagem.controller.RecyclerResultSearch;
import com.example.maquiagem.model.Makeup;
import com.example.maquiagem.model.SearchInternet;
import com.example.maquiagem.view.PersonAlertDialogs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResultActivity extends AppCompatActivity implements ClickRecyclerView {

    private final String MAKEUP_URL = "http://makeup-api.herokuapp.com/api/v1/products.json?";
    private final String URL_NO_IMAGE = "https://github.com/GuilhermeCallegari/Maquiagem/blob" +
            "/main/app/src/main/res/drawable/makeup_no_image.jpg";
    int numberProducts, maxResult;
    private TextView title_loading;
    private RecyclerView recyclerView;
    private RecyclerResultSearch recyclerResultSearch;
    private SkeletonScreen skeletonScreen;
    private DataBaseHelper dataBaseHelper;
    private PersonAlertDialogs dialogs;
    private CursorMakeup cursorMakeup;
    private List<Makeup> makeupListRecycler;
    private String jsonMakeup = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        instanceItens();

        Button btn_main = findViewById(R.id.btn_home);
        btn_main.setOnClickListener(v -> returnMain());

        // Obtem os dados passados pela MainActivity
        Bundle queryBundle = getIntent().getExtras();
        String infoType, infoBrand;

        if (queryBundle != null) {
            infoType = Objects.requireNonNull(queryBundle).getString("product_type", "");
            infoBrand = Objects.requireNonNull(queryBundle).getString("brand", "");

            if (!infoType.equals("") && !infoBrand.equals("")) {

                // Caso já exista produtos no Banco de Dados com a Marca e Tipo inserida no DB
                if (dataBaseHelper.existsInMakeup(infoType, infoBrand)) {
                    setUpRecyclerView();
                    dataBaseHelper.close();
                    showWindow(infoType, infoBrand);
                } else {
                    // Inicia a Ativividade Assincrona
                    asyncTask(infoType, infoBrand);
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

    private void instanceItens() {
        makeupListRecycler = new ArrayList<>();
        dataBaseHelper = new DataBaseHelper(this);
        dialogs = new PersonAlertDialogs(this);
        title_loading = findViewById(R.id.title_loadingMakeup);
        recyclerView = findViewById(R.id.recyclerView);
        cursorMakeup = new CursorMakeup(getApplicationContext());
    }

    // Limpa o Array e o RecyclerView
    public void returnMain() {
        if (makeupListRecycler.size() != 0) {
            makeupListRecycler.clear();
            recyclerResultSearch.notifyDataSetChanged();
        }
        super.onBackPressed();
    }

    // Configura o RecyclerView
    public void setUpRecyclerView() {
        // Configura o RecyclerView
        GridLayoutManager layoutManagerRecycler = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManagerRecycler);

        // Informa o Context, ListArray utilizado e a Activity que será usada
        recyclerResultSearch = new RecyclerResultSearch(this, makeupListRecycler, this);
        recyclerView.setAdapter(recyclerResultSearch);

        layoutManagerRecycler.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 1;
            }
        });

        // Animação do Loading
        title_loading.setVisibility(View.VISIBLE);
        skeletonScreen = Skeleton.bind(recyclerView)
                .adapter(recyclerResultSearch)
                .load(R.layout.layout_default_item_skeleton)
                .color(R.color.white_light)
                .duration(1200)
                .show();
    }

    // Busca Assincrona ---> Consulta API e exibe os dados
    private void asyncTask(String type, String brand) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            jsonMakeup = getMakeup(type, brand);

            handler.post(() -> {

                if (jsonMakeup.equals("")) {
                    handler.post(() -> dialogs.messageWithCloseWindow(this,
                            getString(R.string.title_noExist),
                            getString(R.string.error_noExists)).show());
                    return;
                }

                // Serializa e Insere os Dados no Banco de Dados
                List<Makeup> makeupList = serializationDataMakeup(jsonMakeup);

                if (makeupList != null && makeupList.isEmpty() || !insertInDataBase(makeupList)) {
                    errorData();
                } else {
                    // Inicia e Configura o RecyclerView
                    setUpRecyclerView();
                    showWindow(type, brand);
                }
            });
        });
    }

    // Cria uma URI e inicializa o Metodo SearchInternet
    private String getMakeup(String type, String brand) {

        String BRAND_PARAMETERS = "brand";
        String TYPE_PARAMETERS = "product_type";
        Uri buildMakeup = Uri.parse(MAKEUP_URL).buildUpon()
                .appendQueryParameter(TYPE_PARAMETERS, type)
                .appendQueryParameter(BRAND_PARAMETERS, brand)
                .build();

        return SearchInternet.searchByUrl(buildMakeup.toString(), "GET");
    }

    // Usa a Serilização e Insere os Dados no DataBase
    private boolean insertInDataBase(List<Makeup> makeups) {

        // Verifica se o Array é null ou Vazio = Evita Exceptions
        if (makeups != null && !makeups.isEmpty()) {

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

    // Faz o Tratamento dos Dados Recebidoss
    private List<Makeup> serializationDataMakeup(String json) {

        String name, type, brand, price, currency, description, urlImage;
        int id;
        List<Makeup> makeups = new ArrayList<>();

        JSONArray itemsArray;

        try {
            itemsArray = new JSONArray(json);
            // Recebe o valor do Tamanho da List com os Produtos
            numberProducts = itemsArray.length();

            // Define a Quantidade de Resultados ---> Maximo = 20
            maxResult = numberProducts < 19 ? numberProducts : 20;

            for (int i = 0; i < maxResult; i++) {
                // Pega um objeto de acordo com a Posição (Posição = Item/Produto)
                JSONObject jsonObject = new JSONObject(itemsArray.getString(i));

                try {
                    id = Integer.parseInt(jsonObject.getString("id"));
                    brand = jsonObject.getString("brand");
                    name = jsonObject.getString("name");
                    price = jsonObject.getString("price").
                            replaceAll("[^0-9^,.]", "");
                    currency = jsonObject.getString("currency");
                    type = jsonObject.getString("product_type");
                    description = jsonObject.getString("description").
                            replaceAll("\n", "");
                    urlImage = jsonObject.getString("image_link");

                    // Normalizando as Strings Recebidas (HTML Tags ---> String)
                    brand = Html.fromHtml(brand).toString();
                    name = Html.fromHtml(name).toString();
                    currency = Html.fromHtml(currency).toString();
                    type = Html.fromHtml(type).toString();
                    description = Html.fromHtml(description).toString();

                    //Caso não tenha dados inseridos
                    if (name.equals("null") || name.equals("")) {
                        name = getString(R.string.empty_name);
                    }
                    if (price.equals("null") || price.equals("")) {
                        price = getString(R.string.empty_price);
                    }
                    if (currency.equals("null") || currency.equals("")) {
                        currency = "";
                    }
                    if (description.equals("null") || description.equals("")) {
                        description = getString(R.string.empty_description);
                    }
                    if (urlImage.equals("null") || urlImage.equals("")) {
                        urlImage = URL_NO_IMAGE;
                    }

                    // Intancia a Classe e Insere no Banco de Dados
                    makeups.add(new Makeup(id, brand, name, type, price,
                            currency, description, urlImage));

                } catch (Exception e) {
                    errorData();
                    Log.e("RECOVERY ARRAY", "Erro ao recuperar os valores do Array " +
                            "dos Produtos\n" + e);
                    e.printStackTrace();
                    return null;
                }
            }

            return makeups.isEmpty() ? null : makeups;

        } catch (JSONException e) {
            //Caso dê Algum Problema durante a criação do Array
            errorData();
            Log.e("NOT VALID ARRAY", "Erro no Array ou no Recebimento da String\n" + e);
            e.printStackTrace();
            return null;
        }
    }

    // Mostra os Dados na Tela
    public void showWindow(String type_search, String brand_search) {

        String select_typeBrand = String.format("SELECT * FROM %1$s WHERE %2$s = '%3$s'" +
                        " AND %4$s = '%5$s'",
                DataBaseHelper.TABLE_MAKEUP,
                DataBaseHelper.TYPE_MAKEUP, type_search,
                DataBaseHelper.BRAND_MAKEUP, brand_search);

        // Limpa a Lista caso tivesse algum dado
        if (makeupListRecycler != null && makeupListRecycler.size() > 0) {
            makeupListRecycler.clear();
        }

        // Obtem os Dados da Tabela no Formato de List<Makeup>
        List<Makeup> list_cursorMakeup;

        list_cursorMakeup = cursorMakeup.selectDataBase(select_typeBrand);

        if (list_cursorMakeup == null) {
            errorData();
        } else {
            // Instancia a Lista e atualiza o RecyclerView
            makeupListRecycler.addAll(list_cursorMakeup);
            recyclerResultSearch.notifyDataSetChanged();

            // Remove a Animação do RecyclerView
            recyclerView.postDelayed(() -> {
                skeletonScreen.hide();
                title_loading.setVisibility(View.GONE);
            }, 2500);
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
        makeup_click.setFavorite(!makeup_click.isFavorite());
        dataBaseHelper.updateFavoriteMakeup(makeup_click);
        makeupListRecycler.set(indexChangedItem, makeup_click);
    }
}