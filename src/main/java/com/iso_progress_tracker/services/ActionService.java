package com.iso_progress_tracker.services;

import com.iso_progress_tracker.entities.Action;
import com.iso_progress_tracker.repositories.ActionRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ActionService {

    private final ActionRepository actionRepository;

    // Constructor injection for dependency management
    public ActionService(ActionRepository actionRepository) {
        this.actionRepository = actionRepository;
    }

    // Get all actions across the entire application
    public List<Action> getAllActions() {
        return actionRepository.findAll();
    }

    // Crucial: Get only the actions that belong to a specific ISO Process
    public List<Action> getActionsByProcess(Long processId) {
        return actionRepository.findByProcessId(processId);
    }

    // Find a specific action task by its own ID
    public Optional<Action> getActionById(Long id) {
        return actionRepository.findById(id);
    }

    // Save or update an action task
    public Action saveAction(Action action) {
        return actionRepository.save(action);
    }

    // Delete an action task completely
    public void deleteAction(Long id) {
        actionRepository.deleteById(id);
    }
}