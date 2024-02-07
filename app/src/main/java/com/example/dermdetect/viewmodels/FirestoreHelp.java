package com.example.dermdetect.viewmodels;

import static com.example.dermdetect.viewmodels.UserHistoryManager.decodeBase64ToBitmap;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.dermdetect.auth.AuthManager;
import com.example.dermdetect.ui.SetingsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FirestoreHelp {
    AuthManager auth;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private static final FirestoreHelp instance = new FirestoreHelp();
    public FirestoreHelp(){
    }
    public static FirestoreHelp getInstance() {
        return instance;
    }

    public void getCurrentUserName(TextView textView) {
        auth = new AuthManager();
        String userid = auth.getUserID();

        DocumentReference documentReference = firestore.collection("Users").document(userid);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
                    String nameUser = documentSnapshot.getString("name");
                    textView.append(nameUser);
                    //String imageBase64 = documentSnapshot.getString("imgperfil");
                }
            }
        });
    }

    public void getCurrentUserImage(ImageView imageView) {
        auth = new AuthManager();
        String userid = auth.getUserID();

        DocumentReference documentReference = firestore.collection("Users").document(userid);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
                    String imageBase64 = documentSnapshot.getString("imgperfil");
                    if (imageBase64 != null && !imageBase64.isEmpty()) {
                        Bitmap imageBitmap = decodeBase64ToBitmap(imageBase64);
                        if (imageBitmap != null) {
                            imageView.setImageBitmap(imageBitmap);
                        } else {
                            //Toast.makeText(SetingsActivity.this, "Erro ao decodificar imagem do perfil", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            }
        });


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

        String usuarioID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

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


}
