package com.iso_progress_tracker.services;

import com.iso_progress_tracker.entities.Process;
import com.iso_progress_tracker.repositories.ProcessRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProcessService {

    private final ProcessRepository processRepository;

    public ProcessService(ProcessRepository processRepository) {
        this.processRepository = processRepository;
    }

    // Retrieve all ISO processes to display on the dashboard list
    public List<Process> getAllProcesses() {
        return processRepository.findAll();
    }

    // Find a single process by its ID
    public Process getProcessById(Long id) {
        return processRepository.findById(id).orElse(null);
    }

    // Save or update a process
    public Process saveProcess(Process process) {
        return processRepository.save(process);
    }

    // Delete a process by its ID
    public void deleteProcess(Long id) {
        processRepository.deleteById(id);
    }
    // Inside com.iso_progress_tracker.services.ProcessService

    public long getTotalProcessesCount() {
        return processRepository.count();
    }


    public long getPendingActionsCount() {
        List<Process> processes = processRepository.findAll();
        return processes.stream()
                .flatMap(p -> p.getActions().stream())
                .filter(action -> !"TERMINÉ".equalsIgnoreCase(action.getStatus()))
                .count();
    }
    public List<Process> searchAndFilterProcesses(String keyword, String standard) {
        List<Process> all = processRepository.findAll();

        return all.stream()
                // 1. Filter by Keyword (Name or Responsible Person)
                .filter(p -> {
                    if (keyword == null || keyword.trim().isEmpty()) return true;
                    String k = keyword.toLowerCase();
                    boolean matchesName = p.getName() != null && p.getName().toLowerCase().contains(k);
                    boolean matchesPerson = p.getResponsiblePerson() != null && p.getResponsiblePerson().toLowerCase().contains(k);
                    return matchesName || matchesPerson;
                })
                // 2. Filter by ISO Standard
                .filter(p -> {
                    if (standard == null || standard.trim().isEmpty()) return true;
                    return p.getStandard() != null && p.getStandard().equalsIgnoreCase(standard);
                })
                .toList();
    }
    public boolean processExistsByName(String name) {
        if (name == null || name.isBlank()) return false;
        return processRepository.existsByNameIgnoreCase(name.trim());
    }

}