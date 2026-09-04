package com.iso_progress_tracker.controllers;

import com.iso_progress_tracker.entities.Document;
import com.iso_progress_tracker.services.DocumentService;
import com.iso_progress_tracker.services.ProcessService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentService documentService;
    private final ProcessService processService;

    public DocumentController(DocumentService documentService, ProcessService processService) {
        this.documentService = documentService;
        this.processService = processService;
    }

    @GetMapping
    public String showDocumentsPage(
            @RequestParam(name = "standard", required = false) String standard,
            @RequestParam(name = "processId", required = false) Long processId,
            @RequestParam(name = "sortField", required = false, defaultValue = "id") String sortField,
            @RequestParam(name = "sortDir", required = false, defaultValue = "asc") String sortDir,
            Model model) {

        List<Document> allDocuments = documentService.getAllDocuments();

        // 1. Filter by Standard
        if (standard != null && !standard.isBlank()) {
            allDocuments = allDocuments.stream()
                    .filter(d -> d.getProcess() != null && standard.equalsIgnoreCase(d.getProcess().getStandard()))
                    .collect(java.util.stream.Collectors.toList());
        }

        // 2. Filter by Parent Process
        if (processId != null) {
            allDocuments = allDocuments.stream()
                    .filter(d -> d.getProcess() != null && processId.equals(d.getProcess().getId()))
                    .collect(java.util.stream.Collectors.toList());
        }

        // 3. Multi-Field Sorting (Title, Size, UploadDate)
        boolean isAsc = "asc".equalsIgnoreCase(sortDir);
        java.util.Comparator<Document> comparator;

        switch (sortField.toLowerCase()) {
            case "title":
                comparator = java.util.Comparator.comparing(
                        d -> d.getTitle() != null ? d.getTitle().toLowerCase() : "",
                        java.util.Comparator.naturalOrder()
                );
                break;
            case "size":
                comparator = java.util.Comparator.comparingLong(
                        d -> d.getFileSize() != 0 ? d.getFileSize() : 0L
                );
                break;
            case "uploaddate":
                comparator = java.util.Comparator.comparing(
                        d -> d.getUploadDate() != null ? d.getUploadDate().toString() : "",
                        java.util.Comparator.naturalOrder()
                );
                break;
            case "id":
            default:
                comparator = java.util.Comparator.comparing(Document::getId, java.util.Comparator.nullsFirst(Long::compareTo));
                break;
        }

        if (!isAsc) {
            comparator = comparator.reversed();
        }

        allDocuments = allDocuments.stream().sorted(comparator).collect(java.util.stream.Collectors.toList());

        // 4. Model Attributes
        model.addAttribute("documents", allDocuments);
        model.addAttribute("allProcesses", processService.getAllProcesses());
        model.addAttribute("standard", standard);
        model.addAttribute("processId", processId);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", isAsc ? "desc" : "asc");

        return "processes/documents";
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable("id") Long id) {
        Document doc = documentService.getDocumentById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid document ID: " + id));

        try {
            String relativePath = doc.getFilePath().startsWith("/")
                    ? doc.getFilePath().substring(1)
                    : doc.getFilePath();
            Path filePath = Paths.get(relativePath);
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String contentDisposition = "attachment; filename=\"" + resource.getFilename() + "\"";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteDocument(@PathVariable("id") Long id) {
        Document document = documentService.getDocumentById(id).orElse(null);
        Long processId = (document != null && document.getProcess() != null) ? document.getProcess().getId() : null;

        documentService.deleteDocument(id);

        return processId != null ? "redirect:/processes/" + processId : "redirect:/processes";
    }
}