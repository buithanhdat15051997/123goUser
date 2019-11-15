package sg.go.user.Adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import sg.go.user.Utils.Const;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;


public class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {

    public static final String LOG_TAG = "PlacesAutoCompleteAdapter";
    private ArrayList<String> mResultList = new ArrayList<String>();
    private Context mContext;

    public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {

        super(context, textViewResourceId);

        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mResultList.size();
    }

    @Override
    public String getItem(int index) {

        return mResultList.get(index);

    }

    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults filterResults = new FilterResults();

                if (null != constraint) {

                    // Retrieve the autocomplete results.
                    mResultList = autocomplete(constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = mResultList;
                    filterResults.count = mResultList.size();

                }

                return filterResults;

            }

            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                if (null != results && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    private ArrayList<String> autocomplete(String input) {

        String locale = mContext.getResources().getConfiguration().locale.getCountry();

        Log.d("PlaceAdapter", "Place Url : " + locale.toString());

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(Const.PLACES_API_BASE
                    + Const.TYPE_AUTOCOMPLETE + Const.OUT_JSON);

            sb.append("?sensor=false&key=" + Const.PLACES_AUTOCOMPLETE_API_KEY);
            // sb.append("&location=" + BeanLocation.getLocation().getLatitude()
            // + "," + BeanLocation.getLocation().getLongitude());

            if(locale.equals("VN")||locale.equals("SG")){

                sb.append("&components=country:"+locale.toString().trim());

            }

            sb.append("&radius=500");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));
            Log.d("PlaceAdapter", "Place Url : " + sb.toString());
            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e("mahi", "Error processing Places API URL", e);
            return mResultList;
        } catch (IOException e) {
            Log.e("mahi", "Error connecting to Places API", e);
            return mResultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            // System.out.println(jsonResults.toString());
            // AppLog.Log(LOG_TAG, jsonResults.toString());
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            final JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            ((Activity) mContext).runOnUiThread(new Runnable() {
                public void run() {
            mResultList.clear();
            for (int i = 0; i < predsJsonArray.length(); i++) {
                try {
                    mResultList.add(predsJsonArray.getJSONObject(i).getString(
                            "description"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
                }
            });

        } catch (JSONException e) {
            Log.e("mahi", "Cannot process JSON results", e);
        }

        return mResultList;
    }
}
