package sg.go.user.Fragment;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import sg.go.user.Adapter.PlacesAutoCompleteAdapter;
import sg.go.user.HttpRequester.VolleyRequester;
import sg.go.user.Location.LocationHelper;
import sg.go.user.R;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.EbizworldUtils;
import sg.go.user.Utils.MarkerUtils.SmoothMoveMarker;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by user on 1/7/2017.
 */

public class SearchPlaceFragment extends BaseFragment implements View.OnClickListener, LocationHelper.OnLocationReceived, OnMapReadyCallback {

    private AutoCompleteTextView et_stop_address, et_source_address, et_destination_address, et_source_dia_address, et_destination_dia_address;
    private PlacesAutoCompleteAdapter source_placesadapter, dest_placesadapter, stop_placesadapter;

    private ImageButton search_back;
    private LatLng des_latLng, source_LatLng, stop_latLng;
    private Button btn_search;
    private GoogleMap gMap;
    private LocationHelper locHelper;
    private LatLng currentLatLan;
    private ImageView pin_marker, addStop;
    private boolean s_click = false, d_click = false, stop_click = false;
    private Bundle mBundle;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    SupportMapFragment search_place_map;
    private GoogleMap googleMap;
    private Marker PickUpMarker, DropMarker, StopMarker;
    private ImageView btn_pickLoc, pin_drop_location;
    String sourceAddress, destAddress, stopAddress;
    private String TAG = SearchPlaceFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locHelper = new LocationHelper(activity);
        locHelper.setLocationReceivedLister(this);
        mBundle = savedInstanceState;
        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_searchplace, container, false);

        et_source_address = (AutoCompleteTextView) view.findViewById(R.id.et_source_address);

        et_stop_address = (AutoCompleteTextView) view.findViewById(R.id.et_stop_address);

        et_destination_address = (AutoCompleteTextView) view.findViewById(R.id.et_destination_address);

        search_back = (ImageButton) view.findViewById(R.id.search_back);

        pin_drop_location = (ImageView) view.findViewById(R.id.pin_drop_location);

        addStop = (ImageView) view.findViewById(R.id.addStop);

        addStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_stop_address.getVisibility() == View.GONE) {
                    et_stop_address.setVisibility(View.VISIBLE);
                } else {
                    et_stop_address.setVisibility(View.GONE);
                    stop_latLng = null;
                    if (et_stop_address != null && !et_stop_address.getText().toString().isEmpty()) {
                        et_stop_address.setText("Add Stop");
                    }
                }
            }
        });

        search_back.requestFocus();

        btn_search = (Button) view.findViewById(R.id.btn_search);

        ((ImageView) view.findViewById(R.id.btn_pickLoc)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != googleMap && currentLatLan != null)

                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLan, 15));
            }
        });

        search_place_map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.search_place_map);

        if (null != search_place_map) {

            search_place_map.getMapAsync(this);

        }

        search_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                activity.addFragment(new HomeMapFragment(), false, Const.HOME_MAP_FRAGMENT, true);
                EbizworldUtils.hideKeyBoard(getActivity());

            }
        });

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (des_latLng != null) {

                    BaseFragment.drop_latlan = des_latLng;
                    Log.d(TAG, "Destination: " + des_latLng);

                    if (stop_latLng != null) {

                        Log.e("asher", "stop search map " + stop_latLng);
                        BaseFragment.stop_latlan = stop_latLng;

                    }

                    BaseFragment.searching = true;
                    EbizworldUtils.hideKeyBoard(activity);
                    BaseFragment.s_address = et_source_address.getText().toString();
                    //  BaseMapFragment.d_address = et_destination_address.getText().toString();

                    activity.addFragment(new RequestMapFragment(), false, Const.REQUEST_FRAGMENT, true);
                }

                final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        });

        source_placesadapter = new PlacesAutoCompleteAdapter(activity,
                R.layout.autocomplete_list_text);

        dest_placesadapter = new PlacesAutoCompleteAdapter(activity,
                R.layout.autocomplete_list_text);


        stop_placesadapter = new PlacesAutoCompleteAdapter(activity,
                R.layout.autocomplete_list_text);


        if (source_placesadapter != null) {
            et_source_address.setAdapter(source_placesadapter);

        }

        if (dest_placesadapter != null) {
            et_destination_address.setAdapter(dest_placesadapter);
        }

        if (stop_placesadapter != null) {
            et_stop_address.setAdapter(stop_placesadapter);
        }

        et_source_address.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                et_source_address.setSelection(0);
                sourceAddress = et_source_address.getText().toString();
                d_click = false;
                s_click = true;
                stop_click = false;
                //LatLng latLng = getLocationFromAddress(activity, et_source_address.getText().toString());
                EbizworldUtils.hideKeyBoard(activity);

                final String selectedSourcePlace = source_placesadapter.getItem(i);

                if (selectedSourcePlace != null){

                    try {
                        getLatlanfromAddress(URLEncoder.encode(selectedSourcePlace, "utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }

                et_source_address.dismissDropDown();
            }
        });

        et_destination_address.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                et_destination_address.setSelection(0);
                destAddress = et_destination_address.getText().toString();

                d_click = true;
                s_click = false;
                stop_click = false;

                EbizworldUtils.hideKeyBoard(activity);

                final String selectedDestPlace = dest_placesadapter.getItem(i);

                if (selectedDestPlace != null){

                    try {

                        getLatlanfromAddress(URLEncoder.encode(selectedDestPlace, "utf-8"));

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }

                et_destination_address.dismissDropDown();

            }
        });


        et_stop_address.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                et_stop_address.setSelection(0);
                //LatLng latLng = getLocationFromAddress(activity, et_source_address.getText().toString());
                stopAddress = et_stop_address.getText().toString();
                d_click = false;
                s_click = false;
                stop_click = true;
                EbizworldUtils.hideKeyBoard(activity);
                final String selectedSourcePlace = stop_placesadapter.getItem(i);

                if (selectedSourcePlace != null){

                    try {
                        getLatlanfromAddress(URLEncoder.encode(selectedSourcePlace, "utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }


                }
                et_stop_address.dismissDropDown();

            }
        });
        et_source_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //      showSearchMap(et_source_address.getText().toString(), et_destination_address.getText().toString(), savedInstanceState);

                d_click = false;
                s_click = true;
                stop_click = false;

            }
        });
        et_stop_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //      showSearchMap(et_source_address.getText().toString(), et_destination_address.getText().toString(), savedInstanceState);

                d_click = false;
                s_click = false;
                stop_click = true;

            }
        });

 /*       et_source_address.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    d_click = false;
                    s_click = true;
                    stop_click=false;
                    //showSearchMap(et_source_address.getText().toString(), et_destination_address.getText().toString(), mBundle);
                }

                return true; // return is important...
            }
        });
        et_stop_address.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    d_click = false;
                    s_click = false;
                    stop_click=true;

                    //showSearchMap(et_source_address.getText().toString(), et_destination_address.getText().toString(), mBundle);
                }

                return true; // return is important...
            }
        });


        et_destination_address.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    d_click = true;
                    s_click = false;
                    stop_click=false;
                    // showSearchMap(et_source_address.getText().toString(), et_destination_address.getText().toString(), mBundle)

                }

                return true; // return is important...
            }
        });*/

        et_destination_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //         showSearchMap(et_source_address.getText().toString(), et_destination_address.getText().toString(), savedInstanceState);

                d_click = true;
                s_click = false;
                stop_click = false;

            }
        });
        et_source_address.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {

                et_source_address.setText("");
                et_source_address.requestFocus();
                return false;

            }

        });

        et_destination_address.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                et_destination_address.setText("");
                et_destination_address.requestFocus();
                return false;
            }
        });


       /* et_destination_address.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //Commonutils.showtoast("search clicked", getApplicationContext());

                    if (des_latLng != null) {
                        BaseMapFragment.drop_latlan = des_latLng;
                        BaseMapFragment.searching = true;
                        EbizworldUtils.hideKeyBoard(activity);
                        BaseMapFragment.s_address = et_source_address.getText().toString();
                        BaseMapFragment.d_address = et_destination_address.getText().toString();
                        activity.addFragment(new RequestMapFragment(), false, Const.REQUEST_FRAGMENT, true);
                    }
                    return true;
                }
                return false;
            }
        });*/

        String source_address = "";
        if (savedInstanceState == null) {
            Bundle mBundle = getArguments();
            if (mBundle == null) {
                source_address = "";
            } else {
                source_address = mBundle.getString("pickup_address");
                et_source_address.setText(source_address);
                /*String[] address_lst = source_address.split(",");
                if (address_lst.length > 2) {
                    et_source_address.setText(address_lst[0] + "," + address_lst[1]);
                } else {
                    et_source_address.setText(address_lst[0]);
                }*/
                et_source_address.setSelection(0);

            }
        } else {

            source_address = (String) savedInstanceState.getSerializable("pickup_address");
            et_source_address.setText(source_address);
            /*String[] address_lst = source_address.split(",");
            if (address_lst.length > 2) {
                et_source_address.setText(address_lst[0] + "," + address_lst[1]);
            } else {
                et_source_address.setText(address_lst[0]);
            }*/
            et_source_address.setSelection(0);

        }

        et_destination_address.requestFocus();

        if(sourceAddress != null){

            try {
                getLatlanfromAddress(URLEncoder.encode(source_address, "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }

        btn_search.setEnabled(false);
        btn_search.setBackgroundColor(getResources().getColor(R.color.deeporange200));


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                final Place place = PlaceAutocomplete.getPlace(activity, data);


                /*gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(),
                        15));*/

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (s_click == true) {

                            getCompleteAddressSource(place.getLatLng());
                            sourceAddress = String.valueOf(place.getAddress());
                            source_LatLng = place.getLatLng();

                        } else if (d_click == true) {
                            getCompleteAddressSource(place.getLatLng());
                            destAddress = String.valueOf(place.getAddress());
                            des_latLng = place.getLatLng();

                        } else if (stop_click == true) {
                            getCompleteAddressSource(place.getLatLng());
                            stopAddress = String.valueOf(place.getAddress());
                            stop_latLng = place.getLatLng();
                        }

                    }
                });

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(activity, data);
                // TODO: Handle the error.
                Log.i("mahi", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void showSearchMap(String place, String d_place, Bundle savedInstanceState) {


        final Dialog searchMap = new Dialog(activity, R.style.DialogSlideAnim_leftright_Fullscreen);
        searchMap.requestWindowFeature(Window.FEATURE_NO_TITLE);
        searchMap.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent_black)));
        searchMap.setCancelable(true);
        searchMap.setContentView(R.layout.search_map_dialog);
        MapView mMapView = (MapView) searchMap.findViewById(R.id.search_map);
        pin_marker = (ImageView) searchMap.findViewById(R.id.pin_location);

        final Button btn_done = (Button) searchMap.findViewById(R.id.btn_done);
        ImageButton search_dai_back = (ImageButton) searchMap.findViewById(R.id.search_dai_back);
        et_source_dia_address = (AutoCompleteTextView) searchMap.findViewById(R.id.et_source_dia_address);
        et_source_dia_address.setText(place);
        btn_done.requestFocus();


        et_destination_dia_address = (AutoCompleteTextView) searchMap.findViewById(R.id.et_destination_dia_address);
        final PlacesAutoCompleteAdapter S_placesadapter = new PlacesAutoCompleteAdapter(activity,
                R.layout.autocomplete_list_text);
        et_source_dia_address.setAdapter(S_placesadapter);
        et_source_dia_address.setDropDownBackgroundDrawable(new ColorDrawable(Color.WHITE));

        final PlacesAutoCompleteAdapter D_placesadapter = new PlacesAutoCompleteAdapter(activity,
                R.layout.autocomplete_list_text);
        et_destination_dia_address.setAdapter(D_placesadapter);
        et_destination_dia_address.setText(d_place);
        et_destination_dia_address.setDropDownBackgroundDrawable(new ColorDrawable(Color.WHITE));

        et_source_dia_address.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                s_click = true;
                d_click = false;

                EbizworldUtils.hideKeyBoard(activity);
                pin_marker.setImageResource(R.mipmap.pickup_location);
                return false;
            }
        });


        et_destination_dia_address.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                s_click = false;
                d_click = true;
                //EbizworldUtils.hideKeyBoard(activity);
                pin_marker.setImageResource(R.mipmap.drop_location);
                return false;
            }
        });


        search_dai_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_source_address.setText(et_source_dia_address.getText().toString());
                et_destination_address.setText(et_destination_dia_address.getText().toString());
                try {
                    getLatlanfromAddress(URLEncoder.encode(et_source_dia_address.getText().toString(), "utf-8"));
                    if (!(et_destination_dia_address.getText().toString().length() == 0)) {
                        getLocationforDest(URLEncoder.encode(et_destination_dia_address.getText().toString(), "utf-8"));
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                searchMap.dismiss();
            }
        });

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_source_address.setText(et_source_dia_address.getText().toString());
                et_destination_address.setText(et_destination_dia_address.getText().toString());
                try {
                    getLatlanfromAddress(URLEncoder.encode(et_source_dia_address.getText().toString(), "utf-8"));
                    if (!(et_destination_dia_address.getText().toString().length() == 0)) {
                        getLocationforDest(URLEncoder.encode(et_destination_dia_address.getText().toString(), "utf-8"));
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                searchMap.dismiss();
            }
        });


        et_source_dia_address.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                et_source_dia_address.setSelection(0);
                //LatLng latLng = getLocationFromAddress(activity, et_source_address.getText().toString());
                // EbizworldUtils.hideKeyBoard(activity);
                final String selectedSourcePlace = S_placesadapter.getItem(i);

                if (selectedSourcePlace != null){

                    try {
                        getLatlanfromAddress(URLEncoder.encode(selectedSourcePlace, "utf-8"));

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }


            }
        });

        et_destination_dia_address.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                et_destination_dia_address.setSelection(0);
                //LatLng latLng = getLocationFromAddress(activity, et_source_address.getText().toString());
                EbizworldUtils.hideKeyBoard(activity);
                final String selectedplace = D_placesadapter.getItem(position);
                try {
                    getLocationforDest(URLEncoder.encode(selectedplace, "utf-8"));

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });

        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                gMap = googleMap;

                if (gMap != null) {

                    gMap.getUiSettings().setMyLocationButtonEnabled(true);
                    gMap.getUiSettings().setMapToolbarEnabled(false);
                    gMap.getUiSettings().setScrollGesturesEnabled(false);
                    gMap.getUiSettings().setZoomGesturesEnabled(false);
                    gMap.getUiSettings().setRotateGesturesEnabled(false);
                    gMap.getUiSettings().setTiltGesturesEnabled(false);
                    gMap.setTrafficEnabled(false);

                    if (getActivity() != null) {

                        try {
                            boolean success = gMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.style_map));

                            if (!success) {

                                Log.e(TAG, "MapStyle: parse map style failed");
                            }
                        } catch (Resources.NotFoundException e) {

                            Log.e(TAG, e.getMessage());
                        }

                    }

                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }

                    gMap.setMyLocationEnabled(true);


                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLan,
                            15));

                    gMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                        @Override
                        public void onCameraChange(CameraPosition cameraPosition) {
                            getCompleteAddressString(cameraPosition.target);
                            btn_done.requestFocus();

                        }
                    });

                }

            }
        });

        if (d_click == true) {
            pin_marker.setImageResource(R.mipmap.drop_location);
        } else {
            pin_marker.setImageResource(R.mipmap.pickup_location);

        }
        searchMap.show();
    }

    private void getCompleteAddressString(LatLng target) {
        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.ADDRESS_API_BASE + target.latitude + "," + target.longitude + "&key=" + Const.GOOGLE_API_KEY);

        Log.d("mahi", "map for address" + map);
        new VolleyRequester(activity, Const.GET, map, Const.ServiceCode.ADDRESS_API_BASE, this);
    }

    private void getCompleteAddressSource(LatLng target) {

        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.GEO_DEST + target.latitude + "," + target.longitude + "&key=" + Const.GOOGLE_API_KEY);

        Log.d("mahi", "map for address" + map);
        new VolleyRequester(activity, Const.GET, map, Const.ServiceCode.GEO_DEST, this);
    }

    /*private void getCompleteAddressDest(LatLng target) {
        if (!EbizworldUtils.isNetworkAvailable(activity)) {
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.GEO_DEST + target.latitude + "," + target.longitude + "&key=" + Const.GOOGLE_API_KEY);

        Log.d("mahi", "map for address" + map);
        new VolleyRequester(activity, Const.GET, map, Const.ServiceCode.GEO_DEST, this);
    }*/

    private void getLocationforDest(String selectedDestPlace) {
        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.LOCATION_API_BASE + selectedDestPlace + "&key=" + Const.GOOGLE_API_KEY);
        Log.d("mahi", "map for d_loc" + map);
        new VolleyRequester(activity, Const.GET, map, Const.ServiceCode.LOCATION_API_BASE_DESTINATION, this);
    }

    private void getLatlanfromAddress(String selectedSourcePlace) {
        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.LOCATION_API_BASE + selectedSourcePlace + "&key=" + Const.GOOGLE_API_KEY);

        Log.d("mahi", "map for s_loc" + map);
        new VolleyRequester(activity, Const.GET, map, Const.ServiceCode.LOCATION_API_BASE_SOURCE, this);
    }

    @Override
    public void onResume() {
        super.onResume();

        activity.currentFragment = Const.SEARCH_FRAGMENT;
    }

    @Override
    public void onTaskCompleted(final String response, int serviceCode) {
        switch (serviceCode) {
            case Const.ServiceCode.LOCATION_API_BASE_SOURCE:
                if (null != response) {
                    try {
                        JSONObject job = new JSONObject(response);
                        JSONArray jarray = job.optJSONArray("results");
                        JSONObject locObj = jarray.getJSONObject(0);
                        JSONObject geometryOBJ = locObj.optJSONObject("geometry");
                        JSONObject locationOBJ = geometryOBJ.optJSONObject("location");
                        double lat = locationOBJ.getDouble("lat");
                        double lng = locationOBJ.getDouble("lng");

                        if (s_click == true && BaseFragment.pic_latlan != null) {
                            //   et_source_dia_address.setText(locObj.optString("formatted_address"));
                            //     parseLocationDetails(response);

                            source_LatLng = new LatLng(lat, lng);
                            et_source_address.setText(sourceAddress);
                            BaseFragment.pic_latlan = source_LatLng;

                            if (null != PickUpMarker) {
                                PickUpMarker.setPosition(source_LatLng);
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(source_LatLng,
                                        16));

                                et_source_address.dismissDropDown();
                            }


                        } else if (d_click == true) {
                            //  BaseMapFragment.d_address = locObj.optString("formatted_address");
                            //    parseLocationDetailsDest(response);

                            des_latLng = new LatLng(lat, lng);
                            et_destination_address.setText(destAddress);
                            BaseFragment.d_address = destAddress;

                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(des_latLng,
                                    16));

                            if (null != getActivity() && isAdded()) {

                                btn_search.setEnabled(true);
                                btn_search.setBackgroundColor(getResources().getColor(R.color.deeporange600));

                            }

                            if (DropMarker == null) {

                                MarkerOptions markerOpt = new MarkerOptions();
                                markerOpt.position(des_latLng);
                                markerOpt.icon(BitmapDescriptorFactory.fromResource(R.mipmap.drop_location));
                                DropMarker = googleMap.addMarker(markerOpt);

                            } else {

                                DropMarker.setPosition(des_latLng);

                            }

                            et_destination_address.dismissDropDown();


                            //  d_cli
                        } else if (stop_click == true) {
                            //   parseLocationDetailsStop(response);

                            stop_latLng = new LatLng(lat, lng);
                            BaseFragment.stop_address = stopAddress;

                            et_stop_address.setText(stopAddress);
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(stop_latLng,
                                    16));

                                      /*  if (null != getActivity() && isAdded()) {
                                            btn_search.setEnabled(true);
                                            btn_search.setBackgroundColor(getResources().getColor(R.color.deeporange600));
                                        }*/

                            if (StopMarker == null) {

                                MarkerOptions markerOpt = new MarkerOptions();
                                markerOpt.position(stop_latLng);
                                markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_stop));
                                StopMarker = googleMap.addMarker(markerOpt);

                            } else {

                                StopMarker.setPosition(stop_latLng);

                            }

                            et_stop_address.dismissDropDown();
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case Const.ServiceCode.GEO_DEST:
                if (null != response) {
                    try {
                        JSONObject job = new JSONObject(response);
                        JSONArray jarray = job.optJSONArray("results");
                        final JSONObject locObj = jarray.getJSONObject(0);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (s_click == true && BaseFragment.pic_latlan != null) {
                                    //   et_source_dia_address.setText(locObj.optString("formatted_address"));
                                    //     parseLocationDetails(response);


                                    et_source_address.setText(sourceAddress);
                                    BaseFragment.pic_latlan = source_LatLng;
                                    if (null != PickUpMarker) {
                                        PickUpMarker.setPosition(source_LatLng);
                                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(source_LatLng,
                                                16));
                                    }


                                } else if (d_click == true) {
                                    //  BaseMapFragment.d_address = locObj.optString("formatted_address");
                                    //    parseLocationDetailsDest(response);


                                    BaseFragment.d_address = destAddress;

                                    et_destination_address.setText(destAddress);
                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(des_latLng,
                                            16));

                                    if (null != getActivity() && isAdded()) {
                                        btn_search.setEnabled(true);
                                        btn_search.setBackgroundColor(getResources().getColor(R.color.deeporange600));
                                    }

                                    if (DropMarker == null) {
                                        MarkerOptions markerOpt = new MarkerOptions();
                                        markerOpt.position(des_latLng);
                                        markerOpt.icon(BitmapDescriptorFactory.fromResource(R.mipmap.drop_location));
                                        DropMarker = googleMap.addMarker(markerOpt);
                                    } else {
                                        DropMarker.setPosition(des_latLng);
                                    }


                                    //  d_cli
                                } else if (stop_click == true) {
                                    //   parseLocationDetailsStop(response);


                                    BaseFragment.stop_address = stopAddress;

                                    et_stop_address.setText(stopAddress);
                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(stop_latLng,
                                            16));

                                      /*  if (null != getActivity() && isAdded()) {
                                            btn_search.setEnabled(true);
                                            btn_search.setBackgroundColor(getResources().getColor(R.color.deeporange600));
                                        }*/

                                    if (StopMarker == null) {
                                        MarkerOptions markerOpt = new MarkerOptions();
                                        markerOpt.position(stop_latLng);
                                        markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_stop));
                                        StopMarker = googleMap.addMarker(markerOpt);
                                    } else {
                                        DropMarker.setPosition(stop_latLng);
                                    }


                                }

                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case Const.ServiceCode.ADDRESS_API_BASE:
                if (null != response) {
                    try {
                        JSONObject job = new JSONObject(response);
                        JSONArray jarray = job.optJSONArray("results");
                        final JSONObject locObj = jarray.getJSONObject(0);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!d_click) {
                                    //   et_source_dia_address.setText(locObj.optString("formatted_address"));
                                    //    parseLocationDetails(response);
                                } else {
                                    //  BaseMapFragment.d_address = locObj.optString("formatted_address");
                                    //  parseLocationDetailsDest(response);
                                }

                            }
                        });

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
                        des_latLng = new LatLng(lat, lan);
                        if (null != gMap) {
                            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(des_latLng,
                                    15));
                        }
                        if (null != getActivity() && isAdded()) {
                            btn_search.setEnabled(true);
                            btn_search.setBackgroundColor(getResources().getColor(R.color.deeporange600));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;

        }

    }

    @Override
    public void onLocationReceived(LatLng latlong) {

    }

    @Override
    public void onLocationReceived(Location location) {

    }

    @Override
    public void onConntected(Bundle bundle) {

    }

    @Override
    public void onConntected(Location location) {

        if (null != location) {

            LatLng latlong = new LatLng(location.getLatitude(), location.getLongitude());
            currentLatLan = latlong;

            if (null != gMap) {

                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLan,
                        15));

            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        gMap = null;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        /*TO clear all views */
        ViewGroup mContainer = (ViewGroup) getActivity().findViewById(R.id.content_frame);
        mContainer.removeAllViews();
    }

    @Override
    public void onMapReady(GoogleMap gMap) {
        googleMap = gMap;
        EbizworldUtils.removeProgressDialog();
        if (googleMap != null) {
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            googleMap.getUiSettings().setMapToolbarEnabled(false);
            googleMap.getUiSettings().setScrollGesturesEnabled(false);
            googleMap.getUiSettings().setZoomGesturesEnabled(false);
            googleMap.getUiSettings().setRotateGesturesEnabled(false);
            googleMap.getUiSettings().setTiltGesturesEnabled(false);
            googleMap.setTrafficEnabled(false);

            if (getActivity() != null) {

                try {
                    boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.style_map));

                    if (!success) {

                        Log.e(TAG, "MapStyle: parse map style failed");
                    }
                } catch (Resources.NotFoundException e) {

                    Log.e(TAG, e.getMessage());
                }

                if (BaseFragment.pic_latlan != null){

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(BaseFragment.pic_latlan)
                            .zoom(16).build();

                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    if (null != googleMap) {
                        MarkerOptions markerOpt = new MarkerOptions();
                        markerOpt.position(BaseFragment.pic_latlan);
                        markerOpt.icon(BitmapDescriptorFactory.fromResource(R.mipmap.pickup_location));
                        PickUpMarker = googleMap.addMarker(markerOpt);

                    }

                }


                googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                    @Override
                    public void onCameraMove() {
                        if (d_click) {
                            //pin_drop_location.setVisibility(View.VISIBLE);
                        }
                    }
                });
                googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        if (d_click) {
                            des_latLng = googleMap.getCameraPosition().target;
                            getCompleteAddressString(des_latLng);
                            //  pin_drop_location.setVisibility(View.VISIBLE);
                            if (null != DropMarker) {
                                SmoothMoveMarker.animateMarker(DropMarker, googleMap.getCameraPosition().target, false, googleMap);
                            }

                        }
                    }
                });
            }
        }

   /* private void parseLocationDetails(String response) {
        Log.e("asher","Location "+response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray resultArray = jsonObject.optJSONArray("results");
            if (null != resultArray && resultArray.length() > 0) {

                JSONObject addressObject = resultArray.optJSONObject(0);
                JSONArray addressArray = addressObject.optJSONArray( "address_components");
                if (null != addressArray && addressArray.length() > 0) {
                    for (int i = 0; i < addressArray.length(); i++) {
                        JSONObject locationObject = addressArray.optJSONObject(i);
                        JSONArray typesArray = locationObject.optJSONArray("types");
                        if (null != typesArray && typesArray.length() > 0) {
                            for (int j = 0; j < typesArray.length(); j++) {
                                if (typesArray.get(j).equals("locality")) {
                                    Log.e("asher", "city " + locationObject.optString("long_name"));
                                    //  locationDetails.setCity(locationObject.optString("long_name"));
                                    //    new PreferenceHelper(activity).putCurrentCity(locationObject.optString("long_name"));
                                    new PreferenceHelper(activity).putSourceCity(locationObject.optString("long_name"));
                                }
                                if (typesArray.get(j).equals("country")) {
                                    Log.e("asher", " country " + locationObject.optString("long_name"));
                                    //   locationDetails.setCountry(locationObject.optString("long_name"));
                                    //    new PreferenceHelper(activity).putCurrentCountry(locationObject.optString("long_name"));
                                }

                            }
                        }
                    }
                    Log.e("asher", " inside 0 "+new PreferenceHelper(activity).getDestCity());
                    if (new PreferenceHelper(activity).getDestCity() == null || new PreferenceHelper(activity).getDestCity().isEmpty()) {
                        Log.e("asher", " inside 1 ");
                        et_source_address.setText(sourceAddress);
                        BaseMapFragment.pic_latlan = source_LatLng;
                        if (null != PickUpMarker) {
                            PickUpMarker.setPosition(source_LatLng);
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(source_LatLng,
                                    16));
                        }
                    } else {
                        Log.e("asher", " inside 2 ");
                        if (new PreferenceHelper(activity).getSourceCity().equalsIgnoreCase(new PreferenceHelper(activity).getDestCity())) {
                            Log.e("asher", " inside 3 ");
                            et_source_address.setText(sourceAddress);
                            BaseMapFragment.pic_latlan = source_LatLng;
                            if (null != PickUpMarker) {
                                if(source_LatLng!=null) {
                                    PickUpMarker.setPosition(source_LatLng);
                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(source_LatLng,
                                            16));
                                }

                            }

                        }else{
                            Log.e("asher", " inside 4 ");
                            Toast.makeText(activity,"Enter destination from "+new PreferenceHelper(activity).getSourceCity(),Toast.LENGTH_SHORT).show();
                            et_source_address.setText(sourceAddress);
                            BaseMapFragment.pic_latlan = source_LatLng;
                            if (null != PickUpMarker) {
                                if(source_LatLng!=null) {
                                    PickUpMarker.setPosition(source_LatLng);
                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(source_LatLng,
                                            16));
                                }

                            }
                            et_destination_address.setText(" ");
                            des_latLng=null;
                            destAddress="";

                            et_stop_address.setText(" ");
                            stop_latLng=null;
                            stopAddress="";
                            et_source_address.setVisibility(View.GONE);
                            if(DropMarker!=null) {
                                DropMarker.remove();
                            }
                            if(StopMarker!=null) {
                                StopMarker.remove();
                            }
                            btn_search.setEnabled(false);
                            btn_search.setBackgroundColor(getResources().getColor(R.color.deeporange200));
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //  return locationDetails;
    }


    private void parseLocationDetailsDest(String response) {
        Log.e("asher","Location "+response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray resultArray = jsonObject.optJSONArray("results");
            if (null != resultArray && resultArray.length() > 0) {

                JSONObject addressObject = resultArray.optJSONObject(0);
                JSONArray addressArray = addressObject.optJSONArray("address_components");
                if (null != addressArray && addressArray.length() > 0) {
                    for (int i = 0; i < addressArray.length(); i++) {
                        JSONObject locationObject = addressArray.optJSONObject(i);
                        JSONArray typesArray = locationObject.optJSONArray("types");
                        if (null != typesArray && typesArray.length() > 0) {
                            for (int j = 0; j < typesArray.length(); j++) {
                                if (typesArray.get(j).equals("locality")) {
                                    Log.e("asher","city "+locationObject.optString("long_name"));
                                    //  locationDetails.setCity(locationObject.optString("long_name"));
                                    //    new PreferenceHelper(activity).putCurrentCity(locationObject.optString("long_name"));
                                    new PreferenceHelper(activity).putDestCity(locationObject.optString("long_name"));
                                    if(new PreferenceHelper(activity).getSourceCity().equalsIgnoreCase(new PreferenceHelper(activity).getDestCity())){

                                        BaseMapFragment.d_address = destAddress;

                                        et_destination_address.setText(destAddress);
                                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(des_latLng,
                                                16));

                                        if (null != getActivity() && isAdded()) {
                                            btn_search.setEnabled(true);
                                            btn_search.setBackgroundColor(getResources().getColor(R.color.deeporange600));
                                        }

                                        if (DropMarker == null) {
                                            MarkerOptions markerOpt = new MarkerOptions();
                                            markerOpt.position(des_latLng);
                                            markerOpt.icon(BitmapDescriptorFactory.fromResource(R.mipmap.drop_location));
                                            DropMarker = googleMap.addMarker(markerOpt);
                                        } else {
                                            DropMarker.setPosition(des_latLng);
                                        }
                                    }else{
                                        Toast.makeText(activity,"Select destination from "+new PreferenceHelper(activity).getSourceCity(),Toast.LENGTH_SHORT).show();
                                    }
                                }
                                if (typesArray.get(j).equals("country")) {
                                    Log.e("asher"," country "+locationObject.optString("long_name"));
                                    //   locationDetails.setCountry(locationObject.optString("long_name"));
                                    //    new PreferenceHelper(activity).putCurrentCountry(locationObject.optString("long_name"));
                                }

                            }
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //  return locationDetails;
    }



    private void parseLocationDetailsStop(String response) {
        Log.e("asher","Location "+response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray resultArray = jsonObject.optJSONArray("results");
            if (null != resultArray && resultArray.length() > 0) {

                JSONObject addressObject = resultArray.optJSONObject(0);
                JSONArray addressArray = addressObject.optJSONArray("address_components");
                if (null != addressArray && addressArray.length() > 0) {
                    for (int i = 0; i < addressArray.length(); i++) {
                        JSONObject locationObject = addressArray.optJSONObject(i);
                        JSONArray typesArray = locationObject.optJSONArray("types");
                        if (null != typesArray && typesArray.length() > 0) {
                            for (int j = 0; j < typesArray.length(); j++) {
                                if (typesArray.get(j).equals("locality")) {
                                    Log.e("asher","city "+locationObject.optString("long_name"));
                                    //  locationDetails.setCity(locationObject.optString("long_name"));
                                    //    new PreferenceHelper(activity).putCurrentCity(locationObject.optString("long_name"));
                                    new PreferenceHelper(activity).putStopCity(locationObject.optString("long_name"));
                                    if(new PreferenceHelper(activity).getSourceCity().equalsIgnoreCase(new PreferenceHelper(activity).getStopCity())){

                                        BaseMapFragment.stop_address = stopAddress;

                                        et_stop_address.setText(stopAddress);
                                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(stop_latLng,
                                                16));

                                      *//*  if (null != getActivity() && isAdded()) {
                                            btn_search.setEnabled(true);
                                            btn_search.setBackgroundColor(getResources().getColor(R.color.deeporange600));
                                        }*//*

                                        if (StopMarker == null) {
                                            MarkerOptions markerOpt = new MarkerOptions();
                                            markerOpt.position(stop_latLng);
                                            markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.stop_pin));
                                            StopMarker = googleMap.addMarker(markerOpt);
                                        } else {
                                            DropMarker.setPosition(stop_latLng);
                                        }
                                    }else{
                                        Toast.makeText(activity,"Select stop from "+new PreferenceHelper(activity).getSourceCity(),Toast.LENGTH_SHORT).show();
                                    }

                                }
                                if (typesArray.get(j).equals("country")) {
                                    Log.e("asher"," country "+locationObject.optString("long_name"));
                                    //   locationDetails.setCountry(locationObject.optString("long_name"));
                                    //    new PreferenceHelper(activity).putCurrentCountry(locationObject.optString("long_name"));
                                }

                            }
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //  return locationDetails;
    }*/

    }
}