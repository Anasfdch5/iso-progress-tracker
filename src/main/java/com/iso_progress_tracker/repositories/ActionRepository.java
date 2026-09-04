package com.iso_progress_tracker.repositories;

import com.iso_progress_tracker.entities.Action;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ActionRepository extends JpaRepository<Action, Long> {
    // Custom query method to instantly find all actions matching a specific process ID
    List<Action> findByProcessId(Long processId);
}