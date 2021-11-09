package io.avec.ced.crypto.rsa;

import io.avec.ced.encoding.EncodingUtils;

import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAKeyGenParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Optional;

public class KeyUtils {

    private KeyUtils() {
    }

    public static KeyPair generateRsaKeyPair(KeySize keySize) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
        keyPairGenerator.initialize(new RSAKeyGenParameterSpec(keySize.getKeySize(), RSAKeyGenParameterSpec.F4));
//        keyPairGenerator.initialize(keySize.getKeySize());
        return keyPairGenerator.generateKeyPair();
    }

    public static KeyPair generateKeyPair4096() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        return generateRsaKeyPair(KeySize.BIT_4096);
    }

    public static KeyPair generateKeyPair3072() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        return generateRsaKeyPair(KeySize.BIT_3072);
    }

    public static KeyPair generateKeyPair2048() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        return generateRsaKeyPair(KeySize.BIT_2048);
    }

    public static KeyPair generateKeyPair1024() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        return generateRsaKeyPair(KeySize.BIT_1024);
    }

    public static Optional<RSAPrivateKey> privateKeyFromBytes(byte[] privateKeyEncoded) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        KeyFactory kf = KeyFactory.getInstance("RSA", "BC");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyEncoded);
        return Optional.ofNullable((RSAPrivateKey) kf.generatePrivate(keySpec));
    }

    public static Optional<RSAPrivateKey> privateKeyFromString(String privateKeyBase64Encoded) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        final byte[] bytes = EncodingUtils.base64Decode(privateKeyBase64Encoded);
        return privateKeyFromBytes(bytes);
    }

    public static Optional<RSAPublicKey> publicKeyFromBytes(byte[] publicKeyEncoded) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        KeyFactory kf = KeyFactory.getInstance("RSA", "BC");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyEncoded);
        return Optional.ofNullable((RSAPublicKey)kf.generatePublic(keySpec));
    }

    public static Optional<RSAPublicKey> publicKeyFromString(String publicKeyBase64Encoded) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
        final byte[] publicKeyEncoded = EncodingUtils.base64Decode(publicKeyBase64Encoded);
        return publicKeyFromBytes(publicKeyEncoded);
    }



}
