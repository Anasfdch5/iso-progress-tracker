package com.iso_progress_tracker.services;

import com.iso_progress_tracker.entities.Document;
import com.iso_progress_tracker.repositories.DocumentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;

    @Value("${file.upload-dir:uploads/iso_docs/}")
    private String uploadDir;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    public List<Document> getDocumentsByProcess(Long processId) {
        return documentRepository.findByProcessId(processId);
    }

    public Optional<Document> getDocumentById(Long id) {
        return documentRepository.findById(id);
    }

    public Document saveDocument(Document document) {
        return documentRepository.save(document);
    }

    /**
     * Saves file to disk and persists document entity
     */
    public Document saveDocumentWithFile(Document document, MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            // 1. Ensure upload folder exists
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 2. Prevent filename collisions
            String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "doc.pdf";
            String cleanFilename = System.currentTimeMillis() + "_" + originalFilename.replaceAll("\\s+", "_");
            Path targetPath = uploadPath.resolve(cleanFilename);

            // 3. Write binary file to disk
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 4. Set size (long bytes) and path in entity
            document.setFileSize(file.getSize());
            document.setFilePath("/" + uploadDir + cleanFilename);
        }

        return documentRepository.save(document);
    }

    public void deleteDocument(Long id) {
        Optional<Document> docOpt = documentRepository.findById(id);
        if (docOpt.isPresent()) {
            Document doc = docOpt.get();

            // Delete physical file
            if (doc.getFilePath() != null) {
                try {
                    String relativePath = doc.getFilePath().startsWith("/") ? doc.getFilePath().substring(1) : doc.getFilePath();
                    Files.deleteIfExists(Paths.get(relativePath));
                } catch (IOException e) {
                    System.err.println("Could not delete physical file: " + e.getMessage());
                }
            }

            documentRepository.deleteById(id);
        }
    }
    public boolean documentExistsByTitle(String name) {
        if (name == null || name.isBlank()) return false;
        return documentRepository.existsByTitleIgnoreCase(name.trim());
    }
}