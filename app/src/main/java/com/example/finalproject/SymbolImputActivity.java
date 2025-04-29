package com.example.finalproject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SymbolImputActivity extends AppCompatActivity {

    EditText editText;
    Button button;
    TextView textView;

    private static final String TAG = "SymbolInputActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_symbol_imput);

        editText = findViewById(R.id.editTextText);
        button = findViewById(R.id.button2);
        textView = findViewById(R.id.textView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String symbol = editText.getText().toString().trim();
                Log.d(TAG, "Button clicked with symbol: " + symbol);
                if (symbol.isEmpty()) {
                    textView.setText("Please enter a symbol");
                    Log.d(TAG, "No symbol entered.");
                } else {
                    getStockPrice(symbol);
                }
            }
        });
    }

    private void getStockPrice(final String symbol) {
        Log.d(TAG, "Fetching stock price for: " + symbol);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String apiKey = "5ZBMR67GEIIJQ67K";
                    String urlString = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol="
                            + symbol + "&apikey=" + apiKey;

                    Log.d(TAG, "Request URL: " + urlString);

                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    InputStream inputStream = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    Log.d(TAG, "Response: " + result.toString());

                    JSONObject jsonObject = new JSONObject(result.toString());
                    JSONObject quote = jsonObject.getJSONObject("Global Quote");

                    String stockSymbol = quote.getString("01. symbol");
                    String price = quote.getString("05. price");

                    Log.d(TAG, "Parsed Symbol: " + stockSymbol + ", Price: " + price);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText("Symbol: " + stockSymbol + "\nPrice: $" + price);
                        }
                    });

                } catch (Exception e) {
                    Log.e(TAG, "Error fetching stock data", e);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText("Error getting data.");
                        }
                    });
                }
            }
        });

        thread.start();
    }
}
