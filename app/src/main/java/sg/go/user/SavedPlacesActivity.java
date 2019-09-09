package sg.go.user;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import sg.go.user.HttpRequester.VolleyRequester;

import sg.go.user.Utils.EbizworldUtils;
import com.google.android.gms.maps.model.LatLng;
import sg.go.user.Adapter.FavouritesAdapter;
import sg.go.user.Adapter.PlacesAutoCompleteAdapter;
import sg.go.user.Interface.AsyncTaskCompleteListener;
import sg.go.user.Models.Favourites;
import sg.go.user.Utils.Const;
import sg.go.user.Utils.PreferenceHelper;
import sg.go.user.Utils.RecyclerLongPressClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

public class SavedPlacesActivity extends AppCompatActivity implements AsyncTaskCompleteListener {
    private Toolbar favMainToolbar;
    private RecyclerView fav_lv;
    private ProgressBar fav_progress_bar;
    private ImageButton fav_back, fav_add;
    private TextView fav_empty,addFav_ok,addFavName;
    private ArrayList<Favourites> favouritesArrayList;
    private FavouritesAdapter favouritesAdapter;
    private AutoCompleteTextView addFav_address;
    LatLng fav_latLng;
    String addFav_name="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_places);
        favMainToolbar = (Toolbar) findViewById(R.id.toolbar_fav);

        favouritesArrayList = new ArrayList<>();
        setSupportActionBar(favMainToolbar);
        getSupportActionBar().setTitle(null);
        fav_lv = (RecyclerView) findViewById(R.id.fav_lv);
        fav_progress_bar = (ProgressBar) findViewById(R.id.fav_progress_bar);
        fav_back = (ImageButton) findViewById(R.id.fav_back);
        fav_add = (ImageButton) findViewById(R.id.fav_add);
        fav_empty = (TextView) findViewById(R.id.fav_empty);

        fav_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        fav_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddFavDialog();
            }
        });
        fav_lv.addOnItemTouchListener(new RecyclerLongPressClickListener(this, fav_lv, new RecyclerLongPressClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //    showDetailedHistroy(historylst.get(position));

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
        getSavedPlaces();
    }

    private void openAddFavDialog() {

        final Dialog addFavDialog = new Dialog(this, R.style.DialogThemeforview);
        addFavDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        addFavDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.fade_drawable));
        addFavDialog.setCancelable(true);
        addFavDialog.setContentView(R.layout.add_fav_layout);
        addFav_address=(AutoCompleteTextView)addFavDialog.findViewById(R.id.addFav_address);
        addFavName=(TextView)addFavDialog.findViewById(R.id.addFav_name);
        final PlacesAutoCompleteAdapter favAddressAdapter = new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_list_text);
        addFav_address.setAdapter(favAddressAdapter);
        addFav_address.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                addFav_address.setSelection(0);
                EbizworldUtils.hideKeyBoard(SavedPlacesActivity.this);
                final String selectedDestPlace = favAddressAdapter.getItem(i);
                Log.e("asher","fav address "+addFav_address.getText().toString());
                try {
                    getLocationforDest(URLEncoder.encode(addFav_address.getText().toString(), "utf-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }


        });

        addFav_ok=(TextView)addFavDialog.findViewById(R.id.addFav_ok);
        addFav_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFav_name=addFavName.getText().toString();
                if(addFav_name!=""){
                    if(fav_latLng!=null){
                        addFav();
                        addFavDialog.dismiss();
                    }else {
                        Toast.makeText(getApplicationContext(),"Enter place address",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Enter place name",Toast.LENGTH_SHORT).show();
                }
            }
        });
        addFavDialog.show();

    }
    private void getLocationforDest(String selectedDestPlace) {
        if (!EbizworldUtils.isNetworkAvailable(this)) {

            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put(Const.Params.URL, Const.LOCATION_API_BASE + selectedDestPlace + "&key=" + Const.GOOGLE_API_KEY);
        Log.d("mahi", "map for d_loc" + map);
        new VolleyRequester(this, Const.GET, map, Const.ServiceCode.LOCATION_API_BASE_DESTINATION, this);
    }
    private void addFav() {
        if (!EbizworldUtils.isNetworkAvailable(this)) {
            return;
        }
        EbizworldUtils.showSimpleProgressDialog(this, "", false);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.ADD_FAV);
        map.put(Const.Params.ID, new PreferenceHelper(this).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(this).getSessionToken());
        map.put(Const.Params.LATITUDE, String.valueOf(fav_latLng.latitude));
        map.put(Const.Params.LONGITUDE, String.valueOf(fav_latLng.longitude));
        map.put(Const.Params.ADDRESS, addFav_address.getText().toString());
        map.put(Const.Params.FAVOURITE_NAME, addFav_name);

        Log.d("asher"," add fav map "+ map.toString());
        new VolleyRequester(this, Const.POST, map, Const.ServiceCode.ADD_FAV,
                this);
    }

    private void getSavedPlaces() {
        if (!EbizworldUtils.isNetworkAvailable(this)) {
            return;
        }
        EbizworldUtils.showSimpleProgressDialog(this, "", false);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Const.Params.URL, Const.ServiceType.GET_SAVED_PLACES);
        map.put(Const.Params.ID, new PreferenceHelper(this).getUserId());
        map.put(Const.Params.TOKEN, new PreferenceHelper(this).getSessionToken());

        Log.d("mahi", map.toString());
        new VolleyRequester(this, Const.POST, map, Const.ServiceCode.GET_SAVED_PLACES, this);
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {

        switch (serviceCode) {

            case Const.ServiceCode.GET_SAVED_PLACES:
                EbizworldUtils.removeProgressDialog();
                Log.d("mahi", "res saved places" + response);
                EbizworldUtils.showShortToast(response, SavedPlacesActivity.this);
                if (response != null) {
                    try {
                        JSONObject favobj = new JSONObject(response);
                        if (favobj.getString("success").equals("true")) {
                            //  fav_progress_bar.setVisibility(View.GONE);
                            if (favouritesArrayList != null) {
                                favouritesArrayList.clear();
                            }
                            JSONArray hisArray = favobj.getJSONArray("data");
                            if (hisArray.length() > 0) {
                                for (int i = 0; i < hisArray.length(); i++) {
                                    JSONObject obj = hisArray.getJSONObject(i);
                                    Favourites fav = new Favourites();
                                    fav.setFav_Id(obj.getString("id"));
                                    fav.setFav_Address(obj.getString("address"));
                                    fav.setFav_Latitude(obj.getString("latitude"));
                                    fav.setFav_Longitude(obj.getString("longitude"));
                                    fav.setFav_Name(obj.getString("favourite_name"));
                                    favouritesArrayList.add(fav);
                                }

                                if (favouritesArrayList != null) {
                                    favouritesAdapter = new FavouritesAdapter(this, favouritesArrayList);
                                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                    fav_lv.setLayoutManager(mLayoutManager);
                                    fav_lv.setItemAnimator(new DefaultItemAnimator());
                                    fav_lv.setAdapter(favouritesAdapter);


                                }

                            } else {
                                fav_empty.setVisibility(View.VISIBLE);
                            }

                        } else {
                            fav_progress_bar.setVisibility(View.GONE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case Const.ServiceCode.LOCATION_API_BASE_DESTINATION:
                if (null != response) {
                    try {
                        JSONObject job = new JSONObject(response);
                        JSONArray jarray = job.optJSONArray("results");
                        JSONObject locObj = jarray.getJSONObject(0);
                        JSONObject geometryOBJ = locObj.optJSONObject("geometry");
                        JSONObject locationOBJ = geometryOBJ.optJSONObject("location");
                        double lat = locationOBJ.getDouble("lat");
                        double lan = locationOBJ.getDouble("lng");
                        fav_latLng = new LatLng(lat, lan);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
                case Const.ServiceCode.ADD_FAV:
                    EbizworldUtils.removeProgressDialog();
                    Log.d("asher", "res add fav " + response);
                    if (response != null) {
                        try {
                            JSONObject favobj = new JSONObject(response);
                            if (favobj.getString("success").equals("true")) {
                                getSavedPlaces();
                                Toast.makeText(this,"Added Successfully",Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                            }
                    break;
        }
    }

}
