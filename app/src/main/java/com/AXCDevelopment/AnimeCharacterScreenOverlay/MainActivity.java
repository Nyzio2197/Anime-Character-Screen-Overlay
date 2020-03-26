package com.AXCDevelopment.AnimeCharacterScreenOverlay;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Switch onOffSwitch;
    private AnimeCharacter animeCharacter;
    private ImageView overlayPowerBtn;
    private Context context;
    private WindowManager windowManager;
    private WindowManager.LayoutParams params;
    private MediaPlayer mediaPlayer;
    private static final AnimeCharacter[] ANIME_CHARACTERS = new AnimeCharacter[]
            {new AnimeCharacter("Zero Two",
                    new String[]{"Darling in the Franxx", "Female"},
                    R.drawable.zerotwo,
                    R.drawable.zerotwo,
                    R.raw.darling)
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mediaPlayer = new MediaPlayer();
        animeCharacter = ANIME_CHARACTERS[0];

        // Check for overlay permission. If not enabled, request for it. If enabled, show the overlay
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(context)) {
            CharSequence text = "Please grant the access to the application.";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.fromParts("package", getPackageName(), null)));
        }

        onOffSwitch = findViewById(R.id.start);
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    startPowerOverlay();
                else
                    if (overlayPowerBtn != null)
                        windowManager.removeView(overlayPowerBtn);
            }
        });
        onOffSwitch.setChecked(true);

    }

    @SuppressLint("ClickableViewAccessibility")
    private void startPowerOverlay(){
        // Starts the button overlay.
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        overlayPowerBtn = new ImageView(this);
        overlayPowerBtn.setImageResource(animeCharacter.getImageStatLocation());

        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // APPLICATION_OVERLAY FOR ANDROID 26+ AS THE PREVIOUS VERSION RAISES ERRORS
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            // FOR PREVIOUS VERSIONS USE TYPE_PHONE AS THE NEW VERSION IS NOT SUPPORTED
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        params.x = 0;
        params.y = 0;
        params.height = 200;
        params.width = 200;

        overlayPowerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer = MediaPlayer.create(context, animeCharacter.getAudioLocation());
                    mediaPlayer.start();
                }
            }
        });

        windowManager.addView(overlayPowerBtn, params);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (overlayPowerBtn != null)
            windowManager.removeView(overlayPowerBtn);
    }
}
