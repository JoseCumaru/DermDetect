package com.example.dermdetect.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.dermdetect.R;
import com.example.dermdetect.auth.LoginActivity;
import com.example.dermdetect.viewmodels.FirestoreHelp;
import com.example.dermdetect.viewmodels.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    private final Timer timer = new Timer();
    TimerTask timerTask;
    private FirebaseFirestore db;
    FirestoreHelp firestoreHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //checkNightMode();
        setContentView(R.layout.activity_splash);
        db = FirebaseFirestore.getInstance();
        firestoreHelp = new FirestoreHelp();

        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        checkAuth();
                    }
                });
            }
        };
        timer.schedule(timerTask, 3700);

    }

    /*private void checkNightMode(){
        // Recupera o estado do modo claro/escuro
        SharedPreferences sharedPreferences = getSharedPreferences("MODE", Context.MODE_PRIVATE);
        boolean isNightMode = sharedPreferences.getBoolean("night", false);

        // Apliqua o tema com base no estado
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }*/

    public void checkAuth() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Intent intentL = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intentL);
            finish();
        } else {
            //Coleta o ID do usuário
            String userID = currentUser.getUid();
            firestoreHelp.setCurrentUserData(userID);
            User.getInstance().setUserId(userID);
            //Verifica o tipo de usuario
            Task<Integer> roleTask = verifyUserType(userID);

            roleTask.addOnCompleteListener(new OnCompleteListener<Integer>() {
                @Override
                public void onComplete(@NonNull Task<Integer> task) {
                    if (task.isSuccessful()) {
                        Integer userRole = task.getResult();
                        if (userRole != null) {
                            if (userRole == 1) {

                                Intent intentH = new Intent(SplashActivity.this, HomeActivity.class);
                                startActivity(intentH);
                                finish();
                            } else if (userRole == 2) {
                                Intent intentPH = new Intent(SplashActivity.this, ProfessionalHomeActivity.class);
                                startActivity(intentPH);
                                finish();
                            }
                        }
                    } else {
                        Toast.makeText(SplashActivity.this, "Erro ao obter dados do usuário, tente novamente", Toast.LENGTH_SHORT).show();
                        Intent intentL = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intentL);
                        finish();
                    }
                }
            });
        }
    }

    //Metodo para verificar o tipo de usuário
    public Task<Integer> verifyUserType(String userID) {
        DocumentReference userRef = db.collection("Users").document(userID);

        // Realiza uma consulta para obter o valor do campo "role" no documento do usuário
        return userRef.get().continueWith(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    return Objects.requireNonNull(document.getLong("role")).intValue();
                }
            }
            return null;
        });
    }
    

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}