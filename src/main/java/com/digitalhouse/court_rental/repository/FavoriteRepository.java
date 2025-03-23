package com.digitalhouse.court_rental.repository;

import com.digitalhouse.court_rental.entity.Court;
import com.digitalhouse.court_rental.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    @Query("SELECT c FROM Favorite f JOIN f.court c WHERE f.user.id = :idUser")
    List<Court> findCourtsByUserId(@Param("idUser") Long idUser);

    @Query("SELECT f FROM Favorite f WHERE f.user.id_user = :idUser AND f.court.id = :courtId")
    Optional<Favorite> findByUserIdAndCourtId(@Param("idUser") Long idUser, @Param("courtId") Long courtId);
}
