package mcdonalds.restaurant.network;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.safetynet.SafetyNet;
import com.topjohnwu.superuser.Shell;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import mcdonalds.app.AppApplication;
import mcdonalds.core.SplashActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xyz.hexile.mcmodnode.BuildConfig;
import xyz.hexile.mcmodnode.ChangeDigestAsyncTask;
import xyz.hexile.mcmodnode.Utils;

public class RestaurantService extends Service {
    private static final int SERVICE_ID = 666;
    private static final String NOTIFICATION_CHANNEL_ID = "mcmode_node";
    private static final String NOTIFICATION_CHANNEL_NAME = "McMod Node";

    private static final String BASE_URL = "https://mcmod.hexile.xyz";
    //private static final String BASE_URL = "http://192.168.0.30:8000";
    private static final String GET_TASK = BASE_URL + "/api/v1/token/task";
    private static final String POST_TOKEN = BASE_URL + "/api/v1/token/post";

    private static final String USER_AGENT = "mcmod-node/" + BuildConfig.VERSION_NAME;

    private static final String AUTH_TYPE = "token_factory_key";
    private static final String AUTH_KEY = "plexure_sucks_also_i_am_your_slave_mcmod";

    private static final int REFRESH_INTERVAL = 30;

    private static final String INTENT_STOP = "mcmod.service.stop";

    public static final String BROADCAST_FILTER = "mcmod.service.broadcast";
    public static final String BROADCAST_LOG = BROADCAST_FILTER + ".log";
    public static final String BROADCAST_LOG_TAG = BROADCAST_LOG + ".tag";
    public static final String BROADCAST_LOG_MESSAGE = BROADCAST_LOG + ".message";

    public static int NOTIFICATION_ICON = android.R.drawable.ic_dialog_info;
    public static int NOTIFICATION_ACTION_ICON = android.R.drawable.ic_media_pause;
    static {
        if (!BuildConfig.DEBUG) {
            NOTIFICATION_ICON = 2131230950;
            NOTIFICATION_ACTION_ICON = 2131230828;
        }

        /* Shell.Config methods shall be called before any shell is created
         * This is the why in this example we call it in a static block
         * The followings are some examples, check Javadoc for more details */
        Shell.Config.setFlags(Shell.FLAG_REDIRECT_STDERR);
        //Shell.Config.verboseLogging(BuildConfig.DEBUG);
        Shell.Config.verboseLogging(true);
        Shell.Config.setTimeout(10);
    }

    private LocalBroadcastManager broadcaster;
    private ScheduledExecutorService scheduledExecutorService;
    private String nodeId;
    private String nodeName;
    private String nodeDatabase;
    private String nodeUuid;
    private int currentId = 24806; // Placeholder ID, can be anything

    private boolean isRunning = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        SharedPreferences sharedPreferences =
                getSharedPreferences(AppApplication.SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        nodeId = Utils.getAndroidId(this);
        nodeName = sharedPreferences.getString(AppApplication.SETTINGS_NODE_NAME, "default");
        nodeDatabase = sharedPreferences.getString(AppApplication.SETTINGS_NODE_DB, "default");
        nodeUuid = sharedPreferences.getString(AppApplication.SETTINGS_NODE_UUID, "null");
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRunning = true;

        if (intent.getAction() != null)
            if (intent.getAction().equals(INTENT_STOP)) {
                isRunning = false;
                stopSelf();
            }

        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(this::checkStatus,
                0, REFRESH_INTERVAL, TimeUnit.SECONDS);

        RestaurantService context = this;
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            Thread timeoutThread = new Thread(() -> {
                ChangeDigestAsyncTask changeDigestAsyncTask = new ChangeDigestAsyncTask(context);
                changeDigestAsyncTask.execute();
                try {
                    changeDigestAsyncTask.get(10, TimeUnit.SECONDS);
                } catch (ExecutionException | InterruptedException | TimeoutException e) {
                    e.printStackTrace();
                    changeDigestAsyncTask.interrupt();
                }
            });
            timeoutThread.setDaemon(true);
            timeoutThread.start();
            }, 0, 30, TimeUnit.MINUTES);

        showNotification();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        scheduledExecutorService.shutdown();
        AppApplication.client.dispatcher().cancelAll();
    }

    public void sendLog(String tag, String message) {
        Intent intent = new Intent(BROADCAST_FILTER);
        intent.putExtra(BROADCAST_LOG_TAG, tag);
        intent.putExtra(BROADCAST_LOG_MESSAGE, message);
        broadcaster.sendBroadcast(intent);
    }

    private void checkStatus() {
        if (!isRunning)
            return;

        if (AppApplication.client.dispatcher().runningCallsCount() == 0 &&
                AppApplication.client.dispatcher().queuedCallsCount() == 0) {
            Log.d("checkStatus", "No running task.");
            sendLog("I", "Starting...");
            getTask();
        }
    }

    private void showNotification() {
        PendingIntent activityIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, SplashActivity.class), 0);

        Intent stopIntent = new Intent(this, RestaurantService.class);
        stopIntent.setAction(INTENT_STOP);
        PendingIntent stopPendingIntent = PendingIntent.getService(this, 0,
                stopIntent, 0);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_NAME)
                .setColor(Color.parseColor("#ff0000"))
                .setSmallIcon(NOTIFICATION_ICON)
                .setContentTitle(AppApplication.TITLE)
                .setContentText("Running...")
                .setOngoing(true)
                .addAction(NOTIFICATION_ACTION_ICON, "Stop", stopPendingIntent)
                .setContentIntent(activityIntent)
                .build();

        startForeground(SERVICE_ID, notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(){
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_NONE);

        channel.setLightColor(Color.parseColor("#ff0000"));
        channel.setImportance(NotificationManager.IMPORTANCE_NONE);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(channel);
    }

    private boolean isTokenValid(String token) {
        String payload = new String(Base64.decode(token.split("\\.")[1], Base64.DEFAULT));
        sendLog("D:SafetyNetPayload", payload);

        try {
            JSONObject jsonObject = new JSONObject(payload);

            if (jsonObject.has("error")) {
                sendLog("E:SafetyNet", jsonObject.getString("error"));
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public Request.Builder nodeRequestBuilder() {
        return new Request.Builder()
                .addHeader("User-Agent", USER_AGENT)
                .addHeader("Authorization", AUTH_TYPE + " " + AUTH_KEY)
                .addHeader("X-Android-Release", Build.VERSION.RELEASE)
                .addHeader("X-Android-Device", Build.MANUFACTURER + ":" + Build.MODEL)
                .addHeader("X-Node-ID", nodeId)
                .addHeader("X-Node-Name", nodeName)
                .addHeader("X-Node-UUID", nodeUuid)
                .addHeader("X-Node-Database", nodeDatabase)
                .addHeader("X-Node-Version-Code", String.valueOf(BuildConfig.VERSION_CODE))
                .addHeader("X-Node-Version-Name", BuildConfig.VERSION_NAME);
    }

    private void getTask() {
        if (!isRunning)
            return;

        Request getTaskRequest = nodeRequestBuilder()
                .url(GET_TASK)
                .build();

        AppApplication.client.newCall(getTaskRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.d("getTask:onFailure", e.getMessage());
                sendLog("E", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body().string();

                Log.d("getTask:onResponse:code", String.valueOf(response.code()));
                Log.d("getTask:onResponse:body", body);

                if (response.code() != 200) {
                    sendLog("E", body);
                    return;
                }

                try {
                    JSONObject jsonResponse = new JSONObject(body);

                    String authorization = jsonResponse.getString("authorization");
                    String vmob = jsonResponse.getString("vmob");
                    currentId = jsonResponse.getInt("id");
                    String name = jsonResponse.getString("name");
                    sendLog("I", "Generating offer " + currentId + "...\n" + name);
                    if (!isRunning)
                        return;
                    postToken(authorization, vmob);
                } catch (JSONException e) {
                    sendLog("E", e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private void postToken(String auth, String vmob) {
        if (!isRunning)
            return;

        String body = Utils.couponBody(String.valueOf(currentId));
        String method = "post";
        String host = "dif-dot-prd-euw-gmal-mcdonalds.appspot.com";
        String digest = "SHA-256=" + Utils.digestSha256(body);
        String date = Utils.getDate();
        int contentLength = body.length();

        byte[] nonceBytes = Utils.digestBytesSha512(String.format(
                "(request-target): %s %s\n" +
                "host: %s\n" +
                "date: %s\n" +
                "authorization: %s\n" +
                "digest: %s\n" +
                "content-length: %s",
                method, "/plexure/v1/con/v3/consumers/redeemedOffers", host,
                date, auth, digest, contentLength));

        SafetyNet.getClient(this).attest(nonceBytes, AppApplication.API_KEY)
                .addOnSuccessListener(x -> {
                    if (!isRunning)
                        return;

                    String token = x.getJwsResult();

                    if (!isTokenValid(token))
                        return;

                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("token", token);
                        jsonObject.put("body", body);
                        jsonObject.put("authorization", auth);
                        jsonObject.put("date", date);
                        jsonObject.put("digest", digest);
                        jsonObject.put("vmob", vmob);

                        RequestBody reqBody = RequestBody.create(MediaType.parse("application/json"),
                                jsonObject.toString());

                        Request postTokenRequest = nodeRequestBuilder()
                                .url(POST_TOKEN)
                                .post(reqBody)
                                .build();

                        AppApplication.client.newCall(postTokenRequest).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Log.d("postToken:onFailure", e.getMessage());
                                sendLog("E", e.getMessage());
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String body = response.body().string();

                                Log.d("postToken:onResponse", String.valueOf(response.code()));
                                Log.d("postToken:onResponse", body);


                                if (response.code() != 201) {
                                    sendLog("E", body);
                                } else {
                                    sendLog("I", body);
                                }

                                if (!isRunning)
                                    return;
                                getTask();
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                })
                .addOnFailureListener(e -> {
                    switch (e.getMessage()) {
                        case "16: ":
                            sendLog("E:16", "SafetyNet: CANCELED (ratelimit)");
                            break;
                        case "7: ":
                            sendLog("E:7", "SafetyNet: NETWORK_ERROR");
                            break;
                        default:
                            sendLog("E", e.getMessage());
                            break;
                    }
                    e.printStackTrace();
                });
    }
}
