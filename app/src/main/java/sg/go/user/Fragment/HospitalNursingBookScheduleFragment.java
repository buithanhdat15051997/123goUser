package sg.go.user.Fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.zyyoona7.wheel.WheelView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import sg.go.user.Adapter.AmbulanceOperatorHorizontalAdapter;
import sg.go.user.Adapter.AmbulanceOperatorVerticalAdaper;
import sg.go.user.Adapter.NurseBookingScheduleAdapter;
import sg.go.user.Adapter.PlacesAutoCompleteAdapter;
import sg.go.user.Adapter.SimpleSpinnerAdapter;
import sg.go.user.HttpRequester.VolleyRequester;
import sg.go.user.Interface.AdapterCallback;
import sg.go.user.Interface.AsyncTaskCompleteListener;
import sg.go.user.MainActivity;
import sg.go.user.Models.AmbulanceOperator;
import sg.go.user.Models.DistanceMatrix;
import sg.go.user.Models.FareCaculation;
import sg.go.user.Models.Schedule;
import sg.go.user.R;
import sg.go.user.Utils.Commonutils;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.EbizworldUtils;
import sg.go.user.Utils.ItemClickSupport;
import sg.go.user.Utils.PreferenceHelper;

public class HospitalNursingBookScheduleFragment extends Fragment implements View.OnClickListener, AsyncTaskCompleteListener, AdapterCallback {

    @BindView(R.id.toolbar_add_schedule)
    Toolbar mToolbar_add_schedule;

    @BindView(R.id.add_schedule_back)
    ImageButton mAdd_schedule_back;

    @BindView(R.id.btn_add_schedule)
    Button mBtn_add_schedule;

    @BindView(R.id.list_schedule)
    RecyclerView mList_schedule;

    @BindView(R.id.tv_ambulance_operator_notice)
    TextView mTv_ambulance_operator_notice;

   /* @BindView(R.id.list_schedule_vehicle)
    RecyclerView mList_schedule_vehicle;*/

    @BindView(R.id.btn_register_schedule_submit)
    TextView mBtn_register_schedule_submit;

    @BindView(R.id.btn_another_ambulance_operator)
    Button mBtn_another_ambulance_operator;

    @BindView(R.id.any_ambulance_operator_group)
    LinearLayout mAny_ambulance_operator_group;

    @BindView(R.id.view_select)
    View mView_select;

    @BindView(R.id.wheel_view_ambulance_operator)
    WheelView wheel_view_ambulance_operator;

    private View mView;
    private MainActivity activity;
    private TextView tv_time_date_pickup;

    private AmbulanceOperatorHorizontalAdapter ambulanceOperatorHorizontalAdapter;
    private LatLng sch_pic_latLng, sch_drop_latLng;
    private boolean s_click = false, d_click = false;

    private DatePickerDialog dpd;
    private TimePickerDialog tpd;
    private String dateTime = "";
    private String pickupDateTime = "", datePickup = "", timePickup = "", timeSetPickup = "";
    private DistanceMatrix mDistanceMatrix;
    private ArrayList<AmbulanceOperator> mAmbulanceOperatorsMain, mAmbulanceOperatorsTemp;
    private AmbulanceOperator mAmbulanceOperator;
    private String ambulance_type;
    private List<Schedule> mScheduleList;
    private Schedule mSchedule;
    private NurseBookingScheduleAdapter mNurseBookingScheduleAdapter;
    private Calendar mCalendar;
    private boolean isExist = false;
    private boolean isAnyAmbulanceOperatorGroup = false;
    private List<String> ambulanceOperatorsWheelView;
    private Dialog dialogSchedule, dialogBillingInfo;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (MainActivity) getActivity();

        mAmbulanceOperatorsMain = new ArrayList<>();
        mAmbulanceOperatorsTemp = new ArrayList<>();

        mScheduleList = new ArrayList<>();

        new PreferenceHelper(getActivity()).putRequestType("0");
        new PreferenceHelper(getActivity()).putAmbulance_name(getResources().getString(R.string.any_ambulance));

        getTypes();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_nursing_book_schedule, container, false);
        ButterKnife.bind(this, mView);

        if (activity != null){

            activity.getSupportActionBar();
            activity.setSupportActionBar(mToolbar_add_schedule);

            ambulanceOperatorsWheelView = new ArrayList<>();
            ambulanceOperatorsWheelView.add(getResources().getString(R.string.any_ambulance));

            mTv_ambulance_operator_notice.setSelected(true);

            wheel_view_ambulance_operator.setTypeface(Typeface.SERIF);
            wheel_view_ambulance_operator.setOnWheelChangedListener(new WheelView.OnWheelChangedListener() {
                @Override
                public void onWheelScroll(int scrollOffsetY) {

                }

                @Override
                public void onWheelItemChanged(int oldPosition, int newPosition) {

                }

                @Override
                public void onWheelSelected(int position) {

                    for (int i = 0; i < mAmbulanceOperatorsMain.size(); i++){

                        if(mAmbulanceOperatorsMain.get(i).getAmbulanceOperator().toLowerCase().equals(ambulanceOperatorsWheelView.get(position).toLowerCase())){

                            mAmbulanceOperator = mAmbulanceOperatorsMain.get(i);
                            break;
                        }else {

                            mAmbulanceOperator = null;
                        }
                    }

                    if(mAmbulanceOperator != null){

                        EbizworldUtils.appLogDebug("HaoLS", "mAmbulanceOperator is " + mAmbulanceOperator.getAmbulanceOperator());

                        mTv_ambulance_operator_notice.setText(mAmbulanceOperator.getAmbulanceOperator().toUpperCase() + " " + getResources().getString(R.string.ambulance_operator_notice));
                        new PreferenceHelper(getActivity()).putRequestType(mAmbulanceOperator.getId());
                        new PreferenceHelper(getActivity()).putAmbulance_name(mAmbulanceOperator.getAmbulanceOperator());
                    }else {

                        EbizworldUtils.appLogDebug("HaoLS", "mAmbulanceOperator is null");

                        mTv_ambulance_operator_notice.setText(getResources().getString(R.string.any_ambulance) + " " + getResources().getString(R.string.ambulance_operator_notice));
                        isAnyAmbulanceOperatorGroup = true;
                        new PreferenceHelper(getActivity()).putRequestType("0");
                        new PreferenceHelper(getActivity()).putAmbulance_name(getResources().getString(R.string.any_ambulance));
                    }

                }

                @Override
                public void onWheelScrollStateChanged(int state) {

                }
            });

//        Setup RecyclerView of Operator list
            /*RecyclerView.LayoutManager layoutManagerListScheduleVehicle = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
            mList_schedule_vehicle.setLayoutManager(layoutManagerListScheduleVehicle);
            mList_schedule_vehicle.setHasFixedSize(true);
            mList_schedule_vehicle.setItemAnimator(new DefaultItemAnimator());

            mTv_ambulance_operator_notice.setSelected(true);*/

//        Setup RecyclerView of Schedule list
            RecyclerView.LayoutManager layoutManagerListSchedule = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
            mList_schedule.setLayoutManager(layoutManagerListSchedule);
            mList_schedule.setHasFixedSize(true);
            mList_schedule.setItemAnimator(new DefaultItemAnimator());
            mNurseBookingScheduleAdapter = new NurseBookingScheduleAdapter(activity, mScheduleList);
            mList_schedule.setAdapter(mNurseBookingScheduleAdapter);

//        itemClick for RecyclerView
            /*ItemClickSupport.addTo(mList_schedule_vehicle)
                    .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                        @Override
                        public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                            // do it
                            ambulance_type = mAmbulanceOperatorsMain.get(position).getId();

                            mAmbulanceOperator = new AmbulanceOperator();
                            mAmbulanceOperator = mAmbulanceOperatorsTemp.get(position);

                            mTv_ambulance_operator_notice.setText(mAmbulanceOperator.getAmbulanceOperator().toUpperCase() + " " + getResources().getString(R.string.ambulance_operator_notice));

                            ambulanceOperatorHorizontalAdapter.OnItemClicked(position);
                            ambulanceOperatorHorizontalAdapter.notifyDataSetChanged();

                            isAnyAmbulanceOperatorGroup = false;
                            mView_select.setVisibility(View.INVISIBLE);

                        }
                    });*/

            mAdd_schedule_back.setOnClickListener(this);
            mBtn_add_schedule.setOnClickListener(this);
            mBtn_register_schedule_submit.setOnClickListener(this);

            /*mBtn_another_ambulance_operator.setOnClickListener(this);
            mAny_ambulance_operator_group.setOnClickListener(this);*/
        }

        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
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
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.any_ambulance_operator_group:{

                isAnyAmbulanceOperatorGroup = true;
                mView_select.setVisibility(View.VISIBLE);
                mTv_ambulance_operator_notice.setText(getResources().getString(R.string.any_ambulance) + " " + getResources().getString(R.string.ambulance_operator_notice));

                if (ambulanceOperatorHorizontalAdapter != null){

                    ambulanceOperatorHorizontalAdapter.OnItemClicked(-1); //For view selected hide
                    ambulanceOperatorHorizontalAdapter.notifyDataSetChanged();
                }

            }
            break;

            case R.id.add_schedule_back:{

                if (activity != null){

                    activity.onBackPressed();

                }
                mScheduleList.clear();

            }
            break;

            case R.id.btn_another_ambulance_operator:{

                showAmbulanceOperator(mAmbulanceOperatorsMain);
            }
            break;

            case R.id.btn_add_schedule:{

                showSchedule();

            }
            break;

            case R.id.btn_register_schedule_submit:{

                JSONObject jsonObject = null;
                JSONArray jsonArray = new JSONArray();

                for (int i = 0; i < mScheduleList.size(); i++){

                    jsonObject = new JSONObject();

                    try {

                        jsonObject.put(Const.Params.S_ADDRESS, mScheduleList.get(i).getS_address());
                        jsonObject.put(Const.Params.S_LATITUDE, mScheduleList.get(i).getS_lat());
                        jsonObject.put(Const.Params.S_LONGITUDE, mScheduleList.get(i).getS_lng());
                        jsonObject.put(Const.Params.D_ADDRESS, mScheduleList.get(i).getD_address());
                        jsonObject.put(Const.Params.D_LATITUDE, mScheduleList.get(i).getD_lat());
                        jsonObject.put(Const.Params.D_LONGITUDE, mScheduleList.get(i).getD_lng());
                        /*jsonObject.put(Const.Params.SCHEDULE_REQUEST_TIME, mScheduleList.get(i).getRequest_date());*/
                        jsonObject.put(Const.Params.SCHEDULE_REQUEST_TIME, dateTime);

                        jsonObject.put(Const.Params.SCHEDULE_PATIENT_NAME, mScheduleList.get(i).getPatient_name());
                        jsonObject.put(Const.Params.SCHEDULE_PURPOSE, mScheduleList.get(i).getPurpose());
                        jsonObject.put(Const.Params.SCHEDULE_SPECIAL_REQUEST, mScheduleList.get(i).getSpecial_request());
                        jsonObject.put(Const.Params.SCHEDULE_ROOM_NUMBER, mScheduleList.get(i).getRoom_number());
                        jsonObject.put(Const.Params.SCHEDULE_FLOOR_NUMBER, mScheduleList.get(i).getFloor_number());
                        jsonObject.put(Const.Params.SCHEDULE_BED_NUMBER, mScheduleList.get(i).getBed_number());
                        jsonObject.put(Const.Params.SCHEDULE_WARD_NUMBER, mScheduleList.get(i).getWard());
                        jsonObject.put(Const.Params.SCHEDULE_HOSPITAL, mScheduleList.get(i).getHospital());
                        jsonObject.put(Const.Params.A_AND_E, mScheduleList.get(i).getA_and_e());
                        jsonObject.put(Const.Params.IMH, mScheduleList.get(i).getImh());
                        jsonObject.put(Const.Params.FERRY_TERMINALS, mScheduleList.get(i).getFerry_terminals());
                        jsonObject.put(Const.Params.STAIRCASE, mScheduleList.get(i).getStaircase());
                        jsonObject.put(Const.Params.TARMAC, mScheduleList.get(i).getTarmac());
                        jsonObject.put(Const.Params.WEIGHT, mScheduleList.get(i).getWeight());
                        jsonObject.put(Const.Params.FAMILY_MEMBER, mScheduleList.get(i).getFamilyMember());
                        jsonObject.put(Const.Params.OXYGEN, mScheduleList.get(i).getOxygen_tank());
                        jsonObject.put(Const.Params.CASE_TYPE, mScheduleList.get(i).getCase_type());

                    } catch (JSONException e) {
                        e.printStackTrace();
                        EbizworldUtils.appLogError("HaoLS", "JSON failed: " + e.toString());
                    }

                    jsonArray.put(jsonObject);

                }

                EbizworldUtils.appLogDebug("HaoLS", jsonArray.toString());

                if (mAmbulanceOperatorsMain.size() > 0 && jsonArray.length() > 0 && activity != null){

                    if (!EbizworldUtils.isNetworkAvailable(activity)){

                        EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
                        return;

                    }

                    HashMap<String, String> hashMap = new HashMap<>();

                    if (new PreferenceHelper(activity).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)){

                        hashMap.put(Const.Params.URL, Const.NursingHomeService.REGISTER_SCHEDULE_URL);

                    }else if (new PreferenceHelper(activity).getLoginType().equals(Const.HospitalService.HOSPITAL)){

                        hashMap.put(Const.Params.URL, Const.HospitalService.REGISTER_SCHEDULE_URL);

                    }

                    hashMap.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
                    hashMap.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());

                    hashMap.put(Const.Params.OPERATOR_ID, new PreferenceHelper(activity).getRequestType());
                    hashMap.put(Const.Params.ARR_REQUEST, jsonArray.toString());

                    EbizworldUtils.appLogDebug("HaoLS", "Registering schedule " + hashMap.toString());

                    new VolleyRequester(activity, Const.POST, hashMap, Const.ServiceCode.NURSE_HOSPITAL_REGISTER_SCHEDULE, this);

                    EbizworldUtils.showSimpleProgressDialog(activity, "Processing", false);

                }else {

                    EbizworldUtils.showShortToast("Please make your schedule trip first", activity);
                }

            }
            break;
        }

    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {

        switch (serviceCode){

            case  Const.ServiceCode.BILLING_INFO:{
                EbizworldUtils.appLogInfo("HaoLS", "Hospital/Nursing home's schedule billing info: " + response);

                if (response != null){

                    try {

                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getString("success").equals("true")){

                            JSONObject object = jsonObject.getJSONObject(Const.Params.BILLING_INFO);

                            String staircase = object.getString(Const.Params.STAIRCASE);
                            String weight = object.getString(Const.Params.WEIGHT);
                            String tarmac = object.getString(Const.Params.TARMAC);
                            String oxygen = object.getString(Const.Params.OXYGEN);
                            String caseType = object.getString(Const.Params.CASE_TYPE);
                            String total = object.getString(Const.Params.TOTAL);
                            String currency = object.getString("currency");

                            showDialogBillingInfo(staircase, tarmac, weight, oxygen, caseType, total, currency);
                        }

                    } catch (JSONException e) {

                        e.printStackTrace();
                        EbizworldUtils.appLogError("HaoLS", "Hospital/Nursing home's schedule billing info failed: " + e.toString());

                    }

                }
            }
            break;

            case Const.ServiceCode.NURSE_HOSPITAL_REGISTER_SCHEDULE:{
                EbizworldUtils.removeProgressDialog();
                if (response != null){

                    if (activity != null){

                        try{

                            JSONObject jsonObject = new JSONObject(response);

                            if (jsonObject.getString("success").equals("true")){

                                EbizworldUtils.showShortToast(activity.getResources().getString(R.string.txt_trip_schedule_success), activity);
                                activity.onBackPressed();

                            }else if (jsonObject.getString("success").equals("false")){

                                EbizworldUtils.showShortToast(activity.getResources().getString(R.string.txt_trip_schedule_fail), activity);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            EbizworldUtils.appLogError("HaoLS", "Nurse/Hospital register schedule failed " + e.toString());
                            EbizworldUtils.showShortToast(activity.getResources().getString(R.string.txt_trip_schedule_fail), activity);
                        }

                    }
                }
            }
            break;

            case Const.ServiceCode.LOCATION_API_BASE_SOURCE:{

                if (null != response) {
                    try {
                        JSONObject job = new JSONObject(response);
                        JSONArray jarray = job.optJSONArray("results");
                        JSONObject locObj = jarray.getJSONObject(0);
                        JSONObject geometryOBJ = locObj.optJSONObject("geometry");
                        JSONObject locationOBJ = geometryOBJ.optJSONObject("location");
                        double lat = locationOBJ.getDouble("lat");
                        double lng = locationOBJ.getDouble("lng");
                        sch_pic_latLng = new LatLng(lat, lng);

                        if (mSchedule != null){

                            mSchedule.setS_lat(String.valueOf(lat));
                            mSchedule.setS_lng(String.valueOf(lng));

                            EbizworldUtils.appLogDebug("HaoLS", mSchedule.getS_lat());
                            EbizworldUtils.appLogDebug("HaoLS", mSchedule.getS_lng());

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
            break;
            case Const.ServiceCode.LOCATION_API_BASE_DESTINATION:{

                if (activity != null){

                    if (null != response) {
                        try {
                            JSONObject job = new JSONObject(response);
                            JSONArray jarray = job.optJSONArray("results");
                            JSONObject locObj = jarray.getJSONObject(0);
                            JSONObject geometryOBJ = locObj.optJSONObject("geometry");
                            JSONObject locationOBJ = geometryOBJ.optJSONObject("location");
                            double lat = locationOBJ.getDouble("lat");
                            double lng = locationOBJ.getDouble("lng");
                            sch_drop_latLng = new LatLng(lat, lng);

                            if (mSchedule != null){

                                mSchedule.setD_lat(String.valueOf(lat));
                                mSchedule.setD_lng(String.valueOf(lng));

                                EbizworldUtils.appLogDebug("HaoLS", mSchedule.getD_lat());
                                EbizworldUtils.appLogDebug("HaoLS", mSchedule.getD_lng());

                            }

                            EbizworldUtils.hideKeyBoard(activity);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
            break;

            case Const.ServiceCode.AMBULANCE_OPERATOR:{

                if (activity != null){

                    if (response != null) {

                        EbizworldUtils.appLogInfo("HaoLS", "ambulance operator: " + response);
                        EbizworldUtils.appLogInfo("HaoLS", "Get ambulance operators succeeded");

                        try {

                            JSONObject job = new JSONObject(response);

                            if (job.getString("success").equals("true")) {

                                EbizworldUtils.appLogDebug("HaoLS", "Get ambulance operators succeeded " + response);

                                mAmbulanceOperatorsMain.clear();
                                /*JSONArray jarray = job.getJSONArray("services");*/
                                JSONArray jarray = job.getJSONArray("operator");

                                if (jarray.length() > 0) {

                                    for (int i = 0; i < jarray.length(); i++) {
                                        JSONObject taxiobj = jarray.getJSONObject(i);
                                        AmbulanceOperator ambulanceOperator = new AmbulanceOperator();
                                        ambulanceOperator.setCurrencey_unit(job.optString("currency"));
                                        ambulanceOperator.setId(taxiobj.getString("id"));
                                        ambulanceOperator.setAmbulanceCost(taxiobj.getString("estimated_fare"));
                                        ambulanceOperator.setAmbulanceImage(taxiobj.getString("picture"));
                                        ambulanceOperator.setAmbulanceOperator(taxiobj.getString("name"));
                                        ambulanceOperator.setAmbulance_price_min(taxiobj.getString("price_per_min"));
                                        ambulanceOperator.setAmbulance_price_distance(taxiobj.getString("price_per_unit_distance"));
                                        ambulanceOperator.setAmbulanceSeats(taxiobj.getString("number_seat"));
                                        ambulanceOperator.setBasefare(taxiobj.optString("min_fare"));
                                        mAmbulanceOperatorsMain.add(ambulanceOperator);
                                    }


                                }

                                if (mAmbulanceOperatorsMain.size() > 0){

                                    if (mAmbulanceOperatorsMain.size() > 0){

                                        for (int i = 0; i < mAmbulanceOperatorsMain.size(); i++){

                                            ambulanceOperatorsWheelView.add(mAmbulanceOperatorsMain.get(i).getAmbulanceOperator());
                                        }

                                        wheel_view_ambulance_operator.setData(ambulanceOperatorsWheelView);
                                    }

                                    mAmbulanceOperator = new AmbulanceOperator();
                                    mAmbulanceOperator = mAmbulanceOperatorsMain.get(0);
                                    /*for (int i = 0; i < 3; i++){

                                        mAmbulanceOperatorsTemp.add(mAmbulanceOperatorsMain.get(i));

                                    }

                                    if (mAmbulanceOperatorsTemp.size() > 0){

                                        ambulanceOperatorHorizontalAdapter = new AmbulanceOperatorHorizontalAdapter(activity, mAmbulanceOperatorsTemp, this);
                                        mList_schedule_vehicle.setAdapter(ambulanceOperatorHorizontalAdapter);

                                        mAmbulanceOperator = new AmbulanceOperator();
                                        mAmbulanceOperator = mAmbulanceOperatorsTemp.get(0);

*//*                                        mTv_ambulance_operator_notice.setText(mAmbulanceOperator.getAmbulanceOperator().toUpperCase() + " " + getResources().getString(R.string.ambulance_operator_notice));*//*
                                    }*/
                                }

                            }else {

                                EbizworldUtils.appLogDebug("Amulance type error: ", job.getString("error"));
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }
            }
            break;

            case Const.ServiceCode.GOOGLE_MATRIX:{

                EbizworldUtils.appLogInfo("HaoLS", "Google matrix: " + response);

                if (response != null){

                    try {

                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getString("status").equals("OK")) {
                            JSONArray sourceArray = jsonObject.getJSONArray("origin_addresses");
                            String sourceObject = (String) sourceArray.get(0);

                            JSONArray destinationArray = jsonObject.getJSONArray("destination_addresses");
                            String destinationObject = (String) destinationArray.get(0);

                            JSONArray jsonArray = jsonObject.getJSONArray("rows");

                            JSONObject elementsObject = jsonArray.getJSONObject(0);

                            JSONArray elementsArray = elementsObject.getJSONArray("elements");

                            JSONObject distanceObject = elementsArray.getJSONObject(0);

                            JSONObject dObject = distanceObject.getJSONObject("distance");

                            String distance = dObject.getString("text");

                            JSONObject durationObject = distanceObject.getJSONObject("duration");

                            String duration = durationObject.getString("text");

                            String dis = dObject.getString("value");

                            String dur = durationObject.getString("value");

                            double trip_dis = Integer.valueOf(dis) * 0.001;

                            if (mSchedule != null){

                                mSchedule.setDistance(String.valueOf(trip_dis));
                                mSchedule.setDistance(dur);

                            }

//                            getFare(String.valueOf(trip_dis), dur, service_id);


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        EbizworldUtils.appLogError("HaoLS", "Google Matrix failed: " + e.toString());
                    }
                }

            }
            break;

            case Const.ServiceCode.FARE_CALCULATION:{

                EbizworldUtils.appLogInfo("HaoLS", "Fare Caculation: " + response);

                if (response != null){

                    try {

                        JSONObject job1 = new JSONObject(response);

                        if (job1.getString("success").equals("true")) {

                            FareCaculation fareCaculation = new FareCaculation();
                            fareCaculation.setEstimatedFare(job1.getString("estimated_fare"));
                            fareCaculation.setTaxPrice(job1.getString("tax_price"));
                            fareCaculation.setBasePrice(job1.optString("base_price"));
                            fareCaculation.setMinFare(job1.optString("min_fare"));
                            fareCaculation.setBookingFee(job1.optString("booking_fee"));
                            fareCaculation.setCurrency(job1.optString("currency"));
                            fareCaculation.setDistanceUnit(job1.optString("distance_unit"));

                        }

                    } catch (JSONException e) {

                        e.printStackTrace();
                        EbizworldUtils.appLogError("HaoLS", "Fare caculation failed: " + e.toString());

                    }
                }
            }
            break;
        }
    }

    @Override
    public void onMethodCallback(String id, String taxitype, String taxi_price_distance, String taxi_price_min, String taxiimage, String taxi_seats, String basefare) {

        ambulance_type = id;
        if (activity != null){

            if (null != sch_pic_latLng && null != sch_drop_latLng) {
                findDistanceAndTime(sch_pic_latLng, sch_drop_latLng);
                /*showfareestimate(taxitype, taxi_price_distance, taxi_price_min, taxiimage, taxi_seats, basefare);*/
            } else {
                EbizworldUtils.showShortToast(getResources().getString(R.string.txt_drop_pick_error), activity);
            }

        }

    }

    private void getTypes(/*String dis, String dur*/) {

        if (activity != null){

            if (!EbizworldUtils.isNetworkAvailable(activity)) {

                EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
                return;
            }
            HashMap<String, String> map = new HashMap<String, String>();
        /*map.put(Const.Params.URL, Const.ServiceType.AMBULANCE_OPERATOR);
        map.put(Const.Params.ID, new PreferenceHelper(getActivity()).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(getActivity()).getSessionToken());
        map.put(Const.Params.DISTANCE, dis);
        map.put(Const.Params.TIME, dur);*/

            if (new PreferenceHelper(activity).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)){

                map.put(Const.Params.URL, Const.NursingHomeService.OPERATORS_URL);

            }else if (new PreferenceHelper(activity).getLoginType().equals(Const.HospitalService.HOSPITAL)){

                map.put(Const.Params.URL, Const.HospitalService.OPERATORS_URL);
            }

            map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
            map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());

            Log.d("HaoLS", "Getting ambulance operator " + map.toString());
            new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.AMBULANCE_OPERATOR,
                    this);

        }
    }

    private void getLatLngFromAddress(String selectedSourcePlace) {

        if (activity != null){

            if (!EbizworldUtils.isNetworkAvailable(activity)) {

                EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
                return;
            }

            HashMap<String, String> map = new HashMap<>();
            map.put(Const.Params.URL, Const.LOCATION_API_BASE + selectedSourcePlace + "&key=" + Const.GOOGLE_API_KEY);

            Log.d("mahi", "map for s_loc" + map);
            new VolleyRequester(activity, Const.GET, map, Const.ServiceCode.LOCATION_API_BASE_SOURCE, this);

        }
    }

    private void getLocationforDest(String selectedDestPlace) {

        if (activity != null){

            if (!EbizworldUtils.isNetworkAvailable(activity)) {

                EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
                return;
            }

            HashMap<String, String> map = new HashMap<>();
            map.put(Const.Params.URL, Const.LOCATION_API_BASE + selectedDestPlace + "&key=" + Const.GOOGLE_API_KEY);
            Log.d("mahi", "map for d_loc" + map);
            new VolleyRequester(activity, Const.GET, map, Const.ServiceCode.LOCATION_API_BASE_DESTINATION, this);

        }
    }

    private void findDistanceAndTime(LatLng s_latlan, LatLng d_latlan) {

        if (activity != null){

            if (!EbizworldUtils.isNetworkAvailable(activity)) {

                EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
                return;
            }

            HashMap<String, String> map = new HashMap<>();
            map.put(Const.Params.URL, Const.GOOGLE_MATRIX_URL + Const.Params.ORIGINS + "="
                    + String.valueOf(s_latlan.latitude) + "," + String.valueOf(s_latlan.longitude) + "&" + Const.Params.DESTINATION + "="
                    + String.valueOf(d_latlan.latitude) + "," + String.valueOf(d_latlan.longitude) + "&" + Const.Params.MODE + "="
                    + "driving" + "&" + Const.Params.LANGUAGE + "="
                    + "en-EN" + "&" + "key=" + Const.GOOGLE_API_KEY + "&" + Const.Params.SENSOR + "="
                    + String.valueOf(false));

            EbizworldUtils.appLogDebug("HaoLS", "distance api: " + map);

            new VolleyRequester(activity, Const.GET, map, Const.ServiceCode.GOOGLE_MATRIX, this);

        }
    }

    private void getFare(String distance, String duration, String service_id) {
        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }
        HashMap<String, String> map = new HashMap<String, String>();


        if (new PreferenceHelper(activity).getLoginType().equals(Const.PatientService.PATIENT)){

            map.put(Const.Params.URL, Const.ServiceType.FARE_CALCULATION);

        }else if (new PreferenceHelper(activity).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)){

            map.put(Const.Params.URL, Const.NursingHomeService.FARE_CACULATION_URL);

        }else if (new PreferenceHelper(activity).getLoginType().equals(Const.HospitalService.HOSPITAL)){

            map.put(Const.Params.URL, Const.HospitalService.FARE_CACULATION_URL);

        }
        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());
        map.put(Const.Params.DISTANCE, distance);
        map.put(Const.Params.TIME, duration);
        map.put(Const.Params.AMBULANCE_TYPE, service_id);

        EbizworldUtils.appLogDebug("HaoLS", "Get fare: " + map.toString());
        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.FARE_CALCULATION, this);
    }

//    Show dialog AmbulanceOperator
    private void showAmbulanceOperator(final List<AmbulanceOperator> ambulanceOperators){

        if (activity != null){

            final Dialog dialog = new Dialog(activity, R.style.DialogSlideAnim_leftright_Fullscreen);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(true);

            dialog.setContentView(R.layout.dialog_ambulance_operator_vertical);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
            final RecyclerView ambulanceOperatorList = (RecyclerView) dialog.findViewById(R.id.list_schedule_vehicle);
            ambulanceOperatorList.setLayoutManager(linearLayoutManager);
            ambulanceOperatorList.setHasFixedSize(true);
            ambulanceOperatorList.setItemAnimator(new DefaultItemAnimator());

            if (ambulanceOperators.size() > 0){

                final AmbulanceOperatorVerticalAdaper ambulanceOperatorVerticalAdapter = new AmbulanceOperatorVerticalAdaper(activity, ambulanceOperators);
                ambulanceOperatorList.setAdapter(ambulanceOperatorVerticalAdapter);

                ItemClickSupport.addTo(ambulanceOperatorList).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                        for (int i = 0; i < mAmbulanceOperatorsTemp.size(); i++){

                            if (mAmbulanceOperatorsTemp.get(i).equals(ambulanceOperators.get(position))){

                                isExist = true;
                                break;
                            }else {
                                isExist = false;
                            }
                        }

                        if (isExist == false){

                            if (mAmbulanceOperatorsTemp.size() == 4){

                                mAmbulanceOperatorsTemp.set(3,ambulanceOperators.get(position));

                            }else {

                                mAmbulanceOperatorsTemp.add(ambulanceOperators.get(position));

                            }

                        }
                        ambulanceOperatorVerticalAdapter.OnItemClicked(position);
                        ambulanceOperatorVerticalAdapter.notifyDataSetChanged();

                        ambulanceOperatorHorizontalAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
            }

            dialog.show();
        }
    }
    //    Show schedule method
    private void showSchedule() {

        if (activity != null){

            mSchedule = new Schedule();
            /*mSchedule.setRequest_id(ambulanceOperator.getId());
            mSchedule.setRequest_type(ambulanceOperator.getAmbulanceOperator());
            mSchedule.setRequest_pic(ambulanceOperator.getAmbulanceImage());

            EbizworldUtils.appLogDebug("HaoLS", mSchedule.getRequest_id());
            EbizworldUtils.appLogDebug("HaoLS", mSchedule.getRequest_type());
            EbizworldUtils.appLogDebug("HaoLS", mSchedule.getRequest_id());*/

            dialogSchedule = new Dialog(activity, R.style.DialogSlideAnim_leftright_Fullscreen);
            dialogSchedule.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogSchedule.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialogSchedule.setCancelable(true);

            dialogSchedule.setContentView(R.layout.dialog_nurse_hospital_schedule);
            tv_time_date_pickup = (TextView) dialogSchedule.findViewById(R.id.tv_pickup_time_date);
//            final Spinner spn_pickup_time_date = (Spinner) dialogSchedule.findViewById(R.id.spn_pickup_time_date);
            final TextView btn_sch_submit = (TextView) dialogSchedule.findViewById(R.id.btn_schedule_submit);
            final AutoCompleteTextView et_sch_source_address = (AutoCompleteTextView) dialogSchedule.findViewById(R.id.actv_sch_source_address);
            final AutoCompleteTextView et_sch_destination_address = (AutoCompleteTextView) dialogSchedule.findViewById(R.id.actv_sch_destination_address);
            final EditText edt_patient_name = (EditText) dialogSchedule.findViewById(R.id.edt_patient_name);
            final EditText edt_purpose = (EditText) dialogSchedule.findViewById(R.id.edt_purpose);
            final EditText edt_special_request = (EditText) dialogSchedule.findViewById(R.id.edt_special_request);
            final EditText edt_room_number = (EditText) dialogSchedule.findViewById(R.id.tv_room_number_value);
            final EditText edt_floor_number = (EditText) dialogSchedule.findViewById(R.id.tv_floor_number_value);
            final EditText edt_bed_number = (EditText) dialogSchedule.findViewById(R.id.tv_bed_number_value);
            final EditText edt_ward = (EditText) dialogSchedule.findViewById(R.id.tv_ward_value);
            final EditText edt_hospital = (EditText) dialogSchedule.findViewById(R.id.tv_hospital_value);
            final CheckBox cb_a_and_e = (CheckBox) dialogSchedule.findViewById(R.id.tv_a_and_e_value);
            final CheckBox cb_imh = (CheckBox) dialogSchedule.findViewById(R.id.tv_imh_value);
            final CheckBox cb_ferry_terminals = (CheckBox) dialogSchedule.findViewById(R.id.tv_ferry_terminals_value);
            final CheckBox cb_staircase = (CheckBox) dialogSchedule.findViewById(R.id.cb_staircase_value);
            final CheckBox cb_tarmac = (CheckBox) dialogSchedule.findViewById(R.id.tv_tarmac_value);
//            final Spinner spn_assistive_device = (Spinner) dialogSchedule.findViewById(R.id.tv_assistive_device_value);
            final Spinner spn_oxygen_tank = (Spinner) dialogSchedule.findViewById(R.id.spn_oxygen_tank_value);
            final Spinner spn_case_type = (Spinner) dialogSchedule.findViewById(R.id.spn_pickup_type_value);
            final Spinner spn_weight_value = (Spinner) dialogSchedule.findViewById(R.id.spn_weight_value);
            final Spinner spn_family_member_value = (Spinner) dialogSchedule.findViewById(R.id.spn_family_member_value);

//            Set up Pickup type adapter
            /*final String[] pickupType = {"1"};
            final List<String> pickup_types = new ArrayList<>();
            pickup_types.add("Normal pickup");
            pickup_types.add("Discharge from hospital");
            pickup_types.add("Medical appointment");
            pickup_types.add("Transfer");
            SimpleSpinnerAdapter pickupAdapter = new SimpleSpinnerAdapter(activity, pickup_types);
            spn_pickup_time_date.setAdapter(pickupAdapter);
            spn_pickup_time_date.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if (pickup_types.get(position).equals("Normal pickup")){
                        pickupType[0] = "1";
                    }else if (pickup_types.get(position).equals("Discharge from hospital")){
                        pickupType[0] = "2";
                    }else if (pickup_types.get(position).equals("Medical appointment")){
                        pickupType[0] = "3";
                    }else if (pickup_types.get(position).equals("Transfer")){
                        pickupType[0] = "4";
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });*/

            //Set up Family meber value
            final int[] familyMember = {0};
            List<String> familyMembers = new ArrayList<>();
            familyMembers.add("0");
            familyMembers.add("1");
            familyMembers.add("2");
            SimpleSpinnerAdapter familyMemberSimpleSpinnerAdapter = new SimpleSpinnerAdapter(activity, familyMembers);
            spn_family_member_value.setAdapter(familyMemberSimpleSpinnerAdapter);
            spn_family_member_value.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    switch (position){

                        case 1:{
                            familyMember[0] = 1;
                        }
                        break;

                        case 2:{
                            familyMember[0] = 2;
                        }
                        break;

                        default:{

                            familyMember[0] = 0;
                        }
                        break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            //Set up Weight
            final int[] weight = {0};
            List<String> weightList = new ArrayList<>();
            weightList.add(getResources().getString(R.string.weight_less_than_eighty));
            weightList.add(getResources().getString(R.string.weight_over_eighty));
            weightList.add(getResources().getString(R.string.weight_over_one_hundred));
            weightList.add(getResources().getString(R.string.weight_over_one_hundred_and_twenty));
            SimpleSpinnerAdapter simpleSpinnerAdapter = new SimpleSpinnerAdapter(activity, weightList);
            spn_weight_value.setAdapter(simpleSpinnerAdapter);
            spn_weight_value.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    switch (position){

                        case 0:{

                            weight[0] = 0;

                        }
                        break;

                        case 1:{

                            weight[0] = 1;
                        }
                        break;

                        case 2:{

                            weight[0] = 2;
                        }
                        break;

                        case 3:{

                            weight[0] = 3;
                        }
                        break;

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

//            Set up Assistive Device adapter
            /*final int[] assistiveDeviceValue = {1};
            List<String> assistiveDevices = new ArrayList<>();
            assistiveDevices.add("Wheelchair");
            assistiveDevices.add("Stretcher");
            SimpleSpinnerAdapter assistiveDeviceAdapter = new SimpleSpinnerAdapter(activity, assistiveDevices);
            spn_assistive_device.setAdapter(assistiveDeviceAdapter);
            spn_assistive_device.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if (position == 0){

                        assistiveDeviceValue[0] = 1;

                    }else if (position == 1){

                        assistiveDeviceValue[0] = 2;
                    }

                    EbizworldUtils.appLogDebug("HaoLS", "Assistive " + assistiveDeviceValue[0]);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });*/

//            Set up Oxygen Tank adapter
            final String[] oxygenTank = {"0"};
            final List<String> oxygenTanks = new ArrayList<>();
            oxygenTanks.add("0");
            oxygenTanks.add("2");
            oxygenTanks.add("3");
            oxygenTanks.add("4");
            oxygenTanks.add("5");
            SimpleSpinnerAdapter oxygenTankAdapter = new SimpleSpinnerAdapter(activity, oxygenTanks);
            spn_oxygen_tank.setAdapter(oxygenTankAdapter);
            spn_oxygen_tank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    oxygenTank[0] = oxygenTanks.get(position);
                    EbizworldUtils.appLogDebug("HaoLS", "Oxygen Tank " + oxygenTank[0]);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

//            Set up Case Type adapter
            final int[] caseType = {1};
            final List<String> caseTypes = new ArrayList<>();
            caseTypes.add(getResources().getString(R.string.medical_appointment));
            caseTypes.add(getResources().getString(R.string.ad_hoc));
            caseTypes.add(getResources().getString(R.string.airport_selectar));
            caseTypes.add(getResources().getString(R.string.airport_changi));
            caseTypes.add(getResources().getString(R.string.hospital_discharge));
            caseTypes.add(getResources().getString(R.string.a_and_e));
            caseTypes.add(getResources().getString(R.string.imh));
            caseTypes.add(getResources().getString(R.string.ferry_terminals));
            caseTypes.add(getResources().getString(R.string.airport_tarmac));

            SimpleSpinnerAdapter caseTypeAdapter = new SimpleSpinnerAdapter(activity, caseTypes);
            spn_case_type.setAdapter(caseTypeAdapter);
            spn_case_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    switch (position){

                        case 0:{

                            caseType[0] = 1;
                        }
                        break;

                        case 1:{

                            caseType[0] = 2;
                        }
                        break;

                        case 2:{

                            caseType[0] = 3;
                        }
                        break;

                        case 3:{
                            caseType[0] = 4;
                        }
                        break;

                        case 4:{
                            caseType[0] = 5;
                        }
                        break;

                        case 5:{
                            caseType[0] = 6;
                        }
                        break;

                        case 6:{
                            caseType[0] = 7;
                        }
                        break;

                        case 7:{
                            caseType[0] = 8;
                        }
                        break;

                        case 8:{
                            caseType[0] = 9;
                        }
                        break;
                    }

                    EbizworldUtils.appLogDebug("HaoLS", "Case type " + caseType[0]);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            EbizworldUtils.hideKeyBoard(activity);

            et_sch_source_address.setDropDownBackgroundDrawable(new ColorDrawable(Color.WHITE));
            et_sch_destination_address.setDropDownBackgroundDrawable(new ColorDrawable(Color.WHITE));

            /*try {

                getLatLngFromAddress(URLEncoder.encode(et_sch_source_address.getText().toString(), "utf-8"));

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }*/

            final PlacesAutoCompleteAdapter placesadapter = new PlacesAutoCompleteAdapter(activity,
                    R.layout.autocomplete_list_text);
            final PlacesAutoCompleteAdapter placesadapter2 = new PlacesAutoCompleteAdapter(activity,
                    R.layout.autocomplete_list_text);

            if (placesadapter != null) {
                et_sch_source_address.setAdapter(placesadapter);
            }

            if (placesadapter2 != null) {
                et_sch_destination_address.setAdapter(placesadapter2);
            }

            et_sch_source_address.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    et_sch_source_address.setSelection(0);
                    //sch_pic_latLng[0] = getLocationFromAddress(activity, et_sch_source_address.getText().toString());
                    EbizworldUtils.hideKeyBoard(activity);
                    final String selectedSourcePlace = placesadapter.getItem(i);

                    mSchedule.setS_address(selectedSourcePlace);
                    EbizworldUtils.appLogDebug("HaoLS", mSchedule.getS_address());

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {

                                getLatLngFromAddress(URLEncoder.encode(selectedSourcePlace, "utf-8"));

                            } catch (UnsupportedEncodingException e) {

                                e.printStackTrace();

                            }

                        }
                    }).start();

                }
            });

            et_sch_destination_address.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    et_sch_destination_address.setSelection(0);

                    // sch_drop_latLng[0] = getLocationFromAddress(activity, et_sch_destination_address.getText().toString());
                    EbizworldUtils.hideKeyBoard(activity);

                    final String selectedDestPlace = placesadapter2.getItem(i);

                    mSchedule.setD_address(selectedDestPlace);
                    EbizworldUtils.appLogDebug("HaoLS", mSchedule.getD_address());

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            try {

                                getLocationforDest(URLEncoder.encode(selectedDestPlace, "utf-8"));

                            } catch (UnsupportedEncodingException e) {

                                e.printStackTrace();

                            }

                        }
                    }).start();
                }
            });

            btn_sch_submit.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    if (tv_time_date_pickup.getText().toString().length() == 0) {

                        Commonutils.showtoast(getResources().getString(R.string.txt_error_date_time), activity);

                    } else if (et_sch_destination_address.getText().toString().length() == 0) {

                        Commonutils.showtoast(getResources().getString(R.string.txt_destination_error), activity);

                    } else if (et_sch_source_address.getText().toString().length() == 0) {

                        Commonutils.showtoast(getResources().getString(R.string.txt_pickup_error), activity);

                    }else if (TextUtils.isEmpty(edt_patient_name.getText().toString()) ||
                            TextUtils.isEmpty(edt_purpose.getText().toString()) ||
                            TextUtils.isEmpty(edt_special_request.getText().toString())) {

                        Commonutils.showtoast("patient name, purpose, trips, special request cannot empty", activity);

                    }else{

                            if (sch_pic_latLng != null && sch_drop_latLng != null){

                                findDistanceAndTime(sch_pic_latLng, sch_drop_latLng);

                            }

                            mSchedule.setPatient_name(edt_patient_name.getText().toString());
                            mSchedule.setPurpose(edt_purpose.getText().toString());
                            mSchedule.setSpecial_request(edt_special_request.getText().toString());
                            mSchedule.setRoom_number(Integer.parseInt(edt_room_number.getText().toString()));
                            mSchedule.setFloor_number(Integer.parseInt(edt_floor_number.getText().toString()));
                            mSchedule.setBed_number(Integer.parseInt(edt_bed_number.getText().toString()));
                            mSchedule.setWard(edt_ward.getText().toString());
                            mSchedule.setHospital(edt_hospital.getText().toString());

                        if (cb_a_and_e.isChecked()){

                            mSchedule.setA_and_e(1);

                        }else {

                            mSchedule.setA_and_e(0);
                        }

                        if (cb_imh.isChecked()){

                            mSchedule.setImh(1);

                        }else {

                            mSchedule.setImh(0);
                        }

                        if (cb_ferry_terminals.isChecked()){

                            mSchedule.setFerry_terminals(1);

                        }else {

                            mSchedule.setFerry_terminals(0);
                        }

                        if (cb_staircase.isChecked()){

                            mSchedule.setStaircase(1);
                        }else {

                            mSchedule.setStaircase(0);
                        }

                        if (cb_tarmac.isChecked()){

                            mSchedule.setTarmac(1);
                        }else {

                            mSchedule.setTarmac(0);
                        }

//                            mSchedule.setAssistive_device(assistiveDeviceValue[0]);
//                            mSchedule.setPickup_type(Integer.parseInt(pickupType[0]));

                            mSchedule.setOxygen_tank(Integer.valueOf(oxygenTank[0]));
                            mSchedule.setCase_type(caseType[0]);
                            mSchedule.setWeight(weight[0]);
                            mSchedule.setFamilyMember(familyMember[0]);

                            getBillingInfo(mSchedule);

                            EbizworldUtils.hideKeyBoard(activity);

                            dialogSchedule.dismiss();
                    }

                }

            });

            tv_time_date_pickup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatePicker();
                }
            });

            dialogSchedule.show();
        }

    }

    private void DatePicker() {

        if(activity != null){

            mCalendar = Calendar.getInstance();
            int mYear = mCalendar.get(Calendar.YEAR);
            int mMonth = mCalendar.get(Calendar.MONTH);
            int mDay = mCalendar.get(Calendar.DAY_OF_MONTH);

            dpd = new DatePickerDialog(activity, R.style.datepicker,
                    new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(android.widget.DatePicker view,
                                              int year, int monthOfYear, int dayOfMonth) {
                            // txtDate.setText(dayOfMonth + "-"
                            // + (monthOfYear + 1) + "-" + year);

                            if (view.isShown()) {
                                // Toast.makeText(
                                // getActivity(),
                                // dayOfMonth + "-" + (monthOfYear + 1) + "-"
                                // + year, Toast.LENGTH_LONG).show();

                                datePickup = Integer.toString(year) + "-"
                                        + Integer.toString(monthOfYear + 1) + "-"
                                        + Integer.toString(dayOfMonth);

                                pickupDateTime = datePickup;

                                EbizworldUtils.appLogDebug("HaoLS","Pickup date " + datePickup);
                                mCalendar.set(year, monthOfYear, dayOfMonth);

                                TimePicker();

                                dpd.dismiss();
                            }
                        }
                    }, mYear, mMonth, mDay);

            dpd.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.txt_cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dpd.dismiss();
                        }
                    });
            // dpd.getDatePicker().setMaxDate(addDays(new Date(),90).getTime());
            // dpd.getDatePicker().setMinDate(new Date().getTime());

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, 3);
            cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
            cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
            /*cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));*/
            cal.set(Calendar.SECOND, 0);
            /*cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));*/
            cal.set(Calendar.MILLISECOND, 0);
            dpd.getDatePicker().setMaxDate(cal.getTimeInMillis());

            cal.setTime(new Date());
            cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
            cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
            /*cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));*/
            cal.set(Calendar.SECOND, 0);
            /*cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));*/
            cal.set(Calendar.MILLISECOND, 0);
            dpd.getDatePicker().setMinDate(cal.getTimeInMillis());


            //Cancel already scheduled reminders

            dpd.show();

        }
    }

    public void TimePicker() {

        //Log.d("pavan", "in timePickup picker");

        /*mCalendar = Calendar.getInstance();*/


        if (activity != null){

            mCalendar.add(Calendar.MINUTE, 30);
            int mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
            int mMinute = mCalendar.get(Calendar.MINUTE);

            tpd = new TimePickerDialog(activity, R.style.datepicker, new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(android.widget.TimePicker view,
                                              int hourOfDay, int minute) {
                            // txtTime.setText(hourOfDay + ":" + minute);
                            if (view.isShown()) {
                                tpd.dismiss();
                                // isTimePickerOpen = false;
                                // call api here

                                int hour = hourOfDay;
                                int min = minute;

                                if (hourOfDay > 12){

                                    hour -= 12;
                                    timeSetPickup = "PM";
                                }else if (hourOfDay == 0){

                                    timeSetPickup = "AM";

                                }else if (hourOfDay == 12){

                                    timeSetPickup = "PM";
                                }else {

                                    timeSetPickup = "AM";

                                }

                                if (minute < 10){

                                    timePickup = String.valueOf(hourOfDay)+ ":0" +
                                            String.valueOf(min) + ":" + "00 " + timeSetPickup;

                                }else {

                                    timePickup = String.valueOf(hourOfDay)+ ":" +
                                            String.valueOf(min) + ":" + "00 " + timeSetPickup;

                                }

                                pickupDateTime = pickupDateTime.concat(" " + timePickup);

                                dateTime = datePickup + " "
                                        + Integer.toString(hourOfDay) + ":"
                                        + Integer.toString(minute) + ":" + "00";



                                tv_time_date_pickup.setText(pickupDateTime);

                                mSchedule.setRequest_date(pickupDateTime);

                                EbizworldUtils.appLogDebug("HaoLS", "Pickup time " + timePickup);
                                EbizworldUtils.appLogDebug("HaoLS", "Pickup date and time " + mSchedule.getRequest_date());

                                mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                mCalendar.set(Calendar.MINUTE, minute);

                                tpd.dismiss();
                            }
                        }
                    }, mHour, mMinute, false);

            tpd.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.txt_cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            tpd.dismiss();
                        }
                    });



            tpd.show();

        }

    }

    private void getBillingInfo(Schedule schedule){

        if (!EbizworldUtils.isNetworkAvailable(getActivity())) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        HashMap<String, String> hashMap = new HashMap<>();

        if (new PreferenceHelper(activity).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)){

            hashMap.put(Const.Params.URL, Const.NursingHomeService.BILLING_INFO);

        }else if (new PreferenceHelper(activity).getLoginType().equals(Const.HospitalService.HOSPITAL)){

            hashMap.put(Const.Params.URL, Const.HospitalService.BILLING_INFO);

        }

        hashMap.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        hashMap.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());
        hashMap.put(Const.Params.TIME, schedule.getRequest_date());
        hashMap.put(Const.Params.A_AND_E, String.valueOf(schedule.getA_and_e()));
        hashMap.put(Const.Params.IMH, String.valueOf(schedule.getImh()));
        hashMap.put(Const.Params.FERRY_TERMINALS, String.valueOf(schedule.getFerry_terminals()));
        hashMap.put(Const.Params.STAIRCASE, String.valueOf(schedule.getStaircase()));
        hashMap.put(Const.Params.TARMAC, String.valueOf(schedule.getTarmac()));
        hashMap.put(Const.Params.WEIGHT, String.valueOf(schedule.getWeight()));
        hashMap.put(Const.Params.OXYGEN, String.valueOf(schedule.getOxygen_tank()));
        hashMap.put(Const.Params.CASE_TYPE, String.valueOf(schedule.getCase_type()));

        EbizworldUtils.appLogDebug("HaoLS", "Get Billing info for Hospital/Nursing home's schedule: " + hashMap.toString());

        new VolleyRequester(activity, Const.POST, hashMap, Const.ServiceCode.BILLING_INFO, this);
    }

    private void showDialogBillingInfo(String staircase, String tarmac, String weight, String oxygen, String caseType, String total, String currency){

        dialogBillingInfo = new Dialog(activity, R.style.DialogSlideAnim_leftright_Fullscreen);
        dialogBillingInfo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogBillingInfo.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogBillingInfo.setCancelable(true);
        dialogBillingInfo.setContentView(R.layout.fragment_billing_info);

        final TextView tv_billing_info_notice = (TextView) dialogBillingInfo.findViewById(R.id.tv_billing_info_notice);
        final TextView tv_billing_info_total_price = (TextView) dialogBillingInfo.findViewById(R.id.tv_billing_info_total_price);
        final TextView tv_billing_info_staircase_value = (TextView) dialogBillingInfo.findViewById(R.id.tv_billing_info_staircase_value);
        final TextView tv_billing_info_tarmac_value = (TextView) dialogBillingInfo.findViewById(R.id.tv_billing_info_tarmac_value);
        final TextView tv_billing_info_weight_value = (TextView) dialogBillingInfo.findViewById(R.id.tv_billing_info_weight_value);
        final TextView tv_billing_info_oxygen_tank_value = (TextView) dialogBillingInfo.findViewById(R.id.tv_billing_info_oxygen_tank_value);
        final TextView tv_billing_info_pickup_type_value = (TextView) dialogBillingInfo.findViewById(R.id.tv_billing_info_pickup_type_value);
        final TextView tv_billing_info_confirm = (TextView) dialogBillingInfo.findViewById(R.id.tv_billing_info_confirm);
        final TextView tv_billing_info_deny = (TextView) dialogBillingInfo.findViewById(R.id.tv_billing_info_deny);
        final LinearLayout billing_info_payment_group = (LinearLayout) dialogBillingInfo.findViewById(R.id.billing_info_payment_group);

        billing_info_payment_group.setVisibility(View.GONE);
        tv_billing_info_notice.setVisibility(View.GONE);

        tv_billing_info_total_price.setText(currency + " " + total);
        tv_billing_info_staircase_value.setText(currency + " " + staircase);
        tv_billing_info_tarmac_value.setText(currency + " " + tarmac);
        tv_billing_info_weight_value.setText(currency + " " + weight);
        tv_billing_info_oxygen_tank_value.setText(currency + " " + oxygen);
        tv_billing_info_pickup_type_value.setText(currency + " " + caseType);

        tv_billing_info_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mScheduleList.add(mSchedule);
                mNurseBookingScheduleAdapter.notifyDataSetChanged();

                if (dialogSchedule != null && dialogSchedule.isShowing()){

                    dialogSchedule.dismiss();

                }

                if (dialogBillingInfo != null && dialogBillingInfo.isShowing()){

                    dialogBillingInfo.dismiss();

                }

            }
        });

        tv_billing_info_deny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogBillingInfo.dismiss();
            }
        });

        dialogBillingInfo.show();

    }
}
