package com.jpa.view;

import com.jpa.dto.ClubDTO;
import com.jpa.service.ClubService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.jpa.dto.UserDTO;

@Route("clubes")
public class ClubView extends VerticalLayout implements BeforeEnterObserver {

    private final ClubService clubService;

    private Grid<ClubDTO> grid;
    private TextField searchField;
    private Button addButton;

    public ClubView(ClubService clubService) {
        this.clubService = clubService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        configureHeader();
        configureGrid();
        updateGrid();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        UserDTO user = VaadinSession.getCurrent().getAttribute(UserDTO.class);
        if (user == null) {
            event.rerouteTo("");
        }
    }

    private void configureHeader() {
        H2 title = new H2("Gestión de Clubes");

        searchField = new TextField();
        searchField.setPlaceholder("Buscar club...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> updateGrid());
        searchField.setWidth("300px");

        addButton = new Button("Nuevo Club", VaadinIcon.PLUS.create());
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> openClubDialog(null));

        HorizontalLayout toolbar = new HorizontalLayout(searchField, addButton);
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);

        add(title, toolbar);
    }

    private void configureGrid() {
        grid = new Grid<>(ClubDTO.class, false);
        grid.setSizeFull();

        grid.addColumn(ClubDTO::getIdClub)
                .setHeader("ID")
                .setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(ClubDTO::getNameClub)
                .setHeader("Nombre del Club")
                .setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(club -> club.getCoachName() != null ? club.getCoachName() : "Sin entrenador")
                .setHeader("Entrenador")
                .setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(club -> club.getAssociationName() != null ? club.getAssociationName() : "Sin asociación")
                .setHeader("Asociación")
                .setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(ClubDTO::getTotalPlayers)
                .setHeader("Jugadores")
                .setAutoWidth(true)
                .setSortable(true);

        grid.addComponentColumn(club -> {
            Button editButton = new Button(VaadinIcon.EDIT.create());
            editButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
            editButton.addClickListener(e -> openClubDialog(club));

            Button deleteButton = new Button(VaadinIcon.TRASH.create());
            deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            deleteButton.addClickListener(e -> confirmDelete(club));

            HorizontalLayout actions = new HorizontalLayout(editButton, deleteButton);
            actions.setSpacing(true);
            return actions;
        }).setHeader("Acciones").setAutoWidth(true);

        add(grid);
    }

    private void updateGrid() {
        try {
            String searchTerm = searchField.getValue();
            if (searchTerm != null && !searchTerm.isEmpty()) {
                grid.setItems(clubService.searchClubs(searchTerm));
            } else {
                grid.setItems(clubService.getAllClubs());
            }
        } catch (Exception e) {
            showErrorNotification("Error al cargar los clubes: " + e.getMessage());
        }
    }

    private void openClubDialog(ClubDTO club) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(club == null ? "Nuevo Club" : "Editar Club");
        dialog.setWidth("500px");

        ClubForm form = new ClubForm(club);

        Button saveButton = new Button("Guardar", e -> {
            try {
                if (club == null) {
                    clubService.createClub(
                            form.getNameClub(),
                            form.getCoachId(),
                            form.getAssociationId()
                    );
                    showSuccessNotification("Club creado exitosamente");
                } else {
                    clubService.updateClub(
                            club.getIdClub(),
                            form.getNameClub(),
                            form.getCoachId(),
                            form.getAssociationId()
                    );
                    showSuccessNotification("Club actualizado exitosamente");
                }
                updateGrid();
                dialog.close();
            } catch (Exception ex) {
                showErrorNotification("Error: " + ex.getMessage());
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancelar", e -> dialog.close());

        dialog.getFooter().add(cancelButton, saveButton);
        dialog.add(form);
        dialog.open();
    }

    private void confirmDelete(ClubDTO club) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setHeaderTitle("Confirmar eliminación");

        VerticalLayout content = new VerticalLayout();
        content.add("¿Está seguro que desea eliminar el club: " + club.getNameClub() + "?");
        content.add("Esta acción no se puede deshacer.");

        Button confirmButton = new Button("Eliminar", e -> {
            try {
                clubService.deleteClub(club.getIdClub());
                showSuccessNotification("Club eliminado exitosamente");
                updateGrid();
                confirmDialog.close();
            } catch (Exception ex) {
                showErrorNotification("Error al eliminar: " + ex.getMessage());
            }
        });
        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        Button cancelButton = new Button("Cancelar", e -> confirmDialog.close());

        confirmDialog.getFooter().add(cancelButton, confirmButton);
        confirmDialog.add(content);
        confirmDialog.open();
    }

    private void showSuccessNotification(String message) {
        Notification notification = Notification.show(message, 3000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void showErrorNotification(String message) {
        Notification notification = Notification.show(message, 5000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }

    // Clase interna para el formulario
    private class ClubForm extends VerticalLayout {
        private TextField nameField;
        private TextField coachIdField;
        private TextField associationIdField;

        public ClubForm(ClubDTO club) {
            setSpacing(true);
            setPadding(false);

            nameField = new TextField("Nombre del Club");
            nameField.setWidthFull();
            nameField.setRequired(true);

            coachIdField = new TextField("ID del Entrenador");
            coachIdField.setWidthFull();
            coachIdField.setHelperText("Opcional");

            associationIdField = new TextField("ID de la Asociación");
            associationIdField.setWidthFull();
            associationIdField.setHelperText("Opcional");

            if (club != null) {
                nameField.setValue(club.getNameClub());
                if (club.getCoachId() != null) {
                    coachIdField.setValue(club.getCoachId().toString());
                }
                if (club.getAssociationId() != null) {
                    associationIdField.setValue(club.getAssociationId().toString());
                }
            }

            add(nameField, coachIdField, associationIdField);
        }

        public String getNameClub() {
            return nameField.getValue();
        }

        public Long getCoachId() {
            String value = coachIdField.getValue();
            return (value != null && !value.isEmpty()) ? Long.parseLong(value) : null;
        }

        public Long getAssociationId() {
            String value = associationIdField.getValue();
            return (value != null && !value.isEmpty()) ? Long.parseLong(value) : null;
        }
    }
}