package sg.go.user.restAPI;

import android.app.Activity;

import java.util.HashMap;

import sg.go.user.HttpRequester.VolleyRequester;
import sg.go.user.Interface.AsyncTaskCompleteListener;
import sg.go.user.Models.Schedule;
import sg.go.user.R;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.EbizworldUtils;
import sg.go.user.Utils.PreferenceHelper;

public class BillingInfoAPI {

    private Activity mActivity;
    private AsyncTaskCompleteListener asyncTaskCompleteListener;

    public BillingInfoAPI(Activity mActivity, AsyncTaskCompleteListener asyncTaskCompleteListener) {
        this.mActivity = mActivity;
        this.asyncTaskCompleteListener = asyncTaskCompleteListener;
    }

    public void getPatientScheduleBillingInfo(Schedule schedule, int STATICCODE){

        if (!EbizworldUtils.isNetworkAvailable(mActivity)) {

            EbizworldUtils.showShortToast(mActivity.getResources().getString(R.string.network_error), mActivity);
            return;
        }

        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put(Const.Params.URL, Const.ServiceType.BILLING_INFO);

        hashMap.put(Const.Params.ID, new PreferenceHelper(mActivity).getUserId());
        hashMap.put(Const.Params.TOKEN, new PreferenceHelper(mActivity).getSessionToken());
        hashMap.put(Const.Params.TIME, schedule.getRequest_date());
        hashMap.put(Const.Params.A_AND_E, String.valueOf(schedule.getA_and_e()));
        hashMap.put(Const.Params.IMH, String.valueOf(schedule.getImh()));
        hashMap.put(Const.Params.FERRY_TERMINALS, String.valueOf(schedule.getFerry_terminals()));
        hashMap.put(Const.Params.STAIRCASE, String.valueOf(schedule.getStaircase()));
        hashMap.put(Const.Params.TARMAC, String.valueOf(schedule.getTarmac()));
        hashMap.put(Const.Params.WEIGHT, String.valueOf(schedule.getWeight()));
        hashMap.put(Const.Params.OXYGEN, String.valueOf(schedule.getOxygen_tank()));
        hashMap.put(Const.Params.CASE_TYPE, String.valueOf(schedule.getCase_type()));

        EbizworldUtils.appLogDebug("HaoLS", "Get Billing info for Patient's schedule: " + hashMap.toString());

        new VolleyRequester(mActivity, Const.POST, hashMap, STATICCODE, asyncTaskCompleteListener);
    }
}
