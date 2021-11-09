package io.avec.ced.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.context.annotation.Configuration;

import java.security.Security;

@Configuration
public class BouncyCastleConfiguration {

    public BouncyCastleConfiguration() {
        Security.addProvider(new BouncyCastleProvider());
    }
}
