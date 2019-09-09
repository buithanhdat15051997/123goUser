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
import android.support.v7.widget.SwitchCompat;
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
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import sg.go.user.HttpRequester.VolleyRequester;
import sg.go.user.Models.AmbulanceOperator;
import sg.go.user.Utils.EbizworldUtils;
import com.google.android.gms.maps.model.LatLng;
import com.skyfishjy.library.RippleBackground;
import sg.go.user.Adapter.AirportLstAdapter;
import sg.go.user.Adapter.LocationAdapter;
import sg.go.user.Adapter.TypesAdapter;
import sg.go.user.Models.AirportLst;
import sg.go.user.Models.LocationList;
import sg.go.user.Models.RequestDetail;
import sg.go.user.R;
import sg.go.user.Utils.Commonutils;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.ParseContent;
import sg.go.user.Utils.PreferenceHelper;

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
 * Created by user on 2/23/2017.
 */

public class AirportBookingFragment extends BaseFragment implements View.OnClickListener {
    private ImageButton airport_back;
    private ArrayList<AmbulanceOperator> typesList;
    private ArrayList<AirportLst> airportList;
    private ArrayList<LocationList> locationList;
    private Spinner sp_type_airport, sp_select_address;
    private AutoCompleteTextView et_airport_address;
    LocationAdapter locationadapter;
    private LatLng s_latlan, d_latlan;
    private String s_address, d_address;
    private SwitchCompat switch_mode;
    private String airport_details_id, location_details_id = "";
    private String airport_package_id = "";
    private TextView trip_tolls, trip_fair, from_airport, to_airport, airport_book_btn_later;
    private Button airport_book_btn;
    private boolean ischecked = false;
    private Dialog req_load_dialog;
    Handler checkreqstatus;
    private String datetime = "";
    DatePickerDialog dpd;
    TimePickerDialog tpd;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        typesList = new ArrayList<AmbulanceOperator>();
        airportList = new ArrayList<AirportLst>();
        locationList = new ArrayList<LocationList>();
        checkreqstatus = new Handler();

        getTypes();
        getAirports();
    }

    private void getAirports() {

        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.ServiceType.AIRPORT_LST + Const.Params.ID + "="
                + new PreferenceHelper(activity).getUserId() + "&" + Const.Params.TOKEN + "="
                + new PreferenceHelper(activity).getSessionToken());
        new VolleyRequester(activity, Const.GET, map, Const.ServiceCode.AIRPORT_LST,
                this);
    }

    private void getTypes() {

        if (!EbizworldUtils.isNetworkAvailable(activity)) {
            return;
        }
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.OPERATORS_URL);
        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());

        Log.d("mahi", map.toString());
        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.HOMEAMBULANCE_TYPE,
                this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.airport_booking, container,
                false);
        sp_type_airport = (Spinner) view.findViewById(R.id.sp_type_airport);
        airport_back = (ImageButton) view.findViewById(R.id.airport_back);
        sp_select_address = (Spinner) view.findViewById(R.id.sp_select_address);
        switch_mode = (SwitchCompat) view.findViewById(R.id.switch_mode);
        trip_tolls = (TextView) view.findViewById(R.id.trip_tolls);
        trip_fair = (TextView) view.findViewById(R.id.trip_fair);
        from_airport = (TextView) view.findViewById(R.id.from_airport);
        to_airport = (TextView) view.findViewById(R.id.to_airport);
        airport_book_btn = (Button) view.findViewById(R.id.airport_book_btn);
        airport_book_btn_later = (TextView) view.findViewById(R.id.airport_book_btn_later);
        airport_book_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (airport_package_id.equals("")) {
                    Commonutils.showtoast(getResources().getString(R.string.txt_error_select_details), activity);
                } else if (et_airport_address.getText().toString().length() == 0) {
                    Commonutils.showtoast(getResources().getString(R.string.txt_error_enter_address), activity);
                    et_airport_address.requestFocus();
                } else {
                    if (ischecked == true) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                s_address = airportList.get(sp_select_address.getSelectedItemPosition()).getAirport_address();
                                d_address = et_airport_address.getText().toString();


                                try {
                                    getLatlanfromAddress(URLEncoder.encode(s_address, "utf-8"));
                                    getLocationforDest(URLEncoder.encode(d_address, "utf-8"));
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }


                            }
                        }).start();
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Do something after 100ms

                                showreqloader();
                                createairportrequest();
                            }
                        }, 2000);


                    } else {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                d_address = airportList.get(sp_select_address.getSelectedItemPosition()).getAirport_address();
                                s_address = et_airport_address.getText().toString();

                                try {
                                    getLatlanfromAddress(URLEncoder.encode(s_address, "utf-8"));
                                    getLocationforDest(URLEncoder.encode(d_address, "utf-8"));
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }


                            }
                        }).start();
                        showreqloader();
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Do something after 100ms
                                createairportrequest();
                            }
                        }, 2000);
                    }
                }
            }
        });

        airport_book_btn_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (airport_package_id.equals("")) {
                    Commonutils.showtoast(getResources().getString(R.string.txt_error_select_details), activity);
                } else if (et_airport_address.getText().toString().length() == 0) {
                    Commonutils.showtoast(getResources().getString(R.string.txt_error_enter_address), activity);
                    et_airport_address.requestFocus();
                } else {
                    if (ischecked) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                s_address = airportList.get(sp_select_address.getSelectedItemPosition()).getAirport_address();
                                d_address = et_airport_address.getText().toString();
                                try {
                                    getLatlanfromAddress(URLEncoder.encode(s_address, "utf-8"));
                                    getLocationforDest(URLEncoder.encode(d_address, "utf-8"));
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        DatePicker();

                    } else {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                d_address = airportList.get(sp_select_address.getSelectedItemPosition()).getAirport_address();
                                s_address = et_airport_address.getText().toString();

                                try {
                                    getLatlanfromAddress(URLEncoder.encode(s_address, "utf-8"));
                                    getLocationforDest(URLEncoder.encode(d_address, "utf-8"));
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        DatePicker();
                    }
                }
            }
        });


        et_airport_address = (AutoCompleteTextView) view.findViewById(R.id.et_airport_address);
        sp_type_airport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView != null && typesList.size() > 0) {
                    getFare(location_details_id, airport_details_id, typesList.get(sp_type_airport.getSelectedItemPosition()).getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        sp_select_address.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView != null && typesList.size() > 0) {
                    final AirportLst airlst = (AirportLst) adapterView.getItemAtPosition(i);
                    airport_details_id = airlst.getAirport_id();
                    getFare(location_details_id, airport_details_id, typesList.get(sp_type_airport.getSelectedItemPosition()).getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        et_airport_address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0) {
                    getLocations(charSequence);
                } else {
                    trip_fair.setText("--");
                    trip_tolls.setText("--");
                    airport_package_id = "";
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        airport_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.addFragment(new HomeMapFragment(), false, Const.HOME_MAP_FRAGMENT, true);
            }
        });
        et_airport_address.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (isAdded()) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            locationadapter.notifyDataSetChanged();
                        }
                    });
                }
                et_airport_address.setSelection(0);
                //LatLng latLng = getLocationFromAddress(activity, et_source_address.getText().toString());
                final LocationList locationlst = (LocationList) adapterView.getItemAtPosition(i);
                d_address = locationlst.getLocation_address();
                location_details_id = locationlst.getLocation_id();
                et_airport_address.setText(d_address);
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {

                            getLocationforDest(URLEncoder.encode(d_address, "utf-8"));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        getFare(location_details_id, airport_details_id, typesList.get(sp_type_airport.getSelectedItemPosition()).getId());

                    }
                }).start();
            }
        });
        switch_mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    ischecked = true;
                    to_airport.setTextColor(activity.getResources().getColor(R.color.black));
                    et_airport_address.setHint(getResources().getString(R.string.txt_set_drop_loc));
                    from_airport.setTextColor(activity.getResources().getColor(R.color.circle_color));
                    if (airportList.size() > 0) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                s_address = airportList.get(sp_select_address.getSelectedItemPosition()).getAirport_address();
                                d_address = et_airport_address.getText().toString();
                                try {
                                    getLatlanfromAddress(URLEncoder.encode(s_address, "utf-8"));
                                    getLocationforDest(URLEncoder.encode(d_address, "utf-8"));
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                } else {
                    ischecked = false;
                    to_airport.setTextColor(activity.getResources().getColor(R.color.circle_color));
                    from_airport.setTextColor(activity.getResources().getColor(R.color.black));

                    et_airport_address.setHint(getResources().getString(R.string.set_pickup));
                    if (airportList.size() > 0) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                d_address = airportList.get(sp_select_address.getSelectedItemPosition()).getAirport_address();
                                s_address = et_airport_address.getText().toString();
                                try {
                                    getLatlanfromAddress(URLEncoder.encode(s_address, "utf-8"));
                                    getLocationforDest(URLEncoder.encode(d_address, "utf-8"));
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }

                }

            }
        });


        Log.d("mahi", "is checked" + ischecked);

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

    private void getLocationforDest(String selectedDestPlace) {
        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.LOCATION_API_BASE + selectedDestPlace + "&key=" + Const.GOOGLE_API_KEY);
        Log.d("mahi", "map for d_loc" + map);
        new VolleyRequester(activity, Const.GET, map, Const.ServiceCode.LOCATION_API_BASE_DESTINATION, this);
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

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //Do something after 100ms

                                    airportbooklater(datetime, typesList.get(sp_type_airport.getSelectedItemPosition()).getId());
                                }
                            }, 1000);

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

    private void airportbooklater(String datetime, String service_type) {

        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            return;
        }
        Commonutils.progressdialog_show(activity, "Requesting...");
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.REQUEST_LATER);
        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());
        map.put(Const.Params.SERVICE_TYPE, service_type);
        map.put(Const.Params.S_ADDRESS, s_address);
        map.put(Const.Params.D_ADDRESS, d_address);
        if (s_latlan != null) {
            map.put(Const.Params.LATITUDE, String.valueOf(s_latlan.latitude));
            map.put(Const.Params.LONGITUDE, String.valueOf(s_latlan.longitude));
        }
        if (d_latlan != null) {
            map.put(Const.Params.D_LONGITUDE, String.valueOf(d_latlan.longitude));
            map.put(Const.Params.D_LATITUDE, String.valueOf(d_latlan.latitude));
        }
        map.put("requested_time", datetime);
        map.put(Const.Params.REQ_STATUS_TYPE, "3");
        map.put(Const.Params.AIRPORT_PACKAGE_ID, airport_package_id);
        Log.d("mahi", "later req" + map.toString());
        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.REQUEST_LATER,
                this);

    }

    private void createairportrequest() {

        startgetreqstatus();
        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            return;
        }

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.REQUEST_AMBULANCE);
        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());
        if (s_latlan != null) {
            map.put(Const.Params.S_LATITUDE, String.valueOf(s_latlan.latitude));
            map.put(Const.Params.S_LONGITUDE, String.valueOf(s_latlan.longitude));
        }
        if (d_latlan != null) {
            map.put(Const.Params.D_LONGITUDE, String.valueOf(d_latlan.longitude));
            map.put(Const.Params.D_LATITUDE, String.valueOf(d_latlan.latitude));
        }
        map.put(Const.Params.SERVICE_TYPE, typesList.get(sp_type_airport.getSelectedItemPosition()).getId());
        map.put(Const.Params.S_ADDRESS, s_address);
        map.put(Const.Params.D_ADDRESS, d_address);
        map.put(Const.Params.AIRPORT_PACKAGE_ID, airport_package_id);
        map.put(Const.Params.REQ_STATUS_TYPE, "3");
        Log.d("mahi", "airport map " + map.toString());
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
        map.put(Const.Params.URL, Const.ServiceType.CHECKREQUEST_STATUS);
        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());


        Log.d("mahi", map.toString());
        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.CHECKREQUEST_STATUS,
                this);
    }


    private void getFare(String location_details_id, String airport_details_id, String service_id) {
        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            return;
        }

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.AIRPORT_PACKAGE_FARE);
        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());
        map.put("airport_details_id", airport_details_id);
        map.put("location_details_id", location_details_id);
        map.put(Const.Params.SERVICE_TYPE, service_id);

        Log.d("mahi", map.toString());
        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.AIRPORT_PACKAGE_FARE,
                this);

    }


    private void getLocations(CharSequence charSequence) {

        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.ServiceType.LOCATION_LST + Const.Params.ID + "="
                + new PreferenceHelper(activity).getUserId() + "&" + Const.Params.TOKEN + "="
                + new PreferenceHelper(activity).getSessionToken() + "&" + "key" + "=" + charSequence);
        new VolleyRequester(activity, Const.GET, map, Const.ServiceCode.LOCATION_LST,
                this);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    @Override
    public void onResume() {
        super.onResume();

        activity.currentFragment = Const.PAYMENT_FRAGMENT;
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {

        switch (serviceCode) {

            case Const.ServiceCode.HOMEAMBULANCE_TYPE:
                //Log.d("mahi","type res"+response);
                try {
                    JSONObject job = new JSONObject(response);
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
                                sp_type_airport.setAdapter(adapter);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
                            // Commonutils.showtoast(error, activity);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
                        s_latlan = new LatLng(lat, lan);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case Const.ServiceCode.LOCATION_API_BASE_DESTINATION:
                if (null != response) {
                    try {
                        JSONObject job = new JSONObject(response);
                        JSONArray jarray = job.optJSONArray("results");
                        JSONObject locObj = jarray.getJSONObject(0);
                        JSONObject geometryOBJ = locObj.optJSONObject("geometry");
                        JSONObject locationOBJ = geometryOBJ.optJSONObject("location");
                        double lat = locationOBJ.getDouble("lat");
                        double lan = locationOBJ.getDouble("lng");
                        d_latlan = new LatLng(lat, lan);


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
                        // Commonutils.showtoast(error, activity);
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
            case Const.ServiceCode.AIRPORT_LST:
                //Log.d("mahi","type res"+response);
                try {
                    JSONObject job = new JSONObject(response);
                    if (job.getString("success").equals("true")) {
                        airportList.clear();
                        JSONArray jarray = job.getJSONArray("airport_details");
                        if (jarray.length() > 0) {
                            for (int i = 0; i < jarray.length(); i++) {
                                JSONObject taxiobj = jarray.getJSONObject(i);
                                AirportLst type = new AirportLst();
                                type.setAirport_id(taxiobj.getString("id"));
                                type.setAirport_address(taxiobj.getString("name"));

                                airportList.add(type);
                            }

                            if (airportList != null) {
                                AirportLstAdapter adapter = new AirportLstAdapter(activity, airportList);
                                sp_select_address.setAdapter(adapter);
                            }

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    s_address = airportList.get(sp_select_address.getSelectedItemPosition()).getAirport_address();

                                    try {
                                        getLatlanfromAddress(URLEncoder.encode(s_address, "utf-8"));

                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    }
                                    airport_details_id = airportList.get(sp_select_address.getSelectedItemPosition()).getAirport_id();

                                }
                            }).start();

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Const.ServiceCode.LOCATION_LST:
                Log.d("mahi", "location list" + response);

                try {
                    JSONObject job = new JSONObject(response);
                    if (job.getString("success").equals("true")) {
                        locationList.clear();
                        JSONArray jarray = job.getJSONArray("location_details");
                        //  if (jarray.length() > 0) {
                        for (int i = 0; i < jarray.length(); i++) {
                            JSONObject taxiobj = jarray.getJSONObject(i);
                            LocationList type = new LocationList();
                            type.setLocation_id(taxiobj.getString("id"));
                            type.setLocation_address(taxiobj.getString("name"));
                            locationList.add(type);
                        }

                        if (locationList != null) {

                            if (isAdded()) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        locationadapter = new LocationAdapter(activity, R.layout.autocomplete_list_text, locationList);
                                        et_airport_address.setAdapter(locationadapter);
                                        locationadapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            case Const.ServiceCode.AIRPORT_PACKAGE_FARE:
                Log.d("mahi", "air port res" + response);
                try {
                    JSONObject job = new JSONObject(response);
                    if (job.getString("success").equals("true")) {
                        JSONObject resultObj = job.getJSONObject("airport_price_details");
                        airport_package_id = resultObj.getString("id");
                        String tripfare = resultObj.getString("price");
                        String no_tolls = resultObj.getString("number_tolls");
                        String currency = job.getString("currency");
                        trip_fair.setText(currency + " " + tripfare);
                        trip_tolls.setText(no_tolls);
                    } else {
                        trip_fair.setText("--");
                        trip_tolls.setText("--");
                        airport_package_id = "";
                        if (job.has("error_messages")) {
                            String error = job.getString("error_messages");
                            // Commonutils.showtoast(error, activity);
                        } else if (job.has("error")) {
                            String error_msg = job.getString("error");
                            // Commonutils.showtoast(error_msg, activity);
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
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

        Log.d("mahi", map.toString());
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



