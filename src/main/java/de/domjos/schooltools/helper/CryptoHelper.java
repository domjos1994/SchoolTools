/*
 * Copyright (C) 2017-2019  Dominic Joas
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 */

package de.domjos.schooltools.helper;

import android.util.Base64;

import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoHelper {
    private SecretKey key;
    private SecureRandom random;

    public CryptoHelper(String password) throws Exception {
        int iterationCount = 1000;
        int keyLength = 256;
        int saltLength = keyLength / 8;

        this.random = new SecureRandom();
        byte[] salt = new byte[saltLength];
        this.random.nextBytes(salt);

        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, iterationCount, keyLength);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] keyBytes = keyFactory.generateSecret(keySpec).getEncoded();
        this.key = new SecretKeySpec(keyBytes, "AES");
    }

    public String encrypt(String message) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = new byte[cipher.getBlockSize()];
        this.random.nextBytes(iv);
        IvParameterSpec ivParams = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, this.key, ivParams);
        return this.convertByteToString(cipher.doFinal(this.convertStringToByte(message)));
    }

    public String decrypt(String message) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] iv = new byte[cipher.getBlockSize()];
        this.random.nextBytes(iv);
        IvParameterSpec ivParams = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, this.key, ivParams);
        return this.convertByteToString(cipher.doFinal(this.convertStringToByte(message)));
    }

    private byte[] convertStringToByte(String content) throws Exception {
        return content.getBytes("UTF-8");
    }

    private String convertByteToString(byte[] content) {
        return Base64.encodeToString(content, Base64.DEFAULT);
    }
}
