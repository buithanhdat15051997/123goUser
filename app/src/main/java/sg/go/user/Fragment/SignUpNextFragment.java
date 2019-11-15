package sg.go.user.Fragment;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import sg.go.user.HttpRequester.VolleyRequester;
import sg.go.user.Interface.AsyncTaskCompleteListener;
import sg.go.user.MainActivity;
import sg.go.user.R;
import sg.go.user.Utils.EbizworldUtils;
import sg.go.user.Utils.Commonutils;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.ParseContent;
import sg.go.user.Utils.PreferenceHelper;
import sg.go.user.WelcomeActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.TimeZone;

/**
 * Created by Mahesh on 7/5/2017.
 */

public class SignUpNextFragment extends BaseRegisterFragment implements AsyncTaskCompleteListener {

    @BindView(R.id.user_fullname)
    EditText user_fullname;/*user_lname,*/

    @BindView(R.id.user_email)
    EditText user_email;

    @BindView(R.id.user_password)
    EditText user_password;

    @BindView(R.id.user_referral_code)
    EditText user_referral_code;

    @BindView(R.id.close_sign)
    ImageView close_sign;

    @BindView(R.id.btn_next)
    TextView btn_next;

    @BindView(R.id.applyRefCode)
    TextView applyRefCode;

    @BindView(R.id.txtTermsAndCondition)
    TextView txtTermsAndCondition;

    @BindView(R.id.cbTermsAndCondition)
    CheckBox cbTermsAndCondition;

    @BindView(R.id.input_layout_fullname)
    TextInputLayout input_layout_fname;/*, input_layout_lname*/

    @BindView(R.id.input_layout_email)
    TextInputLayout input_layout_email;

    @BindView(R.id.input_layout_pass)
    TextInputLayout input_layout_pass;

    @BindView(R.id.input_layout_referral_code)
    TextInputLayout input_layout_referral_code;

    private static final int REQUEST_CODE = 133;
    private String mobile = "", country_code = "";
    int i = 0;
    private LinearLayout linear_Terms_Policy;
    private TextView txt_policy_privacy;
    private TextInputLayout input_layout_confirmpass;
    private TextView user_confirmpassword;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.signup2, container, false);

        ButterKnife.bind(this, view);

        linear_Terms_Policy = view.findViewById(R.id.linear_Terms_Policy);
        txt_policy_privacy = view.findViewById(R.id.txt_policy_privacy);

        input_layout_confirmpass = view.findViewById(R.id.input_layout_confirmpass);
        user_confirmpassword = view.findViewById(R.id.user_confirmpassword);

        txtTermsAndCondition.setPaintFlags(txtTermsAndCondition.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txt_policy_privacy.setPaintFlags(txt_policy_privacy.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        close_sign.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        applyRefCode.setOnClickListener(this);
        txtTermsAndCondition.setOnClickListener(this);
        txt_policy_privacy.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();

        if (bundle != null) {

            mobile = bundle.getString(Const.Params.MOBILE);
            country_code = bundle.getString(Const.Params.COUNTRY_CODE);

        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {

            case R.id.close_sign: {

                startActivity(new Intent(activity, WelcomeActivity.class));
            }
            break;

            case R.id.btn_next: {

                switch (i) {
                    case 0:
                        if (isvalid()) {
                            i++;
                            input_layout_fname.setVisibility(View.GONE);
                            /*input_layout_lname.setVisibility(View.GONE);*/
                            input_layout_referral_code.setVisibility(View.GONE);
                            input_layout_email.setVisibility(View.VISIBLE);
                            user_email.setVisibility(View.VISIBLE);

                        }
                        break;

                    case 1:
                        if (isEmailvalid()) {
                            i++;
                            input_layout_email.setVisibility(View.GONE);
                            input_layout_pass.setVisibility(View.VISIBLE);
                            input_layout_confirmpass.setVisibility(View.VISIBLE);
                            linear_Terms_Policy.setVisibility(View.VISIBLE);
//                            cbTermsAndCondition.setVisibility(View.VISIBLE);
//                            txtTermsAndCondition.setVisibility(View.VISIBLE);
                            input_layout_referral_code.setVisibility(View.GONE);
                            user_password.setVisibility(View.VISIBLE);
                            user_referral_code.setVisibility(View.VISIBLE);
                            applyRefCode.setVisibility(View.VISIBLE);
                            btn_next.setText(getResources().getString(R.string.txt_finish));
                        }
                        break;

                    case 2:
                        if (TextUtils.isEmpty(user_password.getText().toString())||TextUtils.isEmpty(user_confirmpassword.getText().toString())) {

                            Log.d("DAT_SIGN_UP", "Hai text rong");

                           // input_layout_pass.setError(getResources().getString(R.string.txt_pass_error));
                            user_password.setError(getResources().getString(R.string.txt_pass_error));
                            user_confirmpassword.setError(getResources().getString(R.string.txt_pass_error));

                            user_password.requestFocus();
                            user_confirmpassword.requestFocus();

                        } else {

                            String pass = user_password.getText().toString();
                            String pass2 = user_confirmpassword.getText().toString();
                            Log.d("DAT_SIGN_UP", "2"+pass+"    "+pass2 );

                          if(user_password.getText().toString().trim().equals(user_confirmpassword.getText().toString().trim())){

                              Log.d("DAT_SIGN_UP", "mat khau da trung nhau");

                              input_layout_pass.setVisibility(View.GONE);
                              input_layout_confirmpass.setVisibility(View.GONE);
                              input_layout_referral_code.setVisibility(View.GONE);



                              if (cbTermsAndCondition.isChecked() == true) {

                                  Log.d("DAT_SIGN_UP", "Check thanh cong");
//                                input_layout_pass.setVisibility(View.GONE);
//                                input_layout_referral_code.setVisibility(View.GONE);
                                  registerManual();

                              } else {
                                  Log.d("DAT_SIGN_UP", "Chua check");

                                  input_layout_pass.setVisibility(View.VISIBLE);
                                  input_layout_confirmpass.setVisibility(View.VISIBLE);
                                  input_layout_referral_code.setVisibility(View.GONE);
//                                btn_next.setEnabled(false);
                                  Toast.makeText(activity, activity.getResources().getString(R.string.txt_accept_term_condition), Toast.LENGTH_LONG).show();
                              }

                          } else {

                              Log.d("DAT_SIGN_UP", "Khong giong nhau");

                              EbizworldUtils.showLongToast(activity.getResources().getString(R.string.txt_pass_confirmpass_not_match),activity);

                          }

                        }

                        break;
//                    case 3:
//                        if (TextUtils.isEmpty(user_password.getText().toString())) {
//                            input_layout_pass.setError(getResources().getString(R.string.txt_pass_error));
//                            user_password.requestFocus();
//
//                        } else {
//                            i++;
////                            input_layout_pass.setVisibility(View.GONE);
////                            input_layout_referral_code.setVisibility(View.GONE);
//                            Log.d("manhdanglam", "onClick: "+cbTermsAndCondition.isChecked());
//                            if (cbTermsAndCondition.isChecked() == true) {
//                                input_layout_pass.setVisibility(View.VISIBLE);
//                                input_layout_referral_code.setVisibility(View.VISIBLE);
//                                registerManual();
//                            }else {
//                                input_layout_pass.setVisibility(View.VISIBLE);
//                                input_layout_referral_code.setVisibility(View.GONE);
////                                btn_next.setEnabled(false);
//                                Toast.makeText(activity, "You should accept our Term & Condition first", Toast.LENGTH_SHORT).show();
//                            }
//
//                        }
//                        break;

                }
            }
            break;

            case R.id.applyRefCode: {

                if (user_referral_code.getText().toString().length() > 0 && !user_referral_code.getText().toString().isEmpty()) {

                    applyReferral();

                }
            }
            break;
            case R.id.txtTermsAndCondition:

                String url = "https://123-go.co/public/Term&ConditionRider.pdf";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                break;

            case R.id.txt_policy_privacy:

                String url_policy = "https://123-go.co/public/Privacy.pdf";
                Intent intent_policy = new Intent(Intent.ACTION_VIEW);
                intent_policy.setData(Uri.parse(url_policy));
                startActivity(intent_policy);

                break;
        }
    }

    private void applyReferral() {
        Commonutils.progressdialog_show(activity, getResources().getString(R.string.crop__wait));

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.APPLY_REFERRAL);
        map.put(Const.Params.REFERRAL_CODE, user_referral_code.getText().toString());
        Log.d("HaoLS", "referral map: " + map.toString());
        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.APPLY_REFERRAL, this);
    }


    private void registerManual() {

        Commonutils.progressdialog_show(activity, getResources().getString(R.string.reg_load));

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.REGISTER);
        map.put(Const.Params.FULLNAME, user_fullname.getText().toString());
        map.put(Const.Params.EMAIL, user_email.getText().toString());
        map.put(Const.Params.PASSWORD, user_password.getText().toString());
        map.put(Const.Params.MOBILE, mobile);
        map.put(Const.Params.COUNTRY_CODE, country_code);
        map.put(Const.Params.CURRENCY, "1");
        map.put(Const.Params.DEVICE_TOKEN, new PreferenceHelper(activity).getDeviceToken());
        map.put(Const.Params.DEVICE_TYPE, Const.DEVICE_TYPE_ANDROID);
        map.put(Const.Params.LOGIN_BY, Const.MANUAL);
        map.put(Const.Params.TIMEZONE, TimeZone.getDefault().getID());
        map.put(Const.Params.REFERRAL_CODE, user_referral_code.getText().toString());

        Log.d("HaoLS", map.toString());

        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.REGISTER, this);

    }

    private boolean isvalid() {
        if (TextUtils.isEmpty(user_fullname.getText().toString())) {
            input_layout_fname.setError(getResources().getString(R.string.txt_fname_error));
            user_fullname.requestFocus();
            return false;
        } /*else if (TextUtils.isEmpty(user_lname.getText().toString())) {
            input_layout_lname.setError(getResources().getString(R.string.txt_lname_error));
            user_lname.requestFocus();
            return false;
        }*/ else {
            input_layout_fname.setError(null);
            /*input_layout_lname.setError(null);*/
            return true;
        }
    }

    private boolean isEmailvalid() {
        if (TextUtils.isEmpty(user_email.getText().toString())) {
            input_layout_email.setError(getResources().getString(R.string.txt_email_error));
            user_email.requestFocus();
            return false;
        } else if (!EbizworldUtils.eMailValidation(user_email.getText().toString())) {
            input_layout_email.setError(getResources().getString(R.string.txt_incorrect_error));
            user_email.requestFocus();
            return false;
        } else {
            input_layout_email.setError(null);
            return true;
        }
    }


    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        switch (serviceCode) {

            case Const.ServiceCode.APPLY_REFERRAL:
                Log.d("HaoLS", "Referral response: " + response);

                if (response != null) {
                    try {

                        JSONObject job1 = new JSONObject(response);
                        if (job1.getString("success").equals("true")) {
                            Commonutils.progressdialog_hide();
                            Toast.makeText(activity, job1.getString("message"), Toast.LENGTH_LONG).show();
                        } else {
                            Commonutils.progressdialog_hide();
                            Toast.makeText(activity, job1.getString("error"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                break;

            case Const.ServiceCode.REGISTER: {

                Log.d("HaoLS", "reg response" + response);
                Commonutils.progressdialog_hide();

                if (response != null)
                    try {

                        JSONObject job1 = new JSONObject(response);
                        if (job1.getString("success").equals("true")) {

                            if (new ParseContent(activity).isSuccessWithStoreId(response)) {

                                new ParseContent(activity).parsePatientAndStoreToDb(response);

                                new PreferenceHelper(activity).putPassword(user_password.getText().toString());

                                startActivity(new Intent(activity, MainActivity.class));

                                activity.finish();


                            } else {

                            }

                        } else {

                            if (job1.has("error_code")) {

                                if (job1.optString("error_code").equals("168")) {
                                    i = 2;
                                    input_layout_email.setVisibility(View.GONE);
                                    input_layout_pass.setVisibility(View.VISIBLE);
                                    input_layout_confirmpass.setVisibility(View.VISIBLE);
                                    input_layout_referral_code.setVisibility(View.VISIBLE);
                                    user_password.setVisibility(View.VISIBLE);
                                    user_referral_code.setVisibility(View.VISIBLE);
                                    btn_next.setText(getResources().getString(R.string.txt_finish));
                                } else {
                                    i = 1;
                                    input_layout_fname.setVisibility(View.GONE);
                                    /*input_layout_lname.setVisibility(View.GONE);*/
                                    input_layout_email.setVisibility(View.VISIBLE);
                                    user_email.setVisibility(View.VISIBLE);
                                    btn_next.setText(getResources().getString(R.string.txt_next_signup));
                                }
                            }

                            if (job1.has("error_messages")) {
                                String error = job1.getString("error_messages");
                                Commonutils.showtoast(error, activity);
                            } else {
                                String error = job1.getString("error");
                                Commonutils.showtoast(error, activity);
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

            }
            break;

            case Const.ServiceCode.CREATE_ADD_CARD_URL:
                EbizworldUtils.appLogDebug("Ashutosh", "BrainTreeClientTokenResponse" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("success").equals("true")) {
                        EbizworldUtils.showShortToast(getResources().getString(R.string.txt_card_success), activity);
                        Commonutils.progressdialog_hide();
                        startActivity(new Intent(activity, MainActivity.class));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Const.ServiceCode.GET_BRAIN_TREE_TOKEN_URL:
                EbizworldUtils.appLogDebug("mahi", "BrainTreeClientTokenResponse" + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("success").equals("true")) {
                        String clientToken = jsonObject.optString("client_token");

                        EbizworldUtils.showShortToast("Feature is fixing", activity);

                        /*PaymentRequest paymentRequest = new PaymentRequest()
                                .clientToken(clientToken)
                                .disablePayPal()
                                .secondaryDescription(getResources().getString(R.string.txt_braintree1))
                                .primaryDescription(getResources().getString(R.string.txt_braintree2))
                                .submitButtonText(getResources().getString(R.string.btn_add_card));
                        startActivityForResult(paymentRequest.getIntent(activity), REQUEST_CODE);*/

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;


            default:
                break;
        }
    }

    private void getBrainTreeClientToken() {
        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.ServiceType.GET_BRAIN_TREE_TOKEN_URL);
        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());

        EbizworldUtils.appLogDebug("mahi", "BrainTreeClientTokenMap" + map);

        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.GET_BRAIN_TREE_TOKEN_URL, this);

    }

    void postNonceToServer(String nonce) {
        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }
        Commonutils.progressdialog_show(activity, "Adding Card...");
        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.ServiceType.CREATE_ADD_CARD_URL);
        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());
        map.put(Const.Params.PAYMENT_METHOD_NONCE, nonce);

        EbizworldUtils.appLogDebug("mahi", "BrainTreeADDCARDMap" + map);

        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.CREATE_ADD_CARD_URL, this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        /*if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentMethodNonce paymentMethodNonce = data.getParcelableExtra(
                        BraintreePaymentActivity.EXTRA_PAYMENT_METHOD_NONCE
                );

                String nonce = paymentMethodNonce.getNonce();
                // Log.d("mahi","none value"+nonce);
                // Send the nonce to your server.
                postNonceToServer(nonce);
            } else {
                // handle errors here, an exception may be available in
                Exception error = null;
                try {
                    error = (Exception) data.getSerializableExtra(BraintreePaymentActivity.EXTRA_ERROR_MESSAGE);
                    Log.d("mahi", "error message" + error);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }*/
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
}
