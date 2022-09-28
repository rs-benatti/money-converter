package com.example.money_converter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    public String inputCurrency;
    public String outputCurrency;
    public JSONObject rates;
    public double  rate;
    public Double conversion_rate = 1.0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView textView = findViewById(R.id.exchangeRate_text);
        textView.setText(Double.toString(conversion_rate));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                // This operation is taking really long, better get a coffee
                // --- Rafael Benatti

                rates = openCurrencyRates();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Button butao = findViewById(R.id.butao);
                        butao.setEnabled(true);
                    }
                });
            }
        });
    }

    public JSONObject openCurrencyRates(){

        // InputStream inputStream = getResources().openRawResource(R.raw.rates_2017_11_02);
        InputStream inputStream = downloadData();

        StringBuilder stringBuilder = new StringBuilder();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line = null;
            while ((line = reader.readLine()) != null){
                stringBuilder.append(line + "\n");
            }

            String jsonString = stringBuilder.toString();

            return new JSONObject(jsonString);

        } catch (IOException e) {
            System.err.println("Warning: could not read rates: " + e.getLocalizedMessage());
        } catch (JSONException e) {
            System.err.println("Warning: could not read rates: " + e.getLocalizedMessage());
        }
        return null;
    }

    public InputStream downloadData(){
        HttpURLConnection urlConnection = null;
        try{
            URL exchangeRatesURL = new URL("https://perso.telecom-paristech.fr/eagan/class/igr201/data/rates_2017_11_02.json");

            urlConnection = (HttpURLConnection) exchangeRatesURL.openConnection();
            urlConnection.connect();

            Log.i("butao", "0");
            InputStream inputStream = urlConnection.getInputStream();

            return inputStream;
        } catch (MalformedURLException e) {

            Log.i("Erro ao baixar dados", "MalformedURLException");
            e.printStackTrace();
        } catch (Exception e){

            Log.i("Erro ao baixar dados", "Erro na função downloadData");
            System.err.println("Warning: could not read rates: " + e.getLocalizedMessage());
        }
        return null;

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                inputCurrency = data.getStringExtra("inputCurrency");
                outputCurrency = data.getStringExtra("outputCurrency");
                refreshViews();
            }
        }
    }

    private void refreshViews(){
        conversion_rate = getRates();
        TextView outputCurrency_text = findViewById(R.id.ouputCurrency_text);
        TextView inputCurrency_text = findViewById(R.id.inputCurrency_text);
        TextView exchangeRate_text = findViewById(R.id.exchangeRate_text);
        EditText inputCurrency_edit = findViewById(R.id.edit_euro);
        exchangeRate_text.setText(Double.toString(conversion_rate));
        EditText outputCurrency_edit = findViewById(R.id.edit_dollar);
        switch (inputCurrency){
            case "EUR":
                inputCurrency_text.setText("Euro");
                inputCurrency_edit.setHint("€");
                break;
            case "USD":
                inputCurrency_text.setText("Dollar");
                inputCurrency_edit.setHint("$");
                break;
            case "GBP":
                inputCurrency_text.setText("Pound");
                inputCurrency_edit.setHint("£");
                break;
        }
        switch (outputCurrency){
            case "EUR":
                outputCurrency_edit.setHint("€");
                outputCurrency_text.setText("Euro");
                break;
            case "USD":
                outputCurrency_edit.setHint("$");
                outputCurrency_text.setText("Dollar");
                break;
            case "GBP":
                outputCurrency_edit.setHint("£");
                outputCurrency_text.setText("Pound");
                break;
        }
    }

    private Double getRates(){
        try {
            double inputRate = rates.getJSONObject("rates").getDouble(inputCurrency);
            double outputRate = rates.getJSONObject("rates").getDouble(outputCurrency);
            return round(outputRate/inputRate, 3);
        } catch (JSONException e) {
            System.err.println("Warning: could not read rates: " + e.getLocalizedMessage());
        }
        return 1.0;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public void convert(View sender){
        EditText euros = (EditText) findViewById(R.id.edit_euro);
        EditText dollars = (EditText) findViewById(R.id.edit_dollar);
        Double euros_values = Double.parseDouble(euros.getText().toString());
        Double dollars_values = euros_values*conversion_rate;
        dollars.setText(Double.toString(dollars_values));

    }
    public void currencies(View sender){
        Intent switchActivityIntent = new Intent(this, CurrentChooserActivity.class);
        startActivityForResult(switchActivityIntent, 1);
    }

}