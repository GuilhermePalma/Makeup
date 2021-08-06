package com.example.maquiagem.view.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.maquiagem.R;
import com.example.maquiagem.controller.ViewPagerAdapter;

public class SlideScreen extends AppCompatActivity {

    private ViewPager viewPager;
    private TextView dot1;
    private TextView dot2;
    private TextView dot3;

    private Button next;
    private Button back;

    private final String FILE_PREFERENCE = "com.example.maquiagem";
    private final String FIRST_LOGIN = "first_login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_screen);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(SlideScreen.this);

        viewPager = findViewById(R.id.viewPager_initial);
        viewPager.setAdapter(viewPagerAdapter);

        next = findViewById(R.id.btn_next);
        dot1 = findViewById(R.id.txt_dotOne);
        dot2 = findViewById(R.id.txt_dotTwo);
        dot3 = findViewById(R.id.txt_dotThree);
        back = findViewById(R.id.btn_back);

        listenerChangePage();
        listenerSkip();
    }

    // Listener do Botão "Pular" --> Pula a apresentação do APP
    private void listenerSkip() {
        next.setOnClickListener( v -> {
            // Define que não é o Primeiro Acesso do Usuario p/ não apresentar novamente o APP
            SharedPreferences preferences = getSharedPreferences(FILE_PREFERENCE, 0);
            preferences.edit().putBoolean(FIRST_LOGIN, false).apply();
            finish();
            startActivity(new Intent(getApplicationContext(), SingUpActivity.class));
        });
    }

    // Listener de Mudança das Paginas do Pager View
    private void listenerChangePage() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Tratamento especial p/ cada Item da Lista
                switch (position){
                    case 0:
                       back.setVisibility(View.INVISIBLE);
                       dot1.setTextColor(getResources().getColor(R.color.red));
                       dot2.setTextColor(getResources().getColor(R.color.black));
                       break;
                    case 1:
                        back.setVisibility(View.VISIBLE);
                        back.setOnClickListener(v -> listenerBack(position));
                        dot1.setTextColor(getResources().getColor(R.color.black));
                        dot2.setTextColor(getResources().getColor(R.color.red));
                        dot3.setTextColor(getResources().getColor(R.color.black));
                        next.setText(R.string.btn_skip);
                        break;
                    case 2:
                        back.setOnClickListener(v -> listenerBack(position));
                        dot2.setTextColor(getResources().getColor(R.color.black));
                        dot3.setTextColor(getResources().getColor(R.color.red));
                        next.setText(R.string.btn_next);
                        break;
                }
            }

            // Listener do Botão "Anterior" ---> Recebe uma posição > 0 e < 3 e atualiza o Item
            private void listenerBack(int position){
                if (position > 0 && position < 3){
                    viewPager.setCurrentItem(position-1);
                }
            }

            @Override
            public void onPageSelected(int position) {}

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

}