package sg.go.user.Adapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import sg.go.user.HttpRequester.VolleyRequester;
import sg.go.user.Interface.AsyncTaskCompleteListener;
import sg.go.user.Models.CreditCards;
import sg.go.user.R;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.EbizworldUtils;
import sg.go.user.Utils.PreferenceHelper;


/**
 * Created by user on 10/5/2016.
 */
public class GetCardsAdapter extends RecyclerView.Adapter<GetCardsAdapter.CardsViewHolder> implements AsyncTaskCompleteListener {

    private List<CreditCards> cardDetailsList;
    private Context mContext;

    public GetCardsAdapter(Context context, List<CreditCards> cardDetailsList) {
        mContext = context;
        this.cardDetailsList = cardDetailsList;
    }

    @Override
    public CardsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_added_card_item_layout, null);
        CardsViewHolder holder = new CardsViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(CardsViewHolder holder, int position) {
        final CreditCards cardDetails = cardDetailsList.get(position);

        holder.cardNumber.setText("**** **** **** " + cardDetails.getCardNumber());
        String card_type = cardDetails.getCardtype().trim().toLowerCase();
        String type = cardDetails.getType();
        Drawable drawable = null;
        VectorDrawable vectorDrawable;
        BitmapDrawable bitmapDrawable;

        /*if(type.equals("card")) {

            switch (card_type) {

                case "americanexpress":
                    drawable = mContext.getResources().getDrawable(R.drawable.bt_amex);
                    break;

                case "visa":
                    drawable = mContext.getResources().getDrawable(R.drawable.bt_visa);
                    break;

                case "mastercard":
                    drawable = mContext.getResources().getDrawable(R.drawable.bt_mastercard);
                    break;

                case "jcb":
                    drawable = mContext.getResources().getDrawable(R.drawable.bt_jcb);
                    break;

                case "maestro":
                    drawable = mContext.getResources().getDrawable(R.drawable.bt_maestro);
                    break;

                case "dinersclub":
                    drawable = mContext.getResources().getDrawable(R.drawable.bt_diners);
                    break;

                case "chinaunionPay":
                    drawable = mContext.getResources().getDrawable(R.drawable.bt_android_pay);
                    break;

                default:

            }

        } else {

            drawable = mContext.getResources().getDrawable(R.drawable.ub__creditcard_paypal);
            holder.cardNumber.setText(cardDetails.getEmail());
            holder.cardRadioButton.setVisibility(View.GONE);

        }

        if(drawable!=null) {
            holder.cardTypeImage.setImageDrawable(drawable);
        }*/

        if (cardDetails.getIsDefault().equals("1")) {

            holder.cardRadioButton.setChecked(true);

        } else {

            holder.cardRadioButton.setChecked(false);

        }

        holder.cardRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setDefaultSelectCard(cardDetails.getCardNumber(),cardDetails.getType(),cardDetails.getCardId());
                } else {

                }


            }
        });
    }

    @Override
    public int getItemCount() {
        return cardDetailsList.size();
    }

    private void setDefaultSelectCard(String cardNumber,String type,String cardId) {
        if (!EbizworldUtils.isNetworkAvailable(mContext)) {

            return;
        }

//        EbizworldUtils.showSimpleProgressDialog(mContext, "Selecting default card...", false);
        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.ServiceType.CREATE_SELECT_CARD_URL);
        map.put(Const.Params.ID, new PreferenceHelper(mContext).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(mContext).getSessionToken());
        map.put(Const.Params.PAYMENT_MODE, type);
        map.put(Const.Params.CARD_ID, cardId);

        EbizworldUtils.appLogDebug("Ashutosh", "CreateSelectCardMap" + map);

        new VolleyRequester(mContext, Const.POST, map, Const.ServiceCode.CREATE_SELECT_CARD_URL, this);
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        JSONObject jsonObject;
        switch (serviceCode) {

            case Const.ServiceCode.CREATE_SELECT_CARD_URL:
                EbizworldUtils.appLogDebug("Ashutosh", "CreateSelectCardResponse" + response);
                try {
                    jsonObject = new JSONObject(response);
                    if (jsonObject.getString("success").equals("true")) {
                        getAddedCard();

                    } else {
                        EbizworldUtils.showShortToast(jsonObject.optString("error_message"), mContext);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Const.ServiceCode.GET_ADDED_CARDS_URL:
                EbizworldUtils.appLogDebug("Ashutosh", "GetAddedCardResponse" + response);
                try {

                    cardDetailsList.clear();
                    jsonObject = new JSONObject(response);
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
                                cardDetailsList.add(cardDetails);
                            }
                            notifyDataSetChanged();
                        }
                    } else {
                        EbizworldUtils.showShortToast(jsonObject.getString("error_message"), mContext);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

        }

    }

    private void getAddedCard() {
        if (!EbizworldUtils.isNetworkAvailable(mContext)) {

            return;
        }
//        EbizworldUtils.showSimpleProgressDialog(mContext, "Fetching All CreditCards...", false);
        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.ServiceType.GET_ADDED_CARDS_URL + Const.Params.ID + "="
                + new PreferenceHelper(mContext).getUserId() + "&" + Const.Params.TOKEN + "="
                +  new PreferenceHelper(mContext).getSessionToken());

        EbizworldUtils.appLogDebug("Ashutosh", "GetAddedCardMap" + map);

        new VolleyRequester(mContext, Const.GET, map, Const.ServiceCode.GET_ADDED_CARDS_URL, this);


    }

    public class CardsViewHolder extends RecyclerView.ViewHolder {
        private TextView cardNumber;
        private ImageView cardTypeImage;
        private RadioButton cardRadioButton;

        public CardsViewHolder(View itemView) {
            super(itemView);
            cardNumber = (TextView) itemView.findViewById(R.id.tv_card_number);
            cardTypeImage = (ImageView) itemView.findViewById(R.id.iv_card_type);
            cardRadioButton = (RadioButton) itemView.findViewById(R.id.rb_card_details);
        }
    }
}
