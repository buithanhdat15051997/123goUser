package sg.go.user.HttpRequester;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import sg.go.user.Interface.AsyncTaskCompleteListener;
import sg.go.user.R;
import sg.go.user.Utils.EbizworldUtils;
import sg.go.user.Utils.Commonutils;
import sg.go.user.Utils.Const;
import sg.go.user.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Amal on 28-06-2015.
 */
public class VolleyRequester {

    private Context activity;
    private AsyncTaskCompleteListener asyncTaskCompleteListener;
    private Map<String, String> map;
    int servicecode;

    // SeekbarTimer seekbar;

    public VolleyRequester(Context activity, int method_type, Map<String, String> map, int servicecode, AsyncTaskCompleteListener asyncTaskCompleteListener) {
        int method;
        this.activity = activity;
        this.asyncTaskCompleteListener = asyncTaskCompleteListener;
        this.map = map;

        this.servicecode = servicecode;
        if (method_type == 0)
            method = Request.Method.GET;

        else
            method = Request.Method.POST;

        String URL = map.get(Const.Params.URL);
        map.remove(Const.Params.URL);

        if (method == Request.Method.POST)
            volley_requester(method, URL, (map == null) ? null : map);

        else
            volley_requester(URL);


    }

    public VolleyRequester(Context activity, int method_type, Map<String, String> map, int servicecode, AsyncTaskCompleteListener asyncTaskCompleteListener, Map<String,String> headerMap) {
        int method = 0;
        this.activity = activity;
        this.asyncTaskCompleteListener = asyncTaskCompleteListener;
        this.map = map;

        this.servicecode = servicecode;
        if (method_type == 0)
            method = Request.Method.GET;
        else if(method_type==1)
            method = Request.Method.POST;
        else if(method_type==2)
            method = Request.Method.PUT;
        else if(method_type==3)
            method = Request.Method.DELETE;

        String URL = map.get(Const.Params.URL);
        map.remove(Const.Params.URL);

        if (method == Request.Method.POST || method==Request.Method.DELETE || method==Request.Method.PUT )

            volley_requesterHeader(method, URL, (map == null) ? null : map,(headerMap==null)?null:headerMap);

        else if(method==Request.Method.GET )

            volley_requesterHeader(URL,headerMap);


    }

    public void volley_requester(int method, String url, final Map<String, String> requestbody) {


        StringRequest jsonObjRequest = new StringRequest(method,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //seekbar.cancel();
                        if(response!=null){
                            asyncTaskCompleteListener.onTaskCompleted(response, servicecode);
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (error instanceof NoConnectionError) {
                            //String msg = "No network connection.Please check your internet";
                           // Commonutils.showtoast(msg, activity);
                            EbizworldUtils.appLogError("HaoLS", "onErrorResponse " + error.toString());
                            Commonutils.progressdialog_hide();
                        }
                    }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params = requestbody;
                return params;
            }

        };

        jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                Const.TIMEOUT,
                Const.MAX_RETRY,
                Const.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(jsonObjRequest);

    }

    public void volley_requester(String url) {

        JsonObjectRequest jsongetrequest = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                if(response!=null){

                    asyncTaskCompleteListener.onTaskCompleted(response.toString(), servicecode);

                }

                //seekbar.cancel();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError) {
                    Log.d("amal", "volley requester 2" + error.toString());
                    String msg = activity.getResources().getString(R.string.network_error);
                    Commonutils.showtoast(msg, activity);
                    Commonutils.progressdialog_hide();

                }
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        // returned data is not JSONObject?
                        e2.printStackTrace();
                    }
                }
            }
        });

        AppController.getInstance().addToRequestQueue(jsongetrequest);
    }


    private class SeekbarTimer extends CountDownTimer {

        public SeekbarTimer(long startTime, long interval) {
            super(startTime, interval);



        }

        @Override
        public void onFinish() {

            Toast.makeText(activity, activity.getResources().getString(R.string.network_error), Toast.LENGTH_LONG).show();

        }

        @Override
        public void onTick(long millisUntilFinished) {


        }
    }

    public void volley_requesterHeader(int method, String url, final Map<String, String> requestbody, final Map<String,String> headerMap) {
        HttpsTrustManager.allowAllSSL();
        EbizworldUtils.appLogDebug("Ashutosh", "Url in http " + url);

        StringRequest jsonObjRequest = new StringRequest
                (method,
                        url,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {

                                if(response!=null) {
                                    EbizworldUtils.appLogDebug("HttpRequester Response", response.toString());

                                    asyncTaskCompleteListener.onTaskCompleted(response.toString(), servicecode);
                                }
                            }
                        }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        EbizworldUtils.appLogDebug("HttpRequester Error",error.toString());
                        if (error instanceof NoConnectionError) {
                            Log.d("pavan", "volley requester 1" + error.toString());
                            String msg = "No network connection.Please check your internet";
//                    Commonutils.showtoast(msg, activity);
//                    Commonutils.progressdialog_hide();
                        }
                    }
                }) {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError
            {
                EbizworldUtils.appLogDebug("HttpRequester"," GetParams");
                Map<String, String> params = new HashMap<String, String>();
                params = requestbody;
                return params;
            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                if(headerMap!=null)
                {
                    return headerMap;

                }
                return headerMap;
            }
        };

        jsonObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                Const.TIMEOUT,
                Const.MAX_RETRY,
                Const.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(jsonObjRequest);

    }


    public void volley_requesterHeader(String url, final Map<String,String> headerMap) {
        HttpsTrustManager.allowAllSSL();
        JsonObjectRequest jsongetrequest = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response)
            {
                if(response!=null) {
                    asyncTaskCompleteListener.onTaskCompleted(response.toString(), servicecode);
                    Log.d("Ashutosh", "volley requester response " + response.toString());
                    //seekbar.cancel();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError) {
                    Log.d("Ashutosh", "volley requester 2" + error.toString());
                    String msg = "No network connection.Please check your internet";
//                    Commonutils.showtoast(msg, activity);
//                    Commonutils.progressdialog_hide();
                }
            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                return headerMap;
            }
        };


        AppController.getInstance().addToRequestQueue(jsongetrequest);
    }

}
