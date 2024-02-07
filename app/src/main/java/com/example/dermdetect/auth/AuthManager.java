package com.example.dermdetect.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.dermdetect.ui.HomeActivity;
import com.example.dermdetect.ui.ProfessionalHomeActivity;
import com.example.dermdetect.viewmodels.FirestoreHelp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AuthManager {

    FirebaseAuth auth;
    FirestoreHelp firestoreHelp;
    private static final AuthManager instance = new AuthManager();

    public AuthManager() {
    }

    public static AuthManager getInstance() {
        return instance;
    }

    public String getUserID(){
        FirebaseUser currentUSer = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUSer != null;
        return currentUSer.getUid();
    }

    public void checkAuth(Context context) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Intent intentL = new Intent(context, LoginActivity.class);
            context.startActivity(intentL);
        } else {
            String userID = currentUser.getUid();
            Task<Integer> roleTask = verifyUserType(userID, context);

            roleTask.addOnCompleteListener(new OnCompleteListener<Integer>() {
                @Override
                public void onComplete(Task<Integer> task) {
                    if (task.isSuccessful()) {
                        Integer userRole = task.getResult();
                        if (userRole != null) {
                            if (userRole == 1) {
                                Intent intentH = new Intent(context, HomeActivity.class);
                                context.startActivity(intentH);
                            } else if (userRole == 2) {
                                Intent intentPH = new Intent(context, ProfessionalHomeActivity.class);
                                context.startActivity(intentPH);
                            }
                        }
                    } else {
                        Toast.makeText(context, "Erro ao obter dados", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public Task<Integer> verifyUserType(String userID, Context context) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Referência ao documento do usuário com base no ID de usuário
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
                // Se o documento não existe ou ocorreu um erro, retorne null ou um valor padrão
                return null;
            }
            return null;
        });
    }

    public void registerUser(Activity registeruser, String spinnerState, String spinnerCity, EditText editEmail, EditText editPassword, EditText editName){
        firestoreHelp = new FirestoreHelp();
        auth = FirebaseAuth.getInstance();
        String editEmailContent = editEmail.getText().toString().trim();
        String editPasswordContent = editPassword.getText().toString().trim();
        auth.createUserWithEmailAndPassword(editEmailContent, editPasswordContent).addOnCompleteListener(cadastro ->{
            if(cadastro.isSuccessful()){
                firestoreHelp.saveDataUser(editName, editEmail, editPassword, spinnerState, spinnerCity);
                Toast.makeText(registeruser,"Cadastro realizado com sucesso", Toast.LENGTH_SHORT).show();
                Intent intentM = new Intent(registeruser, HomeActivity.class);
                registeruser.startActivity(intentM);
                registeruser.finish();

            }else{
                String erro;
                try{
                    throw cadastro.getException();
                } catch (FirebaseAuthWeakPasswordException e){
                    erro = "Digite uma senha com no minimo 6 caracteres";
                    Toast.makeText(registeruser, erro, Toast.LENGTH_SHORT).show();
                } catch (FirebaseAuthUserCollisionException e){
                    erro = "essa conta ja foi cadastrada";
                    Toast.makeText(registeruser,erro, Toast.LENGTH_SHORT).show();
                } catch (FirebaseAuthInvalidCredentialsException e){
                    erro = "email invalido";
                    Toast.makeText(registeruser,erro, Toast.LENGTH_SHORT).show();
                }
                catch (Exception e) {
                    erro = "erro ao cadastrar usuario";
                    Toast.makeText(registeruser,erro, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void registerProfessional(Activity registerProfessional, EditText emailP, EditText passwordP, EditText nameP, EditText cpfP, EditText phoneP, String selectedEspecialization, EditText licenseP){
        firestoreHelp = new FirestoreHelp();
        auth = FirebaseAuth.getInstance();
        String editEmailContent = emailP.getText().toString().trim();
        String editPasswordContent = passwordP.getText().toString().trim();
        auth.createUserWithEmailAndPassword(editEmailContent, editPasswordContent).addOnCompleteListener(cadastro ->{
            if(cadastro.isSuccessful()){
                firestoreHelp.saveDataProfessional(nameP, emailP, cpfP, phoneP, selectedEspecialization, licenseP);
                Toast.makeText(registerProfessional,"Cadastro realizado com sucesso", Toast.LENGTH_SHORT).show();
                Intent intentM = new Intent(registerProfessional, ProfessionalHomeActivity.class);
                registerProfessional.startActivity(intentM);
                registerProfessional.finish();
            }else{
                String erro;
                try{
                    throw cadastro.getException();
                } catch (FirebaseAuthWeakPasswordException e){
                    erro = "Digite uma senha com no minimo 6 caracteres";
                    Toast.makeText(registerProfessional, erro, Toast.LENGTH_SHORT).show();
                } catch (FirebaseAuthUserCollisionException e){
                    erro = "essa conta ja foi cadastrada";
                    Toast.makeText(registerProfessional,erro, Toast.LENGTH_SHORT).show();
                } catch (FirebaseAuthInvalidCredentialsException e){
                    erro = "email invalido";
                    Toast.makeText(registerProfessional,erro, Toast.LENGTH_SHORT).show();
                }
                catch (Exception e) {
                    erro = "erro ao cadastrar usuario";
                    Toast.makeText(registerProfessional,erro, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
