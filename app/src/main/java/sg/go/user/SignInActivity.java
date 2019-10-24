package sg.go.user;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TimeZone;

import sg.go.user.Fragment.ForgotPasswordFragment;
import sg.go.user.Fragment.SignUpMobileFragment;
import sg.go.user.HttpRequester.MultiPartRequester;
import sg.go.user.HttpRequester.VolleyRequester;
import sg.go.user.Interface.AsyncTaskCompleteListener;
import sg.go.user.Models.SocialMediaProfile;
import sg.go.user.Utils.Commonutils;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.EbizworldUtils;
import sg.go.user.Utils.ParseContent;
import sg.go.user.Utils.PreferenceHelper;

/**
 * Created by user on 1/4/2017.
 */

public class SignInActivity extends AppCompatActivity implements View.OnClickListener, AsyncTaskCompleteListener,
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    private ImageButton btn_cancel;
    private TextView btn_forgot_password_login, btn_new_user;
    public static String currentfragment = "";
    private RelativeLayout log_layout;
    /*private Button btn_login_fb;*/
    private String loginType = Const.MANUAL;
    private String sFirstName, sLastName, sEmailId, sPassword, sUserName, sSocial_unique_id, pictureUrl;
    private CallbackManager callbackManager;
    private String sPictureUrl;
    private String sLoginUserId, sLoginPassword;
    private RelativeLayout btn_register_social;

    private GoogleApiClient mGoogleApiClient;
    private String filePath = "";
    private SocialMediaProfile mediaProfile;
    private TextView btn_login_main;
    private EditText et_login_password, et_login_userid;
    private int mFragmentId = 0;
    private String mFragmentTag = null;
    private ImageButton loc_pass;
    private boolean isclicked = false;
    private ParseContent pcontent;
    private TextInputLayout input_layout_userid, input_layout_pass;
   // private TextView btn_register_social;
    private static final int RC_SIGN_IN = 007;
    Dialog social_dialog;
    private ImageView social_img;
    private String TAG = SignInActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (new PreferenceHelper(this).getLoginType().equals(Const.PatientService.PATIENT)){

            FacebookSdk.sdkInitialize(this);
            callbackManager = CallbackManager.Factory.create();

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            facebookRegisterCallBack();

        }

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        setContentView(R.layout.activity_login);

        pcontent = new ParseContent(this);
        btn_cancel = (ImageButton) findViewById(R.id.btn_cancel);
        social_img = (ImageView) findViewById(R.id.social_img);
        input_layout_userid = (TextInputLayout) findViewById(R.id.input_layout_userid);
        input_layout_pass = (TextInputLayout) findViewById(R.id.input_layout_pass);
        et_login_password = (EditText) findViewById(R.id.et_login_password);
        et_login_userid = (EditText) findViewById(R.id.et_login_userid);
        btn_forgot_password_login = (TextView) findViewById(R.id.btn_forgot_password_login);
        btn_new_user = (TextView) findViewById(R.id.btn_new_user);
        //underline
        btn_new_user.setPaintFlags(btn_new_user.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);

        log_layout = (RelativeLayout) findViewById(R.id.log_layout);
        /*btn_login_fb = (Button) findViewById(R.id.btn_login_fb);*/
        btn_login_main = (TextView) findViewById(R.id.btn_login_main);
        loc_pass = (ImageButton) findViewById(R.id.loc_pass);
        btn_register_social = (RelativeLayout) findViewById(R.id.btn_register_social);

        btn_cancel.setOnClickListener(this);
        /*btn_login_fb.setOnClickListener(this);*/
        btn_login_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {

                    if (new PreferenceHelper(SignInActivity.this).getLoginType().equals(Const.PatientService.PATIENT)){

                        userLogin(Const.MANUAL);

                    }
//                    else if (new PreferenceHelper(this).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)){
//
//                        nurseLogin(Const.MANUAL);
//
//                    }else if (new PreferenceHelper(this).getLoginType().equals(Const.HospitalService.HOSPITAL)){
//
//                        hospitalLogin(Const.MANUAL);
//
//                    }
                }
            }
        });

        if (new PreferenceHelper(this).getLoginType().equals(Const.PatientService.PATIENT)) {
            btn_register_social.setVisibility(View.VISIBLE);
            btn_register_social.setOnClickListener(this);
            btn_new_user.setOnClickListener(this);
            btn_forgot_password_login.setOnClickListener(this);

            btn_forgot_password_login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    addFragment(new ForgotPasswordFragment(), false, Const.FORGOT_PASSWORD_FRAGMENT, true);

                    log_layout.setVisibility(View.GONE);

                }
            });



            /*et_login_userid.setText("cr7juve@gmail.com");
            et_login_password.setText("123456");*/

        }else if (new PreferenceHelper(this).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)){

            btn_register_social.setVisibility(View.GONE);
            btn_new_user.setVisibility(View.GONE);
            btn_forgot_password_login.setVisibility(View.GONE);

            /*et_login_userid.setText("haols@ebizworld.com.vn");
            et_login_password.setText("123456");*/

        }else if (new PreferenceHelper(this).getLoginType().equals(Const.HospitalService.HOSPITAL)){

            btn_new_user.setVisibility(View.GONE);
            btn_forgot_password_login.setVisibility(View.GONE);
            btn_register_social.setVisibility(View.VISIBLE);

            /*et_login_userid.setText("haols@ebizworld.com.vn");
            et_login_password.setText("123456");*/

        }



       /* et_login_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loc_pass.setVisibility(View.VISIBLE);
                loc_pass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isclicked == false) {
                            et_login_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            et_login_password.setSelection(et_login_password.getText().length());
                            isclicked = true;
                            loc_pass.setVisibility(View.VISIBLE);

                        } else {
                            isclicked = false;
                            et_login_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            loc_pass.setVisibility(View.VISIBLE);

                            et_login_password.setSelection(et_login_password.getText().length());
                        }
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
*/

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
        ft.replace(R.id.frame_login, fragment, tag);
        ft.setCustomAnimations(R.anim.slide_in_right,
                R.anim.slide_out_left, R.anim.slide_in_left,
                R.anim.slide_out_right);
        ft.commitAllowingStateLoss();
    }


    @Override
    public void onResume() {
        super.onResume();
        currentfragment = "";
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_cancel:
                Intent i = new Intent(this, WelcomeActivity.class);
                startActivity(i);
                new PreferenceHelper(this).putLoginType(null);
                break;

            case R.id.btn_new_user:
                addFragment(new SignUpMobileFragment(), false, Const.REGISTER_FRAGMENT, true);
                log_layout.setVisibility(View.GONE);
                break;
            /*case R.id.btn_login_fb:
                LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email", "user_birthday", "user_photos", "user_location"));
                loginType = Const.SOCIAL_FACEBOOK;
                break;*/

            case R.id.btn_register_social:

                showSocialPopUP();

                break;
            case R.id.btn_login_main:


                break;
            case R.id.btn_forgot_password_login:

                addFragment(new ForgotPasswordFragment(), false, Const.FORGOT_PASSWORD_FRAGMENT, true);
                log_layout.setVisibility(View.GONE);
                break;


        }
    }

    private void showSocialPopUP() {

        social_dialog = new Dialog(SignInActivity.this, R.style.DialogThemeforview);
        social_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        social_dialog.setCancelable(true);
        social_dialog.setContentView(R.layout.social_connect_popup);
        ImageView back_social = (ImageView) social_dialog.findViewById(R.id.back_social);
        LinearLayout lay_google = (LinearLayout) social_dialog.findViewById(R.id.lay_google);
        LinearLayout lay_fb = (LinearLayout) social_dialog.findViewById(R.id.lay_fb);

        back_social.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                social_dialog.dismiss();
            }
        });

        lay_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EbizworldUtils.showSimpleProgressDialog(SignInActivity.this, getResources().getString(R.string.txt_gmail), false);
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);

            }
        });

        lay_fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(SignInActivity.this, Arrays.asList("public_profile", "email", "user_birthday", "user_photos", "user_location"));
                loginType = Const.SOCIAL_FACEBOOK;
            }
        });

        social_dialog.show();
    }

    private void getLoginDetails() {
        sLoginUserId = et_login_userid.getText().toString().trim();
        sLoginPassword = et_login_password.getText().toString().trim();
    }

    private boolean validate() {
        getLoginDetails();
        if (sLoginUserId.length() == 0) {
            input_layout_userid.setError(getResources().getString(R.string.txt_email_error));
            et_login_userid.requestFocus();
            return false;
        } else if (sLoginPassword.length() == 0) {
            input_layout_pass.setError(getResources().getString(R.string.txt_pass_error));
            et_login_password.requestFocus();
            return false;
        } else {
            input_layout_userid.setError(null);
            input_layout_pass.setError(null);
            return true;

        }
    }


    public void startActivityForResult(Intent intent, int requestCode,
                                       String fragmentTag) {

        mFragmentTag = fragmentTag;
        mFragmentId = 0;
        super.startActivityForResult(intent, requestCode);
    }

    private void facebookRegisterCallBack() {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            //    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {

                                if (jsonObject != null && graphResponse != null) {

                                    EbizworldUtils.appLogDebug("HaoLS", "Facebook callback Json Object: " + jsonObject.toString());
                                    EbizworldUtils.appLogDebug("HaoLS", "Facebook callback Graph response: " + graphResponse.toString());

                                    try {

                                        sUserName = jsonObject.getString("name");
                                        sEmailId = jsonObject.getString("email");
                                        sSocial_unique_id = jsonObject.getString("id");

                                        sPictureUrl = "https://graph.facebook.com/" + sSocial_unique_id + "/picture?type=large";

                                        mediaProfile = new SocialMediaProfile();

                                        new AQuery(SignInActivity.this).id(R.id.social_img).image(sPictureUrl,
                                                true,
                                                true,
                                                200,
                                                0,
                                                new BitmapAjaxCallback() {

                                                    @Override
                                                    public void callback(String url, ImageView iv, Bitmap bm,
                                                                         AjaxStatus status) {

                                                        if (url != null && !url.equals("")) {
                                                            sPictureUrl = new AQuery(SignInActivity.this).getCachedFile(url).getPath();
                                                            Log.d(TAG, "Avatar: " + sPictureUrl);
                                                            mediaProfile.setPictureUrl(sPictureUrl);
                                                        }

                                                    }

                                                });

                                        if (sUserName != null) {

                                            String[] name = sUserName.split(" ");

                                            if (name[0] != null) {

                                                mediaProfile.setFirstName(name[0]);

                                            }

                                            if (name[1] != null) {

                                                mediaProfile.setLastName(name[1]);

                                            }
                                        }

                                        /*mediaProfile.setFullname(sUserName);*/

                                        mediaProfile.setEmailId(sEmailId);

                                        mediaProfile.setSocialUniqueId(sSocial_unique_id);

                                        mediaProfile.setLoginType(Const.SOCIAL_FACEBOOK);

                                        EbizworldUtils.appLogDebug("all details", sUserName + "" + sEmailId + " " + " " + sPictureUrl);

                                        if (sSocial_unique_id != null) {

                                            loginType = Const.SOCIAL_FACEBOOK;
                                            userLogin(Const.SOCIAL_FACEBOOK);

                                        } else {

                                            EbizworldUtils.showShortToast("Invalidate Data", SignInActivity.this);

                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        EbizworldUtils.appLogError("HaoLS", "Facebook callback failed: " + e.toString());
                                    }

                                }
                            }
                        }

                );

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,locale,hometown,email,gender,birthday,location");
                request.setParameters(parameters);
                request.executeAsync();
            }


            @Override
            public void onCancel() {
                EbizworldUtils.showLongToast(getString(R.string.login_cancelled), SignInActivity.this);
            }

            @Override
            public void onError(FacebookException error) {
                EbizworldUtils.showLongToast(getString(R.string.login_failed), SignInActivity.this);
                EbizworldUtils.appLogDebug("HaoLS", "Facebook login failed " + error.toString());
            }
        });
    }

    private void userLogin(String logintype) {

        if (!EbizworldUtils.isNetworkAvailable(SignInActivity.this)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), SignInActivity.this);
            return;

        }

        Commonutils.progressdialog_show(this, getResources().getString(R.string.txt_signin));

        if (logintype.equalsIgnoreCase(Const.MANUAL)) {

            HashMap<String, String> map = new HashMap<String, String>();
            map.put(Const.Params.URL, Const.ServiceType.LOGIN);
            map.put(Const.Params.EMAIL, sLoginUserId);
            map.put(Const.Params.PASSWORD, sLoginPassword);

            if (new PreferenceHelper(this).getDeviceToken() == null){

                final String[] token = {""};

                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {

                        if (!task.isSuccessful()){

                            EbizworldUtils.appLogDebug("HaoLS", "Get Firebase token failed: " + task.getException());
                        }else {

                            token[0] = task.getResult().getToken();
                            EbizworldUtils.appLogDebug("HaoLS", "Firebase token: " + token[0]);
                            EbizworldUtils.storeRegistrationId(SignInActivity.this, token[0]);
                        }
                    }

                });

                if (!token[0].equals("")){

                    map.put(Const.Params.DEVICE_TOKEN, new PreferenceHelper(this).getDeviceToken());

                }else {

                    Commonutils.progressdialog_hide();
                    EbizworldUtils.showLongToast(getResources().getString(R.string.something_was_wrong), SignInActivity.this);
                    return;
                }

            }else {

                map.put(Const.Params.DEVICE_TOKEN, new PreferenceHelper(this).getDeviceToken());
            }

            map.put(Const.Params.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID);
            map.put(Const.Params.LOGIN_BY, Const.MANUAL);
            Log.d("HaoLS", "Login: " + map.toString());
            new VolleyRequester(this, Const.POST, map, Const.ServiceCode.LOGIN,
                    this);

        } else {

            HashMap<String, String> map = new HashMap<String, String>();

            map.put(Const.Params.URL, Const.ServiceType.LOGIN);

            map.put(Const.Params.SOCIAL_ID, sSocial_unique_id);

            if (new PreferenceHelper(this).getDeviceToken() == null){

                final String[] token = {""};

                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {

                        if (!task.isSuccessful()){

                            EbizworldUtils.appLogDebug("HaoLS", "Get Firebase token failed: " + task.getException());
                        }else {

                            token[0] = task.getResult().getToken();
                            EbizworldUtils.appLogDebug("HaoLS", "Firebase token: " + token[0]);
                            EbizworldUtils.storeRegistrationId(SignInActivity.this, token[0]);
                        }
                    }

                });

                if (!token[0].equals("")){

                    map.put(Const.Params.DEVICE_TOKEN, new PreferenceHelper(this).getDeviceToken());

                }else {

                    Commonutils.progressdialog_hide();
                    EbizworldUtils.showLongToast(getResources().getString(R.string.something_was_wrong), SignInActivity.this);
                    return;
                }

            }else {

                map.put(Const.Params.DEVICE_TOKEN, new PreferenceHelper(this).getDeviceToken());
            }

            map.put(Const.Params.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID);
            map.put(Const.Params.LOGIN_BY, logintype);

            Log.d("HaoLS", "social " + map.toString());

            new VolleyRequester(this, Const.POST, map, Const.ServiceCode.LOGIN,
                    this);
        }


    }

    private void nurseLogin(String loginType){

        if (!EbizworldUtils.isNetworkAvailable(SignInActivity.this)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), SignInActivity.this);
            return;

        }

        Commonutils.progressdialog_show(this, getResources().getString(R.string.txt_signin));

        if (loginType.equalsIgnoreCase(Const.MANUAL)){

            HashMap<String,String> map = new HashMap<>();
            map.put(Const.Params.URL, Const.NursingHomeService.LOGIN_URL);
            map.put(Const.Params.TOKEN, new PreferenceHelper(this).getDeviceToken());

            if (new PreferenceHelper(this).getDeviceToken() == null){

                final String[] token = {""};

                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {

                        if (!task.isSuccessful()){

                            EbizworldUtils.appLogDebug("HaoLS", "Get Firebase token failed: " + task.getException());

                        }else {

                            token[0] = task.getResult().getToken();
                            EbizworldUtils.appLogDebug("HaoLS", "Firebase token: " + token[0]);
                            EbizworldUtils.storeRegistrationId(SignInActivity.this, token[0]);
                        }
                    }

                });

                if (!token[0].equals("")){

                    map.put(Const.Params.DEVICE_TOKEN, new PreferenceHelper(this).getDeviceToken());

                }else {

                    Commonutils.progressdialog_hide();
                    EbizworldUtils.showLongToast(getResources().getString(R.string.something_was_wrong), SignInActivity.this);
                    return;
                }

            }else {

                map.put(Const.Params.DEVICE_TOKEN, new PreferenceHelper(this).getDeviceToken());
            }

            map.put(Const.Params.DEVICE_TYPE, Const.DEVICE_TYPE);
            map.put(Const.Params.LOGIN_BY, loginType);
            map.put(Const.Params.EMAIL, sLoginUserId);
            map.put(Const.Params.PASSWORD, sLoginPassword);
            Log.d("HaoLS", map.toString());

            new VolleyRequester(this,
                    Const.POST, map,
                    Const.ServiceCode.NURSE_LOGIN,
                    this);
        }

    }

    private void hospitalLogin(String loginType){

        if (!EbizworldUtils.isNetworkAvailable(SignInActivity.this)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), SignInActivity.this);
            return;

        }

        Commonutils.progressdialog_show(this, getResources().getString(R.string.txt_signin));

        if (loginType.equalsIgnoreCase(Const.MANUAL)){

            HashMap<String,String> map = new HashMap<>();
            map.put(Const.Params.URL, Const.HospitalService.LOGIN_URL);
            map.put(Const.Params.TOKEN, new PreferenceHelper(this).getDeviceToken());

            if (new PreferenceHelper(this).getDeviceToken() == null){

                final String[] token = {""};

                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {

                        if (!task.isSuccessful()){

                            EbizworldUtils.appLogDebug("HaoLS", "Get Firebase token failed: " + task.getException());

                        }else {

                            token[0] = task.getResult().getToken();
                            EbizworldUtils.appLogDebug("HaoLS", "Firebase token: " + token[0]);
                            EbizworldUtils.storeRegistrationId(SignInActivity.this, token[0]);
                        }
                    }

                });

                if (!token[0].equals("")){

                    map.put(Const.Params.DEVICE_TOKEN, new PreferenceHelper(this).getDeviceToken());

                }else {

                    Commonutils.progressdialog_hide();
                    EbizworldUtils.showLongToast(getResources().getString(R.string.something_was_wrong), SignInActivity.this);
                    return;
                }

            }else {

                map.put(Const.Params.DEVICE_TOKEN, new PreferenceHelper(this).getDeviceToken());
            }

            map.put(Const.Params.DEVICE_TYPE, Const.DEVICE_TYPE);
            map.put(Const.Params.LOGIN_BY, loginType);
            map.put(Const.Params.EMAIL, sLoginUserId);
            map.put(Const.Params.PASSWORD, sLoginPassword);
            Log.d("HaoLS", map.toString());
            new VolleyRequester(this,
                    Const.POST,
                    map,
                    Const.ServiceCode.HOSPITAL_LOGIN,
                    this);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        EbizworldUtils.appLogDebug("HaoLS", "Activity Res: " + requestCode);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        Fragment fragment = null;

        if (mFragmentId > 0) {

            fragment = getSupportFragmentManager().findFragmentById(mFragmentId);

        } else if (mFragmentTag != null && !mFragmentTag.equalsIgnoreCase("")) {

            fragment = getSupportFragmentManager().findFragmentByTag(
                    mFragmentTag);

        }
        if (fragment != null) {

            fragment.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode == RC_SIGN_IN) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSignInResult(result);
            EbizworldUtils.appLogInfo("HaoLS", "RC_SIGN_IN " + result.toString());

        }
    }

    private void handleGoogleSignInResult(GoogleSignInResult result) {

        if (result.isSuccess()) {

            EbizworldUtils.removeProgressDialog();
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            mediaProfile = new SocialMediaProfile();
            String personName = "";
            String personPhotoUrl = "";
            String email = "";

            if (acct != null) {

                personName = acct.getDisplayName();
                EbizworldUtils.appLogDebug("HaoLS", "display name: " + acct.getDisplayName());

                personPhotoUrl = acct.getPhotoUrl().toString();
                EbizworldUtils.appLogDebug("HaoLS", "photo url: " + personPhotoUrl);

                email = acct.getEmail();

                EbizworldUtils.appLogDebug("HaoLS", "Name: " + personName + ", email: " + email + ", Image: " + personPhotoUrl);

                loginType = "google";

                /*if (personName != null){

                mediaProfile.setFullname(personName);

                }*/

                if (personName.contains(" ")) {

                    String[] split = personName.split(" ");
                    mediaProfile.setFirstName(split[0]);
                    mediaProfile.setLastName(split[1]);

                } else {

                    mediaProfile.setFirstName(personName);

                }

                if (!TextUtils.isEmpty(personPhotoUrl)
                        || !personPhotoUrl.equalsIgnoreCase("null")) {
                    sPictureUrl = personPhotoUrl;
                    new AQuery(SignInActivity.this).id(R.id.social_img).image(sPictureUrl,
                            true,
                            true,
                            200,
                            0,
                            new BitmapAjaxCallback() {

                                @Override
                                public void callback(String url, ImageView iv, Bitmap bm,
                                                     AjaxStatus status) {

                                    if (url != null && !url.equals("")) {
                                        sPictureUrl = new AQuery(SignInActivity.this).getCachedFile(url).getPath();
                                        mediaProfile.setPictureUrl(sPictureUrl);
                                    }

                                }

                            });

                } else {

                    mediaProfile.setPictureUrl("");

                }

                mediaProfile.setEmailId(email);
                sSocial_unique_id = acct.getId();
                mediaProfile.setSocialUniqueId(sSocial_unique_id);

                mediaProfile.setLoginType("google");

                sEmailId = email;
                userLogin(loginType);

            }else {

                EbizworldUtils.removeProgressDialog();
                EbizworldUtils.showShortToast(getResources().getString(R.string.login_failed), SignInActivity.this);

            }


        } else {

            EbizworldUtils.removeProgressDialog();
            EbizworldUtils.showShortToast(getResources().getString(R.string.login_failed), SignInActivity.this);

        }
    }

    @Override
    public void onBackPressed() {

        if (log_layout.getVisibility() == View.GONE) {
            startActivity(new Intent(SignInActivity.this, SignInActivity.class));
        } else {
            startActivity(new Intent(SignInActivity.this, WelcomeActivity.class));
            finish();
        }
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        switch (serviceCode) {
            case Const.ServiceCode.LOGIN:
                EbizworldUtils.appLogInfo("HaoLS", "" + response);
                Commonutils.progressdialog_hide();

                if (response != null) {

                    try {

                        JSONObject job1 = new JSONObject(response);

                        if (job1.getString("success").equals("true")) {

                            if (pcontent.isSuccessWithStoreId(response)) {

                                /*pcontent.parseUserAndStoreToDb(response);*/
                                pcontent.parsePatientAndStoreToDb(response);

                                new PreferenceHelper(this).putPassword(et_login_password.getText()
                                        .toString());
                                startActivity(new Intent(this, MainActivity.class));
                                this.finish();
                            } else {

                            }

                        } else {
                            Commonutils.progressdialog_hide();

                            if (job1.getString("error_code").equals("125")) {

                                if (mediaProfile != null) {
                                /*    RegisterFragment regFragment = new RegisterFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable("social_profile", mediaProfile);
                                    regFragment.setArguments(bundle);
                                    addFragment(regFragment, false, Const.REGISTER_FRAGMENT,
                                            true);
                                    log_layout.setVisibility(View.GONE);*/
                                    registerSocial(mediaProfile);

                                }

                            }else if (job1.getString("error_code").equals("167")){

                                String error = job1.getString("error");
                                EbizworldUtils.showLongToast(error, this);

                            }else {
                                String error = job1.getString("error");
                                EbizworldUtils.showLongToast(error, this);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        EbizworldUtils.appLogError("Manh", "Login failed: " + e.toString());
                    }
                }
                break;

            case Const.ServiceCode.NURSE_LOGIN:{
                Log.d("HaoLS", "Nurse login Response " + response);
                Commonutils.progressdialog_hide();

                if (response != null){

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getString("success").equals("true")){

                            if (pcontent.isSuccessWithStoreId(response)){

                                pcontent.parseNurseAndStoreToDb(response);
                                new PreferenceHelper(this).putPassword(et_login_password.getText()
                                        .toString());

                                startActivity(new Intent(this, MainActivity.class));
                                this.finish();
                            }else {

                                EbizworldUtils.appLogDebug("HaoLS", "Login failed");
                            }

                        }else {

                            String error = jsonObject.getString("error");
                            EbizworldUtils.showLongToast(error, this);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        EbizworldUtils.appLogDebug("HaoLS", "Login failed " + e.toString());
                    }
                }
            }
            break;

            case Const.ServiceCode.HOSPITAL_LOGIN:{
                Log.d("HaoLS", "Hospital login Response " + response);
                Commonutils.progressdialog_hide();

                if (response != null){

                    try {

                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getString("success").equals("true")){

                            if (pcontent.isSuccessWithStoreId(response)){

                                pcontent.parseHospitalAndStoreToDb(response);

                                new PreferenceHelper(this).putPassword(et_login_password.getText().toString());

                                startActivity(new Intent(this, MainActivity.class));
                                this.finish();

                                EbizworldUtils.appLogDebug("HaoLS", "Login succeeded");
                            }else {

                                EbizworldUtils.appLogDebug("HaoLS", "Login failed");
                            }

                        }else {

                            String error = jsonObject.getString("error");
                            EbizworldUtils.showLongToast(error, this);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        EbizworldUtils.appLogDebug("HaoLS", "Login failed " + e.toString());
                    }
                }
            }
            break;

            case Const.ServiceCode.REGISTER:
                Commonutils.progressdialog_hide();
                EbizworldUtils.appLogInfo("HaoLS", "register response: " + response);

                if (response != null)
                    try {

                        JSONObject job1 = new JSONObject(response);
                        if (job1.getString("success").equals("true")) {


                            if (pcontent.isSuccessWithStoreId(response)) {

                                /*pcontent.parseUserAndStoreToDb(response);*/
                                pcontent.parsePatientAndStoreToDb(response);
                                if (null != social_dialog && social_dialog.isShowing()) {
                                    social_dialog.dismiss();
                                }
                                startActivity(new Intent(SignInActivity.this, MainActivity.class));
                                this.finish();

                            }

                        } else {

                            String error = job1.getString("error_messages");
                            Commonutils.showtoast(error, this);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                break;

        }
    }

    private void registerSocial(SocialMediaProfile mediaProfile) {

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.REGISTER);
        map.put(Const.Params.FIRSTNAME, mediaProfile.getFirstName());
        map.put(Const.Params.LAST_NAME, mediaProfile.getLastName());
        /*map.put(Const.Params.FULLNAME, mediaProfile.getFullname());*/
        map.put(Const.Params.EMAIL, mediaProfile.getEmailId());

        if (null != mediaProfile.getPictureUrl()) {
            map.put(Const.Params.PICTURE, mediaProfile.getPictureUrl());
        } else {
            map.put(Const.Params.PICTURE, "");
        }

        // map.put(Const.Params.SPECIALITY, String.valueOf(speclty.getId()));
        map.put(Const.Params.DEVICE_TOKEN,
                new PreferenceHelper(this).getDeviceToken());
        map.put(Const.Params.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID);

        map.put(Const.Params.LOGIN_BY, mediaProfile.getLoginType());
        map.put(Const.Params.SOCIAL_ID, mediaProfile.getSocialUniqueId());

        map.put(Const.Params.MOBILE, "");

        map.put(Const.Params.CURRENCY, "");
        map.put(Const.Params.TIMEZONE, TimeZone.getDefault().getID());
        map.put(Const.Params.COUNTRY, "");

        map.put(Const.Params.GENDER, "");


        Log.d("HaoLS", "Register social network: " + map.toString());

        if (null != mediaProfile.getPictureUrl()) {

            new MultiPartRequester(this, map, Const.ServiceCode.REGISTER,
                    this);
        } else {
            new VolleyRequester(this, Const.POST, map, Const.ServiceCode.REGISTER,
                    this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (new PreferenceHelper(this).getLoginType().equals(Const.PatientService.PATIENT)){

            LoginManager.getInstance().logOut();

        }

    }

    @Override
    public void onStart() {

        super.onStart();

        if (new PreferenceHelper(this).getLoginType().equals(Const.PatientService.PATIENT)){

            mGoogleApiClient.connect();

        }

     /*   OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            //  Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleGoogleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            EbizworldUtils.showSimpleProgressDialog(SignInActivity.this, getResources().getString(R.string.txt_gmail), false);
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    EbizworldUtils.removeProgressDialog();
                    handleGoogleSignInResult(googleSignInResult);
                }
            });
        }*/
    }


    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if (new PreferenceHelper(this).getLoginType().equals(Const.PatientService.PATIENT)){

            if (mGoogleApiClient.isConnected()) {
                mGoogleApiClient.disconnect();
            }

        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.d("HaoLS", "GoogleApiClient onConnectionFailed: " + connectionResult);

    }

    private Bitmap downloadSocialNetworkBitmap(String url){

        HttpURLConnection urlConnection = null;

        try {

            URL uri = new URL(url);
            urlConnection = (HttpURLConnection) uri.openConnection();

            int statusCode = urlConnection.getResponseCode();

            if (statusCode != HttpStatus.SC_OK){

                return null;

            }

            InputStream inputStream = urlConnection.getInputStream();

            if (inputStream != null){

                Log.d(TAG, "downloadSocialNetworkBitmap: Success");

                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                return bitmap;
            }

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }finally {

            if (urlConnection != null){

                urlConnection.disconnect();

            }

        }

        return null;


    }
}
