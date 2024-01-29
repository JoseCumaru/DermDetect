package com.example.dermdetect.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dermdetect.R;
import com.example.dermdetect.ui.HomeActivity;
import com.example.dermdetect.ui.ProfessionalHomeActivity;
import com.example.dermdetect.ui.SplashActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    TextView textRegister, textRecover;
    EditText editEmail, editPassword;
    Button buttonLogin;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private static final int BACK_PRESS_INTERVAL = 2000; // Intervalo em milissegundos
    private long backPressTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();

        initializeComponents();
        initializeClicks();

    }

    private void initializeComponents(){
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        buttonLogin = findViewById(R.id.btnLogin);
        textRegister = findViewById(R.id.textRegister);
        textRecover = findViewById(R.id.textRecover);

        db = FirebaseFirestore.getInstance();
    }

    private void initializeClicks(){
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkData();
            }
        });

        textRecover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRC = new Intent(LoginActivity.this, RecoverActivity.class);
                startActivity(intentRC);
            }
        });

        textRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentR = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intentR);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (backPressTime + BACK_PRESS_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
        } else {
            Toast.makeText(this, "Pressione novamente para sair", Toast.LENGTH_SHORT).show();
        }
        backPressTime = System.currentTimeMillis();
    }

    private void checkData(){
        if(TextUtils.isEmpty(editEmail.getText())){
            Toast.makeText(LoginActivity.this, "Insira seu email", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(editPassword.getText())) {
            Toast.makeText(LoginActivity.this, "Insira sua senha", Toast.LENGTH_SHORT).show();
        }else{
            signInUser();
        }

    }

    public Task<Integer> verifyUserType(String userID) {
        // Constroi a referência ao documento do usuário com base no ID de usuário
        DocumentReference userRef = db.collection("Users").document(userID);

        // Realiza uma consulta para obter o valor do campo "role" no documento do usuário
        return userRef.get().continueWith(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // Obtem o valor do campo "role" como um inteiro
                    Integer role = document.getLong("role").intValue();
                    return role;
                }
            }
            // Se o documento não existe ou ocorreu um erro, retorne null ou um valor padrão
            return null;
        });
    }

    private void signInUser(){
        String editEmailContent = editEmail.getText().toString().trim();
        String editPasswordContent = editPassword.getText().toString().trim();
        auth.signInWithEmailAndPassword(editEmailContent, editPasswordContent).addOnCompleteListener(autenticacao ->{
            //FirebaseUser currentUser = auth.getCurrentUser();
            if(autenticacao.isSuccessful()){
                String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
                Task<Integer> roleTask = verifyUserType(userId);
                roleTask.addOnCompleteListener(new OnCompleteListener<Integer>() {
                    @Override
                    public void onComplete(@NonNull Task<Integer> task) {
                        if (task.isSuccessful()) {
                            Integer userRole = task.getResult();
                            if (userRole != null) {
                                if (userRole == 1) {
                                    Intent intentH = new Intent(LoginActivity.this, HomeActivity.class);
                                    startActivity(intentH);
                                    finish();
                                } else if (userRole == 2) {
                                    Intent intentPH = new Intent(LoginActivity.this, ProfessionalHomeActivity.class);
                                    startActivity(intentPH);
                                    finish();
                                } else {
                                    // Lida com outros valores de userRole de acordo com a lógica do seu aplicativo
                                }
                            } else {
                                // userRole é null, lide com isso de acordo com a lógica do seu aplicativo
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Erro ao acessar os dados", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).addOnFailureListener(exception ->{
            Toast.makeText(LoginActivity.this, "Erro na autenticação, tente novamente", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            //Intent intentL = new Intent(this, LoginActivity.class);
            //startActivity(intentL);
            //finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}