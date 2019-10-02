package mcdonalds.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

import okhttp3.OkHttpClient;

public class AppApplication extends Application {
    public static final String TITLE = "McMod Node";
    public static final String API_KEY = "YOU_HAVE_TO_GET_THIS_YOURSELF";

    public static final String SHARED_PREFS_FILE = "node_settings";
    public static final String SETTINGS_NODE_DB = "node_db";
    public static final String SETTINGS_NODE_NAME = "node_name";
    public static final String SETTINGS_NODE_UUID = "node_uuid";

    public static OkHttpClient client = new OkHttpClient();

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences sharedPreferences =
                getSharedPreferences(SHARED_PREFS_FILE, Context.MODE_PRIVATE);

        if (sharedPreferences.getString(SETTINGS_NODE_UUID, "").isEmpty()) {
            SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
            sharedPreferencesEditor.putString(SETTINGS_NODE_UUID, UUID.randomUUID().toString());
            sharedPreferencesEditor.apply();
        }
    }
}
