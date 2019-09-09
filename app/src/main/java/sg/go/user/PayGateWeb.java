package sg.go.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;

/**
 * Created by Mahesh on 9/20/2017.
 */

public class PayGateWeb extends AppCompatActivity {
    private Toolbar helpToolbar;
    private ImageButton help_back;
    WebView webView;
    ProgressBar web_loader;
    private String URL = "", stringVariable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        URL = getIntent().getExtras().getString("URl", "URl");
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
                AlertDialog.Builder builder = new AlertDialog.Builder(PayGateWeb.this);

                builder.setTitle(getResources().getString(R.string.txt_warn));
                builder.setMessage(getResources().getString(R.string.txt_cancel_trans));

                builder.setPositiveButton(getResources().getString(R.string.txt_yes), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog

                        dialog.dismiss();
                        onBackPressed();
                    }
                });

                builder.setNegativeButton(getResources().getString(R.string.txt_no), new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    private void loadWebViewLoad(WebView webView) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new SSLTolerentPaygateWebViewClient());
        webView.loadUrl(URL);
        webView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                // do your stuff here
                web_loader.setVisibility(View.GONE);
            }
        });
        webView.addJavascriptInterface(new Object() {
            @JavascriptInterface           // For API 17+
            public void performClick(String strl) {
                stringVariable = strl;
                startActivity(new Intent(PayGateWeb.this, AmbulanceWalletActivity.class));
                PayGateWeb.this.finish();
                // Toast.makeText(PayGateWeb.this, stringVariable, Toast.LENGTH_SHORT).show();
            }
        }, "ok");
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private class SSLTolerentPaygateWebViewClient extends WebViewClient {

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
}
