package com.jpa.view;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.jpa.dto.UserDTO;
import com.jpa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

@Route("")
public class LoginView extends VerticalLayout {

    private final UserService userService;
    private final TextField usernameField = new TextField();
    private final PasswordField passwordField = new PasswordField();

    @Autowired
    public LoginView(UserService userService) {
        this.userService = userService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle().set("background-color", "#f5f5f5");

        add(createLoginCard());
    }

    private VerticalLayout createLoginCard() {
        VerticalLayout card = new VerticalLayout();
        card.setWidth("400px");
        card.setPadding(true);
        card.setSpacing(true);
        card.getStyle()
                .set("background", "white")
                .set("border-radius", "8px")
                .set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)")
                .set("padding", "40px");

        // Título
        H1 title = new H1("Iniciar Sesión");
        title.getStyle()
                .set("margin", "0 0 30px 0")
                .set("font-size", "28px")
                .set("font-weight", "600")
                .set("color", "#333");

        // Campo de usuario
        usernameField.setLabel("Usuario o Email");
        usernameField.setWidthFull();
        usernameField.getStyle().set("margin-bottom", "15px");

        // Campo de contraseña
        passwordField.setLabel("Contraseña");
        passwordField.setWidthFull();
        passwordField.getStyle().set("margin-bottom", "20px");

        // Botón de login
        Button loginButton = new Button("Entrar");
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginButton.setWidthFull();
        loginButton.getStyle().set("margin-bottom", "15px");
        loginButton.addClickListener(e -> handleLogin());
        loginButton.addClickShortcut(Key.ENTER);

        // Separador
        Span separator = new Span("o");
        separator.getStyle()
                .set("text-align", "center")
                .set("color", "#999")
                .set("margin", "10px 0");

        // Botón de registro
        Button registerButton = new Button("Crear cuenta");
        registerButton.setWidthFull();
        registerButton.addClickListener(e -> openRegisterDialog());

        card.add(title, usernameField, passwordField, loginButton, separator, registerButton);
        card.setAlignItems(Alignment.STRETCH);

        return card;
    }

    private void handleLogin() {
        String username = usernameField.getValue().trim();
        String password = passwordField.getValue();

        if (username.isEmpty() || password.isEmpty()) {
            Notification.show("Por favor completa todos los campos", 3000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            return;
        }

        try {
            UserDTO user = userService.validateLogin(username, password);

            if (user != null) {
                VaadinSession.getCurrent().setAttribute(UserDTO.class, user);
                usernameField.clear();
                passwordField.clear();
                UI.getCurrent().navigate("main");
            } else {
                Notification.show("Usuario o contraseña incorrectos", 3000, Notification.Position.BOTTOM_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                passwordField.clear();
            }
        } catch (Exception e) {
            Notification.show("Error al iniciar sesión", 3000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void openRegisterDialog() {
        Dialog dialog = new Dialog();
        dialog.setWidth("450px");

        H2 dialogTitle = new H2("Crear cuenta");
        dialogTitle.getStyle().set("margin", "0 0 20px 0");

        TextField firstNameField = new TextField("Nombre");
        firstNameField.setWidthFull();

        TextField lastNameField = new TextField("Apellido");
        lastNameField.setWidthFull();

        TextField usernameField = new TextField("Usuario");
        usernameField.setWidthFull();

        EmailField emailField = new EmailField("Email");
        emailField.setWidthFull();

        PasswordField newPasswordField = new PasswordField("Contraseña");
        newPasswordField.setWidthFull();

        PasswordField confirmPasswordField = new PasswordField("Confirmar contraseña");
        confirmPasswordField.setWidthFull();

        FormLayout formLayout = new FormLayout();
        formLayout.add(firstNameField, lastNameField, usernameField,
                emailField, newPasswordField, confirmPasswordField);

        Button saveButton = new Button("Registrar");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> {
            try {
                if (firstNameField.isEmpty() || lastNameField.isEmpty() ||
                        usernameField.isEmpty() || emailField.isEmpty() ||
                        newPasswordField.isEmpty() || confirmPasswordField.isEmpty()) {
                    Notification.show("Completa todos los campos", 3000, Notification.Position.BOTTOM_CENTER)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }

                if (!newPasswordField.getValue().equals(confirmPasswordField.getValue())) {
                    Notification.show("Las contraseñas no coinciden", 3000, Notification.Position.BOTTOM_CENTER)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }

                userService.createUser(
                        firstNameField.getValue(),
                        lastNameField.getValue(),
                        usernameField.getValue(),
                        emailField.getValue(),
                        newPasswordField.getValue()
                );

                Notification.show("Cuenta creada exitosamente", 3000, Notification.Position.BOTTOM_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                dialog.close();

            } catch (Exception ex) {
                Notification.show(ex.getMessage(), 3000, Notification.Position.BOTTOM_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        Button cancelButton = new Button("Cancelar");
        cancelButton.addClickListener(e -> dialog.close());

        HorizontalLayout buttons = new HorizontalLayout(saveButton, cancelButton);
        buttons.setWidthFull();
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);

        VerticalLayout dialogLayout = new VerticalLayout(dialogTitle, formLayout, buttons);
        dialogLayout.setPadding(true);

        dialog.add(dialogLayout);
        dialog.open();
    }
}