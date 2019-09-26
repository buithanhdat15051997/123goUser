package sg.go.user.Utils;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import sg.go.user.R;
import sg.go.user.Fragment.ScheduleListFragment;
import sg.go.user.Service.AmbulanceNotificationPublisher;

import java.util.Calendar;

public class AlarmUtil {

    public static void scheduleNotification(Context activityContext, Calendar calendar){

        NotificationManager notificationManager = (NotificationManager) activityContext.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(activityContext, ScheduleListFragment.class);
        PendingIntent pendingIntent1 = PendingIntent.getActivity(activityContext, Integer.parseInt(String.valueOf(R.string.notification_channel_id)), intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notification = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            if (notificationManager.getNotificationChannel(activityContext.getResources().getString(R.string.notification_channel_id))  == null){

                NotificationChannel notificationChannel =
                        new NotificationChannel(activityContext.getResources().getString(R.string.notification_channel_id),
                                activityContext.getResources().getString(R.string.notification_channel_name),
                                NotificationManager.IMPORTANCE_HIGH);

                notificationChannel.setDescription(activityContext.getResources().getString(R.string.notification_channel_name_description));
                notificationChannel.enableVibration(true);
                notificationChannel.setVibrationPattern(new long[]{100, 500});

                notificationManager.createNotificationChannel(notificationChannel);

            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(activityContext, String.valueOf(R.string.notification_channel_id))
                    .setContentTitle(activityContext.getResources().getString(R.string.app_name))
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.background_user_login)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentText(activityContext.getResources().getString(R.string.schedule_ride_notification))
                    .setVibrate(new long[]{100, 500})
                    .setDefaults(Notification.DEFAULT_ALL);

            builder.setContentIntent(pendingIntent1);

            notification = builder.build();

        }else {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(activityContext)
                    .setContentTitle(activityContext.getResources().getString(R.string.app_name))
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.background_user_login)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentText(activityContext.getResources().getString(R.string.schedule_ride_notification))
                    .setVibrate(new long[]{100, 500})
                    .setDefaults(Notification.DEFAULT_ALL);

            builder.setContentIntent(pendingIntent1);

            notification = builder.build();

        }

        if (notification != null){

            Intent notificationIntent = new Intent(activityContext, AmbulanceNotificationPublisher.class);
            notificationIntent.putExtra("notification_id", String.valueOf(R.string.notification_channel_id));
            notificationIntent.putExtra("notification", notification);

            Log.d("HaoLS", notification.toString());
            Log.d("HaoLS", notificationIntent.toString());
            Log.d("HaoLS", calendar.getTime() + "");

            PendingIntent pendingIntent2 = PendingIntent.getBroadcast(activityContext,
                    Integer.parseInt(String.valueOf(R.string.notification_channel_id)),
                    notificationIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);

            AlarmManager alarmManager = (AlarmManager) activityContext.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent2);

        }
    }
}
