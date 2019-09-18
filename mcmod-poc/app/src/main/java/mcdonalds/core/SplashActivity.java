package mcdonalds.core;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.Nullable;

import mcdonalds.app.AppApplication;
import xyz.hexile.mcmodpoc.LayoutUtils;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int p8 = LayoutUtils.dpAsPixels(this, 8);
        int p16 = LayoutUtils.dpAsPixels(this, 16);

        // Set theme
        setTheme(android.R.style.Theme_Material);
        setTitle(AppApplication.TITLE);

        ScrollView rootLayout = new ScrollView(this);
        rootLayout.setLayoutParams(LayoutUtils.MATCH_MATCH);

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setLayoutParams(LayoutUtils.MATCH_MATCH);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(p16, p16, p16, p16);

        mainLayout.addView(LayoutUtils.header(this));

        // Email Button
        Button emailButton = new Button(this);
        emailButton.setText("Email");
        emailButton.setOnClickListener(v -> {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("poc", "email");
            startActivity(i);
        });

        // Coupon Button
        Button couponButton = new Button(this);
        couponButton.setText("Coupon");
        couponButton.setOnClickListener(v -> {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("poc", "coupon");
            startActivity(i);
        });

        // Donate Button
        Button donateButton = new Button(this);
        donateButton.setText("Donate");
        donateButton.setOnClickListener(v -> {
            String url = "https://paypal.me/hexile0";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });

        // Set layout
        rootLayout.addView(mainLayout);
        mainLayout.addView(emailButton);
        mainLayout.addView(couponButton);
        mainLayout.addView(donateButton);
        setContentView(rootLayout);
    }
}
