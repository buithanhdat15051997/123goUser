package sg.go.user.Fragment;

import android.app.Dialog;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import sg.go.user.HttpRequester.VolleyRequester;
import sg.go.user.Models.AmbulanceOperator;
import sg.go.user.Utils.EbizworldUtils;
import com.google.android.gms.maps.model.LatLng;
import sg.go.user.Adapter.BoltTypesAdapter;
import sg.go.user.Adapter.SearchPlaceAdapter;
import sg.go.user.Interface.AsyncTaskCompleteListener;
import sg.go.user.Models.RequestDetail;
import sg.go.user.R;
import sg.go.user.Utils.Commonutils;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.ParseContent;
import sg.go.user.Utils.PreferenceHelper;
import sg.go.user.Utils.RecyclerLongPressClickListener;
import com.skyfishjy.library.RippleBackground;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Mahesh on 4/20/2017.
 */

public class Bolt_Msg_Fragment extends BaseFragment implements AsyncTaskCompleteListener, View.OnClickListener {
    private RecyclerView recycel_type;
    private BoltTypesAdapter bolttypeAdapter;
    private ArrayList<AmbulanceOperator> typesList = new ArrayList<AmbulanceOperator>();
    private ImageButton bolt_back;
    private TextView first_msg, bolt_msg_1, bolt_msg_2, tv_s_address, bolt_msg_3, tv_dest_address, bolt_msg_4, tv_approximate_price;
    private LinearLayout btns_request, destination_layout, source_layout, btns_layout, lay_msg_send;
    private Button btn_yes, btn_no, btn_create_request, btn_cancel;
    private ImageView iv_dest_map, iv_source_map;
    private EditText et_message;
    private ImageView btn_send;
    private ListView searchListView;
    private LatLng s_LatLng, d_latlan;
    private ArrayList<String> resultList;
    private SearchPlaceAdapter searchAdapter;
    private String service_id = "";
    private ScrollView scroll_bolt;
    private Handler checkreqstatus = new Handler();
    private String d_Address = "", s_address = "";
    private Dialog req_load_dialog;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.bolt_layout, container,
                false);
        recycel_type = (RecyclerView) view.findViewById(R.id.recycel_type);
        first_msg = (TextView) view.findViewById(R.id.first_msg);
        bolt_msg_1 = (TextView) view.findViewById(R.id.bolt_msg_1);
        bolt_msg_2 = (TextView) view.findViewById(R.id.bolt_msg_2);
        tv_s_address = (TextView) view.findViewById(R.id.tv_s_address);
        bolt_msg_3 = (TextView) view.findViewById(R.id.bolt_msg_3);
        tv_dest_address = (TextView) view.findViewById(R.id.tv_dest_address);
        bolt_msg_4 = (TextView) view.findViewById(R.id.bolt_msg_4);
        tv_approximate_price = (TextView) view.findViewById(R.id.tv_approximate_price);
        btns_request = (LinearLayout) view.findViewById(R.id.btns_request);
        destination_layout = (LinearLayout) view.findViewById(R.id.destination_layout);
        source_layout = (LinearLayout) view.findViewById(R.id.source_layout);
        btns_layout = (LinearLayout) view.findViewById(R.id.btns_layout);
        lay_msg_send = (LinearLayout) view.findViewById(R.id.lay_msg_send);
        btn_yes = (Button) view.findViewById(R.id.btn_yes);
        btn_no = (Button) view.findViewById(R.id.btn_no);
        btn_create_request = (Button) view.findViewById(R.id.btn_create_request);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        et_message = (EditText) view.findViewById(R.id.et_message);
        btn_send = (ImageView) view.findViewById(R.id.btn_send);
        iv_dest_map = (ImageView) view.findViewById(R.id.iv_dest_map);
        iv_source_map = (ImageView) view.findViewById(R.id.iv_source_map);
        iv_source_map.setOnClickListener(this);
        iv_dest_map.setOnClickListener(this);
        btn_send.setOnClickListener(this);
        btn_yes.setOnClickListener(this);
        btn_no.setOnClickListener(this);
        btn_create_request.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        recycel_type.setLayoutManager(mLayoutManager);
        recycel_type.setItemAnimator(new DefaultItemAnimator());
        recycel_type.addItemDecoration(new SpacesItemDecoration(3));
        bolt_back = (ImageButton) view.findViewById(R.id.bolt_back);
        bolt_back.setOnClickListener(this);
        getTypes();

        recycel_type.addOnItemTouchListener(new RecyclerLongPressClickListener(activity, recycel_type, new RecyclerLongPressClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                service_id = typesList.get(position).getId();
                findDistanceAndTime(s_LatLng, d_latlan);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        scroll_bolt = (ScrollView) view.findViewById(R.id.scroll_bolt);

        scroll_bolt.post(new Runnable() {
            @Override
            public void run() {
                scroll_bolt.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    private void findDistanceAndTime(LatLng s_latlan, LatLng d_latlan) {
        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.GOOGLE_MATRIX_URL + Const.Params.ORIGINS + "="
                + String.valueOf(s_latlan.latitude) + "," + String.valueOf(s_latlan.longitude) + "&" + Const.Params.DESTINATION + "="
                + String.valueOf(d_latlan.latitude) + "," + String.valueOf(d_latlan.longitude) + "&" + Const.Params.MODE + "="
                + "driving" + "&" + Const.Params.LANGUAGE + "="
                + "en-EN" + "&" +"key="+Const.GOOGLE_API_KEY + "&" +Const.Params.SENSOR + "="
                + String.valueOf(false));
        Log.e("mahi", "distance api" + map);
        new VolleyRequester(activity, Const.GET, map, Const.ServiceCode.GOOGLE_MATRIX, this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bolt_back:
                activity.addFragment(new HomeMapFragment(), false, Const.HOME_MAP_FRAGMENT, true);
                break;
            case R.id.btn_send:
                if (et_message.getText().toString().length() == 0) {
                    EbizworldUtils.showShortToast("Please enter message!", activity);
                } else {
                    first_msg.setText(et_message.getText().toString());
                    first_msg.setVisibility(View.VISIBLE);
                    bolt_msg_1.setVisibility(View.VISIBLE);
                    lay_msg_send.setVisibility(View.GONE);
                    btns_layout.setVisibility(View.VISIBLE);
                    EbizworldUtils.hideKeyBoard(activity);
                }
                break;
            case R.id.btn_no:
                activity.addFragment(new HomeMapFragment(), false, Const.HOME_MAP_FRAGMENT, true);
                break;
            case R.id.btn_yes:
                btns_layout.setVisibility(View.GONE);
                bolt_msg_2.setVisibility(View.VISIBLE);
                new CountDownTimer(1000, 1000) {

                    public void onTick(long millisUntilFinished) {

                    }

                    public void onFinish() {
                        showDistanceDialog();
                    }

                }.start();

                break;
            case R.id.iv_source_map:
                showDistanceDialog();
                break;
            case R.id.iv_dest_map:
                showdestinationsearch();
                break;
            case R.id.btn_create_request:
                btns_request.setVisibility(View.GONE);
                RequestTaxi();
                break;
            case R.id.btn_cancel:
                clearAll();
                break;

        }

    }

    private void clearAll() {
        first_msg.setVisibility(View.GONE);
        bolt_msg_1.setVisibility(View.GONE);
        bolt_msg_2.setVisibility(View.GONE);
        source_layout.setVisibility(View.GONE);
        bolt_msg_3.setVisibility(View.GONE);
        destination_layout.setVisibility(View.GONE);
        bolt_msg_4.setVisibility(View.GONE);
        recycel_type.setVisibility(View.GONE);
        tv_approximate_price.setVisibility(View.GONE);
        btns_request.setVisibility(View.GONE);
        lay_msg_send.setVisibility(View.VISIBLE);
        et_message.setText("");

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

    private void RequestTaxi() {

        showreqloader();
        startgetreqstatus();
        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            return;
        }

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.REQUEST_AMBULANCE);
        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());
        if (s_LatLng != null) {
            map.put(Const.Params.S_LATITUDE, String.valueOf(s_LatLng.latitude));
            map.put(Const.Params.S_LONGITUDE, String.valueOf(s_LatLng.longitude));
        }
        if (d_latlan != null) {
            map.put(Const.Params.D_LONGITUDE, String.valueOf(d_latlan.longitude));
            map.put(Const.Params.D_LATITUDE, String.valueOf(d_latlan.latitude));
        }
        map.put(Const.Params.SERVICE_TYPE, service_id);
        map.put(Const.Params.S_ADDRESS, s_address);
        map.put(Const.Params.D_ADDRESS, d_Address);
        map.put(Const.Params.REQ_STATUS_TYPE, "1");

        Log.d("mahi", map.toString());
        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.REQUEST_AMBULANCE,
                this);
    }

    private void showDistanceDialog() {
        final Dialog dialog = new Dialog(activity, R.style.DialogThemeforview);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_distance_search);
        dialog.setCancelable(false);
        ImageView backButton = (ImageView) dialog.findViewById(R.id.iv_back);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  dialog.cancel();
                // searchAdapter = null;
            }
        });
        final EditText autoCompleteTextView = (EditText) dialog.findViewById(R.id.auto_search);
        searchListView = (ListView) dialog.findViewById(R.id.lv_search);
        resultList = new ArrayList<>();

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = autoCompleteTextView.getText().toString().toLowerCase(Locale.getDefault());
                getRunnableAddress(text);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                EbizworldUtils.hideKeyBoard(activity);
                s_address = (String) searchAdapter.getItem(arg2);
                autoCompleteTextView.setSelection(0);
                Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        source_layout.setVisibility(View.VISIBLE);
                        tv_s_address.setText(s_address);
                        searchAdapter = null;
                        dialog.cancel();
                        s_LatLng = EbizworldUtils.getLatLngFromAddress(s_address, activity);
                        if (s_LatLng != null) {
                            String img = getGoogleMapThumbnail(s_LatLng.latitude, s_LatLng.longitude);

                            Picasso.get().load(img).into(iv_source_map);

                        } else {

                        }
                        bolt_msg_3.setVisibility(View.VISIBLE);
                        scroll_bolt.post(new Runnable() {
                            @Override
                            public void run() {
                                scroll_bolt.fullScroll(ScrollView.FOCUS_DOWN);
                            }
                        });

                        new CountDownTimer(2000, 1000) {

                            public void onTick(long millisUntilFinished) {

                            }

                            public void onFinish() {
                                if (d_latlan == null) {
                                    showdestinationsearch();
                                }
                            }

                        }.start();
                    }
                });
            }
        });
        dialog.show();

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
                clearAll();
            }
        });


        req_load_dialog.show();
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

    private void showdestinationsearch() {
        final Dialog dialog = new Dialog(activity, R.style.DialogThemeforview);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_distance_search);
        dialog.setCancelable(false);
        ImageView backButton = (ImageView) dialog.findViewById(R.id.iv_back);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  dialog.cancel();
                // searchAdapter = null;
            }
        });
        final EditText autoCompleteTextView = (EditText) dialog.findViewById(R.id.auto_search);
        searchListView = (ListView) dialog.findViewById(R.id.lv_search);
        resultList = new ArrayList<>();
        autoCompleteTextView.setHint(getResources().getString(R.string.txt_set_drop_loc));
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = autoCompleteTextView.getText().toString().toLowerCase(Locale.getDefault());
                getRunnableAddress(text);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                EbizworldUtils.hideKeyBoard(activity);
                d_Address = (String) searchAdapter.getItem(arg2);
                autoCompleteTextView.setSelection(0);
                Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        destination_layout.setVisibility(View.VISIBLE);
                        tv_dest_address.setText(d_Address);
                        searchAdapter = null;
                        dialog.cancel();
                        d_latlan = EbizworldUtils.getLatLngFromAddress(d_Address, activity);
                        if (d_latlan != null) {
                            String url = getGoogleMapThumbnail(d_latlan.latitude, d_latlan.longitude);
                            Picasso.get().load(url).into(iv_dest_map);
                        } else {
                           /* clearAllUser();
                            EbizworldUtils.showShortToast("Please re enter!",activity);*/

                        }

                        scroll_bolt.post(new Runnable() {
                            @Override
                            public void run() {
                                scroll_bolt.fullScroll(ScrollView.FOCUS_DOWN);
                            }
                        });
                        new CountDownTimer(3000, 1000) {

                            public void onTick(long millisUntilFinished) {

                            }

                            public void onFinish() {
                                bolt_msg_4.setVisibility(View.VISIBLE);
                                recycel_type.setVisibility(View.VISIBLE);
                                scroll_bolt.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        scroll_bolt.fullScroll(ScrollView.FOCUS_DOWN);
                                    }
                                });
                            }

                        }.start();

                    }
                });
            }
        });
        dialog.show();
    }

    public static String getGoogleMapThumbnail(double lati, double longi) {
        String staticMapUrl = "http://maps.google.com/maps/api/staticmap?center=" + lati + "," + longi + "&markers=" + lati + "," + longi + "&zoom=14&size=200x200&sensor=false";
        return staticMapUrl;
    }

    private void getRunnableAddress(final String text) {
        Handler handler = null;
        Runnable run = new Runnable() {
            @Override
            public void run() {
                getAddress(text);
            }
        };

        // only canceling the network calls will not help, you need to remove all callbacks as well
        // otherwise the pending callbacks and messages will again invoke the handler and will send the request
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        } else {
            handler = new Handler();
        }
        handler.postDelayed(run, 1000);
    }

    private void getAddress(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, EbizworldUtils.getPlaceAutoCompleteUrl(text));
        EbizworldUtils.appLogDebug("Ashutosh", "AddressMap" + map);
        new VolleyRequester(activity, Const.GET, map, 205, this);
    }


    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
           /* if(parent.getChildPosition(view) == 0)
                outRect.top = space;*/
        }
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
    public void onTaskCompleted(String response, int serviceCode) {
        switch (serviceCode) {
            case Const.ServiceCode.HOMEAMBULANCE_TYPE:
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

                                bolttypeAdapter = new BoltTypesAdapter(activity, typesList);
                                recycel_type.setAdapter(bolttypeAdapter);
                                // new PreferenceHelper(this).putRequestType(typesList.get(0).getId());
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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
                            stopCheckingforstatus();
                        }
                        String error = job1.getString("error");
                        Commonutils.showtoast(error, activity);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
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
                                clearAll();
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


                            BaseFragment.drop_latlan = null;
                            BaseFragment.pic_latlan = null;
                            BaseFragment.s_address = "";
                            BaseFragment.d_address = "";

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
            case Const.ServiceCode.CANCEL_CREATE_REQUEST:
                Log.d("mahi", "cancel req_response" + response);

                if (req_load_dialog != null && req_load_dialog.isShowing()) {
                    req_load_dialog.dismiss();
                }
                break;
            case Const.ServiceCode.GOOGLE_MATRIX:
                Log.d("mahi", "google distance api" + response);
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
                        Log.d("mahi", "time and dis" + dur + " " + dis);

                        getFare(dis, dur, service_id);

                     /*   et_clientlocation.setText(destinationObject);
                        et_doctorlocation.setText(sourceObject);
                        et_distance.setText("Distance:" + " " + distance);
                        et_eta.setText("ETA:" + " " + duration);*/


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Const.ServiceCode.FARE_CALCULATION:

                if (response != null) {
                    try {
                        JSONObject job1 = new JSONObject(response);
                        if (job1.getString("success").equals("true")) {
                            String max_fare = job1.getString("estimated_fare_from");
                            String min_fare = job1.getString("estimated_fare_to");
                            tv_approximate_price.setVisibility(View.VISIBLE);
                            tv_approximate_price.setText("App.Cost:" + " " + "$" + " " + max_fare + " - " + min_fare);
                            btns_request.setVisibility(View.VISIBLE);
                            scroll_bolt.post(new Runnable() {
                                @Override
                                public void run() {
                                    scroll_bolt.fullScroll(ScrollView.FOCUS_DOWN);
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                break;
            case 205:

                EbizworldUtils.appLogDebug("Ashutosh", "GetAddress" + response);

                try {
                    JSONObject jsonObj = new JSONObject(response);
                    JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

                    // Extract the Place descriptions from the results
                    if (predsJsonArray.length() > 0) {
                        resultList.clear();
                        for (int i = 0; i < predsJsonArray.length(); i++) {
                            resultList.add(predsJsonArray.getJSONObject(i).getString(
                                    "description"));
                        }
                    } else {
                        resultList.clear();
                    }
                    if (searchAdapter == null) {
                        searchAdapter = new SearchPlaceAdapter(activity, resultList);
                        searchListView.setAdapter(searchAdapter);
                    } else {
                        searchAdapter.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    Log.e("", "Cannot process JSON results", e);
                }

                break;
        }

    }

    private void getFare(String dis, String dur, String service_id) {
        if (!EbizworldUtils.isNetworkAvailable(activity)) {
            return;
        }
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.FARE_CALCULATION);
        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());
        map.put(Const.Params.DISTANCE, dis);
        map.put(Const.Params.TIME, dur);
        map.put(Const.Params.AMBULANCE_TYPE, service_id);

        Log.d("mahi", map.toString());
        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.FARE_CALCULATION,
                this);
    }

}
