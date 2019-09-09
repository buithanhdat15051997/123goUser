package sg.go.user.restAPI;

import android.app.Activity;

import java.util.HashMap;

import sg.go.user.HttpRequester.VolleyRequester;
import sg.go.user.Interface.AsyncTaskCompleteListener;
import sg.go.user.R;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.EbizworldUtils;
import sg.go.user.Utils.PreferenceHelper;

public class ScheduleListAPI {

    private Activity mActivity;
    private AsyncTaskCompleteListener asyncTaskCompleteListener;

    public ScheduleListAPI(Activity mActivity, AsyncTaskCompleteListener asyncTaskCompleteListener) {
        this.mActivity = mActivity;
        this.asyncTaskCompleteListener = asyncTaskCompleteListener;
    }

    public void getPatientSchedule(int STATICCODE) {

        if (!EbizworldUtils.isNetworkAvailable(mActivity)){

            EbizworldUtils.showShortToast(mActivity.getResources().getString(R.string.network_error), mActivity);
            return;

        }

        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.GET_SCHEDULE);
        map.put(Const.Params.ID, new PreferenceHelper(mActivity).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(mActivity).getSessionToken());

        EbizworldUtils.appLogDebug("HaoLS", "UpcomingRequest " + map.toString());
        new VolleyRequester(mActivity, Const.POST, map, STATICCODE, asyncTaskCompleteListener);
    }

    public void getNursingHomeSchedule(int STATICCODE){

        if (!EbizworldUtils.isNetworkAvailable(mActivity)){

            EbizworldUtils.showShortToast(mActivity.getResources().getString(R.string.network_error), mActivity);
            return;
        }
        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put(Const.Params.URL, Const.NursingHomeService.LIST_SCHEDULE_REQUEST_URL);
        hashMap.put(Const.Params.ID, new PreferenceHelper(mActivity).getUserId());
        hashMap.put(Const.Params.TOKEN, new PreferenceHelper(mActivity).getSessionToken());
        EbizworldUtils.appLogDebug("HaoLS", hashMap.toString());

        EbizworldUtils.appLogDebug("HaoLS", hashMap.toString());

        new VolleyRequester(mActivity, Const.POST, hashMap, STATICCODE, asyncTaskCompleteListener);
    }

    public void getHospitalSchedule(int STATICCODE){

        if (!EbizworldUtils.isNetworkAvailable(mActivity)){

            EbizworldUtils.showShortToast(mActivity.getResources().getString(R.string.network_error), mActivity);
            return;
        }
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(Const.Params.URL, Const.HospitalService.LIST_SCHEDULE_REQUEST_URL);
        hashMap.put(Const.Params.ID, new PreferenceHelper(mActivity).getUserId());
        hashMap.put(Const.Params.TOKEN, new PreferenceHelper(mActivity).getSessionToken());
        EbizworldUtils.appLogDebug("HaoLS", hashMap.toString());

        new VolleyRequester(mActivity, Const.POST, hashMap, STATICCODE, asyncTaskCompleteListener);
    }
}
