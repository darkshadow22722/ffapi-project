package xyz.hexile.mcmodpoc;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.safetynet.SafetyNet;

import java.io.IOException;

import mcdonalds.app.AppApplication;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EmailFragment extends Fragment {

    OkHttpClient client = new OkHttpClient();

    private String id = Utils.randomAndroidId();
    private String username = Utils.encryptDES("DeviceUsernamePrefix" + id);
    private String password = Utils.encryptDES("DevicePasswordPrefix" + id);
    private String vmob = Utils.encryptDES("co.vmob.android.sdk." + id);
    private String authorization;
    private String registrationName = Utils.randomString(5);
    private String registrationLastname = Utils.randomString(5);
    private String registrationEmail = Utils.randomEmail();
    private String registrationPassword = Utils.randomPassword();
    private String registrationBody = Utils.registrationBody(registrationName, registrationLastname, registrationEmail, registrationPassword);
    private byte[] nonce;
    private String digest;
    private String date;
    private String token;

    private void smallTextView(TextView textView) {
        textView.setLayoutParams(LayoutUtils.MATCH_WRAP);
        //textView.setTextColor(WHITE);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
    }

    private void checkValuesBody(Button button) {
        if (registrationName != null && registrationLastname != null && registrationEmail != null &&
                registrationPassword != null && authorization != null) {
            button.setEnabled(true);
        }
    }

    private void updateRegistrationBody(TextView textView) {
        registrationBody = Utils.registrationBody(registrationName, registrationLastname, registrationEmail, registrationPassword);
        textView.setText(registrationBody);
    }

    private Request deviceRegistrationRequest(String username, String password, String vmob) {
        String data = String.format("{\"grant_type\":\"password\",\"username\":\"%s\",\"password\":\"%s\"}", username, password);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), data);
        Request.Builder requestBuilder = new Request.Builder()
                //.addHeader("Digest", "SHA-256=" + Utils.digestSha256(data))
                .addHeader("x-vmob-device_os_version", "9")
                .addHeader("x-vmob-device_type", "a")
                .addHeader("x-vmob-uid", vmob)
                .addHeader("Date", Utils.getDate())
                .addHeader("x-vmob-device_screen_resolution", "1080x1920")
                .addHeader("X-Dif-Platform", "android")
                .addHeader("x-vmob-device", Build.BRAND + " " + Build.MODEL)
                .addHeader("Accept-Language", "it-IT")
                .addHeader("x-vmob-device_utc_offset", "+2:00")
                .addHeader("x-vmob-device_network_type", "wifi")
                .addHeader("x-vmob-application_version", "3587")
                .addHeader("User-Agent", "okhttp/3.12.0")
                .url("https://dif-dot-prd-euw-gmal-mcdonalds.appspot.com/plexure/v1/con/v3/DeviceRegistration")
                .post(body);

        return requestBuilder.build();
    }

    private Request emailRegistrationRequest() {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), registrationBody);
        Request.Builder requestBuilder = new Request.Builder()
                .addHeader("Digest", "SHA-256=" + digest)
                .addHeader("x-vmob-device_os_version", "6.0.1")
                .addHeader("x-vmob-device_type", "a")
                .addHeader("x-vmob-uid", vmob)
                .addHeader("Authorization", "bearer " + authorization)
                .addHeader("Date", date)
                .addHeader("X-Dif-Authorization", "Token headers=\"(request-target) host date authorization digest content-length\",token=" + token)
                .addHeader("x-vmob-device_screen_resolution", "1080x1920")
                .addHeader("X-Dif-Platform", "android")
                .addHeader("x-vmob-device", Build.BRAND + " " + Build.MODEL)
                .addHeader("Accept-Language", "it-IT")
                .addHeader("x-vmob-device_utc_offset", "+2:00")
                .addHeader("x-vmob-device_network_type", "wifi")
                .addHeader("x-vmob-application_version", "3587")
                .header("User-Agent", "okhttp/3.12.0")
                .url("https://dif-dot-prd-euw-gmal-mcdonalds.appspot.com/plexure/v1/con/v3/emailRegistrations")
                .post(body);

        return requestBuilder.build();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.out.println(this.getClass().getName());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Context context = getActivity();

        final int DP_8 = LayoutUtils.dpAsPixels(context, 8);

        // Main layout
        LinearLayout mainLayout = new LinearLayout(context);
        mainLayout.setLayoutParams(LayoutUtils.MATCH_WRAP);
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        // Android ID generator
        LinearLayout androidIdLayout = new LinearLayout(context);
        androidIdLayout.setLayoutParams(LayoutUtils.MATCH_WRAP);
        androidIdLayout.setOrientation(LinearLayout.HORIZONTAL);

        // Android ID generator - EditText
        EditText androidIdEditText = new EditText(context);
        androidIdEditText.setLayoutParams(LayoutUtils.WRAP_WRAP_FILL);
        androidIdEditText.setHint("Android ID");
        /*androidIdEditText.setTextColor(WHITE);
        androidIdEditText.setHintTextColor(WHITE_50A);*/
        androidIdEditText.setText(id);

        // Android ID generator - Button
        Button androidIdButton = new Button(context);
        androidIdButton.setLayoutParams(LayoutUtils.WRAP_WRAP);
        androidIdButton.setText("Random");

        androidIdLayout.addView(androidIdEditText);
        androidIdLayout.addView(androidIdButton);

        // Android ID data
        TextView usernameTextView = new TextView(context);
        smallTextView(usernameTextView);
        usernameTextView.setText(username);
        TextView passwordTextView = new TextView(context);
        smallTextView(passwordTextView);
        passwordTextView.setText(password);
        TextView vmobTextView = new TextView(context);
        smallTextView(vmobTextView);
        vmobTextView.setText(vmob);

        // Device Registration Button
        Button deviceRegistrationButton = new Button(context);
        deviceRegistrationButton.setLayoutParams(LayoutUtils.MATCH_WRAP);
        deviceRegistrationButton.setText("1) Device Registration");
        //deviceRegistrationButton.setEnabled(false);

        // Device Registration Response TextViev
        TextView deviceRegistrationTextView = new TextView(context);
        smallTextView(deviceRegistrationTextView);
        deviceRegistrationTextView.setText("Response");

        // Authorization TextView
        TextView authorizationTextView = new TextView(context);
        authorizationTextView.setLayoutParams(LayoutUtils.MATCH_WRAP);
        //authorizationTextView.setTextColor(WHITE);
        authorizationTextView.setPadding(DP_8, DP_8, DP_8, DP_8);
        authorizationTextView.setSingleLine(true);
        authorizationTextView.setText("Authorization");
        authorizationTextView.setHorizontallyScrolling(true);

        // Name EditText
        EditText nameEdit = new EditText(context);
        nameEdit.setLayoutParams(LayoutUtils.MATCH_WRAP);
        nameEdit.setHint("Name");
        //nameEdit.setTextColor(WHITE);
        //nameEdit.setHintTextColor(WHITE_50A);
        nameEdit.setText(registrationName);

        // Lastname EditText
        EditText lastnameEdit = new EditText(context);
        lastnameEdit.setLayoutParams(LayoutUtils.MATCH_WRAP);
        lastnameEdit.setHint("Last Name");
        //lastnameEdit.setTextColor(WHITE);
        //lastnameEdit.setHintTextColor(WHITE_50A);
        lastnameEdit.setText(registrationLastname);

        // Email EditText
        EditText emailEdit = new EditText(context);
        emailEdit.setLayoutParams(LayoutUtils.MATCH_WRAP);
        emailEdit.setHint("Email");
        //emailEdit.setTextColor(WHITE);
        //emailEdit.setHintTextColor(WHITE_50A);
        emailEdit.setText(registrationEmail);

        // Password EditText
        EditText passwordEdit = new EditText(context);
        passwordEdit.setLayoutParams(LayoutUtils.MATCH_WRAP);
        passwordEdit.setHint("Password");
        //passwordEdit.setTextColor(WHITE);
        //passwordEdit.setHintTextColor(WHITE_50A);
        passwordEdit.setText(registrationPassword);

        // Registration Body TextView
        TextView registrationBodyTextView = new TextView(context);
        smallTextView(registrationBodyTextView);
        registrationBodyTextView.setText("Registration Body");

        // SafetyNet Button
        Button safetyNetButton = new Button(context);
        safetyNetButton.setLayoutParams(LayoutUtils.MATCH_WRAP);
        safetyNetButton.setText("2) SafetyNet token");
        //safetyNetButton.setEnabled(false);

        // SafetyNet token
        TextView tokenTextView = new TextView(context);
        tokenTextView.setLayoutParams(LayoutUtils.MATCH_WRAP);
        //tokenTextView.setTextColor(WHITE);
        tokenTextView.setPadding(DP_8, DP_8, DP_8, DP_8);
        tokenTextView.setSingleLine(true);
        tokenTextView.setText("Token");
        tokenTextView.setHorizontallyScrolling(true);

        // SafetyNet payload
        TextView tokenPayloadTextView = new TextView(context);
        smallTextView(tokenPayloadTextView);
        tokenPayloadTextView.setText("Payload");

        // Email Registration Button
        Button emailRegistrationButton = new Button(context);
        emailRegistrationButton.setLayoutParams(LayoutUtils.MATCH_WRAP);
        emailRegistrationButton.setText("3) Email Registration");
        emailRegistrationButton.setEnabled(false);

        // Email Registration Response TextView
        TextView emailRegistrationTextView = new TextView(context);
        smallTextView(emailRegistrationTextView);
        emailRegistrationTextView.setText("Response");

        // Add buttons listeners
        androidIdButton.setOnClickListener(v -> {
            id = Utils.randomAndroidId();
            androidIdEditText.setText(id);
        });

        deviceRegistrationButton.setOnClickListener(v -> {
            ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Device Registration");
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            client.newCall(deviceRegistrationRequest(username, password, vmob)).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    call.cancel();
                    progressDialog.cancel();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String responseString = response.body().string();

                    getActivity().runOnUiThread(() -> {
                        deviceRegistrationTextView.setText(responseString);
                        progressDialog.cancel();
                        authorization = responseString.split("\"")[3];
                        authorizationTextView.setText(authorization);
                    });
                }
            });
        });

        safetyNetButton.setOnClickListener(v -> {
            ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("SafetyNet token");
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            date = Utils.getDate();
            digest = Utils.digestSha256(registrationBody);

            String data = "(request-target): post /plexure/v1/con/v3/emailRegistrations\n" +
                    "host: dif-dot-prd-euw-gmal-mcdonalds.appspot.com\n" +
                    "date: " + date + "\n" +
                    "authorization: bearer " + authorization + "\n" +
                    "digest: SHA-256=" + digest + "\n" +
                    "content-length: " + registrationBody.length();

            nonce = Utils.digestBytesSha512(data);

            SafetyNet.getClient(context).attest(nonce, AppApplication.API_KEY)
                    .addOnSuccessListener(x -> {
                        token = x.getJwsResult();
                        tokenTextView.setText(token);
                        tokenPayloadTextView.setText(new String(Base64.decode(token.split("\\.")[1], Base64.DEFAULT)));
                        emailRegistrationButton.setEnabled(true);
                        progressDialog.cancel();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "There was an error getting the token. Check the log.", Toast.LENGTH_SHORT).show();
                        progressDialog.cancel();
                        e.printStackTrace();
                    });
        });

        emailRegistrationButton.setOnClickListener(v -> {
            ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Email Registration");
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            client.newCall(emailRegistrationRequest()).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    call.cancel();
                    progressDialog.cancel();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String responseString = response.body().string();

                    getActivity().runOnUiThread(() -> {
                        emailRegistrationTextView.setText(responseString);
                        progressDialog.cancel();
                    });
                }
            });
        });

        androidIdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                id = s.toString();
                username = Utils.encryptDES("DeviceUsernamePrefix" + id);
                password = Utils.encryptDES("DevicePasswordPrefix" + id);
                vmob = Utils.encryptDES("co.vmob.android.sdk." + id);
                usernameTextView.setText("Username: DeviceUsernamePrefix" + id + "\n" + username);
                passwordTextView.setText("Password: DevicePasswordPrefix" + id + "\n" + password);
                vmobTextView.setText("Vmob UID: co.vmob.android.sdk." + id + "\n" + vmob);

                // deviceRegistrationButton.setEnabled(s.length() != 0);
                if (username != null && password != null && vmob != null) {
                    deviceRegistrationButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        nameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                registrationName = s.toString();
                checkValuesBody(safetyNetButton);
                updateRegistrationBody(registrationBodyTextView);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        lastnameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                registrationLastname = s.toString();
                checkValuesBody(safetyNetButton);
                updateRegistrationBody(registrationBodyTextView);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        emailEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                registrationEmail = s.toString();
                checkValuesBody(safetyNetButton);
                updateRegistrationBody(registrationBodyTextView);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        passwordEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                registrationPassword = s.toString();
                checkValuesBody(safetyNetButton);
                updateRegistrationBody(registrationBodyTextView);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Add views to main layout
        mainLayout.addView(androidIdLayout);
        mainLayout.addView(usernameTextView);
        mainLayout.addView(passwordTextView);
        mainLayout.addView(vmobTextView);
        mainLayout.addView(deviceRegistrationButton);
        mainLayout.addView(deviceRegistrationTextView);
        mainLayout.addView(authorizationTextView);
        mainLayout.addView(nameEdit);
        mainLayout.addView(lastnameEdit);
        mainLayout.addView(emailEdit);
        mainLayout.addView(passwordEdit);
        mainLayout.addView(registrationBodyTextView);
        mainLayout.addView(safetyNetButton);
        mainLayout.addView(tokenTextView);
        mainLayout.addView(tokenPayloadTextView);
        mainLayout.addView(emailRegistrationButton);
        mainLayout.addView(emailRegistrationTextView);

        return mainLayout;
    }
}
