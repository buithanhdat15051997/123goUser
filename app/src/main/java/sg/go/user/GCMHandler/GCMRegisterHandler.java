package sg.go.user.GCMHandler;

;

/**
 * Created by user on 6/29/2015.
 */
/*public class GCMRegisterHandler {

    private Activity activity;
    public static final String EXTRA_MESSAGE = "message";

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    GoogleCloudMessaging gcm;
    PreferenceHelper prefs;

    private String regid;
    private String TAG = "pavan";

    public GCMRegisterHandler(Activity activity, BroadcastReceiver mHandleMessageReceiver) {

        prefs = new PreferenceHelper(activity);

        try {
            this.activity = activity;
            // Check device for Play Services APK. If check succeeds, proceed with
            //  GCM registration.
            if (checkPlayServices()) {

                gcm = GoogleCloudMessaging.getInstance(activity);
                regid = getRegistrationId(activity);



                prefs.putDeviceToken(regid);


                if (regid.isEmpty()) {
                    registerInBackground();
                }
            } else {
                Log.d(TAG, "No valid Google Play Services APK found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                activity.finish();
            }
            return false;
        }
        return true;
    }

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


    private String getRegistrationId(Context context) {

        String registrationId = prefs.getRegistrationID();
        if (registrationId.isEmpty()) {
            Log.d(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs.getAppVersion();
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.d(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }


    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(activity);
                    }
                    regid = gcm.register(activity.getResources().getString(R.string.sender_id));
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    //sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo ic_back_white the
                    // message using the 'from' address in the message.

                    // Persist the registration ID - no need to register again.
                    storeRegistrationId(activity, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential ic_back_white-off.
                }
                return msg;
            }


            @Override
            protected void onPostExecute(String msg) {

            }
        }.execute(null, null, null);

    }

    private void storeRegistrationId(Context context, String regId) {

        int appVersion = getAppVersion(context);
        Log.d(TAG, "Saving regId on app version " + appVersion);
        Log.d(TAG,"RegID "+regId);
        prefs.putAppVersion(appVersion);
        prefs.putRegisterationID(regId);
        prefs.putDeviceToken(regId);
    }



}*/
