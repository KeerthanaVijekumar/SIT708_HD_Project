package com.keerthana.ticketmateai.ai;

import android.content.Context;
import android.util.Log;

import com.google.mediapipe.tasks.genai.llminference.LlmInference;

public class LlamaHelper {

    private static final String TAG = "LlamaHelper";
    private static final String MODEL_PATH =
            "/data/local/tmp/llm/gemma3.task";

    private LlmInference llmInference;
    private boolean isInitialized = false;

    public LlamaHelper(Context context) {
        try {
            LlmInference.LlmInferenceOptions options =
                    LlmInference.LlmInferenceOptions.builder()
                            .setModelPath(MODEL_PATH)
                            .setMaxTokens(1024)
                            .setPreferredBackend(LlmInference.Backend.CPU)
                            .build();

            llmInference = LlmInference.createFromOptions(context, options);
            isInitialized = true;
            Log.d(TAG, "LlamaHelper initialized successfully with Gemma-3");

        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize: " + e.getMessage());
            isInitialized = false;
        }
    }

    // --- Summary Generation ---
    public String generateSummary(String ticketTitle, String description) {
        if (!isInitialized) {
            return getFallbackSummary(ticketTitle);
        }
        try {
            String prompt = buildSummaryPrompt(ticketTitle, description);
            String result = llmInference.generateResponse(prompt);
            return result != null ? result.trim() : getFallbackSummary(ticketTitle);
        } catch (Exception e) {
            Log.e(TAG, "Summary generation failed: " + e.getMessage());
            return getFallbackSummary(ticketTitle);
        }
    }

    // --- Script Generation ---
    public String generateScript(String taskDescription) {
        if (!isInitialized) {
            return getFallbackScript(taskDescription);
        }
        try {
            String prompt = buildScriptPrompt(taskDescription);
            String result = llmInference.generateResponse(prompt);
            return result != null ? result.trim() : getFallbackScript(taskDescription);
        } catch (Exception e) {
            Log.e(TAG, "Script generation failed: " + e.getMessage());
            return getFallbackScript(taskDescription);
        }
    }

    // --- Safety Check ---
    public boolean isSafeRequest(String input) {
        if (input == null) return false;
        String lower = input.toLowerCase();
        return !lower.contains("delete all") &&
                !lower.contains("format drive") &&
                !lower.contains("format c:") &&
                !lower.contains("rm -rf") &&
                !lower.contains("drop database") &&
                !lower.contains("drop table") &&
                !lower.contains("shutdown /s") &&
                !lower.contains("remove-item -recurse");
    }

    // --- Gemma-3 Prompt Format ---
    private String buildSummaryPrompt(String title, String description) {
        return "<start_of_turn>user\n" +
                "You are an IT service desk assistant. Be concise and professional.\n\n" +
                "Analyse this support ticket and provide:\n" +
                "1. A 2-sentence summary of the issue\n" +
                "2. Three numbered troubleshooting steps\n\n" +
                "Ticket: " + title + "\n" +
                "Description: " + description + "\n" +
                "<end_of_turn>\n" +
                "<start_of_turn>model\n";
    }

    private String buildScriptPrompt(String taskDescription) {
        return "<start_of_turn>user\n" +
                "You are an IT automation expert. Generate a safe PowerShell script.\n" +
                "If the task is destructive or unclear respond with: UNSAFE_REQUEST\n\n" +
                "Task: " + taskDescription + "\n" +
                "Add comments for each step. Keep it safe and reversible.\n" +
                "<end_of_turn>\n" +
                "<start_of_turn>model\n";
    }

    // --- Fallback responses ---
    private String getFallbackSummary(String title) {
        return "Summary: This ticket relates to \"" + title + "\".\n\n" +
                "1. Review the ticket description carefully\n" +
                "2. Check recent changes or updates on the affected system\n" +
                "3. Escalate to senior analyst if issue persists\n\n" +
                "Note: AI model not loaded. Install Gemma-3 model for full AI analysis.";
    }

    private String getFallbackScript(String task) {
        return "# PowerShell Script\n" +
                "# Task: " + task + "\n" +
                "# Note: AI model not loaded. Install Gemma-3 for script generation.\n\n" +
                "Write-Host 'Please install the Gemma-3 model to enable script generation.'";
    }

    // --- Cleanup ---
    public void close() {
        if (llmInference != null) {
            llmInference.close();
        }
    }
}