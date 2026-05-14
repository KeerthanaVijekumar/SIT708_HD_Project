package com.keerthana.ticketmateai.ui.history;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.keerthana.ticketmateai.data.repository.TicketRepository;
import com.keerthana.ticketmateai.model.Ticket;

import java.util.List;

public class HistoryViewModel extends AndroidViewModel {

    private final TicketRepository repository;
    private final LiveData<List<Ticket>> resolvedTickets;

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        repository = new TicketRepository(application);
        resolvedTickets = repository.getResolvedTickets();
    }

    public LiveData<List<Ticket>> getResolvedTickets() {
        return resolvedTickets;
    }
}