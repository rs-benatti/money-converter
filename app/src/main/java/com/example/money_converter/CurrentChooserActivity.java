package com.example.money_converter;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.money_converter.databinding.ActivityCurrentChooserBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class CurrentChooserActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;
    public JSONObject rates;
    public int rate = 1;
    public String inputCurrency = "EUR";
    public String outputCurrency = "EUR";

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler(Looper.myLooper());
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar
            if (Build.VERSION.SDK_INT >= 30) {
                mContentView.getWindowInsetsController().hide(
                        WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            } else {
                // Note that some of these constants are new as of API 16 (Jelly Bean)
                // and API 19 (KitKat). It is safe to use them, as they are inlined
                // at compile-time and do nothing on earlier devices.
                mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
        }
    };



    public InputStream downloadData(){
        try{
            URL exchangeRatesURL = new URL("https://perso.telecom-paristech.fr/eagan/class/igr201/data/rates_2017_11_02.json");
            InputStream inputStream = exchangeRatesURL.openStream();
            return inputStream;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (Exception e){
            Log.i("Erro ao baixar dados", "Erro na função downloadData");
        }
        return null;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_chooser);

    }

    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };


    public void onRadioButtonClicked(View view){
        boolean checked = ((RadioButton) view).isChecked();

        if (checked){
            switch (view.getId()){
                case R.id.InputEuro:
                    inputCurrency = "EUR";
                    Log.i("Moeda de entrada selecionada: ", "EUR");
                    break;
                case R.id.InputDollar:
                    inputCurrency = "USD";
                    Log.i("Moeda de entrada selecionada: ", "USD");
                    break;
                case R.id.InputLibra:
                    inputCurrency = "GBP";
                    Log.i("Moeda de entrada selecionada: ", "GBP");
                    break;
            }
            switch (view.getId()){
                case R.id.OutputEuro:
                    outputCurrency = "EUR";
                    Log.i("Moeda de saída selecionada: ", "EUR");
                    break;
                case R.id.OutputDollar:
                    outputCurrency = "USD";
                    Log.i("Moeda de saída selecionada: ", "USD");
                    break;
                case R.id.OutputLibra:
                    outputCurrency = "GBP";
                    Log.i("Moeda de saída selecionada: ", "GBP");
                    break;
            }


        }


    }



    public void changeCurrencies(View sender){
        final Intent backToMainIntent = new Intent(this, MainActivity.class);
        backToMainIntent.putExtra("inputCurrency", inputCurrency);
        backToMainIntent.putExtra("outputCurrency", outputCurrency);
        setResult(RESULT_OK, backToMainIntent);
        finish();
    }
}