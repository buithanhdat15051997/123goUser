package sg.go.user.Fragment;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import sg.go.user.HttpRequester.VolleyRequester;
import sg.go.user.Interface.AsyncTaskCompleteListener;
import sg.go.user.R;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.EbizworldUtils;
import sg.go.user.WelcomeActivity;

/**
 * Created by Mahesh on 7/5/2017.
 */

public class OTPFragment extends BaseRegisterFragment implements AsyncTaskCompleteListener {

    @BindView(R.id.et_otp_mobile)
    EditText et_otp_mobile;

    @BindView(R.id.user_otp)
    EditText user_otp;

    @BindView(R.id.ccp)
    CountryCodePicker ccp;

    @BindView(R.id.input_layout_otp)
    TextInputLayout input_layout_otp;

    @BindView(R.id.close_sign)
    ImageView close_sign;

    @BindView(R.id.btn_edit_number)
    ImageView btn_edit_number;

    @BindView(R.id.btn_resend)
    TextView btn_resend;

    @BindView(R.id.btn_confirm_otp)
    TextView btn_confirm_otp;

    private String code = "";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_otp_verify, container, false);

        ButterKnife.bind(this, view);

        btn_resend.setPaintFlags(btn_resend.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        close_sign.setOnClickListener(this);
        btn_edit_number.setOnClickListener(this);
        btn_confirm_otp.setOnClickListener(this);
        btn_resend.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();

        if (bundle != null) {

            String country_code = bundle.getString(Const.Params.COUNTRY_CODE);
            String mobile = bundle.getString(Const.Params.MOBILE);
            code = bundle.getString("code");
            et_otp_mobile.setText(mobile);

            ccp.setDefaultCountryUsingNameCode(country_code);
            ccp.resetToDefaultCountry();
            //user_otp.setText(code);
        }

    }

    private boolean isvalid() {
        if (TextUtils.isEmpty(user_otp.getText().toString())) {

            user_otp.setError(getResources().getString(R.string.txt_otp_error));
            user_otp.requestFocus();

            return false;

        } else if (!user_otp.getText().toString().equals(code)) {

            user_otp.setError(getResources().getString(R.string.txt_otp_wrong));
            user_otp.requestFocus();
            return false;

        } else {

            user_otp.setError(null);
            return true;
        }
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {

            case R.id.close_sign:
                startActivity(new Intent(activity, WelcomeActivity.class));
                break;

            case R.id.btn_edit_number:
                ccp.setCcpClickable(true);
                et_otp_mobile.setEnabled(true);
                et_otp_mobile.requestFocus();
                break;
            case R.id.btn_confirm_otp:

                if (isvalid()) {

                    Bundle mbundle = new Bundle();
                    SignUpNextFragment signUpNextFragment = new SignUpNextFragment();
                    mbundle.putString(Const.Params.MOBILE, et_otp_mobile.getText().toString());
                    mbundle.putString(Const.Params.COUNTRY_CODE, ccp.getSelectedCountryCodeWithPlus());
                    signUpNextFragment.setArguments(mbundle);
                    activity.addFragment(signUpNextFragment, false, "", true);

                }
                break;

            case R.id.btn_resend:
                getOTP();
                break;
        }
    }

    private void getOTP() {

        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;

        }

        EbizworldUtils.showSimpleProgressDialog(activity, "Requesting Otp...", false);

        HashMap<String, String> map = new HashMap<String, String>();

        map.put(Const.Params.URL, Const.ServiceType.GET_OTP + Const.Params.MOBILE + "=" + et_otp_mobile.getText().toString().trim()
                + "&" + Const.Params.COUNTRY_CODE + "=" + ccp.getSelectedCountryCodeWithPlus());

        Log.d("HaoLS", map.toString());

        new VolleyRequester(activity, Const.GET, map, Const.ServiceCode.GET_OTP, this);
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {

        switch (serviceCode) {

            case Const.ServiceCode.GET_OTP:

                EbizworldUtils.appLogInfo("HaoLS", "OTP Response: " + response);

                EbizworldUtils.removeProgressDialog();
                try {
                    JSONObject job = new JSONObject(response);

                    if (job.getString("success").equals("true")) {

                        code = job.optString("code");
                       // user_otp.setText(code);

                    } else {

                        String error = job.optString("message");
                        EbizworldUtils.showShortToast(error, activity);

                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                    EbizworldUtils.appLogError("HaoLS", "GET OTP failed: " + e.toString());

                }
                break;
        }
    }

    @Override
    public void onDestroyView() {

        super.onDestroyView();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

    }
}
