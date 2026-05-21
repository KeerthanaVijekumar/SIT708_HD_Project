package com.keerthana.ticketmateai.ui.detail;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.keerthana.ticketmateai.ai.LlamaHelper;
import com.keerthana.ticketmateai.data.repository.TicketRepository;
import com.keerthana.ticketmateai.model.Ticket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TicketDetailViewModel extends AndroidViewModel {

    public enum AnalysisState {
        IDLE, LOADING, DONE, ERROR, UNSAFE
    }

    private final TicketRepository repository;
    private final MutableLiveData<AnalysisState> analysisState =
            new MutableLiveData<>(AnalysisState.IDLE);
    private final MutableLiveData<String> aiSummary = new MutableLiveData<>();
    private final MutableLiveData<String> suggestedSteps = new MutableLiveData<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public TicketDetailViewModel(@NonNull Application application) {
        super(application);
        repository = new TicketRepository(application);
    }

    public LiveData<Ticket> getTicketById(int id) {
        return repository.getTicketById(id);
    }

    public LiveData<AnalysisState> getAnalysisState() {
        return analysisState;
    }

    public LiveData<String> getAiSummary() {
        return aiSummary;
    }

    public LiveData<String> getSuggestedSteps() {
        return suggestedSteps;
    }

    public void analyseTicket(Ticket ticket) {
        analysisState.setValue(AnalysisState.LOADING);

        executor.execute(() -> {
            try {
                LlamaHelper helper = new LlamaHelper(getApplication());

                // Safety check first
                if (!helper.isSafeRequest(ticket.title + " " + ticket.description)) {
                    mainHandler.post(() ->
                            analysisState.setValue(AnalysisState.UNSAFE));
                    return;
                }

                String result = helper.generateSummary(
                        ticket.title,
                        ticket.description
                );

                // Split summary and steps
                String summary = result;
                String steps = "";
                if (result.contains("1.") || result.contains("Step 1")) {
                    int splitIndex = result.indexOf("1.");
                    if (splitIndex == -1) splitIndex = result.indexOf("Step 1");
                    if (splitIndex > 0) {
                        summary = result.substring(0, splitIndex).trim();
                        steps = result.substring(splitIndex).trim();
                    }
                }

                final String finalSummary = summary;
                final String finalSteps = steps;

                mainHandler.post(() -> {
                    aiSummary.setValue(finalSummary);
                    suggestedSteps.setValue(finalSteps);
                    analysisState.setValue(AnalysisState.DONE);
                });

            } catch (Exception e) {
                mainHandler.post(() ->
                        analysisState.setValue(AnalysisState.ERROR));
            }
        });
    }

    public void approveAndSave(Ticket ticket) {
        String notes = aiSummary.getValue() + "\n\nSteps:\n" + suggestedSteps.getValue();
        ticket.resolutionNotes = notes;
        repository.update(ticket);
    }

    public void resolveTicket(Ticket ticket) {
        repository.resolveTicket(ticket, ticket.resolutionNotes);
    }

    public void update(Ticket ticket) {
        repository.update(ticket);
    }
}