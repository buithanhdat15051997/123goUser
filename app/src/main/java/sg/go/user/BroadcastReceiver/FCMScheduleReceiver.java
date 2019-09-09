package sg.go.user.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import sg.go.user.MainActivity;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.EbizworldUtils;

public class FCMScheduleReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        EbizworldUtils.appLogDebug("AmbulanceFCMService", "Broadcast received action " + intent.getAction());
        //Receive Intent from AmbulanceFCMService for Action Schedule
        if (intent.getAction().equals(Const.NotificationType.SCHEDULE)){

                EbizworldUtils.appLogDebug("AmbulanceFCMService", intent.getExtras().getString(Const.NotificationType.ACTION));

                Intent startActivityIntent = new Intent();
                startActivityIntent.setClassName(context.getPackageName(), MainActivity.class.getName());
                startActivityIntent.putExtra(Const.NotificationType.ACTION, Const.NotificationType.SCHEDULE);
                startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                context.startActivity(startActivityIntent);
                EbizworldUtils.appLogDebug("AmbulanceFCMService", "Starting MainActivity from FCMScheduleReceiver");
        }

    }
}
