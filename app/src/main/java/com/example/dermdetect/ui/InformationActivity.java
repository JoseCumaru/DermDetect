package com.example.dermdetect.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dermdetect.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

public class InformationActivity extends AppCompatActivity {
    TextView textClasse, textDescrition, textSymptons, textTreatment, textCm, informations, nameP1, nameP2, especializationP1, especializationP2;
    ImageView leftIcon, rightIcon;
    String classe;
    FirebaseFirestore firestore;

    CardView cardProfile1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        firestore = FirebaseFirestore.getInstance();
        initializeComponents();
        initializeClicks();
        setInformations();

    }

    private void initializeComponents(){
        Intent intent = getIntent();
        classe = intent.getStringExtra("class");
        textClasse = findViewById(R.id.textClasse);
        textClasse.setText(classe);
        leftIcon = findViewById(R.id.left_icon);
        rightIcon = findViewById(R.id.right_icon);
        textDescrition = findViewById(R.id.textDescrition);
        textSymptons = findViewById(R.id.textSymptons);
        textTreatment = findViewById(R.id.textTreatment);
        textCm = findViewById(R.id.textCM);
        nameP1 = findViewById(R.id.textViewNameP1);
        nameP2 = findViewById(R.id.textViewNameP2);
        especializationP1 = findViewById(R.id.textViewEspecializationP1);
        especializationP2 = findViewById(R.id.textViewEspecializationP2);
        fetchProfessionals();
        cardProfile1 = findViewById(R.id.cardProfileP1);
    }

    private void setInformations(){
        if(Objects.equals(classe, "Acne")){
            textDescrition.setText(R.string.acne_description);
            textSymptons.setText(R.string.acne_symptons);
            textTreatment.setText(R.string.acne_treatment);
            textCm.setText(R.string.acne_cm);

        } else if (Objects.equals(classe, "Rosácea")) {
            textDescrition.setText(R.string.rosacea_description);
            textSymptons.setText(R.string.rosacea_symptons);
            textTreatment.setText(R.string.rosacea_treatment);
            textCm.setText(R.string.rosacea_cm);

        } else if (Objects.equals(classe, "Impetigo")) {
            textDescrition.setText(R.string.impetigo_description);
            textSymptons.setText(R.string.impetigo_symptons);
            textTreatment.setText(R.string.impetigo_treatment);
            textCm.setText(R.string.impetigo_cm);

        } else if (Objects.equals(classe, "Melasma")) {
            textDescrition.setText(R.string.melasma_description);
            textSymptons.setText(R.string.melasma_symptons);
            textTreatment.setText(R.string.melasma_treatment);
            textCm.setText(R.string.melasma_cm);

        } else if (Objects.equals(classe, "Psoríase")) {
            textDescrition.setText(R.string.psoriase_description);
            textSymptons.setText(R.string.psoriase_symptons);
            textTreatment.setText(R.string.psoriase_treatment);
            textCm.setText(R.string.psoriase_cm);

        } else if (Objects.equals(classe, "Micose")) {
            textDescrition.setText(R.string.micose_description);
            textSymptons.setText(R.string.micose_symptons);
            textTreatment.setText(R.string.micose_treatment);
            textCm.setText(R.string.micose_cm);

        } else if (Objects.equals(classe, "Dermatite Atópica")) {
            textDescrition.setText(R.string.dermatite_description);
            textSymptons.setText(R.string.dermatite_symptons);
            textTreatment.setText(R.string.dermatite_treatment);
            textCm.setText(R.string.dermatite_cm);

        } else if (Objects.equals(classe, "Melanoma")) {
            textDescrition.setText(R.string.melanoma_description);
            textSymptons.setText(R.string.melanoma_symptons);
            textTreatment.setText(R.string.melanoma_treatment);
            textCm.setText(R.string.melanoma_cm);
        }
    }

    private void initializeClicks(){
        leftIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        cardProfile1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentProfileP = new Intent(InformationActivity.this, ProfessionalPerfilActivity.class);
                startActivity(intentProfileP);
            }
        });
    }

    private void fetchProfessionals() {
        int number = 2;
        CollectionReference professionalsRef = firestore.collection("Users");

        professionalsRef.whereEqualTo("role", number)
                .whereArrayContains("especialization", "Dermatologia Cirúrgica")
                .limit(2)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int index = 1;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String nameProfessional = document.getString("name");
                            String specializationProfessional = document.getString("especialization");
                            String imagemPerfilProfissional = document.getString("imagePerfil");

                            if (index == 1) {
                                nameP1.setText(nameProfessional);
                                especializationP1.setText(specializationProfessional);
                                // Carregue a imagem de perfil na ImageView correspondente
                                // Use uma biblioteca como Picasso ou Glide para carregar a imagem
                            } else if (index == 2) {
                                nameP2.setText(nameProfessional);
                                especializationP2.setText(specializationProfessional);
                                // Carregue a imagem de perfil na ImageView correspondente
                                // Use uma biblioteca como Picasso ou Glide para carregar a imagem
                            }
                            index++;
                        }
                    } else {
                        Toast.makeText(InformationActivity.this, "Erro ao recuperar dados dos profissionais", Toast.LENGTH_SHORT).show();
                        // Lidar com erros na consulta
                    }
                });
    }


}