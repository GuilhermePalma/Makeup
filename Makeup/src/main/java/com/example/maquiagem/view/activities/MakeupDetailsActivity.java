package com.example.maquiagem.view.activities;

import android.content.Context;
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
import com.example.maquiagem.controller.ManagerDatabase;
import com.example.maquiagem.model.entity.Makeup;
import com.example.maquiagem.view.CustomAlertDialog;
import com.squareup.picasso.Picasso;

public class MakeupDetailsActivity extends AppCompatActivity {

    private Context context;
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

        getInstanceItems();
        setUpToolBar();

        if (intentMakeup != null) {
            makeup = new Makeup();
            makeup.setName(intentMakeup.getStringExtra(ManagerDatabase.NAME_MAKEUP));
            makeup.setType(intentMakeup.getStringExtra(ManagerDatabase.TYPE_MAKEUP));
            makeup.setBrand(intentMakeup.getStringExtra(ManagerDatabase.BRAND_MAKEUP));
            makeup.setCurrency(intentMakeup.getStringExtra(ManagerDatabase.CURRENCY_MAKEUP));
            makeup.setPrice(intentMakeup.getStringExtra(ManagerDatabase.PRICE_MAKEUP));
            makeup.setDescription(intentMakeup.getStringExtra(ManagerDatabase.DESCRIPTION_MAKEUP));
            makeup.setUrlImage(intentMakeup.getStringExtra(ManagerDatabase.URL_IMAGE_MAKEUP));
            makeup.setFavorite(intentMakeup.getBooleanExtra(ManagerDatabase.IS_FAVORITE_MAKEUP, false));

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
        if(getSupportActionBar() != null){
            // Icon de voltar para a Tela Home
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_return_home);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
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

    private void getInstanceItems() {
        context = MakeupDetailsActivity.this;
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
            ManagerDatabase database = new ManagerDatabase(context);

            makeup.setFavorite(!makeup.isFavorite());
            cbx_favorite.setChecked(makeup.isFavorite());

            if(!database.setFavoriteMakeup(makeup)){
                new CustomAlertDialog(context).defaultMessage(R.string.error_api,
                        R.string.error_database, null, null, true).show();
            }
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
