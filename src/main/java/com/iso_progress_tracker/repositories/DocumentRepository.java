package com.iso_progress_tracker.repositories;

import com.iso_progress_tracker.entities.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    // Custom query method to find all documents matching a specific process ID
    List<Document> findByProcessId(Long processId);
    boolean existsByTitleIgnoreCase(String title);
}