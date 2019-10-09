package sg.go.user;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.bottomnavigation.LabelVisibilityMode;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.paypal.android.sdk.payments.LoginActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;

import sg.go.user.Activity.HomeLoginActivity;
import sg.go.user.BroadcastReceiver.FCMScheduleReceiver;
import sg.go.user.Fragment.AccountFragment;
import sg.go.user.Fragment.BaseFragment;
import sg.go.user.Fragment.BillingInfoFragment;
import sg.go.user.Fragment.HistoryRideFragment;
import sg.go.user.Fragment.HomeMapFragment;
import sg.go.user.Fragment.ScheduleListFragment;
import sg.go.user.Fragment.SearchPlaceFragment;
import sg.go.user.Fragment.TravelMapFragment;
import sg.go.user.HttpRequester.VolleyRequester;
import sg.go.user.Interface.AsyncTaskCompleteListener;
import sg.go.user.Models.RequestDetail;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.EbizworldUtils;
import sg.go.user.Utils.ParseContent;
import sg.go.user.Utils.PreferenceHelper;

public class MainActivity extends AppCompatActivity implements AsyncTaskCompleteListener, BottomNavigationView.OnNavigationItemSelectedListener {

//    private DrawerLayout drawerLayout;
//    private ActionBarDrawerToggle drawerToggle;
    public String currentFragment = "";
    private Toolbar mainToolbar;
    private Bundle mbundle;
    private ParseContent pcontent;
    private AlertDialog internetDialog;
    private AlertDialog gpsAlertDialog1;
    private boolean isGpsDialogShowing = false, isRecieverRegistered = false, isNetDialogShowing = false;
    private boolean gpswindowshowing = false;
    AlertDialog.Builder gpsBuilder;
    private LocationManager manager;
//  private ImageButton bnt_menu;
    private Dialog load_dialog;

    private Button btn_select_gps;
    private  TextView txt_exit_gps;

    private Dialog gpsAlertDialog;
    private BottomNavigationView mBottomNavigationView;

    private BroadcastReceiver accountLogoutReceiver;

    private FCMScheduleReceiver mFcmScheduleReceiver;
    private Handler accountStatusHandler = new Handler();
    private Runnable accountStatusRunnable = new Runnable() {
        @Override
        public void run() {

            getTypes();
            accountStatusHandler.postDelayed(this, 30000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        if (!TextUtils.isEmpty(new PreferenceHelper(this).getLanguage())) {

            Locale myLocale = null;
            switch (new PreferenceHelper(this).getLanguage()) {
                case "":
                    myLocale = new Locale("en");
                    break;
                case "en":
                    myLocale = new Locale("en");

                    break;
                case "fr":
                    myLocale = new Locale("fr");
                    break;

            }


            Locale.setDefault(myLocale);
            Configuration config = new Configuration();
            config.locale = myLocale;
            this.getResources().updateConfiguration(config,
                    this.getResources().getDisplayMetrics());
        }

        setContentView(R.layout.activity_main);
        mFcmScheduleReceiver = new FCMScheduleReceiver();

        /*bnt_menu = (ImageButton) findViewById(R.id.bnt_menu);
        bnt_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });*/

        pcontent = new ParseContent(this);

        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.main_bottom_navigation);

        // set title show always
//        removeShiftMode(mBottomNavigationView);
//        mBottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);


        mBottomNavigationView.setOnNavigationItemSelectedListener(this);

        mainToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle(null);

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (new PreferenceHelper(this).getLoginType().equals(Const.PatientService.PATIENT)){
            mbundle = savedInstanceState;

            /*if (TextUtils.isEmpty(new PreferenceHelper(this).getDeviceToken())) {

                registerGcmReceiver(new GcmBroadcastReceiver());

            }*/

            int currentapiVersion = android.os.Build.VERSION.SDK_INT;

            if (currentapiVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.

                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            1);
                    return;
                } else {
                    //Log.d("mahesh","coming check1");
                    checkreqstatus();
                }
            } else {
                //Log.d("mahesh","coming check2");
                checkreqstatus();
            }

            Log.e("asher", "phone main act " + new PreferenceHelper(this).getPhone()+" "+new PreferenceHelper(this).getLoginBy());
            if (!new PreferenceHelper(this).getLoginBy().equalsIgnoreCase(Const.MANUAL)) {
                Log.e("asher", "phone main act1 " + new PreferenceHelper(this).getPhone());
                if (new PreferenceHelper(this).getPhone().isEmpty() || new PreferenceHelper(this).getPhone() == null) {
                    Log.e("asher", "phone main act2 " + new PreferenceHelper(this).getPhone());
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
                    builder.setMessage(getResources().getString(R.string.txt_update_number))
                            .setCancelable(false)
                            .setPositiveButton(getResources().getString(R.string.txt_ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    openProfileActivity();
                                }
                            });

                    android.support.v7.app.AlertDialog alert = builder.create();
                    alert.show();
                }

            }

            if (mBottomNavigationView != null){

                mBottomNavigationView.inflateMenu(R.menu.consumer_bottom_navigation_menu);
                mBottomNavigationView.getMenu().findItem(R.id.action_home).setChecked(true);
                addFragment(new SearchPlaceFragment(), false, Const.HOME_MAP_FRAGMENT, true);

            }

        }else if (new PreferenceHelper(this).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)){

            if (mBottomNavigationView != null){

                mBottomNavigationView.inflateMenu(R.menu.nursing_home_bottom_navigation_menu);
                mBottomNavigationView.getMenu().findItem(R.id.action_home).setChecked(true);
                //Check Intent received from FCMScheduleReceiver
                if (getIntent().getExtras() != null &&
                        getIntent().getExtras().getString(Const.NotificationType.ACTION) != null &&
                        getIntent().getExtras().getString(Const.NotificationType.ACTION).equals(Const.NotificationType.SCHEDULE)){

                    EbizworldUtils.appLogDebug("AmbulanceFCMService", "MainActivity " + getIntent().getExtras().getString(Const.NotificationType.ACTION));

                    addFragment(new ScheduleListFragment(), false, Const.SCHEDULE_LIST_FRAGMENT, true);

                }else {

                    addFragment(new SearchPlaceFragment(), false, Const.HOME_MAP_FRAGMENT, true);
                }




            }
        }else if (new PreferenceHelper(this).getLoginType().equals(Const.HospitalService.HOSPITAL)){

            if (mBottomNavigationView != null){

                mBottomNavigationView.inflateMenu(R.menu.hospital_bottom_navigation_menu);


                mBottomNavigationView.getMenu().findItem(R.id.action_home).setChecked(true);
                //Check Intent received from FCMScheduleReceiver
                if (getIntent().getExtras() != null &&
                        getIntent().getExtras().getString(Const.NotificationType.ACTION) != null &&
                        getIntent().getExtras().getString(Const.NotificationType.ACTION).equals(Const.NotificationType.SCHEDULE)){

                    EbizworldUtils.appLogDebug("AmbulanceFCMService", "MainActivity: " + getIntent().getExtras().getString(Const.NotificationType.ACTION));

                    addFragment(new ScheduleListFragment(), false, Const.SCHEDULE_LIST_FRAGMENT, true);

                }else {

                    addFragment(new SearchPlaceFragment(), false, Const.HOME_MAP_FRAGMENT, true);
                }

            }
        }

//        Get account status
        accountStatusHandler.postDelayed(accountStatusRunnable, 10000);

            // --------------------Logout account-------------------
             accountLogoutReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent != null && intent.getStringExtra(Const.NotificationType.TYPE_ACCOUNT_LOGOUT).equals(Const.NotificationType.TYPE_ACCOUNT_LOGOUT)){

                    EbizworldUtils.showShortToast("You have logged in other device!", MainActivity.this);
                    new PreferenceHelper(MainActivity.this).Logout();
                    startActivity(new Intent(MainActivity.this, SignInActivity.class));
                    MainActivity.this.finish();
                }

            }
        };

    }
   @SuppressLint("RestrictedApi")
   private static void removeShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        for (int i = 0; i < menuView.getChildCount(); i++) {

            BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
            //noinspection RestrictedApi
            item.setShifting(false);
            item.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

            // set once again checked value, so view will be updated
            //noinspection RestrictedApi
            item.setChecked(item.getItemData().isChecked());
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(accountLogoutReceiver,
                new IntentFilter(Const.NotificationType.TYPE_ACCOUNT_LOGOUT));


//        if(currentFragment.equals(Const.HOME_MAP_FRAGMENT)){
//            Log.e("mahi","coming 1");
//            addFragment(new HomeMapFragment(), false, Const.HOME_MAP_FRAGMENT, true);
//        } else if(currentFragment.equals(Const.REQUEST_FRAGMENT)){
//            addFragment(new RequestMapFragment(), false, Const.REQUEST_FRAGMENT, true);
//        } else if(currentFragment.equals(Const.SEARCH_FRAGMENT)){
//            addFragment(new SearchPlaceFragment(), false, Const.SEARCH_FRAGMENT, true);
//        } else if(currentFragment.equals("")) {
//            addFragment(new HomeMapFragment(), false, Const.HOME_MAP_FRAGMENT, true);
//            Log.e("mahi","coming 2");
//        }
    }

    /*public void registerGcmReceiver(BroadcastReceiver mHandleMessageReceiver) {
        if (mHandleMessageReceiver != null) {
            new GCMRegisterHandler(this, mHandleMessageReceiver);
        }


    }*/

    private void openProfileActivity() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    /*private void initDrawer() {
        // TODO Auto-generated method stub
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                mainToolbar, R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
//                invalidateOptionsMenu();
            }
        };

        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.setDrawerIndicatorEnabled(false);
        drawerLayout.post(new Runnable() {
            @Override
            public void run() {

                drawerToggle.syncState();
            }
        });


    }*/

    /*public void closeDrawer() {
        drawerLayout.closeDrawers();
    }*/


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        /*drawerToggle.syncState();*/
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        /*drawerToggle.onConfigurationChanged(newConfig);*/
    }


    @Override
    public void onBackPressed() {
        //EbizworldUtils.appLogDebug("CurrentFragment", currentFragment);

        if (!currentFragment.equals(Const.HOME_MAP_FRAGMENT)) {

            if (currentFragment.equals(Const.REQUEST_FRAGMENT)){

                addFragment(new SearchPlaceFragment(), false, Const.SEARCH_FRAGMENT, true);

            }else if (currentFragment.equals(Const.WALLET_FRAGMENT)){

                addFragment(new AccountFragment(), false, Const.ACCOUNT_FRAGMENT, true);

                mBottomNavigationView.getMenu().findItem(R.id.action_home).setChecked(true);

            }else if(currentFragment.equals(Const.RATING_FRAGMENT)){

                Toast.makeText(this, getResources().getString(R.string.txt_toast_event_back_rating).toString(), Toast.LENGTH_SHORT).show();

            }
//            else if(currentFragment.equals(Const.BILLING_INFO_FRAGMENT)){
//
//                    Toast.makeText(this, "Cannot", Toast.LENGTH_SHORT).show();
//
//               }
            else {

                addFragment(new SearchPlaceFragment(), false, Const.HOME_MAP_FRAGMENT, true);

                mBottomNavigationView.getMenu().findItem(R.id.action_home).setChecked(true);
            }

        } else {

            if (!isFinishing()) {

                openExitDialog();

            }

        }

    }

    private void openExitDialog() {

        final Dialog exit_dialog = new Dialog(this, R.style.DialogSlideAnim_leftright);
        exit_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        exit_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        exit_dialog.setCancelable(false);

        /*exit_dialog.setContentView(R.layout.dialog_exit);
        TextView tvExitOk = (TextView) exit_dialog.findViewById(R.id.tvExitOk);
        TextView tvExitCancel = (TextView) exit_dialog.findViewById(R.id.tvExitCancel);
        tvExitOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exit_dialog.dismiss();
                finishAffinity();
            }
        });
        tvExitCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exit_dialog.dismiss();
            }
        });*/
        exit_dialog.setContentView(R.layout.dialog_logout);

        TextView title = (TextView) exit_dialog.findViewById(R.id.logout_title);
        title.setText(getResources().getString(R.string.dialog_exit_caps));

        TextView content = (TextView) exit_dialog.findViewById(R.id.logout_content);
        content.setText(getResources().getString(R.string.dialog_exit_text));

        TextView btn_yes = (TextView) exit_dialog.findViewById(R.id.btn_logout_yes);
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                exit_dialog.dismiss();
                finishAffinity();

            }
        });

        TextView btn_no = (TextView) exit_dialog.findViewById(R.id.btn_logout_no);
        btn_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                exit_dialog.dismiss();

            }
        });

        exit_dialog.show();
    }
/*---- Dialog GPS ----*/
    private void ShowGpsDialog() {

        isGpsDialogShowing = true;

        gpsAlertDialog = new Dialog(this);

        gpsAlertDialog.setContentView(R.layout.dialog_request_gps);

        gpsAlertDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        btn_select_gps = gpsAlertDialog.findViewById(R.id.btn_select_gps);

        txt_exit_gps = gpsAlertDialog.findViewById(R.id.txt_exit_gps);

        gpsAlertDialog.setCancelable(false);


        btn_select_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // continue with delete
                Intent intent = new Intent(
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                removeGpsDialog();
            }
        });

        txt_exit_gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                removeGpsDialog();
                finishAffinity();

            }
        });



//        gpsBuilder = new AlertDialog.Builder(
//                this);
//        gpsBuilder.setCancelable(false);
//        gpsBuilder.setTitle(getResources().getString(R.string.txt_gps_off))
//                .setMessage(getResources().getString(R.string.txt_gps_msg))
//                .setPositiveButton(getResources().getString(R.string.txt_enable),
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog,
//                                                int which) {
//                                // continue with delete
//                                Intent intent = new Intent(
//                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                                startActivity(intent);
//                                removeGpsDialog();
//                            }
//                        })
//
//                .setNegativeButton(getResources().getString(R.string.txt_exit),
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog,
//                                                int which) {
//                                // do nothing
//                                removeGpsDialog();
//                                finishAffinity();
//                            }
//                        });
//        gpsAlertDialog = gpsBuilder.create();

        gpsAlertDialog.show();

    }


    private void removeGpsDialog() {

        if (gpsAlertDialog != null && gpsAlertDialog.isShowing()) {

            gpsAlertDialog.dismiss();
            isGpsDialogShowing = false;
            gpsAlertDialog = null;


        }
    }

    public void addFragment(Fragment fragment, boolean addToBackStack,
                            String tag, boolean isAnimate) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        if (isAnimate) {
            ft.setCustomAnimations(R.anim.slide_in_right,
                    R.anim.slide_out_left, R.anim.slide_in_left,
                    R.anim.slide_out_right);

        }

        if (addToBackStack) {
            ft.addToBackStack(tag);
        }

        ft.replace(R.id.content_frame, fragment, tag);
        currentFragment = tag;
        ft.commitAllowingStateLoss();

        if(tag.equals(Const.HOME_MAP_FRAGMENT) ||
                tag.equals(Const.SCHEDULE_LIST_FRAGMENT) ||
                tag.equals(Const.HISTORY_FRAGMENT) ||
                tag.equals(Const.ACCOUNT_FRAGMENT)){

            if (mBottomNavigationView != null)
                mBottomNavigationView.setVisibility(View.VISIBLE);

        }else {

            if (mBottomNavigationView != null)
                mBottomNavigationView.setVisibility(View.GONE);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // do something else
            if (isGpsDialogShowing) {
                return;
            }
            ShowGpsDialog();

        } else {

            removeGpsDialog();

        }

        if (new PreferenceHelper(this).getLoginType().equals(Const.NursingHomeService.NURSING_HOME) ||
                new PreferenceHelper(this).getLoginType().equals(Const.HospitalService.HOSPITAL)){

            registerReceiver(mFcmScheduleReceiver, new IntentFilter(Const.NotificationType.SCHEDULE)); //Register Broadcast to receive FCM schedule start
            EbizworldUtils.appLogDebug("AmbulanceFCMService", "MainActivity: Register FCMScheduleReceiver");

        }

        registerReceiver(internetConnectionReciever, new IntentFilter(
                "android.net.conn.CONNECTIVITY_CHANGE"));

        registerReceiver(GpsChangeReceiver, new IntentFilter(
                LocationManager.PROVIDERS_CHANGED_ACTION));
        isRecieverRegistered = true;
    }



     public BroadcastReceiver internetConnectionReciever = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetInfo = connectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo activeWIFIInfo = connectivityManager
                    .getNetworkInfo(connectivityManager.TYPE_WIFI);

            if (activeWIFIInfo.isConnected() || activeNetInfo.isConnected()) {
                removeInternetDialog();
            } else {
                if (isNetDialogShowing) {
                    return;
                }
                showInternetDialog();
            }
        }
    };

    private void removeInternetDialog(){
        if (internetDialog != null && internetDialog.isShowing()) {
            internetDialog.dismiss();
            isNetDialogShowing = false;
            internetDialog = null;

        }
    }

    private void showInternetDialog() {

        isNetDialogShowing = true;
        AlertDialog.Builder internetBuilder = new AlertDialog.Builder(
                MainActivity.this);
        internetBuilder.setCancelable(false);
        internetBuilder
                .setTitle(getString(R.string.dialog_no_internet))
                .setMessage(getString(R.string.dialog_no_inter_message))
                .setPositiveButton(getString(R.string.dialog_enable_3g),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // continue with delete
                                Intent intent = new Intent(
                                        android.provider.Settings.ACTION_SETTINGS);
                                startActivity(intent);
                                removeInternetDialog();
                            }
                        })
                .setNeutralButton(getString(R.string.dialog_enable_wifi),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // User pressed Cancel button. Write
                                // Logic Here
                                startActivity(new Intent(
                                        Settings.ACTION_WIFI_SETTINGS));
                                removeInternetDialog();
                            }
                        })
                .setNegativeButton(getString(R.string.dialog_exit),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // do nothing
                                removeInternetDialog();
                            }
                        });
        internetDialog = internetBuilder.create();
        internetDialog.show();
    }

/*---- EVENT WHEN GPS WILL TURN OFF AND ON ----*/

    public BroadcastReceiver GpsChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction() != null) {

                final LocationManager manager = (LocationManager) context
                        .getSystemService(Context.LOCATION_SERVICE);
                if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    // do something

                    removeGpsDialog();

                } else {
                    // do something else
                    if (isGpsDialogShowing) {
                        return;
                    }

                    ShowGpsDialog();
                }

            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (isRecieverRegistered) {

            unregisterReceiver(GpsChangeReceiver);
            unregisterReceiver(internetConnectionReciever);

            if (new PreferenceHelper(this).getLoginType().equals(Const.NursingHomeService.NURSING_HOME) ||
                    new PreferenceHelper(this).getLoginType().equals(Const.HospitalService.HOSPITAL)){

                unregisterReceiver(mFcmScheduleReceiver); //Unregister Broadcast to receive FCM schedule start
                EbizworldUtils.appLogDebug("AmbulanceFCMService", "MainActivity: Unregister FCMScheduleReceiver");

            }

        }

    }

    private void checkreqstatus() {
        if (!EbizworldUtils.isNetworkAvailable(this)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), MainActivity.this);
            return;
        }
        EbizworldUtils.showSimpleProgressDialog(MainActivity.this, "", false);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.CHECKREQUEST_STATUS);
        map.put(Const.Params.ID, new PreferenceHelper(this).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(this).getSessionToken());


        Log.d("HaoLS", "API calling" + map.toString());
        new VolleyRequester(this, Const.POST, map, Const.ServiceCode.CHECKREQUEST_STATUS,
                this);
    }

    private void getTypes() {

        if (!EbizworldUtils.isNetworkAvailable(this)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), MainActivity.this);
            return;
        }
        HashMap<String, String> map = new HashMap<String, String>();
        /*map.put(Const.Params.URL, Const.ServiceType.AMBULANCE_OPERATOR);
        map.put(Const.Params.ID, new PreferenceHelper(getActivity()).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(getActivity()).getSessionToken());
        map.put(Const.Params.DISTANCE, dis);
        map.put(Const.Params.TIME, dur);*/
        if (new PreferenceHelper(this).getLoginType().equals(Const.PatientService.PATIENT)){

            map.put(Const.Params.URL, Const.ServiceType.OPERATORS_URL);

        }else if (new PreferenceHelper(this).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)){

            map.put(Const.Params.URL, Const.NursingHomeService.OPERATORS_URL);

        }else if (new PreferenceHelper(this).getLoginType().equals(Const.HospitalService.HOSPITAL)){

            map.put(Const.Params.URL, Const.HospitalService.OPERATORS_URL);
        }

        map.put(Const.Params.ID, new PreferenceHelper(this).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(this).getSessionToken());

        Log.d("HaoLS", "Getting ambulance operator " + map.toString());
        new VolleyRequester(this, Const.POST, map, Const.ServiceCode.AMBULANCE_OPERATOR,
                this);
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        switch (serviceCode) {
            case Const.ServiceCode.CHECKREQUEST_STATUS:

                EbizworldUtils.appLogInfo("HaoLS", "check req status " + response);

                if (response != null) {

                    Bundle bundle = new Bundle();
                    RequestDetail requestDetail = null;

                    if (new PreferenceHelper(this).getLoginType().equals(Const.PatientService.PATIENT)){

                        requestDetail = new ParseContent(this).parseRequestStatusNormal(response);

                    }else if (new PreferenceHelper(this).getLoginType().equals(Const.NursingHomeService.NURSING_HOME) ||
                            new PreferenceHelper(this).getLoginType().equals(Const.HospitalService.HOSPITAL)){

                        requestDetail = new ParseContent(this).parseRequestStatusSchedule(response);
                    }
                    /*List<RequestDetail> requestDetails = new ParseContent(activity).*/
                    TravelMapFragment travelfragment = new TravelMapFragment();
                    if (requestDetail == null) {
                        return;
                    }

                    EbizworldUtils.appLogDebug("Status", "onTaskCompleted:" + requestDetail.getTripStatus());

                    switch (requestDetail.getTripStatus()) {
                        case Const.NO_REQUEST:
                            new PreferenceHelper(this).clearRequestData();
                            // startgetProvider();


                            break;

                        case Const.IS_ACCEPTED:{
                            bundle.putSerializable(Const.REQUEST_DETAIL,
                                    requestDetail);
                            bundle.putInt(Const.DRIVER_STATUS,
                                    Const.IS_ACCEPTED);
                            if (!this.currentFragment.equals(Const.TRAVEL_MAP_FRAGMENT)) {
                                travelfragment.setArguments(bundle);
                                this.addFragment(travelfragment, false, Const.TRAVEL_MAP_FRAGMENT,
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
                            if (!this.currentFragment.equals(Const.TRAVEL_MAP_FRAGMENT)) {

                                travelfragment.setArguments(bundle);
                                this.addFragment(travelfragment, false, Const.TRAVEL_MAP_FRAGMENT,
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
                            if (!this.currentFragment.equals(Const.TRAVEL_MAP_FRAGMENT)) {

                                travelfragment.setArguments(bundle);
                                this.addFragment(travelfragment, false, Const.TRAVEL_MAP_FRAGMENT,
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
                            if (!this.currentFragment.equals(Const.TRAVEL_MAP_FRAGMENT)) {

                                travelfragment.setArguments(bundle);
                                this.addFragment(travelfragment, false, Const.TRAVEL_MAP_FRAGMENT,
                                        true);

                            }


                            BaseFragment.drop_latlan = null;
                            BaseFragment.pic_latlan = null;
                            BaseFragment.s_address = "";
                            BaseFragment.d_address = "";

                        }
                        break;

                        case Const.IS_DRIVER_TRIP_ENDED:{

                            /*bundle.putSerializable(Const.REQUEST_DETAIL,
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
                            BaseFragment.d_address = "";*/

                            if (!this.currentFragment.equals(Const.BILLING_INFO_FRAGMENT) && !this.isFinishing()){

                                BillingInfoFragment billingInfoFragment = new BillingInfoFragment();
                                billingInfoFragment.setArguments(bundle);

                                this.addFragment(billingInfoFragment, false, Const.BILLING_INFO_FRAGMENT, true);

                            }
                        }
                        break;

                        case Const.IS_DRIVER_RATED:{

                            bundle.putSerializable(Const.REQUEST_DETAIL,
                                    requestDetail);
                            bundle.putInt(Const.DRIVER_STATUS,
                                    Const.IS_ACCEPTED);
                            if (!this.currentFragment.equals(Const.TRAVEL_MAP_FRAGMENT)) {

                                travelfragment.setArguments(bundle);
                                this.addFragment(travelfragment, false, Const.TRAVEL_MAP_FRAGMENT,
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

            case Const.ServiceCode.AMBULANCE_OPERATOR:{

                EbizworldUtils.appLogInfo("HaoLS", response);

                if (response != null){

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.has("error_code")){

                            if (jsonObject.getInt("error_code") == 104){

                                if (accountStatusHandler != null){

                                    accountStatusHandler.removeCallbacks(accountStatusRunnable);

                                }
                                EbizworldUtils.showShortToast("You have logged in other device!", this);

                                if (jsonObject.has("error")){
                                    EbizworldUtils.appLogDebug("HaoLS", jsonObject.getString("error"));
                                }

                                new PreferenceHelper(this).Logout();
                                startActivity(new Intent(this, WelcomeActivity.class));
                                this.finish();

                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        EbizworldUtils.appLogError("HaoLS", "MainActivity " + e.toString());
                    }
                }
            }
            break;

        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    //Log.d("mahesh","coming to permission");
                    checkreqstatus();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        if (currentFragment.equals(Const.TRAVEL_MAP_FRAGMENT) ||
                currentFragment.equals(Const.REQUEST_FRAGMENT) ||
                currentFragment.equals(Const.NURSE_REGISTER_SCHEDULE_FRAGMENT) ||
                currentFragment.equals(Const.BILLING_INFO_FRAGMENT)){

            Toast.makeText(this, getResources().getString(R.string.navigationbottom_warning), Toast.LENGTH_SHORT).show();

        }else {

            switch (menuItem.getItemId()){

                case R.id.action_home:{

                    addFragment(new SearchPlaceFragment(), false, Const.HOME_MAP_FRAGMENT, true);

                }
                return true;

                case R.id.action_history:{

                    addFragment(new HistoryRideFragment(), false, Const.HISTORY_FRAGMENT, true);

                }

                return true;

                /*case R.id.action_payment:{

                    addFragment(new AddPaymentFragment(), false, Const.PAYMENT_FRAGMENT, true);
                }
                return true;*/

                case R.id.action_account:{

                    addFragment(new AccountFragment(), false, Const.ACCOUNT_FRAGMENT, true);

                }
                return true;

                case R.id.action_nurse_list_schedule:{

                    addFragment(new ScheduleListFragment(), false, Const.SCHEDULE_LIST_FRAGMENT, true);

                }
                return true;
            }

        }
        return false;
    }
}

