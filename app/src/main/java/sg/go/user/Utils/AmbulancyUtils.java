package sg.go.user.Utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

import java.text.SimpleDateFormat;

public class AmbulancyUtils {
    public static String toDateTimeFormat(long dateTime) {
        return new SimpleDateFormat("MMMM dd HH:mm").format(Long.valueOf(dateTime));
    }

    public static String toDateFormat(long dateTime) {
        return new SimpleDateFormat("dd.MM.yyyy").format(Long.valueOf(dateTime));
    }


    private static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

    public static int getScreenHeight(Context context) {
        return getDisplayMetrics(context).heightPixels;
    }

    public static int getScreenWidth(Context context) {
        return getDisplayMetrics(context).widthPixels;
    }

    public static float getScreenDensity(Context context) {
        return getDisplayMetrics(context).density;
    }
}
