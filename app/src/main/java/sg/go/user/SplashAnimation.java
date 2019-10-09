package sg.go.user;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;


import com.github.ybq.android.spinkit.style.Circle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import sg.go.user.HttpRequester.VolleyRequester;
import sg.go.user.Interface.AsyncTaskCompleteListener;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.EbizworldUtils;
import sg.go.user.Utils.PreferenceHelper;
import sg.go.user.Utils.splash.SplashAnimationHelper;

/**
 * Created by Mahesh on 7/19/2017.
 */

public class SplashAnimation extends AppCompatActivity implements AsyncTaskCompleteListener {
    RelativeLayout splashAnimationLayout;
    private SplashAnimationHelper.SplashRouteAnimation splashRouteAnimation;
    int versionCode;
    int mDriver_Version_Code;
    ProgressBar progressBarSplash;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_animation);
        splashAnimationLayout = (RelativeLayout)findViewById(R.id.splashAnimationLayout);
        progressBarSplash = findViewById(R.id.spin_kitSplash);
        com.github.ybq.android.spinkit.style.Circle fadingCircle = new Circle();
        progressBarSplash.setIndeterminateDrawable(fadingCircle);


        try {
            PackageInfo packageInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            versionCode = packageInfo.versionCode;
            new PreferenceHelper(this).putAppVersion(versionCode);

        } catch (PackageManager.NameNotFoundException e) {

            e.printStackTrace();

        }

        EbizworldUtils.appLogError("HaoLS","version " + versionCode + " pref: " + new PreferenceHelper(this).getAppVersion());

        /*getVersionCheck();*/
//        animateToHomeScreen();
//        startProgressAnimation();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashAnimation.this, WellcomeSkipActivity.class);
                startActivity(i);
                // close this activity
                finish();
                progressBarSplash.setVisibility(View.GONE);

            }
        },3000);
    }

    private void getVersionCheck() {
        if (!EbizworldUtils.isNetworkAvailable(this)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), SplashAnimation.this);
            return;
        }
        //    EbizworldUtils.showSimpleProgressDialog(this, "", false);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.GET_VERSION);
        /*map.put(Const.Params.ID, new PreferenceHelper(this).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(this).getSessionToken());*/

        Log.d("HaoLS","version map " + map.toString());
        new VolleyRequester(this, Const.GET, map, Const.ServiceCode.GET_VERSION, this);
    }

    private void startProgressAnimation()
    {
        {
            this.splashRouteAnimation = new SplashAnimationHelper().createSplashAnimation(this);
            this.splashRouteAnimation.startAnimation(this.splashAnimationLayout);
        }
    }


    private void animateToHomeScreen()
    {

        AnimatorSet localAnimatorSet1 = new AnimatorSet();
        AnimatorSet localAnimatorSet2 = new AnimatorSet();
        AnimatorSet localAnimatorSet3 = new AnimatorSet();
        ArrayList localArrayList1 = new ArrayList();
        ArrayList localArrayList2 = new ArrayList();
        localArrayList1.add(ObjectAnimator.ofFloat(this.splashAnimationLayout, "alpha", new float[] { 1.0F, 0.0F }));

        localAnimatorSet2.setDuration(20000);
        localAnimatorSet2.playTogether(localArrayList1);

        localAnimatorSet3.playSequentially(localArrayList2);
        localAnimatorSet3.setDuration(500L);
        localAnimatorSet3.setStartDelay(50L);
        localAnimatorSet1.playSequentially(new Animator[] { localAnimatorSet2, localAnimatorSet3 });
        localAnimatorSet1.addListener(new Animator.AnimatorListener()
        {
            public void onAnimationCancel(Animator paramAnonymousAnimator) {}

            public void onAnimationEnd(Animator paramAnonymousAnimator)
            {
                if (SplashAnimation.this.splashRouteAnimation != null) {
                   // SplashAnimation.this.splashRouteAnimation.stopAnimation();
                }
            }

            public void onAnimationRepeat(Animator paramAnonymousAnimator) {}

            public void onAnimationStart(Animator paramAnonymousAnimator) {}
        });
        localAnimatorSet1.start();
    }


    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();

    }





    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        switch (serviceCode) {

            case Const.ServiceCode.GET_VERSION:
                //     EbizworldUtils.removeProgressDialog();
                EbizworldUtils.appLogInfo("HaoLS", "version response " + response);
                EbizworldUtils.removeProgressDialog();

                if (response != null) {

                    try {

                        JSONObject jsonObject = new JSONObject(response);

//                        Checking for the new version
                        if (jsonObject.optString("success").equalsIgnoreCase("true")) {

                            mDriver_Version_Code = Integer.parseInt(jsonObject.optString("android_driver_version"));

                            if (mDriver_Version_Code > versionCode) {

                                /*AlertDialog.Builder versionBuilder = new AlertDialog.Builder(SplashAnimation.this);
                                versionBuilder.setTitle(getResources().getString(R.string.update_txt_available));
                                versionBuilder.setMessage(getResources().getString(R.string.update_txt))
                                        .setCancelable(false)
                                        .setPositiveButton(getResources().getString(R.string.update_txt_btn), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                //do things
                                                String mPlayStoreLink = "https://play.google.com/store/apps/details?id=com.nikola.user";
                                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mPlayStoreLink));
                                                startActivity(intent);
                                            }
                                        });
                                AlertDialog alert = versionBuilder.create();
                                alert.show();*/

                            } else {

                                new Handler().postDelayed(
                                        new Runnable() {
                                            @Override
                                            public void run() {

                                                Intent i = new Intent(SplashAnimation.this, WelcomeActivity.class);
                                                ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_in_right, R.anim.slide_out_left);
                                                startActivity(i, activityOptions.toBundle());

                                                // close this activity
                                                finish();

                                            }
                                        }, 3000);

                            }
                        } else if (jsonObject.optString("success").equalsIgnoreCase("false")) {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    Intent i = new Intent(SplashAnimation.this, WelcomeActivity.class);
                                    ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_in_right, R.anim.slide_out_left);
                                    startActivity(i, activityOptions.toBundle());

                                    // close this activity
                                    finish();

                                }
                            }, 3000);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        EbizworldUtils.appLogError("HaoLS", "Get version failed " + e.toString());
                    }
                }
                break;
        }
    }
}
