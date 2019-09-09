package sg.go.user.Utils;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

/*import com.company.ambulance2u.GCMHandler.GcmBroadcastReceiver;
import com.company.ambulance2u.GCMHandler.OnBootBroadcastReceiver;*/

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

public class ForegroundListner implements Application.ActivityLifecycleCallbacks {

    public static final long CHECK_DELAY = 500;
    private Thread thread;

    public interface Listener {

        void onBecameForeground(Activity activity);

        void onBecameBackground(Activity activity);

    }

    private static ForegroundListner instance;

    private boolean foreground = false, paused = true;
    private Handler handler = new Handler();
    private List<Listener> listeners = new CopyOnWriteArrayList<Listener>();
    private Runnable check;

    public static ForegroundListner init(Application application) {
        if (instance == null) {
            instance = new ForegroundListner();
            if (Build.VERSION.SDK_INT >= 14)
                application.registerActivityLifecycleCallbacks(instance);
        }
        return instance;
    }

    public static ForegroundListner get(Application application) {
        if (instance == null) {
            init(application);
        }
        return instance;
    }

    public static ForegroundListner get(Context ctx) {
        if (instance == null) {
            Context appCtx = ctx.getApplicationContext();
            if (appCtx instanceof Application) {
                init((Application) appCtx);
            }
            throw new IllegalStateException(
                    "ForegroundListner is not initialised and " +
                            "cannot obtain the Application object");
        }
        return instance;
    }

    public static ForegroundListner getInstance() {
        if (instance == null) {
            throw new IllegalStateException(
                    "ForegroundListner is not initialised - invoke " +
                            "at least once with parameter init/get");
        }
        return instance;
    }

    public boolean isForeground() {
        return foreground;
    }

    public boolean isBackground() {
        return !foreground;
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        paused = false;
        boolean wasBackground = !foreground;
        foreground = true;

        if (check != null)
            handler.removeCallbacks(check);

        if (wasBackground) {
            for (Listener l : listeners) {
                try {
                    l.onBecameForeground(activity);
                } catch (Exception exc) {

                }
            }
        }
    }

    @Override
    public void onActivityPaused(final Activity activity) {
        paused = true;

        if (check != null)
            handler.removeCallbacks(check);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (foreground && paused) {
                    foreground = false;
                    //sendGtmHomeButtonClickEvent(activity);
                    for (Listener l : listeners) {
                        try {
                            l.onBecameBackground(activity);
                        } catch (Exception exc) {

                        }
                    }
                }
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, CHECK_DELAY);

    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.e("mahi", "activity started");
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {

        Log.e("mahi", "activity destroyed");

        /*Calendar cal = Calendar.getInstance();

        Intent i = new Intent(activity, OnBootBroadcastReceiver.class);
        activity.sendBroadcast(i);

        AlarmManager manager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(activity, GcmBroadcastReceiver.class);
        PendingIntent pendingIntent  = PendingIntent.getActivity(activity, (int) System.currentTimeMillis(),
                intent, 0);
        manager.set(AlarmManager.RTC, cal.getTimeInMillis()+ 5000, pendingIntent );*/


    }

}
