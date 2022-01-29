package com.example.maquiagem.view.activities;

import static com.example.maquiagem.controller.ManagerResources.getStringIdNormalized;
import static com.example.maquiagem.controller.ManagerResources.isNullOrEmpty;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import com.example.maquiagem.R;
import com.example.maquiagem.controller.ManagerDatabase;
import com.example.maquiagem.model.entity.Makeup;
import com.example.maquiagem.view.CustomAlertDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Activity responsavel por Mostar os Detalhes de uma Maquiagem
 */
public class MakeupDetailsActivity extends AppCompatActivity {

    public static final String ID_MAKEUP = "id";
    public static final String BRAND_MAKEUP = "brand";
    public static final String NAME_MAKEUP = "name";
    public static final String TYPE_MAKEUP = "type";
    public static final String PRICE_MAKEUP = "price";
    public static final String CURRENCY_MAKEUP = "currency";
    public static final String DESCRIPTION_MAKEUP = "description";
    public static final String URL_IMAGE_MAKEUP = "image";
    public static final String IS_FAVORITE_MAKEUP = "is_favorite";
    public static final String CHAR_PRICE_MAKEUP = "char_price";
    public static final String RATING_MAKEUP = "rating_makeup";
    public static final String TAGS_MAKEUP = "tags_makeup";
    public static final String COLORS_KEY_MAKEUP = "key_colors_makeup";
    public static final String COLORS_VALUE_MAKEUP = "values_color_makeup";
    private static final int NO_USE_COLOR = -10;
    AppCompatRatingBar ratingBar_product;
    private Context context;
    private TextView name;
    private TextView brandType;
    private TextView currency_price;
    private TextView description;
    private ImageView image_product;
    private CheckBox cbx_favorite;
    private TextView txt_currency;
    private Makeup makeup;
    private ChipGroup chipGroup_tags;
    private ChipGroup chipGroup_colors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makeup_details);

        getInstanceItems();
        setUpToolBar();

        // Obtem os Dados passados na Intent ou Exibe um Erro
        Intent intentMakeup = getIntent();
        if (intentMakeup != null) {
            makeup = new Makeup();
            makeup.setName(intentMakeup.getStringExtra(NAME_MAKEUP));
            makeup.setType(intentMakeup.getStringExtra(TYPE_MAKEUP));
            makeup.setBrand(intentMakeup.getStringExtra(BRAND_MAKEUP));
            makeup.setCurrency(intentMakeup.getStringExtra(CURRENCY_MAKEUP));
            makeup.setPrice(intentMakeup.getDoubleExtra(PRICE_MAKEUP, -1));
            makeup.setDescription(intentMakeup.getStringExtra(DESCRIPTION_MAKEUP));
            makeup.setOriginalUrlImage(intentMakeup.getStringExtra(URL_IMAGE_MAKEUP));
            makeup.setFavorite(intentMakeup.getBooleanExtra(IS_FAVORITE_MAKEUP, false));
            makeup.setCharPrice(intentMakeup.getStringExtra(CHAR_PRICE_MAKEUP));
            makeup.setRatingProduct(intentMakeup.getFloatExtra(RATING_MAKEUP, 0));
            makeup.setTags(intentMakeup.getStringArrayExtra(TAGS_MAKEUP));

            // Obtem e Serializa a Lista de Cores
            ArrayList<String> nameColors = intentMakeup.getStringArrayListExtra(COLORS_KEY_MAKEUP);
            ArrayList<Integer> valueColors = intentMakeup.getIntegerArrayListExtra(COLORS_VALUE_MAKEUP);
            if (nameColors != null && valueColors != null) {
                Map<String, Integer> listColors = new HashMap<>();
                for (int i = 0; i < valueColors.size(); i++) {
                    String key = (nameColors.size() - 1) >= i ? nameColors.get(i) : "";
                    listColors.put(key, valueColors.get(i));
                }
                makeup.setColors(listColors);
            }

            if (makeup.getName() == null && makeup.getPrice() == -1) {
                showWindowWithoutData();
            } else {
                showWindowData();
                listenerFavorite();
            }

        } else {
            showWindowWithoutData();
        }

    }

    /**
     * Configura a ToolBar e seu retorna para a {@link MainActivity}
     */
    private void setUpToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar4);
        toolbar.setTitle(getString(R.string.title_details));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            // Icon de voltar para a Tela Home
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_return_home);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
    }

    /**
     * Obtem os IDs e Instancia os Itens que serão usados na Activity
     */
    private void getInstanceItems() {
        context = MakeupDetailsActivity.this;
        name = findViewById(R.id.txt_nameProduct);
        brandType = findViewById(R.id.txt_brandTypeProduct);
        currency_price = findViewById(R.id.txt_priceProduct);
        description = findViewById(R.id.txt_descriptionProduct);
        image_product = findViewById(R.id.img_detailsProduct);
        cbx_favorite = findViewById(R.id.checkBox_favoriteDetails);
        txt_currency = findViewById(R.id.txt_currencyProduct);
        ratingBar_product = findViewById(R.id.ratingBar_makeup);
        chipGroup_tags = findViewById(R.id.chipGroup_tags);
        chipGroup_colors = findViewById(R.id.chipGroup_colors);
    }

    /**
     * Exibe os Dados da Makeup na Activity
     */
    private void showWindowData() {
        // Biblioteca Picasso (Converte URL da IMG ---> IMG)
        Picasso.get().load(makeup.getOriginalUrlImage())
                .error(R.drawable.makeup_no_image)
                .into(image_product);

        name.setText(getStringIdNormalized(context, R.string.formatted_name, new String[]{makeup.getName()}));
        brandType.setText(getStringIdNormalized(context, R.string.formatted_brandType,
                new String[]{makeup.getBrand(), makeup.getType()}));
        currency_price.setText(getStringIdNormalized(context, R.string.formatted_price,
                new String[]{makeup.getCharPrice(), String.valueOf(makeup.getPrice())}));
        description.setText(makeup.getDescription());
        cbx_favorite.setChecked(makeup.isFavorite());

        if (isNullOrEmpty(makeup.getCurrency())) {
            txt_currency.setVisibility(View.GONE);
        } else {
            txt_currency.setText(getStringIdNormalized(context, R.string.formatted_currency,
                    new String[]{makeup.getCurrency()}));
        }

        if (makeup.getRatingProduct() < 0) {
            ratingBar_product.setVisibility(View.GONE);
        } else {
            ratingBar_product.setRating(makeup.getRatingProduct());
        }

        // Obtem a Lista de Tags e Formata sua exibição
        String[] listTags = makeup.getTags();
        if (listTags != null && listTags.length > 0) {
            for (String itemTag : listTags) {
                addChips(chipGroup_tags, itemTag, NO_USE_COLOR);
            }
        } else {
            TextView txt_tags = findViewById(R.id.txt_labelTags);
            txt_tags.setVisibility(View.GONE);
            chipGroup_tags.setVisibility(View.GONE);
        }

        // Obtem a Lista de Cores e Formata sua exibição
        Map<String, Integer> listColors = makeup.getColors();
        if (listColors != null) {
            // Obtem e insere as keys(name)/values(int value hex)
            for (Map.Entry<String, Integer> entry : listColors.entrySet()) {
                addChips(chipGroup_colors, entry.getKey(), entry.getValue());
            }
        } else {
            TextView txt_colors = findViewById(R.id.txt_labelColors);
            txt_colors.setVisibility(View.GONE);
            chipGroup_colors.setVisibility(View.GONE);
        }
    }

    private void addChips(final ChipGroup chipGroupShow, final String titleChip, final int colorChip) {
        if (chipGroupShow == null) return;
        else if (isNullOrEmpty(titleChip) && colorChip == NO_USE_COLOR) return;

        Chip chip = new Chip(MakeupDetailsActivity.this);
        chip.setTextSize(16);
        chip.setPadding(20, 20, 20, 20);
        if (!isNullOrEmpty(titleChip)) chip.setText(titleChip);
        if (colorChip != NO_USE_COLOR) {
            chip.setChipStrokeWidth(5);
            chip.setChipBackgroundColor(ColorStateList.valueOf(colorChip));
        }

        chipGroupShow.addView(chip);
    }

    /**
     * Listener do Botão "Favoritar" Maquiagem
     */
    private void listenerFavorite() {
        cbx_favorite.setOnClickListener(v -> {
            // Atualiza o Estado do Favorite
            ManagerDatabase database = new ManagerDatabase(context);

            makeup.setFavorite(!makeup.isFavorite());
            cbx_favorite.setChecked(makeup.isFavorite());

            if (!database.setFavoriteMakeup(makeup)) {
                new CustomAlertDialog(context).defaultMessage(R.string.error_api,
                        R.string.error_database, null, null, true).show();
            }
        });
    }


    /**
     * Exibe a Activity mostrando que não foi possivel obter os Dados
     */
    private void showWindowWithoutData() {
        name.setText(getString(R.string.error_name));
        currency_price.setText(getString(R.string.error_price));
        brandType.setText(getString(R.string.error_brandType));
        description.setText(getString(R.string.error_description));
        cbx_favorite.setVisibility(View.GONE);
    }
}
