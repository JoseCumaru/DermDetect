package com.example.dermdetect.ui;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dermdetect.R;
import com.example.dermdetect.auth.LoginActivity;
import com.example.dermdetect.viewmodels.FirestoreHelp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetingsActivity extends AppCompatActivity {

    TextView textNomeUser, textEditPerfil, textProblem, textAppearance, textPrivacy, textSupport, textSignOut;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch switcher;
    Boolean nightMode;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    ImageView leftIcon, rightIcon, imgPerfil;
    FirebaseAuth auth;
    FirebaseFirestore firestore;

    FirestoreHelp firestoreHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setings);
        firestoreHelp = new FirestoreHelp();

        initializeComponents();

        //setImgPerfil();
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

    }


    private void initializeComponents() {
        leftIcon = findViewById(R.id.left_icon);
        rightIcon = findViewById(R.id.right_icon);
        textSignOut = findViewById(R.id.textSignOut);
        textNomeUser = findViewById(R.id.nameUser);
        switcher = findViewById(R.id.Switcher);
        imgPerfil =findViewById(R.id.imgPerfil);

        firestoreHelp.getCurrentUserName(textNomeUser);
        firestoreHelp.getCurrentUserImage(imgPerfil);
    }

    private void initializeClicks(){
        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
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
                // Finaliza a HomeActivity (se existir)

                HomeActivity homeActivity = HomeActivity.getInstance();
                    homeActivity.finish();


                finish();
            }
        });

        imgPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(cameraIntent,2);
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            Uri dat = data.getData();
            Bitmap image = null;

            try {
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dat);
            } catch (IOException e) {
                //  erros ao carregar a imagem
                Toast.makeText(SetingsActivity.this, "Erro ao carregar a imagem: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            if (image != null) {
                imgPerfil.setImageBitmap(image);

                // Converte a imagem em um array de bytes (byte[])
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageData = baos.toByteArray();

                List<Byte> imageBytesList = new ArrayList<>();
                for (byte b : imageData) {
                    imageBytesList.add(b);
                }

                String base64ImageData = Base64.encodeToString(imageData, Base64.DEFAULT);


                // Salva a imagem no Firestore e atualiza o campo imgperfil
                String userID = auth.getCurrentUser().getUid();
                firestore = FirebaseFirestore.getInstance();
                DocumentReference userRef = firestore.collection("Users").document(userID);

                // Primeiro, verifica se o documento do usuário existe
                userRef.get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                if (!task.getResult().exists()) {
                                    // se nao existir, cria
                                    Map<String, Object> userData = new HashMap<>();
                                    userData.put("imgperfil", base64ImageData);
                                    userRef.set(userData)
                                            .addOnSuccessListener(aVoid -> {
                                                // Documento do usuário criado com sucesso

                                                userRef.update("imgperfil", base64ImageData)
                                                        .addOnSuccessListener(aVoid2 -> {
                                                            // Sucesso, a imagem foi salva no Firestore
                                                            Toast.makeText(SetingsActivity.this, "Imagem de perfil salva com sucesso", Toast.LENGTH_SHORT).show();
                                                        })
                                                        .addOnFailureListener(e2 -> {
                                                            // erros ao salvar a imagem
                                                            Toast.makeText(SetingsActivity.this, "Erro ao salvar imagem de perfil: " + e2.getMessage(), Toast.LENGTH_SHORT).show();
                                                        });
                                            })
                                            .addOnFailureListener(e1 -> {
                                                // Lidar com erros ao criar o documento do usuário
                                                Toast.makeText(SetingsActivity.this, "Erro ao criar documento do usuário: " + e1.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                } else {
                                    // O documento do usuário já existe, então apenas atualiza o campo "imgperfil"
                                    userRef.update("imgperfil", base64ImageData)
                                            .addOnSuccessListener(aVoid -> {
                                                // Sucesso, a imagem foi salva no Firestore
                                                Toast.makeText(SetingsActivity.this, "Imagem de perfil atualizada com sucesso", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e2 -> {
                                                Toast.makeText(SetingsActivity.this, "Erro ao salvar imagem de perfil: " + e2.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                }
                            } else {
                                Toast.makeText(SetingsActivity.this, "Erro ao verificar o documento do usuário: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    private static Bitmap decodeBase64ToBitmap(String base64) {
        byte[] decodedBytes = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}