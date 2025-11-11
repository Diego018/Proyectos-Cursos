package com.jpa.view;

import com.jpa.dto.ClubDTO;
import com.jpa.dto.CountryDTO;
import com.jpa.dto.PlayerDTO;
import com.jpa.dto.PositionDTO;
import com.jpa.service.ClubService;
import com.jpa.service.CountryService;
import com.jpa.service.PlayerService;
import com.jpa.service.PositionService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route("jugadores")
public class PlayerView extends VerticalLayout {

    private final PlayerService playerService;
    private final ClubService clubService;
    private final PositionService positionService;
    private final CountryService countryService;

    private final Grid<PlayerDTO> grid = new Grid<>(PlayerDTO.class, false);
    private List<PlayerDTO> allPlayers;

    private final TextField searchField = new TextField();

    @Autowired
    public PlayerView(PlayerService playerService,
                      ClubService clubService,
                      PositionService positionService,
                      CountryService countryService) {
        this.playerService = playerService;
        this.clubService = clubService;
        this.positionService = positionService;
        this.countryService = countryService;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H2 title = new H2("Gestión de Jugadores");
        add(title);

        add(createToolbar());
        configureGrid();
        add(grid);

        loadPlayers();
    }

    private HorizontalLayout createToolbar() {
        searchField.setPlaceholder("Buscar jugador...");
        searchField.setValueChangeMode(ValueChangeMode.LAZY);
        searchField.addValueChangeListener(e -> filterPlayers());
        searchField.setWidth("300px");

        Button addButton = new Button("Nuevo Jugador");
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addButton.addClickListener(e -> openPlayerDialog(null));

        Button refreshButton = new Button("Actualizar");
        refreshButton.addClickListener(e -> {
            loadPlayers();
            Notification.show("Lista actualizada").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        });

        Button backButton = new Button("Volver");
        backButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("main")));

        HorizontalLayout toolbar = new HorizontalLayout(searchField, addButton, refreshButton, backButton);
        toolbar.setWidthFull();
        toolbar.setAlignItems(Alignment.CENTER);

        return toolbar;
    }

    private void configureGrid() {
        grid.setWidth("100%");
        grid.setHeight("600px");

        grid.addColumn(PlayerDTO::getIdPlayer).setHeader("ID").setWidth("80px");
        grid.addColumn(PlayerDTO::getNamePlayer).setHeader("Nombre");
        grid.addColumn(PlayerDTO::getLastName).setHeader("Apellido");
        grid.addColumn(PlayerDTO::getAge).setHeader("Edad").setWidth("100px");
        grid.addColumn(PlayerDTO::getClubName).setHeader("Club");
        grid.addColumn(PlayerDTO::getPositionName).setHeader("Posición").setWidth("150px");
        grid.addColumn(PlayerDTO::getCountryName).setHeader("País").setWidth("150px");

        grid.addComponentColumn(player -> {
            Button editButton = new Button("Editar");
            editButton.addThemeVariants(ButtonVariant.LUMO_SMALL);
            editButton.addClickListener(e -> openPlayerDialog(player));

            Button deleteButton = new Button("Eliminar");
            deleteButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            deleteButton.addClickListener(e -> confirmDelete(player));

            HorizontalLayout actions = new HorizontalLayout(editButton, deleteButton);
            return actions;
        }).setHeader("Acciones").setWidth("200px");
    }

    private void loadPlayers() {
        try {
            allPlayers = playerService.getAllPlayers();
            grid.setItems(allPlayers);
            searchField.clear();
        } catch (Exception e) {
            Notification.show("Error al cargar jugadores: " + e.getMessage(), 3000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void filterPlayers() {
        String searchTerm = searchField.getValue().toLowerCase().trim();

        if (searchTerm.isEmpty()) {
            grid.setItems(allPlayers);
            return;
        }

        List<PlayerDTO> filtered = allPlayers.stream()
                .filter(player ->
                        player.getNamePlayer().toLowerCase().contains(searchTerm) ||
                                player.getLastName().toLowerCase().contains(searchTerm) ||
                                (player.getClubName() != null && player.getClubName().toLowerCase().contains(searchTerm))
                )
                .toList();

        grid.setItems(filtered);
    }

    private void openPlayerDialog(PlayerDTO player) {
        Dialog dialog = new Dialog();
        dialog.setWidth("600px");

        H2 dialogTitle = new H2(player == null ? "Nuevo Jugador" : "Editar Jugador");

        TextField nameField = new TextField("Nombre");
        nameField.setWidthFull();
        nameField.setRequired(true);

        TextField lastNameField = new TextField("Apellido");
        lastNameField.setWidthFull();
        lastNameField.setRequired(true);

        IntegerField ageField = new IntegerField("Edad");
        ageField.setWidthFull();
        ageField.setMin(15);
        ageField.setMax(50);
        ageField.setRequired(true);

        Select<ClubDTO> clubSelect = new Select<>();
        clubSelect.setLabel("Club");
        clubSelect.setWidthFull();
        clubSelect.setItemLabelGenerator(ClubDTO::getNameClub);
        clubSelect.setPlaceholder("Seleccionar club");
        List<ClubDTO> clubs = clubService.getAllClubs();
        clubSelect.setItems(clubs);

        Select<PositionDTO> positionSelect = new Select<>();
        positionSelect.setLabel("Posición *");
        positionSelect.setWidthFull();
        positionSelect.setItemLabelGenerator(PositionDTO::getDescPosition);
        positionSelect.setPlaceholder("Seleccionar posición");
        List<PositionDTO> positions = positionService.getAllPositions();
        positionSelect.setItems(positions);

        Select<CountryDTO> countrySelect = new Select<>();
        countrySelect.setLabel("País *");
        countrySelect.setWidthFull();
        countrySelect.setItemLabelGenerator(CountryDTO::getNameCountry);
        countrySelect.setPlaceholder("Seleccionar país");
        List<CountryDTO> countries = countryService.getAllCountries();
        countrySelect.setItems(countries);

        if (player != null) {
            nameField.setValue(player.getNamePlayer());
            lastNameField.setValue(player.getLastName());
            ageField.setValue(player.getAge());

            if (player.getClubId() != null) {
                clubSelect.setValue(clubs.stream()
                        .filter(c -> c.getIdClub().equals(player.getClubId()))
                        .findFirst().orElse(null));
            }

            if (player.getPositionId() != null) {
                positionSelect.setValue(positions.stream()
                        .filter(p -> p.getIdPosition().equals(player.getPositionId()))
                        .findFirst().orElse(null));
            }

            if (player.getCountryId() != null) {
                countrySelect.setValue(countries.stream()
                        .filter(c -> c.getIdCountry().equals(player.getCountryId()))
                        .findFirst().orElse(null));
            }
        }

        FormLayout formLayout = new FormLayout();
        formLayout.add(nameField, lastNameField, ageField, clubSelect, positionSelect, countrySelect);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );

        Button saveButton = new Button("Guardar");
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> {
            try {
                if (nameField.isEmpty() || lastNameField.isEmpty() || ageField.isEmpty() ||
                        positionSelect.isEmpty() || countrySelect.isEmpty()) {
                    Notification.show("Completa los campos obligatorios", 3000, Notification.Position.BOTTOM_CENTER)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                    return;
                }

                Long clubId = clubSelect.getValue() != null ? clubSelect.getValue().getIdClub() : null;
                Long positionId = positionSelect.getValue().getIdPosition();
                Long countryId = countrySelect.getValue().getIdCountry();

                if (player == null) {
                    playerService.createPlayer(
                            nameField.getValue(),
                            lastNameField.getValue(),
                            ageField.getValue(),
                            clubId,
                            positionId,
                            countryId
                    );
                    Notification.show("Jugador creado", 3000, Notification.Position.BOTTOM_CENTER)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                } else {
                    playerService.updatePlayer(
                            player.getIdPlayer(),
                            nameField.getValue(),
                            lastNameField.getValue(),
                            ageField.getValue(),
                            clubId,
                            positionId,
                            countryId
                    );
                    Notification.show("Jugador actualizado", 3000, Notification.Position.BOTTOM_CENTER)
                            .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                }

                loadPlayers();
                dialog.close();
            } catch (Exception ex) {
                Notification.show(ex.getMessage(), 3000, Notification.Position.BOTTOM_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        Button cancelButton = new Button("Cancelar");
        cancelButton.addClickListener(e -> dialog.close());

        HorizontalLayout buttons = new HorizontalLayout(saveButton, cancelButton);

        VerticalLayout dialogLayout = new VerticalLayout(dialogTitle, formLayout, buttons);
        dialogLayout.setPadding(true);

        dialog.add(dialogLayout);
        dialog.open();
    }

    private void confirmDelete(PlayerDTO player) {
        Dialog confirmDialog = new Dialog();
        confirmDialog.setWidth("400px");

        H2 title = new H2("Confirmar eliminación");

        VerticalLayout message = new VerticalLayout();
        message.add("¿Eliminar al jugador?");
        message.add("Nombre: " + player.getNamePlayer() + " " + player.getLastName());
        if (player.getClubName() != null) {
            message.add("Club: " + player.getClubName());
        }

        Button confirmButton = new Button("Eliminar");
        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        confirmButton.addClickListener(e -> {
            try {
                playerService.deletePlayer(player.getIdPlayer());
                Notification.show("Jugador eliminado", 3000, Notification.Position.BOTTOM_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                loadPlayers();
                confirmDialog.close();
            } catch (Exception ex) {
                Notification.show("Error: " + ex.getMessage(), 3000, Notification.Position.BOTTOM_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        Button cancelButton = new Button("Cancelar");
        cancelButton.addClickListener(e -> confirmDialog.close());

        HorizontalLayout buttons = new HorizontalLayout(confirmButton, cancelButton);

        VerticalLayout dialogLayout = new VerticalLayout(title, message, buttons);
        dialogLayout.setPadding(true);

        confirmDialog.add(dialogLayout);
        confirmDialog.open();
    }
}