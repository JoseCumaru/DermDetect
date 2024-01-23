package com.example.dermdetect.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dermdetect.R;
import com.example.dermdetect.query.QueryRequest;

public class ProfessionalPerfilActivity extends AppCompatActivity {

    ImageView leftIcon;
    TextView solicitarConsulta, whatsapp;
    QueryRequest queryRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professional_perfil);

        initializeComponents();
        initializeClicks();
    }

    private void initializeComponents(){

        leftIcon = findViewById(R.id.left_icon);
        solicitarConsulta = findViewById(R.id.textViewSolicitarConsulta);
        whatsapp = findViewById(R.id.textViewWhatsapp);
        queryRequest = new QueryRequest(this);
    }

    private void initializeClicks(){

        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = "97984398480";
                queryRequest.openWhatsAppChat(number);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}