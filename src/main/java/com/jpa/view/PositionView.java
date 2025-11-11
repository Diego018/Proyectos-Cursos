package com.jpa.view;

import com.jpa.dto.PositionDTO;
import com.jpa.dto.UserDTO;
import com.jpa.service.PositionService;
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

@Route("posiciones")
public class PositionView extends VerticalLayout implements BeforeEnterObserver {

    private final PositionService positionService;

    private Grid<PositionDTO> grid;
    private TextField searchField;
    private List<PositionDTO> allPositions = new ArrayList<>();

    public PositionView(PositionService positionService) {
        this.positionService = positionService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        add(createHeader());
        add(createToolbar());
        add(createGrid());

        loadPositions();
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

        H2 title = new H2("Gestión de Posiciones");
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
        searchField.setPlaceholder("Buscar posición...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setWidth("300px");
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> filterPositions(e.getValue()));

        Button addButton = new Button("Nueva Posición", new Icon(VaadinIcon.PLUS));
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> openPositionDialog(null));

        Button refreshButton = new Button(new Icon(VaadinIcon.REFRESH));
        refreshButton.addClickListener(e -> loadPositions());

        toolbar.add(searchField, addButton, refreshButton);
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);

        return toolbar;
    }

    private Grid<PositionDTO> createGrid() {
        grid = new Grid<>(PositionDTO.class, false);
        grid.setSizeFull();

        grid.addColumn(PositionDTO::getIdPosition)
                .setHeader("ID")
                .setWidth("80px")
                .setFlexGrow(0);

        grid.addColumn(PositionDTO::getDescPosition)
                .setHeader("Descripción de la Posición")
                .setFlexGrow(1);

        grid.addColumn(PositionDTO::getTotalPlayers)
                .setHeader("Total Jugadores")
                .setWidth("150px")
                .setFlexGrow(0);

        grid.addComponentColumn(position -> {
            Button editButton = new Button(new Icon(VaadinIcon.EDIT));
            editButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
            editButton.addClickListener(e -> openPositionDialog(position));

            Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            deleteButton.addClickListener(e -> confirmDelete(position));

            HorizontalLayout actions = new HorizontalLayout(editButton, deleteButton);
            actions.setSpacing(true);
            return actions;
        }).setHeader("Acciones").setWidth("150px").setFlexGrow(0);

        return grid;
    }

    private void loadPositions() {
        try {
            allPositions = positionService.getAllPositions();
            grid.setItems(allPositions);
            searchField.clear();
        } catch (Exception e) {
            showNotification("Error al cargar posiciones: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void filterPositions(String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            grid.setItems(allPositions);
        } else {
            List<PositionDTO> filtered = allPositions.stream()
                    .filter(p -> p.getDescPosition().toLowerCase().contains(searchTerm.toLowerCase()))
                    .toList();
            grid.setItems(filtered);
        }
    }

    private void openPositionDialog(PositionDTO position) {
        Dialog dialog = new Dialog();
        dialog.setWidth("400px");

        H2 title = new H2(position == null ? "Nueva Posición" : "Editar Posición");

        TextField descField = new TextField("Descripción de la Posición");
        descField.setWidthFull();
        descField.setRequired(true);
        descField.setPlaceholder("Ej: Delantero, Mediocampista, Defensa...");

        if (position != null) {
            descField.setValue(position.getDescPosition());
        }

        Button saveButton = new Button("Guardar", e -> {
            if (descField.isEmpty()) {
                showNotification("La descripción de la posición es obligatoria", NotificationVariant.LUMO_ERROR);
                return;
            }

            savePosition(position, descField.getValue());
            dialog.close();
        });
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancelar", e -> dialog.close());

        HorizontalLayout buttons = new HorizontalLayout(saveButton, cancelButton);
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttons.setWidthFull();

        VerticalLayout layout = new VerticalLayout(title, descField, buttons);
        layout.setPadding(true);
        layout.setSpacing(true);

        dialog.add(layout);
        dialog.open();
    }

    private void savePosition(PositionDTO position, String description) {
        try {
            if (position == null) {
                positionService.createPosition(description);
                showNotification("Posición creada exitosamente", NotificationVariant.LUMO_SUCCESS);
            } else {
                positionService.updatePosition(position.getIdPosition(), description);
                showNotification("Posición actualizada exitosamente", NotificationVariant.LUMO_SUCCESS);
            }
            loadPositions();
        } catch (Exception e) {
            showNotification("Error al guardar: " + e.getMessage(), NotificationVariant.LUMO_ERROR);
        }
    }

    private void confirmDelete(PositionDTO position) {
        Dialog dialog = new Dialog();

        Span message = new Span("¿Está seguro que desea eliminar la posición '" + position.getDescPosition() + "'?");

        if (position.getTotalPlayers() > 0) {
            Span warning = new Span("⚠️ Esta posición tiene " + position.getTotalPlayers() + " jugador(es) asociado(s).");
            warning.getStyle().set("color", "red").set("font-weight", "bold");

            VerticalLayout content = new VerticalLayout(message, warning);
            content.setPadding(false);

            dialog.add(content);
        } else {
            dialog.add(message);
        }

        Button confirmButton = new Button("Eliminar", e -> {
            deletePosition(position.getIdPosition());
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

    private void deletePosition(Long id) {
        try {
            positionService.deletePosition(id);
            showNotification("Posición eliminada exitosamente", NotificationVariant.LUMO_SUCCESS);
            loadPositions();
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