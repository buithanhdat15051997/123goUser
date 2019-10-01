package sg.go.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import de.hdodenhof.circleimageview.CircleImageView;
import sg.go.user.Adapter.AmbulanceOperatorSpinnerAdapter;
import sg.go.user.Adapter.SimpleSpinnerAdapter;
import sg.go.user.HttpRequester.MultiPartRequester;
import sg.go.user.HttpRequester.VolleyRequester;
import sg.go.user.Interface.AsyncTaskCompleteListener;
import sg.go.user.Models.AmbulanceOperator;
import sg.go.user.Models.Hospital;
import sg.go.user.Models.Nurse;
import sg.go.user.Models.Patient;

import sg.go.user.RealmController.RealmController;
import sg.go.user.Utils.Commonutils;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.EbizworldUtils;
import sg.go.user.Utils.ParseContent;
import sg.go.user.Utils.PreferenceHelper;
import com.soundcloud.android.crop.Crop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import io.realm.Realm;

/**
 * Created by user on 1/7/2017.
 */

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener, AsyncTaskCompleteListener {
    private Toolbar promainToolbar;
    private ImageButton img_profile_back;
    private TextView tv_edit_profile;
    private ImageView profile_image;
    private EditText et_profile_email, edt_profile_mobile, edt_weight, edt_floor_number, edt_ward;
    private EditText edt_fullname, edt_contact_name, edt_contact_number, edt_preferred_username, edt_address;
    private EditText edt_family_member_name, edt_home_number, edt_block_number, edt_unit_number, edt_postal, edt_street_name, edt_additional_pation_information;
    private CheckBox cb_lift_landing, cb_stairs, cb_no_stairs, cb_low_stairs, cb_stretcher, cb_wheel_chair, cb_oxygen, cb_escorts;
    private Spinner spn_ambulance_operator, spn_patient_condition, spn_payment_mode;
    private AmbulanceOperatorSpinnerAdapter mAmbulanceOperatorSpinnerAdapter;
    private RadioGroup profile_radioGroup;
    private RadioButton rd_btn, radio_btn_male, radio_btn_female;
    private Realm realm;
    private AQuery aQuery;
    private String filePath = "";
    private File cameraFile;
    private Uri uri = null;
    private ParseContent pcontent;

    private List<AmbulanceOperator> mAmbulanceOperators;
    private AmbulanceOperator mAmbulanceOperator;
    private Nurse mNurse;
    private Patient mPatient;
    private List<String> mPatientConditions;
    private String mPatientCondition;
    private List<String> mPaymentModes;
    private String mPaymentMode;
    private Hospital mHopital;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        aQuery = new AQuery(this);
        pcontent = new ParseContent(this);

        this.realm = RealmController.with(this).getRealm();
        RealmController.with(this).refresh();

        mAmbulanceOperators = new ArrayList<>();

        //Check login type of App's user
        if (new PreferenceHelper(this).getLoginType().equals(Const.PatientService.PATIENT)){

            setContentView(R.layout.activity_profile);

            promainToolbar = (Toolbar) findViewById(R.id.toolbar_profile);

            setSupportActionBar(promainToolbar);
            getSupportActionBar().setTitle(null);

            img_profile_back = (ImageButton) findViewById(R.id.profile_back);
            tv_edit_profile = (TextView) findViewById(R.id.tv_edit_profile);
            profile_image = (ImageView) findViewById(R.id.img_profile_image);
            edt_fullname = (EditText) findViewById(R.id.edt_fullname);
            edt_family_member_name = (EditText) findViewById(R.id.edt_family_member_name);
            edt_profile_mobile = (EditText) findViewById(R.id.edt_patient_mobile);
            et_profile_email = (EditText) findViewById(R.id.edt_email);
            edt_home_number = (EditText) findViewById(R.id.edt_home_number);
            edt_block_number = (EditText) findViewById(R.id.edt_block_number);
            edt_unit_number = (EditText) findViewById(R.id.edt_unit_number);
            edt_postal = (EditText) findViewById(R.id.edt_postal);
            edt_street_name = (EditText) findViewById(R.id.edt_street_name);
          //  edt_weight = (EditText) findViewById(R.id.edt_weight);
            cb_lift_landing = (CheckBox) findViewById(R.id.cb_lift_landing);
            cb_stairs = (CheckBox) findViewById(R.id.cb_stairs);
            cb_no_stairs = (CheckBox) findViewById(R.id.cb_no_stairs);
            cb_low_stairs = (CheckBox) findViewById(R.id.cb_low_stairs);
            spn_ambulance_operator = (Spinner) findViewById(R.id.spn_ambulance_operator);
            spn_patient_condition = (Spinner) findViewById(R.id.spn_patient_condition);
            cb_stretcher = (CheckBox) findViewById(R.id.cb_stretcher);
            cb_wheel_chair = (CheckBox) findViewById(R.id.cb_wheel_chair);
            cb_oxygen = (CheckBox) findViewById(R.id.cb_oxygen);
            cb_escorts = (CheckBox) findViewById(R.id.cb_escorts);
            edt_additional_pation_information = (EditText) findViewById(R.id.edt_additional_pation_information);
            spn_payment_mode = (Spinner) findViewById(R.id.spn_payment_mode);
            edt_preferred_username = (EditText) findViewById(R.id.edt_preferred_username);



            profile_image.setOnClickListener(this);
            img_profile_back.setOnClickListener(this);
            tv_edit_profile.setOnClickListener(this);

            spn_ambulance_operator.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mAmbulanceOperator = mAmbulanceOperators.get(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            disablePatientViews();

            setPatientValues();
//            setUserValues();

        }else if (new PreferenceHelper(this).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)){

            setContentView(R.layout.activity_nurse_profile);

            promainToolbar = (Toolbar) findViewById(R.id.toolbar_profile);

            setSupportActionBar(promainToolbar);
            getSupportActionBar().setTitle(null);

            img_profile_back = (ImageButton) findViewById(R.id.profile_back);
            tv_edit_profile = (TextView) findViewById(R.id.tv_edit_profile);
            profile_image = (ImageView) findViewById(R.id.img_profile_image);
            edt_fullname = (EditText) findViewById(R.id.et_fullname);
            et_profile_email = (EditText) findViewById(R.id.et_profile_email);
            edt_profile_mobile = (EditText) findViewById(R.id.et_profile_mobile);
            edt_contact_name = (EditText) findViewById(R.id.edt_contact_name);
            edt_contact_number = (EditText) findViewById(R.id.edt_contact_number);
            edt_preferred_username = (EditText) findViewById(R.id.edt_preferred_username);
            edt_address = (EditText) findViewById(R.id.edt_address);
            /*edt_operator_id = (EditText) findViewById(R.id.edt_operator_id);*/
            spn_ambulance_operator = (Spinner) findViewById(R.id.spn_ambulance_operator);

            spn_ambulance_operator.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    mAmbulanceOperator = mAmbulanceOperators.get(position);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            profile_image.setOnClickListener(this);
            img_profile_back.setOnClickListener(this);
            tv_edit_profile.setOnClickListener(this);

            disableNursingHomeViews();

            setNurseValues();

        }else if (new PreferenceHelper(this).getLoginType().equals(Const.HospitalService.HOSPITAL)){

            setContentView(R.layout.activity_hospital_profile);

            promainToolbar = (Toolbar) findViewById(R.id.toolbar_profile);

            setSupportActionBar(promainToolbar);
            getSupportActionBar().setTitle(null);

            img_profile_back = (ImageButton) findViewById(R.id.profile_back);
            tv_edit_profile = (TextView) findViewById(R.id.tv_edit_profile);
            profile_image = (ImageView) findViewById(R.id.img_profile_image);
            edt_fullname = (EditText) findViewById(R.id.et_fullname);
            et_profile_email = (EditText) findViewById(R.id.et_profile_email);
            edt_profile_mobile = (EditText) findViewById(R.id.et_profile_mobile);
            edt_contact_name = (EditText) findViewById(R.id.edt_contact_name);
            edt_contact_number = (EditText) findViewById(R.id.edt_contact_number);
            edt_preferred_username = (EditText) findViewById(R.id.edt_preferred_username);
            edt_postal = (EditText) findViewById(R.id.edt_postal);
            edt_floor_number = (EditText) findViewById(R.id.tv_floor_number_value);
            edt_ward = (EditText) findViewById(R.id.tv_ward_value);
            edt_address = (EditText) findViewById(R.id.edt_address);
            /*edt_operator_id = (EditText) findViewById(R.id.edt_operator_id);*/
            spn_ambulance_operator = (Spinner) findViewById(R.id.spn_ambulance_operator);

            spn_ambulance_operator.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    mAmbulanceOperator = mAmbulanceOperators.get(position);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            profile_image.setOnClickListener(this);
            img_profile_back.setOnClickListener(this);
            tv_edit_profile.setOnClickListener(this);

            disableHospitalViews();

            setHosptialValues();
        }

    }

    /*private void setUserValues() {
        User userprofile = RealmController.with(this).getUser(Integer.valueOf(new PreferenceHelper(this).getUserId()));
        if (userprofile != null) {
            *//*et_firstname.setText(userprofile.getFname());
            et_lastname.setText(userprofile.getLname());*//*
            et_profile_email.setText(userprofile.getEmail());
            edt_profile_mobile.setText(userprofile.getPhone());

            Glide.with(this).load(userprofile.getProfileurl())
                    .transition(new DrawableTransitionOptions().crossFade())
                    .apply(new RequestOptions().placeholder(R.drawable.defult_user).error(R.drawable.defult_user).centerCrop())
                    .into(profile_image);

            if (userprofile.getGender().equals(getString(R.string.txt_male))) {
                radio_btn_male.setChecked(true);

            } else {
                radio_btn_female.setChecked(true);

            }

            new AQuery(this).id(R.id.img_profile_image).image(userprofile.getProfileurl(), true, true,
                    200, 0, new BitmapAjaxCallback() {

                        @Override
                        public void callback(String url, ImageView iv, Bitmap bm,
                                             AjaxStatus status) {

                            if (url != null && !url.equals("")) {
                                filePath = aQuery.getCachedFile(url).getPath();


                            }

                        }

                    });

        }
    }*/

    private void setPatientValues() {

        mPatient = RealmController.with(this).getPatient(Integer.valueOf(new PreferenceHelper(this).getUserId()));

        if (mPatient != null) {

            //Getting Ambulance Operator list for set up to Ambulance Operator Spinner
            getAmbulanceOperator();

            edt_fullname.setText(mPatient.getmFullname());
            Glide.with(this).load(mPatient.getmPicture())
                    .transition(new DrawableTransitionOptions().crossFade())
                    .apply(new RequestOptions().placeholder(R.drawable.defult_user).error(R.drawable.defult_user).centerCrop())
                    .into(profile_image);

            new AQuery(this).id(R.id.img_profile_image).image(mPatient.getmPicture(), true, true,
                    200, 0, new BitmapAjaxCallback() {

                        @Override
                        public void callback(String url, ImageView iv, Bitmap bm,
                                             AjaxStatus status) {

                            if (url != null && !url.equals("")) {
                                filePath = aQuery.getCachedFile(url).getPath();


                            }

                        }

                    });

            edt_family_member_name.setText(mPatient.getmFamilyMemberName());
            edt_profile_mobile.setText(mPatient.getmMobile());
            et_profile_email.setText(mPatient.getmEmail());
            edt_home_number.setText(mPatient.getmHomeNumber());
            edt_block_number.setText(mPatient.getmBlockNumber());
            edt_unit_number.setText(mPatient.getmUnitNumber());
            edt_postal.setText(mPatient.getmPostal());
            edt_street_name.setText(mPatient.getmStreetName());

           // edt_weight.setText(String.valueOf(mPatient.getmWeight()));

            if (mPatient.getmLiftLanding() == 1){

                cb_lift_landing.setChecked(true);

            }

            if (mPatient.getmStairs() == 1){

                cb_stairs.setChecked(true);

            }

            if (mPatient.getmNoStairs() == 1){

                cb_no_stairs.setChecked(true);

            }

            if (mPatient.getmLowStairs() == 1){

                cb_low_stairs.setChecked(true);

            }


//            Set up Patient spinner condition
            EbizworldUtils.appLogDebug("HaoLS", "Setting up Patient spinner condition");
            mPatientConditions = new ArrayList<>();
            mPatientConditions.add("Good");
            mPatientConditions.add("Critical");
            SimpleSpinnerAdapter patientConditionsAdapter = new SimpleSpinnerAdapter(this, mPatientConditions);
            spn_patient_condition.setAdapter(patientConditionsAdapter);
            if (mPatient.getmPatientCondition().equals("Critical")){
                spn_patient_condition.setSelection(1);
            }else {
                spn_patient_condition.setSelection(0);
            }
            spn_patient_condition.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mPatientCondition = mPatientConditions.get(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

//            Set up Payment mode
            EbizworldUtils.appLogDebug("HaoLS", "Setting up Payment mode");
            mPaymentModes = new ArrayList<>();
            mPaymentModes.add("Cash");
            mPaymentModes.add("Monthly Bill");
            mPaymentModes.add("Digital Wallet");
            mPaymentModes.add("Credit Cards");
            SimpleSpinnerAdapter paymentModesAdapter = new SimpleSpinnerAdapter(this, mPaymentModes);
            spn_payment_mode.setAdapter(paymentModesAdapter);
            spn_payment_mode.setSelection(0);
            for (int i = 0; i < mPaymentModes.size(); i++){

                if(mPatient.getmPaymentMode().equals(mPaymentModes.get(i))){

                    spn_payment_mode.setSelection(i);
                    break;
                }
            }
            spn_payment_mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    mPaymentMode = mPaymentModes.get(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            if (mPatient.getmStretcher() == 1){

                cb_stretcher.setChecked(true);

            }

            if (mPatient.getmWheelChair() == 1){

                cb_wheel_chair.setChecked(true);

            }

            if (mPatient.getmOxygen() == 1){

                cb_oxygen.setChecked(true);

            }

            if (mPatient.getmEscorts() == 1){

                cb_escorts.setChecked(true);

            }

            edt_additional_pation_information.setText(mPatient.getmAddInformation());
            /*spn_payment_mode.setText(patientprofile.getmPaymentMode());*/
            edt_preferred_username.setText(mPatient.getmPreferredUsername());

        }
    }

    private void setNurseValues(){

        mNurse = RealmController.with(this).getNurse(Integer.valueOf(new PreferenceHelper(this).getUserId()));

        if (mNurse != null) {

            //Getting Ambulance Operator list for set up to Ambulance Operator Spinner
            getAmbulanceOperator();

            Glide.with(this).load(mNurse.getmPictureUrl())
                    .transition(new DrawableTransitionOptions().crossFade())
                    .apply(new RequestOptions().placeholder(R.drawable.defult_user).error(R.drawable.defult_user).centerCrop())
                    .into(profile_image);

            new AQuery(this).id(R.id.img_profile_image).image(mNurse.getmPictureUrl(), true, true,
                    200, 0, new BitmapAjaxCallback() {

                        @Override
                        public void callback(String url, ImageView iv, Bitmap bm,
                                             AjaxStatus status) {

                            EbizworldUtils.appLogDebug("HaoLS", url);
                            if (!url.isEmpty()) {

                                EbizworldUtils.appLogDebug("HaoLS", url);

                            //    filePath = aQuery.getCachedFile(url).getPath();
                            }

                        }

                    });

            edt_fullname.setText(mNurse.getmFullName());
            et_profile_email.setText(mNurse.getmEmail());
            edt_profile_mobile.setText(mNurse.getmMobile());
            edt_contact_name.setText(mNurse.getmContact_Name());
            edt_contact_number.setText(mNurse.getmContact_Number());
            edt_preferred_username.setText(mNurse.getmPreferred_Username());
            edt_address.setText(mNurse.getmAddress());

            if (mNurse.getmOperatorID() >= 0){

                /*edt_operator_id.setText(String.valueOf(nurseProfile.getmOperatorID()));*/
            }


        }
    }

    private void setHosptialValues(){

        mHopital = RealmController.with(this).getHospital(Integer.valueOf(new PreferenceHelper(this).getUserId()));

        if (mHopital != null) {

            //Getting Ambulance Operator list for set up to Ambulance Operator Spinner
            getAmbulanceOperator();

            Glide.with(this).load(mHopital.getmCompanyPicture())
                    .transition(new DrawableTransitionOptions().crossFade())
                    .apply(new RequestOptions().placeholder(R.drawable.defult_user).error(R.drawable.defult_user).centerCrop())
                    .into(profile_image);

            new AQuery(this).id(R.id.img_profile_image).image(mHopital.getmCompanyPicture(), true, true,
                    200, 0, new BitmapAjaxCallback() {

                        @Override
                        public void callback(String url, ImageView iv, Bitmap bm,
                                             AjaxStatus status) {

                            EbizworldUtils.appLogDebug("HaoLS", url);
                            if (!url.isEmpty()) {

                                EbizworldUtils.appLogDebug("HaoLS", url);

                                //    filePath = aQuery.getCachedFile(url).getPath();
                            }

                        }

                    });

            edt_fullname.setText(mHopital.getmHospitalName());
            et_profile_email.setText(mHopital.getmEmailAddress());
            edt_profile_mobile.setText(mHopital.getmMobileNumber());
            edt_contact_name.setText(mHopital.getmContactName());
            edt_contact_number.setText(mHopital.getmContactNumber());
            edt_preferred_username.setText(mHopital.getmPreferredUsername());
            edt_postal.setText(mHopital.getmPostal());
            edt_floor_number.setText(String.valueOf(mHopital.getmFloorNumber()));
            edt_ward.setText(mHopital.getmWard());
            edt_address.setText(mHopital.getmMainAddress());

        }
    }

    private void disablePatientViews() {
        profile_image.setEnabled(false);
        edt_fullname.setEnabled(false);
        edt_family_member_name.setEnabled(false);
        edt_profile_mobile.setEnabled(false);
        et_profile_email.setEnabled(false);
        edt_home_number.setEnabled(false);
        edt_block_number.setEnabled(false);
        edt_unit_number.setEnabled(false);
        edt_postal.setEnabled(false);
        edt_street_name.setEnabled(false);
      //  edt_weight.setEnabled(false);
        cb_lift_landing.setEnabled(false);
        cb_stairs.setEnabled(false);
        cb_no_stairs.setEnabled(false);
        cb_low_stairs.setEnabled(false);
        spn_ambulance_operator.setEnabled(false);
        spn_patient_condition.setEnabled(false);
        cb_stretcher.setEnabled(false);
        cb_wheel_chair.setEnabled(false);
        cb_oxygen.setEnabled(false);
        cb_escorts.setEnabled(false);
        edt_additional_pation_information.setEnabled(false);
        spn_payment_mode.setEnabled(false);
        edt_preferred_username.setEnabled(false);

    }

    private void disableNursingHomeViews() {
        profile_image.setEnabled(false);
        edt_fullname.setEnabled(false);
        et_profile_email.setEnabled(false);
        edt_profile_mobile.setEnabled(false);
        edt_contact_name.setEnabled(false);
        edt_contact_number.setEnabled(false);
        edt_preferred_username.setEnabled(false);
        edt_address.setEnabled(false);
        /*edt_operator_id.setEnabled(false);*/
        spn_ambulance_operator.setEnabled(false);
    }

    private void disableHospitalViews() {
        profile_image.setEnabled(false);
        edt_fullname.setEnabled(false);
        et_profile_email.setEnabled(false);
        edt_profile_mobile.setEnabled(false);
        edt_contact_name.setEnabled(false);
        edt_contact_number.setEnabled(false);
        edt_preferred_username.setEnabled(false);
        edt_postal.setEnabled(false);
        edt_floor_number.setEnabled(false);
        edt_ward.setEnabled(false);
        edt_address.setEnabled(false);
        /*edt_operator_id.setEnabled(false);*/
        spn_ambulance_operator.setEnabled(false);
    }


    private void enablePatientViews() {

        profile_image.setEnabled(true);
        edt_fullname.setEnabled(true);
        edt_family_member_name.setEnabled(true);
        edt_profile_mobile.setEnabled(true);
        et_profile_email.setEnabled(false);
        edt_home_number.setEnabled(true);
        edt_block_number.setEnabled(true);
        edt_unit_number.setEnabled(true);
        edt_postal.setEnabled(true);
        edt_street_name.setEnabled(true);
       // edt_weight.setEnabled(true);
        cb_lift_landing.setEnabled(true);
        cb_stairs.setEnabled(true);
        cb_no_stairs.setEnabled(true);
        cb_low_stairs.setEnabled(true);
        spn_ambulance_operator.setEnabled(true);
        spn_patient_condition.setEnabled(true);
        cb_stretcher.setEnabled(true);
        cb_wheel_chair.setEnabled(true);
        cb_oxygen.setEnabled(true);
        cb_escorts.setEnabled(true);
        edt_additional_pation_information.setEnabled(true);
        spn_payment_mode.setEnabled(true);
        edt_preferred_username.setEnabled(true);

    }

    private void enableNursingHomeViews() {

        profile_image.setEnabled(true);
        edt_fullname.setEnabled(true);
        et_profile_email.setEnabled(false);
        edt_profile_mobile.setEnabled(true);
        edt_contact_name.setEnabled(true);
        edt_contact_number.setEnabled(true);
        edt_preferred_username.setEnabled(true);
        edt_address.setEnabled(true);
        /*edt_operator_id.setEnabled(true);*/
        spn_ambulance_operator.setEnabled(true);

    }

    private void enableHospitalViews() {

        profile_image.setEnabled(true);
        edt_fullname.setEnabled(true);
        et_profile_email.setEnabled(false);
        edt_profile_mobile.setEnabled(true);
        edt_contact_name.setEnabled(true);
        edt_contact_number.setEnabled(true);
        edt_preferred_username.setEnabled(true);
        edt_postal.setEnabled(true);
        edt_floor_number.setEnabled(true);
        edt_ward.setEnabled(true);
        edt_address.setEnabled(true);
        spn_ambulance_operator.setEnabled(true);

    }

    private void getAmbulanceOperator(){

        if (!EbizworldUtils.isNetworkAvailable(this)){

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), ProfileActivity.this);
            return;

        }

        HashMap<String, String> hashMap = new HashMap<>();

        if (new PreferenceHelper(this).getLoginType().equals(Const.PatientService.PATIENT)){

            hashMap.put(Const.Params.URL, Const.ServiceType.OPERATORS_URL);
            hashMap.put(Const.Params.ID, new PreferenceHelper(this).getUserId());
            hashMap.put(Const.Params.TOKEN, new PreferenceHelper(this).getSessionToken());
            /*hashMap.put(Const.Params.DISTANCE, dis);
            hashMap.put(Const.Params.TIME, dur);*/

        }else if (new PreferenceHelper(this).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)){

            hashMap.put(Const.Params.URL, Const.NursingHomeService.OPERATORS_URL);
            hashMap.put(Const.Params.ID, new PreferenceHelper(this).getUserId());
            hashMap.put(Const.Params.TOKEN, new PreferenceHelper(this).getSessionToken());

        }else if (new PreferenceHelper(this).getLoginType().equals(Const.HospitalService.HOSPITAL)){

            hashMap.put(Const.Params.URL, Const.HospitalService.OPERATORS_URL);
            hashMap.put(Const.Params.ID, new PreferenceHelper(this).getUserId());
            hashMap.put(Const.Params.TOKEN, new PreferenceHelper(this).getSessionToken());

        }

        EbizworldUtils.appLogDebug("HaoLS", "ProfileActivity getting Ambulance Operator " + hashMap.toString());

        new VolleyRequester(this, Const.POST, hashMap, Const.ServiceCode.AMBULANCE_OPERATOR, this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.profile_back:
                onBackPressed();
                break;

            case R.id.tv_edit_profile:

                    if (new PreferenceHelper(this).getLoginType().equals(Const.PatientService.PATIENT)){

                        if (tv_edit_profile.getText().toString().equals(getString(R.string.btn_edit))) {

                            enablePatientViews();

                            tv_edit_profile.setText(getString(R.string.btn_save));

                        } else {

                            if (edt_fullname.getText().length() == 0 || edt_fullname.getText().length() > 255){

                                edt_fullname.setError(getResources().getString(R.string.edittext_inapproriate_notice));
                            }else if (edt_family_member_name.getText().length() == 0 || edt_family_member_name.getText().length() > 255){

                                edt_family_member_name.setError(getResources().getString(R.string.edittext_inapproriate_notice));

                            }else if (edt_profile_mobile.getText().length() < 6 || edt_profile_mobile.getText().length() > 13){

                                edt_profile_mobile.setError(getResources().getString(R.string.edittext_inapproriate_notice));
                            }else if (edt_block_number.getText().toString().equals("")){

                                edt_block_number.setError(getResources().getString(R.string.edittext_inapproriate_notice));

                            }else if (edt_preferred_username.getText().length() == 0 || edt_preferred_username.getText().length() > 255){

                                edt_preferred_username.setError(getResources().getString(R.string.edittext_inapproriate_notice));

                            }else if (edt_additional_pation_information.getText().length() == 0 || edt_additional_pation_information.getText().length() > 255){

                                edt_additional_pation_information.setError(getResources().getString(R.string.edittext_inapproriate_notice));
                            }else {

                                updateUserProfile();
//                                disablePatientViews();

                            }
                        }

                    }else if (new PreferenceHelper(this).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)){

                        if (tv_edit_profile.getText().toString().equals(getString(R.string.btn_edit))) {

                            enableNursingHomeViews();

                            tv_edit_profile.setText(getString(R.string.btn_save));

                        } else {

                            if (TextUtils.isEmpty(edt_fullname.getText().toString())){

                                edt_fullname.setError(getResources().getString(R.string.edittext_empty_notice));

                            }else if(TextUtils.isEmpty(edt_profile_mobile.getText().toString())){

                                edt_profile_mobile.setError(getResources().getString(R.string.edittext_empty_notice));

                            }else if (TextUtils.isEmpty(edt_contact_name.getText().toString())){

                                edt_contact_name.setError(getResources().getString(R.string.edittext_empty_notice));

                            }else if (TextUtils.isEmpty(edt_contact_number.getText().toString())){

                                edt_contact_number.setError(getResources().getString(R.string.edittext_empty_notice));

                            }else if(TextUtils.isEmpty(edt_preferred_username.getText().toString())){

                                edt_preferred_username.setError(getResources().getString(R.string.edittext_empty_notice));

                            }else if (TextUtils.isEmpty(edt_address.getText().toString())){

                                edt_address.setError(getResources().getString(R.string.edittext_empty_notice));

                            }/*else if (TextUtils.isEmpty(edt_operator_id.getText().toString())){

                                edt_operator_id.setError(getResources().getString(R.string.edittext_empty_notice));

                            }*/else {

                                updateNurseProfile();
//                                disableNursingHomeViews();

                            }

                        }

                    }else if (new PreferenceHelper(this).getLoginType().equals(Const.HospitalService.HOSPITAL)){

                        if (tv_edit_profile.getText().toString().equals(getString(R.string.btn_edit))) {

                            enableHospitalViews();

                            tv_edit_profile.setText(getString(R.string.btn_save));

                        } else {

                            if (TextUtils.isEmpty(edt_fullname.getText().toString())){

                                edt_fullname.setError(getResources().getString(R.string.edittext_empty_notice));

                            }else if(TextUtils.isEmpty(edt_profile_mobile.getText().toString())){

                                edt_profile_mobile.setError(getResources().getString(R.string.edittext_empty_notice));

                            }else if (TextUtils.isEmpty(edt_contact_name.getText().toString())){

                                edt_contact_name.setError(getResources().getString(R.string.edittext_empty_notice));

                            }else if (TextUtils.isEmpty(edt_contact_number.getText().toString())){

                                edt_contact_number.setError(getResources().getString(R.string.edittext_empty_notice));

                            }else if(TextUtils.isEmpty(edt_preferred_username.getText().toString())){

                                edt_preferred_username.setError(getResources().getString(R.string.edittext_empty_notice));

                            }else if (TextUtils.isEmpty(edt_postal.getText().toString())){

                                edt_address.setError(getResources().getString(R.string.edittext_empty_notice));

                            }else if (TextUtils.isEmpty(edt_floor_number.getText().toString())){

                                edt_address.setError(getResources().getString(R.string.edittext_empty_notice));

                            }else if (TextUtils.isEmpty(edt_ward.getText().toString())){

                                edt_address.setError(getResources().getString(R.string.edittext_empty_notice));

                            }else if (TextUtils.isEmpty(edt_address.getText().toString())){

                                edt_address.setError(getResources().getString(R.string.edittext_empty_notice));

                            }else {

                                updateHospitalProfile();
//                                disableHospitalViews();

                            }

                        }

                    }

                break;
            case R.id.img_profile_image:

                showPictureDialog();

                break;

        }
    }

    private void updateUserProfile() {

        if(!EbizworldUtils.isNetworkAvailable(ProfileActivity.this)){

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), ProfileActivity.this);
            return;

        }

        Commonutils.progressdialog_show(this, getResources().getString(R.string.updating_pro_load));

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.UPDATE_PROFILE);
        map.put(Const.Params.ID, new PreferenceHelper(this).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(this).getSessionToken());
        map.put(Const.Params.PICTURE, filePath);
        map.put(Const.Params.FULLNAME, edt_fullname.getText().toString());
        map.put(Const.Params.FAMILY_MEMBER_NAME, edt_family_member_name.getText().toString());
        map.put(Const.Params.MOBILE, edt_profile_mobile.getText().toString());
        map.put(Const.Params.EMAIL, et_profile_email.getText().toString());
        map.put(Const.Params.HOME_NUMBER, edt_home_number.getText().toString());
        map.put(Const.Params.BLOCK_NUMBER, edt_block_number.getText().toString());
        map.put(Const.Params.UNIT_NUMBER, edt_unit_number.getText().toString());
        map.put(Const.Params.POSTAL, edt_postal.getText().toString());
        map.put(Const.Params.STREET_NAME, edt_street_name.getText().toString());
        map.put(Const.Params.PREFERRED_USERNAME, edt_preferred_username.getText().toString());
        map.put(Const.Params.WEIGHT, "");

        if (cb_lift_landing.isChecked()){

            map.put(Const.Params.LIFT_LANDING, "1");

        }else {

            map.put(Const.Params.LIFT_LANDING, "0");
        }

        if (cb_stairs.isChecked()){

            map.put(Const.Params.STAIRS, "1");

        }else {

            map.put(Const.Params.STAIRS, "0");
        }

        if (cb_no_stairs.isChecked()){

            map.put(Const.Params.NO_STAIRS, "1");

        }else {

            map.put(Const.Params.NO_STAIRS, "0");
        }

        if (cb_low_stairs.isChecked()){

            map.put(Const.Params.LOW_STAIRS, "1");

        }else {

            map.put(Const.Params.LOW_STAIRS, "0");
        }

        if (mAmbulanceOperator != null){

            map.put(Const.Params.OPERATOR_ID, mAmbulanceOperator.getId());
        }else {

            map.put(Const.Params.OPERATOR_ID, "3"); //Need change

        }

        if(mPatientCondition != null){

            map.put(Const.Params.PATIENT_CONDITION, mPatientCondition.toLowerCase());

        }else {

            map.put(Const.Params.PATIENT_CONDITION, spn_patient_condition.getItemAtPosition(0).toString()); //Need change
        }

        if (cb_stretcher.isChecked()){

            map.put(Const.Params.STRETCHER, "1");

        }else {

            map.put(Const.Params.STRETCHER, "0");
        }

        if (cb_wheel_chair.isChecked()){

            map.put(Const.Params.WHEEL_CHAIR, "1");

        }else {

            map.put(Const.Params.WHEEL_CHAIR, "0");
        }

        if (cb_oxygen.isChecked()){

            map.put(Const.Params.OXYGEN, "1");

        }else {

            map.put(Const.Params.OXYGEN, "0");
        }

        if (cb_escorts.isChecked()){

            map.put(Const.Params.ESCORTS, "1");

        }else {

            map.put(Const.Params.ESCORTS, "0");
        }

        map.put(Const.Params.ADD_INFORMATION, edt_additional_pation_information.getText().toString());

        if(mPaymentMode != null){

            map.put(Const.Params.PAYMENT_MODE, mPaymentMode/*.toLowerCase()*/);

        }else {

            map.put(Const.Params.PAYMENT_MODE, spn_payment_mode.getItemAtPosition(0).toString());
        }

        // map.put(Const.Params.PASSWORD, et_register_your_password.getText().toString());
        /*map.put(Const.Params.SPECIALITY, String.valueOf(speclty.getId()));
        map.put(Const.Params.DEVICE_TOKEN, new PreferenceHelper(this).getDeviceToken());
        map.put(Const.Params.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID);

        map.put(Const.Params.LOGIN_BY, Const.MANUAL);



         map.put(Const.Params.CURRENCY, sp_curency_reg.getSelectedItem().toString());
        map.put(Const.Params.TIMEZONE, TimeZone.getDefault().getID());
         map.put(Const.Params.COUNTRY, sp_country_reg.getSelectedItem().toString());

        int selectedId = profile_radioGroup.getCheckedRadioButtonId();
        rd_btn = (RadioButton) findViewById(selectedId);
        map.put(Const.Params.GENDER, rd_btn.getText().toString());*/

        EbizworldUtils.appLogDebug("HaoLS", map.toString());

        if (filePath.equals("") || null == filePath) {
            new VolleyRequester(this, Const.POST, map, Const.ServiceCode.UPDATE_PROFILE,
                    this);
        } else {
            new MultiPartRequester(this, map, Const.ServiceCode.UPDATE_PROFILE,
                    this);
        }

    }

    private void updateNurseProfile(){

        if(!EbizworldUtils.isNetworkAvailable(ProfileActivity.this)){

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), ProfileActivity.this);
            return;

        }

        Commonutils.progressdialog_show(this, getResources().getString(R.string.updating_pro_load));

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.NursingHomeService.UPDATE_PROFILE_URL);
        map.put(Const.Params.ID, new PreferenceHelper(this).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(this).getSessionToken());
        map.put(Const.Params.FULLNAME, edt_fullname.getText().toString());
        map.put(Const.Params.CONTACT_NAME, edt_contact_name.getText().toString());
        map.put(Const.Params.CONTACT_NO, edt_contact_number.getText().toString());
        map.put(Const.Params.PREFERRED_USERNAME, edt_preferred_username.getText().toString());
        map.put(Const.Params.PICTURE, filePath);
        map.put(Const.Params.MOBILE, edt_profile_mobile.getText().toString());
        map.put(Const.Params.ADDRESS, edt_address.getText().toString());
        /*map.put(Const.Params.OPERATOR_ID, edt_operator_id.getText().toString());*/

        if (mAmbulanceOperator != null){

            map.put(Const.Params.OPERATOR_ID, mAmbulanceOperator.getId());

            EbizworldUtils.appLogDebug("HaoLS", Const.Params.OPERATOR_ID + " " +mAmbulanceOperator.getId());

        }else {

            map.put(Const.Params.OPERATOR_ID, "1");

        }

        /*map.put(Const.Params.GENDER, rd_btn.getText().toString());*/

        EbizworldUtils.appLogDebug("HaoLS", map.toString());

        if (filePath.equals("") || null == filePath) {
            new VolleyRequester(this, Const.POST, map, Const.ServiceCode.UPDATE_PROFILE,
                    this);
        } else {
            new MultiPartRequester(this, map, Const.ServiceCode.UPDATE_PROFILE,
                    this);
        }

    }

    private void updateHospitalProfile(){

        if(!EbizworldUtils.isNetworkAvailable(ProfileActivity.this)){

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), ProfileActivity.this);
            return;

        }

        Commonutils.progressdialog_show(this, getResources().getString(R.string.updating_pro_load));

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.HospitalService.UPDATE_PROFILE_URL);
        map.put(Const.Params.ID, new PreferenceHelper(this).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(this).getSessionToken());
        map.put(Const.Params.FULLNAME, edt_fullname.getText().toString());
        map.put(Const.Params.CONTACT_NAME, edt_contact_name.getText().toString());
        map.put(Const.Params.CONTACT_NO, edt_contact_number.getText().toString());
        map.put(Const.Params.PREFERRED_USERNAME, edt_preferred_username.getText().toString());
        map.put(Const.Params.POSTAL, edt_postal.getText().toString());
        map.put(Const.Params.FLOOR_NUMBER, edt_floor_number.getText().toString());
        map.put(Const.Params.WARD, edt_ward.getText().toString());
        map.put(Const.Params.PICTURE, filePath);
        map.put(Const.Params.MOBILE, edt_profile_mobile.getText().toString());
        map.put(Const.Params.ADDRESS, edt_address.getText().toString());
        /*map.put(Const.Params.OPERATOR_ID, edt_operator_id.getText().toString());*/

        if (mAmbulanceOperator != null){

            map.put(Const.Params.OPERATOR_ID, mAmbulanceOperator.getId());

            EbizworldUtils.appLogDebug("HaoLS", Const.Params.OPERATOR_ID + " " +mAmbulanceOperator.getId());

        }else {

            map.put(Const.Params.OPERATOR_ID, "1");

        }

        /*map.put(Const.Params.GENDER, rd_btn.getText().toString());*/

        EbizworldUtils.appLogDebug("HaoLS", map.toString());

        if (filePath.equals("") || null == filePath) {
            new VolleyRequester(this, Const.POST, map, Const.ServiceCode.UPDATE_PROFILE,
                    this);
        } else {
            new MultiPartRequester(this, map, Const.ServiceCode.UPDATE_PROFILE,
                    this);
        }

    }

    private void getPatientInformation(){

        if(!EbizworldUtils.isNetworkAvailable(ProfileActivity.this)){

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), ProfileActivity.this);
            return;

        }

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Const.Params.URL, Const.ServiceType.GET_USER_PROFILE + Const.Params.ID + "=" + new PreferenceHelper(this).getUserId() +
                "&" + Const.Params.TOKEN + "=" + new PreferenceHelper(this).getSessionToken());

        EbizworldUtils.appLogDebug("HaoLS", "getPatientInformation " + hashMap.toString());

        new VolleyRequester(this, Const.GET, hashMap, Const.ServiceCode.GET_ACCOUNT_INFORMATION, this);
    }

    private void getNurseInformation(){

        if(!EbizworldUtils.isNetworkAvailable(ProfileActivity.this)){

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), ProfileActivity.this);
            return;

        }

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Const.Params.URL, Const.NursingHomeService.GET_ACCOUNT_INFORMATION_URL + Const.Params.ID + "=" + new PreferenceHelper(this).getUserId() +
                "&" + Const.Params.TOKEN + "=" + new PreferenceHelper(this).getSessionToken());

        EbizworldUtils.appLogDebug("HaoLS", "getNurseInformation " + hashMap.toString());

        new VolleyRequester(this, Const.GET, hashMap, Const.ServiceCode.GET_ACCOUNT_INFORMATION, this);
    }

    private void getHospitalInformation(){

        if(!EbizworldUtils.isNetworkAvailable(ProfileActivity.this)){

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), ProfileActivity.this);
            return;

        }

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Const.Params.URL, Const.HospitalService.GET_ACCOUNT_INFORMATION_URL + Const.Params.ID + "=" + new PreferenceHelper(this).getUserId() +
                "&" + Const.Params.TOKEN + "=" + new PreferenceHelper(this).getSessionToken());

        EbizworldUtils.appLogDebug("HaoLS", "getHospitalInformation " + hashMap.toString());

        new VolleyRequester(this, Const.GET, hashMap, Const.ServiceCode.GET_ACCOUNT_INFORMATION, this);
    }

    private void showPictureDialog() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(getResources().getString(R.string.txt_slct_option));
        String[] items = {getResources().getString(R.string.txt_gellery), getResources().getString(R.string.txt_cameray)};
        // dialog.setMessage("*for your security reason we blocked!");
        dialog.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                switch (which) {

                    case 0:

                        choosePhotoFromGallary();
                        break;

                    case 1:

                        takePhotoFromCamera();
                        break;

                }
            }
        });
        dialog.show();

     /*   final Dialog dialog = new Dialog(this, R.style.DialogThemeforview);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.fade_drawable));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.choose_picture_dialog);
        TextView btn_ok = (TextView) dialog.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();*/
    }

    private void choosePhotoFromGallary() {
        try {

            Intent i = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(i, Const.CHOOSE_PHOTO);

        } catch (Exception e) {

            e.printStackTrace();
            Commonutils.showtoast("Gallery not found!", this);

        }

    }

    private void takePhotoFromCamera() {

        Calendar cal = Calendar.getInstance();

        cameraFile = new File(Environment.getExternalStorageDirectory(),
                (cal.getTimeInMillis() + ".jpg"));


        if (!cameraFile.exists()) {

            try {

                cameraFile.createNewFile();

            } catch (IOException e) {

                // TODO Auto-generated catch block
                e.printStackTrace();

            }

        } else {

            cameraFile.delete();

            try {

                cameraFile.createNewFile();

            } catch (IOException e) {

                // TODO Auto-generated catch block
                e.printStackTrace();

            }

        }

        uri = Uri.fromFile(cameraFile);

        Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        i.putExtra(MediaStore.EXTRA_OUTPUT, uri);

        startActivityForResult(i, Const.TAKE_PHOTO);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("mahi", "req code" + requestCode);

        switch (requestCode) {

            case Const.CHOOSE_PHOTO:
                if (data != null) {

                    uri = data.getData();
                    if (uri != null) {

                        beginCrop(uri);

                    } else {
                        Toast.makeText(this, getResources().getString(R.string.txt_img_error),
                                Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case Const.TAKE_PHOTO:


                if (uri != null) {
                    beginCrop(uri);
                } else {
                    Toast.makeText(this, getResources().getString(R.string.txt_img_error),
                            Toast.LENGTH_LONG).show();
                }

                break;
            case Crop.REQUEST_CROP:


                if (data != null)
                    handleCrop(resultCode, data);

                break;
        }
    }

    private void beginCrop(Uri source) {

        Uri outputUri = Uri.fromFile(new File(Environment
                .getExternalStorageDirectory(), (Calendar.getInstance()
                .getTimeInMillis() + ".jpg")));
        Crop.of(source, outputUri).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {

            filePath = getRealPathFromURI(Crop.getOutput(result));

            //.setImageURI(Crop.getOutput(result));
            Glide.with(this).load(filePath)
                    .transition(new DrawableTransitionOptions().crossFade())
                    .apply(new RequestOptions().error(R.drawable.defult_user).placeholder(R.drawable.defult_user).centerCrop())
                    .into(profile_image);

        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null,
                null, null, null);

        if (cursor == null) { // Source is Dropbox or other similar local file
            // path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor
                    .getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        switch (serviceCode) {

            case Const.ServiceCode.UPDATE_PROFILE:
                Commonutils.progressdialog_hide();

                if (response != null) {
                    EbizworldUtils.appLogDebug("HaoLS", "profile response" + response);
                    try {

                        JSONObject job1 = new JSONObject(response);

                        if (job1.getString("success").equals("true")) {

                            try {

                                if (!filePath.equals("")) {

                                    File file = new File(filePath);
                                    file.getAbsoluteFile().delete();

                                }

                                if (cameraFile != null) {
                                    cameraFile.getAbsoluteFile().delete();
                                }

                            } catch (Exception e) {

                                e.printStackTrace();
                            }

                            if (new PreferenceHelper(this).getLoginType().equals(Const.PatientService.PATIENT)){

                                getPatientInformation();


                            }else if (new PreferenceHelper(this).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)){

                                getNurseInformation();

                            }else if (new PreferenceHelper(this).getLoginType().equals(Const.HospitalService.HOSPITAL)){

                                getHospitalInformation();

                            }

                        } else {

                            if (job1.has("error_messages")){

                                String error = job1.getString("error_messages");
                                Commonutils.showtoast(error, this);

                                /*if (new PreferenceHelper(this).getLoginType().equals(Const.PatientService.PATIENT)){

                                    enablePatientViews();

                                }else if (new PreferenceHelper(this).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)){

                                    enableNursingHomeViews();
                                }else if (new PreferenceHelper(this).getLoginType().equals(Const.HospitalService.HOSPITAL)){

                                    enableHospitalViews();
                                }*/

                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        EbizworldUtils.appLogDebug("HaoLS", "Update profile failed " + e.toString());

                        /*if (new PreferenceHelper(this).getLoginType().equals(Const.PatientService.PATIENT)){

                            enablePatientViews();

                        }else if (new PreferenceHelper(this).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)){

                            enableNursingHomeViews();

                        }else if (new PreferenceHelper(this).getLoginType().equals(Const.HospitalService.HOSPITAL)){

                            enableHospitalViews();

                        }*/

                    }
                }


                break;

            case Const.ServiceCode.GET_ACCOUNT_INFORMATION:{

                if (response != null){

                    EbizworldUtils.appLogDebug("HaoLS", "Get account information detail " + response);

                    try {

                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getString("success").equals("true")){

                            if (new PreferenceHelper(this).getLoginType().equals(Const.PatientService.PATIENT)){

                                if (pcontent.isSuccessWithStoreId(response)) {

                                    pcontent.parsePatientAndStoreToDb(response);

                                    Commonutils.showtoast(getString(R.string.update_success_text), this);
                                    tv_edit_profile.setText(getString(R.string.btn_edit));

                                    disablePatientViews();
                                    EbizworldUtils.appLogDebug("HaoLS", "Patient isSuccessWithStore succeeded ");

                                }

                            }else if (new PreferenceHelper(this).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)){

                                if (pcontent.isSuccessWithStoreId(response)){

                                    pcontent.parseNurseAndStoreToDb(response);

                                    Commonutils.showtoast(getString(R.string.update_success_text), this);
                                    tv_edit_profile.setText(getString(R.string.btn_edit));
                                    disableNursingHomeViews();

                                    EbizworldUtils.appLogDebug("HaoLS", "Nurse isSuccessWithStore succeeded ");
                                }


                            }else if (new PreferenceHelper(this).getLoginType().equals(Const.HospitalService.HOSPITAL)){

                                if (pcontent.isSuccessWithStoreId(response)){

                                    pcontent.parseHospitalAndStoreToDb(response);

                                    EbizworldUtils.showShortToast(getString(R.string.update_success_text), this);
                                    tv_edit_profile.setText(getString(R.string.btn_edit));
                                    disableHospitalViews();

                                    EbizworldUtils.appLogDebug("HaoLS", "Hospital isSuccessWithStore succeeded ");
                                }


                            }

                        }else {

                            if (jsonObject.has("error_messages")){

                                String errorMessage = jsonObject.getString("error_messages");

                                EbizworldUtils.showShortToast(errorMessage, this);
                                EbizworldUtils.appLogDebug("HaoLS", "Get account information isSuccessWithStore failed ");

                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        EbizworldUtils.appLogDebug("HaoLS", "Get account information failed " + e.toString());
                    }
                }

            }
            break;

            case Const.ServiceCode.AMBULANCE_OPERATOR:{

                if (response != null){

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getString("success").equals("true")){

                            if (new PreferenceHelper(this).getLoginType().equals(Const.PatientService.PATIENT)){

                                JSONArray jsonArray = jsonObject.getJSONArray("operator");


                                if (jsonArray.length() > 0){

                                    EbizworldUtils.appLogDebug("HaoLS", "Ambulance Operator list " + jsonArray.toString());

                                    mAmbulanceOperators.clear();

                                    for (int i = 0; i < jsonArray.length(); i++){

                                        JSONObject object = jsonArray.getJSONObject(i);

                                        AmbulanceOperator ambulanceOperator = new AmbulanceOperator();

                                        ambulanceOperator.setId(String.valueOf(object.getInt("id")));
                                        ambulanceOperator.setAmbulanceOperator(object.getString("name"));
                                        ambulanceOperator.setAmbulanceImage(object.getString("picture"));
                                        mAmbulanceOperators.add(ambulanceOperator);

                                    }

                                    if (mAmbulanceOperators.size() > 0){

                                        EbizworldUtils.appLogDebug("HaoLS", "Setting up Spinner for Ambulance Operator");

                                        mAmbulanceOperatorSpinnerAdapter = new AmbulanceOperatorSpinnerAdapter(this, mAmbulanceOperators);
                                        spn_ambulance_operator.setAdapter(mAmbulanceOperatorSpinnerAdapter);

                                        if (mPatient != null){

                                            EbizworldUtils.appLogDebug("HaoLS", "mPatient != null");
                                            EbizworldUtils.appLogDebug("HaoLS", "Operator id of Patient " + String.valueOf(mPatient.getmOperatorID()));

                                            for (int i = 0; i < mAmbulanceOperators.size(); i++){

                                                EbizworldUtils.appLogDebug("HaoLS", "Start for loop " + i );
                                                EbizworldUtils.appLogDebug("HaoLS", "Ambulance Operator id " + mAmbulanceOperators.get(i).getId() );

                                                if (mPatient.getmOperatorID() == Integer.parseInt(mAmbulanceOperators.get(i).getId())){

                                                    spn_ambulance_operator.setSelection(i);

                                                    EbizworldUtils.appLogDebug("HaoLS", "Spinner set default text with Operator ID " + String.valueOf(mPatient.getmOperatorID()));
                                                    break;
                                                }
                                            }

                                        }

                                    }

                                }

                            }else if (new PreferenceHelper(this).getLoginType().equals(Const.NursingHomeService.NURSING_HOME) ||
                                    new PreferenceHelper(this).getLoginType().equals(Const.HospitalService.HOSPITAL)){

                                JSONArray jsonArray = jsonObject.getJSONArray("operator");


                                if (jsonArray.length() > 0){

                                    EbizworldUtils.appLogDebug("HaoLS", "Ambulance Operator list " + jsonArray.toString());

                                    mAmbulanceOperators.clear();

                                    for (int i = 0; i < jsonArray.length(); i++){

                                        JSONObject object = jsonArray.getJSONObject(i);

                                        AmbulanceOperator ambulanceOperator = new AmbulanceOperator();

                                        ambulanceOperator.setId(String.valueOf(object.getInt("id")));
                                        ambulanceOperator.setAmbulanceOperator(object.getString("name"));
                                        ambulanceOperator.setAmbulanceImage(object.getString("picture"));
                                        mAmbulanceOperators.add(ambulanceOperator);

                                    }

                                    if (mAmbulanceOperators.size() > 0){

                                        EbizworldUtils.appLogDebug("HaoLS", "Setting up Spinner for Ambulance Operator");

                                        mAmbulanceOperatorSpinnerAdapter = new AmbulanceOperatorSpinnerAdapter(this, mAmbulanceOperators);
                                        spn_ambulance_operator.setAdapter(mAmbulanceOperatorSpinnerAdapter);

                                        if (mNurse != null){

                                            EbizworldUtils.appLogDebug("HaoLS", "mNurse != null");
                                            EbizworldUtils.appLogDebug("HaoLS", "Operator id of Nurse " + String.valueOf(mNurse.getmOperatorID()));

                                            for (int i = 0; i < mAmbulanceOperators.size(); i++){

                                                EbizworldUtils.appLogDebug("HaoLS", "Start for loop " + i );
                                                EbizworldUtils.appLogDebug("HaoLS", "Ambulance Operator id " + mAmbulanceOperators.get(i).getId() );

                                                if (mNurse.getmOperatorID() == Integer.parseInt(mAmbulanceOperators.get(i).getId())){

                                                    spn_ambulance_operator.setSelection(i);

                                                    EbizworldUtils.appLogDebug("HaoLS", "Spinner set default text with Operator ID " + String.valueOf(mNurse.getmOperatorID()));
                                                    break;
                                                }
                                            }

                                        }else if (mHopital != null){

                                            EbizworldUtils.appLogDebug("HaoLS", "mHopital != null");
                                            EbizworldUtils.appLogDebug("HaoLS", "Operator id of Hopital " + String.valueOf(mHopital.getmAmbulanceOperator()));

                                            for (int i = 0; i < mAmbulanceOperators.size(); i++){

                                                EbizworldUtils.appLogDebug("HaoLS", "Start for loop " + i );
                                                EbizworldUtils.appLogDebug("HaoLS", "Ambulance Operator id " + mAmbulanceOperators.get(i).getId() );

                                                if (mHopital.getmAmbulanceOperator() == Integer.parseInt(mAmbulanceOperators.get(i).getId())){

                                                    spn_ambulance_operator.setSelection(i);

                                                    EbizworldUtils.appLogDebug("HaoLS", "Spinner set default text with Operator ID " + String.valueOf(mHopital.getmAmbulanceOperator()));
                                                    break;
                                                }
                                            }

                                        }

                                    }

                                }

                            }



                        }else {

                            EbizworldUtils.appLogDebug("HaoLS", "ProfileActivity get Ambulance Operator failed" + response);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        EbizworldUtils.appLogDebug("HaoLS", "ProfileActivity get Ambulance Operator failed  " + e.toString());
                    }
                }
            }
            break;

        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
