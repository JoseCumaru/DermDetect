package com.example.dermdetect.viewmodels;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dermdetect.R;
import com.example.dermdetect.ui.HistoryActivity;
import com.google.api.Context;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<HistoryItem> historyItemList;

    public HistoryAdapter(List<HistoryItem> historyItemList) {
        this.historyItemList = historyItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (historyItemList.isEmpty()) {

            holder.imageView.setImageDrawable(null);
            holder.diseaseTextView.setText("");
            holder.confidenceTextView.setText("");
            holder.time.setText("");}
        else{
            HistoryItem historyItem = historyItemList.get(position);


            holder.imageView.setImageBitmap(historyItem.getImageURL());
            holder.diseaseTextView.setText(historyItem.getDetectedDisease());
            holder.confidenceTextView.setText(String.format("%.2f%%", historyItem.getConfidence()));

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy  -  HH:mm:ss", Locale.getDefault());
            String formattedTimestamp = dateFormat.format(historyItem.getTimestamp());
            holder.time.setText(formattedTimestamp);
        }
    }

    @Override
    public int getItemCount() {
        return historyItemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView diseaseTextView;
        TextView confidenceTextView;
        TextView time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageHistory);
            diseaseTextView = itemView.findViewById(R.id.textDisease);
            confidenceTextView = itemView.findViewById(R.id.textConfidence);
            time = itemView.findViewById(R.id.date);
        }
    }
}
