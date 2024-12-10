package aucta.dev.mercator_core.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import java.security.spec.KeySpec;
import java.util.Base64;

public class Crypto {
    private static final String UNICODE_FORMAT = "UTF8";
    public static final String DESEDE_ENCRYPTION_SCHEME = "DESede";
    private KeySpec ks;
    private SecretKeyFactory skf;
    private Cipher cipher;
    byte[] arrayBytes;
    private String encryptionKey;
    private String encryptionScheme;
    SecretKey key;

    public Crypto() throws Exception {
        this.encryptionKey = "EtprDs53TW!0Dn{bI>2xS3#$";
        this.encryptionScheme = "DESede";
        this.arrayBytes = this.encryptionKey.getBytes("UTF8");
        this.ks = new DESedeKeySpec(this.arrayBytes);
        this.skf = SecretKeyFactory.getInstance(this.encryptionScheme);
        this.cipher = Cipher.getInstance(this.encryptionScheme);
        this.key = this.skf.generateSecret(this.ks);
    }

    public String encrypt(final String unencryptedString) {
        String encryptedString = null;
        try {
            this.cipher.init(1, this.key);
            final byte[] plainText = unencryptedString.getBytes("UTF8");
            final byte[] encryptedText = this.cipher.doFinal(plainText);
            encryptedString = Base64.getEncoder().encodeToString(encryptedText);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedString;
    }

    public String decrypt(final String encryptedString) {
        String decryptedText = null;
        try {
            this.cipher.init(2, this.key);
            final byte[] encryptedText = Base64.getDecoder().decode(encryptedString);
            final byte[] plainText = this.cipher.doFinal(encryptedText);
            decryptedText = new String(plainText, "UTF8");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return decryptedText;
    }
}
