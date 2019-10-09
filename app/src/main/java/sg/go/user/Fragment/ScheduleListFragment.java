package sg.go.user.Fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import sg.go.user.Adapter.ScheduleAdapter;
import sg.go.user.HttpRequester.VolleyRequester;
import sg.go.user.Interface.AsyncTaskCompleteListener;
import sg.go.user.MainActivity;
import sg.go.user.Models.RequestDetail;
import sg.go.user.Models.Schedule;
import sg.go.user.R;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.EbizworldUtils;
import sg.go.user.Utils.ItemClickSupport;
import sg.go.user.Utils.ParseContent;
import sg.go.user.Utils.PreferenceHelper;
import sg.go.user.WelcomeActivity;
import sg.go.user.restAPI.ScheduleListAPI;

/**
 * Created by user on 2/3/2017.
 */

public class ScheduleListFragment extends Fragment implements AsyncTaskCompleteListener, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener{


    private MainActivity mMainActivity;

    private Toolbar laterToolbar;
    private List<Schedule> mSchedules;
    private List<RequestDetail> mRequestDetails;
    private ScheduleAdapter scheduleAdapter;
    private RecyclerView later_lv;
    private ProgressBar later_progress_bar;
    private ImageButton later_back;
    private TextView later_empty;
    /*private ImageButton btn_schedule;*/
    private View mView;
    private SwipeRefreshLayout swipe_refresh_ride_list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMainActivity = (MainActivity) getActivity();
        mSchedules = new ArrayList<>();
        mRequestDetails = new ArrayList<>();

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                checkRequestStatus();

            }
        }, 3000);*/
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_schedule_list, container, false);

        laterToolbar = (Toolbar) mView.findViewById(R.id.toolbar_add_schedule);

        ((AppCompatActivity)getActivity()).setSupportActionBar(laterToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(null);

        later_lv = (RecyclerView) mView.findViewById(R.id.ride_lv);
        later_progress_bar = (ProgressBar) mView.findViewById(R.id.ride_progress_bar);
        later_back = (ImageButton) mView.findViewById(R.id.add_schedule_back);
        later_empty = (TextView) mView.findViewById(R.id.later_empty);
        /*btn_schedule = (ImageButton) mView.findViewById(R.id.btn_add_schedule);*/
        swipe_refresh_ride_list = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_refresh_ride_list);

        swipe_refresh_ride_list.setOnRefreshListener(this);
        swipe_refresh_ride_list.setColorSchemeColors(getResources().getColor(R.color.lightblueA700));




        if (new PreferenceHelper(getActivity()).getLoginType().equals(Const.PatientService.PATIENT)){ //Check Login type == Consumer

            /*btn_schedule.setVisibility(View.GONE);*/
            later_back.setOnClickListener(this);
            new ScheduleListAPI(mMainActivity, this).getPatientSchedule(Const.ServiceCode.GET_SCHEDULE);
            checkreqstatus();

            ItemClickSupport.addTo(later_lv).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                    Schedule schedule = mSchedules.get(position);
                    RequestDetail requestDetail = null;

                    for (int i = 0; i < mRequestDetails.size(); i++){

                        if (Integer.parseInt(schedule.getRequest_id()) == mRequestDetails.get(i).getRequestId()){

                            requestDetail = mRequestDetails.get(i);

                            EbizworldUtils.appLogDebug("HaoLS", String.valueOf(requestDetail.getRequestId()));

                            break;
                        }
                    }

                    if (schedule.getStatus_request().equals(Const.ScheduleStatus.FINISHED) && schedule.getStatus() == 4){

                        if (requestDetail != null){

                            Bundle bundle = new Bundle();

                            bundle.putSerializable(Const.REQUEST_DETAIL, requestDetail);
                            bundle.putInt(Const.DRIVER_STATUS, Const.IS_DRIVER_TRIP_ENDED);

                            if ( !mMainActivity.currentFragment.equals(Const.RATING_FRAGMENT) && !mMainActivity.isFinishing()){

                                RatingFragment ratingFragment = new RatingFragment();
                                ratingFragment.setArguments(bundle);

                                if (mMainActivity != null){

                                    mMainActivity.addFragment(ratingFragment, false, Const.RATING_FRAGMENT, true);

                                }
                            }

                        }

                    }else if (schedule.getStatus_request().equals(Const.ScheduleStatus.STARTING)){

                        if (requestDetail != null){

                            Bundle bundle = new Bundle();
                            TravelMapFragment travelMapFragment = new TravelMapFragment();

                            bundle.putSerializable(Const.REQUEST_DETAIL,
                                    requestDetail);
                            bundle.putInt(Const.DRIVER_STATUS,
                                    requestDetail.getTripStatus());

                            EbizworldUtils.appLogDebug("HaoLS", String.valueOf(requestDetail.getTripStatus()));

                            if (!mMainActivity.currentFragment.equals(Const.TRAVEL_MAP_FRAGMENT)) {

                                travelMapFragment.setArguments(bundle);
                                mMainActivity.addFragment(travelMapFragment, false, Const.TRAVEL_MAP_FRAGMENT,
                                        true);

                            }

                        }

                    }else {

                        showDetailPatientSchedule(schedule);

                    }
                }
            });

        }else if (new PreferenceHelper(getActivity()).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)){ //Check Login type == Nurse

            later_back.setVisibility(View.GONE);
            /*btn_schedule.setVisibility(View.VISIBLE);
            btn_schedule.setOnClickListener(this);*/
            new ScheduleListAPI(mMainActivity, this).getNursingHomeSchedule(Const.ServiceCode.GET_SCHEDULE);
            checkreqstatus();

            ItemClickSupport.addTo(later_lv).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                    Schedule schedule = mSchedules.get(position);
                    RequestDetail requestDetail = null;

                    for (int i = 0; i < mRequestDetails.size(); i++){

                        if (Integer.parseInt(schedule.getRequest_id()) == mRequestDetails.get(i).getRequestId()){

                            requestDetail = mRequestDetails.get(i);

                            EbizworldUtils.appLogDebug("HaoLS", String.valueOf(requestDetail.getRequestId()));

                            break;
                        }
                    }

                    if (schedule.getStatus_request().equals(Const.ScheduleStatus.FINISHED) && schedule.getStatus() == 4){

                        if (requestDetail != null){

                            Bundle bundle = new Bundle();

                            bundle.putSerializable(Const.REQUEST_DETAIL, requestDetail);
                            bundle.putInt(Const.DRIVER_STATUS, Const.IS_DRIVER_TRIP_ENDED);

                            if ( !mMainActivity.currentFragment.equals(Const.RATING_FRAGMENT) && !mMainActivity.isFinishing()){

                                RatingFragment ratingFragment = new RatingFragment();
                                ratingFragment.setArguments(bundle);

                                if (mMainActivity != null){

                                    mMainActivity.addFragment(ratingFragment, false, Const.RATING_FRAGMENT, true);

                                }
                            }

                        }

                    }else if (schedule.getStatus_request().equals(Const.ScheduleStatus.STARTING)){

                        if (requestDetail != null){

                            Bundle bundle = new Bundle();
                            TravelMapFragment travelMapFragment = new TravelMapFragment();

                            bundle.putSerializable(Const.REQUEST_DETAIL,
                                    requestDetail);
                            bundle.putInt(Const.DRIVER_STATUS,
                                    requestDetail.getTripStatus());

                            EbizworldUtils.appLogDebug("HaoLS", String.valueOf(requestDetail.getTripStatus()));

                            if (!mMainActivity.currentFragment.equals(Const.TRAVEL_MAP_FRAGMENT)) {

                                travelMapFragment.setArguments(bundle);
                                mMainActivity.addFragment(travelMapFragment, false, Const.TRAVEL_MAP_FRAGMENT,
                                        true);

                            }

                        }

                    }else {

                        showDetailNursingHomeAndHospitalSchedule(schedule);

                    }

                }
            });


        }else if (new PreferenceHelper(getActivity()).getLoginType().equals(Const.HospitalService.HOSPITAL)){ //Check Login type == Hospital

            later_back.setVisibility(View.GONE);
            /*btn_schedule.setVisibility(View.VISIBLE);
            btn_schedule.setOnClickListener(this);*/
            new ScheduleListAPI(mMainActivity, this).getHospitalSchedule(Const.ServiceCode.GET_SCHEDULE);
            checkreqstatus();


            ItemClickSupport.addTo(later_lv).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                    Schedule schedule = mSchedules.get(position);
                    RequestDetail requestDetail = null;

                    for (int i = 0; i < mRequestDetails.size(); i++){

                        if (Integer.parseInt(schedule.getRequest_id()) == mRequestDetails.get(i).getRequestId()){

                            requestDetail = mRequestDetails.get(i);

                            EbizworldUtils.appLogDebug("HaoLS", String.valueOf(requestDetail.getRequestId()));

                            break;
                        }
                    }

                    if (schedule.getStatus_request().equals(Const.ScheduleStatus.FINISHED) && schedule.getStatus() == 4){

                        if (requestDetail != null){

                            Bundle bundle = new Bundle();

                            bundle.putSerializable(Const.REQUEST_DETAIL, requestDetail);
                            bundle.putInt(Const.DRIVER_STATUS, Const.IS_DRIVER_TRIP_ENDED);

                            if ( !mMainActivity.currentFragment.equals(Const.RATING_FRAGMENT) && !mMainActivity.isFinishing()){

                                RatingFragment ratingFragment = new RatingFragment();
                                ratingFragment.setArguments(bundle);

                                if (mMainActivity != null){

                                    mMainActivity.addFragment(ratingFragment, false, Const.RATING_FRAGMENT, true);

                                }
                            }

                        }

                    }else if (schedule.getStatus_request().equals(Const.ScheduleStatus.STARTING)){

                        if (requestDetail != null){

                            Bundle bundle = new Bundle();
                            TravelMapFragment travelMapFragment = new TravelMapFragment();

                            bundle.putSerializable(Const.REQUEST_DETAIL,
                                    requestDetail);
                            bundle.putInt(Const.DRIVER_STATUS,
                                    requestDetail.getTripStatus());

                            EbizworldUtils.appLogDebug("HaoLS", String.valueOf(requestDetail.getTripStatus()));

                            if (!mMainActivity.currentFragment.equals(Const.TRAVEL_MAP_FRAGMENT)) {

                                travelMapFragment.setArguments(bundle);
                                mMainActivity.addFragment(travelMapFragment, false, Const.TRAVEL_MAP_FRAGMENT,
                                        true);

                            }

                        }

                    }else {

                        showDetailNursingHomeAndHospitalSchedule(schedule);

                    }

                }
            });

        }

        return  mView;
    }
    public void showAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
        builder.setMessage(getResources().getString(R.string.txt_no_later));
        builder.setCancelable(true);
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }


    private void checkreqstatus() {

        if (!EbizworldUtils.isNetworkAvailable(mMainActivity)) {
            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), mMainActivity);
            return;
        }

        HashMap<String, String> map = new HashMap<String, String>();

        if (new PreferenceHelper(mMainActivity).getLoginType().equals(Const.PatientService.PATIENT)){

            map.put(Const.Params.URL, Const.ServiceType.CHECKREQUEST_STATUS);

        }else if (new PreferenceHelper(getActivity()).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)){

            map.put(Const.Params.URL, Const.NursingHomeService.REQUEST_STATUS_CHECK_URL);

        }else if (new PreferenceHelper(getActivity()).getLoginType().equals(Const.HospitalService.HOSPITAL)){

            map.put(Const.Params.URL, Const.HospitalService.CHECK_REQUEST_STATUS_URL);

        }

        map.put(Const.Params.ID, new PreferenceHelper(mMainActivity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(mMainActivity).getSessionToken());


        Log.d("HaoLS", "CheckRequestStatus " + map.toString());
        new VolleyRequester(mMainActivity, Const.POST, map, Const.ServiceCode.CHECKREQUEST_STATUS,
                this);

    }

    public List<RequestDetail> parseRequestStatusList(String response) {

        if (TextUtils.isEmpty(response)) {
            return null;
        }

        List<RequestDetail> requestDetails = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString("success").equals("true")) {

                /*requestDetail.setCurrnecy_unit(jsonObject.optString("currency"));
                requestDetail.setCancellationFee(jsonObject.optString("cancellation_fine"));*/
                JSONArray jarray = jsonObject.getJSONArray("data");
                if (jarray.length() > 0) {

                    for (int i = 0; i < jarray.length(); i++){

                        RequestDetail requestDetail = new RequestDetail();

                        JSONObject tripData = jarray.getJSONObject(i);

                        if (tripData != null) {

                            if (tripData.has("provider_status")) {

                                requestDetail.setDriverStatus(tripData.getInt("provider_status"));

                            }

                            /*new PreferenceHelper(mMainActivity).putRequestId(Integer.valueOf(tripData.getString("request_id")));*/

                            requestDetail.setRequestId(Integer.valueOf(tripData.getString("request_id")));

                            if (!tripData.getString("provider_name").equals("null")) {

                                requestDetail.setDriver_name(tripData.getString("provider_name"));

                            }

                            if (tripData.has("status")) {

                                requestDetail.setTripStatus(tripData.getInt("status"));


                            }
                            if (!tripData.getString("provider_mobile").equals("null")) {

                                requestDetail.setDriver_mobile(tripData.getString("provider_mobile"));

                            }

                            if (!tripData.getString("provider_picture").equals("null")) {

                                requestDetail.setDriver_picture(tripData.getString("provider_picture"));

                            }

                            if (!tripData.optString("car_image").equals("null")) {

                                requestDetail.setDriver_car_picture(tripData.getString("car_image"));

                            }
                            if (!tripData.optString("model").equals("null")) {
                                requestDetail.setDriver_car_model(tripData
                                        .getString("model"));
                            }
                            if (!tripData.getString("color").equals("null")) {
                                requestDetail.setDriver_car_color(tripData
                                        .getString("color"));
                            }
                            if (!tripData.getString("plate_no").equals("null")) {
                                requestDetail.setDriver_car_number(tripData
                                        .getString("plate_no"));
                            }

                            requestDetail.setRequest_type(tripData.optString("request_status_type"));
                            requestDetail.setNo_tolls(jsonObject.optString("number_tolls"));
                            requestDetail.setDriver_id(tripData
                                    .getString("provider_id"));

                        /*new PreferenceHelper(mMainActivity).putDriver_id(tripData
                                .getString("provider_id"));*/

                            requestDetail.setDriver_rating(tripData
                                    .getString("rating"));

                            requestDetail.setS_address(tripData
                                    .getString("s_address"));

                            requestDetail.setD_address(tripData
                                    .getString("d_address"));

                            requestDetail.setS_lat(tripData
                                    .getString("s_latitude"));

                            requestDetail.setS_lng(tripData
                                    .getString("s_longitude"));

                            requestDetail.setD_lat(tripData
                                    .getString("d_latitude"));

                            requestDetail.setD_lng(tripData
                                    .getString("d_longitude"));



                            requestDetail.setAdStopLatitude(tripData.getString("adstop_latitude"));
                            requestDetail.setAdStopLongitude(tripData.getString("adstop_longitude"));
                            requestDetail.setAdStopAddress(tripData.getString("adstop_address"));
                            requestDetail.setIsAdStop(tripData.getString("is_adstop"));
                            requestDetail.setIsAddressChanged(tripData.getString("is_address_changed"));



                            if (!tripData.getString("driver_latitude").equals("null")) {
                                requestDetail.setDriver_latitude(Double.valueOf(tripData
                                        .getString("driver_latitude")));
                            }
                            if (!tripData.getString("driver_longitude").equals("null")) {
                                requestDetail.setDriver_longitude(Double.valueOf(tripData
                                        .getString("driver_longitude")));
                            }
                            requestDetail.setVehical_img(tripData
                                    .getString("type_picture"));

                        }

                        JSONArray invoicejarray = jsonObject.getJSONArray("invoice");

                        if (invoicejarray.length() > 0) {
                            JSONObject invoiceobj = invoicejarray.getJSONObject(0);
                            requestDetail.setTrip_time(invoiceobj.getString("total_time"));
                            requestDetail.setPayment_mode(invoiceobj.getString("payment_mode"));
                            requestDetail.setTrip_base_price(invoiceobj.getString("base_price"));
                            requestDetail.setTrip_total_price(invoiceobj.getString("total"));
                            requestDetail.setDistance_unit(invoiceobj.optString("distance_unit"));


                            requestDetail.setTrip_distance(invoiceobj
                                    .getString("distance_travel"));
                        }

                        requestDetails.add(requestDetail);
                        EbizworldUtils.appLogDebug("HaoLS ", "RequestDetail " +  requestDetails.get(i).getRequestId() + " " + requestDetails.get(i).getTripStatus());
                    }

                } else {


                    /*requestDetail.setTripStatus(Const.NO_REQUEST);*/

                    /*new PreferenceHelper(mMainActivity).putRequestId(Const.NO_REQUEST);*/

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return requestDetails;
    }

    private void showDetailPatientSchedule (final Schedule schedule){

        if (mMainActivity != null){

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, MMM, dd, yyyy hh:mm a");
            SimpleDateFormat inputformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String scheduleReq_date = schedule.getRequest_date();
            try {

                Date date = inputformat.parse(scheduleReq_date);
                scheduleReq_date = simpleDateFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            final Dialog scheduleDetail = new Dialog(mMainActivity, R.style.DialogSlideAnim_leftright_Fullscreen);
            scheduleDetail.requestWindowFeature(Window.FEATURE_NO_TITLE);
            scheduleDetail.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent_black)));
            scheduleDetail.setCancelable(true);
            scheduleDetail.setContentView(R.layout.dialog_patient_schedule_detail);

            ImageView btn_close_schedule = (ImageView) scheduleDetail.findViewById(R.id.btn_close_schedule);
            TextView trip_date = (TextView) scheduleDetail.findViewById(R.id.trip_date);
            TextView trip_status = (TextView) scheduleDetail.findViewById(R.id.trip_status);
            TextView trip_ambulance_operator = (TextView) scheduleDetail.findViewById(R.id.trip_ambulance_operator);
            TextView trip_source_address = (TextView) scheduleDetail.findViewById(R.id.trip_source_address);
            TextView trip_destination_address = (TextView) scheduleDetail.findViewById(R.id.trip_destination_address);
            final TextView tv_a_and_e = (TextView) scheduleDetail.findViewById(R.id.tv_a_and_e_value);
            final TextView tv_imh = (TextView) scheduleDetail.findViewById(R.id.tv_imh_value);
            final TextView tv_ferry_terminals = (TextView) scheduleDetail.findViewById(R.id.tv_ferry_terminals_value);
            final TextView tv_staircase = (TextView) scheduleDetail.findViewById(R.id.cb_staircase_value);
            final TextView tv_tarmac = (TextView) scheduleDetail.findViewById(R.id.tv_tarmac_value);
            final TextView tv_house_unit = (TextView) scheduleDetail.findViewById(R.id.tv_house_unit_value);
            final TextView tv_weight = (TextView) scheduleDetail.findViewById(R.id.tv_weight_value);
            final TextView tv_oxygen_tank = (TextView) scheduleDetail.findViewById(R.id.spn_oxygen_tank_value);
            final TextView tv_pickup_type = (TextView) scheduleDetail.findViewById(R.id.spn_pickup_type_value);

            btn_close_schedule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    scheduleDetail.dismiss();

                }
            });

            if (schedule !=null){

                trip_date.setText(scheduleReq_date);
                trip_status.setText(schedule.getStatus_request());
                trip_ambulance_operator.setText(schedule.getRequest_type());
                trip_source_address.setText(schedule.getS_address());
                trip_source_address.setSelected(true);

                trip_destination_address.setText(schedule.getD_address());
                trip_destination_address.setSelected(true);

                switch (schedule.getA_and_e()) {
                    case 1:
                        tv_a_and_e.setText(getResources().getString(R.string.txt_yes));
                        break;
                    case 0:
                        tv_a_and_e.setText(getResources().getString(R.string.txt_no));
                        break;
                }

                switch (schedule.getImh()) {
                    case 1:
                        tv_imh.setText(getResources().getString(R.string.txt_yes));
                        break;
                    case 0:
                        tv_imh.setText(getResources().getString(R.string.txt_no));
                        break;
                }

                switch (schedule.getFerry_terminals()) {
                    case 1:
                        tv_ferry_terminals.setText(getResources().getString(R.string.txt_yes));
                        break;
                    case 0:
                        tv_ferry_terminals.setText(getResources().getString(R.string.txt_no));
                        break;
                }

                switch (schedule.getStaircase()) {
                    case 1:
                        tv_staircase.setText(getResources().getString(R.string.txt_yes));
                        break;
                    case 0:
                        tv_staircase.setText(getResources().getString(R.string.txt_no));
                        break;
                }

                switch (schedule.getTarmac()) {
                    case 1:
                        tv_tarmac.setText(getResources().getString(R.string.txt_yes));
                        break;
                    case 0:
                        tv_tarmac.setText(getResources().getString(R.string.txt_no));
                        break;
                }

                tv_house_unit.setText(schedule.getHouseUnit());

                switch (schedule.getWeight()) {
                    case 0:
                        tv_weight.setText(getResources().getString(R.string.weight_less_than_eighty));
                        break;
                    case 1:
                        tv_weight.setText(getResources().getString(R.string.weight_over_eighty));
                        break;

                    case 2:
                        tv_weight.setText(getResources().getString(R.string.weight_over_one_hundred));
                        break;
                    case 3:
                        tv_weight.setText(getResources().getString(R.string.weight_over_one_hundred_and_twenty));
                        break;
                }

                switch (schedule.getOxygen_tank()) {

                    case 0:
                        tv_oxygen_tank.setText("0L");
                        break;

                    case 2:
                        tv_oxygen_tank.setText("2L");
                        break;
                    case 3:
                        tv_oxygen_tank.setText("3L");
                        break;
                    case 4:
                        tv_oxygen_tank.setText("4L");
                        break;
                    case 5:
                        tv_oxygen_tank.setText("5L");
                        break;
                }

                switch (schedule.getCase_type()) {
                    case 1:
                        tv_pickup_type.setText(getResources().getString(R.string.medical_appointment));
                        break;
                    case 2:
                        tv_pickup_type.setText(getResources().getString(R.string.ad_hoc));
                        break;
                    case 3:
                        tv_pickup_type.setText(getResources().getString(R.string.airport_selectar));
                        break;

                    case 4:
                        tv_pickup_type.setText(getResources().getString(R.string.airport_changi));
                        break;

                    case 5:
                        tv_pickup_type.setText(getResources().getString(R.string.hospital_discharge));
                        break;

                    case 6:
                        tv_pickup_type.setText(getResources().getString(R.string.a_and_e));
                        break;

                    case 7:
                        tv_pickup_type.setText(getResources().getString(R.string.imh));
                        break;

                    case 8:
                        tv_pickup_type.setText(getResources().getString(R.string.ferry_terminals));
                        break;

                    case 9:
                        tv_pickup_type.setText(getResources().getString(R.string.airport_tarmac));
                        break;
                }

                scheduleDetail.show();

            }
        }
    }

    private void showDetailNursingHomeAndHospitalSchedule(final Schedule schedule) {

        if (getActivity() != null){

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E, MMM, dd, yyyy hh:mm a");
            SimpleDateFormat inputformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String scheduleReq_date = schedule.getRequest_date();
            try {

                Date date = inputformat.parse(scheduleReq_date);
                scheduleReq_date = simpleDateFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            final Dialog scheduleDetail = new Dialog(getActivity(), R.style.DialogSlideAnim_leftright_Fullscreen);
            scheduleDetail.requestWindowFeature(Window.FEATURE_NO_TITLE);
            scheduleDetail.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent_black)));
            scheduleDetail.setCancelable(true);
            scheduleDetail.setContentView(R.layout.dialog_nurse_hospital_schedule_detail);

            ImageView btn_close_schedule = (ImageView) scheduleDetail.findViewById(R.id.btn_close_schedule);
            TextView trip_date = (TextView) scheduleDetail.findViewById(R.id.trip_date);
            TextView trip_status = (TextView) scheduleDetail.findViewById(R.id.trip_status);
            TextView trip_ambulance_operator = (TextView) scheduleDetail.findViewById(R.id.trip_ambulance_operator);
            TextView trip_source_address = (TextView) scheduleDetail.findViewById(R.id.trip_source_address);
            TextView trip_destination_address = (TextView) scheduleDetail.findViewById(R.id.trip_destination_address);

            final TextView tv_patient_name_value = (TextView) scheduleDetail.findViewById(R.id.tv_patient_name_value);
            final TextView tv_purpose_value = (TextView) scheduleDetail.findViewById(R.id.tv_purpose_value);
            final EditText edt_special_request = (EditText) scheduleDetail.findViewById(R.id.edt_special_request);
            final TextView tv_room_number_value = (TextView) scheduleDetail.findViewById(R.id.tv_room_number_value);
            final TextView tv_floor_number_value = (TextView) scheduleDetail.findViewById(R.id.tv_floor_number_value);
            final TextView tv_bed_number_value = (TextView) scheduleDetail.findViewById(R.id.tv_bed_number_value);
            final TextView tv_ward_value = (TextView) scheduleDetail.findViewById(R.id.tv_ward_value);
            final TextView tv_hospital_value = (TextView) scheduleDetail.findViewById(R.id.tv_hospital_value);
            final TextView tv_a_and_e = (TextView) scheduleDetail.findViewById(R.id.tv_a_and_e_value);
            final TextView tv_imh = (TextView) scheduleDetail.findViewById(R.id.tv_imh_value);
            final TextView tv_ferry_terminals = (TextView) scheduleDetail.findViewById(R.id.tv_ferry_terminals_value);
            final TextView tv_staircase = (TextView) scheduleDetail.findViewById(R.id.cb_staircase_value);
            final TextView tv_tarmac = (TextView) scheduleDetail.findViewById(R.id.tv_tarmac_value);
            final TextView tv_weight = (TextView) scheduleDetail.findViewById(R.id.tv_weight_value);
            final TextView tv_oxygen_tank = (TextView) scheduleDetail.findViewById(R.id.spn_oxygen_tank_value);
            final TextView tv_pickup_type = (TextView) scheduleDetail.findViewById(R.id.spn_pickup_type_value);


            btn_close_schedule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    scheduleDetail.dismiss();

                }
            });

            switch (schedule.getA_and_e()) {
                case 1:
                    tv_a_and_e.setText(getResources().getString(R.string.txt_yes));
                    break;
                case 0:
                    tv_a_and_e.setText(getResources().getString(R.string.txt_no));
                    break;
            }

            switch (schedule.getImh()) {
                case 1:
                    tv_imh.setText(getResources().getString(R.string.txt_yes));
                    break;
                case 0:
                    tv_imh.setText(getResources().getString(R.string.txt_no));
                    break;
            }

            switch (schedule.getFerry_terminals()) {
                case 1:
                    tv_ferry_terminals.setText(getResources().getString(R.string.txt_yes));
                    break;
                case 0:
                    tv_ferry_terminals.setText(getResources().getString(R.string.txt_no));
                    break;
            }

            switch (schedule.getStaircase()) {
                case 1:
                    tv_staircase.setText(getResources().getString(R.string.txt_yes));
                    break;
                case 0:
                    tv_staircase.setText(getResources().getString(R.string.txt_no));
                    break;
            }

            switch (schedule.getTarmac()) {
                case 1:
                    tv_tarmac.setText(getResources().getString(R.string.txt_yes));
                    break;
                case 0:
                    tv_tarmac.setText(getResources().getString(R.string.txt_no));
                    break;
            }

            switch (schedule.getWeight()) {
                case 0:
                    tv_weight.setText(getResources().getString(R.string.weight_less_than_eighty));
                    break;
                case 1:
                    tv_weight.setText(getResources().getString(R.string.weight_over_eighty));
                    break;

                case 2:
                    tv_weight.setText(getResources().getString(R.string.weight_over_one_hundred));
                    break;
                case 3:
                    tv_weight.setText(getResources().getString(R.string.weight_over_one_hundred_and_twenty));
                    break;
            }

            switch (schedule.getOxygen_tank()) {

                case 0:
                    tv_oxygen_tank.setText("0L");
                    break;

                case 2:
                    tv_oxygen_tank.setText("2L");
                    break;
                case 3:
                    tv_oxygen_tank.setText("3L");
                    break;
                case 4:
                    tv_oxygen_tank.setText("4L");
                    break;
                case 5:
                    tv_oxygen_tank.setText("5L");
                    break;
            }

            switch (schedule.getCase_type()) {
                case 1:
                    tv_pickup_type.setText(getResources().getString(R.string.medical_appointment));
                    break;
                case 2:
                    tv_pickup_type.setText(getResources().getString(R.string.ad_hoc));
                    break;
                case 3:
                    tv_pickup_type.setText(getResources().getString(R.string.airport_selectar));
                    break;

                case 4:
                    tv_pickup_type.setText(getResources().getString(R.string.airport_changi));
                    break;

                case 5:
                    tv_pickup_type.setText(getResources().getString(R.string.hospital_discharge));
                    break;

                case 6:
                    tv_pickup_type.setText(getResources().getString(R.string.a_and_e));
                    break;

                case 7:
                    tv_pickup_type.setText(getResources().getString(R.string.imh));
                    break;

                case 8:
                    tv_pickup_type.setText(getResources().getString(R.string.ferry_terminals));
                    break;

                case 9:
                    tv_pickup_type.setText(getResources().getString(R.string.airport_tarmac));
                    break;
            }

            trip_date.setText(scheduleReq_date);
            trip_status.setText(schedule.getStatus_request());
            trip_ambulance_operator.setText(schedule.getRequest_type());
            trip_source_address.setText(schedule.getS_address());
            trip_source_address.setSelected(true);
            trip_destination_address.setText(schedule.getD_address());
            trip_destination_address.setSelected(true);

            if (schedule.getPatient_name() != null){

                tv_patient_name_value.setText(schedule.getPatient_name());

            }

            if (schedule.getPurpose() != null){

                tv_purpose_value.setText(schedule.getPurpose());

            }

            /*if (schedule.getTrips() >= 0){

                tv_trips_value.setText(String.valueOf(schedule.getTrips()));

            }*/

            if (schedule.getSpecial_request() != null){

                edt_special_request.setText(schedule.getSpecial_request());
            }

            if (schedule.getRoom_number() >= 0){

                tv_room_number_value.setText(String.valueOf(schedule.getRoom_number()));

            }

            if (schedule.getFloor_number() >= 0){

                tv_floor_number_value.setText(String.valueOf(schedule.getFloor_number()));

            }

            if (schedule.getBed_number() >= 0){

                tv_bed_number_value.setText(String.valueOf(schedule.getBed_number()));
            }

            if (schedule.getWard() != null){

                tv_ward_value.setText(String.valueOf(schedule.getWard()));

            }

            if (schedule.getHospital() != null){

                tv_hospital_value.setText(schedule.getHospital());

            }


            scheduleDetail.show();

        }

    }


    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        switch (serviceCode) {

            case Const.ServiceCode.GET_SCHEDULE:
                EbizworldUtils.appLogInfo("HaoLS", "List schedule" + response);

                if (response != null && mMainActivity != null) {

                    JSONObject scheduleObject = null;
                    try {

                        scheduleObject = new JSONObject(response);

                        if (scheduleObject.getString("success").equals("true")) {

                            swipe_refresh_ride_list.setRefreshing(false);

                            ParseContent parseContent = new ParseContent(mMainActivity);

                            mSchedules.clear();

                            switch (new PreferenceHelper(mMainActivity).getLoginType()) {

                                case Const.PatientService.PATIENT: {

                                    mSchedules = parseContent.parsePatientSchedule(response);
                                }
                                break;

                                case Const.NursingHomeService.NURSING_HOME: {

                                    mSchedules = parseContent.parseNursingHomeSchedule(response);

                                }
                                break;

                                case Const.HospitalService.HOSPITAL: {

                                    mSchedules = parseContent.parseHospitalSchedule(response);

                                }
                                break;
                            }

                            if (mSchedules != null){

                                scheduleAdapter = new ScheduleAdapter(getActivity(), mSchedules);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                                later_lv.setLayoutManager(mLayoutManager);
                                later_lv.setItemAnimator(new DefaultItemAnimator());
                                later_lv.setAdapter(scheduleAdapter);
                                LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(mMainActivity, getResources().getIdentifier("layout_animation_from_left", "anim", ((AppCompatActivity) getActivity()).getPackageName()));
                                later_lv.setLayoutAnimation(animation);
                                scheduleAdapter.notifyDataSetChanged();
                                later_lv.scheduleLayoutAnimation();

                                Log.d("Manh", "Schedule size: " + mSchedules.size());

                                if (mSchedules.size() == 0) {

                                    later_empty.setVisibility(View.VISIBLE);
                                    showAlertDialog();

                                }

                            }else {
                                mSchedules.clear();
                                later_empty.setVisibility(View.VISIBLE);
                                showAlertDialog();
                            }

                        }else{

                            String error_code = scheduleObject.optString("error_code");
                            if (error_code.equals("104")) {
                                EbizworldUtils.showShortToast("You have logged in other device!", mMainActivity);
                                new PreferenceHelper(mMainActivity).Logout();
                                startActivity(new Intent(mMainActivity, WelcomeActivity.class));
                                mMainActivity.finish();

                            }

                            EbizworldUtils.appLogDebug("HaoLS", "Get schedules failed");
                            swipe_refresh_ride_list.setRefreshing(false);

                            later_empty.setVisibility(View.VISIBLE);
                            showAlertDialog();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();

                        EbizworldUtils.appLogError("HaoLS", "Get schedules failed " + e.toString());

                        if (scheduleAdapter != null) {

                            scheduleAdapter.notifyDataSetChanged();

                        }
                        swipe_refresh_ride_list.setRefreshing(false);
                        later_empty.setVisibility(View.VISIBLE);
                    }

                }
                break;

            case Const.ServiceCode.CHECKREQUEST_STATUS:{

                Log.d("HaoLS", "check req status" + response);

                if (response != null) {

                    mRequestDetails = parseRequestStatusList(response);

                    for (int i = 0; i < mRequestDetails.size(); i++){

                        EbizworldUtils.appLogDebug("HaoLS ", "RequestDetail " +  mRequestDetails.get(i).getRequestId() + " " + mRequestDetails.get(i).getTripStatus());
                    }


                    /*Bundle bundle = new Bundle();
                    RequestDetail requestDetail = new ParseContent(mMainActivity).parseRequestStatusList(response);
                    TravelMapFragment travalfragment = new TravelMapFragment();
                    if (requestDetail == null) {
                        return;
                    }
                    Log.d("Status", "onTaskCompleted:"+requestDetail.getTripStatus());

                    switch (requestDetail.getTripStatus()) {
                        case Const.NO_REQUEST:

                            new PreferenceHelper(mMainActivity).clearRequestData();
                            // startgetProvider();
                            break;

                        case Const.IS_ACCEPTED:

                        case Const.IS_DRIVER_DEPARTED:

                        case Const.IS_DRIVER_ARRIVED:

                        case Const.IS_DRIVER_TRIP_STARTED:{

                            bundle.putSerializable(Const.REQUEST_DETAIL,
                                    requestDetail);
                            bundle.putInt(Const.DRIVER_STATUS,
                                    Const.IS_ACCEPTED);
                            if (!mMainActivity.currentFragment.equals(Const.TRAVEL_MAP_FRAGMENT)) {
                                stopCheckingforproviders();

                                travalfragment.setArguments(bundle);
                                mMainActivity.addFragment(travalfragment, false, Const.TRAVEL_MAP_FRAGMENT,
                                        true);

                            }

                        }
                        break;

                        case Const.IS_DRIVER_TRIP_ENDED:
                            break;

                        case Const.IS_DRIVER_RATED:
                            break;

                        default:
                            break;*/
                }

            }
            break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.add_schedule_back:{

                getActivity().onBackPressed();

            }
            break;

            /*case R.id.btn_add_schedule:{

                mMainActivity.addFragment(new HospitalNursingBookScheduleFragment(), false, Const.NURSE_REGISTER_SCHEDULE_FRAGMENT, true);

            }
            break;*/
        }
    }

    @Override
    public void onRefresh() {

        EbizworldUtils.appLogDebug("HaoLS", "SwipeRefreshLayout: Refreshing");

        if (new PreferenceHelper(mMainActivity).getLoginType().equals(Const.PatientService.PATIENT)){

            new ScheduleListAPI(mMainActivity, this).getPatientSchedule(Const.ServiceCode.GET_SCHEDULE);
            checkreqstatus();

        }else if (new PreferenceHelper(mMainActivity).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)) {

            new ScheduleListAPI(mMainActivity, this).getNursingHomeSchedule(Const.ServiceCode.GET_SCHEDULE);
            checkreqstatus();
        }else if (new PreferenceHelper(mMainActivity).getLoginType().equals(Const.HospitalService.HOSPITAL)) {

            new ScheduleListAPI(mMainActivity, this).getHospitalSchedule(Const.ServiceCode.GET_SCHEDULE);
            checkreqstatus();
        }

    }
}
