package sg.go.user.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import sg.go.user.Adapter.AccountSettingsAdapter;
import sg.go.user.AmbulanceWalletActivity;
import sg.go.user.AskBotActivity;
import sg.go.user.BuildConfig;
import sg.go.user.HelpwebActivity;
import sg.go.user.HttpRequester.VolleyRequester;
import sg.go.user.MainActivity;
import sg.go.user.Models.AccountSettings;
import sg.go.user.Models.Hospital;
import sg.go.user.Models.Nurse;
import sg.go.user.Models.Patient;
import sg.go.user.Models.Wallets;
import sg.go.user.ProfileActivity;
import sg.go.user.R;
import sg.go.user.RealmController.RealmController;
import sg.go.user.SavedPlacesActivity;
import sg.go.user.SignInActivity;
import sg.go.user.Utils.Commonutils;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.EbizworldUtils;
import sg.go.user.Utils.PreferenceHelper;
import sg.go.user.WelcomeActivity;


/**
 * Created by user on 12/28/2016.
 */
public class AccountFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    private ListView accountSettingsListView;
    private MainActivity mMainActivity;
    private RelativeLayout relative_account;
    private ImageView accountIcon;
    private TextView accountName, tv_build_version, txt_total_money_wallet;
    private View view;
    private Dialog dialog;
    private EditText edt_input_money_wallet;

    private Wallets wallets;
    private TextView txt_recharge_ewallet;
    private Button btn_recharge_ewallet;

    private ImageView btn_recharge_cancel_wallet;
    private String Get_Money_Recharge_Wallet;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMainActivity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_account, container, false);

        accountSettingsListView = (ListView) view.findViewById(R.id.lv_drawer_user_settings);

        accountIcon = (ImageView) view.findViewById(R.id.iv_account_icon);

        accountName = (TextView) view.findViewById(R.id.tv_account_name);

        relative_account = view.findViewById(R.id.relative_account);

      //  btn_pay_wallet_demo = view.findViewById(R.id.btn_pay_wallet_demo);

        tv_build_version = (TextView) view.findViewById(R.id.tv_build_version);

        wallets = new Wallets();


        if (new PreferenceHelper(getActivity()).getLoginType().equals(Const.PatientService.PATIENT)) {

            patientSetting();

        } else if (new PreferenceHelper(getActivity()).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)) {

            nurseSetting();

        } else if (new PreferenceHelper(getActivity()).getLoginType().equals(Const.HospitalService.HOSPITAL)) {

            hospitalSetting();
        }

        accountIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mMainActivity, ProfileActivity.class);
                startActivity(i);
            }
        });

        return view;
    }

    private void ApiGetWallet() {

    }
    private void DialogPayWallet() {

//        EbizworldUtils.getSimpleProgressDialog(mMainActivity,"Pls waiting",true);
//
//        ApiGetWallet();
//
//        Log.d("dat_test", "vao day");
//
//        final Dialog dialogRecharge_Wallet = new Dialog(activity);
//        dialogRecharge_Wallet.setContentView(R.layout.dialog_recharge_wallet);
//        dialogRecharge_Wallet.setCancelable(false);
//        dialogRecharge_Wallet.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
//
//        txt_recharge_ewallet = dialogRecharge_Wallet.findViewById(R.id.txt_pay_ewallet);
//        btn_recharge_ewallet = dialogRecharge_Wallet.findViewById(R.id.btn_recharge_ewallet);
//        btn_recharge_cancel_wallet = dialogRecharge_Wallet.findViewById(R.id.btn_pay_cancel_wallet);
//        txt_total_money_wallet = dialogRecharge_Wallet.findViewById(R.id.txt_total_money_wallet);
//        edt_input_money_wallet = dialogRecharge_Wallet.findViewById(R.id.edt_input_money_wallet);
//        dialogRecharge_Wallet.show();
//
//        edt_input_money_wallet.addTextChangedListener(onTextChangedListener());
//
//
//        btn_recharge_cancel_wallet.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                dialogRecharge_Wallet.dismiss();
//
//            }
//        });
//
//        btn_recharge_ewallet.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Get_Money_Recharge_Wallet = edt_input_money_wallet.getText().toString();
//
//                if (Get_Money_Recharge_Wallet.isEmpty()) {
//
//                    EbizworldUtils.showLongToast("Please enter the amount", mMainActivity);
//
//                } else {
//
//                    Get_Money_Recharge_Wallet.replaceAll(",", "");
//                    EbizworldUtils.showLongToast("OK: " + Get_Money_Recharge_Wallet, mMainActivity);
//
//                }
//
//
//            }
//        });

    }

//    char[] characters = {'0','1','2','3','4','5','6','7','8','9'};
//    private String setSDT(String sdt){
//        char[] arraysdt = sdt.toCharArray();
//        String sdtFinish = "";
//
//        for (int i = 0; i < arraysdt.length;i++)
//        {
//            for(int j = 0; j < characters.length;j++)
//            {
//                if(arraysdt[i] == characters[j])
//                {
//                    sdtFinish = sdtFinish + arraysdt[i];
//                }
//            }
//        }
//        return sdtFinish;
//    }

    private TextWatcher onTextChangedListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                edt_input_money_wallet.removeTextChangedListener(this);

                try {
                    String originalString = editable.toString();

                    Long longval;

                    if (originalString.contains(",")) {

                        originalString = originalString.replaceAll(",", "");

                    }
                    longval = Long.parseLong(originalString);

                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);

                    formatter.applyPattern("#,###,###,###");

                    String formattedString = formatter.format(longval);

                    //setting text after format to EditText
                    edt_input_money_wallet.setText(formattedString);

                    edt_input_money_wallet.setSelection(edt_input_money_wallet.getText().length());

                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }

                edt_input_money_wallet.addTextChangedListener(this);
            }
        };


    }

    private void patientSetting() {

        String pictureUrl = new PreferenceHelper(mMainActivity).getPicture();

        /*User userprofile = RealmController.with(this).getUser(Integer.valueOf(new PreferenceHelper(getActivity()).getUserId()));*/
        Patient patientProfile = RealmController.with(this).getPatient(Integer.valueOf(new PreferenceHelper(getActivity()).getUserId()));

        /*String name = new PreferenceHelper(mMainActivity).getUser_name();*/

        tv_build_version.setText("Version: " + BuildConfig.VERSION_NAME);

        if (!pictureUrl.equals("")) {

            Log.e("asher", "nav pic " + pictureUrl);


            /*Picasso.get().load(pictureUrl).error(R.drawable.defult_user).into(accountIcon);*/

            Glide.with(mMainActivity)
                    .load(pictureUrl)
                    .apply(new RequestOptions().error(R.drawable.defult_user))
                    .into(accountIcon);

        }

        if (patientProfile.getmFullname() != null) {

            accountName.setText(patientProfile.getmFullname());

        }

        AccountSettingsAdapter settingsAdapter = new AccountSettingsAdapter(mMainActivity, getAccountSettingsList());

        accountSettingsListView.setAdapter(settingsAdapter);

        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(mMainActivity, getResources().getIdentifier("layout_animation_from_left", "anim", mMainActivity.getPackageName()));

        accountSettingsListView.setLayoutAnimation(animation);

        settingsAdapter.notifyDataSetChanged();

        accountSettingsListView.scheduleLayoutAnimation();

        accountSettingsListView.setOnItemClickListener(this);

    }

    private void nurseSetting() {

        Nurse nurse = RealmController.with(this).getNurse(Integer.valueOf(new PreferenceHelper(getActivity()).getUserId()));

        /*String name = new PreferenceHelper(mMainActivity).getUser_name();
        String pictureUrl = new PreferenceHelper(mMainActivity).getPicture();
        String pictureUrl = nurse.getmPictureUrl();*/

        tv_build_version.setText("Version: " + BuildConfig.VERSION_NAME);

        if (nurse.getmPictureUrl() != null) {

            EbizworldUtils.appLogDebug("HaoLS", "nav pic " + nurse.getmPictureUrl());


            /*Picasso.get().load(pictureUrl).error(R.drawable.defult_user).into(accountIcon);*/

            Glide.with(mMainActivity)
                    .load(nurse.getmPictureUrl())
                    .apply(new RequestOptions().error(R.drawable.defult_user))
                    .into(accountIcon);

        }

        if (nurse.getmFullName() != null) {

            accountName.setText(nurse.getmFullName());

        }

        AccountSettingsAdapter settingsAdapter = new AccountSettingsAdapter(mMainActivity, getAccountSettingsList());

        accountSettingsListView.setAdapter(settingsAdapter);

        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(mMainActivity, getResources().getIdentifier("layout_animation_from_left", "anim", mMainActivity.getPackageName()));

        accountSettingsListView.setLayoutAnimation(animation);

        settingsAdapter.notifyDataSetChanged();

        accountSettingsListView.scheduleLayoutAnimation();

        accountSettingsListView.setOnItemClickListener(this);
    }

    private void hospitalSetting() {

        Hospital hospital = RealmController.with(this).getHospital(Integer.valueOf(new PreferenceHelper(getActivity()).getUserId()));
        /*String name = new PreferenceHelper(mMainActivity).getUser_name();
        String pictureUrl = new PreferenceHelper(mMainActivity).getPicture();

        String pictureUrl = nurse.getmPictureUrl();*/

        tv_build_version.setText("Version: " + BuildConfig.VERSION_NAME);

        if (hospital.getmCompanyPicture() != null) {

            EbizworldUtils.appLogDebug("HaoLS", "nav pic " + hospital.getmCompanyPicture());


            /*Picasso.get().load(pictureUrl).error(R.drawable.defult_user).into(accountIcon);*/

            Glide.with(mMainActivity)
                    .load(hospital.getmCompanyPicture())
                    .apply(new RequestOptions().error(R.drawable.defult_user))
                    .into(accountIcon);

        } else {

            EbizworldUtils.appLogDebug("HaoLS", "nav pic isEmpty");
        }

        if (hospital.getmHospitalName() != null) {

            accountName.setText(hospital.getmHospitalName());

        }

        AccountSettingsAdapter settingsAdapter = new AccountSettingsAdapter(mMainActivity, getAccountSettingsList());

        accountSettingsListView.setAdapter(settingsAdapter);

        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(mMainActivity, getResources().getIdentifier("layout_animation_from_left", "anim", mMainActivity.getPackageName()));

        accountSettingsListView.setLayoutAnimation(animation);

        settingsAdapter.notifyDataSetChanged();

        accountSettingsListView.scheduleLayoutAnimation();

        accountSettingsListView.setOnItemClickListener(this);
    }


    private List<AccountSettings> getAccountSettingsList() {

        List<AccountSettings> accountSettingsList = new ArrayList<>();
//        accountSettingsList.add(new AccountSettings(R.drawable.home_map_marker, getString(R.string.my_home)));
//        accountSettingsList.add(new AccountSettings(R.drawable.flash, getString(R.string.ask_bot)));
//        accountSettingsList.add(new AccountSettings(R.drawable.credit_card, getString(R.string.my_payment)));
//        accountSettingsList.add(new AccountSettings(R.drawable.wallet, getString(R.string.ambulance2u_wallet)));
//        accountSettingsList.add(new AccountSettings(R.drawable.ic_favorite_heart_button, getString(R.string.saved_places)));
//        accountSettingsList.add(new AccountSettings(R.drawable.clock_alert, getString(R.string.ride_history)));

   //     if (new PreferenceHelper(getActivity()).getLoginType().equals(Const.PatientService.PATIENT)) {

//            accountSettingsList.add(new AccountSettings(R.drawable.ic_clock, getString(R.string.txt_hourly_booking)));
//            accountSettingsList.add(new AccountSettings(R.drawable.sale, getString(R.string.referral_title)));
//            accountSettingsList.add(new AccountSettings(R.drawable.ic_list_schedule, getString(R.string.later_title)));

      //  }

        /*accountSettingsList.add(new AccountSettings(R.drawable.wallet, getString(R.string.history_payment)));*/
        accountSettingsList.add(0,new AccountSettings(R.drawable.ic_account_wallet,getString(R.string.nikola_wallet)));
        accountSettingsList.add(1,new AccountSettings(R.drawable.help_circle, getString(R.string.my_help)));
        accountSettingsList.add(2,new AccountSettings(R.drawable.ic_power_off, getString(R.string.txt_logout)));

        return accountSettingsList;

    }

    @Override
    public void onResume() {
        super.onResume();
//        mMainActivity.currentFragment = Const.UserSettingsFragment;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (new PreferenceHelper(getActivity()).getLoginType().equals(Const.PatientService.PATIENT)) {

            switch (position) {
                case 0:
                    //  DialogPayWallet();
                    mMainActivity.addFragment(new WalletFragment(), true, Const.WALLET_FRAGMENT, true);
                    break;
//                case 1:
//                    startActivity(new Intent(mMainActivity, AskBotActivity.class));
//
//                    break;
//                case 0:
//                    startActivity(new Intent(mMainActivity, AddPaymentFragment.class));
//                    break;
//                case 3:
//                    startActivity(new Intent(mMainActivity, AmbulanceWalletActivity.class));
//                    break;
//
//                case 5 :
//                    startActivity(new Intent(mMainActivity, SavedPlacesActivity.class));
//                    break;
//                case 1:
//                    startActivity(new Intent(mMainActivity, HistoryRideFragment.class));
//                    break;
//
//                case 0:
//
//                    if (mMainActivity != null){
//
//                        mMainActivity.addFragment(new HourlyBookingFragment(), false, Const.HOURLY_BOOKING_FRAGMENT, true);
//                    }
//                    break;
//
//                case 0:
//                    showrefferal();
//                    break;
//
//                case 1:{
//                    mMainActivity.addFragment(new HistoryPaymentFragment(), false, Const.HISTORY_PAYMENT_FRAGMENT, true);
//                }
//                break;

                case 1:
                    startActivity(new Intent(mMainActivity, HelpwebActivity.class));
                    break;

                case 2:
                    showLogoutDialog();
                    break;

            }

        } else if (new PreferenceHelper(getActivity()).getLoginType().equals(Const.NursingHomeService.NURSING_HOME) ||
                new PreferenceHelper(getActivity()).getLoginType().equals(Const.HospitalService.HOSPITAL)) {

            switch (position) {

                /*case 0:{
                    mMainActivity.addFragment(new HistoryPaymentFragment(), false, Const.HISTORY_PAYMENT_FRAGMENT, true);
                }
                break;*/

                case 0:
                    startActivity(new Intent(mMainActivity, HelpwebActivity.class));
                    break;

                case 1:
                    showLogoutDialog();
                    break;
            }

        }


    }

    private void showhelp() {
        final Dialog help_dialog = new Dialog(mMainActivity, R.style.DialogSlideAnim_leftright);
        help_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        help_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        help_dialog.setCancelable(true);
        help_dialog.setContentView(R.layout.help_layout);

        help_dialog.show();
    }


    private void showrefferal() {
        final Dialog refrel_dialog = new Dialog(mMainActivity, R.style.DialogThemeforview);
        refrel_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        refrel_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent_black)));
        refrel_dialog.setCancelable(true);
        refrel_dialog.setContentView(R.layout.refferalcode_layout);
        //  user = RealmController.with(this).getUser(Integer.valueOf(new PreferenceHelper(mMainActivity).getUserId()));
        final TextView refCode = refrel_dialog.findViewById(R.id.refCode);
        refCode.setText(new PreferenceHelper(mMainActivity).getReferralCode());
        ((ImageButton) refrel_dialog.findViewById(R.id.referral_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refrel_dialog.dismiss();
            }
        });

        ((ImageView) refrel_dialog.findViewById(R.id.twitter_share)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                refrel_dialog.dismiss();
            }
        });

        if (new PreferenceHelper(mMainActivity).getReferralBONUS().isEmpty()) {
            ((TextView) refrel_dialog.findViewById(R.id.txt_referl_earn)).setText("00");
        } else {

            ((TextView) refrel_dialog.findViewById(R.id.txt_referl_earn)).setText(new PreferenceHelper(mMainActivity).getReferralBONUS());

        }

        ((TextView) refrel_dialog.findViewById(R.id.gm_share)).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                /*Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Hey check out my app and earn by entering my referral code:" + refCode.getText().toString()+" while registering" + "https://play.google.com/store/apps/details?id=com.nikola.user");
                sendIntent.setType("text/plain");
                startActivity(sendIntent);*/
                refrel_dialog.dismiss();

            }

        });

        ((ImageView) refrel_dialog.findViewById(R.id.fb_share)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent
                        .putExtra(Intent.EXTRA_TEXT, "Hey check out my app and earn by entering my referral code:" + refCode.getText().toString() + " while registering.  " + "https://play.google.com/store/apps/details?id=com.nikola.user");
                sendIntent.setType("text/plain");
                sendIntent.setPackage("com.facebook.orca");
                try {
                    startActivity(sendIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(mMainActivity, "Please Install Facebook Messenger", Toast.LENGTH_LONG).show();
                }
                refrel_dialog.dismiss();
            }
        });
        refrel_dialog.show();
    }

    public static int getScreenWidth(Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return size.x;
    }

    private void showLogoutDialog() {

        dialog = new Dialog(mMainActivity,R.style.Custom_Dialog);
        //, R.style.DialogThemeforview

        dialog.setCancelable(true);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        Window window = dialog.getWindow();
        if(window != null){
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); // This flag is required to set otherwise the setDimAmount method will not show any effect
            window.setDimAmount(0.5f); //0 for no dim to 1 for full dim
        }

        dialog.setContentView(R.layout.dialog_logout);

        TextView btn_logout_yes = (TextView) dialog.findViewById(R.id.btn_logout_yes);

        dialog.show();

        btn_logout_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                dialog.dismiss();
//                new PreferenceHelper(mMainActivity).Logout();
//
//                BaseFragment.drop_latlan = null;
//                BaseFragment.pic_latlan = null;
//                BaseFragment.s_address = "";
//                BaseFragment.d_address = "";
//
//                new PreferenceHelper(mMainActivity).Logout();
//                Intent i = new Intent(mMainActivity, WelcomeActivity.class);
//                startActivity(i);
//                mMainActivity.finish();
                logoutApi();
                dialog.dismiss();
            }
        });

        TextView btn_logout_no = (TextView) dialog.findViewById(R.id.btn_logout_no);

        btn_logout_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });




    }

    private void logoutApi() {
        if (!EbizworldUtils.isNetworkAvailable(mMainActivity)) {

            EbizworldUtils.showShortToast(getResources().getString(R.string.network_error), activity);
            return;
        }
        Commonutils.progressdialog_show(mMainActivity, getString(R.string.logout_txt));

        HashMap<String, String> map = new HashMap<String, String>();

        if (new PreferenceHelper(mMainActivity).getLoginType().equals(Const.PatientService.PATIENT)) {

            map.put(Const.Params.URL, Const.ServiceType.LOGOUT_URL);

        } else if (new PreferenceHelper(mMainActivity).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)) {

            map.put(Const.Params.URL, Const.NursingHomeService.LOGOUT_URL);

        } else if (new PreferenceHelper(mMainActivity).getLoginType().equals(Const.HospitalService.HOSPITAL)) {

            map.put(Const.Params.URL, Const.HospitalService.LOGOUT_URL);
        }

        map.put(Const.Params.ID, new PreferenceHelper(mMainActivity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(mMainActivity).getSessionToken());
        Log.d("asher", "logout map " + map.toString());
        new VolleyRequester(mMainActivity, Const.POST, map, Const.ServiceCode.LOGOUT, this);
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        switch (serviceCode) {

            case Const.ServiceCode.SHOW_RECHARGE_WALLET: {
                Log.d("DatGetWallet", "" + response);

                if (response != null) {

                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {

                            String total_Money_Wallet = jsonObject.getString("e_wallet");

                            txt_total_money_wallet.setText(getResources().getString(R.string.txt_total_pay_wallet) + " " + total_Money_Wallet + " S$");

                            new PreferenceHelper(mMainActivity).putTotalAmountWallet(total_Money_Wallet);

                            EbizworldUtils.removeProgressDialog();

                        } else {
                            EbizworldUtils.showLongToast("error", mMainActivity);
                            EbizworldUtils.removeProgressDialog();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {

                    EbizworldUtils.showLongToast("error", mMainActivity);
                    EbizworldUtils.removeProgressDialog();

                }

            }
            break;

            case Const.ServiceCode.LOGOUT:
                Log.d("asher", "logout Response" + response);
                EbizworldUtils.removeProgressDialog();
                try {
                    JSONObject job = new JSONObject(response);
                    if (job.getString("success").equals("true")) {
                        dialog.dismiss();
                        /*new PreferenceHelper(mMainActivity).Logout();*/

                        BaseFragment.drop_latlan = null;
                        BaseFragment.pic_latlan = null;
                        BaseFragment.s_address = "";
                        BaseFragment.d_address = "";

                        new PreferenceHelper(mMainActivity).Logout();

                        if (new PreferenceHelper(mMainActivity).getLoginType().equals(Const.PatientService.PATIENT)) {

                            RealmController.with(this).clearAllPatient();

                        } else if (new PreferenceHelper(mMainActivity).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)) {

                            RealmController.with(this).clearAllNurse();

                        } else if (new PreferenceHelper(mMainActivity).getLoginType().equals(Const.HospitalService.HOSPITAL)) {

                            RealmController.with(this).clearAllHospital();

                        }

                        Intent i = new Intent(mMainActivity, SignInActivity.class);
                        startActivity(i);
                        mMainActivity.finish();

                        EbizworldUtils.appLogDebug("HaoLS", "Logout succeeded");
                    } else {
                        /*String error_code = job.optString("error_code");
                        if (error_code.equals("104")) {
                            EbizworldUtils.showShortToast("You have logged in other device!", mMainActivity);
                            new PreferenceHelper(mMainActivity).Logout();
                            startActivity(new Intent(mMainActivity, WelcomeActivity.class));
                            mMainActivity.finish();

                        }*/

                        EbizworldUtils.appLogDebug("HaoLS", "Logout failed");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    EbizworldUtils.appLogDebug("HaoLS", "Logout failed " + e.toString());
                }


                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
