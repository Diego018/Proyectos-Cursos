package com.jpa.view;

import com.jpa.dto.FootballCompetitionDTO;
import com.jpa.dto.UserDTO;
import com.jpa.service.FootballCompetitionService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Route("competiciones")
public class FootballCompetitionView extends VerticalLayout implements BeforeEnterObserver {

    private final FootballCompetitionService competitionService;

    private Grid<FootballCompetitionDTO> grid;
    private TextField searchField;
    private Button addButton;

    public FootballCompetitionView(FootballCompetitionService competitionService) {
        this.competitionService = competitionService;

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
        H2 title = new H2("Gestión de Competiciones de Fútbol");

        searchField = new TextField();
        searchField.setPlaceholder("Buscar competición...");
        searchField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> updateGrid());
        searchField.setWidth("300px");

        addButton = new Button("Nueva Competición", VaadinIcon.PLUS.create());
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> openCompetitionDialog(null));

        HorizontalLayout toolbar = new HorizontalLayout(searchField, addButton);
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);

        add(title, toolbar);
    }

    private void configureGrid() {
        grid = new Grid<>(FootballCompetitionDTO.class, false);
        grid.setSizeFull();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        grid.addColumn(FootballCompetitionDTO::getIdCompetition)
                .setHeader("ID")
                .setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(FootballCompetitionDTO::getName)
                .setHeader("Nombre")
                .setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(competition -> competition.getCuantityPrice() != null ?
                        "$" + competition.getCuantityPrice() : "Sin premio")
                .setHeader("Premio")
                .setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(competition -> competition.getStartDate() != null ?
                        competition.getStartDate().format(formatter) : "Sin fecha")
                .setHeader("Fecha Inicio")
                .setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(competition -> competition.getEndDate() != null ?
                        competition.getEndDate().format(formatter) : "Sin fecha")
                .setHeader("Fecha Fin")
                .setAutoWidth(true)
                .setSortable(true);

        grid.addColumn(FootballCompetitionDTO::getTotalClubs)
                .setHeader("Clubes")
                .setAutoWidth(true)
                .setSortable(true);

        grid.addComponentColumn(competition -> {
            Button editButton = new Button(VaadinIcon.EDIT.create());
            editButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
            editButton.addClickListener(e -> openCompetitionDialog(competition));

            Button deleteButton = new Button(VaadinIcon.TRASH.create());
            deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            deleteButton.addClickListener(e -> confirmDelete(competition));

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
                grid.setItems(competitionService.searchCompetitions(searchTerm));
            } else {
                grid.setItems(competitionService.getAllCompetitions());
            }
        } catch (Exception e) {
            showErrorNotification("Error al cargar las competiciones: " + e.getMessage());
        }
    }

    private void openCompetitionDialog(FootballCompetitionDTO competition) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(competition == null ? "Nueva Competición" : "Editar Competición");
        dialog.setWidth("500px");

        CompetitionForm form = new CompetitionForm(competition);

        Button saveButton = new Button("Guardar", e -> {
            try {
                if (competition == null) {
                    competitionService.createCompetition(
                            form.getName(),
                            form.getCuantityPrice(),
                            form.getStartDate(),
                            form.getEndDate()
                    );
                    showSuccessNotification("Competición creada exitosamente");
                } else {
                    competitionService.updateCompetition(
                            competition.getIdCompetition(),
                            form.getName(),
                            form.getCuantityPrice(),
                            form.getStartDate(),
                            form.getEndDate()
                    );
                    showSuccessNotification("Competición actualizada exitosamente");
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

    private void confirmDelete(FootballCompetitionDTO competition) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setHeaderTitle("Confirmar eliminación");

        VerticalLayout content = new VerticalLayout();
        content.add("¿Está seguro que desea eliminar la competición: " + competition.getName() + "?");
        content.add("Esta acción no se puede deshacer.");

        Button confirmButton = new Button("Eliminar", e -> {
            try {
                competitionService.deleteCompetition(competition.getIdCompetition());
                showSuccessNotification("Competición eliminada exitosamente");
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

    private class CompetitionForm extends VerticalLayout {
        private TextField nameField;
        private IntegerField priceField;
        private DatePicker startDatePicker;
        private DatePicker endDatePicker;

        public CompetitionForm(FootballCompetitionDTO competition) {
            setSpacing(true);
            setPadding(false);

            nameField = new TextField("Nombre de la Competición");
            nameField.setWidthFull();
            nameField.setRequired(true);

            priceField = new IntegerField("Premio en Dinero");
            priceField.setWidthFull();
            priceField.setHelperText("Opcional");
            priceField.setMin(0);

            startDatePicker = new DatePicker("Fecha de Inicio");
            startDatePicker.setWidthFull();
            startDatePicker.setLocale(new Locale("es", "ES"));

            // Validación para no permitir fechas anteriores a hoy
            startDatePicker.setMin(java.time.LocalDate.now());

            endDatePicker = new DatePicker("Fecha de Fin");
            endDatePicker.setWidthFull();
            endDatePicker.setLocale(new Locale("es", "ES"));

            // Validación para no permitir fechas anteriores a hoy
            endDatePicker.setMin(java.time.LocalDate.now());

            // Validación adicional: la fecha fin no puede ser anterior a la fecha inicio
            startDatePicker.addValueChangeListener(e -> {
                if (e.getValue() != null) {
                    endDatePicker.setMin(e.getValue());
                } else {
                    endDatePicker.setMin(java.time.LocalDate.now());
                }
            });

            if (competition != null) {
                nameField.setValue(competition.getName());
                if (competition.getCuantityPrice() != null) {
                    priceField.setValue(competition.getCuantityPrice());
                }
                if (competition.getStartDate() != null) {
                    startDatePicker.setValue(competition.getStartDate());
                }
                if (competition.getEndDate() != null) {
                    endDatePicker.setValue(competition.getEndDate());
                }
            }

            add(nameField, priceField, startDatePicker, endDatePicker);
        }

        public String getName() {
            return nameField.getValue();
        }

        public Integer getCuantityPrice() {
            return priceField.getValue();
        }

        public java.time.LocalDate getStartDate() {
            return startDatePicker.getValue();
        }

        public java.time.LocalDate getEndDate() {
            return endDatePicker.getValue();
        }
    }
}