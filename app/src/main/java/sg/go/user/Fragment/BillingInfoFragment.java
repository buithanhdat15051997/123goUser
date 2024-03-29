package sg.go.user.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.dropin.DropInActivity;
import com.braintreepayments.api.dropin.DropInRequest;
import com.braintreepayments.api.dropin.DropInResult;
import com.bumptech.glide.Glide;
import com.skyfishjy.library.RippleBackground;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
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

public class BillingInfoFragment extends DialogFragment implements AsyncTaskCompleteListener, View.OnClickListener {

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
    private TextView txt_show_auto_payment, tv_total_erp_charges, tv_total_child_seat_charges, tv_total_orthers, tv_total_extra_cost, btn_choose_type_payment_billing;
    private MainActivity activity;
    private DatabaseHandler mDatabaseHandler;
    private RequestDetail mRequestDetail;
    private RequestOptional mRequestOptional;
    private Dialog requestDialog, dialog_choose_payment,dialog_payment_wallet;
    private TextView cancel_req_create, tv_billing_info_schedule;
    private Handler checkRequestStatusHandler;
    private Button btn_pay_wallet_demo1;
    private ImageView img_logo_hide_billinginfo;
    private int Total_billing;
    private String TotalMoney;
    private int type_payment;
    private float total_billing;
    private double total_after_trip;
    private int type_payment_selected;

    private LinearLayout billing_info_payment_summary_group;
    private Dialog dialog_billing_Schedule;
    private TextView txt_date_time_schedule, txt_show_time_down;

    private DatePickerDialog dpd;
    private TimePickerDialog tpd;

    private CountDownTimer countDownTimer_billing;
    private int possiton_radio_button = 0, count_time_server;

    private String datetime = "", timeSet = "", date = "", time = "", pickupDateTime = "";

    private Runnable requestStatusCheckRunnable = new Runnable() {

        public void run() {

            checkreqstatus();
            checkRequestStatusHandler.postDelayed(this, 5000);

        }

    };
    private Boolean isBroadcastReceiverRegister = false;

    private BroadcastReceiver StatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent != null) {

                if (intent.getStringExtra(Const.NotificationType.SEND_REQUEST).equals(Const.NotificationType.TYPE_SERVICE_REJECT)
                ||intent.getStringExtra(Const.NotificationType.SEND_REQUEST).equals(Const.NotificationType.SEND_REQUEST)) {

                   // Toast.makeText(context, "" + activity.getResources().getString(R.string.btn_no_driver), Toast.LENGTH_SHORT).show();

                    EbizworldUtils.appLogDebug("DAT_CHECK_NOTIFICATION", intent.getStringExtra(Const.NotificationType.SEND_REQUEST) + "");


                    if (requestDialog != null) {

                        requestDialog.dismiss();
                    }

                    stopCheckingforstatus();

                }

//                if(intent.getStringExtra(Const.NotificationType.SEND_REQUEST).equals(Const.NotificationType.TYPE_SERVICE_ACCEPT)){
//
//
//                    Toast.makeText(context, ""+activity.getResources().getString(R.string.txt_load), Toast.LENGTH_SHORT).show();
//
//                    EbizworldUtils.appLogDebug("DAT_CHECK_NOTIFICATION",intent.getStringExtra(Const.NotificationType.SEND_REQUEST)+"");
//
//                    EbizworldUtils.appLogDebug("DAT_CHECK_NOTIFICATION","CHECK_ACCEPT");
//
//                    checkreqstatus();
//
//
//                }

            }
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

        txt_show_auto_payment = mView.findViewById(R.id.txt_show_auto_payment);

        tv_billing_info_schedule = mView.findViewById(R.id.tv_billing_info_schedule);

        billing_info_payment_summary_group = mView.findViewById(R.id.billing_info_payment_summary_group);

        tv_total_erp_charges = mView.findViewById(R.id.tv_total_erp_charges);

        tv_total_child_seat_charges = mView.findViewById(R.id.tv_total_child_seat_charges);

        tv_total_orthers = mView.findViewById(R.id.tv_total_orthers);

        tv_total_extra_cost = mView.findViewById(R.id.tv_total_extra_cost);

        btn_choose_type_payment_billing = mView.findViewById(R.id.tv_choose_type_payment_billing);

        //  activity.mBottomNavigationView.setVisibility(View.GONE);

//        mRequestOptional.setRemark(tv_billing_info_table_price_content_warning.getText().toString());

        if (mRequestOptional != null) {

            GetTypePayment();

            mTv_billing_info_notice.setVisibility(View.GONE);
            billing_info_payment_group.setVisibility(View.GONE);
            txt_show_auto_payment.setVisibility(View.GONE);
            billing_info_payment_summary_group.setVisibility(View.GONE);

            billing_info_table_price_notice_group.setVisibility(View.VISIBLE);
            billing_info_request_group.setVisibility(View.VISIBLE);

            getBillingInfo(mRequestOptional);

            /*---- SET NAME - IMAGE  TYPE CAR - DISTANCE - TIME ----*/
            tv_billing_info_kilo_distance3.setText(mRequestOptional.getKm_send_billinginfo() + " km");
            tv_billing_info_kilo_dola2.setText(mRequestOptional.getTime_send_billinginfo());
            mTv_billing_info_name_typeCar.setText(new PreferenceHelper(activity).getTypeCarBillingInfo());
            Glide.with(activity).load(new PreferenceHelper(activity).getImageTypeCarBillingInfo()).into(img_billing_info_img_typeCar);


        } else {

            /*---- SET NAME - IMAGE  TYPE CAR - DISTANCE - TIME ----*/
            mTv_billing_info_name_typeCar.setText(new PreferenceHelper(activity).getTypeCarBillingInfo());
            tv_billing_info_kilo_distance3.setText(new PreferenceHelper(activity).getDistanceBillingInfo() + " km");
            tv_billing_info_kilo_dola2.setText(new PreferenceHelper(activity).getTimeBillingInfo());
            Glide.with(activity).load(new PreferenceHelper(activity).getImageTypeCarBillingInfo()).into(img_billing_info_img_typeCar);

            getExtraCost();

            //GetTypePayment();

            billing_info_table_price_notice_group.setVisibility(View.GONE);
            billing_info_request_group.setVisibility(View.GONE);
            btn_choose_type_payment_billing.setVisibility(View.GONE);

            if (new PreferenceHelper(activity).gettypePaymentBilling().isEmpty()) {

                mTv_billing_info_notice.setVisibility(View.VISIBLE);
                billing_info_payment_group.setVisibility(View.VISIBLE);
                img_logo_hide_billinginfo.setVisibility(View.VISIBLE);
                billing_info_payment_summary_group.setVisibility(View.VISIBLE);

            } else {

                CheckTypePayPayment();

            }

            checkreqstatus(); //Get check status of request

        }

        btn_pay_wallet_demo1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                getPaymentWallet();

//                dialog_payment_wallet = new Dialog(activity);
//
//                dialog_payment_wallet.setContentView(R.layout.dialog_payment_with_wallet);
//
//                custom_simple_title_paymentwallet = dialog_payment_wallet.findViewById(R.id.custom_simple_title_paymentwallet);
//
//                custom_simple_content_paymentwallet = dialog_payment_wallet.findViewById(R.id.custom_simple_content_paymentwallet);
//
//                btn_yes1_paymentwallet = dialog_payment_wallet.findViewById(R.id.btn_yes1_paymentwallet);
//
//                btn_no_paymentwallet = dialog_payment_wallet.findViewById(R.id.btn_no_paymentwallet);
//
//                dialog_payment_wallet.show();
//
//                custom_simple_title_paymentwallet.setText(getResources().getString(R.string.txt_dialog_pay_ewallet));
//
//                custom_simple_content_paymentwallet.setText(getResources().getString(R.string.txt_content_pay_ewallet));
//
//                btn_yes1_paymentwallet.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//
//                    }
//                });
//
//                btn_no_paymentwallet.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//
//                        dialog_payment_wallet.dismiss();
//
//                    }
//                });

            }
        });

        mBtn_pay_bycash.setOnClickListener(this);
        mBtn_pay_bypaypal.setOnClickListener(this);
        tv_billing_info_confirm.setOnClickListener(this);
        tv_billing_info_deny.setOnClickListener(this);
        tv_billing_info_schedule.setOnClickListener(this);

        return mView;
    }

    private void CheckTypePayPayment() {

        switch (Integer.parseInt(new PreferenceHelper(activity).gettypePaymentBilling())) {

            case 0:

                Log.d("Test_billing", type_payment + "");
                mTv_billing_info_notice.setVisibility(View.VISIBLE);
                billing_info_payment_group.setVisibility(View.VISIBLE);
                img_logo_hide_billinginfo.setVisibility(View.VISIBLE);
                billing_info_payment_summary_group.setVisibility(View.VISIBLE);
                btn_choose_type_payment_billing.setVisibility(View.GONE);
                break;

            case 1:

                Log.d("Test_billing", type_payment + "");
                mTv_billing_info_notice.setVisibility(View.GONE);
                billing_info_payment_group.setVisibility(View.GONE);
                img_logo_hide_billinginfo.setVisibility(View.GONE);
                billing_info_payment_summary_group.setVisibility(View.VISIBLE);
                btn_choose_type_payment_billing.setVisibility(View.GONE);

                txt_show_auto_payment.setText(activity.getResources().getString(R.string.txt_pay_cash));
                txt_show_auto_payment.setVisibility(View.VISIBLE);

                getPaymentByCash();
                break;

            case 2:

                Log.d("Test_billing", type_payment + "");
                mTv_billing_info_notice.setVisibility(View.GONE);
                billing_info_payment_group.setVisibility(View.GONE);
                img_logo_hide_billinginfo.setVisibility(View.GONE);
                billing_info_payment_summary_group.setVisibility(View.VISIBLE);
                btn_choose_type_payment_billing.setVisibility(View.GONE);

                txt_show_auto_payment.setText(activity.getResources().getString(R.string.txt_pay_wallet));
                txt_show_auto_payment.setVisibility(View.VISIBLE);

                getPaymentWallet();
                break;

            case 3:

                Log.d("Test_billing", type_payment + "");
                mTv_billing_info_notice.setVisibility(View.GONE);
                billing_info_payment_group.setVisibility(View.GONE);
                img_logo_hide_billinginfo.setVisibility(View.GONE);
                billing_info_payment_summary_group.setVisibility(View.VISIBLE);
                btn_choose_type_payment_billing.setVisibility(View.GONE);

                txt_show_auto_payment.setText(activity.getResources().getString(R.string.txt_pay_paypal));
                txt_show_auto_payment.setVisibility(View.VISIBLE);

                getBrainTreeClientToken();
                break;

        }
    }

    private void GetTypePayment() {

        EbizworldUtils.showSimpleProgressDialog(activity, getResources().getString(R.string.txt_load), true);

        HashMap<String, String> params = new HashMap<>();

        params.put(Const.Params.URL, Const.ServiceType.GET_TYPE_CHOOSE_PAYMENT);

        params.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());

        params.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());

        Log.d("DAT_BILLING_PAYMENT", params + "");


        new VolleyRequester(activity, Const.POST, params, Const.ServiceCode.GET_TYPE_CHOOSE_PAYMENT, this);

    }

    private void getExtraCost() {

        if (!EbizworldUtils.isNetworkAvailable(getActivity())) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }


        HashMap<String, String> map = new HashMap<>();

        map.put(Const.Params.URL, Const.ServiceType.BILLING_EXTRA_COST);

        map.put(Const.Params.ID, new PreferenceHelper(getActivity()).getUserId());

        map.put(Const.Params.REQUEST_ID, String.valueOf(new PreferenceHelper(getActivity()).getRequestId()));

        map.put(Const.Params.TOKEN, new PreferenceHelper(getActivity()).getSessionToken());

        EbizworldUtils.appLogDebug("DAT_BILLING", "getExtraCost: " + map.toString());

        new VolleyRequester(getActivity(), Const.POST, map, Const.ServiceCode.GET_EXTRA_COST, this);


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
        EbizworldUtils.appLogDebug("DAT_BILLING", "getPaymentByWallet: " + map.toString());

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

            case R.id.tv_billing_info_schedule:

                if (getActivity() != null) {

                    DialogSchedule();

                }

                break;

            case R.id.tv_billing_info_deny: {

                getActivity().getSupportFragmentManager().popBackStack();
//                activity.onBackPressed();

                new RequestOptional().setOverView_Polyline("");
            }
            break;
        }
    }

    private void DialogSchedule() {

        dialog_billing_Schedule = new Dialog(getActivity(), R.style.DialogSlideAnim_leftright_Fullscreen);
        dialog_billing_Schedule.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_billing_Schedule.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog_billing_Schedule.setCancelable(true);

        dialog_billing_Schedule.setContentView(R.layout.dialog_schedule);

        txt_date_time_schedule = dialog_billing_Schedule.findViewById(R.id.txt_date_time_billing_schedule);

        ImageView img_dialog_back_schedule = dialog_billing_Schedule.findViewById(R.id.img_dialog_back_schedule);

        TextView tv_dialog_schedule_confirm = dialog_billing_Schedule.findViewById(R.id.tv_dialog_schedule_confirm);

        TextView tv_dialog_schedule_cancel = dialog_billing_Schedule.findViewById(R.id.tv_dialog_schedule_cancel);

        tv_dialog_schedule_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog_billing_Schedule.dismiss();

            }
        });


        img_dialog_back_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog_billing_Schedule.dismiss();

            }
        });

        txt_date_time_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerSchedule();
            }
        });

        tv_dialog_schedule_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!pickupDateTime.isEmpty()) {

                    Toast.makeText(activity, "" + pickupDateTime.toString(), Toast.LENGTH_LONG).show();

                }
            }
        });

        dialog_billing_Schedule.show();

    }

    private void DatePickerSchedule() {

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        dpd = new DatePickerDialog(getActivity(), R.style.datepicker,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(android.widget.DatePicker view,
                                          int year, int monthOfYear, int dayOfMonth) {

                        if (view.isShown()) {

                            date = Integer.toString(year) + "-"
                                    + Integer.toString(monthOfYear + 1) + "-"
                                    + Integer.toString(dayOfMonth);

                            datetime = date;

                            Log.d("DAT_BILLING", date.toString());

                            TimePickerSchedule();

                            dpd.dismiss();
                        }
                    }
                }, mYear, mMonth, mDay);

        dpd.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.txt_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dpd.dismiss();
                    }
                });


        // dpd.getDatePicker().setMaxDate(addDays(new Date(),90).getTime());
        // dpd.getDatePicker().setMinDate(new Date().getTime());

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 3);
        cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
        /*cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));*/
        cal.set(Calendar.SECOND, 0);
        /*cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));*/
        cal.set(Calendar.MILLISECOND, 0);
        dpd.getDatePicker().setMaxDate(cal.getTimeInMillis());

        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
        /*cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));*/
        cal.set(Calendar.SECOND, 0);

        /*cal.set(Calendar.MILLISECOND, cal.getMinimum(Calendar.MILLISECOND));*/
        cal.set(Calendar.MILLISECOND, 0);
        dpd.getDatePicker().setMinDate(cal.getTimeInMillis());


        dpd.show();

    }

    private void TimePickerSchedule() {


        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 30);
        int mHour = calendar.get(Calendar.HOUR_OF_DAY);
        int mMinute = calendar.get(Calendar.MINUTE);


        tpd = new TimePickerDialog(getActivity(), R.style.datepicker,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(android.widget.TimePicker view,
                                          int hourOfDay, int minute) {


                        if (view.isShown()) {

                            tpd.dismiss();

                            // isTimePickerOpen = false;


                            int hour = hourOfDay;
                            int min = minute;

                            if (hourOfDay > 12) {

                                hour -= 12;

                                timeSet = "PM";

                            } else if (hourOfDay == 0) {

                                timeSet = "AM";

                            } else if (hourOfDay == 12) {

                                timeSet = "PM";
                            } else {

                                timeSet = "AM";

                            }

                            if (minute < 10) {

                                time = String.valueOf(hourOfDay) + ":0" +
                                        String.valueOf(min) + ":" + "00 " + timeSet;

                            } else {

                                time = String.valueOf(hourOfDay) + ":" +
                                        String.valueOf(min) + ":" + "00 " + timeSet;

                            }

                            pickupDateTime = date + " " + time;

                            datetime = datetime.concat(" "
                                    + Integer.toString(hourOfDay) + ":"
                                    + Integer.toString(minute) + ":" + "00");

                            Log.d("DAT_BILLING", pickupDateTime.toString());

                            txt_date_time_schedule.setText(pickupDateTime);

                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            calendar.set(Calendar.MINUTE, minute);

                            /*AlarmUtil.scheduleNotification(getActivity(), calendar);*/
                        }
                    }
                }, mHour, mMinute, false);

        tpd.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.txt_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        tpd.dismiss();

                    }
                });

        tpd.show();


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

        EbizworldUtils.appLogDebug("DAT_PAYPAL", "BrainTreeClientTokenMap: " + map.toString());

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

        EbizworldUtils.appLogDebug("DAT_BILLING", "Get Billing info: " + hashMap.toString());

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

        EbizworldUtils.appLogDebug("DAT_PAYPAL", "postNonceToServer: " + map.toString());

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

//        if(total_after_trip>0){
//
//            map.put("totalPrice",String.valueOf(total_after_trip));
//
//
//        }else {
//
//            map.put("totalPrice", 0+"");
//        }

        EbizworldUtils.appLogDebug("DAT_BILLING", "getPaymentByCash: " + map.toString());

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
        EbizworldUtils.appLogDebug("DAT_BILLING", map.toString());
        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.CHECKREQUEST_STATUS,
                this);
    }

    private void startGetRequestStatus() {
        startCheckstatusTimer();
    }

    private void startCheckstatusTimer() {

        checkRequestStatusHandler.postDelayed(requestStatusCheckRunnable, 5000);

    }

    private void stopCheckingforstatus() {

        if (checkRequestStatusHandler != null) {

            checkRequestStatusHandler.removeCallbacks(requestStatusCheckRunnable);

            Log.d("DAT_BILLING", "stop status handler");
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

        if (total_billing > 0) {

            map.put("totalPrice", String.valueOf(total_billing));

        } else {

            map.put("totalPrice", String.valueOf(0));

        }

        map.put("overview_polyline", new PreferenceHelper(activity).getOverViewPolyline());

        map.put("km", requestOptional.getKm_send_billinginfo().toString());

        Log.d("DAT_BILLING", "Request ambulance: " + map.toString());

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

        EbizworldUtils.appLogDebug("DAT_BILLING", "Cancel request: " + map.toString());
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

        txt_show_time_down = requestDialog.findViewById(R.id.txt_show_time_down);

        rippleBackground.startRippleAnimation();

        cancel_req_create.setEnabled(false);
        cancel_req_create.setBackgroundColor(getResources().getColor(R.color.lightblue100));

        CountDownTime();

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

            case Const.ServiceCode.GET_TYPE_CHOOSE_PAYMENT:

                Log.d("DAT_BILLING_PAYMENT", response + "");

                if (response != null) {

                    EbizworldUtils.removeProgressDialog();

                    try {

                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getBoolean("success")) {

                            if (jsonObject.has("selected_type")) {

                                type_payment_selected = jsonObject.getInt("selected_type");

                                /*---SET BUTTON---*/
                                switch (type_payment_selected) {

                                    case 0:
                                        btn_choose_type_payment_billing.setText(activity.getResources().getString(R.string.txt_choose_a_payment));
                                        break;

                                    case 1:
                                        btn_choose_type_payment_billing.setText(activity.getResources().getString(R.string.txt_pay_cash));

                                        break;

                                    case 2:
                                        btn_choose_type_payment_billing.setText(activity.getResources().getString(R.string.txt_pay_wallet));
                                        break;

                                }

                                EbizworldUtils.appLogDebug("DAT_CHOOSE_PAYMENT", String.valueOf(type_payment_selected));

                                new PreferenceHelper(activity).puttypePaymentBilling(String.valueOf(type_payment_selected));

                                btn_choose_type_payment_billing.setVisibility(View.VISIBLE);

                                btn_choose_type_payment_billing.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

//                                        Bundle bundle = new Bundle();
//
//                                        ChoosePaymentFragment choosePaymentFragment = new ChoosePaymentFragment();
//                                        bundle.putString("billinginfo","billinginfo");
//                                        choosePaymentFragment.setArguments(bundle);
//
//                                        activity.addFragmentNoRefresh(choosePaymentFragment,true,Const.CHOOSE_PAYMENT_FRAGMENT,true);

                                        DialogChooseTypePayment();

                                    }
                                });

                                if (type_payment_selected > 0) {

                                    tv_billing_info_confirm.setVisibility(View.VISIBLE);

                                } else {

                                    btn_choose_type_payment_billing.setVisibility(View.VISIBLE);

                                    tv_billing_info_confirm.setVisibility(View.GONE);


                                }
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

                            dialog_choose_payment.dismiss();

//                            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(activity);
//
//                            builder.setTitle("Selected Successfully");
//
//                            builder.setCancelable(true);
//
//                            builder.setNegativeButton(getResources().getString(R.string.txt_btn_done), new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//
//                                    dialogInterface.dismiss();
//                                }
//                            });
//
//                            android.support.v7.app.AlertDialog alertDialog = builder.create();
//                            alertDialog.show();

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

            case Const.ServiceCode.GET_EXTRA_COST:

                EbizworldUtils.appLogInfo("DAT_BILLING", "getExtraCost: " + response);


                if (response != null) {


                    try {

                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.has("ERP")) {

                            EbizworldUtils.appLogInfo("DAT_BILLING", "getExtraCost1: " + jsonObject.getString("ERP"));
                            tv_total_erp_charges.setText(jsonObject.getString("ERP"));


                        } else {

                        }

                        if (jsonObject.has("child_seat")) {

                            EbizworldUtils.appLogInfo("DAT_BILLING", "getExtraCost2: " + jsonObject.getString("child_seat"));
                            tv_total_child_seat_charges.setText(jsonObject.getString("child_seat"));

                        } else {

                        }

                        if (jsonObject.has("otherExtraValue")) {

                            EbizworldUtils.appLogInfo("DAT_BILLING", "getExtraCost3: " + jsonObject.getString("otherExtraValue"));
                            tv_total_orthers.setText(jsonObject.getString("otherExtraValue"));

                        } else {

                        }

                        if (jsonObject.has("totalExtraCost")) {

                            EbizworldUtils.appLogInfo("DAT_BILLING", "getExtraCost4: " + jsonObject.getString("totalExtraCost"));
                            tv_total_extra_cost.setText(jsonObject.getString("totalExtraCost"));

                        } else {

                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {

                    Toast.makeText(activity, "getExtraCost: Failed", Toast.LENGTH_SHORT).show();


                }

                break;


            case Const.ServiceCode.PAYMENT_WALLET: {

                EbizworldUtils.appLogInfo("DAT_BILLING", "payment_wallet: " + response);
                Commonutils.progressdialog_hide();

                if (response != null) {
                    EbizworldUtils.appLogInfo("DAT_BILLING", "payment_wallet: " + response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getBoolean("success")) {

                            if (!activity.currentFragment.equals(Const.RATING_FRAGMENT) && !activity.isFinishing()) {

                                EbizworldUtils.appLogInfo("DAT_BILLING", "payment_wallet: " + response);

                                Bundle bundle = new Bundle();
                                RatingFragment ratingFragment = new RatingFragment();
                                bundle.putSerializable(Const.REQUEST_DETAIL, mRequestDetail);
                                ratingFragment.setArguments(bundle);
                                activity.addFragment(ratingFragment, false, Const.RATING_FRAGMENT, true);

                            }

                        } else if (jsonObject.getBoolean("success") == false) {

                            if (jsonObject.has("e_wallet_allowed")) {

                                if (jsonObject.getBoolean("e_wallet_allowed") == false) {

                                    Toast.makeText(activity, "" + jsonObject.getString("e_wallet_message"), Toast.LENGTH_SHORT).show();
                                    billing_info_payment_group.setVisibility(View.VISIBLE);
                                    btn_pay_wallet_demo1.setVisibility(View.GONE);

                                }
                            }
                            EbizworldUtils.showLongToast("Payment with Wallet Failed", activity);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();

                        EbizworldUtils.appLogError("DAT_BILLING", "Get billing info failed: " + e.toString());
                    }

                } else {

                    EbizworldUtils.showLongToast("Payment with Wallet Failed", activity);

                }

            }

            break;

            case Const.ServiceCode.BILLING_INFO: {

                EbizworldUtils.appLogInfo("DAT_BILLING", "Billing info: " + response);
                Commonutils.progressdialog_hide();

                try {
                    JSONObject object = new JSONObject(response);

                    if (object.getString("success").equals("true")) {

                        JSONObject jsonObject = object.getJSONObject(Const.Params.BILLING_INFO);
                        Log.d("DAT_BILLING", String.valueOf(jsonObject));

                        if (jsonObject.has("pre_select_type")) {

                            type_payment = jsonObject.getInt("pre_select_type");

                            new PreferenceHelper(activity).puttypePaymentBilling(String.valueOf(type_payment));

                            Log.d("DAT_BILLING", new PreferenceHelper(activity).gettypePaymentBilling());

                        }

//                        if(jsonObject.has("provider_timeout")){
//
//                            count_time_server = Integer.parseInt(jsonObject.getString("provider_timeout"));
//
//                            Log.d("DAT_BILLING_TIMEDOWN",""+count_time_server);
//
//
//                        }

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

                            DecimalFormat decimalFormat = new DecimalFormat("0.00");

                            total_billing = (float) jsonObject.getDouble(Const.Params.TOTAL);

                            mTv_billing_info_total_price.setText(jsonObject.getString("currency") + " " + decimalFormat.format(total_billing));

                            Log.d("DAT_BILLING", jsonObject.getString("currency") + " " + jsonObject.getString(Const.Params.TOTAL).toString());

                            new PreferenceHelper(activity).putTotalPaymentWallet(jsonObject.getString(Const.Params.TOTAL));

                            //  mRequestOptional.setTotal_money_price(jsonObject.getString("currency") + " " + jsonObject.getString(Const.Params.TOTAL));

                        }

                    } else {
                        Toast.makeText(activity, "Fail BillingInfo", Toast.LENGTH_SHORT).show();
//                        activity.addFragment(new HomeMapFragment(), false, Const.HOME_MAP_FRAGMENT, true);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    EbizworldUtils.appLogError("DAT_BILLING", "Get billing info failed: " + e.toString());

//                    activity.addFragment(new HomeMapFragment(), false, Const.HOME_MAP_FRAGMENT, true);
                }

            }
            break;

            case Const.ServiceCode.REQUEST_AMBULANCE:

                EbizworldUtils.appLogInfo("DAT_BILLING", "create req_response succeeded: " + response);

                if (response != null) {

                    Log.d("DatCheckReponse", response.toString());

                    try {
                        JSONObject job1 = new JSONObject(response);

                        if (job1.getString("success").equals("true")) {

                            /*---SET EWALLET---*/
                            if (job1.has("e_wallet_allowed")) {

                                if (job1.getBoolean("e_wallet_allowed") == false) {

                                    Log.d("DatCheckReponse", job1.getBoolean("e_wallet_allowed") + "");

                                    if (requestDialog != null && requestDialog.isShowing()) {

                                        requestDialog.dismiss();

                                        stopCheckingforstatus();
                                    }
                                    if (!job1.getString("e_wallet_message").isEmpty()) {

                                        Toast.makeText(activity, "" + job1.getString("e_wallet_message"), Toast.LENGTH_SHORT).show();

                                    }

                                }
                                if (job1.getBoolean("e_wallet_allowed") == true) {

                                    Log.d("DatCheckReponse", job1.getBoolean("e_wallet_allowed") + "");
                                    new PreferenceHelper(activity).putRequestId(Integer.parseInt(job1.getString("request_id")));


                                    if (requestDialog != null && requestDialog.isShowing()) {

                                        if (cancel_req_create != null) {

                                            cancel_req_create.setEnabled(true);
                                            cancel_req_create.setBackgroundColor(getResources().getColor(R.color.color_background_main));
                                            startGetRequestStatus();

                                        }

                                    }

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

                            if (job1.has("e_wallet_message")) {

                                if (!job1.getString("e_wallet_message").isEmpty()) {

                                    Toast.makeText(activity, "" + job1.getString("e_wallet_message"), Toast.LENGTH_SHORT).show();

                                }
                            }


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
                        EbizworldUtils.appLogError("DAT_BILLING", "create req_response failed: " + e.toString());
                    }
                }
                break;

            case Const.ServiceCode.GET_BRAIN_TREE_TOKEN_URL: {
                EbizworldUtils.appLogInfo("DAT_PAYPAL", "GET_BRAIN_TREE_TOKEN_URL: " + response);

                if (response != null && activity != null) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.getString("success").equals("true")) {

                            EbizworldUtils.showShortToast("Success " + jsonObject.getString("success"), activity);

                            if (jsonObject.has("client_token")) {

                                DropInRequest dropInRequest = new DropInRequest()
                                        .clientToken(jsonObject.getString("client_token"));

                                startActivityForResult(dropInRequest.getIntent(activity), Const.ServiceCode.REQUEST_PAYPAL);
                                activity.mBottomNavigationView.setVisibility(View.GONE);
                            }

                        } else if (jsonObject.getString("success").equals("false")) {

                            if (jsonObject.has("error")) {

                                EbizworldUtils.showShortToast(jsonObject.getString("error"), activity);

                            }

                        }

                    } catch (JSONException e) {

                        e.printStackTrace();
                        EbizworldUtils.appLogError("DAT_BILLING", "GET_BRAIN_TREE_TOKEN_URL: " + e.toString());

                    }


                }

            }
            break;

            case Const.ServiceCode.CANCEL_CREATE_REQUEST:

                EbizworldUtils.appLogInfo("DAT_BILLING", "cancel req_response " + response);

                new PreferenceHelper(activity).putRequestId(-1);

                if (requestDialog != null && requestDialog.isShowing()) {

                    requestDialog.dismiss();
                    stopCheckingforstatus();
                    cancelDownTimer();

                }
                break;

            case Const.ServiceCode.CHECKREQUEST_STATUS: {

                EbizworldUtils.appLogInfo("DAT_BILLING", "check req status: " + response);

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

                                        requestDialog.dismiss();

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
                                            activity.addFragment(travalfragment, true, Const.TRAVEL_MAP_FRAGMENT,
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

                                total_after_trip = Double.parseDouble(mRequestDetail.getTrip_total_price());

                                Log.d("DAT_BILLING_tt", total_after_trip + "");

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
                        EbizworldUtils.appLogError("DAT_BILLING", "CHECKREQUEST_STATUS failed " + e.toString());
                    }


                }

            }
            break;

            case Const.ServiceCode.POST_PAYPAL_NONCE: {

                EbizworldUtils.appLogInfo("DAT_PAYPAL", "Post paypal nonce: " + response);

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
                        EbizworldUtils.appLogError("DAT_PAYPAL", "POST_PAYPAL_NONCE: " + e.toString());
                        EbizworldUtils.showShortToast("POST_PAYPAL_NONCE: " + e.toString(), activity);
                    }
                }

            }
            break;

            case Const.ServiceCode.PAYMENT_CASH: {

                EbizworldUtils.appLogInfo("DAT_BILLING", "Payment Cash " + response);
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
                        EbizworldUtils.appLogError("DAT_BILLING", getResources().getString(R.string.payment_by_cash_failed) + " " + e.toString());
                        Commonutils.progressdialog_hide();
                        EbizworldUtils.showShortToast(getResources().getString(R.string.payment_by_cash_failed), activity);
                    }
                }
            }
            break;
        }
    }

    private void CountDownTime() {

       // int count_time = 1000 * 2;
        //int count_time_total = count_time * count_time_server;

       // Log.d("DAT_BILLING_TIMEDOWN", count_time + "  "  + "  " + count_time_server);

        countDownTimer_billing = new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {

                txt_show_time_down.setText(activity.getResources().getString(R.string.txt_countdown_time) + millisUntilFinished / 1000);

            }

            public void onFinish() {

               // txt_show_time_down.setText("Done!");
               cancel_create_req();

                Commonutils.showtoast(getResources().getString(R.string.txt_no_provider_error), activity);

                if (requestDialog != null) {

                    requestDialog.cancel();
                }
            }

        }.start();
    }

    private void cancelDownTimer() {

        if (countDownTimer_billing != null)
            countDownTimer_billing.cancel();
    }

    private void DialogChooseTypePayment() {


        dialog_choose_payment = new Dialog(activity);
        dialog_choose_payment.setContentView(R.layout.dialog_choose_payment_billing);
        dialog_choose_payment.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);


        RadioGroup radioGroup_Choose_payment_billing = dialog_choose_payment.findViewById(R.id.radioGroup_Choose_payment_billing);
        final RadioButton radioBtn_Payment_Cash_billing = dialog_choose_payment.findViewById(R.id.radioBtn_Payment_Cash_billing);
        // final RadioButton radioBtn_Payment_Ewallet_billing = dialog_choose_payment.findViewById(R.id.radioBtn_Payment_Ewallet_billing);
        // final RadioButton radioBtn_Payment_Paypal_billing = dialog_choose_payment.findViewById(R.id.radioBtn_Payment_Paypal_billing);
        final Button btn_dialog_choose_payment_billing = dialog_choose_payment.findViewById(R.id.btn_dialog_choose_payment_billing);


        switch (type_payment_selected) {
            case 0:
                radioBtn_Payment_Cash_billing.setChecked(false);
                // radioBtn_Payment_Ewallet_billing.setChecked(false);

                break;

            case 1:
                radioBtn_Payment_Cash_billing.setChecked(true);
                possiton_radio_button = 1;

                // radioBtn_Payment_Ewallet_billing.setChecked(false);
                break;
//            case 2:
//
//                radioBtn_Payment_Cash_billing.setChecked(false);
//                radioBtn_Payment_Ewallet_billing.setChecked(true);
//
//                break;
        }


        radioGroup_Choose_payment_billing.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                switch (i) {

                    case R.id.radioBtn_Payment_Cash_billing:

                        btn_dialog_choose_payment_billing.setText(getResources().getString(R.string.btn_confirm) + " " + getResources().getString(R.string.txt_pay_cash));
                        possiton_radio_button = 1;

                        break;

//                    case R.id.radioBtn_Payment_Ewallet_billing:
//
//                        btn_dialog_choose_payment_billing.setText(getResources().getString(R.string.btn_confirm) + " " + getResources().getString(R.string.txt_pay_wallet));
//                        possiton_radio_button = 2;
//                        break;

//                    case R.id.radioBtn_Payment_Paypal_billing:
//
//                        btn_dialog_choose_payment_billing.setText(getResources().getString(R.string.btn_confirm) + " " + getResources().getString(R.string.txt_pay_paypal));
//                        possiton_radio_button = 3;
//                        break;

                }
            }
        });

        btn_dialog_choose_payment_billing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//           || radioBtn_Payment_Paypal_billing.isChecked()|| || radioBtn_Payment_Ewallet_billing.isChecked()

                if (radioBtn_Payment_Cash_billing.isChecked()) {


                    SendChooseAPaymentBilling(possiton_radio_button);

                    Log.d("DAT_BILLING_PAYMENT", "" + possiton_radio_button);

                } else {

                    Toast.makeText(activity, activity.getResources().getString(R.string.txt_pls_choose_payment_type), Toast.LENGTH_SHORT).show();

                }


            }
        });


        dialog_choose_payment.show();


    }

    private void SendChooseAPaymentBilling(int possiton_radio_button) {

        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        EbizworldUtils.showSimpleProgressDialog(activity, getResources().getString(R.string.txt_load), true);


        HashMap<String, String> params = new HashMap<>();

        params.put(Const.Params.URL, Const.ServiceType.CHOOSE_PAYMENT_TYPE);

        params.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());

        params.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());

        params.put(Const.Params.CHOOSE_PAYMENT_SELECT_TYPE, String.valueOf(possiton_radio_button));


        Log.d("DAT_BILLING_PAYMENT", params + "");


        new VolleyRequester(activity, Const.POST, params, Const.ServiceCode.CHOOSE_A_PAYMENT, this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Const.ServiceCode.REQUEST_PAYPAL) {


            if (resultCode == Activity.RESULT_OK) {

                DropInResult dropInResult = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);

                dropInResult.getPaymentMethodType();

                Log.d("DAT_PAYPAL", dropInResult.getPaymentMethodType().toString().trim());

                if (dropInResult.getPaymentMethodNonce() != null) {

                    postNonceToServer(dropInResult.getPaymentMethodNonce().getNonce());

                    EbizworldUtils.appLogDebug("DAT_PAYPAL", "Billing Info Nonce: " + dropInResult.getPaymentMethodNonce().getNonce());
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {

                EbizworldUtils.showShortToast("Request is canceled", activity);

            } else {

                Exception exception = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
                EbizworldUtils.showShortToast(exception.toString(), activity);
                EbizworldUtils.appLogError("DAT_PAYPAL", "PayPal result" + exception.toString());
            }
        }
    }


    @Override
    public void onStop() {
        super.onStop();

        activity.mBottomNavigationView.setVisibility(View.VISIBLE);
        cancelDownTimer();

//        Log.d("Dat", "onStop: ");

    }


    @Override
    public void onStart() {

        activity.mBottomNavigationView.setVisibility(View.GONE);

        Log.d("DAT_BILLING", "onStart: " + isBroadcastReceiverRegister);

        if (!isBroadcastReceiverRegister) {

            LocalBroadcastManager.getInstance(activity).registerReceiver(StatusReceiver, new IntentFilter(Const.NotificationType.SEND_REQUEST));
            isBroadcastReceiverRegister = true;

            Log.d("DAT_BILLING", "onStart: " + isBroadcastReceiverRegister);

        }

        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (isBroadcastReceiverRegister) {

            LocalBroadcastManager.getInstance(activity).unregisterReceiver(StatusReceiver);

            isBroadcastReceiverRegister = false;

            Log.d("DAT_BILLING", "onStop: " + isBroadcastReceiverRegister);
        }

    }
}
