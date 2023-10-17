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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterProfessionalActivity extends AppCompatActivity {
    EditText nameP, emailP, phoneP, licenseP, passwordP;
    Button buttonRegister;
    Spinner especializationP;
    FirebaseAuth auth;
    FirebaseFirestore firestore;
    String especializacaoSelecionada;

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
        phoneP = findViewById(R.id.editContactPhone);

        especializationP = findViewById(R.id.spinnerEspecializationP);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.especializacoes, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        especializationP.setAdapter(adapter);

        licenseP = findViewById(R.id.editLicenseP);
        passwordP = findViewById(R.id.editPasswordP);
        buttonRegister = findViewById(R.id.btnRegister);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
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
                especializacaoSelecionada = parentView.getItemAtPosition(position).toString();
                // Faça algo com a especialização selecionada
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

    }

    private void checkData(){
        if (nameP.getText().equals("") || emailP.getText().equals("") || phoneP.getText().equals("") || especializationP.isActivated() || licenseP.getText().equals("") || passwordP.getText().equals("")) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
        }else {
            verifyLicense();
        }
    }

    private void verifyLicense(){
        String licensePContent = licenseP.getText().toString().trim();
        if (licensePContent.equals("MD-123456")) {
            registerProfessional();
        }else{
            Toast.makeText(this, "Licença Inválida", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerProfessional(){
        String editEmailContent = emailP.getText().toString().trim();
        String editPasswordContent = passwordP.getText().toString().trim();
        auth.createUserWithEmailAndPassword(editEmailContent, editPasswordContent).addOnCompleteListener(cadastro ->{
            if(cadastro.isSuccessful()){
                saveData();
                Toast.makeText(RegisterProfessionalActivity.this,"Cadastro realizado com sucesso", Toast.LENGTH_SHORT).show();
                Intent intentM = new Intent(this, ProfessionalHomeActivity.class);
                startActivity(intentM);
                finish();
            }else{
                String erro;
                try{
                    throw cadastro.getException();
                } catch (FirebaseAuthWeakPasswordException e){
                    erro = "Digite uma senha com no minimo 6 caracteres";
                    Toast.makeText(RegisterProfessionalActivity.this, erro, Toast.LENGTH_SHORT).show();
                } catch (FirebaseAuthUserCollisionException e){
                    erro = "essa conta ja foi cadastrada";
                    Toast.makeText(RegisterProfessionalActivity.this,erro, Toast.LENGTH_SHORT).show();
                } catch (FirebaseAuthInvalidCredentialsException e){
                    erro = "email invalido";
                    Toast.makeText(RegisterProfessionalActivity.this,erro, Toast.LENGTH_SHORT).show();
                }
                catch (Exception e) {
                    erro = "erro ao cadastrar usuario";
                    Toast.makeText(RegisterProfessionalActivity.this,erro, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void saveData(){
        String editNomeContent = nameP.getText().toString().trim();
        String editEmailContent = emailP.getText().toString().trim();
        String editPasswordContent = passwordP.getText().toString().trim();
        String editPhoneContent = phoneP.getText().toString().trim();
        String editEspecializationContent = especializacaoSelecionada;
        String editLicenseContent = licenseP.getText().toString().trim();
        int role = 2;

        Map<String,Object> professionals = new HashMap<>();
        professionals.put("name",editNomeContent);
        professionals.put("email",editEmailContent);
        professionals.put("phone", editPhoneContent);
        professionals.put("especialization", editEspecializationContent);
        professionals.put("license", editLicenseContent);
        professionals.put("role", role);


        String professionalID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = firestore.collection("Users").document(professionalID);
        documentReference.set(professionals);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}