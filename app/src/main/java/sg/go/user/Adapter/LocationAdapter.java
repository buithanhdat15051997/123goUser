package sg.go.user.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import sg.go.user.Models.LocationList;
import sg.go.user.R;

import java.util.ArrayList;

/**
 * Created by user on 2/23/2017.
 */

public class LocationAdapter extends ArrayAdapter<LocationList> implements
        Filterable {
    public static final String LOG_TAG = "PlacesAutoCompleteAdapter";
    private ArrayList<LocationList> resultList;
    private Context context;
    private int textViewResourceId;

    public LocationAdapter(Context context, int textViewResourceId,ArrayList<LocationList> resultList) {
        super(context, textViewResourceId,resultList);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        this.resultList=resultList;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public LocationList getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.autocomplete_list_text, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.tvPlace)).setText(getItem(position).getLocation_address());

        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.

                    // Assign the data to the FilterResults
                    try {
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

}
