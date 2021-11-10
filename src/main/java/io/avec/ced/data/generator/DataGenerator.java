package io.avec.ced.data.generator;


import com.devskiller.jfairy.Fairy;
import com.devskiller.jfairy.producer.person.Person;
import com.devskiller.jfairy.producer.person.PersonProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.vaadin.flow.spring.annotation.SpringComponent;
import io.avec.ced.crypto.CryptoUtils;
import io.avec.ced.crypto.domain.CipherText;
import io.avec.ced.crypto.domain.Password;
import io.avec.ced.crypto.domain.PlainText;
import io.avec.ced.crypto.password.PasswordUtils;
import io.avec.ced.crypto.rsa.KeySize;
import io.avec.ced.crypto.rsa.KeyUtils;
import io.avec.ced.data.Role;
import io.avec.ced.data.dto.SuperheroDTO;
import io.avec.ced.data.entity.Manager;
import io.avec.ced.data.entity.Superhero;
import io.avec.ced.data.entity.SuperheroManager;
import io.avec.ced.data.service.ManagerRepository;
import io.avec.ced.data.service.SuperheroManagerRepository;
import io.avec.ced.data.service.SuperheroRepository;
import io.avec.ced.encoding.EncodingUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.KeyPair;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

@Slf4j
@SpringComponent
public class DataGenerator {

    @Value("${secret.password}")
    private String password;

    private final Random random = new Random();

    @Bean
    public CommandLineRunner loadData(PasswordEncoder passwordEncoder,
                                      SuperheroRepository superheroRepository,
                                      SuperheroManagerRepository superheroManagerRepository,
                                      ManagerRepository managerRepository) {
        return args -> {

            int superheroCount = 20;

            log.info("Generating demo data");


            log.info("... generating 2 Manager entities...");
            Manager bob = new Manager();
            bob.setName("Bob");
            bob.setUsername("bob");
            bob.setHashedPassword(passwordEncoder.encode("bob"));
            bob.setProfilePictureUrl( // https://unsplash.com/s/photos/user-profile
                    "https://i.pravatar.cc/128?img=12");
//                    "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
            bob.setRoles(Collections.singleton(Role.MANAGER));
            generateAndAddRsaKeyPair(bob, KeySize.BIT_2048);
            managerRepository.save(bob);

            Manager alice = new Manager();
            alice.setName("Alice");
            alice.setUsername("alice");
            alice.setHashedPassword(passwordEncoder.encode("alice"));
            alice.setProfilePictureUrl( // https://unsplash.com/s/photos/user-profile
                    "https://i.pravatar.cc/128?img=9");
//                    "https://images.unsplash.com/photo-1607746882042-944635dfe10e?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
            alice.setRoles(Set.of(Role.ADMIN, Role.MANAGER));
            generateAndAddRsaKeyPair(alice, KeySize.BIT_4096);
            managerRepository.save(alice);

            log.info("Generating secret persons");
            final Optional<RSAPublicKey> maybeAlicePublicKey = KeyUtils.publicKeyFromString(alice.getPublicKey());

            maybeAlicePublicKey.ifPresent(alicePublicKey -> {

                ObjectMapper mapper = new ObjectMapper(); // json mapper
                Fairy fairy = Fairy.create(); // Jfairy dummy data

                // create Superheros
                int i = 0;
                while(i < superheroCount) {
                    SuperheroDTO superheroDTO = new SuperheroDTO();
                    final String nickname = generateNickname();
                    superheroDTO.setNickname(nickname);
                    final Person person = fairy.person(PersonProperties.ageBetween(20, 55), PersonProperties.male());
                    superheroDTO.setFirstname(person.getFirstName());
                    superheroDTO.setLastname(person.getLastName());
                    superheroDTO.setDateOfBirth(person.getDateOfBirth());
                    superheroDTO.setCountry(person.getNationality().name());

                    try {
                        // generate superhero password
                        Password superheroPassword = PasswordUtils.generatePassword();

                        // encrypt superhero password with managers public key
                        CipherText superheroPasswordEncrypted = CryptoUtils.rsaEncrypt(new PlainText(superheroPassword.getValue()), alicePublicKey); // RSA encrypted password

                        // Create Superhero JSON from DTO
                        String superheroJson = mapper.writeValueAsString(superheroDTO);

                        // encrypt Superhero JSON with Superhero password
                        CipherText encryptedJson = CryptoUtils.aesEncrypt(new PlainText(superheroJson), superheroPassword);

                        // create Entity Superhero
                        final Superhero superhero = new Superhero(superheroDTO.getNickname(), encryptedJson.getValue());
                        superheroRepository.save(superhero);


                        // Temp logging
//                        log.info("{}", superheroDTO);
//                        log.info("{}", superhero);
                        log.info("Nickname '{}'", superhero.getNickname());

                        // create entity SuperheroManager with managers public key encrypted password
                        SuperheroManager superheroManager = new SuperheroManager(alice, superheroPasswordEncrypted.getValue(), superhero);
                        superheroManagerRepository.save(superheroManager);

                        i++;
                    } catch(DataIntegrityViolationException e) {
                        log.error("Nickname {} was taken. Generating a new one.", nickname );
                        // ignore
                    } catch (Exception e) { // serious exception
//                        e.printStackTrace();
                        log.error(e.getMessage());
                        throw new IllegalStateException("Cannot continue");
                    }
                }
            });

            log.info("Generated demo data");
        };
    }

    private String generateNickname() {

        Faker faker = new Faker(); // java-faker dymmy data

        return switch(random.nextInt(7)) {
            case 0 -> faker.harryPotter().character();
            case 1 -> faker.superhero().name();
            case 2 -> StringUtils.capitalize(faker.animal().name());
            case 4 -> faker.rickAndMorty().character();
            case 5 -> faker.lordOfTheRings().character();
            case 6 -> faker.gameOfThrones().character();
            default -> faker.hobbit().character();
        };

    }

    private void generateAndAddRsaKeyPair(final Manager manager, KeySize keySize) throws Exception {

        final KeyPair keyPair = KeyUtils.generateRsaKeyPair(keySize);

        final byte[] pubKey = keyPair.getPublic().getEncoded();
        final byte[] privKey = keyPair.getPrivate().getEncoded();

        String pub = EncodingUtils.base64Encode(pubKey);
        manager.setPublicKey(pub);

        String priv = EncodingUtils.base64Encode(privKey);
        final CipherText cipherText = CryptoUtils.aesEncrypt(new PlainText(priv), new Password(password));
        manager.setPrivateKey(cipherText.getValue());
    }



}