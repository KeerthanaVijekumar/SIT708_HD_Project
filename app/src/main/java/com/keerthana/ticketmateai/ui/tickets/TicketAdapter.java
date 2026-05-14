package com.keerthana.ticketmateai.ui.tickets;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
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

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private List<Ticket> tickets = new ArrayList<>();
    private OnTicketClickListener listener;

    public interface OnTicketClickListener {
        void onTicketClick(Ticket ticket);
    }

    public TicketAdapter(OnTicketClickListener listener) {
        this.listener = listener;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket ticket = tickets.get(position);
        holder.bind(ticket);
        holder.itemView.setOnClickListener(v -> listener.onTicketClick(ticket));
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    static class TicketViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvDescription, tvStatus, tvPriority, tvDate;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTicketTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvPriority = itemView.findViewById(R.id.tvPriority);
            tvDate = itemView.findViewById(R.id.tvDate);
        }

        public void bind(Ticket ticket) {
            tvTitle.setText(ticket.title);
            tvDescription.setText(ticket.description);
            tvStatus.setText(ticket.status);
            tvPriority.setText(ticket.priority);

            // Format date
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            tvDate.setText(sdf.format(new Date(ticket.createdAt)));

            // Status badge color
            setRoundedBackground(tvStatus, getStatusColor(ticket.status));

            // Priority badge color
            setRoundedBackground(tvPriority, getPriorityColor(ticket.priority));
        }

        private void setRoundedBackground(TextView tv, String colorHex) {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setCornerRadius(20f);
            drawable.setColor(Color.parseColor(colorHex));
            tv.setBackground(drawable);
        }

        private String getStatusColor(String status) {
            if (status == null) return "#6B7280";
            switch (status) {
                case "Open": return "#3B82F6";
                case "In Progress": return "#F59E0B";
                case "Resolved": return "#10B981";
                default: return "#6B7280";
            }
        }

        private String getPriorityColor(String priority) {
            if (priority == null) return "#6B7280";
            switch (priority) {
                case "High": return "#EF4444";
                case "Medium": return "#F59E0B";
                case "Low": return "#10B981";
                default: return "#6B7280";
            }
        }
    }
}