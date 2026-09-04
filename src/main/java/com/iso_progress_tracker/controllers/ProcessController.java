package com.iso_progress_tracker.controllers;

import com.iso_progress_tracker.entities.Action;
import com.iso_progress_tracker.entities.Document;
import com.iso_progress_tracker.entities.Process;
import com.iso_progress_tracker.services.ActionService;
import com.iso_progress_tracker.services.DocumentService;
import com.iso_progress_tracker.services.ProcessService;
import com.iso_progress_tracker.services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@Controller
@RequestMapping("/processes")
public class ProcessController {

    private final ProcessService processService;
    private final ActionService actionService;
    private final DocumentService documentService;
    private final UserService userService;

    public ProcessController(ProcessService processService,
                             ActionService actionService,
                             DocumentService documentService,
                             UserService userService) {
        this.processService = processService;
        this.actionService = actionService;
        this.documentService = documentService;
        this.userService = userService;
    }

    /**
     * Dashboard List View: Retrieves, filters, sorts, and displays ISO processes
     */
    @GetMapping
    public String listProcesses(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "standard", required = false) String standard,
            @RequestParam(name = "sortField", required = false, defaultValue = "id") String sortField,
            @RequestParam(name = "sortDir", required = false, defaultValue = "asc") String sortDir,
            Model model) {

        List<Process> processes = processService.searchAndFilterProcesses(keyword, standard);

        boolean isAsc = "asc".equalsIgnoreCase(sortDir);
        java.util.Comparator<Process> comparator;

        switch (sortField.toLowerCase()) {
            case "name":
                comparator = java.util.Comparator.comparing(
                        p -> p.getName() != null ? p.getName().toLowerCase() : "",
                        java.util.Comparator.naturalOrder()
                );
                break;
            case "responsibleperson":
                comparator = java.util.Comparator.comparing(
                        p -> p.getResponsiblePerson() != null ? p.getResponsiblePerson().toLowerCase() : "",
                        java.util.Comparator.naturalOrder()
                );
                break;
            case "targetdate":
                comparator = java.util.Comparator.comparing(
                        p -> p.getTargetDate() != null ? p.getTargetDate().toString() : "",
                        java.util.Comparator.naturalOrder()
                );
                break;
            case "progress":
                comparator = java.util.Comparator.comparingDouble(Process::getProgressPercentage);
                break;
            case "id":
            default:
                comparator = java.util.Comparator.comparing(Process::getId, java.util.Comparator.nullsFirst(Long::compareTo));
                break;
        }

        if (!isAsc) {
            comparator = comparator.reversed();
        }

        processes = processes.stream().sorted(comparator).collect(java.util.stream.Collectors.toList());

        model.addAttribute("processes", processes);
        model.addAttribute("keyword", keyword);
        model.addAttribute("standard", standard);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", isAsc ? "desc" : "asc");

        return "processes/list";
    }

    /**
     * Displays creation form for adding a new process
     */
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("process", new Process());
        model.addAttribute("users", userService.getActiveUsers());
        return "processes/add";
    }

    /**
     * Handles POST submission to persist a new ISO Process
     */
    @PostMapping("/save")
    public String saveProcess(@ModelAttribute("process") Process process,
                              RedirectAttributes redirectAttributes,
                              Model model) {

        if (processService.processExistsByName(process.getName())) {
            model.addAttribute("error", "A process with the name '" + process.getName() + "' already exists.");
            model.addAttribute("users", userService.getActiveUsers());
            return "processes/add";
        }

        if (process.getProgress() >= 100) {
            process.setStatus("Terminé");
        } else {
            process.setStatus("En cours");
        }

        processService.saveProcess(process);
        return "redirect:/processes";
    }

    /**
     * Removes an entire ISO process record by ID
     */
    @GetMapping("/delete/{id}")
    public String deleteProcess(@PathVariable("id") Long id) {
        processService.deleteProcess(id);
        return "redirect:/processes";
    }

    /**
     * Process Detail Profile View
     */
    @GetMapping("/{id}")
    public String showProcessDetails(@PathVariable("id") Long id, Model model) {
        Process process = processService.getProcessById(id);
        model.addAttribute("process", process);

        model.addAttribute("actions", actionService.getActionsByProcess(id));
        Action blankAction = new Action();
        blankAction.setProcess(process);
        model.addAttribute("newAction", blankAction);

        model.addAttribute("documents", documentService.getDocumentsByProcess(id));
        Document blankDocument = new Document();
        blankDocument.setProcess(process);
        model.addAttribute("newDocument", blankDocument);
        model.addAttribute("users", userService.getActiveUsers());

        return "processes/details";
    }

    /**
     * Displays edit form for modifying an existing ISO Process
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        Process process = processService.getProcessById(id);
        model.addAttribute("process", process);
        model.addAttribute("users", userService.getActiveUsers());
        return "processes/edit";
    }

    /**
     * Handles POST submission to save edits to an existing ISO Process.
     * Merges progress changes with the existing database record.
     */
    @PostMapping("/update")
    public String updateProcess(@ModelAttribute("process") Process formProcess, RedirectAttributes redirectAttributes) {
        Process existingProcess = processService.getProcessById(formProcess.getId());

        if (existingProcess != null) {
            existingProcess.setName(formProcess.getName());
            existingProcess.setStandard(formProcess.getStandard());
            existingProcess.setResponsiblePerson(formProcess.getResponsiblePerson());

            // Explicitly update progress
            existingProcess.setProgress(formProcess.getProgress());

            if (formProcess.getProgress() >= 100) {
                existingProcess.setStatus("Terminé");
            } else {
                existingProcess.setStatus("En cours");
            }

            processService.saveProcess(existingProcess);
            redirectAttributes.addFlashAttribute("success", "Process progress updated successfully!");
        }

        return "redirect:/processes";
    }

    /**
     * Statistics Overview Page
     */
    @GetMapping("/stats")
    public String showStatistics(Model model) {
        List<Process> allProcesses = processService.getAllProcesses();
        List<Document> allDocuments = documentService.getAllDocuments();

        // Executive KPIs
        model.addAttribute("totalProcesses", processService.getTotalProcessesCount());
        model.addAttribute("pendingActions", processService.getPendingActionsCount());
        model.addAttribute("totalDocuments", allDocuments != null ? allDocuments.size() : 0);

        // Standard Compliance Averages
        double iso9001Avg = allProcesses.stream()
                .filter(p -> p.getStandard() != null && "ISO 9001".equalsIgnoreCase(p.getStandard().trim()))
                .mapToDouble(Process::getProgressPercentage)
                .average().orElse(0.0);

        double iso27001Avg = allProcesses.stream()
                .filter(p -> p.getStandard() != null && "ISO 27001".equalsIgnoreCase(p.getStandard().trim()))
                .mapToDouble(Process::getProgressPercentage)
                .average().orElse(0.0);

        model.addAttribute("iso9001Avg", Math.round(iso9001Avg));
        model.addAttribute("iso27001Avg", Math.round(iso27001Avg));

        // Status Breakdown Counts (For Doughnut Chart)
        long countCompleted = allProcesses.stream().filter(p -> p.getProgressPercentage() == 100).count();
        long countUnstarted = allProcesses.stream().filter(p -> p.getProgressPercentage() == 0).count();
        long countInProgress = allProcesses.size() - (countCompleted + countUnstarted);

        model.addAttribute("countCompleted", countCompleted);
        model.addAttribute("countInProgress", countInProgress);
        model.addAttribute("countUnstarted", countUnstarted);

        // Standard Distribution Counts (For Pie Chart)
        long countIso9001 = allProcesses.stream()
                .filter(p -> p.getStandard() != null && "ISO 9001".equalsIgnoreCase(p.getStandard().trim())).count();
        long countIso27001 = allProcesses.stream()
                .filter(p -> p.getStandard() != null && "ISO 27001".equalsIgnoreCase(p.getStandard().trim())).count();
        long countOtherIso = allProcesses.size() - (countIso9001 + countIso27001);

        model.addAttribute("countIso9001", countIso9001);
        model.addAttribute("countIso27001", countIso27001);
        model.addAttribute("countOtherIso", countOtherIso);

        // Responsible Person Ranking
        java.util.Map<String, Long> responsibleRankings = allProcesses.stream()
                .filter(p -> p.getResponsiblePerson() != null && !p.getResponsiblePerson().isBlank())
                .collect(java.util.stream.Collectors.groupingBy(Process::getResponsiblePerson, java.util.stream.Collectors.counting()))
                .entrySet().stream()
                .sorted(java.util.Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(java.util.stream.Collectors.toMap(
                        java.util.Map.Entry::getKey,
                        java.util.Map.Entry::getValue,
                        (e1, e2) -> e1,
                        java.util.LinkedHashMap::new
                ));
        model.addAttribute("responsibleRankings", responsibleRankings);

        // Ranked Processes & Filtered Lists
        List<Process> rankedProcesses = allProcesses.stream()
                .sorted(java.util.Comparator.comparingDouble(Process::getProgressPercentage).reversed())
                .collect(java.util.stream.Collectors.toList());
        model.addAttribute("rankedProcesses", rankedProcesses);

        model.addAttribute("completedProcesses", allProcesses.stream().filter(p -> p.getProgressPercentage() == 100).toList());
        model.addAttribute("unstartedProcesses", allProcesses.stream().filter(p -> p.getProgressPercentage() == 0).toList());

        return "processes/stats";
    }

    /**
     * Export all ISO process metrics into a downloadable CSV report
     */
    @GetMapping("/export")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=iso_processes_report.csv");

        PrintWriter writer = response.getWriter();
        writer.println("ID,Process Name,ISO Standard,Responsible Person,Target Date,Progress (%)");

        List<Process> processes = processService.getAllProcesses();
        for (Process p : processes) {
            writer.println(String.format("%d,\"%s\",\"%s\",\"%s\",%s,%.0f",
                    p.getId(),
                    p.getName() != null ? p.getName().replace("\"", "\"\"") : "",
                    p.getStandard(),
                    p.getResponsiblePerson(),
                    p.getTargetDate(),
                    p.getProgressPercentage()
            ));
        }
    }

    @PostMapping("/documents/save")
    public String saveDocument(@ModelAttribute("newDocument") Document document,
                               @RequestParam("file") MultipartFile file,
                               RedirectAttributes redirectAttributes) {

        if (documentService.documentExistsByTitle(document.getTitle())) {
            redirectAttributes.addFlashAttribute("errorMessage", "A document with the name '" + document.getTitle() + "' already exists.");
            if (document.getProcess() != null && document.getProcess().getId() != null) {
                return "redirect:/processes/" + document.getProcess().getId();
            }
            return "redirect:/processes/documents";
        }

        try {
            documentService.saveDocumentWithFile(document, file);
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/processes/" + document.getProcess().getId() + "?error=upload_failed";
        }
        return "redirect:/processes/" + document.getProcess().getId();
    }

    @GetMapping("/documents")
    public String showDocumentsPage(Model model) {
        model.addAttribute("documents", documentService.getAllDocuments());

        Document newDoc = new Document();
        newDoc.setProcess(new Process());
        model.addAttribute("newDocument", newDoc);

        return "processes/documents";
    }
}