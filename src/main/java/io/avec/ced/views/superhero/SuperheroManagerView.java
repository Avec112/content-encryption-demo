package io.avec.ced.views.superhero;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import io.avec.ced.data.entity.Manager;
import io.avec.ced.data.entity.SuperheroManager;
import io.avec.ced.data.service.SuperheroManagerService;
import io.avec.ced.security.AuthenticatedUser;
import io.avec.ced.views.MainLayout;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;

import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@PageTitle("Superhero ACL")
@Route(value = "superheroacl", layout = MainLayout.class)
@RolesAllowed("manager")
public class SuperheroManagerView extends VerticalLayout {

    @Value("${toggle.superhero.acl.authenticationRequired}")
    private boolean toggleAuthenticationRequired;

    private final Grid<SuperheroManager> grid = new Grid<>(SuperheroManager.class, false);
    private final SuperheroManagerService service;
    private final AuthenticatedUser authenticatedUser;


    @PostConstruct
    private void init() {
        setSizeFull();
        add(grid);

        final Optional<Manager> maybeManager = authenticatedUser.get();
        maybeManager.ifPresentOrElse(manager -> {
            grid.setItems(query -> service.listByManager(manager.getId(), PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                    .stream());

            grid.addColumn("id").setHeader("Id");
            grid.addColumn("superhero.nickname").setHeader("Nickname");
            grid.addColumn("manager.name").setHeader("Manager with access");

        }, () -> Notification.show("You do not have access to any Superhero"));

        grid.addItemClickListener(event -> {

            if (toggleAuthenticationRequired) {
                Dialog dialog = new AuthenticationDialog(authenticatedUser, this::displayManagerSelection);
                dialog.open();
            } else {
                displayManagerSelection();
            }

        });
    }

    private void displayManagerSelection() {
        VerticalLayout verticalLayout = new VerticalLayout();
        Dialog dialog = new Dialog(verticalLayout);

        verticalLayout.add(new Span("Not yet implemented"));

        /* todo
            - Display Managers
            - show managers with access
            - user may give or remove access
         */

        dialog.open();
    }
}
