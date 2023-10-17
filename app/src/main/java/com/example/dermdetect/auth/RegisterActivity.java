package com.example.dermdetect.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dermdetect.R;
import com.example.dermdetect.ui.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText editNome, editEmail, editPassword;

    TextView goToRegisterP;
    Button buttonRegister;

    FirebaseAuth auth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        initializeComponents();
        initializeClicks();


    }

    private void initializeComponents(){
        editNome = findViewById(R.id.editNome);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        buttonRegister = findViewById(R.id.btnRegister);
        goToRegisterP = findViewById(R.id.textRegisterP);
    }

    private void initializeClicks(){

        goToRegisterP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, RegisterProfessionalActivity.class);
                startActivity(intent);
            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkData();
            }
        });
    }

    private void checkData(){
        if(editNome.getText().equals("")){
            Toast.makeText(RegisterActivity.this, "Insira seu nome", Toast.LENGTH_SHORT).show();
        } else if(editEmail.getText().equals("")){
            Toast.makeText(RegisterActivity.this, "Insira seu email", Toast.LENGTH_SHORT).show();
        } else if (editPassword.getText().equals("")) {
            Toast.makeText(RegisterActivity.this, "Insira sua senha", Toast.LENGTH_SHORT).show();
        }else{
            registerUser();
        }
    }

    private void registerUser(){
        String editEmailContent = editEmail.getText().toString().trim();
        String editPasswordContent = editPassword.getText().toString().trim();
        auth.createUserWithEmailAndPassword(editEmailContent, editPasswordContent).addOnCompleteListener(cadastro ->{
            if(cadastro.isSuccessful()){
                saveDates();
                Toast.makeText(RegisterActivity.this,"Cadastro realizado com sucesso", Toast.LENGTH_SHORT).show();
                Intent intentM = new Intent(this, HomeActivity.class);
                startActivity(intentM);
                finish();
            }else{
                String erro;
                try{
                    throw cadastro.getException();
                } catch (FirebaseAuthWeakPasswordException e){
                    erro = "Digite uma senha com no minimo 6 caracteres";
                    Toast.makeText(RegisterActivity.this, erro, Toast.LENGTH_SHORT).show();
                } catch (FirebaseAuthUserCollisionException e){
                    erro = "essa conta ja foi cadastrada";
                    Toast.makeText(RegisterActivity.this,erro, Toast.LENGTH_SHORT).show();
                } catch (FirebaseAuthInvalidCredentialsException e){
                    erro = "email invalido";
                    Toast.makeText(RegisterActivity.this,erro, Toast.LENGTH_SHORT).show();
                }
                catch (Exception e) {
                    erro = "erro ao cadastrar usuario";
                    Toast.makeText(RegisterActivity.this,erro, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveDates(){
        String editNomeContent = editNome.getText().toString().trim();
        String editEmailContent = editEmail.getText().toString().trim();
        String editPasswordContent = editPassword.getText().toString().trim();
        int role = 1;

        Map<String,Object> usuarios = new HashMap<>();
        usuarios.put("name",editNomeContent);
        usuarios.put("email",editEmailContent);
        usuarios.put("password",editPasswordContent);
        usuarios.put("role", role);

        String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = firestore.collection("Users").document(usuarioID);
        documentReference.set(usuarios);
    }

}