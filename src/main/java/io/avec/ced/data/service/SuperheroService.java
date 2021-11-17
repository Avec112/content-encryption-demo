package io.avec.ced.data.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.notification.Notification;
import io.avec.ced.crypto.CryptoUtils;
import io.avec.ced.crypto.domain.CipherText;
import io.avec.ced.crypto.domain.Password;
import io.avec.ced.crypto.domain.PlainText;
import io.avec.ced.crypto.rsa.KeyUtils;
import io.avec.ced.data.dto.SuperheroDTO;
import io.avec.ced.data.entity.Manager;
import io.avec.ced.data.entity.Superhero;
import io.avec.ced.data.entity.SuperheroManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SuperheroService extends CrudService<Superhero, Integer> {

    @Value("${secret.password}")
    private String secretPassword;

    private final SuperheroRepository repository;
    private final SuperheroManagerRepository superheroManagerRepository;

    @Override
    protected JpaRepository<Superhero, Integer> getRepository() {
        return repository;
    }



    public Optional<SuperheroDTO> decryptSuperhero(Manager manager, Superhero superhero) {

        ObjectMapper mapper = new ObjectMapper();

        // Do Manager have access to Superhero info?
        final String nickname = superhero.getNickname();
        final Optional<SuperheroManager> maybeSuperheroManager = superheroManagerRepository.findBySuperheroNicknameEqualsIgnoreCaseAndManager(nickname, manager);
        if(maybeSuperheroManager.isPresent()) {
            try {
                final SuperheroManager superheroManager = maybeSuperheroManager.get();

                // lookup managers private key
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
                // todo
                Notification.show("Could not convert JSON to Object", 4000, Notification.Position.MIDDLE);
            } catch (Exception e) {
                // todo
                Notification.show(e.getMessage(), 4000, Notification.Position.MIDDLE);
            }
        }

        return Optional.empty();

    }
}
