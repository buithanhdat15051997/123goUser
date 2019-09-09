package sg.go.user.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import sg.go.user.Adapter.GetCardsAdapter;
import sg.go.user.HttpRequester.VolleyRequester;
import sg.go.user.Interface.AsyncTaskCompleteListener;
import sg.go.user.MainActivity;
import sg.go.user.Models.CreditCards;
import sg.go.user.R;
import sg.go.user.Utils.EbizworldUtils;
import sg.go.user.Utils.Commonutils;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.PreferenceHelper;
import sg.go.user.Utils.RecyclerLongPressClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 1/21/2017.
 */

public class AddPaymentFragment extends Fragment implements AsyncTaskCompleteListener, View.OnClickListener {
    private Toolbar cardToolbar;
    private ArrayList<CreditCards> cardslst;
    private static final int REQUEST_CODE = 133;
    private FloatingActionButton addCardButton;
    private RecyclerView rv_added_card;
    private GetCardsAdapter adapter;
    private View mViewRoot;
    private MainActivity mMainActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMainActivity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mViewRoot = inflater.inflate(R.layout.fragment_addpayment, container, false);

        cardslst = new ArrayList<CreditCards>();

        cardToolbar = (Toolbar) mViewRoot.findViewById(R.id.toolbar_addcard);
        addCardButton = (FloatingActionButton) mViewRoot.findViewById(R.id.bn_add_card);
        rv_added_card = (RecyclerView) mViewRoot.findViewById(R.id.rv_add_card);
        rv_added_card.addOnItemTouchListener(new RecyclerLongPressClickListener(getActivity(), rv_added_card, new RecyclerLongPressClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onLongItemClick(View view, int position) {

                /*if (!isFinishing()) {

                    showdeletecard(cardslst.get(position).getCardId());

                }
*/

                if (mMainActivity != null){

                    showdeletecard(cardslst.get(position).getCardId());

                }

            }
        }));

        ((AppCompatActivity) getActivity()).setSupportActionBar(cardToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(null);

        addCardButton.setOnClickListener(this);

        getAddedCard();

        return mViewRoot;
    }

    private void showdeletecard(final String cardId) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        dialog.dismiss();
                        removecard(cardId);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        dialog.dismiss();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Are you sure? You want to remove this card?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void removecard(String cardId) {
        if (!EbizworldUtils.isNetworkAvailable(getActivity())) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), mMainActivity);
            return;
        }

        Commonutils.progressdialog_show(getActivity(), "Removing...");
        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.ServiceType.REMOVE_CARD);
        map.put(Const.Params.ID, new PreferenceHelper(getActivity()).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(getActivity()).getSessionToken());
        map.put(Const.Params.CARD_ID, cardId);

        EbizworldUtils.appLogDebug("HaoLS", "remove card: " + map);

        new VolleyRequester(getActivity(), Const.POST, map, Const.ServiceCode.REMOVE_CARD, this);
    }

    private void getBrainTreeClientToken() {
        if (!EbizworldUtils.isNetworkAvailable(getActivity())) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), mMainActivity);
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.ServiceType.GET_BRAIN_TREE_TOKEN_URL);
        map.put(Const.Params.ID, new PreferenceHelper(getActivity()).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(getActivity()).getSessionToken());

        EbizworldUtils.appLogDebug("HaoLS", "BrainTreeClientTokenMap: " + map);

        new VolleyRequester(getActivity(), Const.POST, map, Const.ServiceCode.GET_BRAIN_TREE_TOKEN_URL, this);

    }

    private void getAddedCard() {
        if (!EbizworldUtils.isNetworkAvailable(getActivity())) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), mMainActivity);
            return;
        }
//        EbizworldUtils.showSimpleProgressDialog(mContext, "Fetching All CreditCards...", false);
        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.ServiceType.GET_ADDED_CARDS_URL + Const.Params.ID + "="
                + new PreferenceHelper(getActivity()).getUserId() + "&" + Const.Params.TOKEN + "="
                + new PreferenceHelper(getActivity()).getSessionToken());

        EbizworldUtils.appLogDebug("HaoLS", "GetAddedCardMap: " + map);

        new VolleyRequester(getActivity(), Const.GET, map, Const.ServiceCode.GET_ADDED_CARDS_URL, this);


    }

    /*@Override
    public void onBackPressed() {
        Intent i = new Intent(getActivity(),MainActivity.class);
        startActivity(i);
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        /*if (requestCode == REQUEST_CODE) {

            if (resultCode == Activity.RESULT_OK) {

                PaymentMethodNonce paymentMethodNonce = data.getParcelableExtra(
                        BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE
                );

                String nonce = paymentMethodNonce.getNonce();
               // Log.d("mahi","none value"+nonce);
                // Send the nonce to your server.
                postNonceToServer(nonce);
            } else {
                // handle errors here, an exception may be available in
                Exception error = null;
                try {

                    error = (Exception) data.getSerializableExtra(BraintreePaymentActivity.EXTRA_ERROR_MESSAGE);
                    EbizworldUtils.appLogError("HaoLS", "error message " + error);

                } catch (Exception e) {

                    e.printStackTrace();
                    EbizworldUtils.appLogError("HaoLS", "error message " + e.toString());

                }

            }

        }*/
    }

    void postNonceToServer(String nonce) {
        if (!EbizworldUtils.isNetworkAvailable(getActivity())) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), mMainActivity);
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.ServiceType.CREATE_ADD_CARD_URL);
        map.put(Const.Params.ID, new PreferenceHelper(getActivity()).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(getActivity()).getSessionToken());
        map.put(Const.Params.PAYMENT_METHOD_NONCE, nonce);

        EbizworldUtils.appLogDebug("HaoLS", "BrainTreeADDCARDMap: " + map);

        new VolleyRequester(getActivity(), Const.POST, map, Const.ServiceCode.CREATE_ADD_CARD_URL, this);

    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        switch (serviceCode) {
            case Const.ServiceCode.GET_BRAIN_TREE_TOKEN_URL:
                EbizworldUtils.appLogInfo("HaoLS", "BrainTreeClientTokenResponse: " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("success").equals("true")) {
                        String clientToken = jsonObject.optString("client_token");

                        EbizworldUtils.showShortToast("Feature is fixing", mMainActivity);

                        /*PaymentRequest paymentRequest = new PaymentRequest()
                                .clientToken(clientToken)
                                .submitButtonText("Add Card");
                        startActivityForResult(paymentRequest.getIntent(getActivity()), REQUEST_CODE);*/


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    EbizworldUtils.appLogError("HaoLS", "GET_BRAIN_TREE_TOKEN_URL: " + e.toString());
                }
                break;
            case Const.ServiceCode.CREATE_ADD_CARD_URL:

                EbizworldUtils.appLogInfo("HaoLS", "BrainTreeClientTokenResponse" + response);

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("success").equals("true")) {

                        EbizworldUtils.showShortToast("Card Added Successfully!", getActivity());
                        getAddedCard();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    EbizworldUtils.appLogError("HaoLS", "CREATE_ADD_CARD_URL: " + e.toString());
                }
                break;
            case Const.ServiceCode.REMOVE_CARD:
                EbizworldUtils.appLogInfo("HaoLS","delete card: " + response);
                try {

                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("success").equals("true")) {
                        Commonutils.progressdialog_hide();
                        EbizworldUtils.showShortToast("Card Removed Successfully!", getActivity());

                        getAddedCard();


                    } else {
                        Commonutils.progressdialog_hide();
                        String error_msg = jsonObject.getString("message");
                        Commonutils.showtoast(error_msg,getActivity());
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    EbizworldUtils.appLogError("HaoLS", "Remove card: " + e.toString());
                }
                break;

            case Const.ServiceCode.GET_ADDED_CARDS_URL:
                EbizworldUtils.appLogInfo("HaoLS", "GetAddedCard: " + response);

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    cardslst.clear();

                    if (jsonObject.getString("success").equals("true")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("cards");
                        if (jsonArray != null && jsonArray.length() > 0) {

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject cardObject = jsonArray.getJSONObject(i);
                                CreditCards cardDetails = new CreditCards();
                                cardDetails.setCardId(cardObject.optString("id"));
                                cardDetails.setCardNumber(cardObject.optString("last_four"));
                                cardDetails.setIsDefault(cardObject.optString("is_default"));
                                cardDetails.setCardtype(cardObject.optString("card_type"));
                                cardDetails.setType(cardObject.optString("type"));
                                cardDetails.setEmail(cardObject.optString("email"));
                                cardslst.add(cardDetails);
                            }

                            if (cardslst != null) {
                                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                                rv_added_card.setLayoutManager(layoutManager);
                                adapter = new GetCardsAdapter(getActivity(), cardslst);
                                rv_added_card.setAdapter(adapter);
                                LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getActivity(),getResources().getIdentifier("layout_animation_from_left","anim",getActivity().getPackageName()));
                                rv_added_card.setLayoutAnimation(animation);
                                adapter.notifyDataSetChanged();
                                rv_added_card.scheduleLayoutAnimation();
                            }
                        }

                    } else {

                        EbizworldUtils.showShortToast(jsonObject.getString("error_message"), getActivity());
                        if(adapter!=null){
                            adapter.notifyDataSetChanged();
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    EbizworldUtils.appLogError("HaoLS", "GET_ADDED_CARDS_URL " + e.toString());
                }
                break;

        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.bn_add_card:{

                getBrainTreeClientToken();
            }
        }
    }
}
