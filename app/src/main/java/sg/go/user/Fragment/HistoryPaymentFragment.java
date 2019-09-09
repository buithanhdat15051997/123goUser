package sg.go.user.Fragment;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.TextView;

import sg.go.user.Adapter.HistoryPaymentAdapter;
import sg.go.user.HttpRequester.VolleyRequester;
import sg.go.user.Interface.AsyncTaskCompleteListener;
import sg.go.user.MainActivity;
import sg.go.user.Models.HistoryPayment;
import sg.go.user.R;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.EbizworldUtils;
import sg.go.user.Utils.ItemClickSupport;
import sg.go.user.Utils.PreferenceHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HistoryPaymentFragment extends Fragment implements AsyncTaskCompleteListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.toolbar_history_payment) Toolbar mToolbar_history_payment;

    @BindView(R.id.swipe_refresh_history_payment) SwipeRefreshLayout mSwipe_refresh_history_payment;

    @BindView(R.id.rv_history_payment) RecyclerView mRv_history_payment;

    private View mView;
    private MainActivity mMainActivity;
    private List<HistoryPayment> mHistoryPayments;
    private HistoryPayment mHistoryPayment;
    private HistoryPaymentAdapter mHistoryPaymentAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMainActivity = (MainActivity) getActivity();

        mHistoryPayments = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_history_payment, container, false);
        ButterKnife.bind(this, mView);

        mMainActivity.setSupportActionBar(mToolbar_history_payment);
        mMainActivity.getSupportActionBar();

        mSwipe_refresh_history_payment.setColorSchemeColors(getResources().getColor(R.color.lightblueA700));
        mSwipe_refresh_history_payment.setOnRefreshListener(this);

        ItemClickSupport.addTo(mRv_history_payment).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                if (mHistoryPayments.get(position) != null){

                    showHistoryPaymentDetail(mHistoryPayments.get(position));

                }
            }
        });

        getHistoryPayment();

        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {

        switch (serviceCode){

            case Const.ServiceCode.HISTORY_PAYMENT:{

                mSwipe_refresh_history_payment.setRefreshing(false);
                EbizworldUtils.appLogInfo("HaoLS", "getHistoryFragment: " + response);
                /*EbizworldUtils.showShortToast("getHistoryFragment: " + response, mMainActivity);*/

                if (response != null){

                    try {

                        mHistoryPayments.clear();
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getString("success").equals("true")){

                            JSONArray jsonArray = jsonObject.getJSONArray("history_payment");

                            if (jsonArray.length() > 0){

                                for (int i = 0; i < jsonArray.length(); i++){

                                    JSONObject object = jsonArray.getJSONObject(i);

                                    HistoryPayment historyPayment = new HistoryPayment();
                                    historyPayment.setmRequestID(object.getInt("request_id"));
                                    historyPayment.setmAmbulanceOperator(object.getString("taxi_name"));
                                    historyPayment.setmPickupAddress(object.getString("s_address"));
                                    historyPayment.setmDestinationAddress(object.getString("d_address"));
                                    historyPayment.setmDateCreate(object.getString("date_create"));
                                    historyPayment.setmBasePrice(object.getString("base_price"));
                                    historyPayment.setmTimePrice(object.getString("time_price"));
                                    historyPayment.setmLiftLanding(object.getString("lift_landing"));
                                    historyPayment.setmWeight(object.getString("weight"));
                                    historyPayment.setmOxygenTank(object.getString("oxygen_tank"));
                                    historyPayment.setmTrips(object.getString("trips"));
                                    historyPayment.setmPaymentType(object.getString("payment_type"));
                                    historyPayment.setmTotal(object.getString("total"));

                                    mHistoryPayments.add(historyPayment);
                                }

                                if (mHistoryPayments.size() > 0){

                                    mHistoryPaymentAdapter = new HistoryPaymentAdapter(mMainActivity, mHistoryPayments);
                                    LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getActivity(),
                                            getResources().getIdentifier("layout_animation_from_left",
                                                    "anim",
                                                    ((AppCompatActivity) getActivity()).getPackageName()));
                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mMainActivity, LinearLayoutManager.VERTICAL, false);
                                    mRv_history_payment.setLayoutManager(linearLayoutManager);
                                    mRv_history_payment.setHasFixedSize(true);
                                    mRv_history_payment.setItemAnimator(new DefaultItemAnimator());
                                    mRv_history_payment.setLayoutAnimation(animation);
                                    mRv_history_payment.setAdapter(mHistoryPaymentAdapter);

                                }

                            }
                        }else if (jsonObject.getString("success").equals("false")){

                            if (jsonObject.has("error")){

                                EbizworldUtils.showShortToast(jsonObject.getString("error"), mMainActivity);

                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        EbizworldUtils.appLogError("HaoLS", "getHistoryFragment: " + e.toString());
                    }

                }
            }
            break;
        }
    }

    @Override
    public void onRefresh() {

        mSwipe_refresh_history_payment.setRefreshing(true);
        getHistoryPayment();

    }

    private void getHistoryPayment(){

        if (!EbizworldUtils.isNetworkAvailable(mMainActivity)){

            mSwipe_refresh_history_payment.setRefreshing(false);
            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), mMainActivity);
            return;
        }

        HashMap<String, String> hashMap = new HashMap<>();

        if (new PreferenceHelper(mMainActivity).getLoginType().equals(Const.PatientService.PATIENT)){

            hashMap.put(Const.Params.URL, Const.ServiceType.HISTORY_PAYMENT);

        }else if (new PreferenceHelper(mMainActivity).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)){

            hashMap.put(Const.Params.URL, Const.NursingHomeService.HISTORY_PAYMENT);

        }else if (new PreferenceHelper(mMainActivity).getLoginType().equals(Const.HospitalService.HOSPITAL)){

            hashMap.put(Const.Params.URL, Const.HospitalService.HISTORY_PAYMENT);

        }

        hashMap.put(Const.Params.ID, new PreferenceHelper(mMainActivity).getUserId());
        hashMap.put(Const.Params.TOKEN, new PreferenceHelper(mMainActivity).getSessionToken());

        EbizworldUtils.appLogDebug("HaoLS", "getHistoryPayment: " + hashMap.toString());

        new VolleyRequester(mMainActivity, Const.POST, hashMap, Const.ServiceCode.HISTORY_PAYMENT, this);
    }

    private void showHistoryPaymentDetail(HistoryPayment historyPayment){

        if (mMainActivity != null){

            final Dialog historyPaymentDetail = new Dialog(mMainActivity, R.style.DialogSlideAnim_leftright_Fullscreen);
            historyPaymentDetail.requestWindowFeature(Window.FEATURE_NO_TITLE);
            historyPaymentDetail.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent_black)));
            historyPaymentDetail.setCancelable(true);
            historyPaymentDetail.setContentView(R.layout.dialog_history_payment_detail);

            TextView tv_history_payment_date = historyPaymentDetail.findViewById(R.id.tv_history_payment_date);
            TextView tv_history_payment_ambulance_operator = historyPaymentDetail.findViewById(R.id.tv_history_payment_ambulance_operator);
            TextView tv_history_payment_source = historyPaymentDetail.findViewById(R.id.tv_history_payment_source);
            TextView tv_history_payment_destination = historyPaymentDetail.findViewById(R.id.tv_history_payment_destination);
            TextView tv_history_payment_base_price_value = historyPaymentDetail.findViewById(R.id.tv_history_payment_base_price_value);
            TextView tv_history_payment_lift_landing_value = historyPaymentDetail.findViewById(R.id.tv_history_payment_lift_landing_value);
            TextView tv_history_payment_weight_value = historyPaymentDetail.findViewById(R.id.tv_history_payment_weight_value);
            TextView tv_history_payment_oxygen_tank_value = historyPaymentDetail.findViewById(R.id.tv_history_payment_oxygen_tank_value);
            TextView tv_history_payment_trips_value = historyPaymentDetail.findViewById(R.id.tv_history_payment_trips_value);
            LinearLayout tv_history_payment_trips_group = historyPaymentDetail.findViewById(R.id.tv_history_payment_trips_group);
            TextView tv_history_payment_total_value = historyPaymentDetail.findViewById(R.id.tv_history_payment_total_value);

            tv_history_payment_date.setText(historyPayment.getmDateCreate());
            tv_history_payment_ambulance_operator.setText(historyPayment.getmAmbulanceOperator());
            tv_history_payment_source.setText(historyPayment.getmPickupAddress());
            tv_history_payment_destination.setText(historyPayment.getmDestinationAddress());
            tv_history_payment_base_price_value.setText("$ " + historyPayment.getmBasePrice());
            tv_history_payment_lift_landing_value.setText("$ " + historyPayment.getmLiftLanding());
            tv_history_payment_weight_value.setText("$ " + historyPayment.getmWeight());
            tv_history_payment_oxygen_tank_value.setText("$ " + historyPayment.getmOxygenTank());

            if (historyPayment.getmTrips() == null){

                 tv_history_payment_trips_group.setVisibility(View.GONE);

            }else {

                tv_history_payment_trips_value.setText("$ " + historyPayment.getmTrips());
            }

            tv_history_payment_total_value.setText("$ " + historyPayment.getmTotal());

            historyPaymentDetail.show();

        }
    }
}
