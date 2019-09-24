package sg.go.user.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.SystemClock;


public class PreferenceHelper {
    private SharedPreferences app_prefs;
    private final String USER_ID = "user_id";
    private final String DRIVER_ID = "driver_id";
    private final String EMAIL = "email";
    private final String PASSWORD = "password";
    private final String PICTURE = "picture";
    private final String DEVICE_TOKEN = "device_token";
    private final String SESSION_TOKEN = "session_token";
    private final String LOGIN_BY = "login_by";
    private final String SOCIAL_ID = "social_id";
    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String PRE_LOAD = "preLoad";
    private final String REQUEST_ID = "request_id";
    private final String NAME = "name";
    private final String REQ_TIME = "req_time";
    private final String ACCEPT_TIME = "accept_time";
    private final String CURRENT_TIME = "current_time";
    private final String CURRENCY = "currency";
    private final String LANGUAGE = "language";
    private final String REQUEST_TYPE = "type";
    private final String PAYMENT_MODE = "payment_mode";
    private final String WALLET_KEY = "wallet_key";
    private final String AMBULANCE_NAME = "taxi_name";
    private final String REFERRAL_CODE = "referral_code";
    private final String REFERRAL_BONUS = "referral_bonus";
    private final String PHONE = "phone";
    private final String LOGIN_TYPE = "login_type";
    private  final  String OVERVIEW_POLYLINE="overview_polyline";

    private  final  String TYPECAR_BILLING_INFO ="name_typecar_billing_info";
    private  final  String IMAGE_BILLING_INFO  ="image_typecar_billing_info";

    private final  String RECHARGE_WALLET= "recharge_wallet";
    private final  String TOTAL_RECHARGE_WALLET = "total_recharge_wallet";



    private Context context;

    public PreferenceHelper(Context context) {
        app_prefs = context.getSharedPreferences(Const.PREF_NAME,Context.MODE_PRIVATE);
        this.context = context;
    }


    /*------- Save Total Money Recharge Wallet -------*/

    public  void putTotalRechargeWallet(String total_recharge_wallet){
        Editor editor = app_prefs.edit();
        editor.putString(TOTAL_RECHARGE_WALLET,total_recharge_wallet);
        editor.apply();
    }

    public String getTotalRechargeWallet(){

        return app_prefs.getString(TOTAL_RECHARGE_WALLET,"");
    }

    public void putRechargeWallet(String recharge_wallet){
        Editor editor = app_prefs.edit();
        editor.putString(RECHARGE_WALLET,"recharge_wallet");
        editor.apply();

    }

    public  String  getRechargeWallet(){
        return app_prefs.getString(RECHARGE_WALLET,"");
    }



    /*------- Save Type Car And Image Type Car-------*/

    public  void putTypeCarBillingInfo(String typecar_billing_info){

        Editor editor = app_prefs.edit();
        editor.putString(TYPECAR_BILLING_INFO,typecar_billing_info);
        editor.apply();

    }
    public String getTypeCarBillingInfo(){

        return app_prefs.getString(TYPECAR_BILLING_INFO,"");
    }


    public  void putImageTypeCarBillingInfo(String Imagetypecar_billing_info){

        Editor editor = app_prefs.edit();
        editor.putString(IMAGE_BILLING_INFO,Imagetypecar_billing_info);
        editor.apply();

    }
    public String getImageTypeCarBillingInfo(){

        return app_prefs.getString(IMAGE_BILLING_INFO,"");
    }


    /*------- Save Overview_Polyline -------*/

  public void  putOverViewPolyline(String overviewpolyline_type){

        Editor editor = app_prefs.edit();
        editor.putString(OVERVIEW_POLYLINE,overviewpolyline_type);
        editor.apply();

  }

  public String getOverViewPolyline(){

        return app_prefs.getString(OVERVIEW_POLYLINE,"");
  }



    public void putLoginType(String login_type){

        Editor editor = app_prefs.edit();
        editor.putString(LOGIN_TYPE, login_type);
        editor.apply();
    }

    public String getLoginType(){

        return app_prefs.getString(LOGIN_TYPE, "");

    }


    public void putPhone(String name) {
        Editor edit = app_prefs.edit();
        edit.putString(PHONE, name);
        edit.apply();
    }

    public String getPhone() {
        return app_prefs.getString(PHONE, "");
    }




    public void putUserId(String userId) {
        Editor edit = app_prefs.edit();
        edit.putString(USER_ID, userId);
        edit.apply();
    }

    public void putUser_name(String name) {
        Editor edit = app_prefs.edit();
        edit.putString(NAME, name);
        edit.apply();
    }

    public String getUser_name() {
        return app_prefs.getString(NAME, "");
    }


    public void putEmail(String email) {
        Editor edit = app_prefs.edit();
        edit.putString(EMAIL, email);
        edit.apply();
    }

    public String getEmail() {
        return app_prefs.getString(EMAIL, null);
    }

    public void putPicture(String picture) {
        Editor edit = app_prefs.edit();
        edit.putString(PICTURE, picture);
        edit.apply();
    }

    public void putRequestId(int reqId) {
        Editor edit = app_prefs.edit();
        edit.putInt(REQUEST_ID, reqId);
        edit.apply();
    }

    public int getRequestId() {
        return app_prefs.getInt(REQUEST_ID, Const.NO_REQUEST);
    }


    public String getPicture() {
        return app_prefs.getString(PICTURE, null);
    }

    public void putPassword(String password) {
        Editor edit = app_prefs.edit();
        edit.putString(PASSWORD, password);
        edit.apply();
    }

    public String getPassword() {
        return app_prefs.getString(PASSWORD, null);
    }

    public void putSocialId(String id) {
        Editor edit = app_prefs.edit();
        edit.putString(SOCIAL_ID, id);
        edit.apply();
    }

    public String getSocialId() {
        return app_prefs.getString(SOCIAL_ID, null);
    }

    public String getUserId() {
        return app_prefs.getString(USER_ID, null);

    }

    public void putDeviceToken(String deviceToken) {
        Editor edit = app_prefs.edit();
        edit.putString(DEVICE_TOKEN, deviceToken);
        edit.apply();
    }

    public String getDeviceToken() {
        return app_prefs.getString(DEVICE_TOKEN, null);

    }



    public void putReq_time(long req_time) {
        Editor edit = app_prefs.edit();
        edit.putLong(REQ_TIME, req_time);
        edit.apply();
    }

    public long getReq_time() {
        return app_prefs.getLong(REQ_TIME, SystemClock.uptimeMillis());

    }


    public void putDriver_id(String driver_id) {
        Editor edit = app_prefs.edit();
        edit.putString(DRIVER_ID, driver_id);
        edit.apply();
    }

    public String getDriver_id() {
        return app_prefs.getString(DRIVER_ID, "");

    }


    public void putWallet_key(String wallet_key) {
        Editor edit = app_prefs.edit();
        edit.putString(WALLET_KEY, wallet_key);
        edit.apply();
    }

    public String getWallet_key() {
        return app_prefs.getString(WALLET_KEY, "");

    }
    public void putAmbulance_name(String ambulance_name) {
        Editor edit = app_prefs.edit();
        edit.putString(AMBULANCE_NAME, ambulance_name);
        edit.apply();
    }

    public String getAmbulance_name() {
        return app_prefs.getString(AMBULANCE_NAME, "");

    }

    public void putSessionToken(String sessionToken) {
        Editor edit = app_prefs.edit();
        edit.putString(SESSION_TOKEN, sessionToken);
        edit.apply();
    }

    public String getSessionToken() {
        return app_prefs.getString(SESSION_TOKEN, null);

    }


    public void putRequestType(String req_type) {
        Editor edit = app_prefs.edit();
        edit.putString(REQUEST_TYPE, req_type);
        edit.apply();
    }

    public String getRequestType() {
        return app_prefs.getString(REQUEST_TYPE, "1");

    }

    public void putAccept_time(long accept_time) {
        Editor edit = app_prefs.edit();
        edit.putLong(ACCEPT_TIME, accept_time);
        edit.apply();
    }

    public long getAccept_time() {
        return app_prefs.getLong(ACCEPT_TIME, 0L);

    }

    public void putCurrent_time(long accept_time) {
        Editor edit = app_prefs.edit();
        edit.putLong(CURRENT_TIME, accept_time);
        edit.apply();
    }

    public long getCurent_time() {
        return app_prefs.getLong(CURRENT_TIME, 0L);

    }

    public void putLoginBy(String loginBy) {
        Editor edit = app_prefs.edit();
        edit.putString(LOGIN_BY, loginBy);
        edit.apply();
    }

    public String getLoginBy() {
        return app_prefs.getString(LOGIN_BY,"");
    }

    public void putPaymentMode(String payment_mode) {

        Editor edit = app_prefs.edit();
        edit.putString(PAYMENT_MODE, payment_mode);
        edit.apply();

    }

    public String getPaymentMode() {

        return app_prefs.getString(PAYMENT_MODE, "");

    }

    public void putRegisterationID(String RegID) {
        Editor edit = app_prefs.edit();
        edit.putString(PROPERTY_REG_ID, RegID);
        edit.apply();
    }

    public String getRegistrationID() {
        return app_prefs.getString(PROPERTY_REG_ID, "");
    }


    public void putAppVersion(int version) {
        Editor edit = app_prefs.edit();
        edit.putInt(PROPERTY_APP_VERSION, version);
        edit.apply();
    }

    public int getAppVersion() {
        return app_prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
    }

    public void setPreLoad(boolean totalTime) {

        app_prefs
                .edit()
                .putBoolean(PRE_LOAD, totalTime)
                .apply();
    }

    public boolean getPreLoad() {
        return app_prefs.getBoolean(PRE_LOAD, false);
    }

    public void putCurrency(String currency) {
        Editor edit = app_prefs.edit();
        edit.putString(CURRENCY, currency);
        edit.apply();
    }

    public String getCurrency() {
        return app_prefs.getString(CURRENCY, "");

    }

    public void putLanguage(String language) {
        Editor edit = app_prefs.edit();
        edit.putString(LANGUAGE, language);
        edit.apply();
    }

    public String getLanguage() {
        return app_prefs.getString(LANGUAGE, "");
    }

    public void clearRequestData() {
        putRequestId(Const.NO_REQUEST);
        putReq_time(SystemClock.uptimeMillis());
        putAccept_time(0L);
        putCurrent_time(0L);
        putDriver_id("");

    }

    public void Logout() {

        putLoginType(null);
        putUserId(null);
        putSessionToken(null);
        putSocialId(null);
        putEmail(null);
        putPassword(null);
        putPicture(null);
        putLoginBy(Const.MANUAL);
        putPaymentMode("");
        putWallet_key("");
        putAmbulance_name("");

    }

    public void putReferralCode(String referral_code) {
        Editor edit = app_prefs.edit();
        edit.putString(REFERRAL_CODE, referral_code);
        edit.apply();
    }

    public String getReferralCode() {
        return app_prefs.getString(REFERRAL_CODE, "");
    }

    public void putReferralBonus(String referral_bonus) {
        Editor edit = app_prefs.edit();
        edit.putString(REFERRAL_BONUS, referral_bonus);
        edit.apply();
    }
    public String getReferralBONUS() {
        return app_prefs.getString(REFERRAL_BONUS, "");
    }



}


