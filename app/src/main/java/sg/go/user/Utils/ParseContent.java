package sg.go.user.Utils;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import sg.go.user.Models.AdsList;
import sg.go.user.Models.Hospital;
import sg.go.user.Models.Nurse;
import sg.go.user.Models.Patient;
import sg.go.user.Models.RequestDetail;
import sg.go.user.Models.Schedule;
import sg.go.user.RealmController.RealmController;

/**
 * Created by user on 8/22/2016.
 */
public class ParseContent {
    private Activity activity;
    private Realm mRealm;
    private PreferenceHelper preferenceHelper;
    private final String KEY_SUCCESS = "success";
    private final String KEY_ERROR = "error";
    private final String KEY_ERROR_CODE = "error_code";
    public static final String IS_CANCELLED = "is_cancelled";

    public ParseContent(Activity activity) {
        // TODO Auto-generated constructor stub
        this.activity = activity;
        preferenceHelper = new PreferenceHelper(activity);
    }


    public boolean isSuccessWithStoreId(String response) {

        if (TextUtils.isEmpty(response))
            return false;

        try {

            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.getString(KEY_SUCCESS).equals("true")) {

                if (new PreferenceHelper(activity).getLoginType().equals(Const.PatientService.PATIENT)){

                    preferenceHelper.putUserId(jsonObject
                            .getString(Const.Params.ID));

                    preferenceHelper.putSessionToken(jsonObject
                            .getString(Const.Params.TOKEN));

                    preferenceHelper.putEmail(jsonObject
                            .optString(Const.Params.EMAIL));
                    preferenceHelper.putPicture(jsonObject
                            .optString(Const.Params.PICTURE));

                    if (jsonObject.has(Const.Params.LOGIN_BY)){

                        preferenceHelper.putLoginBy(jsonObject
                                .getString(Const.Params.LOGIN_BY));

                    }

                    if (jsonObject.has(Const.Params.FULLNAME)){

                        preferenceHelper.putUser_name(jsonObject
                                .getString(Const.Params.FULLNAME));

                    }


                    if (!preferenceHelper.getLoginBy().equalsIgnoreCase(Const.MANUAL) && jsonObject.has(Const.Params.SOCIAL_ID)) {

                        preferenceHelper.putSocialId(jsonObject
                                .getString(Const.Params.SOCIAL_ID));

                    }

                    if(jsonObject.has(Const.Params.PAYMENT_MODE)){
                        preferenceHelper.putPaymentMode(jsonObject
                                .getString(Const.Params.PAYMENT_MODE));
                    }

                }else if (new PreferenceHelper(activity).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)){

                    preferenceHelper.putUserId(jsonObject
                            .getString(Const.Params.ID));
                    preferenceHelper.putSessionToken(jsonObject
                            .getString(Const.Params.TOKEN));
                    preferenceHelper.putEmail(jsonObject
                            .optString(Const.Params.EMAIL));
                    preferenceHelper.putPicture(jsonObject
                            .optString(Const.Params.PICTURE));

                    if (jsonObject.has(Const.Params.LOGIN_BY)){

                        preferenceHelper.putLoginBy(jsonObject
                                .getString(Const.Params.LOGIN_BY));

                    }
                }else if (new PreferenceHelper(activity).getLoginType().equals(Const.HospitalService.HOSPITAL)){

                    preferenceHelper.putUserId(jsonObject
                            .getString(Const.Params.ID));
                    preferenceHelper.putSessionToken(jsonObject
                            .getString(Const.Params.TOKEN));

                    if (jsonObject.has(Const.Params.FULLNAME)){

                        preferenceHelper.putUser_name(jsonObject.getString(Const.Params.FULLNAME));

                    }

                    preferenceHelper.putEmail(jsonObject.getString(Const.Params.EMAIL));
                    preferenceHelper.putPicture(jsonObject.getString(Const.Params.PICTURE));

                }

                EbizworldUtils.appLogDebug("HaoLS", "isSuccessWithStoreId succeed");
                return true;

            } else {

                EbizworldUtils.appLogDebug("HaoLS", "isSuccessWithStoreId failed");
                return false;

            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            EbizworldUtils.appLogDebug("HaoLS", "isSuccessWithStoreId failed " + e.toString());
        }

        EbizworldUtils.appLogDebug("HaoLS", "isSuccessWithStoreId failed");
        return false;
    }

    public Hospital parseHospitalAndStoreToDb(String response) {
        Hospital hospital = null;
        try {
            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.getBoolean(KEY_SUCCESS)) {
                hospital = new Hospital();
                mRealm = Realm.getInstance(activity);

                RealmController.with(activity).clearAllHospital();

                hospital.setId(jsonObject.getInt(Const.Params.ID));
                hospital.setmHospitalName(jsonObject.getString(Const.Params.FULLNAME));
                hospital.setmContactName(jsonObject.getString(Const.Params.CONTACT_NAME));
                hospital.setmContactNumber(jsonObject.getString(Const.Params.CONTACT_NO));
                hospital.setmPreferredUsername(jsonObject.getString(Const.Params.PREFERRED_USERNAME));
                hospital.setmMobileNumber(jsonObject.getString(Const.Params.MOBILE));
                hospital.setmEmailAddress(jsonObject.getString(Const.Params.EMAIL));
                hospital.setmMainAddress(jsonObject.getString(Const.Params.ADDRESS));
                hospital.setmCompanyPicture(jsonObject.getString(Const.Params.PICTURE));
                hospital.setmAmbulanceOperator(jsonObject.getInt(Const.Params.OPERATOR_ID));

                if (jsonObject.has(Const.Params.POSTAL)){

                    hospital.setmPostal(jsonObject.getString(Const.Params.POSTAL));

                }

                if (jsonObject.has(Const.Params.FLOOR_NUMBER)){

                    hospital.setmFloorNumber(jsonObject.getInt(Const.Params.FLOOR_NUMBER));

                }

                if (jsonObject.has(Const.Params.WARD)){

                    hospital.setmWard(jsonObject.getString(Const.Params.WARD));

                }


                mRealm.beginTransaction();
                mRealm.copyToRealm(hospital);
                mRealm.commitTransaction();

                EbizworldUtils.appLogDebug("HaoLS", "parseHospitalAndStoreToDB succeeded");


            } else {
                // EbizworldUtils.showToast(jsonObject.getString(KEY_ERROR),

                EbizworldUtils.appLogDebug("HaoLS", "parseHospitalAndStoreToDB failed ");
                // activity);

            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            EbizworldUtils.appLogDebug("HaoLS", "parseHospitalAndStoreToDB failed " + e.toString());
        }


        return hospital;
    }

    public Patient parsePatientAndStoreToDb(String response) {

        Patient patient = null;

        try {

            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.getBoolean(KEY_SUCCESS)) {

                patient = new Patient();

                mRealm = Realm.getInstance(activity);
                RealmController.with(activity).clearAllPatient();

                patient.setId(jsonObject.getInt(Const.Params.ID));

                patient.setmFullname(jsonObject.getString(Const.Params.FULLNAME));
                patient.setmPicture(jsonObject.getString(Const.Params.PICTURE));
                patient.setmMobile(jsonObject.getString(Const.Params.MOBILE));
                preferenceHelper.putPhone(jsonObject.getString(Const.Params.MOBILE));

                if (jsonObject.has(Const.Params.HOME_NUMBER)){

                    patient.setmHomeNumber(jsonObject.getString(Const.Params.HOME_NUMBER));
                }

                if (jsonObject.has(Const.Params.FAMILY_MEMBER_NAME)){

                    patient.setmFamilyMemberName(jsonObject.getString(Const.Params.FAMILY_MEMBER_NAME));

                }

                patient.setmEmail(jsonObject.getString(Const.Params.EMAIL));

                /*if (jsonObject.has("is_registered")){

                    patient.setmIsRegistered(jsonObject.getInt("is_registered"));
                }

                if (jsonObject.has("otp")){

                    patient.setmOTP(jsonObject.getInt("otp"));
                }

                if (jsonObject.has("is_activated")){

                    patient.setmIsActivated(jsonObject.getInt("is_activated"));
                }

                if (jsonObject.has("is_activated")){

                    patient.setmIsActivated(jsonObject.getInt("is_activated"));
                }

                if (jsonObject.has(Const.Params.GENDER)){

                    patient.setmGender(jsonObject.getString(Const.Params.GENDER));
                }

                if (jsonObject.has("paypal_email")){

                    patient.setmPaypalEmail(jsonObject.getString("paypal_email"));
                }

                if (jsonObject.has(Const.Params.TIMEZONE)){

                    patient.setmTimeZone(jsonObject.getString(Const.Params.TIMEZONE));
                }

                if (jsonObject.has(Const.Params.CURRENCY)) {
                    patient.setmCurrencyCode(jsonObject.getString(Const.Params.CURRENCY));
                    preferenceHelper.putCurrency(jsonObject.getString(Const.Params.CURRENCY));
                }

                if (jsonObject.has(Const.Params.COUNTRY)) {
                    patient.setmCountry(jsonObject.getString(Const.Params.COUNTRY));
                }*/

                patient.setmReferralCode(jsonObject.getString(Const.Params.REFERRAL_CODE));

                preferenceHelper.putReferralBonus(jsonObject.getString(Const.Params.REFERRAL_BONUS));

                preferenceHelper.putReferralCode(jsonObject.getString(Const.Params.REFERRAL_CODE));

                if(jsonObject.has("wallet_bay_key")) {
                    preferenceHelper.putWallet_key(jsonObject.optString("wallet_bay_key"));
                }

                if (jsonObject.has(Const.Params.BLOCK_NUMBER)) {
                    /*patient.setmBlockNumber(jsonObject.getString(Const.Params.BLOCK_NUMBER));*/
                    patient.setmBlockNumber(String.valueOf(jsonObject.getInt(Const.Params.BLOCK_NUMBER)));
                }

                if (jsonObject.has(Const.Params.UNIT_NUMBER)) {
                    /*patient.setmBlockNumber(jsonObject.getString(Const.Params.UNIT_NUMBER));*/
                    patient.setmUnitNumber(String.valueOf(jsonObject.getString(Const.Params.UNIT_NUMBER)));
                }

                if (jsonObject.has(Const.Params.STREET_NAME)) {
                    patient.setmStreetName(jsonObject.getString(Const.Params.STREET_NAME));
                }

                if (jsonObject.has(Const.Params.POSTAL)) {
                    patient.setmPostal(jsonObject.getString(Const.Params.POSTAL));
                }

                if (jsonObject.has(Const.Params.WEIGHT)) {
                    patient.setmWeight(jsonObject.getDouble(Const.Params.WEIGHT));
                }

                if (jsonObject.has(Const.Params.LIFT_LANDING)) {
                    patient.setmLiftLanding(jsonObject.getInt(Const.Params.LIFT_LANDING));
                }

                if (jsonObject.has(Const.Params.STAIRS)) {
                    patient.setmStairs(jsonObject.getInt(Const.Params.STAIRS));
                }

                if (jsonObject.has(Const.Params.NO_STAIRS)) {
                    patient.setmNoStairs(jsonObject.getInt(Const.Params.NO_STAIRS));
                }

                if (jsonObject.has(Const.Params.NO_STAIRS)) {
                    patient.setmNoStairs(jsonObject.getInt(Const.Params.NO_STAIRS));
                }

                if (jsonObject.has(Const.Params.LOW_STAIRS)) {
                    patient.setmLowStairs(jsonObject.getInt(Const.Params.LOW_STAIRS));
                }

                if (jsonObject.has(Const.Params.PREFERRED_USERNAME)) {
                    patient.setmPreferredUsername(jsonObject.getString(Const.Params.PREFERRED_USERNAME));
                }

                if (jsonObject.has(Const.Params.OPERATOR_ID)) {
                    patient.setmOperatorID(jsonObject.getInt(Const.Params.OPERATOR_ID));
                }

                if (jsonObject.has(Const.Params.PATIENT_CONDITION)) {
                    patient.setmPatientCondition(jsonObject.getString(Const.Params.PATIENT_CONDITION));
                }

                if (jsonObject.has(Const.Params.STRETCHER)) {
                    patient.setmStretcher(jsonObject.getInt(Const.Params.STRETCHER));
                }

                if (jsonObject.has(Const.Params.WHEEL_CHAIR)) {
                    patient.setmWheelChair(jsonObject.getInt(Const.Params.WHEEL_CHAIR));
                }

                if (jsonObject.has(Const.Params.OXYGEN)) {
                    patient.setmOxygen(jsonObject.getInt(Const.Params.OXYGEN));
                }

                if (jsonObject.has(Const.Params.ESCORTS)) {
                    patient.setmEscorts(jsonObject.getInt(Const.Params.ESCORTS));
                }

                if (jsonObject.has(Const.Params.ADD_INFORMATION)) {
                    patient.setmAddInformation(jsonObject.getString(Const.Params.ADD_INFORMATION));
                }

                if (jsonObject.has(Const.Params.PAYMENT_MODE)) {
                    patient.setmPaymentMode(jsonObject.getString(Const.Params.PAYMENT_MODE));
                }

                //      preferenceHelper.putLoginBy(jsonObject.optString("login_by"));


                mRealm.beginTransaction();
                mRealm.copyToRealm(patient);
                mRealm.commitTransaction();
                EbizworldUtils.appLogDebug("HaoLS", "parsePatientAndStoreToDB succeeded");


            } else {
                // EbizworldUtils.showToast(jsonObject.getString(KEY_ERROR),
                // activity);

                EbizworldUtils.appLogDebug("HaoLS", "parsePatientAndStoreToDB failed ");

            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            EbizworldUtils.appLogDebug("HaoLS", "parsePatientAndStoreToDB failed " + e.toString());
        }

        EbizworldUtils.appLogDebug("HaoLS", "parsePatientAndStoreToDB succeeded");
        return patient;
    }

    public Nurse parseNurseAndStoreToDb(String response) {
        Nurse nurse = null;
        try {
            JSONObject jsonObject = new JSONObject(response);

            if (jsonObject.getBoolean(KEY_SUCCESS)) {
                nurse = new Nurse();
                mRealm = Realm.getInstance(activity);
                RealmController.with(activity).clearAllNurse();

                nurse.setId(jsonObject.getInt(Const.Params.ID));
                nurse.setmFullName(jsonObject.optString(Const.Params.FULLNAME));

                if (jsonObject.has(Const.Params.CONTACT_NAME)){

                    nurse.setmContact_Name(jsonObject.optString(Const.Params.CONTACT_NAME));

                }

                if (jsonObject.has(Const.Params.CONTACT_NO)){

                    nurse.setmContact_Number(jsonObject.optString(Const.Params.CONTACT_NO));

                }

                if (jsonObject.has(Const.Params.PREFERRED_USERNAME)){

                    nurse.setmPreferred_Username(jsonObject.optString(Const.Params.PREFERRED_USERNAME));

                }

                nurse.setmMobile(jsonObject.optString(Const.Params.MOBILE));
                preferenceHelper.putPhone(jsonObject.getString(Const.Params.MOBILE));

                nurse.setmEmail(jsonObject.optString(Const.Params.EMAIL));

                nurse.setmPictureUrl(jsonObject.getString(Const.Params.PICTURE));

                if (jsonObject.has(Const.Params.ADDRESS)){

                    nurse.setmAddress(jsonObject.optString(Const.Params.ADDRESS));

                }

                if (jsonObject.has(Const.Params.TIMEZONE)){

                    nurse.setmTimezone(jsonObject.getString(Const.Params.TIMEZONE));

                }

                if (jsonObject.has(Const.Params.OPERATOR_ID)){

                    nurse.setmOperatorID(Integer.parseInt(jsonObject.getString(Const.Params.OPERATOR_ID)));

                }

                //      preferenceHelper.putLoginBy(jsonObject.optString("login_by"));

                if (jsonObject.has(Const.Params.CURRENCY)) {

                    nurse.setmCurrencyCode(jsonObject.getString(Const.Params.CURRENCY));
                    preferenceHelper.putCurrency(jsonObject.getString(Const.Params.CURRENCY));

                }

                if (jsonObject.has(Const.Params.GENDER)) {

                    nurse.setmGender(jsonObject.getString(Const.Params.GENDER));

                }

                if (jsonObject.has(Const.Params.COUNTRY)) {

                    nurse.setmCountry(jsonObject.getString(Const.Params.COUNTRY));

                }


                mRealm.beginTransaction();
                mRealm.copyToRealm(nurse);
                mRealm.commitTransaction();


            } else {
                // EbizworldUtils.showToast(jsonObject.getString(KEY_ERROR),
                // activity);

            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            EbizworldUtils.appLogDebug("HaoLS", "ParseNurseAndStoreDatabase failed" + e.toString());
        }

        EbizworldUtils.appLogDebug("HaoLS", "ParseNurseAndStoreDatabase success");
        return nurse;
    }


    public RequestDetail parseRequestStatusNormal(String response) {

        if (TextUtils.isEmpty(response)) {
            return null;
        }
        RequestDetail requestDetail = new RequestDetail();
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getBoolean(KEY_SUCCESS)) {

                requestDetail.setCurrnecy_unit(jsonObject.optString("currency"));

                requestDetail.setCancellationFee(jsonObject.optString("cancellation_fine"));


                JSONArray jarray = jsonObject.getJSONArray("data");
                if (jarray.length() > 0) {

                    JSONObject dataObject = jarray.getJSONObject(0);

                    new PreferenceHelper(activity).putRequestId(Integer.valueOf(dataObject.getString("request_id")));
                    EbizworldUtils.appLogDebug("HaoLS", "Request ID: " + new PreferenceHelper(activity).getRequestId());

                    requestDetail.setRequestId(Integer.valueOf(dataObject.getString("request_id")));

                    if (dataObject != null) {

                        if(dataObject.has("overview_polyline")){

                            requestDetail.setPolyline_string(dataObject.getString("overview_polyline"));

                            new PreferenceHelper(activity).putOverViewPolyline(dataObject.getString("overview_polyline"));

                        }

                        if (dataObject.has("provider_status")) {

                            requestDetail.setTripStatus(dataObject.getInt("provider_status"));

                        }

                        if (dataObject.has("later")){

                            requestDetail.setLater(String.valueOf(dataObject.getInt("later")));
                        }

                        if (!dataObject.getString("provider_name").equals("null")) {

                            requestDetail.setDriver_name(dataObject.getString("provider_name"));

                        }

                        if (dataObject.has("status")) {

                            requestDetail.setDriverStatus(dataObject.getInt("status"));


                        }
                        if (!dataObject.getString("provider_mobile").equals("null")) {

                            requestDetail.setDriver_mobile(dataObject.getString("provider_mobile"));

                        }

                        if (!dataObject.getString("provider_picture").equals("null")) {

                            requestDetail.setDriver_picture(dataObject.getString("provider_picture"));

                        }

                        if (!dataObject.optString("car_image").equals("null")) {

                            requestDetail.setDriver_car_picture(dataObject.getString("car_image"));

                        }
                        if (!dataObject.optString("model").equals("null")) {

                            requestDetail.setDriver_car_model(dataObject.getString("model"));
                        }
                        if (!dataObject.getString("color").equals("null")) {

                            requestDetail.setDriver_car_color(dataObject.getString("color"));
                        }
                        if (!dataObject.getString("plate_no").equals("null")) {

                            requestDetail.setDriver_car_number(dataObject.getString("plate_no"));
                        }
                        requestDetail.setRequest_type(dataObject.optString("request_status_type"));
                        requestDetail.setNo_tolls(jsonObject.optString("number_tolls"));
                        requestDetail.setDriver_id(dataObject.getString("provider_id"));

                        new PreferenceHelper(activity).putDriver_id(dataObject.getString("provider_id"));

                        requestDetail.setDriver_rating(dataObject.getString("rating"));
                        requestDetail.setS_address(dataObject.getString("s_address"));
                        requestDetail.setD_address(dataObject.getString("d_address"));
                        requestDetail.setS_lat(dataObject.getString("s_latitude"));
                        requestDetail.setS_lng(dataObject.getString("s_longitude"));
                        requestDetail.setD_lat(dataObject.getString("d_latitude"));
                        requestDetail.setD_lng(dataObject.getString("d_longitude"));


                        requestDetail.setAdStopLatitude(dataObject.getString("adstop_latitude"));
                        requestDetail.setAdStopLongitude(dataObject.getString("adstop_longitude"));
                        requestDetail.setAdStopAddress(dataObject.getString("adstop_address"));
                        requestDetail.setIsAdStop(dataObject.getString("is_adstop"));
                        requestDetail.setIsAddressChanged(dataObject.getString("is_address_changed"));



                        if (!dataObject.getString("driver_latitude").equals("null")) {
                            requestDetail.setDriver_latitude(Double.valueOf(dataObject.getString("driver_latitude")));
                        }
                        if (!dataObject.getString("driver_longitude").equals("null")) {
                            requestDetail.setDriver_longitude(Double.valueOf(dataObject.getString("driver_longitude")));
                        }
                        requestDetail.setVehical_img(dataObject.getString("type_picture"));

                    }


                    JSONArray invoicejarray = jsonObject.getJSONArray(Const.Params.INVOICE);
                    if (invoicejarray.length() > 0) {
                        JSONObject invoiceobj = invoicejarray.getJSONObject(0);
                        requestDetail.setTrip_time(invoiceobj.getString("total_time"));
                        requestDetail.setPayment_mode(invoiceobj.getString("payment_mode"));
                        requestDetail.setTrip_base_price(invoiceobj.getString("base_price"));
                        requestDetail.setTrip_total_price(invoiceobj.getString("total"));
                        requestDetail.setDistance_unit(invoiceobj.optString("distance_unit"));

                        if (invoiceobj.has(Const.Params.A_AND_E)){

                            requestDetail.setA_and_e(invoiceobj.getString(Const.Params.A_AND_E));

                        }

                        if (invoiceobj.has(Const.Params.IMH)){

                            requestDetail.setImh(invoiceobj.getString(Const.Params.IMH));
                        }

                        if (invoiceobj.has(Const.Params.FERRY_TERMINALS)){

                            requestDetail.setFerry_terminals(invoiceobj.getString(Const.Params.FERRY_TERMINALS));
                        }

                        if (invoiceobj.has(Const.Params.STAIRCASE)){

                            requestDetail.setStaircase(invoiceobj.getString(Const.Params.STAIRCASE));
                        }

                        if (invoiceobj.has(Const.Params.WEIGHT)){

                            requestDetail.setWeight(invoiceobj.getString(Const.Params.WEIGHT));
                        }

                        if (invoiceobj.has(Const.Params.TARMAC)){

                            requestDetail.setTarmac(invoiceobj.getString(Const.Params.TARMAC));
                        }

                        if (invoiceobj.has(Const.Params.OXYGEN)){

                            requestDetail.setOxygen_tank(invoiceobj.getString(Const.Params.OXYGEN));
                        }

                        if (invoiceobj.has(Const.Params.CASE_TYPE)){

                            requestDetail.setPickup_type(invoiceobj.getString(Const.Params.CASE_TYPE));
                        }


                        if(invoiceobj.has("distance_travel")) {
                            requestDetail.setTrip_distance(invoiceobj.getString("distance_travel"));
                        }
                    }

                } else {


                    requestDetail.setTripStatus(Const.NO_REQUEST);

                    new PreferenceHelper(activity).putRequestId(Const.NO_REQUEST);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            EbizworldUtils.appLogError("HaoLS", "Parse requestStatus failed: " + e.toString());
        }

        EbizworldUtils.appLogInfo("HaoLS", "parseRequestStatusNormal succeeded");
        return requestDetail;
    }

    public RequestDetail parseRequestStatusSchedule(String response) {

        if (TextUtils.isEmpty(response)) {
            return null;
        }
        RequestDetail requestDetail = new RequestDetail();
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getBoolean(KEY_SUCCESS)) {
                requestDetail.setCurrnecy_unit(jsonObject.optString("currency"));
                requestDetail.setCancellationFee(jsonObject.optString("cancellation_fine"));
                JSONArray jarray = jsonObject.getJSONArray("data");
                if (jarray.length() > 0) {

                    for (int i = 0; i < jarray.length(); i++){

                        EbizworldUtils.appLogDebug("HaoLS", "Loop: " + i);

                        JSONObject driverdata = jarray.getJSONObject(i);

                        if (driverdata != null) {

                            if (driverdata.has("provider_status") && driverdata.has("later")) {

                                if (driverdata.getInt("provider_status") > 0 &&
                                        driverdata.getInt("provider_status") <= 6 &&
                                        driverdata.getInt("later") == 0){

                                        if (driverdata.has("provider_status")) {

                                            requestDetail.setTripStatus(driverdata.getInt("provider_status"));

                                        }

                                        if (driverdata.has("later")){

                                            requestDetail.setLater(String.valueOf(driverdata.getInt("later")));
                                        }

                                        new PreferenceHelper(activity).putRequestId(Integer.valueOf(driverdata.getString("request_id")));

                                        requestDetail.setRequestId(Integer.valueOf(driverdata.getString("request_id")));

                                        if (!driverdata.getString("provider_name").equals("null")) {

                                            requestDetail.setDriver_name(driverdata.getString("provider_name"));

                                        }

                                        if (driverdata.has("status")) {

                                            requestDetail.setDriverStatus(driverdata.getInt("status"));


                                        }
                                        if (!driverdata.getString("provider_mobile").equals("null")) {

                                            requestDetail.setDriver_mobile(driverdata.getString("provider_mobile"));

                                        }

                                        if (!driverdata.getString("provider_picture").equals("null")) {

                                            requestDetail.setDriver_picture(driverdata.getString("provider_picture"));

                                        }

                                        if (!driverdata.optString("car_image").equals("null")) {

                                            requestDetail.setDriver_car_picture(driverdata.getString("car_image"));

                                        }
                                        if (!driverdata.optString("model").equals("null")) {
                                            requestDetail.setDriver_car_model(driverdata
                                                    .getString("model"));
                                        }
                                        if (!driverdata.getString("color").equals("null")) {
                                            requestDetail.setDriver_car_color(driverdata
                                                    .getString("color"));
                                        }
                                        if (!driverdata.getString("plate_no").equals("null")) {
                                            requestDetail.setDriver_car_number(driverdata
                                                    .getString("plate_no"));
                                        }
                                        requestDetail.setRequest_type(driverdata.optString("request_status_type"));
                                        requestDetail.setNo_tolls(jsonObject.optString("number_tolls"));
                                        requestDetail.setDriver_id(driverdata
                                                .getString("provider_id"));
                                        new PreferenceHelper(activity).putDriver_id(driverdata
                                                .getString("provider_id"));
                                        requestDetail.setDriver_rating(driverdata
                                                .getString("rating"));
                                        requestDetail.setS_address(driverdata
                                                .getString("s_address"));
                                        requestDetail.setD_address(driverdata
                                                .getString("d_address"));
                                        requestDetail.setS_lat(driverdata
                                                .getString("s_latitude"));
                                        requestDetail.setS_lng(driverdata
                                                .getString("s_longitude"));
                                        requestDetail.setD_lat(driverdata
                                                .getString("d_latitude"));
                                        requestDetail.setD_lng(driverdata
                                                .getString("d_longitude"));


                                        requestDetail.setAdStopLatitude(driverdata.getString("adstop_latitude"));
                                        requestDetail.setAdStopLongitude(driverdata.getString("adstop_longitude"));
                                        requestDetail.setAdStopAddress(driverdata.getString("adstop_address"));
                                        requestDetail.setIsAdStop(driverdata.getString("is_adstop"));
                                        requestDetail.setIsAddressChanged(driverdata.getString("is_address_changed"));


                                    JSONArray invoiceJSONArray = jsonObject.getJSONArray(Const.Params.INVOICE);

                                    if (invoiceJSONArray.length() > 0) {

                                        JSONObject invoiceObject = invoiceJSONArray.getJSONObject(0);
                                        requestDetail.setTrip_time(invoiceObject.getString("total_time"));
                                        requestDetail.setPayment_mode(invoiceObject.getString("payment_mode"));
                                        requestDetail.setTrip_base_price(invoiceObject.getString("base_price"));
                                        requestDetail.setTrip_total_price(invoiceObject.getString("total"));
                                        requestDetail.setDistance_unit(invoiceObject.optString("distance_unit"));

                                        if (invoiceObject.has(Const.Params.A_AND_E)){

                                            requestDetail.setA_and_e(invoiceObject.getString(Const.Params.A_AND_E));

                                        }

                                        if (invoiceObject.has(Const.Params.IMH)){

                                            requestDetail.setImh(invoiceObject.getString(Const.Params.IMH));
                                        }

                                        if (invoiceObject.has(Const.Params.FERRY_TERMINALS)){

                                            requestDetail.setFerry_terminals(invoiceObject.getString(Const.Params.FERRY_TERMINALS));
                                        }

                                        if (invoiceObject.has(Const.Params.STAIRCASE)){

                                            requestDetail.setStaircase(invoiceObject.getString(Const.Params.STAIRCASE));
                                        }

                                        if (invoiceObject.has(Const.Params.TARMAC)){

                                            requestDetail.setTarmac(invoiceObject.getString(Const.Params.TARMAC));
                                        }

                                        if (invoiceObject.has(Const.Params.WEIGHT)){

                                            requestDetail.setWeight(invoiceObject.getString(Const.Params.WEIGHT));
                                        }

                                        if (invoiceObject.has(Const.Params.OXYGEN)){

                                            requestDetail.setOxygen_tank(invoiceObject.getString(Const.Params.OXYGEN));
                                        }

                                        if (invoiceObject.has(Const.Params.CASE_TYPE)){

                                            requestDetail.setPickup_type(invoiceObject.getString(Const.Params.CASE_TYPE));
                                        }

                                        if(invoiceObject.has("distance_travel")) {
                                            requestDetail.setTrip_distance(invoiceObject.getString("distance_travel"));
                                        }

                                    }

                                        if (!driverdata.getString("driver_latitude").equals("null")) {
                                            requestDetail.setDriver_latitude(Double.valueOf(driverdata
                                                    .getString("driver_latitude")));
                                        }
                                        if (!driverdata.getString("driver_longitude").equals("null")) {
                                            requestDetail.setDriver_longitude(Double.valueOf(driverdata
                                                    .getString("driver_longitude")));
                                        }
                                        requestDetail.setVehical_img(driverdata
                                                .getString("type_picture"));

                                    EbizworldUtils.appLogDebug("HaoLS", "provider status = " + driverdata.getInt("provider_status"));
                                    EbizworldUtils.appLogDebug("HaoLS", "later = " + driverdata.getInt("later"));
                                    break;

                                }else if (driverdata.getInt("provider_status") >= 5 && driverdata.getInt("later") == 1) {

                                    if (driverdata.has("provider_status")) {

                                        requestDetail.setTripStatus(driverdata.getInt("provider_status"));

                                    }

                                    if (driverdata.has("later")){

                                        requestDetail.setLater(String.valueOf(driverdata.getInt("later")));
                                    }

                                    new PreferenceHelper(activity).putRequestId(Integer.valueOf(driverdata.getString("request_id")));

                                    requestDetail.setRequestId(Integer.valueOf(driverdata.getString("request_id")));

                                    if (!driverdata.getString("provider_name").equals("null")) {

                                        requestDetail.setDriver_name(driverdata.getString("provider_name"));

                                    }

                                    if (driverdata.has("status")) {

                                        requestDetail.setDriverStatus(driverdata.getInt("status"));


                                    }
                                    if (!driverdata.getString("provider_mobile").equals("null")) {

                                        requestDetail.setDriver_mobile(driverdata.getString("provider_mobile"));

                                    }

                                    if (!driverdata.getString("provider_picture").equals("null")) {

                                        requestDetail.setDriver_picture(driverdata.getString("provider_picture"));

                                    }

                                    if (!driverdata.optString("car_image").equals("null")) {

                                        requestDetail.setDriver_car_picture(driverdata.getString("car_image"));

                                    }
                                    if (!driverdata.optString("model").equals("null")) {
                                        requestDetail.setDriver_car_model(driverdata
                                                .getString("model"));
                                    }
                                    if (!driverdata.getString("color").equals("null")) {
                                        requestDetail.setDriver_car_color(driverdata
                                                .getString("color"));
                                    }
                                    if (!driverdata.getString("plate_no").equals("null")) {
                                        requestDetail.setDriver_car_number(driverdata
                                                .getString("plate_no"));
                                    }
                                    requestDetail.setRequest_type(driverdata.optString("request_status_type"));
                                    requestDetail.setNo_tolls(jsonObject.optString("number_tolls"));
                                    requestDetail.setDriver_id(driverdata
                                            .getString("provider_id"));
                                    new PreferenceHelper(activity).putDriver_id(driverdata
                                            .getString("provider_id"));
                                    requestDetail.setDriver_rating(driverdata
                                            .getString("rating"));
                                    requestDetail.setS_address(driverdata
                                            .getString("s_address"));
                                    requestDetail.setD_address(driverdata
                                            .getString("d_address"));
                                    requestDetail.setS_lat(driverdata
                                            .getString("s_latitude"));
                                    requestDetail.setS_lng(driverdata
                                            .getString("s_longitude"));
                                    requestDetail.setD_lat(driverdata
                                            .getString("d_latitude"));
                                    requestDetail.setD_lng(driverdata
                                            .getString("d_longitude"));


                                    requestDetail.setAdStopLatitude(driverdata.getString("adstop_latitude"));
                                    requestDetail.setAdStopLongitude(driverdata.getString("adstop_longitude"));
                                    requestDetail.setAdStopAddress(driverdata.getString("adstop_address"));
                                    requestDetail.setIsAdStop(driverdata.getString("is_adstop"));
                                    requestDetail.setIsAddressChanged(driverdata.getString("is_address_changed"));



                                    if (!driverdata.getString("driver_latitude").equals("null")) {
                                        requestDetail.setDriver_latitude(Double.valueOf(driverdata
                                                .getString("driver_latitude")));
                                    }
                                    if (!driverdata.getString("driver_longitude").equals("null")) {
                                        requestDetail.setDriver_longitude(Double.valueOf(driverdata
                                                .getString("driver_longitude")));
                                    }
                                    requestDetail.setVehical_img(driverdata
                                            .getString("type_picture"));

                                    EbizworldUtils.appLogDebug("HaoLS", "provider status = " + driverdata.getInt("provider_status"));
                                    EbizworldUtils.appLogDebug("HaoLS", "later = " + driverdata.getInt("later"));
                                    break;
                                }

                            }
                        }
                    }

                    JSONArray invoicejarray = jsonObject.getJSONArray("invoice");
                    if (invoicejarray.length() > 0) {
                        JSONObject invoiceobj = invoicejarray.getJSONObject(0);
                        requestDetail.setTrip_time(invoiceobj.getString("total_time"));
                        requestDetail.setPayment_mode(invoiceobj.getString("payment_mode"));
                        requestDetail.setTrip_base_price(invoiceobj.getString("base_price"));
                        requestDetail.setTrip_total_price(invoiceobj.getString("total"));
                        requestDetail.setDistance_unit(invoiceobj.optString("distance_unit"));


                        requestDetail.setTrip_distance(invoiceobj
                                .getString("distance_travel"));
                    }

                } else {


                    requestDetail.setTripStatus(Const.NO_REQUEST);

                    new PreferenceHelper(activity).putRequestId(Const.NO_REQUEST);

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return requestDetail;
    }

    public List<AdsList> parseAdsList(JSONArray jsonArray) {
        List<AdsList> adsLists = null;
        Log.e("asher", "region array " + jsonArray);

        adsLists = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            //Creating a json object of the current index

            try {
                //getting json object from current index
                JSONObject obj = jsonArray.getJSONObject(i);
                //getting subCategories from json object
                AdsList details = new AdsList();
                details.setAdDescription(obj.optString("description"));
                details.setAdId(obj.optString("id"));
                details.setAdImage(obj.optString("picture"));
                details.setAdUrl(obj.optString("url"));
                adsLists.add(details);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //Creating ListViewAdapter Object


        return adsLists;
    }

    public List<Schedule> parsePatientSchedule(String response){

        List<Schedule> scheduleList = new ArrayList<>();

        try {
            JSONObject scheduleObject = new JSONObject(response);

            if (scheduleObject.getString("success").equals("true")){

                JSONArray scheduleArray = scheduleObject.getJSONArray("data");

                if (scheduleArray.length() > 0) {

                    for (int i = 0; i < scheduleArray.length(); i++) {

                        JSONObject object = scheduleArray.getJSONObject(i);
                        Schedule schedule = new Schedule();
                        schedule.setRequest_id(object.getString("request_id"));
                        schedule.setRequest_date(object.getString("requested_time"));
                        schedule.setRequest_type(object.getString("service_type_name"));
                        schedule.setRequest_pic(object.getString("type_picture"));
                        schedule.setD_address(object.getString("d_address"));
                        schedule.setS_address(object.getString("s_address"));
                        schedule.setStatus_request(object.getString("status_of_schedule"));
                        schedule.setStatus(Integer.parseInt(object.getString("status")));
                        schedule.setA_and_e(object.getInt(Const.Params.A_AND_E));
                        schedule.setImh(object.getInt(Const.Params.IMH));
                        schedule.setFerry_terminals(object.getInt(Const.Params.FERRY_TERMINALS));
                        schedule.setStaircase(object.getInt(Const.Params.STAIRCASE));
                        schedule.setTarmac(object.getInt(Const.Params.TARMAC));
                        schedule.setHouseUnit(object.getString(Const.Params.HOUSE_UNIT));
                        schedule.setWeight(object.getInt(Const.Params.WEIGHT));
                        schedule.setOxygen_tank(object.getInt(Const.Params.SCHEDULE_OXYGEN_TANK));
                        schedule.setCase_type(object.getInt(Const.Params.CASE_TYPE));

                        scheduleList.add(schedule);
                    }

                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
            EbizworldUtils.appLogError("HaoLS", "Parse Schedule Failed: " + e.toString());
            EbizworldUtils.showShortToast(e.toString(), activity);
        }

        return scheduleList;
    }

    public List<Schedule> parseNursingHomeSchedule(String response){

        List<Schedule> scheduleList = new ArrayList<>();

        try {
            JSONObject scheduleObject = new JSONObject(response);

            if (scheduleObject.getString("success").equals("true")){

                JSONArray scheduleArray = scheduleObject.getJSONArray("data");

                if (scheduleArray.length() > 0) {

                    for (int i = 0; i < scheduleArray.length(); i++) {

                        JSONObject object = scheduleArray.getJSONObject(i);
                        Schedule schedule = new Schedule();
                        schedule.setRequest_id(object.getString("request_id"));
                        schedule.setRequest_date(object.getString("requested_time"));
                        schedule.setRequest_type(object.getString("service_type_name"));
                        schedule.setRequest_pic(object.getString("type_picture"));
                        schedule.setD_address(object.getString("d_address"));
                        schedule.setD_lat(object.getString("d_latitude"));
                        schedule.setD_lng(object.getString("d_longitude"));
                        schedule.setS_address(object.getString("s_address"));
                        schedule.setS_lat(object.getString("s_latitude"));
                        schedule.setS_lng(object.getString("s_longitude"));
                        schedule.setStatus_request(object.getString("status_of_schedule"));
                        schedule.setStatus(Integer.parseInt(object.getString("status")));
                        schedule.setPatient_name(object.getString("patient_name"));
                        schedule.setPurpose(object.getString("purpose"));
                        schedule.setSpecial_request(object.getString("special_request"));
                        schedule.setRoom_number(Integer.parseInt(object.getString("room_no")));
                        schedule.setFloor_number(Integer.parseInt(object.getString("floor_no")));
                        schedule.setBed_number(Integer.parseInt(object.getString("bed_no")));
                        schedule.setWard(object.getString("ward_no"));
                        schedule.setHospital(object.getString("hospital"));
                        schedule.setA_and_e(object.getInt(Const.Params.A_AND_E));
                        schedule.setImh(object.getInt(Const.Params.IMH));
                        schedule.setFerry_terminals(object.getInt(Const.Params.FERRY_TERMINALS));
                        schedule.setStaircase(object.getInt(Const.Params.STAIRCASE));
                        schedule.setTarmac(object.getInt(Const.Params.TARMAC));
                        schedule.setWeight(object.getInt(Const.Params.WEIGHT));
                        schedule.setOxygen_tank(object.getInt(Const.Params.SCHEDULE_OXYGEN_TANK));
                        schedule.setCase_type(object.getInt(Const.Params.CASE_TYPE));

                        scheduleList.add(schedule);
                    }

                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
            EbizworldUtils.appLogError("HaoLS", "Parse Schedule Failed: " + e.toString());
            EbizworldUtils.showShortToast(e.toString(), activity);
        }

        return scheduleList;
    }

    public List<Schedule> parseHospitalSchedule(String response){

        List<Schedule> scheduleList = new ArrayList<>();

        try {
            JSONObject scheduleObject = new JSONObject(response);

            if (scheduleObject.getString("success").equals("true")){

                JSONArray scheduleArray = scheduleObject.getJSONArray("data");

                if (scheduleArray.length() > 0) {

                    for (int i = 0; i < scheduleArray.length(); i++) {

                        JSONObject object = scheduleArray.getJSONObject(i);
                        Schedule schedule = new Schedule();
                        schedule.setRequest_id(object.getString("request_id"));
                        schedule.setRequest_date(object.getString("requested_time"));
                        schedule.setRequest_type(object.getString("service_type_name"));
                        schedule.setRequest_pic(object.getString("type_picture"));
                        schedule.setD_address(object.getString("d_address"));
                        schedule.setD_lat(object.getString("d_latitude"));
                        schedule.setD_lng(object.getString("d_longitude"));
                        schedule.setS_address(object.getString("s_address"));
                        schedule.setS_lat(object.getString("s_latitude"));
                        schedule.setS_lng(object.getString("s_longitude"));
                        schedule.setStatus_request(object.getString("status_of_schedule"));
                        schedule.setStatus(Integer.parseInt(object.getString("status")));
                        schedule.setPatient_name(object.getString("patient_name"));
                        schedule.setPurpose(object.getString("purpose"));
                        schedule.setSpecial_request(object.getString("special_request"));
                        schedule.setRoom_number(Integer.parseInt(object.getString("room_no")));
                        schedule.setFloor_number(Integer.parseInt(object.getString("floor_no")));
                        schedule.setBed_number(Integer.parseInt(object.getString("bed_no")));
                        schedule.setWard(object.getString("ward_no"));
                        schedule.setHospital(object.getString("hospital"));
                        schedule.setA_and_e(object.getInt(Const.Params.A_AND_E));
                        schedule.setImh(object.getInt(Const.Params.IMH));
                        schedule.setFerry_terminals(object.getInt(Const.Params.FERRY_TERMINALS));
                        schedule.setStaircase(object.getInt(Const.Params.STAIRCASE));
                        schedule.setTarmac(object.getInt(Const.Params.TARMAC));
                        schedule.setWeight(object.getInt(Const.Params.WEIGHT));
                        schedule.setOxygen_tank(object.getInt(Const.Params.SCHEDULE_OXYGEN_TANK));
                        schedule.setCase_type(object.getInt(Const.Params.CASE_TYPE));

                        scheduleList.add(schedule);
                    }

                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
            EbizworldUtils.appLogError("HaoLS", "Parse Schedule Failed: " + e.toString());
            EbizworldUtils.showShortToast(e.toString(), activity);
        }

        return scheduleList;
    }
}
