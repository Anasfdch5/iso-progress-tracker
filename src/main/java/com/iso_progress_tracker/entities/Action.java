package com.iso_progress_tracker.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "ACTIONS")
public class Action {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String responsiblePerson;

    private LocalDate deadline;

    @Column(nullable = false)
    private String status; // e.g., "En cours", "Terminé", "En retard"

    private double progress; // percentage progress for this specific action

    @Column(columnDefinition = "TEXT")
    private String comment;

    // Establishing the Relationship: Many Actions belong to One Process
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id", nullable = false)
    private Process process;

    // --- Constructors ---
    public Action() {}

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getResponsiblePerson() { return responsiblePerson; }
    public void setResponsiblePerson(String responsiblePerson) { this.responsiblePerson = responsiblePerson; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getProgress() { return progress; }
    public void setProgress(double progress) { this.progress = progress; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Process getProcess() { return process; }
    public void setProcess(Process process) { this.process = process; }
}