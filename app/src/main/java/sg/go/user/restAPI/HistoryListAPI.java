package sg.go.user.restAPI;

import android.app.Activity;

import java.util.HashMap;

import sg.go.user.HttpRequester.VolleyRequester;
import sg.go.user.Interface.AsyncTaskCompleteListener;
import sg.go.user.R;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.EbizworldUtils;
import sg.go.user.Utils.PreferenceHelper;

public class HistoryListAPI {

    private Activity mActivity;
    private AsyncTaskCompleteListener asyncTaskCompleteListener;

    public HistoryListAPI(Activity mActivity, AsyncTaskCompleteListener asyncTaskCompleteListener) {
        this.mActivity = mActivity;
        this.asyncTaskCompleteListener = asyncTaskCompleteListener;
    }

    public void getPatientHistory(int STATICCODE) {

        if (mActivity != null){

            if (!EbizworldUtils.isNetworkAvailable(mActivity)) {
                EbizworldUtils.showShortToast(mActivity.getResources().getString(R.string.network_error), mActivity);
                return;
            }

            /*EbizworldUtils.showSimpleProgressDialog(getActivity(),"",false);*/

            HashMap<String, String> map = new HashMap<String, String>();

            map.put(Const.Params.URL, Const.ServiceType.GET_HISTORY);
            map.put(Const.Params.ID, new PreferenceHelper(mActivity).getUserId());
            map.put(Const.Params.TOKEN, new PreferenceHelper(mActivity).getSessionToken());

            EbizworldUtils.appLogDebug("HaoLS", "Getting history ride " + map.toString());
            new VolleyRequester(mActivity, Const.POST, map, STATICCODE, asyncTaskCompleteListener);

        }

    }

    public void getNursingHomeHistory(int STATICCODE) {

        if (mActivity != null){

            if (!EbizworldUtils.isNetworkAvailable(mActivity)) {
                EbizworldUtils.showShortToast(mActivity.getResources().getString(R.string.network_error), mActivity);
                return;
            }

            /*EbizworldUtils.showSimpleProgressDialog(getActivity(),"",false);*/

            HashMap<String, String> map = new HashMap<String, String>();

            map.put(Const.Params.URL, Const.NursingHomeService.HISTORY_RIDE_URL);
            map.put(Const.Params.ID, new PreferenceHelper(mActivity).getUserId());
            map.put(Const.Params.TOKEN, new PreferenceHelper(mActivity).getSessionToken());

            EbizworldUtils.appLogDebug("HaoLS", "Getting history ride " + map.toString());
            new VolleyRequester(mActivity, Const.POST, map, STATICCODE, asyncTaskCompleteListener);

        }

    }

    public void getHospitalHistory(int STATICCODE) {

        if (mActivity != null){

            if (!EbizworldUtils.isNetworkAvailable(mActivity)) {
                EbizworldUtils.showShortToast(mActivity.getResources().getString(R.string.network_error), mActivity);
                return;
            }

            HashMap<String, String> map = new HashMap<String, String>();
            map.put(Const.Params.URL, Const.HospitalService.HISTORY_RIDE_URL);
            map.put(Const.Params.ID, new PreferenceHelper(mActivity).getUserId());
            map.put(Const.Params.TOKEN, new PreferenceHelper(mActivity).getSessionToken());

            EbizworldUtils.appLogDebug("HaoLS", "Getting history ride " + map.toString());
            new VolleyRequester(mActivity, Const.POST, map, STATICCODE, asyncTaskCompleteListener);

        }

    }
}
