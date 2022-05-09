package com.example.strobe;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.slider.Slider;

public class MainActivity extends AppCompatActivity {

    private boolean strobeOn = false;
    private int curColor = Color.WHITE;
    private Button strobe;
    private float strobeDelay = 0.0f;
    private String STROBE_DELAY_KEY;
    SharedPreferences prefs ;
    static int MULTIPLIER = 2000; // 1.0 => 2,000 ms = 2 sec

    Handler h = new Handler();
    private Runnable strobeUpdate = new Runnable() {
        @Override
        public void run() {

            if (!strobeOn)
                return;

            // swap strobe colors
            if (curColor == Color.WHITE)
                curColor = Color.BLACK;
            else
                curColor = Color.WHITE;

            strobe.setBackgroundColor(curColor);

            h.postDelayed(this, (long)(strobeDelay * MULTIPLIER));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_main);

        strobe = (Button) findViewById(R.id.strobe);

        Slider strobeSlider = (Slider) findViewById(R.id.strobeSlider);
        strobeSlider.addOnChangeListener((slider, value, fromUser) -> {

            strobeDelay = value;
            if (strobeDelay == 0.0 || strobeDelay == 1.0) {
                if (strobeOn) {
                    strobeOn = false;
                }

                if (strobeDelay == 0.0) {
                    strobe.setBackgroundColor(Color.WHITE);
                }
                else {
                    strobe.setBackgroundColor(Color.BLACK);
                }
            }
            else {
                if (!strobeOn) {
                    h.post(strobeUpdate);
                    strobeOn = true;
                }
            }
        });

        // restore strobeDelay value from shared preferences
        prefs = this.getSharedPreferences(
                getApplicationContext().getPackageName(), Context.MODE_PRIVATE);
        STROBE_DELAY_KEY = getApplicationContext().getPackageName() + "strobe_delay";
        strobeDelay = prefs.getFloat(STROBE_DELAY_KEY, 0.0f);
        strobeSlider.setValue(strobeDelay);
    }

    @Override
    protected void onPause() {

        prefs.edit().putFloat(STROBE_DELAY_KEY, strobeDelay).apply();
        super.onPause();
    }

}
