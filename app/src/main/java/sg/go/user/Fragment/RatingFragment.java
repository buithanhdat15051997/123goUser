package sg.go.user.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aurelhubert.simpleratingbar.SimpleRatingBar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import sg.go.user.DatabaseHandler;
import sg.go.user.HttpRequester.VolleyRequester;
import sg.go.user.MainActivity;
import sg.go.user.Models.RequestDetail;
import sg.go.user.R;
import sg.go.user.Utils.Commonutils;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.EbizworldUtils;
import sg.go.user.Utils.ParseContent;
import sg.go.user.Utils.PreferenceHelper;

/**
 * Created by user on 1/12/2017.
 */

public class RatingFragment extends BaseFragment {
    private RequestDetail requestDetail;
    private TextView tv_total, text_time, text_distance, btn_submit_rating, tv_cancellation_fee;
    private SimpleRatingBar simple_rating_bar;
    private CircleImageView iv_feedback_vehicle, iv_feedback_user, iv_feedback_location;
    String google_img_url = "";
    int rating = 0;
    private AlertDialog.Builder paybuilder;
    private boolean ispayshowing = false;
    DatabaseHandler db;
    private LinearLayout layout_distance, toll_layout;
    private TextView tv_no_tolls, tv_payment_type;
    private Handler checkRequestStatusHandler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            checkreqstatus();
            checkRequestStatusHandler.postDelayed(runnable, 5000);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle mBundle = getArguments();
        db = new DatabaseHandler(activity);
        EbizworldUtils.removeProgressDialog();

        if (db != null) {
            db.DeleteChat(String.valueOf(new PreferenceHelper(activity).getRequestId()));
        }
        activity.currentFragment = Const.RATING_FRAGMENT;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_rating, container,
                false);
        tv_total = (TextView) view.findViewById(R.id.tv_history_detail_total);
        text_time = (TextView) view.findViewById(R.id.text_time);
        text_distance = (TextView) view.findViewById(R.id.text_distance);
        simple_rating_bar = (SimpleRatingBar) view.findViewById(R.id.simple_rating_bar);
        btn_submit_rating = (TextView) view.findViewById(R.id.btn_submit_rating);
        tv_no_tolls = (TextView) view.findViewById(R.id.tv_no_tolls);
        tv_payment_type = (TextView) view.findViewById(R.id.tv_payment_type);
        tv_cancellation_fee = (TextView) view.findViewById(R.id.tv_cancellation_fee);

        iv_feedback_vehicle = (CircleImageView) view.findViewById(R.id.iv_feedback_vehicle);
        iv_feedback_user = (CircleImageView) view.findViewById(R.id.iv_feedback_user);
        iv_feedback_location = (CircleImageView) view.findViewById(R.id.iv_feedback_location);
        layout_distance = (LinearLayout) view.findViewById(R.id.layout_distance);
        toll_layout = (LinearLayout) view.findViewById(R.id.toll_layout);
        rating = simple_rating_bar.getRating();

        simple_rating_bar.setListener(new SimpleRatingBar.SimpleRatingBarListener() {
            @Override
            public void onValueChanged(int value) {
                rating = value;

            }
        });

        checkRequestStatusHandler.postDelayed(runnable, 5000); //Handler CheckRequest Status

        btn_submit_rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                giverating();
            }
        });


        return view;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle mBundle = getArguments();
        if (mBundle != null) {
            requestDetail = (RequestDetail) mBundle.getSerializable(
                    Const.REQUEST_DETAIL);
            google_img_url = getGoogleMapThumbnail(Double.valueOf(requestDetail.getD_lat()), Double.valueOf(requestDetail.getD_lng()));
            //EbizworldUtils.removeLoader();
            tv_total.setText(requestDetail.getCurrnecy_unit() + " " + requestDetail.getTrip_total_price());
            text_time.setText(requestDetail.getTrip_time() + " " + getResources().getString(R.string.min));
            tv_payment_type.setText(getResources().getString(R.string.txt_payment_type) + requestDetail.getPayment_mode());

            /*Picasso.get().load(requestDetail.getDriver_picture()).error(R.drawable.driver).into(iv_feedback_user);
            Picasso.get().load(google_img_url).into(iv_feedback_location);
            Picasso.get().load(requestDetail.getVehical_img()).into(iv_feedback_vehicle);*/

            Glide.with(activity)
                    .load(requestDetail.getDriver_picture())
                    .apply(new RequestOptions().error(R.drawable.carambulance))
                    .into(iv_feedback_user);

            Glide.with(activity)
                    .load(google_img_url)
                    .apply(new RequestOptions().error(R.drawable.carambulance))
                    .into(iv_feedback_location);

            Glide.with(activity)
                    .load(requestDetail.getVehical_img())
                    .apply(new RequestOptions().error(R.drawable.carambulance))
                    .into(iv_feedback_vehicle);

            text_distance.setText(requestDetail.getTrip_distance() + " " + requestDetail.getDistance_unit());

            /*if (requestDetail.getDriverStatus() == 3) {

                if (isAdded() && ispayshowing == false && activity.currentFragment.equals(Const.RATING_FRAGMENT)) {

                    showpaydialog();
                }

            }*/

            if (null != requestDetail.getCancellationFee() &&!requestDetail.getCancellationFee().equals("0")) {
                tv_cancellation_fee.setVisibility(View.VISIBLE);
                tv_cancellation_fee.setText(getResources().getString(R.string.txt_trip_cancel_fee) + " " + requestDetail.getCurrnecy_unit() + " " + requestDetail.getCancellationFee());
            } else {
                tv_cancellation_fee.setVisibility(View.GONE);
            }

            if (requestDetail.getRequest_type().equals("1") || requestDetail.getRequest_type().equals("2")) {
                toll_layout.setVisibility(View.GONE);
            } else {
                toll_layout.setVisibility(View.VISIBLE);
                tv_no_tolls.setText(getResources().getString(R.string.txt_toll)+":" + " " + requestDetail.getNo_tolls());
            }
            if (requestDetail.getRequest_type().equals("2") || requestDetail.getRequest_type().equals("3")) {
                layout_distance.setVisibility(View.GONE);
                iv_feedback_location.setVisibility(View.GONE);
            } else {
                layout_distance.setVisibility(View.VISIBLE);
                iv_feedback_location.setVisibility(View.VISIBLE);
            }

        }
    }

    private void showpaydialog() {
        ispayshowing = true;
        String Mesaage = getResources().getString(R.string.txt_ride_cmplt) + "\n" + "\n" +
                getResources().getString(R.string.you_need_to_pay) +
                requestDetail.getCurrnecy_unit() + " " +
                requestDetail.getTrip_total_price();

        paybuilder = new AlertDialog.Builder(activity);
        paybuilder.setMessage(Mesaage)
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.pay_now), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                        sendpay();
                    }
                });
        AlertDialog alert = paybuilder.create();
        alert.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

          /*TO clear all views */
        ViewGroup mContainer = (ViewGroup) getActivity().findViewById(R.id.content_frame);
        mContainer.removeAllViews();
    }

    private void sendpay() {

        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.PAYNOW);
        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());
        map.put(Const.Params.IS_PAID, "1");
        map.put(Const.Params.PAYMENT_MODE, requestDetail.getPayment_mode());
        map.put(Const.Params.REQUEST_ID, String.valueOf(new PreferenceHelper(activity).getRequestId()));

        Log.d("HaoLS", "send pay: " + map.toString());
        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.PAYNOW,
                this);

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }


    public static String getGoogleMapThumbnail(double lati, double longi) {
        String staticMapUrl = "http://maps.google.com/maps/api/staticmap?center=" + lati + "," + longi + "&markers=" + lati + "," + longi + "&zoom=14&size=150x120&sensor=false&key="+Const.GOOGLE_API_KEY;
        return staticMapUrl;
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.currentFragment = Const.RATING_FRAGMENT;

    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {

        switch (serviceCode) {
            case Const.ServiceCode.PAYNOW:{
                Log.d("HaoLS", "pay provider" + response);
                if (response != null) {

                    try {
                        JSONObject job = new JSONObject(response);
                        if (job.getString("success").equals("true")) {

                            EbizworldUtils.showShortToast(getResources().getString(R.string.txt_payment_success), activity);

                        } else {
                            String error = job.getString("error");
                            showDebtDialog(error);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
                break;

            case Const.ServiceCode.RATE_PROVIDER:
                EbizworldUtils.appLogInfo("HaoLS", "rate provider" + response);
                Commonutils.progressdialog_hide();
                try {
                    JSONObject job = new JSONObject(response);
                    if (job.getString("success").equals("true")) {

                        new PreferenceHelper(activity).clearRequestData();
                        Intent i = new Intent(activity, MainActivity.class);
                        startActivity(i);

                    } else {

                        String error_msg = job.getString("error");
                        Commonutils.showtoast(error_msg, activity);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case Const.ServiceCode.CHECKREQUEST_STATUS:{
                EbizworldUtils.appLogInfo("HaoLS", "CHECKREQUEST_STATUS " + response);

                if (response != null && activity != null){

                    RequestDetail requestDetail = new ParseContent(activity).parseRequestStatusNormal(response);

                    if (requestDetail != null){

                        if (requestDetail.getTripStatus() == Const.IS_DRIVER_RATED) {

                            checkRequestStatusHandler.removeCallbacks(runnable);

                            btn_submit_rating.setBackgroundColor(getResources().getColor(R.color.color_btn_main));
                            btn_submit_rating.setEnabled(true);

                        }else {

                            btn_submit_rating.setBackgroundColor(getResources().getColor(R.color.color_btn_two));
                            btn_submit_rating.setEnabled(false);
                        }


                    }
                }
            }
            break;
            default:
                break;
        }
    }

    private void showDebtDialog(String error) {

        AlertDialog.Builder debtbuilder = new AlertDialog.Builder(activity);
        debtbuilder.setMessage(error)
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.pay_now), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = debtbuilder.create();
        alert.show();
    }

    private void checkreqstatus() {

        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }
        HashMap<String, String> map = new HashMap<String, String>();

        if (new PreferenceHelper(activity).getLoginType().equals(Const.PatientService.PATIENT)){

            map.put(Const.Params.URL, Const.ServiceType.CHECKREQUEST_STATUS);

        }else if (new PreferenceHelper(activity).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)){

            map.put(Const.Params.URL, Const.NursingHomeService.REQUEST_STATUS_CHECK_URL);

        }else if (new PreferenceHelper(activity).getLoginType().equals(Const.HospitalService.HOSPITAL)){

            map.put(Const.Params.URL, Const.HospitalService.CHECK_REQUEST_STATUS_URL);

        }
        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());
        EbizworldUtils.appLogDebug("HaoLS", map.toString());
        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.CHECKREQUEST_STATUS,
                this);
    }

    private void giverating() {

        if (!EbizworldUtils.isNetworkAvailable(activity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }

        Commonutils.progressdialog_show(activity, "Rating...");
        HashMap<String, String> map = new HashMap<String, String>();

        if (new PreferenceHelper(activity).getLoginType().equals(Const.PatientService.PATIENT)){

            map.put(Const.Params.URL, Const.ServiceType.RATE_PROVIDER);

        }else if (new PreferenceHelper(activity).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)){

            map.put(Const.Params.URL, Const.NursingHomeService.RATE_PROVIDER_URL);

        }else if (new PreferenceHelper(activity).getLoginType().equals(Const.HospitalService.HOSPITAL)){

            map.put(Const.Params.URL, Const.HospitalService.RATE_PROVIDER_URL);

        }
        map.put(Const.Params.ID, new PreferenceHelper(activity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(activity).getSessionToken());
        map.put(Const.Params.REQUEST_ID, String.valueOf(new PreferenceHelper(activity).getRequestId()));
        map.put(Const.Params.COMMENT, " ");
        map.put(Const.Params.RATING, String.valueOf(rating));
        EbizworldUtils.appLogDebug("HaoLS", map.toString());
        new VolleyRequester(activity, Const.POST, map, Const.ServiceCode.RATE_PROVIDER,
                this);

    }


}
