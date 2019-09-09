package sg.go.user.restAPI;

import android.app.Activity;
import android.util.Log;

import java.util.HashMap;

import sg.go.user.HttpRequester.VolleyRequester;
import sg.go.user.Interface.AsyncTaskCompleteListener;
import sg.go.user.R;
import sg.go.user.Utils.Commonutils;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.EbizworldUtils;
import sg.go.user.Utils.PreferenceHelper;

public class CancelScheduleRequestAPI {

    private Activity mActivity;
    private AsyncTaskCompleteListener asyncTaskCompleteListener;

    public CancelScheduleRequestAPI(Activity mActivity, AsyncTaskCompleteListener asyncTaskCompleteListener) {
        this.mActivity = mActivity;
        this.asyncTaskCompleteListener = asyncTaskCompleteListener;
    }

    public void cancelPatientSchedule(String req_id, int STATICCODE) {

        if (!EbizworldUtils.isNetworkAvailable(mActivity)) {

            EbizworldUtils.showShortToast(mActivity.getResources().getString(R.string.network_error), mActivity);
            return;
        }
        Commonutils.progressdialog_show(mActivity, "Canceling...");

        HashMap<String, String> map = new HashMap<>();

        map.put(Const.Params.URL, Const.ServiceType.CANCEL_LATER_RIDE + Const.Params.ID + "="
                + new PreferenceHelper(mActivity).getUserId() + "&" + Const.Params.TOKEN + "="
                +  new PreferenceHelper(mActivity).getSessionToken()+"&"+Const.Params.REQUEST_ID + "="+req_id);
        Log.d("BTD",map.toString());

        EbizworldUtils.appLogDebug("HaoLS", "cancel_reg" + map.toString());
        new VolleyRequester(mActivity, Const.GET, map, STATICCODE, asyncTaskCompleteListener);
    }

    public void cancelNurseSchedule(String requestID, int STATICCODE){

        if (!EbizworldUtils.isNetworkAvailable(mActivity)){

            EbizworldUtils.showShortToast(mActivity.getResources().getString(R.string.network_error), mActivity);
            return;

        }

        Commonutils.progressdialog_show(mActivity, "Canceling...");
        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put(Const.Params.URL, Const.NursingHomeService.CANCEL_SCHEDULE_URL);
        hashMap.put(Const.Params.ID, new PreferenceHelper(mActivity).getUserId());
        hashMap.put(Const.Params.TOKEN, new PreferenceHelper(mActivity).getSessionToken());
        hashMap.put(Const.Params.REQUEST_ID, requestID);

        EbizworldUtils.appLogDebug("HaoLS", "cancelNurseSchedule: " + hashMap.toString());

        new VolleyRequester(mActivity, Const.POST, hashMap, STATICCODE, asyncTaskCompleteListener);

    }

    public void cancelHospitalSchedule(String requestID, int STATICCODE){

        if (!EbizworldUtils.isNetworkAvailable(mActivity)){

            EbizworldUtils.showShortToast(mActivity.getResources().getString(R.string.network_error), mActivity);
            return;

        }

        Commonutils.progressdialog_show(mActivity, "Canceling...");
        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put(Const.Params.URL, Const.HospitalService.CANCEL_SCHEDULE_URL);
        hashMap.put(Const.Params.ID, new PreferenceHelper(mActivity).getUserId());
        hashMap.put(Const.Params.TOKEN, new PreferenceHelper(mActivity).getSessionToken());
        hashMap.put(Const.Params.REQUEST_ID, requestID);

        EbizworldUtils.appLogDebug("HaoLS", "cancelNurseSchedule: " + hashMap.toString());

        new VolleyRequester(mActivity, Const.POST, hashMap, STATICCODE, asyncTaskCompleteListener);

    }
}
