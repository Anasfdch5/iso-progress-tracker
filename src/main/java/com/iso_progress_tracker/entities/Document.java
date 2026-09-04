package com.iso_progress_tracker.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "DOCUMENTS")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String fileType;

    // Stored as bytes in database
    private long fileSize;

    @Column(nullable = false)
    private String status = "Uploaded";

    @Column(nullable = false)
    private String filePath;

    private LocalDate uploadDate;

    // Establishing Relationship: Many Documents belong to One Process
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id")
    private Process process;

    // --- Constructors ---
    public Document() {}

    // Automatically set uploadDate when created
    @PrePersist
    protected void onCreate() {
        if (this.uploadDate == null) {
            this.uploadDate = LocalDate.now();
        }
    }

    // Helper method to display formatted size in Thymeleaf (${doc.formattedFileSize})
    public String getFormattedFileSize() {
        if (fileSize < 1024) return fileSize + " B";
        if (fileSize < 1024 * 1024) return String.format("%.1f KB", fileSize / 1024.0);
        return String.format("%.2f MB", fileSize / (1024.0 * 1024.0));
    }

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public LocalDate getUploadDate() { return uploadDate; }
    public void setUploadDate(LocalDate uploadDate) { this.uploadDate = uploadDate; }

    public Process getProcess() { return process; }
    public void setProcess(Process process) { this.process = process; }
}