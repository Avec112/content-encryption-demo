package io.avec.ced.views.superhero;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import io.avec.ced.crypto.CryptoUtils;
import io.avec.ced.crypto.domain.CipherText;
import io.avec.ced.crypto.domain.Password;
import io.avec.ced.crypto.domain.PlainText;
import io.avec.ced.crypto.rsa.KeyUtils;
import io.avec.ced.data.dto.SuperheroDTO;
import io.avec.ced.data.entity.Manager;
import io.avec.ced.data.entity.Superhero;
import io.avec.ced.data.entity.SuperheroManager;
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
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
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
        grid.setItems(query ->  service.list(
                        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());

        grid.addColumn("nickname").setAutoWidth(true);

        grid.addItemClickListener(event -> {
            if(authenticatedUser.isAuthenticated()) {
                if (toggleAuthenticationRequired) {
                    //            Notification.show("User clicked row with ID = " + event.getItem().getId());
                    Dialog dialog = new AuthenticationDialog(authenticatedUser, () -> displaySuperheroDetailsOptionalToggle(event.getItem()));
                    dialog.open();
                } else { // no authentication required
                    displaySuperheroDetailsOptionalToggle(event.getItem());
                }
            } else {
                Notification.show("User not authenticated. Please sign in.", 3000, Notification.Position.TOP_CENTER);
            }

        });
    }

    private void displaySuperheroDetailsOptionalToggle(Superhero superhero) {

        if(toggleCheckNoOneIsLooking) {
            Dialog dialog = new ConfirmDialog(() -> displaySuperheroDetails(superhero));
            dialog.open();
        } else {
            displaySuperheroDetails(superhero);
        }

    }

    private void displaySuperheroDetails(Superhero superhero) {
        Optional<Manager> maybeManager = authenticatedUser.get();
        maybeManager.ifPresentOrElse(manager -> {

            final Optional<SuperheroDTO> maybeDto = decryptSuperhero(manager, superhero);

            maybeDto.ifPresentOrElse(superheroDTO -> {

                Dialog dialog = new SuperheroDialog(superheroDTO);
                dialog.open();

            }, () -> Notification.show("User do not have access to " + superhero.getNickname(), 4000, Notification.Position.MIDDLE));

        }, () -> Notification.show("Please login", 4000, Notification.Position.MIDDLE));
    }

    private Optional<SuperheroDTO> decryptSuperhero(Manager manager, Superhero superhero) {

        ObjectMapper mapper = new ObjectMapper();

        // Do Manager have access to Superhero info?
        final String nickname = superhero.getNickname();
        final Optional<SuperheroManager> maybeSuperheroManager = superheroManagerRepository.findBySuperheroNicknameEqualsIgnoreCaseAndManager(nickname, manager);
        if(maybeSuperheroManager.isPresent()) {
            try {
                final SuperheroManager superheroManager = maybeSuperheroManager.get();

                // lookup managers private key
                final String secretPassword = env.getProperty("secret.password");
                final PlainText managerPrivateKeyPlainText = CryptoUtils.aesDecrypt(new CipherText(manager.getPrivateKey()), new Password(secretPassword));
                final Optional<RSAPrivateKey> maybeManagersPrivateKey = KeyUtils.privateKeyFromString(managerPrivateKeyPlainText.getValue());
                final PrivateKey managersPrivateKey = maybeManagersPrivateKey.orElseThrow();

                // decrypt Superhero password with managers private key
                final String superheroEncryptedPassword = superheroManager.getRsaEncryptedPassword();
                final PlainText superheroPasswordPlainText = CryptoUtils.rsaDecrypt(new CipherText(superheroEncryptedPassword), managersPrivateKey);

                // decrypt encrypted json with superheroPassword
                final String encryptedJson = superhero.getEncryptedJson();
                final PlainText jsonPlainText = CryptoUtils.aesDecrypt(new CipherText(encryptedJson), new Password(superheroPasswordPlainText.getValue()));

                // map json to dto
                return Optional.of(mapper.readValue(jsonPlainText.getValue(), SuperheroDTO.class));

            } catch (JsonProcessingException e) {
                Notification.show("Could not convert JSON to Object", 4000, Notification.Position.MIDDLE);
            } catch (Exception e) {
                Notification.show(e.getMessage(), 4000, Notification.Position.MIDDLE);
            }
        }

        return Optional.empty();

    }



}
