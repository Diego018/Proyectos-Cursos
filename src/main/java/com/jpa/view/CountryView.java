package com.jpa.view;

import com.jpa.dto.CountryDTO;
import com.jpa.dto.UserDTO;
import com.jpa.service.CountryService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
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

import java.util.ArrayList;
import java.util.List;

@Route("paises")
public class CountryView extends VerticalLayout implements BeforeEnterObserver {

    private final CountryService countryService;

    private Grid<CountryDTO> grid;
    private TextField searchField;
    private List<CountryDTO> allCountries = new ArrayList<>();

    public CountryView(CountryService countryService) {
        this.countryService = countryService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        add(createHeader());
        add(createToolbar());
        add(createGrid());

        loadCountries();
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

        H2 title = new H2("Gestión de Países");
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
        searchField.setPlaceholder("Buscar país...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setWidth("300px");
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> filterCountries(e.getValue()));

        Button addButton = new Button("Nuevo País", new Icon(VaadinIcon.PLUS));
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> openCountryDialog(null));

        Button refreshButton = new Button(new Icon(VaadinIcon.REFRESH));
        refreshButton.addClickListener(e -> loadCountries());

        toolbar.add(searchField, addButton, refreshButton);
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);

        return toolbar;
    }

    private Grid<CountryDTO> createGrid() {
        grid = new Grid<>(CountryDTO.class, false);
        grid.setSizeFull();

        grid.addColumn(CountryDTO::getIdCountry)
                .setHeader("ID")
                .setWidth("80px")
                .setFlexGrow(0);

        grid.addColumn(CountryDTO::getNameCountry)
                .setHeader("Nombre del País")
                .setFlexGrow(1);

        grid.addColumn(CountryDTO::getTotalAssociations)
                .setHeader("Asociaciones")
                .setWidth("130px")
                .setFlexGrow(0);

        grid.addColumn(CountryDTO::getTotalPlayers)
                .setHeader("Jugadores")
                .setWidth("120px")
                .setFlexGrow(0);

        grid.addColumn(CountryDTO::getTotalCoaches)
                .setHeader("Entrenadores")
                .setWidth("130px")
                .setFlexGrow(0);

        grid.addComponentColumn(country -> {
            Button editButton = new Button(new Icon(VaadinIcon.EDIT));
            editButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
            editButton.addClickListener(e -> openCountryDialog(country));

            Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            deleteButton.addClickListener(e -> confirmDelete(country));

            HorizontalLayout actions = new HorizontalLayout(editButton, deleteButton);
            actions.setSpacing(true);
            return actions;
        }).setHeader("Acciones").setWidth("150px").setFlexGrow(0);

        return grid;
    }

    private void loadCountries() {
        try {
            allCountries = countryService.getAllCountries();
            grid.setItems(allCountries);
            searchField.clear();
        } catch (Exception e) {
            showNotification("Error al cargar países: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void filterCountries(String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            grid.setItems(allCountries);
        } else {
            List<CountryDTO> filtered = allCountries.stream()
                    .filter(c -> c.getNameCountry().toLowerCase().contains(searchTerm.toLowerCase()))
                    .toList();
            grid.setItems(filtered);
        }
    }

    private void openCountryDialog(CountryDTO country) {
        Dialog dialog = new Dialog();
        dialog.setWidth("400px");

        H2 title = new H2(country == null ? "Nuevo País" : "Editar País");

        TextField nameField = new TextField("Nombre del País");
        nameField.setWidthFull();
        nameField.setRequired(true);

        if (country != null) {
            nameField.setValue(country.getNameCountry());
        }

        Button saveButton = new Button("Guardar", e -> {
            if (nameField.isEmpty()) {
                showNotification("El nombre del país es obligatorio", NotificationVariant.LUMO_ERROR);
                return;
            }

            saveCountry(country, nameField.getValue());
            dialog.close();
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancelar", e -> dialog.close());

        HorizontalLayout buttons = new HorizontalLayout(saveButton, cancelButton);
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttons.setWidthFull();

        VerticalLayout layout = new VerticalLayout(title, nameField, buttons);
        layout.setPadding(true);
        layout.setSpacing(true);

        dialog.add(layout);
        dialog.open();
    }

    private void saveCountry(CountryDTO country, String name) {
        try {
            if (country == null) {
                countryService.createCountry(name);
                showNotification("País creado exitosamente", NotificationVariant.LUMO_SUCCESS);
            } else {
                countryService.updateCountry(country.getIdCountry(), name);
                showNotification("País actualizado exitosamente", NotificationVariant.LUMO_SUCCESS);
            }
            loadCountries();
        } catch (Exception e) {
            showNotification("Error al guardar: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void confirmDelete(CountryDTO country) {
        Dialog dialog = new Dialog();

        Span message = new Span("¿Está seguro que desea eliminar el país '" + country.getNameCountry() + "'?");

        if (country.getTotalAssociations() > 0 || country.getTotalPlayers() > 0 || country.getTotalCoaches() > 0) {
            Span warning = new Span("⚠️ Este país tiene registros asociados.");
            warning.getStyle().set("color", "red").set("font-weight", "bold");

            VerticalLayout content = new VerticalLayout(message, warning);
            content.setPadding(false);

            dialog.add(content);
        } else {
            dialog.add(message);
        }

        Button confirmButton = new Button("Eliminar", e -> {
            deleteCountry(country.getIdCountry());
            dialog.close();
        });
        confirmButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancelar", e -> dialog.close());

        HorizontalLayout buttons = new HorizontalLayout(confirmButton, cancelButton);
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttons.setPadding(true);

        dialog.add(buttons);
        dialog.open();
    }

    private void deleteCountry(Long id) {
        try {
            countryService.deleteCountry(id);
            showNotification("País eliminado exitosamente", NotificationVariant.LUMO_SUCCESS);
            loadCountries();
        } catch (Exception e) {
            showNotification("Error al eliminar: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void showNotification(String message, NotificationVariant variant) {
        Notification notification = new Notification(message, 3000);
        notification.addThemeVariants(variant);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.open();
    }
}