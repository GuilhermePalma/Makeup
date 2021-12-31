package com.example.maquiagem.view.activities;

import static com.example.maquiagem.model.SerializationData.ALL_ITEMS_JSON;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.maquiagem.R;
import com.example.maquiagem.controller.ManagerDatabase;
import com.example.maquiagem.model.entity.Makeup;
import com.example.maquiagem.view.CustomAlertDialog;
import com.example.maquiagem.view.fragments.FragmentListMakeup;
import com.example.maquiagem.view.fragments.FragmentSearchMakeup;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Activity responsavel por Realizar a Busca e Exibir os Resultados das Maquiagens.
 */
public class ResultActivity extends AppCompatActivity {

    private TextView title_loading;
    private CircularProgressIndicator progressIndicator_loading;
    private String uri_received;
    private CustomAlertDialog customDialog;
    private FrameLayout frameLayout_fragment;
    private Context context;

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
        customDialog.messageWithCloseWindow(this, R.string.title_noData, R.string.error_recoveryData,
                null, null).show();
    }

    /**
     * Instancia/Configura os Itens Necessarios
     */
    private void instanceItems() {
        frameLayout_fragment = findViewById(R.id.frame_forFragmentSearch);
        context = ResultActivity.this;
        customDialog = new CustomAlertDialog(context);
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

            // Obtem as Makeups
            Makeup makeup = new Makeup(context);
            List<Makeup> async_list = makeup.getMakeups(executorService, Uri.parse(uri_received), ALL_ITEMS_JSON);

            // Exibe os Resultados na Tela
            runOnUiThread(() -> {
                if (async_list == null || async_list.isEmpty()) {
                    customDialog.messageWithCloseWindow(this, R.string.title_noExist,
                            R.string.error_noExists, null, null).show();
                } else {
                    // Verifica se realizou a Inserção no Banco de Dados
                    if (!insertInDataBase(async_list)) {
                        customDialog.messageWithCloseWindow(this, R.string.title_invalidData,
                                R.string.error_noExists, null, null).show();
                    } else {
                        title_loading.setVisibility(View.GONE);
                        progressIndicator_loading.setVisibility(View.GONE);
                        frameLayout_fragment.setVisibility(View.VISIBLE);
                        // Instancia e Coloca o Fragment List
                        FragmentListMakeup fragmentListMakeup = FragmentListMakeup.newInstance(async_list, "");
                        getSupportFragmentManager().beginTransaction().replace(
                                frameLayout_fragment.getId(), fragmentListMakeup).commit();
                    }
                }
            });
        });
    }

    /**
     * Serializa os Dados e Insere no {@link ManagerDatabase} para ter um registro das maquiagens
     * buscadas
     */
    private boolean insertInDataBase(List<Makeup> makeups) {
        // Verifica se o Array é null ou Vazio = Evita Exceptions
        if (makeups != null && !makeups.isEmpty()) {
            ManagerDatabase managerDatabase = new ManagerDatabase(context);
            int allInserted = 0;

            // Insere cada item do Array no DB
            for (Makeup makeupItem : makeups) {
                allInserted = managerDatabase.insertMakeup(makeupItem) ? allInserted : allInserted + 1;
            }

            return allInserted == 0;
        } else {
            return false;
        }
    }
}