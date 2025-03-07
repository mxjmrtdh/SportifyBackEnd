package com.digitalhouse.court_rental.repository;

import com.digitalhouse.court_rental.entity.ProductFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductFeatureRepository extends JpaRepository<ProductFeature, Integer> {
}
