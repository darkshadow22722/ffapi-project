package mcdonalds.core;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.Nullable;

import xyz.hexile.mcmodpoc.CouponFragment;
import xyz.hexile.mcmodpoc.EmailFragment;
import xyz.hexile.mcmodpoc.LayoutUtils;
import xyz.hexile.mcmodpoc.Utils;

public class MainActivity extends Activity {

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int p8 = LayoutUtils.dpAsPixels(this, 8);
        int p16 = LayoutUtils.dpAsPixels(this, 16);

        // Set theme
        setTheme(android.R.style.Theme_Material);

        // Add back button
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Set title
        Intent intent = getIntent();
        String selectedPoc = intent.getStringExtra("poc");
        setTitle("[PoC] " + selectedPoc);

        ScrollView rootLayout = new ScrollView(this);
        rootLayout.setLayoutParams(LayoutUtils.MATCH_MATCH);

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setLayoutParams(LayoutUtils.MATCH_MATCH);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(p16, p16, p16, p16);
        mainLayout.setId(12345);
        rootLayout.addView(mainLayout);

        mainLayout.addView(LayoutUtils.header(this));

        switch (selectedPoc) {
            case "email":
                getFragmentManager().beginTransaction().add(mainLayout.getId(), new EmailFragment(), selectedPoc).commit();
                break;
            case "coupon":
                getFragmentManager().beginTransaction().add(mainLayout.getId(), new CouponFragment(), selectedPoc).commit();
                break;
        }

        setContentView(rootLayout);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
