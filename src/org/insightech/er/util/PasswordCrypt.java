package org.insightech.er.util;

import java.io.File;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.insightech.er.util.io.FileUtils;

public class PasswordCrypt {

    public static void main(String[] args) throws Exception {
        String encrypted = encrypt("nakajima");
        System.out.println(encrypted);

        String decrypted = decrypt(encrypted);
        System.out.println(decrypted);
    }

    private static final String KEY_ALGORITHM = "AES";

    private static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    private static final File KEY_FILE = new File("password.key");

    public static String encrypt(String password) throws Exception {
        Key key = getKey();

        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] input = password.getBytes();
        byte[] encrypted = cipher.doFinal(input);

        return new String(Base64.encodeBase64(encrypted));
    }

    public static String decrypt(String encryptedPassword) throws Exception {
        Key key = getKey();

        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] encrypted = Base64.decodeBase64(encryptedPassword.getBytes());
        byte[] output = cipher.doFinal(encrypted);

        return new String(output);
    }

    private static Key getKey() throws Exception {
        if (KEY_FILE.exists()) {
            byte[] key = FileUtils.readFileToByteArray(KEY_FILE);

            SecretKeySpec keySpec = new SecretKeySpec(key, KEY_ALGORITHM);
            return keySpec;

        } else {
            Key key = generateKey();
            FileUtils.writeByteArrayToFile(KEY_FILE, key.getEncoded());

            return key;
        }
    }

    private static Key generateKey() throws Exception {
        KeyGenerator generator = KeyGenerator.getInstance(KEY_ALGORITHM);

        SecureRandom random = new SecureRandom();
        generator.init(128, random);
        Key key = generator.generateKey();

        return key;
    }

}
