package io.avec.ced.crypto.rsa;

import io.avec.ced.crypto.domain.CipherText;
import io.avec.ced.crypto.domain.PlainText;
import io.avec.ced.encoding.EncodingUtils;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;

public class RsaCipher {

    public CipherText encrypt(PlainText plainText, PublicKey publicKey) throws Exception {
        final Cipher cipher = Cipher.getInstance("RSA", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        final byte[] cipherText = cipher.doFinal(plainText.getValue().getBytes(StandardCharsets.UTF_8));

        return new CipherText(EncodingUtils.base64Encode(cipherText));
    }

    public PlainText decrypt(CipherText cipherText, PrivateKey privateKey) throws Exception {
        final Cipher cipher = Cipher.getInstance("RSA", "BC");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        final byte[] cipherTextDecoded = EncodingUtils.base64Decode(cipherText.getValue());
        final byte[] plainText = cipher.doFinal(cipherTextDecoded);
        return new PlainText(new String(plainText));
    }
}
