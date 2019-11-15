package sg.go.user.Utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import sg.go.user.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Amal on 28-06-2015.
 */
public class Commonutils {
    private static Dialog mProgressDialog;

    public static void showtoast(String msg, Context context) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void progressdialog_show(Context context, String msg) {

        if (context != null) {

            mProgressDialog = new Dialog(context, R.style.DialogSlideAnim_leftright);
            mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            mProgressDialog.setCancelable(false);
            mProgressDialog.setContentView(R.layout.animation_loading);
            TextView tv_title = (TextView) mProgressDialog.findViewById(R.id.tv_title);
            tv_title.setText(msg);
            mProgressDialog.show();

        }
    }

    public static String message_json(String message_id, String type, String driver_id,
                                      String client_id, String request_id, String message) {

        JSONObject jObject = new JSONObject();
        try {

            jObject.put("id", message_id);
            jObject.put("type", type);
            jObject.put("driver_id", driver_id);
            jObject.put("client_id", client_id);
            jObject.put("request_id", request_id);
            jObject.put("message", message);

            return jObject.toString();

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";
    }

    public static void progressdialog_hide() {
        try {
            if (mProgressDialog != null) {

                if (mProgressDialog.isShowing()) {

                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
