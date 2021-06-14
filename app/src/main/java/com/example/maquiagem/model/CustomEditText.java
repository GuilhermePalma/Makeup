package com.example.maquiagem.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.res.ResourcesCompat;

import com.example.maquiagem.R;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class CustomEditText extends AppCompatEditText {

    public CustomEditText(@NonNull Context context) {
        super(context);
        init();
    }

    public CustomEditText(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomEditText(Context context,
                          AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    // Define o Drawable = Icon (X)
    Drawable clearButtonOff, clearButton;

    // Metodo para criar o componente
    private void init() {

        // Cria os Drawables que serão utilizado
        clearButton = ResourcesCompat.getDrawable(getResources(),
                R.drawable.ic_clear, null);
        clearButtonOff = ResourcesCompat.getDrawable(getResources(),
                R.drawable.ic_clear_off, null);

        // Metodo chamado ao clicar no Botão
        setOnTouchListener(new OnTouchListener() {

            // Validação da API do dispositivo android para funcionar
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                // getCompoundDrawables() ---> Retorna um array do Drawable com as posições
                // Caso o drawable estja no final do texto = [2]
                if ((getCompoundDrawablesRelative()[2] != null)) {

                    float areaEditTextWithoutButton;

                    // Calcula a area do EditText sem o Botão
                    areaEditTextWithoutButton = (getWidth() - getPaddingEnd()
                            - clearButton.getIntrinsicWidth());

                    // Caso o clique (evento recebido) seja no Botão
                    if (event.getX() > areaEditTextWithoutButton) {
                        switch (event.getAction()){
                            case MotionEvent.ACTION_DOWN:
                              /* ACTION_DOWN = Voltar para a Posição Incial = Clique fora do
                                   EditText. Instancia o metodo showClearButton com valor do Icon */
                                showClearButton(clearButtonOff);
                                return false;

                            case MotionEvent.ACTION_UP:
                                // ACTION_UP = Fim do clique no Botão = Apagar (Desativa o Botão)
                                // Limpa o Texto e tira o Botão do EditText
                                setText("");
                                showClearButton(null);
                                return true;

                            default:
                                showClearButton(null);
                                return false;
                        }
                    } else {
                        // Clique Fora do Botão
                        return false;
                    }
                } else {
                    // Caso não obtenha valor do Layout
                    return false;
                }
            }
        });


        // Se o texto muda/mostra/oculta o botão
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s,
                                          int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s,
                                      int start, int before, int count) {
                //showClearButton(clearButton);
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void afterTextChanged(Editable s) {
                // Depois do Texto alterado, caso não tenha nada escrito ---> Tira o Icon
                if (s.length() == 0){
                    // Coloca nehum botão no Final
                    showClearButton(null);
                }
            }
        });
    }

    // Mostra o Botão no Layout ---> Final do Layout (End)
    private void showClearButton(Drawable buttonShow) {
        // Validação da Versão minima da API do dispotivo Android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            // Coloca o Icone do Botão no Fim do Layout
            setCompoundDrawablesRelativeWithIntrinsicBounds
                    (null,null, buttonShow,null);
        }
    }


    // Oculta o Botão no Layout
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void hideClearButton() {
        setCompoundDrawablesRelativeWithIntrinsicBounds
                (null,null, null,null);
    }

}