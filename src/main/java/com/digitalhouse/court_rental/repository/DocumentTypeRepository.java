package com.digitalhouse.court_rental.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.digitalhouse.court_rental.entity.DocumentType;

public interface DocumentTypeRepository extends JpaRepository<DocumentType, Long> {
}
