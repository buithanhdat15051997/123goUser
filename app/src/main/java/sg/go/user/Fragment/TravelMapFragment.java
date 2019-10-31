package sg.go.user.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.simpleratingbar.SimpleRatingBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import sg.go.user.HttpRequester.VolleyRequester;
import sg.go.user.MainActivity;
import sg.go.user.Models.RequestOptional;
import sg.go.user.R;
import sg.go.user.Adapter.CancelReasonAdapter;
import sg.go.user.Adapter.PlacesAutoCompleteAdapter;
import sg.go.user.ChatActivity;
import sg.go.user.Interface.AsyncTaskCompleteListener;
import sg.go.user.Location.LocationHelper;
import sg.go.user.Models.CancelReason;
import sg.go.user.Models.RequestDetail;
import sg.go.user.Utils.EbizworldUtils;
import sg.go.user.Utils.CarAnimation.AnimateMarker;
import sg.go.user.Utils.Commonutils;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.ParseContent;
import sg.go.user.Utils.PreferenceHelper;
import sg.go.user.Utils.RecyclerLongPressClickListener;

import com.gdacciaro.iOSDialog.iOSDialog;
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
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by user on 1/12/2017.
 */
public class TravelMapFragment extends BaseFragment implements LocationHelper.OnLocationReceived, AsyncTaskCompleteListener, OnMapReadyCallback {

    private String TAG = TravelMapFragment.class.getSimpleName();
    Polyline poly_line;
    public static final int NOTIFICATION_ID = 2;
    private GoogleMap googleMap;
    private Bundle mBundle;
    SupportMapFragment user_travel_map;
    private View mViewRoot;
    private LocationHelper locHelper;
    private Location myLocation;
    private RequestDetail requestDetail;
    private int jobStatus = 0;
    private TextView tv_current_location, driver_name, driver_car_number, driver_car_model,
            driver_mobile_number, address_title, tv_driver_status, stopAddress;
    private CircleImageView driver_img;
    private Marker driver_car, source_marker, destination_marker, stop_marker;
    private LatLng d_latlon, s_latlon, driver_latlan, changeLatLng, stop_latlng;
    Handler checkRequestStatus;
    private NotificationManager mNotificationManager;
    private String eta_time = "--";
    private String mobileNo = "";
    private Socket mSocket;
    private Boolean isConnected = true;
    private Boolean isMarkerRotating = false;
    private boolean iscancelpopup = false;
    MarkerOptions pickup_opt;
    private LatLng delayLatlan;
    // private CircleImageView driver_car_img;
    private List<LatLng> mPathPolygonPoints;
    int mIndexCurrentPoint = 0;
    Bitmap mMarkerIcon;
    private LinearLayout cancel_trip, moreLay, addEditLay;
    private ArrayList<CancelReason> cancelReasonLst;
    ImageView sosCall;
    AutoCompleteTextView et_source_dia_address;
    private GoogleMap gMap;
    private ImageView pin_marker;
    RelativeLayout stopLay;
    TextView addStop, editDestination, optionsLine, line;
    ImageView img_confrim_cancel_booking, img_no_cancel_booking;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {

        mViewRoot = inflater.inflate(R.layout.fragment_travel_map, container, false);


        driver_img = (CircleImageView) mViewRoot.findViewById(R.id.img_driver);
        // driver_car_img = (CircleImageView) mViewRoot.findViewById(R.id.img_ambulance);
        tv_current_location = (TextView) mViewRoot.findViewById(R.id.tv_current_location);
        driver_name = (TextView) mViewRoot.findViewById(R.id.driver_name);
        driver_car_number = (TextView) mViewRoot.findViewById(R.id.driver_car_number);
        driver_car_model = (TextView) mViewRoot.findViewById(R.id.driver_car_model);
        driver_mobile_number = (TextView) mViewRoot.findViewById(R.id.driver_mobile_number);
        address_title = (TextView) mViewRoot.findViewById(R.id.address_title);
        tv_driver_status = (TextView) mViewRoot.findViewById(R.id.tv_driver_status);

        activity.mBottomNavigationView.setVisibility(View.GONE);

        /*       sosCall = (ImageView) mViewRoot.findViewById(R.id.sosCall);
        sosCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final iOSDialog cancelDialog = new iOSDialog(activity);
                cancelDialog.setTitle("SOS Contact");
                cancelDialog.setSubtitle("Who do you want to call?");

                cancelDialog.setNegativeLabel(requestDetail.getAdminName());
                cancelDialog.setPositiveLabel(requestDetail.getEmergencyName());
                cancelDialog.setBoldPositiveLabel(false);
                cancelDialog.setNegativeListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View mViewRoot) {
                        mobileNo = requestDetail.getAdminNumber();
                        int permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE);

                        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(
                                    new String[]{Manifest.permission.CALL_PHONE}, 123);
                        } else {
                            call();
                        }
                        cancelDialog.dismiss();
                    }
                });
                cancelDialog.setPositiveListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View mViewRoot) {
                        // cancelRide();
                        mobileNo = requestDetail.getEmergencyContact();
                        int permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE);

                        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(
                                    new String[]{Manifest.permission.CALL_PHONE}, 123);
                        } else {
                            call();
                        }
                        cancelDialog.dismiss();
                    }
                });
                cancelDialog.show();
            }
        });*/

        tv_current_location.setSelected(true);

        user_travel_map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.user_travel_map);

        if (null != user_travel_map) {
            user_travel_map.getMapAsync(this);
        }

        cancel_trip = (LinearLayout) mViewRoot.findViewById(R.id.cancel_trip);

        cancel_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final Dialog dialogcancel = new Dialog(activity);

                dialogcancel.setContentView(R.layout.dialog_cancel_booking);

                img_confrim_cancel_booking = dialogcancel.findViewById(R.id.img_confrim_cancel_booking);

                img_no_cancel_booking = dialogcancel.findViewById(R.id.img_no_cancel_booking);

                dialogcancel.show();

                img_confrim_cancel_booking.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        getCancelRideReasonList();
                        stopCheckingforstatus();
                        dialogcancel.dismiss();
                    }
                });

                img_no_cancel_booking.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogcancel.dismiss();
                    }
                });


//                final iOSDialog cancelDialog = new iOSDialog(activity);
//
//                cancelDialog.setTitle(getResources().getString(R.string.cancel_ride));
//                cancelDialog.setSubtitle(getResources().getString(R.string.cancel_txt));
//
//                cancelDialog.setNegativeLabel(getResources().getString(R.string.txt_no));
//                cancelDialog.setPositiveLabel(getResources().getString(R.string.txt_yes));
//                cancelDialog.setBoldPositiveLabel(false);
//
//                cancelDialog.setNegativeListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        cancelDialog.dismiss();
//                    }
//                });
//
//                cancelDialog.setPositiveListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        // cancelRide();
//                        getCancelRideReasonList();
//                        stopCheckingforstatus();
//                        cancelDialog.dismiss();
//                    }
//                });
//                cancelDialog.show();
            }
        });

        ((LinearLayout) mViewRoot.findViewById(R.id.driver_contact)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogConTact_Trip();

//                final iOSDialog ContactDialog = new iOSDialog(activity);
//                ContactDialog.setTitle(getResources().getString(R.string.txt_contact_driver));
//                ContactDialog.setSubtitle(mobileNo);
//
//                ContactDialog.setNegativeLabel(getResources().getString(R.string.txt_call));
//                ContactDialog.setPositiveLabel(getResources().getString(R.string.txt_msg));
//
//                ContactDialog.setBoldPositiveLabel(false);
//
//                ContactDialog.setNegativeListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        mobileNo = requestDetail.getDriver_mobile();
//                        if (!mobileNo.equals("")) {
//
//                            int permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE);
//
//                            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
//                                requestPermissions(
//                                        new String[]{Manifest.permission.CALL_PHONE}, 123);
//                            } else {
//                                call();
//                            }
//
//
//                        }
//                        ContactDialog.dismiss();
//                    }
//                });
//                ContactDialog.setPositiveListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        sendnotification();
//                        if (requestDetail != null) {
//
//                            Intent i = new Intent(activity, ChatActivity.class);
//                            i.putExtra("receiver_id", requestDetail.getDriver_id());
//                            startActivity(i);
//                        }
//                        ContactDialog.dismiss();
//                    }
//                });
//                ContactDialog.show();
            }
        });


        moreLay = (LinearLayout) mViewRoot.findViewById(R.id.moreLay);
        moreLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //      showMoreDialog(savedInstanceState);
                moreLayoutVisibility();


            }
        });


        addEditLay = (LinearLayout) mViewRoot.findViewById(R.id.addEditLay);
        optionsLine = (TextView) mViewRoot.findViewById(R.id.optionsLine);

        stopAddress = (TextView) mViewRoot.findViewById(R.id.stopAddress);
        stopLay = (RelativeLayout) mViewRoot.findViewById(R.id.stopLay);

        addStop = (TextView) mViewRoot.findViewById(R.id.addStop);
        editDestination = (TextView) mViewRoot.findViewById(R.id.editDrop);
        line = (TextView) mViewRoot.findViewById(R.id.lineCenter);

        addStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.valueOf(requestDetail.getIsAdStop()) == 0) {
                    openMap("stop", savedInstanceState);

                } else {
                    Toast.makeText(activity, "Stop already added", Toast.LENGTH_SHORT).show();

                }
            }
        });


        editDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openMap("dest", savedInstanceState);


            }
        });


        return mViewRoot;
    }

    private void DialogConTact_Trip() {

        final Dialog dialog_contact_trip = new Dialog(activity);

        dialog_contact_trip.setContentView(R.layout.dialog_contact_driver);

        TextView txt_contact_numberPhone = dialog_contact_trip.findViewById(R.id.txt_contact_numberPhone);
        ImageView img_confrim_contact_phone = dialog_contact_trip.findViewById(R.id.img_confrim_contact_phone);
        ImageView img_confirm_contact_message = dialog_contact_trip.findViewById(R.id.img_confirm_contact_message);

        txt_contact_numberPhone.setText(mobileNo);

        img_confrim_contact_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mobileNo = requestDetail.getDriver_mobile();

                if (!mobileNo.equals("")) {

                    int permissionCheck = ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE);

                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(
                                new String[]{Manifest.permission.CALL_PHONE}, 123);
                    } else {

                        call();
                    }
                }

                dialog_contact_trip.dismiss();

            }
        });

        img_confirm_contact_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                sendnotification();
                if (requestDetail != null) {

                    Intent i = new Intent(activity, ChatActivity.class);
                    i.putExtra("receiver_id", requestDetail.getDriver_id());
                    startActivity(i);
                }
                dialog_contact_trip.dismiss();

            }
        });


        dialog_contact_trip.show();


    }

    private void moreLayoutVisibility() {

        if (addEditLay.getVisibility() == View.VISIBLE) {
            addEditLay.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
            //    addEditLay.animate().translationY(addEditLay.getHeight());

        } else {
            addEditLay.setVisibility(View.VISIBLE);
            line.setVisibility(View.VISIBLE);
            //   Animation slideUp = AnimationUtils.loadAnimation(activity, R.anim.slide_up);
            //   addEditLay.setAnimation(slideUp);
            //   addEditLay.animate();
            //   slideUp.start();
            // addEditLay.startAnimation(slideUp);
            Log.e("asher", "visibile ");
        }

        if (optionsLine.getVisibility() == View.VISIBLE) {
            optionsLine.setVisibility(View.GONE);
        } else {
            optionsLine.setVisibility(View.VISIBLE);
        }


    }

   /* private void showMoreDialog(final Bundle savedInstanceState) {

        final Dialog dialog = new Dialog(activity, R.style.NewDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(R.color.dialog_trans);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_more);

        TextView addStop = (TextView) dialog.findViewById(R.id.addStop);
        final TextView editDestination = (TextView) dialog.findViewById(R.id.editDrop);

        addStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.valueOf(requestDetail.getIsAdStop()) == 0) {
                    openMap("stop", savedInstanceState);
                    dialog.dismiss();
                } else {
                    Toast.makeText(activity, "Stop already added", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });


        editDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap("dest", savedInstanceState);
                dialog.dismiss();
            }
        });

        dialog.show();


    }*/

    private void openMap(final String type, Bundle savedInstanceState) {


        final Dialog searchMap = new Dialog(activity, R.style.DialogSlideAnim_leftright_Fullscreen);
        searchMap.requestWindowFeature(Window.FEATURE_NO_TITLE);
        searchMap.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        searchMap.setCancelable(true);
        searchMap.setContentView(R.layout.search_map_dialog);
        MapView mMapView = (MapView) searchMap.findViewById(R.id.search_map);
        pin_marker = (ImageView) searchMap.findViewById(R.id.pin_location);

        final Button btn_done = (Button) searchMap.findViewById(R.id.btn_done);
        ImageButton search_dai_back = (ImageButton) searchMap.findViewById(R.id.search_dai_back);
        et_source_dia_address = (AutoCompleteTextView) searchMap.findViewById(R.id.et_source_dia_address);
        if (type.equalsIgnoreCase("dest")) {
            et_source_dia_address.setText(requestDetail.getD_address());
        }
        btn_done.requestFocus();
        final PlacesAutoCompleteAdapter S_placesadapter = new PlacesAutoCompleteAdapter(activity,
                R.layout.autocomplete_list_text);
        et_source_dia_address.setAdapter(S_placesadapter);
        et_source_dia_address.setDropDownBackgroundDrawable(new ColorDrawable(Color.WHITE));


        /*et_source_dia_address.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                EbizworldUtils.hideKeyBoard(activity);
                pin_marker.setImageResource(R.mipmap.pickup_location);
                return false;
            }
        });*/


        search_dai_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*   try {
                    getLatlanfromAddress(URLEncoder.encode(et_source_dia_address.getText().toString(), "utf-8"));
                    if (!(et_destination_dia_address.getText().toString().length() == 0)) {
                        getLocationforDest(URLEncoder.encode(et_destination_dia_address.getText().toString(), "utf-8"));
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }*/
                searchMap.dismiss();
            }
        });

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* try {
                    getLatlanfromAddress(URLEncoder.encode(et_source_dia_address.getText().toString(), "utf-8"));
                    if (!(et_destination_dia_address.getText().toString().length() == 0)) {
                        getLocationfoFtimerDest(URLEncoder.encode(et_destination_dia_address.getText().toString(), "utf-8"));
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }*/
                if (et_source_dia_address.getText().toString() != null && !et_source_dia_address.getText().toString().isEmpty()) {
                    destinationChangedApi(changeLatLng, et_source_dia_address.getText().toString(), type);
                    searchMap.dismiss();
                } else {
                    Toast.makeText(activity, "Please enter address", Toast.LENGTH_SHORT).show();
                }
                moreLayoutVisibility();
                searchMap.dismiss();
            }
        });

        et_source_dia_address.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                et_source_dia_address.setSelection(0);
//                LatLng latLng = getLocationFromAddress(activity, et_source_address.getText().toString());
//                 EbizworldUtils.hideKeyBoard(activity);
                final String selectedSourcePlace = S_placesadapter.getItem(i);
                try {
                    getLatlanfromAddress(URLEncoder.encode(selectedSourcePlace, "utf-8"));

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


            }
        });


        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {

                gMap = googleMap;

                gMap.getUiSettings().setMyLocationButtonEnabled(true);
                gMap.getUiSettings().setMapToolbarEnabled(false);
                gMap.getUiSettings().setScrollGesturesEnabled(true);
                gMap.getUiSettings().setZoomGesturesEnabled(true);
                gMap.getUiSettings().setRotateGesturesEnabled(false);
                gMap.getUiSettings().setTiltGesturesEnabled(false);
                gMap.setTrafficEnabled(false);


                if (getActivity() != null) {

                    try {
                        boolean success = gMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.mapstyle));

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

                changeLatLng = new LatLng(d_latlon.latitude, d_latlon.longitude);
              /*  if (null != googleMap) {
                    MarkerOptions markerOpt = new MarkerOptions();
                    markerOpt.position(changeLatLng);
                    markerOpt.icon(BitmapDescriptorFactory.fromResource(R.mipmap.drop_location));
                    DropMarker = gMap.addMarker(markerOpt);

                }*/
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(changeLatLng,
                        15));

                gMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {
                        getCompleteAddressString(cameraPosition.target);
                        changeLatLng = new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);
                        //   SmoothMoveMarker.animateMarker(DropMarker, gMap.getCameraPosition().target, false, gMap);
                        btn_done.requestFocus();

                    }
                });

            }
        });

        pin_marker.setImageResource(R.mipmap.drop_location);

        searchMap.show();

    }


    private void getCompleteAddressString(LatLng target) {
        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.ADDRESS_API_BASE + target.latitude + "," + target.longitude + "&key=" + Const.GOOGLE_API_KEY);

        EbizworldUtils.appLogDebug("DAT_TRAVELMAP", "map for address" + map);
        new VolleyRequester(activity, Const.GET, map, Const.ServiceCode.ADDRESS_API_BASE, this);
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


    private void destinationChangedApi(LatLng changeLatLng, String destinationAddress, String type) {
        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }
        Commonutils.progressdialog_show(activity, "Updating " + type + "...");
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.UPDATE_ADDRESS);
        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());
        map.put(Const.Params.REQUEST_ID, String.valueOf(new PreferenceHelper(activity).getRequestId()));
        map.put(Const.Params.LATITUDE, String.valueOf(changeLatLng.latitude));
        map.put(Const.Params.LONGITUDE, String.valueOf(changeLatLng.longitude));
        map.put(Const.Params.ADDRESS, destinationAddress);
        if (type.equalsIgnoreCase("stop")) {

            map.put(Const.Params.CHANGE_TYPE, "0");

        } else if (type.equalsIgnoreCase("dest")) {

            map.put(Const.Params.CHANGE_TYPE, "1");

        }
        Log.e("asher", "update Address map" + map.toString());
        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.UPDATE_ADDRESS, this);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case 123:
                call();

                break;

            default:
                break;
        }
    }


    private void call() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + mobileNo));
        startActivity(callIntent);

    }

    private void sendnotification() {

        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        HashMap<String, String> map = new HashMap<>();

        if (new PreferenceHelper(activity).getLoginType().equals(Const.PatientService.PATIENT)) {

            map.put(Const.Params.URL, Const.ServiceType.USER_MESSAGE_NOTIFY + Const.Params.ID + "="
                    + new PreferenceHelper(activity).getUserId() + "&" + Const.Params.TOKEN + "="
                    + new PreferenceHelper(activity).getSessionToken() + "&" + Const.Params.REQUEST_ID + "=" + new PreferenceHelper(activity).getRequestId());

        } else if (new PreferenceHelper(activity).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)) {

            map.put(Const.Params.URL, Const.NursingHomeService.CHAT_NOTIFICATION_URL + Const.Params.ID + "="
                    + new PreferenceHelper(activity).getUserId() + "&" + Const.Params.TOKEN + "="
                    + new PreferenceHelper(activity).getSessionToken() + "&" + Const.Params.REQUEST_ID + "=" + new PreferenceHelper(activity).getRequestId());

        } else if (new PreferenceHelper(activity).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)) {

            map.put(Const.Params.URL, Const.HospitalService.CHAT_NOTIFICATION_URL + Const.Params.ID + "="
                    + new PreferenceHelper(activity).getUserId() + "&" + Const.Params.TOKEN + "="
                    + new PreferenceHelper(activity).getSessionToken() + "&" + Const.Params.REQUEST_ID + "=" + new PreferenceHelper(activity).getRequestId());
        }

        EbizworldUtils.appLogDebug("DAT_TRAVELMAP", "send_notification: " + map.toString());

        new VolleyRequester(activity, Const.GET, map, Const.ServiceCode.USER_MESSAGE_NOTIFY, this);
    }

    private void cancelRide(String reason_id, String reasontext) {
        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }
        Commonutils.progressdialog_show(activity, "Canceling...");
        HashMap<String, String> map = new HashMap<String, String>();

        if (new PreferenceHelper(activity).getLoginType().equals(Const.PatientService.PATIENT)) {

            map.put(Const.Params.URL, Const.ServiceType.CANCEL_RIDE);

        } else if (new PreferenceHelper(activity).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)) {

            map.put(Const.Params.URL, Const.NursingHomeService.CANCEL_TRIP_URL);

        } else if (new PreferenceHelper(activity).getLoginType().equals(Const.HospitalService.HOSPITAL)) {

            map.put(Const.Params.URL, Const.HospitalService.CANCEL_REQUEST_URL);

        }
        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());
        map.put(Const.Params.REQUEST_ID, String.valueOf(new PreferenceHelper(activity).getRequestId()));
        map.put("reason_id", reason_id);
        map.put("cancellation_reason", reasontext);

        Log.d("mahi", "cancel_reg" + map.toString());

        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.CANCEL_RIDE,
                this);
    }

    private void getCancelRideReasonList() {

        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;

        }

        Commonutils.progressdialog_show(activity, "Loading...");
        HashMap<String, String> map = new HashMap<String, String>();


        if (new PreferenceHelper(activity).getLoginType().equals(Const.PatientService.PATIENT)) {

            map.put(Const.Params.URL, Const.ServiceType.CANCEL_REASON);

        } else if (new PreferenceHelper(activity).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)) {

            map.put(Const.Params.URL, Const.NursingHomeService.CANCEL_REASONS_URL);

        } else if (new PreferenceHelper(activity).getLoginType().equals(Const.HospitalService.HOSPITAL)) {

            map.put(Const.Params.URL, Const.HospitalService.CANCEL_REASONS_URL);

        }

        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());

        Log.d("mahi", "cancel_req lst" + map.toString());
        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.CANCEL_REASON,
                this);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = savedInstanceState;
        checkRequestStatus = new Handler();
        cancelReasonLst = new ArrayList<CancelReason>();

        mPathPolygonPoints = new ArrayList<LatLng>();
        mMarkerIcon = BitmapFactory.decodeResource(getResources(), R.drawable.carambulance);

        try {
            mSocket = IO.socket(Const.ServiceType.SOCKET_URL);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("message", onNewMessage);
        mSocket.on("YiiriCustomer joined", onUserJoined);
        mSocket.on("YiiriCustomer left", onUserLeft);
        mSocket.on("typing", onTyping);
        mSocket.on("stop typing", onStopTyping);
        mSocket.connect();

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

        Bundle mBundle = getArguments();
        if (mBundle != null) {
            requestDetail = (RequestDetail) mBundle.getSerializable(
                    Const.REQUEST_DETAIL);

            jobStatus = mBundle.getInt(Const.DRIVER_STATUS,
                    Const.IS_DRIVER_DEPARTED);

            EbizworldUtils.appLogDebug("DAT_TRAVELMAP", "jobStatus " + String.valueOf(jobStatus));

            /*Picasso.get().load(requestDetail.getDriver_picture()).error(R.drawable.defult_user).into(driver_img);
            Picasso.get().load(requestDetail.getDriver_car_picture()).error(R.drawable.carambulance).into(driver_car_img);*/

            Glide.with(activity)
                    .load(requestDetail.getDriver_picture())
                    .apply(new RequestOptions().error(R.drawable.defult_user))
                    .into(driver_img);

//            Glide.with(activity)
//                    .load(requestDetail.getDriver_car_picture())
//                    .apply(new RequestOptions().error(R.drawable.carambulance))
//                    .into(driver_car_img);

            /*Glide.with(activity).load(R.drawable.defult_user).apply(new RequestOptions().error(R.drawable.defult_user)).into(driver_img);
            Glide.with(activity).load(R.drawable.carambulance).apply(new RequestOptions().error(R.drawable.carambulance)).into(driver_car_img);*/


            if (jobStatus == 1 || jobStatus == 2) {
                address_title.setText(activity.getString(R.string.txt_pickup_address));
                address_title.setTextColor(ContextCompat.getColor(activity, R.color.color_background_main));
                tv_current_location.setText(requestDetail.getS_address());
            } else {

                address_title.setText(activity.getString(R.string.txt_drop_address));

                address_title.setTextColor(ContextCompat.getColor(activity, R.color.color_btn_main));

                if (!requestDetail.getD_address().equals("")) {

                    tv_current_location.setText(requestDetail.getD_address());

                } else {

                    tv_current_location.setText(getResources().getString(R.string.not_available));

                }

                if (source_marker != null) {
                    source_marker.hideInfoWindow();

                }
            }

            driver_name.setText(requestDetail.getDriver_name());

            driver_mobile_number.setText(getResources().getString(R.string.txt_mobile) + " " + requestDetail.getDriver_mobile());

            driver_car_number.setText(getResources().getString(R.string.txt_car_no) + " " + requestDetail.getDriver_car_number());

            driver_car_model.setText(requestDetail.getDriver_car_color() + " " + requestDetail.getDriver_car_model());

            mobileNo = requestDetail.getDriver_mobile();

            if (requestDetail.getRequest_type().equals("2") || requestDetail.getRequest_type().equals("3")) {
                moreLay.setVisibility(View.GONE);
            }

        }

    }


    private Bitmap getMarkerBitmapFromViewforsource(String value) {

        View customMarkerView = ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.source_infowindow, null);
        ImageView info_iv = (ImageView) customMarkerView.findViewById(R.id.info_iv);
        if (value.equals("1")) {
            info_iv.setImageResource(R.mipmap.pickup_location);
            //info_iv.setImageResource(R.mipmap.pickup_location);

        } else {
            info_iv.setImageResource(R.mipmap.drop_location);
        }
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

    private Bitmap getMarkerBitmapFromView(String eta, String value) {
        String time = eta.replaceAll("\\s+", "\n");
        View customMarkerView = ((LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.eta_info_window, null);
        TextView markertext = (TextView) customMarkerView.findViewById(R.id.txt_eta);
        ImageView iv = (ImageView) customMarkerView.findViewById(R.id.eta_iv);
        if (value.equals("1")) {
            iv.setImageResource(R.drawable.s_eta_circle);
        } else {
            iv.setImageResource(R.drawable.d_eta_circle);
        }
        markertext.setText(time);
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

        new VolleyRequester(activity, Const.GET, map, Const.ServiceCode.GOOGLE_DIRECTION_API, this);

    }


    @Override
    public void onResume() {
        super.onResume();
        activity.currentFragment = Const.TRAVEL_MAP_FRAGMENT;
        startgetreqstatus();
        //Log.e("mahi", "Trip fragment");

    }

    @Override
    public void onLocationReceived(LatLng latlong) {

        if (mSocket.connected()) {
            attemptSend(latlong);

        }

    }

    private void attemptSend(LatLng latlong) {

        if (!mSocket.connected()) return;

        JSONObject messageObj = new JSONObject();
        try {
            messageObj.put("latitude", String.valueOf(latlong.latitude));
            messageObj.put("longitude", String.valueOf(latlong.longitude));
            messageObj.put("sender", new PreferenceHelper(activity).getUserId());
            if (null != requestDetail.getDriver_id()) {
                messageObj.put("receiver", requestDetail.getDriver_id());
            }
            messageObj.put("status", "1");
            messageObj.put("request_id", new PreferenceHelper(activity).getRequestId());
            Log.e("mahi", "calling socket" + messageObj.toString());

            mSocket.emit("send location", messageObj);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLocationReceived(Location location) {

    }

    @Override
    public void onConntected(Bundle bundle) {

    }

    @Override
    public void onConntected(Location location) {
        if (null != googleMap) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(s_latlon,
                    17));
        }
    }

    public void drawPath(String result) {

        Log.d("Dat_Preference", new PreferenceHelper(activity).getOverViewPolyline().toString());

//        RequestOptional mOptinal = new RequestOptional();
        //Tranform the string into a json object

//        final JSONObject json;
//        try {
//            json = new JSONObject(result);
//            JSONArray routeArray = json.getJSONArray("routes");
//            JSONObject routes = routeArray.getJSONObject(0);
//            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
//            String encodedString = overviewPolylines.getString("points");
//            String encodedString = mOptinal.getOverView_Polyline().toString();

        String encodedString = new PreferenceHelper(activity).getOverViewPolyline().toString();

        Log.d("Dat_PolylinesData", encodedString);
        List<LatLng> list = decodePoly(encodedString);


        PolylineOptions options = new PolylineOptions().width(8).color(Color.BLACK).geodesic(true);

        for (int z = 0; z < list.size(); z++) {
            LatLng point = list.get(z);
            options.add(point);
        }
        if (googleMap != null) {
            if (poly_line != null) {
                poly_line.remove();
            }
            poly_line = googleMap.addPolyline(options);
        }

//        } catch (JSONException e) {
//            e.printStackTrace();
//        }


//           for(int z = 0; z<list.size()-1;z++){
//                LatLng src= list.get(z);
//                LatLng dest= list.get(z+1);
//                Polyline line = mMap.addPolyline(new PolylineOptions()
//                .add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude,   dest.longitude))
//                .width(2)
//                .color(Color.BLUE).geodesic(true));
//            }
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

    private void startgetreqstatus() {
        startCheckstatusTimer();
    }

    private void startCheckstatusTimer() {
        checkRequestStatus.postDelayed(checkRequestStatusRunnable, 4000);
    }

    private void stopCheckingforstatus() {
        if (checkRequestStatus != null) {
            checkRequestStatus.removeCallbacks(checkRequestStatusRunnable);

            Log.d("mahi", "stop status handler");
        }
    }

    Runnable checkRequestStatusRunnable = new Runnable() {
        public void run() {
            checkreqstatus();
            checkRequestStatus.postDelayed(this, 5000);
        }
    };

    private void checkreqstatus() {
        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }
        HashMap<String, String> map = new HashMap<String, String>();

        if (new PreferenceHelper(getActivity()).getLoginType().equals(Const.PatientService.PATIENT)) {

            map.put(Const.Params.URL, Const.ServiceType.CHECKREQUEST_STATUS);

        } else if (new PreferenceHelper(getActivity()).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)) {

            map.put(Const.Params.URL, Const.NursingHomeService.REQUEST_STATUS_CHECK_URL);

        } else if (new PreferenceHelper(getActivity()).getLoginType().equals(Const.HospitalService.HOSPITAL)) {

            map.put(Const.Params.URL, Const.HospitalService.CHECK_REQUEST_STATUS_URL);

        }
        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());
        Log.d("DAT_TRAVELMAP", map.toString());
        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.CHECKREQUEST_STATUS,
                this);
    }

    private void fitmarkers_toMap() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

//the include method will calculate the min and max bound.
        builder.include(source_marker.getPosition());
        builder.include(destination_marker.getPosition());
        ;

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 12% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        googleMap.moveCamera(cu);


      /* if (s_latlon != null) {

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(s_latlon)
                    .zoom(17).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            //  source_marker.showInfoWindow();

        }*/


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        SupportMapFragment f = (SupportMapFragment) getFragmentManager()
                .findFragmentById(R.id.user_travel_map);
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


        googleMap = null;
    }


    private void findDistanceAndTime(LatLng s_latlan, LatLng d_latlan) {
        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }
        if (null != s_latlan && null != d_latlan) {
            HashMap<String, String> map = new HashMap<>();
            map.put(Const.Params.URL, Const.GOOGLE_MATRIX_URL + Const.Params.ORIGINS + "="
                    + String.valueOf(s_latlan.latitude) + "," + String.valueOf(s_latlan.longitude) + "&" + Const.Params.DESTINATION + "="
                    + String.valueOf(d_latlan.latitude) + "," + String.valueOf(d_latlan.longitude) + "&" + Const.Params.MODE + "="
                    + "driving" + "&" + Const.Params.LANGUAGE + "="
                    + "en-EN" + "&" + "key=" + Const.GOOGLE_API_KEY + "&" + Const.Params.SENSOR + "="
                    + String.valueOf(false));
            Log.e("mahi", "distance api" + map);
            new VolleyRequester(activity, Const.GET, map, Const.ServiceCode.GOOGLE_MATRIX, this);
        } else {
            if (null != source_marker) {
                source_marker.setIcon((BitmapDescriptorFactory
                        .fromBitmap(getMarkerBitmapFromViewforsource("1"))));

            }
        }
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {

        switch (serviceCode) {

            case Const.ServiceCode.UPDATE_ADDRESS:
                EbizworldUtils.appLogInfo("DAT_TRAVELMAP", "update address response " + response);
                Commonutils.progressdialog_hide();
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("success").equals("true")) {

                        if (jsonObject.getString("change_type").equalsIgnoreCase("0")) {

                            requestDetail.setAdStopLatitude(jsonObject.optString("adstop_latitude"));
                            requestDetail.setAdStopLongitude(jsonObject.optString("adstop_longitude"));
                            requestDetail.setAdStopAddress(jsonObject.optString("adstop_address"));
                            requestDetail.setIsAdStop(jsonObject.optString("is_adstop"));


                            if (Integer.valueOf(requestDetail.getIsAdStop()) == 1) {

                                if (stopLay.getVisibility() == View.GONE) {
                                    stopLay.setVisibility(View.VISIBLE);
                                    stopAddress.setText(requestDetail.getAdStopAddress());
                                }
                            }

                            getDirectionsWay(Double.valueOf(requestDetail.getS_lat()), Double.valueOf(requestDetail.getS_lng()), Double.valueOf(requestDetail.getD_lat()), Double.valueOf(requestDetail.getD_lng()),
                                    Double.valueOf(jsonObject.getString("adstop_latitude")), Double.valueOf(jsonObject.getString("adstop_longitude")));


                        } else if (jsonObject.getString("change_type").equalsIgnoreCase("1")) {

                            requestDetail.setD_lat(jsonObject.getString("d_latitude"));
                            requestDetail.setD_lng(jsonObject.getString("d_longitude"));
                            requestDetail.setD_address(jsonObject.getString("d_address"));
                            requestDetail.setIsAddressChanged(jsonObject.getString("is_address_changed"));

                            if (Integer.valueOf(requestDetail.getIsAdStop()) == 1) {

                                getDirectionsWay(Double.valueOf(requestDetail.getS_lat()), Double.valueOf(requestDetail.getS_lng()), Double.valueOf(jsonObject.getString("d_latitude")), Double.valueOf(jsonObject.getString("d_longitude")),
                                        Double.valueOf(requestDetail.getAdStopLatitude()), Double.valueOf(requestDetail.getAdStopLongitude()));
                            } else {

                                getDirections(Double.valueOf(requestDetail.getS_lat()), Double.valueOf(requestDetail.getS_lng()), Double.valueOf(jsonObject.getString("d_latitude")), Double.valueOf(jsonObject.getString("d_longitude")));
                            }


                        }

                    } else if (jsonObject.getString("success").equals("false")) {
                        Toast.makeText(activity, jsonObject.getString("error"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {

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
                                et_source_dia_address.setText(locObj.optString("formatted_address"));

                            }
                        });

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
                        changeLatLng = new LatLng(lat, lan);
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(changeLatLng,
                                15));
                        //    DropMarker.setPosition(changeLatLng);
                        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(changeLatLng, 16));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;


            case Const.ServiceCode.GOOGLE_DIRECTION_API:

                if (response != null) {

                    if (Integer.valueOf(requestDetail.getIsAdStop()) == 1 && stop_marker == null) {

                        stop_latlng = new LatLng(Double.valueOf(requestDetail.getAdStopLatitude()), Double.valueOf(requestDetail.getAdStopLongitude()));

                        MarkerOptions opt = new MarkerOptions();
                        opt.position(stop_latlng);
                        //       opt.title(activity.getResources().getString(R.string.txt_drop_loc));
                        opt.anchor(0.5f, 0.5f);
                        opt.icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.pin_stop));
                        stop_marker = googleMap.addMarker(opt);


                    }
                    if (destination_marker != null) {
                        destination_marker.remove();

                        d_latlon = new LatLng(Double.valueOf(requestDetail.getD_lat()), Double.valueOf(requestDetail.getD_lng()));

                        MarkerOptions opt = new MarkerOptions();
                        opt.position(d_latlon);
                        //       opt.title(activity.getResources().getString(R.string.txt_drop_loc));
                        opt.anchor(0.5f, 0.5f);
                        opt.icon(BitmapDescriptorFactory
                                .fromResource(R.mipmap.drop_location));
                        destination_marker = googleMap.addMarker(opt);

                    } else {

                        d_latlon = new LatLng(Double.valueOf(requestDetail.getD_lat()), Double.valueOf(requestDetail.getD_lng()));

                        MarkerOptions opt = new MarkerOptions();
                        opt.position(d_latlon);
                        //       opt.title(activity.getResources().getString(R.string.txt_drop_loc));
                        opt.anchor(0.5f, 0.5f);
                        opt.icon(BitmapDescriptorFactory
                                .fromResource(R.mipmap.drop_location));
                        destination_marker = googleMap.addMarker(opt);

                    }

                    drawPath(response);

                }
                break;

            case Const.ServiceCode.CANCEL_REASON:
                EbizworldUtils.appLogInfo("DAT_TRAVELMAP", "cancel reason: " + response);
                Commonutils.progressdialog_hide();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    cancelReasonLst.clear();

                    if (jsonObject.getString("success").equals("true")) {

                        JSONArray dataArray = jsonObject.optJSONArray("data");

                        if (null != dataArray && dataArray.length() > 0)

                            for (int i = 0; i < dataArray.length(); i++) {

                                JSONObject dataObj = dataArray.optJSONObject(i);
                                CancelReason cancel = new CancelReason();
                                cancel.setReasonId(dataObj.optString("id"));
                                cancel.setReasontext(dataObj.optString("cancel_reason"));
                                cancelReasonLst.add(cancel);

                            }

                        if (null != cancelReasonLst && cancelReasonLst.size() > 0) {

                            CancelReasonDialog(cancelReasonLst);

                        } else {

                            EbizworldUtils.showShortToast(getResources().getString(R.string.txt_no_cancel_reason), activity);

                        }
                    } else {

                        EbizworldUtils.showShortToast(jsonObject.optString("error_message"), activity);
                        EbizworldUtils.appLogError("DAT_TRAVELMAP", "Cancel reason: " + jsonObject.getString("error_message"));

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    EbizworldUtils.appLogError("DAT_TRAVELMAP", "Cancel reason: " + e.toString());
                }

                break;

            case Const.ServiceCode.USER_MESSAGE_NOTIFY:
                EbizworldUtils.appLogInfo("DAT_TRAVELMAP", "notify trip" + response);

                if (response != null) {

                }
                break;

            case Const.ServiceCode.CANCEL_RIDE:
                Log.d("mahi", "cancel request" + response);
                Commonutils.progressdialog_hide();

                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("success").equals("true")) {

                        String message_cancle_user = jsonObject.getString("cancellation_message");

                        DialogCancelByUser(message_cancle_user);

                        new PreferenceHelper(activity).clearRequestData();
                       // getActivity().onBackPressed();
                        activity.addFragment(new SearchPlaceFragment(), false, Const.HOME_MAP_FRAGMENT, true);


//                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(activity);
//
//                        builder.setMessage(activity.getResources().getString(R.string.txt_cancle_user1)+"S$ " + money_cancle_user+" "+activity.getResources().getString(R.string.txt_cancle_user2));
//
//                        builder.setCancelable(false);
//
//                        builder.setNegativeButton(getResources().getString(R.string.txt_btn_done), new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//
//                                dialogInterface.dismiss();
//
//                            }
//                        });
//
//                        android.support.v7.app.AlertDialog alertDialog = builder.create();
//                        alertDialog.show();


                        // activity.addFragment(new SearchPlaceFragment(), false, Const.SEARCH_FRAGMENT, true);

                    } else {
                        Commonutils.progressdialog_hide();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    EbizworldUtils.appLogDebug("DAT_TRAVELMAP", "Cancel request failed " + e.toString());
                }
                break;

            case Const.ServiceCode.GOOGLE_MATRIX:
                Log.d("mahi", "google distance api" + response);
                try {
                    if (googleMap != null) {

                        googleMap.getUiSettings().setScrollGesturesEnabled(true);

                    }
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
                        eta_time = duration;
                        if (pickup_opt != null && source_marker != null) {
                            // Commonutils.showtoast("showing",activity);
                            if (jobStatus == 1 || jobStatus == 2 || jobStatus == 3) {
                                activity.runOnUiThread(new Runnable() {
                                    public void run() {
                                        if (jobStatus == 3) {
                                            source_marker.setIcon((BitmapDescriptorFactory
                                                    .fromBitmap(getMarkerBitmapFromView("0 MIN", "1"))));
                                        } else {
                                            source_marker.setIcon((BitmapDescriptorFactory
                                                    .fromBitmap(getMarkerBitmapFromView(duration, "1"))));
                                        }
                                        if (null != destination_marker) {
                                            destination_marker.setIcon((BitmapDescriptorFactory
                                                    .fromBitmap(getMarkerBitmapFromViewforsource("2"))));
                                        }

                                    }
                                });

                            } else {
                                activity.runOnUiThread(new Runnable() {
                                    public void run() {

                                        source_marker.setIcon((BitmapDescriptorFactory
                                                .fromBitmap(getMarkerBitmapFromViewforsource("1"))));
                                        if (null != destination_marker) {
                                            destination_marker.setIcon((BitmapDescriptorFactory
                                                    .fromBitmap(getMarkerBitmapFromView(duration, "2"))));
                                        }

                                    }
                                });

                            }
                            // pickup_marker = googleMap.addMarker(pickup_opt);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case Const.ServiceCode.CHECKREQUEST_STATUS:
                EbizworldUtils.appLogInfo("DAT_TRAVELMAP", "check req status: " + response);

                if (response != null) {
                    int money_driver_cancel = 0;

                    Bundle bundle = new Bundle();
                    RequestDetail requestDetail = new ParseContent(activity).parseRequestStatusNormal(response);
                    TravelMapFragment travalfragment = new TravelMapFragment();

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.has("cancellation_fine")) {

                            money_driver_cancel = jsonObject.getInt("cancellation_fine");

                        }else {

                            Log.d("DAT_TRAVELMAP","null");

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (requestDetail == null) {
                        return;
                    }

                    EbizworldUtils.appLogDebug("DAT_TRAVELMAP", "Trip status " + requestDetail.getTripStatus());
                    switch (requestDetail.getTripStatus()) {

                        case Const.NO_REQUEST:

                            if (isAdded() && iscancelpopup == false && googleMap != null && activity.currentFragment.equals(Const.TRAVEL_MAP_FRAGMENT)) {

                                iscancelpopup = true;

                                stopCheckingforstatus();

                                DialogCacelByDriver(money_driver_cancel);

//                                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//                                builder.setMessage(activity.getResources().getString(R.string.txt_cancel_driver1)+" S$ "+money_driver_cancel+" "+activity.getResources().getString(R.string.txt_cancel_driver2))
//                                        .setCancelable(false)
//                                        .setPositiveButton(getResources().getString(R.string.txt_ok), new DialogInterface.OnClickListener() {
//                                            public void onClick(DialogInterface dialog, int id) {
//
//                                                new PreferenceHelper(activity).clearRequestData();
//                                                //googleMap.clear();
//                                                dialog.dismiss();
//                                                getActivity().onBackPressed();
//                                                // activity.addFragment(new SearchPlaceFragment(), false, Const.SEARCH_FRAGMENT, true);
//
//                                            }
//                                        });
//                                AlertDialog alert = builder.create();
//                                alert.show();
                            }
                            break;

                        case Const.IS_ACCEPTED: {

                            jobStatus = Const.IS_ACCEPTED;

                            address_title.setText(activity.getString(R.string.txt_pickup_address));
                            address_title.setTextColor(ContextCompat.getColor(activity, R.color.green));
                            tv_current_location.setText(requestDetail.getS_address());
                            tv_driver_status.setText(activity.getString(R.string.text_job_accepted));

//                            addNotification(activity.getString(R.string.text_job_accepted));

                            findDistanceAndTime(s_latlon, driver_latlan);
                            if (Integer.valueOf(requestDetail.getIsAdStop()) == 1 && stop_marker == null) {
                                getDirectionsWay(s_latlon.latitude, s_latlon.longitude, d_latlon.latitude, d_latlon.longitude, stop_latlng.latitude, stop_latlng.longitude);
                            }
                            if (Integer.valueOf(requestDetail.getIsAdStop()) == 1) {

                                if (stopLay.getVisibility() == View.GONE) {
                                    stopLay.setVisibility(View.VISIBLE);
                                    stopAddress.setText(requestDetail.getAdStopAddress());
                                }
                            }

                            EbizworldUtils.appLogDebug("DAT_TRAVELMAP", "Driver is accepted " + String.valueOf(jobStatus));

                        }
                        break;

                        case Const.IS_DRIVER_DEPARTED: {

                            jobStatus = Const.IS_DRIVER_DEPARTED;
                            address_title.setText(activity.getString(R.string.txt_pickup_address));
                            address_title.setTextColor(ContextCompat.getColor(activity, R.color.green));
                            tv_current_location.setText(requestDetail.getS_address());
                            tv_driver_status.setText(activity.getString(R.string.text_driver_started));
//                            addNotification(activity.getString(R.string.text_driver_started));
                            if (Integer.valueOf(requestDetail.getIsAdStop()) == 1 && stop_marker == null) {
                                getDirectionsWay(s_latlon.latitude, s_latlon.longitude, d_latlon.latitude, d_latlon.longitude, stop_latlng.latitude, stop_latlng.longitude);
                            }
                            findDistanceAndTime(s_latlon, driver_latlan);
                            if (Integer.valueOf(requestDetail.getIsAdStop()) == 1) {
                                if (stopLay.getVisibility() == View.GONE) {
                                    stopLay.setVisibility(View.VISIBLE);
                                    stopAddress.setText(requestDetail.getAdStopAddress());
                                }
                            }

                            EbizworldUtils.appLogDebug("DAT_TRAVELMAP", "Driver is departed " + String.valueOf(jobStatus));

                        }
                        break;

                        case Const.IS_DRIVER_ARRIVED: {

                            jobStatus = Const.IS_DRIVER_ARRIVED;
                            address_title.setText(activity.getString(R.string.txt_drop_address));
                            address_title.setTextColor(ContextCompat.getColor(activity, R.color.red));
                            if (!requestDetail.getD_address().equals("")) {
                                tv_current_location.setText(requestDetail.getD_address());
                            } else {
                                tv_current_location.setText(getResources().getString(R.string.not_available));
                            }
                            tv_driver_status.setText(activity.getString(R.string.text_driver_arrvied));
//                            addNotification(activity.getString(R.string.text_driver_arrvied));
                            if (Integer.valueOf(requestDetail.getIsAdStop()) == 1 && stop_marker == null) {

                                getDirectionsWay(s_latlon.latitude, s_latlon.longitude, d_latlon.latitude, d_latlon.longitude, stop_latlng.latitude, stop_latlng.longitude);

                            }
                            if (Integer.valueOf(requestDetail.getIsAdStop()) == 1) {

                                if (stopLay.getVisibility() == View.GONE) {
                                    stopLay.setVisibility(View.VISIBLE);
                                    stopAddress.setText(requestDetail.getAdStopAddress());
                                }
                            }

                            driver_latlan = new LatLng(requestDetail.getDriver_latitude(), requestDetail.getDriver_longitude());
                            findDistanceAndTime(s_latlon, driver_latlan);

                            EbizworldUtils.appLogDebug("DAT_TRAVELMAP", "Driver arrived " + String.valueOf(jobStatus));

                        }
                        break;
                        case Const.IS_DRIVER_TRIP_STARTED: {

                            cancel_trip.setVisibility(View.GONE);
                            jobStatus = Const.IS_DRIVER_TRIP_STARTED;
                            address_title.setText(activity.getString(R.string.txt_drop_address));
                            address_title.setTextColor(ContextCompat.getColor(activity, R.color.red));
                            if (!requestDetail.getD_address().equals("")) {
                                tv_current_location.setText(requestDetail.getD_address());
                            } else {
                                tv_current_location.setText(getResources().getString(R.string.not_available));
                            }
                            tv_driver_status.setText(activity.getString(R.string.text_trip_started));
//                            addNotification(activity.getString(R.string.text_trip_started));
                            if (Integer.valueOf(requestDetail.getIsAdStop()) == 1 && stop_marker == null) {

                                getDirectionsWay(s_latlon.latitude, s_latlon.longitude, d_latlon.latitude, d_latlon.longitude, stop_latlng.latitude, stop_latlng.longitude);

                            }
                            if (Integer.valueOf(requestDetail.getIsAdStop()) == 1) {

                                if (stopLay.getVisibility() == View.GONE) {
                                    stopLay.setVisibility(View.VISIBLE);
                                    stopAddress.setText(requestDetail.getAdStopAddress());
                                }
                            }
                            cancel_trip.setVisibility(View.GONE);
                            findDistanceAndTime(d_latlon, driver_latlan);

                            EbizworldUtils.appLogDebug("DAT_TRAVELMAP", "Trip is started " + String.valueOf(jobStatus));
                        }
                        break;

                        case Const.IS_DRIVER_TRIP_ENDED: {
                            /*jobStatus = Const.IS_DRIVER_TRIP_ENDED;
                            address_title.setText(activity.getString(R.string.txt_drop_address));
                            address_title.setTextColor(ContextCompat.getColor(activity, R.color.red));
                            if (!requestDetail.getD_address().equals("")) {
                                tv_current_location.setText(requestDetail.getD_address());
                            } else {
                                tv_current_location.setText(getResources().getString(R.string.not_available));
                            }
                            tv_driver_status.setText(activity.getString(R.string.text_trip_completed));
//                            addNotification(activity.getString(R.string.text_trip_completed));
                            cancel_trip.setVisibility(View.GONE);
                            findDistanceAndTime(d_latlon, driver_latlan);
                            bundle.putSerializable(Const.REQUEST_DETAIL,
                                    requestDetail);
                            bundle.putInt(Const.DRIVER_STATUS,
                                    Const.IS_DRIVER_TRIP_ENDED);


                            if (!activity.currentFragment.equals(Const.RATING_FRAGMENT) && !activity.isFinishing()) {

                                stopCheckingforstatus();

                                RatingFragment feedbackFragment = new RatingFragment();
                                feedbackFragment.setArguments(bundle);
                                if (activity != null)
                                    activity.addFragment(feedbackFragment, false, Const.RATING_FRAGMENT,
                                            true);
                            }*/

                            if (!activity.currentFragment.equals(Const.BILLING_INFO_FRAGMENT) && !activity.isFinishing()) {

                                stopCheckingforstatus();

                                BillingInfoFragment billingInfoFragment = new BillingInfoFragment();
                                billingInfoFragment.setArguments(bundle);

                                activity.addFragment(billingInfoFragment, false, Const.BILLING_INFO_FRAGMENT, true);

                            }

                            EbizworldUtils.appLogDebug("DAT_TRAVELMAP", "Trip end " + String.valueOf(jobStatus));

                        }
                        break;
                        case Const.IS_BILLING: {

                            jobStatus = Const.IS_DRIVER_TRIP_ENDED;
                            address_title.setText(activity.getString(R.string.txt_drop_address));
                            address_title.setTextColor(ContextCompat.getColor(activity, R.color.color_btn_main));
                            findDistanceAndTime(d_latlon, driver_latlan);
                            if (!requestDetail.getD_address().equals("")) {
                                tv_current_location.setText(requestDetail.getD_address());
                            } else {
                                tv_current_location.setText(getResources().getString(R.string.not_available));
                            }
                            tv_driver_status.setText(activity.getString(R.string.text_trip_completed));

                            bundle.putSerializable(Const.REQUEST_DETAIL,
                                    requestDetail);
                            bundle.putInt(Const.DRIVER_STATUS,
                                    Const.IS_DRIVER_TRIP_ENDED);
                            cancel_trip.setVisibility(View.GONE);

                            if (!activity.currentFragment.equals(Const.RATING_FRAGMENT)) {

                                stopCheckingforstatus();

                                RatingFragment feedbackFragment = new RatingFragment();
                                feedbackFragment.setArguments(bundle);
                                activity.addFragment(feedbackFragment, false, Const.RATING_FRAGMENT,
                                        true);
                            }

                            EbizworldUtils.appLogDebug("DAT_TRAVELMAP", "Trip rated " + String.valueOf(jobStatus));
                        }

                        break;

                        case Const.IS_DRIVER_RATED: {

                            jobStatus = Const.IS_DRIVER_TRIP_ENDED;
                            address_title.setText(activity.getString(R.string.txt_drop_address));
                            address_title.setTextColor(ContextCompat.getColor(activity, R.color.color_btn_main));
                            findDistanceAndTime(d_latlon, driver_latlan);
                            if (!requestDetail.getD_address().equals("")) {
                                tv_current_location.setText(requestDetail.getD_address());
                            } else {
                                tv_current_location.setText(getResources().getString(R.string.not_available));
                            }
                            tv_driver_status.setText(activity.getString(R.string.text_trip_completed));

                            bundle.putSerializable(Const.REQUEST_DETAIL,
                                    requestDetail);
                            bundle.putInt(Const.DRIVER_STATUS,
                                    Const.IS_DRIVER_TRIP_ENDED);
                            cancel_trip.setVisibility(View.GONE);

                            if (!activity.currentFragment.equals(Const.RATING_FRAGMENT)) {

                                stopCheckingforstatus();

                                RatingFragment feedbackFragment = new RatingFragment();
                                feedbackFragment.setArguments(bundle);
                                activity.addFragment(feedbackFragment, false, Const.RATING_FRAGMENT,
                                        true);
                            }

                            EbizworldUtils.appLogDebug("DAT_TRAVELMAP", "Trip rated " + String.valueOf(jobStatus));
                        }

                        break;
                        default:
                            break;


                    }
                }
        }
    }

    private void DialogCancelByUser(String message_cancle_user) {

        final Dialog dialog_cancel_by_user = new Dialog(activity);

        dialog_cancel_by_user.setContentView(R.layout.dialog_notification_canceled_trip);

        dialog_cancel_by_user.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);


        Window window = dialog_cancel_by_user.getWindow();

        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        wlp.gravity = Gravity.CENTER;

        window.setAttributes(wlp);


        TextView txt_show_thereason_canceled = dialog_cancel_by_user.findViewById(R.id.txt_show_thereason_canceled);

        txt_show_thereason_canceled.setText(message_cancle_user);

        // txt_show_thereason_canceled.setText(activity.getResources().getString(R.string.txt_cancle_user1)+" S$" + money_cancle_user+" "+activity.getResources().getString(R.string.txt_cancle_user2));

        final Button btn_dialog_thereason_confirm = dialog_cancel_by_user.findViewById(R.id.btn_dialog_thereason_confirm);

        btn_dialog_thereason_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //googleMap.clear();
                dialog_cancel_by_user.dismiss();


            }
        });

        dialog_cancel_by_user.show();

    }

    private void DialogCacelByDriver(int money_driver_cancel) {

        final Dialog dialog_cancel_by_driver = new Dialog(activity);

        dialog_cancel_by_driver.setContentView(R.layout.dialog_notification_canceled_trip);

        dialog_cancel_by_driver.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);


        Window window = dialog_cancel_by_driver.getWindow();

        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        wlp.gravity = Gravity.CENTER;

        window.setAttributes(wlp);

        TextView txt_show_thereason_canceled = dialog_cancel_by_driver.findViewById(R.id.txt_show_thereason_canceled);


        txt_show_thereason_canceled.setText(activity.getResources().getString(R.string.txt_cancel_driver1) + " S$ " + money_driver_cancel + " " + activity.getResources().getString(R.string.txt_cancel_driver2));

        final Button btn_dialog_thereason_confirm = dialog_cancel_by_driver.findViewById(R.id.btn_dialog_thereason_confirm);

        btn_dialog_thereason_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new PreferenceHelper(activity).clearRequestData();
                //googleMap.clear();
                dialog_cancel_by_driver.dismiss();

                getActivity().onBackPressed();

            }
        });


        dialog_cancel_by_driver.show();
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isConnected) {

                        try {
                            if (requestDetail.getDriver_id() != null) {
                                JSONObject object = new JSONObject();
                                object.put("sender", new PreferenceHelper(activity).getUserId());
                                object.put("receiver", requestDetail.getDriver_id());
                                Log.e("update_object", "" + object);
                                mSocket.emit("update sender", object);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        isConnected = true;
                    }
                    if (isConnected) {


                        try {
                            if (requestDetail.getDriver_id() != null) {
                                JSONObject object = new JSONObject();
                                object.put("sender", new PreferenceHelper(activity).getUserId());
                                object.put("receiver", requestDetail.getDriver_id());
                                Log.e("update_object", "" + object);
                                mSocket.emit("update sender", object);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isConnected = false;

                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String sender;
                    String receiver;
                    String latitude = "";
                    String longitude = "";
                    String bearing;
                    String location;

                    try {

                        sender = data.getString("sender");
                        receiver = data.getString("receiver");
                        latitude = data.getString("latitude");
                        longitude = data.getString("longitude");
                        bearing = data.getString("bearing");


                        Log.d("mahi", "message from socket" + data.toString());
                        if (googleMap != null) {

                            if(latitude == ""&& longitude == "" && driver_latlan.toString().isEmpty()){


                            }else {

                                driver_latlan = new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
                                delayLatlan = driver_latlan;
                                Location driver_location = new Location("Driver Location");
                                driver_location.setLatitude(driver_latlan.latitude);
                                driver_location.setLongitude(driver_latlan.longitude);

                                if (driver_car == null && null != driver_latlan && null != googleMap) {
                                    driver_car = googleMap.addMarker(new MarkerOptions()
                                            .position(driver_latlan)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.carambulance))
                                            .title(getResources().getString(R.string.txt_driver)));

                                    AnimateMarker.animateMarker(activity, driver_location, driver_car, googleMap, bearing);
                                } else {
                                    AnimateMarker.animateMarker(activity, driver_location, driver_car, googleMap, bearing);
                                }
                            }


                        }

                    } catch (JSONException e) {
                        return;
                    }


                }

            });
        }
    };

    private Emitter.Listener onUserJoined = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    int numUsers;
                    try {

                        username = data.getString("username");
                        numUsers = data.getInt("numUsers");

                    } catch (JSONException e) {
                        return;
                    }
/*
                    addLog(getResources().getString(R.string.message_user_joined, username));
                    addParticipantsLog(numUsers);*/
                }
            });
        }
    };

    private Emitter.Listener onUserLeft = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    int numUsers;

                    try {

                        username = data.getString("username");
                        numUsers = data.getInt("numUsers");

                    } catch (JSONException e) {
                        return;
                    }

                }
            });
        }
    };

    private Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String username;
                    try {

                        username = data.getString("username");

                    } catch (JSONException e) {
                        return;
                    }

                }
            });
        }
    };

    private Emitter.Listener onStopTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    JSONObject data = (JSONObject) args[0];
                    String username;

                    try {

                        username = data.getString("username");

                    } catch (JSONException e) {
                        return;
                    }

                }
            });
        }
    };

  /*  private Runnable onTypingTimeout = new Runnable() {
        @Override
        public void run() {
            if (!mTyping) return;

            mTyping = false;
            mSocket.emit("stop typing");
        }
    };*/

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopCheckingforstatus();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopCheckingforstatus();

        mSocket.disconnect();

        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("message", onNewMessage);
        mSocket.off("YiiriCustomer joined", onUserJoined);
        mSocket.off("YiiriCustomer left", onUserLeft);
        mSocket.off("typing", onTyping);
        mSocket.off("stop typing", onStopTyping);
    }


    @Override
    public void onMapReady(GoogleMap mgoogleMap) {
        // map.
        googleMap = mgoogleMap;
        EbizworldUtils.removeProgressDialog();

        if (googleMap != null) {
            googleMap.setTrafficEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
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

                // EbizworldUtils.removeLoader();
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the YiiriCustomer grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                googleMap.setMyLocationEnabled(false);

                LocationManager lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
                List<String> providers = lm.getProviders(true);
                Location l = null;

                if (requestDetail.getS_lat() != null) {
                    s_latlon = new LatLng(Double.valueOf(requestDetail.getS_lat()), Double.valueOf(requestDetail.getS_lng()));

                    pickup_opt = new MarkerOptions();
                    pickup_opt.position(s_latlon);
                    pickup_opt.title(getResources().getString(R.string.txt_pickup));
                    pickup_opt.anchor(0.5f, 0.5f);
                    pickup_opt.icon(BitmapDescriptorFactory
                            .fromBitmap(getMarkerBitmapFromView(eta_time, "1")));
                    source_marker = googleMap.addMarker(pickup_opt);
                }
                if (requestDetail.getD_lat() != null && !requestDetail.getD_lat().equals("") && !requestDetail.getD_address().equals("")) {
                    d_latlon = new LatLng(Double.valueOf(requestDetail.getD_lat()), Double.valueOf(requestDetail.getD_lng()));

                    MarkerOptions opt = new MarkerOptions();
                    opt.position(d_latlon);
                    opt.title(activity.getResources().getString(R.string.txt_drop_loc));
                    opt.anchor(0.5f, 0.5f);
                    opt.icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.map_drop_marker));
                    destination_marker = googleMap.addMarker(opt);
                }

                driver_latlan = new LatLng(requestDetail.getDriver_latitude(), requestDetail.getDriver_longitude());


                if (d_latlon != null && s_latlon != null) {

                    if (Integer.valueOf(requestDetail.getIsAdStop()) != null && Integer.valueOf(requestDetail.getIsAdStop()) == 1) {

                        stop_latlng = new LatLng(Double.valueOf(requestDetail.getAdStopLatitude()), Double.valueOf(requestDetail.getAdStopLongitude()));

                        getDirectionsWay(s_latlon.latitude, s_latlon.longitude, d_latlon.latitude, d_latlon.longitude, stop_latlng.latitude, stop_latlng.longitude);

                    } else {

                        getDirections(s_latlon.latitude, s_latlon.longitude, d_latlon.latitude, d_latlon.longitude);

                    }
                }

                googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                                                   @Override
                                                   public View getInfoWindow(Marker marker) {
                                                       View vew = null;
                                                       if (destination_marker != null) {
                                                           if (marker.getId().equals(destination_marker.getId())) {
                                                               vew = activity.getLayoutInflater().inflate(R.layout.info_window_dest, null);
                                                     /*  new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                           @Override
                                                           public void run() {

                                                               if (drop_marker != null) {
                                                                   drop_marker.showInfoWindow();
                                                               }
                                                           }
                                                       });*/
                                                           } else if (marker.getId().equals(source_marker.getId())) {
                                                          /* vew = activity.getLayoutInflater().inflate(R.layout.eta_info_window, null);
                                                           TextView txt_location = (TextView) vew.findViewById(R.id.txt_location);
                                                           txt_location.setText(activity.getString(R.string.txt_pickup_loc));
                                                           final TextView txt_eta_marker = (TextView) vew.findViewById(R.id.txt_eta);
*/
                                                               if (jobStatus == 1 || jobStatus == 2) {
                                                                   //  txt_eta_marker.setText(eta_time);
                                                               } else {
                                                              /* vew = activity.getLayoutInflater().inflate(R.layout.info_window_dest, null);
                                                               TextView location = (TextView) vew.findViewById(R.id.txt_location);
                                                               location.setText(activity.getString(R.string.txt_pickup_loc));*/

                                                               }

                                                         /*  new CountDownTimer(2000, 1000) {

                                                               public void onTick(long millisUntilFinished) {

                                                               }

                                                               public void onFinish() {
                                                                   if (jobStatus == 1 || jobStatus == 2) {
                                                                       if (source_marker != null) {
                                                                           source_marker.showInfoWindow();

                                                                       }
                                                                   } else {
                                                                       if (source_marker != null) {
                                                                           source_marker.hideInfoWindow();
                                                                           if (isAdded()) {
                                                                               fitmarkers_toMap();
                                                                           }
                                                                       }
                                                                   }
                                                               }

                                                           }.start();*/

                                                               // final TextView txt_location_marker = (TextView) vew.findViewById(R.id.txt_location);
                                                           } else {
                                                               vew = activity.getLayoutInflater().inflate(R.layout.driver_info_window, null);
                                                               TextView txt_driver_name = (TextView) vew.findViewById(R.id.driver_name);
                                                               SimpleRatingBar driver_rate = (SimpleRatingBar) vew.findViewById(R.id.driver_rate);
                                                               driver_rate.setVisibility(View.GONE);
                                                           }
                                                       } else {
                                                     /*  vew = activity.getLayoutInflater().inflate(R.layout.driver_info_window, null);
                                                       SimpleRatingBar driver_rate = (SimpleRatingBar) vew.findViewById(R.id.driver_rate);
                                                       driver_rate.setVisibility(View.GONE);*/
                                                       }

                                                       return vew;

                                                   }

                                                   @Override
                                                   public View getInfoContents(Marker marker) {
                                                       // Getting mViewRoot from the layout file infowindowlayout.xml
                                                       return null;
                                                   }
                                               }
                );

                if (isAdded() && destination_marker != null) {
                    fitmarkers_toMap();
                }

            }
        }

//    private void addNotification(String text) {
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getContext())
//                .setSmallIcon(R.drawable.icon_launcher)
//                .setContentTitle("Notifications")
//                .setContentText(text);
//        Intent notificationIntent = new Intent(getContext(), TravelMapFragment.class);
//        PendingIntent contentIntent = PendingIntent.getActivity(getContext(), 0, notificationIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//        mBuilder.setContentIntent(contentIntent);
//        // Add as notification
//        NotificationManager manager = (NotificationManager) getActivity().getSystemService(Service.NOTIFICATION_SERVICE);
//        manager.notify(0, mBuilder.build());
//    }

    }

    private void getDirectionsWay(double latitude, double longitude, double latitude1,
                                  double longitude1, double latitideStop, double longitudeStop) {

        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.DIRECTION_API_BASE + Const.ORIGIN + "="
                + String.valueOf(latitude) + "," + String.valueOf(longitude) + "&" + Const.DESTINATION + "="
                + String.valueOf(latitude1) + "," + String.valueOf(longitude1) + "&" + Const.WAYPOINTS + "="
                + String.valueOf(latitideStop) + "," + String.valueOf(longitudeStop) + "&" + Const.EXTANCTION);
        Log.e("asher", "directions stop map " + map);//GOOGLE_DIRECTION_API
        new VolleyRequester(activity, Const.GET, map, Const.ServiceCode.GOOGLE_DIRECTION_API, this);

    }


    private void CancelReasonDialog(final ArrayList<CancelReason> cancelReasonLst) {

        final Dialog CancelReasondialog = new Dialog(activity, R.style.DialogThemeforview);

        CancelReasondialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        CancelReasondialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.fade_drawable));

        CancelReasondialog.setCancelable(false);

        CancelReasondialog.setContentView(R.layout.cancel_request_layout);

        RecyclerView cancel_reason_lst = (RecyclerView) CancelReasondialog.findViewById(R.id.cancel_reason_lst);

        CancelReasonAdapter CancelAdapter = new CancelReasonAdapter(activity, cancelReasonLst);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(activity);

        cancel_reason_lst.setLayoutManager(mLayoutManager);
        cancel_reason_lst.setItemAnimator(new DefaultItemAnimator());
        cancel_reason_lst.setAdapter(CancelAdapter);

        cancel_reason_lst.addOnItemTouchListener(new RecyclerLongPressClickListener(activity, cancel_reason_lst, new RecyclerLongPressClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                cancelRide(cancelReasonLst.get(position).getReasonId(), cancelReasonLst.get(position).getReasontext());

                CancelReasondialog.dismiss();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

        CancelReasondialog.show();

    }

}
