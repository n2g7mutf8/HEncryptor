package team.ttc.hencryptor.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordUtils {

    public static String generatePassword(int length) {
        PasswordGenerator generator = new PasswordGenerator.PasswordGeneratorBuilder().useDigits(true).useLower(true).usePunctuation(true).useUpper(true).build();

        return generator.generate(length);
    }

    public static int getAlgorithmRequirement(String algorithm) {
        int maxlength = 1;

        if (algorithm.contains("512")) {
            maxlength = 64;
        } else if (algorithm.contains("256")) {
            maxlength = 32;
        } else if (algorithm.contains("192") || algorithm.contains("DESede")) {
            maxlength = 24;
        } else if (algorithm.contains("128") || algorithm.contains("AES") || algorithm.contains("Blowfish")) {
            maxlength = 16;
        } else if (algorithm.contains("64")) {
            maxlength = 8;
        } else if (algorithm.contains("56") || algorithm.contains("DES")) {
            maxlength = 7;
        } else if (algorithm.contains("40")) {
            maxlength = 5;
        }

        return maxlength;
    }

    public static boolean isValid(String password, int length) {
        if (password.length() >= length) {
            Pattern letter = Pattern.compile("[a-zA-z]");
            Pattern digit = Pattern.compile("[0-9]");
            Pattern special = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]");

            Matcher hasLetter = letter.matcher(password);
            Matcher hasDigit = digit.matcher(password);
            Matcher hasSpecial = special.matcher(password);

            return hasLetter.find() && hasDigit.find() && hasSpecial.find();
        } else return false;
    }
}
