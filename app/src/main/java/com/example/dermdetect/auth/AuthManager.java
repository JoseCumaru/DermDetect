package com.example.dermdetect.auth;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.dermdetect.ui.HomeActivity;
import com.example.dermdetect.ui.ProfessionalHomeActivity;
import com.example.dermdetect.ui.SplashActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AuthManager extends SplashActivity {

    private FirebaseFirestore db;

    public void checkAuth(Activity SplashActivity) {
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Intent intentL = new Intent(SplashActivity, LoginActivity.class);
            startActivity(intentL);
            finish();
        } else {
            String userID = currentUser.getUid();
            Task<Integer> roleTask = verifyUserType(userID);

            roleTask.addOnCompleteListener(new OnCompleteListener<Integer>() {
                @Override
                public void onComplete(@NonNull Task<Integer> task) {
                    if (task.isSuccessful()) {
                        Integer userRole = task.getResult();
                        if (userRole != null) {
                            if (userRole == 1) {
                                Intent intentH = new Intent(SplashActivity, HomeActivity.class);
                                startActivity(intentH);
                                finish();
                            } else if (userRole == 2) {
                                Intent intentPH = new Intent(SplashActivity, ProfessionalHomeActivity.class);
                                startActivity(intentPH);
                                finish();
                            }
                        }
                    } else {
                        Toast.makeText(SplashActivity, "Erro ao obter dados", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public Task<Integer> verifyUserType(String userID) {
        // Construa a referência ao documento do usuário com base no ID de usuário
        DocumentReference userRef = db.collection("Users").document(userID);

        // Realize uma consulta para obter o valor do campo "role" no documento do usuário
        return userRef.get().continueWith(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Obtenha o valor do campo "role" como um inteiro
                    Integer role = document.getLong("role").intValue();
                    return role;
                }
            }
            // Se o documento não existe ou ocorreu um erro, retorne null ou um valor padrão
            return null;
        });
    }
}
