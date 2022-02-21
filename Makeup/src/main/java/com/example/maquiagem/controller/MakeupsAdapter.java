package com.example.maquiagem.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.maquiagem.R;
import com.example.maquiagem.model.entity.Makeup;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

/**
 * Classe Responsavel por Gerenciar um {@link RecyclerView}. Ela Herda Metodos da Classe
 * {@link RecyclerView.Adapter<>}
 */
public class MakeupsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * Instancia da Classe {@link ClickMakeup} que trata os Cliques nos Itens do {@link RecyclerView}
     */
    private final ClickMakeup clickMakeup;
    /**
     * {@link Context} utilizado na Manipualção de Recursos da Classe
     */
    private final Context context;
    /**
     * Define o Valor da View do Header
     */
    private final int VIEW_TYPE_HEADER = 0;
    /**
     * Define o Valor da View dos Itens
     */
    private final int VIEW_TYPE_ITEM = 1;
    /**
     * {@link List<> } com os Elementos que serão exibidos
     */
    private final List<Makeup> makeupList;
    /**
     * {@link View} configurada com o Header
     */
    private final View header;

    /**
     * Construtor da Classe {@link MakeupsAdapter}
     *
     * @param context           {@link Context} utilziado na Manipulação dos Recursos do APP
     * @param header            {@link View} configurada do Header
     * @param list_makeups      {@link List} das {@link Makeup} que serão exibidas
     * @param clickMakeup Instancia da Classe {@link ClickMakeup} que controla o Clique
     *                          nos Itens
     */
    public MakeupsAdapter(Context context, View header,
                          List<Makeup> list_makeups, ClickMakeup clickMakeup) {
        this.context = context;
        this.header = header;
        this.makeupList = list_makeups;
        this.clickMakeup = clickMakeup;
    }

    /**
     * Metodo Responsavel por Retornar o Tipo de {@link View} a partir de uma Posição
     *
     * @param position posição do Item na Lista
     */
    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    /**
     * Metodo Responsavel pela Criação e Intancia da View que será configurada e Exibida
     *
     * @param viewType  Tipo da {@link View} que será Exibida
     * @param viewGroup Local em que a {@link View} será Inflada
     * @return {@link RecyclerView.ViewHolder}
     * @see #getItemViewType(int)
     * @see ViewHolderListMakeup
     * @see ViewHoldWithOfInstance
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == VIEW_TYPE_HEADER && header != null) {
            // Infla o Layout do Header
            return new ViewHoldWithOfInstance(header);
        } else {
            // Cria uma View e Instancia com o Layout do RecyclerView
            View itemView = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.layout_recycler_list, viewGroup, false);
            // Infla o Layout dos Itens
            return new ViewHolderListMakeup(itemView);
        }
    }

    /**
     * Configura os Itens da {@link View}.
     *
     * @param holder   Classe Herdada da {@link RecyclerView.ViewHolder} que controla a Instancia dos
     *                 itens da {@link View}
     * @param position Posição do Item que está sendo configurado
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        /// Verifica se o Item está na Posição de Item que será configurado como Makeup
        if (holder instanceof ViewHolderListMakeup) {
            // Configura o Index da Makeup à ser Obtida, caso haja ou não o Header
            int realPostion = header == null ? position : Math.min(position - 1, makeupList.size() - 1);

            // Obtem e Formata os Dados da Makeup
            Makeup makeup = makeupList.get(realPostion);
            String name_formatted = ManagerResources.customStringFormat(
                    makeup.getName(), null, position % 5 == 0 ? 28 : 12);

            // Exibe os Dados na Tela
            ((ViewHolderListMakeup) holder).name.setText(name_formatted);
            ((ViewHolderListMakeup) holder).currency_price.setText(ManagerResources.getStringIdNormalized(
                    context, R.string.formatted_price, new String[]{makeup.getCurrency(),
                            String.valueOf(makeup.getPrice())}));

            // TODO: Implementação da Contagem de Favoritos na API
            Random numberRandom = new Random();
            int random_amountFavorite = numberRandom.nextInt(1500);
            ((ViewHolderListMakeup) holder).amount_favorite.setText(ManagerResources.getStringIdNormalized(
                    context, R.string.txt_quantityFavorite, new Object[]{random_amountFavorite}));

            // Recupera se o Produto foi Favoritado ou não (Coloca o Coração em Vermelho ou Branco)
            ((ViewHolderListMakeup) holder).checkBox_favorite.setChecked(makeup.isFavorite());

            // Biblioteca Picasso (Converte URL da IMG ---> IMG)
            Picasso.get().load(makeup.getOriginalUrlImage())
                    .error(R.drawable.makeup_no_image)
                    .into(((ViewHolderListMakeup) holder).image);

            // Listeners dos Cliques nos Itens do RecyclerView
            ((ViewHolderListMakeup) holder).image.setOnClickListener(v -> clickMakeup.onClickProduct(makeup));
            ((ViewHolderListMakeup) holder).cardView.setOnClickListener(v -> clickMakeup.onClickProduct(makeup));
            ((ViewHolderListMakeup) holder).checkBox_favorite.setOnClickListener(v -> {
                // Muda p/ o estado oposto do CheckBox e Instancia o Clique
                ((ViewHolderListMakeup) holder).checkBox_favorite.setChecked(!makeup.isFavorite());
                clickMakeup.onClickFavorite(makeup, position);
            });
        }
    }

    /**
     * Conta a quantidade de Itens no {@link RecyclerView}
     *
     * @return int
     */
    @Override
    public int getItemCount() {
        // Caso a Lista não seja Vazia, retorna a Quantidade de Itens da Lista
        if (makeupList != null && !makeupList.isEmpty()) {
            // Caso possua Header, adiciona mais um Item à lista. Se não, apenas o Tamanho da Lista
            return header != null ? makeupList.size() + 1 : makeupList.size();
        } else return 0;
    }

    /**
     * Verifica se a Posição Atual é a Do Header
     *
     * @return true|false
     * @see #VIEW_TYPE_HEADER
     * @see #getItemViewType(int)
     */
    public boolean isHeader(int positionItem) {
        return positionItem == VIEW_TYPE_HEADER;
    }

    /**
     * Classe que possui a Instancia dos Itens do Layout dos Cards de {@link Makeup}
     */
    protected static class ViewHolderListMakeup extends RecyclerView.ViewHolder {

        private final MaterialCardView cardView;
        private final TextView name;
        private final TextView currency_price;
        private final TextView amount_favorite;
        private final ImageView image;
        private final CheckBox checkBox_favorite;

        /**
         * Recupera os Valors da View Informada
         *
         * @param itemView {@link View} que será obtida as Instancias
         */
        protected ViewHolderListMakeup(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView_recyclerList);
            image = itemView.findViewById(R.id.image_productList);
            checkBox_favorite = itemView.findViewById(R.id.checkBox_favoriteList);
            name = itemView.findViewById(R.id.txt_nameMakeupList);
            currency_price = itemView.findViewById(R.id.txt_priceMakeupList);
            amount_favorite = itemView.findViewById(R.id.txt_amountFavorite);
        }
    }

    /**
     * Classe que dos itens que despreza a Instancia dos seus elementps
     */
    protected static class ViewHoldWithOfInstance extends RecyclerView.ViewHolder {

        public ViewHoldWithOfInstance(@NonNull View itemView) {
            super(itemView);
        }
    }

}