package com.jpa.view;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.Route;
import com.jpa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

@Route("register")
public class RegisterView extends VerticalLayout {

    private final UserService userService;

    // Campos del formulario
    private final TextField firstNameField = new TextField();
    private final TextField lastNameField = new TextField();
    private final TextField usernameField = new TextField();
    private final EmailField emailField = new EmailField();
    private final PasswordField passwordField = new PasswordField();
    private final PasswordField confirmPasswordField = new PasswordField();

    // Binder para validaciones
    private final Binder<UserRegistrationForm> binder = new Binder<>(UserRegistrationForm.class);

    @Autowired
    public RegisterView(UserService userService) {
        this.userService = userService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        getStyle()
                .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                .set("padding", "20px");

        add(createRegisterCard());

        setupValidations();
    }

    private VerticalLayout createRegisterCard() {
        VerticalLayout card = new VerticalLayout();
        card.setWidth("550px");
        card.setPadding(true);
        card.setSpacing(true);
        card.getStyle()
                .set("background", "white")
                .set("border-radius", "12px")
                .set("box-shadow", "0 4px 6px rgba(0, 0, 0, 0.1)");

        // Título y subtítulo
        H1 title = new H1("Crear Cuenta");
        title.getStyle()
                .set("margin", "0")
                .set("color", "#333")
                .set("text-align", "center");

        Span subtitle = new Span("Completa el formulario para registrarte");
        subtitle.getStyle()
                .set("color", "#666")
                .set("text-align", "center")
                .set("margin-bottom", "20px");

        // Configurar campos
        configureFields();

        // Formulario
        FormLayout formLayout = new FormLayout();
        formLayout.add(
                firstNameField, lastNameField,
                usernameField, emailField,
                passwordField, confirmPasswordField
        );
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );
        formLayout.setColspan(usernameField, 2);
        formLayout.setColspan(emailField, 2);

        // Indicador de fortaleza de contraseña
        Span passwordStrength = new Span();
        passwordStrength.getStyle()
                .set("font-size", "0.875rem")
                .set("margin-top", "-10px")
                .set("margin-bottom", "10px");

        passwordField.addValueChangeListener(e -> {
            String password = e.getValue();
            String strength = getPasswordStrength(password);
            passwordStrength.setText(strength);

            if (strength.contains("Débil")) {
                passwordStrength.getStyle().set("color", "#e74c3c");
            } else if (strength.contains("Media")) {
                passwordStrength.getStyle().set("color", "#f39c12");
            } else if (strength.contains("Fuerte")) {
                passwordStrength.getStyle().set("color", "#27ae60");
            }
        });

        // Botón de registro
        Button registerButton = new Button("Crear Cuenta", VaadinIcon.USER_CHECK.create());
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        registerButton.setWidthFull();
        registerButton.getStyle()
                .set("margin-top", "20px")
                .set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                .set("border", "none");
        registerButton.addClickListener(e -> handleRegister());
        registerButton.addClickShortcut(Key.ENTER);

        // Link de login
        Span loginText = new Span("¿Ya tienes cuenta? ");
        Button loginButton = new Button("Inicia sesión aquí");
        loginButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        loginButton.addClickListener(e -> UI.getCurrent().navigate(""));

        HorizontalLayout loginLayout = new HorizontalLayout(loginText, loginButton);
        loginLayout.setWidthFull();
        loginLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        loginLayout.setAlignItems(FlexComponent.Alignment.CENTER);

        card.add(
                title,
                subtitle,
                formLayout,
                passwordStrength,
                registerButton,
                loginLayout
        );

        card.setAlignItems(Alignment.STRETCH);

        return card;
    }

    private void configureFields() {
        // Nombre
        firstNameField.setLabel("Nombre");
        firstNameField.setPlaceholder("Tu nombre");
        firstNameField.setPrefixComponent(VaadinIcon.USER.create());
        firstNameField.setRequired(true);
        firstNameField.setRequiredIndicatorVisible(true);

        // Apellido
        lastNameField.setLabel("Apellido");
        lastNameField.setPlaceholder("Tu apellido");
        lastNameField.setPrefixComponent(VaadinIcon.USER.create());
        lastNameField.setRequired(true);
        lastNameField.setRequiredIndicatorVisible(true);

        // Usuario
        usernameField.setLabel("Nombre de Usuario");
        usernameField.setPlaceholder("Ej: john_doe");
        usernameField.setPrefixComponent(VaadinIcon.AT.create());
        usernameField.setRequired(true);
        usernameField.setRequiredIndicatorVisible(true);
        usernameField.setHelperText("Debe ser único, sin espacios");

        // Email
        emailField.setLabel("Correo Electrónico");
        emailField.setPlaceholder("tu@email.com");
        emailField.setPrefixComponent(VaadinIcon.ENVELOPE.create());
        emailField.setRequired(true);
        emailField.setRequiredIndicatorVisible(true);
        emailField.setHelperText("Debe ser único y válido");

        // Contraseña
        passwordField.setLabel("Contraseña");
        passwordField.setPlaceholder("Mínimo 6 caracteres");
        passwordField.setPrefixComponent(VaadinIcon.LOCK.create());
        passwordField.setRequired(true);
        passwordField.setRequiredIndicatorVisible(true);
        passwordField.setRevealButtonVisible(true);

        // Confirmar contraseña
        confirmPasswordField.setLabel("Confirmar Contraseña");
        confirmPasswordField.setPlaceholder("Repite tu contraseña");
        confirmPasswordField.setPrefixComponent(VaadinIcon.LOCK.create());
        confirmPasswordField.setRequired(true);
        confirmPasswordField.setRequiredIndicatorVisible(true);
        confirmPasswordField.setRevealButtonVisible(true);
    }

    private void setupValidations() {
        // Validación de nombre
        binder.forField(firstNameField)
                .withValidator(new StringLengthValidator(
                        "El nombre debe tener entre 2 y 50 caracteres", 2, 50))
                .bind(UserRegistrationForm::getFirstName, UserRegistrationForm::setFirstName);

        // Validación de apellido
        binder.forField(lastNameField)
                .withValidator(new StringLengthValidator(
                        "El apellido debe tener entre 2 y 50 caracteres", 2, 50))
                .bind(UserRegistrationForm::getLastName, UserRegistrationForm::setLastName);

        // Validación de username
        binder.forField(usernameField)
                .withValidator(new StringLengthValidator(
                        "El usuario debe tener entre 3 y 20 caracteres", 3, 20))
                .withValidator(username -> !username.contains(" "),
                        "El usuario no puede contener espacios")
                .bind(UserRegistrationForm::getUsername, UserRegistrationForm::setUsername);

        // Validación de email
        binder.forField(emailField)
                .withValidator(new EmailValidator("Email inválido"))
                .bind(UserRegistrationForm::getEmail, UserRegistrationForm::setEmail);

        // Validación de contraseña
        binder.forField(passwordField)
                .withValidator(new StringLengthValidator(
                        "La contraseña debe tener al menos 6 caracteres", 6, 100))
                .bind(UserRegistrationForm::getPassword, UserRegistrationForm::setPassword);

        // Validación de confirmación de contraseña
        binder.forField(confirmPasswordField)
                .withValidator(confirmPassword ->
                                confirmPassword.equals(passwordField.getValue()),
                        "Las contraseñas no coinciden")
                .bind(UserRegistrationForm::getConfirmPassword, UserRegistrationForm::setConfirmPassword);
    }

    private void handleRegister() {
        // Validar todos los campos
        if (binder.validate().isOk()) {
            try {
                // Verificar que las contraseñas coincidan
                if (!passwordField.getValue().equals(confirmPasswordField.getValue())) {
                    showErrorNotification("Las contraseñas no coinciden");
                    confirmPasswordField.setInvalid(true);
                    return;
                }

                // Crear usuario
                userService.createUser(
                        firstNameField.getValue().trim(),
                        lastNameField.getValue().trim(),
                        usernameField.getValue().trim(),
                        emailField.getValue().trim(),
                        passwordField.getValue()
                );

                showSuccessNotification("¡Registro exitoso! Redirigiendo al login...");

                // Limpiar campos
                clearFields();

                // Redirigir al login después de 2 segundos
                UI.getCurrent().getPage().executeJs(
                        "setTimeout(() => window.location.href = '', 2000)"
                );

            } catch (Exception e) {
                showErrorNotification("Error al registrar: " + e.getMessage());
            }
        } else {
            showErrorNotification("Por favor corrige los errores en el formulario");
        }
    }

    private String getPasswordStrength(String password) {
        if (password.isEmpty()) {
            return "";
        }

        int strength = 0;

        if (password.length() >= 8) strength++;
        if (password.matches(".*[A-Z].*")) strength++; // Mayúscula
        if (password.matches(".*[a-z].*")) strength++; // Minúscula
        if (password.matches(".*\\d.*")) strength++;   // Número
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) strength++; // Especial

        if (strength <= 2) {
            return "Contraseña: Débil 🔴";
        } else if (strength <= 3) {
            return "Contraseña: Media 🟡";
        } else {
            return "Contraseña: Fuerte 🟢";
        }
    }

    private void clearFields() {
        firstNameField.clear();
        lastNameField.clear();
        usernameField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }

    private void showSuccessNotification(String message) {
        Notification notification = Notification.show(message, 3000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void showErrorNotification(String message) {
        Notification notification = Notification.show(message, 4000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    // Clase interna para el formulario
    public static class UserRegistrationForm {
        private String firstName;
        private String lastName;
        private String username;
        private String email;
        private String password;
        private String confirmPassword;

        // Getters y Setters
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getConfirmPassword() { return confirmPassword; }
        public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    }
}