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
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;

import com.example.maquiagem.R;

import java.util.concurrent.atomic.AtomicBoolean;

public class CustomView extends AppCompatTextView {

    public CustomView(@NonNull Context context) {
        super(context);
        init();
    }

    public CustomView(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomView(Context context,
                             AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    //define o drawable q será instanciado
    Drawable mClearButtonImage;


    //metodo para inicialização do componente
    //TODO adcionar icone
    private void init() {
        // Inicializa o Drawable
        mClearButtonImage = ResourcesCompat.getDrawable(getResources(),
                R.drawable.ic_clear_off, null);

        // define a ação do clique do botãoclique do botão.
        setOnTouchListener(new OnTouchListener() {


            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // getCompoundDrawables()[2] :retorna um array de Drawables contendo start, top, end e bottom
                // Se o Drawabale estiver no final do texto: [2].

                if ((getCompoundDrawablesRelative()[2] != null)) {
                    float clearButtonStart; // Linguagens LTR
                    float clearButtonEnd;  //Linguagens RTL
                    //um boolean que pode ser atualizado dinamicamente
                    AtomicBoolean isClearButtonClicked = new AtomicBoolean(false);
                    // detecta a direção do toque
                    if (getLayoutDirection() == LAYOUT_DIRECTION_RTL) {
                        // Se RTL, cola o botão na esquerda
                        clearButtonEnd = mClearButtonImage
                                .getIntrinsicWidth() + getPaddingStart();
                        // se o toque ocorrer antes do fim do botão
                        //  isClearButtonClicked definido como true.
                        if (event.getX() < clearButtonEnd) {
                            isClearButtonClicked.set(true);
                        }
                    } else {
                        // Se o layout é LTR.

                        clearButtonStart = (getWidth() - getPaddingEnd()
                                - mClearButtonImage.getIntrinsicWidth());
                        // Se o toque ocorrer depois do inicio do botão
                        // isClearButtonClicked = true.
                        if (event.getX() > clearButtonStart) {
                            isClearButtonClicked.set(true);
                        }

                        // verifica o clique do botão
                        if (isClearButtonClicked.get()) {
                            // Verifica o ACTION_DOWN (sempre ocorre antes do ACTION_UP).
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                // troca a versão do botão
                                mClearButtonImage =
                                        ResourcesCompat.getDrawable(getResources(),
                                                R.drawable.ic_clear, null);
                                showClearButton();
                            }
                            // Verifica o  ACTION_UP.
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                // Troca pela versão opaca
                                mClearButtonImage =
                                        ResourcesCompat.getDrawable(getResources(),
                                                R.drawable.ic_clear, null);
                                // limpa o texto
                                //TODO getText().clear()
                                //esconde o botão
                                hideClearButton();
                                return true;
                            }
                        } else {
                            return false;
                        }
                    }
                }
                return false;
            }
        });

        // Se o texto muda mostra/oculta o botão
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s,
                                          int start, int count, int after) {
                // Do nothing.
            }

            @Override
            public void onTextChanged(CharSequence s,
                                      int start, int before, int count) {
                showClearButton();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Do nothing.
            }
        });
    }

    //exibição do botão
    private void showClearButton() {
        // Define  aposição do drawable
        //exige versão minima do sdk
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            setCompoundDrawablesRelativeWithIntrinsicBounds
                    (null,                      // Inicio do texto
                            null,               // Topo do texto.
                            mClearButtonImage,  // Fim do Texto
                            null);              // Abaixo do texto
        }
    }

    /**
     * oculta o botão
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void hideClearButton() {

        setCompoundDrawablesRelativeWithIntrinsicBounds
                (null,             // Inicio do texto
                        null,      // Topo do texto
                        null,      // Fim do texto
                        null);     // Abaixo do texto.
    }


}
