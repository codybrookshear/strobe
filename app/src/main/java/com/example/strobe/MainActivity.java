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
    private int strobeDelay = 0;
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

            h.postDelayed(this, strobeDelay);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_main);

        strobe = (Button) findViewById(R.id.strobe);

        Slider strobeSlider = (Slider) findViewById(R.id.strobeSlider);
        strobeSlider.addOnChangeListener((slider, value, fromUser) -> {

            if (value == 0.0 || value == 1.0) {
                if (strobeOn) {
                    strobeOn = false;
                }

                if (value == 0.0) {
                    strobe.setBackgroundColor(Color.WHITE);
                }
                else {
                    strobe.setBackgroundColor(Color.BLACK);
                }
            }
            else {
                strobeDelay = (int) (value * MULTIPLIER);
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
        strobeDelay = prefs.getInt(STROBE_DELAY_KEY, 0 );

        // error and bounds checking
        float sVal = (float)strobeDelay / MULTIPLIER;
        if (sVal <= 0.003f) sVal = 0.0f;
        else if (sVal > 0.98f) sVal = 1.0f;

        strobeSlider.setValue(sVal);
    }

    @Override
    protected void onPause() {

        prefs.edit().putInt(STROBE_DELAY_KEY, strobeDelay).apply();
        super.onPause();
    }

}
