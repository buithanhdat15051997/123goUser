package sg.go.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import sg.go.user.Utils.PreferenceHelper;

/**
 * Created by Mahesh on 5/24/2017.
 */

public class AskBotActivity extends AppCompatActivity {
    private Toolbar helpToolbar;
    private ImageButton help_back;
    WebView webView;
    ProgressBar web_loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(R.layout.helpview);
        webView = (WebView) findViewById(R.id.webView);
        web_loader = (ProgressBar) findViewById(R.id.web_loader);
        web_loader.setVisibility(View.VISIBLE);
        helpToolbar = (Toolbar) findViewById(R.id.toolbar_help);
        help_back = (ImageButton) findViewById(R.id.help_back);


        setSupportActionBar(helpToolbar);
        getSupportActionBar().setTitle(null);
        loadWebViewLoad(webView);
        help_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void loadWebViewLoad(WebView webView) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        // webView.getSettings().setSupportMultipleWindows(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new SSLTolerentWebViewClient());
        webView.loadUrl("http://prevue.info/web-view/?token=" + new PreferenceHelper(this).getUserId()+"&"+"session_token"+"="+new PreferenceHelper(this).getSessionToken());
        webView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                // do your stuff here
                web_loader.setVisibility(View.GONE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Here put your code
                Log.d("My Webview", url);
                view.loadUrl(url);
                // return true; //Indicates WebView to NOT load the url;
                return false; //Allow WebView to load url
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    private class SSLTolerentWebViewClient extends WebViewClient {

        @Override
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
            builder.setMessage(getResources().getString(R.string.txt_load_web));
            builder.setPositiveButton(getResources().getString(R.string.txt_continue), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.proceed();
                }
            });
            builder.setNegativeButton(getResources().getString(R.string.txt_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.cancel();
                    onBackPressed();
                }
            });
            final AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

    @Override
    public void onBackPressed() {
        if (webView.isFocused() && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();

        }
    }
}