package com.jpa.view;

import com.jpa.dto.UserDTO;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@Route("main")
public class MainView extends VerticalLayout implements BeforeEnterObserver {

    private UserDTO currentUser;

    public MainView() {
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        currentUser = VaadinSession.getCurrent().getAttribute(UserDTO.class);

        if (currentUser != null) {
            add(createHeader());
            add(createContent());
        }
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

        H2 title = new H2("Sistema de Gestión de Fútbol");
        title.getStyle().set("margin", "0").set("font-size", "22px");

        Span user = new Span(currentUser.getFirstName() + " " + currentUser.getLastName());
        user.getStyle().set("margin-left", "auto");

        Button logout = new Button("Salir");
        logout.addClickListener(e -> {
            VaadinSession.getCurrent().setAttribute(UserDTO.class, null);
            UI.getCurrent().navigate("");
        });

        header.add(title, user, logout);
        return header;
    }

    private VerticalLayout createContent() {
        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setPadding(true);
        content.getStyle().set("background", "#ecf0f1");

        H2 subtitle = new H2("Módulos del Sistema");
        subtitle.getStyle().set("margin-bottom", "30px");

        VerticalLayout modules = new VerticalLayout();
        modules.setWidth("800px");
        modules.setSpacing(true);

        modules.add(
                createModuleRow("Usuarios", "users", "Administrar usuarios del sistema"),
                createModuleRow("Países", "paises", "Gestionar países y nacionalidades"),
                createModuleRow("Posiciones", "posiciones", "Administrar posiciones de juego"),
                createModuleRow("Clubes", "clubes", "Gestionar clubes de fútbol"),
                createModuleRow("Jugadores", "jugadores", "Administrar jugadores"),
                createModuleRow("Entrenadores", "entrenadores", "Gestionar cuerpo técnico"),
                createModuleRow("Competiciones", "competiciones", "Administrar torneos y ligas"),
                createModuleRow("Asociaciones", "asociaciones", "Gestionar asociaciones")
        );

        content.add(subtitle, modules);
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        return content;
    }

    private HorizontalLayout createModuleRow(String name, String route, String description) {
        HorizontalLayout row = new HorizontalLayout();
        row.setWidthFull();
        row.setPadding(true);
        row.setAlignItems(FlexComponent.Alignment.CENTER);
        row.getStyle()
                .set("background", "white")
                .set("border-radius", "4px")
                .set("cursor", "pointer")
                .set("border-left", "4px solid #3498db");

        VerticalLayout info = new VerticalLayout();
        info.setSpacing(false);
        info.setPadding(false);

        Span title = new Span(name);
        title.getStyle().set("font-weight", "bold").set("font-size", "16px");

        Span desc = new Span(description);
        desc.getStyle().set("font-size", "14px").set("color", "#7f8c8d");

        info.add(title, desc);

        Button accessButton = new Button("Acceder");
        accessButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
        accessButton.addClickListener(e -> UI.getCurrent().navigate(route));
        accessButton.getStyle().set("margin-left", "auto");

        row.add(info, accessButton);

        row.addClickListener(e -> UI.getCurrent().navigate(route));

        return row;
    }
}