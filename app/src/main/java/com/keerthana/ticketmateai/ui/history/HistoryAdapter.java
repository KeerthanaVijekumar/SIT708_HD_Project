package com.keerthana.ticketmateai.ui.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.keerthana.ticketmateai.R;
import com.keerthana.ticketmateai.model.Ticket;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<Ticket> tickets = new ArrayList<>();

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.bind(tickets.get(position));
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvDate, tvNotes;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvNotes = itemView.findViewById(R.id.tvNotes);
        }

        public void bind(Ticket ticket) {
            tvTitle.setText(ticket.title);

            // Format resolved date
            SimpleDateFormat sdf = new SimpleDateFormat(
                    "dd MMM yyyy, hh:mm a", Locale.getDefault());
            if (ticket.resolvedAt > 0) {
                tvDate.setText("Resolved: " +
                        sdf.format(new Date(ticket.resolvedAt)));
            } else {
                tvDate.setText("Resolved: " +
                        sdf.format(new Date(ticket.createdAt)));
            }

            // Show resolution notes or fallback
            if (ticket.resolutionNotes != null &&
                    !ticket.resolutionNotes.isEmpty()) {
                tvNotes.setText(ticket.resolutionNotes);
            } else {
                tvNotes.setText("No resolution notes recorded.");
            }
        }
    }
}