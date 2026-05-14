package com.keerthana.ticketmateai.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.keerthana.ticketmateai.model.Ticket;

import java.util.List;

@Dao
public interface TicketDao {

    @Insert
    void insert(Ticket ticket);

    @Update
    void update(Ticket ticket);

    @Delete
    void delete(Ticket ticket);

    @Query("SELECT * FROM tickets ORDER BY created_at DESC")
    LiveData<List<Ticket>> getAllTickets();

    @Query("SELECT * FROM tickets WHERE id = :id")
    LiveData<Ticket> getTicketById(int id);

    @Query("SELECT * FROM tickets WHERE status = 'Resolved' ORDER BY resolved_at DESC")
    LiveData<List<Ticket>> getResolvedTickets();

    @Query("SELECT * FROM tickets WHERE status != 'Resolved' ORDER BY created_at DESC")
    LiveData<List<Ticket>> getOpenTickets();

    @Query("SELECT COUNT(*) FROM tickets WHERE status != 'Resolved'")
    LiveData<Integer> getOpenTicketCount();
}