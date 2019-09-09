package sg.go.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageButton;

import sg.go.user.Adapter.GetCardsAdapter;
import sg.go.user.HttpRequester.VolleyRequester;
import sg.go.user.Interface.AsyncTaskCompleteListener;
import sg.go.user.Models.CreditCards;

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

public class AddPaymentActivity extends AppCompatActivity implements AsyncTaskCompleteListener {
    private Toolbar cardToolbar;
    private ArrayList<CreditCards> cardslst;
    private ImageButton payment_back;
    private static final int REQUEST_CODE = 133;
    private FloatingActionButton addCardButton;
    private RecyclerView rv_add_card;
    private GetCardsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cardslst = new ArrayList<CreditCards>();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(R.layout.fragment_addpayment);
        cardToolbar = (Toolbar) findViewById(R.id.toolbar_addcard);
        addCardButton = (FloatingActionButton) findViewById(R.id.bn_add_card);
        rv_add_card = (RecyclerView) findViewById(R.id.rv_add_card);
        rv_add_card.addOnItemTouchListener(new RecyclerLongPressClickListener(this, rv_add_card, new RecyclerLongPressClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onLongItemClick(View view, int position) {
                if (!isFinishing()) {

                    showdeletecard(cardslst.get(position).getCardId());

                }

            }
        }));

        setSupportActionBar(cardToolbar);
        getSupportActionBar().setTitle(null);
        /*payment_back = (ImageButton) findViewById(R.id.payment_back);
        payment_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });*/
        addCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBrainTreeClientToken();
            }
        });

        getAddedCard();

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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure? You want to remove this card?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void removecard(String cardId) {
        if (!EbizworldUtils.isNetworkAvailable(this)) {

            return;
        }

        Commonutils.progressdialog_show(this, "Removing...");
        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.ServiceType.REMOVE_CARD);
        map.put(Const.Params.ID, new PreferenceHelper(this).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(this).getSessionToken());
        map.put(Const.Params.CARD_ID, cardId);

        EbizworldUtils.appLogDebug("HaoLS", "remove card" + map);

        new VolleyRequester(this, Const.POST, map, Const.ServiceCode.REMOVE_CARD, this);
    }

    private void getBrainTreeClientToken() {
        if (!EbizworldUtils.isNetworkAvailable(this)) {

            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.ServiceType.GET_BRAIN_TREE_TOKEN_URL);
        map.put(Const.Params.ID, new PreferenceHelper(this).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(this).getSessionToken());

        EbizworldUtils.appLogDebug("mahi", "BrainTreeClientTokenMap" + map);

        new VolleyRequester(this, Const.POST, map, Const.ServiceCode.GET_BRAIN_TREE_TOKEN_URL, this);

    }

    private void getAddedCard() {
        if (!EbizworldUtils.isNetworkAvailable(this)) {

            return;
        }
//        EbizworldUtils.showSimpleProgressDialog(mContext, "Fetching All CreditCards...", false);
        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.ServiceType.GET_ADDED_CARDS_URL + Const.Params.ID + "="
                + new PreferenceHelper(this).getUserId() + "&" + Const.Params.TOKEN + "="
                + new PreferenceHelper(this).getSessionToken());

        EbizworldUtils.appLogDebug("Ashutosh", "GetAddedCardMap" + map);

        new VolleyRequester(this, Const.GET, map, Const.ServiceCode.GET_ADDED_CARDS_URL, this);


    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
    }

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
                    Log.d("mahi", "error message" + error);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }*/
    }

    void postNonceToServer(String nonce) {
        if (!EbizworldUtils.isNetworkAvailable(this)) {

            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.ServiceType.CREATE_ADD_CARD_URL);
        map.put(Const.Params.ID, new PreferenceHelper(this).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(this).getSessionToken());
        map.put(Const.Params.PAYMENT_METHOD_NONCE, nonce);

        EbizworldUtils.appLogDebug("mahi", "BrainTreeADDCARDMap" + map);

        new VolleyRequester(this, Const.POST, map, Const.ServiceCode.CREATE_ADD_CARD_URL, this);

    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        switch (serviceCode) {
            case Const.ServiceCode.GET_BRAIN_TREE_TOKEN_URL:
                EbizworldUtils.appLogDebug("mahi", "BrainTreeClientTokenResponse" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("success").equals("true")) {
                        String clientToken = jsonObject.optString("client_token");

                        EbizworldUtils.showShortToast("Feature is fixing", AddPaymentActivity.this);
                        /*PaymentRequest paymentRequest = new PaymentRequest()
                                .clientToken(clientToken)
                                .submitButtonText("Add Amount");
                        startActivityForResult(paymentRequest.getIntent(this), REQUEST_CODE);*/


                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Const.ServiceCode.CREATE_ADD_CARD_URL:
                EbizworldUtils.appLogDebug("Ashutosh", "BrainTreeClientTokenResponse" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("success").equals("true")) {

                        EbizworldUtils.showShortToast("Card Added Successfully!", this);
                        getAddedCard();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Const.ServiceCode.REMOVE_CARD:
                Log.d("mahi","delete card"+response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("success").equals("true")) {
                        Commonutils.progressdialog_hide();
                        EbizworldUtils.showShortToast("Card Removed Successfully!", this);

                        getAddedCard();


                    } else {
                        Commonutils.progressdialog_hide();
                        String error_msg = jsonObject.getString("message");
                        Commonutils.showtoast(error_msg,this);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Const.ServiceCode.GET_ADDED_CARDS_URL:
                EbizworldUtils.appLogDebug("Ashutosh", "GetAddedCardRe;sponse" + response);
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
                                LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                                rv_add_card.setLayoutManager(layoutManager);
                                 adapter = new GetCardsAdapter(this, cardslst);
                                rv_add_card.setAdapter(adapter);
                                LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(this,getResources().getIdentifier("layout_animation_from_left","anim",getPackageName()));
                                rv_add_card.setLayoutAnimation(animation);
                                adapter.notifyDataSetChanged();
                                rv_add_card.scheduleLayoutAnimation();
                            }
                        }

                    } else {

                        EbizworldUtils.showShortToast(jsonObject.getString("error_message"), this);
                        if(adapter!=null){
                            adapter.notifyDataSetChanged();
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

        }
    }


}
