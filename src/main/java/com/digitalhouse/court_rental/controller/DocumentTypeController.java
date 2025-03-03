package com.digitalhouse.court_rental.controller;

import com.digitalhouse.court_rental.service.DocumentTypeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.digitalhouse.court_rental.entity.DocumentType;

import java.util.List;

@RestController
@RequestMapping("/api/public/document-types")
public class DocumentTypeController {
    private final DocumentTypeService service;

    public DocumentTypeController(DocumentTypeService service) {
        this.service = service;
    }

    @GetMapping
    public List<DocumentType> getAllDocumentTypes() {
        return service.getAllDocumentTypes();
    }
}
