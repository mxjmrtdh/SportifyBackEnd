package com.digitalhouse.court_rental.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long id;

    @Column(name = "user_name", nullable = false)
    private String name;

    @Column(name = "document", nullable = false)
    private String document;

    @Column(name = "user_last_name", nullable = false)
    private String lastName;
    
    @Column(name = "email_address", nullable = false, unique = true)
    private String email;

    @Column(name = "user_password", nullable = false)
    private String password;

    //
    //@Column(name = "phone_number", nullable = false)
    //private String phoneNumber;

    //@Column(name = "registration_date", nullable = false)
    //private String registrationDate;

    //@Column(name = "id_status")
    //private Integer statusId;
    //

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "id_user"),
        inverseJoinColumns = @JoinColumn(name = "id_rol")
    )
    private Set<Rol> roles = new HashSet<>();

}
