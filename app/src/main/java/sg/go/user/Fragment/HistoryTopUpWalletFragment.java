package sg.go.user.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import sg.go.user.Adapter.HistoryTopUpWalletAdapter;
import sg.go.user.HttpRequester.VolleyRequester;
import sg.go.user.Interface.AsyncTaskCompleteListener;
import sg.go.user.MainActivity;
import sg.go.user.Models.HistoryTopUpWallet;
import sg.go.user.R;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.EbizworldUtils;
import sg.go.user.Utils.PreferenceHelper;

public class HistoryTopUpWalletFragment extends Fragment implements AsyncTaskCompleteListener {

    private RecyclerView Recyc_history_TopUp_Wallet;
    private MainActivity activity;
    private ArrayList<HistoryTopUpWallet> ArraylisthistoryTopUpWallets;
    private HistoryTopUpWalletAdapter historyTopUpWalletAdapter;
    private View view;
    private TextView txt_not_available_topup;

    private ImageView img_history_Topup_wallet_back;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_history_top_up_wallet, container, false);

        Mapping();

        GetHistoryTopUpWallet();

        return view;
    }

    private void Mapping() {

        Recyc_history_TopUp_Wallet = view.findViewById(R.id.Recyc_history_TopUp_Wallet);

        img_history_Topup_wallet_back = view.findViewById(R.id.img_history_Topup_wallet_back);

        txt_not_available_topup = view.findViewById(R.id.txt_not_available_topup);

        img_history_Topup_wallet_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                activity.addFragment(new WalletFragment(), false, Const.WALLET_FRAGMENT, true);

            }
        });

        ArraylisthistoryTopUpWallets = new ArrayList<>();

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        Recyc_history_TopUp_Wallet.setHasFixedSize(true);

        Recyc_history_TopUp_Wallet.setItemViewCacheSize(5);

        Recyc_history_TopUp_Wallet.setDrawingCacheEnabled(true);

        Recyc_history_TopUp_Wallet.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        Recyc_history_TopUp_Wallet.setLayoutManager(layoutManager);

        Recyc_history_TopUp_Wallet.setItemAnimator(new DefaultItemAnimator());

        historyTopUpWalletAdapter = new HistoryTopUpWalletAdapter(getActivity(), ArraylisthistoryTopUpWallets);

        Recyc_history_TopUp_Wallet.setAdapter(historyTopUpWalletAdapter);

    }

    private void GetHistoryTopUpWallet() {

        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }


        HashMap<String, String> params = new HashMap<>();

        params.put(Const.Params.URL, Const.ServiceType.HISTORY_TOPUP_WALLET);

        params.put(Const.Params.ID, new PreferenceHelper(getActivity()).getUserId());

        params.put(Const.Params.TOKEN, new PreferenceHelper(getActivity()).getSessionToken());

        new VolleyRequester(getActivity(), Const.POST, params, 100100, this);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    @Override
    public void onTaskCompleted(String response, int serviceCode) {

        switch (serviceCode) {
            case 100100:
                Log.d("DatGetWallet", response + "");

                if (response != null) {
                    JSONObject jsonObject = null;

                    try {

                        jsonObject = new JSONObject(response);

                        if (jsonObject.getBoolean("success")) {

                            JSONArray jsonArray = new JSONArray(jsonObject.getString("history_topup"));

                            if (jsonArray.length() > 0) {

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                    float moneyTopup = jsonObject1.getInt("money");

                                    float moneyOldTopup = jsonObject1.getInt("pre_money");
                                    String paymentTypeTopup = jsonObject1.getString("payment_type");
                                    String datetimeTopup = jsonObject1.getString("created_at");

                                    Log.d("DatGetWallet", String.valueOf(moneyTopup) + " " + paymentTypeTopup + " " + datetimeTopup);

                                    ArraylisthistoryTopUpWallets.add(new HistoryTopUpWallet(moneyTopup, moneyOldTopup, paymentTypeTopup, datetimeTopup));

                                    historyTopUpWalletAdapter.notifyDataSetChanged();

                                }
                            } else {

                                txt_not_available_topup.setVisibility(View.VISIBLE);
                                Recyc_history_TopUp_Wallet.setVisibility(View.GONE);

                            }


                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {

                    EbizworldUtils.appLogDebug("failed", "");
                }

                break;

        }

    }
}
