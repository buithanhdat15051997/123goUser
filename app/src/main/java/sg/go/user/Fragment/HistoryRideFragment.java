package sg.go.user.Fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import sg.go.user.Adapter.HistoryAdapter;
import sg.go.user.HttpRequester.VolleyRequester;
import sg.go.user.Interface.AsyncTaskCompleteListener;
import sg.go.user.MainActivity;
import sg.go.user.Models.History;
import sg.go.user.R;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.EbizworldUtils;
import sg.go.user.Utils.PreferenceHelper;
import sg.go.user.Utils.RecyclerLongPressClickListener;
import sg.go.user.restAPI.HistoryListAPI;

/**
 * Created by user on 1/20/2017.
 */

public class HistoryRideFragment extends Fragment implements AsyncTaskCompleteListener, SwipeRefreshLayout.OnRefreshListener {
    private Toolbar historymainToolbar;
    private ArrayList<History> historylst;
    private HistoryAdapter historyAdapter;
    private RecyclerView ride_lv;
    private ProgressBar histroy_progress_bar;
    private TextView histroy_empty,txt_date_time_history;
    private View mViewRoot;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Activity activity;
    private DatePickerDialog.OnDateSetListener onDateSetListener;
    private String date_history = "";
    private int month_history = 0;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mViewRoot = inflater.inflate(R.layout.fragment_history_ride, container, false);

        historylst = new ArrayList<History>();
        historymainToolbar = (Toolbar) mViewRoot.findViewById(R.id.toolbar_history);
        ride_lv = (RecyclerView) mViewRoot.findViewById(R.id.ride_lv);
        histroy_progress_bar = (ProgressBar) mViewRoot.findViewById(R.id.histroy_progress_bar);
        histroy_empty = (TextView) mViewRoot.findViewById(R.id.histroy_empty);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mViewRoot.findViewById(R.id.swipe_refresh_history_ride_list);

        txt_date_time_history = mViewRoot.findViewById(R.id.txt_date_time_history);

        txt_date_time_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                DialogChooseDate();


            }
        });

        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                month_history = month+1;

                Log.d("DAT_HISTORY",year+"/"+month+1+"/"+day);

                txt_date_time_history.setText(year+"/"+month_history);

                if(month_history>0){

                    GetHistory(month_history);

                }

            }
        };


        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.color_background_main));
        mSwipeRefreshLayout.setOnRefreshListener(this);

        if (getActivity() != null) {

            ((AppCompatActivity) getActivity()).setSupportActionBar(historymainToolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(null);

            ride_lv.addOnItemTouchListener(new RecyclerLongPressClickListener(getActivity(), ride_lv, new RecyclerLongPressClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                    switch (new PreferenceHelper(activity).getLoginType()) {

                        case Const.PatientService.PATIENT: {

                            showDetailedHistroy(historylst.get(position));

                        }
                        break;

                        case Const.NursingHomeService.NURSING_HOME: {

                           // showNursingHomeDetailedHistroy(historylst.get(position));

                        }
                        break;

                        case Const.HospitalService.HOSPITAL: {

                            //showHospitalDetailedHistroy(historylst.get(position));

                        }
                        break;
                    }

                }

                @Override
                public void onLongItemClick(View view, int position) {

                }
            }));
        }

        switch (new PreferenceHelper(activity).getLoginType()) {

            case Const.PatientService.PATIENT: {

                GetHistory(0);

            }

            break;
//
//            case Const.NursingHomeService.NURSING_HOME: {
//
//                new HistoryListAPI(activity, this).getNursingHomeHistory(Const.ServiceCode.GET_HISTORY);
//
//            }
//            break;
//
//            case Const.HospitalService.HOSPITAL: {
//
//                new HistoryListAPI(activity, this).getHospitalHistory(Const.ServiceCode.GET_HISTORY);
//
//            }
//            break;

        }

        return mViewRoot;
    }

    private void DialogChooseDate() {

        Calendar calendar = Calendar.getInstance();

        int year_history = calendar.get(Calendar.YEAR);
        int month_history = calendar.get(Calendar.MONTH);
        int day_history = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(activity
                ,android.R.style.Theme_Holo_Light_Dialog_MinWidth,onDateSetListener
                ,year_history,month_history,day_history);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ((ViewGroup) dialog.getDatePicker()).findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);

        dialog.show();



    }

    private void GetHistory(int month) {

        // new HistoryListAPI(activity, this).getPatientHistory(Const.ServiceCode.GET_HISTORY);

        if (!EbizworldUtils.isNetworkAvailable(activity)) {
            EbizworldUtils.showShortToast(activity.getResources().getString(R.string.network_error), activity);
        }

        EbizworldUtils.showSimpleProgressDialog(activity, getResources().getString(R.string.txt_load), true);
        /*EbizworldUtils.showSimpleProgressDialog(getActivity(),"",false);*/

        HashMap<String, String> map = new HashMap<String, String>();

       // map.put(Const.Params.URL, Const.ServiceType.GET_HISTORY);

        if(month==0){

            map.put(Const.Params.URL, Const.ServiceType.GET_HISTORY);

        }else {

            map.put("month", String.valueOf(month));
            map.put(Const.Params.URL, Const.ServiceType.GET_HISTORY_MONTH);

        }

        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());


        EbizworldUtils.appLogDebug("HaoLS", "Getting history ride " + map.toString());
        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.GET_HISTORY, this);


    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {

        if (getActivity() != null) {

            switch (serviceCode) {

                case Const.ServiceCode.GET_HISTORY:

                    mSwipeRefreshLayout.setRefreshing(false);

                    EbizworldUtils.appLogInfo("HaoLS", "res his: " + response);

                    if (response != null) {

                        EbizworldUtils.removeProgressDialog();

                        try {

                            JSONObject hisobj = new JSONObject(response);
                            if (hisobj.getString("success").equals("true")) {

                                EbizworldUtils.appLogDebug("HaoLS", "Get history ride succeeded");

                                //histroy_progress_bar.setVisibility(View.GONE);
                                historylst.clear();

                                JSONArray hisArray = hisobj.getJSONArray("requests");
                                if (hisArray.length() > 0) {
                                    for (int i = 0; i < hisArray.length(); i++) {
                                        JSONObject jsonObject = hisArray.getJSONObject(i);
                                        History history = new History();
                                        history.setHistory_id(jsonObject.getString("request_id"));
                                        history.setHistory_Dadd(jsonObject.getString("d_address"));
                                        history.setHistory_Sadd(jsonObject.getString("s_address"));
                                        history.setHistory_date(jsonObject.getString("date"));
                                        history.setProvider_name(jsonObject.getString("provider_name"));

                                        history.setHistory_type(jsonObject.getString("taxi_name"));

                                        history.setHistory_total(jsonObject.getString("total"));
                                        history.setHistory_picture(jsonObject.getString("picture"));
                                        history.setMap_image(jsonObject.getString("map_image"));
                                        history.setBase_price(jsonObject.getString("base_price"));
                                        history.setDistance_travel(jsonObject.getString("distance_travel"));
                                        history.setTotal_time(jsonObject.getString("total_time"));
                                        history.setTax_price(jsonObject.getString("tax_price"));
                                        history.setTime_price(jsonObject.getString("time_price"));
                                        history.setDistance_price(jsonObject.getString("distance_price"));
                                        history.setMin_price(jsonObject.getString("min_price"));
                                        history.setBooking_fee(jsonObject.getString("booking_fee"));
                                        history.setCurrnecy_unit(jsonObject.getString("currency"));
                                        history.setDistance_unit(jsonObject.optString("distance_unit"));

                                        /*--- History New ---*/
                                        if(jsonObject.has("total_extra_value") ||jsonObject.has("unit_price")
                                                ||jsonObject.has("km")||jsonObject.has("payment_mode")){

                                            history.setExtra_charges_his(jsonObject.getString("total_extra_value"));
                                            history.setMoney_type_car(jsonObject.getString("unit_price"));
                                            history.setDistance_his(jsonObject.getDouble("km"));
                                            history.setPayment_mode_his(jsonObject.getString("payment_mode"));

                                        }


                                        if (jsonObject.has(Const.Params.LATER)) {

                                            history.setLater(jsonObject.getString(Const.Params.LATER));
                                        }

//                                        if (jsonObject.has(Const.Params.PLATE_NO)) {
//
//                                            history.setPlate_number(jsonObject.getString(Const.Params.PLATE_NO));
//                                        }
//
//                                        if (jsonObject.has(Const.Params.A_AND_E)) {
//
//                                            history.setA_and_e(jsonObject.getString(Const.Params.A_AND_E));
//                                        }
//
//                                        if (jsonObject.has(Const.Params.IMH)) {
//
//                                            history.setImh(jsonObject.getString(Const.Params.IMH));
//                                        }
//
//                                        if (jsonObject.has(Const.Params.FERRY_TERMINALS)) {
//
//                                            history.setFerry_terminals(jsonObject.getString(Const.Params.FERRY_TERMINALS));
//                                        }
//
//                                        if (jsonObject.has(Const.Params.STAIRCASE)) {
//
//                                            history.setStaircase(jsonObject.getString(Const.Params.STAIRCASE));
//                                        }
//
//                                        if (jsonObject.has(Const.Params.TARMAC)) {
//
//                                            history.setTarmac(jsonObject.getString(Const.Params.TARMAC));
//                                        }
//
//                                        if (jsonObject.has(Const.Params.WEIGHT)) {
//
//                                            history.setWeight(jsonObject.getString(Const.Params.WEIGHT));
//                                        }
//
//                                        if (jsonObject.has(Const.Params.HOUSE_UNIT)) {
//
//                                            history.setHouseUnit(jsonObject.getString(Const.Params.HOUSE_UNIT));
//                                        }
//
//                                        if (jsonObject.has(Const.Params.OXYGEN)) {
//
//                                            history.setOxygen_tank(jsonObject.getString(Const.Params.OXYGEN));
//                                        }
//
//                                        if (jsonObject.has(Const.Params.CASE_TYPE)) {
//
//                                            history.setPickup_type(jsonObject.getString(Const.Params.CASE_TYPE));
//                                        }

                                        historylst.add(history);
                                    }

                                    if (historylst != null) {
                                        historyAdapter = new HistoryAdapter(getActivity(), historylst);
                                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                                        ride_lv.setHasFixedSize(true);
                                        ride_lv.setItemViewCacheSize(5);
                                        ride_lv.setDrawingCacheEnabled(true);
                                        ride_lv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                                        ride_lv.setLayoutManager(mLayoutManager);
                                        ride_lv.setItemAnimator(new DefaultItemAnimator());
                                        ride_lv.setAdapter(historyAdapter);
                                        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getActivity(), getResources().getIdentifier("layout_animation_from_left", "anim", getActivity().getPackageName()));
                                        ride_lv.setLayoutAnimation(animation);
                                        historyAdapter.notifyDataSetChanged();
                                        ride_lv.scheduleLayoutAnimation();
                                    }

                                } else {

                                    Toast.makeText(activity, ""+activity.getResources().getString(R.string.no_history), Toast.LENGTH_SHORT).show();
                                    histroy_empty.setVisibility(View.VISIBLE);
                                }

                            } else {
                                histroy_progress_bar.setVisibility(View.GONE);
                                EbizworldUtils.appLogDebug("HaoLS", "Get history ride failded");
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            EbizworldUtils.appLogDebug("HaoLS", "Get history ride failed " + e.toString());
                            EbizworldUtils.removeProgressDialog();
                        }

                    } else {

                        EbizworldUtils.removeProgressDialog();
                    }
                    break;
            }
        }
    }

    /*@Override
    public void onBackPressed() {
        Intent i = new Intent(this,MainActivity.class);
        startActivity(i);
    }*/


    private void showDetailedHistroy(History history) {

        if (getActivity() != null) {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, MMM, dd, yyyy hh:mm a");
            SimpleDateFormat inputformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String hitory_Date = history.getHistory_date();
            try {

                Date date = inputformat.parse(hitory_Date);
                hitory_Date = simpleDateFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            final Dialog detailedBill = new Dialog(getActivity(), R.style.DialogSlideAnim_leftright_Fullscreen);
            detailedBill.requestWindowFeature(Window.FEATURE_NO_TITLE);
            detailedBill.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent_black)));
            detailedBill.setCancelable(true);

            detailedBill.setContentView(R.layout.dialog_history_detail);

            ImageView btn_close_history = (ImageView) detailedBill.findViewById(R.id.btn_close_history);
            ImageView iv_trip_map = (ImageView) detailedBill.findViewById(R.id.iv_trip_map);
            CircleImageView trip_driver_pic = (CircleImageView) detailedBill.findViewById(R.id.trip_driver_pic);
            TextView trip_driver_name = (TextView) detailedBill.findViewById(R.id.trip_driver_name);
            TextView trip_car_type = (TextView) detailedBill.findViewById(R.id.trip_car_type);
            TextView trip_taxi_type = (TextView) detailedBill.findViewById(R.id.trip_ambulance_type);
            TextView trip_source_address = (TextView) detailedBill.findViewById(R.id.trip_source_address);
            TextView trip_destination_address = (TextView) detailedBill.findViewById(R.id.trip_destination_address);
            TextView tv_total = (TextView) detailedBill.findViewById(R.id.tv_history_detail_total);
            TextView trip_date = (TextView) detailedBill.findViewById(R.id.trip_date);

            TextView txt_paymenttype_history_detail = detailedBill.findViewById(R.id.txt_paymenttype_history_detail);
            TextView txt_distance_history_detail = detailedBill.findViewById(R.id.txt_distance_history_detail);
            TextView txt_extracost_history_detail = detailedBill.findViewById(R.id.txt_extracost_history_detail);

             /*--- Payment Mode---*/
            if(history.getPayment_mode_his().equals("cod")){

                Log.d("DAT_HISTORY",history.getPayment_mode_his().toString());

                txt_paymenttype_history_detail.setText(activity.getResources().getString(R.string.txt_cash));

            }else {
                txt_paymenttype_history_detail.setText(activity.getResources().getString(R.string.txt_no_booking));

            }

            /*--- Distance ---*/
            DecimalFormat decimalFormat_distance = new DecimalFormat("0.0");

            txt_distance_history_detail.setText(decimalFormat_distance.format(history.getDistance_his()));


            /*--- Type car and Money  ---*/
            DecimalFormat decimalFormat_money_type_car = new DecimalFormat("0.00");
            Float money_type_car = Float.parseFloat(history.getMoney_type_car());
            trip_taxi_type.setText(history.getHistory_type()
                    +"("+history.getCurrnecy_unit()
                    +" "+decimalFormat_money_type_car.format(money_type_car)+")");


            /*--- Extra cost  ---*/
            DecimalFormat decimalFormat_extra = new DecimalFormat("0.00");

            float extracost_history = Float.parseFloat(history.getExtra_charges_his());

            txt_extracost_history_detail.setText(decimalFormat_extra.format(extracost_history));

            //TextView trip_amount = (TextView) detailedBill.findViewById(R.id.trip_amount);
//            TextView tv_history_detail_a_and_e_value = (TextView) detailedBill.findViewById(R.id.tv_history_detail_a_and_e_value);
//            TextView tv_history_detail_imh_value = (TextView) detailedBill.findViewById(R.id.tv_history_detail_imh_value);
//            TextView tv_history_detail_ferry_terminals_value = (TextView) detailedBill.findViewById(R.id.tv_history_detail_ferry_terminals_value);
//            TextView tv_history_detail_staircase_value = (TextView) detailedBill.findViewById(R.id.tv_history_detail_staircase_value);
//            TextView tv_history_detail_tarmac_value = (TextView) detailedBill.findViewById(R.id.tv_history_detail_tarmac_value);
//            TextView tv_history_detail_weight_value = (TextView) detailedBill.findViewById(R.id.tv_history_detail_weight_value);
//
//            TextView tv_history_detail_house_unit_value = (TextView) detailedBill.findViewById(R.id.tv_history_detail_house_unit_value);
//            TextView tv_history_detail_plate_number_value = (TextView) detailedBill.findViewById(R.id.tv_history_detail_plate_number_value);
//            TextView tv_history_detail_oxygen_tank_value = (TextView) detailedBill.findViewById(R.id.tv_history_detail_oxygen_tank_value);
//            TextView tv_history_detail_pickup_type_value = (TextView) detailedBill.findViewById(R.id.tv_history_detail_pickup_type_value);

//            tv_history_detail_ferry_terminals_value.setText(history.getCurrnecy_unit() + " " + history.getFerry_terminals());
//            tv_history_detail_staircase_value.setText(history.getCurrnecy_unit() + " " + history.getStaircase());
//            tv_history_detail_a_and_e_value.setText(history.getCurrnecy_unit() + " " + history.getA_and_e());
//            tv_history_detail_imh_value.setText(history.getCurrnecy_unit() + " " + history.getImh());
//            tv_history_detail_tarmac_value.setText(history.getCurrnecy_unit() + " " + history.getTarmac());
//            tv_history_detail_house_unit_value.setText(history.getHouseUnit());
//            tv_history_detail_plate_number_value.setText(history.getPlate_number());
//            tv_history_detail_weight_value.setText(history.getCurrnecy_unit() + " " + history.getWeight());
//            tv_history_detail_oxygen_tank_value.setText(history.getCurrnecy_unit() + " " + history.getOxygen_tank());
//            tv_history_detail_pickup_type_value.setText(history.getCurrnecy_unit() + " " + history.getPickup_type());

            float total_history = Float.parseFloat(history.getHistory_total());
            tv_total.setText(history.getCurrnecy_unit() + " " + decimalFormat_extra.format(total_history));

            Glide.with(this)
                    .load(history.getHistory_picture())
                    .apply(new RequestOptions().error(R.drawable.defult_user))
                    .into(trip_driver_pic);

            if(history.getProvider_name().equals("null")||history.getProvider_name().equals("")){

                trip_driver_name.setText(activity.getResources().getString(R.string.txt_no_booking));

            }else {

                trip_driver_name.setText(getResources().getString(R.string.txt_ride_with) + " " + history.getProvider_name());

            }

            trip_car_type.setText(history.getHistory_type() + " " + getResources().getString(R.string.reciept));
            trip_source_address.setText(history.getHistory_Sadd());
            trip_destination_address.setText(history.getHistory_Dadd());
           // trip_amount.setText(history.getCurrnecy_unit() + history.getHistory_total());
            trip_date.setText(hitory_Date);

            btn_close_history.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    detailedBill.dismiss();
                }
            });

            /*Picasso.get().load(history.getMap_image()).into(iv_trip_map);*/

            Glide.with(getActivity())
                    .load(history.getMap_image())
                    .apply(new RequestOptions().error(R.drawable.background_user_login))
                    .into(iv_trip_map);

            detailedBill.show();
        }

    }

//    private void showNursingHomeDetailedHistroy(History history) {
//
//        if (getActivity() != null) {
//
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, MMM, dd, yyyy hh:mm a");
//            SimpleDateFormat inputformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            String hitory_Date = history.getHistory_date();
//            try {
//
//                Date date = inputformat.parse(hitory_Date);
//                hitory_Date = simpleDateFormat.format(date);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
//            final Dialog detailedBill = new Dialog(getActivity(), R.style.DialogSlideAnim_leftright_Fullscreen);
//            detailedBill.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            detailedBill.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent_black)));
//            detailedBill.setCancelable(true);
//
//            detailedBill.setContentView(R.layout.dialog_history_detail);
//
//            ImageView btn_close_history = (ImageView) detailedBill.findViewById(R.id.btn_close_history);
//            ImageView iv_trip_map = (ImageView) detailedBill.findViewById(R.id.iv_trip_map);
//            CircleImageView trip_driver_pic = (CircleImageView) detailedBill.findViewById(R.id.trip_driver_pic);
//            TextView trip_driver_name = (TextView) detailedBill.findViewById(R.id.trip_driver_name);
//            TextView trip_car_type = (TextView) detailedBill.findViewById(R.id.trip_car_type);
//            TextView trip_taxi_type = (TextView) detailedBill.findViewById(R.id.trip_ambulance_type);
//            TextView trip_source_address = (TextView) detailedBill.findViewById(R.id.trip_source_address);
//            TextView trip_destination_address = (TextView) detailedBill.findViewById(R.id.trip_destination_address);
//            TextView tv_total = (TextView) detailedBill.findViewById(R.id.tv_history_detail_total);
//            TextView trip_date = (TextView) detailedBill.findViewById(R.id.trip_date);
//            TextView trip_amount = (TextView) detailedBill.findViewById(R.id.trip_amount);
//
//            TextView tv_history_detail_a_and_e_value = (TextView) detailedBill.findViewById(R.id.tv_history_detail_a_and_e_value);
//            TextView tv_history_detail_imh_value = (TextView) detailedBill.findViewById(R.id.tv_history_detail_imh_value);
//            TextView tv_history_detail_ferry_terminals_value = (TextView) detailedBill.findViewById(R.id.tv_history_detail_ferry_terminals_value);
//            TextView tv_history_detail_staircase_value = (TextView) detailedBill.findViewById(R.id.tv_history_detail_staircase_value);
//            TextView tv_history_detail_tarmac_value = (TextView) detailedBill.findViewById(R.id.tv_history_detail_tarmac_value);
//            TextView tv_history_detail_weight_value = (TextView) detailedBill.findViewById(R.id.tv_history_detail_weight_value);
//
//            LinearLayout history_detail_house_unit_group = (LinearLayout) detailedBill.findViewById(R.id.history_detail_house_unit_group);
//            TextView tv_history_detail_house_unit_value = (TextView) detailedBill.findViewById(R.id.tv_history_detail_house_unit_value);
//            TextView tv_history_detail_plate_number_value = (TextView) detailedBill.findViewById(R.id.tv_history_detail_plate_number_value);
//            TextView tv_history_detail_oxygen_tank_value = (TextView) detailedBill.findViewById(R.id.tv_history_detail_oxygen_tank_value);
//            TextView tv_history_detail_pickup_type_value = (TextView) detailedBill.findViewById(R.id.tv_history_detail_pickup_type_value);
//
//            if (history.getLater().length() > 0 && Integer.parseInt(history.getLater()) == 1) {
//
//                history_detail_house_unit_group.setVisibility(View.GONE);
//            } else {
//                history_detail_house_unit_group.setVisibility(View.VISIBLE);
//            }
//
//            tv_history_detail_ferry_terminals_value.setText(history.getCurrnecy_unit() + " " + history.getFerry_terminals());
//            tv_history_detail_staircase_value.setText(history.getCurrnecy_unit() + " " + history.getStaircase());
//            tv_history_detail_a_and_e_value.setText(history.getCurrnecy_unit() + " " + history.getA_and_e());
//            tv_history_detail_imh_value.setText(history.getCurrnecy_unit() + " " + history.getImh());
//            tv_history_detail_tarmac_value.setText(history.getCurrnecy_unit() + " " + history.getTarmac());
//            tv_history_detail_house_unit_value.setText(history.getHouseUnit());
//            tv_history_detail_plate_number_value.setText(history.getPlate_number());
//            tv_history_detail_weight_value.setText(history.getCurrnecy_unit() + " " + history.getWeight());
//            tv_history_detail_oxygen_tank_value.setText(history.getCurrnecy_unit() + " " + history.getOxygen_tank());
//            tv_history_detail_pickup_type_value.setText(history.getCurrnecy_unit() + " " + history.getPickup_type());
//            tv_total.setText(history.getCurrnecy_unit() + " " + history.getHistory_total());
//
//
//            /*Picasso.get().load(history.getHistory_picture()).error(R.drawable.defult_user).into(trip_driver_pic);*/
//
//            Glide.with(this)
//                    .load(history.getHistory_picture())
//                    .apply(new RequestOptions().error(R.drawable.defult_user))
//                    .into(trip_driver_pic);
//
//            trip_driver_name.setText(getResources().getString(R.string.txt_ride_with) + " " + history.getProvider_name());
//            trip_car_type.setText(history.getHistory_type() + " " + getResources().getString(R.string.reciept));
//            trip_taxi_type.setText(getResources().getString(R.string.ambulance_operator) + ": " + history.getHistory_type());
//            trip_source_address.setText(history.getHistory_Sadd());
//            trip_destination_address.setText(history.getHistory_Dadd());
//            trip_amount.setText(history.getCurrnecy_unit() + history.getHistory_total());
//            trip_date.setText(hitory_Date);
//
//            btn_close_history.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    detailedBill.dismiss();
//                }
//            });
//
//            /*Picasso.get().load(history.getMap_image()).into(iv_trip_map);*/
//
//            Glide.with(getActivity())
//                    .load(history.getMap_image())
//                    .apply(new RequestOptions().error(R.drawable.background_user_login))
//                    .into(iv_trip_map);
//
//            detailedBill.show();
//        }
//
//    }
//
//    private void showHospitalDetailedHistroy(History history) {
//
//        if (getActivity() != null) {
//
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, MMM, dd, yyyy hh:mm a");
//            SimpleDateFormat inputformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            String hitory_Date = history.getHistory_date();
//            try {
//
//                Date date = inputformat.parse(hitory_Date);
//                hitory_Date = simpleDateFormat.format(date);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
//            final Dialog detailedBill = new Dialog(getActivity(), R.style.DialogSlideAnim_leftright_Fullscreen);
//            detailedBill.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            detailedBill.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent_black)));
//            detailedBill.setCancelable(true);
//
//            detailedBill.setContentView(R.layout.dialog_history_detail);
//
//            ImageView btn_close_history = (ImageView) detailedBill.findViewById(R.id.btn_close_history);
//            ImageView iv_trip_map = (ImageView) detailedBill.findViewById(R.id.iv_trip_map);
//            CircleImageView trip_driver_pic = (CircleImageView) detailedBill.findViewById(R.id.trip_driver_pic);
//            TextView trip_driver_name = (TextView) detailedBill.findViewById(R.id.trip_driver_name);
//            TextView trip_car_type = (TextView) detailedBill.findViewById(R.id.trip_car_type);
//            TextView trip_taxi_type = (TextView) detailedBill.findViewById(R.id.trip_ambulance_type);
//            TextView trip_source_address = (TextView) detailedBill.findViewById(R.id.trip_source_address);
//            TextView trip_destination_address = (TextView) detailedBill.findViewById(R.id.trip_destination_address);
//            TextView tv_total = (TextView) detailedBill.findViewById(R.id.tv_history_detail_total);
//            TextView trip_date = (TextView) detailedBill.findViewById(R.id.trip_date);
//            TextView trip_amount = (TextView) detailedBill.findViewById(R.id.trip_amount);
//
//            TextView tv_history_detail_a_and_e_value = (TextView) detailedBill.findViewById(R.id.tv_history_detail_a_and_e_value);
//            TextView tv_history_detail_imh_value = (TextView) detailedBill.findViewById(R.id.tv_history_detail_imh_value);
//            TextView tv_history_detail_ferry_terminals_value = (TextView) detailedBill.findViewById(R.id.tv_history_detail_ferry_terminals_value);
//            TextView tv_history_detail_staircase_value = (TextView) detailedBill.findViewById(R.id.tv_history_detail_staircase_value);
//            TextView tv_history_detail_tarmac_value = (TextView) detailedBill.findViewById(R.id.tv_history_detail_tarmac_value);
//            TextView tv_history_detail_weight_value = (TextView) detailedBill.findViewById(R.id.tv_history_detail_weight_value);
//
//            LinearLayout history_detail_house_unit_group = (LinearLayout) detailedBill.findViewById(R.id.history_detail_house_unit_group);
//            TextView tv_history_detail_house_unit_value = (TextView) detailedBill.findViewById(R.id.tv_history_detail_house_unit_value);
//            TextView tv_history_detail_oxygen_tank_value = (TextView) detailedBill.findViewById(R.id.tv_history_detail_oxygen_tank_value);
//            TextView tv_history_detail_pickup_type_value = (TextView) detailedBill.findViewById(R.id.tv_history_detail_pickup_type_value);
//
//            if (history.getLater().length() > 0 && Integer.parseInt(history.getLater()) == 1) {
//
//                history_detail_house_unit_group.setVisibility(View.GONE);
//            } else {
//                history_detail_house_unit_group.setVisibility(View.VISIBLE);
//            }
//
//            tv_history_detail_ferry_terminals_value.setText(history.getCurrnecy_unit() + " " + history.getFerry_terminals());
//            tv_history_detail_staircase_value.setText(history.getCurrnecy_unit() + " " + history.getStaircase());
//            tv_history_detail_a_and_e_value.setText(history.getCurrnecy_unit() + " " + history.getA_and_e());
//            tv_history_detail_imh_value.setText(history.getCurrnecy_unit() + " " + history.getImh());
//            tv_history_detail_tarmac_value.setText(history.getCurrnecy_unit() + " " + history.getTarmac());
//            tv_history_detail_house_unit_value.setText(history.getHouseUnit());
//            tv_history_detail_weight_value.setText(history.getCurrnecy_unit() + " " + history.getWeight());
//            tv_history_detail_oxygen_tank_value.setText(history.getCurrnecy_unit() + " " + history.getOxygen_tank());
//            tv_history_detail_pickup_type_value.setText(history.getCurrnecy_unit() + " " + history.getPickup_type());
//            tv_total.setText(history.getCurrnecy_unit() + " " + history.getHistory_total());
//
//
//            /*Picasso.get().load(history.getHistory_picture()).error(R.drawable.defult_user).into(trip_driver_pic);*/
//
//            Glide.with(this)
//                    .load(history.getHistory_picture())
//                    .apply(new RequestOptions().error(R.drawable.defult_user))
//                    .into(trip_driver_pic);
//
//            trip_driver_name.setText(getResources().getString(R.string.txt_ride_with) + " " + history.getProvider_name());
//            trip_car_type.setText(history.getHistory_type() + " " + getResources().getString(R.string.reciept));
//            trip_taxi_type.setText(getResources().getString(R.string.ambulance_operator) + ": " + history.getHistory_type());
//            trip_source_address.setText(history.getHistory_Sadd());
//            trip_destination_address.setText(history.getHistory_Dadd());
//            trip_amount.setText(history.getCurrnecy_unit() + history.getHistory_total());
//            trip_date.setText(hitory_Date);
//
//            btn_close_history.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    detailedBill.dismiss();
//                }
//            });
//
//            /*Picasso.get().load(history.getMap_image()).into(iv_trip_map);*/
//
//            Glide.with(getActivity())
//                    .load(history.getMap_image())
//                    .apply(new RequestOptions().error(R.drawable.background_user_login))
//                    .into(iv_trip_map);
//
//            detailedBill.show();
//        }
//
//    }

    @Override
    public void onRefresh() {

        mSwipeRefreshLayout.setRefreshing(true);
        switch (new PreferenceHelper(activity).getLoginType()) {

            case Const.PatientService.PATIENT: {

                if(month_history>0){

                    GetHistory(month_history);

                }else {

                    GetHistory(0);
                }


               // new HistoryListAPI(activity, this).getPatientHistory(Const.ServiceCode.GET_HISTORY);

            }
            break;

            case Const.NursingHomeService.NURSING_HOME: {

                new HistoryListAPI(activity, this).getNursingHomeHistory(Const.ServiceCode.GET_HISTORY);

            }
            break;

            case Const.HospitalService.HOSPITAL: {

                new HistoryListAPI(activity, this).getHospitalHistory(Const.ServiceCode.GET_HISTORY);

            }
            break;
        }

    }
}
