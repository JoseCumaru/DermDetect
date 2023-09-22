package com.example.dermdetect.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.example.dermdetect.R;

public class AboutActivity extends AppCompatActivity {

    ImageView leftIcon, rightIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        initializeComponents();
        initializeClicks();

    }

    private void initializeComponents() {
        leftIcon = findViewById(R.id.left_icon);
        rightIcon = findViewById(R.id.right_icon);
    }

    private void initializeClicks(){
        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        rightIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(AboutActivity.this, rightIcon);
                popupMenu.getMenuInflater().inflate(R.menu.menu_itens, popupMenu.getMenu());

                // Configure um ouvinte de clique de item de menu
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // Lide com o clique no item de menu aqui
                        switch (item.getItemId()) {
                            case 1:
                                // Ação para o Item 1
                                return true;
                            case 2:
                                // Ação para o Item 2
                                return true;
                            // Adicione mais casos conforme necessário
                            case  3:
                            default:
                                return false;
                        }
                    }
                });

                // Exiba o menu
                popupMenu.show();

            }
        });

    }
}
