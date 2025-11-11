package com.jpa.view;

import com.jpa.dto.CoachDTO;
import com.jpa.dto.CountryDTO;
import com.jpa.dto.UserDTO;
import com.jpa.service.CoachService;
import com.jpa.service.CountryService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.util.List;

@Route("entrenadores")
public class CoachView extends VerticalLayout implements BeforeEnterObserver {

    private final CoachService coachService;
    private final CountryService countryService;

    private Grid<CoachDTO> grid;
    private TextField searchField;

    public CoachView(CoachService coachService, CountryService countryService) {
        this.coachService = coachService;
        this.countryService = countryService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        add(createHeader());
        add(createToolbar());
        add(createGrid());

        updateGrid();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        UserDTO user = VaadinSession.getCurrent().getAttribute(UserDTO.class);
        if (user == null) {
            event.rerouteTo("");
        }
    }

    private HorizontalLayout createHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setPadding(true);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.getStyle()
                .set("background", "#2c3e50")
                .set("color", "white")
                .set("min-height", "60px");

        Button backButton = new Button(new Icon(VaadinIcon.ARROW_LEFT));
        backButton.addClickListener(e -> UI.getCurrent().navigate("main"));
        backButton.getStyle().set("color", "white");

        H2 title = new H2("Gestión de Entrenadores");
        title.getStyle().set("margin", "0 0 0 10px").set("font-size", "22px");

        header.add(backButton, title);
        return header;
    }

    private HorizontalLayout createToolbar() {
        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.setWidthFull();
        toolbar.setPadding(true);
        toolbar.setSpacing(true);
        toolbar.getStyle().set("background", "white");

        searchField = new TextField();
        searchField.setPlaceholder("Buscar entrenador...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setWidth("300px");
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> updateGrid());

        Button addButton = new Button("Nuevo Entrenador", new Icon(VaadinIcon.PLUS));
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> openCoachDialog(null));

        Button refreshButton = new Button(new Icon(VaadinIcon.REFRESH));
        refreshButton.addClickListener(e -> {
            searchField.clear();
            updateGrid();
        });

        toolbar.add(searchField, addButton, refreshButton);
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);

        return toolbar;
    }

    private Grid<CoachDTO> createGrid() {
        grid = new Grid<>(CoachDTO.class, false);
        grid.setSizeFull();

        grid.addColumn(CoachDTO::getIdCoach)
                .setHeader("ID")
                .setWidth("80px")
                .setFlexGrow(0);

        grid.addColumn(CoachDTO::getNameCoach)
                .setHeader("Nombre")
                .setFlexGrow(1);

        grid.addColumn(CoachDTO::getLastName)
                .setHeader("Apellido")
                .setFlexGrow(1);

        grid.addColumn(CoachDTO::getAge)
                .setHeader("Edad")
                .setWidth("100px")
                .setFlexGrow(0);

        grid.addColumn(coach -> coach.getCountryName() != null ? coach.getCountryName() : "Sin país")
                .setHeader("País")
                .setWidth("150px");

        grid.addColumn(coach -> coach.getClubName() != null ? coach.getClubName() : "Sin club")
                .setHeader("Club")
                .setWidth("200px");

        grid.addComponentColumn(coach -> {
            Button editButton = new Button(new Icon(VaadinIcon.EDIT));
            editButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
            editButton.addClickListener(e -> openCoachDialog(coach));

            Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            deleteButton.addClickListener(e -> confirmDelete(coach));

            HorizontalLayout actions = new HorizontalLayout(editButton, deleteButton);
            actions.setSpacing(true);
            return actions;
        }).setHeader("Acciones").setWidth("150px").setFlexGrow(0);

        return grid;
    }

    private void updateGrid() {
        try {
            String searchTerm = searchField.getValue();
            if (searchTerm != null && !searchTerm.isEmpty()) {
                grid.setItems(coachService.searchCoaches(searchTerm));
            } else {
                grid.setItems(coachService.getAllCoaches());
            }
        } catch (Exception e) {
            showErrorNotification("Error al cargar los entrenadores: " + e.getMessage());
        }
    }

    private void openCoachDialog(CoachDTO coach) {
        Dialog dialog = new Dialog();
        dialog.setWidth("500px");

        H2 dialogTitle = new H2(coach == null ? "Nuevo Entrenador" : "Editar Entrenador");

        TextField nameField = new TextField("Nombre *");
        nameField.setWidthFull();
        nameField.setRequired(true);

        TextField lastNameField = new TextField("Apellido *");
        lastNameField.setWidthFull();
        lastNameField.setRequired(true);

        IntegerField ageField = new IntegerField("Edad *");
        ageField.setWidthFull();
        ageField.setMin(18);
        ageField.setMax(100);
        ageField.setRequired(true);

        Select<CountryDTO> countrySelect = new Select<>();
        countrySelect.setLabel("País");
        countrySelect.setWidthFull();
        countrySelect.setItemLabelGenerator(CountryDTO::getNameCountry);
        countrySelect.setPlaceholder("Seleccionar país (opcional)");
        List<CountryDTO> countries = countryService.getAllCountries();
        countrySelect.setItems(countries);

        if (coach != null) {
            nameField.setValue(coach.getNameCoach());
            lastNameField.setValue(coach.getLastName());
            ageField.setValue(coach.getAge());

            if (coach.getCountryId() != null) {
                countrySelect.setValue(countries.stream()
                        .filter(c -> c.getIdCountry().equals(coach.getCountryId()))
                        .findFirst().orElse(null));
            }
        }

        Button saveButton = new Button("Guardar", e -> {
            try {
                if (nameField.isEmpty() || lastNameField.isEmpty() || ageField.isEmpty()) {
                    showErrorNotification("Completa los campos obligatorios");
                    return;
                }

                Long countryId = countrySelect.getValue() != null ? countrySelect.getValue().getIdCountry() : null;

                if (coach == null) {
                    coachService.createCoach(
                            nameField.getValue(),
                            lastNameField.getValue(),
                            ageField.getValue(),
                            countryId
                    );
                    showSuccessNotification("Entrenador creado exitosamente");
                } else {
                    coachService.updateCoach(
                            coach.getIdCoach(),
                            nameField.getValue(),
                            lastNameField.getValue(),
                            ageField.getValue(),
                            countryId
                    );
                    showSuccessNotification("Entrenador actualizado exitosamente");
                }
                updateGrid();
                dialog.close();
            } catch (Exception ex) {
                showErrorNotification("Error: " + ex.getMessage());
            }
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancelar", e -> dialog.close());

        HorizontalLayout buttons = new HorizontalLayout(saveButton, cancelButton);
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttons.setWidthFull();

        VerticalLayout dialogLayout = new VerticalLayout(dialogTitle, nameField, lastNameField, ageField, countrySelect, buttons);
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(true);

        dialog.add(dialogLayout);
        dialog.open();
    }

    private void confirmDelete(CoachDTO coach) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setWidth("400px");

        H2 title = new H2("Confirmar eliminación");

        VerticalLayout message = new VerticalLayout();
        message.add("¿Eliminar al entrenador?");
        message.add("Nombre: " + coach.getNameCoach() + " " + coach.getLastName());
        if (coach.getClubName() != null) {
            message.add("⚠️ Este entrenador está asignado al club: " + coach.getClubName());
        }

        Button confirmButton = new Button("Eliminar", e -> {
            try {
                coachService.deleteCoach(coach.getIdCoach());
                showSuccessNotification("Entrenador eliminado exitosamente");
                updateGrid();
                confirmDialog.close();
            } catch (Exception ex) {
                showErrorNotification("Error al eliminar: " + ex.getMessage());
            }
        });
        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);

        Button cancelButton = new Button("Cancelar", e -> confirmDialog.close());

        HorizontalLayout buttons = new HorizontalLayout(confirmButton, cancelButton);
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttons.setPadding(true);

        VerticalLayout dialogLayout = new VerticalLayout(title, message, buttons);
        dialogLayout.setPadding(true);

        confirmDialog.add(dialogLayout);
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
}