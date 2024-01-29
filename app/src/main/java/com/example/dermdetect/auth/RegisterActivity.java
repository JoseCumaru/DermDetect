package com.example.dermdetect.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

    EditText editName, editEmail, editPassword;
    String spinnerStateContent, spinnerCityContent;
    TextView goToRegisterP;
    Button buttonRegister;
    Spinner spinnerState, spinnerCity;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        initializeComponents();
        initializeClicks();
        initializeSpinners();
    }

    private void initializeComponents(){
        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        buttonRegister = findViewById(R.id.btnRegister);
        goToRegisterP = findViewById(R.id.textRegisterP);
        spinnerState = findViewById(R.id.spinnerEstate);
        spinnerCity = findViewById(R.id.spinnerCity);
        authManager = new AuthManager();
    }

    private void initializeSpinners(){
        ArrayAdapter<CharSequence> estadosAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.estados_array,
                android.R.layout.simple_spinner_item
        );
        estadosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerState.setAdapter(estadosAdapter);

        // Defina um ouvinte de seleção para o Spinner de estado
        spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String spinnerContent = parentView.getItemAtPosition(position).toString();
                spinnerStateContent = spinnerContent;
                updateCidadesAdapter(spinnerCity, position);
                spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        String spinnerContent = parentView.getItemAtPosition(position).toString();
                        spinnerCityContent = spinnerContent;
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // quando nada é selecionado
                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Não é necessário fazer nada aqui
            }
        });
    }
    private void updateCidadesAdapter(Spinner spinnerCidade, int estadoPosition) {
        int cidadesArrayResourceId;
        switch (estadoPosition) {
            case 0:
                cidadesArrayResourceId = R.array.cidades_amazonas_array;
                break;
            case 1:
                cidadesArrayResourceId = R.array.cidades_acre_array;
                break;
            case 2:
                cidadesArrayResourceId = R.array.cidades_para_array;
                break;
            // Adicione mais casos conforme necessário
            default:
                cidadesArrayResourceId = R.array.cidades_amazonas_array; // Padrão para o exemplo
        }

        // Cria o adaptador para o Spinner de cidade com base no array de cidades
        ArrayAdapter<CharSequence> cidadesAdapter = ArrayAdapter.createFromResource(
                this,
                cidadesArrayResourceId,
                android.R.layout.simple_spinner_item
        );
        cidadesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // adaptador atualizado para o Spinner de cidade
        spinnerCidade.setAdapter(cidadesAdapter);
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
        if(editName.getText().toString().trim().equals("") && editEmail.getText().toString().trim().equals("") && editPassword.getText().toString().trim().equals("")){
            Toast.makeText(RegisterActivity.this, "Insira seus dados", Toast.LENGTH_SHORT).show();
        }else if(editName.getText().toString().trim().equals("")){
            Toast.makeText(RegisterActivity.this, "Insira seu nome", Toast.LENGTH_SHORT).show();
        } else if(editEmail.getText().toString().trim().equals("")){
            Toast.makeText(RegisterActivity.this, "Insira seu email", Toast.LENGTH_SHORT).show();
        } else if (editPassword.getText().toString().trim().equals("")) {
            Toast.makeText(RegisterActivity.this, "Insira sua senha", Toast.LENGTH_SHORT).show();
        }else{
            authManager.registerUser(RegisterActivity.this, spinnerStateContent, spinnerCityContent, editEmail, editPassword, editName);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}