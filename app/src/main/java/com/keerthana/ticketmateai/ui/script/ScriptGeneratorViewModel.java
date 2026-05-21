package com.keerthana.ticketmateai.ui.script;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.keerthana.ticketmateai.ai.LlamaHelper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScriptGeneratorViewModel extends AndroidViewModel {

    public enum ScriptState {
        IDLE, LOADING, DONE, ERROR, UNSAFE
    }

    private final MutableLiveData<ScriptState> scriptState =
            new MutableLiveData<>(ScriptState.IDLE);
    private final MutableLiveData<String> generatedScript =
            new MutableLiveData<>();
    private final ExecutorService executor =
            Executors.newSingleThreadExecutor();
    private final Handler mainHandler =
            new Handler(Looper.getMainLooper());

    public ScriptGeneratorViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<ScriptState> getScriptState() {
        return scriptState;
    }

    public LiveData<String> getGeneratedScript() {
        return generatedScript;
    }

    public void generateScript(String taskDescription) {
        scriptState.setValue(ScriptState.LOADING);

        executor.execute(() -> {
            try {
                LlamaHelper helper = new LlamaHelper(getApplication());

                // Safety check first
                if (!helper.isSafeRequest(taskDescription)) {
                    mainHandler.post(() ->
                            scriptState.setValue(ScriptState.UNSAFE));
                    return;
                }

                String result = helper.generateScript(taskDescription);

                // Check if model returned unsafe flag
                if (result.contains("UNSAFE_REQUEST")) {
                    mainHandler.post(() ->
                            scriptState.setValue(ScriptState.UNSAFE));
                    return;
                }

                mainHandler.post(() -> {
                    generatedScript.setValue(result);
                    scriptState.setValue(ScriptState.DONE);
                });

            } catch (Exception e) {
                mainHandler.post(() ->
                        scriptState.setValue(ScriptState.ERROR));
            }
        });
    }
}