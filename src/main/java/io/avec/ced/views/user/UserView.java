package io.avec.ced.views.user;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import io.avec.ced.crypto.CryptoUtils;
import io.avec.ced.crypto.domain.CipherText;
import io.avec.ced.crypto.domain.Password;
import io.avec.ced.crypto.rsa.KeyUtils;
import io.avec.ced.data.entity.User;
import io.avec.ced.data.service.UserService;
import io.avec.ced.views.MainLayout;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;

import javax.annotation.PostConstruct;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

@Slf4j
@RequiredArgsConstructor
@PageTitle("User")
@Route(value = "user", layout = MainLayout.class)
//@RolesAllowed("admin")
@AnonymousAllowed
public class UserView extends VerticalLayout {
    private final Grid<User> grid = new Grid<>(User.class, false);
    private final UserService userService;
    private final Environment env;

    @PostConstruct
    private void init() {
        grid.setItems(query -> userService.list(
                        PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());

        grid.addColumn("username").setAutoWidth(true);
        grid.addColumn("name").setAutoWidth(true);
//        grid.addColumn("hashedPassword").setAutoWidth(true);
        grid.addColumn(this::publicKey).setHeader("Public Key").setAutoWidth(true).setSortable(false);
        grid.addColumn(this::privateKey).setHeader("Private Key").setAutoWidth(true).setSortable(false);
        grid.addColumn("roles").setAutoWidth(true);
        grid.addComponentColumn(user -> new Image(user.getProfilePictureUrl(), user.getName())).setAutoWidth(true);

        add(grid);
    }

    private String privateKey(User user) {
        try {
            final var ciperText = new CipherText(user.getPrivateKey());
            final var password = new Password(env.getProperty("secret.password"));
            final var plainText = CryptoUtils.aesDecrypt(ciperText, password); // decrypt
            final var rsaPrivateKey = KeyUtils.privateKeyFromString(plainText.getValue());
            if(rsaPrivateKey.isPresent()) {
                final var privateKey = rsaPrivateKey.get();
                return privateKey.getAlgorithm() + ", " + privateKey.getFormat() + ", " + privateKey.getModulus().bitLength();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return "Error reading private key";
    }

    private String publicKey(User user) {
        try {
            final var rsaPublicKey = KeyUtils.publicKeyFromString(user.getPublicKey());
            if(rsaPublicKey.isPresent()) {
                final var publicKey = rsaPublicKey.get();
                return publicKey.getAlgorithm() + ", " + publicKey.getFormat() + ", " + publicKey.getModulus().bitLength();
            }

        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchProviderException e) {
            log.error(e.getMessage());
        }
        return "Error reading public key";
    }
}
