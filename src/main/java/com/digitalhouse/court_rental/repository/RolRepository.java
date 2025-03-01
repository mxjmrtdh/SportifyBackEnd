package com.digitalhouse.court_rental.repository;

import com.digitalhouse.court_rental.entity.Rol;
import com.digitalhouse.court_rental.enums.NameRol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface RolRepository extends JpaRepository<Rol, Long>{
    Optional<Rol> findByName(NameRol name);

}
