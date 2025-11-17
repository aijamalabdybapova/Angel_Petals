package com.flowershop.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class EncryptionService {

    @Value("${encryption.secret-key:flowershop-2024-key}")
    private String secretKey;

    private static final String ALGORITHM = "AES";

    public String encrypt(String data) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка шифрования данных", e);
        }
    }

    public String decrypt(String encryptedData) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка дешифрования данных", e);
        }
    }

    public String maskEmail(String email) {
        if (email == null || email.isEmpty()) return "";
        String[] parts = email.split("@");
        if (parts.length != 2) return email;

        String username = parts[0];
        String domain = parts[1];

        if (username.length() <= 2) {
            return "*".repeat(username.length()) + "@" + domain;
        }

        return username.charAt(0) + "*".repeat(username.length() - 2) +
                username.charAt(username.length() - 1) + "@" + domain;
    }

    public String maskPhone(String phone) {
        if (phone == null || phone.isEmpty()) return "";
        if (phone.length() <= 4) return phone;

        return phone.substring(0, phone.length() - 4) + "****";
    }
}