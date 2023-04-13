package team.ttc.hencryptor.utils;

import team.ttc.hencryptor.exception.CipherException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CipherUtils {
    public static void encrypt(String key, File inputFile, File outputFile, String algorithm) throws CipherException {
        CipherUtils.doCrypto(1, key, inputFile, outputFile, algorithm);
    }

    public static void decrypt(String key, File inputFile, File outputFile, String algorithm) throws CipherException {
        CipherUtils.doCrypto(2, key, inputFile, outputFile, algorithm);
    }

    public static void encrypt(String key, File inputFile, File outputFile, String algorithm, byte[] salt, int iterations) throws CipherException {
        CipherUtils.doCrypto(1, key, inputFile, outputFile, algorithm, salt, iterations);
    }

    public static void decrypt(String key, File inputFile, File outputFile, String algorithm, byte[] salt, int iterations) throws CipherException {
        CipherUtils.doCrypto(2, key, inputFile, outputFile, algorithm, salt, iterations);
    }

    public static void encrypt(String key, File inputFile, File outputFile, String algorithm, byte[] salt, int iterations, IvParameterSpec ivSpec) throws CipherException {
        CipherUtils.doCrypto(1, key, inputFile, outputFile, algorithm, salt, iterations, ivSpec);
    }

    public static void decrypt(String key, File inputFile, File outputFile, String algorithm, byte[] salt, int iterations, IvParameterSpec ivSpec) throws CipherException {
        CipherUtils.doCrypto(2, key, inputFile, outputFile, algorithm, salt, iterations, ivSpec);
    }

    public static void warp(String key, File inputFile, File outputFile, String algorithm) throws CipherException {
        CipherUtils.doCrypto(3, key, inputFile, outputFile, algorithm);
    }

    public static void unwarp(String key, File inputFile, File outputFile, String algorithm) throws CipherException {
        CipherUtils.doCrypto(4, key, inputFile, outputFile, algorithm);
    }

    private static void doCrypto(int cipherMode, String key, File inputFile, File outputFile, String algorithm) throws CipherException {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), algorithm);
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(cipherMode, secretKey);
            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);
            byte[] outputBytes = cipher.doFinal(inputBytes);
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);
            inputStream.close();
            outputStream.close();
        } catch (IOException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException |
                 IllegalBlockSizeException | NoSuchPaddingException ex) {
            throw new CipherException("Error while trying en/decrypt a file.", ex);
        }
    }

    private static void doCrypto(int cipherMode, String key, File inputFile, File outputFile, String algorithm, byte[] salt, int iterations) throws CipherException {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), algorithm);
            Cipher cipher = Cipher.getInstance(algorithm);
            if (algorithm.contains("PBE")) {
                cipher.init(cipherMode, (Key) secretKey, new PBEParameterSpec(salt, iterations));
            }
            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);
            byte[] outputBytes = cipher.doFinal(inputBytes);
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);
            inputStream.close();
            outputStream.close();
        } catch (IOException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException |
                 IllegalBlockSizeException | NoSuchPaddingException ex) {
            throw new CipherException("Error while trying en/decrypt a file.", ex);
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    public static int getMaxLength(String algorithm) throws CipherException {
        try {
            return Cipher.getMaxAllowedKeyLength(algorithm);
        } catch (NoSuchAlgorithmException ex) {
            throw new CipherException("Error while trying to get max key length.", ex);
        }
    }

    private static void doCrypto(int cipherMode, String key, File inputFile, File outputFile, String algorithm, byte[] salt, int iterations, IvParameterSpec ivSpec) throws CipherException {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), algorithm);
            Cipher cipher = Cipher.getInstance(algorithm);
            if (algorithm.contains("PBE") || algorithm.contains("With") || algorithm.contains("And")) {
                cipher.init(cipherMode, (Key) secretKey, new PBEParameterSpec(salt, iterations, ivSpec));
            }
            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);
            byte[] outputBytes = cipher.doFinal(inputBytes);
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);
            inputStream.close();
            outputStream.close();
        } catch (IOException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException |
                 IllegalBlockSizeException | NoSuchPaddingException ex) {
            throw new CipherException("Error while trying en/decrypt a file.", ex);
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }
}
