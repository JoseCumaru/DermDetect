package com.example.testedermdetect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class InformationActivity extends AppCompatActivity {

    TextView textClasse, informations;
    ImageView leftIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        initializeComponents();
        initializeClicks();
    }


    private void initializeComponents(){
        Intent intent = getIntent();
        String classe = intent.getStringExtra("class");
        textClasse = findViewById(R.id.textClasse);
        textClasse.setText(classe);
        leftIcon = findViewById(R.id.left_icon);
    }

    private void initializeClicks(){
        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InformationActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}