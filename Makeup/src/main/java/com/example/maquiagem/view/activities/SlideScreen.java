package com.example.maquiagem.view.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.maquiagem.R;
import com.example.maquiagem.controller.ManagerSharedPreferences;
import com.example.maquiagem.controller.ViewPagerAdapter;

/**
 * Activvity responsavel pela Apresentação das Funções do APP. A apresentação é feita por meio do {@link ViewPager}
 */
public class SlideScreen extends AppCompatActivity {

    private Context context;
    private ViewPager viewPager;
    private TextView txt_dot1;
    private TextView txt_dot2;
    private TextView txt_dot3;

    private Button btn_next;
    private Button btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_screen);

        context = SlideScreen.this;
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(context);

        viewPager = findViewById(R.id.viewPager_initial);
        viewPager.setAdapter(viewPagerAdapter);

        btn_next = findViewById(R.id.btn_next);
        txt_dot1 = findViewById(R.id.txt_dotOne);
        txt_dot2 = findViewById(R.id.txt_dotTwo);
        txt_dot3 = findViewById(R.id.txt_dotThree);
        btn_back = findViewById(R.id.btn_back);

        listenerChangePage();
        listenerSkip();
    }

    /**
     * Lister do Botão de "Pular" as Apresentações
     */
    private void listenerSkip() {
        btn_next.setOnClickListener(v -> {
            // Define que não é o Primeiro Acesso do Usuario p/ não apresentar novamente o APP
            new ManagerSharedPreferences(context).setFirstLogin(false);
            finish();
            startActivity(new Intent(context, SingUpActivity.class));
        });
    }

    /**
     * Listener das Mudanças entre Paginas
     */
    private void listenerChangePage() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Tratamento especial p/ cada Item da Lista
                switch (position) {
                    case 0:
                        btn_back.setVisibility(View.INVISIBLE);
                        txt_dot1.setTextColor(getResources().getColor(R.color.red));
                        txt_dot2.setTextColor(getResources().getColor(R.color.black));
                        break;
                    case 1:
                        btn_back.setVisibility(View.VISIBLE);
                        btn_back.setOnClickListener(v -> listenerBack(position));
                        txt_dot1.setTextColor(getResources().getColor(R.color.black));
                        txt_dot2.setTextColor(getResources().getColor(R.color.red));
                        txt_dot3.setTextColor(getResources().getColor(R.color.black));
                        btn_next.setText(R.string.btn_skip);
                        break;
                    case 2:
                        btn_back.setOnClickListener(v -> listenerBack(position));
                        txt_dot2.setTextColor(getResources().getColor(R.color.black));
                        txt_dot3.setTextColor(getResources().getColor(R.color.red));
                        btn_next.setText(R.string.btn_next);
                        break;
                }
            }

            // Listener do Botão "Anterior" ---> Recebe uma posição > 0 e < 3 e atualiza o Item
            private void listenerBack(int position) {
                if (position > 0 && position < 3) {
                    viewPager.setCurrentItem(position - 1);
                }
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

}