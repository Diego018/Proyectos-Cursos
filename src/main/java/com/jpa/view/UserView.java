package com.jpa.view;

import com.jpa.dto.UserDTO;
import com.jpa.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Route("users")
public class UserView extends VerticalLayout {

    private final UserService userService;
    private final Grid<UserDTO> grid = new Grid<>(UserDTO.class, false);
    private List<UserDTO> allUsers;

    // Filtros
    private final TextField searchField = new TextField();

    @Autowired
    public UserView(UserService userService) {
        this.userService = userService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        // Título
        H2 title = new H2("Gestión de Usuarios");
        add(title);

        // Barra de búsqueda y acciones
        add(createToolbar());

        // Grid
        configureGrid();
        add(grid);

        loadUsers();
    }

    private HorizontalLayout createToolbar() {
        // Campo de búsqueda
        searchField.setPlaceholder("Buscar por nombre, apellido, usuario o email...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> filterUsers());
        searchField.setWidth("400px");

        // Botones de acción
        Button addButton = new Button("Nuevo Usuario", VaadinIcon.PLUS.create());
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> openUserDialog(null));

        Button refreshButton = new Button("Actualizar", VaadinIcon.REFRESH.create());
        refreshButton.addClickListener(e -> {
            loadUsers();
            Notification.show("Lista actualizada").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });

        Button sortButton = new Button("Ordenar por ID", VaadinIcon.SORT.create());
        sortButton.addClickListener(e -> sortById());

        HorizontalLayout toolbar = new HorizontalLayout(
                searchField, addButton, refreshButton, sortButton
        );
        toolbar.setWidthFull();
        toolbar.setAlignItems(Alignment.CENTER);
        toolbar.setSpacing(true);

        return toolbar;
    }

    private void configureGrid() {
        grid.setWidth("100%");
        grid.setHeight("600px");

        // Columnas
        grid.addColumn(UserDTO::getIdUser)
                .setHeader("ID")
                .setWidth("80px")
                .setSortable(true);

        grid.addColumn(UserDTO::getFirstName)
                .setHeader("Nombre")
                .setSortable(true);

        grid.addColumn(UserDTO::getLastName)
                .setHeader("Apellido")
                .setSortable(true);

        grid.addColumn(UserDTO::getUsername)
                .setHeader("Usuario")
                .setSortable(true);

        grid.addColumn(UserDTO::getEmail)
                .setHeader("Correo")
                .setSortable(true);

        // Columna de acciones
        grid.addComponentColumn(user -> {
            Button editButton = new Button(VaadinIcon.EDIT.create());
            editButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
            editButton.addClickListener(e -> openUserDialog(user));

            Button deleteButton = new Button(VaadinIcon.TRASH.create());
            deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
            deleteButton.addClickListener(e -> confirmDelete(user));

            HorizontalLayout actions = new HorizontalLayout(editButton, deleteButton);
            actions.setSpacing(true);
            return actions;
        }).setHeader("Acciones").setWidth("150px");

        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                grid.select(event.getValue());
            }
        });
    }

    private void loadUsers() {
        try {
            allUsers = userService.getAllUsersDTO().stream()
                    .sorted(Comparator.comparing(UserDTO::getIdUser))
                    .collect(Collectors.toList());
            grid.setItems(allUsers);
            searchField.clear();
        } catch (Exception e) {
            showErrorNotification("Error al cargar usuarios: " + e.getMessage());
        }
    }

    private void filterUsers() {
        String searchTerm = searchField.getValue().toLowerCase().trim();

        if (searchTerm.isEmpty()) {
            grid.setItems(allUsers);
            return;
        }

        List<UserDTO> filtered = allUsers.stream()
                .filter(user ->
                        user.getFirstName().toLowerCase().contains(searchTerm) ||
                                user.getLastName().toLowerCase().contains(searchTerm) ||
                                user.getUsername().toLowerCase().contains(searchTerm) ||
                                user.getEmail().toLowerCase().contains(searchTerm)
                )
                .collect(Collectors.toList());

        grid.setItems(filtered);
    }

    private void sortById() {
        List<UserDTO> sorted = allUsers.stream()
                .sorted(Comparator.comparing(UserDTO::getIdUser))
                .collect(Collectors.toList());
        grid.setItems(sorted);
        Notification.show("Ordenado por ID").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void openUserDialog(UserDTO user) {
        Dialog dialog = new Dialog();
        dialog.setWidth("500px");

        H2 dialogTitle = new H2(user == null ? "Nuevo Usuario" : "Editar Usuario");

        // Campos del formulario
        TextField firstNameField = new TextField("Nombre");
        firstNameField.setRequired(true);

        TextField lastNameField = new TextField("Apellido");
        lastNameField.setRequired(true);

        TextField usernameField = new TextField("Usuario");
        usernameField.setRequired(true);

        EmailField emailField = new EmailField("Correo");
        emailField.setRequired(true);

        PasswordField passwordField = new PasswordField("Contraseña");
        passwordField.setRequired(user == null); // Solo requerida para nuevos usuarios

        // Si es edición, llenar campos
        if (user != null) {
            firstNameField.setValue(user.getFirstName());
            lastNameField.setValue(user.getLastName());
            usernameField.setValue(user.getUsername());
            emailField.setValue(user.getEmail());
            passwordField.setPlaceholder("Dejar vacío para mantener la actual");
        }

        FormLayout formLayout = new FormLayout();
        formLayout.add(firstNameField, lastNameField, usernameField, emailField);

        if (user == null) {
            formLayout.add(passwordField);
        }

        // Botones
        Button saveButton = new Button("Guardar", VaadinIcon.CHECK.create());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> {
            try {
                if (user == null) {
                    // Crear nuevo usuario
                    userService.createUser(
                            firstNameField.getValue(),
                            lastNameField.getValue(),
                            usernameField.getValue(),
                            emailField.getValue(),
                            passwordField.getValue()
                    );
                    showSuccessNotification("Usuario creado exitosamente");
                } else {
                    // Actualizar usuario
                    userService.updateUser(
                            user.getIdUser(),
                            firstNameField.getValue(),
                            lastNameField.getValue(),
                            usernameField.getValue(),
                            emailField.getValue()
                    );
                    showSuccessNotification("Usuario actualizado exitosamente");
                }
                loadUsers();
                dialog.close();
            } catch (Exception ex) {
                showErrorNotification("Error: " + ex.getMessage());
            }
        });

        Button cancelButton = new Button("Cancelar", VaadinIcon.CLOSE.create());
        cancelButton.addClickListener(e -> dialog.close());

        HorizontalLayout buttons = new HorizontalLayout(saveButton, cancelButton);
        buttons.setSpacing(true);

        VerticalLayout dialogLayout = new VerticalLayout(dialogTitle, formLayout, buttons);
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(true);

        dialog.add(dialogLayout);
        dialog.open();
    }

    private void confirmDelete(UserDTO user) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setWidth("400px");

        H2 title = new H2("Confirmar eliminación");

        VerticalLayout message = new VerticalLayout();
        message.add("¿Está seguro de eliminar al usuario?");
        message.add("Nombre: " + user.getFirstName() + " " + user.getLastName());
        message.add("Usuario: " + user.getUsername());

        Button confirmButton = new Button("Eliminar", VaadinIcon.TRASH.create());
        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        confirmButton.addClickListener(e -> {
            try {
                userService.deleteUser(user.getIdUser());
                showSuccessNotification("Usuario eliminado exitosamente");
                loadUsers();
                confirmDialog.close();
            } catch (Exception ex) {
                showErrorNotification("Error al eliminar: " + ex.getMessage());
            }
        });

        Button cancelButton = new Button("Cancelar");
        cancelButton.addClickListener(e -> confirmDialog.close());

        HorizontalLayout buttons = new HorizontalLayout(confirmButton, cancelButton);
        buttons.setSpacing(true);

        VerticalLayout dialogLayout = new VerticalLayout(title, message, buttons);
        dialogLayout.setPadding(true);

        confirmDialog.add(dialogLayout);
        confirmDialog.open();
    }

    private void showSuccessNotification(String message) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.TOP_CENTER);
    }

    private void showErrorNotification(String message) {
        Notification notification = Notification.show(message, 5000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}