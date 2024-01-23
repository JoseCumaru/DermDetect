package com.example.dermdetect.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dermdetect.R;
import com.example.dermdetect.viewmodels.HistoryAdapter;
import com.example.dermdetect.viewmodels.UserHistoryManager;

public class HistoryActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageView leftIcon, itemDelete;
    ProgressBar progressBar;
    HistoryAdapter historyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initializeComponents();
        initializeClicks();
    }

    private void initializeComponents(){
        leftIcon = findViewById(R.id.left_icon);
        itemDelete = findViewById(R.id.imgItemDelete);
        progressBar = findViewById(R.id.progressbar);
        progressBar.setVisibility(View.VISIBLE);
        recyclerView = findViewById(R.id.recyclerHistory);
        UserHistoryManager.getHistory()
                .addOnSuccessListener(historyItemList -> {
                    progressBar.setVisibility(View.INVISIBLE);
                    // O histórico foi recuperado com sucesso
                    // Cria uma instância do HistoryAdapter e forneçe a lista de HistoryItem
                    historyAdapter = new HistoryAdapter(historyItemList);

                    // Configura o RecyclerView para exibir o histórico
                    recyclerView.setLayoutManager(new LinearLayoutManager(this)); // Use o layout desejado
                    recyclerView.setAdapter(historyAdapter);
                })

                .addOnFailureListener(e -> {
                    // Lida com erros ao recuperar o histórico do Firestore
                    Toast.makeText(HistoryActivity.this, "Erro ao recuperar o histórico", Toast.LENGTH_SHORT).show();
                });
    }


    private void initializeClicks(){
        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
    }


}