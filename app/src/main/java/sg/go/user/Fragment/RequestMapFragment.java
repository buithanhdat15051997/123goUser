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
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.simpleratingbar.SimpleRatingBar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.zyyoona7.wheel.WheelView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import sg.go.user.Adapter.AmbulanceOperatorHorizontalAdapter;
import sg.go.user.Adapter.AmbulanceOperatorVerticalAdaper;
import sg.go.user.Adapter.PaymentModeAdapter;
import sg.go.user.Adapter.SimpleSpinnerAdapter;
import sg.go.user.Dialog.HospitalDischargeWarningDialog;
import sg.go.user.HttpRequester.VolleyRequester;
import sg.go.user.Interface.AdapterCallback;
import sg.go.user.Interface.AsyncTaskCompleteListener;
import sg.go.user.Interface.DialogFragmentCallback;
import sg.go.user.Location.LocationHelper;
import sg.go.user.Models.AmbulanceOperator;
import sg.go.user.Models.NearByDrivers;
import sg.go.user.Models.Payments;
import sg.go.user.Models.RequestOptional;
import sg.go.user.R;
import sg.go.user.Utils.Commonutils;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.EbizworldUtils;
import sg.go.user.Utils.ItemClickSupport;
import sg.go.user.Utils.PreferenceHelper;
import sg.go.user.Utils.RecyclerLongPressClickListener;


/**
 * Created by user on 2/3/2017.
 */

public class RequestMapFragment extends BaseFragment implements LocationHelper.OnLocationReceived, AsyncTaskCompleteListener, OnMapReadyCallback, AdapterCallback {

    private GoogleMap googleMap;
    private Bundle mBundle;
    SupportMapFragment user_request_map;
    private View view;
    private LocationHelper locHelper;
    private Location myLocation;
    private LatLng mLatLng;
    private LatLng des_latLng, source_LatLng, stop_latLng;
    private static final int DURATION = 2000;
    private TextView btn_request_cab, tv_no_seats, tv_estimate_fare,
            tv_cashtype, tv_total_dis, tv_promocode, txt_ShowNameRoutes;
    ///CARDVIEW
    private Button cardShowRedA, cardShowRedB, cardShowRedC;
    private ArrayList<Button> arrayListCardShow;

    private CheckBox cb_ferry_terminals_value, cb_staircase_value, cb_a_and_e_value, cb_imh_value, cb_tarmac_value;
    private Spinner spn_weight_value, spn_family_member_value;
    private EditText edt_house_unit;
    private Spinner spn_oxygen_tank;
    private Spinner spn_pickup_type;
    private EditText trip_remark;
    private static Marker pickup_marker, drop_marker, stop_marker;
    MarkerOptions pickup_opt;

    private static Polyline poly_line_click;
    private ArrayList<Polyline> polylineData;

    private RelativeLayout lay_payment;
    public static ImageButton btn_mylocation;
    public static LinearLayout request_layout;
    private ArrayList<AmbulanceOperator> mAmbulanceOperatorsMain, mAmbulanceOperatorsTemp;
    private AmbulanceOperator mAmbulanceOperator;
    private AmbulanceOperatorHorizontalAdapter ambulanceOperatorHorizontalAdapter;
    private RecyclerView lst_vehicle;
    private ArrayList<NearByDrivers> driverslatlngs = new ArrayList<>();
    PolylineOptions options;
    private String nearest_eta = "--";

    // gửi point vẽ đường
    public static String OverView_Polyline;
    //

    private HashMap<Marker, Integer> markermap = new HashMap<>();
    private ArrayList<Marker> markers = new ArrayList<Marker>();
    private int marker_position;
    private String service_id = "1";
    private ProgressBar pbfareProgress;
    private LatLng driverlatlan;
    Handler providerhandler;
    Handler checkreqstatus;
    private ArrayList<Payments> paymentlst;
    private String pickup_add = "", tax_price = "", promoCode = "";
    RelativeLayout mapLayout;
    private String base_price = "", min_price = "", booking_fee = "", currency = "", distance_unit = "";
    private List<LatLng> listLatLng = new ArrayList<>();
    private Polyline blackPolyLine, greyPolyLine;
    private Dialog promo_dialog;
    private EditText et_promocode;
    private LinearLayout promo_layout;
    private ProgressBar load_progress;
    private String TAG = "DatDirection";
    private LinearLayout linearLayoutShowDirec;
    private EditText dialog_remark_content;
    private Button dialog_remark_btn_okay, dialog_remark_btn_cancel;

    //    private int assistiveDeviceValue = 1;
    private int oxygenTank = 0;
    private int case_type = 1, weight = 0, familyMember = 0;
    private TextView mTv_ambulance_operator_notice;
    private Button mBtn_another_ambulance_operator;
    private LinearLayout mAny_ambulance_operator_group;
    private WheelView wheel_view_ambulance_operator;
    private List<String> ambulanceOperatorsWheelView;
    private View mView_select;
    private boolean isExist = false;
    private boolean isAnyAmbulanceOperatorGroup = false;

    private FrameLayout bottomSheetLayout;
    private TextView tv_optional;
    private RequestOptional mRequestOptional;
    // Distance and Time

    public static String Distance_Request;
    public static String Time_Request;

    private JSONArray routeArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_request, container,
                false);

        arrayListCardShow = new ArrayList<>();
        cardShowRedA = view.findViewById(R.id.ClickRoutesA);
        cardShowRedB = view.findViewById(R.id.ClickRoutesB);
        cardShowRedC = view.findViewById(R.id.ClickRoutesC);
        arrayListCardShow.add(cardShowRedA);
        arrayListCardShow.add(cardShowRedB);
        arrayListCardShow.add(cardShowRedC);

        linearLayoutShowDirec = view.findViewById(R.id.linearshowDirection);
        txt_ShowNameRoutes = view.findViewById(R.id.Show_NameRoutes);


        wheel_view_ambulance_operator = (WheelView) view.findViewById(R.id.wheel_view_ambulance_operator);
        btn_mylocation = (ImageButton) view.findViewById(R.id.btn_mylocation);
        request_layout = (LinearLayout) view.findViewById(R.id.req_cabs);
        btn_request_cab = (TextView) view.findViewById(R.id.btn_request_cab);
        tv_cashtype = (TextView) view.findViewById(R.id.tv_cashtype);
        tv_no_seats = (TextView) view.findViewById(R.id.tv_no_seats);
        lst_vehicle = (RecyclerView) view.findViewById(R.id.list_vehicle);
        mTv_ambulance_operator_notice = (TextView) view.findViewById(R.id.tv_ambulance_operator_notice);
        mBtn_another_ambulance_operator = (Button) view.findViewById(R.id.btn_another_ambulance_operator);
        mAny_ambulance_operator_group = (LinearLayout) view.findViewById(R.id.any_ambulance_operator_group);
        mView_select = (View) view.findViewById(R.id.view_select);
        lay_payment = (RelativeLayout) view.findViewById(R.id.lay_payment);
        promo_layout = (LinearLayout) view.findViewById(R.id.promo_layout);
        tv_promocode = (TextView) view.findViewById(R.id.tv_promocode);
        load_progress = (ProgressBar) view.findViewById(R.id.load_progress);
        trip_remark = (EditText) view.findViewById(R.id.trip_remark);

        cb_a_and_e_value = (CheckBox) view.findViewById(R.id.tv_a_and_e_value);
        cb_imh_value = (CheckBox) view.findViewById(R.id.tv_imh_value);
        cb_ferry_terminals_value = (CheckBox) view.findViewById(R.id.tv_ferry_terminals_value);
        cb_staircase_value = (CheckBox) view.findViewById(R.id.cb_staircase_value);
        cb_tarmac_value = (CheckBox) view.findViewById(R.id.tv_tarmac_value);

        spn_family_member_value = (Spinner) view.findViewById(R.id.spn_family_member_value);
        spn_weight_value = (Spinner) view.findViewById(R.id.spn_weight_value);
        edt_house_unit = (EditText) view.findViewById(R.id.edt_house_unit_value);
        spn_oxygen_tank = (Spinner) view.findViewById(R.id.spn_oxygen_tank_value);
        spn_pickup_type = (Spinner) view.findViewById(R.id.spn_pickup_type_value);

        tv_optional = (TextView) view.findViewById(R.id.tv_optional);
        bottomSheetLayout = (FrameLayout) view.findViewById(R.id.request_map_bottom_sheet);

        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {

                switch (i) {

                    case BottomSheetBehavior.STATE_HIDDEN: {

                        EbizworldUtils.appLogDebug("HaoLS", "BottomSheet hidden");
                        bottomSheetBehavior.setPeekHeight(tv_optional.getHeight());
                    }
                    break;

                    case BottomSheetBehavior.STATE_COLLAPSED: {

                        EbizworldUtils.appLogDebug("HaoLS", "BottomSheet collapsed");
                        bottomSheetBehavior.setPeekHeight(tv_optional.getHeight());
                        tv_optional.setText(getResources().getString(R.string.txt_next));

                    }
                    break;

                    case BottomSheetBehavior.STATE_EXPANDED: {

                        tv_optional.setText(getResources().getString(R.string.txt_back));
                        EbizworldUtils.appLogDebug("HaoLS", "BottomSheet expanded");
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        tv_optional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {

                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    tv_optional.setText(getResources().getString(R.string.txt_back));

                } else {

                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    tv_optional.setText(getResources().getString(R.string.txt_next));
                }
            }
        });

        new PreferenceHelper(getActivity()).putRequestType("0");
        new PreferenceHelper(getActivity()).putAmbulance_name(getResources().getString(R.string.any_ambulance));
        mTv_ambulance_operator_notice.setSelected(true);
        mTv_ambulance_operator_notice.setText(new PreferenceHelper(activity).getAmbulance_name() + " " + getResources().getString(R.string.ambulance_operator_notice));


        //Set up WheelView
        wheel_view_ambulance_operator.setTypeface(Typeface.SERIF);
        ambulanceOperatorsWheelView = new ArrayList<>();
        ambulanceOperatorsWheelView.add(getResources().getString(R.string.any_ambulance));
        wheel_view_ambulance_operator.setOnWheelChangedListener(new WheelView.OnWheelChangedListener() {
            @Override
            public void onWheelScroll(int scrollOffsetY) {

            }

            @Override
            public void onWheelItemChanged(int oldPosition, int newPosition) {

            }

            @Override
            public void onWheelSelected(int position) {

                for (int i = 0; i < mAmbulanceOperatorsMain.size(); i++) {

                    if (mAmbulanceOperatorsMain.get(i).getAmbulanceOperator().toLowerCase().equals(ambulanceOperatorsWheelView.get(position).toLowerCase())) {

                        mAmbulanceOperator = mAmbulanceOperatorsMain.get(i);
                        break;
                    } else {

                        mAmbulanceOperator = null;
                    }
                }

                if (mAmbulanceOperator != null) {

                    EbizworldUtils.appLogDebug("HaoLS", "mAmbulanceOperator is " + mAmbulanceOperator.getAmbulanceOperator());

                    mTv_ambulance_operator_notice.setText(mAmbulanceOperator.getAmbulanceOperator().toUpperCase() + " " + getResources().getString(R.string.ambulance_operator_notice));
                    new PreferenceHelper(getActivity()).putRequestType(mAmbulanceOperator.getId());
                    new PreferenceHelper(getActivity()).putAmbulance_name(mAmbulanceOperator.getAmbulanceOperator());
                } else {

                    EbizworldUtils.appLogDebug("HaoLS", "mAmbulanceOperator is null");

                    mTv_ambulance_operator_notice.setText(getResources().getString(R.string.any_ambulance) + " " + getResources().getString(R.string.ambulance_operator_notice));
                    isAnyAmbulanceOperatorGroup = true;
                    new PreferenceHelper(getActivity()).putRequestType("0");
                    new PreferenceHelper(getActivity()).putAmbulance_name(getResources().getString(R.string.any_ambulance));
                }

                getAllProviders(mLatLng);

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

                switch (position) {

                    case 1: {
                        familyMember = 1;
                    }
                    break;

                    case 2: {
                        familyMember = 2;
                    }
                    break;

                    default: {
                        familyMember = 0;
                    }
                    break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Set up Weight
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

                switch (position) {

                    case 0: {

                        weight = 0;

                    }
                    break;

                    case 1: {

                        weight = 1;
                    }
                    break;

                    case 2: {

                        weight = 2;
                    }
                    break;

                    case 3: {

                        weight = 3;
                    }
                    break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //            Set up Assistive Device adapter
        /*List<String> assistiveDevices = new ArrayList<>();
        assistiveDevices.add("Wheelchair");
        assistiveDevices.add("Stretcher");
        SimpleSpinnerAdapter assistiveDeviceAdapter = new SimpleSpinnerAdapter(activity, assistiveDevices);
        spn_assistive_device.setAdapter(assistiveDeviceAdapter);
        spn_assistive_device.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0){

                    assistiveDeviceValue = 1;

                }else if (position == 1){

                    assistiveDeviceValue = 2;
                }

                EbizworldUtils.appLogDebug("HaoLS", "Assistive " + assistiveDeviceValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

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
        final List<String> pickupTypes = new ArrayList<>();
        pickupTypes.add(getResources().getString(R.string.medical_appointment));
        pickupTypes.add(getResources().getString(R.string.ad_hoc));
        pickupTypes.add(getResources().getString(R.string.airport_selectar));
        pickupTypes.add(getResources().getString(R.string.airport_changi));
        pickupTypes.add(getResources().getString(R.string.hospital_discharge));
        pickupTypes.add(getResources().getString(R.string.a_and_e));
        pickupTypes.add(getResources().getString(R.string.imh));
        pickupTypes.add(getResources().getString(R.string.ferry_terminals));
        pickupTypes.add(getResources().getString(R.string.airport_tarmac));

        SimpleSpinnerAdapter caseTypeAdapter = new SimpleSpinnerAdapter(activity, pickupTypes);
        spn_pickup_type.setAdapter(caseTypeAdapter);
        spn_pickup_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {

                    case 0: {

                        case_type = 1;
                    }
                    break;

                    case 1: {

                        case_type = 2;

                    }
                    break;

                    case 2: {

                        case_type = 3;
                    }
                    break;

                    case 3: {

                        case_type = 4;
                    }
                    break;

                    case 4: {

//                        case_type = 5;

                        FragmentManager fragmentManager = ((AppCompatActivity) activity).getSupportFragmentManager();
                        HospitalDischargeWarningDialog hospitalDischargeWarningDialog = new HospitalDischargeWarningDialog();
                        hospitalDischargeWarningDialog.setCancelable(false);
                        hospitalDischargeWarningDialog.show(fragmentManager, Const.HOSPITAL_DISCHARGE_WARNING_DIALOGFRAGMENT);
                        hospitalDischargeWarningDialog.setDialogDismissCallback(new DialogFragmentCallback.DialogDismissCallback() {
                            @Override
                            public void onDialogDismiss(Boolean isDismiss) {

                                if (isDismiss == true) {

                                    case_type = 1;
                                    spn_pickup_type.setSelection(0, true);
                                }
                            }
                        });

                    }
                    break;

                    case 5: {

                        case_type = 6;
                    }
                    break;

                    case 6: {

                        case_type = 7;
                    }
                    break;

                    case 7: {

                        case_type = 8;
                    }
                    break;

                    case 8: {

                        case_type = 9;
                    }
                    break;
                }

                EbizworldUtils.appLogDebug("HaoLS", "Case type " + case_type);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        user_request_map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.user_request_map);

        if (null != user_request_map) {

            user_request_map.getMapAsync(this);

        }

        mapLayout = (RelativeLayout) view.findViewById(R.id.map_lay);

        ItemClickSupport.addTo(lst_vehicle)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        // do it

                        isAnyAmbulanceOperatorGroup = false;
                        mAmbulanceOperator = mAmbulanceOperatorsTemp.get(position);
                        new PreferenceHelper(activity).putRequestType(mAmbulanceOperator.getId());
                        mTv_ambulance_operator_notice.setText(mAmbulanceOperator.getAmbulanceOperator().toUpperCase() + " " + getResources().getString(R.string.ambulance_operator_notice));
                        btn_request_cab.setText(getResources().getString(R.string.txt_reqst) + " " + mAmbulanceOperator.getAmbulanceOperator());
                        tv_no_seats.setText("1-" + " " + mAmbulanceOperator.getAmbulanceSeats());
                        ambulanceOperatorHorizontalAdapter.OnItemClicked(position);
                        ambulanceOperatorHorizontalAdapter.notifyDataSetChanged();
                        getAllProviders(mLatLng);
                        new PreferenceHelper(activity).putAmbulance_name(mAmbulanceOperator.getAmbulanceOperator());

                        mView_select.setVisibility(View.INVISIBLE);

                    }
                });

        btn_mylocation.setOnClickListener(this);
        btn_request_cab.setOnClickListener(this);
        lay_payment.setOnClickListener(this);
        promo_layout.setOnClickListener(this);
        mBtn_another_ambulance_operator.setOnClickListener(this);
        mAny_ambulance_operator_group.setOnClickListener(this);

        return view;

    }

    private void setupTypesView() {
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
        lst_vehicle.setLayoutManager(mLayoutManager);
        lst_vehicle.setItemAnimator(new DefaultItemAnimator());
        lst_vehicle.addItemDecoration(new SpacesItemDecoration(size.x / 20));

    }


    @Override
    public void onMapReady(GoogleMap mgoogleMap) {
        googleMap = mgoogleMap;

        if (googleMap != null) {

            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.getUiSettings().setMapToolbarEnabled(false);
            googleMap.getUiSettings().setScrollGesturesEnabled(true);
            googleMap.getUiSettings().setZoomGesturesEnabled(true);
            googleMap.getUiSettings().setRotateGesturesEnabled(false);
            googleMap.getUiSettings().setTiltGesturesEnabled(false);
            googleMap.setTrafficEnabled(false);


            if (getActivity() != null) {

                try {
                    boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.mapstyle));

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

            googleMap.setMyLocationEnabled(false);


            if (pic_latlan != null) {

                pickup_opt = new MarkerOptions();
                pickup_opt.position(pic_latlan);
                pickup_opt.title(activity.getResources().getString(R.string.txt_current_loc));
                pickup_opt.anchor(0.5f, 0.5f);
                pickup_opt.zIndex(1);
                pickup_opt.icon(BitmapDescriptorFactory
                        .fromBitmap(getMarkerBitmapFromView(nearest_eta)));
                pickup_marker = googleMap.addMarker(pickup_opt);

                btn_mylocation.setVisibility(View.GONE);

            }

            if (drop_latlan != null) {
                MarkerOptions opt = new MarkerOptions();
                opt.position(drop_latlan);
                opt.title(activity.getResources().getString(R.string.txt_drop_loc));
                opt.anchor(0.5f, 0.5f);
                opt.icon(BitmapDescriptorFactory
                        .fromResource(R.mipmap.drop_location));
                drop_marker = googleMap.addMarker(opt);
            }


            if (stop_latlan != null) {
                Log.e("asher", "stop req map " + stop_latlan);
                MarkerOptions opt = new MarkerOptions();
                opt.position(stop_latlan);
                //       opt.title(activity.getResources().getString(R.string.txt_drop_loc));
                opt.anchor(0.5f, 0.5f);
                opt.icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.pin_stop));
                stop_marker = googleMap.addMarker(opt);
            }
          /*  if (pickup_marker != null && drop_marker != null) {
                fitmarkers_toMap();
            }*/


        }

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                                           @Override
                                           public View getInfoWindow(Marker marker) {
                                               View vew = null;
                                               if (drop_marker != null) {
                                                   if (marker.getId().equals(drop_marker.getId())) {
                                                       vew = activity.getLayoutInflater().inflate(R.layout.info_window_dest, null);
                                                     /*  new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                           @Override
                                                           public void run() {

                                                               if (drop_marker != null) {
                                                                   drop_marker.showInfoWindow();
                                                               }
                                                           }
                                                       });*/
                                                   } else if (marker.getId().equals(pickup_marker.getId())) {
                                                       pickup_marker.hideInfoWindow();
                                                      /* vew = activity.getLayoutInflater().inflate(R.layout.eta_info_window, null);
                                                       final TextView txt_eta_marker = (TextView) vew.findViewById(R.id.txt_eta);


                                                       txt_eta_marker.setText(nearest_eta);*/
/*

                                                       new CountDownTimer(2000, 1000) {

                                                           public void onTick(long millisUntilFinished) {

                                                           }

                                                           public void onFinish() {
                                                               if (driverlatlan != null && pic_latlan != null) {

                                                               }
                                                               if (pickup_marker != null) {
                                                                   pickup_marker.showInfoWindow();

                                                               }
                                                           }

                                                       }.start();*/
/*
                                                       new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                           @Override
                                                           public void run() {

                                                               if (pickup_marker != null) {
                                                                   pickup_marker.showInfoWindow();

                                                               }
                                                           }
                                                       });*/
                                                  /*     Thread t = new Thread() {

                                                           @Override
                                                           public void run() {
                                                               try {
                                                                   while (!isInterrupted()) {
                                                                       Thread.sleep(4000);
                                                                       runOnUiThread(new Runnable() {
                                                                           @Override
                                                                           public void run() {
                                                                               // update TextView here!

                                                                           }
                                                                       });
                                                                   }
                                                               } catch (InterruptedException e) {
                                                               }
                                                           }
                                                       };

                                                       t.start();*/

                                                       // final TextView txt_location_marker = (TextView) vew.findViewById(R.id.txt_location);
                                                   } else {
                                                       vew = activity.getLayoutInflater().inflate(R.layout.driver_info_window, null);
                                                       TextView txt_driver_name = (TextView) vew.findViewById(R.id.driver_name);
                                                       if (driverslatlngs.size() > 0) {
                                                           txt_driver_name.setText(driverslatlngs.get(marker_position).getDriver_name());
                                                           SimpleRatingBar driver_rate = (SimpleRatingBar) vew.findViewById(R.id.driver_rate);
                                                           driver_rate.setRating(driverslatlngs.get(marker_position).getDriver_rate());
                                                       }
                                                   }
                                               } else {
                                                   vew = activity.getLayoutInflater().inflate(R.layout.driver_info_window, null);
                                                   TextView txt_driver_name = (TextView) vew.findViewById(R.id.driver_name);

                                                   if (driverslatlngs.size() > 0) {
                                                       txt_driver_name.setText(driverslatlngs.get(marker_position).getDriver_name());
                                                       SimpleRatingBar driver_rate = (SimpleRatingBar) vew.findViewById(R.id.driver_rate);
                                                       driver_rate.setRating(driverslatlngs.get(marker_position).getDriver_rate());

                                                   }
                                               }

                                               return vew;

                                           }

                                           @Override
                                           public View getInfoContents(Marker marker) {
                                               // Getting view from the layout file infowindowlayout.xml
                                               return null;
                                           }
                                       }
        );

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if (pickup_marker != null) {
                    pickup_marker.hideInfoWindow();
                    if (!marker.getId().equals(pickup_marker.getId()) && !marker.getId().equals(drop_marker.getId())) {
                        marker_position = markermap.get(marker);
                    }
                } else {
                    marker_position = markermap.get(marker);
                }


                return false;
            }
        });


        if (pickup_marker != null && drop_marker != null) {
            fitmarkers_toMap();
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

        EbizworldUtils.appLogDebug("HaoLS", "distance api " + map.toString());

        new VolleyRequester(activity, Const.GET, map, 101, this);
    }

    @Override
    public void onMethodCallback(String id, String ambulance_type, String ambulance_price_distance, String ambulance_price_min, String ambulance_image, String ambulance_seats, String basefare) {
        service_id = id;
        findDistanceAndTime(pic_latlan, drop_latlan);
        showfareestimate(ambulance_type, ambulance_price_distance, ambulance_price_min, ambulance_image, ambulance_seats, basefare);

    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {

            this.space = space;

        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(space, 2, space, 2);

            // Add top margin only for the first item to avoid double space between items
           /* if(parent.getChildPosition(view) == 0)
                outRect.top = space;*/
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBundle = savedInstanceState;
        mAmbulanceOperatorsMain = new ArrayList<AmbulanceOperator>();
        mAmbulanceOperatorsTemp = new ArrayList<>();
        paymentlst = new ArrayList<Payments>();
        providerhandler = new Handler();
        checkreqstatus = new Handler();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        try {
            MapsInitializer.initialize(getActivity());
        } catch (Exception e) {
        }

        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

        locHelper = new LocationHelper(activity);
        locHelper.setLocationReceivedLister(this);
        setupTypesView();
//        getTypesforhome();

        if (new PreferenceHelper(activity).getRequestId() == Const.NO_REQUEST) {
            startgetProvider();
        }


        if (pic_latlan != null && drop_latlan != null) {

            if (stop_latlan != null) {

                getDirectionsWay(pic_latlan.latitude, pic_latlan.longitude, drop_latlan.latitude, drop_latlan.longitude, stop_latlan.latitude, stop_latlan.longitude);

            } else {

                getDirections(pic_latlan.latitude, pic_latlan.longitude, drop_latlan.latitude, drop_latlan.longitude);

            }
            findDistanceAndTimeforTypes(pic_latlan, drop_latlan);

        }
        //updatepayment("card");
    }

    private void getAmbulanceOperators(String dis, String dur) {

        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        HashMap<String, String> map = new HashMap<String, String>();
        if (getActivity() != null) {

            if (new PreferenceHelper(getActivity()).getLoginType().equals(Const.PatientService.PATIENT)) {

                map.put(Const.Params.URL, Const.ServiceType.OPERATORS_URL);

            } else if (new PreferenceHelper(getActivity()).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)) {

                map.put(Const.Params.URL, Const.NursingHomeService.OPERATORS_URL);

            } else if (new PreferenceHelper(getActivity()).getLoginType().equals(Const.HospitalService.HOSPITAL)) {

                map.put(Const.Params.URL, Const.HospitalService.OPERATORS_URL);

            }

        }

        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());
        map.put(Const.Params.DISTANCE, dis);
        map.put(Const.Params.TIME, dur);

        Log.d("HaoLS", "Getting ambulance operators " + map.toString());
        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.AMBULANCE_OPERATOR,
                this);
    }


    @Override
    public void onLocationReceived(LatLng latlong) {
        if (latlong != null) {
            /*googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng,
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
            // drawTrip(mLatLng);
            myLocation = location;
            LatLng latLang = new LatLng(location.getLatitude(),
                    location.getLongitude());
            mLatLng = latLang;

        }

    }

    private void getAllProviders(LatLng latlong) {

        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        HashMap<String, String> map = new HashMap<String, String>();

        if (new PreferenceHelper(activity).getLoginType().equals(Const.PatientService.PATIENT)) {

            map.put(Const.Params.URL, Const.ServiceType.GET_PROVIDERS);

        } else if (new PreferenceHelper(activity).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)) {

            map.put(Const.Params.URL, Const.NursingHomeService.GUEST_PROVIDER_URL);

        } else if (new PreferenceHelper(activity).getLoginType().equals(Const.HospitalService.HOSPITAL)) {

            map.put(Const.Params.URL, Const.HospitalService.GUEST_PROVIDER_LIST_URL);

        }

        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());

        if (latlong != null) {
            map.put(Const.Params.LATITUDE, String.valueOf(latlong.latitude));
            map.put(Const.Params.LONGITUDE, String.valueOf(latlong.longitude));
        }

        map.put(Const.Params.AMBULANCE_TYPE, new PreferenceHelper(activity).getRequestType());
        Log.d(TAG, "serve id " + new PreferenceHelper(activity).getRequestType());

        Log.d("HaoLS", "nearby drivers " + map.toString());
        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.GET_PROVIDERS,
                this);

    }

    @Override
    public void onConntected(Bundle bundle) {

    }

    @Override
    public void onConntected(Location location) {

        if (location != null && googleMap != null) {
            LatLng currentlatLang = new LatLng(location.getLatitude(), location.getLongitude());
            /*googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentlatLang,
                    15));*/
            pickup_add = getCompleteAddressString(currentlatLang.latitude, currentlatLang.longitude);
        }
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

    private void getDirections(double latitude, double longitude, double latitude1, double longitude1) {

        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.DIRECTION_API_BASE + Const.ORIGIN + "="
                + String.valueOf(latitude) + "," + String.valueOf(longitude) + "&" + Const.DESTINATION + "="
                + String.valueOf(latitude1) + "," + String.valueOf(longitude1) + "&" + Const.EXTANCTION);
        Log.d(TAG, "getDirections" + map);
        new VolleyRequester(activity, Const.GET, map, Const.ServiceCode.GOOGLE_DIRECTION_API, this);

    }

    private void getDirectionsWay(double latitude, double longitude, double latitude1, double longitude1, double latitideStop, double longitudeStop) {

        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.DIRECTION_API_BASE + Const.ORIGIN + "="
                + String.valueOf(latitude) + "," + String.valueOf(longitude) + "&" + Const.DESTINATION + "="
                + String.valueOf(latitude1) + "," + String.valueOf(longitude1) + "&" + Const.WAYPOINTS + "="
                + String.valueOf(latitideStop) + "," + String.valueOf(longitudeStop) + "&" + Const.EXTANCTION);
        Log.e("asher", "directions stop map " + map);
        new VolleyRequester(activity, Const.GET, map, Const.ServiceCode.GOOGLE_DIRECTION_API, this);
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


    public void drawPath(String result) {

        try {
            //Tranform the string into a json object
            polylineData = new ArrayList<>();
            final JSONObject json = new JSONObject(result);
            Log.d(TAG, "Routes    1   :" + json);
            routeArray = json.getJSONArray("routes");
            linearLayoutShowDirec.setVisibility(View.VISIBLE);

            Distance_Request = routeArray.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getString("text");
            Time_Request = routeArray.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getString("text");
//            float km1 = Float.parseFloat(km);
//            float time1 = Float.parseFloat(time);
            OverView_Polyline = routeArray.getJSONObject(0).getJSONObject("overview_polyline").getString("points");

            txt_ShowNameRoutes.setText("Name Routes : A" + "  Duration :" + Time_Request + "  Distance :" + Distance_Request);
            txt_ShowNameRoutes.setBackgroundColor(Color.parseColor("#FF0000"));

            String color[] = {"#ff0000", "#00ff00", "#7883cf"};
            int i;
            for (i = 0; i < routeArray.length(); i++) {

                JSONObject routes = routeArray.getJSONObject(i);

                JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");

                String encodedString = overviewPolylines.getString("points");


                Log.d(TAG, "enCodeString  :" + encodedString);

                List<LatLng> list = decodePoly(encodedString);

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

                    googleMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
                        @Override
                        public void onPolylineClick(Polyline polyline) {
                            linearLayoutShowDirec.setVisibility(View.VISIBLE);

                            String txt_Name[] = {" A", " B", " C"};
                            String color[] = {"#FF0000", "#00FF00", "#7883CF"};
                            for (int j = 0; j < polylineData.size(); j++) {

                                if (polylineData.get(j).getColor() == polyline.getColor()) {


                                    try {

                                        Log.d("DatTest3Routes", String.valueOf(routeArray.getJSONObject(j).getJSONObject("overview_polyline").getString("points")));

                                        OverView_Polyline = routeArray.getJSONObject(j).getJSONObject("overview_polyline").getString("points");

                                        Distance_Request = routeArray.getJSONObject(j).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getString("text");
                                        Time_Request = routeArray.getJSONObject(j).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getString("text");
//                                        txt_ShowNameRoutes.setText("Name Routes :"+ txt_Name[j]);
                                        txt_ShowNameRoutes.setText("Name Routes:" + txt_Name[j] + "  Duration:" + Time_Request + "  Distance:" + Distance_Request);

                                        pickup_marker.setIcon((BitmapDescriptorFactory
                                                .fromBitmap(getMarkerBitmapFromView(Time_Request))));

                                        txt_ShowNameRoutes.setBackgroundColor(Color.parseColor(color[j]));
                                        Toast.makeText(activity, Distance_Request + " " + Time_Request, Toast.LENGTH_SHORT).show();

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
                            poly_line_click = polyline;
                        }
                    });

//                    cardShowRedA.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                        ClickPolylines(0);
//                        }
//                    });
//
//                    cardShowRedB.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                        ClickPolylines(1);
//
//                        }
//                    });
//
//                    cardShowRedC.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                         ClickPolylines(2);
//                        }
//                    });
                }
//                PolylineOptions greyOptions = new PolylineOptions();
//                greyOptions.width(8);
//                greyOptions.color(getResources().getColor(R.color.deeporange200));
//                greyPolyLine = googleMap.addPolyline(greyOptions);
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//                    Log.e("asher", "inside animate polyline ");
//                    animatePolyLine();
//                }
            }
        } catch (JSONException e) {

        }
        setVisiableCardShow();

        //Click CardView

        for (int j = 0; j < polylineData.size(); j++) {
            final int finalJ = j;
            arrayListCardShow.get(j).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ClickPolylines(finalJ);
                }
            });
        }
    }

    private void setVisiableCardShow() {
        String color3[] = {"#FF0000", "#00FF00", "#7883CF"};
        for (int i = 0; i < polylineData.size(); i++) {
            arrayListCardShow.get(i).setVisibility(View.VISIBLE);
            arrayListCardShow.get(i).setBackgroundColor(Color.parseColor(color3[i]));
//            arrayListCardShow.get(i).setText();

        }
    }

    private void ClickPolylines(int i) {
        // get possition ,khi click vào button
        poly_line_click = polylineData.get(i);
        Log.d("DatTest3Routes", String.valueOf(polylineData.get(i)));

        String txt_Name[] = {" A", " B", " C"};

        String color[] = {"#FF0000", "#00FF00", "#7883CF"};

        for (int j = 0; j < polylineData.size(); j++) {

            if (polylineData.get(j).getColor() == poly_line_click.getColor()) {

                try {
                    Log.d("DatTest3Routes", String.valueOf(routeArray.getJSONObject(j).getJSONObject("overview_polyline").getString("points")));

                    OverView_Polyline = routeArray.getJSONObject(j).getJSONObject("overview_polyline").getString("points");

                    Distance_Request = routeArray.getJSONObject(j).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getString("text");

                    Time_Request = routeArray.getJSONObject(j).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getString("text");
//                                        txt_ShowNameRoutes.setText("Name Routes :"+ txt_Name[j]);
                    txt_ShowNameRoutes.setText("Name Routes:" + txt_Name[j] + "  Duration:" + Time_Request + "  Distance:" + Distance_Request);
                    pickup_marker.setIcon((BitmapDescriptorFactory
                            .fromBitmap(getMarkerBitmapFromView(Time_Request))));

                    txt_ShowNameRoutes.setBackgroundColor(Color.parseColor(color[j]));
                    Toast.makeText(activity, Distance_Request + " " + Time_Request, Toast.LENGTH_SHORT).show();

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


    private void fitmarkers_toMap() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

//the include method will calculate the min and max bound.
        builder.include(pickup_marker.getPosition());
        builder.include(drop_marker.getPosition());
        LatLngBounds bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (height * 0.19); // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        if (null != googleMap) {
            googleMap.moveCamera(cu);
        }
       /* if (pic_latlan != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(pic_latlan)
                    .zoom(12).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


        }*/

        //pickup_marker.showInfoWindow();

        request_layout.setVisibility(View.VISIBLE);
        //findDistanceAndTimeforeta(pic_latlan, driverlatlan);
        //findDistanceAndTime();

    }

    private void findDistanceAndTimeforeta(LatLng pic_latlan, LatLng latlan) {

        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.GOOGLE_MATRIX_URL + Const.Params.ORIGINS + "="
                + String.valueOf(pic_latlan.latitude) + "," + String.valueOf(pic_latlan.longitude) + "&" + Const.Params.DESTINATION + "="
                + String.valueOf(latlan.latitude) + "," + String.valueOf(latlan.longitude) + "&" + Const.Params.MODE + "="
                + "driving" + "&" + Const.Params.LANGUAGE + "="
                + "en-EN" + "&" + "key=" + Const.GOOGLE_API_KEY + "&" + Const.Params.SENSOR + "="
                + String.valueOf(false));
        Log.e("mahi", "distance api" + map);
        new VolleyRequester(activity, Const.GET, map, Const.ServiceCode.GOOGLE_MATRIX_ETA, this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_mylocation:
/*
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng,
                        15));*/
                break;
            case R.id.btn_request_cab:
                // chuyển fragment gửi đường
                getTripOptional();

                if (mRequestOptional != null) {

                    Bundle bundle = new Bundle();
                    bundle.putParcelable(Const.Params.REQUEST_OPTIONAL, mRequestOptional);
                    BillingInfoFragment billingInfoFragment = new BillingInfoFragment();
                    billingInfoFragment.setArguments(bundle);
                    activity.addFragment(billingInfoFragment, false, Const.BILLING_INFO_FRAGMENT, true);
                    stopCheckingforproviders();
                    Commonutils.progressdialog_show(activity, "Loading...");
                }

                break;

            case R.id.lay_payment:
                getPaymentsmethods();
                break;
            case R.id.promo_layout:
                showpromo();
                break;

            case R.id.btn_another_ambulance_operator: {

                showAmbulanceOperator(mAmbulanceOperatorsMain);
            }
            break;

            case R.id.any_ambulance_operator_group: {

                isAnyAmbulanceOperatorGroup = true;
                mView_select.setVisibility(View.VISIBLE);
                mTv_ambulance_operator_notice.setText(getResources().getString(R.string.any_ambulance) + " " + getResources().getString(R.string.ambulance_operator_notice));

                if (ambulanceOperatorHorizontalAdapter != null) {

                    ambulanceOperatorHorizontalAdapter.OnItemClicked(-1);
                    ambulanceOperatorHorizontalAdapter.notifyDataSetChanged();

                }

                if (providerhandler != null) {

                    providerhandler.removeCallbacks(getProvidersRunnable);

                }

                getAllProviders(mLatLng);

            }
            break;

        }

    }

    private void getPaymentsmethods() {

        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.GET_PAYMENT_MODES + Const.Params.ID + "="
                + new PreferenceHelper(activity).getUserId() + "&" + Const.Params.TOKEN + "="
                + new PreferenceHelper(activity).getSessionToken());

        Log.d("HaoLS", "getPaymentModes: " + map.toString());
        new VolleyRequester(activity, Const.GET, map, Const.ServiceCode.GET_PAYMENT_MODES, this);


    }

    private void getTripOptional() {

        mRequestOptional = new RequestOptional();
        mRequestOptional.setId(new PreferenceHelper(activity).getUserId());
        mRequestOptional.setToken(new PreferenceHelper(activity).getSessionToken());

        if (pic_latlan != null) {

            mRequestOptional.setPic_lat(pic_latlan.latitude);
            mRequestOptional.setPic_lng(pic_latlan.longitude);
        }
        if (drop_latlan != null) {

            mRequestOptional.setDrop_lng(drop_latlan.longitude);
            mRequestOptional.setDrop_lat(drop_latlan.latitude);

        }
        if (stop_latlan != null) {

            mRequestOptional.setIsAddStop(1);
            mRequestOptional.setAddStop_lng(stop_latlan.longitude);
            mRequestOptional.setAddStop_lat(stop_latlan.latitude);
            mRequestOptional.setAddStop_address(stop_address);
        }
//        if(OverView_Polyline != null){
//            mRequestOptional.setOverView_Polyline(OverView_Polyline);
//        }

        mRequestOptional.setOperator_id(Integer.parseInt(new PreferenceHelper(activity).getRequestType()));
        mRequestOptional.setPic_address(s_address);
        mRequestOptional.setDrop_address(d_address);
        mRequestOptional.setRequest_status_type(1);
        mRequestOptional.setPromoCode(promoCode);
        mRequestOptional.setRemark(trip_remark.getText().toString());

        if (cb_a_and_e_value.isChecked()) {

            mRequestOptional.setA_and_e(1);

        } else {

            mRequestOptional.setA_and_e(0);
        }

        if (cb_imh_value.isChecked()) {

            mRequestOptional.setImh(1);

        } else {

            mRequestOptional.setImh(0);
        }

        if (cb_ferry_terminals_value.isChecked()) {

            mRequestOptional.setFerry_terminals(1);

        } else {

            mRequestOptional.setFerry_terminals(0);
        }

        if (cb_staircase_value.isChecked()) {

            mRequestOptional.setStaircase(1);

        } else {

            mRequestOptional.setStaircase(0);

        }

        if (cb_tarmac_value.isChecked()) {

            mRequestOptional.setTarmac(1);

        } else {

            mRequestOptional.setTarmac(0);

        }

        mRequestOptional.setFamily_member(familyMember);
        mRequestOptional.setHouseUnit(edt_house_unit.getText().toString().trim());
        mRequestOptional.setWeight(weight);
        mRequestOptional.setOxygen(oxygenTank);
        mRequestOptional.setCaseType(case_type);
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(activity, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();

            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return strAdd;
    }


    @Override
    public void onResume() {

        super.onResume();

        activity.currentFragment = Const.REQUEST_FRAGMENT;

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
        providerhandler.postDelayed(getProvidersRunnable, 4000);
    }

    private void stopCheckingforproviders() {
        if (providerhandler != null) {
            providerhandler.removeCallbacks(getProvidersRunnable);

            // Log.d("mahi", "stop provider handler");
        }
    }

    Runnable getProvidersRunnable = new Runnable() {
        public void run() {

            getAllProviders(mLatLng);

            providerhandler.postDelayed(this, 4000);
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //Log.e("mahi", "on destory view is calling");
        SupportMapFragment f = (SupportMapFragment) getFragmentManager()
                .findFragmentById(R.id.user_request_map);
        if (f != null) {
            try {
                getFragmentManager().beginTransaction().remove(f).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /*TO clear all views */
        if (getActivity() != null) {

            ViewGroup mContainer = (ViewGroup) getActivity().findViewById(R.id.content_frame);
            mContainer.removeAllViews();

        }

        googleMap = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopCheckingforproviders();
        if (googleMap != null) {
            googleMap.clear();
        }
    }

    @Override
    public void onTaskCompleted(final String response, int serviceCode) {


        switch (serviceCode) {
            case Const.ServiceCode.GOOGLE_DIRECTION_API:

                if (response != null) {
                    Log.d(TAG, "Reponse   1:" + response);
                    drawPath(response);

                }

                break;
            case Const.ServiceCode.AMBULANCE_OPERATOR:

                EbizworldUtils.appLogInfo("HaoLS", "Ambulance Operators " + response);

                if (response != null) {

                    try {
                        JSONObject job = new JSONObject(response);

                        if (job.getString("success").equals("true")) {

                            mAmbulanceOperatorsMain.clear();

                            JSONArray jarray = new JSONArray();

                            if (getActivity() != null) {

                                if (new PreferenceHelper(getActivity()).getLoginType().equals(Const.PatientService.PATIENT)) {

                                    jarray = job.getJSONArray("operator");

                                } else if (new PreferenceHelper(getActivity()).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)) {

                                    jarray = job.getJSONArray("operator");
                                } else if (new PreferenceHelper(getActivity()).getLoginType().equals(Const.HospitalService.HOSPITAL)) {

                                    jarray = job.getJSONArray("operator");
                                }

                            }


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
                                    mAmbulanceOperatorsMain.add(type);

                                }

                                /*if (mAmbulanceOperatorsMain != null) {

                                    ambulanceOperatorHorizontalAdapter = new AmbulanceOperatorHorizontalAdapter(activity, mAmbulanceOperatorsMain, this);
                                    lst_vehicle.setAdapter(ambulanceOperatorHorizontalAdapter);
                                    LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getActivity(), getResources().getIdentifier("layout_animation_from_right", "anim", getActivity().getPackageName()));
                                    lst_vehicle.setLayoutAnimation(animation);
                                    ambulanceOperatorHorizontalAdapter.notifyDataSetChanged();
                                    lst_vehicle.scheduleLayoutAnimation();
                                    load_progress.setVisibility(View.GONE);

                                    if (mAmbulanceOperatorsMain.size() > 0) {

                                        btn_request_cab.setText(getResources().getString(R.string.txt_reqst) + " " + mAmbulanceOperatorsMain.get(0).getAmbulanceOperator());
                                        tv_no_seats.setText("1-" + " " + mAmbulanceOperatorsMain.get(0).getAmbulanceSeats());
                                        new PreferenceHelper(activity).putAmbulance_name(mAmbulanceOperatorsMain.get(0).getAmbulanceOperator());

                                    }

                                }*/

                                if (mAmbulanceOperatorsMain.size() > 0 && mAmbulanceOperatorsMain != null) {

                                    if (mAmbulanceOperatorsMain.size() > 0) {

                                        for (int i = 0; i < mAmbulanceOperatorsMain.size(); i++) {

                                            ambulanceOperatorsWheelView.add(mAmbulanceOperatorsMain.get(i).getAmbulanceOperator());
                                        }

                                        wheel_view_ambulance_operator.setData(ambulanceOperatorsWheelView);
                                    }

                                    EbizworldUtils.appLogDebug("HaoLS", "Getting copy to mAmbulanceOperatorsTemp");

                                    mAmbulanceOperatorsTemp.clear();

                                    /*for (int i = 0; i < 3; i++){

                                        mAmbulanceOperatorsTemp.add(mAmbulanceOperatorsMain.get(i));

                                    }

                                    if (mAmbulanceOperatorsTemp.size() > 0){

                                        ambulanceOperatorHorizontalAdapter = new AmbulanceOperatorHorizontalAdapter(activity, mAmbulanceOperatorsTemp, this);
                                        lst_vehicle.setAdapter(ambulanceOperatorHorizontalAdapter);
                                        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getActivity(), getResources().getIdentifier("layout_animation_from_right", "anim", getActivity().getPackageName()));
                                        lst_vehicle.setLayoutAnimation(animation);
                                        ambulanceOperatorHorizontalAdapter.notifyDataSetChanged();
                                        lst_vehicle.scheduleLayoutAnimation();
                                        load_progress.setVisibility(View.GONE);

                                        mAmbulanceOperator = new AmbulanceOperator();
                                        mAmbulanceOperator = mAmbulanceOperatorsTemp.get(0);
                                        mTv_ambulance_operator_notice.setText(mAmbulanceOperator.getAmbulanceOperator().toUpperCase() + " " + getResources().getString(R.string.ambulance_operator_notice));

                                        btn_request_cab.setText(getResources().getString(R.string.txt_reqst) + " " + mAmbulanceOperator.getAmbulanceOperator());
                                        tv_no_seats.setText("1-" + " " + mAmbulanceOperator.getAmbulanceSeats());
                                        new PreferenceHelper(activity).putAmbulance_name(mAmbulanceOperator.getAmbulanceOperator());

                                    }*/
                                }


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

                        }


                        if (driverslatlngs.size() > 0) {

                            driverlatlan = driverslatlngs.get(0).getLatlan();

                            if (driverlatlan != null && pic_latlan != null) {
                                findDistanceAndTimeforeta(pic_latlan, driverlatlan);
                            }

                            if (null != markers && markers.size() > 0) {

                                for (Marker marker : markers) {

                                    marker.remove();

                                }

                            }

                            final Marker[] driver_marker = new Marker[1];
                            for (int i = 0; i < driverslatlngs.size(); i++) {
                           /* CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                                    driverslatlngs.get(i), 15);*/
                                // Log.d("mahi","markers size"+driverslatlngs.get(i).toString());

                                final MarkerOptions currentOption = new MarkerOptions();
                                currentOption.position(driverslatlngs.get(i).getLatlan());
                                currentOption.title(driverslatlngs.get(i).getDriver_name());
                                currentOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance_car));
                                if (googleMap != null) {
                                    activity.runOnUiThread(new Runnable() {
                                        public void run() {

                                            driver_marker[0] = googleMap.addMarker(currentOption);

                                        }
                                    });
                                    markers.add(driver_marker[0]);
                                    //googleMap.animateCamera(location);
                                    markermap.put(driver_marker[0], i);

                                    /*trip_remark.setEnabled(true);*/

                                    btn_request_cab.setEnabled(true);
                                    /*googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng,
                                            15));*/

                                    /*if (isAnyAmbulanceOperatorGroup == true){

                                        btn_request_cab.setText(getResources().getString(R.string.txt_reqst) + " " + Const.ANY_AMBULANCE_OPERATORS);
                                        btn_request_cab.setTextColor(activity.getResources().getColor(R.color.white));
                                        btn_request_cab.setBackgroundColor(activity.getResources().getColor(R.color.deeporange600));
                                        isAnyAmbulanceOperatorGroup = false;

                                    }else {

                                        btn_request_cab.setText(getResources().getString(R.string.txt_reqst) + " " + new PreferenceHelper(activity).getAmbulance_name());
                                        btn_request_cab.setTextColor(activity.getResources().getColor(R.color.white));
                                        btn_request_cab.setBackgroundColor(activity.getResources().getColor(R.color.deeporange600));

                                    }*/

                                    btn_request_cab.setText(getResources().getString(R.string.txt_reqst) + " " + new PreferenceHelper(activity).getAmbulance_name());
                                    btn_request_cab.setTextColor(activity.getResources().getColor(R.color.white));
                                    btn_request_cab.setBackgroundColor(activity.getResources().getColor(R.color.deeporange600));


                                    // setUpMap();
                                }
                            }


                        } else {

                            for (Marker marker : markers) {
                                marker.remove();
                            }
                            markers.clear();
                            // Toast.makeText(activity, activity.getString(R.string.txt_drivers_error), Toast.LENGTH_SHORT).show();

                            /*trip_remark.setEnabled(false);*/

                            btn_request_cab.setEnabled(false);
                            btn_request_cab.setText(getResources().getString(R.string.btn_no_driver));
                           /* if (null != googleMap) {
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLatLng,
                                        14));
                            }*/
                            btn_request_cab.setTextColor(activity.getResources().getColor(R.color.deeporange600));
                            btn_request_cab.setBackgroundColor(activity.getResources().getColor(R.color.deeporange200));
                            if (pickup_opt != null && pickup_marker != null) {
                                activity.runOnUiThread(new Runnable() {

                                    public void run() {

                                        pickup_marker.setIcon((BitmapDescriptorFactory
                                                .fromBitmap(getMarkerBitmapFromView(nearest_eta))));

                                    }

                                });

                            }

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case 101:

                EbizworldUtils.appLogInfo("HaoLS", "findDistanceandTime " + response);

                if (response != null) {

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

                            nearest_eta = duration;

                            String dur = durationObject.getString("value");

                            //Log.d("mahi", "time and dis" + dur + " " + dis);

                            double trip_dis = Integer.valueOf(dis) * 0.001;

                            getAmbulanceOperators(String.valueOf(trip_dis), dur);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        EbizworldUtils.appLogError("HaoLS", "findDistanceandTime " + e.toString());
                    }
                }

                break;

            case Const.ServiceCode.GOOGLE_MATRIX:
                EbizworldUtils.appLogInfo("HaoLS", "google distance api" + response);

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
                        nearest_eta = duration;

                        String dis = dObject.getString("value");

                        String dur = durationObject.getString("value");

                        // Log.d("mahi", "time and dis" + dur + " " + dis);

                        double trip_dis = Integer.valueOf(dis) * 0.001;

                        getFare(String.valueOf(trip_dis), dur, service_id);

                        tv_total_dis.setText(distance);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case Const.ServiceCode.GOOGLE_MATRIX_ETA:
                EbizworldUtils.appLogInfo("HaoLS", "google distance api" + response);

                if (response != null) {
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

                            final String duration = durationObject.getString("text");

                            nearest_eta = duration;

//                            if (pickup_opt != null && pickup_marker != null) {
//                                activity.runOnUiThread(new Runnable() {
//                                    public void run() {
//                                        pickup_marker.setIcon((BitmapDescriptorFactory
//                                                .fromBitmap(getMarkerBitmapFromView(duration))));
//
//                                    }
//                                });
//
//                            }

                            // setUpMap();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                break;

            case Const.ServiceCode.FARE_CALCULATION:
                EbizworldUtils.appLogInfo("HaoLS", "estimate fare" + response);

                if (response != null) {
                    try {
                        JSONObject job1 = new JSONObject(response);
                        if (job1.getString("success").equals("true")) {
                            String fare = job1.getString("estimated_fare");
                            tax_price = job1.getString("tax_price");
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
            case Const.ServiceCode.PAYMENT_MODE_UPDATE:
                if (response != null) {
                    try {
                        JSONObject job1 = new JSONObject(response);
                        if (job1.getString("success").equals("true")) {
                            // Commonutils.showtoast("Payment Option Updated Successfully!", activity);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                break;

            case Const.ServiceCode.VALIDATE_PROMO:
                Log.d("mahi", "validate promo response" + response);
                EbizworldUtils.removeProgressDialog();
                if (response != null) {
                    try {
                        JSONObject job1 = new JSONObject(response);
                        if (job1.getString("success").equals("true")) {

                            EbizworldUtils.showShortToast(job1.optString("error_message"), activity);
                            promoCode = et_promocode.getText().toString();
                            promo_dialog.dismiss();
                            tv_promocode.setText(getResources().getString(R.string.txt_promo_success));
                            tv_promocode.setTextColor(getResources().getColor(R.color.dark_green));
                            promo_layout.setEnabled(false);

                        } else {
                            EbizworldUtils.showShortToast(job1.optString("error_message"), activity);
                            et_promocode.requestFocus();
                            promo_layout.setEnabled(true);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case Const.ServiceCode.GET_PAYMENT_MODES:
                EbizworldUtils.appLogInfo("Manh", "payment response" + response);

                if (response != null) {
                    try {
                        JSONObject job1 = new JSONObject(response);
                        if (job1.getString("success").equals("true")) {
                            paymentlst.clear();
                            JSONArray paymentarray = job1.getJSONArray("payment_modes");
                            if (paymentarray.length() > 0) {
                                for (int i = 0; i < paymentarray.length(); i++) {
                                    Payments paymnts = new Payments();
                                    paymnts.setPayment_name(paymentarray.getString(i));
                                    paymentlst.add(paymnts);
                                }
                            }
                            if (paymentlst != null && isAdded()) {
                                showpaymentoptionlst(paymentlst);
                            }


                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void showpaymentoptionlst(final ArrayList<Payments> paymentlst) {

        final Dialog pay_dialog = new Dialog(activity, R.style.DialogThemeforview);
        pay_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        pay_dialog.setCancelable(true);
        pay_dialog.setContentView(R.layout.select_payment);
        ImageButton btn_pay_viewcancel = (ImageButton) pay_dialog.findViewById(R.id.btn_pay_viewcancel);
        RecyclerView lv_cards = (RecyclerView) pay_dialog.findViewById(R.id.lv_cards);
        TextView tv_payment_title = (TextView) pay_dialog.findViewById(R.id.tv_payment_title);
        PaymentModeAdapter payadapter = new PaymentModeAdapter(activity, paymentlst);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);
        lv_cards.setLayoutManager(mLayoutManager);
        lv_cards.setItemAnimator(new DefaultItemAnimator());
        lv_cards.setAdapter(payadapter);

        if (new PreferenceHelper(activity).getPaymentMode().equals("cod")) {
            tv_payment_title.setText(getResources().getString(R.string.txt_selected_type) + " " + getResources().getString(R.string.txt_cash));
        } else if (new PreferenceHelper(activity).getPaymentMode().equals("paypal")) {
            tv_payment_title.setText(getResources().getString(R.string.txt_selected_type) + " " + getResources().getString(R.string.txt_paypal));
        } else if (new PreferenceHelper(activity).getPaymentMode().equals("walletbay")) {
            tv_payment_title.setText(getResources().getString(R.string.txt_selected_type) + " " + getResources().getString(R.string.txt_wallet));
        } else {
            tv_payment_title.setText(getResources().getString(R.string.txt_selected_type) + " " + getResources().getString(R.string.txt_card));
        }

        lv_cards.addOnItemTouchListener(new RecyclerLongPressClickListener(activity, lv_cards, new RecyclerLongPressClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                updatepayment(paymentlst.get(position).getPayment_name());
                new PreferenceHelper(activity).putPaymentMode(paymentlst.get(position).getPayment_name());

                if (new PreferenceHelper(activity).getPaymentMode().equals("cod")) {
                    tv_cashtype.setText(getResources().getString(R.string.txt_selected_type) + " " + getResources().getString(R.string.txt_cash));
                } else if (new PreferenceHelper(activity).getPaymentMode().equals("paypal")) {
                    tv_cashtype.setText(getResources().getString(R.string.txt_selected_type) + " " + getResources().getString(R.string.txt_paypal));
                } else if (new PreferenceHelper(activity).getPaymentMode().equals("walletbay")) {
                    tv_cashtype.setText(getResources().getString(R.string.txt_selected_type) + " " + getResources().getString(R.string.txt_wallet));
                } else {
                    tv_cashtype.setText(getResources().getString(R.string.txt_selected_type) + " " + getResources().getString(R.string.txt_card));
                }

                pay_dialog.dismiss();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
        btn_pay_viewcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pay_dialog.dismiss();
            }
        });
        pay_dialog.show();
    }

    private void updatepayment(String payment_name) {

        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.PAYMENT_MODE_UPDATE);
        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());

        map.put(Const.Params.PAYMENT_MODE, payment_name);

        Log.d("HaoLS", "update payment: " + map.toString());
        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.PAYMENT_MODE_UPDATE,
                this);
    }

    private void getFare(String distance, String duration, String service_id) {

        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        HashMap<String, String> map = new HashMap<String, String>();


        if (new PreferenceHelper(activity).getLoginType().equals(Const.PatientService.PATIENT)) {

            map.put(Const.Params.URL, Const.ServiceType.FARE_CALCULATION);

        } else if (new PreferenceHelper(activity).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)) {

            map.put(Const.Params.URL, Const.NursingHomeService.FARE_CACULATION_URL);

        } else if (new PreferenceHelper(activity).getLoginType().equals(Const.HospitalService.HOSPITAL)) {

            map.put(Const.Params.URL, Const.HospitalService.FARE_CACULATION_URL);

        }
        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());
        map.put(Const.Params.DISTANCE, distance);
        map.put(Const.Params.TIME, duration);
        map.put(Const.Params.AMBULANCE_TYPE, service_id);

        Log.d("mahi", map.toString());
        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.FARE_CALCULATION,
                this);
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
        farebreak.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.deeporange200)));
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
        tv_service_tax_price.setText(currency + tax_price);
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

    private void animatePolyLine() {

        ValueAnimator animator = ValueAnimator.ofInt(0, 100);
        animator.setDuration(1200);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {

                List<LatLng> latLngList = blackPolyLine.getPoints();
                int initialPointSize = latLngList.size();
                int animatedValue = (int) animator.getAnimatedValue();
                int newPoints = (animatedValue * listLatLng.size()) / 100;

                if (initialPointSize < newPoints) {
                    latLngList.addAll(listLatLng.subList(initialPointSize, newPoints));
                    blackPolyLine.setPoints(latLngList);
                }


            }
        });

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {

            Log.e("asher", "inside animate polyline listener");
            animator.addListener(polyLineAnimationListener);
            animator.start();
        }

    }

    Animator.AnimatorListener polyLineAnimationListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {

            //addMarker(listLatLng.get(listLatLng.size()-1));
        }

        @Override
        public void onAnimationEnd(Animator animator) {

            List<LatLng> blackLatLng = blackPolyLine.getPoints();
            List<LatLng> greyLatLng = greyPolyLine.getPoints();

            greyLatLng.clear();
            greyLatLng.addAll(blackLatLng);
            blackLatLng.clear();

            blackPolyLine.setPoints(blackLatLng);
            greyPolyLine.setPoints(greyLatLng);

            blackPolyLine.setZIndex(2);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                animator.start();
                animatePolyLine();
            }
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    };

    private void showpromo() {

        promo_dialog = new Dialog(activity, R.style.DialogSlideAnim_leftright);
        promo_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        promo_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        promo_dialog.setCancelable(true);
        promo_dialog.setContentView(R.layout.promo_layout);
        et_promocode = promo_dialog.findViewById(R.id.et_promocode);
        et_promocode.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        TextView cencel_promocode = (TextView) promo_dialog.findViewById(R.id.cencel_promocode);
        TextView apply_promocode = (TextView) promo_dialog.findViewById(R.id.apply_promocode);
        apply_promocode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(et_promocode.getText().toString())) {
                    ApplyPromoCode(et_promocode.getText().toString());
                } else {

                    EbizworldUtils.showShortToast(getResources().getString(R.string.txt_error_promo), activity);
                    et_promocode.requestFocus();

                }
            }
        });

        cencel_promocode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promo_dialog.dismiss();
            }
        });

        promo_dialog.show();
    }

    private void ApplyPromoCode(String promoValue) {

        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        EbizworldUtils.showSimpleProgressDialog(activity, "", false);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.VALIDATE_PROMO);
        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());
        map.put(Const.Params.PROMOCODE, promoValue);
        Log.d("mahi", map.toString());
        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.VALIDATE_PROMO,
                this);

    }

    private void showRemarkDialog() {

        final Dialog dialog = new Dialog(getActivity(), R.style.DialogThemeforview);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_remark);

        dialog_remark_content = (EditText) dialog.findViewById(R.id.dialog_remark_content);
        dialog_remark_btn_okay = (Button) dialog.findViewById(R.id.dialog_remark_btn_okay);
        dialog_remark_btn_cancel = (Button) dialog.findViewById(R.id.dialog_remark_btn_cancel);

        dialog_remark_content.setText(trip_remark.getText().toString());

        dialog_remark_btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

            }
        });

        dialog_remark_btn_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String remark = dialog_remark_content.getText().toString();
                trip_remark.setText(remark);
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    //    Show dialog AmbulanceOperator
    private void showAmbulanceOperator(final List<AmbulanceOperator> ambulanceOperators) {

        if (getActivity() != null) {

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

            if (ambulanceOperators.size() > 0) {

                final AmbulanceOperatorVerticalAdaper ambulanceOperatorVerticalAdapter = new AmbulanceOperatorVerticalAdaper(getActivity(), ambulanceOperators);
                ambulanceOperatorList.setAdapter(ambulanceOperatorVerticalAdapter);

                ItemClickSupport.addTo(ambulanceOperatorList).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {

                        for (int i = 0; i < mAmbulanceOperatorsTemp.size(); i++) {

                            if (mAmbulanceOperatorsTemp.get(i).equals(ambulanceOperators.get(position))) {

                                isExist = true;
                                break;
                            } else {
                                isExist = false;
                            }
                        }

                        if (isExist == false) {

                            if (mAmbulanceOperatorsTemp.size() == 4) {

                                mAmbulanceOperatorsTemp.set(3, ambulanceOperators.get(position));

                            } else {

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
}

