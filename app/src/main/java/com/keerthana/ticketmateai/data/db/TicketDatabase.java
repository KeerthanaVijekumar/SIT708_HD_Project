package com.keerthana.ticketmateai.data.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.keerthana.ticketmateai.model.Ticket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Ticket.class}, version = 1, exportSchema = false)
public abstract class TicketDatabase extends RoomDatabase {

    private static volatile TicketDatabase INSTANCE;
    private static final ExecutorService databaseExecutor =
            Executors.newSingleThreadExecutor();

    public abstract TicketDao ticketDao();

    public static TicketDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (TicketDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    TicketDatabase.class,
                                    "ticket_database"
                            )
                            .addCallback(seedCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public static ExecutorService getDatabaseExecutor() {
        return databaseExecutor;
    }

    // Seed database with sample tickets on first launch
    private static final RoomDatabase.Callback seedCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseExecutor.execute(() -> {
                TicketDao dao = INSTANCE.ticketDao();

                Ticket t1 = new Ticket();
                t1.title = "Outlook not opening";
                t1.description = "User reports Outlook crashes on launch. Profile corruption suspected after recent Windows update on Dell Latitude 5420.";
                t1.priority = "High";
                dao.insert(t1);

                Ticket t2 = new Ticket();
                t2.title = "Disk cleanup required";
                t2.description = "C: drive at 95% capacity on workstation WS-042. User unable to save files. Temp files and old logs need clearing.";
                t2.priority = "High";
                dao.insert(t2);

                Ticket t3 = new Ticket();
                t3.title = "New user Entra ID enrollment";
                t3.description = "New analyst John Smith joining Monday. Needs Entra ID account, MFA setup, VPN access and mailbox provisioning.";
                t3.priority = "Medium";
                dao.insert(t3);

                Ticket t4 = new Ticket();
                t4.title = "VPN dropping intermittently";
                t4.description = "Three users on Floor 2 reporting VPN disconnects every 30-40 minutes. Cisco AnyConnect v4.10. Issue started after network maintenance.";
                t4.priority = "Medium";
                dao.insert(t4);

                Ticket t5 = new Ticket();
                t5.title = "Printer offline — Floor 3";
                t5.description = "Canon MF445dw on Floor 3 showing offline to all users. Print spooler may need restart. Last working yesterday at 3PM.";
                t5.priority = "Low";
                dao.insert(t5);
            });
        }
    };
}