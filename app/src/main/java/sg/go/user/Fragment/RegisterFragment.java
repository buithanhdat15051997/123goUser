package sg.go.user.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import sg.go.user.Adapter.SpinnerLanguageAdapter;
import sg.go.user.HttpRequester.VolleyRequester;
import sg.go.user.Interface.AsyncTaskCompleteListener;
import sg.go.user.HttpRequester.MultiPartRequester;
import sg.go.user.MainActivity;
import sg.go.user.Models.SocialMediaProfile;
import sg.go.user.R;
import sg.go.user.SignInActivity;
import sg.go.user.Utils.EbizworldUtils;
import sg.go.user.Utils.Commonutils;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.ParseContent;
import sg.go.user.Utils.PreferenceHelper;
import sg.go.user.Utils.ReadFiles;
import com.soundcloud.android.crop.Crop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * Created by user on 1/5/2017.
 */

public class RegisterFragment extends BaseRegisterFragment implements AsyncTaskCompleteListener {
    private ImageButton btn_back_reg;
    private String type = Const.MANUAL;
    private EditText et_register_full_name/*, et_register_last_name*/, et_register_your_email, et_register_your_password, et_register_phone;
    private CircleImageView iv_register_user_icon;
    private AQuery aQuery;
    private String socialUrl;
    private ImageButton vis_pass;
    private boolean isclicked = false;
    private LinearLayout password_lay;
    private String filePath = "";
    private String socialId;
    private Spinner sp_code, sp_country_reg, sp_curency_reg;
    private ArrayList<String> countryCodes, countryCodesIso;
    private SpinnerLanguageAdapter adapter_currencey, adapter;
    private Uri uri = null;
    private File cameraFile;
    private TextView rigister_btn;
    private String sFullName, sLastName, sEmailId, sPassword, medical_no, phone;
    private ParseContent pcontent;
    private RadioGroup radioGroup;
    private RadioButton rd_btn;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        btn_back_reg = (ImageButton) view.findViewById(R.id.btn_back_reg);
        btn_back_reg.setOnClickListener(this);
        aQuery = new AQuery(activity);
        countryCodes = new ArrayList<>();
        countryCodesIso = new ArrayList<>();
        pcontent = new ParseContent(activity);
        iv_register_user_icon = (CircleImageView) view.findViewById(R.id.iv_register_user_icon);
        et_register_full_name = (EditText) view.findViewById(R.id.et_register_fullname);
        /*et_register_last_name = (EditText) view.findViewById(R.id.et_register_last_name);*/
        et_register_your_email = (EditText) view.findViewById(R.id.et_register_your_email);
        et_register_your_password = (EditText) view.findViewById(R.id.et_register_your_password);
        et_register_phone = (EditText) view.findViewById(R.id.et_register_phone);
        rigister_btn = (TextView) view.findViewById(R.id.rigister_btn);
        password_lay = (LinearLayout) view.findViewById(R.id.password_lay);
        vis_pass = (ImageButton) view.findViewById(R.id.vis_pass);
        sp_code = (Spinner) view.findViewById(R.id.sp_code);
        sp_country_reg = (Spinner) view.findViewById(R.id.sp_country_reg);
        sp_curency_reg = (Spinner) view.findViewById(R.id.sp_curency_reg);
        radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);

        iv_register_user_icon.setOnClickListener(this);
        rigister_btn.setOnClickListener(this);

        et_register_your_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                vis_pass.setVisibility(View.VISIBLE);
                vis_pass.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isclicked == false) {
                            et_register_your_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            et_register_your_password.setSelection(et_register_your_password.getText().length());
                            isclicked = true;
                            vis_pass.setVisibility(View.VISIBLE);

                        } else {
                            isclicked = false;
                            et_register_your_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            vis_pass.setVisibility(View.VISIBLE);

                            et_register_your_password.setSelection(et_register_your_password.getText().length());
                        }
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        setSpinners();

        return view;

    }

    private void setSpinners() {
        ArrayAdapter<String> countryCodeAdapter = new ArrayAdapter<String>(activity, R.layout.spinner_item, parseCountryCodes());
        //  countryCodeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_code.setAdapter(countryCodeAdapter);
        TelephonyManager tm = (TelephonyManager) activity.getSystemService(activity.TELEPHONY_SERVICE);
        String countryCodeValue = tm.getNetworkCountryIso();
        for (int i = 0; i < countryCodesIso.size(); i++) {
            if (countryCodesIso.get(i).equalsIgnoreCase(countryCodeValue)) {
                sp_code.setSelection(i);
            }
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, R.layout.spinner_item, parseCountry());
        sp_country_reg.setAdapter(adapter);

        String[] lst_currency = getResources().getStringArray(R.array.currency);
        Integer[] currency_imageArray = {R.drawable.us, R.drawable.ic_india};

        adapter_currencey = new SpinnerLanguageAdapter(activity, R.layout.spinner_value_layout, lst_currency, currency_imageArray);
        sp_curency_reg.setAdapter(adapter_currencey);
    }

    public ArrayList<String> parseCountry() {
        String response = "";
        ArrayList<String> list = new ArrayList<String>();
        try {
            response = ReadFiles.readRawFileAsString(activity,
                    R.raw.countrycodes);

            JSONArray array = new JSONArray(response);
            Log.d("mahi", "countries" + response);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                list.add(object.getString("name"));
            }

            Collections.sort(list);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<String> parseCountryCodes() {
        String response = "";
        ArrayList<String> list = new ArrayList<String>();
        try {
            response = ReadFiles.readRawFileAsString(activity,
                    R.raw.countrycodes);

            JSONArray array = new JSONArray(response);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                list.add(object.getString("alpha-2") + " (" + object.getString("phone-code") + ")");
                countryCodes.add(object.getString("phone-code"));
                countryCodesIso.add(object.getString("alpha-2"));
            }

            Collections.sort(list);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back_reg:
                Intent i = new Intent(activity, SignInActivity.class);
                startActivity(i);
                break;
            case R.id.iv_register_user_icon:
                showPictureDialog();
                break;
            case R.id.rigister_btn:
                if (validate()) {

                    registeration(type, socialId);
                }
                break;

        }
    }

    private void registeration(String type, String socialId) {

        Commonutils.progressdialog_show(activity, getResources().getString(R.string.reg_load));
        // Speciality speclty = speciality_list.get(pos);

        if (type.equals(Const.MANUAL)) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(Const.Params.URL, Const.ServiceType.REGISTER);
            map.put(Const.Params.FULLNAME, sFullName);
            /*map.put(Const.Params.FIRSTNAME, sFullName);
            map.put(Const.Params.LAST_NAME, sLastName);*/
            map.put(Const.Params.EMAIL, sEmailId);
            map.put(Const.Params.PASSWORD, et_register_your_password.getText().toString());
            map.put(Const.Params.PICTURE, filePath);

            // map.put(Const.Params.SPECIALITY, String.valueOf(speclty.getId()));
            map.put(Const.Params.DEVICE_TOKEN,
                    new PreferenceHelper(activity).getDeviceToken());
            map.put(Const.Params.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID);

            map.put(Const.Params.LOGIN_BY, Const.MANUAL);
            String[] items1 = sp_code.getSelectedItem().toString().split(" ");
            String country = items1[0];
            String code = items1[1];
            map.put(Const.Params.MOBILE, code.replace("(", "").replace(")", "") + "" + phone);

            map.put(Const.Params.CURRENCY, sp_curency_reg.getSelectedItem().toString());
            map.put(Const.Params.TIMEZONE, TimeZone.getDefault().getID());
            map.put(Const.Params.COUNTRY, sp_country_reg.getSelectedItem().toString());

            int selectedId = radioGroup.getCheckedRadioButtonId();
            rd_btn = (RadioButton) activity.findViewById(selectedId);
            map.put(Const.Params.GENDER, rd_btn.getText().toString());

            Log.d("mahi", map.toString());
            if (filePath.equals("") || null == filePath) {
                new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.REGISTER,
                        this);
            } else {
                new MultiPartRequester(activity, map, Const.ServiceCode.REGISTER,
                        this);
            }
        } else {
            registerSocial(socialId, type);
        }

    }

    private void registerSocial(String socialId, String type) {

        //Speciality speclty = speciality_list.get(pos);


        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.REGISTER);
        map.put(Const.Params.FIRSTNAME, sFullName);
        map.put(Const.Params.LAST_NAME, sLastName);
        map.put(Const.Params.EMAIL, sEmailId);
        map.put(Const.Params.PICTURE, filePath);

        // map.put(Const.Params.SPECIALITY, String.valueOf(speclty.getId()));
        map.put(Const.Params.DEVICE_TOKEN,
                new PreferenceHelper(activity).getDeviceToken());
        map.put(Const.Params.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID);

        map.put(Const.Params.LOGIN_BY, type);
        map.put(Const.Params.SOCIAL_ID, socialId);


        String[] items1 = sp_code.getSelectedItem().toString().split(" ");
        String country = items1[0];
        String code = items1[1];
        map.put(Const.Params.MOBILE, code.replace("(", "").replace(")", "") + "" + phone);

        map.put(Const.Params.CURRENCY, sp_curency_reg.getSelectedItem().toString());
        map.put(Const.Params.TIMEZONE, TimeZone.getDefault().getID());
        map.put(Const.Params.COUNTRY, sp_country_reg.getSelectedItem().toString());

        int selectedId = radioGroup.getCheckedRadioButtonId();
        rd_btn = (RadioButton) activity.findViewById(selectedId);
        map.put(Const.Params.GENDER, rd_btn.getText().toString());


        Log.d("mahi", "social reg" + map.toString());
        if (filePath.equals("") || null == filePath) {
            new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.REGISTER,
                    this);
        } else {
            new MultiPartRequester(activity, map, Const.ServiceCode.REGISTER,
                    this);
        }

    }


    private boolean validate() {
        getAllRegisterDetails();

        if (sFullName.length() == 0) {
            et_register_full_name.setError(getResources().getString(R.string.txt_fname_error));
            et_register_full_name.requestFocus();
            return false;

        } /*else if (sLastName.length() == 0) {
            et_register_last_name.setError(getResources().getString(R.string.txt_lname_error));
            et_register_last_name.requestFocus();
            return false;

        } */else if (sEmailId.length() == 0) {
            et_register_your_email.setError(getResources().getString(R.string.txt_email_error));
            et_register_your_email.requestFocus();
            return false;

        } else if (!EbizworldUtils.eMailValidation(sEmailId)) {
            et_register_your_email.setError(getResources().getString(R.string.txt_incorrect_error));
            et_register_your_email.requestFocus();
            return false;

        }/*  else if (filePath == null || filePath.equals("")) {
            EbizworldUtils.showLongToast(getResources().getString(R.string.txt_picture_error), activity);
            return false;

        }*/ else if (phone.length() == 0) {
            et_register_phone.setError(getResources().getString(R.string.txt_phone_error));
            et_register_phone.requestFocus();
            return false;

        }
        if (password_lay.getVisibility() == View.VISIBLE) {
            if (TextUtils.isEmpty(et_register_your_password.getText().toString())) {
                et_register_your_password.setError(getResources().getString(R.string.txt_pass_error));
                et_register_your_password.requestFocus();
                return false;

            } else if (et_register_your_password.getText().toString().length() < 8) {
                et_register_your_password.setError(getResources().getString(R.string.txt_pass8_error));
                et_register_your_password.requestFocus();
                return false;
            }
        }

        return true;
    }

    private void getAllRegisterDetails() {
        sFullName = et_register_full_name.getText().toString().trim();
        /*sLastName = et_register_last_name.getText().toString().trim();*/
        sEmailId = et_register_your_email.getText().toString().trim();
        phone = et_register_phone.getText().toString();
        if (!type.equals(Const.MANUAL)) {
           /* filePath = new AQuery(activity).getCachedFile(socialUrl)
                    .getAbsolutePath();*/
        }
    }

    private void showPictureDialog() {
      /*  AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
        dialog.setTitle(getResources().getString(R.string.txt_slct_option));
        String[] items = {getResources().getString(R.string.txt_gellery), getResources().getString(R.string.txt_cameray), getResources().getString(R.string.txt_warning)};

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
        dialog.show();*/

       final Dialog dialog = new Dialog(activity, R.style.DialogThemeforview);
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

        dialog.show();
    }

    private void choosePhotoFromGallary() {
        try {
            Intent i = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            activity.startActivityForResult(i, Const.CHOOSE_PHOTO, Const.REGISTER_FRAGMENT);
        } catch (Exception e) {
            e.printStackTrace();
            Commonutils.showtoast("Gallery not found!", activity);
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
        activity.startActivityForResult(i, Const.TAKE_PHOTO, Const.REGISTER_FRAGMENT);
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = activity.getContentResolver().query(contentURI, null,
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle mBundle = getArguments();
        if (mBundle != null) {
            SocialMediaProfile mediaProfile = (SocialMediaProfile) mBundle.getSerializable("social_profile");
            if (mediaProfile != null) {
                type = mediaProfile.getLoginType();
                setSocailDetailsOnView(mediaProfile);

            }
        }

    }

    private void setSocailDetailsOnView(SocialMediaProfile mediaProfile) {
       /* aQuery.id(iv_register_user_icon).image(mediaProfile.getPictureUrl(), true, true,
                300, 300, new BitmapAjaxCallback() {
                    @Override
                    public void callback(String url, ImageView iv, Bitmap bm,
                                         AjaxStatus status) {

                        filePath = aQuery.getCachedFile(url).getPath();

                        iv.setImageBitmap(bm);
                    }
                });*/

        socialId = mediaProfile.getSocialUniqueId();
        et_register_full_name.setText(mediaProfile.getFirstName());
        /*et_register_last_name.setText(mediaProfile.getLastName());*/
        et_register_your_email.setText(mediaProfile.getEmailId());
        socialUrl = mediaProfile.getPictureUrl();
        et_register_your_password.setVisibility(View.GONE);
        password_lay.setVisibility(View.GONE);

        Log.d("mahi", "fb profile" + mediaProfile.getSocialUniqueId());
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.currentfragment = Const.REGISTER_FRAGMENT;
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
                        Toast.makeText(activity, getResources().getString(R.string.txt_img_error),
                                Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case Const.TAKE_PHOTO:


                if (uri != null) {
                    beginCrop(uri);
                } else {
                    Toast.makeText(activity, getResources().getString(R.string.txt_img_error),
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
        Crop.of(source, outputUri).asSquare().start(activity);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {

            filePath = getRealPathFromURI(Crop.getOutput(result));

            //.setImageURI(Crop.getOutput(result));
            Glide.with(activity).load(filePath)
                    .apply(new RequestOptions().centerCrop())
                    .into(iv_register_user_icon);
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(activity, Crop.getError(result).getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {

        Fragment fragment = (getFragmentManager()
                .findFragmentById(R.id.frame_login));
        if (fragment.isResumed()) {
            getFragmentManager().beginTransaction().remove(fragment)
                    .commitAllowingStateLoss();
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

    }


    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        switch (serviceCode) {
            case Const.ServiceCode.REGISTER:
                Commonutils.progressdialog_hide();
                Log.d("DAT123GO", "reg response" + response);
                if (response != null)
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

                            if (pcontent.isSuccessWithStoreId(response)) {

                                /*pcontent.parseUserAndStoreToDb(response);*/
                                pcontent.parsePatientAndStoreToDb(response);
                                new PreferenceHelper(activity).putPassword(et_register_your_password.getText()
                                        .toString());

                                startActivity(new Intent(activity, MainActivity.class));

                            } else {

                            }

                        } else {

                            String error = job1.getString("error_messages");
                            Commonutils.showtoast(error, activity);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                break;


            default:
                break;

        }
    }

}
