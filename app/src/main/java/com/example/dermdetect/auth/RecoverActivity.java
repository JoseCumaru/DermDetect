package com.example.dermdetect.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dermdetect.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class RecoverActivity extends AppCompatActivity {

    EditText editEmail;
    Button btnEnviar;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover);
        initializeComponents();
        initializeClicks();
    }

    private void initializeClicks(){

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword(editEmail.getText().toString().trim());
            }
        });
    }

    private void resetPassword(String email) {
        if (TextUtils.isEmpty(email)) {
            // Trate o caso em que o email está vazio
            Toast.makeText(this,"O campo de email está vazio", Toast.LENGTH_SHORT).show();
            System.out.println("O email está vazio");
            return;
        }

        auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Sucesso: Um email de redefinição de senha foi enviado para o usuário

                            Toast.makeText(RecoverActivity.this,"Um email de redefinição de senha foi enviado para " + email, Toast.LENGTH_SHORT).show();

                        } else {
                            // Falha: Ocorreu um erro ao enviar o email de redefinição de senha
                            Toast.makeText(RecoverActivity.this,"Falha ao enviar email de redefinição de senha: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void initializeComponents(){
        editEmail = findViewById(R.id.editEmailR);
        btnEnviar = findViewById(R.id.btnEnviar);
    }

}