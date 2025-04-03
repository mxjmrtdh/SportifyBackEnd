package com.digitalhouse.court_rental.service;

import com.digitalhouse.court_rental.dto.CourtDTO;
import com.digitalhouse.court_rental.dto.CourtRequestDTO;
import com.digitalhouse.court_rental.dto.PagedResponse;
import com.digitalhouse.court_rental.entity.Court;
import com.digitalhouse.court_rental.entity.Feature;
import com.digitalhouse.court_rental.entity.ProductFeature;
import com.digitalhouse.court_rental.entity.Status;
import com.digitalhouse.court_rental.entity.court.City;
import com.digitalhouse.court_rental.entity.court.Sport;
import com.digitalhouse.court_rental.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourtService {
    private final CourtRepository courtRepository;
    private final SportRepository sportRepository;
    private final CityRepository cityRepository;
    private final StatusRepository statusRepository;
    private final ImgurService imgurService;
    private final FeatureRepository featureRepository;
    private final ProductFeatureRepository productFeatureRepository;

    public Court createCourt(CourtRequestDTO courtRequest, List<MultipartFile> images) {
        if (courtRepository.findByCourtName(courtRequest.getName()).isPresent()) {
            throw new IllegalArgumentException("La cancha ya está registrada");
        }

        Court court = new Court();
        court.setCourtName(courtRequest.getName());
        court.setCourtDescription(courtRequest.getDescription());
        court.setCapacity(courtRequest.getCapacity());
        court.setPricePerHour(courtRequest.getPricePerHour());
        court.setAddress(courtRequest.getAddress());
        court.setNeighborhood(courtRequest.getNeighborhood());

        Sport sport = sportRepository.findById((long) courtRequest.getSportId())
                .orElseThrow(() -> new RuntimeException("Sport not found"));
        City city = cityRepository.findById(courtRequest.getCityId())
                .orElseThrow(() -> new RuntimeException("City not found"));
        Status status = statusRepository.findById(courtRequest.getStatusId())
                .orElseThrow(() -> new RuntimeException("Status not found"));

        court.setSport(sport);
        court.setCity(city);
        court.setStatus(status);

        // Subir imágenes
        List<String> imageLinks = new ArrayList<>();
        if (images != null) {
            for (MultipartFile image : images) {
                try {
                    String link = imgurService.uploadFile(image);
                    imageLinks.add(link);
                } catch (Exception e) {
                    log.error("Error al subir imagen a Imgur", e);
                }
            }
        }
        court.setImageUrl(imageLinks);

        Court savedCourt = courtRepository.save(court);

        if (courtRequest.getFeatureIds() != null) {
            for (Integer featureId : courtRequest.getFeatureIds()) {
                Feature feature = featureRepository.findById(Long.valueOf(featureId))
                        .orElseThrow(() -> new RuntimeException("Feature not found"));

                ProductFeature productFeature = new ProductFeature();
                productFeature.setCourt(savedCourt);
                productFeature.setFeature(feature);

                productFeatureRepository.save(productFeature);
            }
        }

        return savedCourt;
    }

    public PagedResponse<CourtDTO> getAllCourts(int page, int size) {
        List<Object[]> results = courtRepository.getCourts(page -1, size);
        Map<Integer, CourtDTO> courtMap = new HashMap<>();

        results.forEach(obj -> {
            int courtId = obj[0] instanceof Integer ? (Integer) obj[0] : Integer.parseInt(obj[0].toString());

            CourtDTO dto = courtMap.computeIfAbsent(courtId, id -> {
                CourtDTO newDto = new CourtDTO();
                newDto.setId(courtId);
                newDto.setName((String) obj[1]);
                newDto.setSport((String) obj[2]);
                newDto.setCity((String) obj[3]);
                newDto.setStatus((String) obj[4]);
                newDto.setDescription((String) obj[5]);
                newDto.setCapacity((Integer) obj[6]);
                newDto.setPricePerHour((BigDecimal) obj[7]);
                newDto.setAddress((String) obj[8]);
                newDto.setNeighborhood((String) obj[9]);
                newDto.setImageUrl(new ArrayList<>());
                newDto.setFeatures(new ArrayList<>());
                newDto.setFeaturesImageUrl(new ArrayList<>());
                return newDto;
            });

            if (obj[10] != null) {
                if (!dto.getImageUrl().contains(obj[10].toString())) {
                    dto.getImageUrl().add(obj[10].toString());
                }
            }

            if (obj[11] != null && !dto.getFeatures().contains(obj[11].toString())) {
                dto.getFeatures().add(obj[11].toString());
            }

            if (obj[12] != null && !dto.getFeaturesImageUrl().contains(obj[12].toString())) {
                dto.getFeaturesImageUrl().add(obj[12].toString());
            }
        });

        List<CourtDTO> courts = new ArrayList<>(courtMap.values());

        long totalElements = courtRepository.countTotalCourts();

        return new PagedResponse<>(courts, page, size, totalElements);
    }


    public CourtDTO getCourtById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        List<Object[]> results = courtRepository.getCourtById(Long.valueOf(id));

        if (results.isEmpty()) {
            throw new EntityNotFoundException("Court not found");
        }

        CourtDTO courtDTO = null;
        Set<String> images = new HashSet<>();
        Map<String, String> features = new LinkedHashMap<>();
        Map<String, String> featuresImageUrl = new LinkedHashMap<>();

        for (Object[] obj : results) {
            if (courtDTO == null) { // Asignar la instancia en la primera iteración
                courtDTO = new CourtDTO();
                courtDTO.setId((Integer) obj[0]);
                courtDTO.setName((String) obj[1]);
                courtDTO.setSport((String) obj[2]);
                courtDTO.setCity((String) obj[3]);
                courtDTO.setStatus((String) obj[4]);
                courtDTO.setDescription((String) obj[5]);
                courtDTO.setCapacity((Integer) obj[6]);
                courtDTO.setPricePerHour((BigDecimal) obj[7]);
                courtDTO.setAddress((String) obj[8]);
                courtDTO.setNeighborhood((String) obj[9]);
            }

            if (obj[10] != null) {
                images.add(obj[10].toString());
            }

            if (obj[11] != null) {
                features.put(obj[11].toString(), obj[11].toString());
            }

            if (obj[12] != null) {
                featuresImageUrl.put(obj[12].toString(), obj[12].toString());
            }
        }

        courtDTO.setImageUrl(new ArrayList<>(images));
        courtDTO.setFeatures(new ArrayList<>(features.keySet()));
        courtDTO.setFeaturesImageUrl(new ArrayList<>(featuresImageUrl.keySet()));

        return courtDTO;
    }

    public List<CourtDTO> getRandomCourts() {
        List<Object[]> results = courtRepository.getRandomCourts();
        List<CourtDTO> courts = new ArrayList<>();

        for (Object[] row : results) {
            CourtDTO courtDTO = new CourtDTO();
            courtDTO.setId((int) row[0]);
            courtDTO.setName((String) row[1]);
            courtDTO.setDescription((String) row[2]);
            courtDTO.setSport((String) row[3]);
            courtDTO.setCapacity((int) row[4]);
            courtDTO.setPricePerHour((BigDecimal) row[5]);
            courtDTO.setStatus((String) row[6]);
            courtDTO.setAddress((String) row[7]);
            courtDTO.setNeighborhood((String) row[8]);
            courtDTO.setImageUrl(Collections.singletonList((String) row[10]));
            courtDTO.setCity((String) row[9]);
            courts.add(courtDTO);
        }
        return courts;
    }

    public void deleteCourt(Long id) {
        Court court = courtRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Court not found"));

        Status inactiveStatus = statusRepository.findById(2)
                .orElseThrow(() -> new EntityNotFoundException("Inactive status not found"));

        court.setStatus(inactiveStatus);
        courtRepository.save(court);
    }

    public List<CourtDTO> getCourtsBySport(int sportId) {
        List<Object[]> results = courtRepository.searchByCategory(sportId);
        List<CourtDTO> courts = new ArrayList<>();

        for (Object[] row : results) {
            CourtDTO courtDTO = new CourtDTO();
            courtDTO.setId((int) row[0]);
            courtDTO.setName((String) row[1]);
            courtDTO.setDescription((String) row[2]);
            courtDTO.setSport((String) row[3]);
            courtDTO.setCapacity((int) row[4]);
            courtDTO.setPricePerHour((BigDecimal) row[5]);
            courtDTO.setStatus((String) row[6]);
            courtDTO.setAddress((String) row[7]);
            courtDTO.setNeighborhood((String) row[8]);
            courtDTO.setCity((String) row[9]);
            courts.add(courtDTO);
        }
        return courts;
    }

    @Transactional
    public Court updateCourt(Long courtId, CourtRequestDTO courtRequest, List<MultipartFile> images) {
        Court court = courtRepository.findById(courtId)
                .orElseThrow(() -> new RuntimeException("Court not found"));

        court.setCourtName(courtRequest.getName());
        court.setCourtDescription(courtRequest.getDescription());
        court.setCapacity(courtRequest.getCapacity());
        court.setPricePerHour(courtRequest.getPricePerHour());
        court.setAddress(courtRequest.getAddress());
        court.setNeighborhood(courtRequest.getNeighborhood());

        Sport sport = sportRepository.findById((long) courtRequest.getSportId())
                .orElseThrow(() -> new RuntimeException("Sport not found"));
        City city = cityRepository.findById(courtRequest.getCityId())
                .orElseThrow(() -> new RuntimeException("City not found"));
        Status status = statusRepository.findById(courtRequest.getStatusId())
                .orElseThrow(() -> new RuntimeException("Status not found"));

        court.setSport(sport);
        court.setCity(city);
        court.setStatus(status);

        // Eliminar todas las features asociadas
        productFeatureRepository.deleteByCourtId(courtId);

        // Agregar nuevas features
        if (courtRequest.getFeatureIds() != null) {
            for (Integer featureId : courtRequest.getFeatureIds()) {
                Feature feature = featureRepository.findById(Long.valueOf(featureId))
                        .orElseThrow(() -> new RuntimeException("Feature not found"));

                ProductFeature productFeature = new ProductFeature();
                productFeature.setCourt(court);
                productFeature.setFeature(feature);

                productFeatureRepository.save(productFeature);
            }
        }

        // Eliminar imágenes anteriores y subir nuevas imágenes
        court.setImageUrl(new ArrayList<>());
        if (images != null && !images.isEmpty()) {
            List<String> imageLinks = new ArrayList<>();
            for (MultipartFile image : images) {
                try {
                    String link = imgurService.uploadFile(image);
                    imageLinks.add(link);
                } catch (Exception e) {
                    log.error("Error al subir imagen a Imgur", e);
                }
            }
            court.setImageUrl(imageLinks);
        }

        return courtRepository.save(court);
    }
}
