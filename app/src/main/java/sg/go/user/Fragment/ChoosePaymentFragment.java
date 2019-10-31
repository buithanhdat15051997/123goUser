package sg.go.user.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import sg.go.user.HttpRequester.VolleyRequester;
import sg.go.user.Interface.AsyncTaskCompleteListener;
import sg.go.user.MainActivity;
import sg.go.user.R;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.EbizworldUtils;
import sg.go.user.Utils.PreferenceHelper;

public class ChoosePaymentFragment extends Fragment implements AsyncTaskCompleteListener {

    private MainActivity activity;
    private View view;
    private RadioGroup radioGroup_Choose_payment;
    private TextView txtShow_Payment_Selected;
    private RadioButton radioBtn_Payment_Cash, radioBtn_Payment_Ewallet, radioBtn_Payment_Paypal, radioBtn_Payment_None;
    private Button btn_choose_payment;
    private ImageView img_choose_payment_back;

    private ArrayList<String> arrayTypePayment;


    private int possiton = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_choose_payment, container, false);

        arrayTypePayment = new ArrayList<>();

        arrayTypePayment.add(getResources().getString(R.string.not_available));
        arrayTypePayment.add(getResources().getString(R.string.txt_pay_cash));
        arrayTypePayment.add(getResources().getString(R.string.txt_pay_wallet));
        arrayTypePayment.add(getResources().getString(R.string.txt_pay_paypal));

        radioGroup_Choose_payment = view.findViewById(R.id.radioGroup_Choose_payment);

        txtShow_Payment_Selected = view.findViewById(R.id.txtShow_Payment_Selected);

        radioBtn_Payment_Cash = view.findViewById(R.id.radioBtn_Payment_Cash);

        radioBtn_Payment_Ewallet = view.findViewById(R.id.radioBtn_Payment_Ewallet);

        radioBtn_Payment_Paypal = view.findViewById(R.id.radioBtn_Payment_Paypal);

        radioBtn_Payment_None = view.findViewById(R.id.radioBtn_Payment_None);


        btn_choose_payment = view.findViewById(R.id.btn_choose_payment);

        img_choose_payment_back = view.findViewById(R.id.img_choose_payment_back);

        GetTypePayment();


        img_choose_payment_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                activity.addFragment(new AccountFragment(), true, Const.ACCOUNT_FRAGMENT, false);

            }
        });


        radioGroup_Choose_payment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                switch (i) {
                    case R.id.radioBtn_Payment_None:

                        btn_choose_payment.setText(getResources().getString(R.string.btn_confirm) + " " + getResources().getString(R.string.txt_none));
                        possiton = 0;

                        break;

                    case R.id.radioBtn_Payment_Cash:

                        btn_choose_payment.setText(getResources().getString(R.string.btn_confirm) + " " + getResources().getString(R.string.txt_pay_cash));
                        possiton = 1;
                        break;

                    case R.id.radioBtn_Payment_Ewallet:

                        btn_choose_payment.setText(getResources().getString(R.string.btn_confirm) + " " + getResources().getString(R.string.txt_pay_wallet));
                        possiton = 2;
                        break;

                    case R.id.radioBtn_Payment_Paypal:

                        btn_choose_payment.setText(getResources().getString(R.string.btn_confirm) + " " + getResources().getString(R.string.txt_pay_paypal));
                        possiton = 3;
                        break;

                }

            }
        });

        btn_choose_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (radioBtn_Payment_None.isChecked() || radioBtn_Payment_Cash.isChecked() || radioBtn_Payment_Ewallet.isChecked() || radioBtn_Payment_Paypal.isChecked()) {

                    SendChooseAPayment(possiton);

                    Log.d("DAT_CHOOSE_PAYMENT", "" + possiton);

                } else {

                    Toast.makeText(activity, activity.getResources().getString(R.string.txt_pls_choose_payment_type), Toast.LENGTH_SHORT).show();

                }

            }
        });

        return view;
    }

    private void GetTypePayment() {

        EbizworldUtils.showSimpleProgressDialog(activity, getResources().getString(R.string.txt_load), true);

        HashMap<String, String> params = new HashMap<>();

        params.put(Const.Params.URL, Const.ServiceType.GET_TYPE_CHOOSE_PAYMENT);

        params.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());

        params.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());

        Log.d("DAT_CHOOSE_PAYMENT", params + "");


        new VolleyRequester(activity, Const.POST, params, Const.ServiceCode.GET_TYPE_CHOOSE_PAYMENT, this);

    }

    private void SendChooseAPayment(int possiton) {

        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        EbizworldUtils.showSimpleProgressDialog(activity, getResources().getString(R.string.txt_load), true);


        HashMap<String, String> params = new HashMap<>();

        params.put(Const.Params.URL, Const.ServiceType.CHOOSE_PAYMENT_TYPE);

        params.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());

        params.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());

        params.put(Const.Params.CHOOSE_PAYMENT_SELECT_TYPE, String.valueOf(possiton));


        Log.d("DAT_CHOOSE_PAYMENT", params + "");


        new VolleyRequester(activity, Const.POST, params, Const.ServiceCode.CHOOSE_A_PAYMENT, this);


    }


    @Override
    public void onTaskCompleted(String response, int serviceCode) {

        switch (serviceCode) {

            case Const.ServiceCode.GET_TYPE_CHOOSE_PAYMENT:

                Log.d("DAT_CHOOSE_PAYMENT", response + "");

                if (response != null) {

                    EbizworldUtils.removeProgressDialog();

                    try {

                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getBoolean("success")) {

                            int typePayment = jsonObject.getInt("selected_type");

                            EbizworldUtils.appLogDebug("DAT_CHOOSE_PAYMENT", String.valueOf(typePayment));

                            switch (typePayment) {
                                case 0:
                                    txtShow_Payment_Selected.setText(getResources().getString(R.string.txt_selected_type) + ": " + arrayTypePayment.get(0));
                                    radioBtn_Payment_None.setChecked(true);
                                    break;

                                case 1:

                                    txtShow_Payment_Selected.setText(getResources().getString(R.string.txt_selected_type) + ": " + arrayTypePayment.get(1));
                                    radioBtn_Payment_Cash.setChecked(true);
                                    break;

                                case 2:

                                    txtShow_Payment_Selected.setText(getResources().getString(R.string.txt_selected_type) + ": " + arrayTypePayment.get(2));
                                    radioBtn_Payment_Ewallet.setChecked(true);
                                    break;

                                case 3:

                                    txtShow_Payment_Selected.setText(getResources().getString(R.string.txt_selected_type) + ": " + arrayTypePayment.get(3));
                                    radioBtn_Payment_Paypal.setChecked(true);
                                    break;
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                break;


            case Const.ServiceCode.CHOOSE_A_PAYMENT:

                Log.d("DAT_CHOOSE_PAYMENT", response + "");

                if (response != null) {

                    EbizworldUtils.removeProgressDialog();

                    try {

                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getBoolean("success")) {

                            GetTypePayment();

                            AlertDialog.Builder builder = new AlertDialog.Builder(activity);

                            builder.setTitle("Selected Successfully");

                            builder.setCancelable(true);

                            builder.setNegativeButton(getResources().getString(R.string.txt_btn_done), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    dialogInterface.dismiss();
                                }
                            });

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();

                        } else {

                            EbizworldUtils.showLongToast("error", activity);
                            EbizworldUtils.removeProgressDialog();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {

                    EbizworldUtils.removeProgressDialog();

                }
                break;

        }

    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        possiton = 0;

    }
}
