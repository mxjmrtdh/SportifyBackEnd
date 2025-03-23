package com.digitalhouse.court_rental.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.util.Base64;
import java.util.Map;

@Service
public class ImgurService {

    @Value("${imgur.client.id}")  // Debes agregar esta propiedad en application.properties
    private String clientId;

    private static final String IMGUR_UPLOAD_URL = "https://api.imgur.com/3/upload";

    public String uploadFile(MultipartFile file) {
        try {
            // Convertir imagen a Base64
            String encodedImage = Base64.getEncoder().encodeToString(file.getBytes());

            // Crear headers para la petición
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Client-ID " + clientId);

            // Crear cuerpo de la petición
            Map<String, String> body = Map.of("image", encodedImage);

            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);
            RestTemplate restTemplate = new RestTemplate();

            // Realizar la petición a Imgur
            ResponseEntity<Map> response = restTemplate.exchange(IMGUR_UPLOAD_URL, HttpMethod.POST, requestEntity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseData = (Map<String, Object>) response.getBody().get("data");
                return (String) responseData.get("link");  // Retorna el enlace de la imagen subida
            } else {
                throw new RuntimeException("Error al subir la imagen a Imgur");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al subir imagen: " + e.getMessage());
        }
    }
}