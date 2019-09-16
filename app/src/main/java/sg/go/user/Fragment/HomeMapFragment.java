package sg.go.user.Fragment;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.aurelhubert.simpleratingbar.SimpleRatingBar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import sg.go.user.Adapter.AdsAdapter;
import sg.go.user.Adapter.AmbulanceOperatorHorizontalAdapter;
import sg.go.user.Adapter.AmbulanceOperatorVerticalAdaper;
import sg.go.user.Adapter.PlacesAutoCompleteAdapter;
import sg.go.user.Adapter.SimpleSpinnerAdapter;
import sg.go.user.HttpRequester.VolleyRequester;
import sg.go.user.Interface.AdapterCallback;
import sg.go.user.Interface.AsyncTaskCompleteListener;
import sg.go.user.Interface.DialogFragmentCallback;
import sg.go.user.Location.LocationHelper;
import sg.go.user.Models.AdsList;
import sg.go.user.Models.AmbulanceOperator;
import sg.go.user.Models.HospitalDischarge;
import sg.go.user.Models.NearByDrivers;
import sg.go.user.Models.RequestDetail;
import sg.go.user.Models.Schedule;
import sg.go.user.R;
import sg.go.user.Utils.Commonutils;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.EbizworldUtils;
import sg.go.user.Utils.ItemClickSupport;
import sg.go.user.Utils.ParseContent;
import sg.go.user.Utils.PreferenceHelper;
import sg.go.user.Dialog.HospitalDischargeOptionDialog;
import sg.go.user.restAPI.BillingInfoAPI;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by user on 1/5/2017.
 */

public class HomeMapFragment extends BaseFragment implements LocationHelper.OnLocationReceived,
        AsyncTaskCompleteListener,
        OnMapReadyCallback,
        AdapterCallback,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnCameraMoveListener {

    private String TAG = HomeMapFragment.class.getSimpleName();
    private GoogleMap mGoogleMap;
    private Bundle mBundle;
    SupportMapFragment HomemapFragment;
    private View view;
    private LocationHelper locHelper;
    private Location myLocation;
    private LatLng latlong;
    private static final int DURATION = 1500;
    private TextView tv_current_location, tv_time_date, tv_total_dis, tv_estimate_fare;

    private TextView mTv_ambulance_operator_notice;
    private Button mBtn_another_ambulance_operator;
    private LinearLayout mAny_ambulance_operator_group;
    private View mView_select;
    private boolean isAnyAmbulanceOperatorGroup = false;
    private boolean isExist = false;

    private View view_between_schedule_current_location;
    private static Marker pickup_marker, drop_marker, my_marker;
    MarkerOptions pickup_opt;
    /*private FloatingActionButton btn_floating_hourly, btn_floating_airport, btn_floating_bolt;*/

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private AutoCompleteTextView et_sch_destination_address, et_sch_source_address;
    private PlacesAutoCompleteAdapter mPlacesAutoCompleteAdapter;
    private LatLng sourceLatLng, destinationLatLng;
    private String sourceAddress, destinationAddress;
    private String base_price = "", min_price = "", booking_fee = "", currency = "", distance_unit = "", ambulance_price = "";

    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Boolean mLocationPermissionGranted = true;

    private static LinearLayout layout_search;
    AmbulanceOperatorHorizontalAdapter ambulanceOperatorHorizontalAdapter;
    private boolean s_click = false, d_click = false;
    public static ImageButton btn_mylocation;

    private ArrayList<AmbulanceOperator> mAmbulanceOperatorsMain, mAmbulanceOperatorsTemp;
    private AmbulanceOperator mAmbulanceOperator;

    private ArrayList<NearByDrivers> driverslatlngs = new ArrayList<>();

    private HashMap<Marker, Integer> markermap = new HashMap<>();
    private ArrayList<Marker> mMarkersMap = new ArrayList<>();
    private ArrayList<Marker> markers = new ArrayList<Marker>();
    private int marker_position;

    private LatLng driverlatlan;
    Handler providerhandler = new Handler();
    public static String pickup_add = "";
    private ImageButton btn_add_schedule;
    DatePickerDialog dpd;
    TimePickerDialog tpd;
    String datetime = "", timeSet = "", date = "", time = "", pickupDateTime = "";
    private LatLng sch_pic_latLng, sch_drop_latLng;
    private String ambulance_type;
    private ProgressBar pbfareProgress;
    private ImageView bottomSheetArrowImage;
    private RecyclerView adsRecyclerView;
    private AdsAdapter adsAdapter;
    private List<AdsList> adsLists;
    private CheckBox cb_a_and_e, cb_imh, cb_ferry_terminals, cb_staircase, cb_tarmac;
    private Spinner spn_oxygen_tank, spn_pickup_type, spn_weight_value, spn_family_member_value;
    private int oxygenTank = 0, weight = 0, caseType = 1, familyMember = 0;
    private Dialog dialogSchedule, dialogBillingInfo;
    private Schedule mPatientSchedule;
    private HospitalDischarge mHospitalDischarge;
    private EditText edt_tv_house_unit_value;

    AsyncTaskCompleteListener asyncTaskCompleteListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBundle = savedInstanceState;
        mAmbulanceOperatorsMain = new ArrayList<AmbulanceOperator>();
        mAmbulanceOperatorsTemp = new ArrayList<>();
        /*providerhandler = new Handler();*/

        asyncTaskCompleteListener = (AsyncTaskCompleteListener) this;

        if (new PreferenceHelper(activity).getLoginType().equals(Const.PatientService.PATIENT)){

            getTypesforhome();

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.home_map_fragment, container,
                false);

        if (getActivity() != null){

            ((AppCompatActivity) getActivity()).getSupportActionBar();
        }

        tv_current_location = (TextView) view.findViewById(R.id.tv_current_location);
        layout_search = (LinearLayout) view.findViewById(R.id.layout_search);
        btn_mylocation = (ImageButton) view.findViewById(R.id.btn_mylocation);
        btn_add_schedule = (ImageButton) view.findViewById(R.id.btn_add_schedule);
        view_between_schedule_current_location = (View) view.findViewById(R.id.view_between_schedule_current_location);
        /*btn_floating_hourly = (FloatingActionButton) view.findViewById(R.id.btn_floating_hourly);
        btn_floating_airport = (FloatingActionButton) view.findViewById(R.id.btn_floating_airport);
        btn_floating_bolt = (FloatingActionButton) view.findViewById(R.id.btn_floating_bolt);*/

        HomemapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.home_map);
        btn_mylocation.setOnClickListener(this);
        tv_current_location.setOnClickListener(this);
        tv_current_location.setSelected(true);

        btn_add_schedule.setOnClickListener(this);

        if (HomemapFragment != null) {

            HomemapFragment.getMapAsync(this);

        }

        providerhandler.postDelayed(runnable, 1000);

        return view;
    }

    private void getAds() {
        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }
        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.ServiceType.ADVERTISEMENTS);
        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());

        EbizworldUtils.appLogDebug(TAG, "adsList " + map);

        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.ADVERTISEMENTS, this);
    }

    private void rotateArrow(float v) {
        bottomSheetArrowImage.setRotation(-180 * v);
    }


    public void startNewActivity(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent != null) {
            // We found the activity now start the activity
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            // Bring user to the market or let them choose an app?
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + packageName + "&hle=en"));
            context.startActivity(intent);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        EbizworldUtils.removeProgressDialog();

        if (mGoogleMap != null) {

            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                mLocationPermissionGranted = false;

                return;
            }
            getUISettingsGoogleMap(); //Customize method
            setMapStyle(); //Customize style map
            /*getDeviceLocation();*/

             mGoogleMap.setMyLocationEnabled(true);
            //mGoogleMap.setMaxZoomPreference(17);
            //  EbizworldUtils.removeLoader();

            /*MapStyleOptions style = MapStyleOptions.loadRawResourceStyle(activity, R.raw.night_modemap);
            mGoogleMap.setMapStyle(style);*/

//            Info driver windows
            mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                                               @Override
                                               public View getInfoWindow(Marker marker) {
                                                   View view = null;

                                                   if (markermap.get(marker) != -1 && markermap.get(marker) != -2) {

                                                       view = activity.getLayoutInflater().inflate(R.layout.driver_info_window, null);

                                                       TextView txt_driver_name = (TextView) view.findViewById(R.id.driver_name);

                                                       if (driverslatlngs.size() > 0) {
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

        mGoogleMap.setOnMapClickListener(this);
        mGoogleMap.setOnMarkerClickListener(this);

    }

    private void getDeviceLocation(){

        Log.d(TAG, "getDeviceLocation: getting the device current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        try {

            if (mLocationPermissionGranted){

                Task location = mFusedLocationProviderClient.getLastLocation();

                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        if (task.isSuccessful()){

                            Log.d(TAG, "onComplete: Location found" + task.getResult());

                            Location location = (Location) task.getResult();
                            BaseFragment.pic_latlan = new LatLng(location.getLatitude(), location.getLongitude());

                            moveCamera(new LatLng(location.getLatitude(), location.getLongitude()), 15);
//                            addOverlay(location.getLatitude(), location.getLongitude());

                        }else {

                            Log.d(TAG, "onComplete: Location not found");
                            EbizworldUtils.showShortToast("Unable to get current location", getActivity());
                        }
                    }
                });
            }
        }catch (SecurityException e){

            Log.d(TAG, e.getMessage());
        }

    }

    private void moveCamera(LatLng latLng, float zoom){

        Log.d(TAG, "moveCamera: moving to the latitude " + latLng.latitude + " and longitude " + latLng.longitude);

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

    }

    private void updateLocationUI(){

        if (mGoogleMap == null){

            return;

        }

        try {

            if (mLocationPermissionGranted){

                mGoogleMap.setMyLocationEnabled(true);
                getUISettingsGoogleMap();

            }else {

                mGoogleMap.setMyLocationEnabled(false);
                getLocationPermission();
            }

        }catch (SecurityException e){

            Log.e(TAG, e.getMessage());

        }
    }

    private void getLocationPermission(){

        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            mLocationPermissionGranted = true;

        }else {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    Const.PermissionRequestCode.ACCESS_LOCATION);

        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        mLocationPermissionGranted = false;

        switch (requestCode){

            case Const.PermissionRequestCode.ACCESS_LOCATION:{

//                If permission cancelled, array will empty
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    mLocationPermissionGranted = true;

                    if (mGoogleMap != null){

                        mGoogleMap.setMyLocationEnabled(true);

                    }

                }

            }
            break;

        }
    }

    //    Set onMarkerClick
    @Override
    public boolean onMarkerClick(Marker marker) {

        if (markermap.get(marker) != -1 && markermap.get(marker) != -2) {

            marker_position = markermap.get(marker);

        } else if (markermap.get(marker) == -1) {

            SearchPlaceFragment searcfragment = new SearchPlaceFragment();
            Bundle mbundle = new Bundle();
            mbundle.putString("pickup_address", pickup_add);
            searcfragment.setArguments(mbundle);
            activity.addFragment(searcfragment, false, Const.SEARCH_FRAGMENT, true);

        }

        return false;
    }

    //    Settings UI for GoogleMap
    public void getUISettingsGoogleMap(){

        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        mGoogleMap.getUiSettings().setIndoorLevelPickerEnabled(false);
        mGoogleMap.getUiSettings().setTiltGesturesEnabled(false);
        mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        mGoogleMap.getUiSettings().setRotateGesturesEnabled(false);

    }

//    Set style for map
    public void setMapStyle(){

        if (getActivity() != null){

            try {
                boolean success = mGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.style_map));

                if (!success){

                    EbizworldUtils.appLogError(TAG, "MapStyle: parse map style failed");
                }
            }catch (Resources.NotFoundException e){

                EbizworldUtils.appLogError(TAG, e.getMessage());
            }

        }
    }

    private void getTypesforhome() {
        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }


        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.OPERATORS_URL);
        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());

        Log.d("HaoLS", "Home ambulance type " + map.toString());
        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.HOMEAMBULANCE_TYPE,
                this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null){

            try {
                MapsInitializer.initialize(getActivity());
            } catch (Exception e) {

            }

            locHelper = new LocationHelper(activity);
            locHelper.setLocationReceivedLister(this);
            final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

            if (getView().getWindowToken() != null){

                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

            }

            if (new PreferenceHelper(activity).getLoginType().equals(Const.PatientService.PATIENT)){

                getTypesforhome();
            }

            if (new PreferenceHelper(activity).getRequestId() == Const.NO_REQUEST) {
                startgetProvider();
            }

        }

    }


    @Override
    public void onLocationReceived(final LatLng latlong) {
        if (latlong != null) {
           /* mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlong,
                    16));*/

        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

    }


    @Override
    public void onLocationReceived(Location location) {
        if (location != null) {
            // drawTrip(latlong);
            myLocation = location;
            LatLng latLang = new LatLng(location.getLatitude(),
                    location.getLongitude());
            latlong = latLang;

        }

    }

    private void addOverlay(double latitude, double longitude) {

        if (null != mGoogleMap) {

            GroundOverlay groundOverlay = mGoogleMap.addGroundOverlay(new
                    GroundOverlayOptions()
                    .position(new LatLng(latitude, longitude), 100)
                    .transparency(0.5f)
                    .zIndex(3)
                    .image(BitmapDescriptorFactory.fromBitmap(drawableToBitmap(activity.getResources().getDrawable(R.drawable.map_overlay)))));

            startOverlayAnimation(groundOverlay);
        }
    }


    private void startOverlayAnimation(final GroundOverlay groundOverlay) {

        AnimatorSet animatorSet = new AnimatorSet();

        ValueAnimator vAnimator = ValueAnimator.ofInt(0, 200);
        vAnimator.setRepeatCount(ValueAnimator.INFINITE);
        vAnimator.setRepeatMode(ValueAnimator.RESTART);
        vAnimator.setInterpolator(new LinearInterpolator());

        vAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                final Integer val = (Integer) valueAnimator.getAnimatedValue();
                groundOverlay.setDimensions(val);

            }

        });

        ValueAnimator tAnimator = ValueAnimator.ofFloat(0, 1);
        tAnimator.setRepeatCount(ValueAnimator.INFINITE);
        tAnimator.setRepeatMode(ValueAnimator.RESTART);
        tAnimator.setInterpolator(new LinearInterpolator());

        tAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float val = (Float) valueAnimator.getAnimatedValue();
                groundOverlay.setTransparency(val);
            }

        });

        animatorSet.setDuration(3000);
        animatorSet.playTogether(vAnimator, tAnimator);
        animatorSet.start();
    }


    private Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }


    private void getAllProviders(LatLng latlong) {

        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        HashMap<String, String> map = new HashMap<String, String>();
        if (new PreferenceHelper(activity).getLoginType().equals(Const.PatientService.PATIENT)){

            map.put(Const.Params.URL, Const.ServiceType.GET_PROVIDERS);

        }else if (new PreferenceHelper(activity).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)){

            map.put(Const.Params.URL, Const.NursingHomeService.GUEST_PROVIDER_URL);

        }else if (new PreferenceHelper(activity).getLoginType().equals(Const.HospitalService.HOSPITAL)){

            map.put(Const.Params.URL, Const.HospitalService.GUEST_PROVIDER_LIST_URL);

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
    public void onConntected(Bundle bundle) {

    }

    @Override
    public void onConntected(Location location) {

        if (location != null && null != mGoogleMap) {

            mGoogleMap.clear();

            final LatLng currentlatLang = new LatLng(location.getLatitude(), location.getLongitude());
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentlatLang,
                    16));

            BaseFragment.pic_latlan = currentlatLang;
            getCompleteAddressString(currentlatLang.latitude, currentlatLang.longitude);
            /*addOverlay(currentlatLang.latitude, currentlatLang.longitude);
            MarkerOptions markerOpt = new MarkerOptions();
            markerOpt.position(currentlatLang);
            markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.circle_map));
            Marker locMark = mGoogleMap.addMarker(markerOpt);
            markermap.put(locMark, -2);

            mMarkersMap.add(locMark); //Testing*/

        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_current_location:
                SearchPlaceFragment searcfragment = new SearchPlaceFragment();
                Bundle mbundle = new Bundle();
                mbundle.putString("pickup_address", pickup_add);
                searcfragment.setArguments(mbundle);
                activity.addFragment(searcfragment, false, Const.SEARCH_FRAGMENT, true);
                break;
            case R.id.btn_mylocation:
                if (mGoogleMap != null && latlong != null) {
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlong,
                            16));
                }
                break;

            case R.id.btn_add_schedule:{

                if (new PreferenceHelper(activity).getLoginType().equals(Const.PatientService.PATIENT)){

                    if (null != pickup_add && null != mAmbulanceOperatorsTemp) {

                        showSchedule(pickup_add, mAmbulanceOperatorsMain);

                    } else {

                        EbizworldUtils.showShortToast(getResources().getString(R.string.txt_error), activity);

                    }

                }else if (new PreferenceHelper(activity).getLoginType().equals(Const.NursingHomeService.NURSING_HOME) ||
                        new PreferenceHelper(activity).getLoginType().equals(Const.HospitalService.HOSPITAL)){

                    activity.addFragment(new HospitalNursingBookScheduleFragment(), false, Const.NURSE_REGISTER_SCHEDULE_FRAGMENT, true);

                }

            }


                break;

            /*case R.id.btn_floating_hourly:
                HourlyBookingFragment hourlyfragment = new HourlyBookingFragment();
                Bundle nbundle = new Bundle();
                nbundle.putString("pickup_address", pickup_add);
                hourlyfragment.setArguments(nbundle);
                activity.addFragment(hourlyfragment, false, Const.HISTORY_FRAGMENT, true);
                break;
            case R.id.btn_floating_airport:
                activity.addFragment(new AirportBookingFragment(), false, Const.PAYMENT_FRAGMENT, true);
                break;
            case R.id.btn_floating_bolt:
                // startActivity(new Intent(activity, Bolt_Msg_Fragment.class));
                activity.addFragment(new Bolt_Msg_Fragment(), false, Const.BOLT_FRAGMENT, true);
                break;*/

            case R.id.btn_another_ambulance_operator:{

                showAmbulanceOperator(mAmbulanceOperatorsMain);

            }
            break;

            case R.id.any_ambulance_operator_group:{

                isAnyAmbulanceOperatorGroup = true;
                mView_select.setVisibility(View.VISIBLE);
                mTv_ambulance_operator_notice.setText(getResources().getString(R.string.any_ambulance) + " " + getResources().getString(R.string.ambulance_operator_notice));

                if (ambulanceOperatorHorizontalAdapter != null){

                    ambulanceOperatorHorizontalAdapter.OnItemClicked(-1);
                    ambulanceOperatorHorizontalAdapter.notifyDataSetChanged();

                }


            }
            break;
        }

    }

    //    Show dialog AmbulanceOperator
    private void showAmbulanceOperator(final List<AmbulanceOperator> ambulanceOperators){

        if (getActivity() != null){

            final Dialog dialog = new Dialog(getActivity(), R.style.DialogSlideAnim);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(true);

            dialog.setContentView(R.layout.dialog_ambulance_operator_vertical);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            final RecyclerView ambulanceOperatorList = (RecyclerView) dialog.findViewById(R.id.list_schedule_vehicle);
            ambulanceOperatorList.setLayoutManager(linearLayoutManager);
            ambulanceOperatorList.setHasFixedSize(true);
            ambulanceOperatorList.setItemAnimator(new DefaultItemAnimator());

            if (ambulanceOperators.size() > 0){

                final AmbulanceOperatorVerticalAdaper ambulanceOperatorVerticalAdapter = new AmbulanceOperatorVerticalAdaper(getActivity(), ambulanceOperators);
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

    private void showSchedule(final String pickup_add, final ArrayList<AmbulanceOperator> ambulanceOperators) {
        sch_drop_latLng = null;

        if (getActivity() != null){

            dialogSchedule = new Dialog(getActivity(), R.style.DialogSlideAnim_leftright_Fullscreen);
            dialogSchedule.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogSchedule.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialogSchedule.setCancelable(true);

            dialogSchedule.setContentView(R.layout.dialog_patient_schedule);
//            final Spinner spn_pickup_time_date = (Spinner) dialogSchedule.findViewById(R.id.spn_pickup_time_date);
            et_sch_source_address = (AutoCompleteTextView) dialogSchedule.findViewById(R.id.actv_sch_source_address);
            et_sch_destination_address = (AutoCompleteTextView) dialogSchedule.findViewById(R.id.actv_sch_destination_address);
            mTv_ambulance_operator_notice = (TextView) dialogSchedule.findViewById(R.id.tv_ambulance_operator_notice);
            mBtn_another_ambulance_operator = (Button) dialogSchedule.findViewById(R.id.btn_another_ambulance_operator);
            mAny_ambulance_operator_group = (LinearLayout) dialogSchedule.findViewById(R.id.any_ambulance_operator_group);
            mView_select = (View) dialogSchedule.findViewById(R.id.view_select);
            cb_a_and_e = (CheckBox) dialogSchedule.findViewById(R.id.tv_a_and_e_value);
            cb_imh = (CheckBox) dialogSchedule.findViewById(R.id.tv_imh_value);
            cb_ferry_terminals = (CheckBox) dialogSchedule.findViewById(R.id.tv_ferry_terminals_value);
            cb_staircase = (CheckBox) dialogSchedule.findViewById(R.id.cb_staircase_value);
            cb_tarmac = (CheckBox) dialogSchedule.findViewById(R.id.tv_tarmac_value);
            edt_tv_house_unit_value = (EditText) dialogSchedule.findViewById(R.id.edt_house_unit_value);
            spn_family_member_value = (Spinner) dialogSchedule.findViewById(R.id.spn_family_member_value);
            spn_oxygen_tank = (Spinner) dialogSchedule.findViewById(R.id.spn_oxygen_tank_value);
            spn_pickup_type = (Spinner) dialogSchedule.findViewById(R.id.spn_pickup_type_value);
            spn_weight_value = (Spinner) dialogSchedule.findViewById(R.id.spn_weight_value);

            new PreferenceHelper(getActivity()).putRequestType("0");
            new PreferenceHelper(getActivity()).putAmbulance_name(getResources().getString(R.string.any_ambulance));
            mTv_ambulance_operator_notice.setSelected(true);
            mTv_ambulance_operator_notice.setText(new PreferenceHelper(getActivity()).getAmbulance_name() + " " + getResources().getString(R.string.ambulance_operator_notice));

            final WheelView wheel_view_ambulance_operator = (WheelView) dialogSchedule.findViewById(R.id.wheel_view_ambulance_operator);
            wheel_view_ambulance_operator.setTypeface(Typeface.SERIF);
            final List<String> ambulanceOperatorsWheelView = new ArrayList<>();
            ambulanceOperatorsWheelView.add(getResources().getString(R.string.any_ambulance));

            if (ambulanceOperators.size() > 0){

                for (int i = 0; i < ambulanceOperators.size(); i++){

                    ambulanceOperatorsWheelView.add(ambulanceOperators.get(i).getAmbulanceOperator());
                }

                wheel_view_ambulance_operator.setData(ambulanceOperatorsWheelView);
            }

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

                        isAnyAmbulanceOperatorGroup = true;
                        new PreferenceHelper(getActivity()).putRequestType("0");
                        new PreferenceHelper(getActivity()).putAmbulance_name(getResources().getString(R.string.any_ambulance));
                        mTv_ambulance_operator_notice.setText(new PreferenceHelper(getActivity()).getAmbulance_name() + " " + getResources().getString(R.string.ambulance_operator_notice));
                    }

                }

                @Override
                public void onWheelScrollStateChanged(int state) {

                }
            });

            //Set up Family meber value
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
                            familyMember = 1;
                        }
                        break;

                        case 2:{
                            familyMember = 2;
                        }
                        break;

                        default:{
                            familyMember = 0;
                        }
                        break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

//            Set up Weight
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

                            weight = 0;

                        }
                        break;

                        case 1:{

                            weight = 1;
                        }
                        break;

                        case 2:{

                            weight = 2;
                        }
                        break;

                        case 3:{

                            weight = 3;
                        }
                        break;

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

//            Set up Oxygen Tank adapter
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

                    oxygenTank = Integer.parseInt(oxygenTanks.get(position));
                    EbizworldUtils.appLogDebug("HaoLS", "Oxygen Tank " + oxygenTank);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

//            Set up Case Type adapter
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
            spn_pickup_type.setAdapter(caseTypeAdapter);
            spn_pickup_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    switch (position){

                        case 0:{

                            caseType = 1;
                        }
                        break;

                        case 1:{

                            caseType = 2;
                        }
                        break;

                        case 2:{

                            caseType = 3;
                        }
                        break;

                        case 3:{
                            caseType = 4;
                        }
                        break;

                        case 4:{

                            FragmentManager fragmentManager = ((AppCompatActivity)activity).getSupportFragmentManager();
                            HospitalDischargeOptionDialog hospitalDischargeOptionDialog = new HospitalDischargeOptionDialog();

                            if (mHospitalDischarge != null){

                                Bundle bundle = new Bundle();
                                bundle.putParcelable(Const.HOSPITAL_DISCHARGE_OPTION, mHospitalDischarge);
                                hospitalDischargeOptionDialog.setArguments(bundle);
                            }

                            hospitalDischargeOptionDialog.setCancelable(false);
                            hospitalDischargeOptionDialog.show(fragmentManager, Const.HOSPITAL_DISCHARGE_OPTION_DIALOGFRAGMENT);
                            hospitalDischargeOptionDialog.setHospitalDischargeCallback(new DialogFragmentCallback.HospitalDischargeCallback() {
                                @Override
                                public void onHospitalDischargeCallback(HospitalDischarge hospitalDischarge) {
                                    if (hospitalDischarge != null){

                                        mHospitalDischarge = hospitalDischarge;
                                        caseType = 5;
                                        EbizworldUtils.appLogDebug("HaoLS", "Case type: " + caseType);

                                    }
                                }

                                @Override
                                public void onHospitalDischargeCancel(Boolean isDismiss) {

                                    if (isDismiss == true){

                                        caseType = 1;
                                        spn_pickup_type.setSelection(0);
                                        EbizworldUtils.appLogDebug("HaoLS", "Case type: " + caseType);

                                    }

                                }
                            });

                        }
                        break;

                        case 5:{
                            caseType = 6;
                        }
                        break;

                        case 6:{
                            caseType = 7;
                        }
                        break;

                        case 7:{
                            caseType = 8;
                        }
                        break;

                        case 8:{
                            caseType = 9;
                        }
                        break;
                    }

                    EbizworldUtils.appLogDebug("HaoLS", "Case type " + caseType);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            /*et_sch_source_address.setText(pickup_add);*/
            et_sch_source_address.setDropDownBackgroundDrawable(new ColorDrawable(Color.WHITE));
            et_sch_destination_address.setDropDownBackgroundDrawable(new ColorDrawable(Color.WHITE));

            if (mAmbulanceOperator != null){

                mTv_ambulance_operator_notice.setSelected(true);
                mTv_ambulance_operator_notice.setText(mAmbulanceOperator.getAmbulanceOperator().toUpperCase() + " " + getResources().getString(R.string.ambulance_operator_notice));

            }

            TextView btn_sch_submit = (TextView) dialogSchedule.findViewById(R.id.btn_schedule_submit);

            final PlacesAutoCompleteAdapter placesadapter = new PlacesAutoCompleteAdapter(getActivity(),
                    R.layout.autocomplete_list_text);
            final PlacesAutoCompleteAdapter placesadapter2 = new PlacesAutoCompleteAdapter(getActivity(),
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
                    EbizworldUtils.hideKeyBoard(getActivity());
                    final String selectedSourcePlace = placesadapter.getItem(i);
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
                    EbizworldUtils.hideKeyBoard(getActivity());

                    final String selectedDestPlace = placesadapter2.getItem(i);

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

                    if (tv_time_date.getText().toString().length() == 0) {

                        Commonutils.showtoast(getResources().getString(R.string.txt_error_date_time), activity);

                    } else if (et_sch_destination_address.getText().toString().length() == 0) {

                        Commonutils.showtoast(getResources().getString(R.string.txt_destination_error), activity);

                    } else {

                        mPatientSchedule = getPatientSchedule(sch_drop_latLng,
                                                            sch_pic_latLng,
                                                            new PreferenceHelper(activity).getRequestType(),
                                                            datetime,
                                                            et_sch_source_address.getText().toString(),
                                                            et_sch_destination_address.getText().toString(),
                                                            cb_a_and_e,
                                                            cb_imh,
                                                            cb_ferry_terminals,
                                                            cb_staircase,
                                                            cb_tarmac,
                                                            edt_tv_house_unit_value.getText().toString().trim(),
                                                            familyMember,
                                                            weight,
                                                            oxygenTank,
                                                            caseType);

                        if (mPatientSchedule != null){

                            new BillingInfoAPI(activity, asyncTaskCompleteListener).getPatientScheduleBillingInfo(mPatientSchedule, Const.ServiceCode.BILLING_INFO);
//                            getBillingInfo(mPatientSchedule);

                        }


                    }


                }
            });

            tv_time_date = (TextView) dialogSchedule.findViewById(R.id.tv_pickup_time_date);
            tv_time_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatePicker();
                }
            });
            mBtn_another_ambulance_operator.setOnClickListener(this);
            mAny_ambulance_operator_group.setOnClickListener(this);

            dialogSchedule.show();
        }

    }


//    Get places auto complete
    private void getPlacesAutoComplete(Context context, AutoCompleteTextView autoCompleteTextView, PlacesAutoCompleteAdapter placesAutoCompleteAdapter){

        if (context != null){

            placesAutoCompleteAdapter = new PlacesAutoCompleteAdapter(context, R.layout.autocomplete_list_text);

            autoCompleteTextView.setAdapter(placesAutoCompleteAdapter);
        }
    }

    private void bookSchedulePatient(Schedule schedule, HospitalDischarge hospitalDischarge) {

        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        Commonutils.progressdialog_show(activity, "Requesting...");

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.REQUEST_LATER);
        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());

        map.put(Const.Params.SERVICE_TYPE, schedule.getOperatorID());

        map.put(Const.Params.S_ADDRESS, schedule.getS_address());
        map.put(Const.Params.D_ADDRESS, schedule.getD_address());
//        map.put(Const.Params.PICKUP_TYPE, String.valueOf(pickup_type));

        map.put(Const.Params.LATITUDE, schedule.getS_lat());
        map.put(Const.Params.LONGITUDE, schedule.getS_lng());

        map.put(Const.Params.D_LATITUDE, schedule.getD_lat());
        map.put(Const.Params.D_LONGITUDE, schedule.getD_lng());

        map.put(Const.Params.SCHEDULE_REQUEST_TIME, schedule.getRequest_date());

        map.put(Const.Params.REQ_STATUS_TYPE, "1");

        map.put(Const.Params.A_AND_E, String.valueOf(schedule.getA_and_e()));

        map.put(Const.Params.IMH, String.valueOf(schedule.getImh()));

        map.put(Const.Params.FERRY_TERMINALS, String.valueOf(schedule.getFerry_terminals()));

        map.put(Const.Params.STAIRCASE, String.valueOf(schedule.getStaircase()));

        map.put(Const.Params.TARMAC, String.valueOf(schedule.getTarmac()));

        map.put(Const.Params.FAMILY_MEMBER, String.valueOf(schedule.getFamilyMember()));
        map.put(Const.Params.HOUSE_UNIT, schedule.getHouseUnit());
        map.put(Const.Params.OXYGEN, String.valueOf(schedule.getOxygen_tank()));
        map.put(Const.Params.WEIGHT, String.valueOf(schedule.getWeight()));
        map.put(Const.Params.CASE_TYPE, String.valueOf(schedule.getCase_type()));

        if (schedule.getCase_type() == 5 && hospitalDischarge != null){

            map.put(Const.Params.SCHEDULE_PATIENT_NAME, hospitalDischarge.getPatientName());
            map.put(Const.Params.SCHEDULE_WARD_NUMBER, hospitalDischarge.getWardNumber());
            map.put(Const.Params.SCHEDULE_HOSPITAL, hospitalDischarge.getHospital());
            map.put(Const.Params.SCHEDULE_ROOM_NUMBER, hospitalDischarge.getRoomNumber());
            map.put(Const.Params.SCHEDULE_BED_NUMBER, hospitalDischarge.getBedNumber());
            map.put(Const.Params.TIME_OF_DISCHARGE, hospitalDischarge.getTimeOfDischarge());

        }

        EbizworldUtils.appLogDebug("HaoLS", "Create schedule: " + map.toString());

        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.REQUEST_LATER, this);

    }

    private void showDialogBillingInfo(String staircase, String tarmac, String weight, String oxygen, String caseType, String total, String currency){

        dialogBillingInfo = new Dialog(activity, R.style.DialogSlideAnim_leftright_Fullscreen);
        dialogBillingInfo.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogBillingInfo.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogBillingInfo.setCancelable(true);
        dialogBillingInfo.setContentView(R.layout.fragment_billing_info);

        final TextView tv_billing_info_notice = (TextView) dialogBillingInfo.findViewById(R.id.tv_billing_info_notice);
        final TextView tv_billing_info_total_price = (TextView) dialogBillingInfo.findViewById(R.id.tv_billing_info_total_price);
       // final TextView tv_billing_info_staircase_value = (TextView) dialogBillingInfo.findViewById(R.id.tv_billing_info_staircase_value);
       // final TextView tv_billing_info_tarmac_value = (TextView) dialogBillingInfo.findViewById(R.id.tv_billing_info_tarmac_value);
       // final TextView tv_billing_info_weight_value = (TextView) dialogBillingInfo.findViewById(R.id.tv_billing_info_weight_value);
       // final TextView tv_billing_info_oxygen_tank_value = (TextView) dialogBillingInfo.findViewById(R.id.tv_billing_info_oxygen_tank_value);
        final TextView tv_billing_info_pickup_type_value = (TextView) dialogBillingInfo.findViewById(R.id.tv_billing_info_pickup_type_value);
        final TextView tv_billing_info_confirm = (TextView) dialogBillingInfo.findViewById(R.id.tv_billing_info_confirm);
        final TextView tv_billing_info_deny = (TextView) dialogBillingInfo.findViewById(R.id.tv_billing_info_deny);
        final LinearLayout billing_info_payment_group = (LinearLayout) dialogBillingInfo.findViewById(R.id.billing_info_payment_group);

        billing_info_payment_group.setVisibility(View.GONE);
        tv_billing_info_notice.setVisibility(View.GONE);

        tv_billing_info_total_price.setText(currency + " " + total);
       // tv_billing_info_staircase_value.setText(currency + " " + staircase);
       // tv_billing_info_tarmac_value.setText(currency + " " + tarmac);
       // tv_billing_info_weight_value.setText(currency + " " + weight);
       // tv_billing_info_oxygen_tank_value.setText(currency + " " + oxygen);
        tv_billing_info_pickup_type_value.setText(currency + " " + caseType);

        tv_billing_info_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mPatientSchedule != null){

                    bookSchedulePatient(mPatientSchedule, mHospitalDischarge);

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

    private void getBillingInfo(Schedule schedule){

        if (!EbizworldUtils.isNetworkAvailable(getActivity())) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put(Const.Params.URL, Const.ServiceType.BILLING_INFO);

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

        EbizworldUtils.appLogDebug("HaoLS", "Get Billing info for Patient's schedule: " + hashMap.toString());

        new VolleyRequester(activity, Const.POST, hashMap, Const.ServiceCode.BILLING_INFO, this);
    }

    private Schedule getPatientSchedule(LatLng dropLatLng,
                                        LatLng pickLatLng,
                                        String type,
                                        String dateTime,
                                        String s_address,
                                        String d_address,
                                        CheckBox cb_a_and_e,
                                        CheckBox cb_imh,
                                        CheckBox cb_ferry_terminals,
                                        CheckBox cb_staircase,
                                        CheckBox cb_tarmac,
                                        String houseUnit,
                                        int familyMember,
                                        int weight,
                                        int oxygenTank,
                                        int case_type) {

        Schedule patientSchedule = new Schedule();

        patientSchedule.setOperatorID(type);
        patientSchedule.setS_address(s_address);
        patientSchedule.setD_address(d_address);
        patientSchedule.setS_lat(String.valueOf(pickLatLng.latitude));
        patientSchedule.setS_lng(String.valueOf(pickLatLng.longitude));
        patientSchedule.setD_lat(String.valueOf(dropLatLng.latitude));
        patientSchedule.setD_lng(String.valueOf(dropLatLng.longitude));
        patientSchedule.setRequest_date(dateTime);
        patientSchedule.setStatus_request("1");

        if (cb_a_and_e.isChecked()){

            patientSchedule.setA_and_e(1);
        }else {

            patientSchedule.setA_and_e(0);
        }

        if (cb_imh.isChecked()){

            patientSchedule.setImh(1);
        }else {

            patientSchedule.setImh(0);
        }

        if (cb_ferry_terminals.isChecked()){

            patientSchedule.setFerry_terminals(1);
        }else {

            patientSchedule.setFerry_terminals(0);
        }

        if (cb_staircase.isChecked()){

            patientSchedule.setStaircase(1);
        }else {

            patientSchedule.setStaircase(0);
        }

        if (cb_tarmac.isChecked()){

            patientSchedule.setTarmac(1);
        }else {

            patientSchedule.setTarmac(0);
        }

        patientSchedule.setFamilyMember(familyMember);
        patientSchedule.setHouseUnit(houseUnit);
        patientSchedule.setOxygen_tank(oxygenTank);
        patientSchedule.setWeight(weight);
        patientSchedule.setCase_type(case_type);

        return patientSchedule;
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

                            date = Integer.toString(year) + "-"
                                    + Integer.toString(monthOfYear + 1) + "-"
                                    + Integer.toString(dayOfMonth);

                            datetime = date;

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

    public void TimePicker() {

        //Log.d("pavan", "in time picker");

        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 30);
        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
        int mMinute = calendar.get(Calendar.MINUTE);


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
                            int hour = hourOfDay;
                            int min = minute;

                            if (hourOfDay > 12){

                                hour -= 12;
                                timeSet = "PM";
                            }else if (hourOfDay == 0){

                                timeSet = "AM";

                            }else if (hourOfDay == 12){

                                timeSet = "PM";
                            }else {

                                timeSet = "AM";

                            }

                            if (minute < 10){

                                time = String.valueOf(hourOfDay)+ ":0" +
                                        String.valueOf(min) + ":" + "00 " + timeSet;

                            }else {

                                time = String.valueOf(hourOfDay)+ ":" +
                                        String.valueOf(min) + ":" + "00 " + timeSet;

                            }

                            pickupDateTime = date + " " + time;

                            datetime = datetime.concat(" "
                                    + Integer.toString(hourOfDay) + ":"
                                    + Integer.toString(minute) + ":" + "00");

                            tv_time_date.setText(pickupDateTime);

                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            calendar.set(Calendar.MINUTE, minute);

                            /*AlarmUtil.scheduleNotification(getActivity(), calendar);*/
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

    @Override
    public void onMethodCallback(String id, String taxitype, String taxi_price_distance, String taxi_price_min, String taxiimage, String taxi_seats, String basefare) {
        ambulance_type = id;
        if (null != sch_pic_latLng && null != sch_drop_latLng) {
            findDistanceAndTime(sch_pic_latLng, sch_drop_latLng);
            /*showfareestimate(taxitype, taxi_price_distance, taxi_price_min, taxiimage, taxi_seats, basefare);*/
        } else {
            EbizworldUtils.showShortToast(getResources().getString(R.string.txt_drop_pick_error), activity);
        }

    }

    @Override
    public void onMapClick(LatLng latLng) {

        if (null != my_marker) {
            my_marker.remove();
        }

        MarkerOptions pickup_opt = new MarkerOptions();
        pickup_opt.position(latLng);
        pickup_opt.icon(BitmapDescriptorFactory
                .fromBitmap(getMarkerBitmapFromView("---")));

        if (null != mGoogleMap) {
            my_marker = mGoogleMap.addMarker(pickup_opt);
            markermap.put(my_marker, -1);
            mMarkersMap.add(my_marker); //Testing
            BaseFragment.pic_latlan = latLng;
            getCompleteAddress(latLng.latitude, latLng.longitude);
        }


    }

    @Override
    public void onCameraMove() {

    }

    public static class SpacesItemDecoration extends RecyclerView.ItemDecoration {
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


    @Override
    public void onResume() {
        super.onResume();


        activity.currentFragment = Const.HOME_MAP_FRAGMENT;
        //setUpMap();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();
        stopCheckingforproviders();
    }

    private void startgetProvider() {
        startCheckProviderTimer();
        //Log.e("mahi", "req_id" + new PreferenceHelper(activity).getRequestId());
    }

    private void startCheckProviderTimer() {
        providerhandler.postDelayed(runnable, 2000);
    }

    private void stopCheckingforproviders() {
        if (providerhandler != null) {
            providerhandler.removeCallbacks(runnable);

            // Log.d("mahi", "stop provider handler");
        }
    }

    Runnable runnable = new Runnable() {
        public void run() {
            getAllProviders(latlong);
            checkreqstatus();
            providerhandler.postDelayed(this, 5000);
        }
    };

    private void checkreqstatus() {
        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        HashMap<String, String> map = new HashMap<String, String>();

        if (new PreferenceHelper(getActivity()).getLoginType().equals(Const.PatientService.PATIENT)){

            map.put(Const.Params.URL, Const.ServiceType.CHECKREQUEST_STATUS);

        }else if (new PreferenceHelper(getActivity()).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)){

            map.put(Const.Params.URL, Const.NursingHomeService.REQUEST_STATUS_CHECK_URL);

        }else if (new PreferenceHelper(getActivity()).getLoginType().equals(Const.HospitalService.HOSPITAL)){

            map.put(Const.Params.URL, Const.HospitalService.CHECK_REQUEST_STATUS_URL);

        }

        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());


        Log.d("HaoLS", "Check request status " + map.toString());
        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.CHECKREQUEST_STATUS,
                this);
    }

    private void getLatLngFromAddress(String selectedSourcePlace) {
        if (!EbizworldUtils.isNetworkAvailable(activity)) {
            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.LOCATION_API_BASE + selectedSourcePlace + "&key=" + Const.GOOGLE_API_KEY);

        Log.d("HaoLS", "map for s_loc" + map);
        new VolleyRequester(activity, Const.GET, map, Const.ServiceCode.LOCATION_API_BASE_SOURCE, this);
    }

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("mahi", "on destory view is calling");
        SupportMapFragment f = (SupportMapFragment) getFragmentManager()
                .findFragmentById(R.id.home_map);
        if (f != null) {
            try {
                getFragmentManager().beginTransaction().remove(f).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

          /*TO clear all views */
        ViewGroup mContainer = (ViewGroup) getActivity().findViewById(R.id.content_frame);
        mContainer.removeAllViews();

        mGoogleMap = null;

    }

    @Override
    public void onDestroy() {
        stopCheckingforproviders();
        super.onDestroy();

    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {

        switch (serviceCode) {

            case Const.ServiceCode.ADDRESS_API_BASE:
                if (null != response) {
                    try {
                        JSONObject job = new JSONObject(response);
                        JSONArray jarray = job.optJSONArray("results");
                        JSONObject locObj = jarray.getJSONObject(0);
                        pickup_add = locObj.optString("formatted_address");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                break;
            case Const.ServiceCode.GOOGLE_ADDRESS_API:
                if (null != response) {
                    try {
                        JSONObject job = new JSONObject(response);
                        JSONArray jarray = job.optJSONArray("results");
                        JSONObject locObj = jarray.getJSONObject(0);
                        pickup_add = locObj.optString("formatted_address");
                        tv_current_location.setText(pickup_add);

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (null != my_marker && null != mGoogleMap)
                                    my_marker.setIcon((BitmapDescriptorFactory
                                            .fromBitmap(getMarkerBitmapFromView(pickup_add))));
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case Const.ServiceCode.LOCATION_API_BASE_SOURCE:
                if (null != response) {
                    EbizworldUtils.appLogDebug("HaoLS", "Location API base source " + response);
                    try {
                        JSONObject job = new JSONObject(response);
                        JSONArray jarray = job.optJSONArray("results");
                        JSONObject locObj = jarray.getJSONObject(0);
                        JSONObject geometryOBJ = locObj.optJSONObject("geometry");
                        JSONObject locationOBJ = geometryOBJ.optJSONObject("location");
                        double lat = locationOBJ.getDouble("lat");
                        double lan = locationOBJ.getDouble("lng");
                        sch_pic_latLng = new LatLng(lat, lan);


                    } catch (JSONException e) {
                        e.printStackTrace();
                        EbizworldUtils.appLogError("HaoLS", "Location API base source: " + e.toString());
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
                        sch_drop_latLng = new LatLng(lat, lan);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case Const.ServiceCode.HOMEAMBULANCE_TYPE:

                if (response != null){

                    EbizworldUtils.appLogDebug("HaoLS", "homeambulance type1111 " + response);

                    try {
                        JSONObject job = new JSONObject(response);

                        if (job.getString("success").equals("true")) {

                            EbizworldUtils.appLogDebug("HaoLS", "Get homeambulance type " + job.toString());

                            mAmbulanceOperatorsMain.clear();
                            JSONArray jarray = job.getJSONArray("operator");
                            if (jarray.length() > 0) {
                                for (int i = 0; i < jarray.length(); i++) {

                                    EbizworldUtils.appLogDebug("HaoLS", "Get homeambulance type index " + i);

                                    JSONObject jsonObject = jarray.getJSONObject(i);
                                    AmbulanceOperator ambulanceOperator = new AmbulanceOperator();
                                    ambulanceOperator.setCurrencey_unit(job.optString("currency"));
                                    ambulanceOperator.setId(jsonObject.getString("id"));
                                    ambulanceOperator.setAmbulanceCost(jsonObject.getString("min_fare"));
                                    ambulanceOperator.setAmbulanceImage(jsonObject.getString("picture"));
                                    ambulanceOperator.setAmbulanceOperator(jsonObject.getString("name"));
                                    ambulanceOperator.setAmbulance_price_min(jsonObject.getString("price_per_min"));
                                    ambulanceOperator.setAmbulance_price_distance(jsonObject.getString("price_per_unit_distance"));
                                    ambulanceOperator.setAmbulanceSeats(jsonObject.getString("number_seat"));
                                    mAmbulanceOperatorsMain.add(ambulanceOperator);
                                }

                                /*if (mAmbulanceOperatorsMain != null && mAmbulanceOperatorsMain.size() > 0) {
                                    new PreferenceHelper(activity).putRequestType(mAmbulanceOperatorsMain.get(0).getId());
                                    if (null != ambulanceOperatorHorizontalAdapter) {
                                        ambulanceOperatorHorizontalAdapter.notifyDataSetChanged();
                                    }

                                }*/

                                if (mAmbulanceOperatorsMain.size() > 0 && mAmbulanceOperatorsMain != null){

                                    EbizworldUtils.appLogDebug("HaoLS", "Getting copy to mAmbulanceOperatorsTemp");

                                    mAmbulanceOperatorsTemp.clear();

                                    for (int i = 0; i < 3; i++){

                                        mAmbulanceOperatorsTemp.add(mAmbulanceOperatorsMain.get(i));

                                    }

                                    if (mAmbulanceOperatorsTemp.size() > 0){

                                        /*if (null != ambulanceOperatorHorizontalAdapter) {
                                            ambulanceOperatorHorizontalAdapter.notifyDataSetChanged();
                                        }*/

                                        mAmbulanceOperator = new AmbulanceOperator();
                                        mAmbulanceOperator = mAmbulanceOperatorsTemp.get(0);
                                    }
                                }

                            }
                        }else {

                            EbizworldUtils.appLogDebug("HaoLS", "homeambulance type failed ");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        EbizworldUtils.appLogDebug("HaoLS", "homeambulance type failed " + e.toString());
                    }

                }
                break;

            case  Const.ServiceCode.BILLING_INFO:{
                EbizworldUtils.appLogInfo("HaoLS", "Patient's schedule billing info: " + response);

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
                        EbizworldUtils.appLogError("HaoLS", "Patient's schedule billing info failed: " + e.toString());

                    }

                }
            }
            break;

            case Const.ServiceCode.REQUEST_LATER:

                if (response != null) {

                    try {

                        JSONObject job = new JSONObject(response);

                        if (job.getString("success").equals("true")) {

                            Log.d("HaoLS", "Create schedule succeeded" + response);

                            Commonutils.progressdialog_hide();
                            Commonutils.showtoast(getResources().getString(R.string.txt_trip_schedule_success), activity);
                            new PreferenceHelper(getActivity()).putRequestType("0");
                            new PreferenceHelper(getActivity()).putAmbulance_name(getResources().getString(R.string.any_ambulance));

                            if (dialogSchedule != null && dialogSchedule.isShowing()){

                                dialogSchedule.dismiss();

                            }

                            if (dialogBillingInfo != null && dialogBillingInfo.isShowing()){

                                dialogBillingInfo.dismiss();

                            }

                        } else {

                            Commonutils.progressdialog_hide();
                            String error = job.getString("error");
                            Commonutils.showtoast(error, activity);

                        }

                    } catch (JSONException e) {

                        e.printStackTrace();
                        EbizworldUtils.appLogDebug("HaoLS", "Request schedule failed " + e.toString());

                    }
                }

                break;
            case Const.ServiceCode.CHECKREQUEST_STATUS:

                EbizworldUtils.appLogInfo("HaoLS", "check req status " + response);

                if (response != null) {

                    Bundle bundle = new Bundle();
                    RequestDetail requestDetail = null;

                    if (new PreferenceHelper(activity).getLoginType().equals(Const.PatientService.PATIENT)){

                        requestDetail = new ParseContent(activity).parseRequestStatusNormal(response);

                    }else if (new PreferenceHelper(activity).getLoginType().equals(Const.NursingHomeService.NURSING_HOME) ||
                            new PreferenceHelper(activity).getLoginType().equals(Const.HospitalService.HOSPITAL)){

                        requestDetail = new ParseContent(activity).parseRequestStatusSchedule(response);
                    }
                    /*List<RequestDetail> requestDetails = new ParseContent(activity).*/
                    TravelMapFragment travelfragment = new TravelMapFragment();
                    if (requestDetail == null) {
                        return;
                    }

                    Log.d("Status", "onTaskCompleted:" + requestDetail.getTripStatus());

                    switch (requestDetail.getTripStatus()) {
                        case Const.NO_REQUEST:
                            new PreferenceHelper(activity).clearRequestData();
                            // startgetProvider();


                            break;

                        case Const.IS_ACCEPTED:{
                            bundle.putSerializable(Const.REQUEST_DETAIL,
                                    requestDetail);
                            bundle.putInt(Const.DRIVER_STATUS,
                                    Const.IS_ACCEPTED);
                            if (!activity.currentFragment.equals(Const.TRAVEL_MAP_FRAGMENT)) {
                                stopCheckingforproviders();

                                travelfragment.setArguments(bundle);
                                activity.addFragment(travelfragment, false, Const.TRAVEL_MAP_FRAGMENT,
                                        true);

                            }


                            BaseFragment.drop_latlan = null;
                            BaseFragment.pic_latlan = null;
                            BaseFragment.s_address = "";
                            BaseFragment.d_address = "";
                        }
                        break;

                        case Const.IS_DRIVER_DEPARTED:{
                            bundle.putSerializable(Const.REQUEST_DETAIL,
                                    requestDetail);
                            bundle.putInt(Const.DRIVER_STATUS,
                                    Const.IS_ACCEPTED);
                            if (!activity.currentFragment.equals(Const.TRAVEL_MAP_FRAGMENT)) {
                                stopCheckingforproviders();

                                travelfragment.setArguments(bundle);
                                activity.addFragment(travelfragment, false, Const.TRAVEL_MAP_FRAGMENT,
                                        true);

                            }


                            BaseFragment.drop_latlan = null;
                            BaseFragment.pic_latlan = null;
                            BaseFragment.s_address = "";
                            BaseFragment.d_address = "";
                        }
                        break;

                        case Const.IS_DRIVER_ARRIVED:{
                            bundle.putSerializable(Const.REQUEST_DETAIL,
                                    requestDetail);
                            bundle.putInt(Const.DRIVER_STATUS,
                                    Const.IS_ACCEPTED);
                            if (!activity.currentFragment.equals(Const.TRAVEL_MAP_FRAGMENT)) {
                                stopCheckingforproviders();

                                travelfragment.setArguments(bundle);
                                activity.addFragment(travelfragment, false, Const.TRAVEL_MAP_FRAGMENT,
                                        true);

                            }


                            BaseFragment.drop_latlan = null;
                            BaseFragment.pic_latlan = null;
                            BaseFragment.s_address = "";
                            BaseFragment.d_address = "";
                        }
                        break;

                        case Const.IS_DRIVER_TRIP_STARTED:{

                            bundle.putSerializable(Const.REQUEST_DETAIL,
                                    requestDetail);
                            bundle.putInt(Const.DRIVER_STATUS,
                                    Const.IS_ACCEPTED);
                            if (!activity.currentFragment.equals(Const.TRAVEL_MAP_FRAGMENT)) {
                                stopCheckingforproviders();

                                travelfragment.setArguments(bundle);
                                activity.addFragment(travelfragment, false, Const.TRAVEL_MAP_FRAGMENT,
                                        true);

                            }


                            BaseFragment.drop_latlan = null;
                            BaseFragment.pic_latlan = null;
                            BaseFragment.s_address = "";
                            BaseFragment.d_address = "";

                        }
                        break;

                        case Const.IS_DRIVER_TRIP_ENDED:{

                            if (!activity.currentFragment.equals(Const.BILLING_INFO_FRAGMENT) && !activity.isFinishing()){

                                stopCheckingforproviders();

                                BillingInfoFragment billingInfoFragment = new BillingInfoFragment();
                                billingInfoFragment.setArguments(bundle);

                                activity.addFragment(billingInfoFragment, false, Const.BILLING_INFO_FRAGMENT, true);

                            }
                        }
                        break;

                        case Const.IS_DRIVER_RATED:{

                            bundle.putSerializable(Const.REQUEST_DETAIL,
                                    requestDetail);
                            bundle.putInt(Const.DRIVER_STATUS,
                                    Const.IS_ACCEPTED);
                            if (!activity.currentFragment.equals(Const.TRAVEL_MAP_FRAGMENT)) {
                                stopCheckingforproviders();

                                travelfragment.setArguments(bundle);
                                activity.addFragment(travelfragment, false, Const.TRAVEL_MAP_FRAGMENT,
                                        true);

                            }


                            BaseFragment.drop_latlan = null;
                            BaseFragment.pic_latlan = null;
                            BaseFragment.s_address = "";
                            BaseFragment.d_address = "";
                        }
                        break;

                        default:
                            break;

                    }

                }
                break;

            case Const.ServiceCode.GET_PROVIDERS:
                EbizworldUtils.appLogInfo("HaoLS", "providers: " + response);

                if (response != null) {
                    try {
                        if (mGoogleMap != null) {

                            mGoogleMap.getUiSettings().setScrollGesturesEnabled(true);

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
                            /*String error_code = job.optString("error_code");
                            if (error_code.equals("104")) {
                                EbizworldUtils.showShortToast("You have logged in other device!", activity);
                                new PreferenceHelper(activity).Logout();
                                startActivity(new Intent(activity, WelcomeActivity.class));
                                activity.finish();

                            }*/

                            EbizworldUtils.appLogDebug("HaoLS", "Get providers failed");
                        }


                        if (driverslatlngs.size() > 0) {

                            /*if (mGoogleMap != null) {
                                mGoogleMap.clear();
                            }*/
                            for (Marker marker : markers) {
                                marker.remove();
                            }
                            markers.clear();

                            for (int i = 0; i < driverslatlngs.size(); i++) {
                           /* CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                                    driverslatlngs.get(i), 15);*/
                                // Log.d("mahi","markers size"+driverslatlngs.get(i).toString());

                                final MarkerOptions currentOption = new MarkerOptions();
                                currentOption.position(driverslatlngs.get(i).getLatlan());
                                currentOption.title(driverslatlngs.get(i).getDriver_name());
                                currentOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance_car));
                                if (mGoogleMap != null) {
                                    final Marker[] driver_marker = new Marker[1];
                                    final int finalI = i;

                                    activity.runOnUiThread(new Runnable() {
                                        public void run() {
                                            if (null == driver_marker[0]) {

                                                //driver_marker[0].remove();
                                                driver_marker[0] = mGoogleMap.addMarker(currentOption);

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
            case 101:

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
                        //Log.d("mahi", "time and dis" + dur + " " + dis);
                        double trip_dis = Integer.valueOf(dis) * 0.001;
                        getTypes(String.valueOf(trip_dis), dur);


                     /*   et_clientlocation.setText(destinationObject);
                        et_doctorlocation.setText(sourceObject);
                        et_distance.setText("Distance:" + " " + distance);
                        et_eta.setText("ETA:" + " " + duration);*/


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            case Const.ServiceCode.GOOGLE_MATRIX:
                // Log.d("mahi", "google distance api" + response);
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
                        // Log.d("mahi", "time and dis" + dur + " " + dis);
                        double trip_dis = Integer.valueOf(dis) * 0.001;
                        getFare(String.valueOf(trip_dis), dur, ambulance_type);
                        tv_total_dis.setText(distance);

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
                Log.d("mahi", "estimate fare" + response);

                if (response != null) {
                    try {
                        JSONObject job1 = new JSONObject(response);
                        if (job1.getString("success").equals("true")) {
                            String fare = job1.getString("estimated_fare");
                            ambulance_price = job1.getString("ambulance_price");
                            base_price = job1.optString("base_price");
                            min_price = job1.optString("min_fare");
                            booking_fee = job1.optString("booking_fee");
                            currency = job1.optString("currency");
                            distance_unit = job1.optString("distance_unit");
                            tv_estimate_fare.setVisibility(View.VISIBLE);
                            tv_estimate_fare.setText(currency + fare);
                            if (pbfareProgress != null) {
                                pbfareProgress.setVisibility(View.GONE);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                break;
            case Const.ServiceCode.AMBULANCE_OPERATOR:
                Log.d("HaoLS", "Ambulance operator" + response);

                if (response != null) {
                    try {
                        JSONObject job = new JSONObject(response);

                        if (job.getString("success").equals("true")) {

                            mAmbulanceOperatorsMain.clear();

                            JSONArray jarray = job.getJSONArray("operator");

                            if (jarray.length() > 0) {
                                for (int i = 0; i < jarray.length(); i++) {
                                    JSONObject taxiobj = jarray.getJSONObject(i);
                                    AmbulanceOperator type = new AmbulanceOperator();
                                    type.setCurrencey_unit(job.optString("currency"));
                                    type.setId(taxiobj.getString("id"));
                                    type.setAmbulanceCost(taxiobj.getString("estimated_fare"));
                                    type.setAmbulanceImage(taxiobj.getString("picture"));
                                    type.setAmbulanceOperator(taxiobj.getString("name"));
                                    type.setAmbulance_price_min(taxiobj.getString("price_per_min"));
                                    type.setAmbulance_price_distance(taxiobj.getString("price_per_unit_distance"));
                                    type.setAmbulanceSeats(taxiobj.getString("number_seat"));
                                    type.setBasefare(taxiobj.optString("min_fare"));
                                    mAmbulanceOperatorsMain.add(type);
                                }

                                if (mAmbulanceOperatorsMain != null) {
                                    // ambulanceOperatorHorizontalAdapter = new AmbulanceOperatorHorizontalAdapter(activity, mAmbulanceOperatorsMain, this);
                                    ambulanceOperatorHorizontalAdapter.notifyDataSetChanged();
                                }


                            }

                        } else {

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
                break;

            case Const.ServiceCode.ADVERTISEMENTS:
                EbizworldUtils.appLogDebug(TAG, "addsListResponse " + response);
                try {
                    JSONObject job1 = new JSONObject(response);
                    if (job1.getString("success").equals("true")) {
                        JSONArray jsonArray = job1.optJSONArray("data");
                        if (null != adsLists) {
                            adsLists.clear();
                        }
                        if (null != jsonArray && jsonArray.length() > 0) {
                            adsLists = new ParseContent(activity).parseAdsList(jsonArray);
                            if (adsLists != null) {
                                adsAdapter = new AdsAdapter(adsLists, activity);
                                //Adding adapter to Listview
                                adsRecyclerView.setAdapter(adsAdapter);
                            }
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


    private void getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.ADDRESS_API_BASE + LATITUDE + "," + LONGITUDE + "&key=" + Const.GOOGLE_API_KEY);

        Log.d("mahi", "map for address" + map);
        new VolleyRequester(activity, Const.GET, map, Const.ServiceCode.ADDRESS_API_BASE, this);
    }

    private void getCompleteAddress(double LATITUDE, double LONGITUDE) {
        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.ADDRESS_API_BASE + LATITUDE + "," + LONGITUDE + "&key=" + Const.GOOGLE_API_KEY);

        Log.d("mahi", "map for address" + map);
        new VolleyRequester(activity, Const.GET, map, Const.ServiceCode.GOOGLE_ADDRESS_API, this);
    }


    private Bitmap getMarkerBitmapFromView(String place) {
        View customMarkerView = ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.info_window_pickup, null);
        TextView markertext = (TextView) customMarkerView.findViewById(R.id.txt_pickup_location);

        markertext.setText(place);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                final Place place = PlaceAutocomplete.getPlace(getActivity(), data);


                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!d_click) {
                            et_sch_source_address.setText(place.getAddress());
                            sch_pic_latLng = place.getLatLng();
                            if (null != sch_drop_latLng && null != sch_pic_latLng) {
                                findDistanceAndTimeforTypes(sch_pic_latLng, sch_drop_latLng);
                            }

                        } else {
                            et_sch_destination_address.setText(place.getAddress());
                            sch_drop_latLng = place.getLatLng();
                            findDistanceAndTimeforTypes(sch_pic_latLng, sch_drop_latLng);
                        }

                    }
                });

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                // TODO: Handle the error.
                Log.i("mahi", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private void findDistanceAndTimeforTypes(LatLng pic_latlan, LatLng drop_latlan) {
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
        Log.e("mahi", "distance api" + map);
        new VolleyRequester(activity, Const.GET, map, 101, this);
    }

    private void getTypes(String dis, String dur) {
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

        Log.d("mahi", map.toString());
        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.AMBULANCE_OPERATOR,
                this);
    }

    private void findDistanceAndTime(LatLng s_latlan, LatLng d_latlan) {
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
        Log.e("mahi", "distance api" + map);
        new VolleyRequester(activity, Const.GET, map, Const.ServiceCode.GOOGLE_MATRIX, this);
    }


    private void showfareestimate(String taxitype, final String taxi_price_distance, final String taxi_price_min, String taxiimage, String taxi_seats, final String taxi_cost) {


        final Dialog faredialog = new Dialog(activity, R.style.DialogSlideAnim);
        faredialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        faredialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        faredialog.setCancelable(false);
        faredialog.setContentView(R.layout.fare_popup);
        ImageView type_img = (ImageView) faredialog.findViewById(R.id.fare_taxi_img);
        TextView tv_fare_taxi_name = (TextView) faredialog.findViewById(R.id.tv_fare_taxi_name);
        tv_estimate_fare = (TextView) faredialog.findViewById(R.id.tv_estimate_fare);
        tv_total_dis = (TextView) faredialog.findViewById(R.id.tv_total_dis);
        pbfareProgress = (ProgressBar) faredialog.findViewById(R.id.pbfareProgress);
        //TextView tv_cost_dis_fare = (TextView) faredialog.findViewById(R.id.tv_cost_dis_fare);
        TextView tv_total_capcity = (TextView) faredialog.findViewById(R.id.tv_total_capcity);
        TextView fare_done = (TextView) faredialog.findViewById(R.id.fare_done);
        ImageView btn_info = (ImageView) faredialog.findViewById(R.id.btn_info);



        /*Picasso.get().load(taxiimage).error(R.drawable.frontal_ambulance_cab).into(type_img);*/

        Glide.with(activity)
                .load(taxiimage)
                .apply(new RequestOptions().error(R.drawable.ambulance_car))
                .into(type_img);

        tv_fare_taxi_name.setText(taxitype);
        tv_total_capcity.setText("1-" + taxi_seats);

        btn_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showfarebreakdown(base_price, taxi_price_distance, taxi_price_min, min_price, booking_fee, currency, distance_unit);
            }
        });
        fare_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                faredialog.dismiss();
            }
        });


        faredialog.show();
    }

    private void showfarebreakdown(String base_price, String taxi_price_distance, String taxi_price_min, String min_price, String booking_fee, String currency, String distance_unit) {
        final Dialog farebreak = new Dialog(activity, R.style.DialogSlideAnim_leftright_Fullscreen);
        farebreak.requestWindowFeature(Window.FEATURE_NO_TITLE);
        farebreak.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent_black)));
        farebreak.setCancelable(true);
        farebreak.setContentView(R.layout.fare_breakdown);
        TextView tv_dis_title = (TextView) farebreak.findViewById(R.id.tv_dis_title);
        TextView tv_base_fare = (TextView) farebreak.findViewById(R.id.tv__history_detail_base_fare);
        TextView tv_min_fare = (TextView) farebreak.findViewById(R.id.tv_history_detail_min_fare);
        TextView tv_per_min_cost = (TextView) farebreak.findViewById(R.id.tv_per_min_cost);
        TextView tv_per_km_price = (TextView) farebreak.findViewById(R.id.tv_per_km_price);
        TextView tv_service_tax_price = (TextView) farebreak.findViewById(R.id.tv_history_detail_service_tax_price);
        TextView tv_booking_price = (TextView) farebreak.findViewById(R.id.tv_history_detail_booking_price);

        tv_base_fare.setText(currency + base_price);
        tv_booking_price.setText(currency + booking_fee);
        tv_min_fare.setText(currency + min_price);
        tv_per_min_cost.setText(currency + taxi_price_min);
        tv_per_km_price.setText(currency + taxi_price_distance);
        tv_service_tax_price.setText(currency + ambulance_price);
        ImageView close_popup = (ImageView) farebreak.findViewById(R.id.close_popup);
        tv_dis_title.setText(getResources().getString(R.string.txt_per) + " " + distance_unit);
        close_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                farebreak.cancel();
            }
        });


        farebreak.show();

    }

    private void getFare(String distance, String duration, String service_id) {
        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.FARE_CALCULATION);
        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());
        map.put(Const.Params.DISTANCE, distance);
        map.put(Const.Params.TIME, duration);
        map.put(Const.Params.AMBULANCE_TYPE, service_id);

        Log.d("mahi", map.toString());
        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.FARE_CALCULATION,
                this);
    }


}
