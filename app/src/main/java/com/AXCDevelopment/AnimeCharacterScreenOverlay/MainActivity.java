package com.AXCDevelopment.AnimeCharacterScreenOverlay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;

public class MainActivity extends AppCompatActivity {

    private static final String ON_OFF_SWITCH_STATE = "onOffSwitchState";

    private Switch onOffSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        onOffSwitch = (Switch) findViewById(R.id.start);
        if (savedInstanceState != null) {
            onOffSwitch.setChecked(Boolean.parseBoolean(savedInstanceState.getString(ON_OFF_SWITCH_STATE)));
            Log.v("UserLog", "outState restored");
        }
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    playMedia(R.raw.darling);
                    startOverlay();
                } else {
                    stopOverlay();
                }
            }
        });

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(ON_OFF_SWITCH_STATE, "" + onOffSwitch.isChecked());
        Log.v("UserLog", "outState added");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onOffSwitch.setChecked(Boolean.parseBoolean(savedInstanceState.getString(ON_OFF_SWITCH_STATE)));
        Log.v("UserLog", "outState restored");
    }

    public void startOverlay() {
        Intent serviceIntent = new Intent(this, ScreenOverlay.class);
        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");
        ContextCompat.startForegroundService(this, serviceIntent);
    }
    public void stopOverlay() {
        Intent serviceIntent = new Intent(this, ScreenOverlay.class);
        stopService(serviceIntent);
    }

    private void playMedia(int resid) {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, resid);
        mediaPlayer.start();
    }



}
