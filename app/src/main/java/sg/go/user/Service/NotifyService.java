package sg.go.user.Service;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import sg.go.user.R;
import sg.go.user.Fragment.TravelMapFragment;
import sg.go.user.Interface.AsyncTaskCompleteListener;
import sg.go.user.Utils.Const;

public class NotifyService extends Service implements AsyncTaskCompleteListener {

    @Override
    public IBinder onBind(Intent intent) {
       return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Service", "onStartCommand:");
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        Log.d("Service", "onStartCommand:");
    }

    public void onTaskCompleted(String response, int serviceCode) {

        switch (serviceCode) {

            case Const.IS_ACCEPTED:
            addNotification(getApplication().getString(R.string.text_job_accepted));
                break;

            case Const.IS_DRIVER_DEPARTED:
                addNotification(getApplication().getString(R.string.text_driver_started));
                break;

            case Const.IS_DRIVER_ARRIVED:
                addNotification(getApplication().getString(R.string.text_driver_arrvied));
                break;

            case Const.IS_DRIVER_TRIP_STARTED:
                addNotification(getApplication().getString(R.string.text_trip_started));
                break;

            case Const.IS_DRIVER_TRIP_ENDED:
                addNotification(getApplication().getString(R.string.text_trip_completed));
                break;
        }
    }

    public void addNotification(String text) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_logoapp)
                .setContentTitle("Notifications")
                .setContentText(text);
        Intent notificationIntent = new Intent(getApplicationContext(), TravelMapFragment.class);

        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Service.NOTIFICATION_SERVICE);
        manager.notify(0, mBuilder.build());
    }
}
