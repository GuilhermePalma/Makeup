package com.example.maquiagem.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.maquiagem.R;
import com.example.maquiagem.controller.DataBaseHelper;
import com.example.maquiagem.model.Makeup;
import com.squareup.picasso.Picasso;

public class MakeupDetailsActivity extends AppCompatActivity {

    private TextView name;
    private TextView brandType;
    private TextView currency_price;
    private TextView description;
    private ImageView image_product;
    private CheckBox cbx_favorite;

    private Makeup makeup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makeup_details);

        Intent intentMakeup = getIntent();

        getInstanceItens();
        setUpToolBar();

        if (intentMakeup != null) {
            makeup = new Makeup();
            makeup.setName(intentMakeup.getStringExtra(DataBaseHelper.NAME_MAKEUP));
            makeup.setType(intentMakeup.getStringExtra(DataBaseHelper.TYPE_MAKEUP));
            makeup.setBrand(intentMakeup.getStringExtra(DataBaseHelper.BRAND_MAKEUP));
            makeup.setCurrency(intentMakeup.getStringExtra(DataBaseHelper.CURRENCY_MAKEUP));
            makeup.setPrice(intentMakeup.getStringExtra(DataBaseHelper.PRICE_MAKEUP));
            makeup.setDescription(intentMakeup.getStringExtra(DataBaseHelper.DESCRIPTION_MAKEUP));
            makeup.setUrlImage(intentMakeup.getStringExtra(DataBaseHelper.URL_IMAGE_MAKEUP));
            makeup.setFavorite(intentMakeup.getBooleanExtra(DataBaseHelper.IS_FAVORITE_MAKEUP, false));

            if (makeup.getName() == null && makeup.getPrice() == null) {
                showWindowWithoutData();
            } else {
                showWindowData();
                listenerFavorite();
            }

        } else {
            showWindowWithoutData();
        }

    }

    private void setUpToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar4);
        toolbar.setTitle(getString(R.string.title_details));
        setSupportActionBar(toolbar);
        // Icon de voltar para a Tela Home
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_return_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    /* Trata o Clique no Icone "Return" da ToolBar
       Fecha essa Activity e Mantem os dados dos Produtos da ResultActivity */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getInstanceItens() {
        name = findViewById(R.id.txt_nameProduct);
        brandType = findViewById(R.id.txt_brandTypeProduct);
        currency_price = findViewById(R.id.txt_priceProduct);
        description = findViewById(R.id.txt_descriptionProduct);
        image_product = findViewById(R.id.img_detailsProduct);
        cbx_favorite = findViewById(R.id.checkBox_favoriteDetails);
    }

    private void showWindowData() {
        // Biblioteca Picasso (Converte URL da IMG ---> IMG)
        Picasso.get().load(makeup.getUrlImage())
                .error(R.drawable.makeup_no_image)
                .into(image_product);

        name.setText(Html.fromHtml(getString(R.string.formatted_name, makeup.getName())));
        brandType.setText(getString(R.string.formatted_brandType,
                makeup.getBrand(), makeup.getType()));
        currency_price.setText(getString(R.string.formatted_currencyPrice,
                makeup.getCurrency(), makeup.getPrice()));
        description.setText(makeup.getDescription());
        cbx_favorite.setChecked(makeup.isFavorite());
    }

    private void listenerFavorite() {
        cbx_favorite.setOnClickListener(v -> {

            // Atualiza o Estado do Favorite
            DataBaseHelper database = new DataBaseHelper(this);

            makeup.setFavorite(!makeup.isFavorite());
            cbx_favorite.setChecked(makeup.isFavorite());

            database.updateFavoriteMakeup(makeup);
            database.close();
        });
    }


    private void showWindowWithoutData() {
        name.setText(Html.fromHtml(getString(R.string.error_name)));
        currency_price.setText(getString(R.string.error_price));
        brandType.setText(getString(R.string.error_brandType));
        description.setText(getString(R.string.error_description));
        cbx_favorite.setVisibility(View.GONE);
    }
}
