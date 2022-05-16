package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private String trends;
    private ProgressBar progressBar;
    private TextView textView;
    private Button next;
    private Button prev;
    //webview
    private WebView webView;
    private WebSettings setBew;
    private static long back_pressed;
    private ArrayList<String> type;
    private int counter =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webview);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.tv_show);
        next = findViewById(R.id.btn_next);
        prev = findViewById(R.id.btn_prev);
        sets();
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setWebViewClient(new ProWeWebViewClient());
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);
                if (progress == 100) {
                    progressBar.setVisibility(View.GONE);

                }
            }
        });
        Intent intent = getIntent();
        type = intent.getStringArrayListExtra("list");

        loader(type.get(0));

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clean();
                assert type !=null;
                String id = type.get(counter);
                loader(id);
                counter+=1;
                if (counter == type.size()) {
                    counter = 0;
                }
            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clean();
                assert type !=null;
                String id = type.get(counter);
                loader(id);
                counter-=1;
                if (counter <0 ) {
                    counter = type.size()-1;
                }
            }
        });

    }

    private void loader(String id) {
        Retrofit retrofit = RetrofitClient.getInstance();
        Holder holder = retrofit.create(Holder.class);
        Call<Data> test = holder.getType(id);
        test.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                Data finalData = response.body();
                trends = finalData.getPayload().toString().split("=",2)[1].replaceAll(Pattern.quote("}")," ");
                if (finalData.getType().contains("text")) {
                    textView.setText(trends);
                }else if(finalData.getType().contains("webpage")){
                    webView.loadUrl(trends);
                }else {
                    Toast.makeText(MainActivity.this, "Неизвестные данные", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
            textView.setVisibility(View.GONE);
        }else{
            if (back_pressed +2000 >System.currentTimeMillis()) {
                super.onBackPressed();
            }else Toast.makeText(this, "Нажмите еще раз чтобы выйти", Toast.LENGTH_SHORT).show();
                    back_pressed = System.currentTimeMillis();
        }
    }

    private void sets() {
        setBew = webView.getSettings();
        setBew.setAppCacheEnabled(true);
        setBew.setDomStorageEnabled(true);
        setBew.setDatabaseEnabled(true);
        setBew.setSupportZoom(false);
        setBew.setAllowContentAccess(true);
        setBew.setAllowFileAccess(true);
        setBew.setJavaScriptEnabled(true);
        setBew.setLoadWithOverviewMode(true);
        setBew.setUseWideViewPort(true);
        setBew.setJavaScriptCanOpenWindowsAutomatically(true);
    }



    private class ProWeWebViewClient extends WebViewClient {

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            webView.setVisibility(View.VISIBLE);
            super.onPageFinished(view, url);
        }
    }
    public void clean(){
        textView.setText("");
        webView.setVisibility(View.GONE);
    }
}