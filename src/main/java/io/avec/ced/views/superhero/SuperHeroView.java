package io.avec.ced.views.superhero;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
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
public class SuperHeroView extends VerticalLayout {
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
            Optional<Manager> maybeManager = authenticatedUser.get();
            maybeManager.ifPresentOrElse(manager -> {
                final Superhero superhero = event.getItem();

                VerticalLayout layout = new VerticalLayout(new H1(superhero.getNickname()));
                ObjectMapper mapper = new ObjectMapper();

                // Do Manager have access to Superhero info?
                final String nickname = superhero.getNickname();
                final Optional<SuperheroManager> maybeSuperheroManager = superheroManagerRepository.findBySuperheroNicknameEqualsIgnoreCaseAndManager(nickname, manager);
                maybeSuperheroManager.ifPresentOrElse(superheroManager -> {
                    final SuperheroDTO dto;
                    try {

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
                        dto = mapper.readValue(jsonPlainText.getValue(), SuperheroDTO.class);

                        layout.add(createSuperheroLayout(dto));

                        Dialog dialog = new Dialog(layout);
                        dialog.open();
                    } catch (JsonProcessingException e) {
                        Notification.show("Could not convert JSON to Object", 4000, Notification.Position.MIDDLE);
                    } catch (Exception e) {
                        Notification.show(e.getMessage(), 4000, Notification.Position.MIDDLE);
                    }
                }, () -> Notification.show("User do not have access to " + nickname, 4000, Notification.Position.MIDDLE));

            }, () -> Notification.show("Please login", 4000, Notification.Position.MIDDLE));
//            }, () -> UI.getCurrent().navigate(LoginView.class));

        });
    }

    private VerticalLayout createSuperheroLayout(SuperheroDTO dto) {
        VerticalLayout form = new VerticalLayout();

        final TextField firstname = new TextField("First name");
        firstname.setValue(dto.getFirstname());
        form.add(firstname);

        final TextField lastname = new TextField("Last name");
        lastname.setValue(dto.getLastname());
        form.add(lastname);

        final DatePicker dob = new DatePicker("Last name");
        dob.setValue(dto.getDateOfBirth());
        form.add(dob);

        final TextField country = new TextField("Country");
        country.setValue(dto.getCountry());
        form.add(country);

        return form;
    }

}
