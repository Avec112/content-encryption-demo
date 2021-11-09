package io.avec.ced.data.generator;


import com.vaadin.exampledata.DataType;
import com.vaadin.exampledata.ExampleDataGenerator;
import com.vaadin.flow.spring.annotation.SpringComponent;
import io.avec.ced.crypto.CryptoUtils;
import io.avec.ced.crypto.domain.CipherText;
import io.avec.ced.crypto.domain.Password;
import io.avec.ced.crypto.domain.PlainText;
import io.avec.ced.crypto.rsa.KeySize;
import io.avec.ced.crypto.rsa.KeyUtils;
import io.avec.ced.data.Role;
import io.avec.ced.data.entity.SamplePerson;
import io.avec.ced.data.entity.User;
import io.avec.ced.data.service.SamplePersonRepository;
import io.avec.ced.data.service.UserRepository;
import io.avec.ced.encoding.EncodingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.KeyPair;
import java.time.LocalDateTime;
import java.util.Collections;

@SpringComponent
public class DataGenerator {

    @Value("${secret.password}")
    private String password;

    @Bean
    public CommandLineRunner loadData(PasswordEncoder passwordEncoder, SamplePersonRepository samplePersonRepository,
            UserRepository userRepository) {
        return args -> {
            Logger logger = LoggerFactory.getLogger(getClass());
            if (samplePersonRepository.count() != 0L) {
                logger.info("Using existing database");
                return;
            }
            int seed = 123;

            logger.info("Generating demo data");

            logger.info("... generating 100 Sample Person entities...");
            ExampleDataGenerator<SamplePerson> samplePersonRepositoryGenerator = new ExampleDataGenerator<>(
                    SamplePerson.class, LocalDateTime.of(2021, 10, 6, 0, 0, 0));
            samplePersonRepositoryGenerator.setData(SamplePerson::setId, DataType.ID);
            samplePersonRepositoryGenerator.setData(SamplePerson::setFirstName, DataType.FIRST_NAME);
            samplePersonRepositoryGenerator.setData(SamplePerson::setLastName, DataType.LAST_NAME);
            samplePersonRepositoryGenerator.setData(SamplePerson::setEmail, DataType.EMAIL);
            samplePersonRepositoryGenerator.setData(SamplePerson::setPhone, DataType.PHONE_NUMBER);
            samplePersonRepositoryGenerator.setData(SamplePerson::setDateOfBirth, DataType.DATE_OF_BIRTH);
            samplePersonRepositoryGenerator.setData(SamplePerson::setOccupation, DataType.OCCUPATION);
            samplePersonRepositoryGenerator.setData(SamplePerson::setImportant, DataType.BOOLEAN_10_90);
            samplePersonRepository.saveAll(samplePersonRepositoryGenerator.create(100, seed));

            logger.info("... generating 2 User entities...");
            User user = new User();
            user.setName("Bob");
            user.setUsername("bob");
            user.setHashedPassword(passwordEncoder.encode("bob"));
            user.setProfilePictureUrl( // https://unsplash.com/s/photos/user-profile
                    "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
            user.setRoles(Collections.singleton(Role.USER));
            generateAndAddRsaKeyPair(user, KeySize.BIT_2048);
            userRepository.save(user);

            User admin = new User();
            admin.setName("Alice");
            admin.setUsername("alice");
            admin.setHashedPassword(passwordEncoder.encode("alice"));
            admin.setProfilePictureUrl( // https://unsplash.com/s/photos/user-profile
                    "https://images.unsplash.com/photo-1607746882042-944635dfe10e?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=128&h=128&q=80");
            admin.setRoles(Collections.singleton(Role.ADMIN));
            generateAndAddRsaKeyPair(admin, KeySize.BIT_4096);
            userRepository.save(admin);

            logger.info("Generated demo data");
        };
    }


    private void generateAndAddRsaKeyPair(final User user, KeySize keySize) throws Exception {

        final KeyPair keyPair = KeyUtils.generateRsaKeyPair(keySize);

        final byte[] pubKey = keyPair.getPublic().getEncoded();
        final byte[] privKey = keyPair.getPrivate().getEncoded();

        String pub = EncodingUtils.base64Encode(pubKey);
        user.setPublicKey(pub);

        String priv = EncodingUtils.base64Encode(privKey);
        final CipherText cipherText = CryptoUtils.aesEncrypt(new PlainText(priv), new Password(password));
        user.setPrivateKey(cipherText.getValue());
    }



}