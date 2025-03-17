package com.digitalhouse.court_rental.Repo;

import com.digitalhouse.court_rental.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    /**
     * Verifica si un nombre de usuario ya existe en la base de datos.
     * @param name El nombre de usuario que se desea verificar.
     * @return true si el nombre de usuario ya existe; false si no existe.
     */
    boolean existsByName(String name);

    /**
     * Busca un usuario en la base de datos utilizando su nombre de usuario.
     *
     * @param name El nombre de usuario del usuario que se desea buscar.
     * @return Un objeto Optional que contiene el usuario si se encuentra, o está vacío si no se encuentra.
     */
    Optional<User> findByName(String name);
    /**
     * Verifica si un correo electrónico ya está registrado en la base de datos.
     *
     * @param email El correo electrónico que se desea verificar.
     * @return true si el correo electrónico ya existe; false si no existe.
     */
    boolean existsByEmail(String email);



    Optional<User> findByEmail(String email);
}
