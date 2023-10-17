package com.example.dermdetect.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dermdetect.R;

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

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryItem historyItem = historyItemList.get(position);

        // Exibir os dados do HistoryItem nos elementos da interface do usuário
        holder.imageView.setImageBitmap(historyItem.getImageURL());
        holder.diseaseTextView.setText(historyItem.getDetectedDisease());
        holder.confidenceTextView.setText(String.format("%.2f%%", historyItem.getConfidence()));

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy  -  HH:mm:ss", Locale.getDefault());
        String formattedTimestamp = dateFormat.format(historyItem.getTimestamp());
        holder.time.setText(formattedTimestamp);
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
