package sg.go.user.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import sg.go.user.HttpRequester.VolleyRequester;
import sg.go.user.Interface.AsyncTaskCompleteListener;
import sg.go.user.MainActivity;
import sg.go.user.R;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.EbizworldUtils;
import sg.go.user.Utils.PreferenceHelper;


public class ChangePasswordFragment extends Fragment implements AsyncTaskCompleteListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private MainActivity mainActivity;
    private View view;
    private Toolbar toolbar;
    private EditText edt_old_password, edt_new_password, edt_pre_new_password;
    private Button btn_confirm_change_password;
    private TextInputLayout input_layout_old_password, input_layout_new_password, input_layout_new_pre_password;
    private String old_password, new_password, new_pre_password;
    private ImageView img_back_changepassword;



    public ChangePasswordFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();

        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_change_password, container, false);

        img_back_changepassword = view.findViewById(R.id.img_back_changepassword);
        toolbar = view.findViewById(R.id.toolbar_wallet);

        edt_old_password = view.findViewById(R.id.edt_old_password);
        edt_new_password = view.findViewById(R.id.edt_new_password);
        edt_pre_new_password = view.findViewById(R.id.edt_pre_new_password);

        input_layout_old_password = view.findViewById(R.id.input_layout_old_password);
        input_layout_new_password = view.findViewById(R.id.input_layout_new_password);
        input_layout_new_pre_password = view.findViewById(R.id.input_layout_new_pre_password);

        btn_confirm_change_password = view.findViewById(R.id.btn_confirm_change_password);

        img_back_changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mainActivity.addFragment(new AccountFragment(), true, Const.ACCOUNT_FRAGMENT, false);

            }
        });


        btn_confirm_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                old_password= edt_old_password.getText().toString().trim();
                new_password = edt_new_password.getText().toString().trim();
                new_pre_password=edt_pre_new_password.getText().toString().trim();

                Log.d("DAT_CHANGE_PASSWORD",old_password+"  "+new_password+"   "+new_pre_password);

                if(old_password.isEmpty()||new_password.isEmpty()||new_pre_password.isEmpty()){

                    Toast.makeText(mainActivity, mainActivity.getResources().getString(R.string.txt_please_enter_changepassword), Toast.LENGTH_LONG).show();

                }else {

                    ChangePassWord();
                }

            }
        });

        return view;
    }

    private void ChangePassWord() {

        HashMap<String, String> params = new HashMap<>();


        EbizworldUtils.showSimpleProgressDialog(mainActivity,getResources().getString(R.string.txt_load),true);

        params.put(Const.Params.URL, Const.ServiceType.CHANGE_PASSWORD);

        params.put(Const.Params.ID, new PreferenceHelper(mainActivity).getUserId());

        params.put(Const.Params.TOKEN, new PreferenceHelper(mainActivity).getSessionToken());

        params.put("old_password", String.valueOf(old_password));

        params.put("password", String.valueOf(new_password));

        params.put("confirm_password", String.valueOf(new_pre_password));

        Log.d("DAT_CHANGE_PASSWORD", params + "");

        new VolleyRequester(mainActivity, Const.POST, params, Const.ServiceCode.CHANGE_PASSWORD, this);


    }


    @Override
    public void onTaskCompleted(String response, int serviceCode) {

        switch (serviceCode) {

            case Const.ServiceCode.CHANGE_PASSWORD:

                EbizworldUtils.appLogDebug("DAT_CHANGE_PASSWORD",response + "");

                if(response!=null){

                    EbizworldUtils.removeProgressDialog();
                    try {

                        JSONObject jsonObject = new JSONObject(response);

                        if(jsonObject.getBoolean("success")){

                            if(jsonObject.has("message")){

                                showAlertDialog(jsonObject.getString("message"));

                            }

                        }else {

                            if(jsonObject.has("error")){

                                Toast.makeText(mainActivity, ""+jsonObject.getString("error"), Toast.LENGTH_LONG).show();

                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        EbizworldUtils.appLogDebug("DAT_CHANGE_PASSWORD",e.toString());
                    }


                }else {

                    EbizworldUtils.removeProgressDialog();
                    Toast.makeText(mainActivity, "Faild", Toast.LENGTH_SHORT).show();

                }

                break;
        }
    }

    private void showAlertDialog(String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
       // builder.setTitle(message);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setNegativeButton(mainActivity.getResources().getString(R.string.txt_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                mainActivity.addFragment(new AccountFragment(), true, Const.ACCOUNT_FRAGMENT, true);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }
}
