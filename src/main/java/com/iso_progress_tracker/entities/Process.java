package com.iso_progress_tracker.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PROCESSES")
public class Process {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String responsiblePerson;//drop down for "responsibles"

    @Column(nullable = false)
    private String standard; // e.g., "ISO 9001" or "ISO 27001"

    @Column(nullable = false)
    private String status; // e.g., "En cours", "Terminé"

    @Column(nullable = false)
    private LocalDate targetDate;


    private double progress;// percentage progress tracker



    // --- Relationship with Actions (Cascade Delete Activation) ---
    @OneToMany(mappedBy = "process", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Action> actions = new ArrayList<>();



    @OneToMany(mappedBy = "process", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Document> documents = new ArrayList<>();



    @Transient // Calculated on the fly - not saved in the database
    public int getProgressPercentage() {
        if (actions == null || actions.isEmpty()) {
            return (int) Math.round(this.progress); // 0% if no actions exist yet
        }

        double totalPoints = actions.stream()
                .mapToDouble(action -> {
                    if ("TERMINÉ".equalsIgnoreCase(action.getStatus())) return 100.0;
                    if ("EN COURS".equalsIgnoreCase(action.getStatus())) return 50.0;
                    return 0.0; // "À FAIRE" or null
                })
                .sum();

        return (int) Math.round(totalPoints / actions.size());
    }

    // --- Constructors ---
    public Process() {}

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getResponsiblePerson() { return responsiblePerson; }
    public void setResponsiblePerson(String responsiblePerson) { this.responsiblePerson = responsiblePerson; }

    public String getStandard() { return standard; }
    public void setStandard(String standard) { this.standard = standard; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getTargetDate() { return targetDate; }
    public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }

    public double getProgress() { return progress; }
    public void setProgress(double progress) { this.progress = progress; }

    public List<Action> getActions() { return actions; }
    public void setActions(List<Action> actions) { this.actions = actions; }


    public List<Document> getDocuments() { return documents; }
    public void setDocuments(List<Document> documents) { this.documents = documents; }


}