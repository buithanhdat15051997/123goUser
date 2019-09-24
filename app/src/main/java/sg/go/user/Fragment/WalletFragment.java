package sg.go.user.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import sg.go.user.HttpRequester.VolleyRequester;
import sg.go.user.Interface.AsyncTaskCompleteListener;
import sg.go.user.Models.Wallets;
import sg.go.user.R;
import sg.go.user.Utils.Commonutils;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.EbizworldUtils;
import sg.go.user.Utils.PreferenceHelper;

public class WalletFragment extends BaseFragment  implements AsyncTaskCompleteListener {

    Wallets wallets;
    PreferenceHelper preferenceHelper_Wallet;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_wallet,container,false);
        wallets = new Wallets();

        return view;
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        switch (serviceCode){
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
