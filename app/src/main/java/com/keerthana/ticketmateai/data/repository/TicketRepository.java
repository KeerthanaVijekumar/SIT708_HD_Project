package com.keerthana.ticketmateai.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.keerthana.ticketmateai.data.db.TicketDatabase;
import com.keerthana.ticketmateai.data.db.TicketDao;
import com.keerthana.ticketmateai.model.Ticket;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class TicketRepository {

    private final TicketDao ticketDao;
    private final ExecutorService executor;

    private final LiveData<List<Ticket>> allTickets;
    private final LiveData<List<Ticket>> openTickets;
    private final LiveData<List<Ticket>> resolvedTickets;
    private final LiveData<Integer> openTicketCount;

    public TicketRepository(Application application) {
        TicketDatabase db = TicketDatabase.getInstance(application);
        ticketDao = db.ticketDao();
        executor = TicketDatabase.getDatabaseExecutor();

        allTickets = ticketDao.getAllTickets();
        openTickets = ticketDao.getOpenTickets();
        resolvedTickets = ticketDao.getResolvedTickets();
        openTicketCount = ticketDao.getOpenTicketCount();
    }

    // --- Getters ---

    public LiveData<List<Ticket>> getAllTickets() {
        return allTickets;
    }

    public LiveData<List<Ticket>> getOpenTickets() {
        return openTickets;
    }

    public LiveData<List<Ticket>> getResolvedTickets() {
        return resolvedTickets;
    }

    public LiveData<Integer> getOpenTicketCount() {
        return openTicketCount;
    }

    public LiveData<Ticket> getTicketById(int id) {
        return ticketDao.getTicketById(id);
    }

    // --- Operations (run on background thread) ---

    public void insert(Ticket ticket) {
        executor.execute(() -> ticketDao.insert(ticket));
    }

    public void update(Ticket ticket) {
        executor.execute(() -> ticketDao.update(ticket));
    }

    public void delete(Ticket ticket) {
        executor.execute(() -> ticketDao.delete(ticket));
    }

    public void resolveTicket(Ticket ticket, String notes) {
        executor.execute(() -> {
            ticket.status = "Resolved";
            ticket.resolutionNotes = notes;
            ticket.resolvedAt = System.currentTimeMillis();
            ticketDao.update(ticket);
        });
    }
}