package com.keerthana.ticketmateai.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tickets")
public class Ticket {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "status")
    public String status; // "Open", "In Progress", "Resolved"

    @ColumnInfo(name = "priority")
    public String priority; // "Low", "Medium", "High"

    @ColumnInfo(name = "ai_summary")
    public String aiSummary;

    @ColumnInfo(name = "suggested_steps")
    public String suggestedSteps;

    @ColumnInfo(name = "resolution_notes")
    public String resolutionNotes;

    @ColumnInfo(name = "generated_script")
    public String generatedScript;

    @ColumnInfo(name = "created_at")
    public long createdAt;

    @ColumnInfo(name = "resolved_at")
    public long resolvedAt;

    // Constructor
    public Ticket() {
        this.createdAt = System.currentTimeMillis();
        this.status = "Open";
        this.priority = "Medium";
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getAiSummary() { return aiSummary; }
    public void setAiSummary(String aiSummary) { this.aiSummary = aiSummary; }

    public String getSuggestedSteps() { return suggestedSteps; }
    public void setSuggestedSteps(String suggestedSteps) { this.suggestedSteps = suggestedSteps; }

    public String getResolutionNotes() { return resolutionNotes; }
    public void setResolutionNotes(String resolutionNotes) { this.resolutionNotes = resolutionNotes; }

    public String getGeneratedScript() { return generatedScript; }
    public void setGeneratedScript(String generatedScript) { this.generatedScript = generatedScript; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public long getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(long resolvedAt) { this.resolvedAt = resolvedAt; }
}