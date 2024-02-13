package com.sss.sharedstore.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public class Encriptie {

    public static String genereazaSalt() throws Exception {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[8];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String returneazaParolaCriptata(String parola, String salt) throws Exception {

        KeySpec spec = new PBEKeySpec(parola.toCharArray(), Base64.getDecoder().decode(salt), 30000, 160);

        return Base64.getEncoder().encodeToString(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
                .generateSecret(spec).getEncoded());
    }

}

