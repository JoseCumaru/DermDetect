package com.example.dermdetect.ui;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;

import com.example.dermdetect.R;
import com.example.dermdetect.auth.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class SetingsActivity extends AppCompatActivity {

    TextView textNomeUser, textEditPerfil, textProblem, textAppearance, textPrivacy, textSupport, textSignOut;
    Switch switcher;
    Boolean nightMode;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    ImageView leftIcon, rightIcon;
    FirebaseAuth auth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setings);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        initializeComponents();
        initializeClicks();
        setNightMode();
    }

    private void setNightMode(){

        //usa-se SharedPreferences para salvar o modo se sair do app
        sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        nightMode = sharedPreferences.getBoolean("night", false); //Light mode é o modo padrão

        if(nightMode){
            switcher.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(nightMode){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("night", false);
                }else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor = sharedPreferences.edit();
                    editor.putBoolean("night", true);
                }
                editor.apply();

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        String userId = auth.getCurrentUser().getUid();
        DocumentReference documentReference = firestore.collection("Users").document(userId);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if(documentSnapshot != null){
                    textNomeUser.setText(documentSnapshot.getString("name"));
                }
            }
        });

    }

    private void initializeComponents() {
        leftIcon = findViewById(R.id.left_icon);
        rightIcon = findViewById(R.id.right_icon);
        textSignOut = findViewById(R.id.textSignOut);
        textNomeUser = findViewById(R.id.nameUser);
        switcher = findViewById(R.id.Switcher);
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
                PopupMenu popupMenu = new PopupMenu(SetingsActivity.this, rightIcon);
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

        textSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent intentL = new Intent(SetingsActivity.this, LoginActivity.class);
                startActivity(intentL);
            }
        });
    }
}