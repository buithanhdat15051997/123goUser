package sg.go.user.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.bumptech.glide.Glide;
import com.skyfishjy.library.RippleBackground;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import sg.go.user.DatabaseHandler;
import sg.go.user.HttpRequester.VolleyRequester;
import sg.go.user.Interface.AsyncTaskCompleteListener;
import sg.go.user.MainActivity;
import sg.go.user.Models.RequestDetail;
import sg.go.user.Models.RequestOptional;
import sg.go.user.R;
import sg.go.user.Utils.Commonutils;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.EbizworldUtils;
import sg.go.user.Utils.ParseContent;
import sg.go.user.Utils.PreferenceHelper;

public class BillingInfoFragment extends Fragment implements AsyncTaskCompleteListener, View.OnClickListener {

    @BindView(R.id.tv_billing_info_notice)
    TextView mTv_billing_info_notice;

    @BindView(R.id.tv_billing_info_total_price)
    TextView mTv_billing_info_total_price;

    // distance
    @BindView(R.id.tv_billing_info_kilo_distance3)
    TextView tv_billing_info_kilo_distance3;

    @BindView(R.id.tv_billing_info_kilo_dola2)
    TextView tv_billing_info_kilo_dola2;


    @BindView(R.id.btn_pay_bycash)
    Button mBtn_pay_bycash;

    @BindView(R.id.btn_pay_bypaypal)
    Button mBtn_pay_bypaypal;

//    @BindView(R.id.btn_pay_wallet_demo)
//    Button btn_pay_wallet_demo;

//    @BindView(R.id.tv_billing_info_imh_value)
//    TextView mTv_billing_info_imh_value;

//    @BindView(R.id.tv_billing_info_ferry_terminals_value)
//    TextView mTv_billing_info_ferry_terminals_value;

//    @BindView(R.id.tv_billing_info_staircase_value)
//    TextView mTv_billing_info_staircase_value;

//    @BindView(R.id.tv_billing_info_tarmac_value)
//    TextView mTv_billing_info_tarmac_value;

    @BindView(R.id.tv_billing_info_table_price_content_warning)
    TextView tv_billing_info_table_price_content_warning;

    @BindView(R.id.img_billing_info_img_typeCar)
    ImageView img_billing_info_img_typeCar;

    @BindView(R.id.tv_billing_info_name_typeCar)
    TextView mTv_billing_info_name_typeCar;


//    @BindView(R.id.tv_billing_info_pickup_type_value)
//    TextView mTv_billing_info_pickup_type_value;

    //kiểu của bạn hào
    @BindView(R.id.billing_info_payment_group)
    LinearLayout billing_info_payment_group;

    @BindView(R.id.billing_info_table_price_notice_group)
    LinearLayout billing_info_table_price_notice_group;

    @BindView(R.id.billing_info_request_group)
    LinearLayout billing_info_request_group;

    @BindView(R.id.tv_billing_info_confirm)
    TextView tv_billing_info_confirm;

    @BindView(R.id.tv_billing_info_deny)
    TextView tv_billing_info_deny;


    private View mView;
    private TextView custom_simple_title_paymentwallet, custom_simple_content_paymentwallet, btn_yes1_paymentwallet, btn_no_paymentwallet;
    private MainActivity activity;
    private DatabaseHandler mDatabaseHandler;
    private RequestDetail mRequestDetail;
    private RequestOptional mRequestOptional;
    private Dialog requestDialog;
    private TextView cancel_req_create;
    private Handler checkRequestStatusHandler;
    private Button btn_pay_wallet_demo1;
    private ImageView img_logo_hide_billinginfo;
    private int Total_billing;
    private Dialog dialog_payment_wallet;
    private String TotalMoney;


    private Runnable requestStatusCheckRunnable = new Runnable() {

        public void run() {

            checkreqstatus();
            checkRequestStatusHandler.postDelayed(this, 10000);

        }

    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (MainActivity) getActivity();

        mDatabaseHandler = new DatabaseHandler(activity);

        if (mDatabaseHandler != null) {

            mDatabaseHandler.DeleteChat(String.valueOf(new PreferenceHelper(activity).getRequestId())); //Delete chat from Request ID

        }

        if (getArguments() != null) {

            mRequestOptional = getArguments().getParcelable(Const.Params.REQUEST_OPTIONAL);

        }

        checkRequestStatusHandler = new Handler();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = inflater.inflate(R.layout.fragment_billing_info, container, false);
        ButterKnife.bind(this, mView);


        btn_pay_wallet_demo1 = mView.findViewById(R.id.btn_pay_wallet_demo1);
        img_logo_hide_billinginfo = mView.findViewById(R.id.img_logo_hide_billinginfo);

//        mRequestOptional.setRemark(tv_billing_info_table_price_content_warning.getText().toString());

        if (mRequestOptional != null) {

            mTv_billing_info_notice.setVisibility(View.GONE);
            billing_info_payment_group.setVisibility(View.GONE);
            billing_info_table_price_notice_group.setVisibility(View.VISIBLE);
            billing_info_request_group.setVisibility(View.VISIBLE);


            /*---- SET NAME - IMAGE - DISTANCE - TIME ----*/
            tv_billing_info_kilo_distance3.setText(mRequestOptional.getKm_send_billinginfo() + " km");
            tv_billing_info_kilo_dola2.setText(mRequestOptional.getTime_send_billinginfo());

            mTv_billing_info_name_typeCar.setText(new PreferenceHelper(activity).getTypeCarBillingInfo());
            Glide.with(activity).load(new PreferenceHelper(activity).getImageTypeCarBillingInfo()).into(img_billing_info_img_typeCar);

            getBillingInfo(mRequestOptional);

        } else {

            /*---- SET NAME - IMAGE  TYPE CAR ----*/
            mTv_billing_info_name_typeCar.setText(new PreferenceHelper(activity).getTypeCarBillingInfo());
            Glide.with(activity).load(new PreferenceHelper(activity).getImageTypeCarBillingInfo()).into(img_billing_info_img_typeCar);
            mTv_billing_info_notice.setVisibility(View.VISIBLE);
            billing_info_payment_group.setVisibility(View.VISIBLE);
            img_logo_hide_billinginfo.setVisibility(View.VISIBLE);
            billing_info_table_price_notice_group.setVisibility(View.GONE);
            billing_info_request_group.setVisibility(View.GONE);

            /*---- SET NAME - IMAGE - DISTANCE - TIME ----*/
            tv_billing_info_kilo_distance3.setText(new PreferenceHelper(activity).getDistanceBillingInfo() + " km");
            tv_billing_info_kilo_dola2.setText(new PreferenceHelper(activity).getTimeBillingInfo());

            checkreqstatus(); //Get check status of request

        }

        btn_pay_wallet_demo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//                builder.setTitle("Payment with Wallet");
//                builder.setMessage("YOUR");
//                builder.setCancelable(false);
//                builder.setPositiveButton("COUTINUE TO PAYMENT", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Toast.makeText(activity, "Không thoát được", Toast.LENGTH_SHORT).show();
//                    }
//                });
//                builder.setNegativeButton("Đăng xuất", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                    }
//                });
//                AlertDialog alertDialog = builder.create();
//                alertDialog.show();
                dialog_payment_wallet = new Dialog(activity);

                dialog_payment_wallet.setContentView(R.layout.dialog_payment_with_wallet);

                custom_simple_title_paymentwallet = dialog_payment_wallet.findViewById(R.id.custom_simple_title_paymentwallet);

                custom_simple_content_paymentwallet = dialog_payment_wallet.findViewById(R.id.custom_simple_content_paymentwallet);

                btn_yes1_paymentwallet = dialog_payment_wallet.findViewById(R.id.btn_yes1_paymentwallet);

                btn_no_paymentwallet = dialog_payment_wallet.findViewById(R.id.btn_no_paymentwallet);

                dialog_payment_wallet.show();

                custom_simple_title_paymentwallet.setText(getResources().getString(R.string.txt_dialog_pay_ewallet));

                custom_simple_content_paymentwallet.setText(getResources().getString(R.string.txt_content_pay_ewallet));

                btn_yes1_paymentwallet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        getPaymentWallet();
                    }
                });

                btn_no_paymentwallet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialog_payment_wallet.dismiss();

                    }
                });


            }
        });
        mBtn_pay_bycash.setOnClickListener(this);
        mBtn_pay_bypaypal.setOnClickListener(this);

        tv_billing_info_confirm.setOnClickListener(this);
        tv_billing_info_deny.setOnClickListener(this);

        return mView;
    }

    private void getPaymentWallet() {

        if (!EbizworldUtils.isNetworkAvailable(getActivity())) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        Commonutils.progressdialog_show(activity, "Loading...");

        HashMap<String, String> map = new HashMap<>();

        map.put(Const.Params.URL, Const.ServiceType.PAYMENT_WALLLET);

        map.put(Const.Params.ID, new PreferenceHelper(getActivity()).getUserId());

        map.put(Const.Params.REQUEST_ID, String.valueOf(new PreferenceHelper(getActivity()).getRequestId()));

        map.put(Const.Params.TOKEN, new PreferenceHelper(getActivity()).getSessionToken());

        map.put(Const.Params.AMOUNT, new PreferenceHelper(getActivity()).getTotalPaymentWallet().toString());

        //  map.put("amount", mRequestOptional.getTotal_money_price().toString());
        //  map.put(Const.Params.REQUEST_ID, String.valueOf(new PreferenceHelper(getActivity()).getRequestId()));
        EbizworldUtils.appLogDebug("HaoLS", "getPaymentByWallet: " + map.toString());

        new VolleyRequester(getActivity(), Const.POST, map, Const.ServiceCode.PAYMENT_WALLET, this);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (requestDialog != null && requestDialog.isShowing()) {

            cancel_create_req();

            requestDialog.dismiss();

        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_pay_bycash: {

                getPaymentByCash();

            }
            break;

            case R.id.btn_pay_bypaypal: {

                getBrainTreeClientToken();

            }

            break;

            case R.id.tv_billing_info_confirm: {

                new PreferenceHelper(activity).putOverViewPolyline(SearchPlaceFragment.OverView_Polyline_Home.toString());

                // Log.d("Dat_Preference",new PreferenceHelper(activity).putOverViewPolyline(RequestMapFragment.OverView_Polyline);

                if (mRequestOptional != null) {

                    RequestAmbulance(mRequestOptional);

                }
            }
            break;

            case R.id.tv_billing_info_deny: {
                // back fragment
                getActivity().onBackPressed();
//                activity.addFragment(new SearchPlaceFragment(), true, Const.SEARCH_FRAGMENT, true);
                new RequestOptional().setOverView_Polyline("");
            }
            break;
        }
    }

    private void DialogPaymentWithWallet() {
        Log.d("dat_test", "vao day");

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        final AlertDialog OptionDialog = alertDialog.create();

        alertDialog.setTitle("Payment With Your Wallet");
        alertDialog.setMessage("");
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                OptionDialog.dismiss();
            }
        });

        OptionDialog.show();

    }


    /*---- GET PAYPAL  ----*/

    private void getBrainTreeClientToken() {

        if (!EbizworldUtils.isNetworkAvailable(getActivity())) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        HashMap<String, String> map = new HashMap<>();

        if (new PreferenceHelper(activity).getLoginType().equals(Const.PatientService.PATIENT)) {

            map.put(Const.Params.URL, Const.ServiceType.GET_BRAIN_TREE_TOKEN_URL);

        } else if (new PreferenceHelper(activity).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)) {

            map.put(Const.Params.URL, Const.NursingHomeService.GET_BRAINTREE_TOKEN_URL);

        } else if (new PreferenceHelper(activity).getLoginType().equals(Const.HospitalService.HOSPITAL)) {

            map.put(Const.Params.URL, Const.HospitalService.GET_BRAINTREE_TOKEN_URL);

        }

        map.put(Const.Params.ID, new PreferenceHelper(getActivity()).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(getActivity()).getSessionToken());

        EbizworldUtils.appLogDebug("HaoLS", "BrainTreeClientTokenMap: " + map.toString());

        new VolleyRequester(getActivity(), Const.POST, map, Const.ServiceCode.GET_BRAIN_TREE_TOKEN_URL, this);

    }

// ---------------GUI  BILLINFO-------------------

    private void getBillingInfo(RequestOptional requestOptional) {
        /*--- Check Internet ---*/

        if (!EbizworldUtils.isNetworkAvailable(getActivity())) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Const.Params.URL, Const.ServiceType.BILLING_INFO);
        hashMap.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        hashMap.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());
        hashMap.put(Const.Params.SERVICE_TYPE_CAR, String.valueOf(mRequestOptional.getOperator_id()));
//        hashMap.put(Const.Params.A_AND_E, String.valueOf(requestOptional.getA_and_e()));
//        hashMap.put(Const.Params.IMH, String.valueOf(requestOptional.getImh()));
//        hashMap.put(Const.Params.FERRY_TERMINALS, String.valueOf(requestOptional.getFerry_terminals()));
//        hashMap.put(Const.Params.STAIRCASE, String.valueOf(requestOptional.getStaircase()));
//        hashMap.put(Const.Params.TARMAC, String.valueOf(requestOptional.getTarmac()));
//        hashMap.put(Const.Params.WEIGHT, String.valueOf(requestOptional.getWeight()));
//        hashMap.put(Const.Params.OXYGEN, String.valueOf(requestOptional.getOxygen()));
//        hashMap.put(Const.Params.CASE_TYPE, String.valueOf(requestOptional.getCaseType()));
        hashMap.put(Const.Params.SERVICE_KILOMET, String.valueOf(mRequestOptional.getKm_send_billinginfo()));

        EbizworldUtils.appLogDebug("HaoLS", "Get Billing info: " + hashMap.toString());

        new VolleyRequester(activity, Const.POST, hashMap, Const.ServiceCode.BILLING_INFO, this);
    }

    /*--- GET PAYPAL ---*/

    private void postNonceToServer(String nonce) {

        if (!EbizworldUtils.isNetworkAvailable(getActivity())) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;

        }

        Commonutils.progressdialog_show(activity, "Loading...");
        HashMap<String, String> map = new HashMap<>();

//        if (new PreferenceHelper(activity).getLoginType().equals(Const.PatientService.PATIENT)){

        map.put(Const.Params.URL, Const.ServiceType.POST_PAYPAL_NONCE_URL);

//        }else if (new PreferenceHelper(activity).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)){
//
//            map.put(Const.Params.URL, Const.NursingHomeService.POST_PAYPAL_NONCE_URL);
//
//        }else if (new PreferenceHelper(activity).getLoginType().equals(Const.HospitalService.HOSPITAL)){
//
//            map.put(Const.Params.URL, Const.HospitalService.POST_PAYPAL_NONCE_URL);
//
//        }

        map.put(Const.Params.ID, new PreferenceHelper(getActivity()).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(getActivity()).getSessionToken());
        map.put(Const.Params.REQUEST_ID, String.valueOf(new PreferenceHelper(getActivity()).getRequestId()));
        map.put(Const.Params.PAYMENT_METHOD_NONCE, nonce);

        EbizworldUtils.appLogDebug("HaoLS", "postNonceToServer: " + map.toString());

        new VolleyRequester(getActivity(), Const.POST, map, Const.ServiceCode.POST_PAYPAL_NONCE, this);

    }

    private void getPaymentByCash() {

        if (!EbizworldUtils.isNetworkAvailable(getActivity())) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        Commonutils.progressdialog_show(activity, "Loading...");
        HashMap<String, String> map = new HashMap<>();

        if (new PreferenceHelper(activity).getLoginType().equals(Const.PatientService.PATIENT)) {

            map.put(Const.Params.URL, Const.ServiceType.PAYMENT_CASH);

        } else if (new PreferenceHelper(activity).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)) {

            map.put(Const.Params.URL, Const.NursingHomeService.PAYMENT_BY_CASH_URL);

        } else if (new PreferenceHelper(activity).getLoginType().equals(Const.HospitalService.HOSPITAL)) {

            map.put(Const.Params.URL, Const.HospitalService.PAYMENT_BY_CASH_URL);

        }

        map.put(Const.Params.ID, new PreferenceHelper(getActivity()).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(getActivity()).getSessionToken());
        map.put(Const.Params.REQUEST_ID, String.valueOf(new PreferenceHelper(getActivity()).getRequestId()));

        EbizworldUtils.appLogDebug("HaoLS", "getPaymentByCash: " + map.toString());

        new VolleyRequester(getActivity(), Const.POST, map, Const.ServiceCode.PAYMENT_CASH, this);
    }


    private void checkreqstatus() {

        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        HashMap<String, String> map = new HashMap<String, String>();

        if (new PreferenceHelper(activity).getLoginType().equals(Const.PatientService.PATIENT)) {

            map.put(Const.Params.URL, Const.ServiceType.CHECKREQUEST_STATUS);

        } else if (new PreferenceHelper(activity).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)) {

            map.put(Const.Params.URL, Const.NursingHomeService.REQUEST_STATUS_CHECK_URL);

        } else if (new PreferenceHelper(activity).getLoginType().equals(Const.HospitalService.HOSPITAL)) {

            map.put(Const.Params.URL, Const.HospitalService.CHECK_REQUEST_STATUS_URL);

        }
        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());
        EbizworldUtils.appLogDebug("HaoLS", map.toString());
        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.CHECKREQUEST_STATUS,
                this);
    }

    private void startGetRequestStatus() {
        startCheckstatusTimer();
    }

    private void startCheckstatusTimer() {

        checkRequestStatusHandler.postDelayed(requestStatusCheckRunnable, 10000);

    }

    private void stopCheckingforstatus() {

        if (checkRequestStatusHandler != null) {

            checkRequestStatusHandler.removeCallbacks(requestStatusCheckRunnable);

            Log.d("HaoLS", "stop status handler");
        }
    }

    // ---------------GUI  REQUEST AMBULANCE -------------------
    private void RequestAmbulance(RequestOptional requestOptional) {

        String REMARK1 = tv_billing_info_table_price_content_warning.getText().toString();

        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        showreqloader();
//        startGetRequestStatus();
        HashMap<String, String> map = new HashMap<String, String>();


        map.put(Const.Params.URL, Const.ServiceType.REQUEST_AMBULANCE);
        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());

        map.put(Const.Params.S_LATITUDE, String.valueOf(requestOptional.getPic_lat()));
        map.put(Const.Params.S_LONGITUDE, String.valueOf(requestOptional.getPic_lng()));

        map.put(Const.Params.D_LONGITUDE, String.valueOf(requestOptional.getDrop_lng()));
        map.put(Const.Params.D_LATITUDE, String.valueOf(requestOptional.getDrop_lat()));

//        map.put(Const.Params.IS_ADSTOP, String.valueOf(requestOptional.getIsAddStop()));
//        map.put(Const.Params.ADSTOP_LONGITUDE, String.valueOf(requestOptional.getAddStop_lng()));
//        map.put(Const.Params.ADSTOP_LATITUDE, String.valueOf(requestOptional.getAddStop_lat()));
//        map.put(Const.Params.ADSTOP_ADDRESS, requestOptional.getAddStop_address());
//new PreferenceHelper(getActivity()).getRequestType()

        //
        if (requestOptional.getOperator_id() != 0) {
            map.put(Const.Params.SERVICE_TYPE, String.valueOf(requestOptional.getOperator_id()));
        } else {
            map.put(Const.Params.SERVICE_TYPE, new PreferenceHelper(getActivity()).getRequestType());
        }


        map.put(Const.Params.S_ADDRESS, requestOptional.getPic_address());
        map.put(Const.Params.D_ADDRESS, requestOptional.getDrop_address());

        map.put(Const.Params.REQ_STATUS_TYPE, String.valueOf(requestOptional.getRequest_status_type()));
        map.put(Const.Params.PROMOCODE, requestOptional.getPromoCode());

        if (REMARK1.isEmpty() || REMARK1.equals("")) {
            map.put(Const.Params.REMARK, requestOptional.getRemark());
        } else {
            map.put(Const.Params.REMARK, REMARK1);
        }


//        map.put(Const.Params.A_AND_E,String.valueOf(requestOptional.getA_and_e()));
//        map.put(Const.Params.IMH,String.valueOf(requestOptional.getImh()));
//        map.put(Const.Params.FERRY_TERMINALS,String.valueOf(requestOptional.getFerry_terminals()));
//        map.put(Const.Params.STAIRCASE,String.valueOf(requestOptional.getStaircase()));
//        map.put(Const.Params.TARMAC,String.valueOf(requestOptional.getTarmac()));
//        map.put(Const.Params.FAMILY_MEMBER, String.valueOf(requestOptional.getFamily_member()));
//        map.put(Const.Params.HOUSE_UNIT, requestOptional.getHouseUnit());
//        map.put(Const.Params.WEIGHT, String.valueOf(requestOptional.getWeight()));
//        map.put(Const.Params.OXYGEN,String.valueOf(requestOptional.getOxygen()));
//        map.put(Const.Params.CASE_TYPE, String.valueOf(requestOptional.getCaseType()));


//        map.put("overview_polyline", String.valueOf(requestOptional.getOverView_Polyline()));
        map.put("overview_polyline", new PreferenceHelper(activity).getOverViewPolyline());

        map.put("km", requestOptional.getKm_send_billinginfo().toString());

        Log.d("GetRequest_Diver", "Request ambulance: " + map.toString());
        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.REQUEST_AMBULANCE,
                this);
    }

    private void cancel_create_req() {
        HashMap<String, String> map = new HashMap<String, String>();

        if (new PreferenceHelper(activity).getLoginType().equals(Const.PatientService.PATIENT)) {

            map.put(Const.Params.URL, Const.ServiceType.CANCEL_CREATE_REQUEST);

        } else if (new PreferenceHelper(activity).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)) {

            map.put(Const.Params.URL, Const.NursingHomeService.WAITING_CANCEL_REQUEST_URL);

        } else if (new PreferenceHelper(activity).getLoginType().equals(Const.HospitalService.HOSPITAL)) {

            map.put(Const.Params.URL, Const.HospitalService.WAITING_CANCEL_REQUEST_URL);

        }
        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());
        map.put(Const.Params.REQUEST_ID, String.valueOf(new PreferenceHelper(activity).getRequestId()));

        EbizworldUtils.appLogDebug("HaoLS", "Cancel request: " + map.toString());
        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.CANCEL_CREATE_REQUEST, this);
    }

    private void showreqloader() {

        requestDialog = new Dialog(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        requestDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent_black)));
        requestDialog.setCancelable(false);
        requestDialog.setContentView(R.layout.request_loading);

        final RippleBackground rippleBackground = (RippleBackground) requestDialog.findViewById(R.id.content);
        cancel_req_create = (TextView) requestDialog.findViewById(R.id.cancel_req_create);
        final TextView req_status = (TextView) requestDialog.findViewById(R.id.req_status);
        rippleBackground.startRippleAnimation();

        cancel_req_create.setEnabled(false);
        cancel_req_create.setBackgroundColor(getResources().getColor(R.color.lightblue100));
        cancel_req_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                req_status.setText(getResources().getString(R.string.txt_canceling_req));

                cancel_create_req();

                new PreferenceHelper(activity).clearRequestData();

                stopCheckingforstatus();

            }
        });


        requestDialog.show();
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {

        switch (serviceCode) {
            case Const.ServiceCode.PAYMENT_WALLET: {

                EbizworldUtils.appLogInfo("BTD", "payment_wallet: " + response);
                Commonutils.progressdialog_hide();

                if (response != null) {
                    EbizworldUtils.appLogInfo("BTD", "payment_wallet: " + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getBoolean("success")) {

                            dialog_payment_wallet.dismiss();

                            if (!activity.currentFragment.equals(Const.RATING_FRAGMENT) && !activity.isFinishing()) {

                                EbizworldUtils.appLogInfo("BTD", "payment_wallet: " + response);

                                Bundle bundle = new Bundle();
                                RatingFragment ratingFragment = new RatingFragment();
                                bundle.putSerializable(Const.REQUEST_DETAIL, mRequestDetail);
                                ratingFragment.setArguments(bundle);
                                activity.addFragment(ratingFragment, false, Const.RATING_FRAGMENT, true);

                            }


                        } else if (jsonObject.getBoolean("success") == false) {

                            EbizworldUtils.showLongToast("Payment with Wallet Failed", activity);
                            dialog_payment_wallet.dismiss();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        EbizworldUtils.appLogError("HaoLS", "Get billing info failed: " + e.toString());
                    }

                } else {

                    EbizworldUtils.showLongToast("Payment with Wallet Failed", activity);
                    dialog_payment_wallet.dismiss();
                }


            }

            break;

            case Const.ServiceCode.BILLING_INFO: {

                EbizworldUtils.appLogInfo("HaoLS", "Billing info: " + response);
                Commonutils.progressdialog_hide();

                try {
                    JSONObject object = new JSONObject(response);

                    if (object.getString("success").equals("true")) {

                        JSONObject jsonObject = object.getJSONObject(Const.Params.BILLING_INFO);
                        Log.d("DatBilling", String.valueOf(jsonObject));
//                        if (jsonObject.has(Const.Params.A_AND_E)){
//
//                            mTv_billing_info_a_and_e_value.setText(jsonObject.getString("currency") + " " + jsonObject.getString(Const.Params.A_AND_E));
//                        }
//
//                        if (jsonObject.has(Const.Params.IMH)){
//
//                            mTv_billing_info_imh_value.setText(jsonObject.getString("currency") + " " + jsonObject.getString(Const.Params.IMH));
//                        }
//
//                        if (jsonObject.has(Const.Params.FERRY_TERMINALS)){
//
//                            mTv_billing_info_ferry_terminals_value.setText(jsonObject.getString("currency") + " " + jsonObject.getString(Const.Params.FERRY_TERMINALS));
//                        }
//
//                        if (jsonObject.has(Const.Params.STAIRCASE)){
//
//                            mTv_billing_info_staircase_value.setText(jsonObject.getString("currency") + " " + jsonObject.getString(Const.Params.STAIRCASE));
//                        }
//
//                        if (jsonObject.has(Const.Params.TARMAC)){
//
//                            mTv_billing_info_tarmac_value.setText(jsonObject.getString("currency") + " " + jsonObject.getString(Const.Params.TARMAC));
//                        }
//
//                        if (jsonObject.has(Const.Params.WEIGHT)){
//
//                            mTv_billing_info_weight_value.setText(jsonObject.getString("currency") + " " + jsonObject.getString(Const.Params.WEIGHT));
//                        }
//
//                        if (jsonObject.has(Const.Params.OXYGEN)){
//
//                            mTv_billing_info_oxygen_tank_value.setText(jsonObject.getString("currency") + " " + jsonObject.getString(Const.Params.OXYGEN));
//                        }
//
//                        if (jsonObject.has(Const.Params.CASE_TYPE)){
//
//                            mTv_billing_info_pickup_type_value.setText(jsonObject.getString("currency") + " " + jsonObject.getString(Const.Params.CASE_TYPE));
//                        }
                        if (jsonObject.has(Const.Params.TOTAL)) {

                            mTv_billing_info_total_price.setText(jsonObject.getString("currency") + " " + jsonObject.getString(Const.Params.TOTAL));

                            new PreferenceHelper(activity).putTotalPaymentWallet(jsonObject.getString(Const.Params.TOTAL));

                            //  mRequestOptional.setTotal_money_price(jsonObject.getString("currency") + " " + jsonObject.getString(Const.Params.TOTAL));


                        }

                    } else {
                        Toast.makeText(activity, "Fail BillingInfo", Toast.LENGTH_SHORT).show();
//                        activity.addFragment(new HomeMapFragment(), false, Const.HOME_MAP_FRAGMENT, true);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    EbizworldUtils.appLogError("HaoLS", "Get billing info failed: " + e.toString());

//                    activity.addFragment(new HomeMapFragment(), false, Const.HOME_MAP_FRAGMENT, true);
                }

            }
            break;

            case Const.ServiceCode.REQUEST_AMBULANCE:
                EbizworldUtils.appLogInfo("HaoLS", "create req_response succeeded: " + response);

                if (response != null) {

                    Log.d("DatCheckReponse", response.toString());

                    try {
                        JSONObject job1 = new JSONObject(response);
                        if (job1.getString("success").equals("true")) {

                            new PreferenceHelper(activity).putRequestId(Integer.parseInt(job1.getString("request_id")));
                            startGetRequestStatus();

                            if (requestDialog != null && requestDialog.isShowing()) {

                                if (cancel_req_create != null) {

                                    cancel_req_create.setEnabled(true);
                                    cancel_req_create.setBackgroundColor(getResources().getColor(R.color.color_background_main));

                                }

                            }

                        }
//                         else if (job1.getString("success").equals("false")) {
//
//                            if (job1.getString("error_code").equalsIgnoreCase("166")) {
//                                Intent payment = new Intent(activity, AddPaymentFragment.class);
//                                startActivity(payment);
//                            }
//
//                        }
                        else {

                            // startgetProvider();
                            if (requestDialog != null && requestDialog.isShowing()) {
                                requestDialog.dismiss();
                                stopCheckingforstatus();
                            }
                            String error = job1.getString("error");
                            Commonutils.showtoast(error, activity);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        EbizworldUtils.appLogError("HaoLS", "create req_response failed: " + e.toString());
                    }
                }
                break;

            case Const.ServiceCode.GET_BRAIN_TREE_TOKEN_URL: {
                EbizworldUtils.appLogInfo("HaoLS", "GET_BRAIN_TREE_TOKEN_URL: " + response);

                if (response != null && activity != null) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getString("success").equals("true")) {

                            EbizworldUtils.showShortToast("Success " + jsonObject.getString("success"), activity);

                            if (jsonObject.has("client_token")) {

                                DropInRequest dropInRequest = new DropInRequest()
                                        .clientToken(jsonObject.getString("client_token"));

                                startActivityForResult(dropInRequest.getIntent(activity), Const.ServiceCode.REQUEST_PAYPAL);
                            }

                        } else if (jsonObject.getString("success").equals("false")) {

                            if (jsonObject.has("error")) {

                                EbizworldUtils.showShortToast(jsonObject.getString("error"), activity);

                            }

                        }

                    } catch (JSONException e) {

                        e.printStackTrace();
                        EbizworldUtils.appLogError("HaoLS", "GET_BRAIN_TREE_TOKEN_URL: " + e.toString());

                    }


                }

            }
            break;

            case Const.ServiceCode.CANCEL_CREATE_REQUEST:

                EbizworldUtils.appLogInfo("HaoLS", "cancel req_response " + response);

                new PreferenceHelper(activity).putRequestId(-1);

                if (requestDialog != null && requestDialog.isShowing()) {

                    requestDialog.dismiss();

                }
                break;

            case Const.ServiceCode.CHECKREQUEST_STATUS: {

                EbizworldUtils.appLogInfo("HaoLS", "check req status: " + response);

                if (response != null) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getString("success").equals("true")) {

                            mRequestDetail = new ParseContent(activity).parseRequestStatusNormal(response);

                            if (mRequestDetail == null) {

                                return;

                            }

                            if (mRequestOptional != null) {

                                Bundle bundle = new Bundle();

                                switch (mRequestDetail.getTripStatus()) {

                                    case Const.NO_REQUEST:
                                        new PreferenceHelper(activity).clearRequestData();
                                        // startgetProvider();
                                        if (requestDialog != null && requestDialog.isShowing()) {

                                            requestDialog.dismiss();
                                            Commonutils.showtoast(getResources().getString(R.string.txt_no_provider_error), activity);
                                            stopCheckingforstatus();

                                        }

                                        break;

                                    case Const.IS_ACCEPTED:

                                        bundle.putSerializable(Const.REQUEST_DETAIL,
                                                mRequestDetail);

                                        bundle.putInt(Const.DRIVER_STATUS,
                                                Const.IS_ACCEPTED);

                                        if (!activity.currentFragment.equals(Const.TRAVEL_MAP_FRAGMENT)) {

//                                            stopCheckingforproviders();
                                            stopCheckingforstatus();

                                            if (requestDialog != null && requestDialog.isShowing()) {

                                                requestDialog.dismiss();

                                            }

                                            TravelMapFragment travalfragment = new TravelMapFragment();
                                            travalfragment.setArguments(bundle);
                                            activity.addFragment(travalfragment, false, Const.TRAVEL_MAP_FRAGMENT,
                                                    true);

                                        }


                                        BaseFragment.drop_latlan = null;
                                        BaseFragment.pic_latlan = null;
                                        BaseFragment.s_address = "";
                                        BaseFragment.d_address = "";

                                        break;
                                    case Const.IS_DRIVER_DEPARTED:

                                        break;
                                    case Const.IS_DRIVER_ARRIVED:

                                        break;
                                    case Const.IS_DRIVER_TRIP_STARTED:

                                        break;
                                    case Const.IS_DRIVER_TRIP_ENDED: {

                                    }
                                    break;
                                    case Const.IS_DRIVER_RATED: {

                                    }
                                    break;
                                    default:
                                        break;

                                }

                            } else {

                                mTv_billing_info_total_price.setText(mRequestDetail.getCurrnecy_unit() + " " + mRequestDetail.getTrip_total_price());
                                //   mTv_billing_info_a_and_e_value.setText(mRequestDetail.getCurrnecy_unit() + " " + mRequestDetail.getA_and_e());
                                //    mTv_billing_info_imh_value.setText(mRequestDetail.getCurrnecy_unit() + " " + mRequestDetail.getImh());
                                //    mTv_billing_info_ferry_terminals_value.setText(mRequestDetail.getCurrnecy_unit() + " " + mRequestDetail.getFerry_terminals());
                                //     mTv_billing_info_staircase_value.setText(mRequestDetail.getCurrnecy_unit() + " " + mRequestDetail.getStaircase());
                                //       mTv_billing_info_tarmac_value.setText(mRequestDetail.getCurrnecy_unit() + " " + mRequestDetail.getTarmac());
                                //    mTv_billing_info_weight_value.setText(mRequestDetail.getCurrnecy_unit() + " " + mRequestDetail.getWeight());
//                                mTv_billing_info_oxygen_tank_value.setText(mRequestDetail.getCurrnecy_unit() + " " + mRequestDetail.getOxygen_tank());
                                //   mTv_billing_info_pickup_type_value.setText(mRequestDetail.getCurrnecy_unit() + " " + mRequestDetail.getPickup_type());

                            }

                        } else if (jsonObject.getString("success").equals("false")) {

                            if (jsonObject.has("error")) {

                                EbizworldUtils.showShortToast(jsonObject.getString("error"), activity);

                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        EbizworldUtils.appLogError("HaoLS", "CHECKREQUEST_STATUS failed " + e.toString());
                    }


                }

            }
            break;

            case Const.ServiceCode.POST_PAYPAL_NONCE: {

                EbizworldUtils.appLogInfo("HaoLS", "Post paypal nonce: " + response);

                Commonutils.progressdialog_hide();

                if (response != null) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getString("success").equals("true")) {

                            if (!activity.currentFragment.equals(Const.RATING_FRAGMENT) && !activity.isFinishing()) {

                                Bundle bundle = new Bundle();
                                RatingFragment ratingFragment = new RatingFragment();
                                bundle.putSerializable(Const.REQUEST_DETAIL, mRequestDetail);
                                ratingFragment.setArguments(bundle);
                                activity.addFragment(ratingFragment, false, Const.RATING_FRAGMENT, true);

                            }
                        } else if (jsonObject.getString("success").equals("false")) {

                            if (jsonObject.has("error")) {

                                EbizworldUtils.showShortToast(jsonObject.getString("error"), activity);

                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        EbizworldUtils.appLogError("HaoLS", "POST_PAYPAL_NONCE: " + e.toString());
                        EbizworldUtils.showShortToast("POST_PAYPAL_NONCE: " + e.toString(), activity);
                    }
                }

            }
            break;

            case Const.ServiceCode.PAYMENT_CASH: {

                EbizworldUtils.appLogInfo("HaoLS", "Payment Cash " + response);
                Commonutils.progressdialog_hide();

                if (response != null) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getString("success").equals("true")) {

                            if (!activity.currentFragment.equals(Const.RATING_FRAGMENT) && !activity.isFinishing()) {

                                Bundle bundle = new Bundle();
                                RatingFragment ratingFragment = new RatingFragment();
                                bundle.putSerializable(Const.REQUEST_DETAIL, mRequestDetail);
                                ratingFragment.setArguments(bundle);
                                activity.addFragment(ratingFragment, false, Const.RATING_FRAGMENT, true);

                            }

                        } else if (jsonObject.getString("success").equals("false")) {

                            if (jsonObject.has("error")) {

                                EbizworldUtils.showShortToast(jsonObject.getString("error"), activity);

                            } else {

                                EbizworldUtils.showShortToast(getResources().getString(R.string.payment_by_cash_failed), activity);

                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        EbizworldUtils.appLogError("HaoLS", getResources().getString(R.string.payment_by_cash_failed) + " " + e.toString());
                        EbizworldUtils.showShortToast(getResources().getString(R.string.payment_by_cash_failed), activity);
                    }
                }
            }
            break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Const.ServiceCode.REQUEST_PAYPAL) {

            if (resultCode == Activity.RESULT_OK) {

                DropInResult dropInResult = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);

                if (dropInResult.getPaymentMethodNonce() != null) {

                    postNonceToServer(dropInResult.getPaymentMethodNonce().getNonce());

                    EbizworldUtils.appLogDebug("HaoLS", "Billing Info Nonce: " + dropInResult.getPaymentMethodNonce().getNonce());
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {

                EbizworldUtils.showShortToast("Request is canceled", activity);

            } else {

                Exception exception = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                EbizworldUtils.showShortToast(exception.toString(), activity);
                EbizworldUtils.appLogError("HaoLS", "PayPal result" + exception.toString());
            }
        }
    }
}
