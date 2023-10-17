package com.example.dermdetect.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.example.dermdetect.R;

public class ProfessionalHomeActivity extends AppCompatActivity {

    ImageView leftIcon, rightIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professional_home);

        initializeComponents();
        initializeClicks();

        leftIcon.setVisibility(View.INVISIBLE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_itens, menu); // Infla o menu a partir do arquivo menu.xml
        return true;
    }

    private void initializeComponents(){
        leftIcon = findViewById(R.id.left_icon);
        rightIcon = findViewById(R.id.right_icon);
    }

    private void initializeClicks(){
        rightIcon.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ProfessionalHomeActivity.this, findViewById(R.id.right_icon), R.style.PopupMenuStyle);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.menu_itens, popupMenu.getMenu());

                // Configure um ouvinte de clique de item de menu
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // Lidar com o clique no item de menu
                        switch (item.getTitle().toString()) {
                            case "Configurações":
                                Intent intentS = new Intent(ProfessionalHomeActivity.this, SetingsActivity.class);
                                startActivity(intentS);
                                return true;

                            case "Histórico":
                                return true;

                            case "Sobre":
                                Intent intentA = new Intent(ProfessionalHomeActivity.this, AboutActivity.class);
                                startActivity(intentA);
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                // Exibe o menu
                popupMenu.show();
            }
        });
    }
}