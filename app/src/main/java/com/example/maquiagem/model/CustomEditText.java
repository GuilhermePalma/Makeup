package com.example.maquiagem.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.res.ResourcesCompat;

import com.example.maquiagem.R;

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
    Drawable mClearButtonImage;


    // Metodo para criar o componente
    private void init() {

        // Obtem um Drawable para ser utilizado
        mClearButtonImage = ResourcesCompat.getDrawable(getResources(),
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

                    float clearButtonStart; // Linguagens LTR

                    // Tipo Booleano que pode ser atualizado automaticamente
                    AtomicBoolean isClearButtonClicked = new AtomicBoolean(false);

                    // Calcula a Posição do Drawable
                    clearButtonStart = (getWidth() - getPaddingEnd()
                            - mClearButtonImage.getIntrinsicWidth());

                    // Caso o clique no botão seja logo depois da criação dele
                    if (event.getX() > clearButtonStart) {
                        // Alteraa Variavel do Botaõ de Limpar
                        isClearButtonClicked.set(true);
                    }

                    // Recupera o valor do AtomicBoolean = True
                    if (isClearButtonClicked.get()) {

                        // Recupera o Valor do Toque do Usuario (MotionEvent.____)
                        // ACTION_DOWN = Ação de Voltar para a Posição Incial
                        // Quando o usuario clica fora do EditText
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            // Atr
                            mClearButtonImage =
                                    ResourcesCompat.getDrawable(getResources(),
                                            R.drawable.ic_clear, null);
                            showClearButton();
                        }
                        // Verifica o  ACTION_UP.
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            // ACTION_UP = Fim do pressionar do Botão (Clique) ---> Apagar
                            // Desativa o Botão
                            // Caso insira algum Texto ---> Mostra o Botão Opaco na Tela
                            mClearButtonImage =
                                    ResourcesCompat.getDrawable(getResources(),
                                            R.drawable.ic_clear_off, null);
                            // limpa o texto
                             getText().clear();
                            //esconde o botão
                            hideClearButton();
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        // Caso o valor do AtomicButton = False
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
                // Metodo Vazio
            }

            @Override
            public void onTextChanged(CharSequence s,
                                      int start, int before, int count) {
                showClearButton();
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0){
                    mClearButtonImage =
                            ResourcesCompat.getDrawable(getResources(),
                                    R.drawable.ic_clear_off, null);
                    hideClearButton();
                }
            }
        });
    }

    // Mostra o Botão no Layout ---> Final do Layout (End)
    private void showClearButton() {
        // Validação da Versão minima da API do dispotivo Android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            // Coloca o Icone do Botão no Fim do Layout
            setCompoundDrawablesRelativeWithIntrinsicBounds
                    (null,null, mClearButtonImage,null);
        }
    }


    // Oculta o Botão no Layout
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void hideClearButton() {
        setCompoundDrawablesRelativeWithIntrinsicBounds
                (null,null, null,null);
    }

}