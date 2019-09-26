package sg.go.user.Fragment;

import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import sg.go.user.Adapter.PlacesAutoCompleteAdapter;
import sg.go.user.Adapter.TypeCarRequestAdapter;
import sg.go.user.HttpRequester.VolleyRequester;
import sg.go.user.Location.LocationHelper;
import sg.go.user.Models.AmbulanceOperator;
import sg.go.user.Models.NearByDrivers;
import sg.go.user.Models.RequestOptional;
import sg.go.user.Models.TypeCarRequest;
import sg.go.user.R;
import sg.go.user.Utils.Commonutils;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.EbizworldUtils;
import sg.go.user.Utils.MarkerUtils.SmoothMoveMarker;
import sg.go.user.Utils.PreferenceHelper;

import com.aurelhubert.simpleratingbar.SimpleRatingBar;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by user on 1/7/2017.
 */

public class SearchPlaceFragment extends BaseFragment implements View.OnClickListener, LocationHelper.OnLocationReceived, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private AutoCompleteTextView et_stop_address, et_source_address, et_destination_address, et_source_dia_address, et_destination_dia_address;
    private PlacesAutoCompleteAdapter source_placesadapter, dest_placesadapter, stop_placesadapter;
    private HashMap<Marker, Integer> markermap = new HashMap<>();
    private ArrayList<NearByDrivers> driverslatlngs = new ArrayList<>();
    private ArrayList<Marker> markers = new ArrayList<Marker>();
    private ImageButton search_back;
    private static Marker my_marker;
    private LatLng des_latLng, source_LatLng, stop_latLng;
    private Button btn_search;
    private int marker_position;
    private GoogleMap gMap;
    private GoogleMap mGoogleMap;

    private ArrayList<Marker> mMarkersMap = new ArrayList<>();
    private LocationHelper locHelper;
    private LatLng currentLatLan;

    private LatLng fromLocation, toLocation;

    private ImageView pin_marker, addStop;
    private boolean s_click = false, d_click = false, stop_click = false;
    private Bundle mBundle;

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    SupportMapFragment search_place_map;
    private GoogleMap googleMap;

    private Marker PickUpMarker, DropMarker, StopMarker;
    private ImageView btn_pickLoc, pin_drop_location;
    String sourceAddress, destAddress, stopAddress;


    // --------------------------------------//
    private String TAG = SearchPlaceFragment.class.getSimpleName();
    private ArrayList<Polyline> polylineData;
    private JSONArray routeArray;

    public static String Distance_Request_Home;
    public static String Time_Request_Home;

    private Button cardShowRedA, cardShowRedB, cardShowRedC;
    private ArrayList<Button> arrayListButtonShow;

    public static String OverView_Polyline_Home;
    PolylineOptions options;

    private Polyline blackPolyLine, greyPolyLine;
    private List<LatLng> listLatLng = new ArrayList<>();
    private static Polyline poly_line_click_home;

    /*---- Bottom sheet ----*/
    private BottomSheetBehavior bottomSheetBehavior1;
    private FrameLayout bottomSheetLayout1;
    private TextView btn_request_cab_home, mTv_ambulance_operator_notice_home, tv_optional_home;
    private EditText trip_remark_home;

    /*--- Adapter Type Car ----*/
    private TypeCarRequestAdapter typeCarRequest_Adapter_Home;
    private ArrayList<TypeCarRequest> typeCarRequest_ArrayList_home;
    private RecyclerView Recyc_Type_Car;
    private RequestOptional mRequestOptional_Home;
    private ImageView img_cancel_choss_type_car;

    ProgressBar progressBarHome;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locHelper = new LocationHelper(activity);
        locHelper.setLocationReceivedLister(this);
        Log.d("ccccccccccc", " onCreate");
        mBundle = savedInstanceState;
        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
        }
    }


    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    private void fitmarkers_toMap(LatLng fromlocation, LatLng tolocation) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

//the include method will calculate the min and max bound.
        builder.include(fromlocation);
        builder.include(tolocation);
        LatLngBounds bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (height * 0.19); // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        if (null != googleMap) {
            googleMap.moveCamera(cu);
        }
    }

    /*Draw Polylines 3 Routes*/
    public void drawPath(String result) {

        try {
            //Tranform the string into a json object
            polylineData = new ArrayList<>();
            final JSONObject json = new JSONObject(result);
            Log.d(TAG, "Routes    1   :" + json);
            routeArray = json.getJSONArray("routes");

//            Distance_Request_Home = routeArray.getJSONObject(0)
//                    .getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getString("text");
//
//            Time_Request_Home = routeArray
//                    .getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getString("text");
//            OverView_Polyline_Home = routeArray.getJSONObject(0).getJSONObject("overview_polyline").getString("points");

            String color[] = {"#E23338", "#212121", "#1DCCC8"};
          //  String color[] = {"#ff0000", "#00ff00", "#7883cf"};
            int i;
            for (i = 0; i < routeArray.length(); i++) {

                JSONObject routes = routeArray.getJSONObject(i);

                JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");

                String encodedString = overviewPolylines.getString("points");


                Log.d(TAG, "enCodeString  :" + encodedString);

                List<LatLng> list = decodePoly(encodedString);

//                options = new PolylineOptions().width(20).color(Color.parseColor(color[0])).geodesic(true).clickable(true);

                options = new PolylineOptions().width(10).color(Color.parseColor(color[i])).geodesic(true).clickable(true);

                for (int z = 0; z < list.size(); z++) {

                    LatLng point = list.get(z);
                    // check permission draw color
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        options.add(point);

                    } else {

                        options.add(point);
                    }
                }

                this.listLatLng.addAll(list);

                if (googleMap != null) {

                    blackPolyLine = googleMap.addPolyline(options);

                    polylineData.add(blackPolyLine);

                    blackPolyLine.setClickable(true);

                    /*----  POLYLINES CLICK ----*/
                    googleMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
                        @Override
                        public void onPolylineClick(Polyline polyline) {
                            //  linearLayoutShowDirec.setVisibility(View.VISIBLE);

                            String txt_Name[] = {" A", " B", " C"};
                            String color[] = {"#E23338", "#212121", "#1DCCC8"};
                            for (int j = 0; j < polylineData.size(); j++) {

                                if (polylineData.get(j).getColor() == polyline.getColor()) {
                                    try {

                                        Log.d("DatTest3Routes", String.valueOf(routeArray.getJSONObject(j).getJSONObject("overview_polyline").getString("points")));

                                        OverView_Polyline_Home = routeArray.getJSONObject(j).getJSONObject("overview_polyline").getString("points");
                                        Distance_Request_Home = routeArray.getJSONObject(j).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getString("text");

                                        Time_Request_Home = routeArray.getJSONObject(j).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getString("text");
//                                        txt_ShowNameRoutes.setText("Name Routes :"+ txt_Name[j]);
                                        //  txt_ShowNameRoutes.setText("Name Routes:"+txt_Name[j]+"  Duration:" + Time_Request+"  Distance:" + Distance_Request);

                                        PickUpMarker.setIcon((BitmapDescriptorFactory
                                                .fromBitmap(getMarkerBitmapFromView(Time_Request_Home))));
                                        //  txt_ShowNameRoutes.setBackgroundColor(Color.parseColor(color[j]));
                                        //   Toast.makeText(activity, Distance_Request_Home + " " + Time_Request_Home, Toast.LENGTH_SHORT).show();

                                        polylineData.get(j).setZIndex(1);


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    polylineData.get(j).setWidth(20);
                                } else {

                                    polylineData.get(j).setWidth(10);
                                    polylineData.get(j).setZIndex(0);
                                }

                            }
                            poly_line_click_home = polyline;
                        }
                    });
                }

            }
        } catch (JSONException e) {

        }

        // ------- VISIBLE AND EVENT CLICK CARDVIEW -------
        setVisiableCardShowMain();
        ClickButtonRoutes();


    }

    private void ClickButtonRoutes() {

        for (int j = 0; j < polylineData.size(); j++) {

            final int finalJ = j;
            arrayListButtonShow.get(j).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //   Toast.makeText(activity, "hihi", Toast.LENGTH_SHORT).show();
                    ClickPolylinesHome(finalJ);

                }
            });
        }
    }

    private void GetfindDistanceAndTimeforTypes(LatLng pic_latlan, LatLng drop_latlan) {
        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        HashMap<String, String> map = new HashMap<>();

        map.put(Const.Params.URL, Const.GOOGLE_MATRIX_URL + Const.Params.ORIGINS + "="
                + String.valueOf(pic_latlan.latitude) + "," + String.valueOf(pic_latlan.longitude) + "&" + Const.Params.DESTINATION + "="
                + String.valueOf(drop_latlan.latitude) + "," + String.valueOf(drop_latlan.longitude) + "&" + Const.Params.MODE + "="
                + "driving" + "&" + Const.Params.LANGUAGE + "="
                + "en-EN" + "&" + "key=" + Const.GOOGLE_API_KEY + "&" + Const.Params.SENSOR + "="
                + String.valueOf(false));

        EbizworldUtils.appLogDebug("HaoLS", "distance api " + map.toString());

        new VolleyRequester(activity, Const.GET, map, 123456, this);

    }

    private void ClickPolylinesHome(int finalJ) {

        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        // progressBar
        progressBarHome.setVisibility(View.VISIBLE);


        if (fromLocation != null && toLocation != null) {
            GetfindDistanceAndTimeforTypes(fromLocation, toLocation);
        } else {
            Toast.makeText(activity, "null", Toast.LENGTH_SHORT).show();
        }

        poly_line_click_home = polylineData.get(finalJ);

        Log.d("DatTest3Routes", String.valueOf(polylineData.get(finalJ)));

        String txt_Name[] = {" A", " B", " C"};

        String color[] = {"#E23338", "#212121", "#1DCCC8"};

        for (int j = 0; j < polylineData.size(); j++) {

            if (polylineData.get(j).getColor() == poly_line_click_home.getColor()) {

                try {
                    Log.d("DatTest3Routes", String.valueOf(routeArray.getJSONObject(j).getJSONObject("overview_polyline").getString("points")));

                    OverView_Polyline_Home = routeArray.getJSONObject(j).getJSONObject("overview_polyline").getString("points");

                    Distance_Request_Home = routeArray.getJSONObject(j).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getString("text");

                    Time_Request_Home = routeArray.getJSONObject(j).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getString("text");
//                                        txt_ShowNameRoutes.setText("Name Routes :"+ txt_Name[j]);
//                    txt_ShoweDetailRoutes.setText("Name Routes:"+txt_Name[j]+"  Duration:" + Time_Request+"  Distance:" + Distance_Request);

                    PickUpMarker.setIcon((BitmapDescriptorFactory
                            .fromBitmap(getMarkerBitmapFromView(Time_Request_Home))));

//                    txt_ShowNameRoutes.setBackgroundColor(Color.parseColor(color[j]));

//                    Toast.makeText(activity, Distance_Request_Home + " " + Time_Request_Home, Toast.LENGTH_SHORT).show();

                    polylineData.get(j).setZIndex(1);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                polylineData.get(j).setWidth(20);
            } else {

                polylineData.get(j).setWidth(10);

                polylineData.get(j).setZIndex(0);
            }

        }

        /*---- Open Bottom Sheet ----*/
//        Toast.makeText(activity, "Hihi", Toast.LENGTH_SHORT).show();
        bottomSheetLayout1.setVisibility(View.VISIBLE);
        bottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);

        img_cancel_choss_type_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetLayout1.setVisibility(View.GONE);
                if (bottomSheetBehavior1.getState() == BottomSheetBehavior.STATE_COLLAPSED) {

                    bottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);

                    bottomSheetLayout1.setVisibility(View.VISIBLE);

                } else {
                    bottomSheetBehavior1.setState(BottomSheetBehavior.STATE_COLLAPSED);

                }

            }
        });

    }

    private void setVisiableCardShowMain() {
        String color2[] = {"#E23338", "#212121", "#1DCCC8"};
        String txt_Name2[] = {"A", "B", "C"};
        for (int i = 0; i < polylineData.size(); i++) {

            arrayListButtonShow.get(i).setVisibility(View.VISIBLE);
            arrayListButtonShow.get(i).setTextColor(Color.parseColor(color2[i]));

            try {
                Distance_Request_Home = routeArray.getJSONObject(i).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getString("text");

                Time_Request_Home = routeArray.getJSONObject(i).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getString("text");

                arrayListButtonShow.get(i).setText(txt_Name2[i] + " :" + Distance_Request_Home + "       " + Time_Request_Home);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    private void getDirections(double latitude, double longitude, double latitude1, double longitude1) {
        // Api v? ba ???ng
        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.DIRECTION_API_BASE + Const.ORIGIN + "="
                + String.valueOf(latitude) + "," + String.valueOf(longitude) + "&" + Const.DESTINATION + "="
                + String.valueOf(latitude1) + "," + String.valueOf(longitude1) + "&" + Const.EXTANCTION);
        Log.d(TAG, "getDirections man hình search " + map);
        new VolleyRequester(activity, Const.GET, map, Const.ServiceCode.GOOGLE_DIRECTION_API, this);

    }

    private void getAllProviders(LatLng latlong) {

        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        HashMap<String, String> map = new HashMap<String, String>();
        if (new PreferenceHelper(activity).getLoginType().equals(Const.PatientService.PATIENT)) {

            map.put(Const.Params.URL, Const.ServiceType.GET_PROVIDERS);

        }
        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());
        if (latlong != null) {
            map.put(Const.Params.LATITUDE, String.valueOf(latlong.latitude));
            map.put(Const.Params.LONGITUDE, String.valueOf(latlong.longitude));
        }
//        map.put(Const.Params.AMBULANCE_TYPE, new PreferenceHelper(activity).getRequestType());
        map.put(Const.Params.AMBULANCE_TYPE, "0");
        Log.d("HaoLS", "nearby drivers home " + map.toString());
        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.GET_PROVIDERS,
                this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_searchplace, container, false);

        typeCarRequest_ArrayList_home = new ArrayList<>();

        progressBarHome = view.findViewById(R.id.spin_kitHome);

        com.github.ybq.android.spinkit.style.Circle fadingCircle = new Circle();

        progressBarHome.setIndeterminateDrawable(fadingCircle);



        /*---- Adapter Tyoe Car*/
        Recyc_Type_Car = view.findViewById(R.id.Recyc_Type_Car);

        Recyc_Type_Car.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);

        Recyc_Type_Car.setLayoutManager(linearLayoutManager);


        /* BUTTON SHOW */
        arrayListButtonShow = new ArrayList<>();
        cardShowRedA = view.findViewById(R.id.btnRoutes1);
        cardShowRedB = view.findViewById(R.id.btnRoutes2);
        cardShowRedC = view.findViewById(R.id.btnRoutes3);

        arrayListButtonShow.add(cardShowRedA);
        arrayListButtonShow.add(cardShowRedB);
        arrayListButtonShow.add(cardShowRedC);

        /*---- Bottom Sheet ----*/
        bottomSheetLayout1 = (FrameLayout) view.findViewById(R.id.request_map_bottom_sheet1);
        btn_request_cab_home = (TextView) view.findViewById(R.id.btn_request_cab);
        trip_remark_home = (EditText) view.findViewById(R.id.trip_remark);
        img_cancel_choss_type_car = view.findViewById(R.id.img_cancel_choss_type_car);

      //  mTv_ambulance_operator_notice_home = (TextView) view.findViewById(R.id.tv_ambulance_operator_notice);
       // mTv_ambulance_operator_notice_home.setSelected(true);

        tv_optional_home = view.findViewById(R.id.tv_optional);
        bottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheetLayout1);
        bottomSheetBehavior1.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                switch (i) {

                    case BottomSheetBehavior.STATE_HIDDEN: {

                        EbizworldUtils.appLogDebug("HaoLS", "BottomSheet hidden");
                        bottomSheetBehavior1.setPeekHeight(tv_optional_home.getHeight());
                    }
                    break;

                    case BottomSheetBehavior.STATE_COLLAPSED: {

                        EbizworldUtils.appLogDebug("HaoLS", "BottomSheet collapsed");
                        bottomSheetBehavior1.setPeekHeight(tv_optional_home.getHeight());
                        tv_optional_home.setText(getResources().getString(R.string.txt_next));

                    }
                    break;

                    case BottomSheetBehavior.STATE_EXPANDED: {

                        tv_optional_home.setText(getResources().getString(R.string.txt_next));
                        EbizworldUtils.appLogDebug("HaoLS", "BottomSheet expanded");
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });


        et_source_address = (AutoCompleteTextView) view.findViewById(R.id.et_source_address);

        et_stop_address = (AutoCompleteTextView) view.findViewById(R.id.et_stop_address);

        et_destination_address = (AutoCompleteTextView) view.findViewById(R.id.et_destination_address);

        search_back = (ImageButton) view.findViewById(R.id.search_back);

        pin_drop_location = (ImageView) view.findViewById(R.id.pin_drop_location);

//        addStop = (ImageView) view.findViewById(R.id.addStop);
//        addStop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (et_stop_address.getVisibility() == View.GONE) {
//                    et_stop_address.setVisibility(View.VISIBLE);
//                } else {
//                    et_stop_address.setVisibility(View.GONE);
//                    stop_latLng = null;
//                    if (et_stop_address != null && !et_stop_address.getText().toString().isEmpty()) {
//                        et_stop_address.setText("Add Stop");
//                    }
//                }
//            }
//        });


        search_back.requestFocus();
        Log.d("ccccccccccc", " onCreateView");
        btn_search = (Button) view.findViewById(R.id.btn_search);

        ((ImageView) view.findViewById(R.id.btn_pickLoc)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != googleMap && currentLatLan != null)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLan, 15));
                PickUpMarker.setPosition(currentLatLan);
                getCompleteAddressString(currentLatLan);
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
// s? ki?n click from
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

                if (selectedSourcePlace != null) {

                    try {
                        getLatlanfromAddress(URLEncoder.encode(selectedSourcePlace, "utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }

                et_source_address.dismissDropDown();
            }
        });
// s? ki?n click to  d click
        et_destination_address.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                et_destination_address.setSelection(0);
                destAddress = et_destination_address.getText().toString();

                d_click = true;
                s_click = false;
                stop_click = false;

                EbizworldUtils.hideKeyBoard(activity);

                final String selectedDestPlace = dest_placesadapter.getItem(i);

                if (selectedDestPlace != null) {

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

                if (selectedSourcePlace != null) {

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

//        et_source_address.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (MotionEvent.ACTION_UP == event.getAction()) {
//                    d_click = false;
//                    s_click = true;
//                    stop_click=false;
//                    //showSearchMap(et_source_address.getText().toString(), et_destination_address.getText().toString(), mBundle);
//                }
//
//                return true; // return is important...
//            }
//        });
//        et_stop_address.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (MotionEvent.ACTION_UP == event.getAction()) {
//                    d_click = false;
//                    s_click = false;
//                    stop_click=true;
//
//                    //showSearchMap(et_source_address.getText().toString(), et_destination_address.getText().toString(), mBundle);
//                }
//
//                return true; // return is important...
//            }
//        });
//
//
//        et_destination_address.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (MotionEvent.ACTION_UP == event.getAction()) {
//                    d_click = true;
//                    s_click = false;
//                    stop_click=false;
//                    // showSearchMap(et_source_address.getText().toString(), et_destination_address.getText().toString(), mBundle)
//
//                }
//
//                return true; // return is important...
//            }
//        });

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
                Log.d("manh111", "ddang vao day ");
                source_address = mBundle.getString("pickup_address");
                // et_source_address.setText(gMap.getMapType());
                /*String[] address_lst = source_address.split(",");
                if (address_lst.length > 2) {
                    et_source_address.setText(address_lst[0] + "," + address_lst[1]);
                } else {
                    et_source_address.setText(address_lst[0]);
                }*/
                // et_source_address.setSelection(0);

            }
        } else {
            Log.d("manh111", "khong vao day ");
            source_address = (String) savedInstanceState.getSerializable("pickup_address");
            et_source_address.setText(source_address);
            //  et_source_address.setSelection(0);
        }

        et_destination_address.requestFocus();

        if (sourceAddress != null) {

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


    private void getDiverOperators(String dis, String dur) {

        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.OPERATORS_URL);

        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());

        map.put(Const.Params.DISTANCE, dis);
        map.put(Const.Params.TIME, dur);

        Log.d("HaoLS", "Getting ambulance operators " + map.toString());
        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.AMBULANCE_OPERATOR,
                this);
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
                        Log.d("ccccccccccc", " onActivityResult");
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

    // l?y tên v? trí d?ng text
    private void getCompleteAddressString(LatLng target) {
        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }
        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.ADDRESS_API_BASE + target.latitude + "," + target.longitude + "&key=" + Const.GOOGLE_API_KEY);
        Log.d("mahi11111", "map for address" + map);
        new VolleyRequester(activity, Const.GET, map, Const.ServiceCode.ADDRESS_API_BASE, this);
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

                if (selectedSourcePlace != null) {

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

                if (googleMap != null) {
                    Log.d("MAnh11111", "onMapReady: dsd");
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                    googleMap.getUiSettings().setMapToolbarEnabled(false);
                    googleMap.getUiSettings().setScrollGesturesEnabled(false);
                    googleMap.getUiSettings().setZoomGesturesEnabled(true);
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

                    //   gMap.setMyLocationEnabled(true);
//                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLan,
//                            15));
//                    gMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
//                        @Override
//                        public void onCameraChange(CameraPosition cameraPosition) {
//                            getCompleteAddressString(cameraPosition.target);
//                            btn_done.requestFocus();
//                        }
//                    });

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


    private void getCompleteAddressSource(LatLng target) {

        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.GEO_DEST + currentLatLan.latitude + "," + target.longitude + "&key=" + Const.GOOGLE_API_KEY);

        Log.d("mahi", "map for address" + map);
        new VolleyRequester(activity, Const.GET, map, Const.ServiceCode.GEO_DEST, this);
    }
//
//    private void getCompleteAddressDest(LatLng target) {
//        if (!EbizworldUtils.isNetworkAvailable(activity)) {
//            return;
//        }
//
//        HashMap<String, String> map = new HashMap<>();
//        map.put(Const.Params.URL, Const.GEO_DEST + target.latitude + "," + target.longitude + "&key=" + Const.GOOGLE_API_KEY);
//
//        Log.d("mahi", "map for address" + map);
//        new VolleyRequester(activity, Const.GET, map, Const.ServiceCode.GEO_DEST, this);
//    }

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
            case 123456:

                if (response != null) {
                    Log.d("123456789", response.toString());

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

//                            nearest_eta = duration;

                            String dur = durationObject.getString("value");

                            //Log.d("mahi", "time and dis" + dur + " " + dis);

                            double trip_dis = Integer.valueOf(dis) * 0.001;

                            getDiverOperators(String.valueOf(trip_dis), dur);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        EbizworldUtils.appLogError("HaoLS", "findDistanceandTime " + e.toString());
                    }
                }

                break;


            case Const.ServiceCode.AMBULANCE_OPERATOR:
                if (response != null) {

                    progressBarHome.setVisibility(View.GONE);

                    // clear arraylist
                    Log.d("Dat_operator1", response.toString());

                    if (typeCarRequest_ArrayList_home.size() > 0) {

                        typeCarRequest_ArrayList_home.clear();
                    }
                    try {
                        JSONObject job = new JSONObject(response);
                        Log.d("Dat_operator2", job.toString());

                        if (job.getString("success").equals("true")) {


                            JSONArray jarray = new JSONArray();
                            jarray = job.getJSONArray("operator");

                            Log.d("Dat_operator3", jarray.toString());

                            if (jarray.length() > 0) {

                                for (int i = 0; i < jarray.length(); i++) {

                                    JSONObject jarrayJSONObject = jarray.getJSONObject(i);
                                    AmbulanceOperator type = new AmbulanceOperator();

                                    type.setCurrencey_unit(job.optString("currency"));
                                    type.setId(jarrayJSONObject.getString("id"));
                                    type.setAmbulanceCost(jarrayJSONObject.getString("estimated_fare"));

                                    type.setAmbulanceImage(jarrayJSONObject.getString("picture"));
                                    type.setAmbulanceOperator(jarrayJSONObject.getString("name"));

                                    type.setAmbulance_price_min(jarrayJSONObject.getString("price_per_min"));
                                    type.setAmbulance_price_distance(jarrayJSONObject.getString("price_per_unit_distance"));
                                    type.setAmbulanceSeats(jarrayJSONObject.getString("number_seat"));
                                    type.setBasefare(jarrayJSONObject.optString("min_fare"));
                                    /* ADD ARRAYLIST TYPE CAR */

                                    //  Log.d("aaaaaa",jarrayJSONObject.getString("name")+jarrayJSONObject.getString("picture").toString());

                                    typeCarRequest_ArrayList_home.add(new TypeCarRequest(jarrayJSONObject.getString("name").toString(),
                                            jarrayJSONObject.getString("picture").toString(), jarrayJSONObject.getString("service_fee").toString()));

                                }

                                /*--- Event On Click Item RecyclerView Type Car ---*/

                                typeCarRequest_Adapter_Home = new TypeCarRequestAdapter(activity, typeCarRequest_ArrayList_home, new TypeCarRequestAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {

                                        getTripOptional_Home(position + 1);

                                        // Toast.makeText(activity, position + 1 + " Select: " + typeCarRequest_ArrayList_home.get(position).getImga_Type_Car_Request() + "   " + typeCarRequest_ArrayList_home.get(position).getName_Type_Car_Request(), Toast.LENGTH_SHORT).show();

                                        if (mRequestOptional_Home != null) {

                                    /*        mRequestOptional_Home.setNameType_send_billinginfo(typeCarRequest_ArrayList_home.get(position).getName_Type_Car_Request());
                                            mRequestOptional_Home.setImgType_send_billinginfo(typeCarRequest_ArrayList_home.get(position).getImga_Type_Car_Request());*/

                                            // Save PreferenceHelper
                                            new PreferenceHelper(activity).putTypeCarBillingInfo(typeCarRequest_ArrayList_home.get(position).getName_Type_Car_Request());
                                            new PreferenceHelper(activity).putImageTypeCarBillingInfo(typeCarRequest_ArrayList_home.get(position).getImga_Type_Car_Request());

                                            Bundle bundle = new Bundle();
                                            bundle.putParcelable(Const.Params.REQUEST_OPTIONAL, mRequestOptional_Home);

                                            BillingInfoFragment billingInfoFragment = new BillingInfoFragment();
                                            billingInfoFragment.setArguments(bundle);
                                            activity.addFragment(billingInfoFragment, true, Const.BILLING_INFO_FRAGMENT, true);

                                        } else {
                                            Toast.makeText(activity, "Null", Toast.LENGTH_SHORT).show();
                                        }


                                    }
                                });

                                Recyc_Type_Car.setAdapter(typeCarRequest_Adapter_Home);


                            }

                        } else {

                            EbizworldUtils.appLogError("HaoLS", "Get Ambulance Operator failed");
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        EbizworldUtils.appLogError("HaoLS", "Get Ambulance Operator failed");
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
                        double lng = locationOBJ.getDouble("lng");

                        if (s_click == true && currentLatLan != null) {
                            //   et_source_dia_address.setText(locObj.optString("formatted_address"));
                            //     parseLocationDetails(response);
                            Log.d("getApigoogle", "khi click vào ");
                            source_LatLng = new LatLng(lat, lng);
//                            source_LatLng = currentLatLan;
                            fromLocation = source_LatLng;

                            Log.d("getApigoogle", "khi click vào  " + sourceAddress);

                            et_source_address.setText(sourceAddress);

                            if (null != PickUpMarker) {
                                Log.d("getApigoogle", "vao trong ");
                                PickUpMarker.setPosition(source_LatLng);
                                PickUpMarker.setTitle(sourceAddress);
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(source_LatLng,
                                        16));
                                et_source_address.dismissDropDown();
                            }
                        } else if (d_click == true) {
                            Log.d(TAG, "khi click vào  ");
                            des_latLng = new LatLng(lat, lng);

                            toLocation = des_latLng;
                            et_destination_address.setText(destAddress);
                            BaseFragment.d_address = destAddress;
                            // googleMap.getUiSettings()

                            // Log.d(TAG, "onTaskCompleted: "+des_latLng);

                            if (DropMarker == null && des_latLng != null) {
                                // DropMarker.setPosition(des_latLng);
                                MarkerOptions markerOpt = new MarkerOptions();
                                markerOpt.position(des_latLng);
//                                markerOpt.title(et_destination_address.getText().toString());
                                markerOpt.icon(BitmapDescriptorFactory
                                        .fromResource(R.drawable.pin_stop));
                                DropMarker = googleMap.addMarker(markerOpt);
                                markermap.put(DropMarker, -2);

                            } else {

                                DropMarker.setPosition(des_latLng);

                            }
                            et_destination_address.dismissDropDown();

                            if (des_latLng != null) {

                                toLocation = des_latLng;
                                BaseFragment.drop_latlan = des_latLng;
                                Log.d(TAG, "Destination: " + des_latLng);
                                BaseFragment.searching = true;
                                EbizworldUtils.hideKeyBoard(activity);
                                BaseFragment.s_address = et_source_address.getText().toString();
//                                activity.addFragment(new RequestMapFragment(), false, Const.REQUEST_FRAGMENT, true);
                            }

//                            final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//                            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);


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

                        if (fromLocation != null && des_latLng != null) {
                            Log.d(TAG, "ve duong");
                            if (polylineData != null) {
//                                routeArray
                                for (Polyline line : polylineData) {
                                    line.remove();
                                }
                                polylineData.clear();
                                Log.d(TAG, "soduong: " + polylineData.size());
                            }
                            fitmarkers_toMap(fromLocation, des_latLng);
                            getDirections(fromLocation.latitude, fromLocation.longitude, toLocation.latitude, toLocation.longitude);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {

                    // Toast.makeText(activity, "Hihi", Toast.LENGTH_SHORT).show();
                }
                break;

            case Const.ServiceCode.GEO_DEST:
                Log.d("getApigoogle", "GEO_DEST");
                if (null != response) {
                    try {
                        JSONObject job = new JSONObject(response);
                        JSONArray jarray = job.optJSONArray("results");
                        final JSONObject locObj = jarray.getJSONObject(0);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (s_click == true) {
                                    //   et_source_dia_address.setText(locObj.optString("formatted_address"));
                                    //     parseLocationDetails(response);
                                    et_source_address.setText(sourceAddress);
//                                    googleMap
                                    // BaseFragment.pic_latlan = source_LatLng;
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
                                        //  markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_stop));
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
            case Const.ServiceCode.GOOGLE_DIRECTION_API:

                if (response != null) {

                    Log.d(TAG, "Reponse   1:" + response);
                    drawPath(response);
                }

                break;
            case Const.ServiceCode.ADDRESS_API_BASE:
                Log.d("getApigoogle", "lay vi tri dang text");
                if (null != response) {
                    try {
                        JSONObject job = new JSONObject(response);
                        JSONArray jarray = job.optJSONArray("results");
                        final JSONObject locObj = jarray.getJSONObject(0);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (!d_click) {
//                                    et_source_address.setText(locObj.optString("formatted_address"));
                                    et_source_address.setText(locObj.optString("formatted_address"));

                                    sourceAddress = locObj.optString("formatted_address");
                                    // parseLocationDetails(response);
                                    Log.d(TAG, "run: " + currentLatLan);

                                    BaseFragment.pic_latlan = currentLatLan;

                                    Log.d(TAG, "run: " + BaseFragment.pic_latlan);

                                    PickUpMarker.setTitle(sourceAddress);

                                } else {

                                    et_destination_address.setText(locObj.optString("formatted_address"));
//                                    et_source_address.setText(locObj.optString("formatted_address"));
//                                      BaseMapFragment.d_address = locObj.optString("formatted_address");
//                                      parseLocationDetailsDest(response);
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

            case Const.ServiceCode.GET_PROVIDERS:
                EbizworldUtils.appLogInfo("HaoLS", "providers: " + response);
                if (response != null) {
                    try {
                        if (googleMap != null) {

                            googleMap.getUiSettings().setScrollGesturesEnabled(true);

                        }
                        JSONObject job = new JSONObject(response);
                        if (job.getString("success").equals("true")) {
                            driverslatlngs.clear();
                            JSONArray jarray = job.getJSONArray("providers");
                            for (int i = 0; i < jarray.length(); i++) {
                                JSONObject driversObj = jarray.getJSONObject(i);
                                NearByDrivers drivers = new NearByDrivers();
                                drivers.setLatlan(new LatLng(Double.valueOf(driversObj.getString("latitude")), Double.valueOf(driversObj.getString("longitude"))));
                                drivers.setId(driversObj.getString("id"));
                                drivers.setDriver_name(driversObj.getString("first_name"));
                                if (driversObj.getString("rating").equals("0")) {
                                    drivers.setDriver_rate(0);
                                } else {
                                    drivers.setDriver_rate(Integer.valueOf(driversObj.getString("rating").charAt(0)));
                                }
                                drivers.setDriver_distance(driversObj.getString("distance"));
                                driverslatlngs.add(drivers);
                            }

                        } else {


                            EbizworldUtils.appLogDebug("HaoLS", "Get providers failed");
                        }


                        if (driverslatlngs.size() > 0) {
                            for (Marker marker : markers) {
                                marker.remove();
                            }
                            markers.clear();

                            for (int i = 0; i < driverslatlngs.size(); i++) {
                                Log.d(TAG, "get provider " + driverslatlngs.get(i).getDriver_name());
                           /* CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                                    driverslatlngs.get(i), 15);*/
                                // Log.d("mahi","markers size"+driverslatlngs.get(i).toString());
/*----Icon Driver----*/
                                final MarkerOptions currentOption = new MarkerOptions();
                                currentOption.position(driverslatlngs.get(i).getLatlan());
                                currentOption.title(driverslatlngs.get(i).getDriver_name());
                                currentOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.carambulance));
                                if (googleMap != null) {
                                    final Marker[] driver_marker = new Marker[1];
                                    final int finalI = i;

                                    activity.runOnUiThread(new Runnable() {
                                        public void run() {
                                            if (null == driver_marker[0]) {

                                                //driver_marker[0].remove();
                                                driver_marker[0] = googleMap.addMarker(currentOption);

                                            } else {
                                                driver_marker[0].setPosition(driverslatlngs.get(finalI).getLatlan());
                                            }

                                        }
                                    });

                                    markers.add(driver_marker[0]);
                                    //mGoogleMap.animateCamera(location);
                                    markermap.put(driver_marker[0], i);

                                }
                            }

                        } else {

                            for (Marker marker : markers) {
                                marker.remove();
                            }
                            markers.clear();

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;

        }

    }
    /*---- SEND BUNDLE TO BILLINGINFO FRAGMENT  ----*/

    private void getTripOptional_Home(int possitionType_Car) {

        mRequestOptional_Home = new RequestOptional();
        mRequestOptional_Home.setId(new PreferenceHelper(activity).getUserId());
        mRequestOptional_Home.setToken(new PreferenceHelper(activity).getSessionToken());
        mRequestOptional_Home.setOperator_id(possitionType_Car);

        mRequestOptional_Home.setPic_lat(toLocation.latitude);
        mRequestOptional_Home.setPic_lng(toLocation.longitude);
        mRequestOptional_Home.setDrop_lat(fromLocation.latitude);
        mRequestOptional_Home.setDrop_lng(fromLocation.longitude);

        mRequestOptional_Home.setPic_address(et_source_address.getText().toString().trim());
        mRequestOptional_Home.setDrop_address(et_destination_address.getText().toString().trim());

        mRequestOptional_Home.setKm_send_billinginfo(Distance_Request_Home.replaceAll(" km", "").toString());


    }

    @Override
    public void onLocationReceived(LatLng latlong) {

    }

    @Override
    public void onLocationReceived(Location location) {
        LatLng latlong = new LatLng(location.getLatitude(), location.getLongitude());
        currentLatLan = latlong;
        getAllProviders(currentLatLan);
    }

    @Override
    public void onConntected(Bundle bundle) {

    }

    @Override
    public void onConntected(Location location) {
        Log.d("ccccccccccc", " onConntected" + location.getLatitude());
        if (null != location) {
            LatLng latlong = new LatLng(location.getLatitude(), location.getLongitude());
            currentLatLan = latlong;
            fromLocation = latlong;
            if (null != googleMap) {
                getCompleteAddressString(fromLocation);
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(currentLatLan)
                        .zoom(16).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                if (null != googleMap) {
                    MarkerOptions markerOpt = new MarkerOptions();
                    markerOpt.position(currentLatLan);
                    markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_stop));
                    PickUpMarker = googleMap.addMarker(markerOpt);
                    markermap.put(PickUpMarker, -1);
                    mMarkersMap.add(PickUpMarker);
                    //  Log.d("ccccccccccc", "onMapReady: ");
                }
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
        Log.d("ccccccccccc", " onMapReady");
        googleMap = gMap;
        EbizworldUtils.removeProgressDialog();
        if (googleMap != null) {
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            googleMap.getUiSettings().setMapToolbarEnabled(false);
            googleMap.getUiSettings().setScrollGesturesEnabled(true);
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
            googleMap.getUiSettings().setRotateGesturesEnabled(false);
            googleMap.getUiSettings().setTiltGesturesEnabled(false);
            googleMap.setTrafficEnabled(false);
            //    googleMap.setMyLocationEnabled(true);
            if (getActivity() != null) {

                try {
                    boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.style_map));

                    if (!success) {

                        Log.e(TAG, "MapStyle: parse map style failed");
                    }
                } catch (Resources.NotFoundException e) {

                    Log.e(TAG, e.getMessage());
                }
                //Log.d(TAG, "onMapReady: "+currentLatLan);
                if (currentLatLan != null) {
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(currentLatLan)
                            .zoom(16).build();
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    if (null != googleMap) {
                        MarkerOptions markerOpt = new MarkerOptions();
                        markerOpt.position(currentLatLan);
                        markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_stop));
                        PickUpMarker = googleMap.addMarker(markerOpt);
                        //  Log.d("ccccccccccc", "onMapReady: ");

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
                // vị trí điểm đến thay đổi theo cammera
                googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        if (d_click) {
                            des_latLng = googleMap.getCameraPosition().target;
/*                            getCompleteAddressString(des_latLng);
                              pin_drop_location.setVisibility(View.VISIBLE);
                            if (null != DropMarker) {
                                SmoothMoveMarker.animateMarker(DropMarker, googleMap.getCameraPosition().target, false, googleMap);
                            }*/

                        }
                    }
                });
                googleMap.setOnMarkerClickListener(this);
                googleMap.setOnMapClickListener(this);
                googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                                                   @Override
                                                   public View getInfoWindow(Marker marker) {
                                                       View view = null;

                                                       if (markermap.get(marker) != -1 && markermap.get(marker) != -2) {
                                                           view = activity.getLayoutInflater().inflate(R.layout.driver_info_window, null);
                                                           TextView txt_driver_name = (TextView) view.findViewById(R.id.driver_name);
                                                           if (driverslatlngs.size() > 0) {
                                                               Log.d(TAG, "getInfoWindow: " + driverslatlngs.get(marker_position).getDriver_name());
                                                               txt_driver_name.setText(driverslatlngs.get(marker_position).getDriver_name());
                                                               SimpleRatingBar driver_rate = (SimpleRatingBar) view.findViewById(R.id.driver_rate);
                                                               driver_rate.setRating(driverslatlngs.get(marker_position).getDriver_rate());

                                                           }

                                                       }

                                                       return view;

                                                   }

                                                   @Override
                                                   public View getInfoContents(Marker marker) {
                                                       // Getting view from the layout file infowindowlayout.xml
                                                       return null;
                                                   }
                                               }
                );
            }

        }


    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, "onMarkerClick: " + markermap.get(marker));
        if (markermap.get(marker) != -1 && markermap.get(marker) != -2) {
            marker_position = markermap.get(marker);
        } else if (markermap.get(marker) == -1) {

        }

        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    private Bitmap getMarkerBitmapFromView(String eta) {
        String time = eta.replaceAll("\\s+", "\n");
        View customMarkerView = ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.eta_info_window, null);
        TextView markertext = (TextView) customMarkerView.findViewById(R.id.txt_eta);
        markertext.setText(time);
        markertext.setAllCaps(true);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }
}
