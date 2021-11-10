package io.avec.ced.crypto.password;

import io.avec.ced.crypto.domain.Password;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilsTest {

    @Test
    void generatePassword() {
        final Password password = PasswordUtils.generatePassword();
        assertThat(password).isNotNull();
        assertThat(password.getValue()).isNotBlank();
        System.out.println(password.getValue());
        assertThat(password.getValue().length()).isGreaterThanOrEqualTo(10);
    }
}