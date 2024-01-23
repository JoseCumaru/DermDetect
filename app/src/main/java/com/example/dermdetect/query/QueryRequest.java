package com.example.dermdetect.query;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

public class QueryRequest {

    private Context context;

    public QueryRequest(Context context) {
        this.context = context;
    }
    String userID;
    String description;

    public QueryRequest() {
        // Construtor vazio necessário para Firebase Firestore
    }

    public QueryRequest(String userID, String description) {
        this.userID = userID;
        this.description = description;
    }

    public void openWhatsAppChat(String phoneNumber) {
        try {
            // Verifica se o WhatsApp está instalado no dispositivo
            PackageManager packageManager = context.getPackageManager();
            Intent whatsappIntent = new Intent(Intent.ACTION_VIEW);
            String url = "https://api.whatsapp.com/send?phone=" + phoneNumber;
            whatsappIntent.setPackage("com.whatsapp");

            if (packageManager.resolveActivity(whatsappIntent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                whatsappIntent.setData(Uri.parse(url));
                context.startActivity(whatsappIntent);
            } else {
                // WhatsApp não está instalado
                Toast.makeText(context, "WhatsApp não está instalado no seu dispositivo", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            // Lida com possíveis erros
            Toast.makeText(context, "Erro ao abrir o WhatsApp: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }



    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
