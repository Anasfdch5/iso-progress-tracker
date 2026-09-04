package com.iso_progress_tracker.repositories;

import com.iso_progress_tracker.entities.Process;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessRepository extends JpaRepository<Process, Long> {
    // Standard CRUD operations are inherited automatically!
    boolean existsByNameIgnoreCase(String name);
}