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
import android.widget.Toast;

import com.example.dermdetect.R;
import com.example.dermdetect.ui.HomeActivity;
import com.example.dermdetect.ui.ProfessionalHomeActivity;
import com.example.dermdetect.viewmodels.FirestoreHelp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterProfessionalActivity extends AppCompatActivity {
    EditText nameP, emailP, passwordP, cpfP, phoneP, licenseP;
    Button buttonRegister;
    Spinner especializationP;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    String selectedEspecialization;
    AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_professional);

        initializeComponents();
        initializeClicks();
    }

    private void initializeComponents(){
        nameP = findViewById(R.id.editNomeP);
        emailP = findViewById(R.id.editEmailP);
        passwordP = findViewById(R.id.editPasswordP);
        cpfP = findViewById(R.id.editCpf);
        phoneP = findViewById(R.id.editContactPhone);

        especializationP = findViewById(R.id.spinnerEspecializationP);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.especializacoes, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        especializationP.setAdapter(adapter);

        licenseP = findViewById(R.id.editLicenseP);

        buttonRegister = findViewById(R.id.btnRegister);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        authManager = new AuthManager();
    }

    private void initializeClicks(){
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkData();
            }
        });

        especializationP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedEspecialization = parentView.getItemAtPosition(position).toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

    }

    private void checkData(){
        if (nameP.getText().equals("") || emailP.getText().equals("") || phoneP.getText().equals("") || especializationP.isActivated() || licenseP.getText().equals("") || passwordP.getText().equals("")) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
        }
        else {
            verifyLicense();
        }
    }

    private void verifyLicense(){
        String licensePContent = licenseP.getText().toString().trim();
        if (licensePContent.equals("MD-123456")) {
            authManager.registerProfessional(this, emailP, passwordP, nameP, cpfP, phoneP, selectedEspecialization, licenseP);
        }else{
            Toast.makeText(this, "Licença Inválida", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}