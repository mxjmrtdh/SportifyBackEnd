package com.digitalhouse.court_rental.Listener;

import com.digitalhouse.court_rental.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Setter
@Getter
public class RegistrationCompleteEvent extends ApplicationEvent {

    private User user;
    private String applicationUrl;

    // Constructor que inicializa el evento con el usuario y la URL de la aplicación.
    public RegistrationCompleteEvent(User user, String applicationUrl) {
        super(user); // Llama al constructor de la clase base ApplicationEvent.
        this.user = user; // Asigna el usuario al campo correspondiente.
        this.applicationUrl = applicationUrl; // Asigna la URL de la aplicación.
    }

}
