package sg.go.user.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;

import sg.go.user.HttpRequester.VolleyRequester;
import sg.go.user.Interface.AsyncTaskCompleteListener;
import sg.go.user.MainActivity;
import sg.go.user.Models.Wallets;
import sg.go.user.R;
import sg.go.user.Utils.Commonutils;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.EbizworldUtils;
import sg.go.user.Utils.PreferenceHelper;

public class WalletFragment extends BaseFragment  implements AsyncTaskCompleteListener {

    private Wallets wallets;
    private TextView txt_recharge_ewallet,txt_total_money_wallet;
    private ImageView img_pay_ewallet;
    private EditText edt_input_money_wallet;
    private Button btn_recharge_ewallet;
    PreferenceHelper preferenceHelper_Wallet;
    private Toolbar toolbar_wallet;
    private String Get_Money_Recharge_Wallet;
    private ImageButton img_wallet_back;
    private  MainActivity activity;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_wallet,container,false);



        mapping();

        ApiGetWallet();

        wallets = new Wallets();

        return view;
    }

    private void mapping() {

        img_pay_ewallet = view.findViewById(R.id.img_pay_ewallet);

        btn_recharge_ewallet = view.findViewById(R.id.btn_recharge_ewallet);

        txt_total_money_wallet = view.findViewById(R.id.txt_total_money_wallet);

        edt_input_money_wallet = view.findViewById(R.id.edt_input_money_wallet);

        toolbar_wallet = view.findViewById(R.id.toolbar_wallet);

        img_wallet_back = view.findViewById(R.id.img_wallet_back);

        edt_input_money_wallet.addTextChangedListener(onTextChangedListener());

        img_wallet_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                activity.addFragment(new AccountFragment(),true,Const.ACCOUNT_FRAGMENT,false);


            }
        });
        btn_recharge_ewallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Get_Money_Recharge_Wallet = edt_input_money_wallet.getText().toString();

                if (Get_Money_Recharge_Wallet.isEmpty()) {

                    EbizworldUtils.showLongToast("Please enter the amount", activity);

                } else {

                    Get_Money_Recharge_Wallet.replaceAll(",", "");

                    EbizworldUtils.showLongToast("OK: " + Get_Money_Recharge_Wallet, activity);

                }

            }
        });



    }

    private TextWatcher onTextChangedListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                edt_input_money_wallet.removeTextChangedListener(this);

                try {
                    String origiString = editable.toString();

                    Long longval;

                    if (origiString.contains(",")) {

                        origiString = origiString.replaceAll(",", "");

                    }
                    longval = Long.parseLong(origiString);

                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);

                    formatter.applyPattern("#,###,###,###");

                    String formattedString = formatter.format(longval);

                    //setting text after format to EditText
                    edt_input_money_wallet.setText(formattedString);

                    edt_input_money_wallet.setSelection(edt_input_money_wallet.getText().length());

                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }

                edt_input_money_wallet.addTextChangedListener(this);
            }
        };


    }



    private void ApiGetWallet() {

        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        HashMap<String, String> map = new HashMap<String, String>();

        map.put(Const.Params.URL, Const.ServiceType.EWALLET);

        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());

        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());

        Log.d("HaoLS", "Getting show wallet " + map.toString());
        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.SHOW_RECHARGE_WALLET,
                this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

        }
    }

    private void postNonceToServer(String nonce) {

        if (!EbizworldUtils.isNetworkAvailable(getActivity())) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;

        }

        Commonutils.progressdialog_show(activity, "Loading...");
        HashMap<String, String> map = new HashMap<>();

//        if (new PreferenceHelper(activity).getLoginType().equals(Const.PatientService.PATIENT)){

        map.put(Const.Params.URL, Const.ServiceType.POST_PAYPAL_NONCE_URL);

//        }else if (new PreferenceHelper(activity).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)){
//
//            map.put(Const.Params.URL, Const.NursingHomeService.POST_PAYPAL_NONCE_URL);
//
//        }else if (new PreferenceHelper(activity).getLoginType().equals(Const.HospitalService.HOSPITAL)){
//
//            map.put(Const.Params.URL, Const.HospitalService.POST_PAYPAL_NONCE_URL);
//
//        }

        map.put(Const.Params.ID, new PreferenceHelper(getActivity()).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(getActivity()).getSessionToken());
        map.put(Const.Params.REQUEST_ID, String.valueOf(new PreferenceHelper(getActivity()).getRequestId()));
        map.put(Const.Params.PAYMENT_METHOD_NONCE, nonce);

        EbizworldUtils.appLogDebug("HaoLS", "postNonceToServer: " + map.toString());

        new VolleyRequester(getActivity(), Const.POST, map, Const.ServiceCode.POST_PAYPAL_NONCE, this);

    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        switch (serviceCode){

            case Const.ServiceCode.SHOW_RECHARGE_WALLET: {
                Log.d("DatGetWallet", "" + response);

                if (response != null) {

                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response);

                        if (jsonObject.getBoolean("success")) {

                            String total_Money_Wallet = jsonObject.getString("e_wallet");

                            txt_total_money_wallet.setText(" S$ "+ total_Money_Wallet );

                            new PreferenceHelper(activity).putTotalRechargeWallet(total_Money_Wallet);



                        } else {
                            EbizworldUtils.showLongToast("error", activity);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {

                    EbizworldUtils.showLongToast("error", activity);
                    EbizworldUtils.removeProgressDialog();

                }

            }
            break;


            case  Const.ServiceCode.GET_BRAIN_TREE_TOKEN_URL_WALLET:
            {
                EbizworldUtils.appLogInfo("HaoLS", "GET_BRAIN_TREE_TOKEN_URL: " + response);

                if (response != null && activity != null){

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getString("success").equals("true")){

                            EbizworldUtils.showShortToast("Success " + jsonObject.getString("success"), activity);

                            if (jsonObject.has("client_token")){

                                DropInRequest dropInRequest = new DropInRequest()
                                        .clientToken(jsonObject.getString("client_token"));

                                startActivityForResult(dropInRequest.getIntent(activity), Const.ServiceCode.REQUEST_PAYPAL_WALLET);
                            }

                        }else if (jsonObject.getString("success").equals("false")){

                            if (jsonObject.has("error")){

                                EbizworldUtils.showShortToast(jsonObject.getString("error"), activity);

                            }

                        }

                    } catch (JSONException e) {

                        e.printStackTrace();
                        EbizworldUtils.appLogError("HaoLS", "GET_BRAIN_TREE_TOKEN_URL: " + e.toString());

                    }


                }


            }
                break;



        }
    }

    private void getBrainTreeClientToken() {

        if (!EbizworldUtils.isNetworkAvailable(getActivity())) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        HashMap<String, String> map = new HashMap<>();

        map.put(Const.Params.URL, Const.ServiceType.GET_BRAIN_TREE_TOKEN_URL);
        map.put(Const.Params.ID, new PreferenceHelper(getActivity()).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(getActivity()).getSessionToken());

        EbizworldUtils.appLogDebug("HaoLS", "BrainTreeClientTokenMap: " + map.toString());

        new VolleyRequester(getActivity(), Const.POST, map, Const.ServiceCode.GET_BRAIN_TREE_TOKEN_URL_WALLET, this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Const.ServiceCode.REQUEST_PAYPAL){

            if (resultCode == Activity.RESULT_OK){

                DropInResult dropInResult = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);

                if (dropInResult.getPaymentMethodNonce() != null){

                    postNonceToServerWallet(dropInResult.getPaymentMethodNonce().getNonce());
                    EbizworldUtils.appLogDebug("HaoLS", "Billing Info Nonce: " + dropInResult.getPaymentMethodNonce().getNonce());

                }

            }else if (resultCode == Activity.RESULT_CANCELED){

                EbizworldUtils.showShortToast("Request is canceled", activity);

            }else {

                Exception exception = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                EbizworldUtils.showShortToast(exception.toString(), activity);
                EbizworldUtils.appLogError("HaoLS", "PayPal result" + exception.toString());
            }
        }
    }

    private void postNonceToServerWallet(String nonce) {
        if (!EbizworldUtils.isNetworkAvailable(getActivity())) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;

        }

        Commonutils.progressdialog_show(activity, "Loading...");
        HashMap<String, String> map = new HashMap<>();

        map.put(Const.Params.URL, Const.ServiceType.POST_PAYPAL_NONCE_URL);
        map.put(Const.Params.ID, new PreferenceHelper(getActivity()).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(getActivity()).getSessionToken());
        map.put(Const.Params.REQUEST_ID, String.valueOf(new PreferenceHelper(getActivity()).getRequestId()));
        map.put(Const.Params.PAYMENT_METHOD_NONCE, nonce);

        EbizworldUtils.appLogDebug("HaoLS", "postNonceToServer: " + map.toString());

        new VolleyRequester(getActivity(), Const.POST, map, Const.ServiceCode.POST_PAYPAL_NONCE, this);
    }
}
