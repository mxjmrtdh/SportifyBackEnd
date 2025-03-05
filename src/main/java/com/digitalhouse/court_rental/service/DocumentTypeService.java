package com.digitalhouse.court_rental.service;

import com.digitalhouse.court_rental.repository.DocumentTypeRepository;
import org.springframework.stereotype.Service;
import com.digitalhouse.court_rental.entity.DocumentType;

import java.util.List;

@Service
public class DocumentTypeService {
    private final DocumentTypeRepository documentTypeRepository;

    public DocumentTypeService(DocumentTypeRepository repository) {
        this.documentTypeRepository = repository;
    }

    public List<DocumentType> getAllDocumentTypes() {
        return documentTypeRepository.findAll();
    }
}
