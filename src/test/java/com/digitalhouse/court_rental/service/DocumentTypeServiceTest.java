package com.digitalhouse.court_rental.service;

import com.digitalhouse.court_rental.entity.DocumentType;
import com.digitalhouse.court_rental.repository.DocumentTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentTypeServiceTest {


    @Mock
    private DocumentTypeRepository documentTypeRepository;

    @InjectMocks
    private DocumentTypeService documentTypeService;

    private DocumentType documentType1;
    private DocumentType documentType2;

    @BeforeEach
    void setUp() {
        documentType1 = new DocumentType();
        documentType1.setId(1);
        documentType1.setDocument_type("Passport");

        documentType2 = new DocumentType();
        documentType2.setId(2);
        documentType2.setDocument_type("Driver's License");
    }

    @Test
    void testGetAllDocumentTypes_Success() {
        when(documentTypeRepository.findAll()).thenReturn(Arrays.asList(documentType1, documentType2));

        List<DocumentType> result = documentTypeService.getAllDocumentTypes();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Passport", result.get(0).getDocument_type());
        assertEquals("Driver's License", result.get(1).getDocument_type());

        verify(documentTypeRepository, times(1)).findAll();
    }

    @Test
    void testGetAllDocumentTypes_EmptyList() {
        when(documentTypeRepository.findAll()).thenReturn(Collections.emptyList());

        List<DocumentType> result = documentTypeService.getAllDocumentTypes();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(documentTypeRepository, times(1)).findAll();
    }

}