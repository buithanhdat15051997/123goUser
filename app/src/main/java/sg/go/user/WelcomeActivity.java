package sg.go.user;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;
import java.util.Locale;

import sg.go.user.Adapter.Adapter_ViewPagerFragment;
import sg.go.user.Adapter.GetStartedPictureAdapter;
import sg.go.user.Adapter.SpinnerLanguageAdapter;
import sg.go.user.Models.ItemGetstartedPicture;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.PreferenceHelper;

/*import com.company.ambulance2u.GCMHandler.GCMRegisterHandler;
import com.company.ambulance2u.GCMHandler.GcmBroadcastReceiver;*/

/**
 * Created by user on 1/3/2017.
 */

public class WelcomeActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
    private Button welcome_btn;
    private Spinner sp_country_reg;
    private SpinnerLanguageAdapter adapter_language;
    private boolean is_selected = false;

//    private ViewPager viewpager_getstarted;
//    private DotsIndicator dots_indicator_getstarted;
//    private ArrayList<ItemGetstartedPicture> Array_itemGetstartedPictures;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

     /*   try {
            Mint.initAndStartSession(this.getApplication(), "9905015e");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }*/

         if (!TextUtils.isEmpty(new PreferenceHelper(this).getUserId())) {
             startActivity(new Intent(this, MainActivity.class));
             this.finish();
             return;
         }

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
        setContentView(R.layout.activity_welcome);
        getPermission();
        sp_country_reg = (Spinner) findViewById(R.id.sp_country_reg);

//        viewpager_getstarted = findViewById(R.id.viewpager_getstarted);
//        dots_indicator_getstarted = findViewById(R.id.dots_indicator_getstarted);

       // ShowViewPager();


        //  getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        welcome_btn = (Button) findViewById(R.id.welcome_btn);
        welcome_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new PreferenceHelper(WelcomeActivity.this).putLoginType(Const.PatientService.PATIENT);

                Intent i = new Intent(getApplicationContext(), SignInActivity.class);

                ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_in_right, R.anim.slide_out_left);

                startActivity(i, activityOptions.toBundle());
            }
        });


        /*if (TextUtils.isEmpty(new PreferenceHelper(this).getDeviceToken())) {

            registerGcmReceiver(new GcmBroadcastReceiver());

        }*/

        String[] lst_currency = getResources().getStringArray(R.array.language);
        Integer[] currency_imageArray = {null, R.drawable.ic_united_states, R.drawable.ic_france};

        adapter_language = new SpinnerLanguageAdapter(this, R.layout.spinner_value_layout, lst_currency, currency_imageArray);
        sp_country_reg.setAdapter(adapter_language);
        if (!TextUtils.isEmpty(new PreferenceHelper(this).getLanguage())) {

            switch (new PreferenceHelper(this).getLanguage()) {
                case "":
                    sp_country_reg.setSelection(0, false);
                    break;
                case "en":
                    sp_country_reg.setSelection(1, false);

                    break;
                case "fr":
                    sp_country_reg.setSelection(2, false);
                    break;

            }

        }
        sp_country_reg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // EbizworldUtils.showShortToast(""+i,WelcomeActivity.this);
                switch (i) {
                    case 0:
                        new PreferenceHelper(WelcomeActivity.this).putLanguage("");
                        break;
                    case 1:
                        new PreferenceHelper(WelcomeActivity.this).putLanguage("en");
                        setLocale("en");
                        break;
                    case 2:
                        new PreferenceHelper(WelcomeActivity.this).putLanguage("fr");
                        setLocale("fr");
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

    private void ShowViewPager() {

//        Array_itemGetstartedPictures = new ArrayList<>();
//        Array_itemGetstartedPictures.add(new ItemGetstartedPicture(R.drawable.img_viewpager_one));
//        Array_itemGetstartedPictures.add(new ItemGetstartedPicture(R.drawable.img_viewpager_two));
//        Array_itemGetstartedPictures.add(new ItemGetstartedPicture(R.drawable.img_viewpager_three));
//        GetStartedPictureAdapter adapter_viewpager = new GetStartedPictureAdapter(WelcomeActivity.this,Array_itemGetstartedPictures);
//        dots_indicator_getstarted.setViewPager(viewpager_getstarted);
//        viewpager_getstarted.setAdapter(adapter_viewpager);
    //    Adapter_ViewPagerFragment adapter_viewPagerFragment =new Adapter_ViewPagerFragment(getSupportFragmentManager(),3);

//        dots_indicator_getstarted.setViewPager(viewpager_getstarted);
//
//        viewpager_getstarted.setAdapter(adapter_viewPagerFragment);
//
//        viewpager_getstarted.setOffscreenPageLimit(3);//số lượng page lưu vào bộ nhớ cache để tránh tình trạng load lại


    }


//    public void registerGcmReceiver(BroadcastReceiver mHandleMessageReceiver) {
//        if (mHandleMessageReceiver != null) {
//            new GCMRegisterHandler(this, mHandleMessageReceiver);
//        }
//    }

    @SuppressLint("NewApi")
    private void getPermission() {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {

            String[] permissions_dummy = new String[6];
            int i = 0;

            String permission = "android.permission.WRITE_EXTERNAL_STORAGE";
            int res = checkCallingOrSelfPermission(permission);
            if (res != PackageManager.PERMISSION_GRANTED) {


                permissions_dummy[i] = permission;
                i = i + 1;


            }

            permission = "android.permission.READ_CONTACTS";
            res = checkCallingOrSelfPermission(permission);
            if (res != PackageManager.PERMISSION_GRANTED) {


                permissions_dummy[i] = permission;
                i = i + 1;


            }
            permission = "android.permission.CAMERA";
            res = checkCallingOrSelfPermission(permission);
            if (res != PackageManager.PERMISSION_GRANTED) {


                permissions_dummy[i] = permission;
                i = i + 1;


            }
            permission = "android.permission.ACCESS_FINE_LOCATION";
            res = checkCallingOrSelfPermission(permission);
            if (res != PackageManager.PERMISSION_GRANTED) {


                permissions_dummy[i] = permission;
                i = i + 1;


            }

            permission = "android.permission.READ_PHONE_STATE";
            res = checkCallingOrSelfPermission(permission);
            if (res != PackageManager.PERMISSION_GRANTED) {


                permissions_dummy[i] = permission;
                i = i + 1;

            }

            permission = "android.permission.READ_SMS";
            res = checkCallingOrSelfPermission(permission);
            if (res != PackageManager.PERMISSION_GRANTED) {


                permissions_dummy[i] = permission;
                i = i + 1;


            }


            String[] permissions = new String[i];

            for (int j = 0; j < i; j++) {

                permissions[j] = permissions_dummy[j];

            }


            int yourRequestId = 1;
            if (i != 0) {

                // Do something for lollipop and above versions
                requestPermissions(permissions, yourRequestId);
            }

        }

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onResume() {
        super.onResume();
        // put your code here...


    }


    @Override
    public void onBackPressed() {
        finishAffinity();
    }


    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        Configuration config = new Configuration();
        config.locale = myLocale;
        this.getResources().updateConfiguration(config,
                this.getResources().getDisplayMetrics());

        Intent refresh = new Intent(this, WelcomeActivity.class);
        startActivity(refresh);
        this.overridePendingTransition(0, 0);
    }
}
