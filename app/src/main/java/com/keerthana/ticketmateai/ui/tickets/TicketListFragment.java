package com.keerthana.ticketmateai.ui.tickets;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.keerthana.ticketmateai.R;
import com.keerthana.ticketmateai.databinding.FragmentTicketListBinding;
import com.keerthana.ticketmateai.model.Ticket;

public class TicketListFragment extends Fragment {

    private FragmentTicketListBinding binding;
    private TicketListViewModel viewModel;
    private TicketAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTicketListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setup ViewModel
        viewModel = new ViewModelProvider(this).get(TicketListViewModel.class);

        // Setup RecyclerView
        adapter = new TicketAdapter(ticket -> {
            // Navigate to detail screen passing ticket ID
            Bundle args = new Bundle();
            args.putInt("ticketId", ticket.getId());
            Navigation.findNavController(view)
                    .navigate(R.id.ticketDetailFragment, args);
        });

        binding.recyclerViewTickets.setLayoutManager(
                new LinearLayoutManager(getContext()));
        binding.recyclerViewTickets.setAdapter(adapter);

        // Observe open tickets
        viewModel.getOpenTickets().observe(getViewLifecycleOwner(), tickets -> {
            if (tickets == null || tickets.isEmpty()) {
                binding.recyclerViewTickets.setVisibility(View.GONE);
                binding.emptyState.setVisibility(View.VISIBLE);
            } else {
                binding.recyclerViewTickets.setVisibility(View.VISIBLE);
                binding.emptyState.setVisibility(View.GONE);
                adapter.setTickets(tickets);
            }
        });

        // Observe open ticket count for header
        viewModel.getOpenTicketCount().observe(getViewLifecycleOwner(), count -> {
            if (count != null) {
                binding.tvOpenCount.setText(count + " open ticket" +
                        (count == 1 ? "" : "s") + " need attention");
            }
        });

        // FAB — open add ticket bottom sheet
        binding.fab.setOnClickListener(v -> showAddTicketSheet());
    }

    private void showAddTicketSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View sheetView = LayoutInflater.from(getContext())
                .inflate(R.layout.bottom_sheet_add_ticket, null);
        dialog.setContentView(sheetView);

        TextInputEditText etTitle = sheetView.findViewById(R.id.etTitle);
        TextInputEditText etDescription = sheetView.findViewById(R.id.etDescription);
        TextInputEditText etPriority = sheetView.findViewById(R.id.etPriority);
        MaterialButton btnSubmit = sheetView.findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(v -> {
            String title = etTitle.getText() != null ?
                    etTitle.getText().toString().trim() : "";
            String description = etDescription.getText() != null ?
                    etDescription.getText().toString().trim() : "";
            String priority = etPriority.getText() != null ?
                    etPriority.getText().toString().trim() : "Medium";

            if (!title.isEmpty()) {
                Ticket ticket = new Ticket();
                ticket.title = title;
                ticket.description = description;
                ticket.priority = priority.isEmpty() ? "Medium" : priority;
                viewModel.insert(ticket);
                dialog.dismiss();
            } else {
                etTitle.setError("Title is required");
            }
        });

        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}