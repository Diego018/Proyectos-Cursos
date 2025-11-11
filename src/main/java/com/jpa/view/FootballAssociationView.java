package com.jpa.view;

import com.jpa.dto.CountryDTO;
import com.jpa.dto.FootballAssociationDTO;
import com.jpa.dto.UserDTO;
import com.jpa.service.CountryService;
import com.jpa.service.FootballAssociationService;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.util.List;

@Route("asociaciones")
public class FootballAssociationView extends VerticalLayout implements BeforeEnterObserver {

    private final FootballAssociationService associationService;
    private final CountryService countryService;

    private Grid<FootballAssociationDTO> grid;
    private TextField searchField;

    public FootballAssociationView(FootballAssociationService associationService,
                                   CountryService countryService) {
        this.associationService = associationService;
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

        H2 title = new H2("Gestión de Asociaciones de Fútbol");
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
        searchField.setPlaceholder("Buscar asociación...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setWidth("300px");
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> updateGrid());

        Button addButton = new Button("Nueva Asociación", new Icon(VaadinIcon.PLUS));
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> openAssociationDialog(null));

        Button refreshButton = new Button(new Icon(VaadinIcon.REFRESH));
        refreshButton.addClickListener(e -> {
            searchField.clear();
            updateGrid();
        });

        toolbar.add(searchField, addButton, refreshButton);
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);

        return toolbar;
    }

    private Grid<FootballAssociationDTO> createGrid() {
        grid = new Grid<>(FootballAssociationDTO.class, false);
        grid.setSizeFull();

        grid.addColumn(FootballAssociationDTO::getIdAssociation)
                .setHeader("ID")
                .setWidth("80px")
                .setFlexGrow(0);

        grid.addColumn(FootballAssociationDTO::getNameAssociation)
                .setHeader("Nombre")
                .setFlexGrow(1);

        grid.addColumn(association -> association.getPresident() != null ? association.getPresident() : "Sin presidente")
                .setHeader("Presidente")
                .setFlexGrow(1);

        grid.addColumn(association -> association.getCountryName() != null ? association.getCountryName() : "Sin país")
                .setHeader("País")
                .setWidth("150px");

        grid.addColumn(FootballAssociationDTO::getTotalClubs)
                .setHeader("Clubes")
                .setWidth("100px")
                .setFlexGrow(0);

        grid.addComponentColumn(association -> {
            Button editButton = new Button(new Icon(VaadinIcon.EDIT));
            editButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
            editButton.addClickListener(e -> openAssociationDialog(association));

            Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
            deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            deleteButton.addClickListener(e -> confirmDelete(association));

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
                grid.setItems(associationService.searchAssociations(searchTerm));
            } else {
                grid.setItems(associationService.getAllAssociations());
            }
        } catch (Exception e) {
            showErrorNotification("Error al cargar las asociaciones: " + e.getMessage());
        }
    }

    private void openAssociationDialog(FootballAssociationDTO association) {
        Dialog dialog = new Dialog();
        dialog.setWidth("500px");

        H2 dialogTitle = new H2(association == null ? "Nueva Asociación" : "Editar Asociación");

        TextField nameField = new TextField("Nombre de la Asociación *");
        nameField.setWidthFull();
        nameField.setRequired(true);

        TextField presidentField = new TextField("Presidente");
        presidentField.setWidthFull();
        presidentField.setPlaceholder("Opcional");

        Select<CountryDTO> countrySelect = new Select<>();
        countrySelect.setLabel("País *");
        countrySelect.setWidthFull();
        countrySelect.setItemLabelGenerator(CountryDTO::getNameCountry);
        countrySelect.setPlaceholder("Seleccionar país");
        List<CountryDTO> countries = countryService.getAllCountries();
        countrySelect.setItems(countries);

        if (association != null) {
            nameField.setValue(association.getNameAssociation());
            if (association.getPresident() != null) {
                presidentField.setValue(association.getPresident());
            }
            if (association.getCountryId() != null) {
                countrySelect.setValue(countries.stream()
                        .filter(c -> c.getIdCountry().equals(association.getCountryId()))
                        .findFirst().orElse(null));
            }
        }

        Button saveButton = new Button("Guardar", e -> {
            try {
                if (nameField.isEmpty() || countrySelect.isEmpty()) {
                    showErrorNotification("Completa los campos obligatorios");
                    return;
                }

                String president = presidentField.getValue() != null && !presidentField.getValue().isEmpty()
                        ? presidentField.getValue() : null;
                Long countryId = countrySelect.getValue().getIdCountry();

                if (association == null) {
                    associationService.createAssociation(
                            nameField.getValue(),
                            president,
                            countryId
                    );
                    showSuccessNotification("Asociación creada exitosamente");
                } else {
                    associationService.updateAssociation(
                            association.getIdAssociation(),
                            nameField.getValue(),
                            president,
                            countryId
                    );
                    showSuccessNotification("Asociación actualizada exitosamente");
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

        VerticalLayout dialogLayout = new VerticalLayout(dialogTitle, nameField, presidentField, countrySelect, buttons);
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(true);

        dialog.add(dialogLayout);
        dialog.open();
    }

    private void confirmDelete(FootballAssociationDTO association) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setWidth("400px");

        H2 title = new H2("Confirmar eliminación");

        VerticalLayout message = new VerticalLayout();
        message.add("¿Eliminar la asociación?");
        message.add("Nombre: " + association.getNameAssociation());

        if (association.getTotalClubs() > 0) {
            message.add("⚠️ Esta asociación tiene " + association.getTotalClubs() + " club(es) asociado(s).");
        }

        Button confirmButton = new Button("Eliminar", e -> {
            try {
                associationService.deleteAssociation(association.getIdAssociation());
                showSuccessNotification("Asociación eliminada exitosamente");
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