package sg.go.user.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
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

public class SignUpMobileFragment extends BaseRegisterFragment implements AsyncTaskCompleteListener {

    @BindView(R.id.user_mobile_nuber)
    EditText user_mobile_nuber;

    @BindView(R.id.close_sign)
    ImageView close_sign;

    @BindView(R.id.ccp)
    CountryCodePicker ccp;

    @BindView(R.id.btn_confirm_phone)
    TextView btn_confirm_phone;

    @BindView(R.id.input_layout_phone)
    TextInputLayout input_layout_phone;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signup_mobile_layout, container, false);

        ButterKnife.bind(this, view);

        user_mobile_nuber.requestFocus();
        close_sign.setOnClickListener(this);
        btn_confirm_phone.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.close_sign:
                startActivity(new Intent(activity, WelcomeActivity.class));
                break;
            case R.id.btn_confirm_phone:
                if (TextUtils.isEmpty(user_mobile_nuber.getText().toString())) {
                   // input_layout_phone.setError(getResources().getString(R.string.txt_phone_error));

                    user_mobile_nuber.setError(getResources().getString(R.string.txt_phone_error));
                    user_mobile_nuber.requestFocus();
                } else {
                    input_layout_phone.setError(null);
                    getOTP();

                }
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
        map.put(Const.Params.URL, Const.ServiceType.GET_OTP + Const.Params.MOBILE + "=" + user_mobile_nuber.getText().toString().trim()
                + "&" + Const.Params.COUNTRY_CODE + "=" + ccp.getSelectedCountryCodeWithPlus());

        EbizworldUtils.appLogDebug("HaoLS", map.toString());
        new VolleyRequester(activity, Const.GET, map, Const.ServiceCode.GET_OTP,
                this);
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        switch (serviceCode) {
            case Const.ServiceCode.GET_OTP:
                EbizworldUtils.appLogInfo("HaoLS", "OTP Response: " + response);
                EbizworldUtils.removeProgressDialog();

                if (response != null){

                    try {
                        JSONObject job = new JSONObject(response);

                        if (job.getString("success").equals("true")) {

                            EbizworldUtils.appLogDebug("HaoLS", "OTP Response success");

                            String code = job.optString("code");
                            Bundle mbundle = new Bundle();

                            OTPFragment otpFragment = new OTPFragment();
                            mbundle.putString(Const.Params.COUNTRY_CODE, ccp.getSelectedCountryNameCode());
                            mbundle.putString(Const.Params.MOBILE, user_mobile_nuber.getText().toString());
                            mbundle.putString("code", code);
                            otpFragment.setArguments(mbundle);

                            activity.addFragment(otpFragment, false, "", true);

                        } else {

                            String error = job.optString("message");
                            EbizworldUtils.appLogError("HaoLS", "OTP Response fail " + error);
                            EbizworldUtils.showShortToast(error, activity);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        EbizworldUtils.appLogError("HaoLS", "OTP Response failed " + e.toString());
                    }

                }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();


    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {

        super.onDestroy();


    }
}
