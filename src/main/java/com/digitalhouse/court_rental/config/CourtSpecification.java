package com.digitalhouse.court_rental.config;

import com.digitalhouse.court_rental.entity.Court;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class CourtSpecification {
    public static Specification<Court> searchCourts(Integer cityId, Integer sportId, LocalDate bookingDate, LocalTime startTime, LocalTime endTime) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (cityId != null) {
                predicates.add(criteriaBuilder.equal(root.get("city").get("idCity"), cityId));
            }

            if (sportId != null) {
                predicates.add(criteriaBuilder.equal(root.get("sport").get("idSport"), sportId));
            }

            if (bookingDate != null) {
                var bookingsJoin = root.join("bookings", JoinType.LEFT);

                Predicate datePredicate = criteriaBuilder.equal(bookingsJoin.get("bookingDate"), bookingDate);

                Predicate timePredicate = criteriaBuilder.or(
                        criteriaBuilder.between(bookingsJoin.get("startTime"), startTime, endTime),
                        criteriaBuilder.between(bookingsJoin.get("endTime"), startTime, endTime),
                        criteriaBuilder.and(
                                criteriaBuilder.lessThanOrEqualTo(bookingsJoin.get("startTime"), startTime),
                                criteriaBuilder.greaterThanOrEqualTo(bookingsJoin.get("endTime"), endTime)
                        )
                );

                predicates.add(criteriaBuilder.not(criteriaBuilder.and(datePredicate, timePredicate)));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
