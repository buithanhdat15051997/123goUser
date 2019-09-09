package sg.go.user;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import sg.go.user.Adapter.WalletAdapter;
import sg.go.user.HttpRequester.VolleyRequester;
import sg.go.user.Interface.AsyncTaskCompleteListener;
import sg.go.user.Models.Patient;
import sg.go.user.Models.Wallets;

import sg.go.user.RealmController.RealmController;
import sg.go.user.Utils.EbizworldUtils;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.PreferenceHelper;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import io.realm.Realm;

/**
 * Created by Mahesh on 29/8/2017.
 */

public class AmbulanceWalletActivity extends AppCompatActivity implements View.OnClickListener, AsyncTaskCompleteListener {
    private Toolbar promainToolbar;
    private ImageButton wallet_back;
    private TextView tv_1, tv_2, tv_3;
    private EditText et_enter_amount;
    private String CONFIG_ENVIRONMENT = "";
    private String CONFIG_CLIENT_ID = "";
    private String MODE = "";
    private Realm realm;

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private Button btn_add_money;
    PayPalPayment addMoneytoWallet;
    private ArrayList<Wallets> walletLst = new ArrayList<Wallets>();

    private static PayPalConfiguration config;
    /*User userprofile;*/

    Patient patientProfile;
    private TextView tv_total_balance;
    private String WALLET_SECRET_KEY = "", STRIPE_KEY = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(R.layout.ambulance2u_wallet);
        WALLET_SECRET_KEY = new PreferenceHelper(this).getWallet_key();
        promainToolbar = (Toolbar) findViewById(R.id.toolbar_wallet);
        (btn_add_money = (Button) findViewById(R.id.btn_add_money)).setOnClickListener(this);
        this.realm = RealmController.with(this).getRealm();
        RealmController.with(this).refresh();

        /*userprofile = RealmController.with(this).getUser(Integer.valueOf(new PreferenceHelper(this).getUserId()));*/
        patientProfile = RealmController.with(this).getPatient(Integer.valueOf(new PreferenceHelper(this).getUserId()));

        setSupportActionBar(promainToolbar);
        getSupportActionBar().setTitle(null);
        wallet_back = (ImageButton) findViewById(R.id.wallet_back);
        tv_1 = (TextView) findViewById(R.id.tv_1);
        tv_2 = (TextView) findViewById(R.id.tv_2);
        tv_3 = (TextView) findViewById(R.id.tv_3);
        tv_total_balance = (TextView) findViewById(R.id.tv_total_balance);
        et_enter_amount = (EditText) findViewById(R.id.et_enter_amount);

        wallet_back.setOnClickListener(this);
        tv_1.setOnClickListener(this);
        tv_2.setOnClickListener(this);
        tv_3.setOnClickListener(this);

        if (!TextUtils.isEmpty(WALLET_SECRET_KEY)) {
            getWalletTypes();
            getWalletBalance();
        }

        btn_add_money.setEnabled(false);
        btn_add_money.setBackgroundColor(getResources().getColor(R.color.light_grey));

    }

    private void getWalletBalance() {
        if (!EbizworldUtils.isNetworkAvailable(this)) {
            return;
        }
        if (/*userprofile != null*/ patientProfile != null) {

            EbizworldUtils.showSimpleProgressDialog(this, "Loading...", false);
            HashMap<String, String> map = new HashMap<String, String>();
            HashMap<String, String> mapHead = new HashMap<String, String>();
            try {

                map.put(Const.Params.URL, Const.ServiceType.WALLET_BALANCE + new PreferenceHelper(this).getUserId() + "/balance?" +
                        "name=" + URLEncoder.encode(patientProfile.getmFullname(), "utf-8") +
                        "&full_mobile_no=" + patientProfile.getmMobile() +
                        "&email=" + patientProfile.getmEmail());

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            mapHead.put("authorization", WALLET_SECRET_KEY);
            Log.d("mahi", map.toString());
            new VolleyRequester(this, Const.GET, map, Const.ServiceCode.WALLET_BALANCE,
                    this, mapHead);
        }
    }

    private void getWalletTypes() {
        if (!EbizworldUtils.isNetworkAvailable(this)) {
            return;
        }

        HashMap<String, String> map = new HashMap<String, String>();
        HashMap<String, String> mapHead = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.WALLET_TYPES);
        mapHead.put("Authorization", WALLET_SECRET_KEY);

        Log.d("mahi", map.toString());
        new VolleyRequester(this, Const.GET, map, Const.ServiceCode.WALLET_TYPES,
                this, mapHead);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.wallet_back:
                onBackPressed();
                break;
            case R.id.tv_1:
                et_enter_amount.setText("");
                et_enter_amount.setText("200");
                et_enter_amount.setSelection(et_enter_amount.getText().toString().length());
                break;
            case R.id.tv_2:
                et_enter_amount.setText("");
                et_enter_amount.setText("500");
                et_enter_amount.setSelection(et_enter_amount.getText().toString().length());
                break;
            case R.id.tv_3:
                et_enter_amount.setText("");
                et_enter_amount.setText("1000");
                et_enter_amount.setSelection(et_enter_amount.getText().toString().length());
                break;
            case R.id.btn_add_money:
                if (TextUtils.isEmpty(et_enter_amount.getText().toString())) {
                    et_enter_amount.setError(getResources().getString(R.string.error_empty_amount));
                    et_enter_amount.requestFocus();
                } else {
                    if (null != walletLst && walletLst.size() > 0) {
                        showWalletOptions(walletLst);
                    } else {
                        EbizworldUtils.showShortToast(getResources().getString(R.string.error_no_wallet_gateway), this);
                    }

                }
                break;

        }

    }

    private void showWalletOptions(final ArrayList<Wallets> walletLst) {
        final Dialog dialog = new Dialog(this, R.style.DialogThemeforview);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.fade_drawable));
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.wallet_popup);
        GridView grid_wallet = (GridView) dialog.findViewById(R.id.grid_wallet);
        WalletAdapter Adapter = new WalletAdapter(this, walletLst);
        grid_wallet.setAdapter(Adapter);

        grid_wallet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (walletLst.get(position).getGateway_name()) {

                    case Const.paypal:
                        if (TextUtils.isEmpty(CONFIG_CLIENT_ID)) {
                            EbizworldUtils.showShortToast(getResources().getString(R.string.error_payment_key), AmbulanceWalletActivity.this);
                            dialog.dismiss();
                        } else {
                            payBypaypal(et_enter_amount.getText().toString());
                            dialog.dismiss();
                        }
                        break;
                    case Const.paygate:
                        dialog.dismiss();
                        showPayGate();
                        break;
                    case Const.stripe:
                        dialog.dismiss();
                        showStripe();
                        break;

                }

            }
        });

        dialog.show();
    }

    private void showStripe() {
        final Dialog payStripe = new Dialog(AmbulanceWalletActivity.this, R.style.DialogThemeforview);
        payStripe.requestWindowFeature(Window.FEATURE_NO_TITLE);
        payStripe.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.fade_drawable));
        payStripe.setCancelable(true);
        payStripe.setContentView(R.layout.strip_layout);
        final CardInputWidget mCardInputWidget = (CardInputWidget) payStripe.findViewById(R.id.card_input_widget_stripe);

        final Stripe stripe = new Stripe(AmbulanceWalletActivity.this, STRIPE_KEY);


        payStripe.findViewById(R.id.confirm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null == mCardInputWidget.getCard()) {
                    EbizworldUtils.showShortToast(getResources().getString(R.string.error_card), AmbulanceWalletActivity.this);
                } else if (null != mCardInputWidget.getCard().getNumber() && null != mCardInputWidget.getCard().getExpMonth() && null != mCardInputWidget.getCard().getExpYear() && null != mCardInputWidget.getCard().getCVC()) {
                    EbizworldUtils.showSimpleProgressDialog(AmbulanceWalletActivity.this, getResources().getString(R.string.txt_load), false);
                    Card card = new Card(mCardInputWidget.getCard().getNumber(), mCardInputWidget.getCard().getExpMonth(), mCardInputWidget.getCard().getExpYear(), mCardInputWidget.getCard().getCVC());
                    if (!card.validateNumber()) {
                        // Do not continue token creation.

                    } else {
                        stripe.createToken(
                                card,
                                new TokenCallback() {
                                    public void onSuccess(Token token) {
                                        // Send token to your server
                                        EbizworldUtils.removeProgressDialog();
                                        CreditBalance(token.getId(), "stripe");
                                        payStripe.dismiss();
                                    }

                                    public void onError(Exception error) {
                                        // Show localized error message
                                        Toast.makeText(AmbulanceWalletActivity.this,
                                                error.getLocalizedMessage(),
                                                Toast.LENGTH_LONG
                                        ).show();
                                    }
                                }
                        );
                    }
                } else {
                    EbizworldUtils.showShortToast(getResources().getString(R.string.error_card), AmbulanceWalletActivity.this);
                }

            }
        });


        payStripe.show();
    }


    private void showPayGate() {
        final Dialog payGatedialog = new Dialog(AmbulanceWalletActivity.this, R.style.DialogThemeforview);
        payGatedialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        payGatedialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.fade_drawable));
        payGatedialog.setCancelable(true);
        payGatedialog.setContentView(R.layout.creditcard_view);
        FloatingActionButton btn_floating_ok = (FloatingActionButton) payGatedialog.findViewById(R.id.btn_floating_ok);
        final EditText et_card_no = (EditText) payGatedialog.findViewById(R.id.et_card_no);
        final EditText et_card_month = (EditText) payGatedialog.findViewById(R.id.et_card_month);
        final EditText et_card_year = (EditText) payGatedialog.findViewById(R.id.et_card_year);
        final EditText et_card_ccv = (EditText) payGatedialog.findViewById(R.id.et_card_ccv);
        final EditText et_card_holder_fname = (EditText) payGatedialog.findViewById(R.id.et_card_holder_fname);
        final EditText et_card_holder_lname = (EditText) payGatedialog.findViewById(R.id.et_card_holder_lname);
        final EditText et_card_holder_email = (EditText) payGatedialog.findViewById(R.id.et_card_holder_email);

        btn_floating_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(et_card_no.getText().toString())) {
                    EbizworldUtils.showShortToast(getResources().getString(R.string.txt_enter_card), AmbulanceWalletActivity.this);
                    et_card_no.requestFocus();
                } else if (TextUtils.isEmpty(et_card_month.getText().toString())) {
                    EbizworldUtils.showShortToast(getResources().getString(R.string.txt_error_month), AmbulanceWalletActivity.this);
                    et_card_month.requestFocus();
                } else if (TextUtils.isEmpty(et_card_year.getText().toString())) {
                    EbizworldUtils.showShortToast(getResources().getString(R.string.txt_error_year), AmbulanceWalletActivity.this);
                    et_card_year.requestFocus();
                } else if (TextUtils.isEmpty(et_card_ccv.getText().toString())) {
                    EbizworldUtils.showShortToast(getResources().getString(R.string.txt_error_ccv), AmbulanceWalletActivity.this);
                    et_card_ccv.requestFocus();
                } else if (TextUtils.isEmpty(et_card_holder_fname.getText().toString())) {
                    EbizworldUtils.showShortToast(getResources().getString(R.string.txt_fname_error), AmbulanceWalletActivity.this);
                    et_card_holder_fname.requestFocus();
                } else if (TextUtils.isEmpty(et_card_holder_lname.getText().toString())) {
                    EbizworldUtils.showShortToast(getResources().getString(R.string.txt_lname_error), AmbulanceWalletActivity.this);
                    et_card_holder_lname.requestFocus();
                } else if (TextUtils.isEmpty(et_card_holder_email.getText().toString())) {
                    EbizworldUtils.showShortToast(getResources().getString(R.string.txt_email_error), AmbulanceWalletActivity.this);
                    et_card_holder_email.requestFocus();
                } else {
                    payGatedialog.dismiss();
                    /*sendpayGateData(et_card_no.getText().toString(),
                    et_card_month.getText().toString(),
                    et_card_year.getText().toString(),
                    et_card_ccv.getText().toString(),
                    et_card_holder_fname.getText().toString(),
                    et_card_holder_lname.getText().toString(),
                    et_card_holder_email.getText().toString());*/
                }


            }


        });

        payGatedialog.show();


    }

    private void sendpayGateData(String cardNo, String cardMonth, String cardYear, String cardCVV, /*String Fname, String Lname*/ String fullname, String email) {
        if (!EbizworldUtils.isNetworkAvailable(this)) {
            return;
        }
        if (patientProfile != null) {

            EbizworldUtils.showSimpleProgressDialog(this, "Loading...", false);
            HashMap<String, String> map = new HashMap<String, String>();
            HashMap<String, String> mapHead = new HashMap<String, String>();

            map.put(Const.Params.URL, Const.ServiceType.WALLET_BALANCE + new PreferenceHelper(this).getUserId() + "/paygate/initiate");
            /*map.put(Const.Params.FIRSTNAME, Fname);
            map.put(Const.Params.LAST_NAME, Lname);*/
            map.put(Const.Params.FULLNAME, fullname);
            map.put(Const.Params.EMAIL, email);
            map.put("card_no", cardNo);
            map.put("expiry_date", cardMonth + cardYear);
            map.put("cvv", cardCVV);
            map.put("amount", et_enter_amount.getText().toString());

            mapHead.put("authorization", WALLET_SECRET_KEY);
            Log.d("mahi", map.toString());
            new VolleyRequester(this, Const.POST, map, Const.ServiceCode.WALLET_PAYGATE,
                    this, mapHead);
        }

    }

    private void payBypaypal(String amount) {

        addMoneytoWallet = new PayPalPayment(new BigDecimal(amount), "USD",
                "Wallet", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(AmbulanceWalletActivity.this,
                PaymentActivity.class);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, addMoneytoWallet);

        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }


    public void onFuturePaymentPressed(View pressed) {
        Intent intent = new Intent(AmbulanceWalletActivity.this,
                PayPalFuturePaymentActivity.class);

        startActivityForResult(intent, REQUEST_CODE_FUTURE_PAYMENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {

            if (resultCode == Activity.RESULT_OK) {

                PaymentConfirmation confirm = data
                        .getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                if (confirm != null) {

                    Log.d("HaoLS", "payment confirm " + confirm.toJSONObject().toString());

                    JSONObject job = confirm.toJSONObject();
                    JSONObject responseOBJ = job.optJSONObject("response");
                    String TRX_ID = responseOBJ.optString("id");

                    if (null != TRX_ID && !(TRX_ID.length() == 0)) {
                        CreditBalance(TRX_ID, "PAYPAL");
                    }


                    try {

                        EbizworldUtils.appLogDebug("HaoLS", "payment confirm 2: " + confirm.getPayment().toJSONObject().toString(4));

                    } catch (JSONException e) {

                        e.printStackTrace();
                        EbizworldUtils.appLogError("HaoLS", "onActivityResult " + e.toString());
                    }


                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                System.out.println("The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                System.out
                        .println("An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        } else if (requestCode == REQUEST_CODE_FUTURE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth = data
                        .getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Log.i("FuturePaymentExample", auth.toJSONObject()
                                .toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.i("FuturePaymentExample", authorization_code);

                        sendAuthorizationToServer(auth);
                        Toast.makeText(getApplicationContext(),
                                "Future Payment code received from PayPal",
                                Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        Log.e("FuturePaymentExample",
                                "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("FuturePaymentExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i("FuturePaymentExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        }
    }

    private void CreditBalance(String trx_id, String paymentType) {
        if (!EbizworldUtils.isNetworkAvailable(this)) {
            return;
        }
        EbizworldUtils.showSimpleProgressDialog(this, "Adding Amount...", false);
        HashMap<String, String> map = new HashMap<String, String>();
        HashMap<String, String> mapHead = new HashMap<String, String>();

        map.put(Const.Params.URL, Const.ServiceType.WALLET_HOST_URL + "/api/businesses/users/" + new PreferenceHelper(this).getUserId() + "/balance/credit");
        map.put("gateway", paymentType);
        map.put("payment_id", trx_id);
        if (paymentType.equals("stripe")) {
            map.put("amount", et_enter_amount.getText().toString());
        }
        mapHead.put("Authorization", WALLET_SECRET_KEY);

        Log.d("mahi", map.toString());
        new VolleyRequester(this, Const.POST, map, Const.ServiceCode.WALLET_CREDIT,
                this, mapHead);
    }

    private void sendAuthorizationToServer(PayPalAuthorization authorization) {

    }

    public void onFuturePaymentPurchasePressed(View pressed) {
        // Get the Application Correlation ID from the SDK
        String correlationId = PayPalConfiguration
                .getApplicationCorrelationId(this);

        Log.i("FuturePaymentExample", "Application Correlation ID: "
                + correlationId);

        // TODO: Send correlationId and transaction details to your server for
        // processing with
        // PayPal...
        Toast.makeText(getApplicationContext(),
                "App Correlation ID received from SDK", Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public void onDestroy() {
        // Stop service when done
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        switch (serviceCode) {
            case Const.ServiceCode.WALLET_TYPES:
                Log.d("wallet", "wallet types" + response);
                EbizworldUtils.showShortToast(response, AmbulanceWalletActivity.this);
                try {
                    JSONObject job = new JSONObject(response);
                    if (job.getString("success").equals("true")) {
                        walletLst.clear();
                        JSONObject dataObj = job.getJSONObject("data");
                        JSONArray gatwayArray = dataObj.getJSONArray("gateways");
                        for (int i = 0; i < gatwayArray.length(); i++) {
                            Wallets wall = new Wallets();
                            JSONObject wallObj = gatwayArray.getJSONObject(i);
                            wall.setGateway_id(wallObj.optString("gateway_id"));
                            wall.setGateway_name(wallObj.optString("gateway_name"));
                            JSONObject settingObj = wallObj.optJSONObject("settings");
                            if (settingObj.has("PAYPAL_CLIENT_ID")) {
                                CONFIG_CLIENT_ID = settingObj.optString("PAYPAL_CLIENT_ID");
                            }
                            if (settingObj.has("PAYPAL_LIVE_MODE")) {
                                MODE = settingObj.optString("PAYPAL_LIVE_MODE");
                            }
                            if (settingObj.has("stripe_api_publishable_key")) {
                                STRIPE_KEY = settingObj.optString("stripe_api_publishable_key");
                            }

                            walletLst.add(wall);
                        }
                        if (TextUtils.isEmpty(CONFIG_CLIENT_ID)) {
                            btn_add_money.setEnabled(true);
                            btn_add_money.setBackgroundColor(getResources().getColor(R.color.black));
                            EbizworldUtils.removeProgressDialog();
                            EbizworldUtils.showShortToast(getResources().getString(R.string.error_payment_key), this);
                        } else {
                            IntitiatePayPal();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case Const.ServiceCode.WALLET_BALANCE:

                Log.d("wallet", "balance" + response);
                EbizworldUtils.showShortToast(response, AmbulanceWalletActivity.this);

                try {
                    JSONObject job = new JSONObject(response);
                    if (job.getString("success").equals("true")) {
                        JSONObject data = job.getJSONObject("data");
                        tv_total_balance.setText("$" + " " + data.optString("user_balance"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case Const.ServiceCode.WALLET_CREDIT:
                EbizworldUtils.removeProgressDialog();
                Log.d("wallet", "balance credit" + response);
                EbizworldUtils.showShortToast(response, AmbulanceWalletActivity.this);
                try {
                    JSONObject job = new JSONObject(response);
                    if (job.getString("success").equals("true")) {
                        JSONObject data = job.getJSONObject("data");
                        tv_total_balance.setText("$" + " " + data.optString("user_balance"));
                        et_enter_amount.setText("");
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.add_money_success),
                                Toast.LENGTH_LONG).show();
                    } else {

                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.fail_add_money),
                                Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case Const.ServiceCode.WALLET_PAYGATE:
                EbizworldUtils.removeProgressDialog();
                Log.d("wallet", "Paygate URL" + response);
                EbizworldUtils.showShortToast(response, AmbulanceWalletActivity.this);
                try {
                    JSONObject job = new JSONObject(response);
                    if (job.getString("success").equals("true")) {
                        JSONObject data = job.getJSONObject("data");
                        String URl = data.optString("payment_start_url");

                        Intent intent = new Intent(AmbulanceWalletActivity.this, PayGateWeb.class);
                        intent.putExtra("URl", URl);// pass your values and retrieve them in the other Activity using keyName
                        startActivity(intent);
                        this.finish();
                    } else {
                        String text = job.optString("text");
                        Toast.makeText(getApplicationContext(), text,
                                Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }

    }

    private void IntitiatePayPal() {
        if (MODE.equals("true")) {
            CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_PRODUCTION;
        } else {
            CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;
        }
        if (!TextUtils.isEmpty(CONFIG_CLIENT_ID))
            config = new PayPalConfiguration()
                    .environment(CONFIG_ENVIRONMENT)
                    .clientId(CONFIG_CLIENT_ID);

                /*// The following are only used in PayPalFuturePaymentActivity.
                .merchantName("Wallet Merchant")
                .merchantPrivacyPolicyUri(Uri.parse("https://www.example.com/privacy"))
                .merchantUserAgreementUri(Uri.parse("https://www.example.com/legal"));*/


        new CountDownTimer(4000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                Intent intent = new Intent(AmbulanceWalletActivity.this, PayPalService.class);
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                startService(intent);

                btn_add_money.setEnabled(true);
                btn_add_money.setBackgroundColor(getResources().getColor(R.color.black));
                EbizworldUtils.removeProgressDialog();

            }

        }.start();


    }


}
