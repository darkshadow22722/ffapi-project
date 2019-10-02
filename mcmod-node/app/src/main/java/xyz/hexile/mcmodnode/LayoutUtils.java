package xyz.hexile.mcmodnode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import mcdonalds.app.AppApplication;

public class LayoutUtils {
    public static final int BLACK = Color.parseColor("#000000");
    public static final int RED = Color.parseColor("#ff0000");
    public static final int GREEN = Color.parseColor("#00ff00");
    public static final int BLUE = Color.parseColor("#0000ff");
    public static final int WHITE = Color.parseColor("#ffffff");
    public static final int WHITE_50A = Color.parseColor("#80ffffff");

    public static final LinearLayout.LayoutParams MATCH_MATCH =
            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);

    public static final LinearLayout.LayoutParams MATCH_WRAP =
            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

    public static final LinearLayout.LayoutParams MATCH_WRAP_FILL =
            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);

    public static final LinearLayout.LayoutParams WRAP_WRAP =
            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

    public static final LinearLayout.LayoutParams WRAP_WRAP_FILL =
            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);

    public static final LinearLayout.LayoutParams WRAP_WRAP_CH =
            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

    static {
        WRAP_WRAP_CH.gravity = Gravity.CENTER;
    }

    public static int dpAsPixels(Context context, int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    @SuppressLint("SetTextI18n")
    public static LinearLayout header(Context context) {
        int p16 = LayoutUtils.dpAsPixels(context, 16);

        // Main layout
        LinearLayout mainLayout = new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.HORIZONTAL);
        mainLayout.setLayoutParams(MATCH_WRAP);
        mainLayout.setGravity(Gravity.CENTER);
        mainLayout.setPadding(0, 0, 0, p16);

        // Text Layout
        LinearLayout textLayout = new LinearLayout(context);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        textLayout.setLayoutParams(WRAP_WRAP);

        // Title
        TextView titleTextView = new TextView(context);
        titleTextView.setLayoutParams(LayoutUtils.MATCH_WRAP);
        titleTextView.setText(AppApplication.TITLE);
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        titleTextView.setTypeface(titleTextView.getTypeface(), Typeface.BOLD);
        titleTextView.setTextColor(LayoutUtils.WHITE);
        titleTextView.setGravity(Gravity.CENTER);
        textLayout.addView(titleTextView);

        // Copyright
        TextView copyrightTextView = new TextView(context);
        copyrightTextView.setLayoutParams(LayoutUtils.MATCH_WRAP);
        copyrightTextView.setGravity(Gravity.CENTER);
        copyrightTextView.setTextColor(LayoutUtils.WHITE_50A);
        copyrightTextView.setText("Copyright 2019 Giacomo Ferretti");
        copyrightTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        textLayout.addView(copyrightTextView);

        // Android Device Info
        TextView buildInfoTextView = new TextView(context);
        buildInfoTextView.setLayoutParams(LayoutUtils.WRAP_WRAP_CH);
        buildInfoTextView.setTextColor(LayoutUtils.WHITE);
        buildInfoTextView.setGravity(Gravity.CENTER);
        buildInfoTextView.setPadding(p16, 0, 0, 0);
        buildInfoTextView.setText(BuildConfig.VERSION_NAME + " [" + BuildConfig.VERSION_CODE + "]");

        mainLayout.addView(textLayout);
        mainLayout.addView(buildInfoTextView);

        return mainLayout;
    }
}
