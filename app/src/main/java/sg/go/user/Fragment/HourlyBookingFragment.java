package sg.go.user.Fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import sg.go.user.HttpRequester.VolleyRequester;
import sg.go.user.Models.AmbulanceOperator;
import sg.go.user.Utils.EbizworldUtils;
import com.google.android.gms.maps.model.LatLng;
import sg.go.user.Adapter.PlacesAutoCompleteAdapter;
import sg.go.user.Adapter.TypesAdapter;
import sg.go.user.Models.RequestDetail;
import sg.go.user.R;
import sg.go.user.Utils.Commonutils;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.ParseContent;
import sg.go.user.Utils.PreferenceHelper;
import com.skyfishjy.library.RippleBackground;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by user on 2/13/2017.
 */

public class HourlyBookingFragment extends BaseFragment implements View.OnClickListener {
    private ImageButton hourly_back;
    private ArrayList<AmbulanceOperator> typesList;
    private Spinner sp_type;
    private AutoCompleteTextView et_hourly_source_address;
    private PlacesAutoCompleteAdapter placesadapter;
    private PlacesAutoCompleteAdapter dest_placesadapter;
    private LatLng des_latLng, source_latlan;
    private TextView trip_fair, text_distance, hourly_book_btn_later;
    private Button hourly_book_btn;
    private EditText et_no_hours;
    private String service_type = "";
    private String hourly_package_id = "";
    private Dialog req_load_dialog;
    Handler checkreqstatus;
    private String datetime = "";
    DatePickerDialog dpd;
    TimePickerDialog tpd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        typesList = new ArrayList<AmbulanceOperator>();
        checkreqstatus = new Handler();
        getTypes();

    }

    private void getTypes() {

        if (!EbizworldUtils.isNetworkAvailable(activity)) {
            return;
        }
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.OPERATORS_URL);
        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());
EbizworldUtils.showSimpleProgressDialog(activity,"",false);
        Log.d("mahi", map.toString());
        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.HOMEAMBULANCE_TYPE,
                this);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.hourly_booking, container,
                false);
        hourly_back = (ImageButton) view.findViewById(R.id.hourly_back);
        sp_type = (Spinner) view.findViewById(R.id.sp_type);
        et_hourly_source_address = (AutoCompleteTextView) view.findViewById(R.id.et_hourly_source_address);
        //et_hourly_destination_address = (AutoCompleteTextView) view.findViewById(R.id.et_hourly_destination_address);
        trip_fair = (TextView) view.findViewById(R.id.trip_fair);
        text_distance = (TextView) view.findViewById(R.id.text_distance);
        hourly_book_btn = (Button) view.findViewById(R.id.hourly_book_btn);
        hourly_book_btn_later = (TextView) view.findViewById(R.id.hourly_book_btn_later);
        hourly_book_btn.setOnClickListener(this);
        hourly_book_btn_later.setOnClickListener(this);
        et_no_hours = (EditText) view.findViewById(R.id.et_no_hours);
        et_no_hours.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0 && null != typesList) {

                    getfare(typesList.get(sp_type.getSelectedItemPosition()).getId(), charSequence);
                } else {
                    trip_fair.setText("--");
                    text_distance.setText("--");
                    hourly_package_id = "";
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (typesList != null && et_no_hours.getText().toString().length() != 0) {
                    getfare(typesList.get(i).getId(), et_no_hours.getText().toString());
                } else {
                    trip_fair.setText("--");
                    text_distance.setText("--");
                    hourly_package_id = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        placesadapter = new PlacesAutoCompleteAdapter(activity,
                R.layout.autocomplete_list_text);
        dest_placesadapter = new PlacesAutoCompleteAdapter(activity,
                R.layout.autocomplete_list_text);


        if (placesadapter != null) {
            et_hourly_source_address.setAdapter(placesadapter);

        }


        et_hourly_source_address.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                et_hourly_source_address.setSelection(0);
                //LatLng latLng = getLocationFromAddress(activity, et_source_address.getText().toString());
                final String selectedSourcePlace = placesadapter.getItem(i);
                EbizworldUtils.hideKeyBoard(activity);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getLatlanfromAddress(URLEncoder.encode(selectedSourcePlace, "utf-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }


                    }
                }).start();

            }
        });


        hourly_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                activity.addFragment(new HomeMapFragment(), false, Const.HOME_MAP_FRAGMENT, true);

            }
        });
        return view;
    }

    private void getLatlanfromAddress(String selectedSourcePlace) {
        if (!EbizworldUtils.isNetworkAvailable(activity)) {
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.LOCATION_API_BASE + selectedSourcePlace + "&key=" + Const.GOOGLE_API_KEY);

        Log.d("mahi", "map for s_loc" + map);
        new VolleyRequester(activity, Const.GET, map, Const.ServiceCode.LOCATION_API_BASE_SOURCE, this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String source_address = "";
        if (savedInstanceState == null) {
            Bundle mBundle = getArguments();
            if (mBundle == null) {
                source_address = "";
            } else {
                source_address = mBundle.getString("pickup_address");
                et_hourly_source_address.setText(source_address);
                et_hourly_source_address.setSelection(0);

            }
        } else {
            source_address = (String) savedInstanceState.getSerializable("pickup_address");
            et_hourly_source_address.setText(source_address);
            et_hourly_source_address.setSelection(0);
        }

        try {
            getLatlanfromAddress(URLEncoder.encode(et_hourly_source_address.getText().toString(), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }


    private void getfare(String service_type, CharSequence charSequence) {

        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            return;
        }
        EbizworldUtils.showSimpleProgressDialog(activity,"",false);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.HOURLY_PACKAGE_FARE);
        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());
        map.put(Const.Params.SERVICE_TYPE, service_type);
        map.put(Const.Params.NO_HOUR, String.valueOf(charSequence));
        Log.d("mahi", map.toString());

        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.HOURLY_PACKAGE_FARE,
                this);
    }


    @Override
    public void onResume() {
        super.onResume();

        activity.currentFragment = Const.HISTORY_FRAGMENT;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.hourly_book_btn:
                if (et_hourly_source_address.getText().toString().length() == 0) {
                    Commonutils.showtoast(getResources().getString(R.string.txt_pickup_error), activity);
                    et_hourly_source_address.requestFocus();

                } else if (hourly_package_id.equals("")) {
                    Commonutils.showtoast(getResources().getString(R.string.txt_hourly_error), activity);
                    et_no_hours.requestFocus();
                } else {
                    createhourlyrequest();
                }
                break;
            case R.id.hourly_book_btn_later:
                if (et_hourly_source_address.getText().toString().length() == 0) {
                    Commonutils.showtoast(getResources().getString(R.string.txt_pickup_error), activity);
                    et_hourly_source_address.requestFocus();

                } else if (hourly_package_id.equals("")) {
                    Commonutils.showtoast(getResources().getString(R.string.txt_hourly_error), activity);
                    et_no_hours.requestFocus();
                } else {
                    DatePicker();
                }
                break;
        }

    }

    private void DatePicker() {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        dpd = new DatePickerDialog(getActivity(), R.style.datepicker,
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

                            datetime = Integer.toString(year) + "-"
                                    + Integer.toString(monthOfYear + 1) + "-"
                                    + Integer.toString(dayOfMonth);

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
        cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));
        dpd.getDatePicker().setMaxDate(cal.getTimeInMillis());
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
        cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));
        dpd.getDatePicker().setMinDate(cal.getTimeInMillis());

        dpd.show();
    }

    public void TimePicker() {

        //Log.d("pavan", "in time picker");

        final Calendar c = Calendar.getInstance();
        c.add(Calendar.MINUTE, 30);
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        tpd = new TimePickerDialog(getActivity(), R.style.datepicker,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(android.widget.TimePicker view,
                                          int hourOfDay, int minute) {
                        // txtTime.setText(hourOfDay + ":" + minute);
                        if (view.isShown()) {
                            tpd.dismiss();
                            // isTimePickerOpen = false;
                            // call api here
                            datetime = datetime.concat(" "
                                    + Integer.toString(hourOfDay) + ":"
                                    + Integer.toString(minute) + ":" + "00");

                            Hourlybooklater(datetime, typesList.get(sp_type.getSelectedItemPosition()).getId());
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

    private void Hourlybooklater(String datetime, String service_type) {

        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            return;
        }
        Commonutils.progressdialog_show(activity, "Requesting...");
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.REQUEST_LATER);
        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());
        map.put(Const.Params.SERVICE_TYPE, service_type);
        map.put(Const.Params.S_ADDRESS, et_hourly_source_address.getText().toString());
        map.put(Const.Params.D_ADDRESS, "");
        if (source_latlan != null) {
            map.put(Const.Params.LATITUDE, String.valueOf(source_latlan.latitude));
            map.put(Const.Params.LONGITUDE, String.valueOf(source_latlan.longitude));
        }

        map.put(Const.Params.D_LONGITUDE, "");
        map.put(Const.Params.D_LATITUDE, "");

        map.put("requested_time", datetime);
        map.put(Const.Params.REQ_STATUS_TYPE, "2");
        map.put(Const.Params.HOURLY_PACKAGE_ID, hourly_package_id);
        Log.d("mahi", "later req" + map.toString());
        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.REQUEST_LATER,
                this);

    }

    private void createhourlyrequest() {

        showreqloader();
        startgetreqstatus();
        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            return;
        }

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.REQUEST_AMBULANCE);
        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());
        if (source_latlan != null) {
            map.put(Const.Params.S_LATITUDE, String.valueOf(source_latlan.latitude));
            map.put(Const.Params.S_LONGITUDE, String.valueOf(source_latlan.longitude));
        }

        map.put(Const.Params.D_LONGITUDE, "");
        map.put(Const.Params.D_LATITUDE, "");

        map.put(Const.Params.SERVICE_TYPE, typesList.get(sp_type.getSelectedItemPosition()).getId());
        map.put(Const.Params.S_ADDRESS, et_hourly_source_address.getText().toString());
        map.put(Const.Params.D_ADDRESS, "");
        map.put(Const.Params.HOURLY_PACKAGE_ID, hourly_package_id);
        map.put(Const.Params.REQ_STATUS_TYPE, "2");
        Log.d("mahi", "hourly map " + map.toString());
        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.REQUEST_AMBULANCE,
                this);
    }

    private void showreqloader() {

        req_load_dialog = new Dialog(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        req_load_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        req_load_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent_black)));
        req_load_dialog.setCancelable(false);
        req_load_dialog.setContentView(R.layout.request_loading);
        final RippleBackground rippleBackground = (RippleBackground) req_load_dialog.findViewById(R.id.content);
        TextView cancel_req_create = (TextView) req_load_dialog.findViewById(R.id.cancel_req_create);
        final TextView req_status = (TextView) req_load_dialog.findViewById(R.id.req_status);
        rippleBackground.startRippleAnimation();
        cancel_req_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                req_status.setText(getResources().getString(R.string.txt_canceling_req));
                cancel_create_req();
                new PreferenceHelper(activity).clearRequestData();
                stopCheckingforstatus();
            }
        });


        req_load_dialog.show();
    }

    private void startgetreqstatus() {
        startCheckstatusTimer();
    }

    private void startCheckstatusTimer() {
        checkreqstatus.postDelayed(reqrunnable, 4000);
    }

    private void stopCheckingforstatus() {
        if (checkreqstatus != null) {
            checkreqstatus.removeCallbacks(reqrunnable);

            Log.d("mahi", "stop status handler");
        }
    }

    Runnable reqrunnable = new Runnable() {
        public void run() {
            checkreqstatus();
            checkreqstatus.postDelayed(this, 4000);
        }
    };

    private void checkreqstatus() {
        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            return;
        }

        HashMap<String, String> map = new HashMap<String, String>();
        if (new PreferenceHelper(getActivity()).getLoginType().equals(Const.PatientService.PATIENT)){

            map.put(Const.Params.URL, Const.ServiceType.CHECKREQUEST_STATUS);

        }else if (new PreferenceHelper(getActivity()).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)){

            map.put(Const.Params.URL, Const.NursingHomeService.REQUEST_STATUS_CHECK_URL);

        }
        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());


        Log.d("mahi", map.toString());
        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.CHECKREQUEST_STATUS,
                this);
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {

        switch (serviceCode) {

            case Const.ServiceCode.HOMEAMBULANCE_TYPE:
                //Log.d("mahi","type res"+response);
                try {
                    JSONObject job = new JSONObject(response);
                    EbizworldUtils.removeProgressDialog();
                    if (job.getString("success").equals("true")) {
                        typesList.clear();
                        JSONArray jarray = job.getJSONArray("services");
                        if (jarray.length() > 0) {
                            for (int i = 0; i < jarray.length(); i++) {
                                JSONObject taxiobj = jarray.getJSONObject(i);
                                AmbulanceOperator type = new AmbulanceOperator();
                                type.setId(taxiobj.getString("id"));
                                type.setAmbulanceCost(taxiobj.getString("min_fare"));
                                type.setAmbulanceImage(taxiobj.getString("picture"));
                                type.setAmbulanceOperator(taxiobj.getString("name"));
                                type.setAmbulance_price_min(taxiobj.getString("price_per_min"));
                                type.setAmbulance_price_distance(taxiobj.getString("price_per_unit_distance"));
                                type.setAmbulanceSeats(taxiobj.getString("number_seat"));
                                typesList.add(type);
                            }

                            if (typesList != null) {
                                TypesAdapter adapter = new TypesAdapter(activity, typesList);
                                sp_type.setAdapter(adapter);
                            }
                        }
                    }else if (job.getString("success").equals("false")){

                        EbizworldUtils.showShortToast("Something was wrong", getActivity());

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Const.ServiceCode.LOCATION_API_BASE_SOURCE:
                if (null != response) {
                    try {
                        JSONObject job = new JSONObject(response);
                        JSONArray jarray = job.optJSONArray("results");
                        JSONObject locObj = jarray.getJSONObject(0);
                        JSONObject geometryOBJ = locObj.optJSONObject("geometry");
                        JSONObject locationOBJ = geometryOBJ.optJSONObject("location");
                        double lat = locationOBJ.getDouble("lat");
                        double lan = locationOBJ.getDouble("lng");
                        source_latlan = new LatLng(lat, lan);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case Const.ServiceCode.REQUEST_AMBULANCE:
                Log.d("mahi", "create req_response" + response);
                try {
                    JSONObject job1 = new JSONObject(response);
                    if (job1.getString("success").equals("true")) {

                    } else {
                        // startgetProvider();
                        if (req_load_dialog != null && req_load_dialog.isShowing()) {
                            req_load_dialog.dismiss();
                        }
                        String error = job1.getString("error");
                        Commonutils.showtoast(error, activity);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Const.ServiceCode.HOURLY_PACKAGE_FARE:
                Log.d("mahi", "fare response" + response);
                EbizworldUtils.removeProgressDialog();
                try {
                    JSONObject job = new JSONObject(response);
                    if (job.getString("success").equals("true")) {
                        JSONObject fareobj = job.getJSONObject("hourly_package_details");
                        String price = fareobj.getString("price");
                        String currency = job.getString("currency");
                        String distance = fareobj.getString("distance");
                        hourly_package_id = fareobj.getString("id");
                        trip_fair.setText(currency + " " + price);
                        text_distance.setText(distance);
                    } else {
                        trip_fair.setText("--");
                        text_distance.setText("--");
                        hourly_package_id = "";
                        if (job.has("error_messages")) {
                            String error = job.getString("error_messages");
                             Commonutils.showtoast(error, activity);
                        }
                        if (job.has("error")) {
                            String error_msg = job.getString("error");
                            //Commonutils.showtoast(error_msg, activity);
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Const.ServiceCode.CANCEL_CREATE_REQUEST:
                Log.d("mahi", "cancel req_response" + response);

                if (req_load_dialog != null && req_load_dialog.isShowing()) {
                    req_load_dialog.dismiss();
                }
                break;
            case Const.ServiceCode.CHECKREQUEST_STATUS:
                Log.d("mahi", "check req status" + response);

                if (response != null) {

                    Bundle bundle = new Bundle();
                    RequestDetail requestDetail = new ParseContent(activity).parseRequestStatusNormal(response);
                    TravelMapFragment travalfragment = new TravelMapFragment();
                    if (requestDetail == null) {
                        return;
                    }


                    switch (requestDetail.getTripStatus()) {
                        case Const.NO_REQUEST:
                            new PreferenceHelper(activity).clearRequestData();
                            // startgetProvider();
                            if (req_load_dialog != null && req_load_dialog.isShowing()) {
                                req_load_dialog.dismiss();
                                Commonutils.showtoast(getResources().getString(R.string.txt_no_provider_error), activity);
                                stopCheckingforstatus();
                            }

                            break;

                        case Const.IS_ACCEPTED:
                            bundle.putSerializable(Const.REQUEST_DETAIL,
                                    requestDetail);
                            bundle.putInt(Const.DRIVER_STATUS,
                                    Const.IS_ACCEPTED);
                            if (!activity.currentFragment.equals(Const.TRAVEL_MAP_FRAGMENT)) {

                                stopCheckingforstatus();
                                if (req_load_dialog != null && req_load_dialog.isShowing()) {
                                    req_load_dialog.dismiss();
                                }
                                travalfragment.setArguments(bundle);
                                activity.addFragment(travalfragment, false, Const.TRAVEL_MAP_FRAGMENT,
                                        true);

                            }

                            break;
                        case Const.IS_DRIVER_DEPARTED:


                            break;
                        case Const.IS_DRIVER_ARRIVED:

                            break;
                        case Const.IS_DRIVER_TRIP_STARTED:

                            break;
                        case Const.IS_DRIVER_TRIP_ENDED:


                            break;
                        case Const.IS_DRIVER_RATED:

                            break;
                        default:

                            break;

                    }
                }
                break;
            case Const.ServiceCode.REQUEST_LATER:
                Log.d("mahi", "create req later" + response);
                if (response != null) {
                    try {
                        JSONObject job = new JSONObject(response);
                        if (job.getString("success").equals("true")) {
                            Commonutils.progressdialog_hide();
                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                            builder.setMessage(getResources().getString(R.string.txt_trip_schedule_success))
                                    .setCancelable(false)
                                    .setPositiveButton(getResources().getString(R.string.txt_ok), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.dismiss();
                                            activity.addFragment(new HomeMapFragment(), false, Const.HOME_MAP_FRAGMENT, true);

                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            Commonutils.progressdialog_hide();
                            String error = job.getString("error");
                            Commonutils.showtoast(error, activity);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                break;
            default:
                break;
        }
    }

    private void cancel_create_req() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.CANCEL_CREATE_REQUEST);
        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());


        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.CANCEL_CREATE_REQUEST,
                this);
    }

    @Override
    public void onDetach() {

        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        /*TO clear all views */
        ViewGroup mContainer = (ViewGroup) getActivity().findViewById(R.id.content_frame);
        mContainer.removeAllViews();
        super.onDestroyView();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
