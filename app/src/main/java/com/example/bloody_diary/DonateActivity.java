package com.example.bloody_diary;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DonateActivity extends AppCompatActivity {
    private final String operation    = "Purchase";
    private String merchant_id  = "";
    private String signature          = "";
    private String order_id           = "";
    private final String currency_iso = "UAH";
    private final String description  = "Test payment";
    private final String add_params   = "";
    private final String approve_url  = "http://niania.um.la";
    private final String decline_url  = "http://niania.um.la";
    private final String cancel_url   = "http://niania.um.la";
    private final String callback_url = "http://niania.um.la";
    private final String redirect     = "0";
    private final String auth_type    = "1";
    private String receipt      = "";

    private final String urlPG    = "jdbc:postgresql://35.246.126.180:5432/aeuhkfqq";
    private final String username = "aeuhkfqq";
    private final String password = "QK3QLUuGZfVnQg30k7AjDU1v29geVIc9";

    private final String KEY_OPERATION    = "operation";
    private final String KEY_MERCHANT_ID  = "merchant_id";
    private final String KEY_AMOUNT       = "amount";
    private final String KEY_SIGNATURE    = "signature";
    private final String KEY_ORDER_ID     = "order_id";
    private final String KEY_CURRENCY_ISO = "currency_iso";
    private final String KEY_DESCRIPTION  = "description";
    private final String KEY_ADD_PARAMS   = "add_params";
    private final String KEY_APPROVE_URL  = "approve_url";
    private final String KEY_DECLINE_URL  = "decline_url";
    private final String KEY_CANCEL_URL   = "cancel_url";
    private final String KEY_CALLBACK_URL = "callback_url";
    private final String KEY_REDIRECT     = "redirect";
    private final String KEY_LANGUAGE     = "language";
    private final String KEY_AUTH_TYPE    = "auth_type";
    private final String logTag           = "myLogs";
    private final String[] url = {""};
    private double donateSumUah = 0;
    private double donateSumUsd = 0;
    private double usdRate = 28;
    private double eurRate = 33;
    private RadioGroup rgDonate1;
    private RadioGroup rgDonate2;
    private TextView tvDisplayRates;
    private TextView tvDisplaySum;
    WebView webView;
    Uri data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        webView = findViewById(R.id.webView);
        tvDisplayRates = findViewById(R.id.tvDisplayRatesWeb);
        tvDisplaySum   = findViewById(R.id.tvDisplaySumWeb);
        tvDisplaySum.setText(String.format("Donation sum is %.2f UAH", donateSumUah));

        rgDonate1      = findViewById(R.id.rgDonate1);
        rgDonate2      = findViewById(R.id.rgDonate2);

        View llRadio = findViewById(R.id.llRadio);

        RadioButton rbtn1usd   = findViewById(R.id.rbtn1usd);
        RadioButton rbtn2usd   = findViewById(R.id.rbtn2usd);
        RadioButton rbtn5usd   = findViewById(R.id.rbtn5usd);
        RadioButton rbtn10usd  = findViewById(R.id.rbtn10usd);
        RadioButton rbtn20usd  = findViewById(R.id.rbtn20usd);
        RadioButton rbtn50usd  = findViewById(R.id.rbtn50usd);
        RadioButton rbtn100usd = findViewById(R.id.rbtn100usd);
        RadioButton rbtn200usd = findViewById(R.id.rbtn200usd);

        GETStringRequest();

        (findViewById(R.id.btnPay)).setOnClickListener(v -> {
            if (donateSumUah > 0) {
                ViewGroup parent = (ViewGroup) llRadio.getParent();
                if (parent != null) {
                    parent.removeView(llRadio);
                };

                // Get order date
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                String date = sdf.format(Calendar.getInstance().getTime());
                // Get receipt
                long unixTime = System.currentTimeMillis() / 1000L;
                receipt = Long.toHexString(unixTime).toUpperCase();
                // Register order at backend
                RegOrder(1, donateSumUah, date, currency_iso, description, receipt);
                // Get order num and signature from backend
                GetOrder(receipt);
                // Get merchant_id from backend
                GetMerchantId();
                // POST Request to ConcordPay
                POSTStringRequest();
                Log.d(logTag, "String value of url: " + url[0]);

            } else {
                Toast.makeText(getApplicationContext(),"Nothing is chosen",
                        Toast.LENGTH_LONG).show();
            }
        });

        rgDonate1.setOnCheckedChangeListener((rg, checkedId) -> {
            switch (checkedId) {
                case R.id.rbtn1usd:
                    rgDonate2.clearCheck(); rgDonate1.check(rbtn1usd.getId());
                    donateSumUsd = 1; donateSumUah = donateSumUsd * usdRate; break;
                case R.id.rbtn2usd:
                    rgDonate2.clearCheck(); rgDonate1.check(rbtn2usd.getId());
                    donateSumUsd = 2; donateSumUah = donateSumUsd * usdRate; break;
                case R.id.rbtn5usd:
                    rgDonate2.clearCheck(); rgDonate1.check(rbtn5usd.getId());
                    donateSumUsd = 5; donateSumUah = donateSumUsd * usdRate; break;
                case R.id.rbtn10usd:
                    rgDonate2.clearCheck(); rgDonate1.check(rbtn10usd.getId());
                    donateSumUsd = 10; donateSumUah = donateSumUsd * usdRate; break;
                default:
                    break;
            }
            tvDisplaySum.setText(String.format("Donation sum is %.2f UAH", donateSumUah));
            Log.d("myLogs", "SettingsActivity: donateSum = " + donateSumUsd);
        });

        rgDonate2.setOnCheckedChangeListener((rg, checkedId) -> {
            switch (checkedId) {
                case R.id.rbtn20usd:
                    rgDonate1.clearCheck(); rgDonate2.check(rbtn20usd.getId());
                    donateSumUsd = 20; donateSumUah = donateSumUsd * usdRate;
                    tvDisplaySum.setText(
                            String.format("%s", String.format("Checkout sum is %s UAH / ", usdRate * 1)));
                    break;
                case R.id.rbtn50usd:
                    rgDonate1.clearCheck(); rgDonate2.check(rbtn50usd.getId());
                    donateSumUsd = 50; donateSumUah = donateSumUsd * usdRate; break;
                case R.id.rbtn100usd:
                    rgDonate1.clearCheck(); rgDonate2.check(rbtn100usd.getId());
                    donateSumUsd = 100; donateSumUah = donateSumUsd * usdRate; break;
                case R.id.rbtn200usd:
                    rgDonate1.clearCheck(); rgDonate2.check(rbtn200usd.getId());
                    donateSumUsd = 200; donateSumUah = donateSumUsd * usdRate; break;
                default:
                    break;
            }
            tvDisplaySum.setText(String.format("Donation sum is %.2f UAH", donateSumUah));
            Log.d("myLogs", "SettingsActivity: donateSum = " + donateSumUsd);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_donate_activity, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return super.onCreateOptionsMenu(menu);}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private void GETStringRequest() {
        // Instantiate the GET RequestQueue
        RequestQueue queue = SingletonRequestQueue.getInstance(getApplicationContext()).getRequestQueue();
        VolleyLog.DEBUG = true;
        String url ="https://bank.gov.ua/NBUStatService/v1/statdirectory/exchangenew?json";

        // Request a string response from the provided URL
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    JSONArray jsonArray;
                    JSONObject jsonObject;
                    VolleyLog.wtf(response);
                    try {
                        jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            if (jsonObject.getInt("r030") == 840) {
                                usdRate = jsonObject.getDouble("rate");
                            }
                            if (jsonObject.getInt("r030") == 978) {
                                eurRate = jsonObject.getDouble("rate");
                            }
                            tvDisplayRates.setText(
                                    String.format("%s%s", String.format("1 USD = %s UAH / ", usdRate * 1),
                                    String.format("1 EUR = %s UAH", eurRate * 1)));
                            donateSumUah = donateSumUsd * usdRate;
                            tvDisplaySum.setText(String.format("Donation sum is %.2f UAH", donateSumUah));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Toast.makeText(getApplicationContext(),"Something went wrong!", Toast.LENGTH_LONG).show());

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void POSTStringRequest() {
        RequestQueue queue = SingletonRequestQueue.getInstance(getApplicationContext()).getRequestQueue();
        VolleyLog.DEBUG = true;
        String uri = "https://pay.concord.ua/api/";

        //POST StringRequest begin
        StringRequest stringRequest = new StringRequest(Request.Method.POST, uri,
                response -> {
                    JSONObject jsonObject = null;
                    VolleyLog.wtf(response);
                    try {
                        jsonObject = new JSONObject(response);
                        url[0] = jsonObject.getString("url");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d(logTag, "Response from server: " + response);
                    Log.d(logTag, "Response as JObject: " + url[0]);

                    webView.setWebViewClient(new MyWebViewClient());
                    webView.setWebChromeClient(new WebChromeClient());
                    webView.getSettings().setDomStorageEnabled(true);
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.canGoForward();

                    webView.loadUrl(url[0]);
                    //Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                }, errorListener) {
            @Override
            public Priority getPriority() {
                return Priority.LOW;
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(KEY_OPERATION   , operation);
                params.put(KEY_MERCHANT_ID , merchant_id);
                params.put(KEY_AMOUNT      , String.format(Locale.US, "%.2f", donateSumUah));
                params.put(KEY_SIGNATURE   , signature);
                params.put(KEY_ORDER_ID    , order_id);
                params.put(KEY_CURRENCY_ISO, currency_iso);
                params.put(KEY_DESCRIPTION , description);
                params.put(KEY_ADD_PARAMS  , add_params);
                params.put(KEY_APPROVE_URL , approve_url);
                params.put(KEY_DECLINE_URL , decline_url);
                params.put(KEY_CANCEL_URL  , cancel_url);
                params.put(KEY_CALLBACK_URL, callback_url);
                params.put(KEY_REDIRECT    , redirect);
                params.put(KEY_LANGUAGE    , "en");
                params.put(KEY_AUTH_TYPE   , auth_type);
                Log.d(logTag, "POST-Body params: " + params.toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                Log.d(logTag, "POST-Headers params: " + headers.toString());
                return headers;
            }
        };
        queue.add(stringRequest);
    }
    //POST StringRequest end

    private void GetMerchantId() {
        // Get merchant_id from backend
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Class.forName("org.postgresql.Driver");
                    Connection db = DriverManager.getConnection(urlPG, username, password);
                    Statement stm = db.createStatement();
                    String s = "select merchant_id from public.merchant where id = " + 1;
                    Log.d(logTag, "Query: " + s);
                    ResultSet rs = stm.executeQuery(s);
                    Log.d(logTag, "ResultSet size: " + rs.toString());
                    while (rs.next()) {
                        merchant_id = rs.getString(1);
                        Log.d(logTag, "Merchant_ID: " + rs.getString(1));
                    }
                    rs.close();
                    stm.close();
                    db.close();
                } catch (SQLException | ClassNotFoundException e) {
                    System.out.println(e.getMessage());}
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void RegOrder(int pg_merchant, double pg_amount, String odate,
                          String pg_currency, String pg_description, String pg_receipt) {
        // Register order at backend
        Thread thread = new Thread(() -> {
            try {
                Class.forName("org.postgresql.Driver");
                Connection db = DriverManager.getConnection(urlPG, username, password);
                Statement st = db.createStatement();
                st.executeQuery(
                        "insert into public.orders (\n" +
                                "merchant, amount, odate, currency, description, receipt) values (" +
                                pg_merchant    + ", " +
                                pg_amount      + ", to_date('" +
                                odate          + "', 'DD.MM.YYYY'), '" +
                                pg_currency    + "', '" +
                                pg_description + "', '" +
                                pg_receipt     + "')");
                st.close();
                db.close();
            }
            catch (SQLException | ClassNotFoundException e) {
                System.out.println(e.getMessage());}
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void GetOrder(String pg_receipt) {
        // Get order num and signature from backend
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Class.forName("org.postgresql.Driver");
                    Connection db = DriverManager.getConnection(urlPG, username, password);
                    Statement stm = db.createStatement();
                    String s = "select oname, signature from public.orders where receipt = '" + pg_receipt + "'";
                    Log.d(logTag, "Query: " + s);
                    ResultSet rs = stm.executeQuery(s);
                    while (rs.next()) {
                        order_id = rs.getString(1);
                        Log.d(logTag, "Order num: " + rs.getString(1));
                        signature = rs.getString(2);
                        Log.d(logTag, "Signature: " + rs.getString(2));
                    }
                    rs.close();
                    stm.close();
                    db.close();
                }
                catch (SQLException | ClassNotFoundException e) {
                    System.out.println(e.getMessage());}
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    Response.ErrorListener errorListener = error -> {
        if (error instanceof NetworkError) {
            Toast.makeText(getApplicationContext(), "No network available", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
        }
    };

   @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.webView.canGoBack()) {
            this.webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(url[0]);
            return true;
        }
    }
}