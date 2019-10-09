package sg.go.user.FCMHandler;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import sg.go.user.ChatActivity;
import sg.go.user.MainActivity;
import sg.go.user.R;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.EbizworldUtils;
import sg.go.user.Utils.PreferenceHelper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class AmbulanceFCMService extends FirebaseMessagingService {

    private NotificationManager mNotificationManager;
    /*private String TAG = AmbulanceFCMService.class.getSimpleName();*/
    private String TAG = "AmbulanceFCMService";
    private int mMessage_Type = 0;

    PreferenceHelper mPreferenceHelper;
    private String mToken = null;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0){

            Log.d(TAG, "Data: " + remoteMessage.getData());
            Log.d(TAG, "Action: " + remoteMessage.getData().get(Const.NotificationType.ACTION));
            Log.d(TAG, "Type: " + remoteMessage.getData().get(Const.NotificationType.TYPE));

            if (remoteMessage.getData().get(Const.NotificationType.ACTION) != null &&
                    remoteMessage.getData().get(Const.NotificationType.TYPE) != null){

                if (remoteMessage.getData().get(Const.NotificationType.ACTION).equals(Const.NotificationType.SCHEDULE) /*&&
                        remoteMessage.getData().get(Const.NotificationType.TYPE).equals(Const.NotificationType.TYPE_SCHEDULE_STARTED)*/){

                    //Send Intent to FCMScheduleReceiver
                    Intent intent = new Intent(Const.NotificationType.SCHEDULE);
                    intent.putExtra(Const.NotificationType.ACTION, Const.NotificationType.TYPE_SCHEDULE_STARTED);
                    intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                    sendBroadcast(intent);
                    EbizworldUtils.appLogDebug(TAG, "Intent sent to broadcast with action " + intent.getAction());

                }/*else if (remoteMessage.getData().get(Const.NotificationType.ACTION).equals(Const.NotificationType.CHAT_MESSAGE) &&
                        remoteMessage.getData().get(Const.NotificationType.TYPE).equals(Const.NotificationType.CHAT_MESSAGE)){

                    sendNotification(remoteMessage.getData().get(Const.NotificationType.BODY), remoteMessage.getData().get(Const.NotificationType.TYPE));
                }else {

                    sendNotification(remoteMessage.getData().get(Const.NotificationType.BODY), remoteMessage.getData().get(Const.NotificationType.TYPE));
                }*/

                sendNotification(remoteMessage.getData().get(Const.NotificationType.BODY), remoteMessage.getData().get(Const.NotificationType.TYPE));

            }

        }


        /*if (remoteMessage.getNotification() != null){

            Log.d(TAG, "Priority: " + remoteMessage.getPriority());
            Log.d(TAG, "Notification title: " + remoteMessage.getNotification().getTitle());
            Log.d(TAG, "Notification body: " + remoteMessage.getNotification().getBody());

            sendNotification(remoteMessage.getNotification().getBody());

        }*/

    }

    @Override
    public void onNewToken(String s) {

        mToken = s;

        storeRegistrationId(this, mToken);

        Log.d(TAG, "Token: " + mToken);

    }

    private void sendNotification(String body, String type) {

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = null;

        if (type.equals(Const.NotificationType.CHAT_MESSAGE)){
            intent = new Intent(this, ChatActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("newques", "new_quest");
        }else {

            intent = new Intent(this, MainActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        }
         if(type.equals(Const.NotificationType.TYPE_ACCOUNT_LOGOUT)){

             intent = new Intent(Const.NotificationType.TYPE_ACCOUNT_LOGOUT);

             intent.putExtra(Const.NotificationType.TYPE_ACCOUNT_LOGOUT, type);

             LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

         }

//        if (mMessage_Type == Const.NotificationType.SCHEDULE_CODE){
//
//            intent = new Intent(this, MainActivity.class);
//            intent.putExtra(Const.NotificationType.MESSAGE_TYPE, Const.NotificationType.SCHEDULE_CODE);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//
//        }else {
//
//            intent = new Intent(this, MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        }

        //Dùng cho phương thức setContenIntent(...)
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        //Dùng cho phương thức setSound(...)
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Log.e(TAG, "rec push o" + body);

            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel mChannel = new NotificationChannel(String.valueOf(getString(R.string.notification_channel_id)), getResources().getString(R.string.notification_channel_name), importance);
            mChannel.setDescription(getString(R.string.notification_channel_name_description));
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 500});
            mNotificationManager = getSystemService(NotificationManager.class);
            mNotificationManager.createNotificationChannel(mChannel);


            if (mNotificationManager.getNotificationChannel(String.valueOf(getResources().getString(R.string.notification_channel_id))) == null) {

                mChannel = new NotificationChannel(String.valueOf(getResources().getString(R.string.notification_channel_id)), getResources().getString(R.string.notification_channel_name), importance);
                mChannel.setDescription(getResources().getString(R.string.notification_channel_name_description));
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 500});

                mNotificationManager.createNotificationChannel(mChannel);

            }

            NotificationCompat.Builder  mBuilder = new NotificationCompat.Builder(this, String.valueOf(getResources().getString(R.string.notification_channel_id)))
                    .setContentTitle(getResources().getString(R.string.app_name))  // required
                    .setSmallIcon(R.drawable.background_user_login) // required
                    .setContentText(body)  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationManager.IMPORTANCE_MAX)
                    .setVibrate(new long[]{100, 500});

            try{

                mNotificationManager.notify(Integer.parseInt(getResources().getString(R.string.notification_channel_id)), mBuilder.build());

            }catch (NumberFormatException e){
                e.printStackTrace();
            }

        }else {

            //Dùng cho notificationManager.notify(...)
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.background_user_login)
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContentText(body)
                    .setAutoCancel(true)
                    .setColor(getResources().getColor(R.color.deeporange600))
                    .setSound(defaultSoundUri)
                    .setVibrate(new long[]{100,500})
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentIntent(pendingIntent);

            mNotificationManager.notify(0, builder.build());

        }

    }

    /*private void sendNotification(String msg, String type) {

        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent chat_intent = null;

        if(type.equals("2")){

            chat_intent = new Intent(this, ChatActivity.class);
            chat_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            chat_intent.putExtra("newques", "new_quest");

        } else {

            chat_intent = new Intent(this, MainActivity.class);
            chat_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            chat_intent.putExtra("newques", "new_quest");

        }

        PendingIntent contentIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(),
                chat_intent, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Log.e(TAG, "rec push o" + msg);

            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel mChannel = mNotificationManager.getNotificationChannel(String.valueOf(NOTIFICATION_ID));

            if (mChannel == null) {

                mChannel = new NotificationChannel(String.valueOf(NOTIFICATION_ID), getResources().getString(R.string.app_name), importance);
                mChannel.setDescription(msg);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 500});

                mNotificationManager.createNotificationChannel(mChannel);

            }

            NotificationCompat.Builder  mBuilder = new NotificationCompat.Builder(this, String.valueOf(NOTIFICATION_ID))
                    .setContentTitle(getResources().getString(R.string.app_name))  // required
                    .setSmallIcon(R.drawable.ic_launcher) // required
                    .setContentText(msg)  // required
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(contentIntent)
                    .setVibrate(new long[]{100, 500});

            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        }else {

            Log.e(TAG, "rec push " + msg);

            Notification.Builder mBuilder = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setStyle(new Notification.BigTextStyle().bigText(msg))
                    .setVibrate(new long[]{100, 500})
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setAutoCancel(true)
                    .setContentText(msg)
                    .setContentIntent(contentIntent);

            mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }

    }*/

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void storeRegistrationId(Context context, String regId) {

        mPreferenceHelper = new PreferenceHelper(this);
        int appVersion = getAppVersion(context);
        Log.d(TAG, "Saving regId on app version " + appVersion);
        Log.d(TAG,"RegID "+regId);
        mPreferenceHelper.putAppVersion(appVersion);
        mPreferenceHelper.putRegisterationID(regId);
        mPreferenceHelper.putDeviceToken(regId);
    }
}
