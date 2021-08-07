package com.example.maquiagem.view.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.maquiagem.R;
import com.example.maquiagem.model.Makeup;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class MakeupDetailsActivity extends AppCompatActivity {

    private TextView name;
    private TextView brandType;
    private TextView currency_price;
    private TextView description;
    private ImageView image_product;

    private Makeup makeup;

    private static final String BRAND = "brand";
    private static final String NAME = "name";
    private static final String PRICE = "price";
    private static final String CURRENCY = "currency";
    private static final String DESCRIPTION = "description";
    private static final String TYPE = "type";
    private static final String URL_IMAGE = "url_image";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makeup_details);

        Intent intentMakeup = getIntent();

        getInstanceItens();
        setUpToolBar();

        if (intentMakeup != null) {
            makeup = new Makeup();
            makeup.setName(intentMakeup.getStringExtra(NAME));
            makeup.setType(intentMakeup.getStringExtra(TYPE));
            makeup.setBrand(intentMakeup.getStringExtra(BRAND));
            makeup.setCurrency(intentMakeup.getStringExtra(CURRENCY));
            makeup.setPrice(intentMakeup.getStringExtra(PRICE));
            makeup.setDescription(intentMakeup.getStringExtra(DESCRIPTION));
            makeup.setUrlImage(intentMakeup.getStringExtra(URL_IMAGE));

            if (makeup.getName() == null && makeup.getPrice() == null) {
                showWindowWithoutData();
            }

            showWindowData();

        } else {
            showWindowWithoutData();
        }

    }

    private void setUpToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar4);
        toolbar.setTitle(getString(R.string.title_details));
        setSupportActionBar(toolbar);
        // Icon de voltar para a Tela Home
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_return_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    private void getInstanceItens() {
        name = findViewById(R.id.txt_nameProduct);
        brandType = findViewById(R.id.txt_brandTypeProduct);
        currency_price = findViewById(R.id.txt_priceProduct);
        description = findViewById(R.id.txt_descriptionProduct);
        image_product = findViewById(R.id.img_detailsProduct);
    }

    private void showWindowData() {
        // Biblioteca Picasso (Converte URL da IMG ---> IMG)
        Picasso.with(getApplicationContext()).load(makeup.getUrlImage())
                .error(R.drawable.makeup_no_image)
                .into(image_product);

        name.setText(Html.fromHtml(getString(R.string.formatted_name, makeup.getName())));
        brandType.setText(getString(R.string.formatted_brandType,
                makeup.getBrand(), makeup.getType()));
        currency_price.setText(getString(R.string.formatted_currencyPrice,
                makeup.getCurrency(), makeup.getPrice()));
        description.setText(makeup.getDescription());
    }

    private void showWindowWithoutData() {
        name.setText(Html.fromHtml(getString(R.string.error_name)));
        currency_price.setText(getString(R.string.error_price));
        brandType.setText(getString(R.string.error_brandType));
        description.setText(getString(R.string.error_description));
    }
}
