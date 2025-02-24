package com.digitalhouse.court_rental.entity.court;

import com.digitalhouse.court_rental.entity.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Sport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idSport;

    private String sportName;

    @ManyToOne
    @JoinColumn(name = "id_status")
    private Status status;
}
