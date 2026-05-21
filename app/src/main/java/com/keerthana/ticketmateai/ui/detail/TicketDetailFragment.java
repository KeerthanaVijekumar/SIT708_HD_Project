package com.keerthana.ticketmateai.ui.detail;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.keerthana.ticketmateai.databinding.FragmentTicketDetailBinding;
import com.keerthana.ticketmateai.model.Ticket;

public class TicketDetailFragment extends Fragment {

    private FragmentTicketDetailBinding binding;
    private TicketDetailViewModel viewModel;
    private Ticket currentTicket;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTicketDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(TicketDetailViewModel.class);

        // Get ticket ID from arguments
        int ticketId = getArguments() != null ?
                getArguments().getInt("ticketId", -1) : -1;

        if (ticketId == -1) return;

        // Observe ticket data
        viewModel.getTicketById(ticketId).observe(getViewLifecycleOwner(), ticket -> {
            if (ticket != null) {
                currentTicket = ticket;
                populateUI(ticket);
            }
        });

        // Observe analysis state
        viewModel.getAnalysisState().observe(getViewLifecycleOwner(), state -> {
            switch (state) {
                case IDLE:
                    binding.loadingState.setVisibility(View.GONE);
                    break;
                case LOADING:
                    binding.loadingState.setVisibility(View.VISIBLE);
                    binding.btnAnalyse.setEnabled(false);
                    binding.cardAiSummary.setVisibility(View.GONE);
                    binding.cardSuggestedSteps.setVisibility(View.GONE);
                    binding.cardUnsafe.setVisibility(View.GONE);
                    break;
                case DONE:
                    binding.loadingState.setVisibility(View.GONE);
                    binding.btnAnalyse.setEnabled(true);
                    binding.cardAiSummary.setVisibility(View.VISIBLE);
                    binding.cardSuggestedSteps.setVisibility(View.VISIBLE);
                    binding.cardUnsafe.setVisibility(View.GONE);
                    break;
                case ERROR:
                    binding.loadingState.setVisibility(View.GONE);
                    binding.btnAnalyse.setEnabled(true);
                    Toast.makeText(getContext(),
                            "Analysis failed. Check model is installed.",
                            Toast.LENGTH_LONG).show();
                    break;
                case UNSAFE:
                    binding.loadingState.setVisibility(View.GONE);
                    binding.btnAnalyse.setEnabled(true);
                    binding.cardUnsafe.setVisibility(View.VISIBLE);
                    break;
            }
        });

        // Observe AI results
        viewModel.getAiSummary().observe(getViewLifecycleOwner(), summary -> {
            if (summary != null) binding.tvAiSummary.setText(summary);
        });

        viewModel.getSuggestedSteps().observe(getViewLifecycleOwner(), steps -> {
            if (steps != null && !steps.isEmpty()) {
                binding.tvSuggestedSteps.setText(steps);
            } else {
                binding.tvSuggestedSteps.setText("Review ticket details and apply standard resolution procedure.");
            }
        });

        // Analyse button
        binding.btnAnalyse.setOnClickListener(v -> {
            if (currentTicket != null) {
                viewModel.analyseTicket(currentTicket);
            }
        });

        // Approve button
        binding.btnApprove.setOnClickListener(v -> {
            if (currentTicket != null) {
                viewModel.approveAndSave(currentTicket);
                binding.btnResolve.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(),
                        "Notes saved successfully", Toast.LENGTH_SHORT).show();
            }
        });

        // Resolve button
        binding.btnResolve.setOnClickListener(v -> {
            if (currentTicket != null) {
                viewModel.resolveTicket(currentTicket);
                Toast.makeText(getContext(),
                        "Ticket marked as resolved", Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            }
        });
    }

    private void populateUI(Ticket ticket) {
        binding.tvTicketTitle.setText(ticket.title);
        binding.tvDescription.setText(ticket.description);
        binding.tvStatus.setText(ticket.status);
        binding.tvPriority.setText(ticket.priority);

        setRoundedBackground(binding.tvStatus, getStatusColor(ticket.status));
        setRoundedBackground(binding.tvPriority, getPriorityColor(ticket.priority));
    }

    private void setRoundedBackground(android.widget.TextView tv, String colorHex) {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}