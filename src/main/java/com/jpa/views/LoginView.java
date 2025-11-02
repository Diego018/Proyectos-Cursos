package com.jpa.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("login")
public class LoginView extends VerticalLayout {

    private final LoginForm loginForm = new LoginForm();

    public LoginView() {

        H1 title = new H1("Iniciar sesión");

        loginForm.addLoginListener(e -> {

                    if ("admin".equals(e.getUsername()) && "1234".equals(e.getPassword())) {
                        UI.getCurrent().navigate("crud");
                    } else {
                        loginForm.setError(true);
                        Notification.show("Credenciales inválidas", 3000, Notification.Position.MIDDLE);

                    }

                });

        add(title, loginForm);
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

    }
}

