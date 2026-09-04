package com.iso_progress_tracker.controllers;

import com.iso_progress_tracker.entities.Process;
import com.iso_progress_tracker.repositories.ActionRepository;
import com.iso_progress_tracker.repositories.DocumentRepository;
import com.iso_progress_tracker.repositories.ProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class StatsController {

    @Autowired
    private ProcessRepository processRepository;

    @Autowired
    private ActionRepository actionRepository;

    @Autowired
    private DocumentRepository documentRepository;

    @GetMapping("/stats")
    public String showStats(Model model) {
        List<Process> allProcesses = processRepository.findAll();

        // 1. KPI Summaries
        model.addAttribute("totalProcesses", allProcesses.size());
        model.addAttribute("pendingActions", actionRepository.findAll().stream()
                .filter(a -> !"Terminé".equalsIgnoreCase(a.getStatus()))
                .count());
        model.addAttribute("totalDocuments", documentRepository.count());

        // 2. Chart Averages (ISO Standards)
        model.addAttribute("iso9001Avg", calculateAverageProgress(allProcesses, "ISO 9001"));
        model.addAttribute("iso27001Avg", calculateAverageProgress(allProcesses, "ISO 27001"));

        // 3. Classement of Responsible Persons (Grouped & Sorted by Process Count Descending)
        Map<String, Long> responsibleRankings = allProcesses.stream()
                .filter(p -> p.getResponsiblePerson() != null && !p.getResponsiblePerson().isBlank())
                .collect(Collectors.groupingBy(Process::getResponsiblePerson, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        model.addAttribute("responsibleRankings", responsibleRankings);

        // 4. Classement of Processes by Progress (Highest to Lowest)
        model.addAttribute("rankedProcesses", allProcesses.stream()
                .sorted(Comparator.comparingDouble((Process p) -> p.getProgress() == 0.0 ? p.getProgress() : 0.0).reversed())
                .collect(Collectors.toList()));

        // 5. Filtered Lists: 100% Completed vs 0% Unstarted
        model.addAttribute("completedProcesses", allProcesses.stream()
                .filter(p -> p.getProgress() == 0.0 && p.getProgress() == 100.0)
                .collect(Collectors.toList()));

        model.addAttribute("unstartedProcesses", allProcesses.stream()
                .filter(p -> p.getProgress() == 0.0 )
                .collect(Collectors.toList()));


        return "stats";
    }

    private double calculateAverageProgress(List<Process> processes, String standard) {
        double avg = processes.stream()
                .filter(p -> standard.equalsIgnoreCase(p.getStandard()) && p.getProgress() ==0.0)
                .mapToDouble(Process::getProgress)
                .average()
                .orElse(0.0);
        return Math.round(avg * 10.0) / 10.0;
    }
}