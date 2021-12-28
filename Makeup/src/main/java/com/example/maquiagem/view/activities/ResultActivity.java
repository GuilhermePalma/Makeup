package com.example.maquiagem.view.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.maquiagem.R;
import com.example.maquiagem.controller.DataBaseHelper;
import com.example.maquiagem.model.Makeup;
import com.example.maquiagem.model.SearchInternet;
import com.example.maquiagem.model.SerializationData;
import com.example.maquiagem.view.CustomAlertDialog;
import com.example.maquiagem.view.fragments.FragmentListMakeup;
import com.example.maquiagem.view.fragments.FragmentSearchMakeup;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResultActivity extends AppCompatActivity {

    private TextView title_loading;
    private CircularProgressIndicator progressIndicator_loading;
    private String uri_received;
    private CustomAlertDialog dialogs;
    private List<Makeup> makeupListSearch;
    private FrameLayout frameLayout_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        // Instancia/Configura os Widgets e Classes que serão Usadas
        instanceItems();
        Button btn_main = findViewById(R.id.btn_home);
        btn_main.setOnClickListener(v -> finish());

        // Obtem os dados passados pelo Fragment SearchMakeup
        Bundle queryBundle = getIntent().getExtras();

        // Se não for Nulo, Obtem os Dados passado pela Activity
        if (queryBundle != null) {
            uri_received = queryBundle.getString(FragmentSearchMakeup.KEY_URI, "");
            if (uri_received == null || !uri_received.equals("")) {
                // Realiza a Busca Assincrona na Internet
                asyncTask();
                return;
            }
        }
        // Mensagem de Erro caso não tenha a URI disponivel
        dialogs.messageWithCloseWindow(this, getString(R.string.title_noData),
                getString(R.string.error_recoveryData)).show();
    }

    /**
     * Instancia/Configura os Itens Necessarios
     */
    private void instanceItems() {
        frameLayout_fragment = findViewById(R.id.frame_forFragmentSearch);
        makeupListSearch = new ArrayList<>();
        dialogs = new CustomAlertDialog(this);
        title_loading = findViewById(R.id.txt_titleLoadingSearch);
        progressIndicator_loading = findViewById(R.id.progress_loadingSearch);
    }

    /**
     * Realiza a Busca Assincrona a partir da URI formada no {@link FragmentSearchMakeup}
     */
    private void asyncTask() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        // Mostra o Layout "Carregando"
        frameLayout_fragment.setVisibility(View.GONE);
        title_loading.setVisibility(View.VISIBLE);
        progressIndicator_loading.setVisibility(View.VISIBLE);

        // Execução da Tarefa de Forma Assincrona
        executorService.execute(() -> {
            // Obtem o JSON
            String jsonReciver_search = SearchInternet.searchByUrl(uri_received, "GET");

            runOnUiThread(() -> {
                // Tratamento do JSON
                if (jsonReciver_search == null || jsonReciver_search.equals("")) {
                    dialogs.messageWithCloseWindow(this,
                            getString(R.string.title_noExist),
                            getString(R.string.error_noExists)).show();
                } else {
                    // Serializa O JSON e Adiciona ele na List usada no Fragment
                    makeupListSearch = new SerializationData(this).serializationJsonMakeup(
                            jsonReciver_search, SerializationData.ALL_ITEMS_JSON);

                    // Verifica se Há Itens na Lista e se Realizou Inserção no Banco de Dados
                    if (makeupListSearch == null || makeupListSearch.isEmpty() || !insertInDataBase(makeupListSearch)) {
                        dialogs.messageWithCloseWindow(this, getString(R.string.title_invalidData),
                                getString(R.string.error_noExists)).show();
                    } else {
                        title_loading.setVisibility(View.GONE);
                        progressIndicator_loading.setVisibility(View.GONE);
                        frameLayout_fragment.setVisibility(View.VISIBLE);
                        // Instancia e Coloca o Fragment List
                        FragmentListMakeup fragmentListMakeup = FragmentListMakeup
                                .newInstance(makeupListSearch, "");
                        getSupportFragmentManager().beginTransaction().replace(
                                frameLayout_fragment.getId(), fragmentListMakeup).commit();
                    }
                }
            });
        });
    }

    /**
     * Serializa os Dados e Insere no {@link DataBaseHelper} para ter um registro das maquiagens
     * buscadas
     */
    private boolean insertInDataBase(List<Makeup> makeups) {
        // Verifica se o Array é null ou Vazio = Evita Exceptions
        if (makeups != null && !makeups.isEmpty()) {
            DataBaseHelper dataBaseHelper = new DataBaseHelper(this);
            // Insere cada item do Array no DB
            for (Makeup makeupItem : makeups) {
                if (!dataBaseHelper.existsInMakeup(makeupItem.getType(), makeupItem.getBrand())) {
                    dataBaseHelper.insertMakeup(makeupItem);
                }
            }
            dataBaseHelper.close();
            return true;
        } else {
            return false;
        }
    }
}