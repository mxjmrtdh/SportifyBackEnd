package com.digitalhouse.court_rental.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "favorites")
@Getter
@Setter
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_court", nullable = false)
    private Court court;

    public Favorite() {}

    public Favorite(User user, Court court) {
        this.user = user;
        this.court = court;
    }
}
