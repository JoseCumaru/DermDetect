package com.example.dermdetect.viewmodels;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.example.dermdetect.viewmodels.HistoryItem;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserHistoryManager {
    private static FirebaseFirestore firestore;
    private static FirebaseAuth auth;

    public UserHistoryManager(){
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    private String encodeBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public Task<DocumentReference> addToHistory(Bitmap image, String detectedDisease, double confidence) {
        String currentUserUid = auth.getCurrentUser().getUid();
        Map<String, Object> historyData = new HashMap<>();
        historyData.put("image", encodeBitmapToBase64(image)); // Converta a imagem Bitmap para um formato apropriado
        historyData.put("detectedDisease", detectedDisease);
        historyData.put("confidence", confidence);
        historyData.put("timestamp", new Date());

        return firestore.collection("Users")
                .document(currentUserUid) // Substitua pelo UID do usuário atual
                .collection("History")
                .add(historyData);
    }

    public static void removeFromHistory(String historyItemId){
        if (auth.getCurrentUser() != null) {
            String userID = auth.getCurrentUser().getUid();

            // Acessa o item de histórico específico e exclua-o
            CollectionReference historyRef = firestore.collection("Users").document(userID).collection("History");
            historyRef.document(historyItemId).delete();
        }
    }

    public static Task<List<HistoryItem>> getHistory() {
        // Verifica se o usuário está autenticado
        if (auth.getCurrentUser() != null) {
            // Obtem o ID do usuário atual
            String userId = auth.getCurrentUser().getUid();

            // Acessa a coleção de histórico do usuário atual
            CollectionReference historyRef = firestore.collection("Users").document(userId).collection("History");

            // Executa a consulta para obter todos os documentos na coleção
            return historyRef.orderBy("timestamp", Query.Direction.DESCENDING).get().continueWith(task -> {
                if (task.isSuccessful()) {
                    List<HistoryItem> historyList = new ArrayList<>();

                    // Itera sobre os documentos recuperados
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Converte cada documento em um objeto HistoryItem
                        String imageBase64 = document.getString("image");
                        String detectedDisease = document.getString("detectedDisease");
                        double confidence = document.getDouble("confidence");
                        Date timestamp = document.getDate("timestamp");

                        Bitmap imageBitmap = decodeBase64ToBitmap(imageBase64);

                        HistoryItem historyItem = new HistoryItem(imageBitmap, detectedDisease, confidence, timestamp);

                        // Adiciona o objeto HistoryItem à lista
                        historyList.add(historyItem);
                    }

                    return historyList;
                } else {
                    // Tratamento de erro
                    return null;
                }
            });
        } else {
            // O usuário não está autenticado, retorna uma lista vazia ou lidere com isso de acordo com sua lógica
            return Tasks.forResult(new ArrayList<HistoryItem>());
        }
    }

    private static Bitmap decodeBase64ToBitmap(String base64) {
        byte[] decodedBytes = Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
