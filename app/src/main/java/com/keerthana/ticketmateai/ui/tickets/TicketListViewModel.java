package com.keerthana.ticketmateai.ui.tickets;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.keerthana.ticketmateai.data.repository.TicketRepository;
import com.keerthana.ticketmateai.model.Ticket;

import java.util.List;

public class TicketListViewModel extends AndroidViewModel {

    private final TicketRepository repository;
    private final LiveData<List<Ticket>> openTickets;
    private final LiveData<Integer> openTicketCount;

    public TicketListViewModel(@NonNull Application application) {
        super(application);
        repository = new TicketRepository(application);
        openTickets = repository.getOpenTickets();
        openTicketCount = repository.getOpenTicketCount();
    }

    public LiveData<List<Ticket>> getOpenTickets() {
        return openTickets;
    }

    public LiveData<Integer> getOpenTicketCount() {
        return openTicketCount;
    }

    public void insert(Ticket ticket) {
        repository.insert(ticket);
    }

    public void delete(Ticket ticket) {
        repository.delete(ticket);
    }
}