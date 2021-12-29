package com.example.maquiagem.view.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.maquiagem.R;
import com.example.maquiagem.model.entity.Makeup;
import com.example.maquiagem.view.CustomAlertDialog;
import com.example.maquiagem.view.activities.ResultActivity;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class FragmentSearchMakeup extends Fragment {

    // Chave do Intent.put
    public static final String KEY_URI = "url_search";

    // Constantes do Campo da API
    private final String URL_API = "http://makeup-api.herokuapp.com/api/v1/products.json?";
    private final String BRAND = "brand";
    private final String TYPE = "product_type";
    private final String CATEGORY = "product_category";
    private final String TAGS = "product_tags";

    // Widgets Utilizados
    private final Activity activity;
    private final Context context;
    private String[] array_category;
    private Makeup makeup;
    private List<String> listTags;
    private AutoCompleteTextView autoComplete_type;
    private AutoCompleteTextView autoComplete_brand;
    private AutoCompleteTextView autoComplete_category;
    private TextInputLayout inputLayout_category;
    private Button btn_search;
    private Button btn_tags;
    private ChipGroup chipGroup_tags;
    private String[] array_brand;
    private String[] array_type;

    /**
     * Construtor da Classe do Fragment SearchMakeup
     */
    public FragmentSearchMakeup(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
    }

    /**
     * Criação e Configuração dos Itens do Fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Define a View, com o design  usado
        View view = inflater.inflate(R.layout.fragment_search_makeup, container, false);

        // Instancia dos Itens
        autoComplete_type = view.findViewById(R.id.autoComplete_type);
        autoComplete_brand = view.findViewById(R.id.autoComplete_brand);
        autoComplete_category = view.findViewById(R.id.autoComplete_category);
        inputLayout_category = view.findViewById(R.id.inputLayout_category);
        btn_search = view.findViewById(R.id.btn_search);
        btn_tags = view.findViewById(R.id.button_showTags);
        chipGroup_tags = view.findViewById(R.id.chips_tags);

        // Array das Tags selecionadas p/ pesqusia, Classe Utilizada e Array de Categorias Vazio
        listTags = new ArrayList<>();
        makeup = new Makeup();

        // Instancia dos Arrays dos AutoCompleteText
        array_category = new String[0];
        array_brand = context.getResources().getStringArray(R.array.array_brand);
        array_type = context.getResources().getStringArray(R.array.array_type);

        // Configura e Adiciona os Chips de Tags
        // Arrays dos AutoCompleteText
        String[] array_tags = context.getResources().getStringArray(R.array.array_tags);
        for (String item : array_tags) {
            addChip(item);
        }

        // Configura os AutoCompleteText
        setUpType();
        setUpBrand();
        setUpCategory();

        // Listener do Botão "Pesquisar" e "Mostar Tags"
        listenerSearch();
        btn_tags.setOnClickListener(v -> {
            // Altera a Visibilidade do ChipsGroup e o Icone do Botão
            if (chipGroup_tags.getVisibility() == View.GONE) {
                chipGroup_tags.setVisibility(View.VISIBLE);
                btn_tags.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                        R.drawable.ic_keyboard_arrow_up, 0);
            } else {
                chipGroup_tags.setVisibility(View.GONE);
                btn_tags.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                        R.drawable.ic_keyboard_arrow_down, 0);
            }
        });

        // Infla/Inicia o Layout do Fragment
        return view;
    }

    /**
     * Adiciona e Configura Chips a partir de um Array de Tags
     */
    private void addChip(String text_item) {
        Chip itemChip = new Chip(activity, null, R.style.Widget_MaterialComponents_Chip_Choice);
        itemChip.setText(text_item);
        itemChip.setCheckedIconVisible(true);
        itemChip.setCheckable(true);
        itemChip.setOnClickListener(v -> {
            // Adiciona a String do Item à Lista de Tags q formarão a URL
            if (itemChip.isChecked()) {
                listTags.add(text_item);
            } else listTags.remove(text_item);
        });
        chipGroup_tags.addView(itemChip);
    }

    /**
     * Listener do botão "Pesquisar"
     */
    private void listenerSearch() {
        btn_search.setOnClickListener(v -> {
            // Verifica se os Dados forma Preenchidos
            if (makeup.getBrand().equals("") && makeup.getType().equals("") && listTags.isEmpty()) {
                new CustomAlertDialog(context).message(getString(R.string.error_input),
                        getString(R.string.error_notArgsSearch)).show();
            } else {
                // Inicia a Activity Result com a URL que será consultada
                Intent intentResult = new Intent(context, ResultActivity.class);
                intentResult.putExtra(KEY_URI, buildUri());
                startActivity(intentResult);
            }
        });
    }

    /**
     * Fomra a URL para pesquisar
     */
    private String buildUri() {
        String brand_formatted = makeup.getBrand();
        String type_formatted = makeup.getType();
        String category_formatted = makeup.getCategory();

        // Obtem o Numero do Ultimo Item da Lista
        int listLength = listTags.size() - 1;
        // Formatação de Cada Tag p/ usar na Pesquisa da URL
        StringBuilder tagsWithCommas = new StringBuilder();
        for (String item_tag : listTags) {
            // Formatação das Virgulas
            int indexItem = listTags.indexOf(item_tag);
            if (indexItem == listLength) tagsWithCommas.append(item_tag);
            else tagsWithCommas.append(item_tag).append(",");
        }

        Uri build_uriAPI = Uri.parse(URL_API).buildUpon()
                .appendQueryParameter(BRAND, brand_formatted)
                .appendQueryParameter(TYPE, type_formatted)
                .appendQueryParameter(CATEGORY, category_formatted)
                .appendQueryParameter(TAGS, tagsWithCommas.toString())
                .build();

        return build_uriAPI.toString();
    }

    /**
     * Configura o AutoCompleteText com o Array de Marcas disponivel
     */
    private void setUpBrand() {
        ArrayAdapter<String> adapterBrand = new ArrayAdapter<>(context,
                android.R.layout.simple_dropdown_item_1line, array_brand);
        autoComplete_brand.setAdapter(adapterBrand);

        // Listener das Opções do AutoCompleteText --> Atribui valor a Brand(marca) da Pesquisa
        autoComplete_brand.setOnItemClickListener((parent, view, position, id) ->
                makeup.setBrand(array_brand[position])
        );
    }

    /**
     * Configura o AutoCompleteText com o Array de Tipos disponivel
     */
    private void setUpType() {
        ArrayAdapter<String> adapterType = new ArrayAdapter<>(context,
                android.R.layout.simple_dropdown_item_1line, array_type);
        autoComplete_type.setAdapter(adapterType);

        // Listener das Opções do AutoCompleteText
        autoComplete_type.setOnItemClickListener((parent, view, position, id) -> {
            makeup.setType(array_type[position]);
            setUpCategory();
        });
    }

    /**
     * Configura o AutoCompleteText com a Categoria de Acordo com o Tipo Selecionado
     */
    private void setUpCategory() {
        // Configura a mensagem de erro da "Categoria" caso nenhum "Tipo" foi selecionado
        autoComplete_category.setOnClickListener(v -> {
            // Exibe uma Mensagem caso a Categoria não esteja disponivel
            if (array_category.length < 1) {
                new CustomAlertDialog(context).message(getString(R.string.error_valueRequired),
                        Html.fromHtml(getString(R.string.error_category)).toString()).show();
            }
        });
        inputLayout_category.setEndIconOnClickListener(v -> {
            if (array_category.length < 1) {
                new CustomAlertDialog(context).message(getString(R.string.error_valueRequired),
                        Html.fromHtml(getString(R.string.error_category)).toString()).show();
            } else autoComplete_category.showDropDown();
        });

        // Preparação da Lista p/ os diferentes tipos de Tipos
        switch (makeup.getType()) {
            case "Blush":
                array_category = context.getResources().
                        getStringArray(R.array.array_categoryBlush);
                break;
            case "Bronzer":
                array_category = context.getResources().
                        getStringArray(R.array.array_categoryBronzer);
                break;
            case "Eyebrow":
                array_category = context.getResources().
                        getStringArray(R.array.array_categoryEyebrow);
                break;
            case "Eyeliner":
                array_category = context.getResources().
                        getStringArray(R.array.array_categoryEyeliner);
                break;
            case "Eyeshadow":
                array_category = context.getResources().
                        getStringArray(R.array.array_categoryEyeshadow);
                break;
            case "Foundation":
                array_category = context.getResources().
                        getStringArray(R.array.array_categoryFoundation);
                break;
            case "Lip liner":
                array_category = context.getResources().
                        getStringArray(R.array.array_categoryLipLiner);
                break;
            case "Lipstick":
                array_category = context.getResources().
                        getStringArray(R.array.array_categoryLipstick);
                break;
            default:
                array_category = new String[0];
                return;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_dropdown_item_1line, array_category);
        autoComplete_category.setAdapter(adapter);

        // Listener das Opções do AutoCompleteText
        autoComplete_category.setOnItemClickListener((parent, view, position, id) ->
                makeup.setCategory(array_category[position]));
    }

}