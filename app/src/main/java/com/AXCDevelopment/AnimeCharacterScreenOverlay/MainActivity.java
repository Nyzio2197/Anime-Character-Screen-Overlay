package com.AXCDevelopment.AnimeCharacterScreenOverlay;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.InetAddresses;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    public String version;

    private Switch onOffSwitch;
    private Spinner selectorSpinner;
    private SeekBar sizeSeekBar;
    private TextView updateTextView;
    private EditText sizeEditText;
    private AnimeCharacter animeCharacter;
    private ImageView overlayPowerBtn;
    private Context context;
    private WindowManager windowManager;
    private WindowManager.LayoutParams params;
    private MediaPlayer mediaPlayer;
    private int size;
    private static final AnimeCharacter[] ANIME_CHARACTERS = new AnimeCharacter[]
            {
                    new AnimeCharacter("Zero Two",
                            new String[]{"Darling in the Franxx", "Female"},
                            R.drawable.zerotwo,
                            R.drawable.zerotwo,
                            R.raw.darling),
                    new AnimeCharacter("Nezuko",
                            new String[]{"Demon Slayer", "Female"},
                            R.drawable.nezuko,
                            R.drawable.nezuko,
                            R.raw.nezuko_sound),
                    new AnimeCharacter("Rem",
                            new String[]{"Re:Zero - Starting Life in Another World", "Female"},
                            R.drawable.rem,
                            R.drawable.rem,
                            R.raw.rem_love)
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        version = getString(R.string.version).substring(1);

        context = this;
        mediaPlayer = new MediaPlayer();
        animeCharacter = ANIME_CHARACTERS[0];
        size = 200;

        setUpTextView();

        setUpSpinner();

        setUpSeekBar();

        setUpEditText();

        // Check for overlay permission. If not enabled, request for it. If enabled, show the overlay
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(context)) {
            CharSequence text = "Please grant the access to the application.";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.fromParts("package", getPackageName(), null)));
        }

        setUpSwitch();

    }

    @SuppressLint("ClickableViewAccessibility")
    private void startPowerOverlay(){
        // Starts the button overlay.
        mediaPlayer = MediaPlayer.create(context, animeCharacter.getAudioLocation());
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        overlayPowerBtn = new ImageView(context);
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

        params.gravity = Gravity.BOTTOM;
        params.x = 0;
        params.height = size;
        params.width = size;

        overlayPowerBtn.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private float initialTouchX;
            private long latestPressTime = 0;

            @Override public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Save current x/y
                        initialX = params.x;
                        initialTouchX = event.getRawX();
                        // Check for double clicks.
                        if (latestPressTime == 0 || latestPressTime + 500 < System.currentTimeMillis()) {
                            latestPressTime = System.currentTimeMillis();
                        } else {
                            // Doubleclicked. Do any action you'd like
                        }
                        if (!mediaPlayer.isPlaying()) {
                            mediaPlayer.start();
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        // deprecated flip function
                        /*
                        if (params.x < 0)
                            overlayPowerBtn.setScaleX(-1);
                        else
                            overlayPowerBtn.setScaleX(1);
                        windowManager.updateViewLayout(overlayPowerBtn, params);
                         */
                        return true;
                }
                return false;
            }
        });

        windowManager.addView(overlayPowerBtn, params);
    }

    String versionLine;

    private void setUpTextView() {
        updateTextView = findViewById(R.id.update);
        versionLine = "";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url ="http://raw.githubusercontent.com/alandaboi/Anime-Character-Screen-Overlay/master/app/src/main/res/values/strings.xml";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // read text returned by server
                        Scanner in = new Scanner(response);
                        String line;
                        while ((line = in.nextLine()) != null) {
                            if (line.contains("version")) {
                                versionLine = line;
                                break;
                            }
                        }
                        in.close();
                        Log.v("USERInfo", versionLine);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                    }
                });
        requestQueue.add(stringRequest);
        requestQueue.start();
        Log.v("USERInfo", "String request made");
        versionLine.trim();
        String newestVersion = "";
        for (String x : versionLine.split("")) {
            if (x.matches("[0-9].+")) {
                newestVersion += x;
            }
        }
        Log.v("newestVersion", newestVersion);
        if ((!version.isEmpty() && !newestVersion.isEmpty()) && (
                Integer.parseInt(version.split(".")[2]) < Integer.parseInt(newestVersion.split(".")[2]) ||
                        Integer.parseInt(version.split(".")[1]) < Integer.parseInt(newestVersion.split(".")[1]) ||
                        Integer.parseInt(version.split(".")[0]) < Integer.parseInt(newestVersion.split(".")[0]))) {
            updateTextView.setText("Click to Update to: " + newestVersion);
            updateTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/alandaboi/Anime-Character-Screen-Overlay/raw/master/app/release/Anime%20Overlay.apk"));
                    startActivity(browserIntent);
                }
            });
        }

    }

    private void setUpSpinner() {
        selectorSpinner = findViewById(R.id.select);
        selectorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // On selecting a spinner item
                String character = parent.getItemAtPosition(position).toString();
                animeCharacter = ANIME_CHARACTERS[position];
                if (overlayPowerBtn != null) {
                    windowManager.removeView(overlayPowerBtn);
                    startPowerOverlay();
                }
                // Showing selected spinner item
                Toast.makeText(parent.getContext(), character + " selected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter<AnimeCharacter> dataAdapter =
                new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, ANIME_CHARACTERS);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectorSpinner.setAdapter(dataAdapter);
    }

    private void setUpSeekBar() {
        sizeSeekBar = findViewById(R.id.size);
        sizeSeekBar.setMax(500);
        sizeSeekBar.setPadding(5, 0, 20, 0);
        sizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                this.progress = progress;
                size = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (sizeEditText != null) {
                    sizeEditText.setText("" + progress);
                }
                if (overlayPowerBtn != null) {
                    windowManager.removeView(overlayPowerBtn);
                    params.height = progress;
                    params.width = progress;
                    windowManager.addView(overlayPowerBtn, params);
                }
            }
        });
        sizeSeekBar.setProgress(200);
    }

    private void setUpEditText() {
        sizeEditText = findViewById(R.id.editSize);
        sizeEditText.setText("" + sizeSeekBar.getProgress());
        sizeEditText.setFilters(new InputFilter[]{new InputFilterMinMax(0, 500)});
        sizeEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    sizeSeekBar.setProgress(Integer.parseInt(sizeEditText.getText().toString()));
                    if (overlayPowerBtn != null) {
                        windowManager.removeView(overlayPowerBtn);
                        params.height = sizeSeekBar.getProgress();
                        params.width = sizeSeekBar.getProgress();
                        windowManager.addView(overlayPowerBtn, params);
                    }
                    InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    sizeEditText.clearFocus();
                    return true;
                }
                return false;
            }
        });

    }

    private void setUpSwitch() {
        onOffSwitch = findViewById(R.id.start);
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startPowerOverlay();
                }
                else {
                    if (overlayPowerBtn != null)
                        windowManager.removeView(overlayPowerBtn);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (overlayPowerBtn != null)
            windowManager.removeView(overlayPowerBtn);
    }

    private class InputFilterMinMax implements InputFilter {
        private int min, max;

        public InputFilterMinMax(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public InputFilterMinMax(String min, String max) {
            this.min = Integer.parseInt(min);
            this.max = Integer.parseInt(max);
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                int input = Integer.parseInt(dest.toString() + source.toString());
                if (isInRange(min, max, input))
                    return null;
            } catch (NumberFormatException nfe) { }
            return "";
        }

        private boolean isInRange(int a, int b, int c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }

}
