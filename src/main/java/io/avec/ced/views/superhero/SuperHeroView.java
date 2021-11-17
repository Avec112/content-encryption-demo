package io.avec.ced.views.superhero;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import io.avec.ced.data.dto.SuperheroDTO;
import io.avec.ced.data.entity.Manager;
import io.avec.ced.data.entity.Superhero;
import io.avec.ced.data.service.SuperheroManagerRepository;
import io.avec.ced.data.service.SuperheroService;
import io.avec.ced.security.AuthenticatedUser;
import io.avec.ced.views.MainLayout;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@PageTitle("Superhero")
@Route(value = "superhero", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
//@RolesAllowed("admin")
@AnonymousAllowed
@CssImport(value="./themes/myapp/views/dialog-styles.css", themeFor = "vaadin-dialog-overlay")
public class SuperHeroView extends VerticalLayout {

    @Value("${toggle.superhero.decrypt.authenticationRequired:true}")
    private boolean toggleAuthenticationRequired;

    @Value("${toggle.superhero.decrypt.confirmNobodyIsLooking:true}")
    private boolean toggleCheckNoOneIsLooking;

    private final Grid<Superhero> grid = new Grid<>(Superhero.class, false);
    private final SuperheroService service;
    private final SuperheroManagerRepository superheroManagerRepository;
    private final Environment env;
    private final AuthenticatedUser authenticatedUser;

    @PostConstruct
    private void init() {
        setSizeFull();
        add(grid);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setItems(query ->  service.list(
                        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());

        grid.addColumn("nickname").setAutoWidth(true);

        grid.addItemClickListener(e -> {
            if(authenticatedUser.isAuthenticated()) {
                if(toggleCheckNoOneIsLooking) {
                    Dialog dialog = new ConfirmDialog(() -> displaySuperheroDetailsOptionalToggle(e.getItem()));
                    dialog.open();
                } else { // no authentication required
                    displaySuperheroDetailsOptionalToggle(e.getItem());
                }
            } else {
                Notification.show("User not authenticated. Please sign in.", 3000, Notification.Position.TOP_CENTER);
            }

        });
    }

    private void displaySuperheroDetailsOptionalToggle(Superhero superhero) {
        if (toggleAuthenticationRequired) {
            //            Notification.show("User clicked row with ID = " + event.getItem().getId());
            Dialog dialog = new AuthenticationDialog(authenticatedUser, () -> displaySuperheroDetails(superhero));
            dialog.open();
        } else {
            displaySuperheroDetails(superhero);
        }

    }

    private void displaySuperheroDetails(Superhero superhero) {
        Optional<Manager> maybeManager = authenticatedUser.get();
        maybeManager.ifPresentOrElse(manager -> {

            final Optional<SuperheroDTO> maybeDto = service.decryptSuperhero(manager, superhero);

            maybeDto.ifPresentOrElse(superheroDTO -> {

                Dialog dialog = new SuperheroDialog(superheroDTO);
                dialog.open();

            }, () -> Notification.show("User do not have access to " + superhero.getNickname(), 4000, Notification.Position.MIDDLE));

        }, () -> Notification.show("Please login", 4000, Notification.Position.MIDDLE));
    }





}
