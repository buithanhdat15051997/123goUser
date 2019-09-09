package sg.go.user.restAPI;

import android.content.Context;

import java.util.HashMap;

import sg.go.user.HttpRequester.VolleyRequester;
import sg.go.user.Interface.AsyncTaskCompleteListener;
import sg.go.user.R;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.EbizworldUtils;
import sg.go.user.Utils.PreferenceHelper;

public class MethodGET {

    public static void sendnotification(Context context, AsyncTaskCompleteListener asyncTaskCompleteListener) {

        if (!EbizworldUtils.isNetworkAvailable(context)) {

            EbizworldUtils.showShortToast(context.getResources().getString(R.string.network_error), context);
            return;
        }

        HashMap<String, String> map = new HashMap<>();

        if (new PreferenceHelper(context).getLoginType().equals(Const.PatientService.PATIENT)){

            map.put(Const.Params.URL, Const.ServiceType.USER_MESSAGE_NOTIFY + Const.Params.ID + "="
                    + new PreferenceHelper(context).getUserId() + "&" + Const.Params.TOKEN + "="
                    + new PreferenceHelper(context).getSessionToken() + "&" + Const.Params.REQUEST_ID + "=" + new PreferenceHelper(context).getRequestId());

        }else if (new PreferenceHelper(context).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)){

            map.put(Const.Params.URL, Const.NursingHomeService.CHAT_NOTIFICATION_URL + Const.Params.ID + "="
                    + new PreferenceHelper(context).getUserId() + "&" + Const.Params.TOKEN + "="
                    + new PreferenceHelper(context).getSessionToken() + "&" + Const.Params.REQUEST_ID + "=" + new PreferenceHelper(context).getRequestId());

        }else if (new PreferenceHelper(context).getLoginType().equals(Const.NursingHomeService.NURSING_HOME)){

            map.put(Const.Params.URL, Const.HospitalService.CHAT_NOTIFICATION_URL + Const.Params.ID + "="
                    + new PreferenceHelper(context).getUserId() + "&" + Const.Params.TOKEN + "="
                    + new PreferenceHelper(context).getSessionToken() + "&" + Const.Params.REQUEST_ID + "=" + new PreferenceHelper(context).getRequestId());
        }

        EbizworldUtils.appLogDebug("HaoLS", "send_notification: " + map.toString());

        new VolleyRequester(context, Const.GET, map, Const.ServiceCode.USER_MESSAGE_NOTIFY, asyncTaskCompleteListener);
    }
}
