package com.example.maquiagem.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.maquiagem.R;

/**
 * Classe que Gerencia a Exibição do Itens do ViewPager. Ela herda as propriedades da Classe {@link PagerAdapter}
 */
public class ViewPagerAdapter extends PagerAdapter {

    private final LayoutInflater layoutInflater;
    private final Context context;

    /**
     * Inicializador da Classe {@link ViewPagerAdapter}
     *
     * @param context {@link Context} utilizado na Manipulação dos Itens
     */
    public ViewPagerAdapter(Context context) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Metodo responsavel por Instanciar um Item
     *
     * @param container Local onde será Exibido o Item
     * @param position  Posição do Item
     * @return {@link Object}
     */
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        // Define o Layout e onde será usado
        View view = layoutInflater.inflate(R.layout.activity_layout_slide_screen,
                container, false);

        // Recupera os IDs
        ImageView imageItem = view.findViewById(R.id.img_slide);
        TextView title = view.findViewById(R.id.txt_slideTitle);
        TextView description = view.findViewById(R.id.txt_slideDescription);

        // Caracteristicas de Cada Item
        switch (position) {
            case 0:
                imageItem.setImageResource(R.drawable.cosmetics);
                imageItem.setContentDescription(context.getString(R.string.descriptionImg_api));
                title.setText(R.string.title_api);
                description.setText(R.string.description_api);
                break;
            case 1:
                imageItem.setImageResource(R.drawable.translate);
                imageItem.setContentDescription(context.getString(R.string.descriptionImg_idioms));
                title.setText(R.string.title_idioms);
                description.setText(R.string.description_idioms);
                break;
            case 2:
                imageItem.setImageResource(R.drawable.backup);
                imageItem.setContentDescription(context.getString(R.string.descriptionImg_backup));
                title.setText(R.string.title_backup);
                description.setText(R.string.description_backup);
                break;
            default:
                break;
        }

        container.addView(view);
        return view;
    }

    /**
     * Retorna o Numero total de Itens Exibidos
     *
     * @return int
     */
    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    /**
     * Exclui a View
     */
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
