package xyz.hexile.mcmodnode;

import android.content.pm.PackageManager;
import android.os.AsyncTask;

import com.topjohnwu.superuser.Shell;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.zip.Adler32;

import mcdonalds.restaurant.network.RestaurantService;

public class ChangeDigestAsyncTask extends AsyncTask<Void, String, String> {

    private WeakReference<RestaurantService> serviceWeakReference;

    private enum WriteMode {
        DD,
        CP
    }

    private WriteMode selectedMode = WriteMode.DD;

    public ChangeDigestAsyncTask(RestaurantService context) {
        serviceWeakReference = new WeakReference<>(context);
    }

    private void sendLog(String message) {
        if (serviceWeakReference.get() != null)
            serviceWeakReference.get().sendLog("DigestTask", message);
    }

    private void outputCommand(Shell.Result command) {
        publishProgress("ERROR: " + command.getCode());
        for (String line : command.getOut()) {
            publishProgress("STDOUT: " + line);
        }
        for (String line : command.getErr()) {
            publishProgress("STDERR: " + line);
        }
    }

    public static String byteNotation(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            stringBuilder.append(String.format("\\x%02X", b));
        }
        return stringBuilder.toString();
    }

    public void interrupt() {
        Thread.currentThread().interrupt();
    }

    @Override
    protected void onPreExecute() {
        sendLog("Starting...");
    }

    @Override
    protected String doInBackground(Void... voids) {
        String apkDigest = "default";

        if (serviceWeakReference.get() == null)
            return "error";

        try {
            String baseApkLocation = Utils.getBaseApkLocation(serviceWeakReference.get(), "com.mcdonalds.mobileapp");
            sendLog("APK location: " + baseApkLocation);

            // Load APK bytes
            byte[] apkData = Utils.readFileToByteArray(baseApkLocation);
            ByteBuffer apkDataBuffer = ByteBuffer.wrap(apkData);

            if (apkData.length == 0)
                return "ERROR NO BYTES";

            // Get current digest
            apkDigest = Utils.digestSha256(apkData);
            publishProgress("Current digest: " + apkDigest);

            // Search for extra data
            publishProgress("Searching for extra data...");
            byte[] extraPattern = new byte[]{80, 108, 101, 120, 117, 114, 101, 65, 98, 117, 115, 101};
            int extraOffset = Utils.indexOf(apkData, extraPattern);
            int extraLength = 10;

            if (extraOffset == -1)
                return "ERROR PATTERN NOT FOUND";

            // Modify extra bytes
            publishProgress("Current data: " + new String((Arrays.copyOfRange(apkData, extraOffset + extraPattern.length, extraOffset + extraPattern.length + extraLength))));
            byte[] extraBytes = Utils.generateRandomBytes(extraLength);
            apkDataBuffer.position(extraOffset + extraPattern.length);
            apkDataBuffer.put(extraBytes);
            publishProgress("Modded data: " + new String((Arrays.copyOfRange(apkData, extraOffset + extraPattern.length, extraOffset + extraPattern.length + extraLength))));

            // Calculate new digest
            publishProgress("Calculating new digest...");
            MessageDigest digestSha1 = MessageDigest.getInstance("SHA-1");
            byte[] bytesToDigest = Arrays.copyOfRange(apkData, 32, apkData.length);
            byte[] digestArray = digestSha1.digest(bytesToDigest);
            apkDataBuffer.position(12);
            apkDataBuffer.put(digestArray);

            // Calculate new checksum
            publishProgress("Calculating new checksum...");
            Adler32 checksum = new Adler32();
            byte[] bytesToChecksum = Arrays.copyOfRange(apkData, 12, apkData.length);
            checksum.update(bytesToChecksum);
            byte[] checksumBytes = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt((int) checksum.getValue()).array();
            apkDataBuffer.position(8);
            apkDataBuffer.put(checksumBytes);

            // Get new APK digest
            apkDigest = Utils.digestSha256(apkData);

            // Check if busybox
            Shell.Result busyboxCheck = Shell.sh("which busybox").exec();
            if (!busyboxCheck.isSuccess()) {
                publishProgress("BusyBox not found, cannot use dd method. The app may crash.");
                selectedMode = WriteMode.CP;
            }

            switch (selectedMode) {
                default:
                case DD:
                    Shell.Result ddChecksum = Shell.su(
                            String.format(
                                    "printf '%s' | busybox dd of='%s' bs=1 seek=%s conv=notrunc",
                                    byteNotation(checksumBytes), baseApkLocation, 8)).exec();
                    if (!ddChecksum.isSuccess()) {
                        publishProgress("ddChecksum EXIT CODE: " + ddChecksum.getCode());
                        for (String line : ddChecksum.getOut()) {
                            publishProgress("STDOUT: " + line);
                        }
                        for (String line : ddChecksum.getErr()) {
                            publishProgress("STDERR: " + line);
                        }
                        return "ddChecksum ERROR";
                    }

                    Shell.Result ddDigest = Shell.su(
                            String.format(
                                    "printf '%s' | busybox dd of='%s' bs=1 seek=%s conv=notrunc",
                                    byteNotation(digestArray), baseApkLocation, 12)).exec();
                    if (!ddDigest.isSuccess()) {
                        publishProgress("ddChecksum EXIT CODE: " + ddDigest.getCode());
                        for (String line : ddDigest.getOut()) {
                            publishProgress("STDOUT: " + line);
                        }
                        for (String line : ddDigest.getErr()) {
                            publishProgress("STDERR: " + line);
                        }
                        return "ddDigest ERROR";
                    }

                    Shell.Result ddExtraBytes = Shell.su(
                            String.format(
                                    "printf '%s' | busybox dd of='%s' bs=1 seek=%s conv=notrunc",
                                    byteNotation(extraBytes), baseApkLocation, extraOffset +
                                            extraPattern.length)).exec();
                    if (!ddExtraBytes.isSuccess()) {
                        publishProgress("ddChecksum EXIT CODE: " + ddExtraBytes.getCode());
                        for (String line : ddExtraBytes.getOut()) {
                            publishProgress("STDOUT: " + line);
                        }
                        for (String line : ddExtraBytes.getErr()) {
                            publishProgress("STDERR: " + line);
                        }
                        return "ddExtraBytes ERROR";
                    }

                    break;
                case CP:
                    File backupApkFolder = new File("/data/local/tmp/mcmod_node");
                    File backupApkFile = new File("/data/local/tmp/mcmod_node/mcd.apk.bak");

                    // Check if folder exists
                    if (!backupApkFolder.exists()) {
                        // Create folder command
                        publishProgress("Creating folder " + backupApkFolder.getAbsolutePath());
                        String createFolderCommand = "mkdir " + backupApkFolder.getAbsolutePath();
                        Shell.Result createFolder = Shell.su(createFolderCommand).exec();
                        if (!createFolder.isSuccess()) {
                            outputCommand(createFolder);
                            return "createFolder: error";
                        }

                        // Fix permissions command
                        publishProgress("Fixing folder permissions...");
                        String fixFolderCommand = "chmod 777 " + backupApkFolder.getAbsolutePath();
                        Shell.Result fixFolder = Shell.su(fixFolderCommand).exec();
                        if (!fixFolder.isSuccess()) {
                            outputCommand(fixFolder);
                            return "fixFolder: error";
                        }
                    }

                    // Check if backup exists
                    if (!backupApkFile.exists()) {
                        // Copy file command
                        publishProgress("Copying " + baseApkLocation + " to " + backupApkFile.getAbsolutePath());
                        String copyFileCommand = String.format("cp %s %s", baseApkLocation,
                                backupApkFile.getAbsolutePath());
                        Shell.Result copyFile = Shell.su(copyFileCommand).exec();
                        if (!copyFile.isSuccess()) {
                            outputCommand(copyFile);
                            return "copyFile: error";
                        }

                        // Fix permissions command
                        publishProgress("Fixing file permissions...");
                        String fixFileCommand = "chown root:root " + backupApkFile.getAbsolutePath() +
                                " && chmod 666 " + backupApkFile.getAbsolutePath();
                        Shell.Result fixFile = Shell.su(fixFileCommand).exec();
                        if (!fixFile.isSuccess()) {
                            outputCommand(fixFile);
                            return "fixFile: error";
                        }
                    }

                    // Save modded APK
                    publishProgress("Writing to " + backupApkFile.getAbsolutePath() + "...");
                    OutputStream stream = new FileOutputStream(backupApkFile);
                    stream.write(apkData);
                    stream.close();

                    // Overwrite original
                    publishProgress("Copying " + backupApkFile.getAbsolutePath() + " to " + baseApkLocation);
                    Shell.Result overwriteCopy = Shell.su(String.format("cp %s %s", backupApkFile.getAbsolutePath(), baseApkLocation)).exec();
                    if (!overwriteCopy.isSuccess()) {
                        publishProgress("overwriteCopy EXIT CODE: " + overwriteCopy.getCode());
                        for (String line : overwriteCopy.getOut()) {
                            publishProgress("STDOUT: " + line);
                        }
                        for (String line : overwriteCopy.getErr()) {
                            publishProgress("STDERR: " + line);
                        }
                        return "overwriteCopy: error";
                    }

                    Shell.Result overwriteFix = Shell.su("chown system:system " + baseApkLocation + " && chmod 644 " + baseApkLocation).exec();
                    if (!overwriteFix.isSuccess()) {
                        publishProgress("overwriteFix EXIT CODE: " + overwriteFix.getCode());
                        for (String line : overwriteFix.getOut()) {
                            publishProgress("STDOUT: " + line);
                        }
                        for (String line : overwriteFix.getErr()) {
                            publishProgress("STDERR: " + line);
                        }
                        return "overwriteFix: error";
                    }

                    break;
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
            sendLog("ERROR: " + e.getMessage());
            if (e.getMessage().equals("com.mcdonalds.mobileapp"))
                apkDigest = "McDonald's app is not installed. The node won't work.";
        }

        return apkDigest;
    }

    @Override
    protected void onPostExecute(String result) {
        sendLog("DigestTask result: " + result);
    }

    @Override
    protected void onProgressUpdate(String... messages) {
        for (String message : messages) {
            sendLog(message);
        }
    }

    @Override
    protected void onCancelled(String aString) {

    }

    @Override
    protected void onCancelled() {

    }
}
