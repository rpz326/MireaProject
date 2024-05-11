package ru.mirea.sukhanovmd.mireaproject;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

public class ExchangeRatesFragment extends Fragment {

    private TextView dollarRateTextView;
    private TextView euroRateTextView;
    private Button updateButton;

    private static final String EXCHANGE_RATES_URL = "https://open.er-api.com/v6/latest/RUB";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exchange_rates, container, false);

        dollarRateTextView = view.findViewById(R.id.dollarRateTextView);
        euroRateTextView = view.findViewById(R.id.euroRateTextView);
        updateButton = view.findViewById(R.id.updateButton);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DownloadExchangeRatesTask().execute(EXCHANGE_RATES_URL);
            }
        });

        return view;
    }

    private class DownloadExchangeRatesTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            String result = null;
            try {
                result = downloadData(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                try {
                    JSONObject responseJson = new JSONObject(result);
                    JSONObject ratesJson = responseJson.getJSONObject("rates");
                    double dollarRate = ratesJson.getDouble("USD");
                    double euroRate = ratesJson.getDouble("EUR");
                    
                    double rubleRateUSD = roundNum(1 / dollarRate);
                    double rubleRateEUR = roundNum(1 / euroRate);

                    dollarRateTextView.setText(rubleRateUSD + " ₽");
                    euroRateTextView.setText(rubleRateEUR + " ₽");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getContext(), "Ошибка получения курса валют", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String downloadData(String address) throws IOException {
        InputStream inputStream = null;
        String data = "";
        try {
            URL url = new URL(address);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();
                data = convertStreamToString(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return data;
    }

    private String convertStreamToString(InputStream inputStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return stringBuilder.toString();
    }

    private double roundNum(double value) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return Double.parseDouble(decimalFormat.format(value));
    }
}