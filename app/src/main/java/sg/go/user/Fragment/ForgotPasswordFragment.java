package sg.go.user.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import sg.go.user.HttpRequester.VolleyRequester;
import sg.go.user.Interface.AsyncTaskCompleteListener;
import sg.go.user.R;
import sg.go.user.SignInActivity;
import sg.go.user.Utils.Commonutils;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.EbizworldUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by user on 1/5/2017.
 */

public class ForgotPasswordFragment extends BaseRegisterFragment implements AsyncTaskCompleteListener {

    private ImageButton btn_forgot_cancel;
    private EditText et_email_forgot;
    private TextView forgot_pass_btn;
    private TextInputLayout input_layout_email_forgot;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);
        btn_forgot_cancel = (ImageButton) view.findViewById(R.id.btn_forgot_cancel);
        et_email_forgot = (EditText) view.findViewById(R.id.et_email_forgot);
        forgot_pass_btn = (TextView) view.findViewById(R.id.forgot_pass_btn);

        input_layout_email_forgot = (TextInputLayout) view.findViewById(R.id.input_layout_email_forgot);
        btn_forgot_cancel.setOnClickListener(this);
        forgot_pass_btn.setOnClickListener(this);


        return view;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_forgot_cancel:
                Intent i = new Intent(activity, SignInActivity.class);
                startActivity(i);
                break;

            case R.id.forgot_pass_btn:
                if (et_email_forgot.getText().toString().length() == 0) {
                    et_email_forgot.requestFocus();
                    et_email_forgot.setError(activity.getString(R.string.txt_email_error));
                } else {
                    input_layout_email_forgot.setError(null);
                    RequestPassword();
                }
                break;


        }
    }

    private void RequestPassword() {

        if (!EbizworldUtils.isNetworkAvailable(activity)){

            EbizworldUtils.showLongToast(getResources().getString(R.string.network_error), activity);
            return;

        }

        Commonutils.progressdialog_show(activity, getResources().getString(R.string.req_pass_load));

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.FORGOT_PASSWORD);
        map.put(Const.Params.EMAIL, et_email_forgot.getText().toString());
        EbizworldUtils.appLogDebug("HaoLS", "social: " + map.toString());
        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.FORGOT_PASSWORD,
                this);

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        activity.currentfragment = Const.FORGOT_PASSWORD_FRAGMENT;
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

            case Const.ServiceCode.FORGOT_PASSWORD:

                Commonutils.progressdialog_hide();
                EbizworldUtils.appLogError("HaoLS", "Forgot password: " + response.toString());


                try {
                    JSONObject job = new JSONObject(response);
                    if (job.getString("success").equals("true")) {

                        EbizworldUtils.showLongToast(activity.getResources().getString(R.string.txt_success_forgot_password), activity);
                        Intent i = new Intent(activity, SignInActivity.class);
                        startActivity(i);

                    } else {

                        if(job.has("error")){
                            String error = job.getString("error");
                            EbizworldUtils.showLongToast(error, activity);

                        }

                        String error = job.getString("error_messages");
                        Commonutils.showtoast(error, activity);

                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                    EbizworldUtils.appLogError("HaoLS", "Forgot password: " + e.toString());

                }

                break;
        }

    }

}
