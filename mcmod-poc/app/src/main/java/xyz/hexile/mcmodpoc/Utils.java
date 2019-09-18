package xyz.hexile.mcmodpoc;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.provider.Settings;
import android.util.Base64;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class Utils {

    @SuppressLint("HardwareIds")
    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String capitalize(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    public static String randomAndroidId() {
        Random random = new Random();
        return String.format("%016x", random.nextLong());
    }

    public static String randomString(int length) {
        String dictionary = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int rndCharAt = random.nextInt(dictionary.length());
            char rndChar = dictionary.charAt(rndCharAt);
            sb.append(rndChar);
        }

        return sb.toString();
    }

    public static String randomPassword() {
        return "P" + randomString(10) + "1";
    }

    public static String randomEmail() {
        return randomString(10).toLowerCase() + "@" + randomString(5).toLowerCase() + ".com";
    }

    public static String registrationBody(String registrationName, String registrationLastname,
                                          String registrationEmail, String registrationPassword) {
        return String.format("{\"grant_type\":\"password\",\"username\":\"%s\"," +
                        "\"password\":\"%s\",\"emailRegistration\":{\"emailAddress\":\"%s\"," +
                        "\"password\":\"%s\",\"firstName\":\"%s\",\"lastName\":\"%s\",\"gender\":\"f\"," +
                        "\"dateOfBirth\":\"1990-01-01\",\"tagValueAddReferenceCodes\":[\"merchantId587\"," +
                        "\"italy_family\",\"italy_family\"]}}", registrationEmail, registrationPassword,
                registrationEmail, registrationPassword, registrationName, registrationLastname);
    }

    public static String couponBody(String id) {
        return String.format("{\"offerInstanceUniqueId\":\"%s\",\"offerId\":%s}", id, id);
    }

    public static String getDate() {
        String pattern = "EEE, dd MMM yyyy HH:mm:ss z";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.US);

        return simpleDateFormat.format(new Date());
    }

    public static String encryptDES(String input) {
        try {
            String desKey = "co.vmob.sdk.android.encrypt.key";

            SecretKeyFactory factory = SecretKeyFactory.getInstance("DES");
            SecretKey key = factory.generateSecret(new DESKeySpec(desKey.getBytes()));

            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.ENCRYPT_MODE, key);

            return Base64.encodeToString(cipher.doFinal(input.getBytes()), Base64.URL_SAFE).replace('\n', '_');
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] digestBytesSha512(String input) {
        return digestBytesSha(input, "SHA-512");
    }

    public static byte[] digestBytesSha256(String input) {
        return digestBytesSha(input, "SHA-256");
    }

    public static byte[] digestBytesSha(String input, String method) {
        try {
            MessageDigest md = MessageDigest.getInstance(method);
            return md.digest(input.getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String digestSha512(String input) {
        return digestSha(input, "SHA-512");
    }

    public static String digestSha256(String input) {
        return digestSha(input, "SHA-256");
    }

    public static String digestSha(String input, String method) {
        try {
            MessageDigest md = MessageDigest.getInstance(method);
            byte[] messageDigest = md.digest(input.getBytes());

            return Base64.encodeToString(messageDigest, Base64.DEFAULT).replaceAll("\n", "");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }
}
