package xyz.hexile.mcmodnode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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

    public static String digestSha512(byte[] input) {
        return digestSha(input, "SHA-512");
    }

    public static String digestSha256(byte[] input) {
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

    public static String digestSha(byte[] input, String method) {
        try {
            MessageDigest md = MessageDigest.getInstance(method);
            byte[] messageDigest = md.digest(input);

            return Base64.encodeToString(messageDigest, Base64.DEFAULT).replaceAll("\n", "");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getBaseApkLocation(Context context, String packageName) throws PackageManager.NameNotFoundException {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(packageName, 0);
        return pi.applicationInfo.sourceDir;
    }

    public static byte[] updateByteArray(byte[] input, int offset, byte[] newArray) {
        ByteBuffer buffer = ByteBuffer.wrap(input);
        buffer.position(offset);
        buffer.put(newArray);
        return buffer.array();
    }

    public static byte[] generateRandomBytes(int length) {
        byte[] bytes = new byte[length];
        new Random().nextBytes(bytes);
        return bytes;
    }

    public static byte[] bytesFromHex(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static byte[] readFileToByteArray(File file) throws IOException {
        FileInputStream fis;
        byte[] bytes = new byte[(int) file.length()];
        fis = new FileInputStream(file);
        fis.read(bytes);
        fis.close();
        return bytes;
    }

    public static byte[] readFileToByteArray(String file) throws IOException {
        return readFileToByteArray(new File(file));
    }

    public static int indexOf(byte[] data, byte[] pattern) {
        if (data.length == 0) return -1;

        int[] failure = computeFailure(pattern);
        System.out.println("FAILURE: " + Arrays.toString(failure));
        int j = 0;

        for (int i = 0; i < data.length; i++) {
            while (j > 0 && pattern[j] != data[i]) {
                j = failure[j - 1];
            }
            if (pattern[j] == data[i]) { j++; }
            if (j == pattern.length) {
                return i - pattern.length + 1;
            }
        }
        return -1;
    }

    public static int lastIndexOf(byte[] data, byte[] pattern) {
        if (data.length == 0) return -1;

        int[] failure = computeFailure(pattern);
        int j = pattern.length - 1;

        for (int i = data.length - 1; i > 0; i--) {
            while (j < pattern.length - 1 && pattern[j] != data[i]) {
                j = failure[j + 1];
            }
            if (pattern[j] == data[i]) { j--; }
            if (j == 0) {
                return i - 1;
            }
        }
        return -1;
    }

    public static int[] computeFailure(byte[] pattern) {
        int[] failure = new int[pattern.length];

        int j = 0;
        for (int i = 1; i < pattern.length; i++) {
            while (j > 0 && pattern[j] != pattern[i]) {
                j = failure[j - 1];
            }
            if (pattern[j] == pattern[i]) {
                j++;
            }
            failure[i] = j;
        }

        return failure;
    }
}
