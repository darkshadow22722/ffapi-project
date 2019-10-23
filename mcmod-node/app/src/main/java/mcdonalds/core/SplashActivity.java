package mcdonalds.core;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Html;
import android.text.InputFilter;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.topjohnwu.superuser.Shell;

import java.text.SimpleDateFormat;
import java.util.Date;

import mcdonalds.app.AppApplication;
import mcdonalds.restaurant.network.RestaurantService;
import xyz.hexile.mcmodnode.BuildConfig;
import xyz.hexile.mcmodnode.LayoutUtils;
import xyz.hexile.mcmodnode.Utils;

public class SplashActivity extends Activity {

    private Button startButton;
    private EditText nodeNameEditText;
    private EditText nodeDatabaseEditText;
    private TextView statusTextView;
    private CheckBox autoscrollCheckBox;
    private ScrollView logScrollView;
    private TextView logTextView;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPreferencesEditor;

    private StringBuilder logBuilder = new StringBuilder();

    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int p8 = LayoutUtils.dpAsPixels(this, 8);
        int p16 = LayoutUtils.dpAsPixels(this, 16);
        int p24 = LayoutUtils.dpAsPixels(this, 24);

        LinearLayout.LayoutParams MATCH_WRAP_M16 =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        MATCH_WRAP_M16.setMargins(p16, 0, p16, p16);

        LinearLayout.LayoutParams MATCH_WRAP_M16_8 =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        MATCH_WRAP_M16_8.setMargins(p16, 0, p16, p8);

        sharedPreferences =
                getSharedPreferences(AppApplication.SHARED_PREFS_FILE, Context.MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();

        // Set theme
        setTheme(android.R.style.Theme_Material_NoActionBar);
        setTitle(AppApplication.TITLE);

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setLayoutParams(LayoutUtils.MATCH_MATCH);
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        // Main layout
        LinearLayout headerLayout = new LinearLayout(this);
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);
        headerLayout.setLayoutParams(LayoutUtils.MATCH_WRAP);
        headerLayout.setGravity(Gravity.CENTER);
        headerLayout.setPadding(p16, p16, p16, p16);

        // Text Layout
        LinearLayout textLayout = new LinearLayout(this);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        textLayout.setLayoutParams(LayoutUtils.WRAP_WRAP);
        textLayout.setPadding(0, 0, p16, 0);

        // Title
        TextView titleTextView = new TextView(this);
        titleTextView.setLayoutParams(LayoutUtils.MATCH_WRAP);
        titleTextView.setPadding(0, 0, 0, p8);
        titleTextView.setText(AppApplication.TITLE);
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        titleTextView.setTypeface(titleTextView.getTypeface(), Typeface.BOLD);
        titleTextView.setTextColor(LayoutUtils.WHITE);
        titleTextView.setGravity(Gravity.CENTER);
        textLayout.addView(titleTextView);

        // Copyright
        TextView copyrightTextView = new TextView(this);
        copyrightTextView.setLayoutParams(LayoutUtils.MATCH_WRAP);
        copyrightTextView.setGravity(Gravity.CENTER);
        copyrightTextView.setTextColor(LayoutUtils.WHITE_50A);
        copyrightTextView.setText(BuildConfig.VERSION_NAME + " [" + BuildConfig.VERSION_CODE + "]");
        copyrightTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        textLayout.addView(copyrightTextView);

        // Start Button
        startButton = new Button(this);
        startButton.setLayoutParams(LayoutUtils.WRAP_WRAP_CH);
        startButton.setGravity(Gravity.CENTER);
        startButton.setText("Start");
        startButton.setOnClickListener(v -> startNode());
        headerLayout.addView(textLayout);
        headerLayout.addView(startButton);

        // EditText
        String nodeName = sharedPreferences.getString(AppApplication.SETTINGS_NODE_NAME, "");
        nodeNameEditText = new EditText(this);
        nodeNameEditText.setLayoutParams(MATCH_WRAP_M16_8);
        nodeNameEditText.setHint("Node Name");
        nodeNameEditText.setText(nodeName);
        InputFilter filter = (source, start, end, dest, dstart, dend) -> {
            String extraChars = "-+_*\\|!\"£$%&/()='?^@°§#€{}[]<>.,:;";

            // Only keep characters that are alphanumeric
            StringBuilder builder = new StringBuilder();
            for (int i = start; i < end; i++) {
                char c = source.charAt(i);
                if (Character.isLetterOrDigit(c) || extraChars.indexOf(c) >= 0) {
                    builder.append(c);
                }
            }

            // If all characters are valid, return null, otherwise only return the filtered characters
            boolean allCharactersValid = (builder.length() == end - start);
            return allCharactersValid ? null : builder.toString();
        };
        nodeNameEditText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(30),
                filter});

        String databaseName = sharedPreferences.getString(AppApplication.SETTINGS_NODE_DB, "default");
        nodeDatabaseEditText = new EditText(this);
        nodeDatabaseEditText.setLayoutParams(MATCH_WRAP_M16_8);
        nodeDatabaseEditText.setHint("Database Name");
        nodeDatabaseEditText.setText(databaseName);
        nodeDatabaseEditText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(30),
                filter});

        // Status TextView
        statusTextView = new TextView(this);
        statusTextView.setLayoutParams(MATCH_WRAP_M16_8);

        // AndroidID TextView
        TextView androidIdTextView = new TextView(this);
        androidIdTextView.setLayoutParams(MATCH_WRAP_M16_8);
        androidIdTextView.setText(Html.fromHtml("<b>Android ID:</b> " + Utils.getAndroidId(this)));

        // UUID TextView
        TextView uuidTextView = new TextView(this);
        uuidTextView.setLayoutParams(MATCH_WRAP_M16_8);
        uuidTextView.setText(Html.fromHtml("<b>Node UUID:</b> " +
                sharedPreferences.getString(AppApplication.SETTINGS_NODE_UUID, "error")));

        // Autoscroll CheckBox
        int[] attrs = new int[] { android.R.attr.listChoiceIndicatorMultiple };
        TypedArray ta = getTheme().obtainStyledAttributes(attrs);
        Drawable checkBoxDrawable = ta.getDrawable(0);
        autoscrollCheckBox = new CheckBox(this);
        autoscrollCheckBox.setLayoutParams(MATCH_WRAP_M16);
        autoscrollCheckBox.setCompoundDrawablesWithIntrinsicBounds(null, null, checkBoxDrawable, null);
        autoscrollCheckBox.setButtonDrawable(null);
        autoscrollCheckBox.setText("Log autoscroll");
        autoscrollCheckBox.setChecked(true);

        // Log ScrollView
        logScrollView = new ScrollView(this);
        logScrollView.setLayoutParams(LayoutUtils.MATCH_MATCH);
        logScrollView.setPadding(p8, p8, p8, p8);
        logScrollView.setBackgroundColor(Color.parseColor("#000000"));

        // Log TextView
        logTextView = new TextView(this);
        logTextView.setLayoutParams(LayoutUtils.MATCH_MATCH);
        logTextView.setTextColor(LayoutUtils.WHITE);
        logTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        logTextView.setTypeface(Typeface.MONOSPACE);
        logScrollView.addView(logTextView);

        mainLayout.addView(headerLayout);
        mainLayout.addView(nodeNameEditText);
        mainLayout.addView(nodeDatabaseEditText);
        mainLayout.addView(statusTextView);
        mainLayout.addView(androidIdTextView);
        mainLayout.addView(uuidTextView);
        mainLayout.addView(autoscrollCheckBox);
        mainLayout.addView(logScrollView);

        setContentView(mainLayout);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.hasExtra(RestaurantService.BROADCAST_LOG_TAG) &&
                        intent.hasExtra(RestaurantService.BROADCAST_LOG_MESSAGE)) {
                    updateLog(intent.getStringExtra(RestaurantService.BROADCAST_LOG_TAG),
                            intent.getStringExtra(RestaurantService.BROADCAST_LOG_MESSAGE));
                }
            }
        };

        // Check root access
        if (Shell.rootAccess()) {
            Toast.makeText(this, "Gained root access!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No root access.\nThe app may not work properly.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateStatus();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(RestaurantService.BROADCAST_FILTER));
    }

    @Override
    protected void onStop() {
        if (broadcastReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        }
        super.onStop();
    }

    public void updateLog(String tag, String message) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

        String textColor = "#ffffff";
        if (tag.startsWith("E"))
            textColor = "#ff0000";

        logBuilder
                .append("<font color=\"")
                .append(textColor)
                .append("\">[")
                .append(simpleDateFormat.format(new Date()))
                .append(" ")
                .append(tag)
                .append("] ")
                .append(message)
                .append("</font><br>");

        logTextView.setText(Html.fromHtml(logBuilder.toString()), TextView.BufferType.SPANNABLE);

        if (autoscrollCheckBox.isChecked()) {
            logScrollView.post(() -> {
                logScrollView.fullScroll(View.FOCUS_DOWN);
            });
        }
    }

    public void updateStatus(String status) {
        if (!isServiceRunning(RestaurantService.class)) {
            statusTextView.setText(Html.fromHtml("<b>Status:</b> " + status));
        } else {
            statusTextView.setText(Html.fromHtml("<b>Status:</b> " + status));
        }
    }

    public void updateStatus() {
        String button = "Start";
        String status = "disconnected";
        if (isServiceRunning(RestaurantService.class)) {
            button = "Stop";
            status = "connected";
        }

        startButton.setText(button);
        statusTextView.setText(Html.fromHtml("<b>Status:</b> " + status));
    }

    void startNode() {
        if (isNetworkConnected()) {
            Intent serviceIntent = new Intent(this, RestaurantService.class);

            if (!isServiceRunning(RestaurantService.class)) {
                String nodeName = nodeNameEditText.getText().toString();
                if (nodeName.isEmpty())
                    nodeName = "default_node";

                String nodeDatabase = nodeDatabaseEditText.getText().toString();
                if (nodeDatabase.isEmpty())
                    nodeDatabase = "default";


                sharedPreferencesEditor.putString(AppApplication.SETTINGS_NODE_NAME, nodeName);
                sharedPreferencesEditor.putString(AppApplication.SETTINGS_NODE_DB, nodeDatabase);
                sharedPreferencesEditor.apply();

                updateLog("I", "Starting " + nodeNameEditText.getText().toString() + " for " +
                        nodeDatabaseEditText.getText().toString() + " [" +
                        BuildConfig.VERSION_NAME + "]...");

                startService(serviceIntent);
                updateStatus();
            } else {
                updateLog("I","Stopping Node...");

                stopService(serviceIntent);
                updateStatus();
            }
        } else {
            Toast.makeText(this, "You are not connected to a network.", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
