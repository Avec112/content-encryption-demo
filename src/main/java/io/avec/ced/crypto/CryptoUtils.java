package io.avec.ced.crypto;

import io.avec.ced.crypto.aes.AesCipher;
import io.avec.ced.crypto.domain.CipherText;
import io.avec.ced.crypto.domain.Password;
import io.avec.ced.crypto.domain.PlainText;
import io.avec.ced.crypto.rsa.RsaCipher;

import java.security.PrivateKey;
import java.security.PublicKey;

public class CryptoUtils {

    private CryptoUtils() {
    }

    public static CipherText aesEncrypt(PlainText plainText, Password password) throws Exception {
        AesCipher cipher = new AesCipher();
        return cipher.encrypt(plainText, password);
    }

    public static PlainText aesDecrypt(CipherText ciperText, Password password) throws Exception {
        AesCipher cipher = new AesCipher();
        return cipher.decrypt(ciperText, password);
    }

    public static CipherText rsaEncrypt(PlainText plainText, PublicKey publicKey) throws Exception {
        RsaCipher cipher = new RsaCipher();
        return cipher.encrypt(plainText, publicKey);
    }

    public static PlainText rsaDecrypt(CipherText ciperText, PrivateKey privateKey) throws Exception {
        RsaCipher cipher = new RsaCipher();
        return cipher.decrypt(ciperText, privateKey);
    }
}
