package com.example.dermdetect.viewmodels;

import android.widget.EditText;
import android.widget.Spinner;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirestoreHelp {

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private static final FirestoreHelp instance = new FirestoreHelp();
    public FirestoreHelp(){
    }
    public static FirestoreHelp getInstance() {
        return instance;
    }


    public void saveDataUser(EditText editName, EditText editEmail, EditText editPassword, String spinnerState, String spinnerCity){
        String editNomeContent = editName.getText().toString().trim();
        String editEmailContent = editEmail.getText().toString().trim();
        String editPasswordContent = editPassword.getText().toString().trim();
        int role = 1;

        Map<String,Object> usuarios = new HashMap<>();
        usuarios.put("name",editNomeContent);
        usuarios.put("email",editEmailContent);
        usuarios.put("password",editPasswordContent);
        usuarios.put("state", spinnerState);
        usuarios.put("city", spinnerCity);
        usuarios.put("role", role);

        String usuarioID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = firestore.collection("Users").document(usuarioID);
        documentReference.set(usuarios);
    }


    public void saveDataProfessional(EditText nameP, EditText emailP, EditText cpfP, EditText phoneP, String selectedEspecialization, EditText licenseP){
        String editNomeContent = nameP.getText().toString().trim();
        String editEmailContent = emailP.getText().toString().trim();
        String editCpfContent = cpfP.getText().toString().trim();
        String editPhoneContent = phoneP.getText().toString().trim();
        String editEspecializationContent = String.valueOf(selectedEspecialization);
        String editLicenseContent = licenseP.getText().toString().trim();
        int role = 2;

        Map<String,Object> professionals = new HashMap<>();
        professionals.put("name",editNomeContent);
        professionals.put("email",editEmailContent);
        professionals.put("cpf", editCpfContent);
        professionals.put("phone", editPhoneContent);
        professionals.put("especialization", editEspecializationContent);
        professionals.put("license", editLicenseContent);
        professionals.put("role", role);

        String professionalID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DocumentReference documentReference = firestore.collection("Users").document(professionalID);
        documentReference.set(professionals);
    }

    public void getUserData(){

    }

}
